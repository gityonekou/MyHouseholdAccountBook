# Phase 4: 残存クラスのリファクタリング計画書

## 1. 概要

### 1.1 目的

Phase 3完了後の残存課題に対応し、コードベース全体の品質向上と保守性向上を実現します。

### 1.2 背景

**Phase 3完了時の状況**:

Phase 3では以下の成果を得ました：
- Step 1: Money基底クラス継承による3クラスの移行完了
- Step 2: NullableMoney基底クラス継承による6クラスの移行完了
- Step 3: NullableDateValue基底クラス作成とPaymentDate統合（3クラス→1クラス）

**残存課題**:

1. **日付系クラス**: EventStartDate、EventEndDate、ShoppingDate
2. **命名改善**: SisyutuKingakuB/C/BCクラスの不明瞭な命名

### 1.3 スコープ

**対象タスク**:

| No | タスク | 目的 | 優先度 |
|----|-------|------|--------|
| 1 | クラス名改善 | SisyutuKingakuB/C/BCの命名をビジネス意味が分かる名前に変更 | 高 |
| 2 | EventDate系移行 | EventStartDate、EventEndDateのDateValue継承移行 | 中 |
| 3 | ShoppingDate移行 | ShoppingDateのDateValue継承移行 | 中 |

---

## 2. タスク詳細

### 2.1 タスク1: クラス名改善（SisyutuKingakuB/C/BC）

#### 2.1.1 現状の問題

**現在のクラス名**:
- `SisyutuKingakuB`: 支出金額B（意味不明）
- `SisyutuKingakuC`: 支出金額C（意味不明）
- `SisyutuKingakuBC`: 支出金額BC（意味不明）

**ビジネス上の意味**:
- **支出金額B**: 無駄遣い（軽度）の金額
- **支出金額C**: 無駄遣い（重度）の金額
- **支出金額BC**: 無駄遣い合計金額（B + C）

支出金額登録時、ユーザーは「無駄遣いなし」「無駄遣い（軽度）」「無駄遣い（重度）」を選択して登録します。
支出項目毎にこれらが集計され、無駄遣いの金額と割合が可視化されます。

#### 2.1.2 改善案

**新しいクラス名**:
```java
SisyutuKingakuB  → MinorWasteExpenditure    // 無駄遣い（軽度）支出金額
SisyutuKingakuC  → SevereWasteExpenditure   // 無駄遣い（重度）支出金額
SisyutuKingakuBC → TotalWasteExpenditure    // 無駄遣い合計支出金額
```

**命名の根拠**:
- **Minor**: 軽度を表す一般的な英語表現
- **Severe**: 重度を表す一般的な英語表現
- **Total**: 合計を表す明確な表現
- **WasteExpenditure**: 無駄遣い支出金額の意味が明確

#### 2.1.3 影響範囲

**クラスファイル**（3ファイル）:
- [SisyutuKingakuB.java](../src/main/java/com/yonetani/webapp/accountbook/domain/type/account/inquiry/SisyutuKingakuB.java)
- [SisyutuKingakuC.java](../src/main/java/com/yonetani/webapp/accountbook/domain/type/account/inquiry/SisyutuKingakuC.java)
- [SisyutuKingakuBC.java](../src/main/java/com/yonetani/webapp/accountbook/domain/type/account/inquiry/SisyutuKingakuBC.java)

**テストクラス**（3ファイル）:
- [SisyutuKingakuBTest.java](../src/test/java/com/yonetani/webapp/accountbook/domain/type/account/inquiry/SisyutuKingakuBTest.java)
- [SisyutuKingakuCTest.java](../src/test/java/com/yonetani/webapp/accountbook/domain/type/account/inquiry/SisyutuKingakuCTest.java)
- [SisyutuKingakuBCTest.java](../src/test/java/com/yonetani/webapp/accountbook/domain/type/account/inquiry/SisyutuKingakuBCTest.java)

