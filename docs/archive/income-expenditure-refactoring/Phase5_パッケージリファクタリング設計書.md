# パッケージリファクタリング設計書

## 1. 概要

Phase 5 で実施した収支登録UseCaseのリファクタリングにより、
プロジェクト全体のパッケージ分類に複数の問題が明らかになりました。
本設計書では、DDDの原則に従ったパッケージ再編の方針と、リファクタリング後の
全パッケージ構造・クラス配置・移動元パッケージを記載します。

---

## 2. 現状の課題

### 課題1: `domain/type/account/inquiry/` が catch-all になっている

現在 36 個のドメインタイプが `inquiry` サブパッケージに集中している。
しかし多くのクラスは照会専用ではなく、登録（Registration）UseCaseでも使用されるため
「inquiry」という分類が不適切です。

Phase 5 の IncomeAndExpenditureRegistConfirmUseCase では以下の `inquiry` パッケージのクラスを使用:
- `SisyutuCode`, `SisyutuItemCode`, `SisyutuKubun`, `ExpectedExpenditureAmount`, `WithdrawingAmount`

### 課題2: ドメインタイプのクラス名に日本語ローマ字が残っている

Phase 1-4 で `SisyutuKingaku → ExpenditureAmount` のリネームを実施しましたが、
`SisyutuCode`, `SisyutuItemCode`, `SisyutuKubun` など多数のクラスが日本語ローマ字名のままです。

### 課題3: リポジトリインターフェースが `inquiry/` に誤分類

`ExpenditureTableRepository`, `IncomeTableRepository` 等のCRUD操作を持つリポジトリが
`domain/repository/account/inquiry/` に配置されており、登録機能からも使用されています。

### 課題4: プレゼンテーション層のFormクラスが誤分類

登録画面で使用する `IncomeItemForm`, `ExpenditureItemForm` 等が
`presentation/request/account/inquiry/` に配置されています。

### 課題5: `application/usecase/account/regist/` に異なるビジネス機能が混在

収支登録（IncomeAndExpenditure系 5クラス）と買い物登録（Shopping系 3クラス）が
同じ `regist/` パッケージに混在しています。
UseCase パッケージは Presentation 層の Controller 配下の URL 構造に合わせて作成されたため、
ビジネス機能の分類としては不適切です。

### 課題6: `common/component/` に Application 層のコンポーネントが誤配置

`common/component/` パッケージに、リポジトリを呼び出しビジネスロジックを担う
`@Component` クラスが 5 件配置されています。
`common/` は「インフラ横断的な関心事（例外、定数、ユーティリティ等）」を置く場所であり、
Application 層のビジネス処理を担うコンポーネントは `application/usecase/` 配下に置くべきです。

| クラス | 問題 |
|--------|------|
| `AccountBookUserInquiryUseCase` | `account`・`itemmanage`・presentation 層全体で使用するアプリ全体共通コンポーネント |
| `CodeTableItemComponent` | `account`・`itemmanage` 両方で使用するアプリ全体共通コンポーネント |
| `SisyutuItemComponent` | `account`・`itemmanage` 両方で使用するアプリ全体共通コンポーネント |
| `SisyutuKingakuItemHolderComponent` | `account/` 配下のみで使用するコンポーネント |
| `IncomeAndExpenditureRegistListComponent` | `incomeandexpenditure/` のみで使用するコンポーネント |

---

## 3. リファクタリングの方針

### Step A（Claude Code実施）: ドメインタイプのリネーム

- `Sisyutu*` → `Expenditure*` への統一（Phase 1-4 の `SisyutuKingaku → ExpenditureAmount` と同方針）
- `Syuunyuu*` → `Income*` への統一
- リネーム後にドメインタイプのテストクラスを作成

### Step B（Eclipse実施）: パッケージの移動

- `domain/type/account/inquiry/` を機能別サブパッケージに再編（Step A リネーム後に実施）
- `domain/model/account/inquiry/` を機能別サブパッケージに再編
- `domain/repository/account/inquiry/` の `inquiry` レベルを廃止
- `infrastructure/datasource|mapper|dto/account/inquiry/` を対応するパッケージに移動
- `presentation/request/account/inquiry/` の一部クラスを `regist/` へ移動
- `application/usecase/account/regist/` を `incomeandexpenditure/` と `shoppingregist/` に分割
- `application/usecase/account/component/` を新規作成し account 共通コンポーネントを集約
- `application/usecase/common/` を新規作成しアプリ全体共通コンポーネントを移動
- `common/component/` の全クラスを上記 application 層へ移動後、パッケージを削除

