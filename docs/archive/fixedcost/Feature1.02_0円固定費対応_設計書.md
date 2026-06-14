# Feature2: 0円固定費登録対応 設計書

## 1. 概要

### 1.1 目的

固定費管理画面において支払金額0円の固定費の登録・更新を可能にする。
また、収支登録画面で0円固定費が含まれる場合にユーザへ注意喚起し、
内容確認ボタン押下時に0円のまま登録されることを防ぐ。

### 1.2 対象ブランチ

`feature-1.02-dev2`

### 1.3 背景

固定費の中には「月によって金額が変わるもの」や「毎月発生するが金額未定のもの」がある。
現状の実装では `FixedCostInfoUpdateForm.shiharaiKingaku` に `@Min(1)` バリデーションが
設定されているため、0円での登録ができない。
ドメインタイプ `FixedCostPaymentAmount` はすでに0以上を許容しているため（マイナス値のみ禁止）、
変更が必要なのは **フォームバリデーション層のみ**。

### 1.4 対象画面・クラス

| 種別 | クラス名 / ファイル名 | 変更種別 |
|------|----------------------|---------|
| リクエストフォーム | `FixedCostInfoUpdateForm` | 修正 |
| テンプレート | `FixedCostInfoManageUpdate.html` | 修正 |
| UseCase | `IncomeAndExpenditureInitUseCase` | 修正・追加 |
| Controller | `IncomeAndExpenditureRegistController` | 修正 |
| テストSQL（Controller専用） | `IncomeAndExpenditureRegistControllerIntegrationTest.sql` | 新規作成 |

---

## 2. 変更詳細

### ① 固定費フォームのバリデーション変更

#### 対象

- `FixedCostInfoUpdateForm.shiharaiKingaku`（固定費更新画面のみ）
- `FixedCostBulkUpdateForm` は **対応対象外**（一括更新での0円登録は非対応）

#### 変更内容

```java
// FixedCostInfoUpdateForm.java
// 変更前
@NotNull
@Min(1)
private Integer shiharaiKingaku;

// 変更後
@NotNull
@Min(0)
private Integer shiharaiKingaku;
```

#### HTML の `required` 属性は変更しない

- `FixedCostInfoManageUpdate.html` の支払金額入力欄 → `required` 存続
- 0円は許可するが、未入力はNGのまま

---

### ①-2 固定費区分=「予定支払い金額」の場合、0円不可（@AssertTrue追加）

#### 対象

- `FixedCostInfoUpdateForm` — `@AssertTrue isValidShiharaiKingakuForKubun()` 追加
- `FixedCostInfoManageUpdate.html` — エラー表示追加

#### 背景

固定費区分=「2: 予定支払い金額」は「今後手動で更新することが前提の区分」であるため、
0円のまま登録することは意味がない（誤登録防止）。
一方、固定費区分=「1: 支払い金額確定」は0円固定費として登録することを許容する。
（例：住民税のように支払い時に金額が確定するものを別途住民税積立として固定費登録しており、実際の住民税支払いの固定費を0円で登録し、年間固定費合計との整合性を保つケース）


#### FixedCostInfoUpdateForm への追加

```java
/**
 * 相関チェック(固定費区分が「予定支払い金額」の場合、支払金額に0円は設定不可)
 */
@AssertTrue(message = "固定費区分が「予定支払い金額」の場合、支払金額に0円は設定できません。")
public boolean isValidShiharaiKingakuForKubun() {
    // 固定費区分が「予定支払い金額(2)」かつ支払金額が0円の場合、false
    if (Objects.equals(fixedCostKubun, MyHouseholdAccountBookContent.FIXED_COST_ESTIMATE_SELECTED_VALUE)
            && shiharaiKingaku != null && shiharaiKingaku == 0) {
        return false;
    }
    return true;
}
```

- `FIXED_COST_ESTIMATE_SELECTED_VALUE = "2"`（`MyHouseholdAccountBookContent` 定義済み）
- フィールド名は `validShiharaiKingakuForKubun`（`isValidShiharaiKingakuForKubun` の `is` を除いたもの）

#### FixedCostInfoManageUpdate.html への追加

支払金額の `<p class="invalid-feedback" th:errors="*{shiharaiKingaku}">` の直後に追加：

```html
<!--/* 固定費区分=2（予定支払い金額）の場合0円不可チェックの結果を出力 */-->
<p class="invalid-feedback" th:errors="*{validShiharaiKingakuForKubun}" style="display: block;"></p>
```

- `style="display: block;"` は `needCheckShiharaiTukiOptionalContext` の既存パターンと同様

---

### ② 収支登録画面初期表示での0円固定費メッセージ出力

#### 対象

