# Feature1: 支出別一覧追加 設計書

## 1. 概要

### 1.1 目的

月別収支確認画面（`AccountMonth.html`）に「支出別一覧」表示機能を追加する。
現在の「支出項目別」表示に加え、支出テーブル（EXPENDITURE_TABLE）の1件1行表示に切り替えられるようにする。

### 1.2 対象ブランチ

`feature-1.02-dev1`

### 1.3 対象画面・クラス

| 種別 | クラス名 / ファイル名 |
|------|----------------------|
| Controller | `AccountMonthInquiryController` |
| Controller（訂正リダイレクト先） | `IncomeAndExpenditureRegistController` |
| UseCase | `AccountMonthInquiryUseCase` |
| Response | `AccountMonthInquiryResponse` |
| ドメインモデル（新規） | `AccountMonthInquiryExpenditureList` |
| ドメインタイプ（修正） | `NullableDateValue` |
| HTML | `AccountMonth.html` |

---

## 2. 画面仕様

### 2.1 表示切替ボタン

「支出一覧」エリアの先頭に、表示切替ボタンを追加する。

| 状態 | 支出項目別ボタン | 支出別ボタン |
|------|----------------|------------|
| 支出項目別表示中 | プライマリ（disabled） | アウトライン（submit） |
| 支出別表示中 | アウトライン（submit） | プライマリ（disabled） |

参考：紙芝居 `AccountBookMonth_支出項目別.html`・`AccountBookMonth_支出別.html`

### 2.2 表示切替の仕様

- **viewType** パラメータで表示種別を管理する
  - `"item"` → 支出項目別（現状の表示。デフォルト）
  - `"expenditure"` → 支出別（新規追加）
- 切り替えはサーバーサイド POST で行う
- 前月・次月ボタンのフォームに `viewType` を hidden で引き継ぐ
- 以下の画面遷移では常に `viewType=item`（支出項目別）で表示する
  - 初期表示（GET）
  - 登録完了後のリダイレクト表示（`/registComplete/`）
  - 他タブからの遷移時（`POST /myhacbook/accountinquiry/accountmonth/`）

### 2.3 支出別一覧の表示項目

紙芝居：`AccountBookMonth_支出別.html`

#### テーブルヘッダ

| カラム | クラス |
|--------|-------|
| 支出名(区分) | `w20` |
| 支払日 | `w5` |
| 支出金額 | `w10`（右寄せ） |
| （訂正ボタン） | `w5`（中央寄せ） |
| 支出詳細 | なし |

#### テーブル行

- **支出名(区分)**：支出区分に応じてプレフィックスを付与
  - 無駄遣い（重度）`ExpenditureCategory=3` → `【無駄遣いC】` + 支出名
  - 無駄遣い（軽度）`ExpenditureCategory=2` → `【無駄遣いB】` + 支出名
  - 無駄遣いなし `ExpenditureCategory=1` → 支出名のみ
- **支払日**：日のみ表示（例：`27日`）。支払日が null の場合は空文字
- **支出金額**：`toFormatString()` 形式（例：`16,800円`）
- **訂正ボタン**：`text-bg-success btn-sm` のボタン（POST submit）
- **支出詳細**：`ExpenditureDetailContext` の値（空の場合は空表示）

#### 行のハイライト

- **奇数行**（1行目・3行目・5行目…）：`table-warning` クラスを付与
- **偶数行**（2行目・4行目・6行目…）：クラスなし

Thymeleaf では `th:each` の iteration status を使用して実装する。

```html
<th:block th:each="item, iterStat : ${expenditureList}">
    <tr th:class="${iterStat.odd} ? 'table-warning' : ''">
```

> **注意**：行ハイライトは行番号（奇偶）で決まる。支出区分（`ExpenditureCategory`）は `displayName` のプレフィックス表示にのみ使用する。

#### 最終行（合計行）

