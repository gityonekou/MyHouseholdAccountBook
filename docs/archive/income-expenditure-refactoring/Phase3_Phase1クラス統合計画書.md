# Phase3: Phase1クラス統合計画書

## 1. 統合の目的と背景

### 1.1 目的
Phase3_Step1で月次収支照会(account.inquiry)パッケージの金額クラスをMoney基底クラス継承に移行したが、Phase1で既に作成済みの共通金額クラス(domain.type.common)と機能が重複していることが判明した。

本計画では、inquiry専用クラスを廃止し、Phase1の共通クラスに統合することで以下を実現する:
- コードの重複削減
- 保守性の向上
- より堅牢な実装の適用(Phase1クラスはnull値チェック等が充実)

### 1.2 背景
**Phase1で作成された共通クラス(2025/11/18):**
- IncomeAmount (収入金額)
- ExpenditureAmount (支出金額)
- BalanceAmount (収支金額)

**Phase3_Step1で作成されたinquiry専用クラス:**
- SyuunyuuKingaku (2023/10/15作成 → 2025/12/14 Money継承)
- SisyutuKingaku (2023/10/07作成 → 2025/12/15 Money継承)

**Phase3_Step2の対象クラス:**
- SyuusiKingaku (2023/10/15作成 → Money未継承)

Phase1クラスの方が後発で、より堅牢な実装(算術メソッドのnull値チェック、結果検証等)を持つため、これらに統合することが適切と判断。

---

## 2. クラス対応関係

### 2.1 置き換え対応表

| Phase3 inquiry専用クラス | Phase1 共通クラス | 状態 |
|-------------------------|------------------|------|
| SyuunyuuKingaku | IncomeAmount | 置き換え可能 |
| SisyutuKingaku | ExpenditureAmount | 置き換え可能 |
| SyuusiKingaku | BalanceAmount | 置き換え可能 |

### 2.2 機能比較

#### 2.2.1 IncomeAmount vs SyuunyuuKingaku

**共通機能:**
- Money基底クラス継承
- 0以上の値のみ許可(マイナス値禁止)
- ZERO定数
- from(BigDecimal) ファクトリメソッド

**IncomeAmountの優位点:**
- add()メソッド: null値チェックあり
- subtract()メソッド: null値チェック + 結果マイナス値チェックあり

**SyuunyuuKingakuの固有機能:**
- from(IncomeRegistItem): セッション情報からの生成メソッド
  - 収入区分が「積立からの取崩し(3)」の場合、ZERO返却
  - それ以外の場合、セッション情報の収入金額から生成

**必要な対応:**
- from(IncomeRegistItem)メソッドをIncomeAmountに追加

#### 2.2.2 ExpenditureAmount vs SisyutuKingaku

**共通機能:**
- Money基底クラス継承
- 0以上の値のみ許可(マイナス値禁止)
- ZERO定数
- from(BigDecimal) ファクトリメソッド
- add()メソッド
- subtract()メソッド

**ExpenditureAmountの優位点:**
- add()メソッド: null値チェックあり
- subtract()メソッド: null値チェック + 結果マイナス値チェックあり
- applyCoupon()メソッド: クーポン金額適用機能

**SisyutuKingakuの特徴:**
- シンプルな算術メソッド実装(null値チェックなし)

**必要な対応:**
- なし(ExpenditureAmountがすべての機能を包含)

#### 2.2.3 BalanceAmount vs SyuusiKingaku

**共通機能:**
- 収支金額を表現
- マイナス値を許可(赤字の場合)
- ZERO定数

**BalanceAmountの優位点:**
- **Money基底クラス継承済み**(SyuusiKingakuは未継承)
- isDeficit()メソッド: 赤字判定
- isSurplus()メソッド: 黒字判定
- calculate()メソッド: 収入-支出計算
- toFormatString()等、Moneyの全メソッド使用可能

**SyuusiKingakuの特徴:**
- 独自実装(Moneyを継承していない)
- toString()のみ(DomainCommonUtils使用)

**必要な対応:**
- なし(BalanceAmountがすべての機能を包含)
- **重要**: SyuusiKingakuをMoney継承に移行する必要がなくなる

---

## 3. 移行手順

### 3.1 Phase1クラスへの機能追加

#### Step 1: IncomeAmountへfrom(IncomeRegistItem)メソッド追加

**追加内容:**
```java
/**
 *<pre>
 * 収支登録情報(セッション情報)の値から「収入金額」項目の値を表すドメインタイプを生成します
 *
 * [ガード節]
 * ・null値
 *</pre>
 * @param income 収支登録情報(セッション情報)
 * @return 「収入金額」項目ドメインタイプ
 *
 */
public static IncomeAmount from(IncomeRegistItem income) {
    // ガード節(null)
    if(income == null) {
        throw new MyHouseholdAccountBookRuntimeException(
            "収支登録情報(セッション情報)の設定値がnullです。管理者に問い合わせてください。");
    }
    // 収入区分が「積立からの取崩し(3)」の場合、
    // 積立金取崩し金額の収支登録情報(セッション情報)となるので値0の収入金額を生成して返却
    if(Objects.equals(income.getIncomeKubun(),
        MyHouseholdAccountBookContent.INCOME_KUBUN_WITHDREW_SELECTED_VALUE)) {
        return ZERO;
    }
    // 収支登録情報(セッション情報)の収入金額から「収入金額」項目の値を生成して返却
    return new IncomeAmount(income.getIncomeKingaku());
}
```

