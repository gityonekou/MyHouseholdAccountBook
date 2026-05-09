# Feature② 固定費合計表示変更 設計書

## 概要

固定費一覧画面で表示している合計金額を「奇数月合計 / 偶数月合計」から
「現在対象月・+1か月・+2か月 の3か月分の合計」に変更する。

あわせて、固定費一覧の表示順を「支出項目表示順」のみから
「支出項目表示順 → 固定費支払月」の複合ソートに変更する。

---

## 変更対象ファイル一覧

| # | 区分 | ファイル | 変更内容 |
|---|------|---------|---------|
| 1 | ドメインタイプ | `TargetYearMonth.java` | `plusMonths()` / `toDisplayLabel()` メソッド追加 |
| 2 | ドメインモデル | `FixedCostInquiryList.java` | `oddMonthGoukei/anEvenMonthGoukei` 削除・`calculateMonthlyTotal()` メソッド追加 |
| 3 | UseCase | `FixedCostInquiryUseCase.java` | `setFixedCostItemList` 修正（年月取得・3か月合計設定） |
| 4 | Response | `AbstractFixedCostItemListResponse.java` | フィールド変更・ラベルフィールド追加 |
| 5 | HTML | `FixedCostInfoManageInit.html` | 合計行3行に変更・動的ラベル表示 |
| 6 | HTML | `FixedCostInfoManageActSelect.html` | 同上 |
| 7 | SQL | `FixedCostInquirySelectSql01.sql` | ORDER BY に FIXED_COST_SHIHARAI_TUKI 追加 |
| 8 | SQL | `FixedCostInquirySelectSql02.sql` | ORDER BY に FIXED_COST_SHIHARAI_TUKI 追加 |
| 9 | テスト(新規) | `TargetYearMonthTest.java` | ドメインタイプ単体テスト新規作成（C1網羅）|
| 10 | テスト(新規) | `FixedCostInquiryListTest.java` | ドメインモデル単体テスト新規作成（C1網羅必須） |
| 11 | テスト | `FixedCostInquiryUseCaseIntegrationTest.java` | 合計値検証を3か月分に更新・固定費5件に変更 |
| 12 | テスト | `FixedCostInquiryUseCaseBulkUpdateIntegrationTest.java` | 合計値検証を3か月分に更新・anyMatch→インデックス検証に変更 |
| 13 | テスト | `FixedCostRegistConfirmUseCaseIntegrationTest.java` | テストSQLに0005追加による件数変更（4→5件、execAddコード0005→0006） |
| 14 | テスト | `FixedCostInfoManageControllerIntegrationTest.java` | テストSQLに0005追加による hasSize(4)→hasSize(5) 変更（5箇所） |
| 15 | テスト | `FixedCostInfoManageBulkUpdateControllerIntegrationTest.java` | 変更なし |
| 16 | テストSQL | `FixedCostInquiryIntegrationTest.sql` | ソート確認用データ追加（1件） |
| 17 | テストSQL | `FixedCostBulkUpdateIntegrationTest.sql` | 0002の支払月を毎月→奇数月に変更（ソート確認用） |

---

## 1. ドメインタイプ変更：`TargetYearMonth`

### 1-1. 追加メソッド

Feature②に必要な最小限として以下2メソッドを追加する。

```java
/**
 * 指定した月数後の TargetYearMonth を返します。
 */
public TargetYearMonth plusMonths(int months) {
    YearMonth ym = YearMonth.of(Integer.parseInt(year.getValue()), Integer.parseInt(month.getValue()));
    YearMonth result = ym.plusMonths(months);
    return TargetYearMonth.from(String.valueOf(result.getYear()), String.format("%02d", result.getMonthValue()));
}

/**
 * "YYYY年MM月" 形式の表示ラベルを返します（月の先頭0あり）。
 * 例: 2025年11月、2026年01月
 */
public String toDisplayLabel() {
    return year.getValue() + "年" + month.getValue() + "月";
}
```

> `java.time.YearMonth` のインポートが必要。

---

## 2. ドメインモデル変更：`FixedCostInquiryList`

### 2-1. フィールド削除

以下フィールドを削除する（派生値のためフィールドとして保持しない）。

