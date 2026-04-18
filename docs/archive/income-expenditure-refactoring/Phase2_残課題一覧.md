# Phase2 残課題一覧

## ドキュメント情報
- **作成日**: 2025-12-09
- **Phase**: Phase2 DDDリファクタリング完了後
- **目的**: Phase2で発見された課題・改善点を整理し、Phase3以降の計画に反映する

---

## 1. アーキテクチャ上の課題

### 1-0. ユースケース層の行数削減目標未達の原因分析と評価 ✅ 完了（2025-12-14）

**優先度**: 🔴 高 → ✅ 完了

**概要**:
- Phase2の目標: UseCase層の行数削減
- 当初の評価: 目標未達（268行 → 246行、8%削減のみ）
- 分析結果: **目標達成**（目標はexecReadメソッド約50行で、実績47行）

**分析結果**:
1. **目標の正しい解釈**
   - ❌ 誤った解釈: クラス全体で約50行（非現実的）
   - ✅ 正しい解釈: execReadメソッドで約50行

2. **実績**
   - execReadメソッド: 67行 → 47行（-29.9%）✅ 目標達成
   - クラス全体: 268行 → 246行（-8.2%）
   - ビジネスロジック25行をドメイン層に移行

3. **リファクタリング成果の評価**
   - ビジネスロジックのドメイン層への移行: ✅ 完了
   - if文によるビジネス判定の削除: ✅ 完了（100%削減）
   - オーケストレーションへの専念: ✅ 達成
   - DDD原則への準拠: ✅ 達成

**結論**: Phase2のリファクタリングは**成功**

**成果物**:
- [Phase2_UseCase層行数削減分析報告書.md](Phase2_UseCase層行数削減分析報告書.md) - 詳細分析結果
- [Phase2_完了報告.md](Phase2_完了報告.md) - 評価を「目標達成」に更新済み

**完了日**: 2025-12-14

---

### 1-1. Money基底クラス継承への移行

**優先度**: 🔴 高

**概要**:
- Phase1で`Money`抽象基底クラスと`IncomeAmount`が作成済み
- Phase2では以下の金額系ドメインタイプが独立して実装されている
  - `SyuunyuuKingaku`（収入金額）
  - `WithdrewKingaku`（積立金取崩金額）
  - `SyuunyuuKingakuTotalAmount`（収入金額合計）
  - `SisyutuKingaku`（支出金額）
  - `SisyutuKingakuTotalAmount`（支出金額合計）
- 金額型の実装パターンが2つ混在している状態

**問題点**:
1. コードの重複
   - バリデーションロジック（null、スケール、マイナス値チェック）が各クラスで重複
   - ZERO定数の実装が各クラスで重複
2. 一貫性の欠如
   - 金額演算のAPIが統一されていない
   - equals/hashCode/compareToの実装パターンがバラバラ
3. 保守性の低下
   - 共通ロジックの修正時に複数箇所を変更する必要がある

**対応方針**:
1. すべての金額系ドメインタイプを`Money`継承に統一
2. 共通メソッドの活用
   - `protected BigDecimal add(Money other)` - 基底クラスで実装済み
   - `protected BigDecimal subtract(Money other)` - 基底クラスで実装済み
   - `public int compareTo(Money other)` - 基底クラスで実装済み
   - `public boolean isZero()` - 基底クラスで実装済み
   - `public boolean isPositive()` - 基底クラスで実装済み
   - `public boolean isNegative()` - 基底クラスで実装済み
3. バリデーションの統一
   - `protected static void validate(BigDecimal value, String typeName)` - 基底クラスで実装済み

**Phase3での修正影響**:
- Phase2で実施した値オブジェクト指向のリファクタリングは95%そのまま利用可能
- 修正が必要な箇所
  - `SyuunyuuKingakuTotalAmount.from(SyuunyuuKingaku, WithdrewKingaku)`: 1行の軽微な修正
    - 修正前: `BigDecimal totalAmount = income.getValue().add(withdrew.getValue());`
    - 修正後: `BigDecimal totalAmount = income.add(withdrew);`
  - `SisyutuKingakuTotalAmount.add()`: 同様に軽微な修正

**Phase3での作業見積もり**:
- 影響ファイル数: 約5-7ファイル（金額系ドメインタイプ）
- テスト修正: 既存テストはほぼ変更不要（内部実装の変更のため）

---

### 1-2. IncomeAmountとSyuunyuuKingakuの統合検討

**優先度**: 🟡 中

**概要**:
- `domain.type.common.IncomeAmount`（Phase1で作成）
- `domain.type.account.inquiry.SyuunyuuKingaku`（Phase2以前から存在）
- 同じ「収入金額」を表す2つのクラスが存在

**問題点**:
1. 概念の重複
   - どちらも「収入金額」を表すが、用途が異なる可能性
2. 使い分けが不明確
   - 新規開発時にどちらを使うべきか判断が難しい
3. コードの一貫性
   - 一方がMoney継承、もう一方が独立実装という不整合

**対応方針**:
1. 用途の明確化
   - `IncomeAmount`: 汎用的な収入金額（登録・更新機能で使用）
   - `SyuunyuuKingaku`: 照会機能専用の収入金額
2. 統合の検討
   - Phase3でドメインモデルを精査し、統合可能か判断
   - パッケージ構成の見直し（`common` vs `account.inquiry`）

**Phase3での作業**:
- ドメインモデルの分析
- 統合 or 役割分担の方針決定
- ドキュメント化

---

## 2. テスト関連の課題

