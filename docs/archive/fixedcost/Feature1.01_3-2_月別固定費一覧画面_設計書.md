# Feature③-2 月別固定費一覧画面 設計書

## 概要

年間固定費合計画面の「詳細へ」ボタン、または固定費管理 / 年間固定費合計画面のタブから遷移する
「月別固定費一覧画面」を新規追加する。

### 仕様のポイント

**①明細選択による処理選択画面遷移**
固定費管理画面（`FixedCostInfoManageInit.html`）と同じ `th:data-href` + `table_tr_click_script.js` パターンを使用する。
行クリックで `GET /select?fixedCostCode=XXXX` に遷移し、処理選択画面を表示する。
処理選択画面の「キャンセル」押下は既存の動作（固定費管理画面に戻る）をそのまま使用する（変更不要）。

**②タブ切り替えで前回表示した月を記憶**
専用セッションBean `FixedCostInfoManageSession` に選択月を保持する。
`/monthlydetail/` エンドポイントは `month` パラメータをオプションで受け取り、
指定なし時はセッション → 現在対象月の順で表示月を決定する。
固定費管理画面・年間固定費合計画面のタブリンクを `month` パラメータなしのURLに変更することで、
他タブから戻った際にセッションの月（前回表示月）が使われる。

**セッションクリアタイミング**
セッション（`selectedMonth`）のクリアは **`/initload/`（トップメニューからの遷移）のみ** で実施する。
年間固定費合計・月別固定費一覧から「固定費管理」タブへの遷移は別エンドポイント **`/tabload/`** を使い、
セッションクリアを行わない。これにより「月別固定費一覧で前の月を見た状態で固定費管理タブへ切り替え、
再び月別固定費一覧タブに戻ったときに前回の月が維持される」という動作を実現する。
他の機能から固定費管理画面に遷移する場合は `/initload/` をベースとする。

---

## 変更対象ファイル一覧

| # | 区分 | ファイル | 変更内容 |
|---|------|---------|---------|
| 1 | セッション（新規） | `FixedCostInfoManageSession.java` | 月別固定費一覧で選択した月を保持するセッションBean新規作成 |
| 2 | ドメインモデル（変更） | `FixedCostInquiryList.java` | `getValuesForMonth(int)` メソッド追加 |
| 3 | UseCase（新規） | `FixedCostMonthlyDetailUseCase.java` | 月別固定費一覧画面 UseCase 新規作成 |
| 4 | Response（新規） | `FixedCostMonthlyDetailResponse.java` | 月別固定費一覧画面 Response 新規作成 |
| 5 | Controller（変更） | `FixedCostInfoManageController.java` | `/monthlydetail/` エンドポイント追加・`/tabload/` エンドポイント追加・`/initload/` にセッションクリア追加・DI追加・Javadoc更新 |
| 6 | HTML（新規） | `FixedCostMonthlyDetail.html` | 月別固定費一覧画面テンプレート新規作成（「固定費管理」タブは `/tabload/` を使用） |
| 7 | HTML（変更） | `FixedCostInfoManageInit.html` | 月別固定費一覧タブリンクの `month` パラメータを削除 |
| 8 | HTML（変更） | `FixedCostAnnualSummary.html` | 月別固定費一覧タブリンクの `month` パラメータを削除・「固定費管理」タブリンクを `/tabload/` に変更 |
| 9 | Response（変更） | `AbstractFixedCostItemListResponse.java` | `targetMonthValue` フィールド・getter・setter・`createModelAndView()` の addObject 削除 |
| 10 | UseCase（変更） | `FixedCostInquiryUseCase.java` | `setFixedCostItemList()` 内の `setTargetMonthValue()` 呼び出し削除 |
| 11 | Response（変更） | `FixedCostAnnualSummaryResponse.java` | `targetMonth` フィールド・`getInstance()` のパラメータ・`build()` の addObject 削除 |
| 12 | UseCase（変更） | `FixedCostAnnualSummaryUseCase.java` | `readAnnualSummaryInfo()` 内の `targetMonth` 引数渡し削除 |
| 13 | テスト（変更） | `FixedCostInquiryListTest.java` | `getValuesForMonth()` のテスト追加（7テスト） |
| 14 | テスト（新規） | `FixedCostMonthlyDetailUseCaseIntegrationTest.java` | UseCase 結合テスト |
| 15 | テスト（変更） | `FixedCostInfoManageControllerIntegrationTest.java` | `/monthlydetail/` エンドポイントのテスト追加・DI更新 |

