# Phase 3 Step 3: inquiry関連日付系クラスのDateValue継承移行計画書

## 1. 概要

### 1.1 目的

Phase 3 Step 2で実施したMoney/NullableMoney基底クラス継承移行の成果を踏まえ、inquiry関連の日付系クラスに対してNullableDateValue基底クラス継承を適用します。これにより、コードの重複排除、保守性の向上、API統一を実現します。

### 1.2 背景

**DateValue基底クラスの存在**:

DateValue基底クラスは既に2025年11月29日に作成されており、以下の機能を提供しています：
- 日付のバリデーション（null、形式チェック）
- 日付の比較機能（compareTo、isBefore、isAfter）
- 日付のパース機能（yyyyMMdd形式、yearMonth+day形式）
- 日付の表示形式統一（yyyyMMdd形式、yyyy/MM/dd形式）

**Phase1共通クラスの状況**:

以下のPhase1共通日付クラスは既にDateValueを継承しています：
- IncomeDate（収入日）※null非許容

**Phase 3 Step 2の成果**:

Phase 3 Step 2では以下の成果を得ました：
- Money/NullableMoney基底クラス継承による6クラスの移行完了
- 121テストケース作成
- コード重複約100行削減
- API統一とドメイン凝集度向上

### 1.3 スコープ（実施時に変更）

**実施内容**:
1. NullableDateValue基底クラスの新規作成
2. PaymentDate、ShiharaiDate、SisyutuShiharaiDateの**3クラスをPaymentDateに統合**
3. PaymentDateをNullableDateValue継承に変更（null許容化）

**変更理由**:
- DBテーブルで支払日項目は「支出テーブル：EXPENDITURE_TABLE」の「SIHARAI_DATE」のみ
- 支払日は全てnull許容のため、3つのクラスを統合することでシンプルな設計を実現

**対象外**:
- EventStartDate、EventEndDate（event関連、Phase 4以降で対応）
- ShoppingDate（shoppingregist関連、Phase 4以降で対応）

---

## 2. 現状分析

### 2.1 DateValue基底クラスの特徴

**ファイルパス**: [DateValue.java](../src/main/java/com/yonetani/webapp/accountbook/domain/type/common/DateValue.java)

**特徴**:
- **抽象基底クラス**: すべての日付系ドメインタイプの基底クラス
- **null非許容**: validate()メソッドでnull値をチェック
- **比較機能**: compareTo、isBefore、isAfter
- **パース機能**: parseDate(yyyyMMdd)、parseDate(yearMonth, day)
- **フォーマット機能**: toStringFormatyyyyMMdd()、toStringFormatyyyySPMMSPdd()

**主要メソッド**:
```java
public abstract class DateValue {
    private final LocalDate value;

    protected static void validate(LocalDate value, String typeName) {
        // null値チェック
    }

    protected static LocalDate parseDate(String dateString, String typeName) {
        // yyyyMMdd形式のパース
    }

    protected static LocalDate parseDate(String yearMonth, String day, String typeName) {
        // yearMonth + day形式のパース
    }

    public int compareTo(DateValue other) { }
    public boolean isBefore(DateValue other) { }
    public boolean isAfter(DateValue other) { }
    public String toStringFormatyyyyMMdd() { }
    public String toStringFormatyyyySPMMSPdd() { }

    @Override
    public String toString() {
        return toStringFormatyyyySPMMSPdd();
    }
}
```

### 2.2 対象クラスの特徴

#### 2.2.1 ShiharaiDate（支払日）

**ファイルパス**: [ShiharaiDate.java](../src/main/java/com/yonetani/webapp/accountbook/domain/type/account/inquiry/ShiharaiDate.java)

**特徴**:
- **null値許容**: 支払日がnullの場合も許容（特殊な設計）
- **from(LocalDate)**: LocalDateから生成（null許容）
- **from(yearMonth, day)**: 年月と日付から生成（day=nullの場合、nullを保持）
- **DomainCommonUtils依存**: toString()でDomainCommonUtils.formatyyyySPMMSPdd()を使用