| 項目 | colspan | 表示 |
|------|---------|------|
| 支出合計（ラベル） | 2 | 固定文字列 |
| 支出合計金額 | 1 | 全行の支出金額合計 |
| 空白 | 2 | なし |

合計行のクラス：`table-success`

### 2.4 訂正ボタンの動作

1. 支出別一覧の各行の「訂正」ボタンを押下する
2. `POST /myhacbook/accountinquiry/accountmonth/dispatchaction/?expenditureCorrect` にリクエスト
   - パラメータ: `targetYearMonth`（対象年月）、`expenditureCode`（支出コード）
3. Controller が `GET /myhacbook/accountregist/incomeandexpenditure/expenditurecorrectload/` にリダイレクト
   - RedirectAttributes 経由で `targetYearMonth`・`expenditureCode` を引き継ぐ
4. `IncomeAndExpenditureRegistController.getExpenditureCorrectionLoad()` が：
   1. `registListSession.clearData(targetYearMonth, targetYearMonth)` でセッション初期化
   2. `usecase.readUpdateInfo()` で当月の収支データを読込・セッション設定
   3. `expenditureRegistUseCase.readExpenditureUpdateSelect()` で指定支出コードの訂正フォームを表示

---

## 3. クラス設計

### 3.1 新規クラス：`AccountMonthInquiryExpenditureList`

**パッケージ**：`com.yonetani.webapp.accountbook.domain.model.account.inquiry`

支出テーブル（EXPENDITURE_TABLE）から取得した支出情報を月別収支確認画面の「支出別一覧」表示用にラップするドメインモデル。

#### 内部クラス：`ExpenditureRow`

| フィールド | 型 | 説明 |
|-----------|-----|------|
| `expenditureCode` | `ExpenditureCode` | 支出コード（訂正ボタンの POST パラメータ用） |
| `expenditureName` | `ExpenditureName` | 支出名称 |
| `expenditureCategory` | `ExpenditureCategory` | 支出区分 |
| `paymentDate` | `PaymentDate` | 支払日（null 許容） |
| `expenditureAmount` | `ExpenditureAmount` | 支出金額 |
| `expenditureDetailContext` | `ExpenditureDetailContext` | 支出詳細 |

ファクトリメソッド：`ExpenditureRow.from(ExpenditureItem item)`

#### リストクラス：`AccountMonthInquiryExpenditureList`

| フィールド | 型 | 説明 |
|-----------|-----|------|
| `values` | `List<ExpenditureRow>` | 支出行リスト |
| `totalAmount` | `ExpenditureTotalAmount` | 支出合計金額（`from()` 内で計算） |

| メソッド | 戻り値 | 説明 |
|---------|--------|------|
| `from(ExpenditureItemInquiryList list)` | `AccountMonthInquiryExpenditureList` | ファクトリメソッド。リストを変換し合計を計算 |
| `isEmpty()` | `boolean` | リストが空の場合 true |
| `getValues()` | `List<ExpenditureRow>` | Lombok @Getter |
| `getTotalAmount()` | `ExpenditureTotalAmount` | Lombok @Getter |

---

### 3.2 修正クラス：`NullableDateValue`

**パッケージ**：`com.yonetani.webapp.accountbook.domain.type.common`

#### 追加メソッド：`toDayString()`

```
/**
 * 日付を「DD日」形式の文字列で返します。
 * null 値の場合は空文字列を返します。
 */
public String toDayString() {
    if(isNull()) { return ""; }
    return value.getDayOfMonth() + "日";
}
```

---

### 3.3 修正クラス：`AccountMonthInquiryResponse`

#### 追加 内部クラス：`ExpenditureRow`（レスポンス DTO）

| フィールド | 型 | 説明 |
|-----------|-----|------|
| `expenditureCode` | `String` | 支出コード（訂正 POST パラメータ用） |
| `displayName` | `String` | 支出名（区分プレフィックス付き） |
| `paymentDay` | `String` | 支払日（"DD日"形式、null → 空文字） |
| `expenditureAmount` | `String` | 支出金額（フォーマット済み） |
| `expenditureDetailContext` | `String` | 支出詳細 |

