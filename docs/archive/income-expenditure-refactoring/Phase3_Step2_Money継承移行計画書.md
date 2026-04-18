# Phase 3 Step 2: 残りのinquiry関連金額系クラスのMoney継承移行計画書

## 1. 概要

### 1.1 目的

Phase 3 Step 1で実施したMoney基底クラス継承移行の成果を踏まえ、残りのinquiry関連金額系クラスに対してMoney基底クラス継承を適用します。これにより、コードの重複排除、保守性の向上、API統一を実現します。

### 1.2 背景

Phase 3 Step 1では以下のクラスをMoney基底クラスに移行しました:
- SyuunyuuKingaku（収入金額）
- SyuunyuuKingakuTotalAmount（収入金額合計）
- SisyutuKingaku（支出金額）
- SisyutuKingakuTotalAmount（支出金額合計）

さらに、WithdrewKingakuのAPI統一と基底クラスメソッドの活用により、以下の成果を得ました:
- 94テストケースすべて成功
- コード重複の排除
- null安全性の統一
- ドメイン凝集度の向上

**Phase1クラス統合による影響**:

2025年12月21日に実施されたPhase1クラス統合作業（詳細は[Phase3_Phase1クラス統合計画書.md](Phase3_Phase1クラス統合計画書.md)参照）により、以下の変更が発生しました:

1. **SyuunyuuKingaku、SisyutuKingaku、SyuusiKingakuを削除**:
   - SyuunyuuKingaku → IncomeAmount（Phase1共通クラス）に置換
   - SisyutuKingaku → ExpenditureAmount（Phase1共通クラス）に置換
   - SyuusiKingaku → BalanceAmount（Phase1共通クラス）に置換

2. **Step2対象クラスの部分的完了**:
   - SisyutuKingakuB/C/BCのgetPercentage()メソッドが、SisyutuKingaku（削除済み）ではなくExpenditureAmount（Phase1クラス）を使用するように変更
   - SyuusiKingakuTotalAmountのadd()メソッドが、SyuusiKingaku（削除済み）ではなくBalanceAmount（Phase1クラス）を使用するように変更
   - IncomingAmountのfrom()メソッドが、SyuunyuuKingaku（削除済み）ではなくIncomeAmount（Phase1クラス）を使用するように変更

3. **スコープの変更**:
   - SyuusiKingakuは既にBalanceAmountに置き換えられたため、本計画書の対象外となりました
   - Phase1共通クラス（IncomeAmount、ExpenditureAmount、BalanceAmount）は既にMoney基底クラスを継承しており、より堅牢な実装を提供しています

### 1.3 スコープ

**対象クラス（6クラス）**:
1. NullableMoney（null許容金額基底クラス）※新規作成
2. SisyutuYoteiKingaku（支出予定金額）
3. SisyutuKingakuB（支出金額B）※Phase1統合で部分完了
4. SisyutuKingakuC（支出金額C）※Phase1統合で部分完了
5. SisyutuKingakuBC（支出金額BとCの合計値）※Phase1統合で部分完了
6. ShiharaiKingaku（支払金額）

**リファクタリング対象**:
- WithdrewKingaku（NullableMoney継承に変更）

**対象外**:
- SyuusiKingaku（Phase1統合でBalanceAmountに置換済み）
- SyuunyuuKingaku（Phase1統合でIncomeAmountに置換済み）
- SisyutuKingaku（Phase1統合でExpenditureAmountに置換済み）

---

## 2. 現状分析

### 2.1 対象クラスの特徴

#### 2.1.1 SisyutuYoteiKingaku（支出予定金額）

**ファイルパス**: [SisyutuYoteiKingaku.java](../src/main/java/com/yonetani/webapp/accountbook/domain/type/account/inquiry/SisyutuYoteiKingaku.java)

**特徴**:
- **マイナス値非許容**: マイナス値チェックあり
- **null非許容**: nullチェックあり
- **スケール2**: BigDecimalのスケールは2
- **add()メソッドあり**: 支出予定金額の加算処理
- **DomainCommonUtils依存**: toString()でDomainCommonUtils.formatKingakuAndYen()を使用

