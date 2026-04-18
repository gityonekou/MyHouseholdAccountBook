# Phase 5 - Step 2: 結合テスト作成計画書

## 1. 目的

**Phase5 Step2の目的**:
- リファクタリング前の `IncomeAndExpenditureRegistUseCase` の動作を保証する結合テストを作成
- リファクタリング後（Step3-5）も同じテストが通ることで、振る舞いが保たれていることを証明
- **テストファースト**のアプローチでリスクを最小化

**重要原則**:
> リファクタリング = 外部から見た振る舞いを変えずに内部構造を改善すること

---

## 2. 作成する結合テストの概要

### 2.1 作成するテストクラス（6つ）

Phase5 Step1で確定した5分割方針に基づき、以下の6つの結合テストクラスを作成します:

#### アプリケーション層テスト（5つ）- リファクタリング前の動作保証

| # | テストクラス名 | 対象レイヤー | 対象機能 | メソッド数 | 優先度 |
|---|--------------|------------|---------|----------|--------|
| 1 | **IncomeAndExpenditureInitIntegrationTest** | UseCase層 | 収支登録・更新画面初期表示 | 4 | 最高（最初に作成） |
| 2 | **IncomeRegistIntegrationTest** | UseCase層 | 収入登録操作 | 4 | 高 |
| 3 | **ExpenditureRegistIntegrationTest** | UseCase層 | 支出登録操作 | 4 | 高 |
| 4 | **ExpenditureItemSelectIntegrationTest** | UseCase層 | 支出項目選択画面 | 2 | 高 |
| 5 | **IncomeAndExpenditureRegistConfirmIntegrationTest** | UseCase層 | 登録確認・完了処理 | 3 | 高 |

#### Controller層テスト（1つ）- 分割後のUseCase結合確認

| # | テストクラス名 | 対象レイヤー | 対象機能 | 優先度 |
|---|--------------|------------|---------|--------|
| 6 | **IncomeAndExpenditureRegistControllerIntegrationTest** | Controller層 | 収支登録Controller | 中（UseCase層テスト完了後） |

**作成順序の理由**:

**Phase 2-1~2-5: アプリケーション層テスト（最優先）**
1. **IncomeAndExpenditureInitIntegrationTest** を最初に作成
   - 画面初期表示の基本パターンを確立
   - 固定費自動生成のロジックをテスト
   - 依存が多いが、画面表示のみでセッション操作がシンプル

2. **IncomeRegistIntegrationTest** を次に作成
   - 収入のセッション操作パターンを確立
   - 依存が少なく（1個）、テストしやすい
   - 支出操作のテストパターンに活かせる

3. **ExpenditureRegistIntegrationTest** を次に作成
   - 支出のセッション操作をテスト
   - 収入操作のパターンを活用できる

4. **ExpenditureItemSelectIntegrationTest** を次に作成
   - 依存が少なく（1個）、独立性が高い
   - 戻り値が異なるため、別パターンとして確立

5. **IncomeAndExpenditureRegistConfirmIntegrationTest** を最後に作成
   - DB更新処理を含むため、テストデータ準備とアサーションが最も複雑
   - トランザクションのロールバック確認が必要

**Phase 2-6: Controller層テスト**
6. **IncomeAndExpenditureRegistControllerIntegrationTest** を作成
   - アプリケーション層テスト完了後に作成
   - 5つのUseCaseが正しく結合されているかを検証
   - `AccountMonthInquiryControllerIntegrationTest` と同レベルの内容

---

## 3. テストアーキテクチャ

### 3.1 参考にする既存テスト

**参考テストクラス**:
- **`AccountMonthInquiryIntegrationTest.java`** (アプリケーション層) ← **ベースとして使用**
  - SpringBootTest + @Transactional のアプローチ
  - UseCaseクラスを直接Autowired
  - SQLファイルによるテストデータ投入
  - アサーションのパターン

- `AccountMonthInquiryControllerIntegrationTest.java` (Controller層)
  - 参考として確認（Controller層のテストパターン）

### 3.2 テストの実装方針

#### 実装方針1: SpringBootTest + UseCase直接テスト

既存の `AccountMonthInquiryIntegrationTest` と同様のアプローチを採用:

```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
    "/sql/initsql/schema_test.sql",
    "/com/yonetani/webapp/accountbook/application/usecase/account/regist/ExpenditureItemSelectIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
class ExpenditureItemSelectIntegrationTest {

    @Autowired
    private IncomeAndExpenditureRegistUseCase useCase;

    /**
     * テスト用のログインユーザ情報を作成します。
     */
    private LoginUserInfo createLoginUser() {
        return LoginUserInfo.from("user01", "テストユーザ01");
    }

    @Test
    @DisplayName("正常系：支出項目選択画面初期表示")
    void testReadExpenditureAddSelect_NormalCase() {
        // Given: テストユーザ
        LoginUserInfo user = createLoginUser();

        // When: 支出項目選択画面を表示
        ExpenditureItemSelectResponse response = useCase.readExpenditureAddSelect(user);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);
        // ... アサーション
    }
}
```