> **注意**：行のハイライト（奇数行 = `table-warning`）は HTML テンプレート側で `iterStat.odd` により制御するため、Response DTO に判定フィールドは持たない。

ファクトリメソッド：`ExpenditureRow.from(String expenditureCode, String displayName, String paymentDay, String expenditureAmount, String expenditureDetailContext)`

> **DDD準拠ポイント**：レスポンス DTO はプレゼンテーション層のクラスであり、ドメインモデルを直接参照してはならない。ドメイン→DTO の変換（プレフィックス付与・フォーマット変換含む）はすべて UseCase 層の `convertExpenditureList()` で実施し、変換済みの値を本ファクトリメソッドに渡す。

#### 追加フィールド（既存クラスへ）

| フィールド | 型 | デフォルト | 説明 |
|-----------|-----|----------|------|
| `viewType` | `String` | `"item"` | 表示種別（`@Setter`） |
| `expenditureList` | `List<ExpenditureRow>` | 空リスト | 支出別一覧 |
| `expenditureTotalAmount` | `String` | `null` | 支出合計（フォーマット済み、`@Setter`） |

#### 追加メソッド

- `addExpenditureList(List<ExpenditureRow> addList)`：支出別一覧をセット
- `buildWithData()` へ `viewType`・`expenditureList`・`expenditureTotalAmount` の Model 追加

---

### 3.4 修正クラス：`AccountMonthInquiryUseCase`

#### 追加フィールド

```java
// 指定月の支出情報を取得するリポジトリー
private final ExpenditureTableRepository expenditureRepository;
```

#### 変更メソッド：公開 `read()` メソッドに `viewType` 追加

| 既存メソッド | 変更後 |
|-------------|-------|
| `read(LoginUserInfo user)` | viewType を `"item"` で固定し `execRead()` に渡す（シグネチャ変更なし） |
| `read(LoginUserInfo user, String targetYearMonth)` | 同上（シグネチャ変更なし） |
| `read(LoginUserInfo user, String targetYearMonth, String returnYearMonth)` | 同上（シグネチャ変更なし） |

> **設計方針**：Controller から渡す viewType は `execRead()` の引数とし、既存の public メソッドは後方互換のためシグネチャを変更しない。viewType を受け取る新規 overload を追加する形で対応する。

#### 追加メソッド（public）

```java
public AccountMonthInquiryResponse read(LoginUserInfo user, String targetYearMonth, String returnYearMonth, String viewType)
```

> **注意**：3引数版 `read(user, ym, viewType)` は追加しない。既存メソッド `read(user, ym, returnYm)` とシグネチャが競合するため、4引数版のみとする。

#### 変更メソッド：`execRead()`

引数に `String viewType` を追加。

```java
private AccountMonthInquiryResponse execRead(
    LoginUserInfo user,
    AccountMonthInquiryTargetYearMonthInfo targetYearMonthInfo,
    String viewType)
```

`viewType` は `"expenditure"` のみ有効。それ以外はすべて `"item"` として扱う（正規化）。

**処理順番（①〜⑦）**

```
① SisyutuKingakuTable から支出金額情報リストを取得
② IncomeAndExpenditureTable から収支集約を取得
③ ExpenditureTable から支出情報リストを取得（viewType によらず常時取得）
    ※ ④の整合性検証で支出テーブルの有無チェックが必要なため常時取得する
④ データ存在の整合性検証（IncomeAndExpenditureConsistencyService.validateDataExistence()）
    ・収支データなし & 支出金額データあり → DataInconsistencyException
    ・収支データなし & 支出データあり    → DataInconsistencyException
⑤ 収支情報をレスポンスに設定（viewType 非依存）
    ・収支データなし → メッセージ設定 & 早期リターン
    ・収支データあり → 収入/支出/収支各金額を設定
⑥ viewType=item のみ：支出金額情報リストをレスポンスに設定（convertExpenditureItemList()）
⑦ viewType=expenditure のみ：支出別一覧をレスポンスに設定（convertExpenditureList()）および支出合計金額を設定
```