---

## 4. Step A: リネーム一覧（Claude Code実施）

### 4-1. domain/type リネーム

| No | リネーム前 | リネーム後 | 意味 |
|---|---|---|---|
| 1 | `SisyutuCode` | `ExpenditureCode` | 支出コード（支出テーブルの識別コード） |
| 2 | `SisyutuKubun` | `ExpenditureCategory` | 支出区分（無駄遣いなし/軽度/重度） |
| 3 | `SisyutuDetailContext` | `ExpenditureDetailContext` | 支出詳細内容 |
| 4 | `SisyutuName` | `ExpenditureName` | 支出名称 |
| 5 | `SisyutuItemCode` | `ExpenditureItemCode` | 支出項目コード（支出項目テーブルの識別コード） |
| 6 | `SisyutuItemName` | `ExpenditureItemName` | 支出項目名 |
| 7 | `SisyutuItemSort` | `ExpenditureItemSortOrder` | 支出項目の表示順序 |
| 8 | `SisyutuItemLevel` | `ExpenditureItemLevel` | 支出項目の階層レベル（1〜5） |
| 9 | `SisyutuItemDetailContext` | `ExpenditureItemDetailContext` | 支出項目詳細内容 |
| 10 | `ParentSisyutuItemCode` | `ParentExpenditureItemCode` | 親支出項目コード |
| 11 | `SyuunyuuCode` | `IncomeCode` | 収入コード（収入テーブルの識別コード） |
| 12 | `SyuunyuuKubun` | `IncomeCategory` | 収入区分（通常/積立金取崩/ボーナス等） |
| 13 | `SyuunyuuDetailContext` | `IncomeDetailContext` | 収入詳細内容 |

### 4-2. domain/model リネーム（Step Bと合わせて実施）

| No | リネーム前 | リネーム後 | 備考 |
|---|---|---|---|
| 1 | `SisyutuItem` | `ExpenditureItemInfo` | `ExpenditureItem`（支出レコード）との衝突回避のため `Info` を付加 |
| 2 | `SisyutuItemInquiryList` | `ExpenditureItemInfoInquiryList` | 同上 |
| 3 | `SisyutuKingakuItem` | `ExpenditureAmountItem` | 支出金額テーブル1レコード |
| 4 | `SisyutuKingakuItemHolder` | `ExpenditureAmountItemHolder` | 支出金額テーブルレコード群のホルダー |
| 5 | `SisyutuKingakuItemInquiryList` | `ExpenditureAmountItemInquiryList` | 支出金額テーブルの照会結果リスト |

### 4-3. domain/repository リネーム（Step Bと合わせて実施）

| No | リネーム前 | リネーム後 |
|---|---|---|
| 1 | `SisyutuItemTableRepository` | `ExpenditureItemTableRepository` |
| 2 | `SisyutuKingakuTableRepository` | `ExpenditureAmountTableRepository` |

### 4-4. infrastructure リネーム（Step Bと合わせて実施）

| No | リネーム前 | リネーム後 | 種別 |
|---|---|---|---|
| 1 | `SisyutuItemTableDataSource` | `ExpenditureItemTableDataSource` | datasource |
| 2 | `SisyutuKingakuTableDataSource` | `ExpenditureAmountTableDataSource` | datasource |
| 3 | `SisyutuItemTableMapper` | `ExpenditureItemTableMapper` | mapper |
| 4 | `SisyutuKingakuTableMapper` | `ExpenditureAmountTableMapper` | mapper |
| 5 | `SisyutuItemReadWriteDto` | `ExpenditureItemReadWriteDto` | dto |
| 6 | `SisyutuKingakuReadWriteDto` | `ExpenditureAmountReadWriteDto` | dto |
| 7 | `SisyutuKingakuAndSisyutuItemReadDto` | `ExpenditureAmountAndItemReadDto` | dto |

---

## 5. リファクタリング後のパッケージ設計

### 5-1. domain/type/account/

#### 変更後の全体構造

```
domain/type/account/
├── event/                    ← 変更なし
├── expenditure/              ← 新規作成（支出テーブル関連型）
├── expenditureinfo/          ← 新規作成（支出項目テーブル関連型）
├── fixedcost/                ← 型追加（ShiharaiKingaku → FixedCostPaymentAmount）
├── income/                   ← 新規作成（収入テーブル関連型）
├── incomeandexpenditure/     ← 新規作成（収支テーブル関連型）
├── inquiry/                  ← 縮小（照会専用集計・分析型のみ残す）
├── shop/                     ← 変更なし
├── shoppingitem/             ← 変更なし
└── shoppingregist/           ← 変更なし
```

