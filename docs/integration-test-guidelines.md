# 結合テスト 詳細ガイドライン（UseCase層 / Controller層）

**作成日**: 2026-02-15
**適用範囲**: アプリケーション層（UseCase）の結合テスト、プレゼンテーション層（Controller）の結合テスト
**上位方針書**: `docs/specifications/07_テスト方針書.md` セクション4（統合テストの方針）
**参考**: Phase5 Step2で作成した6つの結合テストクラス（76テスト）の実績に基づく

---

## 目次

1. [目的と適用範囲](#1-目的と適用範囲)
2. [テストクラスの基本構成（UseCase層）](#2-テストクラスの基本構成)
3. [レスポンス検証ルール](#3-レスポンス検証ルール)
4. [アサーション（検証）ルール](#4-アサーション検証ルール)
5. [セッションデータの検証パターン](#5-セッションデータの検証パターン)
6. [DB検証パターン](#6-db検証パターン)
7. [テストデータ設計](#7-テストデータ設計)
8. [テストケース設計の観点](#8-テストケース設計の観点)
9. [テストクラスのJavadoc記述ルール](#9-テストクラスのjavadoc記述ルール)
10. [Controller層テストガイドライン](#10-controller層テストガイドライン)

---

## 1. 目的と適用範囲

### 1.1 本ガイドラインの目的

本ガイドラインは、アプリケーション層（UseCase）の結合テストを作成する際の**詳細な実装ルール**を定めるものです。
`docs/specifications/07_テスト方針書.md` の上位方針に基づき、Phase5 Step2の実績から確立した具体的なパターンとルールを文書化しています。

### 1.2 適用範囲

- **対象レイヤー**:
  - アプリケーション層（`application/usecase/`配下のUseCaseクラス） → セクション2〜9
  - プレゼンテーション層（`presentation/controller/`配下のControllerクラス） → セクション10
- **テスト種別**:
  - UseCase層: `@SpringBootTest` を使用した結合テスト（UseCase → Component → Repository → DB）
  - Controller層: `@SpringBootTest` + MockMvc（`standaloneSetup`）を使用した結合テスト
- **対象外**: ドメイン層単体テスト

### 1.3 関連ドキュメント

| ドキュメント | 内容 |
|---|---|
| `docs/specifications/07_テスト方針書.md` | テスト全体方針（カバレッジ目標、命名規則など） |
| `docs/test-data-design-rules.md` | テストデータの設計ルール（コード採番、階層構造など） |

---

## 2. テストクラスの基本構成

### 2.1 アノテーション構成

```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
    "/sql/initsql/schema_test.sql",
    "/com/yonetani/webapp/.../XxxIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
class XxxIntegrationTest {

    @Autowired
    private XxxUseCase useCase;

    // テストメソッド
}
```

**必須アノテーション**:
- `@SpringBootTest` - Spring Boot統合テスト
- `@ActiveProfiles("test")` - テストプロファイル（H2データベース）
- `@Transactional` - 各テスト後の自動ロールバック
- `@Sql` + `@SqlConfig(encoding = "UTF-8")` - テストデータ投入（UTF-8エンコーディング必須）

### 2.2 テスト対象の注入方式

```java
// UseCaseクラスを直接@Autowired（Mockは使用しない）
@Autowired
private XxxUseCase useCase;
```

- UseCaseのメソッドを**直接呼び出して**テスト
- MockMvcは使用しない（Controller層のテストではないため）
- Mockitoのモックは使用しない

### 2.3 共通ヘルパーメソッド

テストクラス内に共通のヘルパーメソッドを用意し、テストデータ準備の重複を排除する。

```java
/**
 * テスト用のログインユーザ情報を作成します。
 */
private LoginUserInfo createLoginUser() {
    return LoginUserInfo.from("user01", "テストユーザ01");
}
```

---

## 3. レスポンス検証ルール

### 3.1 基本方針：レスポンスクラスのgetterで直接検証する

**必須ルール**: UseCaseメソッドの戻り値（レスポンスクラス）のフィールドは、**getterメソッドを通じて直接検証する**。

```java
// ○ 正しい検証方法：getterで直接アクセス
assertEquals("固定費(課税)＞水光熱通費＞電気代", response.getSisyutuItemName());
assertEquals(2, response.getIncomeListInfo().size());
assertNotNull(response.getExpenditureSelectItemForm());

// × 禁止パターン①：build() → ModelAndView経由での検証
ModelAndView mav = response.build();
assertEquals("固定費(課税)＞水光熱通費＞電気代", mav.getModel().get("sisyutuItemName"));

// × 禁止パターン②：build() → ModelMap経由での検証（①と同等）
ModelMap model = response.setLoginUserName("...").build().getModelMap();
assertEquals("固定費(課税)＞水光熱通費＞電気代", model.getAttribute("sisyutuItemName"));
List<FixedCostItem> list = (List<FixedCostItem>) model.getAttribute("fixedCostItemList");
```

**理由**:
- `build()`はプレゼンテーション層（Controller→画面）の責務であり、UseCase結合テストの検証対象外
- getterによる直接アクセスの方がテストの意図が明確
- 型安全（ModelAndViewの`get()`・ModelMapの`getAttribute()`は`Object`を返すためキャストが必要）

### 3.2 レスポンスクラスへの@Getter追加ルール

テスト対象のレスポンスクラスにgetterが存在しない場合、**Lombokの`@Getter`アノテーションを追加する**。

**追加の基準**:
- テストで検証が必要なフィールドにgetterが存在しない場合に追加
- クラスレベルまたはフィールドレベルで追加（既存の`@Setter`との共存可）
- 親クラスのフィールドも検証対象であれば、親クラスにも追加

```java
// 例：ExpenditureItemSelectResponse にクラスレベル@Getterを追加
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter  // ← テスト検証のために追加
public class ExpenditureItemSelectResponse extends AbstractExpenditureItemInfoManageResponse {
    @Setter
    private ExpenditureSelectItemForm expenditureSelectItemForm;
    @Setter
    private String sisyutuItemName;
    // ...
}

// 例：親クラスのフィールドにフィールドレベル@Getterを追加
public abstract class AbstractExpenditureItemInfoManageResponse extends AbstractResponse {
    @Getter  // ← テスト検証のために追加
    private List<ExpenditureItem> expenditureItemList = new ArrayList<>();
}
```

### 3.3 privateインナークラスのpublic化ルール

レスポンスクラスのインナークラス（内部クラス）が`private`である場合、テストからフィールド値を検証できない。
テストで**インナークラスのフィールド値の検証が必要な場合**は、`private`を`public`に変更する。

```java
// ○ テスト検証のためにpublicに変更
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public static class ExpenditureItem {  // private → public に変更
    private final String sisyutuItemCode;
    private final String sisyutuItemName;
    // ...
}

// × privateのままではテストからフィールド値にアクセスできない
private static class ExpenditureItem {
    // テストで response.getExpenditureItemList().get(0).getSisyutuItemCode() が不可
}
```

**判断基準**:
- リストの`size()`のみの検証で十分な場合 → 変更不要
- リスト内の各要素のフィールド値を検証する必要がある場合 → `public`に変更

**注意点**:
- `@RequiredArgsConstructor(access = AccessLevel.PRIVATE)` はそのまま維持する（インスタンス生成はクラス内部のファクトリメソッドに限定）
- `@Getter`がクラスに付与されていることを確認する（フィールド値取得のため）
- ファクトリメソッド（`from()`等）のアクセスレベルは用途に応じて判断する（外部から生成不要であれば`private`のまま）

---

## 4. アサーション（検証）ルール

### 4.1 具体値による検証を優先する

**必須ルール**: 検証可能なフィールドは`assertEquals`で**期待する具体値**と比較する。`assertNotNull`のみの検証は不十分。

```java
// ○ 具体値で検証
assertEquals("固定費(課税)＞水光熱通費＞電気代", response.getSisyutuItemName());
assertEquals("0037", response.getExpenditureSelectItemForm().getSisyutuItemCode());
assertEquals(new BigDecimal("380000.00"), syuusiItem.getRegularIncomeAmount().getValue());

// △ assertNotNullのみ（具体値検証が可能なのに使う場合は不十分）
assertNotNull(response.getSisyutuItemName());
```

**`assertNotNull`が適切な場合**:
- オブジェクトの存在確認が目的で、具体値の検証が不要またはテスト観点外の場合

### 4.2 件数検証

リスト系のフィールドは、件数を`assertEquals`で検証する。

```java
assertEquals(6, response.getExpenditureItemList().size(), "トップレベルのカテゴリが6件");
assertEquals(2, response.getIncomeListInfo().size(), "収入一覧は2件");
```

### 4.3 検証メッセージの付与

重要な検証には第3引数で検証意図を記述する。

```java
assertEquals(6, response.getExpenditureItemList().size(), "トップレベル（Level1）のカテゴリが6件であること");
assertFalse(form.isEventCodeRequired(), "非イベント系項目のためeventCodeRequired=false");
assertTrue(response.isTransactionSuccessFull(), "transactionSuccessFull=true");
```

---

## 5. セッションデータの検証パターン

### 5.1 DATA_TYPE × ACTION_TYPE の組み合わせ

セッション操作（収入・支出の追加/更新/削除）をテストする際は、以下の組み合わせを意識する。

| DATA_TYPE | ACTION_TYPE | 意味 | テスト観点 |
|---|---|---|---|
| `DATA_TYPE_NEW` | `ACTION_TYPE_ADD` | 新規追加 | セッションリストに追加される |
| `DATA_TYPE_LOAD` | `ACTION_TYPE_UPDATE` | DB既存データの更新 | セッション内の該当データが更新される |
| `DATA_TYPE_NEW` | `ACTION_TYPE_DELETE` | 新規データの削除 | セッションリストから**完全削除** |
| `DATA_TYPE_LOAD` | `ACTION_TYPE_DELETE` | DB既存データの削除 | ACTION=DELETEでセッションに**残る**（論理削除） |
| `DATA_TYPE_LOAD` | `ACTION_TYPE_NON_UPDATE` | 変更なし | DB操作スキップ |

**重要**: 削除のテストは DATA_TYPE による動作の違いを必ず両方テストする。

### 5.2 セッションリストの操作検証

```java
// 追加後のリストサイズ確認
assertEquals(1, response.getIncomeRegistItemList().size(), "収入が1件追加されていること");

// 追加されたデータの具体値確認
IncomeRegistItem addedItem = response.getIncomeRegistItemList().get(0);
assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, addedItem.getAction());
assertEquals("給料", addedItem.getIncomeName());

// 削除後のリスト（DATA_TYPE_NEWの場合はリストから消える）
assertEquals(0, response.getIncomeRegistItemList().size(), "新規データ削除でリストから消えること");

// 削除後のリスト（DATA_TYPE_LOADの場合はDELETEアクションで残る）
assertEquals(1, response.getIncomeRegistItemList().size());
assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_DELETE,
    response.getIncomeRegistItemList().get(0).getAction());
```

---

## 6. DB検証パターン

### 6.1 DB更新結果の検証方式

DB更新を伴うテストでは、`@Autowired`したリポジトリを使用してDB状態を直接検証する。

```java
@Autowired
private IncomeTableRepository incomeRepository;

@Autowired
private ExpenditureTableRepository expenditureRepository;

@Autowired
private IncomeAndExpenditureTableRepository incomeAndExpenditureRepository;

// DB検証
SearchQueryUserIdAndYearMonth searchQuery = createSearchQuery("202512");
int incomeCount = incomeRepository.countById(searchQuery);
assertEquals(2, incomeCount, "INCOME_TABLEに2件登録されていること");
```

### 6.2 金額計算の整合性検証

DB更新後の金額は、入力データとの整合性を具体値で検証する。

```java
IncomeAndExpenditureItem syuusiItem = incomeAndExpenditureRepository.select(searchQuery);
// 収入金額 = 350,000 + 30,000 = 380,000
assertEquals(new BigDecimal("380000.00"), syuusiItem.getRegularIncomeAmount().getValue());
// 支出金額 = 10000+2000+1000+5000+3000+5000+10000+2000 = 38,000
assertEquals(new BigDecimal("38000.00"), syuusiItem.getExpenditureAmount().getValue());
```

### 6.3 transactionSuccessFullフラグの検証

DB更新完了後・キャンセル完了後は、`transactionSuccessFull`フラグが`true`に設定されることを検証する。

```java
assertTrue(response.isTransactionSuccessFull(), "transactionSuccessFull=true");
```

### 6.4 完了メッセージの検証

DB更新完了後のメッセージも具体値で検証する。

```java
assertTrue(response.hasMessages());
assertEquals("2025年12月度の収支情報を登録しました。", response.getMessagesList().get(0));
```

### 6.5 ロールバックテストのクラス分離パターン

DB更新処理で**例外発生時のロールバック**を検証する場合は、通常の `@Transactional` 付きテストクラスとは**別クラス**に分離する必要がある。

**理由**: `@Transactional` 付きテストクラスでは、テスト実行と被テストコードが同一トランザクション内で動作するため、サービス層の `@Transactional` がロールバックしても、同一トランザクション内でその変更が可視状態のままになる。実際のロールバックを証明するには、テストクラス自身が `@Transactional` を持たないことが必要。

#### 実装パターン

```java
@SpringBootTest
@ActiveProfiles("test")
// @Transactional を付与しない（サービス層の@Transactionalが本物のトランザクション境界になる）
@Sql(scripts = {
    "/path/to/XxxRollbackTest-cleanup.sql",  // まず既存データを完全削除
    "/sql/initsql/schema_test.sql",
    "/path/to/XxxIntegrationTest.sql"         // テストデータ投入
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
   config = @SqlConfig(encoding = "UTF-8"))
@Sql(scripts = {
    "/path/to/XxxRollbackTest-cleanup.sql"   // テスト後に後片付け
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
   config = @SqlConfig(encoding = "UTF-8"))
class XxxRollbackTest {

    @Autowired
    private XxxUseCase useCase;
    @Autowired
    private XxxTableRepository repository;

    @Test
    @DisplayName("ロールバック確認：例外発生時に全件がロールバックされること")
    void testRollback_ExceptionOccurs() {
        // When: 例外が発生する操作を実行
        assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> {
            useCase.execRegistAction(user, incomeList, expenditureList);
        });

        // Then: DBにデータが残っていないこと（ロールバックされた）
        assertEquals(0, repository.countById(searchQuery), "ロールバックにより0件");
    }
}
```

#### クリーンアップSQLの必要性

`@Transactional` がないため、テスト後にDBにデータが残る。`executionPhase = AFTER_TEST_METHOD` の `@Sql` でクリーンアップを行うとともに、テスト開始前にも `BEFORE_TEST_METHOD` で確実にクリーンアップする。

```sql
-- XxxRollbackTest-cleanup.sql
DELETE FROM INCOME_TABLE WHERE USER_ID = 'user01';
DELETE FROM EXPENDITURE_TABLE WHERE USER_ID = 'user01';
DELETE FROM INCOME_AND_EXPENDITURE_TABLE WHERE USER_ID = 'user01';
DELETE FROM SISYUTU_KINGAKU_TABLE WHERE USER_ID = 'user01';
```

#### ロールバックテストクラスの命名規則

- クラス名: `XxxRollbackTest`（`IntegrationTest` サフィックスではなく `RollbackTest`）
- 通常のテストクラス（`XxxIntegrationTest`）とは別ファイルに分離する
- テストデータSQLは通常テストの `XxxIntegrationTest.sql` を再利用可能

---

## 7. テストデータ設計

### 7.1 SQLファイルの配置

テストデータSQLは、テストクラスと同じパッケージ構造でresources配下に配置する。

```
src/test/resources/com/yonetani/webapp/accountbook/application/usecase/xxx/
└── XxxIntegrationTest.sql
```

### 7.2 テストデータの基本方針

- **テストクラスごとに独立したSQLファイル**を作成する（テスト間の依存を排除）
- SQLファイル内にコメントでデータの目的を明記する
- マスタデータ（支出項目コード0001-0060など）は本番と同等のデータを使用する
- 詳細なデータ設計ルールは `docs/test-data-design-rules.md` を参照

### 7.3 セッションデータの準備

セッションデータ（`IncomeRegistItem`、`ExpenditureRegistItem`など）はヘルパーメソッドで作成する。

```java
private List<ExpenditureRegistItem> createNewExpenditureList() {
    List<ExpenditureRegistItem> list = new ArrayList<>();
    list.add(ExpenditureRegistItem.from(
        MyHouseholdAccountBookContent.DATA_TYPE_NEW,
        MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
        "20251201120000001", "0051", "", "飲食(無駄遣いなし)", "1",
        "飲食詳細", "05", new BigDecimal("10000"), false));
    return list;
}
```

**注意点**:
- `ExpenditureRegistItem`の`siharaiDate`（支払日）は、ADD/UPDATEアクション時に有効な日付値（例: `"05"`, `"15"`, `"25"`）を設定すること。空文字`""`はバリデーションエラーになる
- NON_UPDATEアクションでは`siharaiDate`は検証されないため空文字でも可

---

## 8. テストケース設計の観点

### 8.1 必須テスト観点一覧

アプリケーション層の結合テストでは、以下の観点を網羅する。

#### 正常系

| 観点 | 説明 | 例 |
|---|---|---|
| 基本動作 | メソッドが正常に実行され、期待するレスポンスが返ること | 画面初期表示、一覧取得 |
| 具体的なフィールド値 | レスポンスの各フィールドが期待する具体値であること | 支出項目名の＞区切り階層表示 |
| リスト件数 | 一覧系のリストが期待する件数であること | 支出項目一覧6件（Level1カテゴリ数） |
| セッション操作結果 | 追加/更新/削除後のセッションリストが正しいこと | 追加後リストサイズ+1 |
| DB更新結果 | INSERT/UPDATE/DELETE後のDB状態が正しいこと | 件数検証、金額計算の整合性 |
| 完了フラグ・メッセージ | transactionSuccessFull、完了メッセージが正しいこと | "YYYY年MM月度の収支情報を登録しました。" |

#### 異常系

| 観点 | 説明 | 例 |
|---|---|---|
| 不正入力 | 不正な引数で例外がスローされること | 存在しない収入コードで更新 |
| 必須パラメータ欠落 | null/空リストで例外がスローされること | 収入リストnull |
| データ不整合 | DBデータの不整合で例外がスローされること | 収入テーブルなし・支出テーブルあり |

### 8.2 削除アクション設定データの除外確認

画面表示用一覧を返すメソッドでは、**DELETEアクション設定されたデータが一覧から除外される**ことを検証する。

```java
// Given: 3件中1件がDELETE
// When: 一覧表示
// Then: DELETE除外で2件
assertEquals(2, response.getExpenditureListInfo().size(), "支出一覧はDELETE除外で2件");
```

### 8.3 データ区分（kubun）別の検証

ドメインロジックが区分値によって分岐する場合は、各区分をテストで網羅する。

```java
// 例：収入区分による金額マッピング
// kubun "1"(給与), "2"(副業), "4"(その他) → RegularIncomeAmount
// kubun "3"(積立からの取崩し) → WithdrawingAmount
```

### 8.4 支出項目名の階層表示検証

支出項目の階層表示（＞区切り）は、各レベルでの表示を検証する。

```java
// Level3: 3階層
assertEquals("固定費(課税)＞水光熱通費＞電気代", response.getSisyutuItemName());
// Level2: 2階層
assertEquals("固定費(課税)＞水光熱通費", response.getSisyutuItemName());
// Level1: 1階層（＞なし）
assertEquals("事業経費", response.getSisyutuItemName());
```

### 8.5 収入区分（kubun）別の金額マッピング検証

収入区分によって金額の格納先が変わる場合は、**全区分のパターン**を同一テストで検証する。

| kubun値 | 区分名 | INCOME_KINGAKU（収入金額） | WITHDREW_KINGAKU（取崩金額） |
|---------|--------|--------------------------|---------------------------|
| "1" | 給与等 | 加算される | 加算されない |
| "2" | 副業等 | 加算される | 加算されない |
| "3" | 積立からの取崩し | **加算されない** | 加算される |

**重要**: kubun="3" の収入金額は `INCOME_KINGAKU` ではなく `WITHDREW_KINGAKU` に加算される。両者が混在するテストデータを用意し、金額の格納先が仕様通りであることを確認する。

```java
// 収入3件（kubun=1:350000、kubun=2:30000、kubun=3:15000）登録後
// INCOME_KINGAKU = 350,000 + 30,000 = 380,000（kubun=3は加算しない）
assertEquals(new BigDecimal("380000.00"), syuusiItem.getRegularIncomeAmount().getValue(),
    "INCOME_KINGAKUはkubun=1,2のみ加算されること");
// WITHDREW_KINGAKU = 15,000（kubun=3のみ）
assertEquals(new BigDecimal("15000.00"), syuusiItem.getWithdrawingAmount().getValue(),
    "WITHDREW_KINGAKUはkubun=3のみ加算されること");
```

### 8.6 clearStartFlgによる支出金額制御の検証

固定費の開始月判定フラグ（`clearStartFlg`）によって、登録される支出金額が変わる。

| clearStartFlg | EXPENDITURE_KINGAKU（支出金額） | EXPENDITURE_ESTIMATE_KINGAKU（支出予定金額） |
|---|---|---|
| `false`（通常） | 入力値がそのまま登録 | 入力値がそのまま登録 |
| `true`（固定費開始月） | **0円（または null）** | 入力値が登録される（予定金額は保持） |

**テスト観点**: 両パターンのテストデータを同一テストに含め、`clearStartFlg=true` のデータが支出合計から除外されることも合わせて検証する。

```java
// clearStartFlg=false: 通常登録
// → EXPENDITURE_KINGAKU=12,000、EXPENDITURE_ESTIMATE_KINGAKU=12,000

// clearStartFlg=true（電気代0037）:
// → EXPENDITURE_KINGAKU=0、EXPENDITURE_ESTIMATE_KINGAKU=12,000（入力値は保持）

// 収支テーブルのEXPENDITURE_KINGAKU集計はclearStartFlg=trueを除外すること
assertEquals(new BigDecimal("38000.00"), syuusiItem.getExpenditureAmount().getValue(),
    "EXPENDITURE_KINGAKUはclearStartFlg=trueを除外した合計であること");
// EXPENDITURE_ESTIMATE_KINGAKUはclearStartFlg=trueも含む
assertEquals(new BigDecimal("50000.00"), syuusiItem.getExpenditureEstimateAmount().getValue(),
    "EXPENDITURE_ESTIMATE_KINGAKUはclearStartFlg=trueを含む全支出予定金額の合計であること");
```

### 8.7 SISYUTU_KINGAKU_TABLE（支出金額テーブル）の集計ルール検証

同一支出項目コードへの複数支出登録がある場合、支出金額テーブルへの集計ルールを検証する。

| 項目 | 新規登録時 | 更新時 |
|------|----------|--------|
| SISYUTU_KINGAKU（支出金額） | 支出テーブルの支出金額合計 | 再計算後の合計 |
| SISYUTU_YOTEI_KINGAKU（支出予定金額） | 入力値の合計（新規=支出金額と同値） | **更新されない**（初回値を保持） |
| SISYUTU_SIHARAI_DATE（支払日） | MAX(各支払日) | MAX(削除以外の有効支払日) |

**テスト観点**:

```java
// 食費(0051)に3件登録（5,000+3,000+5,000）後のSISYUTU_KINGAKU_TABLE
SisyutuKingakuItem sisyutuItem = sisyutuKingakuRepository.select(searchQuery, "0051");
// 支出金額 = 5,000 + 3,000 + 5,000 = 13,000
assertEquals(new BigDecimal("13000.00"), sisyutuItem.getSisyutuKingaku().getValue(),
    "SISYUTU_KINGAKUは全支出の合計であること");
// 予定金額（新規登録時は入力値の合計）
assertEquals(new BigDecimal("13000.00"), sisyutuItem.getSisyutuYoteiKingaku().getValue(),
    "SISYUTU_YOTEI_KINGAKUは新規登録時に入力値と同値であること");
// 支払日（3件の支払日からMAX）
assertEquals(LocalDate.of(2025, 12, 15), sisyutuItem.getSisyutuSiharaiDate(),
    "SISYUTU_SIHARAI_DATEはMAX(各支払日)であること");

// 更新時：SISYUTU_YOTEI_KINGAKUは変更されないこと
assertEquals(beforeYoteiKingaku, afterItem.getSisyutuYoteiKingaku().getValue(),
    "SISYUTU_YOTEI_KINGAKUは更新操作で変更されないこと");
```

### 8.8 混合アクション（NON_UPDATE/UPDATE/ADD/DELETE）の網羅的検証

DB更新処理のテストでは、単一アクションだけでなく、**全アクション種別の混合**シナリオを検証する。

**検証すべきアクション組み合わせ**:

| ACTION_TYPE | DB操作 | テスト観点 |
|---|---|---|
| `NON_UPDATE` | DB変更なし | 既存データが変更されないこと |
| `UPDATE` | 既存データ更新 | 更新後の値が正しいこと |
| `ADD` | 新規INSERT | コードが自動採番され正しく登録されること |
| `DELETE` | 論理削除（DELETE_FLG=true） | 最終集計（収支金額・支出金額テーブル）から除外されること |

```java
// 同一テストメソッド内でNON_UPDATE/UPDATE/ADD/DELETE混在のセッションデータを用意
List<IncomeRegistItem> updateList = new ArrayList<>();
// NON_UPDATE: 変更なし（UPDATE件数=0）
updateList.add(IncomeRegistItem.from(NON_UPDATE, NON_UPDATE, "01", "1", "", "給与", 350000));
// UPDATE: 既存データ更新
updateList.add(IncomeRegistItem.from(LOAD, UPDATE, "02", "2", "", "副業", 40000));
// ADD: 新規追加
updateList.add(IncomeRegistItem.from(NEW, ADD, null, "3", "", "取崩", 15000));
// DELETE: 論理削除（集計から除外）
updateList.add(IncomeRegistItem.from(LOAD, DELETE, "04", "3", "", "削除対象", 10000));
```

---

## 9. テストクラスのJavadoc記述ルール

### 9.1 クラスヘッダーに記載する情報

テストクラスの先頭Javadocに以下の情報を必ず記載する。

```java
/**
 * 機能名の統合テストクラスです。
 *
 * <pre>
 * UseCaseクラス名 の以下メソッドをテストします。
 *
 * [テスト方針]
 * ・リファクタリング前の振る舞いを記録
 * ・データベースアクセスを含む全レイヤーの結合テスト
 * ・正常系・異常系の両方をカバー
 *
 * [対象メソッド]
 * 1. methodName1 - メソッドの概要
 * 2. methodName2 - メソッドの概要
 *
 * [テストシナリオ]
 * methodName1:
 *   1. 正常系: テストケース名
 *   2. 異常系: テストケース名
 * methodName2:
 *   3. 正常系: テストケース名
 *
 * [テストデータ]
 * - 使用するマスタデータの概要
 * - テストデータの特記事項
 * </pre>
 */
```

### 9.2 テストメソッドのJavadoc

各テストメソッドには検証内容を明記する。

```java
/**
 *<pre>
 * テスト①：正常系：テストケース名
 *
 * 【検証内容】
 * ・検証する観点1
 * ・検証する観点2
 * ・検証する観点3
 *</pre>
 */
@Test
@DisplayName("正常系：テストケース名")
void testMethodName_NormalCase() {
```

---

---

## 10. Controller層テストガイドライン

### 10.1 目的と位置づけ

Controller層の結合テストは、**HTTPリクエスト/レスポンスの視点**でControllerが正しく動作することを検証します。UseCase層テスト（セクション2〜9）がビジネスロジックを検証するのに対し、Controller層テストは以下を検証します。

- URLパス・HTTPメソッドのマッピング正確性
- パラメータバインディング（フォーム値の受け取り）
- ビュー名の正確性
- モデル属性の設定（`model().attributeExists()`等）
- セッション操作（`verify()`）
- リダイレクト先URLの正確性
- HTTP ステータスコード

### 10.2 アノテーション構成

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
    "/sql/initsql/schema_test.sql",
    "/com/yonetani/webapp/.../XxxIntegrationTest.sql"  // UseCase層SQLを再利用
}, config = @SqlConfig(encoding = "UTF-8"))
public class XxxControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private XxxUseCase useCase;  // 本物のSpring Bean

    @Mock
    private LoginUserSession mockLoginUserSession;  // モック化

    @Mock
    private XxxRegistSession mockXxxRegistSession;  // モック化

    @BeforeEach
    void setupMockMvc() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders
            .standaloneSetup(new XxxController(useCase, mockLoginUserSession, mockXxxRegistSession))
            .setControllerAdvice(new MyHouseholdAccountBookControllerAdvice(mockLoginUserSession))
            .build();

        // デフォルトセッション設定（全テスト共通）
        doReturn(createLoginUser()).when(mockLoginUserSession).getLoginUserInfo();
        when(mockXxxRegistSession.getTargetYearMonth()).thenReturn("202511");
        when(mockXxxRegistSession.getIncomeRegistItemList()).thenReturn(new ArrayList<>());
        when(mockXxxRegistSession.getExpenditureRegistItemList()).thenReturn(new ArrayList<>());
    }
}
```

**必須アノテーション**:
- `@SpringBootTest` - Spring Boot統合テスト（UseCase本物Beanのロードに必要）
- `@AutoConfigureMockMvc` - MockMvcの自動設定（`standaloneSetup` を使うが宣言は必要）
- `@ActiveProfiles("test")` - テストプロファイル（H2データベース）
- `@Transactional` - 各テスト後の自動ロールバック
- `@Sql` + `@SqlConfig(encoding = "UTF-8")` - テストデータ投入（UTF-8エンコーディング必須）

### 10.3 UseCase vs セッションBeanのモック戦略

| 種別 | 注入方式 | 理由 |
|------|----------|------|
| UseCaseクラス | `@Autowired`（本物のSpring Bean） | DBアクセス含む実処理を実行し、結合テストとしての価値を持たせる |
| LoginUserSession | `@Mock`（Mockitoモック） | テスト用ユーザ情報を任意に設定するため |
| XxxRegistSession | `@Mock`（Mockitoモック） | セッション状態を任意に制御するため |

**MockMvcセットアップ方式**: `standaloneSetup` を使用する。`@Autowired MockMvc` ではなく、コンストラクタにモック化したセッションBeanを渡してControllerインスタンスを生成する。

```java
// ○ standaloneSetup（セッションモックを渡せる）
mockMvc = MockMvcBuilders
    .standaloneSetup(new IncomeAndExpenditureRegistController(useCase, mockLoginUserSession, mockSession))
    .setControllerAdvice(new MyHouseholdAccountBookControllerAdvice(mockLoginUserSession))
    .build();

// × webAppContextSetup（セッションBeanのモック化が困難）
```

### 10.4 @BeforeEach でのデフォルトセッション設定

各テストで共通して必要なセッション設定は `@BeforeEach` でデフォルト値を設定し、個別テストで必要に応じて上書きする。特に、セッションリストは `new ArrayList<>()` でデフォルト設定しておくことで、各テストでの `NullPointerException` を防止する。

```java
@BeforeEach
void setupMockMvc() {
    // 必ず MockitoAnnotations.openMocks(this) を呼び出す
    MockitoAnnotations.openMocks(this);

    // デフォルトセッション設定
    doReturn(createLoginUser()).when(mockLoginUserSession).getLoginUserInfo();
    when(mockRegistListSession.getTargetYearMonth()).thenReturn("202511");
    when(mockRegistListSession.getReturnYearMonth()).thenReturn("202511");
    when(mockRegistListSession.getIncomeRegistItemList()).thenReturn(new ArrayList<>());
    when(mockRegistListSession.getExpenditureRegistItemList()).thenReturn(new ArrayList<>());
}

@Test
void testSomeEndpoint() {
    // 特定テストのみ別の値を上書き
    when(mockRegistListSession.getTargetYearMonth()).thenReturn("202512");
    // ...
}
```

### 10.5 フォームの action フィールドの注意点

**重要**: `IncomeItemForm.action`、`ExpenditureItemForm.action` などのフォームフィールドは、HTMLではhidden inputとして自動設定される。MockMvcテストでは、`.param("action", ...)` を**明示的に**追加しないとControllerでnullになり、500エラーの原因になる。

```java
// ○ action フィールドを明示的に設定
mockMvc.perform(post("/myhacbook/.../incomeupdate")
    .param("action", MyHouseholdAccountBookContent.ACTION_TYPE_ADD)  // ← 必須
    .param("incomeKubun", "1")
    .param("incomeKingaku", "300000")
    .with(user("user01").roles("USER"))
    .with(csrf()))
    .andExpect(status().is3xxRedirection());

// × action が未設定 → UseCase内で「未定義のアクション」例外が発生し500エラー
mockMvc.perform(post("/myhacbook/.../incomeupdate")
    .param("incomeKubun", "1")
    .param("incomeKingaku", "300000")
    // ...
```

**判断基準**: POSTエンドポイントでフォームに `action` フィールドがある場合は必ず設定すること。

### 10.6 モデルキー名の注意点

`AbstractResponse.createModelAndView()` がモデルに設定するキー名は `"messages"` である。`"messageList"` ではないので注意。

```java
// ○ 正しいモデルキー名
.andExpect(model().attributeExists("messages"))

// × 誤ったモデルキー名（AttributeNotFoundException になる）
.andExpect(model().attributeExists("messageList"))
```

**確認方法**: `AbstractResponse.java` の `createModelAndView()` メソッドを参照。

### 10.7 nullリストのセッション操作検証

`anyList()` Matcher は `null` にマッチしない。セッションへのnull設定を `verify()` で検証する場合は、直接 `null` を渡すこと。

```java
// ○ null 直接指定
verify(mockRegistListSession).setExpenditureRegistItemList(null);

// × anyList() は null にマッチしない（verification失敗）
verify(mockRegistListSession).setExpenditureRegistItemList(anyList());
```

**背景**: `readInitInfo` メソッドなど、セッションに `null` を設定するケースがある（新規登録画面ではセッション支出リストをnullリセットする等）。

### 10.8 ビルドコマンドの注意点（必須）

**必ず `mvn clean test` を使うこと**。`mvn test`（clean なし）では以下の問題が発生する。

- Eclipseのインクリメンタルコンパイラがコンパイルしたクラスファイルが残存
- Spring Security の静的インポート（`user()`, `csrf()`）と Hamcrest の静的インポートが競合
- 全テストが「`user()` is undefined」等のエラーで失敗する

```bash
# ○ 必ずこちらを使用
mvn clean test

# × clean なしは使用しない（古いクラスファイルが残る）
mvn test
```

**インポートのルール**: Hamcrest の `import static org.hamcrest.Matchers.*` のようなワイルドカードインポートは使用しない。Spring Security の静的インポートと競合するため、必要なメソッドのみ個別にインポートする。

```java
// ○ 個別インポート
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

// × ワイルドカードインポート（競合の原因）
import static org.hamcrest.Matchers.*;
```

### 10.9 テストデータSQLの再利用

Controller層テスト用に新規SQLファイルを作成する必要はない。対応するUseCase層テストで使用しているSQLファイルを `@Sql` で指定して再利用する。

```java
@Sql(scripts = {
    "/sql/initsql/schema_test.sql",
    // UseCase層テストのSQLを再利用（新規作成不要）
    "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureRegistConfirmIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
```

**理由**: Controller層テストは「UseCaseが正しく結合されているか」を検証するため、UseCase層テストと同じデータで十分。

### 10.10 テストケース設計の観点

Controller層テストでは以下の観点をカバーする。

#### 必須検証項目

| 観点 | 検証方法 | 例 |
|------|----------|---|
| HTTP ステータス | `status().isOk()` / `status().is3xxRedirection()` | GETは200、POST成功はリダイレクト |
| ビュー名 | `view().name("...")` | `"account/regist/IncomeAndExpenditureRegist"` |
| モデル属性の存在 | `model().attributeExists("...")` | `"messages"`, `"targetYearMonth"` |
| リダイレクト先 | `redirectedUrl("...")` | `/myhacbook/accountregist/incomeandexpenditure/updateComplete/` |
| セッション操作 | `verify(mockSession).setXxx(...)` | 初期化メソッドの呼び出し確認 |

#### テストグループの推奨構成

Controller の全エンドポイントに対して、以下のグループでテストを設計する。

| グループ | 内容 | テスト例 |
|---------|------|---------|
| 初期表示系 | 画面表示エンドポイント（GET/POST初期化） | initload, updateload |
| 収入操作系 | 収入の選択・追加・更新・削除 | incomeaddselect, incomeupdate, incomedelete |
| 支出操作系 | 支出の選択・追加・更新・削除 | expenditureaddselect, expenditureupdate, expendituredelete |
| 支出項目選択系 | 支出項目選択画面の操作 | expenditureitemactselect, expenditureselect, expenditurecancel |
| 登録確認・完了系 | 登録確認・キャンセル・登録実行 | registcheck, registcancel, regist |

#### バリデーションエラーのテスト

バリデーションエラー時（BindingResult にエラーあり）はビューに戻るパターンを検証する。

```java
@Test
@DisplayName("異常系：バリデーションエラー時は画面に戻る")
void testPostIncomeUpdate_ValidationError_ShowsView() throws Exception {
    mockMvc.perform(post("/myhacbook/.../incomeupdate")
        .param("action", MyHouseholdAccountBookContent.ACTION_TYPE_ADD)
        // 必須パラメータ（incomeKubun）を未設定 → バリデーションエラー
        .with(user("user01").roles("USER"))
        .with(csrf()))
        .andExpect(status().isOk())  // リダイレクトでなく画面表示
        .andExpect(view().name("account/regist/IncomeAndExpenditureRegist"));
}
```

---

## 更新履歴

| 日付 | 更新内容 |
|------|---------|
| 2026-02-15 | 初版作成（Phase5 Step2の実績に基づく） |
| 2026-02-24 | セクション6.5「ロールバックテストのクラス分離パターン」追加（IncomeAndExpenditureRegistRollbackTestの実績から）。セクション8.5〜8.8（収入kubun別金額マッピング、clearStartFlg制御、SISYUTU_KINGAKU_TABLE集計ルール、混合アクション網羅）追加（IncomeAndExpenditureRegistConfirmIntegrationTestの実績から） |
| 2026-02-25 | タイトル・適用範囲をController層テストを含む形に更新。目次にセクション10追加。セクション1.2適用範囲を更新（対象外からController層を削除し対象として追記）。セクション10「Controller層テストガイドライン」新規追加（standaloneSetupパターン・UseCase vs Sessionモック戦略・@BeforeEachデフォルト設定・actionフィールド注意・モデルキー名・null検証・mvn clean test必須・SQL再利用・テストケース観点）（IncomeAndExpenditureRegistControllerIntegrationTestの実績から） |
| 2026-04-21 | セクション3.1の禁止パターンに `build() → ModelMap経由` を明示追加（`response.build().getModelMap()` パターンも禁止対象であることを明確化）（FixedCostInfoManageUseCaseIntegrationTestの実績から） |
