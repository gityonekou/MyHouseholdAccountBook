# Phase 2: 月次収支照会機能 既存コード分析レポート

**作成日**: 2025年11月30日
**対象機能**: 月次収支照会（AccountMonthInquiry）

---

## 1. 概要

月次収支照会機能は、指定月の収支情報（収入・支出・残高）を照会する機能です。

### 1.1 主な機能

1. **現在の決算月の収支取得**
2. **指定月の収支取得**
3. **収支データの整合性検証**
4. **支出項目一覧の表示**

---

## 2. 既存アーキテクチャ

### 2.1 レイヤー構成

```
┌─────────────────────────────────────┐
│  Presentation Layer                 │
│  - AccountMonthInquiryController    │
│  - AccountMonthInquiryResponse      │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  Application Layer                  │
│  - AccountMonthInquiryUseCase       │ ← 268行（リファクタリング対象）
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  Domain Layer                       │
│  - IncomeAndExpenditureItem         │
│  - AccountMonthInquiryExpenditure   │
│    ItemList                         │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  Infrastructure Layer               │
│  - IncomeAndExpenditureTable        │
│    Repository                       │
│  - SisyutuKingakuTableRepository    │
│  - IncomeTableRepository            │
│  - ExpenditureTableRepository       │
└─────────────────────────────────────┘
```

---

## 3. 問題点の分析

### 3.1 ユースケースクラスの肥大化

**AccountMonthInquiryUseCase.java: 268行**

#### 主な責務の混在

1. **データ取得**: リポジトリからのデータ取得
2. **業務ロジック**: 整合性チェック（213-227行）
3. **データ変換**: ドメインモデル→レスポンス変換（245-267行）
4. **エラーハンドリング**: 例外スロー（204-206行, 217-219行, 225-227行）

#### 具体的な問題コード

**整合性チェックロジック（ユースケース層に存在）:**
```java
// 行213-219: 収入金額の整合性チェック
SyuunyuuKingakuTotalAmount incomeKingakuTotalAmount = incomeRepository.sumIncomeKingaku(inquiryModel);
IncomingAmount chkIncomeAmount = IncomingAmount.from(sisyutuResult.getSyuunyuuKingaku(), sisyutuResult.getWithdrewKingaku());
if(!chkIncomeAmount.getSyuunyuuKingakuTotalAmount().equals(incomeKingakuTotalAmount)) {
    throw new MyHouseholdAccountBookRuntimeException("該当月の収入情報が一致しません。管理者に問い合わせてください。[yearMonth=" + inquiryModel.getYearMonth() + "]");
}

// 行221-227: 支出金額の整合性チェック
SisyutuKingakuTotalAmount expenditureKingakuTotalAmount = expenditureRepository.sumExpenditureKingaku(inquiryModel);
SisyutuKingakuTotalAmount chkExpenditureKingaku = SisyutuKingakuTotalAmount.from(sisyutuResult.getSisyutuKingaku().getValue());
if(!chkExpenditureKingaku.equals(expenditureKingakuTotalAmount)) {
    throw new MyHouseholdAccountBookRuntimeException("該当月の支出情報が一致しません。管理者に問い合わせてください。[yearMonth=" + inquiryModel.getYearMonth() + "]");
}
```

**👉 問題点**: この整合性チェックは**ドメインロジック**であり、ドメイン層に配置すべき

---

### 3.2 ドメインモデルの貧弱化

**IncomeAndExpenditureItem.java**

現状のドメインモデル:
```java
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class IncomeAndExpenditureItem {
    private final UserId userId;
    private final TargetYear targetYear;
    private final TargetMonth targetMonth;
    private final SyuunyuuKingaku syuunyuuKingaku;      // 収入金額
    private final WithdrewKingaku withdrewKingaku;      // 積立金取崩金額
    private final SisyutuYoteiKingaku sisyutuYoteiKingaku; // 支出予定金額
    private final SisyutuKingaku sisyutuKingaku;        // 支出金額
    private final SyuusiKingaku syuusiKingaku;          // 収支金額

    public static IncomeAndExpenditureItem from(...) { ... }

    public boolean isEmpty() {
        return syuunyuuKingaku == null;
    }
}
```

**👉 問題点**:
- データホルダーとしてのみ機能
- ビジネスロジック（整合性検証）がない
- 不変条件を保護していない

---

### 3.3 業務ルールの発見

コード分析から以下の業務ルールを抽出:

#### 1. 収支の整合性ルール

**ルール1**: 収入金額の整合性
```
収支テーブルの収入金額 + 積立金取崩金額 = 収入テーブルの収入金額合計
```

**ルール2**: 支出金額の整合性
```
収支テーブルの支出金額 = 支出テーブルの支出金額合計
```

**ルール3**: 収支金額の計算
```
収支金額 = 収入金額 + 積立金取崩金額 - 支出金額
```
※支出予定金額は、月次収支の新規登録時にアプリ側で計算され、以降は変更されない参照専用の値

**ルール3補足**: 支出予定金額について
- 支出予定金額は月次収支の新規登録時に自動計算される
- 登録後は変更されない（履歴・参照用データ）
- 収支金額の計算には含まれない

#### 2. データ存在チェックルール