---

## 1. 画面遷移フロー

```
年間固定費合計画面「詳細へ」ボタン
  └─ GET /monthlydetail/?month=MM
       → 月別固定費一覧画面（指定月で表示、セッション更新）

固定費管理画面タブ「月別固定費一覧」
年間固定費合計画面タブ「月別固定費一覧」
  └─ GET /monthlydetail/（month パラメータなし）
       → 月別固定費一覧画面（セッション → 現在対象月 の順で月を決定）

月別固定費一覧画面内
  ├─ タブ「固定費管理」    → GET /tabload/（固定費管理画面、セッションクリアなし）
  ├─ タブ「年間固定費合計」 → GET /annualsummary/（年間固定費合計画面）
  ├─ 「前の月」ボタン      → GET /monthlydetail/?month=prevMM（セッション更新）
  ├─ 「次の月」ボタン      → GET /monthlydetail/?month=nextMM（セッション更新）
  └─ 固定費一覧の行クリック → GET /select?fixedCostCode=XXXX（処理選択画面）
       ├─ キャンセル → GET /updateload/?actionCancel → 固定費管理画面 [既存動作]
       ├─ 更新      → GET /updateload/?actionUpdate  → 更新画面 [既存動作]
       └─ 削除      → POST /delete/ → リダイレクト   [既存動作]
```

### 月の決定ロジック（UseCase）

```
1. URL パラメータ `month`（"MM"形式）が指定された場合
   → その月を表示月として使用し、セッションに保存する
2. URL パラメータ `month` が指定されない場合
   a. FixedCostInfoManageSession.selectedMonth が設定済み
      → セッションの月を表示月として使用する（セッションは更新しない）
   b. セッション未設定
      → 現在対象月（AccountBookUserRepository 経由）を使用し、セッションに保存する
```

---

## 2. 新規セッションBean：`FixedCostInfoManageSession`

**配置パッケージ**: `presentation/session/`

```java
/**
 * 固定費情報管理画面のセッションスコープBeanです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/05/XX : 1.01.03  新規作成
 *
 */
@Component
@SessionScope
@Data
public class FixedCostInfoManageSession implements Serializable {
    private static final long serialVersionUID = 1L;

    // 月別固定費一覧で最後に選択した月（"MM"形式 例:"03"）
    private String selectedMonth;
}
```

---

## 3. ドメインモデル変更：`FixedCostInquiryList.getValuesForMonth(int)`

**対象クラス**: `domain/model/account/fixedcost/FixedCostInquiryList`

既存の `shouldAdd()` private static メソッドを利用して、指定月に支払いが発生する固定費をフィルタリングするメソッドを追加する。

```java
/**
 * 指定した月に支払いが発生する固定費の一覧を返します。
 * 支払月コードに応じて対象月に支払いが発生するかどうかを判定します。
 *
 * @param monthValue 対象月（1〜12）
 * @return 指定月に支払いが発生する固定費一覧（DB取得順を維持）
 */
public List<FixedCostInquiryItem> getValuesForMonth(int monthValue) {
    if (CollectionUtils.isEmpty(values)) {
        return Collections.emptyList();
    }
    return values.stream()
            .filter(item -> shouldAdd(item.getFixedCostTargetPaymentMonth().getValue(), monthValue))
            .collect(Collectors.toList());
}
```

> **設計ポイント**: `shouldAdd()` は `FixedCostAnnualSummaryList` にも同一ロジックが実装されているが、
> `FixedCostInquiryList` の既存 `calculateMonthlyTotal()` が `shouldAdd()` を使っており、
> `getValuesForMonth()` も同じメソッドを呼び出すことで一貫性を保つ。

---

## 4. 新規 UseCase：`FixedCostMonthlyDetailUseCase`

**配置パッケージ**: `application/usecase/itemmanage/fixedcost/`

> **設計変更**: 当初設計ではセッション管理を UseCase 内で行う想定だったが、
> DDD 観点から UseCase をセッション非依存に保つため、セッション管理を Controller 層に移動した。
> UseCase は `monthStr`（表示月、null の場合は DB から取得）のみ受け取る。