```java
// 削除
private final FixedCostPaymentTotalAmount oddMonthGoukei;
// 削除
private final FixedCostPaymentTotalAmount anEvenMonthGoukei;
```

### 2-2. `from()` メソッドの簡素化

**変更前**: リスト受け取り後に奇数月・偶数月合計を計算してフィールドに保持していた。  
**変更後**: 合計計算ロジックを除去し、リストを保持するだけにする。

```java
public static FixedCostInquiryList from(List<FixedCostInquiryItem> values) {
    if (CollectionUtils.isEmpty(values)) {
        return new FixedCostInquiryList(Collections.emptyList());
    } else {
        return new FixedCostInquiryList(values);
    }
}
```

### 2-3. 追加メソッド `calculateMonthlyTotal()`

指定した対象月に支払いが発生する固定費の合計を計算して返すメソッドを追加する。
UseCaseが対象月・+1か月・+2か月を渡して3回呼び出すことで3か月分の合計を得る。

```java
/**
 * 指定した対象月における固定費支払金額の合計を計算して返します。
 */
public FixedCostPaymentTotalAmount calculateMonthlyTotal(TargetYearMonth targetMonth) {
    if (CollectionUtils.isEmpty(values)) {
        return FixedCostPaymentTotalAmount.ZERO;
    }
    int monthValue = Integer.parseInt(targetMonth.getMonth());
    FixedCostPaymentTotalAmount total = FixedCostPaymentTotalAmount.ZERO;
    for (FixedCostInquiryItem item : values) {
        if (shouldAdd(item.getFixedCostTargetPaymentMonth().getValue(), monthValue)) {
            total = total.add(item.getFixedCostPaymentAmount());
        }
    }
    return total;
}
```

**判定メソッド `shouldAdd()` （private static）**:

| 支払月コード | 意味 | 加算条件 |
|-------------|------|---------|
| `"00"` | 毎月 | 常に加算 |
| `"20"` | 奇数月 | targetMonthの月が奇数なら加算 |
| `"30"` | 偶数月 | targetMonthの月が偶数なら加算 |
| `"01"`〜`"12"` | 特定月指定 | コード値の月とtargetMonthの月が一致なら加算 |
| `"40"` | その他任意 | 常に加算 |

```java
private static boolean shouldAdd(String shiharaiTukiCode, int monthValue) {
    switch (shiharaiTukiCode) {
        case MyHouseholdAccountBookContent.SHIHARAI_TUKI_EVERY_SELECTED_VALUE:    // "00"
        case MyHouseholdAccountBookContent.SHIHARAI_TUKI_OPTIONAL_SELECTED_VALUE: // "40"
            return true;
        case MyHouseholdAccountBookContent.SHIHARAI_TUKI_ODD_SELECTED_VALUE:      // "20"
            return monthValue % 2 == 1;
        case MyHouseholdAccountBookContent.SHIHARAI_TUKI_AN_EVEN_SELECTED_VALUE:  // "30"
            return monthValue % 2 == 0;
        default:
            // "01"〜"12"：コード値を数値に変換して月と比較
            return Integer.parseInt(shiharaiTukiCode) == monthValue;
    }
}
```

### 2-4. 影響範囲

- `FixedCostTableDataSource.findByUserId()` / `findByExpenditureItemCode()` の返却型・シグネチャ変更なし
- `from()` のシグネチャ変更なし
- UseCase側で `calculateMonthlyTotal()` を呼ぶだけで3か月合計が得られる

---

## 3. UseCase変更：`FixedCostInquiryUseCase`

### 3-1. フィールド追加（DI）

`AccountBookUserRepository` をDIに追加する（既存でDI済みの場合は確認して不要なら追加しない）。

```java
@Autowired
private AccountBookUserRepository accountBookUserRepository;
```

### 3-2. `setFixedCostItemList` 修正

**変更前**:
```java
private void setFixedCostItemList(UserId userId, AbstractFixedCostItemListResponse response) {
    FixedCostInquiryList searchResult = fixedCostRepository.findByUserId(SearchQueryUserId.from(userId));
    if (searchResult.isEmpty()) {
        response.addMessage("登録済み固定費情報が0件です。");
    } else {
        response.addFixedCostItemList(createFixedCostItemList(searchResult));
        response.setOddMonthGoukei(searchResult.getOddMonthGoukei().toFormatString());
        response.setAnEvenMonthGoukei(searchResult.getAnEvenMonthGoukei().toFormatString());
    }
}
```