#### 追加メソッド（private）

```java
private List<AccountMonthInquiryResponse.ExpenditureRow> convertExpenditureList(
    AccountMonthInquiryExpenditureList list)
```

`ExpenditureRow` への変換時、支出区分のプレフィックス付与は `ExpenditureCategory.toDisplayLabel()` に委譲する。

```java
String label = domain.getExpenditureCategory().toDisplayLabel();
String prefix = label.isEmpty() ? "" : "【" + label + "】";
```

---

### 3.5 修正クラス：`ExpenditureCategory`（ドメインタイプ）

**パッケージ**：`com.yonetani.webapp.accountbook.domain.type.account.expenditure`

#### 追加メソッド：`toDisplayLabel()`

支出区分に対応する表示ラベル文字列を返す。`【】` ブラケット付与などの表示整形は UseCase 側で行う。

```java
public String toDisplayLabel() {
    if (this.equals(WASTED_C)) { return MyHouseholdAccountBookContent.WASTED_C_VIEW_VALUE; }
    if (this.equals(WASTED_B)) { return MyHouseholdAccountBookContent.WASTED_B_VIEW_VALUE; }
    return "";
}
```

| 支出区分 | 戻り値 |
|---------|-------|
| `WASTED_C`（無駄遣い・重度） | `"無駄遣いC"` |
| `WASTED_B`（無駄遣い・軽度） | `"無駄遣いB"` |
| `NON_WASTED`（無駄遣いなし） | `""` （空文字） |

> **DDD設計根拠**：「無駄遣いC」「無駄遣いB」という表示ラベルの対応関係はドメイン知識であり、UseCase 層や presentation 層で分散して持つべきではない。ドメインタイプ自身が知識を保持することで、表示名の変更が一箇所に局所化される。

---

### 3.6 修正クラス：`IncomeAndExpenditureConsistencyService`（ドメインサービス）

**パッケージ**：`com.yonetani.webapp.accountbook.domain.service.account.inquiry`

#### 変更メソッド：`validateDataExistence()` に引数追加

第4引数 `AccountMonthInquiryExpenditureList` を追加し、支出テーブルデータの整合性チェックを追加する。

```java
public void validateDataExistence(
    IncomeAndExpenditure aggregate,
    AccountMonthInquiryExpenditureItemList expenditureItemList,
    AccountMonthInquiryExpenditureList expenditureList,
    SearchQueryUserIdAndYearMonth searchCondition)
```

**追加チェック**：収支データなし & 支出データあり → `DataInconsistencyException`

---

### 3.7 修正クラス：`AccountMonthInquiryController`

#### 変更：既存エンドポイント

| メソッド | 変更内容 |
|---------|--------|
| `getInitAccountMonth()` | viewType=`"item"` で UseCase を呼び出し（変更なし・UseCase 側 default） |
| `postAccountMonth()` | viewType=`"item"` で UseCase を呼び出し（タブ遷移は常に item） |
| `postBeforeAccountMonth()` | `@RequestParam(value="viewType", defaultValue="item")` 追加 → UseCase に渡す |
| `postNextAccountMonth()` | 同上 |
| `registComplete()` | viewType=`"item"` で UseCase を呼び出し（変更なし） |

#### 追加：エンドポイント

**表示切替**

```
POST /myhacbook/accountinquiry/accountmonth/targetcontrol/
パラメータ name="viewTypeSwitch"
```

```java
@PostMapping(value="/targetcontrol/", params = "viewTypeSwitch")
public ModelAndView postViewTypeSwitch(
    @RequestParam("targetYearMonth") String targetYearMonth,
    @RequestParam("viewType") String viewType)
```

UseCase の `read(user, targetYearMonth, targetYearMonth, viewType)` を呼び出し、`buildWithData()` を返す。