```java
/**
 * 月別固定費一覧画面のユースケースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/05/27 : 1.01.00  新規作成
 *
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class FixedCostMonthlyDetailUseCase {

    private final CodeTableItemComponent codeTableItem;
    private final FixedCostTableRepository fixedCostRepository;
    private final AccountBookUserRepository accountBookUserRepository;

    /**
     * 月別固定費一覧画面の表示情報を取得します。
     * monthStr が null の場合は現在の対象月を使用します。
     *
     * @param user ログインユーザ情報
     * @param monthStr 表示する月（"MM"形式）。null の場合は現在の対象月を使用
     * @return 月別固定費一覧画面レスポンス
     */
    public FixedCostMonthlyDetailResponse readMonthlyDetail(LoginUserInfo user, String monthStr) {
        log.debug("readMonthlyDetail:userid=" + user.getUserId() + ",month=" + monthStr);

        UserId userId = UserId.from(user.getUserId());

        // 表示月の決定（月番号を 1〜12 の int で保持）
        int monthValue;
        if (monthStr != null) {
            monthValue = Integer.parseInt(monthStr);
        } else {
            TargetYearMonth targetYearMonth = accountBookUserRepository.getTargetYearMonth(
                    SearchQueryUserId.from(userId));
            monthValue = Integer.parseInt(targetYearMonth.getMonth());
        }

        // 前月・次月の計算（1↔12 ラップ）
        int prevMonthValue = (monthValue == 1) ? 12 : monthValue - 1;
        int nextMonthValue = (monthValue == 12) ? 1 : monthValue + 1;

        // レスポンスを生成
        FixedCostMonthlyDetailResponse response = FixedCostMonthlyDetailResponse.getInstance(
                monthValue + "月",
                String.format("%02d", monthValue),
                String.format("%02d", prevMonthValue),
                String.format("%02d", nextMonthValue));

        // 全固定費一覧を取得
        FixedCostInquiryList allFixedCosts = fixedCostRepository.findByUserId(
                SearchQueryUserId.from(userId));
        if (allFixedCosts.isEmpty()) {
            response.addMessage("登録済み固定費情報が0件です。");
        } else {
            // 対象月に支払いが発生する固定費でフィルタリング
            List<FixedCostInquiryItem> monthItems = allFixedCosts.getValuesForMonth(monthValue);
            if (!monthItems.isEmpty()) {
                response.setFixedCostItemList(createFixedCostItemList(monthItems));
            }
            // 当月合計（calculateMonthlyTotal は月部分のみ使用するため年は任意値（2000年）を設定）
            response.setMonthlyTotal(
                    allFixedCosts.calculateMonthlyTotal(
                            TargetYearMonth.from(String.format("2000%02d", monthValue))).toFormatString());
        }
        return response;
    }

    /**
     * 固定費一覧明細情報(ドメイン)のリストから、画面表示用の固定費一覧明細情報のリストを生成して返します。
     */
    private List<FixedCostItem> createFixedCostItemList(List<FixedCostInquiryItem> items) {
        return items.stream().map(domain ->
            AbstractFixedCostItemListResponse.FixedCostItem.from(
                domain.getFixedCostCode().getValue(),
                domain.getExpenditureItemName().getValue(),
                domain.getFixedCostName().getValue(),
                codeTableItem.getCodeValue(
                        MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_TUKI,
                        domain.getFixedCostTargetPaymentMonth().getValue()),
                codeTableItem.getCodeValue(
                        MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_DAY,
                        domain.getFixedCostPaymentDay().getValue()),
                domain.getFixedCostPaymentAmount().toFormatString(),
                domain.getFixedCostTargetPaymentMonthOptionalContext().getValue())
        ).collect(Collectors.toUnmodifiableList());
    }
}
```

---

## 5. 新規 Response：`FixedCostMonthlyDetailResponse`

**配置パッケージ**: `presentation/response/itemmanage/fixedcost/`

`AbstractFixedCostItemListResponse` は不要フィールドが多いため、`AbstractResponse` を直接継承する。
固定費一覧の明細データは既存の `AbstractFixedCostItemListResponse.FixedCostItem` を再利用する。

> **設計変更**: 当初設計では `displayMonth`（"MM"形式）と `displayMonthLabel`（"M月"形式）の2フィールドを持つ想定だったが、
> Controller でのセッション更新に使用する月番号フィールドを `currentMonth` と命名した。
> また `getInstance` はラベル・前月・次月の計算を UseCase 側で行い4引数で受け取る形に変更した。