**現在の実装**:
```java
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class SisyutuYoteiKingaku {
    private final BigDecimal value;
    public static final SisyutuYoteiKingaku ZERO = SisyutuYoteiKingaku.from(BigDecimal.ZERO.setScale(2));

    public static SisyutuYoteiKingaku from(BigDecimal sisyutuYoteiKingaku) {
        // null、マイナス、スケールチェック
    }

    public SisyutuYoteiKingaku add(SisyutuYoteiKingaku addValue) {
        return new SisyutuYoteiKingaku(this.value.add(addValue.getValue()));
    }

    @Override
    public String toString() {
        return DomainCommonUtils.formatKingakuAndYen(value);
    }
}
```

#### 2.1.3 ShiharaiKingaku（支払金額）

**ファイルパス**: [ShiharaiKingaku.java](../src/main/java/com/yonetani/webapp/accountbook/domain/type/account/inquiry/ShiharaiKingaku.java)

**特徴**:
- **マイナス値非許容**: マイナス値チェックあり
- **null非許容**: nullチェックあり
- **スケール2**: BigDecimalのスケールは2
- **DomainCommonUtils依存**: toString()でDomainCommonUtils.formatKingakuAndYen()を使用

**現在の実装**:
```java
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class ShiharaiKingaku {
    private final BigDecimal value;
    public static final ShiharaiKingaku ZERO = ShiharaiKingaku.from(BigDecimal.ZERO.setScale(2));

    public static ShiharaiKingaku from(BigDecimal kingaku) {
        // null、マイナス、スケールチェック
    }

    @Override
    public String toString() {
        return DomainCommonUtils.formatKingakuAndYen(value);
    }
}
```

#### 2.1.3 SisyutuKingakuB/C（支出金額B/C）

**ファイルパス**:
- [SisyutuKingakuB.java](../src/main/java/com/yonetani/webapp/accountbook/domain/type/account/inquiry/SisyutuKingakuB.java)
- [SisyutuKingakuC.java](../src/main/java/com/yonetani/webapp/accountbook/domain/type/account/inquiry/SisyutuKingakuC.java)

**特徴**:
- **null値許容**: 値がnullの場合も許容（特殊な設計）
- **マイナス値非許容**: マイナス値チェックあり
- **スケール2**: BigDecimalのスケールは2
- **add/subtractメソッドあり**: null値を考慮した加算・減算処理
- **専用フォーマットメソッド**: toSisyutuKingakuBString() / toSisyutuKingakuCString()
- **割合計算メソッド**: getPercentage()メソッドあり
- **Phase1統合対応済み**: getPercentage()メソッドの引数がExpenditureAmount（Phase1クラス）に変更済み

**現在の実装**:
```java
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class SisyutuKingakuB {
    private final BigDecimal value; // null許容

    public static SisyutuKingakuB from(BigDecimal kingakub) {
        if (kingakub == null) {
            return new SisyutuKingakuB(null); // null許容
        }
        // マイナス、スケールチェック
    }

    public SisyutuKingakuB add(SisyutuKingakuB addValue) {
        // null安全な加算処理
    }

    public SisyutuKingakuB subtract(SisyutuKingakuB subtractValue) {
        // null安全な減算処理
    }

    public String toSisyutuKingakuBString() {
        return DomainCommonUtils.formatKingakuAndYen(value);
    }

    // Phase1統合でExpenditureAmountを使用するように変更済み
    public String getPercentage(ExpenditureAmount expenditureAmount) {
        // 支出金額Bの割合算出
    }
}
```

#### 2.1.4 SisyutuKingakuBC（支出金額BとCの合計値）

**ファイルパス**: [SisyutuKingakuBC.java](../src/main/java/com/yonetani/webapp/accountbook/domain/type/account/inquiry/SisyutuKingakuBC.java)