**変更後**:
```java
private void setFixedCostItemList(UserId userId, AbstractFixedCostItemListResponse response) {
    // 固定費一覧を取得
    FixedCostInquiryList searchResult = fixedCostRepository.findByUserId(SearchQueryUserId.from(userId));
    if (searchResult.isEmpty()) {
        response.addMessage("登録済み固定費情報が0件です。");
    } else {
        // 現在の対象年月を取得（固定費0件ユーザへのNPE回避のためelse内で取得）
        NowTargetYearMonth nowTargetYearMonth = accountBookUserRepository.getNowTargetYearMonth(
                SearchQueryUserId.from(userId));
        // TargetYearMonth.plusMonths() で+1・+2か月を生成
        TargetYearMonth ym0 = nowTargetYearMonth.getYearMonth();
        TargetYearMonth ym1 = ym0.plusMonths(1);
        TargetYearMonth ym2 = ym0.plusMonths(2);
        response.addFixedCostItemList(createFixedCostItemList(searchResult));
        // 年月ラベルを設定（TargetYearMonth.toDisplayLabel() を利用）
        response.setTargetMonthLabel(ym0.toDisplayLabel());
        response.setTargetMonthPlus1Label(ym1.toDisplayLabel());
        response.setTargetMonthPlus2Label(ym2.toDisplayLabel());
        // 3か月合計を設定（FixedCostInquiryList.calculateMonthlyTotal() を利用）
        response.setTargetMonthGoukei(searchResult.calculateMonthlyTotal(ym0).toFormatString());
        response.setTargetMonthPlus1Goukei(searchResult.calculateMonthlyTotal(ym1).toFormatString());
        response.setTargetMonthPlus2Goukei(searchResult.calculateMonthlyTotal(ym2).toFormatString());
    }
}
```

> **設計上の注意**: `getNowTargetYearMonth()` を `else` ブロック内に置くことで、
> ACCOUNT_BOOK_USERテーブルに存在しないユーザ（固定費0件）のNPEを防ぐ。
> 固定費が存在しない場合はDB呼び出し自体が不要なため、この配置がより適切な設計となる。

---

## 4. Response変更：`AbstractFixedCostItemListResponse`

### 4-1. フィールド変更

**変更前**:
```java
// 奇数月合計
@Getter @Setter private String oddMonthGoukei;
// 偶数月合計
@Getter @Setter private String anEvenMonthGoukei;
```

**変更後**:
```java
// 対象月ラベル（例: "2025年11月"）
@Getter @Setter private String targetMonthLabel;
// 対象月+1ラベル（例: "2025年12月"）
@Getter @Setter private String targetMonthPlus1Label;
// 対象月+2ラベル（例: "2026年1月"）
@Getter @Setter private String targetMonthPlus2Label;
// 対象月合計
@Getter @Setter private String targetMonthGoukei;
// 対象月+1合計
@Getter @Setter private String targetMonthPlus1Goukei;
// 対象月+2合計
@Getter @Setter private String targetMonthPlus2Goukei;
```

### 4-2. `createModelAndView` 変更

**変更前**:
```java
modelAndView.addObject("oddMonthGoukei", oddMonthGoukei);
modelAndView.addObject("anEvenMonthGoukei", anEvenMonthGoukei);
```

**変更後**:
```java
modelAndView.addObject("targetMonthLabel", targetMonthLabel);
modelAndView.addObject("targetMonthPlus1Label", targetMonthPlus1Label);
modelAndView.addObject("targetMonthPlus2Label", targetMonthPlus2Label);
modelAndView.addObject("targetMonthGoukei", targetMonthGoukei);
modelAndView.addObject("targetMonthPlus1Goukei", targetMonthPlus1Goukei);
modelAndView.addObject("targetMonthPlus2Goukei", targetMonthPlus2Goukei);
```

---

## 5. HTML変更