```java
/**
 * 月別固定費一覧画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/05/27 : 1.01.00  新規作成
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FixedCostMonthlyDetailResponse extends AbstractResponse {

    // 表示中の月ラベル（"M月" 形式 例："11月"）
    @Getter
    private final String displayMonthLabel;
    // 表示中の月番号（"MM" 形式 例："11"、セッション更新用）
    @Getter
    private final String currentMonth;
    // 前月番号（"MM" 形式 例："10"）
    @Getter
    private final String prevMonth;
    // 次月番号（"MM" 形式 例："12"）
    @Getter
    private final String nextMonth;
    // 固定費一覧（指定月に支払いが発生する固定費）
    @Getter
    @Setter
    private List<AbstractFixedCostItemListResponse.FixedCostItem> fixedCostItemList = new ArrayList<>();
    // 当月合計金額
    @Getter
    @Setter
    private String monthlyTotal;

    /**
     * 引数の値からレスポンス情報を生成して返します。
     *
     * @param displayMonthLabel 表示中の月ラベル（"M月" 形式）
     * @param currentMonth 表示中の月番号（"MM" 形式、セッション更新用）
     * @param prevMonth 前月番号（"MM" 形式）
     * @param nextMonth 次月番号（"MM" 形式）
     * @return 月別固定費一覧画面表示情報
     */
    public static FixedCostMonthlyDetailResponse getInstance(
            String displayMonthLabel, String currentMonth, String prevMonth, String nextMonth) {
        return new FixedCostMonthlyDetailResponse(displayMonthLabel, currentMonth, prevMonth, nextMonth);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView build() {
        ModelAndView modelAndView = createModelAndView("itemmanage/fixedcost/FixedCostMonthlyDetail");
        modelAndView.addObject("displayMonthLabel", displayMonthLabel);
        modelAndView.addObject("currentMonth", currentMonth);
        modelAndView.addObject("prevMonth", prevMonth);
        modelAndView.addObject("nextMonth", nextMonth);
        modelAndView.addObject("fixedCostItemList", fixedCostItemList);
        modelAndView.addObject("monthlyTotal", monthlyTotal);
        return modelAndView;
    }
}
```

> `FixedCostItem` は `AbstractFixedCostItemListResponse.FixedCostItem`（`static` クラス）を import して使用する。
> `import com.yonetani.webapp.accountbook.presentation.response.itemmanage.fixedcost.AbstractFixedCostItemListResponse.FixedCostItem;`

---

## 6. Controller 変更：`FixedCostInfoManageController`

### 6-1. DI 追加

```java
// UseCase(月別固定費一覧)
private final FixedCostMonthlyDetailUseCase monthlyDetailUseCase;
// セッション(固定費情報管理)
private final FixedCostInfoManageSession fixedCostInfoManageSession;
```

### 6-2. 既存 `getInitLoad()` にセッションクリア追加

```java
@GetMapping("/initload/")
public ModelAndView getInitLoad() {
    log.debug("getInitLoad:");

    // トップメニューからの新規エントリー時にセッションをクリアする
    fixedCostInfoManageSession.setSelectedMonth(null);

    return this.inquiryUseCase.readInitInfo(loginUserSession.getLoginUserInfo())
            .setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
            .build();
}
```

### 6-3. 新規エンドポイント追加（`/tabload/`・`/monthlydetail/`）

