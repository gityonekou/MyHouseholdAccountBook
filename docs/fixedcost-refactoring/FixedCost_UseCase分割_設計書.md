# 固定費管理機能 UseCase分割 設計書

## 1. 概要

### 1.1 目的

`FixedCostInfoManageUseCase` が参照系・更新系の責務を混在させていたため、
DDD設計方針に従い以下の2クラスに分割する。

| UseCase | 責務 | 対象メソッド数 |
|---|---|---|
| `FixedCostInfoManageUseCase` | 参照系（画面表示情報取得） | 7 |
| `FixedCostRegistConfirmUseCase` | 更新系（DB登録・更新・削除） | 3 |

### 1.2 対象ブランチ・バージョン

- **ブランチ**: `feature-1.01-dev1`
- **バージョン**: 1.01.00
- **完了日**: 2026/04/19

---

## 2. リファクタリング前の構造

### 2.1 リファクタリング前クラス構成

```
FixedCostInfoManageUseCase（参照系7 + 更新系3 = 計10メソッド）
    ├── (参照) readInitInfo
    ├── (参照) readActSelectItemInfo
    ├── (参照) hasFixedCostInfoBySisyutuItem
    ├── (参照) readRegisteredFixedCostInfoBySisyutuItem
    ├── (参照) readAddFixedCostInfoBySisyutuItem
    ├── (参照) readUpdateFixedCostInfo
    ├── (参照) readUpdateBindingErrorSetInfo
    ├── (更新) execDelete                ← 削除系
    └── (更新) execAction                ← ADD/UPDATEを内部でif分岐

FixedCostInfoManageController
    └── FixedCostInfoManageUseCase（単一依存）
```

### 2.2 `execAction` の内部ロジック

```java
// リファクタリング前: 1メソッド内でADD/UPDATEを分岐
public FixedCostInfoManageUpdateResponse execAction(LoginUserInfo user, FixedCostInfoUpdateForm inputForm) {
    if (ACTION_TYPE_ADD.equals(inputForm.getAction())) {
        // 固定費コード採番 → 追加処理
    } else if (ACTION_TYPE_UPDATE.equals(inputForm.getAction())) {
        // 更新処理
    } else {
        throw new MyHouseholdAccountBookRuntimeException("未定義のアクション...");
    }
}
```

---

## 3. リファクタリング後の構造

### 3.1 クラス構成

```
FixedCostInfoManageUseCase（参照系のみ 7メソッド）
    ├── readInitInfo
    ├── readActSelectItemInfo
    ├── hasFixedCostInfoBySisyutuItem
    ├── readRegisteredFixedCostInfoBySisyutuItem
    ├── readAddFixedCostInfoBySisyutuItem
    ├── readUpdateFixedCostInfo
    └── readUpdateBindingErrorSetInfo

FixedCostRegistConfirmUseCase（更新系 3メソッド）【新規作成】
    ├── execDelete   ← FixedCostInfoManageUseCaseから移動
    ├── execAdd      ← execActionのADD分岐を独立メソッド化
    └── execUpdate   ← execActionのUPDATE分岐を独立メソッド化

FixedCostInfoManageController
    ├── FixedCostInfoManageUseCase（参照系）
    └── FixedCostRegistConfirmUseCase（更新系）【依存追加】
```

### 3.2 ファイルパス

| クラス | パス |
|---|---|
| `FixedCostInfoManageUseCase` | `src/main/java/.../application/usecase/itemmanage/FixedCostInfoManageUseCase.java` |
| `FixedCostRegistConfirmUseCase` | `src/main/java/.../application/usecase/itemmanage/FixedCostRegistConfirmUseCase.java` |
| `FixedCostInfoManageController` | `src/main/java/.../presentation/controller/itemmanage/FixedCostInfoManageController.java` |

---

## 4. メソッド対応表

### 4.1 FixedCostInfoManageUseCase（参照系）

| メソッド名 | 戻り値 | 説明 |
|---|---|---|
| `readInitInfo` | `FixedCostInfoManageInitResponse` | 初期表示画面情報取得（デフォルト） |
| `readActSelectItemInfo` | `FixedCostInfoManageActSelectResponse` | 処理選択画面情報取得 |
| `hasFixedCostInfoBySisyutuItem` | `boolean` | 指定支出項目に固定費が登録済みか判定 |
| `readRegisteredFixedCostInfoBySisyutuItem` | `FixedCostInfoManageInitResponse` | 登録済み支出項目の固定費一覧付き初期表示 |
| `readAddFixedCostInfoBySisyutuItem` | `FixedCostInfoManageUpdateResponse` | 追加画面表示（支出項目指定） |
| `readUpdateFixedCostInfo` | `FixedCostInfoManageUpdateResponse` | 更新画面表示（固定費コード指定） |
| `readUpdateBindingErrorSetInfo` | `FixedCostInfoManageUpdateResponse` | バリデーションエラー時の画面再表示 |