**特徴**:
- **複合ドメインオブジェクト**: SisyutuKingakuBとSisyutuKingakuCの合計値を保持
- **3つのフィールド**: value（合計値）、sisyutuKingakuB、sisyutuKingakuC
- **null値考慮**: B、Cのnull値を考慮した合計値計算
- **専用フォーマットメソッド**: toSisyutuKingakuBCString()
- **割合計算メソッド**: getPercentage()メソッドあり
- **Phase1統合対応済み**: getPercentage()メソッドの引数がExpenditureAmount（Phase1クラス）に変更済み

**現在の実装**:
```java
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class SisyutuKingakuBC {
    private final BigDecimal value;
    private final SisyutuKingakuB sisyutuKingakuB;
    private final SisyutuKingakuC sisyutuKingakuC;

    public static SisyutuKingakuBC from(SisyutuKingakuB kingakuB, SisyutuKingakuC kingakuC) {
        // B、Cのnull値を考慮した合計値計算
    }

    // Phase1統合でExpenditureAmountを使用するように変更済み
    public String getPercentage(ExpenditureAmount expenditureAmount) {
        // 支出金額BCの割合算出
    }
}
```

### 2.2 課題

| No | 課題 | 対象クラス | 影響 |
|----|------|----------|------|
| 1 | コードの重複 | 全クラス | BigDecimal操作、バリデーションロジックが重複 |
| 2 | DomainCommonUtils依存 | SisyutuYoteiKingaku, ShiharaiKingaku, SisyutuKingakuB, SisyutuKingakuC, SisyutuKingakuBC | ドメインロジックが外部ユーティリティに依存 |
| 3 | API不統一 | SisyutuKingakuB/C | 専用メソッド名（toSisyutuKingakuBString()等）を使用 |
| 4 | null値許容クラス間のコード重複 | WithdrewKingaku, SisyutuKingakuB, SisyutuKingakuC | null安全な算術演算ロジックが3クラスで重複 |
| 5 | 複合ドメインオブジェクト | SisyutuKingakuBC | 3つのフィールドを持つ特殊な設計 |
| 6 | Phase1統合の部分完了 | SisyutuKingakuB, SisyutuKingakuC, SisyutuKingakuBC | getPercentage()メソッドは既に更新済みだが、クラス本体はMoney継承していない |

---

## 3. 移行方針

### 3.1 基本方針

1. **NullableMoney基底クラスの新規作成**:
   - null値を許容する金額クラスの共通基底クラスを作成
   - WithdrewKingaku、SisyutuKingakuB/Cで共通のnull安全ロジックを提供
   - Phase1共通クラス（common package）に配置

2. **Money基底クラス継承可能なクラス**:
   - SisyutuYoteiKingaku
   - ShiharaiKingaku

   → Phase 3 Step 1と同じパターンで移行

3. **NullableMoney基底クラス継承可能なクラス**:
   - WithdrewKingaku（リファクタリング）
   - SisyutuKingakuB
   - SisyutuKingakuC

   → NullableMoney継承により、null安全演算ロジックの重複を排除

4. **基底クラス継承困難なクラス**:
   - SisyutuKingakuBC（複合ドメインオブジェクト）

   → API統一のみ実施

**注意**: Phase1統合により、SisyutuKingakuB/C/BCは既にExpenditureAmount（Phase1クラス）を使用するように部分的に更新されています。残りの作業は主にNullableMoney継承とAPI統一です。

### 3.2 移行パターン分類

#### パターン1: 標準Money継承（SisyutuYoteiKingaku, ShiharaiKingaku）

**適用条件**:
- null非許容
- マイナス値非許容
- スケール2
- add/subtractメソッドがMoney基底クラスのパターンと一致

**移行手順**:
1. Money基底クラスを継承
2. @EqualsAndHashCode(callSuper = true)に変更
3. 重複フィールド・メソッドを削除
4. バリデーションをMoney.validate()に委譲
5. add()メソッドをsuper.add()に変更
6. toString()をオーバーライドしてtoFormatString()を追加
7. テストクラスを作成