**重要な違い**:
- ❌ MockMvc は使用しない（Controller層のテストではないため）
- ✅ UseCaseクラスを直接 `@Autowired` で注入
- ✅ UseCaseのメソッドを直接呼び出してテスト
- ✅ `@Transactional` で各テスト後に自動ロールバック
```

#### 実装方針2: テストデータ準備

**テストデータの準備方法**:
1. **SQLファイルによる初期データ投入**:
   - `/sql/initsql/schema_test.sql` でスキーマ作成
   - テスト専用SQLでテストデータ投入

2. **テストごとにトランザクションロールバック**:
   - `@Transactional` により各テスト完了後にロールバック
   - テスト間のデータ分離を保証

#### 実装方針3: セッション管理

**セッション情報の扱い**:

アプリケーション層のテストでは、セッション情報は **UseCaseメソッドの引数** として直接渡します。

```java
@Test
@DisplayName("正常系：収入追加_セッション操作")
void testExecIncomeAction_Add() {
    // Given: テストユーザ、対象年月
    LoginUserInfo user = createLoginUser();
    String targetYearMonth = "202511";

    // Given: 収入セッションリスト（事前状態）
    List<IncomeRegistItem> incomeList = new ArrayList<>();
    // 空リストから開始

    // Given: 新規追加する収入フォーム
    IncomeItemForm form = new IncomeItemForm();
    form.setAction("add");
    form.setIncomeKubun("1");
    form.setIncomeName("給料");
    form.setIncomeKingaku("300000");
    // ... その他の設定

    // When: 収入追加アクション実行
    IncomeAndExpenditureRegistResponse response = useCase.execIncomeAction(
        user,
        targetYearMonth,
        form,
        incomeList,  // ← セッションリストを引数として渡す
        expenditureList
    );

    // Then: レスポンスに収入が追加されている
    assertNotNull(response);
    assertNotNull(response.getIncomeRegistItemList());
    assertEquals(1, response.getIncomeRegistItemList().size());

    // Then: 追加された収入の内容を検証
    var addedIncome = response.getIncomeRegistItemList().get(0);
    assertEquals("給料", addedIncome.getIncomeName());
    assertEquals("300000", addedIncome.getIncomeKingaku());
}
```

**重要なポイント**:
- ✅ セッションリストは通常のJavaリストとして作成
- ✅ UseCaseメソッドの引数として渡す
- ✅ レスポンスから更新後のセッションリストを取得して検証
- ❌ Mockitoのモックは不要（Controller層ではないため）

#### 実装方針4: Controller層テスト（Phase 2-4）

**Controller層テストの実装方針**:

`AccountMonthInquiryControllerIntegrationTest` と同様のアプローチを採用:

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
    "/sql/initsql/schema_test.sql",
    "/com/yonetani/webapp/accountbook/presentation/controller/account/regist/IncomeAndExpenditureRegistControllerIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
public class IncomeAndExpenditureRegistControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private IncomeAndExpenditureRegistUseCase useCase;

    @Mock
    private LoginUserSession mockLoginUserSession;

    @BeforeEach
    void setupMockMvc() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(new IncomeAndExpenditureRegistController(
                useCase,
                mockLoginUserSession))
            .setControllerAdvice(new MyHouseholdAccountBookControllerAdvice(
                mockLoginUserSession))
            .build();
    }

    @Test
    @DisplayName("正常系：新規登録画面表示_固定費自動生成")
    public void testGetInitInfo_WithFixedCost() throws Exception {
        // ユーザ情報をモックに設定
        doReturn(createLoginUser()).when(mockLoginUserSession).getLoginUserInfo();

        // 画面表示の検証
        mockMvc.perform(get("/myhacbook/accountregist/accountmonth/initload")
                .param("targetYearMonth", "202511")
                .with(user("user01").password("password").roles("USER"))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("account/regist/AccountMonthRegist"))
            .andExpect(model().attributeExists("targetYearMonth"))
            .andExpect(model().attributeExists("expenditureRegistItemList"));
    }
}
```

**Controller層テストの目的**:
- ✅ リクエストマッピング（URLパス、HTTPメソッド）の確認
- ✅ パラメータバインディングの確認
- ✅ ビュー名の確認
- ✅ モデル属性の確認
- ✅ セッション管理の確認
- ✅ 例外ハンドリング（ControllerAdvice）の確認

**重要な違い（アプリケーション層テストとの比較）**:
- ✅ MockMvc を使用（HTTPリクエスト/レスポンスをシミュレート）
- ✅ LoginUserSession をモック化
- ✅ ビュー名、モデル属性、ステータスコードを検証
- ✅ URL、HTTPメソッド、パラメータの正確性を検証

---

## 4. テストクラス詳細設計

### 4.1 IncomeAndExpenditureInitIntegrationTest

**対象メソッド** (4個):
1. `readInitInfo` - 新規登録時の画面初期表示（固定費自動生成）
2. `readUpdateInfo` - 更新時の画面初期表示（DB登録済み情報）
3. `readIncomeAndExpenditureInfoList` - 収入・支出一覧の再表示
4. `readRegistCheckErrorSetInfo` - 入力確認時のエラー処理（収入未登録）