**現在の実装**:
```java
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class ShiharaiDate {
    private final LocalDate value; // null許容

    public static ShiharaiDate from(LocalDate datetime) {
        return new ShiharaiDate(datetime); // null許容
    }

    public static ShiharaiDate from(String yearMonth, String day) {
        if(!StringUtils.hasLength(day)) {
            return new ShiharaiDate(null); // null許容
        }
        // yearMonthとdayをパースしてLocalDateに変換
        // LocalDate.parse()を直接使用
    }

    @Override
    public String toString() {
        return DomainCommonUtils.formatyyyySPMMSPdd(value);
    }
}
```

#### 2.2.2 SisyutuShiharaiDate（支出支払日）

**ファイルパス**: [SisyutuShiharaiDate.java](../src/main/java/com/yonetani/webapp/accountbook/domain/type/account/inquiry/SisyutuShiharaiDate.java)

**特徴**:
- **null値許容**: 支出支払日がnullの場合も許容（特殊な設計）
- **from(LocalDate)**: LocalDateから生成（null許容）
- **DomainCommonUtils依存**: toString()でDomainCommonUtils.formatyyyySPMMSPdd()を使用

**現在の実装**:
```java
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class SisyutuShiharaiDate {
    private final LocalDate value; // null許容

    public static SisyutuShiharaiDate from(LocalDate datetime) {
        return new SisyutuShiharaiDate(datetime); // null許容
    }

    @Override
    public String toString() {
        return DomainCommonUtils.formatyyyySPMMSPdd(value);
    }
}
```

### 2.3 課題

| No | 課題 | 対象クラス | 影響 |
|----|------|----------|------|
| 1 | コードの重複 | 全クラス | LocalDate操作、フォーマットロジックが重複 |
| 2 | DomainCommonUtils依存 | ShiharaiDate, SisyutuShiharaiDate | ドメインロジックが外部ユーティリティに依存 |
| 3 | null値許容とDateValueの不一致 | ShiharaiDate, SisyutuShiharaiDate | DateValueはnull非許容だが、対象クラスはnull許容 |
| 4 | パース処理の重複 | ShiharaiDate | DateValueのparseDate()メソッドと同等の処理を独自実装 |
| 5 | API不統一 | ShiharaiDate, SisyutuShiharaiDate | DateValue継承クラスと異なるメソッド名・構造 |

### 2.4 NullableDateValue基底クラスの必要性

**課題3への対応**:

ShiharaiDate、SisyutuShiharaiDateは**null値を許容**する設計ですが、DateValue基底クラスは**null非許容**です。

**Phase 3 Step 2の教訓**:

Phase 3 Step 2では、null許容金額クラスに対してNullableMoney基底クラスを新規作成し、以下の成果を得ました：
- null安全演算ロジックの基底クラス化
- Money（null非許容）とNullableMoney（null許容）の明確な使い分け
- WithdrewKingaku、SisyutuKingakuB/C/BCの統一的な設計

**提案**:

同様のアプローチとして、**NullableDateValue基底クラス**を新規作成し、null許容日付クラスの共通基底クラスとします。

---

## 3. 移行方針

### 3.1 基本方針

1. **NullableDateValue基底クラスの新規作成**:
   - null値を許容する日付クラスの共通基底クラスを作成
   - ShiharaiDate、SisyutuShiharaiDateで共通のnull安全ロジックを提供
   - Phase1共通クラス（common package）に配置

2. **DateValue基底クラスとの関係**:
   - NullableDateValueはDateValueを継承**しない**（独立した基底クラス）
   - 理由：DateValueのvalidate()がnull値を拒否するため、継承すると矛盾が生じる
   - Money/NullableMoneyと同じパターン

3. **NullableDateValue基底クラス継承可能なクラス**:
   - ShiharaiDate
   - SisyutuShiharaiDate

   → NullableDateValue継承により、null安全フォーマットロジックの重複を排除

### 3.2 移行パターン

#### パターン: NullableDateValue継承（ShiharaiDate, SisyutuShiharaiDate）

**適用条件**:
- null値許容（DateValue基底クラスと異なる）
- null安全なフォーマット処理が必要