#### パターン2: NullableMoney継承（WithdrewKingaku, SisyutuKingakuB, SisyutuKingakuC）

**適用条件**:
- null値許容（Money基底クラスと異なる）
- null安全な算術演算が必要
- Phase1統合でgetPercentage()メソッドは既にExpenditureAmountを使用（SisyutuKingakuB/Cのみ）

**移行手順**:
1. NullableMoney基底クラスを継承
2. @EqualsAndHashCode(callSuper = true)に変更
3. 重複フィールド・メソッドを削除（value、add/subtract等）
4. バリデーションをNullableMoney.validate()に委譲
5. add/subtractメソッドをsuper.add/super.subtractに変更
6. 専用メソッド（toSisyutuKingakuBString()等）は互換性のため残す
7. テストを追加（WithdrewKingakuは既存94テストで回帰確認）

**Phase1統合による変更点**:
- SisyutuKingakuB/C: getPercentage()メソッドの引数は既にExpenditureAmountに変更済み
- この部分の作業は不要

#### パターン3: 複合オブジェクト対応（SisyutuKingakuBC）

**適用条件**:
- 複数フィールドを持つ複合ドメインオブジェクト
- Phase1統合でgetPercentage()メソッドは既にExpenditureAmountを使用

**対応手順**:
1. toFormatString()メソッドを追加
2. toString()をデバッグ用に変更
3. DomainCommonUtils依存を削除
4. テストを追加

**Phase1統合による変更点**:
- getPercentage()メソッドの引数は既にExpenditureAmountに変更済み
- この部分の作業は不要

---

## 4. 実装計画

### 4.1 作業ステップ

#### Step 0: NullableMoney基底クラスの作成とWithdrewKingakuのリファクタリング

**対象**: NullableMoney.java（新規作成）、WithdrewKingaku.java（リファクタリング）

**作業内容**:
1. NullableMoney基底クラスを作成（commonパッケージ）
   - null許容のvalueフィールド
   - null安全なadd/subtract/multiplyメソッド
   - toFormatString()メソッド（null時は空文字返却）
   - toString()メソッド（デバッグ用）
   - validate()メソッド（スケール、マイナス値チェック）
2. WithdrewKingakuをNullableMoney継承に移行
   - 重複コード削除
   - 基底クラスメソッド活用
3. WithdrewKingaku既存テスト94件で回帰テスト実行

**注意**: NullableMoneyは抽象クラスのため、直接テストクラスは作成しません。WithdrewKingakuなどの継承クラスのテストで間接的にNullableMoneyの機能を検証します。

**想定工数**: 1.5-2時間

#### Step 1: SisyutuYoteiKingaku、ShiharaiKingakuのMoney継承移行

**対象**: パターン1適用

**作業内容**:
1. Money基底クラス継承
2. テストクラス作成（各15-20テスト想定）
3. UseCase層の修正確認

**想定工数**: 2-3時間

#### Step 2: SisyutuKingakuB、SisyutuKingakuCのNullableMoney継承移行

**対象**: パターン2適用

**作業内容**:
1. NullableMoney基底クラスを継承
2. 重複コード削除（value、add/subtract等）
3. 専用メソッド維持（toSisyutuKingakuBString()等）
4. テストクラス作成（各20-25テスト想定）

**Phase1統合済み作業**:
- ~~getPercentage()メソッドの引数をExpenditureAmountに変更~~ （完了済み）

**想定工数**: 2時間（NullableMoney継承により重複コード削減）

#### Step 3: SisyutuKingakuBCのAPI統一

**対象**: パターン3適用

**作業内容**:
1. toFormatString()追加
2. テストクラス作成（20テスト想定）

**Phase1統合済み作業**:
- ~~getPercentage()メソッドの引数をExpenditureAmountに変更~~ （完了済み）

**想定工数**: 1時間（Phase1統合により作業量削減）

### 4.2 総工数見積もり

