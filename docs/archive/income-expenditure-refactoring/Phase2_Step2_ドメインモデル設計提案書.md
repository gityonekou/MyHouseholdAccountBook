# Phase 2 Step 2: ドメインモデル設計提案書

**作成日**: 2025年12月2日
**対象機能**: 月次収支照会（AccountMonthInquiry）
**目的**: DDD原則に基づいたドメインモデルの設計

---

## 目次

1. [DDD設計の基本原則](#1-ddd設計の基本原則)
2. [現状の問題点と解決方針](#2-現状の問題点と解決方針)
3. [ドメインモデル設計](#3-ドメインモデル設計)
4. [層間の責務分離](#4-層間の責務分離)
5. [実装順序](#5-実装順序)

---

## 1. DDD設計の基本原則

### 1.1 DDDの4つの階層

```
┌─────────────────────────────────────────┐
│ Presentation Layer (プレゼンテーション層) │
│ - Controller                            │
│ - Response/Request DTO                  │
│ - View (HTML/Template)                  │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ Application Layer (アプリケーション層)    │
│ - UseCase                               │
│ - トランザクション制御                    │
│ - オーケストレーション（調整）            │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ Domain Layer (ドメイン層)                │
│ - Entity (エンティティ)                  │
│ - Value Object (値オブジェクト)          │
│ - Aggregate (集約)                       │
│ - Domain Service (ドメインサービス)      │
│ - Repository Interface                   │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ Infrastructure Layer (インフラ層)        │
│ - Repository Implementation             │
│ - MyBatis Mapper                        │
│ - External API Client                   │
└─────────────────────────────────────────┘
```

### 1.2 各層の責務

#### Presentation Layer（プレゼンテーション層）
- **責務**: ユーザーインターフェースとの接点
- **やるべきこと**:
  - HTTPリクエストの受け取り
  - 入力値の検証（形式チェック）
  - UseCaseの呼び出し
  - レスポンスの整形（DTO変換）
- **やってはいけないこと**:
  - ビジネスロジックの実装
  - データベース直接アクセス
  - ドメインオブジェクトを直接返す

#### Application Layer（アプリケーション層）
- **責務**: ユースケースの実現
- **やるべきこと**:
  - トランザクション境界の制御
  - 複数のドメインオブジェクトの調整（オーケストレーション）
  - リポジトリからのデータ取得・保存
  - ドメインサービスの呼び出し
- **やってはいけないこと**:
  - ビジネスロジックの実装（ドメイン層に委譲）
  - if文で分岐する業務ルール判定
  - 金額計算などのドメインロジック

#### Domain Layer（ドメイン層）
- **責務**: ビジネスロジックの実装
- **やるべきこと**:
  - 業務ルールの実装
  - データの整合性保証
  - 不変条件の保護
  - ドメインイベントの発行
- **やってはいけないこと**:
  - データベースアクセス
  - 外部APIの直接呼び出し
  - プレゼンテーション層への依存

#### Infrastructure Layer（インフラ層）
- **責務**: 技術的な実装詳細
- **やるべきこと**:
  - データベースアクセス
  - 外部APIとの通信
  - ファイルシステムアクセス
- **やってはいけないこと**:
  - ビジネスロジックの実装

---

## 2. 現状の問題点と解決方針

### 2.1 現状の問題点まとめ

#### 問題1: ユースケース層の肥大化（268行）

**現状のコード**:
```java
// AccountMonthInquiryUseCase.java (行213-227)
// 収入金額の整合性チェック
SyuunyuuKingakuTotalAmount incomeKingakuTotalAmount = incomeRepository.sumIncomeKingaku(inquiryModel);
IncomingAmount chkIncomeAmount = IncomingAmount.from(
    sisyutuResult.getSyuunyuuKingaku(),
    sisyutuResult.getWithdrewKingaku()
);
if(!chkIncomeAmount.getSyuunyuuKingakuTotalAmount().equals(incomeKingakuTotalAmount)) {
    throw new MyHouseholdAccountBookRuntimeException("該当月の収入情報が一致しません。");
}

// 支出金額の整合性チェック
SisyutuKingakuTotalAmount expenditureKingakuTotalAmount = expenditureRepository.sumExpenditureKingaku(inquiryModel);
SisyutuKingakuTotalAmount chkExpenditureKingaku = SisyutuKingakuTotalAmount.from(
    sisyutuResult.getSisyutuKingaku().getValue()
);
if(!chkExpenditureKingaku.equals(expenditureKingakuTotalAmount)) {
    throw new MyHouseholdAccountBookRuntimeException("該当月の支出情報が一致しません。");
}
```

**何が問題か**:
- 整合性チェックは**ビジネスルール**である
- ユースケース層は「オーケストレーション（調整役）」に徹するべき
- if文によるビジネスロジック判定がユースケース層にある

#### 問題2: ドメインモデルの貧弱化（貧血ドメインモデル）

**現状のコード**:
```java
// IncomeAndExpenditureItem.java
@Getter
public class IncomeAndExpenditureItem {
    private final SyuunyuuKingaku syuunyuuKingaku;
    private final WithdrewKingaku withdrewKingaku;
    private final SisyutuKingaku sisyutuKingaku;
    private final SyuusiKingaku syuusiKingaku;

    public boolean isEmpty() {
        return syuunyuuKingaku == null;
    }
}
```

**何が問題か**:
- 照会機能では単なるデータホルダーとして使用（実際には登録・更新機能で金額計算ロジックを持つ）
- **整合性検証ロジック**がドメイン層にない
- ユースケース層に整合性チェックのif文が散在

#### 問題3: Response層にロジックが混在

**現状のコード**:
```java
// AccountMonthInquiryResponse.java (行177-201)
@Override
public ModelAndView build() {
    if(syuusiDataFlg) {
        // 収支データありの場合
        ModelAndView modelAndView = createModelAndView("account/inquiry/AccountMonth");
        // ...
        return modelAndView;
    } else {
        // 収支データなしの場合
        ModelAndView modelAndView = createModelAndView("account/inquiry/AccountMonthRegistCheck");
        // ...
        return modelAndView;
    }
}
```

**何が問題か**:
- ビュー切り替えのビジネスロジックがResponse層にある
- この判断はController層で行うべき

### 2.2 解決方針

```
【現状】
UseCase層(268行) に業務ロジックが集中
        ↓
【目標】
UseCase層(~50行)  : オーケストレーションのみ
Domain層          : 業務ロジックを集約
Response層        : DTO変換のみ
```

---

## 3. ドメインモデル設計

### 3.1 集約の設計

#### IncomeAndExpenditure（収支）集約

**集約ルート**: `IncomeAndExpenditure`

**責務**:
- 収支情報の一貫性を保証
- 収支金額の計算
- 整合性検証のためのデータ提供

**クラス図**:
```
┌─────────────────────────────────────────┐
│  IncomeAndExpenditure (集約ルート)       │
├─────────────────────────────────────────┤
│ - userId: UserId                        │
│ - targetYearMonth: YearMonth            │
│ - incomeAmount: IncomeAmount            │
│ - withdrewAmount: WithdrewAmount        │
│ - estimatedExpenditureAmount            │
│ - expenditureAmount: ExpenditureAmount  │
│ - balanceAmount: BalanceAmount          │
├─────────────────────────────────────────┤
│ + calculateBalance(): BalanceAmount     │
│ + getTotalIncome(): TotalIncomeAmount   │
│ + isDataExists(): boolean               │
│ + isEmpty(): boolean                    │
└─────────────────────────────────────────┘
```

**設計のポイント**:
1. **不変性**: すべてのフィールドをfinalにし、生成後は変更不可
2. **自己完結性**: 収支金額の計算ロジックを内部に持つ
3. **整合性保証**: コンストラクタで不正な状態を拒否

**実装例**:
```java
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class IncomeAndExpenditure {
    // 識別子
    private final UserId userId;
    private final YearMonth targetYearMonth;

    // 金額情報
    private final IncomeAmount incomeAmount;                     // 収入金額
    private final WithdrewAmount withdrewAmount;                 // 積立金取崩金額
    private final EstimatedExpenditureAmount estimatedAmount;    // 支出予定金額
    private final ExpenditureAmount expenditureAmount;           // 支出金額
    private final BalanceAmount balanceAmount;                   // 収支金額

    /**
     * ファクトリメソッド: 収支データから生成
     */
    public static IncomeAndExpenditure reconstruct(
            UserId userId,
            YearMonth targetYearMonth,
            IncomeAmount incomeAmount,
            WithdrewAmount withdrewAmount,
            EstimatedExpenditureAmount estimatedAmount,
            ExpenditureAmount expenditureAmount,
            BalanceAmount balanceAmount) {

        // 不変条件の検証
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(targetYearMonth, "targetYearMonth must not be null");

        return new IncomeAndExpenditure(
            userId, targetYearMonth,
            incomeAmount, withdrewAmount, estimatedAmount,
            expenditureAmount, balanceAmount
        );
    }

    /**
     * 収支金額を取得
     *
     * 注意: 照会機能では金額計算は行わない
     * 収支金額はデータベースに保存されている値をそのまま返す
     * （登録・更新機能で計算済みの値が保存されている）
     */
    public BalanceAmount getBalanceAmount() {
        return balanceAmount;
    }

    /**
     * 収入合計金額を取得（収入 + 積立取崩）
     */
    public TotalIncomeAmount getTotalIncome() {
        BigDecimal income = incomeAmount != null ? incomeAmount.getValue() : BigDecimal.ZERO;
        BigDecimal withdrew = withdrewAmount != null ? withdrewAmount.getValue() : BigDecimal.ZERO;
        return TotalIncomeAmount.from(income.add(withdrew));
    }

    /**
     * データが存在するか判定
     */
    public boolean isDataExists() {
        return incomeAmount != null;
    }

    /**
     * 空のデータか判定
     */
    public boolean isEmpty() {
        return !isDataExists();
    }
}
```

### 3.2 ドメインサービスの設計

#### IncomeAndExpenditureConsistencyService（整合性検証サービス）

**責務**:
- 収支データの整合性を検証
- 収入テーブル・支出テーブルとの整合性チェック

**なぜドメインサービスが必要か**:
- 整合性チェックは**複数のリポジトリ**にまたがる処理
- 集約単体では完結しない（外部データとの比較が必要）
- しかし、明確なビジネスルールである

**クラス図**:
```
┌──────────────────────────────────────────────────┐
│  IncomeAndExpenditureConsistencyService          │
├──────────────────────────────────────────────────┤
│ - incomeRepository: IncomeTableRepository        │
│ - expenditureRepository: ExpenditureTableRepo... │
├──────────────────────────────────────────────────┤
│ + validateIncomeConsistency(...)                 │
│ + validateExpenditureConsistency(...)            │
│ + validateAll(...)                               │
└──────────────────────────────────────────────────┘
```

**実装例**:
```java
@Service
@RequiredArgsConstructor
public class IncomeAndExpenditureConsistencyService {

    private final IncomeTableRepository incomeRepository;
    private final ExpenditureTableRepository expenditureRepository;

    /**
     * 収入金額の整合性を検証
     *
     * ビジネスルール:
     * 収支テーブルの収入金額 + 積立取崩金額 = 収入テーブルの合計金額
     *
     * @param aggregate 検証対象の収支集約
     * @param searchCondition 検索条件
     * @throws IncomeAmountInconsistencyException 整合性エラー
     */
    public void validateIncomeConsistency(
            IncomeAndExpenditure aggregate,
            SearchQueryUserIdAndYearMonth searchCondition) {

        // 収入テーブルから合計金額を取得
        SyuunyuuKingakuTotalAmount actualTotal =
            incomeRepository.sumIncomeKingaku(searchCondition);

        // 収支集約から期待値を取得
        TotalIncomeAmount expectedTotal = aggregate.getTotalIncome();

        // 整合性チェック
        if (!expectedTotal.equals(actualTotal)) {
            throw new IncomeAmountInconsistencyException(
                String.format("収入金額が一致しません。yearMonth=%s, expected=%s, actual=%s",
                    searchCondition.getYearMonth(),
                    expectedTotal.getValue(),
                    actualTotal.getValue())
            );
        }
    }

    /**
     * 支出金額の整合性を検証
     *
     * ビジネスルール:
     * 収支テーブルの支出金額 = 支出テーブルの合計金額
     *
     * @param aggregate 検証対象の収支集約
     * @param searchCondition 検索条件
     * @throws ExpenditureAmountInconsistencyException 整合性エラー
     */
    public void validateExpenditureConsistency(
            IncomeAndExpenditure aggregate,
            SearchQueryUserIdAndYearMonth searchCondition) {

        // 支出テーブルから合計金額を取得
        SisyutuKingakuTotalAmount actualTotal =
            expenditureRepository.sumExpenditureKingaku(searchCondition);

        // 収支集約から期待値を取得
        ExpenditureAmount expectedAmount = aggregate.getExpenditureAmount();

        // 整合性チェック
        if (expectedAmount != null && !expectedAmount.equals(actualTotal)) {
            throw new ExpenditureAmountInconsistencyException(
                String.format("支出金額が一致しません。yearMonth=%s, expected=%s, actual=%s",
                    searchCondition.getYearMonth(),
                    expectedAmount.getValue(),
                    actualTotal.getValue())
            );
        }
    }

    /**
     * すべての整合性を一括検証
     */
    public void validateAll(
            IncomeAndExpenditure aggregate,
            SearchQueryUserIdAndYearMonth searchCondition) {

        validateIncomeConsistency(aggregate, searchCondition);
        validateExpenditureConsistency(aggregate, searchCondition);
    }
}
```

### 3.3 ドメイン例外の設計

**IncomeAmountInconsistencyException**:
```java
/**
 * 収入金額不整合例外
 *
 * ビジネスルール違反:
 * 収支テーブルの収入金額と収入テーブルの合計金額が一致しない
 */
public class IncomeAmountInconsistencyException extends MyHouseholdAccountBookRuntimeException {
    public IncomeAmountInconsistencyException(String message) {
        super(message);
    }
}
```

**ExpenditureAmountInconsistencyException**:
```java
/**
 * 支出金額不整合例外
 *
 * ビジネスルール違反:
 * 収支テーブルの支出金額と支出テーブルの合計金額が一致しない
 */
public class ExpenditureAmountInconsistencyException extends MyHouseholdAccountBookRuntimeException {
    public ExpenditureAmountInconsistencyException(String message) {
        super(message);
    }
}
```

---

## 4. 層間の責務分離

### 4.1 リファクタリング後の処理フロー

```
【現状】
Controller
    ↓
UseCase (268行) ← すべてここに集中
  - データ取得
  - 整合性チェック ← ドメインロジック
  - 金額計算      ← ドメインロジック
  - Response変換
    ↓
Response (build()でビュー切り替え)
```

```
【リファクタリング後】
Controller
  - UseCaseの呼び出し
  - ビュー切り替え判定 ← Responseから移動
    ↓
UseCase (~50行)
  - リポジトリからデータ取得
  - ドメインサービス呼び出し ← オーケストレーションのみ
  - Response変換
    ↓
DomainService
  - 整合性検証 ← ビジネスロジック
    ↓
Aggregate
  - 金額計算 ← ビジネスロジック
  - 不変条件保証
    ↓
Response (DTO)
  - データ保持のみ
```

### 4.2 UseCase層のリファクタリング方針

#### リファクタリング前（268行）:
```java
public AccountMonthInquiryResponse execRead(LoginUserInfo user, String yearMonth) {
    // 1. データ取得
    SearchQueryUserIdAndYearMonth inquiryModel = SearchQueryUserIdAndYearMonth.from(user.getUserId(), yearMonth);
    IncomeAndExpenditureItem sisyutuResult = sisyutuRepository.findByIdAndYearMonth(inquiryModel);

    // 2. データ存在チェック
    if(sisyutuResult.isEmpty()) {
        AccountMonthInquiryResponse response = AccountMonthInquiryResponse.getInstance(targetYearMonthInfo);
        response.setSyuusiDataFlg(false);
        return response;
    }

    // 3. 整合性チェック（収入） ← ドメインロジック
    SyuunyuuKingakuTotalAmount incomeKingakuTotalAmount = incomeRepository.sumIncomeKingaku(inquiryModel);
    IncomingAmount chkIncomeAmount = IncomingAmount.from(sisyutuResult.getSyuunyuuKingaku(), sisyutuResult.getWithdrewKingaku());
    if(!chkIncomeAmount.getSyuunyuuKingakuTotalAmount().equals(incomeKingakuTotalAmount)) {
        throw new MyHouseholdAccountBookRuntimeException("収入情報が一致しません");
    }

    // 4. 整合性チェック（支出） ← ドメインロジック
    SisyutuKingakuTotalAmount expenditureKingakuTotalAmount = expenditureRepository.sumExpenditureKingaku(inquiryModel);
    SisyutuKingakuTotalAmount chkExpenditureKingaku = SisyutuKingakuTotalAmount.from(sisyutuResult.getSisyutuKingaku().getValue());
    if(!chkExpenditureKingaku.equals(expenditureKingakuTotalAmount)) {
        throw new MyHouseholdAccountBookRuntimeException("支出情報が一致しません");
    }

    // 5. 支出項目リスト取得
    AccountMonthInquiryExpenditureItemList sisyutuList = sisyutuKingakuRepository.selectMonthSisyutuKingakuList(inquiryModel);

    // 6. Response変換
    // ... 40行以上の変換処理
}
```

#### リファクタリング後（~50行）:
```java
public AccountMonthInquiryResponse execRead(LoginUserInfo user, String yearMonth) {
    // 1. データ取得
    SearchQueryUserIdAndYearMonth searchCondition =
        SearchQueryUserIdAndYearMonth.from(user.getUserId(), yearMonth);

    IncomeAndExpenditure aggregate =
        incomeAndExpenditureRepository.findByUserIdAndYearMonth(searchCondition);

    // 2. データ存在チェック
    if (aggregate.isEmpty()) {
        return createEmptyResponse(targetYearMonthInfo);
    }

    // 3. 整合性検証（ドメインサービスに委譲）
    consistencyService.validateAll(aggregate, searchCondition);

    // 4. 支出項目リスト取得
    AccountMonthInquiryExpenditureItemList expenditureList =
        expenditureItemRepository.findByUserIdAndYearMonth(searchCondition);

    // 5. Response変換
    return createResponse(aggregate, expenditureList, targetYearMonthInfo);
}

// Responseの生成ロジックをヘルパーメソッドに抽出
private AccountMonthInquiryResponse createResponse(
        IncomeAndExpenditure aggregate,
        AccountMonthInquiryExpenditureItemList expenditureList,
        AccountMonthInquiryTargetYearMonthInfo targetYearMonthInfo) {

    AccountMonthInquiryResponse response =
        AccountMonthInquiryResponse.getInstance(targetYearMonthInfo);

    // 金額情報の設定（データベースから取得した値をそのまま設定）
    response.setSyuunyuuKingaku(aggregate.getIncomeAmount().formatValue());
    response.setWithdrewKingaku(aggregate.getWithdrewAmount().formatValue());
    response.setSisyutuYoteiKingaku(aggregate.getEstimatedAmount().formatValue());
    response.setSisyutuKingaku(aggregate.getExpenditureAmount().formatValue());
    response.setSyuusiKingaku(aggregate.getBalanceAmount().formatValue());  // DB保存済みの収支金額

    // 支出項目リストの設定
    response.addExpenditureItemList(convertToExpenditureListItems(expenditureList));

    return response;
}
```

### 4.3 Response層のリファクタリング方針

#### リファクタリング前:
```java
// AccountMonthInquiryResponse.java
@Override
public ModelAndView build() {
    if(syuusiDataFlg) {  // ← ビジネスロジック判定
        ModelAndView modelAndView = createModelAndView("account/inquiry/AccountMonth");
        // ...
        return modelAndView;
    } else {
        ModelAndView modelAndView = createModelAndView("account/inquiry/AccountMonthRegistCheck");
        // ...
        return modelAndView;
    }
}
```

#### リファクタリング後:

**Response層**: build()からビジネスロジックを削除
```java
// AccountMonthInquiryResponse.java
@Override
public ModelAndView build() {
    // 常に同じビューを返す（ビジネスロジック削除）
    ModelAndView modelAndView = createModelAndView("account/inquiry/AccountMonth");
    modelAndView.addObject("targetYearMonthInfo", targetYearMonthInfo);
    modelAndView.addObject("syuunyuuKingaku", syuunyuuKingaku);
    modelAndView.addObject("sisyutuKingaku", sisyutuKingaku);
    modelAndView.addObject("withdrewKingaku", withdrewKingaku);
    modelAndView.addObject("sisyutuYoteiKingaku", sisyutuYoteiKingaku);
    modelAndView.addObject("syuusiKingaku", syuusiKingaku);
    modelAndView.addObject("expenditureItemList", expenditureItemList);
    return modelAndView;
}
```

**Controller層**: ビュー切り替えロジックを追加
```java
// AccountMonthInquiryController.java
@PostMapping("/myhacbook/accountinquiry/accountmonth/")
public ModelAndView getAccountMonth(...) {
    AccountMonthInquiryResponse response = useCase.execRead(user, yearMonth);

    // ビジネスロジックに基づいてビューを切り替え
    if (response.isDataExists()) {
        return response.build();  // 通常の収支画面
    } else {
        // データなしの場合は登録確認画面へ
        ModelAndView modelAndView = new ModelAndView("account/inquiry/AccountMonthRegistCheck");
        modelAndView.addObject("targetYearMonthInfo", response.getTargetYearMonthInfo());
        return modelAndView;
    }
}
```

### 4.4 ExpenditureListItemの扱い

**ユーザー様からの質問**:
> プレゼンテーション層にドメインオブジェクトを持つことがDDDの設計上正しい設計であるなら、ExpenditureListItemではなくドメインオブジェクトをメンバに持ってもいいのではないか？

**回答**: **NO** - プレゼンテーション層にドメインオブジェクトを直接持たせるべきではありません。

#### なぜダメなのか

```
【NG例】
Domain層: ExpenditureItem (ドメインオブジェクト)
            ↓ 直接参照
Presentation層: Response が ExpenditureItem を保持
```

**問題点**:
1. **関心の分離違反**: ドメイン層とプレゼンテーション層が密結合
2. **変更の影響**: ドメインモデル変更時に画面層も変更が必要
3. **表示形式の制約**: ドメインオブジェクトは表示に最適化されていない
4. **カプセル化の破壊**: ドメインの内部構造が外部に露出

#### 正しいアプローチ

```
【OK例】
Domain層: ExpenditureItem (ドメインオブジェクト)
            ↓ 変換
Application層: UseCase が DTO に変換
            ↓
Presentation層: ExpenditureListItem (DTO)
```

**ExpenditureListItemの役割**:
- **DTO (Data Transfer Object)**: 層間のデータ転送専用オブジェクト
- **表示最適化**: 画面表示に必要な形式にフォーマット済み
- **変更の影響を遮断**: ドメインモデル変更の影響を受けない

**実装方針**:
```java
// UseCase層で変換
private List<ExpenditureListItem> convertToExpenditureListItems(
        AccountMonthInquiryExpenditureItemList domainList) {

    return domainList.getValues().stream()
        .map(domainItem -> ExpenditureListItem.from(
            domainItem.getSisyutuItemLevel(),
            domainItem.getSisyutuItemName(),
            domainItem.getSisyutuKingaku().formatValue(),  // ドメイン→表示用フォーマット
            domainItem.getSisyutuKingakuB().formatValue(),
            domainItem.calculatePercentageB(),  // ← 割合計算はドメイン層で実行
            domainItem.getSisyutuKingakuC().formatValue(),
            domainItem.calculatePercentageC(),  // ← 割合計算はドメイン層で実行
            domainItem.getSisyutuKingakuBC().formatValue(),
            domainItem.calculatePercentage(),   // ← 割合計算はドメイン層で実行
            domainItem.getSiharaiDate().formatValue()
        ))
        .collect(Collectors.toList());
}
```

---

## 5. 実装順序

### 5.1 Step 2-1: ドメイン例外の作成

**作成するファイル**:
1. `IncomeAmountInconsistencyException.java`
2. `ExpenditureAmountInconsistencyException.java`

**実装時間**: 10分

### 5.2 Step 2-2: 集約の作成

**作成するファイル**:
1. `IncomeAndExpenditure.java` (集約ルート)

**修正するファイル**:
- 既存の`IncomeAndExpenditureItem.java`をリファクタリング

**実装時間**: 30分

### 5.3 Step 2-3: ドメインサービスの作成

**作成するファイル**:
1. `IncomeAndExpenditureConsistencyService.java`

**実装時間**: 20分

### 5.4 Step 2-4: リポジトリインターフェースの調整

**修正するファイル**:
- `IncomeAndExpenditureTableRepository.java`: 戻り値を`IncomeAndExpenditure`に変更

**実装時間**: 10分

### 5.5 Step 2-5: 統合テストの実行

**確認事項**:
- 既存の9つの統合テストがすべてパスすることを確認
- ドメインロジック変更後もテストが通ることを確認

**実装時間**: 5分

---

## 6. まとめ

### 6.1 リファクタリングのゴール

| 項目 | リファクタリング前 | リファクタリング後 |
|------|-------------------|-------------------|
| UseCase行数 | 268行 | ~50行 |
| ビジネスロジックの場所 | UseCase層に集中 | Domain層に集約 |
| Response層の責務 | ビュー切り替えロジックあり | DTO変換のみ |
| テスト容易性 | UseCase層の巨大テスト | ドメイン層の単体テスト可能 |

### 6.2 DDD設計の利点

1. **保守性向上**: ビジネスロジックがドメイン層に集約され、変更箇所が明確
2. **テスト容易性**: ドメイン層を単独でテスト可能
3. **可読性向上**: UseCase層がシンプルになり、処理の流れが理解しやすい
4. **再利用性**: ドメインロジックを他のユースケースから再利用可能

### 6.3 次のステップ

Phase 2 Step 3で、実際にユースケース層のリファクタリングを実施します。

---

## 7. 将来の統合計画（Phase 3以降）

### 7.1 現在の設計判断

**Phase 2では2つのドメインモデルが共存**:
- `IncomeAndExpenditure`: 照会機能用（今回新規作成）
- `IncomeAndExpenditureItem`: 登録・更新機能用（既存）

**共存させる理由**:
1. **Phase 2の範囲を照会機能に限定**し、リスクを最小化
2. **段階的リファクタリング**による安全性確保
3. **統合テストの継続的な成功**を保証
4. 登録・更新機能への影響を避け、**影響範囲を明確化**

### 7.2 IncomeAndExpenditureItemの現在の役割

**既存の実装** (src/main/java/.../domain/model/account/inquiry/IncomeAndExpenditureItem.java):

```java
/**
 * 収支テーブル情報の値を表すドメインモデル
 *
 * 現在の使用箇所:
 * 1. 照会機能（Phase 2でIncomeAndExpenditureに移行予定）
 * 2. 登録・更新機能（Phase 3以降で移行予定）
 */
public class IncomeAndExpenditureItem {

    // 既存の金額計算ロジック（登録・更新機能で使用中）

    /**
     * 新規登録時の収支テーブル情報を生成
     * - 入り方金額 = 収入金額合計 + 積立金取崩金額合計
     * - 収支金額 = 入り方金額 - 支出金額合計
     */
    public static IncomeAndExpenditureItem createAddTypeIncomeAndExpenditureItem(...) {
        // 金額計算ロジック（lines 134-137）
    }

    /**
     * 更新時の収支テーブル情報を生成
     */
    public static IncomeAndExpenditureItem createUpdTypeIncomeAndExpenditureItem(...) {
        // 金額計算ロジック（lines 178-181）
    }

    /**
     * 支出金額を加算
     */
    public IncomeAndExpenditureItem addSisyutuKingaku(SisyutuKingaku addValue) {
        // 金額計算ロジック（lines 229-233）
    }

    /**
     * 支出金額を減算
     */
    public IncomeAndExpenditureItem subtractSisyutuKingaku(SisyutuKingaku subtractValue) {
        // 金額計算ロジック（lines 266-270）
    }
}
```

### 7.3 Phase 3での統合計画

**目標**: 単一のドメインモデルへの統合

#### 7.3.1 統合手順

**Step 1**: 登録・更新機能の統合テスト作成
```
- 収支登録機能の統合テスト
- 収支更新機能の統合テスト
- 支出追加機能の統合テスト
- 支出削除機能の統合テスト
```

**Step 2**: IncomeAndExpenditureItemのロジックをIncomeAndExpenditureに移行
```java
public class IncomeAndExpenditure {

    // 照会用メソッド（Phase 2で実装済み）
    public static IncomeAndExpenditure reconstruct(...) { }
    public TotalIncomeAmount getTotalIncome() { }
    public BalanceAmount getBalanceAmount() { }

    // 登録・更新用メソッド（Phase 3で追加）

    /**
     * 新規登録時の収支集約を生成
     *
     * ビジネスルール:
     * - 入り方金額 = 収入金額合計 + 積立金取崩金額合計
     * - 収支金額 = 入り方金額 - 支出金額合計
     */
    public static IncomeAndExpenditure createNew(
            UserId userId,
            YearMonth yearMonthDomain,
            SyuunyuuKingakuTotalAmount incomeKingakuTotalAmount,
            WithdrewKingakuTotalAmount withdrewKingakuTotalAmount,
            SisyutuYoteiKingakuTotalAmount sisyutuYoteiKingakuTotalAmount,
            SisyutuKingakuTotalAmount sisyutuKingakuTotalAmount) {

        // 入り方金額の計算
        BigDecimal incomingAmount = incomeKingakuTotalAmount.getValue()
            .add(withdrewKingakuTotalAmount.getNullSafeValue());

        // 収支金額の計算
        BigDecimal balanceAmount = incomingAmount
            .subtract(sisyutuKingakuTotalAmount.getValue());

        return new IncomeAndExpenditure(
            userId,
            yearMonthDomain,
            IncomeAmount.from(incomeKingakuTotalAmount.getValue()),
            WithdrewAmount.from(withdrewKingakuTotalAmount.getValue()),
            EstimatedExpenditureAmount.from(sisyutuYoteiKingakuTotalAmount.getValue()),
            ExpenditureAmount.from(sisyutuKingakuTotalAmount.getValue()),
            BalanceAmount.from(balanceAmount)
        );
    }

    /**
     * 更新時の収支集約を生成
     */
    public static IncomeAndExpenditure createForUpdate(
            UserId userId,
            YearMonth yearMonthDomain,
            SyuunyuuKingakuTotalAmount incomeKingakuTotalAmount,
            WithdrewKingakuTotalAmount withdrewKingakuTotalAmount,
            SisyutuKingakuTotalAmount expenditureKingakuTotalAmount) {

        // 入り方金額の計算
        BigDecimal incomingAmount = incomeKingakuTotalAmount.getValue()
            .add(withdrewKingakuTotalAmount.getNullSafeValue());

        // 収支金額の計算
        BigDecimal balanceAmount = incomingAmount
            .subtract(expenditureKingakuTotalAmount.getValue());

        return new IncomeAndExpenditure(
            userId,
            yearMonthDomain,
            IncomeAmount.from(incomeKingakuTotalAmount.getValue()),
            WithdrewAmount.from(withdrewKingakuTotalAmount.getValue()),
            EstimatedExpenditureAmount.ZERO,  // 更新時は支出予定金額をゼロに設定
            ExpenditureAmount.from(expenditureKingakuTotalAmount.getValue()),
            BalanceAmount.from(balanceAmount)
        );
    }

    /**
     * 支出金額を加算した新しい収支集約を生成
     *
     * 不変性を保つため、現在のオブジェクトは変更せず新しいオブジェクトを返す
     */
    public IncomeAndExpenditure addExpenditure(ExpenditureAmount addValue) {

        // 新しい支出金額
        ExpenditureAmount newExpenditureAmount =
            ExpenditureAmount.from(
                this.expenditureAmount.getValue().add(addValue.getValue())
            );

        // 入り方金額の計算
        BigDecimal incomingAmount = this.incomeAmount.getValue()
            .add(this.withdrewAmount.getNullSafeValue());

        // 新しい収支金額の計算
        BigDecimal newBalanceAmount = incomingAmount
            .subtract(newExpenditureAmount.getValue());

        return new IncomeAndExpenditure(
            this.userId,
            this.targetYearMonth,
            this.incomeAmount,
            this.withdrewAmount,
            this.estimatedAmount,
            newExpenditureAmount,
            BalanceAmount.from(newBalanceAmount)
        );
    }

    /**
     * 支出金額を減算した新しい収支集約を生成
     */
    public IncomeAndExpenditure subtractExpenditure(ExpenditureAmount subtractValue) {

        // 新しい支出金額
        ExpenditureAmount newExpenditureAmount =
            ExpenditureAmount.from(
                this.expenditureAmount.getValue().subtract(subtractValue.getValue())
            );

        // 入り方金額の計算
        BigDecimal incomingAmount = this.incomeAmount.getValue()
            .add(this.withdrewAmount.getNullSafeValue());

        // 新しい収支金額の計算
        BigDecimal newBalanceAmount = incomingAmount
            .subtract(newExpenditureAmount.getValue());

        return new IncomeAndExpenditure(
            this.userId,
            this.targetYearMonth,
            this.incomeAmount,
            this.withdrewAmount,
            this.estimatedAmount,
            newExpenditureAmount,
            BalanceAmount.from(newBalanceAmount)
        );
    }
}
```

**Step 3**: 登録・更新機能のユースケースをリファクタリング
```
- IncomeAndExpenditureItemの使用箇所をIncomeAndExpenditureに置き換え
- 統合テストがパスすることを確認
```

**Step 4**: IncomeAndExpenditureItemを削除
```
- すべての参照がIncomeAndExpenditureに置き換わったことを確認
- IncomeAndExpenditureItem.javaを削除
- 最終的な統合テストの実行
```

### 7.4 統合後のメリット

**単一のドメインモデル**:
- 照会・登録・更新のすべての機能を1つのクラスで実現
- ビジネスロジックの重複が解消
- 保守性の向上

**明確な責務分離**:
```
照会機能: reconstruct() + getter methods
登録機能: createNew()
更新機能: createForUpdate()
金額操作: addExpenditure() / subtractExpenditure()
```

**テスト容易性の向上**:
- すべてのビジネスロジックを単一クラスでテスト可能
- 照会・登録・更新の整合性を保証

### 7.5 移行スケジュール（案）

| Phase | 対象機能 | 期間（目安） | 成果物 |
|-------|---------|------------|-------|
| Phase 2 | 照会機能のみ | 完了 | IncomeAndExpenditure（照会用） |
| Phase 3-1 | 登録機能の統合テスト作成 | 1週間 | 登録機能の統合テスト |
| Phase 3-2 | 登録機能のリファクタリング | 1週間 | createNew()実装 |
| Phase 3-3 | 更新機能の統合テスト作成 | 1週間 | 更新機能の統合テスト |
| Phase 3-4 | 更新機能のリファクタリング | 1週間 | createForUpdate()実装 |
| Phase 3-5 | 支出操作機能のリファクタリング | 1週間 | addExpenditure()/subtractExpenditure()実装 |
| Phase 3-6 | IncomeAndExpenditureItem削除 | 1日 | クリーンアップ完了 |

**重要な原則**:
- **各フェーズで統合テストがパスすることを確認**
- **段階的にリファクタリング**し、影響範囲を最小化
- **既存機能を壊さない**ことを最優先

---

**最終更新日**: 2025年12月5日
**作成者**: DDD Refactoring Project Phase 2