#### テストケース設計（実装完了）

| # | テストケース名 | 対象メソッド | テスト観点 |
|---|--------------|------------|----------|
| 1 | 正常系：新規登録_セッションデータ確認_10月固定費 | readInitInfo | 10月固定費パターン（偶数月）のセッションデータ検証 |
| 2 | 正常系：新規登録_画面表示内容確認_10月固定費 | readInitInfo | 10月固定費パターンの画面表示データ検証 |
| 3 | 正常系：新規登録_セッションデータ確認_11月固定費 | readInitInfo | 11月固定費パターン（奇数月）のセッションデータ検証、支出詳細4パターン検証 |
| 4 | 正常系：新規登録_画面表示内容確認_11月固定費 | readInitInfo | 11月固定費パターンの画面表示データ検証、支出詳細4パターン検証 |
| 5 | 正常系：新規登録画面表示_固定費なし | readInitInfo | 固定費が0件の場合のメッセージ表示、空の支出リスト、画面表示データ0件確認 |
| 6 | 正常系：新規登録_必須登録データ未登録時メッセージ確認 | readInitInfo | 買い物登録用支出項目が未登録の場合のエラーメッセージ8件 |
| 7 | 正常系：新規登録_全必須登録データ登録済みメッセージ確認 | readInitInfo | 買い物登録用支出項目が全登録済みの場合のメッセージなし |
| 8 | 正常系：新規登録_一部必須登録データのみ登録時メッセージ確認 | readInitInfo | 買い物登録用支出項目が一部のみ登録の場合のエラーメッセージ4件 |
| 9 | 正常系：更新_セッションデータ確認 | readUpdateInfo | DB登録済み収支情報のセッションデータ検証、対象外月除外、支払日NULL検証 |
| 10 | 正常系：更新_画面表示内容確認 | readUpdateInfo | DB登録済み収支情報の画面表示データ検証、支払日NULL時の空文字表示 |
| 11 | 異常系：更新_収入テーブル情報なし・支出あり | readUpdateInfo | 収入テーブル未登録時の例外発生確認 |
| 12 | 正常系：更新_収入テーブル情報あり・支出なし | readUpdateInfo | 支出テーブル未登録時の空リスト確認（過去データ対応） |
| 13 | 正常系：収入・支出一覧再表示（削除アクション設定データの除外確認含む） | readIncomeAndExpenditureInfoList | 削除アクション設定データが画面表示用一覧から除外されること |
| 14 | 正常系：入力確認エラー_収入未登録 | readRegistCheckErrorSetInfo | 収入未登録時のエラーメッセージ表示 |

**実装テストケース数**: 14個

**備考**:
- 「固定費999件超」のテストは境界値テストの性質が強いため、単体テストで検証すべき範囲として結合テストからは除外
- セッションデータ⇒画面表示データの変換検証はreadInitInfo/readUpdateInfoテストで網羅済み
- テスト13に削除アクション設定データの画面表示除外確認を追加（レビュー指摘対応）

---

### 4.2 IncomeRegistIntegrationTest

**対象メソッド** (4個):
1. `readIncomeAddSelect` - 収入新規追加選択画面表示
2. `readIncomeUpdateSelect` - 収入更新選択画面表示
3. `readIncomeUpdateBindingErrorSetInfo` - 収入更新時のバインディングエラー処理
4. `execIncomeAction` - 収入の追加・更新・削除（セッション操作）

#### テストケース設計（実装完了）

| # | テストケース名 | 対象メソッド | テスト観点 |
|---|--------------|------------|----------|
| 1 | 正常系：新規追加画面表示_フォーム初期値確認 | readIncomeAddSelect | アクション=ADD、収入区分名=【新規追加】が設定される |
| 2 | 正常系：更新画面表示_選択収入情報のフォーム設定確認 | readIncomeUpdateSelect | 選択した収入コードに対応する情報がフォームに設定される |
| 3 | 異常系：更新画面表示_収入コード不存在エラー | readIncomeUpdateSelect | 存在しない収入コード指定時に例外がスローされる |
| 4 | 正常系：バインディングエラー時_入力値保持確認 | readIncomeUpdateBindingErrorSetInfo | バインディングエラー時に入力値がそのまま保持される |
| 5 | 正常系：収入新規追加_セッションへの追加確認 | execIncomeAction | 収入コード自動生成、セッションリストに追加、完了メッセージ |
| 6 | 正常系：収入更新_セッションの更新確認 | execIncomeAction | 既存収入の更新、完了メッセージ |
| 7 | 正常系：収入削除_新規データタイプ_セッションから完全削除 | execIncomeAction | DATA_TYPE_NEW時はセッションから完全削除 |
| 8 | 正常系：収入削除_ロードデータタイプ_削除アクション設定 | execIncomeAction | DATA_TYPE_LOAD時はアクション=DELETEでセッションに残る |
| 9 | 異常系：収入更新_収入コード不存在エラー | execIncomeAction | 存在しない収入コードで更新時に例外がスローされる |
| 10 | 異常系：収入削除_収入コード不存在エラー | execIncomeAction | 存在しない収入コードで削除時に例外がスローされる |