### 5-1. `FixedCostInfoManageInit.html` / `FixedCostInfoManageActSelect.html`

両ファイルとも同一の合計行を修正する。

**変更前**（2行）:
```html
<tr class="table-success">
    <td colspan="4">奇数月合計(毎月、奇数月、その他任意の合計値)</td>
    <td th:text="${oddMonthGoukei}" align="right">奇数月合計</td>
    <td></td>
</tr>
<tr class="table-success">
    <td colspan="4">偶数月合計(毎月、偶数月、その他任意の合計値)</td>
    <td th:text="${anEvenMonthGoukei}" align="right">偶数月合計</td>
    <td></td>
</tr>
```

**変更後**（3行）:
```html
<tr class="table-success">
    <td colspan="4" th:text="${targetMonthLabel + '合計'}">YYYY年MM月合計</td>
    <td th:text="${targetMonthGoukei}" align="right">対象月合計</td>
    <td></td>
</tr>
<tr>
    <td colspan="4" th:text="${targetMonthPlus1Label + '合計'}">YYYY年MM月合計</td>
    <td th:text="${targetMonthPlus1Goukei}" align="right">対象月+1合計</td>
    <td></td>
</tr>
<tr class="table-success">
    <td colspan="4" th:text="${targetMonthPlus2Label + '合計'}">YYYY年MM月合計</td>
    <td th:text="${targetMonthPlus2Goukei}" align="right">対象月+2合計</td>
    <td></td>
</tr>
```

> 対象月・対象月+2は `table-success`（緑）、対象月+1は背景色なし（白）。

---

## 6. SQL変更（表示順変更）

### 6-1. `FixedCostInquirySelectSql01.sql`

**変更前**:
```sql
ORDER BY B.SISYUTU_ITEM_SORT
```

**変更後**:
```sql
ORDER BY B.SISYUTU_ITEM_SORT, A.FIXED_COST_SHIHARAI_TUKI
```

支払月コードの昇順: 毎月(`00`) → 01〜12月 → 奇数月(`20`) → 偶数月(`30`) → その他任意(`40`)

### 6-2. `FixedCostInquirySelectSql02.sql`

同様に `ORDER BY B.SISYUTU_ITEM_SORT, A.FIXED_COST_SHIHARAI_TUKI` に変更。

---

## 7. テスト変更

### 7-1. 新規作成：`FixedCostInquiryListTest.java`（単体テスト、C1網羅必須）

**テストの場所**: `src/test/java/.../domain/model/account/fixedcost/`

`FixedCostInquiryList.calculateMonthlyTotal()` のC1網羅テストを新規作成する。  
テストパラメータの対象年月は 2025/11（奇数月）をベースとし、3か月 = 11月(奇)・12月(偶)・01月(奇) で検証する。

| テストNo | テスト名 | 支払月コード | 検証内容 |
|---------|---------|------------|---------|
| ① | 空リスト | - | `calculateMonthlyTotal()` が全月0を返すこと |
| ② | 毎月(`00`) | `00` | 11月・12月・01月すべてに加算されること |
| ③ | 奇数月(`20`) | `20` | 11月(奇)・01月(奇)に加算、12月(偶)は加算されないこと |
| ④ | 偶数月(`30`) | `30` | 12月(偶)のみ加算、11月・01月(奇)は加算されないこと |
| ⑤ | 11月指定(`11`) | `11` | 11月のみ加算、12月・01月は加算されないこと |
| ⑥ | 1月指定(`01`) | `01` | 01月(+2か月目)のみ加算、11月・12月は加算されないこと |
| ⑦ | その他任意(`40`) | `40` | 11月・12月・01月すべてに加算されること |
| ⑧ | 複合パターン | 全種混在 | 各コードが正しく加算・除外されること |

> テスト⑤⑥で `default` 分岐（01〜12月指定）を網羅する。  
> ⑤は「対象月=11月に一致」、⑥は「+2か月目=1月に一致」のケースを確認する。

### 7-2. テストSQL変更・追加

#### `FixedCostInquiryIntegrationTest.sql` へのデータ追加

表示順（SISYUTU_ITEM_SORT, FIXED_COST_SHIHARAI_TUKI 複合ソート）の動作確認のため、
同一支出項目内に異なる支払月コードの固定費を1件追加する。