**ルール4**: 収支データと支出データの整合性
```
支出データが存在する場合、収支データも必ず存在する
```

---

## 4. リファクタリング対象の特定

### 4.1 ユースケース層から移動すべきロジック

| No | 現在の場所 | 行番号 | ロジック内容 | 移動先 |
|----|----------|--------|------------|--------|
| 1 | AccountMonthInquiryUseCase | 213-219 | 収入金額の整合性チェック | ドメインサービス or 集約 |
| 2 | AccountMonthInquiryUseCase | 221-227 | 支出金額の整合性チェック | ドメインサービス or 集約 |
| 3 | AccountMonthInquiryUseCase | 202-206 | 収支データ存在チェック | ドメインサービス |

### 4.2 ドメイン層に追加すべき要素

#### 集約の設計案

**IncomeAndExpenditure（収支）集約**
- 集約ルート: IncomeAndExpenditure
- エンティティ: Income（収入明細）, Expenditure（支出明細）
- 値オブジェクト: TotalIncomeAmount, TotalExpenditureAmount, BalanceAmount

#### ドメインサービスの設計案

**IncomeAndExpenditureConsistencyService**
- 責務: 収支の整合性検証
- メソッド:
  - `validateIncomeConsistency()`: 収入金額の整合性チェック
  - `validateExpenditureConsistency()`: 支出金額の整合性チェック
  - `validateBalanceConsistency()`: 収支金額の整合性チェック

---

## 5. テストカバレッジの現状

### 5.1 既存テストの状況

```bash
# 検索結果
find src/test -name "*AccountMonthInquiry*Test.java"
```

**結果**: テストクラスが存在しない ❌

**👉 Phase 2 Step 1で作成が必要**

---

## 6. リファクタリング後の理想形

### 6.1 ユースケースの簡素化イメージ

**リファクタリング前: 268行**
```java
public class AccountMonthInquiryUseCase {
    public AccountMonthInquiryResponse execRead(...) {
        // 200行以上のロジック
        // - データ取得
        // - 整合性チェック
        // - 計算
        // - 変換
    }
}
```

**リファクタリング後: 約50行（目標）**
```java
public class AccountMonthInquiryUseCase {

    private final IncomeAndExpenditureRepository repository;
    private final IncomeAndExpenditureConsistencyService consistencyService;

    public AccountMonthInquiryResponse read(UserId userId, TargetYearMonth yearMonth) {
        // 1. ドメインオブジェクトを取得
        IncomeAndExpenditure incomeAndExpenditure =
            repository.findByUserIdAndYearMonth(userId, yearMonth)
                .orElseThrow(() -> new IncomeAndExpenditureNotFoundException());

        // 2. ドメインサービスで整合性検証
        consistencyService.validateConsistency(incomeAndExpenditure);

        // 3. レスポンスに変換
        return toResponse(incomeAndExpenditure);
    }

    private AccountMonthInquiryResponse toResponse(IncomeAndExpenditure domain) {
        // 変換処理のみ（30行程度）
    }
}
```

**削減行数**: 約200行 → 約50行（75%削減）

---

## 7. 次のステップ（Phase 2 Step 1）

### 7.1 既存機能のテスト作成

**優先順位1: 統合テスト**
```java
@SpringBootTest
@Sql("/test-data/account-month-inquiry.sql")
class AccountMonthInquiryIntegrationTest {

    @Test
    void 月次収支照会_正常系_データ存在() {
        // Given: テストデータ準備
        // When: 照会実行
        // Then: 期待する結果が返る
    }

    @Test
    void 月次収支照会_正常系_データなし() {
        // データがない場合のテスト
    }

    @Test
    void 月次収支照会_異常系_収入不整合() {
        // 収入金額が一致しない場合
    }

    @Test
    void 月次収支照会_異常系_支出不整合() {
        // 支出金額が一致しない場合
    }
}
```

**優先順位2: ユースケーステスト**
```java
class AccountMonthInquiryUseCaseTest {
    // 現在のロジックをテスト
    // リファクタリング前後で動作が変わらないことを保証
}
```

### 7.2 テスト作成の所要時間見積もり

- 統合テスト作成: 2-3日
- ユースケーステスト作成: 1-2日
- **合計: 3-5日**

---

## 8. まとめ

### 8.1 現状の問題点

1. ✅ ユースケース層の肥大化（268行）
2. ✅ ドメインロジックがユースケース層に存在
3. ✅ ドメインモデルが貧弱（データホルダー化）
4. ✅ テストが存在しない

### 8.2 リファクタリングの効果（期待値）

| 指標 | 現状 | 目標 | 改善率 |
|------|------|------|--------|
| ユースケース行数 | 268行 | 50行 | 75%削減 |
| ドメイン層の業務ロジック | 0% | 80% | - |
| テストカバレッジ | 0% | 80% | - |
| 保守性 | 低 | 高 | - |

### 8.3 次回アクション

**Phase 2 Step 1: 既存機能のテスト作成**
1. テストデータ準備用SQLファイル作成
2. 統合テスト作成
3. ユースケーステスト作成
4. 全テストグリーン確認

---

**作成者**: Claude Code
**レビュー**: 要確認