`IncomeAndExpenditureInitUseCase.readInitInfo()`

#### 変更箇所

固定費一覧から支出登録情報リストを生成するループ処理の後に、
0円の支出登録情報がある場合は注意メッセージを追加する。

#### 実装方針

```
固定費一覧 → ExpenditureRegistItemList を生成（既存処理）

// 【追加】0円の支出登録情報がある場合、件数分メッセージを追加
for(ExpenditureRegistItem item : expenditureRegistItemList) {
    if (BigDecimal.ZERO.compareTo(item.getExpenditureKingaku()) == 0) {
        String displayName = buildExpenditureDisplayName(item);
        response.addMessage(
            String.format("「%s」の支払金額が0円で登録されています。実際の支払金額で更新が必要です。", displayName));
    }
}
```

#### メッセージ形式

```
「{支出名}」の支払金額が0円で登録されています。実際の支払金額で更新が必要です。
```

#### 注意: 支出名の生成ロジックの共通化について

`readInitInfo()` 内での0円チェック時と `IncomeAndExpenditureRegistListComponent` での表示名生成が
同一ロジックになる。共通化の検討余地はあるが、今回は責務の観点から **各クラスに同等のロジックを実装** する方針とする。
（UseCase内の一時的なロジックをComponentに持ち込むと責務が曖昧になるため）

---

### ③ ExpenditureItemForm（支出追加フォーム）の支払金額

**対応不要。** 収支登録画面での手動支出追加における0円入力はユーザ操作の意図なし領域のため、
現状の `@Min(1)` バリデーションを維持する。

---

### ④ 内容確認ボタン押下時のセッションデータ検証（UseCase集約）

#### 設計方針

収入0件チェックと0円支出チェックの **両方をUseCase内に集約** し、
Controller は UseCase を呼び出して `hasMessages()` で結果を判定するだけにする（薄いController原則）。

#### UseCase 新メソッド `readRegistCheckValidateInfo()`

旧メソッド `readRegistCheckErrorSetInfo()`・`readZeroAmountExpenditureCheckErrorSetInfo()` を **削除** し、
両チェックを統合した単一メソッドを追加する。

**シグネチャ**：

```java
/**
 * 内容確認ボタン押下時のセッションデータ検証を行い画面表示情報を取得します。
 * ①収入登録情報が0件の場合、「収入情報が未登録です。」メッセージを設定して即時リターン
 * ②削除以外かつ更新なし(NON_UPDATE)以外の支出登録情報のうち、支払金額が0円のものについてメッセージを設定
 *   (NON_UPDATEはDBからロードされた支出で過去に意図的に登録された0円のためチェック対象外)
 * メッセージが0件の場合は正常（バリデーション通過）となります。
 */
public IncomeAndExpenditureRegistResponse readRegistCheckValidateInfo(
        LoginUserInfo user,
        String targetYearMonth,
        List<IncomeRegistItem> incomeRegistItemList,
        List<ExpenditureRegistItem> expenditureRegistItemList)
```

**内部処理**：

```
// レスポンスを生成
// registListComponent.setIncomeAndExpenditureInfoList() でセッション情報を画面に設定

// ①収入登録情報が0件の場合、エラーメッセージを設定して即時リターン
if (incomeRegistItemList.isEmpty()) {
    response.addMessage("収入情報が未登録です。");
    return response;
}

// ②削除以外かつ更新なし(NON_UPDATE)以外の支出登録情報のうち、支払金額が0円のものについてメッセージを設定
// (NON_UPDATEはDBからロードされた支出で過去に意図的に登録された0円のためチェック対象外)
expenditureRegistItemList.stream()
        .filter(item -> !Objects.equals(item.getAction(), ACTION_TYPE_DELETE))
        .filter(item -> !Objects.equals(item.getAction(), ACTION_TYPE_NON_UPDATE))
        .filter(item -> BigDecimal.ZERO.compareTo(item.getExpenditureKingaku()) == 0)
        .forEach(item -> response.addMessage(
            String.format("「%s」の支出が0円から更新されていません。実際の支払金額に更新してください。",
                buildExpenditureDisplayName(item))));
```

**メッセージ形式**：

```
「{支出名}」の支出が0円から更新されていません。実際の支払金額に更新してください。
```

#### Controller 変更内容

`getRegistCheckLoad()` を UseCase 呼び出し + `hasMessages()` 判定に簡略化する。

**変更後のフロー**：

