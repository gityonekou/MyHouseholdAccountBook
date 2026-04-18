# DDD設計：金額クラスの統合判断基準

## 概要

このドキュメントでは、「積立金取崩金額(WithdrewKingaku)」と「積立金取崩金額合計(WithdrewKingakuTotalAmount)」を統合すべきでない理由について、DDD（ドメイン駆動設計）の観点から説明します。

---

## 1. 質問の背景

### 疑問点
> 「積立金取崩金額」のクラスWithdrewKingakuと、「積立金取崩金額合計」のクラスWithdrewKingakuTotalAmountですが、積立金取崩金額を加算したものが積立金取崩金額合計となるので、そもそも責務は同じとなるので、積立金取崩金額合計のクラスは積立金取崩金額クラスに統合するのが正しいＤＤＤのドメイン設計でしょうか？

### 一見すると
- 両者は「金額」という同じデータ型
- 合計は個別金額を加算したもの
- **責務が同じに見える**

---

## 2. 結論：統合すべきではない

**WithdrewKingaku** と **WithdrewKingakuTotalAmount** は、**責務が異なる**ため、別クラスとして維持すべきです。

---

## 3. 統合すべきでない理由

### 3.1 コンテキストの違い

| 項目 | WithdrewKingaku<br>(積立金取崩金額) | WithdrewKingakuTotalAmount<br>(積立金取崩金額合計) |
|------|-------------------------------------|---------------------------------------------------|
| **表現する概念** | 個別の取崩 | 月次全体の取崩合計 |
| **スコープ** | 特定の積立項目からの取崩 | 全積立項目の合計 |
| **ドメインレベル** | 明細レベル | サマリーレベル |
| **ビジネス上の意味** | 「この積立から取り崩した金額」 | 「今月取り崩した総額」 |

**DDDの原則**: 異なるコンテキストは異なるクラスで表現すべき

---

### 3.2 値の性質の違い

```
WithdrewKingaku: 単一項目の金額（基本値）
   ↓ 集計操作
WithdrewKingakuTotalAmount: 複数項目を集計した金額（導出値）
```

#### 基本値（Base Value）
- データベースに直接格納される値
- ドメインイベントの結果として生成される値
- 例：個別の取崩金額

#### 導出値（Derived Value）
- 他の値から計算される値
- 集計・計算処理の結果
- 例：取崩金額の合計

**DDDの原則**: 基本値と導出値は、異なるビジネス概念を表現する

---

### 3.3 利用場所の違い

#### WithdrewKingaku の利用場所
```java
// 積立金取崩明細（個別の取崩データ）
public class 積立金取崩明細 {
    private 積立項目コード sisanKoumokuCode;
    private WithdrewKingaku withdrawKingaku;  // ← 個別金額
    // ...
}
```

#### WithdrewKingakuTotalAmount の利用場所
```java
// 月次収支サマリー（集計データ）
public class 月次収支サマリー {
    private TargetYearMonth targetYearMonth;
    private WithdrewKingakuTotalAmount totalWithdrew;  // ← 合計金額
    // ...
}
```

**DDDの原則**: 異なる集約（Aggregate）で使用される値オブジェクトは分離すべき

---

### 3.4 型安全性の向上

別クラスにすることで、コンパイル時の型チェックが可能になります。

#### ❌ 統合した場合（型による区別ができない）
```java
public void updateSummary(WithdrewKingaku amount) {
    // これは個別金額？それとも合計金額？
    // コンパイラは区別できない
}
```

#### ✅ 分離した場合（型で明確に区別）
```java
// 個別金額を受け取るメソッド
public void addWithdrawal(WithdrewKingaku amount) {
    // 明確に個別金額
}

// 合計金額を受け取るメソッド
public void updateTotal(WithdrewKingakuTotalAmount totalAmount) {
    // 明確に合計金額
    // 間違って個別金額を渡すとコンパイルエラー
}
```

**DDDの原則**: 型による意図の表明（Intention-Revealing Interfaces）

---

## 4. DDD の Value Object 設計原則

Eric Evans の「ドメイン駆動設計」より:

> "Value Objects should express a **conceptual whole**"
> （値オブジェクトは**概念的な全体**を表現すべき）

### 適用例

- **WithdrewKingaku**: 「個別の取崩金額」という概念
- **WithdrewKingakuTotalAmount**: 「月次の取崩合計金額」という概念

これらは**異なる概念的な全体**を表現しているため、別クラスが適切です。

---

## 5. 類似ケースとの比較

### 5.1 Phase 3 で統合したケース

#### EventDate への統合
```
EventStartDate  ┐
                ├─→ EventDate（統合）
EventEndDate    ┘
```

**統合理由**:
- 全て「イベントの日付」という**同じ概念**
- 違いは「開始/終了」という**コンテキストのみ**
- フィールド名で区別可能（`eventStartDate`, `eventEndDate`）
- ビジネスロジック上の役割が同じ

---

### 5.2 統合しないケース（本件）

#### WithdrewKingaku と WithdrewKingakuTotalAmount
```
WithdrewKingaku          （個別金額）
                         ↓ 集計
WithdrewKingakuTotalAmount（合計金額）
```

**分離理由**:
- 「個別金額」と「合計金額」は**異なる概念**
- 集約の違い（明細集約 vs サマリー集約）
- ビジネスロジック上の役割が異なる
- 値の性質が異なる（基本値 vs 導出値）

---

## 6. 統合判断のチェックリスト

Value Object を統合すべきか判断する際のチェックリスト:

| チェック項目 | 統合OK | 統合NG |
|-------------|--------|--------|
| **同じビジネス概念か？** | ✓ イベント日 | ✗ 個別金額 vs 合計金額 |
| **同じ集約で使用されるか？** | ✓ 同じ | ✗ 明細 vs サマリー |
| **値の性質は同じか？** | ✓ 基本値 | ✗ 基本値 vs 導出値 |
| **コンテキストのみの違いか？** | ✓ 開始/終了 | ✗ スコープが異なる |
| **フィールド名で区別可能か？** | ✓ 可能 | △ 可能だが不適切 |

### 判断基準

- **5項目中4項目以上が「統合OK」** → 統合を検討
- **2項目以上が「統合NG」** → 分離を維持

本件の場合、**4項目が「統合NG」**のため、分離が適切です。

---

## 7. Money 基底クラスによる共通化

両クラスとも `Money` を継承することで、以下を実現:

### 共通化されるもの
- 金額操作（加算、減算、比較など）
- バリデーションロジック
- フォーマット処理

### 分離されるもの（型による区別）
- クラス名による意図の明確化
- メソッドシグネチャでの型チェック
- ドメイン概念の表現

```java
// Money 基底クラス（共通ロジック）
public abstract class Money {
    protected final BigDecimal value;

    public Money add(Money other) { /* ... */ }
    public Money subtract(Money other) { /* ... */ }
    // 共通メソッド
}

// 個別金額（明細レベル）
public class WithdrewKingaku extends Money {
    public static WithdrewKingaku from(BigDecimal value) { /* ... */ }
}

// 合計金額（サマリーレベル）
public class WithdrewKingakuTotalAmount extends Money {
    public static WithdrewKingakuTotalAmount from(BigDecimal value) { /* ... */ }
}
```

**利点**:
- コードの重複を避ける（DRY原則）
- 型による意図の表明（Intention-Revealing Interfaces）
- ドメイン概念の明確化

---

## 8. 実装上のメリット

### 8.1 コードの可読性

#### ✅ 分離した場合
```java
public class MonthlyReport {
    // 何を表すか一目瞭然
    private WithdrewKingakuTotalAmount totalWithdrew;

    public void updateTotal(List<WithdrawDetail> details) {
        BigDecimal sum = details.stream()
            .map(d -> d.getWithdrewKingaku().getValue())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 合計金額であることが型で明確
        this.totalWithdrew = WithdrewKingakuTotalAmount.from(sum);
    }
}
```

#### ❌ 統合した場合
```java
public class MonthlyReport {
    // これは個別？合計？
    private WithdrewKingaku totalWithdrew;  // ← 混乱を招く
}
```

---

