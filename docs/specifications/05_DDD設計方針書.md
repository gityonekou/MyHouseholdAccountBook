# マイ家計簿アプリケーション DDD設計方針書

**作成日**: 2024年11月  
**更新日**: 2026年10月18日（固定費関連の記述を実装に合わせて修正。集約3・5.5節・7.3節。1.2節のコンテキスト図を支払方法・銀行口座管理の追加、買い物管理の実装状況に合わせて修正）  
**対象**: 本プロジェクト全体（新規機能開発・既存コードのリファクタリングを問わない）

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
3. **マスタ管理コンテキスト** - 支出項目、店舗、イベント、支払方法、銀行口座

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
│  ・買い物（Shopping）※目指す設計    │
│  ・買い物明細（ShoppingDetail）     │
│    ※目指す設計。可変件数の明細を    │
│      持つ構造は未実装               │
│                                     │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│   マスタ管理コンテキスト            │
│                                     │
│  ・支出項目（ExpenditureCategory）  │
│  ・店舗（Shop）                     │
│  ・イベント（Event）                │
│  ・支払方法（PaymentMethod）        │
│  ・銀行口座（BankAccount）          │
│                                     │
└─────────────────────────────────────┘
```

**固定費が収支管理コンテキストに属する理由**:
- 固定費の主な目的は「支出の自動生成」
- 収支管理機能と密接に関連
- 単純なCRUDではなくビジネスロジック（スケジュール判定、支出生成）を持つ

**支払方法・銀行口座がマスタ管理コンテキストに属する理由**:
- 支出・固定費・買い物登録から「選択される参照用マスタ」であり、スケジュール判定のような複雑な業務ロジックを持たない
- 店舗・イベントと同様、業務の主役ではなく他の機能から参照される位置づけ
- （参考）実装上のパッケージは`domain.model.account.paymentmethod`/`bankaccount`に置かれており、収支管理コンテキストのパッケージ配下にあるが、コンテキストとしての位置づけはマスタ管理である（feature-1.03-dev1で追加）

**買い物管理コンテキストの現状（2026年10月時点）**:
- 現在実装されているのは「買い物登録（簡易）」（`SimpleShoppingRegistItem`、`domain.model.account.shoppingregist`配下）のみ。食料品・外食・日用品・衣料品など**固定8カテゴリの金額**を1件の登録データで保持する簡易設計であり、`Shopping`集約・可変件数の`ShoppingDetail`という本来の設計（4.2節「集約2: Shopping」参照）はまだ実装されていない
- 4.2節・3節の`Shopping`/`ShoppingDetail`の記述は、今後この領域を拡張する際に目指すべき設計として残している（新規実装時は、簡易設計をそのまま複製するのではなく、この設計に近づける方向で検討すること）

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
    // 固定費情報（すべて値オブジェクト）
    private final UserId userId;
    private final FixedCostCode fixedCostCode;
    private final FixedCostName fixedCostName;                 // 固定費名(支払名)
    private final FixedCostDetailContext fixedCostDetailContext;
    private final ExpenditureItemCode expenditureItemCode;      // 支出項目コード
    private final FixedCostKubun fixedCostKubun;                // 固定費区分
    private final FixedCostTargetPaymentMonth fixedCostTargetPaymentMonth;   // 支払月（コード値）
    private final FixedCostTargetPaymentMonthOptionalContext fixedCostTargetPaymentMonthOptionalContext;
    private final FixedCostPaymentDay fixedCostPaymentDay;      // 支払日
    private final PaymentMethodCode paymentMethodCode;
    private final FixedCostPaymentAmount fixedCostPaymentAmount;

    // ビジネスメソッド

    /**
     * 一括更新項目（支払日、支払金額）のみを更新した新しいインスタンスを返す
     */
    public FixedCost updateBulkUpdateItem(
            FixedCostPaymentDay fixedCostPaymentDay,
            FixedCostPaymentAmount fixedCostPaymentAmount) {
        return new FixedCost(userId, fixedCostCode, fixedCostName, fixedCostDetailContext,
                expenditureItemCode, fixedCostKubun, fixedCostTargetPaymentMonth,
                fixedCostTargetPaymentMonthOptionalContext, fixedCostPaymentDay,
                paymentMethodCode, fixedCostPaymentAmount);
    }

    /**
     * 支出詳細の表示値（固定費内容詳細＋支払月任意詳細）を組み立てて返す
     */
    public String getExpenditureDetailContext() { /* ... */ }
}
```

**設計上の判断（当初構想との違い）**:
- 当初（Phase 3検討時点）は「指定月に固定費が発生するか」を`FixedCost`自身の`shouldGenerateFor()`で判定し、`PaymentSchedule`/`ValidPeriod`という専用の値オブジェクトで支払いパターン・有効期間を表現する構想だった
- 実際の要件（固定費の支払月パターンは「毎月/奇数月/偶数月/特定月/その他任意」の固定区分で十分に表現でき、有効期間の概念も不要）に対してはこの構想は過剰だったため、支払月を**コード値（`FixedCostTargetPaymentMonth`）**で保持し、月への該当判定はそのVOのメソッドに持たせるシンプルな設計を採用した（8.5節「オーバーエンジニアリングの回避」の実践例）
- そのため`FixedCost`自身には複雑な不変条件はなく、「指定月に支払いが発生するか」の判定は5.5節の`FixedCostTargetPaymentMonth`が担う
- 固定費から支出情報への変換（収支登録画面への反映等）は、`IncomeAndExpenditureGenerationService`のような専用ドメインサービスではなく、各UseCase（`FixedCostInquiryUseCase`、`FixedCostAnnualSummaryUseCase`、`IncomeAndExpenditureInitUseCase`等）が`FixedCostTableRepository`から固定費一覧を取得し、`FixedCostTargetPaymentMonth`の判定メソッドやリポジトリの検索条件で対象月分を絞り込む形で行っている（詳細は7.3節）

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