---

#### 5-1-1. domain/type/account/expenditure/（新規）

支出テーブル（SISYUTU_TABLE）に対応するドメインタイプ。

| クラス名（リネーム後） | リネーム前 | 移動元パッケージ | 概要 |
|---|---|---|---|
| `ExpenditureCode` | `SisyutuCode` | `inquiry` | 支出コード（3桁0パディング数値）。採番・バリデーション機能あり |
| `ExpenditureCategory` | `SisyutuKubun` | `inquiry` | 支出区分（1:通常, 2:無駄遣い軽度, 3:重度）。`isWastedBOrC()`判定メソッド実装 |
| `ExpenditureDetailContext` | `SisyutuDetailContext` | `inquiry` | 支出詳細内容（テキスト） |
| `ExpenditureName` | `SisyutuName` | `inquiry` | 支出名称 |
| `ExpenditureId` | （変更なし） | `inquiry` | 支出コードのIdentifier基底クラス実装 |
| `EnableUpdateFlg` | （変更なし） | `inquiry` | 更新可否フラグ（true=更新可能） |

---

#### 5-1-2. domain/type/account/expenditureinfo/（新規）

支出項目テーブル（SISYUTU_ITEM_TABLE）に対応するドメインタイプ。

| クラス名（リネーム後） | リネーム前 | 移動元パッケージ | 概要 |
|---|---|---|---|
| `ExpenditureItemCode` | `SisyutuItemCode` | `inquiry` | 支出項目コード（4桁0パディング数値）。採番・バリデーション・親コード変換機能あり |
| `ExpenditureItemName` | `SisyutuItemName` | `inquiry` | 支出項目名 |
| `ExpenditureItemSortOrder` | `SisyutuItemSort` | `inquiry` | 支出項目の表示順序 |
| `ExpenditureItemLevel` | `SisyutuItemLevel` | `inquiry` | 支出項目の階層レベル（1〜5） |
| `ExpenditureItemDetailContext` | `SisyutuItemDetailContext` | `inquiry` | 支出項目の詳細内容 |
| `ParentExpenditureItemCode` | `ParentSisyutuItemCode` | `inquiry` | 親支出項目コード（4桁0パディング数値） |
| `ExpenditureCategoryId` | （変更なし）※要確認 | `inquiry` | 支出項目コードのIdentifier基底クラス実装。`ExpenditureItemId`へのリネームも検討 |

> **注記**: `ExpenditureCategoryId` は「支出項目コードのIdentifier」であるため、
> `ExpenditureItemId` へのリネームが命名一貫性の観点で望ましい（任意）。

---

#### 5-1-3. domain/type/account/income/（新規）

収入テーブル（SYUUNYUU_TABLE）に対応するドメインタイプ。

| クラス名（リネーム後） | リネーム前 | 移動元パッケージ | 概要 |
|---|---|---|---|
| `IncomeCode` | `SyuunyuuCode` | `inquiry` | 収入コード（2桁0パディング数値）。採番・バリデーション機能あり |
| `IncomeCategory` | `SyuunyuuKubun` | `inquiry` | 収入区分（1:通常収入, 2:その他, 3:積立金取崩, 4:ボーナス等） |
| `IncomeDetailContext` | `SyuunyuuDetailContext` | `inquiry` | 収入詳細内容（テキスト） |
| `IncomeId` | （変更なし） | `inquiry` | 収入コードのIdentifier基底クラス実装 |

---

#### 5-1-4. domain/type/account/incomeandexpenditure/（新規）

収支テーブル（INCOME_AND_EXPENDITURE_TABLE）に対応するドメインタイプ。
照会・登録の両機能で使用するため `inquiry/` から分離。

| クラス名 | 移動元パッケージ | 概要 |
|---|---|---|
| `BalanceTotalAmount` | `inquiry` | 収支金額合計（収入合計－支出合計。マイナス値許可） |
| `ExpectedExpenditureAmount` | `inquiry` | 支出予定金額（収支テーブルの1項目。登録UseCaseでも使用） |
| `ExpectedExpenditureTotalAmount` | `inquiry` | 支出予定金額合計 |
| `ExpenditureTotalAmount` | `inquiry` | 支出金額合計 |
| `RegularIncomeTotalAmount` | `inquiry` | 通常収入金額合計（収入区分1,2,4の合算。積立金取崩除外） |
| `TotalAvailableFunds` | `inquiry` | 利用可能資金合計（通常収入＋積立金取崩） |
| `WithdrawingAmount` | `inquiry` | 積立金取崩金額（収入区分3の収入額。登録UseCaseでも使用） |
| `WithdrawingTotalAmount` | `inquiry` | 積立金取崩金額合計 |