### 8.2 将来の拡張性

合計金額に独自のバリデーションが必要になる可能性:

```java
public class WithdrewKingakuTotalAmount extends Money {
    public static WithdrewKingakuTotalAmount from(BigDecimal value) {
        validate(value, "積立金取崩金額合計");

        // 合計金額特有のバリデーション
        if (value.compareTo(MAX_MONTHLY_WITHDRAWAL) > 0) {
            throw new DomainException("月次取崩限度額を超えています");
        }

        return new WithdrewKingakuTotalAmount(value);
    }
}
```

---

## 9. 統合すべきケース：IncomingAmount → SyuunyuuKingakuTotalAmount

### 9.1 統合前の状況

#### IncomingAmount（入り金額）
```java
/**
 * 「入り金額」項目の値を表すドメインタイプです。
 * 収支のうち、入ってきたお金の合計値「収入金額と積立金取崩金額の値の合算値」となります。
 */
public class IncomingAmount {
    private final BigDecimal value;

    // 収入金額 + 積立金取崩金額から生成
    public static IncomingAmount from(SyuunyuuKingaku syuunyuuKingaku,
                                       WithdrewKingaku withdrewKingaku) {
        return new IncomingAmount(
            syuunyuuKingaku.getValue().add(withdrewKingaku.getNullSafeValue())
        );
    }

    // SyuunyuuKingakuTotalAmountへの変換メソッド
    public SyuunyuuKingakuTotalAmount getSyuunyuuKingakuTotalAmount() {
        return SyuunyuuKingakuTotalAmount.from(value);
    }
}
```

#### SyuunyuuKingakuTotalAmount（収入金額合計）
```java
/**
 * 「収入金額合計」項目の値を表すドメインタイプです。
 */
public class SyuunyuuKingakuTotalAmount {
    private final BigDecimal value;

    // BigDecimalから直接生成
    public static SyuunyuuKingakuTotalAmount from(BigDecimal totalAmount) {
        // バリデーション
        return new SyuunyuuKingakuTotalAmount(totalAmount);
    }

    // 収入金額を加算
    public SyuunyuuKingakuTotalAmount add(SyuunyuuKingaku addValue) {
        return new SyuunyuuKingakuTotalAmount(this.value.add(addValue.getValue()));
    }
}
```

#### 使用フロー
```
SyuunyuuKingaku + WithdrewKingaku
        ↓
  IncomingAmount（中間オブジェクト）
        ↓ getSyuunyuuKingakuTotalAmount()
SyuunyuuKingakuTotalAmount（最終的な値）
```

---

### 9.2 統合すべきと判断した理由

| チェック項目 | 判断 | 理由 |
|-------------|------|------|
| **同じビジネス概念か？** | ✓ 統合OK | 両者とも「収入の合計金額」を表現 |
| **中間オブジェクトか？** | ✓ 統合OK | IncomingAmountは変換が唯一の目的 |
| **責務の重複があるか？** | ✓ 統合OK | 計算ロジックが同じ（収入 + 取崩） |
| **不要な変換ステップか？** | ✓ 統合OK | IncomingAmount → SyuunyuuKingakuTotalAmount が無駄 |
| **コードの簡潔性向上？** | ✓ 統合OK | 中間オブジェクト排除で処理が直接的に |

**結論**: IncomingAmountは「中間変換オブジェクト」であり、SyuunyuuKingakuTotalAmountと同じビジネス概念を表現しているため、統合すべき。

---

### 9.3 統合後の設計