**必要なimport追加:**
- java.util.Objects
- com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent
- com.yonetani.webapp.accountbook.presentation.session.IncomeRegistItem

### 3.2 影響範囲調査

#### Step 2: SyuunyuuKingaku使用箇所の調査
- Grepでパッケージimport文を検索
- 使用箇所をリストアップ
- 置き換え影響を分析

#### Step 3: SisyutuKingaku使用箇所の調査
- Grepでパッケージimport文を検索
- 使用箇所をリストアップ
- 置き換え影響を分析

#### Step 4: SyuusiKingaku使用箇所の調査
- Grepでパッケージimport文を検索
- 使用箇所をリストアップ
- 置き換え影響を分析

### 3.3 クラス置き換え実施

#### Step 5: SyuunyuuKingaku → IncomeAmount 置き換え
1. 各使用箇所でimport文を変更
2. クラス名を置き換え
3. コンパイルエラーがないことを確認

#### Step 6: SisyutuKingaku → ExpenditureAmount 置き換え
1. 各使用箇所でimport文を変更
2. クラス名を置き換え
3. コンパイルエラーがないことを確認

#### Step 7: SyuusiKingaku → BalanceAmount 置き換え
1. 各使用箇所でimport文を変更
2. クラス名を置き換え
3. toString()の使用箇所をtoFormatString()に変更(必要に応じて)
4. コンパイルエラーがないことを確認

### 3.4 旧クラスの削除

#### Step 8: inquiry専用クラスの削除
1. SyuunyuuKingaku.java 削除
2. SisyutuKingaku.java 削除
3. SyuusiKingaku.java 削除
4. 削除前にバックアップ取得を確認

### 3.5 テスト実施

#### Step 9: 単体テストの実施
1. 影響を受けるテストクラスを特定
2. テスト実行
3. テスト結果NG時は「テストNG時の対応フロー」に従う

#### Step 10: 結合テストの実施
1. AccountMonthInquiryIntegrationTest実行
2. その他の結合テスト実行
3. テスト結果NG時は「テストNG時の対応フロー」に従う

### 3.6 テストNG時の対応フロー

テスト結果がNGとなった場合の標準対応フロー:

1. **即座に作業を停止**
   - ソース修正やデータ修正を勝手に実施しない

2. **エラー内容の分析**
   - エラーメッセージ、スタックトレース、失敗したテストケースを確認
   - 原因を特定(仕様の誤解、実装ミス、テストデータ不備、等)

3. **修正案の作成**
   - 何を修正すべきか明確化
   - 修正による影響範囲を検討

4. **ユーザーへの報告**
   - 以下の形式で報告し、承認を得る

5. **承認後に修正実施**
   - ユーザーの承認を得てから修正
   - 絶対に勝手に修正しない

**報告フォーマット:**
```
## テスト失敗報告

### 失敗したテスト
- テストクラス: [クラス名]
- テストメソッド: [メソッド名]
- テストケース: [概要]

### エラー内容
[エラーメッセージ/スタックトレース]

### 原因分析
[なぜ失敗したかの分析]

### 修正提案
**修正箇所:**
- ファイル: [ファイルパス]
- 行数: [該当行]

**修正内容:**
[修正前後のコード、またはデータ]

**修正理由:**
[なぜこの修正が必要か]

### 影響範囲
[この修正による他への影響]

### 承認依頼
上記修正でよろしいでしょうか？
```

---

## 4. リスク分析

### 4.1 想定されるリスク

| リスク | 影響度 | 対策 |
|-------|--------|------|
| 使用箇所の見落とし | 高 | Grep検索で徹底的に調査 |
| メソッド名の違いによる不具合 | 中 | toString() vs toFormatString()の使い分け確認 |
| テストデータの不整合 | 中 | テスト実行で検証 |
| パフォーマンスへの影響 | 低 | 同じMoney継承のため影響なし |

### 4.2 ロールバック手順

万が一問題が発生した場合:
1. Gitで変更前の状態に戻す
2. 削除したクラスファイルをリストア
3. テスト実行で正常性確認

---

## 5. 期待される効果

### 5.1 コード品質向上
- **重複コード削減**: 3つのクラス削減
- **保守性向上**: 修正箇所が1箇所に集約
- **堅牢性向上**: Phase1クラスのより厳密なnull値チェック適用