---

#### 5-1-5. domain/type/account/inquiry/（縮小・残存）

照会機能（AccountMonthInquiry, AccountYearInquiry）専用の集計・分析・表示用ドメインタイプ。
登録機能では使用しないため `inquiry/` に留まる。

| クラス名 | 概要 |
|---|---|
| `InsyokuNitiyouhinKingaku` | 飲食日用品カテゴリ支出金額（年次照会の内訳集計） |
| `IruiJyuukyoSetubiKingaku` | 衣類住居設備カテゴリ支出金額（年次照会の内訳集計） |
| `JigyouKeihiKingaku` | 事業経費カテゴリ支出金額（年次照会の内訳集計） |
| `KoteiHikazeiKingaku` | 固定（非課税）カテゴリ支出金額（年次照会の内訳集計） |
| `KoteiKazeiKingaku` | 固定（課税）カテゴリ支出金額（年次照会の内訳集計） |
| `SyumiGotakuKingaku` | 趣味娯楽カテゴリ支出金額（年次照会の内訳集計） |
| `MinorWasteExpenditure` | 無駄遣い（軽度）支出金額。割合計算機能あり |
| `SevereWasteExpenditure` | 無駄遣い（重度）支出金額。割合計算機能あり |
| `TotalWasteExpenditure` | 無駄遣い合計支出金額。月次集計。軽度・重度の内訳保持 |
| `WasteExpenditureTotalAmount` | 無駄遣い支出金額年間合計 |

---

#### 5-1-6. domain/type/account/fixedcost/（型追加・リネームあり）

固定費テーブル（FIXED_COST_TABLE）に対応するドメインタイプ。既存クラスは変更なし。
`inquiry/` に誤配置されていた `ShiharaiKingaku` をリネームのうえ移動する。

| 操作 | クラス名（変更後） | 変更前 | 概要 |
|---|---|---|---|
| リネーム＆移動 | `FixedCostPaymentAmount` | `ShiharaiKingaku`（`inquiry/`） | 固定費の支払金額（0以上のMoney型）。`add()` メソッドあり |

**パッケージ選定理由**:
- `ShiharaiKingaku` の使用箇所は `FixedCost`（ドメインモデル）、`FixedCostInquiryList`（ドメインモデル）、`FixedCostTableDataSource`（インフラ）が中心
- `IncomeAndExpenditureInitUseCase` での使用は固定費テーブルの読み込みに伴うものであり、概念のオーナーは固定費ドメイン
- 汎用性はないため `domain.type.common` には置かない

---

### 5-2. domain/model/account/

#### 変更後の全体構造

```
domain/model/account/
├── event/                ← 変更なし
├── expenditure/          ← 新規（支出テーブル関連モデル）
├── expenditureinfo/      ← 新規（支出項目テーブル関連モデル）
├── fixedcost/            ← 変更なし
├── income/               ← 新規（収入テーブル関連モデル）
├── incomeandexpenditure/ ← 新規（収支テーブル関連モデル）
├── inquiry/              ← 縮小（照会専用リスト・集約のみ残す）
├── searchquery/          ← 変更なし
├── shop/                 ← 変更なし
├── shoppingitem/         ← 変更なし
└── shoppingregist/       ← 変更なし
```

---

#### 5-2-1. domain/model/account/expenditure/（新規）

| クラス名（リネーム後） | リネーム前 | 移動元パッケージ | 概要 |
|---|---|---|---|
| `ExpenditureItem` | （変更なし） | `inquiry` | 支出テーブル1レコードのドメインモデル |
| `ExpenditureAmountItem` | `SisyutuKingakuItem` | `inquiry` | 支出金額テーブル1レコードのドメインモデル |
| `ExpenditureAmountItemHolder` | `SisyutuKingakuItemHolder` | `inquiry` | 支出金額テーブルレコード群のホルダー（登録UseCaseで使用） |
| `ExpenditureAmountItemInquiryList` | `SisyutuKingakuItemInquiryList` | `inquiry` | 支出金額テーブルの照会結果リスト |

---

#### 5-2-2. domain/model/account/expenditureinfo/（新規）