#### SyuunyuuKingakuTotalAmount（統合後）
```java
/**
 * 「収入金額合計」項目の値を表すドメインタイプです。
 * 「収入金額」項目と「積立金取崩金額」項目の合算値が「収入金額合計」項目の値となります。
 */
@EqualsAndHashCode(callSuper = true)
public class SyuunyuuKingakuTotalAmount extends Money {

    public static final SyuunyuuKingakuTotalAmount ZERO =
        SyuunyuuKingakuTotalAmount.from(Money.MONEY_ZERO);

    // 方法1: BigDecimalから直接生成（既存）
    public static SyuunyuuKingakuTotalAmount from(BigDecimal totalAmount) {
        Money.validate(totalAmount, "収入金額合計");
        if(BigDecimal.ZERO.compareTo(totalAmount) > 0) {
            throw new MyHouseholdAccountBookRuntimeException(
                "「収入金額合計」項目の設定値がマイナスです。");
        }
        return new SyuunyuuKingakuTotalAmount(totalAmount);
    }

    // 方法2: 収入金額と積立金取崩金額から生成（IncomingAmountの機能を統合）
    public static SyuunyuuKingakuTotalAmount from(IncomeAmount income,
                                                    WithdrewKingaku withdrew) {
        if(income == null) {
            throw new MyHouseholdAccountBookRuntimeException(
                "収入金額の設定値がnullです。");
        }
        if(withdrew == null) {
            throw new MyHouseholdAccountBookRuntimeException(
                "積立金取崩金額の設定値がnullです。");
        }

        // 収入金額と積立金取崩金額を加算（IncomingAmountと同じロジック）
        BigDecimal totalAmount = income.getValue().add(withdrew.getNullSafeValue());
        return new SyuunyuuKingakuTotalAmount(totalAmount);
    }

    // IncomeAmountを加算
    public SyuunyuuKingakuTotalAmount add(IncomeAmount addValue) {
        return SyuunyuuKingakuTotalAmount.from(super.add(addValue));
    }
}
```

#### 使用フロー（統合後）
```
SyuunyuuKingaku + WithdrewKingaku
        ↓ from(income, withdrew)
SyuunyuuKingakuTotalAmount（直接生成）
```

---

### 9.4 統合による改善効果

#### ✅ 改善点

1. **中間オブジェクトの排除**
   - IncomingAmountクラスが不要に
   - クラス数削減: 2クラス → 1クラス

2. **不要な変換ステップの削減**
   ```java
   // 統合前（2ステップ）
   IncomingAmount incoming = IncomingAmount.from(income, withdrew);
   SyuunyuuKingakuTotalAmount total = incoming.getSyuunyuuKingakuTotalAmount();

   // 統合後（1ステップ）
   SyuunyuuKingakuTotalAmount total =
       SyuunyuuKingakuTotalAmount.from(income, withdrew);
   ```

3. **責務の明確化**
   - IncomingAmountの唯一の責務は「SyuunyuuKingakuTotalAmountへの変換」
   - 変換だけのクラスは不要

4. **Money基底クラスの活用**
   - 統合後のSyuunyuuKingakuTotalAmountはMoneyを継承
   - 金額操作の共通メソッドが利用可能

#### ⚠️ WithdrewKingaku/WithdrewKingakuTotalAmountとの違い

| 観点 | IncomingAmount<br>→ SyuunyuuKingakuTotalAmount | WithdrewKingaku<br>vs WithdrewKingakuTotalAmount |
|------|----------------------------------------------|------------------------------------------------|
| **ビジネス概念** | 同じ（収入の合計） | 異なる（個別 vs 合計） |
| **値の性質** | 同じ（導出値） | 異なる（基本値 vs 導出値） |
| **利用場所** | 同じ（サマリー集約） | 異なる（明細 vs サマリー） |
| **クラスの役割** | 中間変換のみ | それぞれ独自の役割 |
| **判断** | ✅ 統合すべき | ❌ 統合すべきでない |

---

### 9.5 統合判断のポイント

#### 中間オブジェクトパターンのチェックリスト

以下の条件を**すべて満たす場合**、中間オブジェクトは統合すべき:

1. ✅ **変換が唯一の目的**: 他のクラスへの変換メソッドのみを持つ
2. ✅ **同じビジネス概念**: 変換先クラスと同じドメイン概念を表現
3. ✅ **独自のビジネスロジックがない**: 単純な値の受け渡しのみ
4. ✅ **他の場所で使用されない**: 変換以外の用途がない

**IncomingAmountの場合**:
- ✅ `getSyuunyuuKingakuTotalAmount()` のみが実質的なメソッド
- ✅ SyuunyuuKingakuTotalAmountと同じ「収入合計」を表現
- ✅ 加算ロジックのみで独自ロジックなし
- ✅ 変換以外の用途で使用されていない