> **実装ポイント**：表示切替は同月内の操作であるため `returnYearMonth=targetYearMonth` として4引数版を呼び出す。3引数版 `read(user, ym, viewType)` は既存メソッドとのシグネチャ競合により追加しない。

> **前提条件**：表示切替は収支データが存在する月のみ（支出別ボタンは収支データがある場合のみ表示）。したがって `syuusiDataFlg` チェックは不要。

**訂正**

```
POST /myhacbook/accountinquiry/accountmonth/dispatchaction/
パラメータ name="expenditureCorrect"
```

```java
@PostMapping(value = "/dispatchaction/", params = "expenditureCorrect")
public ModelAndView postExpenditureCorrect(
    @RequestParam("targetYearMonth") String targetYearMonth,
    @RequestParam("expenditureCode") String expenditureCode,
    RedirectAttributes redirectAttributes)
```

`RedirectAttributes` に `targetYearMonth`・`expenditureCode` を設定し、
`redirect:/myhacbook/accountregist/incomeandexpenditure/expenditurecorrectload/` にリダイレクト。

---

### 3.8 修正クラス：`IncomeAndExpenditureRegistController`

#### 追加：エンドポイント

```
GET /myhacbook/accountregist/incomeandexpenditure/expenditurecorrectload/
```

```java
@GetMapping("/expenditurecorrectload/")
public ModelAndView getExpenditureCorrectionLoad(
    @RequestParam("targetYearMonth") String targetYearMonth,
    @RequestParam("expenditureCode") String expenditureCode)
```

処理内容：
1. `registListSession.clearData(targetYearMonth, targetYearMonth)` でセッション初期化
2. `usecase.readUpdateInfo(loginUserSession.getLoginUserInfo(), targetYearMonth)` で当月収支データ読込
3. セッションに収入・支出リストを設定
4. `expenditureRegistUseCase.readExpenditureUpdateSelect(...)` で支出訂正フォームを返却

---

## 4. HTML 設計

### 4.1 `AccountMonth.html` 変更箇所

#### 変更①：収支エリアに「■ 収支 ■」見出しを追加

現行 `AccountMonth.html` の「月毎収支まとめ表示エリア」（54行目）に見出しが存在しないため、収支テーブルの直前に追加する（紙芝居 `AccountBookMonth_支出項目別.html`・`AccountBookMonth_支出別.html` 116行目の `<p class="h6">■ 収支 ■</p>` に対応）。

```html
<!-- 月毎収支まとめ表示エリア -->
<div class="card-body">
    <p class="h6">■ 収支 ■</p>   ← 追加
    <table class="table table-sm table-bordered">
```

#### 変更②：前月・次月フォームに viewType の hidden 追加

`<form id="ChangeAccountMonth" ...>` に以下を追加：

```html
<input type="hidden" name="viewType" th:value="${viewType}" />
```

#### 変更③：支出一覧エリアに表示切替ボタンを追加

支出項目テーブルの上（`th:block th:if="${#lists.size(expenditureItemList)}>0"` の外側・前）に追加。
収支データがある場合のみ表示（支出項目別リストと同じ条件）。

現行 `AccountMonth.html` には「■ 支出一覧 ■」見出しが存在しないため、切替ボタンと合わせて追加する（紙芝居の `<p class="h6">■ 支出一覧 ■</p>` に対応）。

```html
<!-- 支出一覧 見出し＋表示切替ボタン -->
<p class="h6">■ 支出一覧 ■</p>
<div class="mb-3">
  <form name="viewTypeForm" class="d-flex" method="post"
        th:action="@{/myhacbook/accountinquiry/accountmonth/targetcontrol/}">
    <!-- 支出項目別ボタン -->
    <button th:if="${viewType == 'item'}"
            class="btn btn-primary" type="button" disabled>支出項目別</button>
    <button th:if="${viewType != 'item'}"
            class="btn btn-outline-primary" name="viewTypeSwitch" type="submit">支出項目別</button>
    <!-- 支出別ボタン -->
    <button th:if="${viewType == 'expenditure'}"
            class="btn btn-primary" type="button" disabled>支出別</button>
    <button th:if="${viewType != 'expenditure'}"
            class="btn btn-outline-primary" name="viewTypeSwitch" type="submit">支出別</button>
    <input type="hidden" th:field="*{targetYearMonth}" />
    <!-- 切り替え先 viewType（現在 item → expenditure へ、現在 expenditure → item へ） -->
    <input type="hidden" name="viewType"
           th:value="${viewType == 'item'} ? 'expenditure' : 'item'" />
  </form>
</div>
```