**追加データ**:
```sql
-- 0005: 電気代夏季割増 (0037:電気代, 区分:1=確定, 偶数月(30), 27日, 8,000円)
-- ※ 0002(電気代概算, tuki=00) と同一 SISYUTU_ITEM_SORT → FIXED_COST_SHIHARAI_TUKI でソート
('user01', '0005', '電気代夏季割増', '夏季偶数月のみ割増', '0037', '1', '30', NULL, '27', 8000.00, false)
```

**DB取得順（変更後）**:  
0003(sort=0201010000, tuki=20) → 0001(sort=0303010000, tuki=00) → 0002(sort=0306010000, tuki=00) → **0005(sort=0306010000, tuki=30)** → 0004(sort=0306020000, tuki=40)

**3か月合計（固定費5件、user01=2025年11月）**:

| 対象月 | 年月 | 加算される固定費 | 合計 |
|--------|------|----------------|------|
| targetMonth | 2025年11月(奇) | 0003(16,590,奇数)+0001(60,000,毎月)+0002(12,000,毎月)+0005(偶数=除外)+0004(10,000,任意) | **98,590円** |
| targetMonth+1 | 2025年12月(偶) | 0003(奇数=除外)+0001(60,000)+0002(12,000)+0005(8,000,偶数)+0004(10,000) | **90,000円** |
| targetMonth+2 | 2026年01月(奇) | 0003(16,590,奇数)+0001(60,000)+0002(12,000)+0005(偶数=除外)+0004(10,000) | **98,590円** |

ラベル: `"2025年11月"` / `"2025年12月"` / `"2026年01月"`

#### `FixedCostBulkUpdateIntegrationTest.sql` のデータ変更

0002(共益費) の支払月を `'00'`(毎月) → `'20'`(奇数月) に変更し、
同一支出項目(0030)内でソート順が検証できるようにする。

**変更前**:
```sql
('user01', '0002', '共益費', '毎月27日引き落とし', '0030', '1', '00', NULL, '27', 8000.00, false),
```

**変更後**:
```sql
('user01', '0002', '共益費', '奇数月27日引き落とし', '0030', '1', '20', NULL, '27', 8000.00, false),
```

**DB取得順（変更後）**:  
0001(sort=0303010000, tuki=00) → **0002(sort=0303010000, tuki=20)** → 0003(sort=0306010000, tuki=00)  
（同一SORT内で毎月=00 → 奇数月=20 の順になることを確認）

**3か月合計（固定費3件、user01=2025年11月）**:

| 対象月 | 年月 | 加算される固定費 | 合計 |
|--------|------|----------------|------|
| targetMonth | 2025年11月(奇) | 0001(60,000,毎月)+0002(8,000,奇数月)+0003(12,000,毎月) | **80,000円** |
| targetMonth+1 | 2025年12月(偶) | 0001(60,000)+0002(奇数月=除外)+0003(12,000) | **72,000円** |
| targetMonth+2 | 2026年01月(奇) | 0001(60,000)+0002(8,000,奇数月)+0003(12,000) | **80,000円** |

ラベル: `"2025年11月"` / `"2025年12月"` / `"2026年01月"`

### 7-3. 既存テストメソッドの変更内容

#### `FixedCostInquiryUseCaseIntegrationTest`

| テスト | 変更内容 |
|--------|---------|
| テスト⓪ `testReadInitInfo_0件` | `getOddMonthGoukei()` → `getTargetMonthGoukei()` 等、nullチェックを3か月分に変更 |
| テスト① `testReadInitInfo_5件` | ①固定費が5件に増加（4件→5件）<br>②DB取得順コメントを5件で更新<br>③5件目(0005: 電気代夏季割増)の検証追加<br>④合計値を3か月分(98,590/90,000/98,590)に変更<br>⑤ラベル検証(2025年11月/2025年12月/2026年01月)を追加 |
| テスト②④⑥⑧⑩ (`readActSelectItemInfo`系) | fixedCostItemList が5件になるため件数を変更 |

#### `FixedCostInquiryUseCaseBulkUpdateIntegrationTest`

