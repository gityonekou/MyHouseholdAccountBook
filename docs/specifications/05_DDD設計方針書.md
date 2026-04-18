# マイ家計簿アプリケーション DDD設計方針書

**作成日**: 2024年11月  
**更新日**: 2025年11月27日（固定費管理の設計を追加）  
**対象**: リファクタリングプロジェクト全体

---

## 目次

1. [DDDの基本原則](#1-dddの基本原則)
2. [レイヤー構成](#2-レイヤー構成)
3. [パッケージ構成](#3-パッケージ構成)
4. [集約の設計](#4-集約の設計)
5. [値オブジェクトの設計](#5-値オブジェクトの設計)
6. [リポジトリパターン](#6-リポジトリパターン)
7. [ドメインサービス](#7-ドメインサービス)
8. [DDDの適用度合いと柔軟性](#8-dddの適用度合いと柔軟性) ⭐ **NEW**

---

## 1. DDDの基本原則

### 1.1 ユビキタス言語

業務用語を統一し、コードに反映します。

**主要な用語**:
- **収支**: IncomeAndExpenditure
- **収入**: Income
- **支出**: Expenditure
- **固定費**: FixedCost
- **買い物**: Shopping
- **対象年月**: TargetYearMonth

### 1.2 境界づけられたコンテキスト

家計簿アプリケーションを以下のコンテキストに分割：

1. **収支管理コンテキスト** - 月次・年次の収支管理、固定費管理
2. **買い物管理コンテキスト** - 日々の買い物記録
3. **マスタ管理コンテキスト** - 支出項目、店舗、イベント

#### コンテキスト図

```
┌─────────────────────────────────────┐
│   収支管理コンテキスト              │
│                                     │
│  ・収支（IncomeAndExpenditure）     │
│  ・収入（Income）                   │
│  ・支出（Expenditure）              │
│  ・固定費（FixedCost）              │
│                                     │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│   買い物管理コンテキスト            │
│                                     │
│  ・買い物（Shopping）               │
│  ・買い物明細（ShoppingDetail）     │
│                                     │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│   マスタ管理コンテキスト            │
│                                     │
│  ・支出項目（ExpenditureCategory）  │
│  ・店舗（Shop）                     │
│  ・イベント（Event）                │
│                                     │
└─────────────────────────────────────┘
```

**固定費が収支管理コンテキストに属する理由**:
- 固定費の主な目的は「支出の自動生成」
- 収支管理機能と密接に関連
- 単純なCRUDではなくビジネスロジック（スケジュール判定、支出生成）を持つ

---

## 2. レイヤー構成

### 2.1 4層アーキテクチャ

```
┌─────────────────────────────────────┐
│   Presentation Layer (Controller)   │
│   - REST API                         │
│   - View (Thymeleaf)                 │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│   Application Layer (UseCase)       │
│   - トランザクション境界             │
│   - DTO変換                          │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│   Domain Layer                       │
│   - 集約（Aggregate）                │
│   - エンティティ（Entity）           │
│   - 値オブジェクト（Value Object）   │
│   - ドメインサービス                 │
│   - リポジトリIF                     │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│   Infrastructure Layer               │
│   - リポジトリ実装（DataSource）     │
│   - MyBatis Mapper                   │
│   - DTO                              │
└─────────────────────────────────────┘
```

### 2.2 依存関係のルール

- **上位層は下位層に依存できる**
- **下位層は上位層に依存してはいけない**
- **ドメイン層はどの層にも依存しない**（依存性逆転の原則）

---

## 3. パッケージ構成

```
com.yonetani.webapp.accountbook
├── presentation
│   └── controller          # コントローラー
│
├── application
│   └── usecase             # ユースケース（既存）
│
├── domain
│   ├── model               # ドメインモデル
│   │   ├── account         # 収支管理
│   │   │   ├── IncomeAndExpenditure.java（集約ルート）
│   │   │   ├── Income.java（エンティティ）
│   │   │   ├── Expenditure.java（エンティティ）
│   │   │   └── FixedCost.java（集約ルート）
│   │   ├── shopping        # 買い物管理
│   │   │   ├── Shopping.java（集約ルート）
│   │   │   └── ShoppingDetail.java（エンティティ）
│   │   └── master          # マスタ管理
│   │       ├── ExpenditureCategory.java
│   │       └── Shop.java
│   │
│   ├── type                # 値オブジェクト
│   │   ├── common          # 共通（全サブドメインで使用）
│   │   │   ├── Money.java（抽象基底クラス）
│   │   │   ├── IncomeAmount.java（収入金額）
│   │   │   ├── ExpenditureAmount.java（支出金額）
│   │   │   ├── BalanceAmount.java（収支金額）
│   │   │   ├── CouponAmount.java（クーポン金額）
│   │   │   ├── Identifier.java（抽象基底クラス）
│   │   │   └── TargetYearMonth.java
│   │   └── account         # 収支関連の特化型
│   │       ├── PaymentSchedule.java（固定費スケジュール）
│   │       ├── ValidPeriod.java（固定費有効期間）
│   │       └── ScheduleType.java（スケジュールタイプ列挙型）
│   │
│   ├── repository          # リポジトリIF
│   │   ├── IncomeAndExpenditureRepository.java
│   │   ├── FixedCostRepository.java
│   │   └── ShoppingRepository.java
│   │
│   ├── service             # ドメインサービス
│   │   ├── IncomeAndExpenditureService.java
│   │   └── IncomeAndExpenditureGenerationService.java
│   │
│   └── exception           # ドメイン例外
│       ├── DomainException.java
│       ├── InvalidValueException.java
│       └── FixedCostNotApplicableException.java
│
└── infrastructure
    └── datasource          # リポジトリ実装
        ├── IncomeAndExpenditureDataSource.java
        └── account         # 既存のDTO、Mapper
            ├── dto
            └── mapper
```

### 3.1 パッケージ設計の方針

#### 3.1.1 共通値オブジェクト（type.common）の配置基準

**基準**: 複数のサブドメインで使用される基本概念は`common`パッケージに配置

**common配置の対象**:
- 金額系値オブジェクト（Money, IncomeAmount, ExpenditureAmount, BalanceAmount, CouponAmount）
  - 理由: 収支、買い物、固定費など複数の機能で共通使用
  - ドメイン全体の基本概念

- 識別子系値オブジェクト（Identifier, UserId, など）
  - 理由: 全てのエンティティで使用される基本型

- 日付系値オブジェクト（TargetYearMonth）
  - 理由: 複数の機能で共通使用

**サブドメイン固有パッケージ（type.account など）の配置基準**:
- 特定のサブドメインでのみ使用される特化型
- 例: 収支計算の特殊なルールを持つ値オブジェクト

**メリット**:
1. **再利用性**: 共通の概念を全機能で統一的に使用
2. **依存関係の明確化**: 買い物機能がaccount配下に依存する不自然さを回避
3. **保守性**: 金額ルールの変更が一箇所で済む

---

## 4. 集約の設計

### 4.1 集約の定義

**集約 = 一貫性を保証すべきオブジェクトの塊**

### 4.2 主要な集約

#### 集約1: IncomeAndExpenditure（収支）

```java
public class IncomeAndExpenditure {
    // 集約ルート
    private IncomeAndExpenditureId id;
    private UserId userId;
    private TargetYearMonth targetYearMonth;
    
    // 内部エンティティ
    private List<Income> incomes;
    private List<Expenditure> expenditures;
    
    // 計算済み値（不変条件）
    private IncomeAmount totalIncome;
    private ExpenditureAmount totalExpenditure;
    private BalanceAmount balance;
    private ExpenditureSummary expenditureSummary;
    
    // 不変条件の保護
    private void ensureConsistency() {
        // totalIncome = sum(incomes)
        // totalExpenditure = sum(expenditures)
        // balance = totalIncome - totalExpenditure
    }
}
```

**不変条件**:
1. 収入合計 = 各収入の合計
2. 支出合計 = 各支出の合計
3. 収支 = 収入合計 - 支出合計

#### 集約2: Shopping（買い物）

```java
public class Shopping {
    // 集約ルート
    private ShoppingId id;
    private ShoppingDate shoppingDate;
    private ShopId shopId;
    
    // 内部エンティティ
    private List<ShoppingDetail> details;
    
    // 計算済み値
    private Amount totalAmount;
    
    // ビジネスメソッド
    public Expenditure toExpenditure() {
        // 買い物を支出に変換
    }
}
```

#### 集約3: FixedCost（固定費）

```java
public class FixedCost {
    // 集約ルート
    private FixedCostId id;
    private UserId userId;
    
    // 固定費情報
    private FixedCostName name;              // 例: "家賃"
    private ExpenditureAmount amount;        // 例: 100,000円
    private ExpenditureCategoryId categoryId; // 支出項目ID
    
    // スケジュール（重要な業務ルール）
    private PaymentSchedule schedule;        // 値オブジェクト
    // - 毎月X日
    // - 年1回X月X日
    // - 2ヶ月ごと、など
    
    // 有効期間
    private ValidPeriod validPeriod;         // 値オブジェクト
    // - 開始年月
    // - 終了年月（Optional）
    
    // ビジネスメソッド
    
    /**
     * 指定月に固定費が発生するか判定
     */
    public boolean shouldGenerateFor(TargetYearMonth yearMonth) {
        return validPeriod.contains(yearMonth) 
            && schedule.matchesMonth(yearMonth);
    }
    
    /**
     * 指定月の支出を生成
     */
    public Expenditure generateExpenditure(
            TargetYearMonth yearMonth,
            ExpenditureId newId) {
        
        if (!shouldGenerateFor(yearMonth)) {
            throw new FixedCostNotApplicableException(
                "この月には固定費は発生しません");
        }
        
        // 支出エンティティを生成
        return Expenditure.create(
            newId,
            ExpenditureName.of(this.name.getValue() + "（固定費）"),
            this.amount,
            PaymentDate.fromSchedule(yearMonth, this.schedule),
            this.categoryId,
            Optional.empty() // イベントなし
        );
    }
}
```

**不変条件**:
1. 有効期間内でのみ支出を生成可能
2. スケジュールに合致する月のみ支出を生成
3. 生成される支出は固定費の情報を引き継ぐ

**業務ルール**:
- 固定費は定期的な支出を表す
- 収支自動生成時に該当月の固定費から支出を生成
- スケジュールパターン（毎月、年次、隔月など）に応じて判定

### 4.3 集約の境界

**原則**: 1つの集約 = 1つのトランザクション境界

- 収支と買い物は **別々の集約**
- 収支と固定費も **別々の集約**
- 支出項目マスタも **独立した集約**

**固定費と収支の関係**:
- 固定費は収支を「生成する」関係
- 同一トランザクションで扱う必要はない
- ドメインサービス（IncomeAndExpenditureGenerationService）が両者を協調させる

```
FixedCost（固定費）
    ↓ generateExpenditure()
Expenditure（支出）
    ↓ addExpenditure()
IncomeAndExpenditure（収支）
```

---

## 5. 値オブジェクトの設計

### 5.1 値オブジェクトの特徴

1. **不変性** - 一度生成したら変更できない
2. **等価性** - 値が同じなら等しい
3. **自己検証** - 不正な値を持たない

### 5.2 金額系の値オブジェクト

#### Money（抽象クラス）

```java
public abstract class Money {
    protected final BigDecimal value;
    
    protected Money(BigDecimal value) {
        this.value = Objects.requireNonNull(value);
    }
    
    public Money add(Money other) {
        return createInstance(this.value.add(other.value));
    }
    
    protected abstract Money createInstance(BigDecimal value);
}
```

#### 具象クラス

```java
// 収入金額（0以上）
public class IncomeAmount extends Money {
    private IncomeAmount(BigDecimal value) {
        super(value);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidValueException("収入金額は0以上");
        }
    }
    
    public static IncomeAmount of(BigDecimal value) {
        return new IncomeAmount(value);
    }
    
    public static final IncomeAmount ZERO = of(BigDecimal.ZERO);
}

// 支出金額（0以上）
public class ExpenditureAmount extends Money { ... }

// 収支金額（マイナスOK）
public class BalanceAmount extends Money {
    public static BalanceAmount calculate(
            IncomeAmount income, 
            ExpenditureAmount expenditure) {
        return new BalanceAmount(
            income.getValue().subtract(expenditure.getValue())
        );
    }
    
    public boolean isDeficit() {
        return value.compareTo(BigDecimal.ZERO) < 0;
    }
}
```

### 5.3 ID系の値オブジェクト

#### Identifier（抽象クラス）

```java
public abstract class Identifier {
    protected final String value;
    
    protected Identifier(String value) {
        if (value == null || value.isEmpty()) {
            throw new InvalidValueException("IDは必須");
        }
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
```

#### 具象クラス

```java
// ユーザーID
public class UserId extends Identifier {
    private UserId(String value) {
        super(value);
        if (value.length() > 50) {
            throw new InvalidValueException("ユーザーIDは50文字以内");
        }
    }
    
    public static UserId of(String value) {
        return new UserId(value);
    }
}

// 収支ID（複合キー）
public class IncomeAndExpenditureId extends Identifier {
    private IncomeAndExpenditureId(String value) {
        super(value);
    }
    
    public static IncomeAndExpenditureId of(
            UserId userId, 
            TargetYearMonth yearMonth) {
        String value = userId.getValue() + ":" + yearMonth.toYYYYMM();
        return new IncomeAndExpenditureId(value);
    }
}
```

### 5.4 日付系の値オブジェクト

```java
// 対象年月（重要な値オブジェクト）
public class TargetYearMonth {
    private final YearMonth value;
    
    private TargetYearMonth(YearMonth value) {
        this.value = Objects.requireNonNull(value);
    }
    
    public static TargetYearMonth of(int year, int month) {
        return new TargetYearMonth(YearMonth.of(year, month));
    }
    
    public static TargetYearMonth of(String yyyyMM) {
        return new TargetYearMonth(
            YearMonth.parse(yyyyMM, DateTimeFormatter.ofPattern("yyyyMM"))
        );
    }
    
    public TargetYearMonth nextMonth() {
        return new TargetYearMonth(value.plusMonths(1));
    }
    
    public TargetYearMonth previousMonth() {
        return new TargetYearMonth(value.minusMonths(1));
    }
    
    public String toYYYYMM() {
        return value.format(DateTimeFormatter.ofPattern("yyyyMM"));
    }
}
```

### 5.5 固定費関連の値オブジェクト

固定費のスケジュール管理と有効期間を表す値オブジェクト。

**注記**:
- ValidPeriod（有効期間）は将来の拡張性を考慮した設計です
- 現状の画面では有効期間の入力項目は存在しません
- Phase 3の固定費管理リファクタリング時に、実際の業務要件に応じて以下を判断します：
  - ValidPeriodを実装するか
  - シンプルに開始年月のみで運用するか
  - 将来の拡張に備えて設計のみ残すか

#### PaymentSchedule（支払いスケジュール）

```java
/**
 * 支払いスケジュール
 * 固定費がいつ発生するかを定義
 */
public class PaymentSchedule {
    private final ScheduleType type;  // MONTHLY, YEARLY, BIMONTHLY
    private final int dayOfMonth;     // 支払日（1-31）
    private final Optional<Integer> monthOfYear; // 年次の場合の月（1-12）
    
    private PaymentSchedule(ScheduleType type, int dayOfMonth, 
                           Optional<Integer> monthOfYear) {
        this.type = Objects.requireNonNull(type);
        
        if (dayOfMonth < 1 || dayOfMonth > 31) {
            throw new InvalidValueException("支払日は1-31の範囲");
        }
        this.dayOfMonth = dayOfMonth;
        
        if (monthOfYear.isPresent()) {
            int month = monthOfYear.get();
            if (month < 1 || month > 12) {
                throw new InvalidValueException("月は1-12の範囲");
            }
        }
        this.monthOfYear = monthOfYear;
    }
    
    /**
     * 毎月支払いのスケジュール
     */
    public static PaymentSchedule monthly(int dayOfMonth) {
        return new PaymentSchedule(ScheduleType.MONTHLY, dayOfMonth, 
                                   Optional.empty());
    }
    
    /**
     * 年1回支払いのスケジュール
     */
    public static PaymentSchedule yearly(int month, int dayOfMonth) {
        return new PaymentSchedule(ScheduleType.YEARLY, dayOfMonth, 
                                   Optional.of(month));
    }
    
    /**
     * 2ヶ月ごと支払いのスケジュール
     */
    public static PaymentSchedule bimonthly(int dayOfMonth) {
        return new PaymentSchedule(ScheduleType.BIMONTHLY, dayOfMonth, 
                                   Optional.empty());
    }
    
    /**
     * 指定月にこのスケジュールが該当するか判定
     */
    public boolean matchesMonth(TargetYearMonth yearMonth) {
        switch (type) {
            case MONTHLY:
                return true;  // 毎月発生
            case YEARLY:
                return yearMonth.getMonth() == monthOfYear.get();
            case BIMONTHLY:
                return yearMonth.getMonth() % 2 == 0;
            default:
                return false;
        }
    }
    
    public int getDayOfMonth() {
        return dayOfMonth;
    }
}

/**
 * スケジュールタイプ
 */
public enum ScheduleType {
    MONTHLY("毎月"),
    YEARLY("年1回"),
    BIMONTHLY("2ヶ月ごと");
    
    private final String displayName;
    
    ScheduleType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
```

#### ValidPeriod（有効期間）

```java
/**
 * 有効期間
 * 固定費がいつからいつまで有効かを定義
 */
public class ValidPeriod {
    private final TargetYearMonth startYearMonth;
    private final Optional<TargetYearMonth> endYearMonth;
    
    private ValidPeriod(TargetYearMonth startYearMonth, 
                       Optional<TargetYearMonth> endYearMonth) {
        this.startYearMonth = Objects.requireNonNull(startYearMonth);
        this.endYearMonth = Objects.requireNonNull(endYearMonth);
        
        // 終了年月は開始年月より後でなければならない
        if (endYearMonth.isPresent()) {
            if (!endYearMonth.get().isAfter(startYearMonth)) {
                throw new InvalidValueException(
                    "終了年月は開始年月より後でなければなりません");
            }
        }
    }
    
    /**
     * 期限なしの有効期間
     */
    public static ValidPeriod indefinite(TargetYearMonth startYearMonth) {
        return new ValidPeriod(startYearMonth, Optional.empty());
    }
    
    /**
     * 期限ありの有効期間
     */
    public static ValidPeriod limited(TargetYearMonth startYearMonth, 
                                     TargetYearMonth endYearMonth) {
        return new ValidPeriod(startYearMonth, Optional.of(endYearMonth));
    }
    
    /**
     * 指定月がこの有効期間に含まれるか判定
     */
    public boolean contains(TargetYearMonth yearMonth) {
        boolean afterStart = !yearMonth.isBefore(startYearMonth);
        boolean beforeEnd = endYearMonth
            .map(end -> !yearMonth.isAfter(end))
            .orElse(true);  // 終了なしの場合は常にtrue
        
        return afterStart && beforeEnd;
    }
    
    /**
     * 有効期間が終了しているか
     */
    public boolean isExpired(TargetYearMonth currentYearMonth) {
        return endYearMonth
            .map(end -> currentYearMonth.isAfter(end))
            .orElse(false);  // 終了なしの場合は期限切れにならない
    }
    
    public TargetYearMonth getStartYearMonth() {
        return startYearMonth;
    }
    
    public Optional<TargetYearMonth> getEndYearMonth() {
        return endYearMonth;
    }
}
```

---

## 6. リポジトリパターン

### 6.1 基本方針

- **インターフェースはドメイン層**に配置
- **実装はインフラ層**に配置（依存性逆転）
- **DTOの変換はリポジトリ実装**が担当

### 6.2 リポジトリインターフェース

```java
// domain/repository/IncomeAndExpenditureRepository.java
public interface IncomeAndExpenditureRepository {
    
    Optional<IncomeAndExpenditure> findById(IncomeAndExpenditureId id);
    
    Optional<IncomeAndExpenditure> findByUserIdAndYearMonth(
        UserId userId,
        TargetYearMonth yearMonth
    );
    
    void save(IncomeAndExpenditure incomeAndExpenditure);
    
    void delete(IncomeAndExpenditureId id);
    
    List<IncomeAndExpenditure> findByUserIdAndYear(
        UserId userId,
        int year
    );
}
```

### 6.3 リポジトリ実装

```java
// infrastructure/datasource/IncomeAndExpenditureDataSource.java
@Repository
public class IncomeAndExpenditureDataSource 
        implements IncomeAndExpenditureRepository {
    
    private final IncomeAndExpenditureMapper mapper;
    private final IncomeMapper incomeMapper;
    private final ExpenditureMapper expenditureMapper;
    
    @Override
    public Optional<IncomeAndExpenditure> findByUserIdAndYearMonth(
            UserId userId,
            TargetYearMonth yearMonth) {
        
        // 1. DTOを取得（既存のMapperを使用）
        IncomeAndExpenditureDto dto = mapper.selectByUserIdAndYearMonth(
            userId.getValue(),
            yearMonth.toYYYYMM()
        );
        
        if (dto == null) {
            return Optional.empty();
        }
        
        // 2. 収入リストを取得
        List<IncomeDto> incomeDtos = incomeMapper.selectByKey(...);
        List<Income> incomes = incomeDtos.stream()
            .map(this::convertToIncome)
            .collect(Collectors.toList());
        
        // 3. 支出リストを取得
        List<ExpenditureDto> expenditureDtos = expenditureMapper.selectByKey(...);
        List<Expenditure> expenditures = expenditureDtos.stream()
            .map(this::convertToExpenditure)
            .collect(Collectors.toList());
        
        // 4. ドメインモデルを再構築
        IncomeAndExpenditure domain = IncomeAndExpenditure.reconstruct(
            IncomeAndExpenditureId.of(userId, yearMonth),
            userId,
            yearMonth,
            incomes,
            expenditures
        );
        
        return Optional.of(domain);
    }
    
    private Income convertToIncome(IncomeDto dto) {
        return Income.reconstruct(
            IncomeId.of(dto.getIncomeCode()),
            IncomeName.of(dto.getIncomeName()),
            IncomeAmount.of(dto.getIncomeAmount()),
            IncomeDate.of(dto.getIncomeDate()),
            Optional.ofNullable(dto.getIncomeCategory())
                .map(IncomeCategory::of)
        );
    }
    
    private Expenditure convertToExpenditure(ExpenditureDto dto) {
        // 同様の変換処理
    }
}
```

### 6.4 DTO ↔ ドメインモデルの変換

**重要な原則**:
- **DTOはインフラ層のみ**で使用
- **ドメイン層はDTOを知らない**
- **変換はリポジトリ実装が担当**

```
[DTO（インフラ）] ←→ [Repository実装] ←→ [ドメインモデル（ドメイン）]
                       ↑
                     変換処理
```

---

## 7. ドメインサービス

### 7.1 ドメインサービスとは

**複数の集約にまたがるビジネスロジック**を扱うサービス

### 7.2 使用例

```java
// domain/service/IncomeAndExpenditureService.java
@Service
public class IncomeAndExpenditureService {
    
    /**
     * 整合性検証
     */
    public void validateConsistency(IncomeAndExpenditure incomeAndExpenditure) {
        
        // 収入合計の検証
        IncomeAmount calculatedIncome = incomeAndExpenditure.getIncomes()
            .stream()
            .map(Income::getAmount)
            .reduce(IncomeAmount.ZERO, IncomeAmount::add);
        
        if (!calculatedIncome.equals(incomeAndExpenditure.getTotalIncome())) {
            throw new IncomeAndExpenditureInconsistencyException(
                "収入合計が一致しません"
            );
        }
        
        // 支出合計の検証
        // 収支金額の検証
        // ...
    }
    
    /**
     * 月次締め処理
     */
    public void closeMonth(IncomeAndExpenditure incomeAndExpenditure) {
        // 整合性チェック
        validateConsistency(incomeAndExpenditure);
        
        // 締め処理
        incomeAndExpenditure.close();
    }
}
```

### 7.3 収支生成サービス（固定費連携）

固定費から支出を自動生成する責務を持つドメインサービス。

```java
// domain/service/IncomeAndExpenditureGenerationService.java
@Service
public class IncomeAndExpenditureGenerationService {
    
    private final FixedCostRepository fixedCostRepository;
    
    public IncomeAndExpenditureGenerationService(
            FixedCostRepository fixedCostRepository) {
        this.fixedCostRepository = fixedCostRepository;
    }
    
    /**
     * 指定月の収支を自動生成（固定費を含む）
     * 
     * @param userId ユーザーID
     * @param yearMonth 対象年月
     * @return 生成された収支（固定費からの支出を含む）
     */
    public IncomeAndExpenditure generateForMonth(
            UserId userId,
            TargetYearMonth yearMonth) {
        
        // 1. 新しい収支を作成
        IncomeAndExpenditure incomeAndExpenditure = 
            IncomeAndExpenditure.create(
                IncomeAndExpenditureId.of(userId, yearMonth),
                userId,
                yearMonth
            );
        
        // 2. ユーザーの固定費を取得
        List<FixedCost> fixedCosts = 
            fixedCostRepository.findByUserId(userId);
        
        // 3. 該当月に発生する固定費から支出を生成
        for (FixedCost fixedCost : fixedCosts) {
            if (fixedCost.shouldGenerateFor(yearMonth)) {
                
                // 新しい支出IDを生成
                ExpenditureId newId = ExpenditureId.generate();
                
                // 固定費から支出を生成
                Expenditure expenditure = 
                    fixedCost.generateExpenditure(yearMonth, newId);
                
                // 収支に支出を追加
                incomeAndExpenditure.addExpenditure(expenditure);
            }
        }
        
        return incomeAndExpenditure;
    }
    
    /**
     * 既存の収支に固定費からの支出を追加
     * 
     * @param incomeAndExpenditure 既存の収支
     */
    public void addFixedCostExpenditures(
            IncomeAndExpenditure incomeAndExpenditure) {
        
        UserId userId = incomeAndExpenditure.getUserId();
        TargetYearMonth yearMonth = incomeAndExpenditure.getTargetYearMonth();
        
        // ユーザーの固定費を取得
        List<FixedCost> fixedCosts = 
            fixedCostRepository.findByUserId(userId);
        
        // 該当月に発生する固定費から支出を生成して追加
        for (FixedCost fixedCost : fixedCosts) {
            if (fixedCost.shouldGenerateFor(yearMonth)) {
                ExpenditureId newId = ExpenditureId.generate();
                Expenditure expenditure = 
                    fixedCost.generateExpenditure(yearMonth, newId);
                incomeAndExpenditure.addExpenditure(expenditure);
            }
        }
    }
}
```

**このサービスの責務**:
1. FixedCost集約とIncomeAndExpenditure集約の協調
2. 固定費のスケジュール判定
3. 支出の自動生成と収支への追加
4. 両集約のライフサイクルの独立性を保つ

**使用シーン**:
- ユースケース層から呼び出される
- 収支の新規作成時
- 月次の収支自動生成バッチ

---

## 8. DDDの適用度合いと柔軟性

### 8.1 基本方針

**完全順守を基本としつつ、実用性も重視する**

家計簿アプリケーションは以下の理由からDDD完全順守が適切：
1. **コード規模**: 約18,000行（中規模）
2. **業務ルールの複雑性**: 整合性チェック、集計、階層構造など
3. **ドメインモデルの数**: 収支、買い物、固定費、支出項目など複数
4. **データの関連性**: 集約間の関連が多く複雑
5. **今後の拡張性**: 機能追加の可能性あり
6. **学習効果**: 個人プロジェクトとしてDDDを実践的に学べる

### 8.2 厳密に適用する領域（DDD完全順守）

以下の領域は**ビジネスロジックが複雑**であり、DDDの恩恵が大きいため完全順守：

#### 8.2.1 収支管理（IncomeAndExpenditure集約）

**理由**: 
- 複雑な不変条件（合計金額の整合性、収支計算）
- 多数の業務ルール
- データの一貫性が重要

**適用内容**:
```java
✅ 集約パターン（IncomeAndExpenditure、Income、Expenditure）
✅ 値オブジェクト（IncomeAmount、ExpenditureAmount、BalanceAmount）
✅ リポジトリパターン（IF + 実装の分離）
✅ ドメインサービス（整合性検証、月次締め）
✅ ドメイン例外（IncomeAndExpenditureInconsistencyException）
```

#### 8.2.2 買い物管理（Shopping集約）

**理由**:
- 明細の集約管理
- 支出への変換ロジック
- 将来的な拡張可能性

**適用内容**:
```java
✅ 集約パターン（Shopping、ShoppingDetail）
✅ リポジトリパターン
✅ ビジネスメソッド（toExpenditure()）
```

#### 8.2.3 支出項目の階層管理

**理由**:
- 親子関係の複雑さ
- 削除可否判定などのビジネスルール

**適用内容**:
```java
✅ 集約パターン（ExpenditureCategory）
✅ ドメインサービス（階層チェック、削除可否判定）
✅ リポジトリパターン
```

#### 8.2.4 固定費管理（FixedCost集約）

**理由**:
- 複雑なスケジュール判定ロジック
- 支出の自動生成という重要な業務ルール
- 収支管理との密接な連携
- 有効期間の管理

**適用内容**:
```java
✅ 集約パターン（FixedCost）
✅ 値オブジェクト（PaymentSchedule、ValidPeriod、ScheduleType）
✅ リポジトリパターン
✅ ドメインサービス（IncomeAndExpenditureGenerationService）
✅ ドメイン例外（FixedCostNotApplicableException）
```

**固定費がマスタ管理ではない理由**:
| 比較項目 | マスタ管理（例: 店舗） | 固定費 |
|---------|---------------------|--------|
| 業務ロジック | ほぼなし（CRUD） | **複雑**（スケジュール判定、支出生成） |
| 他集約との関連 | 参照されるのみ | **収支を生成する** |
| 変更頻度 | 低い | **定期的に評価される** |
| → 分類 | マスタ管理 | **独立した集約** |

---

### 8.3 柔軟にする領域（簡素化OK）

以下の領域は**ビジネスロジックがシンプル**なため、柔軟に対応：

#### 8.3.1 シンプルなマスタ管理

**対象**: 店舗マスタ、イベントマスタなど

**理由**:
- 単純なCRUD操作のみ
- 複雑な業務ルールがない
- 他の集約への影響が少ない

**簡素化の内容**:
```java
// リポジトリパターンは使うが、実装を簡素化
public interface ShopRepository {
    Optional<Shop> findById(ShopId id);
    List<Shop> findAll();
    void save(Shop shop);
    void delete(ShopId id);
}

// シンプルな実装でOK
@Repository
public class ShopDataSource implements ShopRepository {
    // 複雑な抽象化は不要
    // 直接的な変換処理で十分
}
```

#### 8.3.2 統計・レポート系クエリ

**対象**: 月次統計、年次レポートなど

**理由**:
- 読み取り専用
- ドメインロジックがない
- パフォーマンスが重要

**簡素化の内容**:
```java
// CQRS的なアプローチ
// QueryServiceで直接DTOを返却してもOK
public interface IncomeAndExpenditureQueryService {
    MonthlyStatisticsDto getMonthlyStatistics(UserId userId, int year);
    // リポジトリを経由せず、直接DTOを返す
}

@Service
public class IncomeAndExpenditureQueryServiceImpl {
    // MyBatisで直接集計クエリを実行
    // ドメインモデルへの変換は不要
}
```

#### 8.3.3 バッチ処理

**対象**: 月次締めバッチなど

**理由**:
- 一括処理のためパフォーマンスが重要
- ユーザー操作を経由しない

**柔軟な対応**:
```java
// ドメインサービスは使いつつ、
// パフォーマンス優先で一括処理もOK
@Service
public class MonthlyCloseBatchService {
    
    public void closeAllUsers(TargetYearMonth yearMonth) {
        // 複数ユーザーを一括処理
        // 必要に応じてJDBC直接アクセスもOK
    }
}
```

---

### 8.4 判断基準

新しい機能を追加する際、以下の基準でDDDの適用度合いを判断：

| 判断項目 | 厳密適用 | 柔軟対応 |
|---------|---------|---------|
| **業務ルールの複雑性** | 複雑な計算・検証がある | シンプルなCRUD |
| **データの一貫性** | 不変条件が多い | 制約が少ない |
| **集約間の関連** | 密に関連している | 独立している |
| **変更頻度** | 頻繁に変更される | ほぼ変更されない |
| **パフォーマンス要求** | 通常 | 非常に高い |
| **将来の拡張性** | 拡張可能性が高い | 固定的 |

#### 判断フロー

```
新機能の追加
    ↓
【質問1】複雑な業務ルールがあるか？
    YES → 厳密適用を検討
    NO  → 質問2へ
    ↓
【質問2】データの一貫性が重要か？
    YES → 厳密適用を検討
    NO  → 質問3へ
    ↓
【質問3】将来の拡張可能性は？
    高い → 厳密適用を検討
    低い → 柔軟対応でOK
```

---

### 8.5 オーバーエンジニアリングの回避

**避けるべきパターン**:

❌ **シンプルなエンティティに過度な抽象化**
```java
// 悪い例：たった3フィールドのエンティティに
public class EventName {
    private final String value;
    // 複雑なバリデーション、変換、etc...
}
→ 単純なStringで十分な場合もある
```

❌ **不要なドメインサービス**
```java
// 悪い例：単純なCRUDに不要なサービス
public class ShopDomainService {
    public void validateShop(Shop shop) {
        // 特に検証することがない...
    }
}
→ 集約内で完結する処理はサービス不要
```

❌ **過度なファクトリパターン**
```java
// 悪い例：シンプルな生成に複雑なファクトリ
public class ShopFactory {
    public Shop create(String name, String address) {
        // 特に複雑な生成ロジックがない...
        return new Shop(name, address);
    }
}
→ コンストラクタやファクトリメソッドで十分
```

---

### 8.6 実用的なバランス

**推奨アプローチ**:

```
【コア機能】（収支管理、買い物管理）
    ↓
DDD完全順守
- 集約パターン
- 値オブジェクト
- リポジトリパターン
- ドメインサービス
- ドメイン例外

【サポート機能】（マスタ管理）
    ↓
基本的なDDDパターン
- 集約（シンプルな構造）
- 値オブジェクト（必要最小限）
- リポジトリパターン

【ユーティリティ機能】（統計、レポート）
    ↓
柔軟なアプローチ
- QueryService（直接DTO返却）
- パフォーマンス優先
```

---

### 8.7 学習効果との両立

**個人プロジェクトの強み**:

✅ **試行錯誤ができる**
- 過度に複雑になったら簡素化
- 必要になったら厳密化

✅ **段階的に適用できる**
- Phase 1: コア機能を厳密に
- Phase 2-3: 学びを活かして他機能へ
- Phase 4: バランスを調整

✅ **実践的なスキル習得**
- DDDの適用判断力が身につく
- アーキテクチャの設計力が向上

---

### 8.8 まとめ

| 領域 | 適用度 | 理由 |
|------|-------|------|
| **収支管理** | 完全順守 | 複雑な業務ルール、重要な整合性 |
| **買い物管理** | 完全順守 | 集約管理、変換ロジック |
| **支出項目** | 完全順守 | 階層構造、削除制約 |
| **店舗マスタ** | 簡素化 | シンプルなCRUD |
| **統計・レポート** | 柔軟 | 読み取り専用、パフォーマンス重視 |
| **バッチ処理** | 柔軟 | 一括処理、パフォーマンス重視 |

**基本姿勢**:
- DDDの原則は守る
- 実用性も重視する
- 完璧主義にならない
- 継続的に改善する

---

## まとめ

この設計方針書に従うことで：

1. ✅ **業務ロジックがドメイン層に集約**される
2. ✅ **テストしやすいコード**になる
3. ✅ **保守性・拡張性**が向上する
4. ✅ **DDDの実践的な学習**ができる
5. ✅ **実用的なバランス**を保てる

リファクタリングの各フェーズで、この方針書を参照しながら実装を進めてください。

---

**作成者**: Claude (Anthropic)  
**最終更新**: 2025年11月17日