#### 変更④：支出別一覧テーブルを追加

`th:block th:if="${#lists.size(expenditureItemList)}>0"` ブロックの後（支出ボタンエリアの前）に追加。

```html
<!-- 支出別一覧（viewType=expenditure の場合のみ表示） -->
<th:block th:if="${viewType == 'expenditure'}">
<div class="card-body">
  <table class="table table-sm table-bordered">
    <thead>
      <tr class="table-secondary">
        <th scope="col" class="w20">支出名(区分)</th>
        <th scope="col" class="w5">支払日</th>
        <th scope="col" class="w10" style="vertical-align:middle; text-align:center;">支出金額</th>
        <th scope="col" class="w5">&emsp;</th>
        <th scope="col">支出詳細</th>
      </tr>
    </thead>
    <tbody>
      <th:block th:each="item : ${expenditureList}">
        <tr th:class="${item.severeWaste} ? 'table-warning' : ''">
          <td class="w20" th:text="${item.displayName}">支出名(区分)</td>
          <td class="w5" th:text="${item.paymentDay}">支払日</td>
          <td class="w10" align="right" th:text="${item.expenditureAmount}">支出金額</td>
          <td class="w5" align="center">
            <form method="post"
                  th:action="@{/myhacbook/accountinquiry/accountmonth/dispatchaction/}">
              <button class="btn text-bg-success btn-sm"
                      name="expenditureCorrect" type="submit">訂正</button>
              <input type="hidden" name="targetYearMonth" th:value="*{targetYearMonth}" />
              <input type="hidden" name="expenditureCode" th:value="${item.expenditureCode}" />
            </form>
          </td>
          <td th:text="${item.expenditureDetailContext}">支出詳細</td>
        </tr>
      </th:block>
      <!-- 支出合計行 -->
      <tr class="table-success">
        <td class="w25" colspan="2">支出合計</td>
        <td class="w10" align="right" th:text="${expenditureTotalAmount}">支出合計</td>
        <td colspan="2"></td>
      </tr>
    </tbody>
  </table>
</div>
</th:block>
```

---

## 5. シーケンス図

### 5.1 初期表示（支出項目別）

```
Browser      Controller        UseCase             Repository
  |               |               |                    |
  |  GET /        |               |                    |
  |-------------->|               |                    |
  |               | read(user)    |                    |
  |               |-------------->|  select(query)     |
  |               |               |------------------->|
  |               |               |<-------------------|
  |               |               | findByPrimaryKey() |
  |               |               |------------------->|
  |               |               |<-------------------|
  |               |<--------------|                    |
  |  AccountMonth |               |                    |
  | (viewType=item)|              |                    |
  |<--------------|               |                    |
```

### 5.2 支出別に切替（viewType=expenditure）

```
Browser          Controller             UseCase               Repository
  |                   |                    |                       |
  | POST viewTypeSwitch|                   |                       |
  | targetYearMonth    |                   |                       |
  | viewType=expenditure                   |                       |
  |------------------->|                   |                       |
  |                    | read(user,ym,vt)  |                       |
  |                    |------------------>|  select(query)        |
  |                    |                   |---------------------->|
  |                    |                   |<----------------------|
  |                    |                   | findByPrimaryKey()    |
  |                    |                   |---------------------->|
  |                    |                   |<----------------------|
  |                    |                   | findBy(query) ※新規  |
  |                    |                   |---------------------->|
  |                    |                   |<----------------------|
  |                    |<------------------|                       |
  | AccountMonth       |                   |                       |
  | (viewType=expenditure)                 |                       |
  |<-------------------|                   |                       |
```