### 4.2 FixedCostRegistConfirmUseCase（更新系）

| メソッド名 | 戻り値 | トランザクション | 説明 |
|---|---|---|---|
| `execDelete` | `FixedCostInfoManageActSelectResponse` | `@Transactional` | 固定費論理削除 |
| `execAdd` | `FixedCostInfoManageUpdateResponse` | `@Transactional` | 固定費新規追加（コード採番含む） |
| `execUpdate` | `FixedCostInfoManageUpdateResponse` | `@Transactional` | 固定費更新 |

### 4.3 FixedCostInfoManageController の変更点

| エンドポイント | 変更前 | 変更後 |
|---|---|---|
| `POST /delete/` | `usecase.execDelete(...)` | `registConfirmUseCase.execDelete(...)` |
| `POST /update/` (ADD) | `usecase.execAction(...)` | `registConfirmUseCase.execAdd(...)` |
| `POST /update/` (UPDATE) | `usecase.execAction(...)` | `registConfirmUseCase.execUpdate(...)` |

---

## 5. 依存関係の変更

### 5.1 FixedCostInfoManageUseCase の依存（変更なし）

```java
private final ExpenditureItemInfoComponent expenditureItemInfoComponent;
private final CodeTableItemComponent codeTableItem;
private final FixedCostTableRepository fixedCostRepository;
```

### 5.2 FixedCostRegistConfirmUseCase の依存（新規作成）

```java
private final ExpenditureItemInfoComponent expenditureItemInfoComponent;
private final CodeTableItemComponent codeTableItem;
private final FixedCostTableRepository fixedCostRepository;
```

> **注意**: 両UseCaseは同じ依存を持つ。`getUpdateResponse` / `createFixedCost` の
> privateメソッドは両クラスで実装を持つ（重複）。
> 将来的に共通コンポーネント化を検討してもよいが、現時点では範囲外とする。

### 5.3 FixedCostInfoManageController の依存（変更あり）

```java
// 変更前
private final FixedCostInfoManageUseCase usecase;

// 変更後
private final FixedCostInfoManageUseCase usecase;           // 参照系
private final FixedCostRegistConfirmUseCase registConfirmUseCase;  // 更新系（追加）
```

---

## 6. テスト設計

### 6.1 テストファイル構成

| テストクラス | テスト数 | 対象 | SQLファイル |
|---|---|---|---|
| `FixedCostInfoManageUseCaseIntegrationTest` | 10 | 参照系UseCase全メソッド | `FixedCostInfoManageIntegrationTest.sql` |
| `FixedCostRegistConfirmUseCaseIntegrationTest` | 5 | 更新系UseCase全メソッド | `FixedCostInfoManageIntegrationTest.sql` |
| `FixedCostInfoManageControllerIntegrationTest` | 12 | Controller全エンドポイント | `FixedCostInfoManageIntegrationTest.sql` |

### 6.2 テストデータ（FixedCostInfoManageIntegrationTest.sql）

- **ユーザ**: `user01`
- **支出項目マスタ**: 0001〜0060（60件）
- **固定費テーブル**: 3件

| 固定費コード | 支払名 | 支出項目コード | 支払月 | 支払日 | 支払金額 | 備考 |
|---|---|---|---|---|---|---|
| `0001` | 家賃 | 0030（家賃） | 毎月（00） | 27日 | 60,000円 | `execUpdate` テスト対象 |
| `0002` | 電気代概算 | 0037（電気代） | 毎月（00） | 27日 | 12,000円 | — |
| `0003` | 国民年金保険 | 0015（国民年金） | 奇数月（20） | 月初営業日（00） | 16,590円 | `execDelete` テスト対象 |

- **追加テスト対象支出項目**: `0035`（自由用途積立金）← 固定費未登録（`execAdd` テスト用）

### 6.3 DB取得順序の注意

固定費一覧は `SISYUTU_ITEM_SORT` 昇順でソートされるため、実際の取得順は以下の通り：

```
1位: 0003（国民年金保険 ← sort: 0201010000）
2位: 0001（家賃         ← sort: 0303010000）
3位: 0002（電気代概算   ← sort: 0306010000）
```

### 6.4 金額フォーマット

`FixedCostPaymentAmount.toFormatString()` は `60,000円` 形式（`¥` マークなし）を返す。

---

## 7. テストシナリオ一覧

### 7.1 FixedCostInfoManageUseCaseIntegrationTest

