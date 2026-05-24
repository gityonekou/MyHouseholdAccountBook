# Feature③-1 年間固定費合計画面 設計書

## 概要

固定費管理機能に「年間固定費合計」画面を新規追加する。
あわせて、既存の固定費管理画面にタブナビゲーションを追加し、
3画面（固定費管理 / 年間固定費合計 / 月別固定費一覧）を相互にタブ遷移できるようにする。

また、本対応に合わせて以下のパッケージ・ディレクトリ分割を実施する。
- `templates/itemmanage/fixedcost/` サブディレクトリ新設（固定費関連HTMLを集約）
- `presentation/response/itemmanage/fixedcost/` サブパッケージ新設（固定費関連Responseを集約）

---

## 変更対象ファイル一覧

| # | 区分 | ファイル | 変更内容 |
|---|------|---------|---------|
| 1 | 共通定数（変更） | `MyHouseholdAccountBookContent.java` | 年間固定費合計カテゴリ判定用SisyutuItemCode定数を追加 |
| 2 | 共通型（変更） | `Money.java` | `toZeroDashString()` メソッドを追加（0円→"ー"） |
| 3 | 共通型（変更） | `NullableMoney.java` | `toZeroDashString()` メソッドを追加（null→""、0円→"ー"） |
| 4 | ドメインモデル（新規） | `FixedCostAnnualSummaryList.java` | 年間固定費合計データのドメインモデル新規作成 |
| 5 | DTO（新規） | `FixedCostAnnualSummaryReadDto.java` | 年間固定費合計クエリ結果DTOを新規作成 |
| 6 | SQL（新規） | `FixedCostAnnualSummarySelectSql01.sql` | SISYUTU_ITEM_TABLE SORT前方一致結合クエリを新規作成 |
| 7 | Mapper（変更） | `FixedCostTableMapper.java` | `findForAnnualSummaryByUserId` メソッド追加 |
| 8 | Repository IF（変更） | `FixedCostTableRepository.java` | `findForAnnualSummaryByUserId` メソッド追加 |
| 9 | DataSource（変更） | `FixedCostTableDataSource.java` | `findForAnnualSummaryByUserId` メソッド追加 |
| 10 | UseCase（新規） | `FixedCostAnnualSummaryUseCase.java` | 年間固定費合計画面 UseCase を新規作成 |
| 11 | Response（新規） | `FixedCostAnnualSummaryResponse.java` | 年間固定費合計画面 Response を新規作成 |
| 12 | Controller（変更） | `FixedCostInfoManageController.java` | `/annualsummary/` エンドポイント追加・DI追加 |
| 13 | HTML（新規） | `FixedCostAnnualSummary.html` | 年間固定費合計画面テンプレートを新規作成 |
| 14 | HTML（移動＋変更） | `FixedCostInfoManageInit.html` | `fixedcost/` サブディレクトリへ移動＋タブナビ追加 |
| 15 | HTML（移動） | `FixedCostInfoManageActSelect.html` | `fixedcost/` サブディレクトリへ移動 |
| 16 | HTML（移動） | `FixedCostInfoManageUpdate.html` | `fixedcost/` サブディレクトリへ移動 |
| 17 | HTML（移動） | `FixedCostBulkUpdate.html` | `fixedcost/` サブディレクトリへ移動 |
| 18 | Response（移動） | `AbstractFixedCostItemListResponse.java` | `fixedcost/` サブパッケージへ移動・`targetMonthValue` フィールド追加 |
| 19 | Response（移動） | `FixedCostInfoManageInitResponse.java` | `fixedcost/` サブパッケージへ移動・viewName変更 |
| 20 | Response（移動） | `FixedCostInfoManageActSelectResponse.java` | `fixedcost/` サブパッケージへ移動・viewName変更 |
| 21 | Response（移動） | `FixedCostInfoManageUpdateResponse.java` | `fixedcost/` サブパッケージへ移動・viewName変更 |
| 22 | Response（移動） | `FixedCostBulkUpdateResponse.java` | `fixedcost/` サブパッケージへ移動・viewName変更 |
| 23 | テスト（変更） | `MoneyTest.java` | `toZeroDashString()` テスト追加 |
| 24 | テスト（変更） | `NullableMoneyTest.java` | `toZeroDashString()` テスト追加 |
| 25 | テスト（新規） | `FixedCostAnnualSummaryListTest.java` | ドメインモデル単体テスト（C1網羅） |
| 26 | テスト（新規） | `FixedCostAnnualSummaryUseCaseIntegrationTest.java` | UseCase 結合テスト |
| 27 | テスト（変更） | `FixedCostInfoManageControllerIntegrationTest.java` | viewName変更に伴う更新 |
| 28 | テスト（変更） | `FixedCostInfoManageBulkUpdateControllerIntegrationTest.java` | viewName変更に伴う更新 |
| 29 | テストSQL（新規） | `FixedCostAnnualSummaryIntegrationTest.sql` | UseCase 結合テスト用 SQL |

---

## 1. パッケージ・ディレクトリ分割

### 1-1. `templates/itemmanage/fixedcost/` サブディレクトリ新設

```
src/main/resources/templates/itemmanage/
├── fixedcost/                              ← 新設
│   ├── FixedCostAnnualSummary.html         (Feature③-1 新規)
│   ├── FixedCostInfoManageInit.html        (移動)
│   ├── FixedCostInfoManageActSelect.html   (移動)
│   ├── FixedCostInfoManageUpdate.html      (移動)
│   └── FixedCostBulkUpdate.html            (移動)
├── ExpenditureItemInfoManageInit.html      (変更なし)
├── ExpenditureItemInfoManageActSelect.html (変更なし)
├── ...
```