```
// UseCaseでセッションデータ（収入0件・0円支出）のバリデーションを実施
IncomeAndExpenditureRegistResponse validationResponse = usecase.readRegistCheckValidateInfo(...)

// バリデーションエラーがある場合は収支登録画面にエラーメッセージを表示
if (validationResponse.hasMessages()) {
    return validationResponse.setLoginUserName(...).build();
}

// バリデーション通過 → 収支登録内容確認画面に遷移
return incomeAndExpenditureRegistConfirmUseCase.readRegistCheckInfo(...).setLoginUserName(...).build();
```

- `BigDecimal`・`Objects` のインポートは不要になるため削除する

---

## 3. 変更ファイル一覧

| ファイル | 変更種別 | 変更内容 |
|---------|---------|---------|
| `presentation/request/itemmanage/FixedCostInfoUpdateForm.java` | 修正 | `shiharaiKingaku` の `@Min(1)` → `@Min(0)`、`@AssertTrue isValidShiharaiKingakuForKubun()` 追加 |
| `resources/templates/itemmanage/fixedcost/FixedCostInfoManageUpdate.html` | 修正 | `th:errors="*{validShiharaiKingakuForKubun}"` エラー表示追加（支払金額エラー直後） |
| `application/usecase/account/incomeandexpenditure/IncomeAndExpenditureInitUseCase.java` | 修正・追加 | `readInitInfo()` に0円メッセージ追加、旧`readRegistCheckErrorSetInfo()`・`readZeroAmountExpenditureCheckErrorSetInfo()` を削除し `readRegistCheckValidateInfo()` として統合、privateメソッド `buildExpenditureDisplayName()` 追加 |
| `presentation/controller/account/regist/IncomeAndExpenditureRegistController.java` | 修正 | `getRegistCheckLoad()` を `readRegistCheckValidateInfo()` + `hasMessages()` 判定に簡略化、`BigDecimal`・`Objects` インポート削除 |
| `presentation/controller/account/regist/IncomeAndExpenditureRegistControllerIntegrationTest.sql` | 新規作成 | Controllerテスト専用SQL（UseCaseSQL + FIXED_COST_TABLEに0円固定費1件追加） |

---

## 4. テスト設計

### 4.1 単体テスト（不要）

フォームバリデーション変更は `@Min` アノテーションのみであり、ドメインロジックの変更なし。
ドメインタイプ `FixedCostPaymentAmount` はすでに0を許容しているためテスト追加不要。

### 4.2 統合テスト

#### FixedCostInfoManageControllerIntegrationTest

固定費フォームのバリデーション動作確認。

**追加テストケース**：

| テスト番号 | テスト名 | 検証内容 |
|-----------|---------|---------|
| ⑲ | `testPostUpdate_addSuccess_zeroAmount` | 支払金額0円 + 固定費区分=1 → `@Min(0)` バリデーション通過・正常リダイレクト |
| ⑳ | `testPostUpdate_validationError_kubun2ZeroAmount` | 支払金額0円 + 固定費区分=2 → `@AssertTrue` バリデーションエラー（`validShiharaiKingakuForKubun` にエラー） |
| ㉑ | `testPostUpdate_addSuccess_kubun2NonZeroAmount` | 支払金額1円 + 固定費区分=2 → `@AssertTrue` バリデーション通過・正常リダイレクト |

#### IncomeAndExpenditureInitIntegrationTest

`readRegistCheckValidateInfo()` の動作確認。

**追加・変更テストケース**：

| テスト番号 | テスト名 | 検証内容 |
|-----------|---------|---------|
| ⑭（変更） | `testReadRegistCheckValidateInfo_NormalCase_NoIncomeData` | 収入未登録 → `hasMessages()=true`、「収入情報が未登録です。」メッセージ設定、0円支出メッセージは追加されない（即時リターン確認） |
| ⑮（追加） | `testReadRegistCheckValidateInfo_NormalCase_ZeroAmountExpenditure` | 収入あり・0円支出あり（ADDアクション）→ `hasMessages()=true`、0円支出メッセージ設定、DELETEアクションは除外確認 |
| ⑯（追加） | `testReadRegistCheckValidateInfo_NormalCase_ValidationPass` | 収入あり・0円支出なし → `hasMessages()=false` |
| ⑰（追加） | `testReadRegistCheckValidateInfo_NormalCase_ZeroAmountNonUpdate_ValidationPass` | 0円支出あり・NON_UPDATEアクション → `hasMessages()=false`（固定費区分=2由来の更新フロー0円支出は除外） |

#### IncomeAndExpenditureRegistControllerIntegrationTest

**②に対するテスト**（`readInitInfo()` 拡張）：
- 新規収支登録の初期表示で0円固定費がある場合、注意メッセージが表示されること（GROUP 6: ⑥-A）