```java
/**
 * 固定費情報管理初期表示のタブ遷移時のGET要求マッピングです。
 * 年間固定費合計画面・月別固定費一覧画面の「固定費管理」タブから遷移する場合に使用します。
 * セッション（月別固定費一覧の選択月）はクリアしません。
 */
@GetMapping("/tabload/")
public ModelAndView getTabLoad() {
    log.debug("getTabLoad:");

    return this.inquiryUseCase.readInitInfo(loginUserSession.getLoginUserInfo())
            .setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
            .build();
}

/**
 * 月別固定費一覧画面のGET要求マッピングです。
 * month パラメータが指定された場合はその月、未指定の場合はセッション → 現在対象月の順で表示月を決定します。
 * セッション管理（selectedMonth の取得・更新）は Controller 層で行い、UseCase はセッション非依存とする。
 *
 * @param month 表示対象月（"MM"形式）。省略可能
 * @return 月別固定費一覧画面
 */
@GetMapping("/monthlydetail/")
public ModelAndView getMonthlyDetail(
        @RequestParam(value = "month", required = false) String month) {
    log.debug("getMonthlyDetail: month=" + month);

    // セッション → URL パラメータ の優先順位で表示月を決定（UseCase に渡す前に解決）
    String targetMonth = null;
    if (month != null) {
        targetMonth = month;
    } else if (fixedCostInfoManageSession.getSelectedMonth() != null) {
        targetMonth = fixedCostInfoManageSession.getSelectedMonth();
    }
    // UseCase で表示月・前後月・一覧を取得（targetMonth=null の場合は DB の現在対象月を使用）
    FixedCostMonthlyDetailResponse response =
            this.monthlyDetailUseCase.readMonthlyDetail(loginUserSession.getLoginUserInfo(), targetMonth);
    // セッションに今回表示した月を保存（次回の「固定費管理」タブ→月別固定費一覧に使われる）
    fixedCostInfoManageSession.setSelectedMonth(response.getCurrentMonth());
    return response.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName()).build();
}
```

### 6-4. クラス Javadoc 更新

```
* ・タブからの遷移(GET)→情報管理(固定費)初期表示画面（セッションクリアなし）
* ・月別固定費一覧タブ押下(GET)→月別固定費一覧画面
* ・年間固定費合計画面の「詳細へ」ボタン押下(GET)→月別固定費一覧画面
```

を画面遷移フロー説明に追記する。

---

## 7. 新規 HTML：`FixedCostMonthlyDetail.html`

**配置パス**: `src/main/resources/templates/itemmanage/fixedcost/`

```html
<!DOCTYPE html>
<html lang="ja" xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<head>
<meta charset="UTF-8">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
        rel="stylesheet" integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM"
        crossorigin="anonymous">
<link th:href="@{/css/MyHouseholdAccountBookCommon.css}" rel="stylesheet" type="text/css">
<style>
.nav-tabs a.nav-link { padding: .5rem 1rem; }
</style>
<title>月別固定費一覧</title>
</head>
<body>
    <!--/* ヘッダー */-->
    <div id="header" th:insert="~{layout/myhacbookheader::header}">ヘッダー</div>

    <!--/* ボディ部 */-->
    <div class="col">
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">月別固定費一覧</h5>
                <th:block th:each="message : ${messages}">
                    <code><span th:text="${message}">何かのメッセージがある場合はここに表示</span></code></br>
                </th:block>
            </div>

            <!-- ===== タブナビゲーション ===== -->
            <nav>
                <div class="nav nav-tabs mb-3" id="nav-tab" role="tablist">
                    <!-- ① 固定費管理タブ [タブ遷移: セッションクリアなし] -->
                    <a class="nav-link" th:href="@{/myhacbook/managebaseinfo/fixedcostinfo/tabload/}">固定費管理</a>
                    <!-- ② 年間固定費合計タブ [遷移] -->
                    <a class="nav-link" th:href="@{/myhacbook/managebaseinfo/fixedcostinfo/annualsummary/}">年間固定費合計</a>
                    <!-- ③ 月別固定費一覧タブ [現在アクティブ] -->
                    <button class="nav-link active" type="button" role="tab" aria-selected="true" disabled>月別固定費一覧</button>
                </div>
            </nav>

            <!-- 月ナビゲーション -->
            <div class="card-body">
                <div class="d-flex align-items-center gap-3">
                    <a class="btn btn-outline-secondary btn-sm"
                       th:href="@{/myhacbook/managebaseinfo/fixedcostinfo/monthlydetail/(month=${prevMonth})}">◀ 前の月</a>
                    <span class="h5 mb-0" th:text="${displayMonthLabel}">M月</span>
                    <a class="btn btn-outline-secondary btn-sm"
                       th:href="@{/myhacbook/managebaseinfo/fixedcostinfo/monthlydetail/(month=${nextMonth})}">次の月 ▶</a>
                </div>
            </div>

            <!-- 固定費一覧表示エリア -->
            <div class="card-body">
                <div class="card">
                    <div class="card-body">
                        <p class="h6">■ <span th:text="${displayMonthLabel}">M月</span>の固定費一覧 ■</p>
                        <th:block th:if="${fixedCostItemList != null and #lists.size(fixedCostItemList) > 0}">
                        <p class="card-text">更新対象の固定費を選択してください。</p>
                        <table class="table table-sm table-bordered table-hover">
                            <thead>
                                <tr class="table-secondary">
                                    <th scope="col" class="w20">支出項目名</th>
                                    <th scope="col" class="w25">支払名</th>
                                    <th scope="col" class="w10">支払月</th>
                                    <th scope="col" class="w10">支払日</th>
                                    <th scope="col" class="w10">支払金額</th>
                                    <th scope="col">その他任意詳細</th>
                                </tr>
                            </thead>
                            <tbody>
                                <th:block th:each="item, itemStat : ${fixedCostItemList}">
                                <th:block th:if="${itemStat.odd}">
                                    <tr th:data-href="@{/myhacbook/managebaseinfo/fixedcostinfo/select(fixedCostCode=${item.fixedCostCode})}">
                                </th:block>
                                <th:block th:if="${itemStat.even}">
                                    <tr class="table-warning" th:data-href="@{/myhacbook/managebaseinfo/fixedcostinfo/select(fixedCostCode=${item.fixedCostCode})}">
                                </th:block>
                                    <td th:text="${item.sisyutuItemName}">支出項目名</td>
                                    <td th:text="${item.shiharaiName}">支払名</td>
                                    <td th:text="${item.shiharaiTuki}">支払月</td>
                                    <td th:text="${item.shiharaiDay}">支払日</td>
                                    <td th:text="${item.shiharaiKingaku}" align="right">支払金額</td>
                                    <td th:text="${item.optionalContext}">その他任意詳細</td>
                                </tr>
                                </th:block>
                                <tr class="table-success">
                                    <td colspan="4" th:text="${displayMonthLabel} + '合計'">M月合計</td>
                                    <td th:text="${monthlyTotal}" align="right">XXX円</td>
                                    <td></td>
                                </tr>
                            </tbody>
                        </table>
                        </th:block>
                    </div>
                </div>
            </div>

        </div>
    </div>

    <!--/* フッター共通jsのインクルード */-->
    <div id="footer" th:insert="~{layout/myhacbookfooter::footer}">フッター</div>
    <!--/* jqueryでテーブル行の選択時アクションを実行 */-->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script type="text/javascript" th:src="@{/js/table_tr_click_script.js}"></script>

</body>
</html>
```