### 5.3 訂正ボタン押下

```
Browser         Controller(Inquiry)   Controller(Regist)   UseCase(Init/Expend)
  |                  |                       |                      |
  | POST expenditureCorrect                  |                      |
  | targetYearMonth                          |                      |
  | expenditureCode                          |                      |
  |----------------->|                       |                      |
  |                  | redirect              |                      |
  |                  |  expenditurecorrectload?                    |
  |                  |  targetYearMonth=xxx                        |
  |                  |  expenditureCode=xxx                        |
  |<-----------------|                       |                      |
  | GET expenditurecorrectload               |                      |
  |---------------------------------------->|                      |
  |                  |                       | registListSession.clearData()
  |                  |                       | readUpdateInfo()    |
  |                  |                       |-------------------->|
  |                  |                       |<--------------------|
  |                  |                       | registListSession 設定      |
  |                  |                       | readExpenditureUpdateSelect()|
  |                  |                       |-------------------->|
  |                  |                       |<--------------------|
  |    収支登録画面（支出訂正フォーム）         |                      |
  |<----------------------------------------|                      |
```

---

## 6. データ設計

### 6.1 使用テーブル（追加）

| テーブル | 用途 | リポジトリ | メソッド |
|---------|------|----------|---------|
| EXPENDITURE_TABLE | 支出別一覧取得 | `ExpenditureTableRepository` | `findBy(SearchQueryUserIdAndYearMonth)` |

返却型：`ExpenditureItemInquiryList`（既存）

---

## 7. テスト方針

### 7.1 ドメインモデルテスト（Unit）

`AccountMonthInquiryExpenditureListTest`

| # | テストケース |
|---|------------|
| ① | 空リストで `from()` → `isEmpty()=true`、`totalAmount=0` |
| ② | 1件（無駄遣いなし）で `from()` → `isEmpty()=false`、`totalAmount` 正しい |
| ③ | 複数件で `from()` → `totalAmount` は全件の合計 |
| ④ | 無駄遣いC の行の `expenditureCategory` が `ExpenditureCategory.WASTED_C` |
| ⑤ | 無駄遣いB の行の `expenditureCategory` が `ExpenditureCategory.WASTED_B` |
| ⑥ | 無駄遣いなし の行の `expenditureCategory` が `ExpenditureCategory.NON_WASTED` |
| ⑦ | 支払日あり → `paymentDate.toDayString()` で `DD日` 形式 |
| ⑧ | 支払日 null → `paymentDate.toDayString()` で 空文字 |

> **設計変更**：④⑤⑥は当初 `displayName` のプレフィックスを検証する予定だったが、プレフィックス生成ロジックが `ExpenditureCategory.toDisplayLabel()` → UseCase へ移動したため、ドメインモデルテストでは `expenditureCategory` フィールドの値を検証するものに変更した。プレフィックス付与の検証は UseCase 統合テスト（7.2 ⑦ ⑧）で担う。

### 7.2 UseCase 統合テスト

`AccountMonthInquiryIntegrationTest`（既存ファイルへ追加）

| # | テストケース |
|---|------------|
| ① | `read(user, ym)` → データが正常取得できる（既存） |
| ② | `read(user, ym, returnYm, "expenditure")` → 支出別リストが設定される |
| ③ | `read(user, ym, returnYm, "expenditure")` → 支出合計金額が正しい |
| ④ | `read(user, ym, returnYm, "expenditure")` → NON_WASTED の displayName はプレフィックスなし |
| ⑤ | `read(user, ym, returnYm, "expenditure")` → WASTED_B の displayName に `【無駄遣いB】` プレフィックスがつく |
| ⑥ | ~~WASTED_C の displayName に `【無駄遣いC】` プレフィックスがつく~~ → `ExpenditureCategoryTest.testToDisplayLabel_WastedC` で検証済みのため省略 |
| ⑦ | viewType=item → 支出項目別リスト（SisyutuKingaku）が設定される |
| ⑧ | viewType=expenditure → 支出項目別リスト（SisyutuKingaku）が設定されない |