**実装テストケース数**: 10個

**備考**:
- セッション操作が中心のため、DBデータはユーザーマスタのみ（コード定義は外部ファイル）
- 削除テストはデータタイプ（新規/ロード）による動作の違いを網羅

---

### 4.3 ExpenditureRegistIntegrationTest

**対象メソッド** (4個):
1. `readExpenditureUpdateSelect` - 支出更新選択画面表示
2. `readExpenditureUpdateBindingErrorSetInfo` - 支出更新時のバインディングエラー処理
3. `readNewExpenditureItem` - 選択した支出項目の新規支出情報表示
4. `execExpenditureAction` - 支出の追加・更新・削除（セッション操作）

#### テストケース設計（実装完了）

| # | テストケース名 | 対象メソッド | テスト観点 |
|---|--------------|------------|----------|
| 1 | 正常系：更新画面表示_選択支出情報のフォーム設定確認 | readExpenditureUpdateSelect | 選択した支出コードに対応する情報がフォームに設定される（支出項目名＞区切り、イベントコード確認含む） |
| 2 | 異常系：更新画面表示_支出コード不存在エラー | readExpenditureUpdateSelect | 存在しない支出コード指定時に例外がスローされる |
| 3 | 正常系：バインディングエラー時_入力値保持確認 | readExpenditureUpdateBindingErrorSetInfo | バインディングエラー時に入力値がそのまま保持される |
| 4 | 正常系：新規支出項目表示_フォーム初期値確認 | readNewExpenditureItem | 支出項目名具体値・イベントコード空文字・詳細がフォーム初期値に設定される |
| 5 | 正常系：新規支出項目表示_イベントコード設定あり | readNewExpenditureItem | イベント系支出項目で支出項目名＞区切り＋イベント名【】表示確認 |
| 6 | 異常系：新規支出項目表示_イベント必須未設定エラー | readNewExpenditureItem | イベント必須なのに未設定時に例外がスローされる |
| 7 | 正常系：支出新規追加_セッションへの追加確認 | execExpenditureAction | 支出コード自動生成(17桁)、イベントコード具体値設定、セッションリストに追加、完了メッセージ |
| 8 | 正常系：支出更新_セッションの更新確認 | execExpenditureAction | 既存支出の更新、完了メッセージ |
| 9 | 正常系：支出削除_新規データタイプ_セッションから完全削除 | execExpenditureAction | DATA_TYPE_NEW時はセッションから完全削除 |
| 10 | 正常系：支出削除_ロードデータタイプ_削除アクション設定 | execExpenditureAction | DATA_TYPE_LOAD時はアクション=DELETEでセッションに残る |
| 11 | 異常系：支出更新_支出コード不存在エラー | execExpenditureAction | 存在しない支出コードで更新時に例外がスローされる |
| 12 | 異常系：支出削除_支出コード不存在エラー | execExpenditureAction | 存在しない支出コードで削除時に例外がスローされる |

**実装テストケース数**: 12個

**備考**:
- DBデータはユーザーマスタ、支出項目マスタ（0001～0061）、イベントテーブルを使用（コード定義は外部ファイル）
- 支出項目マスタはInitIntegrationTest.sqlと同等のデータ（0001-0060）＋イベント系支出項目（0061）
- 削除テストはデータタイプ（新規/ロード）による動作の違いを網羅
- 支出項目名の＞区切り階層表示およびイベント名表示をテストケースで確認
- レビュー指摘対応: テスト5(イベント系テストケース)追加、テスト1(支出項目名・イベントコード確認)強化、テスト7(支出コード17桁・イベントコード具体値)強化

---

### 4.4 ExpenditureItemSelectIntegrationTest

**対象メソッド** (2個):
1. `readExpenditureAddSelect` - 支出項目選択画面初期表示
2. `readExpenditureItemActSelect` - 支出項目選択後の確認画面表示

#### テストケース設計（実装完了）

| # | テストケース名 | 対象メソッド | テスト観点 |
|---|--------------|------------|----------|
| 1 | 正常系：支出項目選択画面初期表示_支出項目一覧確認 | readExpenditureAddSelect | 支出項目一覧が階層構造で取得される（61件）、メッセージなし |
| 2 | 正常系：支出項目選択_非イベント系Level3項目 | readExpenditureItemActSelect | 電気代(0037)選択→sisyutuItemName="固定費(課税)＞水光熱通費＞電気代"、詳細内容確認、eventCodeRequired=false、eventSelectList=null |
| 3 | 正常系：支出項目選択_イベント系支出項目 | readExpenditureItemActSelect | コミケ(0061)選択→sisyutuItemName="趣味娯楽＞イベント費＞コミケ"、eventCodeRequired=true、eventCode先頭値設定、eventSelectList存在 |
| 4 | 正常系：支出項目選択_Level2項目 | readExpenditureItemActSelect | 水光熱通費(0004)選択→sisyutuItemName="固定費(課税)＞水光熱通費"、＞区切り2階層確認 |
| 5 | 正常系：支出項目選択_Level1項目 | readExpenditureItemActSelect | 固定費(税金)(0001)選択→sisyutuItemName="固定費(税金)"、＞区切りなし確認 |