| クラス名（リネーム後） | リネーム前 | 移動元パッケージ | 概要 |
|---|---|---|---|
| `ExpenditureItemInfo` | `SisyutuItem` | `inquiry` | 支出項目テーブル1レコードのドメインモデル（マスタ）。`ExpenditureItem`（支出レコード）との名称衝突回避のため`Info`付加 |
| `ExpenditureItemInfoInquiryList` | `SisyutuItemInquiryList` | `inquiry` | 支出項目マスタの照会結果リスト |

---

#### 5-2-3. domain/model/account/income/（新規）

| クラス名 | 移動元パッケージ | 概要 |
|---|---|---|
| `IncomeItem` | `inquiry` | 収入テーブル1レコードのドメインモデル |
| `IncomeItemInquiryList` | `inquiry` | 収入テーブルの照会結果リスト |

---

#### 5-2-4. domain/model/account/incomeandexpenditure/（新規）

| クラス名 | 移動元パッケージ | 概要 |
|---|---|---|
| `IncomeAndExpenditure` | `inquiry` | 収支テーブル1レコードのドメインモデル |
| `IncomeAndExpenditureItem` | `inquiry` | 収支テーブルの集約モデル（照会・登録両用） |
| `IncomeAndExpenditureInquiryList` | `inquiry` | 収支テーブルの照会結果リスト |

---

#### 5-2-5. domain/model/account/inquiry/（縮小・残存）

照会機能専用の集計・表示用モデル。

| クラス名 | 概要 |
|---|---|
| `AccountMonthInquiryExpenditureItemList` | 月次照会画面の支出項目リスト集計モデル |
| `AccountYearMeisaiInquiryList` | 年間明細照会リスト集計モデル |
| `ExpenditureItemInquiryList` | 支出テーブルの照会結果リスト（照会専用） |

---

### 5-3. domain/repository/account/

#### 変更後の全体構造

`inquiry/` サブパッケージを廃止し、全リポジトリを `account/` 直下に移動。

```
domain/repository/account/
├── event/           ← 変更なし
├── fixedcost/       ← 変更なし
├── shop/            ← 変更なし
├── shoppingitem/    ← 変更なし
├── shoppingregist/  ← 変更なし
│
│ ←── 以下は inquiry/ から移動（inquiry/ を廃止）
├── ExpenditureTableRepository.java
├── ExpenditureItemTableRepository.java       ← SisyutuItemTableRepository をリネーム
├── ExpenditureAmountTableRepository.java     ← SisyutuKingakuTableRepository をリネーム
├── IncomeAndExpenditureTableRepository.java
└── IncomeTableRepository.java
```

#### 移動クラス一覧

| クラス名（リネーム後） | リネーム前 | 移動元パッケージ | 概要 |
|---|---|---|---|
| `ExpenditureTableRepository` | （変更なし） | `inquiry` | 支出テーブルCRUD |
| `ExpenditureItemTableRepository` | `SisyutuItemTableRepository` | `inquiry` | 支出項目テーブルCRUD |
| `ExpenditureAmountTableRepository` | `SisyutuKingakuTableRepository` | `inquiry` | 支出金額テーブルCRUD |
| `IncomeAndExpenditureTableRepository` | （変更なし） | `inquiry` | 収支テーブルCRUD |
| `IncomeTableRepository` | （変更なし） | `inquiry` | 収入テーブルCRUD |

---

### 5-4. infrastructure/datasource/account/

`inquiry/` サブパッケージを廃止。リポジトリと同様に `account/` 直下に移動。

| クラス名（リネーム後） | リネーム前 | 移動元パッケージ |
|---|---|---|
| `ExpenditureTableDataSource` | （変更なし） | `inquiry` |
| `ExpenditureItemTableDataSource` | `SisyutuItemTableDataSource` | `inquiry` |
| `ExpenditureAmountTableDataSource` | `SisyutuKingakuTableDataSource` | `inquiry` |
| `IncomeAndExpenditureTableDataSource` | （変更なし） | `inquiry` |
| `IncomeTableDataSource` | （変更なし） | `inquiry` |

---

### 5-5. infrastructure/mapper/account/

`inquiry/` サブパッケージを廃止。`account/` 直下に移動。

| クラス名（リネーム後） | リネーム前 | 移動元パッケージ |
|---|---|---|
| `ExpenditureTableMapper` | （変更なし） | `inquiry` |
| `ExpenditureItemTableMapper` | `SisyutuItemTableMapper` | `inquiry` |
| `ExpenditureAmountTableMapper` | `SisyutuKingakuTableMapper` | `inquiry` |
| `IncomeAndExpenditureTableMapper` | （変更なし） | `inquiry` |
| `IncomeTableMapper` | （変更なし） | `inquiry` |