- `account/` テンプレートが `inquiry/` / `regist/` に整理されているのと同じパターンを適用
- HTML 移動はファイルの物理移動のみ（Eclipseのリファクタリング操作で実施）

### 1-2. `presentation/response/itemmanage/fixedcost/` サブパッケージ新設

```
presentation/response/itemmanage/
├── fixedcost/                                      ← 新設
│   ├── FixedCostAnnualSummaryResponse.java          (Feature③-1 新規)
│   ├── AbstractFixedCostItemListResponse.java       (移動)
│   ├── FixedCostInfoManageInitResponse.java         (移動)
│   ├── FixedCostInfoManageActSelectResponse.java    (移動)
│   ├── FixedCostInfoManageUpdateResponse.java       (移動)
│   └── FixedCostBulkUpdateResponse.java             (移動)
├── AbstractExpenditureItemInfoManageResponse.java   (変更なし)
├── ExpenditureItemInfoManageInitResponse.java       (変更なし)
├── ...
```

- Eclipseのリファクタリング（パッケージ移動）操作で実施
- UseCase・Controller の import が自動更新される

### 1-3. viewName 変更

各 Response クラスの `build()` 内 viewName を変更する。

| クラス | 変更前 | 変更後 |
|--------|--------|--------|
| `FixedCostInfoManageInitResponse` | `"itemmanage/FixedCostInfoManageInit"` | `"itemmanage/fixedcost/FixedCostInfoManageInit"` |
| `FixedCostInfoManageActSelectResponse` | `"itemmanage/FixedCostInfoManageActSelect"` | `"itemmanage/fixedcost/FixedCostInfoManageActSelect"` |
| `FixedCostInfoManageUpdateResponse` | `"itemmanage/FixedCostInfoManageUpdate"` | `"itemmanage/fixedcost/FixedCostInfoManageUpdate"` |
| `FixedCostBulkUpdateResponse` | `"itemmanage/FixedCostBulkUpdate"` | `"itemmanage/fixedcost/FixedCostBulkUpdate"` |

---

## 2. 共通型クラスへのメソッド追加

### 2-1. `Money.toZeroDashString()` 追加

**配置クラス**: `domain/type/common/Money`

```java
/**
 * 金額が0の場合は「ー」、それ以外はフォーマット済み文字列を返します。
 * 年間固定費合計画面など、0円を「ー」で表示する用途に使用します。
 */
public String toZeroDashString() {
    return isZero() ? "ー" : toFormatString();
}
```

**テスト追加**: `MoneyTest` に以下を追加する（`Money` のサブクラスである具体クラスを使用）
- `0円の場合"ー"を返すこと`
- `正の金額の場合フォーマット済み文字列を返すこと`

### 2-2. `NullableMoney.toZeroDashString()` 追加

**配置クラス**: `domain/type/common/NullableMoney`

```java
/**
 * null値の場合は空文字列、0円の場合は「ー」、それ以外はフォーマット済み文字列を返します。
 * null値を持つ金額項目を年間固定費合計画面等で「ー」表示する用途に使用します。
 */
public String toZeroDashString() {
    if (value == null) {
        return "";
    }
    return isZero() ? "ー" : toFormatString();
}
```

**テスト追加**: `NullableMoneyTest` に以下を追加する
- `null値の場合空文字列を返すこと`
- `0円の場合"ー"を返すこと`
- `正の金額の場合フォーマット済み文字列を返すこと`

---

## 3. タブナビゲーション追加（既存 HTML の更新）

### 3-1. `AbstractFixedCostItemListResponse` への `targetMonthValue` 追加

タブナビゲーションの「月別固定費一覧」リンクに現在対象月（`"MM"` 形式）が必要なため、
`AbstractFixedCostItemListResponse` に新規フィールドを追加する。

```java
// 現在の対象月（月別固定費一覧タブリンク用、"MM"形式 例:"05"）
@Getter
@Setter
private String targetMonthValue;
```

`createModelAndView()` に追加：

```java
modelAndView.addObject("targetMonthValue", targetMonthValue);
```

`FixedCostInquiryUseCase.setFixedCostItemList()` で既存の `targetMonthLabel` 設定と同時に設定する：

```java
response.setTargetMonthValue(ym0.getMonth()); // TargetYearMonth.getMonth() = "MM"形式
```

### 3-2. `FixedCostInfoManageInit.html` のタブナビゲーション追加

```html
<!-- ===== タブナビゲーション ===== -->
<nav>
    <div class="nav nav-tabs mb-3" id="nav-tab" role="tablist">
        <!-- ① 固定費管理タブ [現在アクティブ] -->
        <button class="nav-link active" type="button" role="tab" aria-selected="true" disabled>固定費管理</button>
        <!-- ② 年間固定費合計タブ [遷移] -->
        <a class="nav-link" th:href="@{/myhacbook/managebaseinfo/fixedcostinfo/annualsummary/}">年間固定費合計</a>
        <!-- ③ 月別固定費一覧タブ [現在対象月でデフォルト遷移] -->
        <a class="nav-link" th:href="@{/myhacbook/managebaseinfo/fixedcostinfo/monthlydetail/(month=${targetMonthValue})}">月別固定費一覧</a>
    </div>
</nav>
```

- CSS に `.nav-tabs a.nav-link { padding: .5rem 1rem; }` を追加（`<a>` タグを `<button>` タグと同一外観にするため）
- 「月別固定費一覧」URL（Feature③-2 未実装）は 404 になるが許容（Feature③-2 完了時に有効化）

---

## 4. 新規ドメインモデル：`FixedCostAnnualSummaryList`

**配置パッケージ**: `domain/model/account/fixedcost/`

### 4-1. 設計方針

- SQL で SISYUTU_ITEM_TABLE を 2 段結合し、各固定費の「直接の支出項目コード」「親コード」「祖父コード」を取得する
- Java 側で Level-1・Level-2 コードを解決し、8 列のいずれに属するかを判定する
- `shouldAdd()` ロジックは `FixedCostInquiryList` と同一のため、private static メソッドとして再実装する