**移行手順**:
1. NullableDateValue基底クラスを継承
2. @EqualsAndHashCode(callSuper = true)に変更
3. 重複フィールド・メソッドを削除（value、toString等）
4. from()メソッドを基底クラスのvalidate()（null許容版）に適合
5. ShiharaiDateのfrom(yearMonth, day)は独自メソッドとして維持
6. テストを追加

---

## 4. 実装計画（実施内容に更新）

### 4.1 実施した作業ステップ

#### Step 1: NullableDateValue基底クラスの作成 ✅

**対象**: NullableDateValue.java（新規作成）

**実施内容**:
1. NullableDateValue基底クラスを作成（commonパッケージ）
   - null許容のvalueフィールド
   - null安全なフォーマットメソッド（toCompactString、toDisplayString、toJapaneseDisplayString）
   - validate()メソッド（null許容）
   - compareTo、isBefore、isAfterメソッド（null値はLocalDate.MINとして扱う）
   - isNull()メソッド
   - toString()メソッド（デバッグ用、null時は空文字返却）
2. NullableDateValueTest.java作成（37テストケース）
   - 基本機能テスト
   - null値処理テスト
   - 比較機能テスト
   - フォーマット機能テスト

**実施日**: 2025-12-28

#### Step 2: PaymentDateの統合とリファクタリング ✅

**対象**: PaymentDate.java、PaymentDateTest.java、ShiharaiDate.java（削除）、SisyutuShiharaiDate.java（削除）

**実施内容**:
1. PaymentDateをNullableDateValue継承に変更
   - DateValue継承からNullableDateValue継承に変更
   - null値許容に対応
2. SisyutuShiharaiDateのmax()メソッドをPaymentDateに追加
3. ShiharaiDate、SisyutuShiharaiDateを削除
4. PaymentDateTest.java拡充（21テストケース）
   - null値生成テスト
   - max()メソッドテスト
   - compareTo、equals、hashCodeテスト

**実施日**: 2025-12-28

#### Step 3: 既存クラスの置き換えとテスト整備 ✅

**対象**:
- ExpenditureItem.java
- SisyutuKingakuItem.java
- AccountMonthInquiryExpenditureItemList.java
- DomainAssertions.java
- AccountMonthInquiryIntegrationTest.java
- AccountMonthInquiryUseCase.java

**実施内容**:
1. ドメインモデルの更新
   - ExpenditureItem.java: ShiharaiDate → PaymentDate
   - SisyutuKingakuItem.java: SisyutuShiharaiDate → PaymentDate
   - AccountMonthInquiryExpenditureItemList.java: 両方 → PaymentDate
2. テストインフラの整備
   - DomainAssertions.java: NullableDateValue用アサーションメソッド4件追加
   - AccountMonthInquiryIntegrationTest.java: 支払日検証追加（null値テスト含む）
3. バグ修正
   - AccountMonthInquiryUseCase.java: toString() → toDisplayString()（支払日表示形式修正）

**実施日**: 2025-12-28

### 4.2 実施結果

| ステップ | 対象クラス | 実施内容 | テスト結果 |
|---------|----------|---------|----------|
| Step 1 | NullableDateValue | 基底クラス作成 | 37/37テスト成功 |
| Step 2 | PaymentDate | 3クラス統合、NullableDateValue継承 | 21/21テスト成功 |
| Step 3 | 関連クラス | 置き換え、テスト整備、バグ修正 | 10/10統合テスト成功 |
| **合計** | **8クラス変更、2クラス削除、3テストクラス作成** | **完了** | **68/68テスト成功** |

---

## 5. 実施成果

### 5.1 定量的成果

| 指標 | Phase3 Step2完了時 | Phase3 Step3完了後（実績） | 改善 |
|------|------------------|------------------------|------|
| DateValue継承完了クラス数 | 2クラス（Phase1共通クラス） | 1クラス（IncomeDate） | -1クラス（PaymentDate統合） |
| NullableDateValue継承完了クラス数 | 0 | 1クラス（PaymentDate） | +1クラス（新規） |
| 基底クラス数 | 3（Money、NullableMoney、DateValue） | 4（+NullableDateValue） | +1クラス |
| API統一完了クラス数 | 11クラス | 12クラス | +1クラス |
| テストクラス数 | 13クラス | 15クラス | +2クラス |
| テストケース数 | 215 | 273 | +58 |
| 削減クラス数 | - | 2クラス削除 | ShiharaiDate、SisyutuShiharaiDate |
| 統合テスト強化 | - | 支払日検証追加 | null値テスト含む |