**実装テストケース数**: 5個

**備考**:
- DBデータはExpenditureRegistIntegrationTest.sqlと同等（支出項目マスタ0001-0061＋EVENT_ITEM_TABLE）
- 支出項目名の＞区切り階層表示をLevel1/2/3の各レベルで検証
- イベント系支出項目ではeventSelectListの存在とeventCode先頭値設定を確認
- 既存テストレビュー観点の反映: 具体値による検証（assertNotNull不可）、＞区切り表示、イベントコード確認

---

### 4.5 IncomeAndExpenditureRegistConfirmIntegrationTest

**対象メソッド** (3個):
1. `readRegistCheckInfo` - 登録内容確認画面表示
2. `readRegistCancelInfo` - 登録キャンセル時のトランザクション完了
3. `execRegistAction` - 収支情報の登録・更新後のトランザクション完了（4テーブル更新）

#### テストケース設計（実装完了）

| # | テストケース名 | 対象メソッド | テスト観点 |
|---|--------------|------------|----------|
| 1 | 正常系：確認画面表示_収入支出一覧・削除アクション除外確認 | readRegistCheckInfo | 削除アクション設定データが除外される、表示用収入一覧・支出一覧・合計金額の確認 |
| 2 | 正常系：キャンセル_メッセージとリダイレクト設定確認 | readRegistCancelInfo | "YYYY年MM月度の収支登録をキャンセルしました。"メッセージ、transactionSuccessFull設定 |
| 3 | 異常系：キャンセル_不正な対象年月 | readRegistCancelInfo | 不正なreturnYearMonth指定時の例外確認 |
| 4 | 正常系：新規登録_全テーブルINSERT確認 | execRegistAction | 新規月(202512)への登録、INCOME/EXPENDITURE/SISYUTU_KINGAKU/INCOME_AND_EXPENDITURE全テーブルへのINSERT、必須8項目チェック通過、金額計算の整合性確認 |
| 5 | 正常系：更新_混合アクション（NON_UPDATE/UPDATE/ADD/DELETE） | execRegistAction | 既存月(202511)の更新、収入・支出の各アクション処理、論理削除確認、INCOME_AND_EXPENDITURE_TABLEの金額再計算確認 |
| 6 | 正常系：変更なし_全件NON_UPDATE | execRegistAction | 全セッションデータがNON_UPDATE、"変更箇所がありませんでした"メッセージ確認、transactionSuccessFull設定 |
| 7 | 異常系：収入リスト空エラー | execRegistAction | 空の収入リスト渡し時の例外確認 |
| 8 | 異常系：収入リストnullエラー | execRegistAction | null渡し時の例外確認 |

**実装テストケース数**: 8個（IncomeAndExpenditureRegistConfirmIntegrationTest本体）+ 2個（IncomeAndExpenditureRegistRollbackTest） = 計10個