### 4-2. `AnnualSummaryColumn`（内部 enum）

```java
public enum AnnualSummaryColumn {
    JIGYOU_KEIHI,      // 事業経費
    HIKOZEI,           // 固定費(非課税)
    SEIKATSUHI,        // 固定費(生活費) ※固定費(課税)から積立(投資)・積立金を除いた値
    TSUMITATE_TOUSHI,  // 積立(投資)
    TSUMITATE_KIN,     // 積立金
    IRUI_JUKYO,        // 衣類住居設備
    INSHOKU,           // 飲食日用品
    SHUMI              // 趣味娯楽
}
```

### 4-3. `FixedCostAnnualSummaryItem`（内部クラス）

SQL 取得済みデータのドメイン表現。

| フィールド | 型 | 説明 |
|---|---|---|
| `fixedCostTargetPaymentMonth` | `FixedCostTargetPaymentMonth` | 支払月コード |
| `fixedCostPaymentAmount` | `FixedCostPaymentAmount` | 支払金額 |
| `level1SisyutuItemCode` | `String` | Level-1 祖先支出項目コード（SQL の ANCLVL1 から取得） |
| `level2SisyutuItemCode` | `String` | Level-2 祖先支出項目コード（Level-1 項目の場合は null） |

> **設計ポイント**: `sisyutuItemLeafLevel` は不要。SQL で SISYUTU_ITEM_SORT の前方一致を使って
> Level-1・Level-2 祖先を直接取得するため、Java 側で階層をたどる必要がない。

### 4-4. `MonthlyRow`（内部クラス）

```java
public static class MonthlyRow {
    private final int month; // 1〜12
    private final Map<AnnualSummaryColumn, FixedCostPaymentTotalAmount> columnAmounts;

    public FixedCostPaymentTotalAmount getAmount(AnnualSummaryColumn column) { ... }
    public FixedCostPaymentTotalAmount getMonthTotal() { /* 全列の合計 */ }
}
```

### 4-5. `YearlyRow`（内部クラス）

12か月の月別合計を集計した年間合計データ。`buildYearlyRow()` が生成して返す。

```java
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public static class YearlyRow {
    private final Map<AnnualSummaryColumn, FixedCostPaymentTotalAmount> columnAmounts;

    public FixedCostPaymentTotalAmount getAmount(AnnualSummaryColumn column) {
        return columnAmounts.getOrDefault(column, FixedCostPaymentTotalAmount.ZERO);
    }

    public FixedCostPaymentTotalAmount getYearTotal() {
        FixedCostPaymentTotalAmount total = FixedCostPaymentTotalAmount.ZERO;
        for (FixedCostPaymentTotalAmount amount : columnAmounts.values()) {
            total = total.add(amount);
        }
        return total;
    }
}
```

> **設計ポイント（DDD境界）**: 年間合計の集計は「データをどう集約するか」というドメインロジックであるため、
> UseCase ではなくドメイン層に配置する。UseCase は DTO 変換（`toZeroDashString()`）のみを担う。
> `buildYearlyRow()` は内部で `buildMonthlyRows()` を呼び出すため月別行を二重計算するが、
> 固定費データは最大数百件と少量であり、パフォーマンス上の問題はない。

### 4-6. `FixedCostAnnualSummaryList` クラス

```java
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FixedCostAnnualSummaryList {

    private final List<FixedCostAnnualSummaryItem> values;

    public static FixedCostAnnualSummaryList from(List<FixedCostAnnualSummaryItem> values) { ... }

    public boolean isEmpty() { ... }

    /** 1月〜12月の MonthlyRow リストを生成して返します。 */
    public List<MonthlyRow> buildMonthlyRows() {
        List<MonthlyRow> rows = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            rows.add(buildMonthlyRow(month));
        }
        return rows;
    }

    /** 12か月分の月別合計を集計した YearlyRow を生成して返します。 */
    public YearlyRow buildYearlyRow() {
        Map<AnnualSummaryColumn, FixedCostPaymentTotalAmount> totals = new EnumMap<>(AnnualSummaryColumn.class);
        for (AnnualSummaryColumn col : AnnualSummaryColumn.values()) {
            totals.put(col, FixedCostPaymentTotalAmount.ZERO);
        }
        for (MonthlyRow monthlyRow : buildMonthlyRows()) {
            for (AnnualSummaryColumn col : AnnualSummaryColumn.values()) {
                totals.merge(col, monthlyRow.getAmount(col), FixedCostPaymentTotalAmount::add);
            }
        }
        return new YearlyRow(totals);
    }

    private MonthlyRow buildMonthlyRow(int month) {
        Map<AnnualSummaryColumn, FixedCostPaymentTotalAmount> amounts = new EnumMap<>(AnnualSummaryColumn.class);
        for (AnnualSummaryColumn col : AnnualSummaryColumn.values()) {
            amounts.put(col, FixedCostPaymentTotalAmount.ZERO);
        }
        for (FixedCostAnnualSummaryItem item : values) {
            if (shouldAdd(item.getFixedCostTargetPaymentMonth().getValue(), month)) {
                AnnualSummaryColumn col = determineColumn(item);
                amounts.merge(col, item.getFixedCostPaymentAmount().toTotalAmount(),
                        FixedCostPaymentTotalAmount::add);
            }
        }
        return new MonthlyRow(month, amounts);
    }

    // FixedCostInquiryList.shouldAdd() と同一ロジック
    private static boolean shouldAdd(String shiharaiTukiCode, int monthValue) { ... }
}
```

### 4-7. カテゴリ判定ロジック `determineColumn()`