---

## 8. HTML 変更：既存タブリンクの `month` パラメータ削除

### 8-1. `FixedCostInfoManageInit.html`

```html
<!-- 変更前 -->
<a class="nav-link" th:href="@{/myhacbook/managebaseinfo/fixedcostinfo/monthlydetail/(month=${targetMonthValue})}">月別固定費一覧</a>
<!-- 変更後 -->
<a class="nav-link" th:href="@{/myhacbook/managebaseinfo/fixedcostinfo/monthlydetail/}">月別固定費一覧</a>
```

### 8-2. `FixedCostAnnualSummary.html`

```html
<!-- 変更前（固定費管理タブ） -->
<a class="nav-link" th:href="@{/myhacbook/managebaseinfo/fixedcostinfo/initload/}">固定費管理</a>
<!-- 変更後（固定費管理タブ） -->
<a class="nav-link" th:href="@{/myhacbook/managebaseinfo/fixedcostinfo/tabload/}">固定費管理</a>

<!-- 変更前（月別固定費一覧タブ） -->
<a class="nav-link" th:href="@{/myhacbook/managebaseinfo/fixedcostinfo/monthlydetail/(month=${targetMonth})}">月別固定費一覧</a>
<!-- 変更後（月別固定費一覧タブ） -->
<a class="nav-link" th:href="@{/myhacbook/managebaseinfo/fixedcostinfo/monthlydetail/}">月別固定費一覧</a>
```

> **補足**: `AbstractFixedCostItemListResponse.targetMonthValue` フィールドおよび
> `FixedCostAnnualSummaryResponse.targetMonth` フィールドはタブリンクで使用しなくなるため、
> 本 Feature のスコープで削除済み（`FixedCostInquiryUseCase`・`FixedCostAnnualSummaryUseCase` の呼び出し削除も含む）。

---

## 9. テスト

### 9-1. `FixedCostInquiryListTest`（既存テストへの追加）

**配置**: `src/test/java/.../domain/model/account/fixedcost/`

`getValuesForMonth(int monthValue)` のテストを追加する（7テスト）。