**新規作成クラス**:
- NullableDateValue.java（基底クラス）
- NullableDateValueTest.java（37テストケース）
- PaymentDateTest.java（21テストケース、拡充）

**削除クラス**:
- ShiharaiDate.java
- SisyutuShiharaiDate.java

**変更クラス**:
- PaymentDate.java（NullableDateValue継承、max()メソッド追加）
- ExpenditureItem.java（ShiharaiDate → PaymentDate）
- SisyutuKingakuItem.java（SisyutuShiharaiDate → PaymentDate）
- AccountMonthInquiryExpenditureItemList.java（両方 → PaymentDate）
- DomainAssertions.java（NullableDateValue用アサーション追加）
- AccountMonthInquiryIntegrationTest.java（支払日検証追加）
- AccountMonthInquiryUseCase.java（バグ修正：toString→toDisplayString）

### 5.2 定性的成果

1. **コード品質向上**: NullableDateValue継承により重複コード排除、3クラスを1クラスに統合
2. **API統一**: すべての日付系クラスで統一的なフォーマットメソッドを提供
3. **保守性向上**: 基底クラスメソッド活用によりメンテナンス箇所削減
4. **ドメイン凝集度向上**: DomainCommonUtils依存を排除
5. **テストカバレッジ向上**: 包括的なテストスイート構築（58テストケース追加）
6. **null安全性向上**: NullableDateValue導入により統一的なnull処理を実現
7. **設計一貫性**: Money/NullableMoneyと同じパターンで日付系クラスを設計
8. **設計シンプル化**: 3つの支払日クラスを1つに統合、より理解しやすい設計に
9. **バグ修正**: 支払日表示形式の不具合を発見・修正

---

## 6. リスクと対策

### 6.1 リスク

| No | リスク | 影響度 | 発生確率 | 対策 |
|----|-------|--------|---------|------|
| 1 | NullableDateValueとDateValueの2つの基底クラスによる設計複雑化 | 中 | 低 | Money/NullableMoneyと同じパターンを適用 |
| 2 | ShiharaiDateのfrom(yearMonth, day)メソッドの独自性 | 低 | 低 | 独自メソッドとして維持 |
| 3 | UseCase層での影響範囲 | 低 | 低 | toString()の挙動変更なし、影響は限定的 |
| 4 | null値の扱いに関する既存コードへの影響 | 中 | 低 | 既存のnull許容動作を維持 |

### 6.2 対策

1. **段階的移行**: ステップごとに確実に実施
2. **包括的テスト**: 各クラスで10-20テストケース作成
3. **後方互換性維持**: 既存のnull許容動作を維持
4. **統合テスト**: UseCase層のテストで影響確認
5. **基底クラス使い分けルール明確化**: DateValue（null非許容）、NullableDateValue（null許容）の使い分けを文書化

---

## 7. 次のステップ

Phase 3 Step 3完了後の展開:

1. **Phase 4**: その他のドメインクラスの基底クラス継承移行
   - event関連日付クラス（EventStartDate、EventEndDate）
   - shoppingregist関連日付クラス（ShoppingDate）
   - その他のドメインクラス

2. **Phase 5以降**: 全ドメインクラスの基底クラス継承完了
   - 全ドメインクラスのAPI統一
   - テストカバレッジ100%達成

---

## 8. 更新履歴

| 版数 | 更新日 | 更新内容 |
|------|--------|---------|
| 1.00.00 | 2025-12-25 | 初版作成 |
| 2.00.00 | 2025-12-28 | 実施完了に伴う更新。PaymentDate統合方針への変更、実施結果の記載 |

---

**作成日**: 2025-12-25
**最終更新日**: 2025-12-28
**作成者**: DDD Refactoring Project Phase 3
**ステータス**: ✅完了