SQL の SORT 前方一致結合により `level1SisyutuItemCode` / `level2SisyutuItemCode` が直接取得済みのため、
階層レベルによる分岐は不要。Level 1〜5 すべてに対して同一ロジックで動作する。

```java
private AnnualSummaryColumn determineColumn(FixedCostAnnualSummaryItem item) {
    String level1Code = item.getLevel1SisyutuItemCode();
    // Level-1 項目は Level-2 祖先なし（null）→ level1Code で代替
    String level2Code = (item.getLevel2SisyutuItemCode() != null)
            ? item.getLevel2SisyutuItemCode()
            : level1Code;

    switch (level1Code) {
        case MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_JIGYOU_KEIHI:       // "0001"
            return AnnualSummaryColumn.JIGYOU_KEIHI;
        case MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_FIXED_COST_HIKOZEI: // "0013"
            return AnnualSummaryColumn.HIKOZEI;
        case MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_FIXED_COST_KAZEI:   // "0023"
            if (MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_TSUMITATE_TOUSHI.equals(level2Code)) {
                return AnnualSummaryColumn.TSUMITATE_TOUSHI; // "0031"
            }
            if (MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_TSUMITATE_KIN.equals(level2Code)) {
                return AnnualSummaryColumn.TSUMITATE_KIN;    // "0033"
            }
            return AnnualSummaryColumn.SEIKATSUHI;
        case MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_IRUI_JUKYO_SETSUBI: // "0045"
            return AnnualSummaryColumn.IRUI_JUKYO;
        case MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_INSHOKU_NICHIYOHIN: // "0049"
            return AnnualSummaryColumn.INSHOKU;
        case MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_SHUMI_GORAKU:       // "0056"
            return AnnualSummaryColumn.SHUMI;
        default:
            return AnnualSummaryColumn.SEIKATSUHI; // 予防的フォールバック
    }
}
```

---

## 5. 共通定数追加：`MyHouseholdAccountBookContent`

カテゴリ判定に使用する SisyutuItemCode 定数を追加する。
これらはシステム定義の固定値（SISYUTU_ITEM_BASE_TABLE のマスタデータ）であるため定数化が適切。

```java
// ===== 年間固定費合計カテゴリ判定用 SisyutuItemCode 定数 =====
// Level-1 コード（最上位支出項目分類）
public static final String SISYUTU_ITEM_CODE_JIGYOU_KEIHI        = "0001"; // 事業経費
public static final String SISYUTU_ITEM_CODE_FIXED_COST_HIKOZEI  = "0013"; // 固定費(非課税)
public static final String SISYUTU_ITEM_CODE_FIXED_COST_KAZEI    = "0023"; // 固定費(課税)
public static final String SISYUTU_ITEM_CODE_IRUI_JUKYO_SETSUBI  = "0045"; // 衣類住居設備
public static final String SISYUTU_ITEM_CODE_INSHOKU_NICHIYOHIN  = "0049"; // 飲食日用品
public static final String SISYUTU_ITEM_CODE_SHUMI_GORAKU        = "0056"; // 趣味娯楽
// Level-2 コード（固定費(課税) 配下の特別分類）
public static final String SISYUTU_ITEM_CODE_TSUMITATE_TOUSHI    = "0031"; // 積立(投資)
public static final String SISYUTU_ITEM_CODE_TSUMITATE_KIN       = "0033"; // 積立金
```

---

## 6. Repository / DTO / SQL

### 6-1. `FixedCostTableRepository`（インターフェース変更）

```java
/**
 * 年間固定費合計画面用に、固定費情報を支出項目階層情報込みで取得します。
 */
FixedCostAnnualSummaryList findForAnnualSummaryByUserId(SearchQueryUserId userId);
```

### 6-2. `FixedCostAnnualSummaryReadDto`（新規）

**配置パッケージ**: `infrastructure/dto/account/fixedcost/`

| フィールド | 型 | 内容 |
|---|---|---|
| `fixedCostCode` | `String` | 固定費コード |
| `fixedCostName` | `String` | 固定費名(支払名) |
| `fixedCostDetailContext` | `String` | 固定費内容詳細 |
| `sisyutuItemName` | `String` | 支出項目名 |
| `sisyutuItemCode` | `String` | 固定費に紐付く支出項目コード（リーフ） |
| `level1SisyutuItemCode` | `String` | Level-1 祖先支出項目コード（ANCLVL1 から取得） |
| `level2SisyutuItemCode` | `String` | Level-2 祖先支出項目コード（Level-1 項目は null） |
| `fixedCostShiharaiTuki` | `String` | 支払月コード |
| `fixedCostShiharaiTukiOptionalContext` | `String` | 支払月任意詳細 |
| `fixedCostShiharaiDay` | `String` | 支払日 |
| `shiharaiKingaku` | `BigDecimal` | 支払金額 |

### 6-3. `FixedCostAnnualSummarySelectSql01.sql`（新規）

**配置パス**: `src/main/resources/sql/account/fixedcost/`