---

### 5-6. infrastructure/dto/account/

`inquiry/` サブパッケージを廃止。DTO を内容に応じて再配置。

#### 変更後の全体構造

```
infrastructure/dto/account/
├── event/           ← 変更なし
├── expenditure/     ← 新規（支出テーブルDTO）
├── expenditureinfo/ ← 新規（支出項目テーブルDTO）
├── fixedcost/       ← 変更なし
├── income/          ← 新規（収入テーブルDTO）
├── incomeandexpenditure/ ← 新規（収支テーブルDTO）
├── inquiry/         ← 縮小（照会専用DTOのみ残す）
├── shop/            ← 変更なし
├── shoppingitem/    ← 変更なし
└── shoppingregist/  ← 変更なし
```

#### 移動クラス一覧

| クラス名（リネーム後） | リネーム前 | 移動後パッケージ | 概要 |
|---|---|---|---|
| `ExpenditureReadWriteDto` | （変更なし） | `expenditure` | 支出テーブル用ReadWrite DTO |
| `ExpenditureAmountReadWriteDto` | `SisyutuKingakuReadWriteDto` | `expenditure` | 支出金額テーブル用ReadWrite DTO |
| `ExpenditureAmountAndItemReadDto` | `SisyutuKingakuAndSisyutuItemReadDto` | `expenditure` | 支出金額＋支出項目の結合照会用 DTO |
| `ExpenditureItemReadWriteDto` | `SisyutuItemReadWriteDto` | `expenditureitem` | 支出項目テーブル用ReadWrite DTO |
| `IncomeReadWriteDto` | （変更なし） | `income` | 収入テーブル用ReadWrite DTO |
| `IncomeAndExpenditureReadWriteDto` | （変更なし） | `incomeandexpenditure` | 収支テーブル用ReadWrite DTO |
| `AccountYearMeisaiInquiryReadDto` | （変更なし） | `inquiry`（残存） | 年次明細照会専用の読み込み DTO |

---

### 5-7. presentation/request/account/

登録フォームクラスを `inquiry/` から `regist/` に移動。

#### 移動クラス一覧

| クラス名 | 移動元パッケージ | 移動後パッケージ | 概要 |
|---|---|---|---|
| `IncomeItemForm` | `inquiry` | `regist` | 収入登録・更新・削除フォーム |
| `ExpenditureItemForm` | `inquiry` | `regist` | 支出登録・更新・削除フォーム |
| `ExpenditureSelectItemForm` | `inquiry` | `regist` | 支出項目選択フォーム（登録フローで使用） |
| `SimpleShoppingRegistInfoForm` | `inquiry` | `regist` | 簡易買い物登録フォーム |

> **注記**: 移動後、`presentation/request/account/inquiry/` は空になるため削除。

---

### 5-8. application/usecase/ の再編

#### 変更後の全体構造

```
application/usecase/
├── account/
│   ├── inquiry/                  ← 変更なし（AccountMonthInquiry, AccountYearInquiry）
│   ├── incomeandexpenditure/     ← 新規（regist/ から収支登録系を分離）
│   ├── shoppingregist/           ← 新規（regist/ から買い物登録系を分離）
│   └── component/                ← 新規（account 共通コンポーネント）
├── adminmenu/                    ← 変更なし
├── itemmanage/                   ← 変更なし
└── common/                       ← 新規（アプリ全体共通コンポーネント）
```

> `regist/` パッケージは全クラス移動後に削除。
> `common/component/` パッケージは全クラス移動後に削除。

---

#### 5-8-1. application/usecase/account/incomeandexpenditure/（新規）

| クラス名 | 移動元 | 概要 |
|---------|--------|------|
| `IncomeAndExpenditureInitUseCase` | `account/regist` | 収支登録画面の初期化 |
| `IncomeRegistUseCase` | `account/regist` | 収入登録セッション操作 |
| `ExpenditureRegistUseCase` | `account/regist` | 支出登録セッション操作 |
| `ExpenditureItemSelectUseCase` | `account/regist` | 支出項目選択画面 |
| `IncomeAndExpenditureRegistConfirmUseCase` | `account/regist` | 登録確認・DB更新 |
| `IncomeAndExpenditureRegistListComponent` | `common/component` | 収支登録リスト共通処理（このパッケージ内のみで使用） |

---

#### 5-8-2. application/usecase/account/shoppingregist/（新規）