| ステップ | 対象クラス数 | 想定工数 | 備考 |
|---------|------------|---------|------|
| Step 0 | 1（+WithdrewKingakuリファクタ） | 1.5-2時間 | NullableMoney新規作成 |
| Step 1 | 2 | 2-3時間 | Money継承 |
| Step 2 | 2 | 2時間 | NullableMoney継承 |
| Step 3 | 1 | 1時間 | API統一のみ |
| **合計** | **6** | **6.5-8時間** | |

**Phase1統合による削減**:
- SyuusiKingakuの削除により1クラス削減（1時間削減）
- SisyutuKingakuB/C/BCのgetPercentage()メソッド更新作業が不要（0.5-1時間削減）

**NullableMoney導入による追加工数**:
- NullableMoney基底クラス作成（+1.5-2時間）
- WithdrewKingakuリファクタリング（Step 0に含む）
- 長期的にはnull許容金額クラスの保守性が向上

---

## 5. 期待される成果

### 5.1 定量的成果

| 指標 | Phase3 Step1完了時 | Phase1統合完了時 | Phase3 Step2完了後（予想） | 改善 |
|------|------------------|----------------|------------------------|------|
| Money継承完了クラス数 | 4クラス | 7クラス（Phase1共通クラス含む） | 9クラス | +2クラス（Step2で） |
| NullableMoney継承完了クラス数 | 0 | 0 | 3クラス | +3クラス（新規） |
| 基底クラス数 | 1（Money） | 1（Money） | 2（Money、NullableMoney） | +1クラス |
| API統一完了クラス数 | 5クラス（WithdrewKingaku含む） | 8クラス（Phase1共通クラス含む） | 11クラス | +3クラス（Step2で） |
| テストクラス作成数 | 5クラス | 8クラス（Phase1共通クラステスト含む） | 13クラス | +5クラス（Step2で） |
| テストケース数 | 94 | 約140（Phase1統合テスト含む） | 235-260（想定） | +95-120（Step2で） |
| 削除されたクラス数 | 0 | 3クラス（SyuunyuuKingaku, SisyutuKingaku, SyuusiKingaku） | - | -3クラス（重複排除） |

**Phase1統合による追加成果**:
- inquiry固有クラス3つをPhase1共通クラスに統合（コード重複排除）
- Phase1共通クラス（IncomeAmount、ExpenditureAmount、BalanceAmount）はより堅牢な実装を提供
- Step2対象クラスのgetPercentage()メソッドが既にPhase1クラスを使用（部分的に作業完了）

**NullableMoney導入による追加成果**:
- null許容金額クラス3つ（WithdrewKingaku、SisyutuKingakuB/C）のnull安全演算ロジックを基底クラスに集約
- null処理の重複コードを約60-80行削減（見込み）
- 将来的なnull許容金額クラスの実装が容易に

### 5.2 定性的成果

1. **コード品質向上**: Money/NullableMoney継承により重複コード排除
2. **API統一**: すべての金額系クラスでtoFormatString()を提供
3. **保守性向上**: 基底クラスメソッド活用によりメンテナンス箇所削減
4. **ドメイン凝集度向上**: DomainCommonUtils依存を排除
5. **テストカバレッジ向上**: 包括的なテストスイート構築
6. **Phase1統合による追加成果**: inquiry固有クラスの削減とPhase1共通クラスへの統合
7. **NullableMoney導入による追加成果**: null許容金額クラスの統一的な設計と保守性向上

---

## 6. リスクと対策

### 6.1 リスク