### 7.3 ドメインサービステスト（Unit）

`IncomeAndExpenditureConsistencyServiceTest`（既存ファイルへ追加）

| # | テストケース |
|---|------------|
| 既存テスト | `validateDataExistence()` 拡張後も GREEN であること |
| ① | 収支データなし & 支出金額データあり & 支出データなし → `DataInconsistencyException` |
| ② | 収支データなし & 支出金額データなし & 支出データあり → `DataInconsistencyException`（新規追加チェック） |

### 7.4 ドメインタイプテスト（Unit）

`ExpenditureCategoryTest`（既存ファイルへ追加）

| # | テストケース |
|---|------------|
| ① | `NON_WASTED.toDisplayLabel()` → 空文字を返す |
| ② | `WASTED_B.toDisplayLabel()` → `"無駄遣いB"` を返す |
| ③ | `WASTED_C.toDisplayLabel()` → `"無駄遣いC"` を返す |

### 7.5 Controller 統合テスト

`AccountMonthInquiryControllerIntegrationTest`（既存ファイルへ追加）

| # | テストケース |
|---|------------|
| 既存テスト | 修正後も GREEN であること |
| ① | `POST /targetcontrol/?viewTypeSwitch` with `viewType=expenditure` → 支出別テーブル表示 |
| ② | `POST /targetcontrol/?viewTypeSwitch` with `viewType=item` → 支出項目別テーブル表示 |
| ③ | 前月・次月ボタン押下時に viewType が引き継がれる |
| ④ | `GET /registComplete/` → viewType が `item` で表示される |
| ⑤ | `POST /dispatchaction/?expenditureCorrect` → `expenditurecorrectload` へリダイレクト |

---

## 8. 実装順序

1. `NullableDateValue.toDayString()` 追加
2. `AccountMonthInquiryExpenditureList` 新規作成（ドメインモデル）
3. `AccountMonthInquiryResponse` 修正（`ExpenditureRow` 内部クラス・フィールド追加）
   - `ExpenditureRow.from()` は変換済み String 値を引数に取る形式（DDD準拠）
4. `IncomeAndExpenditureConsistencyService.validateDataExistence()` 拡張（第4引数追加）
5. `ExpenditureCategory.toDisplayLabel()` 追加（ドメインタイプ）
6. `AccountMonthInquiryUseCase` 修正
   - `ExpenditureTableRepository` 追加
   - `execRead()` 処理順番①〜⑦に見直し（ExpenditureTable を常時取得に変更）
   - `convertExpenditureList()` 追加（`toDisplayLabel()` 委譲）
7. `AccountMonthInquiryController` 修正（viewType 引数・新規エンドポイント）
8. `IncomeAndExpenditureRegistController` 修正（`expenditurecorrectload` 追加）
9. `AccountMonth.html` 修正（切替ボタン・支出別テーブル）
10. テスト追加・全件グリーン確認

---

## 9. 備考・制約事項

- 支出別一覧の表示順は EXPENDITURE_TABLE の取得順（`ExpenditureTableRepository.findBy()` の実装依存）
- 収支データなし（`syuusiDataFlg=false`）の月は支出別表示に切り替えない（ボタン自体が表示されない）
- `postViewTypeSwitch()` は収支データが存在する前提であるため、`buildRegistCheck()` 分岐は設けない
- `viewType` が `"item"` でも `"expenditure"` でもない不正値は、UseCase 側で `"item"` として扱う（デフォルト化）
- 訂正ボタンを押すと収支登録セッションが初期化される（既存の収支登録画面の編集中データは消える）ため、今後 UX 上の注意喚起が必要かどうか別途検討する