→ **4つすべて満たすため、統合が適切**

---

### 9.6 リファクタリング手順

1. **SyuunyuuKingakuTotalAmountにfromメソッド追加**
   ```java
   public static SyuunyuuKingakuTotalAmount from(IncomeAmount income,
                                                   WithdrewKingaku withdrew)
   ```

2. **IncomingAmount使用箇所を置き換え**
   ```java
   // 変更前
   IncomingAmount incoming = IncomingAmount.from(income, withdrew);
   SyuunyuuKingakuTotalAmount total = incoming.getSyuunyuuKingakuTotalAmount();

   // 変更後
   SyuunyuuKingakuTotalAmount total =
       SyuunyuuKingakuTotalAmount.from(income, withdrew);
   ```

3. **全テスト実行で動作確認**

4. **IncomingAmountクラスを削除**

---

## 10. まとめ

### 結論

#### 統合すべきでないケース
**WithdrewKingaku と WithdrewKingakuTotalAmount は統合すべきではない**

- **異なるビジネス概念**を表現（個別 vs 合計）
- **異なるコンテキスト**で使用（明細 vs サマリー）
- **異なる値の性質**（基本値 vs 導出値）
- **型安全性**の向上（コンパイル時チェック）
- **将来の拡張性**（独自バリデーション追加の可能性）

推奨: **2クラス分離を維持**

---

#### 統合すべきケース
**IncomingAmount → SyuunyuuKingakuTotalAmount は統合すべき**

- **同じビジネス概念**を表現（収入の合計）
- **中間変換オブジェクト**であり独自の責務なし
- **不要な変換ステップ**の排除
- **コードの簡潔性**向上
- **Money基底クラス**の活用

推奨: **SyuunyuuKingakuTotalAmountに統合**

---

### DDDの観点
- ✅ ドメイン概念の明確な表現
- ✅ Intention-Revealing Interfaces（意図を明らかにするインターフェース）
- ✅ Ubiquitous Language（ユビキタス言語）の適用
- ✅ Money 基底クラスによる共通化と型による区別の両立
- ✅ 中間オブジェクトの排除（必要な場合のみ存在させる）

### 統合判断の原則

1. **ビジネス概念が同じ** → 統合を検討
2. **ビジネス概念が異なる** → 分離を維持
3. **中間変換のみが目的** → 統合すべき
4. **独自のビジネスロジックがある** → 分離を維持
5. **型安全性が重要** → 分離を維持

---

---

## 10. ロジック重複の許容：TotalWasteExpenditure vs WasteExpenditureTotalAmount

### 10.1 状況

Phase 4完了後、**月次の無駄遣い合計（TotalWasteExpenditure）**と**年次の無駄遣い合計（WasteExpenditureTotalAmount）**という2つのクラスが存在し、両者は**全く同じビジネスロジック**を持っています。

**重複しているロジック**:
- `from(MinorWasteExpenditure, SevereWasteExpenditure)` - 生成ロジック
- `add()` - 加算ロジック
- `getPercentage()` - 支出に対する割合計算
- `getMinorWasteExpenditurePercentage()` - 軽度の割合計算

### 10.2 DRY原則との矛盾

一見すると、**DRY（Don't Repeat Yourself）原則に違反**しているように見えます：
- 同じロジックが2箇所に存在
- 変更時に両方を修正する必要がある
- 保守コストの増加

### 10.3 なぜロジック重複を許容するか

#### 理由1: 異なる境界づけられたコンテキスト（Bounded Context）

DDDの重要な概念「境界づけられたコンテキスト」に基づくと、**月次コンテキスト**と**年次コンテキスト**は異なる境界を持ちます：

| 項目 | 月次コンテキスト | 年次コンテキスト |
|------|-----------------|----------------|
| **スコープ** | 特定月の収支 | 年間の収支集計 |
| **使用場面** | 月次収支登録・照会 | 年間収支照会・レポート |
| **データソース** | 月次収支テーブル | 月次データの集計 |
| **ビジネス意味** | 「今月の無駄遣い」 | 「今年の無駄遣い推移」 |