**重要**: `stream().anyMatch` での検証はソート順を確認できないため、**インデックスアクセスに変更**する。

| テスト | 変更内容 |
|--------|---------|
| テスト① `testReadActSelectItemInfo_0001` | ①`get(0)`:0001(家賃), `get(1)`:0002(共益費) のインデックス検証に変更<br>②0002の支払月コンテキストを「毎月」→「奇数月」に変更<br>③3か月合計(80,000/72,000/80,000)とラベルの検証を追加 |
| テスト② `testReadActSelectItemInfo_0003` | 合計値の検証(80,000/72,000/80,000)とラベルの検証を追加 |
| テスト③ `testReadBulkUpdateInfo_success` | `anyMatch` → `get(0)`:0001, `get(1)`:0002 のインデックス検証に変更 |
| テスト⑤ `testReadBulkUpdateBindingErrorSetInfo` | 同様にインデックス検証に変更 |

#### `FixedCostRegistConfirmUseCaseIntegrationTest`

テストSQL（`FixedCostInquiryIntegrationTest.sql`）に0005を追加したことで固定費が5件になったため、
件数検証を変更する。

| テスト | 変更内容 |
|--------|---------|
| `testExecAdd_success` | before件数4→5、after件数5→6、自動採番コード「0005」→「0006」に変更 |
| `testExecUpdate_success` | before件数4→5に変更 |

#### `FixedCostInfoManageControllerIntegrationTest`

テストSQL（`FixedCostInquiryIntegrationTest.sql`）に0005を追加したことで固定費が5件になったため、
`hasSize(4)` → `hasSize(5)` に変更する（5箇所）。

#### `FixedCostInfoManageBulkUpdateControllerIntegrationTest`

別SQLを使用しており影響なし。**変更なし**。

---

## 8. 変更しないもの（影響なし）

- `FixedCostTableRepository.java`（リポジトリIF変更なし）
- `FixedCostTableDataSource.java`（`from()` シグネチャ変更なし）
- `FixedCostTableListSelectSql01.sql`（月指定リスト検索用、収支登録画面で使用）
- `FixedCostTableCountSql01/02.sql`
- `FixedCostTableSelectSql01.sql`（1件取得）
- `FixedCostInfoManageUpdateResponse`（更新画面、合計表示なし）
- `FixedCostBulkUpdateResponse`（一括更新画面、合計表示なし）

---

## 9. 作業順序

1. ドメインタイプ変更（`TargetYearMonth.java`）
2. ドメインモデル変更（`FixedCostInquiryList.java`）
3. UseCase変更（`FixedCostInquiryUseCase.java`）
4. Response変更（`AbstractFixedCostItemListResponse.java`）
5. HTML変更（2ファイル）
6. SQL変更（2ファイル）
7. テストSQL変更（`FixedCostInquiryIntegrationTest.sql`, `FixedCostBulkUpdateIntegrationTest.sql`）
8. 単体テスト新規作成（`TargetYearMonthTest.java`, `FixedCostInquiryListTest.java`）
9. 既存テスト修正（`FixedCostInquiryUseCaseIntegrationTest.java`, `FixedCostInquiryUseCaseBulkUpdateIntegrationTest.java`, `FixedCostRegistConfirmUseCaseIntegrationTest.java`, `FixedCostInfoManageControllerIntegrationTest.java`）
10. `mvn clean test` でAll GREEN確認（897件）

---

## 10. 変更履歴

| 日付 | 内容 |
|------|------|
| 2026/05/07 | 初版作成 |
| 2026/05/07 | 設計見直し：`calculateMonthlyTotal()` メソッド化・`TargetYearMonth` への年月計算集約・ソート確認用テストデータ追加・C1網羅単体テスト追加 |
| 2026/05/09 | 実装完了に伴う設計書修正：①UseCase実装の `getNowTargetYearMonth` 配置変更（else内に移動・NPE回避）②ラベル先頭0表記統一（"2026年01月"）③変更対象ファイル追記（TargetYearMonthTest・FixedCostRegistConfirmUseCaseIntegrationTest・FixedCostInfoManageControllerIntegrationTest）④実装ステータス：**完了（897件 GREEN）** |