| # | テスト名 | 対象メソッド | 検証内容 |
|---|---|---|---|
| ① | 固定費3件一覧表示 | `readInitInfo` | リスト3件、合計金額（奇数月88,590円・偶数月72,000円）、DB順序 |
| ② | 処理選択画面_家賃 | `readActSelectItemInfo` | 固定費詳細情報（コード・支払名・支払月・金額）、固定費一覧3件 |
| ③ | 存在しない固定費コードで例外 | `readActSelectItemInfo` | `MyHouseholdAccountBookRuntimeException` 発生 |
| ④ | 登録済み判定_登録あり | `hasFixedCostInfoBySisyutuItem` | 0030(家賃) → `true` |
| ⑤ | 登録済み判定_登録なし | `hasFixedCostInfoBySisyutuItem` | 0035(自由用途積立金) → `false` |
| ⑥ | 登録済み初期表示_0030 | `readRegisteredFixedCostInfoBySisyutuItem` | 登録済みフラグtrue、固定費1件（0001のみ）、支出項目コード情報あり |
| ⑦ | 追加画面_0035 | `readAddFixedCostInfoBySisyutuItem` | フォームのaction=ADD、sisyutuItemCode=0035、選択ボックス設定あり |
| ⑧ | 更新画面_固定費0001 | `readUpdateFixedCostInfo` | フォーム全フィールドの値確認（code/name/kubun/tuki/day/kingaku） |
| ⑨ | 存在しない固定費コードで例外 | `readUpdateFixedCostInfo` | `MyHouseholdAccountBookRuntimeException` 発生 |
| ⑩ | バリデーションエラー時画面表示 | `readUpdateBindingErrorSetInfo` | フォーム値維持、選択ボックス設定あり |

### 7.2 FixedCostRegistConfirmUseCaseIntegrationTest

| # | テスト名 | 対象メソッド | 検証内容 |
|---|---|---|---|
| ① | 固定費0003論理削除 | `execDelete` | トランザクション完了、DELETE_FLG=true、他レコード影響なし |
| ② | 存在しない固定費コードで例外 | `execDelete` | `MyHouseholdAccountBookRuntimeException` 発生 |
| ③ | 新規固定費追加_0035 | `execAdd` | トランザクション完了、DB4件、追加レコードの全フィールド確認 |
| ⑤ | 固定費0002更新 | `execUpdate` | トランザクション完了、固定費名・支払金額更新確認、件数変化なし |
| ⑥ | 存在しない固定費コードで更新例外 | `execUpdate` | `MyHouseholdAccountBookRuntimeException` 発生 |

### 7.3 FixedCostInfoManageControllerIntegrationTest

| # | エンドポイント | 検証内容 |
|---|---|---|
| 1 | `GET /initload/` | view名・fixedCostItemList 3件・loginUserName |
| 2 | `GET /select?fixedCostCode=0001` | view名・fixedCostInfo・fixedCostItemList 3件 |
| 3 | `GET /addload?sisyutuItemCode=0035` | view名(Update)・fixedCostInfoUpdateForm |
| 4 | `GET /addload?sisyutuItemCode=0030` | view名(Init)・registeredFlg=true |
| 5 | `POST /updateload/ (actionAdd)` | view名(Update)・fixedCostInfoUpdateForm |
| 6 | `POST /updateload/ (actionUpdate)` | view名(Update)・fixedCostInfoUpdateForm |
| 7 | `POST /updateload/ (actionCancel)` | view名(Init)・fixedCostItemList 3件 |
| 8 | `POST /delete/` | 3xxリダイレクト → /updateComplete/ |
| 9 | `POST /update/ (バリデーションNG)` | view名(Update)に戻る |
| 10 | `POST /update/ (ADD成功)` | 3xxリダイレクト → /updateComplete/ |
| 11 | `POST /update/ (UPDATE成功)` | 3xxリダイレクト → /updateComplete/ |
| 12 | `GET /updateComplete/` | view名(Init)・fixedCostItemList 3件 |

---

## 8. 変更ファイル一覧

### 8.1 修正ファイル

| ファイル | 変更内容 |
|---|---|
| `FixedCostInfoManageUseCase.java` | `execDelete`・`execAction` を削除、`createFixedCost` privateメソッドを削除、`@Transactional` import を削除 |
| `FixedCostInfoManageController.java` | `FixedCostRegistConfirmUseCase` 依存追加、`postDelete`・`postUpdate` の呼び出し先変更 |

### 8.2 新規作成ファイル

| ファイル | 内容 |
|---|---|
| `FixedCostRegistConfirmUseCase.java` | 更新系UseCase（execDelete/execAdd/execUpdate） |
| `FixedCostInfoManageIntegrationTest.sql` | 統合テスト用共通テストデータ |
| `FixedCostInfoManageUseCaseIntegrationTest.java` | 参照系UseCaseの統合テスト（10件） |
| `FixedCostRegistConfirmUseCaseIntegrationTest.java` | 更新系UseCaseの統合テスト（5件） |
| `FixedCostInfoManageControllerIntegrationTest.java` | Controller統合テスト（12件） |

---

## 9. テスト実行結果

```
Tests run: 835, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

全835件グリーン確認済み。