固定費の「対象月に支払いが発生するか」の判定を担う値オブジェクト。

**設計の経緯**: Phase 3検討時点では、支払いパターンを`PaymentSchedule`（毎月/年次/隔月などのタイプ＋日付）、有効期間を`ValidPeriod`（開始/終了年月）という専用の値オブジェクトで表現する構想だった。実際の要件では支払月パターンは固定のコード区分（毎月/奇数月/偶数月/特定月/その他任意）で表現でき、有効期間の概念も不要だったため、よりシンプルな`FixedCostTargetPaymentMonth`（コード値＋判定メソッド）に設計を収束させた（8.5節参照）。

#### FixedCostTargetPaymentMonth（固定費支払月）

```java
/**
 * 「固定費支払月(支払月)」項目の値を表すドメインタイプ
 * コード値: "00"=毎月, "20"=奇数月, "30"=偶数月, "40"=その他任意, "01"〜"12"=特定月
 */
public class FixedCostTargetPaymentMonth {
    private final String value;

    public static FixedCostTargetPaymentMonth from(String value) {
        return new FixedCostTargetPaymentMonth(value);
    }

    /**
     * 指定した月(1〜12)に支払いが発生するかどうかを判定する
     */
    public boolean appliesToMonth(int monthValue) {
        switch (value) {
            case SHIHARAI_TUKI_EVERY_SELECTED_VALUE:      // "00" 毎月
            case SHIHARAI_TUKI_OPTIONAL_SELECTED_VALUE:   // "40" その他任意
                return true;
            case SHIHARAI_TUKI_ODD_SELECTED_VALUE:        // "20" 奇数月
                return monthValue % 2 == 1;
            case SHIHARAI_TUKI_AN_EVEN_SELECTED_VALUE:    // "30" 偶数月
                return monthValue % 2 == 0;
            default:                                       // "01"〜"12" 特定月
                return Integer.parseInt(value) == monthValue;
        }
    }

    /**
     * 「その他任意」かどうかを判定する
     */
    public boolean isOptional() {
        return SHIHARAI_TUKI_OPTIONAL_SELECTED_VALUE.equals(value);
    }
}
```

**この設計で解決されたこと**: 以前は「対象月に支払いが発生するか」の判定ロジック（上記switch文相当）が`FixedCostInquiryList`・`FixedCostAnnualSummaryList`という2つのドメインモデルに重複実装され、「その他任意」かどうかの判定もusecase層に生値比較として複数箇所漏れていた。`FixedCostTargetPaymentMonth`に`appliesToMonth()`・`isOptional()`という振る舞いを持たせることで、判定ロジックの実体を値オブジェクトに一元化し、呼び出し側（`FixedCostInquiryList`、`FixedCostAnnualSummaryList`、各UseCase）はこのメソッドを呼ぶだけになっている（5.1節「自己検証・振る舞い」を持つ値オブジェクトの実践例。feature-1.03-dev1で対応）。

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

### 7.3 固定費と収支・一覧表示の連携（実装: ドメインサービスは使わない方式）

**設計の経緯**: Phase 3検討時点では、`FixedCost`集約と`IncomeAndExpenditure`集約を協調させる専用のドメインサービス（`IncomeAndExpenditureGenerationService`）を置く構想だった。実際にはこの粒度のサービスを導入するほどの協調ロジックは発生せず、各UseCaseが直接`FixedCostTableRepository`から固定費一覧を取得し、5.5節の`FixedCostTargetPaymentMonth`の判定メソッド（またはリポジトリの検索条件）で対象月分を絞り込む、という方式に落ち着いている。8.3節「柔軟にする領域」の考え方どおり、協調ロジックが単純な場合はドメインサービスを置かず各UseCaseに委ねている一例。

**代表的な利用パターン**:

1. **一覧・集計表示（in-memoryフィルタ）**: `FixedCostInquiryUseCase`（固定費一覧）・`FixedCostAnnualSummaryUseCase`（年間固定費集計）は、`FixedCostTableRepository`でユーザーの固定費を全件取得し、`FixedCostInquiryList`/`FixedCostAnnualSummaryList`（いずれもdomain/model）が内部で`item.getFixedCostTargetPaymentMonth().appliesToMonth(monthValue)`を呼んで対象月の固定費を絞り込む。

2. **収支新規登録時の初期値設定（検索条件による絞り込み）**: `IncomeAndExpenditureInitUseCase`は、対象月に該当し得る支払月コード（毎月"00"／対象月の特定コード／奇数月"20"か偶数月"30"／その他任意"40"）のリストを組み立て、`FixedCostTableRepository.findByFixedCostTargetPaymentMonthList()`の検索条件としてDB側で絞り込む。これは同じ業務ルール（どのコードが対象月に該当するか）をSQL検索条件向けに表現したものであり、`FixedCostTargetPaymentMonth.appliesToMonth()`とは別の実装になっている（将来的な統一・共通化の余地がある箇所として認識している）。

**この設計の責務分担**:
- 「対象月に支払いが発生するか」の判定ロジックの実体は`FixedCostTargetPaymentMonth`（値オブジェクト）に集約されている
- 固定費一覧の取得・絞り込みはUseCaseごとに行い、専用のドメインサービスは置かない
- `FixedCost`集約から`Expenditure`エンティティを直接生成する処理（`generateExpenditure()`相当）は存在しない。固定費の情報は、収支登録画面側のフォーム（`IncomeItemForm`/支出用フォーム）の初期値として引き継がれる形で連携している

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
**最終更新**: 2026年10月18日