**使用箇所**（10ファイル）:
- AccountMonthInquiryExpenditureItemList.java
- AccountYearMeisaiInquiryList.java
- SisyutuKingakuItem.java
- SisyutuKingakuItemHolder.java
- AccountMonthInquiryUseCase.java
- AccountYearInquiryUseCase.java
- IncomeAndExpenditureRegistUseCase.java
- SimpleShoppingRegistUseCase.java
- ShoppingRegistExpenditureAndSisyutuKingakuComponent.java
- ShoppingRegistTopMenuUseCase.java
- SisyutuKingakuTableDataSource.java
- SisyutuKingakuReadWriteDto.java
- AccountMonthInquiryIntegrationTest.java

**DTOフィールド名**:
- `sisyutuKingakuB` → `minorWasteExpenditure`
- `sisyutuKingakuC` → `severeWasteExpenditure`

※ただし、DBカラム名（`SISYUTU_KINGAKU_B`、`SISYUTU_KINGAKU_C`）は変更しません。

#### 2.1.4 実装手順

**Step 1: クラスファイルのリネーム**
1. SisyutuKingakuB.java → MinorWasteExpenditure.java（リネーム + クラス名変更）
2. SisyutuKingakuC.java → SevereWasteExpenditure.java（リネーム + クラス名変更）
3. SisyutuKingakuBC.java → TotalWasteExpenditure.java（リネーム + クラス名変更）
4. Javadocコメント更新

**Step 2: テストクラスのリネームと更新**
1. SisyutuKingakuBTest.java → MinorWasteExpenditureTest.java
2. SisyutuKingakuCTest.java → SevereWasteExpenditureTest.java
3. SisyutuKingakuBCTest.java → TotalWasteExpenditureTest.java
4. テストケース内の参照更新

**Step 3: ドメインモデルの更新**
1. SisyutuKingakuItem.java（フィールド名とgetter名更新）
2. AccountMonthInquiryExpenditureItemList.java（import更新）
3. AccountYearMeisaiInquiryList.java（import更新）
4. SisyutuKingakuItemHolder.java（import更新）

**Step 4: UseCaseクラスの更新**
1. AccountMonthInquiryUseCase.java（変数名、メソッド名更新）
2. AccountYearInquiryUseCase.java（変数名、メソッド名更新）
3. IncomeAndExpenditureRegistUseCase.java（変数名更新）
4. SimpleShoppingRegistUseCase.java（変数名更新）
5. ShoppingRegistExpenditureAndSisyutuKingakuComponent.java（変数名更新）
6. ShoppingRegistTopMenuUseCase.java（変数名更新）

**Step 5: インフラ層の更新**
1. SisyutuKingakuTableDataSource.java（import更新）
2. SisyutuKingakuReadWriteDto.java（フィールド名更新）

**Step 6: 統合テストの更新**
1. AccountMonthInquiryIntegrationTest.java（コメント更新）

**Step 7: テスト実行**
1. MinorWasteExpenditureTest実行
2. SevereWasteExpenditureTest実行
3. TotalWasteExpenditureTest実行
4. AccountMonthInquiryIntegrationTest実行
5. 全テスト実行

#### 2.1.5 リスク

| リスク | 影響度 | 対策 |
|--------|--------|------|
| IDEのリファクタリング機能で漏れが発生 | 高 | 手動でGrep検索し全置換箇所を確認 |
| フィールド名変更によるシリアライズ互換性 | 低 | DTOは内部のみ使用、外部APIなし |
| テストケースの更新漏れ | 中 | 全テスト実行で確認 |

---

### 2.2 タスク2: EventDate系クラスの移行

#### 2.2.1 対象クラス

- **EventStartDate**: イベント開始日
- **EventEndDate**: イベント終了日

#### 2.2.2 実装方針

Phase 3 Step 3と同様の手順で実施：
1. 既存DateValue継承確認
2. null許容の必要性確認
3. NullableDateValue継承への移行（必要に応じて）
4. テスト作成

**詳細は実施時に別途計画書作成**

---

### 2.3 タスク3: ShoppingDateの移行

#### 2.3.1 対象クラス

- **ShoppingDate**: 買い物登録日

#### 2.3.2 実装方針

Phase 3 Step 3と同様の手順で実施：
1. 既存DateValue継承確認
2. null許容の必要性確認
3. NullableDateValue継承への移行（必要に応じて）
4. テスト作成

**詳細は実施時に別途計画書作成**

---

## 3. 実施スケジュール