**備考**:
- DBデータはInitIntegrationTest.sqlをベースに拡張（202511既存データ＋SISYUTU_KINGAKU_TABLE）
- 新規登録テスト(#4)ではセッション支出リストに買い物登録必須8項目(0051×3区分,0052,0050,0046,0007,0047)を全網羅
- 更新テスト(#5)では既存DBデータとセッションデータの組み合わせで必須8項目を確保
- DB検証は@Autowiredしたリポジトリ(IncomeTableRepository等)のfindBy/countByメソッドで実施
- @Transactional内でのDB変更は検証可能（テスト後に自動ロールバック）
- 既存テストレビュー観点の反映: 削除アクションデータの除外確認、transactionSuccessFull設定確認

#### トランザクションロールバック確認テスト（IncomeAndExpenditureRegistRollbackTest）

| # | テストケース名 | テスト観点 |
|---|--------------|----------|
| R1 | ロールバック確認_不正アクション例外 | 未定義アクション("err")で例外が発生した場合、INSERT済みの収入3件が全てロールバックされること |
| R2 | ロールバック確認_必須8項目未登録例外 | 必須支出項目(0046:被服費)が未登録の場合、全件INSERT後の最終チェックで例外が発生し、全件（収入3件＋支出7件）がロールバックされること |

**ロールバックテストのクラス分離理由**:
- `@Transactional`付きテストクラスでは同一トランザクション内でロールバック確認ができないため、専用クラス（`@Transactional`なし）に分離
- テスト後のDB残留データは `IncomeAndExpenditureRegistRollbackTest-cleanup.sql` で確実にクリーンアップ
- 詳細は `docs/integration-test-guidelines.md` セクション6.5参照

---

## 5. テストデータ準備戦略

### 5.1 SQLファイルによるテストデータ投入

**作成するSQLファイル**:
```
src/test/resources/com/yonetani/webapp/accountbook/application/usecase/account/regist/
├── IncomeAndExpenditureInitIntegrationTest.sql
├── IncomeRegistIntegrationTest.sql
├── ExpenditureRegistIntegrationTest.sql
├── ExpenditureItemSelectIntegrationTest.sql
└── IncomeAndExpenditureRegistConfirmIntegrationTest.sql
```

**各SQLファイルの内容**:

#### ExpenditureItemSelectIntegrationTest.sql
```sql
-- 支出項目テーブルのテストデータ
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, ...) VALUES
('user01', '001', '食費', ...),
('user01', '002', '日用品', ...),
('user01', '003', '光熱費', ...);
```

#### IncomeAndExpenditureInputIntegrationTest.sql
```sql
-- 固定費テーブルのテストデータ（固定費自動生成用）
INSERT INTO FIXED_COST_TABLE (USER_ID, FIXED_COST_CODE, FIXED_COST_NAME, SHIHARAI_TUKI, ...) VALUES
('user01', 'FC001', '家賃', '00', ...), -- 毎月
('user01', 'FC002', '電気代', '20', ...), -- 奇数月
('user01', 'FC003', 'ガス代', '30', ...); -- 偶数月

-- 支出項目テーブル
INSERT INTO SISYUTU_ITEM_TABLE (USER_ID, SISYUTU_ITEM_CODE, SISYUTU_ITEM_NAME, ...) VALUES
('user01', '001', '固定費', ...);

-- 既存の収支データ（更新テスト用）
INSERT INTO INCOME_AND_EXPENDITURE_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, ...) VALUES
('user01', '2025', '11', ...);

INSERT INTO INCOME_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, INCOME_CODE, ...) VALUES
('user01', '2025', '11', 'INC001', ...);

INSERT INTO EXPENDITURE_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_CODE, ...) VALUES
('user01', '2025', '11', 'EXP001', ...);
```

#### IncomeAndExpenditureRegistConfirmIntegrationTest.sql
```sql
-- 登録・更新テスト用の既存データ
INSERT INTO INCOME_AND_EXPENDITURE_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, ...) VALUES
('user01', '2025', '12', ...);

-- 収入テーブル
INSERT INTO INCOME_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, INCOME_CODE, ...) VALUES
('user01', '2025', '12', 'INC001', ...);

-- 支出テーブル
INSERT INTO EXPENDITURE_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_CODE, ...) VALUES
('user01', '2025', '12', 'EXP001', ...);

-- 支出金額テーブル
INSERT INTO SISYUTU_KINGAKU_TABLE (USER_ID, TARGET_YEAR, TARGET_MONTH, SISYUTU_ITEM_CODE, ...) VALUES
('user01', '2025', '12', '001', ...);
```

### 5.2 テストヘルパーメソッド

**共通ヘルパーメソッド**:
```java
/**
 * テスト用のログインユーザ情報を作成します。
 */
private LoginUserInfo createLoginUser() {
    return LoginUserInfo.from("user01", "テストユーザ01");
}

/**
 * テスト用の収入セッションリストを作成します。
 */
private List<IncomeRegistItem> createTestIncomeList() {
    List<IncomeRegistItem> list = new ArrayList<>();
    list.add(IncomeRegistItem.from(
        "NEW", "ADD", "INC001", "1", "", "給料", "2025/11/25", "300000"));
    return list;
}

/**
 * テスト用の支出セッションリストを作成します。
 */
private List<ExpenditureRegistItem> createTestExpenditureList() {
    List<ExpenditureRegistItem> list = new ArrayList<>();
    list.add(ExpenditureRegistItem.from(
        "NEW", "ADD", "EXP001", "001", "", "家賃", "1", "", "2025/11/27", "80000", false));
    return list;
}
```

---

## 6. 実施手順

### 6.1 Step 2 の詳細ステップ

#### Phase 2-1: IncomeAndExpenditureInitIntegrationTest の作成

**所要時間**: 2-3時間

1. テストクラスの作成
   - `@SpringBootTest`, `@Transactional` の設定
   - UseCaseの@Autowired

2. テストデータSQLの作成
   - `IncomeAndExpenditureInitIntegrationTest.sql`
   - 固定費、支出項目、既存収支データ

3. テストケースの実装 (6-8個)
   - 画面初期表示テスト（固定費自動生成含む）
   - 更新画面表示テスト
   - 入力確認エラー処理

4. テスト実行と確認
   - すべてのテストがGREEN

**完了基準**: すべてのテストがGREEN

---

#### Phase 2-2: IncomeRegistIntegrationTest の作成

**所要時間**: 2-3時間

1. テストクラスの作成
   - UseCaseの@Autowired

2. テストデータSQLの作成
   - `IncomeRegistIntegrationTest.sql`

3. テストヘルパーメソッドの作成
   - `createTestIncomeList()`

4. テストケースの実装 (6-8個)
   - 収入追加・更新・削除のセッション操作テスト
   - バインディングエラー処理

5. テスト実行と確認
   - すべてのテストがGREEN

**完了基準**: すべてのテストがGREEN

---

#### Phase 2-3: ExpenditureRegistIntegrationTest の作成

**所要時間**: 2-3時間

1. テストクラスの作成
   - UseCaseの@Autowired

2. テストデータSQLの作成
   - `ExpenditureRegistIntegrationTest.sql`

3. テストヘルパーメソッドの作成
   - `createTestExpenditureList()`

4. テストケースの実装 (6-8個)
   - 支出追加・更新・削除のセッション操作テスト
   - バインディングエラー処理

5. テスト実行と確認
   - すべてのテストがGREEN

**完了基準**: すべてのテストがGREEN

---

#### Phase 2-4: ExpenditureItemSelectIntegrationTest の作成

**所要時間**: 1-2時間

1. テストクラスの作成
   - UseCaseの@Autowired

2. テストデータSQLの作成
   - `ExpenditureItemSelectIntegrationTest.sql`
   - 支出項目テーブルのテストデータ

3. テストケースの実装 (3-5個)
   - 支出項目選択画面初期表示
   - 支出項目選択確認
   - 異常系テスト

4. テスト実行と確認
   - すべてのテストがGREEN

**完了基準**: すべてのテストがGREEN

---

#### Phase 2-5: IncomeAndExpenditureRegistConfirmIntegrationTest の作成

**所要時間**: 3-4時間

1. テストクラスの作成
   - UseCaseの@Autowired
   - トランザクション設定の確認

2. テストデータSQLの作成
   - `IncomeAndExpenditureRegistConfirmIntegrationTest.sql`
   - 既存の収支データ（更新テスト用）

3. テストケースの実装 (8-10個)
   - 確認画面表示テスト
   - キャンセル処理テスト
   - DB登録テスト (新規・更新・収入のみ・支出のみ・両方)
   - トランザクションロールバックテスト

4. DB更新の検証
   - 4テーブル（収支、収入、支出、支出金額）への更新確認
   - setTransactionSuccessFull()の呼び出し確認

5. テスト実行と確認
   - すべてのテストがGREEN

**完了基準**: すべてのテストがGREEN、4テーブルの更新が正しく検証される

---

#### Phase 2-6: IncomeAndExpenditureRegistControllerIntegrationTest の作成

**所要時間**: 2-3時間

1. テストクラスの作成
   - `@SpringBootTest`, `@AutoConfigureMockMvc` の設定
   - MockMvcのセットアップ（standaloneSetup）
   - LoginUserSessionのモック設定

2. テストデータSQLの作成
   - `IncomeAndExpenditureRegistControllerIntegrationTest.sql`
   - Phase 2-1~2-5で作成したSQLを統合・再利用

3. テストケースの実装（主要エンドポイント）
   - 新規登録画面表示テスト（固定費自動生成）
   - 更新画面表示テスト
   - 収入操作エンドポイントテスト
   - 支出操作エンドポイントテスト
   - 登録確認エンドポイントテスト
   - 登録実行エンドポイントテスト
   - エラーハンドリングテスト（ControllerAdvice）

4. HTTPリクエスト/レスポンスの検証
   - リクエストマッピング（URL、HTTPメソッド）
   - パラメータバインディング
   - ビュー名、モデル属性
   - セッション操作
   - ステータスコード

5. テスト実行と確認
   - すべてのテストがGREEN

**完了基準**: すべてのテストがGREEN、HTTPレベルでの結合動作が検証される

**重要**: Phase 2-6 は Phase 2-1~2-5 完了後に実施（アプリケーション層テストが全てGREENになった後）

---

### 6.2 Step 2 全体の完了基準

**Step 2 完了基準**:
- ✅ 6つの結合テストクラスが作成されている
  - アプリケーション層テスト（5つ）
  - Controller層テスト（1つ）
- ✅ すべてのテストがGREEN（推定35-45個のテストケース）
- ✅ 主要なビジネスシナリオがカバーされている
- ✅ エッジケース（エラー処理、バリデーション）がテストされている
- ✅ DB更新処理が正しく検証されている
- ✅ トランザクションのロールバックが確認されている

**重要**: この完了基準を満たすまで、Step 3（リファクタリング）に進まないこと

---

## 7. リスクと対策

### 7.1 想定されるリスク

| リスク | 影響度 | 発生確率 | 対策 |
|--------|--------|---------|------|
| テストデータ準備の複雑化 | 中 | 高 | SQLファイルを段階的に作成、共通データは再利用 |
| セッションモックの設定漏れ | 中 | 中 | ヘルパーメソッドを用意、各テストで明示的に設定 |
| トランザクションロールバックの確認不足 | 高 | 低 | DB更新後のデータ確認テストを必ず実施 |
| テスト作成に時間がかかりすぎる | 中 | 中 | 小さく始める（ExpenditureItemSelect から）、段階的に拡大 |

### 7.2 対策の実施

**対策1**: 小さく始める
- ExpenditureItemSelectIntegrationTest から開始（最も小さい）
- パターンを確立してから IncomeAndExpenditureInputIntegrationTest に展開

**対策2**: 既存テストを参考にする
- `AccountMonthInquiryControllerIntegrationTest` の実装パターンを活用
- MockMvcのセットアップ方法を流用

**対策3**: テストデータの再利用
- 共通のテストデータ（ユーザ、支出項目など）は使い回す
- SQLファイルに明確なコメントを追加

---

## 8. 成果物

### 8.1 Step 2 で作成するファイル

**テストクラス** (7ファイル):
```
【アプリケーション層テスト】
src/test/java/com/yonetani/webapp/accountbook/application/usecase/account/regist/
├── IncomeAndExpenditureInitIntegrationTest.java        ✅ 実装完了（14個）
├── IncomeRegistIntegrationTest.java                    ✅ 実装完了（10個）
├── ExpenditureRegistIntegrationTest.java               ✅ 実装完了（12個）
├── ExpenditureItemSelectIntegrationTest.java           ✅ 実装完了（5個）
├── IncomeAndExpenditureRegistConfirmIntegrationTest.java ✅ 実装完了（8個）
└── IncomeAndExpenditureRegistRollbackTest.java         ✅ 実装完了（2個）

【Controller層テスト】
src/test/java/com/yonetani/webapp/accountbook/presentation/controller/account/regist/
└── IncomeAndExpenditureRegistControllerIntegrationTest.java  ✅ 実装完了（25個）
```

**テストデータSQL** (7ファイル):
```
【アプリケーション層テスト用】
src/test/resources/com/yonetani/webapp/accountbook/application/usecase/account/regist/
├── IncomeAndExpenditureInitIntegrationTest.sql          ✅ 作成済み
├── IncomeRegistIntegrationTest.sql                      ✅ 作成済み
├── ExpenditureRegistIntegrationTest.sql                 ✅ 作成済み
├── ExpenditureItemSelectIntegrationTest.sql             ✅ 作成済み
├── IncomeAndExpenditureRegistConfirmIntegrationTest.sql ✅ 作成済み
└── IncomeAndExpenditureRegistRollbackTest-cleanup.sql   ✅ 作成済み

【Controller層テスト用】
※ 新規SQLファイル作成不要（IncomeAndExpenditureRegistConfirmIntegrationTest.sqlを@Sqlで指定し再利用）
```

**実績総テストケース数**: 76個（アプリケーション層51個 + Controller層25個）
  - IncomeAndExpenditureInitIntegrationTest: 14個 ✅
  - IncomeRegistIntegrationTest: 10個 ✅
  - ExpenditureRegistIntegrationTest: 12個 ✅
  - ExpenditureItemSelectIntegrationTest: 5個 ✅
  - IncomeAndExpenditureRegistConfirmIntegrationTest: 8個 ✅
  - IncomeAndExpenditureRegistRollbackTest: 2個 ✅
  - IncomeAndExpenditureRegistControllerIntegrationTest: 25個 ✅

---

## 9. 次のアクション

### 9.1 アプリケーション層テストの完了状況

**Phase 2-1〜2-5 完了**: IncomeAndExpenditureRegistConfirmIntegrationTest + IncomeAndExpenditureRegistRollbackTest まで実装完了（計51個 全件GREEN）

1. ✅ IncomeAndExpenditureInitIntegrationTest（14個）
2. ✅ IncomeRegistIntegrationTest（10個）
3. ✅ ExpenditureRegistIntegrationTest（12個）
4. ✅ ExpenditureItemSelectIntegrationTest（5個）
5. ✅ IncomeAndExpenditureRegistConfirmIntegrationTest（8個）
6. ✅ IncomeAndExpenditureRegistRollbackTest（2個）

### 9.2 完了状況

**Phase 2-6 完了**: IncomeAndExpenditureRegistControllerIntegrationTest 実装完了（25個 全件GREEN）

**Step 2 全Phase完了**: アプリケーション層5クラス（51個）+ Controller層1クラス（25個）= 合計76個 全件GREEN

**次のアクション**: Step 3（IncomeAndExpenditureRegistUseCase のリファクタリング）着手可能

---

## 10. 更新履歴

| 版数 | 更新日 | 更新内容 |
|------|--------|---------|
| 1.00.00 | 2026-01-10 | 初版作成（Phase5 Step2 計画書） |
| 1.01.00 | 2026-02-24 | Phase2-1〜2-5実装完了に伴う更新。4.5にIncomeAndExpenditureRegistRollbackTestのテストケース設計追加。8.1成果物リストをRollbackTest含む7クラスに更新し実装ステータス追記。9（次のアクション）を実績反映に更新 |
| 1.02.00 | 2026-02-25 | Phase2-6実装完了に伴う更新。8.1成果物リストのControllerテストを実装完了（25個）に更新（SQL新規作成不要・UseCase層SQL再利用）。実績総テストケース数を76個に更新。9.2を全Phase完了・Step3着手可能に更新 |

---

**ステータス**: ✅ Phase2-1〜2-6 全て実装完了（76テスト全件GREEN）
**次のアクション**: Step 3（IncomeAndExpenditureRegistUseCase のリファクタリング）着手可能