| No | リスク | 影響度 | 発生確率 | 対策 | NullableMoney導入後の状況 |
|----|-------|--------|---------|------|------------------------|
| 1 | WithdrewKingakuリファクタリングの影響範囲 | 中 | 中 | 既存94テストで回帰確認 | 新規リスク |
| 2 | NullableMoneyとMoneyの2つの基底クラスによる設計複雑化 | 中 | 低 | 明確な使い分けルールを文書化 | 新規リスク |
| 3 | SisyutuKingakuBCの複合オブジェクト設計がMoney継承を困難にする | 高 | 高 | API統一のみ実施（パターン3） | 変更なし |
| 4 | UseCase層での影響範囲が広い | 中 | 低 | 事前に使用箇所を調査、テスト実行 | Phase1統合で影響範囲を確認済み（リスク低減） |
| 5 | 既存の専用メソッド名を使用している箇所がある | 中 | 中 | 互換性のため専用メソッドを残す | 変更なし |
| 6 | Phase1統合との整合性確保 | 中 | 低 | Phase1クラスとの連携を確認 | 変更なし |

**Phase1統合により解消されたリスク**:
- ~~SyuusiKingakuのマイナス値許容がMoney継承を困難にする~~（SyuusiKingakuは削除され、BalanceAmountに置換済み）

**NullableMoney導入により解消されたリスク**:
- ~~SisyutuKingakuB/Cのnull値許容がMoney継承を困難にする~~（NullableMoney継承により解決）

### 6.2 対策

1. **段階的移行**: パターンごとにステップ分割
2. **包括的テスト**: 各クラスで15-25テストケース作成
3. **後方互換性維持**: 専用メソッド名を残す
4. **統合テスト**: UseCase層のテストで影響確認
5. **Phase1統合成果の活用**: Phase1共通クラスとの整合性を確保
6. **基底クラス使い分けルール明確化**: Money（null非許容）、NullableMoney（null許容）の使い分けを文書化

---

## 7. 次のステップ

Phase 3 Step 2完了後の展開:

1. **Phase 3 Step 3**: 日付系クラスのDateValue継承移行
   - ShiharaiDate
   - SisyutuShiharaiDate

2. **Phase 4以降**: その他のドメインクラスの基底クラス継承移行
   - regist関連クラス
   - event関連クラス

---

## 8. 実施結果（2025-12-25完了）

### 8.1 実施概要

Phase 3 Step 2のすべての作業が2025年12月21日～25日にかけて完了しました。

**実施期間**: 2025-12-21 ～ 2025-12-25（5日間）

### 8.2 実施内容

#### Step 0: NullableMoneyとWithdrewKingakuのリファクタリング（2025-12-21完了）

**完了内容**:
- ✅ NullableMoney基底クラス作成（commonパッケージ）
- ✅ WithdrewKingakuのNullableMoney継承移行
- ✅ 既存94テストすべて成功

**成果**:
- null安全演算ロジックを基底クラスに集約
- コード重複を約80行削減

#### Step 1: SisyutuYoteiKingaku、ShiharaiKingakuのMoney継承移行（2025-12-21完了）

**完了内容**:
- ✅ SisyutuYoteiKingaku: Money継承、テスト26件作成
- ✅ ShiharaiKingaku: Money継承、テスト17件作成
- ✅ すべてのテスト成功

**成果**:
- Money基底クラスのバリデーションとフォーマット機能を活用
- DomainCommonUtils依存を排除

#### Step 2: SisyutuKingakuB、SisyutuKingakuCのNullableMoney継承移行（2025-12-23完了）

**完了内容**:
- ✅ SisyutuKingakuB: NullableMoney継承、テスト29件作成
- ✅ SisyutuKingakuC: NullableMoney継承、テスト29件作成
- ✅ toString()削除、toFormatString()への移行完了
- ✅ toSisyutuKingakuBString()/toSisyutuKingakuCString()を@Deprecatedに変更
- ✅ UseCase層の呼び出し箇所をtoFormatString()に変更
- ✅ すべてのテスト成功

**成果**:
- null安全演算ロジックの重複を排除
- API統一により基底クラスのtoFormatString()を使用

#### Step 3: SisyutuKingakuBCのNullableMoney継承移行（2025-12-25完了）

**完了内容**:
- ✅ SisyutuKingakuBC: NullableMoney継承、テスト20件作成
- ✅ toString()削除、toFormatString()への移行完了
- ✅ toSisyutuKingakuBCString()を@Deprecatedに変更
- ✅ UseCase層の呼び出し箇所をtoFormatString()に変更（3箇所）
- ✅ すべてのテスト成功