| タスク | 優先度 | 見積もり工数 | 備考 |
|--------|--------|-------------|------|
| タスク1: クラス名改善 | 高 | 中 | 影響範囲広いが単純作業 |
| タスク2: EventDate系移行 | 中 | 小 | Phase 3パターン踏襲 |
| タスク3: ShoppingDate移行 | 中 | 小 | Phase 3パターン踏襲 |

**推奨実施順序**:
1. タスク1（クラス名改善）を最優先で実施
2. タスク2、タスク3は必要性に応じて実施

---

## 4. 期待成果

### 4.1 タスク1完了時の成果

**定量的成果**:
- クラス名変更: 3クラス
- テストクラス名変更: 3クラス
- 影響ファイル更新: 約13ファイル

**定性的成果**:
- **可読性向上**: ビジネスドメインの意味が明確なクラス名
- **保守性向上**: 新規参画者がコード理解しやすい
- **ドキュメント性向上**: コード自体がドメイン知識を表現

### 4.2 Phase 4完了時の全体成果

- **DateValue継承完了**: すべての日付系クラスが基底クラスを継承
- **命名統一**: 意味不明な略語を排除
- **API統一**: 統一的なインターフェースを提供

---

## 5. Phase 4実施結果

### 5.1 実施完了タスク

**✅ タスク1: クラス名改善（SisyutuKingakuB/C/BC）**
- SisyutuKingakuB → MinorWasteExpenditure（無駄遣い軽度）
- SisyutuKingakuC → SevereWasteExpenditure（無駄遣い重度）
- SisyutuKingakuBC → TotalWasteExpenditure（無駄遣い合計）
- テストクラス3件作成
- 影響範囲13ファイル更新完了

**✅ タスク2: EventDate系クラスの移行**
- EventStartDate、EventEndDate削除
- EventDate（DateValue継承）に統合完了
- テストクラス作成完了

**✅ タスク3: ShoppingDateの調査と改善**
- DateValue継承は不適切と判断（TargetYearMonth検証が必要）
- toDisplayString()メソッド追加（yyyy/MM/dd形式）
- toString()はデバッグ用（ISO-8601形式）として維持
- テストクラス作成完了

### 5.2 追加実施タスク

**✅ IncomingAmountクラスの削除**
- SyuunyuuKingakuTotalAmountへの統合が前Phaseで完了済み
- 中間変換オブジェクトとして不要と判断
- 削除完了

**✅ DDD設計ドキュメント作成**
- ドキュメント: [DDD設計_金額クラス統合判断基準.md](DDD設計_金額クラス統合判断基準.md)
- 値オブジェクト統合vs分離の判断基準を明文化
- WithdrewKingaku/WithdrewKingakuTotalAmount分離維持の根拠
- IncomingAmount統合の根拠
- 中間オブジェクトパターンのチェックリスト

### 5.3 最終成果

**定量的成果**:
- クラス名変更: 3クラス → 3クラス（名前改善）
- 日付クラス統合: 2クラス → 1クラス（EventDate）
- 不要クラス削除: IncomingAmount削除
- テストクラス作成: 7件
- ドキュメント作成: 1件

**定性的成果**:
- ビジネスドメイン概念の明確化（WasteExpenditure系）
- DateValue継承パターンの適用範囲明確化
- DDD設計判断基準の文書化
- コードベース全体の可読性・保守性向上

---

## 6. Phase 5への引き継ぎ

Phase 4完了により、基盤となる値オブジェクト（Money、DateValue系）のリファクタリングが完了しました。

**Phase 5の計画**:
- **対象**: 収支登録機能のリファクタリング
- **ユースケース**: [IncomeAndExpenditureRegistUseCase.java](../src/main/java/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureRegistUseCase.java)
- **目的**: 値オブジェクトリファクタリング成果を活用したユースケース層の改善

Phase 5詳細計画は別途作成予定。

---

## 7. 更新履歴

| 版数 | 更新日 | 更新内容 |
|------|--------|---------|
| 1.00.00 | 2025-12-28 | 初版作成 |
| 2.00.00 | 2026-01-02 | Phase 4完了、実施結果追記、Phase 5計画追記 |

---

**作成日**: 2025-12-28
**最終更新日**: 2026-01-02
**作成者**: DDD Refactoring Project Phase 4
**ステータス**: ✅完了