```sql
-- 固定費テーブル:FIXED_COST_TABLE と支出項目テーブル:SISYUTU_ITEM_TABLE から
-- 指定ユーザIDの固定費情報を Level-1・Level-2 祖先コード込みで取得します。
-- B       ：固定費に直接紐付く支出項目（リーフ）
-- ANCLVL1 ：SORT前方一致(2桁)でBのLevel-1祖先を取得（自分自身を含む）
-- ANCLVL2 ：SORT前方一致(4桁)でBのLevel-2祖先を取得（Level-1項目の場合はnull）
SELECT A.FIXED_COST_CODE, A.FIXED_COST_NAME, A.FIXED_COST_DETAIL_CONTEXT,
       B.SISYUTU_ITEM_NAME,
       A.SISYUTU_ITEM_CODE,
       ANCLVL1.SISYUTU_ITEM_CODE AS LEVEL1_SISYUTU_ITEM_CODE,
       ANCLVL2.SISYUTU_ITEM_CODE AS LEVEL2_SISYUTU_ITEM_CODE,
       A.FIXED_COST_SHIHARAI_TUKI, A.FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT,
       A.FIXED_COST_SHIHARAI_DAY, A.SHIHARAI_KINGAKU
  FROM FIXED_COST_TABLE AS A
  JOIN SISYUTU_ITEM_TABLE AS B
    ON A.USER_ID = B.USER_ID AND A.SISYUTU_ITEM_CODE = B.SISYUTU_ITEM_CODE
  JOIN SISYUTU_ITEM_TABLE AS ANCLVL1
    ON A.USER_ID = ANCLVL1.USER_ID
   AND ANCLVL1.SISYUTU_ITEM_LEVEL = '1'
   AND SUBSTRING(B.SISYUTU_ITEM_SORT, 1, 2) = SUBSTRING(ANCLVL1.SISYUTU_ITEM_SORT, 1, 2)
  LEFT JOIN SISYUTU_ITEM_TABLE AS ANCLVL2
    ON A.USER_ID = ANCLVL2.USER_ID
   AND ANCLVL2.SISYUTU_ITEM_LEVEL = '2'
   AND SUBSTRING(B.SISYUTU_ITEM_SORT, 1, 4) = SUBSTRING(ANCLVL2.SISYUTU_ITEM_SORT, 1, 4)
 WHERE A.USER_ID = /*[# mb:p="dto.userId"]*/ 1 /*[/]*/ AND A.DELETE_FLG IS FALSE
 ORDER BY B.SISYUTU_ITEM_SORT, A.FIXED_COST_SHIHARAI_TUKI
```

**SORT 前方一致の根拠**（`docs/test-data-design-rules.md` §SISYUTU_ITEM_SORT より）:

SISYUTU_ITEM_SORT は 10 桁の数値文字列で、各階層 2 桁ずつ `[L1][L2][L3][L4][L5]` の構造を持つ。
- `SUBSTRING(SORT, 1, 2)` が一致 → 同じ Level-1 グループ
- `SUBSTRING(SORT, 1, 4)` が一致 → 同じ Level-2 グループ

この性質を使い、リーフが Level 1〜5 のいずれであっても親チェーンを再帰的にたどることなく
Level-1・Level-2 祖先を O(1) で特定できる。

**階層レベル別の取得値一覧**:

| リーフ Level | `LEVEL1_SISYUTU_ITEM_CODE` | `LEVEL2_SISYUTU_ITEM_CODE` |
|---|---|---|
| Level 1 | 自身のコード | null（ANCLVL2 LEFT JOIN 不一致） |
| Level 2 | Level-1 祖先コード | 自身のコード（自身が Level-2） |
| Level 3〜5 | Level-1 祖先コード | Level-2 祖先コード |

### 6-4. `FixedCostTableDataSource`・`FixedCostTableMapper`（変更）

既存パターンに倣い `findForAnnualSummaryByUserId()` を追加する。
MyBatis は `FixedCostAnnualSummaryReadDto` にカラム名でマッピングする。

---

## 7. 新規 UseCase：`FixedCostAnnualSummaryUseCase`

**配置パッケージ**: `application/usecase/itemmanage/fixedcost/`

```java
@Service
@RequiredArgsConstructor
public class FixedCostAnnualSummaryUseCase {
    private final FixedCostTableRepository fixedCostRepository;
    private final AccountBookUserRepository accountBookUserRepository;

    public FixedCostAnnualSummaryResponse readAnnualSummaryInfo(LoginUserInfo user) {
        UserId userId = UserId.from(user.getUserId());
        // 現在の対象年月を取得（NowTargetYearMonth は TargetYearMonth に統合済み）
        TargetYearMonth targetYearMonth = accountBookUserRepository.getTargetYearMonth(
                SearchQueryUserId.from(userId));

        // 年間固定費合計データを取得
        FixedCostAnnualSummaryList summaryList = fixedCostRepository.findForAnnualSummaryByUserId(
                SearchQueryUserId.from(userId));

        // レスポンス生成（現在対象月を月別固定費一覧タブリンク用に渡す）
        FixedCostAnnualSummaryResponse response = FixedCostAnnualSummaryResponse.getInstance(
                targetYearMonth.getMonth()); // 例："05"

        if (summaryList.isEmpty()) {
            response.addMessage("登録済み固定費情報が0件です。");
        } else {
            response.setAnnualSummaryRowList(
                    createAnnualSummaryRowList(summaryList.buildMonthlyRows(), summaryList.buildYearlyRow()));
        }
        return response;
    }

    // 年間合計の集計はドメイン（buildYearlyRow）が担う。UseCase は DTO 変換のみ行う。
    private List<AnnualSummaryRowItem> createAnnualSummaryRowList(
            List<MonthlyRow> monthlyRows, YearlyRow yearlyRow) {
        List<AnnualSummaryRowItem> rows = new ArrayList<>();
        for (MonthlyRow row : monthlyRows) {
            rows.add(createDataRow(row));
        }
        rows.add(createTotalRow(yearlyRow));
        return rows;
    }

    private AnnualSummaryRowItem createTotalRow(YearlyRow yearlyRow) {
        return AnnualSummaryRowItem.createTotalRow(
                yearlyRow.getAmount(AnnualSummaryColumn.JIGYOU_KEIHI).toZeroDashString(),
                yearlyRow.getAmount(AnnualSummaryColumn.HIKOZEI).toZeroDashString(),
                yearlyRow.getAmount(AnnualSummaryColumn.SEIKATSUHI).toZeroDashString(),
                yearlyRow.getAmount(AnnualSummaryColumn.TSUMITATE_TOUSHI).toZeroDashString(),
                yearlyRow.getAmount(AnnualSummaryColumn.TSUMITATE_KIN).toZeroDashString(),
                yearlyRow.getAmount(AnnualSummaryColumn.IRUI_JUKYO).toZeroDashString(),
                yearlyRow.getAmount(AnnualSummaryColumn.INSHOKU).toZeroDashString(),
                yearlyRow.getAmount(AnnualSummaryColumn.SHUMI).toZeroDashString(),
                yearlyRow.getYearTotal().toZeroDashString());
    }
}
```