### 2-1. AccountMonthInquiryControllerIntegrationTestのエラー

**優先度**: ⚪ （ユーザー対応）

**概要**:
- セッションスコープの`LoginUserSession`から`LoginUserInfo`を取得できない
- Spring Securityに絡むセッション管理の問題

**現状**:
- 統合テストが10/11失敗（Phase2完了時点では別のテストを実行）
- テスト失敗の原因: `scopedTarget.loginUserSession`の`loginUserInfo`がnull

**エラー詳細**:
```
java.lang.NullPointerException: Cannot invoke
"com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo.getUserId()"
because "user" is null
```

**対応方針**:
- ユーザー様が引き取り済み
- Phase3では対応不要

**参考情報**:
- エラー発生箇所: `AccountMonthInquiryUseCase.java:74`
- セッション状態: Spring Securityの認証は成功しているが、スコープ付きBeanのプロキシ解決に問題

---

## 3. 実装上の改善点

### 3-1. BigDecimalの取り扱いパターン統一

**優先度**: 🔵 低（Money継承で自動的に改善）

**概要**:
- Phase2で値オブジェクト指向にリファクタリング済み
- Phase3でMoney継承に移行することで、さらに改善可能

**具体例**:

**現在（Phase2完了時点）**:
```java
// SyuunyuuKingakuTotalAmount.from(SyuunyuuKingaku, WithdrewKingaku)
BigDecimal totalAmount = income.getValue().add(withdrew.getValue());
return new SyuunyuuKingakuTotalAmount(totalAmount);
```

**Phase3でMoney継承後**:
```java
BigDecimal totalAmount = income.add(withdrew);  // protected Money.add()を使用
return new SyuunyuuKingakuTotalAmount(totalAmount);
```

**対応方針**:
- Money継承移行時に自然に解決
- 個別対応は不要

---

## 4. ドキュメント・設計上の課題

### 4-1. Phase2の設計ドキュメント完備

**優先度**: 🔴 高

**概要**:
- Phase2で作成したドメインモデルの設計意図をドキュメント化
- 新規メンバーや将来の自分が理解できるように記録を残す

**必要な内容**:
1. 集約設計の意図
   - なぜ`IncomeAndExpenditure`を集約ルートとしたのか
   - 集約の境界をどう決定したのか
2. ドメインサービスの責務
   - `IncomeAndExpenditureConsistencyService`の役割
   - なぜドメインサービスが必要だったのか
3. 整合性検証のビジネスルール
   - 収入金額の整合性ルール
   - 支出金額の整合性ルール
   - データ存在の整合性ルール
4. テスト戦略
   - 単体テストのカバレッジ方針
   - 統合テストのスコープ

**対応方針**:
- Phase3開始時に作成
- フォーマットは既存の`Phase2_Step2_ドメインモデル設計提案書.md`を参考

---

## 5. Phase2で実施した改善（記録）

### 5-1. 値オブジェクト指向へのリファクタリング

**実施日**: 2025-12-08

**改善内容**:
1. `IncomeAndExpenditure.getTotalIncome()`の修正
   - 修正前: プリミティブ値（BigDecimal）で計算
   - 修正後: 値オブジェクト同士で計算
2. `SyuunyuuKingakuTotalAmount.from(SyuunyuuKingaku, WithdrewKingaku)`の追加
   - ドメインロジック「収入金額合計 = 収入金額 + 積立金取崩金額」をカプセル化
3. `SisyutuKingakuTotalAmount.from(SisyutuKingaku)`の追加
   - 整合性検証での値オブジェクト比較を可能に
4. `IncomeAndExpenditureConsistencyService.validateExpenditureConsistency()`の修正
   - BigDecimal比較から値オブジェクト比較に変更

**成果**:
- ドメイン知識のカプセル化強化
- 型安全性の向上
- Phase3へのスムーズな移行準備完了

---

## Phase3への引き継ぎ事項

### Phase3で対応すべき課題

1. **必須対応**:
   - ✅ 1-1. Money基底クラス継承への移行
   - ✅ 4-1. Phase2の設計ドキュメント完備

2. **Phase3で検討**:
   - 🔍 1-2. IncomeAmountとSyuunyuuKingakuの統合検討

3. **Phase3で完了した課題**:
   - ✅ 1-0. ユースケース層の行数削減目標の原因分析と評価（2025-12-14完了）

4. **Phase3では対応不要**:
   - ❌ 2-1. AccountMonthInquiryControllerIntegrationTestのエラー（ユーザー対応）
   - ❌ 3-1. BigDecimalの取り扱いパターン統一（Money継承で自動解決）

### Phase3のゴール提案

Phase2の残課題を踏まえ、Phase3では以下を目標とする：
1. **Money基底クラス継承への完全移行**
   - すべての金額系ドメインタイプの統一
   - コード品質の向上
2. **ドメインモデルの完成度向上**
   - Phase2設計ドキュメントの作成
   - ドメイン用語の統一
3. **Phase4（登録・更新機能）への準備**
   - 照会機能のドメインモデルの安定化
   - 登録・更新機能で再利用可能な設計の確立

---

## 参考資料

- [Phase2_Step2_ドメインモデル設計提案書.md](./Phase2_Step2_ドメインモデル設計提案書.md)
- Money基底クラス: `src/main/java/com/yonetani/webapp/accountbook/domain/type/common/Money.java`
- IncomeAmount: `src/main/java/com/yonetani/webapp/accountbook/domain/type/common/IncomeAmount.java`