| クラス名 | 移動元 | 概要 |
|---------|--------|------|
| `ShoppingRegistTopMenuUseCase` | `account/regist` | 買い物登録トップメニュー |
| `ShoppingRegistUseCase` | `account/regist` | 買い物登録 |
| `SimpleShoppingRegistUseCase` | `account/regist` | 簡易買い物登録 |

---

#### 5-8-3. application/usecase/account/component/（新規）

`incomeandexpenditure/` と `shoppingregist/` の両方から使用される account 共通コンポーネント。

| クラス名 | 移動元 | 使用箇所 | 概要 |
|---------|--------|----------|------|
| `ShoppingRegistExpenditureItemComponent`（旧: `ShoppingRegistExpenditureAndSisyutuKingakuComponent`） | `account/regist` | incomeandexpenditure(2), shoppingregist(2) | 買い物登録に必須の支出項目のチェック・取得を行うコンポーネント |
| `SisyutuKingakuItemHolderComponent` | `common/component` | incomeandexpenditure(1), shoppingregist(1) | 支出金額テーブルレコード群のホルダー取得 |

---

#### 5-8-4. application/usecase/common/（新規）

`account/`・`itemmanage/` など複数の usecase パッケージをまたいで使用されるアプリ全体共通コンポーネント。

| クラス名 | 移動元 | 使用箇所 | 概要 |
|---------|--------|----------|------|
| `AccountBookUserInquiryUseCase` | `common/component` | inquiry, shoppingregist, itemmanage, presentation(top) | ログインユーザーの家計簿情報・対象年月を取得 |
| `CodeTableItemComponent` | `common/component` | incomeandexpenditure(3), shoppingregist(1), itemmanage(3) | コードテーブルのマップデータ提供（フォーム用ドロップダウン等） |
| `SisyutuItemComponent` | `common/component` | incomeandexpenditure(2), itemmanage(3) | 支出項目マスタの検索・一覧取得 |

---

## 6. Step B 実施チェックリスト（Eclipse実施）

> Eclipse の「パッケージのリファクタリング（Refactor → Move / Rename）」を使用すること。
> Step A（domain/type リネーム）完了後に実施すること。

### domain/type/account/ の再編

- [OK] `inquiry/` → `expenditure/` に移動: ExpenditureCode, ExpenditureCategory, ExpenditureDetailContext, ExpenditureName, ExpenditureEventCode,【未使用のため削除】ExpenditureId
- [OK] `inquiry/` → `expenditureinfo/` に移動: ExpenditureItemCode, ExpenditureItemName, ExpenditureItemSortOrder, ExpenditureItemLevel, ExpenditureItemDetailContext, ParentExpenditureItemCode, 【未使用のため削除】ExpenditureCategoryId
- [OK] `inquiry/` → `income/` に移動: IncomeCode, IncomeCategory, IncomeDetailContext, 【未使用のため削除】IncomeId
- [OK] `inquiry/` → `incomeandexpenditure/` に移動: BalanceTotalAmount, ExpectedExpenditureAmount, ExpectedExpenditureTotalAmount, ExpenditureTotalAmount, RegularIncomeTotalAmount, TotalAvailableFunds, WithdrawingAmount, WithdrawingTotalAmount
- [OK] `inquiry/` → `fixedcost/` に移動＆リネーム: ShiharaiKingaku → FixedCostPaymentAmount
- [OK] `domain/type/commonに移動/` → EnableUpdateFlg

### domain/model/account/ の再編

- [OK] `inquiry/` → `expenditure/` に移動: ExpenditureItem, ExpenditureAmountItem(SisyutuKingakuItem), ExpenditureAmountItemHolder(SisyutuKingakuItemHolder), ExpenditureAmountItemInquiryList(SisyutuKingakuItemInquiryList)
- [OK] `inquiry/` → `expenditureinfo/` に移動: ExpenditureItemInfo(SisyutuItem), ExpenditureItemInfoInquiryList(SisyutuItemInquiryList)
- [OK] `inquiry/` → `income/` に移動: IncomeItem, IncomeItemInquiryList
- [OK] `inquiry/` → `incomeandexpenditure/` に移動: IncomeAndExpenditure, IncomeAndExpenditureItem, IncomeAndExpenditureInquiryList

### domain/repository/account/ の再編

- [OK] `inquiry/` → `account/` 直下に移動（inquiry サブパッケージ廃止）: 全5クラス

### infrastructure 各層の再編