---

## 8. 新規 Response：`FixedCostAnnualSummaryResponse`

**配置パッケージ**: `presentation/response/itemmanage/fixedcost/`

```java
public class FixedCostAnnualSummaryResponse extends AbstractResponse {

    /** 年間固定費合計の1行データ（データ行12行 + 合計行1行 = 計13行） */
    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AnnualSummaryRowItem {
        private final String monthLabel;       // "1月"〜"12月"、"合計"
        private final String jigyouKeihi;      // 事業経費（"ー" or "XXX円"）
        private final String hikozei;          // 固定費(非課税)
        private final String seikatsuhi;       // 固定費(生活費)
        private final String tsumitateToushi;  // 積立(投資)
        private final String tsumitateKin;     // 積立金
        private final String iruiJukyo;        // 衣類住居設備
        private final String inshoku;          // 飲食日用品
        private final String shumiGoraku;      // 趣味娯楽
        private final String monthTotal;       // 月合計（太字表示）
        private final String detailMonth;      // "01"〜"12"（合計行は null）
        private final boolean evenMonth;       // 偶数月フラグ（table-warning クラス適用用）
        private final boolean totalRow;        // 合計行フラグ
    }

    // 月別固定費一覧タブリンク用の現在対象月（"MM"形式）
    @Getter private final String targetMonth;
    // 年間固定費合計行リスト（12行 + 合計行）
    @Getter @Setter private List<AnnualSummaryRowItem> annualSummaryRowList;

    public static FixedCostAnnualSummaryResponse getInstance(String targetMonth) {
        return new FixedCostAnnualSummaryResponse(targetMonth);
    }

    @Override
    public ModelAndView build() {
        ModelAndView modelAndView = createModelAndView("itemmanage/fixedcost/FixedCostAnnualSummary");
        modelAndView.addObject("targetMonth", targetMonth);
        modelAndView.addObject("annualSummaryRowList", annualSummaryRowList);
        return modelAndView;
    }
}
```

**金額の "ー" 表示ルール**: `FixedCostPaymentTotalAmount` は `Money` のサブクラスであるため、
`toZeroDashString()` メソッド（セクション 2-1）を直接呼び出して金額文字列を生成する。

---

## 9. Controller 変更：`FixedCostInfoManageController`

### 9-1. DI 追加

```java
// UseCase(年間固定費合計)
private final FixedCostAnnualSummaryUseCase annualSummaryUseCase;
```

### 9-2. 新規エンドポイント追加

```java
/**
 * 年間固定費合計画面のGET要求マッピングです。
 */
@GetMapping("/annualsummary/")
public ModelAndView getAnnualSummary() {
    log.debug("getAnnualSummary:");
    return this.annualSummaryUseCase.readAnnualSummaryInfo(loginUserSession.getLoginUserInfo())
            .setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
            .build();
}
```

### 9-3. クラス Javadoc 更新

年間固定費合計画面への遷移を画面遷移フローに追記する。

---

## 10. 新規 HTML：`FixedCostAnnualSummary.html`

**配置パス**: `src/main/resources/templates/itemmanage/fixedcost/`

### タブナビゲーション部分

```html
<nav>
    <div class="nav nav-tabs mb-3" id="nav-tab" role="tablist">
        <a class="nav-link" th:href="@{/myhacbook/managebaseinfo/fixedcostinfo/initload/}">固定費管理</a>
        <button class="nav-link active" type="button" role="tab" aria-selected="true" disabled>年間固定費合計</button>
        <a class="nav-link" th:href="@{/myhacbook/managebaseinfo/fixedcostinfo/monthlydetail/(month=${targetMonth})}">月別固定費一覧</a>
    </div>
</nav>
```

### テーブル部分（抜粋）

```html
<p class="h6">■ 年間固定費合計 ■</p>
<p class="card-text">
    各月の固定費合計です。金額0の場合は「ー」を表示します。<br>
    <span class="text-muted small">
        ※列「固定費(生活費)」= 固定費(課税)から積立(投資)・積立金を除いた値
        （地代家賃・水光熱通費・税金支払い・保険料 など）
    </span>
</p>
<table class="table table-sm table-bordered table-hover">
    <thead>
        <tr class="table-secondary">
            <th class="w5" style="vertical-align:middle;">月</th>
            <th class="w7 text-end" style="vertical-align:middle;">事業経費</th>
            <th class="w7 text-end" style="vertical-align:middle;">固定費<br>(非課税)</th>
            <th class="w8 text-end" style="vertical-align:middle;">固定費<br>(生活費)</th>
            <th class="w7 text-end" style="vertical-align:middle;">積立<br>(投資)</th>
            <th class="w7 text-end" style="vertical-align:middle;">積立金</th>
            <th class="w7 text-end" style="vertical-align:middle;">衣類<br>住居設備</th>
            <th class="w7 text-end" style="vertical-align:middle;">飲食<br>日用品</th>
            <th class="w7 text-end" style="vertical-align:middle;">趣味<br>娯楽</th>
            <th class="w8 text-end" style="vertical-align:middle;">月合計</th>
            <th class="w5" style="vertical-align:middle;">詳細</th>
        </tr>
    </thead>
    <tbody>
        <tr th:each="row : ${annualSummaryRowList}"
            th:classappend="${row.totalRow} ? 'table-success' : (${row.evenMonth} ? 'table-warning' : '')">
            <td th:text="${row.monthLabel}">1月</td>
            <td class="text-end" th:text="${row.jigyouKeihi}">ー</td>
            <td class="text-end" th:text="${row.hikozei}">ー</td>
            <td class="text-end" th:text="${row.seikatsuhi}">ー</td>
            <td class="text-end" th:text="${row.tsumitateToushi}">ー</td>
            <td class="text-end" th:text="${row.tsumitateKin}">ー</td>
            <td class="text-end" th:text="${row.iruiJukyo}">ー</td>
            <td class="text-end" th:text="${row.inshoku}">ー</td>
            <td class="text-end" th:text="${row.shumiGoraku}">ー</td>
            <td class="text-end"><strong th:text="${row.monthTotal}">XXX円</strong></td>
            <td>
                <a th:if="${!row.totalRow}"
                   class="btn btn-sm btn-outline-primary"
                   th:href="@{/myhacbook/managebaseinfo/fixedcostinfo/monthlydetail/(month=${row.detailMonth})}">詳細へ</a>
            </td>
        </tr>
    </tbody>
</table>
```