### 5.2 開発効率向上
- **統一されたAPI**: 全機能でPhase1クラスを使用
- **ビジネスメソッド活用**: BalanceAmountの赤字/黒字判定メソッド利用可能

### 5.3 Phase3_Step2への影響
- **SyuusiKingakuのMoney継承移行が不要に**: BalanceAmountは既にMoney継承済み
- **Phase3_Step2の作業範囲縮小**: 残り5クラスに集中可能

---

## 6. 実施スケジュール(概要)

| Step | 作業内容 | 想定状況 |
|------|---------|---------|
| 1 | IncomeAmountへのメソッド追加 | 新規実装 |
| 2-4 | 影響範囲調査 | 使用箇所特定 |
| 5-7 | クラス置き換え実施 | 機械的置き換え |
| 8 | 旧クラス削除 | クリーンアップ |
| 9-10 | テスト実施 | 品質保証 |

---

## 7. 備考

### 7.1 Phase3_Step2計画書への影響
- Phase3_Step2_Money継承移行計画書.mdの更新が必要
- SyuusiKingakuが対象クラスから除外される

### 7.2 ドキュメント更新
- Phase3_Step1_Money継承移行計画書.md: 本統合作業への参照追加
- 本計画書: 作業履歴の記録

---

## 8. 作業完了報告

### 8.1 実施内容

**実施日**: 2025/12/21

**作業ステータス**: ✅ 完了

以下のすべてのステップを完了しました:

1. ✅ **Step 1**: IncomeAmountへfrom(IncomeRegistItem)メソッド追加
   - ファイル: [IncomeAmount.java:83-98](src/main/java/com/yonetani/webapp/accountbook/domain/type/common/IncomeAmount.java#L83-L98)
   - version: 1.01.00に更新

2. ✅ **Step 2-4**: 影響範囲調査
   - SyuunyuuKingaku使用箇所: 12ファイル特定
   - SisyutuKingaku使用箇所: 33ファイル特定
   - SyuusiKingaku使用箇所: 6ファイル特定

3. ✅ **Step 5-7**: クラス置き換え実施
   - すべての使用箇所で置き換え完了
   - Phase3_Step2対象クラス(SisyutuKingakuB/C/BC)でも部分的にPhase1クラス使用

4. ✅ **Step 8**: 旧クラス削除
   - SyuunyuuKingaku.java 削除
   - SisyutuKingaku.java 削除
   - SyuusiKingaku.java 削除

5. ✅ **Step 9-10**: テスト実施
   - AccountMonthInquiryIntegrationTest: 10テスト成功
   - SyuunyuuKingakuTotalAmountTest: 13テスト成功
   - SisyutuKingakuTotalAmountTest: 21テスト成功

### 8.2 修正を要した箇所

**テストコンパイルエラー修正:**
1. [IncomeAndExpenditureConsistencyServiceTest.java](src/test/java/com/yonetani/webapp/accountbook/domain/service/account/inquiry/IncomeAndExpenditureConsistencyServiceTest.java) - テストコード内のSisyutuKingaku参照
2. [IncomeAmountTest.java](src/test/java/com/yonetani/webapp/accountbook/domain/type/common/IncomeAmountTest.java) - from(null)の曖昧な参照
3. [SyuunyuuKingakuTotalAmountTest.java](src/test/java/com/yonetani/webapp/accountbook/domain/type/account/inquiry/SyuunyuuKingakuTotalAmountTest.java) - IncomeAmountを使用
4. [SisyutuKingakuTotalAmountTest.java](src/test/java/com/yonetani/webapp/accountbook/domain/type/account/inquiry/SisyutuKingakuTotalAmountTest.java) - ExpenditureAmountを使用
5. [AccountMonthInquiryUseCase.java:216](src/main/java/com/yonetani/webapp/accountbook/application/usecase/account/inquiry/AccountMonthInquiryUseCase.java#L216) - `toString()` → `toFormatString()` 変更

### 8.3 達成された効果

1. **コード品質向上**
   - ✅ 重複コード削減: 3クラス削除
   - ✅ 堅牢性向上: Phase1クラスのnull値チェック適用

2. **Phase3_Step2への影響**
   - ✅ SyuusiKingakuのMoney継承移行が不要に
   - ✅ Phase3_Step2の作業範囲縮小

3. **統一API**
   - ✅ 全機能でPhase1共通クラスを使用
   - ✅ BalanceAmountのビジネスメソッド(isDeficit/isSurplus)利用可能

### 8.4 コード変更サマリ

**追加/修正:**
- IncomeAmount: from(IncomeRegistItem)メソッド追加

**削除:**
- SyuunyuuKingaku.java
- SisyutuKingaku.java
- SyuusiKingaku.java

**置き換え影響:**
- 本体コード: 51ファイル
- テストコード: 5ファイル

---

## 9. 作業履歴

| 日付 | version | 内容 |
|------|---------|------|
| 2025/12/21 | 1.00.00 | 新規作成 |
| 2025/12/21 | 1.01.00 | 作業完了・完了報告追加 |