- [OK] `datasource/account/inquiry/` → `account/` 直下に移動: 全5クラス
- [OK] `mapper/account/inquiry/` → `account/` 直下に移動: 全5クラス
- [OK] `mapperのSQLを上記にあわせる → `account/` 直下に移動: 全5SQLファイル
- [OK] `mapperのSQLとマッパークラスのSQLのアノテーションのソースファイルへのリンクを修正
- [OK] `dto/account/inquiry/` → 各対応パッケージに移動: 全7クラス

### presentation/request/account/ の再編

- [OK] `inquiry/` → `regist/` に移動: IncomeItemForm, ExpenditureItemForm, ExpenditureSelectItemForm, SimpleShoppingRegistInfoForm
- [OK] 移動後、空になった `inquiry/` を削除

### application/usecase/ の再編

- [OK] `account/regist/` → `account/incomeandexpenditure/` に移動: ExpenditureItemSelectUseCase, ExpenditureRegistUseCase, IncomeAndExpenditureInitUseCase, IncomeAndExpenditureRegistConfirmUseCase, IncomeRegistUseCase
- [OK] `account/regist/` → `account/shoppingregist/` に移動: ShoppingRegistTopMenuUseCase, ShoppingRegistUseCase, SimpleShoppingRegistUseCase
- [OK] `account/regist/ShoppingRegistExpenditureAndSisyutuKingakuComponent` → `account/component/ShoppingRegistExpenditureItemComponent` にリネーム＆移動
- [OK] `common/component/IncomeAndExpenditureRegistListComponent` → `account/incomeandexpenditure/` に移動
- [OK] `common/component/SisyutuKingakuItemHolderComponent` → `account/component/` に移動
- [OK] `common/component/AccountBookUserInquiryUseCase` → `usecase/common/` に移動
- [OK] `common/component/CodeTableItemComponent` → `usecase/common/` に移動
- [OK] `common/component/SisyutuItemComponent` → `usecase/common/` に移動
- [OK] 移動後、空になった `account/regist/` を削除
- [OK] 移動後、空になった `common/component/` を削除

---

## 7. 影響範囲の概算

| 変更種別 | 対象クラス数 | 主な影響先 |
|---|---|---|
| Step A: domain/type リネーム（13クラス）★完了 | 13 | 全UseCaseおよびモデルクラス（import更新） |
| Step B: domain/type パッケージ移動 | 26 | 全UseCaseおよびモデルクラス（import更新） |
| Step B: domain/model パッケージ移動 | 11 | UseCaseおよびDataSourceクラス |
| Step B: domain/repository パッケージ移動 | 5 | UseCaseおよびDataSourceクラス |
| Step B: infrastructure パッケージ移動 | 17 | DataSourceおよびMapperクラス |
| Step B: presentation/request 移動 | 4 | Controllerクラス（import更新） |
| Step B: application/usecase 再編（移動）★完了 | 14 | 各UseCase・Controllerクラス（import更新） |
| Step B: common/component 解体★完了 | 5 | 各UseCase（import更新） + common/component削除 |
| **合計** | **95** | |

> Eclipse のリファクタリング機能（Rename, Move）を使用することで、import の自動更新が可能。

---

## 8. Step B 完了後の残課題（TODO）★完了

### 8-1. IncomeAndExpenditure 集約の拡張 ★完了

**対象クラス**: `domain/model/account/incomeandexpenditure/IncomeAndExpenditure.java`

**背景**:
Phase 2 で `IncomeAndExpenditure` を照会専用の集約ルートとして作成した際、以下の拡張計画を Javadoc に記載していた。
> [Phase 3以降の拡張計画]
> ・登録・更新機能のサポート
> ・金額計算ロジックの実装
> ・IncomeAndExpenditureItemの統合

Phase 5 の収支登録リファクタリングでは、この計画が未実施のまま完了した。
現状、`IncomeAndExpenditure`（集約ルート）と `IncomeAndExpenditureItem`（DB登録/更新用）が同じメンバ変数を重複して持っていた。

**実施内容（完了）**:
- `IncomeAndExpenditureItem` のメソッドを `IncomeAndExpenditure` に統合（`createForAdd`・`createForUpdate`・`addExpenditureAmount`・`subtractExpenditureAmount`）
- リポジトリの `select(SearchQueryUserIdAndYearMonth)` を廃止し `findByPrimaryKey` に統一
- 影響クラスを修正: `IncomeAndExpenditureRegistConfirmUseCase`・`SimpleShoppingRegistUseCase`・`IncomeAndExpenditureTableDataSource`・`IncomeAndExpenditureReadWriteDto` 等
- `IncomeAndExpenditureItem` クラスを削除
- 全808テスト GREEN 確認済み（2026/04/16）