**成果**:
- 複合ドメインオブジェクトでもNullableMoney継承により統一的な設計を実現
- BとCの合計値計算ロジックを維持しつつ、基底クラスの機能を活用

### 8.3 最終成果

#### 定量的成果

| 指標 | 計画値 | 実績値 | 達成率 |
|------|--------|--------|--------|
| Money継承完了クラス数 | 2クラス | 2クラス | 100% |
| NullableMoney継承完了クラス数 | 3クラス | 4クラス（WithdrewKingaku含む） | 133% |
| テストクラス作成数 | 5クラス | 5クラス | 100% |
| テストケース数 | 95-120件 | 121件（26+17+29+29+20） | 101-127% |
| 削減コード行数 | 60-80行 | 約100行 | 125-167% |
| 実施工数 | 6.5-8時間 | 約7時間 | 適正範囲内 |

**新規作成クラス**:
- NullableMoney.java（基底クラス）
- SisyutuYoteiKingakuTest.java（26テスト）
- ShiharaiKingakuTest.java（17テスト）
- SisyutuKingakuBTest.java（29テスト）
- SisyutuKingakuCTest.java（29テスト）
- SisyutuKingakuBCTest.java（20テスト）

**リファクタリング完了クラス**:
- WithdrewKingaku.java（NullableMoney継承）
- SisyutuYoteiKingaku.java（Money継承）
- ShiharaiKingaku.java（Money継承）
- SisyutuKingakuB.java（NullableMoney継承）
- SisyutuKingakuC.java（NullableMoney継承）
- SisyutuKingakuBC.java（NullableMoney継承）

#### 定性的成果

1. **コード品質向上**: Money/NullableMoney継承により重複コード排除完了
2. **API統一**: すべての金額系クラスでtoFormatString()を提供
3. **保守性向上**: 基底クラスメソッド活用によりメンテナンス箇所削減
4. **ドメイン凝集度向上**: DomainCommonUtils依存を完全排除
5. **テストカバレッジ向上**: 包括的なテストスイート構築完了（全215テスト成功）
6. **null安全性向上**: NullableMoney導入により統一的なnull処理を実現

### 8.4 課題と対策

**実施中に発生した課題**:

| 課題 | 対策 | 結果 |
|------|------|------|
| toString()の期待値相違 | 基底クラスの仕様に合わせてテストを修正 | 解決 |
| toFormatString()のnull値表示 | NullableMoneyは空文字列""を返す仕様に統一 | 解決 |
| UseCase層への影響範囲 | 呼び出し箇所を検索して全箇所修正 | 解決 |

**今後の注意点**:
- 専用メソッド（toSisyutuKingakuBString()等）は@Deprecatedのため、新規コードでは使用しない
- 既存コードは段階的にtoFormatString()へ移行推奨

---

## 9. 更新履歴

| 版数 | 更新日 | 更新内容 |
|------|--------|---------|
| 1.00.00 | 2025-12-16 | 初版作成 |
| 1.01.00 | 2025-12-21 | Phase1クラス統合による影響を反映。SyuusiKingakuを対象外に変更、対象クラス数を6→5に削減、工数見積もりを更新 |
| 1.02.00 | 2025-12-22 | NullableMoney基底クラス導入を決定。対象クラス数を5→6に変更（NullableMoney追加）、WithdrewKingakuをリファクタリング対象に追加、SisyutuKingakuB/CをNullableMoney継承に変更、工数見積もりを6.5-8時間に更新 |
| 2.00.00 | 2025-12-25 | **Phase 3 Step 2完了**。実施結果セクションを追加。全6クラスの移行完了、121テストケース作成、すべてのテスト成功 |

---

**作成日**: 2025-12-16
**最終更新日**: 2025-12-25
**作成者**: DDD Refactoring Project Phase 3
**ステータス**: ✅ 完了（2025-12-25）