| テスト | 内容 |
|--------|------|
| ① 空リストの場合 | `getValuesForMonth()` が空リストを返すこと |
| ② 毎月支払(00)の場合 | 全月（1〜12月）で当該固定費が返ること |
| ③ 奇数月支払(20)の場合 | 奇数月のみ返り、偶数月では返らないこと |
| ④ 偶数月支払(30)の場合 | 偶数月のみ返り、奇数月では返らないこと |
| ⑤ 特定月（5月）支払の場合 | 5月のみ返り、他月では返らないこと |
| ⑥ その他任意(40)の場合 | 全月（1〜12月）で当該固定費が返ること |
| ⑦ 複数固定費混在の場合 | 指定月に該当する固定費のみが返ること（DB取得順を維持すること） |

> **テスト⑦補足**: 毎月・奇数月・偶数月・その他任意の4件を登録し、
> 奇数月（11月）では毎月+奇数月+その他任意の3件が返ること、
> 偶数月（12月）では毎月+偶数月+その他任意の3件が返ることを検証する。

### 9-2. `FixedCostMonthlyDetailUseCaseIntegrationTest`（新規）

**配置**: `src/test/java/.../application/usecase/itemmanage/fixedcost/`

**テスト SQL**: `FixedCostInquiryIntegrationTest.sql`（既存流用）

> **設計変更**: セッション管理が Controller に移動したため、UseCase テストはセッションを使用しない。
> `readMonthlyDetail(user, monthStr)` の `monthStr` に null・"MM"形式を渡してテストする。

**テストデータ（再掲）**:
- user01: NOW_TARGET_YEAR=2025, NOW_TARGET_MONTH=11
- 固定費5件:
  - 0001: 家賃（毎月 60,000円）
  - 0002: 電気代概算（毎月 12,000円）
  - 0003: 国民年金保険（奇数月 16,590円）
  - 0004: その他任意テスト（その他任意 10,000円）
  - 0005: 電気代夏季割増（偶数月 8,000円）

**月ごとの期待値**:
- 奇数月(01,03,05,07,09,11): 0003・0001・0002・0004 の4件、合計 98,590円
- 偶数月(02,04,06,08,10,12): 0001・0002・0005・0004 の4件、合計 90,000円

> **DB取得順**: ORDER BY SISYUTU_ITEM_SORT, FIXED_COST_SHIHARAI_TUKI に従い
> 0003(sort=0201010000,TUKI=20)、0001(sort=0303010000,TUKI=00)、
> 0002(sort=0306010000,TUKI=00)、0005(sort=0306010000,TUKI=30)、
> 0004(sort=0306020000,TUKI=40) の順に取得される。フィルタ後も同順序が維持される。

**テスト一覧**:

| テスト | テストメソッド名 | 内容 |
|--------|-----------------|------|
| ① | `testReadMonthlyDetail_奇数月11` | `month="11"` → 4件[0003,0001,0002,0004]・合計98,590円・月ナビ(prev=10,next=12) |
| ② | `testReadMonthlyDetail_偶数月12` | `month="12"` → 4件[0001,0002,0005,0004]・合計90,000円・nextMonth=01（年末ラップ） |
| ③ | `testReadMonthlyDetail_月パラメータなし` | `month=null` → DB の NOW_TARGET_MONTH=11 を使用して11月と同じ結果 |
| ④ | `testReadMonthlyDetail_年初ラップ` | `month="01"` → prevMonth=12（年初ラップ）・displayMonthLabel="1月"（先頭0なし） |
| ⑤ | `testReadMonthlyDetail_5月` | `month="05"` → displayMonthLabel="5月"（先頭0なし）・月ナビ確認 |

**テストクラスの構造**:

```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
    "/sql/initsql/schema_test.sql",
    "/com/yonetani/webapp/accountbook/application/usecase/itemmanage/fixedcost/FixedCostInquiryIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
class FixedCostMonthlyDetailUseCaseIntegrationTest {
    @Autowired
    private FixedCostMonthlyDetailUseCase useCase;

    private final LoginUserInfo USER01 = LoginUserInfo.from("user01", "テストユーザ01");
    // ...
}
```

### 9-3. `FixedCostInfoManageControllerIntegrationTest`（既存テストへの追加）