---

## 11. テスト

### 11-1. `FixedCostAnnualSummaryListTest`（ドメインモデル単体テスト、C1網羅必須）

**配置**: `src/test/java/.../domain/model/account/fixedcost/`

`buildMonthlyRows()`、`buildYearlyRow()` および `determineColumn()` の C1 網羅テストを作成する。

`FixedCostAnnualSummaryItem` の生成には `level1SisyutuItemCode`・`level2SisyutuItemCode` を直接設定する
（SQL の ANCLVL1/ANCLVL2 結合結果を模倣）。

#### `buildMonthlyRows()` / `determineColumn()` テスト（①〜⑫）

| テスト | 内容 |
|--------|------|
| ① 空リスト | `buildMonthlyRows()` が全月・全列 ZERO を返すこと |
| ② Level-1 コード:0023、Level-2 コード:null（Level-1項目直接） | SEIKATSUHI 列に加算されること |
| ③ Level-1 コード:0023、Level-2 コード:0029（地代家賃） | SEIKATSUHI 列に加算されること |
| ④ Level-1 コード:0023、Level-2 コード:0031（積立(投資)） | TSUMITATE_TOUSHI 列に加算されること |
| ⑤ Level-1 コード:0023、Level-2 コード:0033（積立金） | TSUMITATE_KIN 列に加算されること |
| ⑥ Level-1 コード:0013（固定費(非課税)） | HIKOZEI 列に加算されること |
| ⑦ Level-1 コード:0001（事業経費） | JIGYOU_KEIHI 列に加算されること |
| ⑧ Level-1 コード:0045（衣類住居設備） | IRUI_JUKYO 列に加算されること |
| ⑨ Level-1 コード:0049（飲食日用品） | INSHOKU 列に加算されること |
| ⑩ Level-1 コード:0056（趣味娯楽） | SHUMI 列に加算されること |
| ⑪ 支払月コード全種（毎月/奇数月/偶数月/特定月/その他任意）| 各月で正しく加算・除外されること（`shouldAdd` C1網羅） |
| ⑫ 合計行の検証 | 12月の月合計と列合計が正しく計算されること |

> **注**: `shouldAdd()` のコードカバレッジは `FixedCostInquiryListTest` と重複するが、
> `FixedCostAnnualSummaryListTest` でも独立して検証する。

#### `buildYearlyRow()` テスト（⑬〜⑱）

| テスト | 内容 |
|--------|------|
| ⑬ 空リスト | `buildYearlyRow()` が全列 ZERO、年間合計 ZERO を返すこと |
| ⑭ 毎月支払（SEIKATSUHI） | 月額 10,000円 × 12か月 = 120,000円の年間合計が返ること |
| ⑮ 奇数月支払（HIKOZEI） | 月額 10,000円 × 6か月 = 60,000円の年間合計が返ること |
| ⑯ 偶数月支払（TSUMITATE_TOUSHI） | 月額 5,000円 × 6か月 = 30,000円の年間合計が返ること |
| ⑰ 特定月（5月）支払（TSUMITATE_KIN） | 月額 20,000円 × 1か月 = 20,000円の年間合計が返ること |
| ⑱ 複数カテゴリ混在 | 毎月：JIGYOU_KEIHI=10,000円、SEIKATSUHI=20,000円、HIKOZEI=5,000円 → 年間合計 420,000円（120,000+240,000+60,000）が返ること |

### 11-2. `FixedCostAnnualSummaryUseCaseIntegrationTest`（UseCase 結合テスト）

**配置**: `src/test/java/.../application/usecase/itemmanage/fixedcost/`

**テスト SQL**: `FixedCostAnnualSummaryIntegrationTest.sql`（新規作成）

**テストデータ設計**:

- user01: 固定費 4 件（支払月コード全種 + SISYUTU_ITEM_TABLE の階層データを含む）
  - 0001: 家賃（固定費(課税) > 地代家賃 > 家賃, 毎月, 80,000円）→ SEIKATSUHI
  - 0002: 電気代概算（固定費(課税) > 水光熱通費 > 電気代, 毎月, 8,000円）→ SEIKATSUHI
  - 0003: 国民年金保険（固定費(非課税) > 社会保険 > 国民年金保険, 毎月, 16,520円）→ HIKOZEI
  - 0004: 積立NISA（固定費(課税) > 積立(投資) > 積立NISA, 毎月, 20,000円）→ TSUMITATE_TOUSHI
- 現在対象年月: 2026年05月

**検証内容**:

| テスト | 内容 |
|--------|------|
| ① `testReadAnnualSummaryInfo_0件` | 固定費0件の場合、メッセージが設定されること |
| ② `testReadAnnualSummaryInfo_4件` | 12行+合計行が生成されること、月合計・カテゴリ別合計が正しいこと、targetMonth="05"が設定されること |

### 11-3. `MoneyTest`・`NullableMoneyTest`（既存テスト変更）

`MoneyTest` に以下を追加する（`Money` のサブクラス（`FixedCostPaymentTotalAmount` 等）を用いて検証）:
- `toZeroDashString_値が0円の場合ダッシュを返すこと`
- `toZeroDashString_正の金額の場合フォーマット済み文字列を返すこと`

`NullableMoneyTest` に以下を追加する:
- `toZeroDashString_値がnullの場合空文字列を返すこと`
- `toZeroDashString_値が0円の場合ダッシュを返すこと`
- `toZeroDashString_正の金額の場合フォーマット済み文字列を返すこと`

### 11-4. 既存テスト変更

| テスト | 変更内容 |
|--------|---------|
| `FixedCostInfoManageControllerIntegrationTest` | viewName の期待値を `"itemmanage/fixedcost/FixedCostInfoManageInit"` 等に更新。`FixedCostAnnualSummaryUseCase` DI 追加に伴う MockMvc セットアップ更新。`/annualsummary/` エンドポイントのテスト⑯ `testGetAnnualSummary` を追加（13行検証、targetMonth="11"、loginUserName検証）。 |
| `FixedCostInfoManageBulkUpdateControllerIntegrationTest` | viewName 変更に伴う更新。`FixedCostAnnualSummaryUseCase` DI 追加に伴う MockMvc セットアップ更新。 |

> **Controller 結合テスト向けの Controller クラス変更**: `FixedCostAnnualSummaryUseCase` の DI 追加のため、
> `FixedCostInfoManageController` のコンストラクタが変わる。MockMvc セットアップ時に対応すること。

> **Hamcrest インポートについて**: `FixedCostInfoManageControllerIntegrationTest` は
> `import static org.hamcrest.Matchers.*` のワイルドカードインポートを使用している（プロジェクト方針として Controller テストクラスのみ許容）。
> `hasSize(13)` / `is("11")` 等について IDE が null 型安全警告を出すが、動作上は問題なし。

---

## 12. 変更しないもの（影響なし）

- `FixedCostTableRepository` の既存メソッド（`findByUserId` 等）
- `FixedCostInquiryUseCase`・`FixedCostRegistConfirmUseCase`（メソッド変更なし）
- `FixedCostInquirySelectSql01.sql`・`FixedCostInquirySelectSql02.sql`（変更なし）
- `FixedCostInquiryList`・`FixedCost`・`FixedCostList`（変更なし）
- `ExpenditureItemInfoManage` 系クラス・HTML 一式（変更なし）

---

## 13. 作業順序

1. `Money` に `toZeroDashString()` 追加 ＋ `MoneyTest` にテスト追加
2. `NullableMoney` に `toZeroDashString()` 追加 ＋ `NullableMoneyTest` にテスト追加
3. `MyHouseholdAccountBookContent` に定数追加
4. パッケージ分割（Eclipseリファクタリング操作）
   - 4a. Response クラス 5件を `fixedcost/` サブパッケージへ移動（import 自動更新）
   - 4b. HTML 4件を `templates/itemmanage/fixedcost/` へ移動
   - 4c. 各 Response の viewName を変更
   - 4d. `mvn clean test` で既存テスト GREEN 確認（viewName 検証箇所の修正も含む）
5. `AbstractFixedCostItemListResponse` に `targetMonthValue` 追加
6. `FixedCostInquiryUseCase.setFixedCostItemList()` で `targetMonthValue` 設定
7. `FixedCostInfoManageInit.html` にタブナビゲーション追加
8. DTO 新規作成（`FixedCostAnnualSummaryReadDto`）
9. SQL 新規作成（`FixedCostAnnualSummarySelectSql01.sql`）
10. Mapper・DataSource・Repository IF にメソッド追加
11. ドメインモデル新規作成（`FixedCostAnnualSummaryList`）
12. ドメインモデル単体テスト新規作成（`FixedCostAnnualSummaryListTest`）
13. Response 新規作成（`FixedCostAnnualSummaryResponse`）
14. UseCase 新規作成（`FixedCostAnnualSummaryUseCase`）
15. Controller にエンドポイント追加
16. HTML 新規作成（`FixedCostAnnualSummary.html`）
17. テスト SQL 新規作成（`FixedCostAnnualSummaryIntegrationTest.sql`）
18. UseCase 結合テスト新規作成（`FixedCostAnnualSummaryUseCaseIntegrationTest`）
19. Controller 結合テスト更新（DI 追加対応）
20. `mvn clean test` で All GREEN 確認

---

## 14. 変更履歴

| 日付 | 内容 |
|------|------|
| 2026/05/17 | 初版作成 |
| 2026/05/23 | レビュー指摘対応（Level 5対応のSQL設計変更・Money/NullableMoneyメソッド追加・targetYear削除・TargetYearMonthリポジトリメソッド修正） |
| 2026/05/24 | DDD改善対応: `YearlyRow` 内部クラス（§4-5）・`buildYearlyRow()` メソッド（§4-6）を追加。年間合計集計ロジックを UseCase からドメイン層に移動。UseCase の `createAnnualSummaryRowList` 引数を `(List<MonthlyRow>, YearlyRow)` に変更（§7）。`FixedCostAnnualSummaryListTest` に `buildYearlyRow()` のテスト⑬〜⑱を追加（§11-1）。`FixedCostInfoManageControllerIntegrationTest` に `/annualsummary/` エンドポイントのテスト⑯ `testGetAnnualSummary` を追加（§11-4）。 |