> **アサーション注意点（⑥-A）**: `readInitInfo()` は `checkComponent.checkExpenditureRegistItemList()` も実行するため、
> テストデータ（家賃1件のみ）では買い物必須項目チェックのエラーメッセージ8件も同時に追加される。
> メッセージ件数ではなく `hasItem(containsString("「家賃」の支払金額が0円で登録されています"))` で期待メッセージの存在を検証する。

**④に対するテスト**（`getRegistCheckLoad()` 拡張）：
- セッションに0円支出（ADDアクション）がある状態で内容確認ボタン押下 → 収支登録画面に戻り0円警告メッセージ表示（GROUP 6: ⑥-B）
  - メッセージ件数 `hasSize(1)` + `hasItem(containsString("「家賃」の支出が0円から更新されていません"))` で検証
  - ※ `ACTION_TYPE_ADD` を使用（`ACTION_TYPE_NON_UPDATE` はチェック対象外のため。NON_UPDATE 除外はUseCase単体テスト⑰で確認）
- 支出が削除フラグの場合は0円チェックから除外 → 正常遷移（GROUP 6: ⑥-C）

### 4.3 テストSQL

#### IncomeAndExpenditureInitIntegrationTest（UseCase テスト）

既存の共有SQL `IncomeAndExpenditureInitIntegrationTest.sql` をそのまま流用。
テストデータはテストメソッド内でセッションを直接設定する方式。

#### IncomeAndExpenditureRegistControllerIntegrationTest（Controller テスト）

Controller専用SQLを使用（共有SQLと独立）：

```
src/test/resources/com/yonetani/webapp/accountbook/presentation/controller/account/regist/
    IncomeAndExpenditureRegistControllerIntegrationTest.sql
```

- 既存の `IncomeAndExpenditureRegistConfirmIntegrationTest.sql` と同等のデータを含む
- FIXED_COST_TABLE に0円固定費1件（`家賃`, `FIXED_COST_KUBUN=1`, `SHIHARAI_KINGAKU=0.00`）を追加
- Controller専用SQLとすることで、共有UseCaseテストSQLのデータ変更によるテスト干渉を防止

---

## 5. 留意事項

### 5.1 `clearStartFlg` との関係と更新フローのバグ修正

`ExpenditureRegistItem.clearStartFlg`（0円開始設定フラグ）は `FixedCostKubun.isClearStart()` から設定される固定費区分の属性であり、今回の「支払金額が0円の固定費」とは別の概念。
`clearStartFlg = true` の固定費は「金額を0から手入力で積み上げる用途」であり、
今回の対象（支払金額フィールドに0が設定されている固定費）とは独立している。

#### readUpdateInfo() パスで0円チェックが誤作動する問題と修正

- `ExpenditureItem.java`（152行目）において、`clearStartFlg=true` の場合は DB への登録時に支払金額を強制的に0円に変換する仕様がある
- 初期登録後（`readInitInfo()` 経由）、`clearStartFlg=true` の支出が0円として EXPENDITURE_TABLE に登録される
- 次の更新フロー（`readUpdateInfo()`）でDBからロードされる際、`clearStartFlg` は常に `false` がセットされ（287行目）、支払金額は0円になる
- このため `readRegistCheckValidateInfo()` の0円チェックが誤作動し、更新フローの内容確認ボタン押下がブロックされる問題が発生する

**修正内容**: `readRegistCheckValidateInfo()` の0円チェックに `ACTION_TYPE_NON_UPDATE` の除外フィルタを追加した（②参照）。

理由: `ACTION_TYPE_NON_UPDATE` のアイテムは `readUpdateInfo()` でDBからロードされ、ユーザが変更していない支出を意味する。これらは過去に意図的に登録された0円（clearStart 由来 または 固定費区分=1の意図的0円）であるため、再度チェックする必要はない。ユーザが明示的に0円を設定・変更した場合（ADD/UPDATE）は引き続きブロックする。

### 5.2 DB登録時の0円チェックについて

`execRegistAction()` での0円支出の二重チェックは **追加しない**。
内容確認ボタン押下時（④）でチェック済みのため、確認画面→登録ボタン押下のフローでは0円支出は存在しないことが保証される。

### 5.3 一括更新フォームへの0円対応

`FixedCostBulkUpdateForm` は **対応対象外**。
0円固定費登録は特殊ケースのため、固定費個別更新（`FixedCostInfoUpdateForm`）のみ対応とする。
一括更新では引き続き `@Min(1)` バリデーションを維持し、0円登録を禁止する。

### 5.4 旧メソッドの廃止

`readRegistCheckErrorSetInfo()` および `readZeroAmountExpenditureCheckErrorSetInfo()` は
`readRegistCheckValidateInfo()` に統合したため削除済み。
アーカイブドキュメント（Phase5設計書等）に記載の旧メソッド名は参照のみとして扱う。