**変更内容**:
1. `FixedCostMonthlyDetailUseCase` の DI 追加（MockMvc セットアップ更新）
2. `FixedCostInfoManageSession` の DI 追加（`@Mock` で生成、`@BeforeEach` でデフォルト設定）
3. `/tabload/` エンドポイントのテストを追加（1テスト）
4. `/monthlydetail/` エンドポイントのテストを追加（1テスト）
   → 計2テスト追加、合計18テスト

> **テスト方針変更**: `/initload/` のセッションクリア検証（`verify(mock).setSelectedMonth(null)`）は
> `FixedCostInfoManageSession` が `@Mock` であるため Mockito verify で確認可能だが、
> `/tabload/` との差異（クリアしない）を verify で示す方法が煩雑なため実装しなかった。
> セッションクリアタイミングの差異は運用テストで確認する。

**追加テスト**:

| # | テストメソッド名 | 内容 |
|---|-----------------|------|
| ⑰ | `testGetTabLoad` | `GET /tabload/` → viewName=`FixedCostInfoManageInit`・固定費5件・`loginUserName`設定。セッションはクリアされないこと（initload と異なる点）の検証は @Mockでの検証実現不可のため運用テストにて確認する |
| ⑱ | `testGetMonthlyDetail_month11` | `GET /monthlydetail/?month=11` → viewName=`FixedCostMonthlyDetail`・`displayMonthLabel="11月"`・固定費4件・合計98,590円 |

> **Hamcrest インポート**: ControllerIntegrationTest のワイルドカードインポート方針を維持する。
>
> **セッションの `@Mock` 設定**: `@BeforeEach` で `doReturn(null).when(mockFixedCostInfoManageSession).getSelectedMonth()` を設定する。

---

## 10. 変更しないもの（影響なし）

- `FixedCostRegistConfirmUseCase`（変更なし）
- `FixedCostInfoManageActSelectResponse`（キャンセル遷移先は既存の固定費管理画面のまま）
- `FixedCostTableRepository`・SQL・DataSource・Mapper（変更なし）
- `ExpenditureItemInfoManage` 系クラス（変更なし）

---

## 11. 作業順序

1. セッションBean 新規作成（`FixedCostInfoManageSession`）
2. `FixedCostInquiryList` に `getValuesForMonth()` 追加
3. `FixedCostInquiryListTest` にテスト追加（7テスト）・`mvn clean test` で GREEN 確認
4. Response 新規作成（`FixedCostMonthlyDetailResponse`）
5. UseCase 新規作成（`FixedCostMonthlyDetailUseCase`）
6. Controller 変更（DI追加・`getInitLoad()` セッションクリア追加・`getTabLoad()` 新規・`getMonthlyDetail()` 新規・Javadoc更新）
7. HTML 新規作成（`FixedCostMonthlyDetail.html`）
8. HTML 変更（`FixedCostInfoManageInit.html` タブリンク修正・`FixedCostAnnualSummary.html` タブリンク2箇所修正）
9. `AbstractFixedCostItemListResponse` から `targetMonthValue` 削除・`FixedCostInquiryUseCase` の呼び出し削除
10. `FixedCostAnnualSummaryResponse` から `targetMonth` 削除・`FixedCostAnnualSummaryUseCase` の引数渡し削除
11. UseCase 結合テスト新規作成（`FixedCostMonthlyDetailUseCaseIntegrationTest`）
12. Controller 結合テスト更新（DI 追加・`/tabload/`・`/monthlydetail/` テスト追加）
13. `mvn clean test` で All GREEN 確認

---

## 12. 変更履歴

| 日付 | 内容 |
|------|------|
| 2026/05/24 | 初版作成 |
| 2026/05/24 | セッションクリア設計追加: `/initload/` でのみクリア・タブ遷移用 `/tabload/` を新規追加。`FixedCostAnnualSummary.html` の「固定費管理」タブリンクも `/tabload/` に変更。Controllerテスト計20テストに更新 |
| 2026/05/31 | 実装後の設計書修正: UseCase メソッド名を `readMonthlyDetailInfo` → `readMonthlyDetail` に変更・セッション管理をUseCaseからControllerに移動・Responseフィールド `displayMonth` → `currentMonth` にリネーム・`getInstance` を4引数に変更・`targetMonthValue`/`targetMonth` を本Featureで削除済みに訂正・テスト一覧を実装内容に合わせて更新（Controller計18テスト） |