**DDDの原則**: 異なるコンテキスト間では、同じビジネスロジックでも別実装とすることで、**コンテキストの独立性**を保つことができる。

#### 理由2: 将来の独立した変更への対応

月次と年次で、将来的に異なる要件が発生する可能性があります：

**月次固有の要件例**:
- 予算に対する無駄遣いの割合計算
- 前月との比較ロジック
- 月次特有のバリデーション

**年次固有の要件例**:
- 四半期ごとの集計
- 年間推移グラフ用の計算
- 税務申告用の特殊な集計ロジック

これらの変更が発生した際、**クラスが分離されていることで、影響範囲が限定**されます。

#### 理由3: 既存パターンとの一貫性

他の月次vs年次クラスも同様のパターンです：

| 月次クラス | 年次合計クラス | 継承関係 |
|-----------|--------------|---------|
| `IncomeAmount` | `IncomeTotalAmount` | なし（独立） |
| `ExpenditureAmount` | `ExpenditureTotalAmount` | なし（独立） |
| `TotalWasteExpenditure` | `WasteExpenditureTotalAmount` | なし（独立） |

**アーキテクチャの一貫性**を維持するため、同じパターンを踏襲します。

### 10.4 ロジック重複のリスク軽減策

重複を許容する代わりに、以下の対策を実施します：

#### 対策1: テストによる整合性保証

`WasteExpenditureTotalAmountTest`に整合性検証テストを追加：

```java
@Test
@DisplayName("整合性検証：TotalWasteExpenditureと計算結果が一致すること")
void testConsistencyWithTotalWasteExpenditure() {
    // 準備
    MinorWasteExpenditure minor = MinorWasteExpenditure.from(new BigDecimal("3000.00"));
    SevereWasteExpenditure severe = SevereWasteExpenditure.from(new BigDecimal("2000.00"));

    TotalWasteExpenditure monthly = TotalWasteExpenditure.from(minor, severe);
    WasteExpenditureTotalAmount yearly = WasteExpenditureTotalAmount.from(minor, severe);

    // 検証：同じ入力で同じ結果を返すこと
    assertEquals(monthly.getValue(), yearly.getValue(), "合計値が一致すること");
    assertEquals(monthly.getMinorWasteExpenditurePercentage(),
                 yearly.getMinorWasteExpenditurePercentage(), "軽度の割合が一致すること");

    ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("50000.00"));
    assertEquals(monthly.getPercentage(expenditure),
                 yearly.getPercentage(expenditure), "支出に対する割合が一致すること");
}
```

**効果**: ビジネスロジックの変更時に、両クラスで計算結果が一致することを自動検証できる。

#### 対策2: ドキュメント化

このドキュメントに設計判断の理由を明記し、**なぜロジック重複を許容したか**を将来の開発者に伝える。

#### 対策3: レビュープロセス

片方のクラスのビジネスロジックを変更する際は、もう片方への影響を必ずレビューする。

### 10.5 結論

**TotalWasteExpenditure**と**WasteExpenditureTotalAmount**のロジック重複は、**DDD的に許容可能**です。

**判断基準**:
- ✅ 異なる境界づけられたコンテキスト
- ✅ 将来の独立した変更の可能性
- ✅ アーキテクチャの一貫性
- ✅ テストによる整合性保証

**DRY原則との関係**:
- DRY原則は重要だが、**絶対的なルールではない**
- コンテキスト分離による**保守性向上**の方が優先される場合がある
- **実用主義的なバランス**が重要

---

## 参考文献

- Eric Evans『ドメイン駆動設計』（Domain-Driven Design: Tackling Complexity in the Heart of Software）
- Vaughn Vernon『実践ドメイン駆動設計』（Implementing Domain-Driven Design）
- Martin Fowler『リファクタリング』（Refactoring: Improving the Design of Existing Code）

---

**文書作成日**: 2025年12月30日
**最終更新日**: 2026年1月4日
**対象プロジェクト**: MyHouseholdAccountBook（家計簿アプリ）
**Phase**: Phase 4 完了後の設計レビュー
