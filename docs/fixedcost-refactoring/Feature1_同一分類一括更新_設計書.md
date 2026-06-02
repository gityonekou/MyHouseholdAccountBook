# Feature①: 固定費 同一分類一括更新 設計書

## 1. 概要

同一支出項目（ExpenditureItemCode）に複数の固定費が登録されている場合に、
選択した固定費の支払日・支払金額を初期値として、同一支出項目の複数固定費を一括で更新する機能。

### 更新対象フィールド（一括更新で変更するもの）

- 支払日（`fixedCostPaymentDay`）
- 支払金額（`fixedCostPaymentAmount`）

---

## 2. 変更対象ファイル一覧

### 2.1 新規作成

| ファイル | 配置パッケージ/パス |
|---|---|
| `FixedCostBulkUpdateForm.java` | `presentation/request/itemmanage/` |
| `FixedCostBulkUpdateResponse.java` | `presentation/response/itemmanage/` |
| `FixedCostBulkUpdate.html` | `src/main/resources/templates/itemmanage/` |

### 2.2 変更

| ファイル | 変更種別 | 概要 |
|---|---|---|
| `FixedCostInquiryUseCase.java` | メソッド変更・追加 | `readActSelectItemInfo` に兄弟固定費チェック追加、`readBulkUpdateInfo` 新規追加 |
| `FixedCostRegistConfirmUseCase.java` | メソッド追加 | `execBulkUpdate` 新規追加 |
| `FixedCostInfoManageActSelectResponse.java` | フィールド・内部クラス追加 | 兄弟固定費一覧保持用フィールド追加 |
| `FixedCostInfoManageActSelect.html` | HTML修正 | 兄弟固定費一覧エリア・[一括更新]ボタン追加 |
| `FixedCostInfoManageUpdate.html` | HTML修正 | [キャンセル]ボタン追加 |
| `FixedCostInfoManageController.java` | メソッド追加 | `/update/?actionCancel`、`/bulkupdate/` ルート追加 |

---

## 3. 画面フロー

```
GET /select?fixedCostCode=XXXX
  └─ FixedCostInquiryUseCase.readActSelectItemInfo()
       ├─ 同一支出項目の固定費が2件以上
       │    → hasSiblingFixedCost=true → ActSelect.html（拡張版）
       │         ├─ [更新] GET /updateload/?actionUpdate → Update.html
       │         │         ├─ [登録/更新] POST /update/?actionAdd or ?actionUpdate → 完了リダイレクト → Init.html
       │         │         └─ [キャンセル] POST /update/?actionCancel → ActSelect.html ← ★仕様追加
       │         ├─ [一括更新] GET /bulkupdate/?baseFixedCostCode=XXXX → BulkUpdate.html（新規）← ★GETに変更
       │         │         ├─ [一括更新] POST /bulkupdate/?actionBulkUpdate → 完了リダイレクト → Init.html
       │         │         └─ [キャンセル] POST /bulkupdate/?actionCancel → ActSelect.html
       │         ├─ [キャンセル] GET /updateload/?actionCancel → Init.html（変更なし）
       │         └─ [削除] POST /delete/ → Init.html（変更なし）
       └─ 同一支出項目の固定費が1件のみ
            → hasSiblingFixedCost=false → ActSelect.html（変更なし）
                 └─ [更新] GET /updateload/?actionUpdate → Update.html
                           ├─ [登録/更新] POST /update/?actionAdd or ?actionUpdate → 完了リダイレクト → Init.html
                           └─ [キャンセル] POST /update/?actionCancel
                                    ├─ action=update → ActSelect.html ← ★仕様追加
                                    └─ action=add    → Init.html        ← ★仕様追加
```

> **注**: 更新画面（`FixedCostInfoManageUpdate.html`）のキャンセルボタンは既存の `/updateload/?actionCancel`（→ Init.html）とは
> 別ルート `/update/?actionCancel` として追加する。フォームの hidden `action` 値（`add`/`update`）でキャンセル先を振り分ける。

---

## 4. UseCase 仕様

### 4.1 FixedCostInquiryUseCase（参照系）変更

#### 4.1.1 `readActSelectItemInfo` 変更

**変更箇所**: 既存メソッドのレスポンス生成後に、兄弟固定費チェックを追加する。

```java
// 追加処理（既存の setFixedCostItemList の後）

// 選択固定費の支出項目コードで同一支出項目に属する固定費の件数を取得
int siblingCount = fixedCostRepository.countByExpenditureItemCode(
        SearchQueryUserIdAndExpenditureItemCode.from(userId, searchResult.getExpenditureItemCode()));

// 2件以上の場合、兄弟固定費一覧を取得してレスポンスに設定
if (siblingCount >= 2) {
    FixedCostInquiryList siblingList = fixedCostRepository.findByExpenditureItemCode(
            SearchQueryUserIdAndExpenditureItemCode.from(userId, searchResult.getExpenditureItemCode()));
    response.addSiblingFixedCostItemList(createSiblingFixedCostItemList(siblingList));
}
```

**追加する private メソッド** `createSiblingFixedCostItemList`:

```java
private List<SiblingFixedCostItem> createSiblingFixedCostItemList(FixedCostInquiryList searchResult) {
    return searchResult.getValues().stream().map(domain -> {
        String shiharaiTukiDetailContext = codeTableItem.getCodeValue(
                MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_TUKI,
                domain.getFixedCostTargetPaymentMonth().getValue());
        String shiharaiTukiOptionalContext = Objects.equals(
                domain.getFixedCostTargetPaymentMonth().getValue(),
                MyHouseholdAccountBookContent.SHIHARAI_TUKI_OPTIONAL_SELECTED_VALUE)
                ? domain.getFixedCostTargetPaymentMonthOptionalContext().getValue()
                : "";
        return FixedCostInfoManageActSelectResponse.SiblingFixedCostItem.from(
                domain.getFixedCostCode().getValue(),
                domain.getFixedCostName().getValue(),
                shiharaiTukiDetailContext,
                shiharaiTukiOptionalContext,
                codeTableItem.getCodeValue(
                        MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_DAY,
                        domain.getFixedCostPaymentDay().getValue()),
                domain.getFixedCostPaymentAmount().toFormatString());
    }).collect(Collectors.toUnmodifiableList());
}
```

#### 4.1.2 `readBulkUpdateInfo` 新規追加

```java
public FixedCostBulkUpdateResponse readBulkUpdateInfo(LoginUserInfo user, String fixedCostCodeStr) {
    log.debug("readBulkUpdateInfo:userid=" + user.getUserId() + ",fixedCostCode=" + fixedCostCodeStr);

    UserId userId = UserId.from(user.getUserId());
    FixedCostCode fixedCostCode = FixedCostCode.from(fixedCostCodeStr);

    // 基準となる固定費情報を取得（フォームの初期値として使用）
    FixedCost baseFixedCost = fixedCostRepository.findByPrimaryKey(
            SearchQueryUserIdAndFixedCostCode.from(userId, fixedCostCode));
    if (baseFixedCost == null) {
        throw new MyHouseholdAccountBookRuntimeException(
                "選択した固定費が固定費テーブル:FIXED_COST_TABLEに存在しません。[fixedCostCode=" + fixedCostCode + "]");
    }

    // フォームの初期値として基準固定費の支払日・支払金額を設定
    FixedCostBulkUpdateForm form = new FixedCostBulkUpdateForm();
    form.setBaseFixedCostCode(baseFixedCost.getFixedCostCode().getValue());
    form.setShiharaiDay(baseFixedCost.getFixedCostPaymentDay().getValue());
    form.setShiharaiKingaku(baseFixedCost.getFixedCostPaymentAmount().toIntegerValue());

    return getBulkUpdateResponse(userId, baseFixedCost.getExpenditureItemCode(), form);
}
```

**private メソッド** `getBulkUpdateResponse`（`readBulkUpdateInfo` と `execBulkUpdate` で共用）:

```java
private FixedCostBulkUpdateResponse getBulkUpdateResponse(
        UserId userId, ExpenditureItemCode expenditureItemCode, FixedCostBulkUpdateForm form) {

    // 支払日選択ボックス情報を取得
    List<CodeAndValuePair> shiharaiDayList = codeTableItem.getCodeValues(
            MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_DAY);
    if (shiharaiDayList == null) {
        throw new MyHouseholdAccountBookRuntimeException(
                "コード定義ファイルに「固定費支払日情報：" 
                + MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_DAY + "」が登録されていません。");
    }

    // レスポンスを生成
    FixedCostBulkUpdateResponse response = FixedCostBulkUpdateResponse.getInstance(
            form,
            shiharaiDayList.stream().map(pair ->
                OptionItem.from(pair.getCode().getValue(), pair.getCodeValue().getValue()))
                .collect(Collectors.toList()));

    // 支出項目名を設定
    response.setSisyutuItemName(
            expenditureItemInfoComponent.getExpenditureItemName(userId, expenditureItemCode));

    // 同一支出項目の全固定費一覧を取得して設定
    FixedCostInquiryList siblingList = fixedCostRepository.findByExpenditureItemCode(
            SearchQueryUserIdAndExpenditureItemCode.from(userId, expenditureItemCode));
    response.addBulkUpdateTargetList(createBulkUpdateTargetList(siblingList));

    return response;
}
```

**追加する private メソッド** `createBulkUpdateTargetList`:

```java
private List<FixedCostBulkUpdateResponse.BulkUpdateTargetItem> createBulkUpdateTargetList(
        FixedCostInquiryList searchResult) {
    return searchResult.getValues().stream().map(domain -> {
        String shiharaiTukiDetailContext = codeTableItem.getCodeValue(
                MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_TUKI,
                domain.getFixedCostTargetPaymentMonth().getValue());
        String shiharaiTukiOptionalContext = Objects.equals(
                domain.getFixedCostTargetPaymentMonth().getValue(),
                MyHouseholdAccountBookContent.SHIHARAI_TUKI_OPTIONAL_SELECTED_VALUE)
                ? domain.getFixedCostTargetPaymentMonthOptionalContext().getValue()
                : "";
        return FixedCostBulkUpdateResponse.BulkUpdateTargetItem.from(
                domain.getFixedCostCode().getValue(),
                domain.getFixedCostName().getValue(),
                shiharaiTukiDetailContext,
                shiharaiTukiOptionalContext,
                codeTableItem.getCodeValue(
                        MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_DAY,
                        domain.getFixedCostPaymentDay().getValue()),
                domain.getFixedCostPaymentAmount().toFormatString());
    }).collect(Collectors.toUnmodifiableList());
}
```

---

### 4.2 FixedCostRegistConfirmUseCase（更新系）変更

#### 4.2.1 `execBulkUpdate` 新規追加

戻り値は `FixedCostBulkUpdateResponse` で統一する（後述の「5.3 戻り値の設計判断」参照）。

```java
@Transactional
public FixedCostBulkUpdateResponse execBulkUpdate(LoginUserInfo user, FixedCostBulkUpdateForm inputForm) {
    log.debug("execBulkUpdate:userid=" + user.getUserId() + ",inputForm=" + inputForm);

    UserId userId = UserId.from(user.getUserId());

    int totalUpdateCount = 0;
    for (String fixedCostCodeStr : inputForm.getCheckedFixedCostCodeList()) {
        FixedCostCode fixedCostCode = FixedCostCode.from(fixedCostCodeStr);

        // 更新対象の固定費情報を取得
        FixedCost target = fixedCostRepository.findByPrimaryKey(
                SearchQueryUserIdAndFixedCostCode.from(userId, fixedCostCode));
        if (target == null) {
            throw new MyHouseholdAccountBookRuntimeException(
                    "更新対象の固定費が固定費テーブル:FIXED_COST_TABLEに存在しません。[fixedCostCode=" + fixedCostCode + "]");
        }

        // 支払日・支払金額のみを差し替えた新しい固定費オブジェクトを生成
        FixedCost updateData = FixedCost.from(
                target.getUserId().getValue(),
                target.getFixedCostCode().getValue(),
                target.getFixedCostName().getValue(),
                target.getFixedCostDetailContext().getValue(),
                target.getExpenditureItemCode().getValue(),
                target.getFixedCostKubun().getValue(),
                target.getFixedCostTargetPaymentMonth().getValue(),
                target.getFixedCostTargetPaymentMonthOptionalContext().getValue(),
                inputForm.getShiharaiDay(),      // 一括適用: 支払日
                inputForm.getShiharaiKingaku()); // 一括適用: 支払金額

        int updateCount = fixedCostRepository.update(updateData);
        if (updateCount != 1) {
            throw new MyHouseholdAccountBookRuntimeException(
                    "固定費テーブル:FIXED_COST_TABLEへの更新件数が不正でした。[件数=" + updateCount + "][fixedCostCode=" + fixedCostCode + "]");
        }
        totalUpdateCount++;
    }

    // レスポンスを生成（getBulkUpdateResponse は FixedCostInquiryUseCase 側に定義するため、
    // RegistConfirmUseCase では独自に最低限のレスポンスを生成し、完了フラグとメッセージを設定）
    FixedCostBulkUpdateResponse response = FixedCostBulkUpdateResponse.getInstance(inputForm, List.of());
    response.addMessage("固定費を一括更新しました。[" + totalUpdateCount + "件]");
    response.setTransactionSuccessFull();
    return response;
}
```

> **補足**: 成功時は `setTransactionSuccessFull()` を呼んだ後、Controller が `buildRedirect(redirectAttributes)` を呼ぶため、
> 画面表示用データ（固定費一覧・選択ボックス等）は使われない。そのため成功時のレスポンス生成は最小限で問題ない。

---

### 4.3 readBulkUpdateBindingErrorSetInfo 新規追加（バリデーションエラー時の画面復元用）

バリデーションエラー時に一括更新画面へ戻る際に使用する。`readBulkUpdateInfo` との違いは、
送信済みの `inputForm` をそのまま渡すことで、ユーザーのチェックボックス選択・支払日・支払金額の入力状態を保持する点。

```java
public FixedCostBulkUpdateResponse readBulkUpdateBindingErrorSetInfo(
        LoginUserInfo user, FixedCostBulkUpdateForm inputForm) {
    log.debug("readBulkUpdateBindingErrorSetInfo:userid=" + user.getUserId());

    UserId userId = UserId.from(user.getUserId());
    FixedCostCode baseFixedCostCode = FixedCostCode.from(inputForm.getBaseFixedCostCode());

    // 支出項目コードを取得するため基準固定費を検索
    FixedCost baseFixedCost = fixedCostRepository.findByPrimaryKey(
            SearchQueryUserIdAndFixedCostCode.from(userId, baseFixedCostCode));
    if (baseFixedCost == null) {
        throw new MyHouseholdAccountBookRuntimeException(
                "選択した固定費が固定費テーブル:FIXED_COST_TABLEに存在しません。[fixedCostCode=" + baseFixedCostCode + "]");
    }

    // 送信済み inputForm をそのまま渡してレスポンスを生成（チェック状態・入力値を保持）
    return getBulkUpdateResponse(userId, baseFixedCost.getExpenditureItemCode(), inputForm);
}
```

> **`readBulkUpdateInfo` との使い分け**:
> - 初回表示（ActSelect.html からの遷移）: `readBulkUpdateInfo` → DB から基準固定費の値をフォーム初期値にセット
> - バリデーションエラー後の再表示: `readBulkUpdateBindingErrorSetInfo` → 送信済み `inputForm` でフォーム値を復元

---

### 4.4 戻り値の設計判断（No.4 回答）

`execBulkUpdate` の戻り値を `FixedCostBulkUpdateResponse` に統一する。

| 観点 | 内容 |
|---|---|
| **既存パターンとの一貫性** | `execDelete`→`ActSelectResponse`、`execAdd/Update`→`UpdateResponse` と同じく、操作に対応するResponseクラスを返す設計。`execBulkUpdate`→`BulkUpdateResponse` は自然な流れ |
| **Controller がシンプル** | 型変換・キャストなし。エラー時も成功時も同じ型で `build()` / `buildRedirect()` を呼べる |
| **成功時の無駄な処理** | 成功時も `FixedCostBulkUpdateResponse` を生成するが、`setTransactionSuccessFull()` 後はリダイレクトのみで画面データは不使用。最小限のコンストラクタ呼び出しで済む（DB アクセスなし） |

---

## 5. Response/Form 仕様

### 5.1 FixedCostInfoManageActSelectResponse 変更

#### 追加する内部クラス `SiblingFixedCostItem`

```java
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public static class SiblingFixedCostItem {
    private final String fixedCostCode;                  // 固定費コード
    private final String shiharaiName;                   // 支払名
    private final String shiharaiTukiDetailContext;      // 支払月（コード変換済み）
    private final String shiharaiTukiOptionalContext;    // 支払月任意詳細（その他任意選択時のみ値あり）
    private final String shiharaiDay;                    // 支払日（コード変換済み）
    private final String shiharaiKingaku;                // 支払金額（フォーマット済み）

    public static SiblingFixedCostItem from(String fixedCostCode, String shiharaiName,
            String shiharaiTukiDetailContext, String shiharaiTukiOptionalContext,
            String shiharaiDay, String shiharaiKingaku) {
        return new SiblingFixedCostItem(fixedCostCode, shiharaiName,
                shiharaiTukiDetailContext, shiharaiTukiOptionalContext, shiharaiDay, shiharaiKingaku);
    }
}
```

#### 追加するフィールド

```java
// 同一支出項目の兄弟固定費一覧（2件以上の場合のみ値あり）
@Getter
private List<SiblingFixedCostItem> siblingFixedCostItemList = new ArrayList<>();
```

#### 追加するメソッド

```java
// 兄弟固定費一覧を設定
public void addSiblingFixedCostItemList(List<SiblingFixedCostItem> list) {
    if (!CollectionUtils.isEmpty(list)) {
        siblingFixedCostItemList.addAll(list);
    }
}

// 兄弟固定費が存在するかどうか（テンプレート用）
public boolean isHasSiblingFixedCost() {
    return !siblingFixedCostItemList.isEmpty();
}
```

#### `build()` メソッド変更

```java
@Override
public ModelAndView build() {
    ModelAndView modelAndView = createModelAndView("itemmanage/FixedCostInfoManageActSelect");
    modelAndView.addObject("fixedCostInfo", fixedCostInfo);
    modelAndView.addObject("hasSiblingFixedCost", isHasSiblingFixedCost());        // 追加
    modelAndView.addObject("siblingFixedCostItemList", siblingFixedCostItemList);  // 追加
    return modelAndView;
}
```

---

### 5.2 FixedCostBulkUpdateForm（新規）

```java
package com.yonetani.webapp.accountbook.presentation.request.itemmanage;

import java.util.List;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FixedCostBulkUpdateForm {

    // 基準固定費コード（起点となった選択固定費のコード）
    private String baseFixedCostCode;

    // 一括適用する支払日（必須）
    @NotBlank
    private String shiharaiDay;

    // 一括適用する支払金額（必須、1以上）
    @NotNull
    @Min(1)
    private Integer shiharaiKingaku;

    // 更新対象の固定費コードリスト（チェックボックス選択値、1件以上必須）
    @NotEmpty
    private List<String> checkedFixedCostCodeList;
}
```

---

### 5.3 FixedCostBulkUpdateResponse（新規）

#### クラス階層

```
AbstractResponse
  └─ FixedCostBulkUpdateResponse  ← AbstractFixedCostItemListResponse は継承しない
                                     （一覧の意味が異なるため独立実装）
```

#### 内部クラス `BulkUpdateTargetItem`

```java
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public static class BulkUpdateTargetItem {
    private final String fixedCostCode;                  // 固定費コード
    private final String shiharaiName;                   // 支払名
    private final String shiharaiTukiDetailContext;      // 支払月（コード変換済み）
    private final String shiharaiTukiOptionalContext;    // 支払月任意詳細（その他任意選択時のみ値あり）
    private final String shiharaiDay;                    // 現在の支払日（コード変換済み）
    private final String shiharaiKingaku;                // 現在の支払金額（フォーマット済み）

    public static BulkUpdateTargetItem from(String fixedCostCode, String shiharaiName,
            String shiharaiTukiDetailContext, String shiharaiTukiOptionalContext,
            String shiharaiDay, String shiharaiKingaku) {
        return new BulkUpdateTargetItem(fixedCostCode, shiharaiName,
                shiharaiTukiDetailContext, shiharaiTukiOptionalContext, shiharaiDay, shiharaiKingaku);
    }
}
```

#### クラス定義

```java
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FixedCostBulkUpdateResponse extends AbstractResponse {

    // 入力フォーム（一括適用値の初期値入り）
    @Getter
    private final FixedCostBulkUpdateForm fixedCostBulkUpdateForm;

    // 支払日選択ボックス
    @Getter
    private final SelectViewItem shiharaiDaySelectList;

    // 支出項目名（画面見出し用）
    @Getter
    @Setter
    private String sisyutuItemName;

    // 一括更新対象の固定費一覧
    @Getter
    private List<BulkUpdateTargetItem> bulkUpdateTargetList = new ArrayList<>();

    public static FixedCostBulkUpdateResponse getInstance(
            FixedCostBulkUpdateForm form, List<OptionItem> shiharaiDayOptionList) {
        SelectViewItem shiharaiDaySelectList = SelectViewItem.from(shiharaiDayOptionList);
        shiharaiDaySelectList.setDefaultValue(form.getShiharaiDay());
        return new FixedCostBulkUpdateResponse(form, shiharaiDaySelectList);
    }

    public void addBulkUpdateTargetList(List<BulkUpdateTargetItem> list) {
        if (!CollectionUtils.isEmpty(list)) {
            bulkUpdateTargetList.addAll(list);
        }
    }

    @Override
    public ModelAndView build() {
        ModelAndView modelAndView = createModelAndView("itemmanage/FixedCostBulkUpdate");
        modelAndView.addObject("fixedCostBulkUpdateForm", fixedCostBulkUpdateForm);
        modelAndView.addObject("shiharaiDaySelectList", shiharaiDaySelectList);
        modelAndView.addObject("sisyutuItemName", sisyutuItemName);
        modelAndView.addObject("bulkUpdateTargetList", bulkUpdateTargetList);
        return modelAndView;
    }

    @Override
    protected String buildRedirectUrl(RedirectAttributes redirectAttributes) {
        return "redirect:/myhacbook/managebaseinfo/fixedcostinfo/updateComplete/";
    }
}
```

---

## 6. HTML 仕様

### 6.1 FixedCostInfoManageActSelect.html 変更

#### 追加エリアの挿入位置

「選択支出情報表示エリア」`</div>` の直後・「固定費一覧表示エリア」の前に挿入する。

```html
<!-- 同一分類の固定費一覧エリア（同一支出項目に複数固定費がある場合のみ表示） -->
<div class="card-body" th:if="${hasSiblingFixedCost}">
    <div class="card">
        <form name="FixedCostBulkUpdateLoad" method="get"
              th:action="@{/myhacbook/managebaseinfo/fixedcostinfo/bulkupdate/}">
        <div class="card-body">
            <p class="h6">■同一分類の固定費一覧■</p>
            <p class="card-text">以下の固定費がすべて同一の支出項目「<span th:text="${fixedCostInfo.sisyutuItemName}"></span>」に属しています。一括更新が可能です。</p>
            <table class="table table-sm table-bordered">
                <thead>
                    <tr class="table-secondary">
                        <th scope="col" class="w25">支払名</th>
                        <th scope="col" class="w10">支払月</th>
                        <th scope="col" class="w10">支払日</th>
                        <th scope="col" class="w10">支払金額</th>
                        <th scope="col">その他任意詳細</th>
                    </tr>
                </thead>
                <tbody>
                    <tr th:each="item, itemStat : ${siblingFixedCostItemList}"
                        th:classappend="${itemStat.even} ? 'table-warning'">
                        <td th:text="${item.shiharaiName}">支払名</td>
                        <td th:text="${item.shiharaiTukiDetailContext}">支払月</td>
                        <td th:text="${item.shiharaiDay}">支払日</td>
                        <td th:text="${item.shiharaiKingaku}" align="right">支払金額</td>
                        <td th:text="${item.shiharaiTukiOptionalContext}"></td>
                    </tr>
                </tbody>
            </table>
            <div class="mb-3">
                <button class="btn btn-primary" type="submit">一括更新</button>
            </div>
            <input type="hidden" name="baseFixedCostCode" th:value="${fixedCostInfo.fixedCostCode}" />
        </div>
        </form>
    </div>
</div>
```

> **補足**: 一括更新画面の初回表示は `GET /bulkupdate/?baseFixedCostCode=XXXX` で行う。
> POST フォームにすると CSRF トークンが必要になるが、一括更新画面への遷移はデータ更新を伴わない参照操作のため GET が適切。
> 一括更新の実行・キャンセルは BulkUpdate.html 内の同一フォームから POST で送信するため、
> `method` と `name` の設計はそれぞれのフォームで独立している。

---

### 6.2 FixedCostInfoManageUpdate.html 変更（★仕様追加）

#### 追加箇所

ボタン行（`<div class="mb-3">` 内の[登録]/[更新]ボタンの直後）にキャンセルボタンを追加する。

```html
<div class="mb-3">
    <button class="btn btn-primary" name="actionAdd" type="submit" th:if="*{#strings.equals(action, 'add')}">登録</button>
    <button class="btn btn-primary" name="actionUpdate" type="submit" th:if="*{#strings.equals(action, 'update')}">更新</button>
    <!-- ★追加★ -->
    <button class="btn btn-primary" name="actionCancel" type="submit">キャンセル</button>
    <input type="hidden" th:field="*{action}" />
    <input type="hidden" th:field="*{fixedCostCode}" />
    <input type="hidden" th:field="*{sisyutuItemCode}" />
</div>
```

> キャンセル先の振り分けは Controller 側で行う（フォームの `fixedCostCode` の有無で判定）。

---

### 6.3 FixedCostBulkUpdate.html（新規）

テンプレートパス: `src/main/resources/templates/itemmanage/FixedCostBulkUpdate.html`

```html
<!DOCTYPE html>
<html lang="ja" xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<head>
<meta charset="UTF-8">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" ...>
<link th:href="@{/css/MyHouseholdAccountBookCommon.css}" rel="stylesheet" type="text/css">
<title>情報管理(固定費)</title>
</head>
<body>
    <div id="header" th:insert="~{layout/myhacbookheader::header}">ヘッダー</div>

    <div class="col">
        <div class="card">
            <div class="card-body">
                <h5 class="card-title">情報管理(固定費)</h5>
                <th:block th:each="message : ${messages}">
                    <code><span th:text="${message}"></span></code><br>
                </th:block>
            </div>

            <!-- 一括適用値入力・更新対象選択・ボタンを1フォームで構成 -->
            <div class="card-body">
                <div class="card">
                    <form name="FixedCostBulkUpdate" method="post"
                          th:action="@{/myhacbook/managebaseinfo/fixedcostinfo/bulkupdate/}"
                          th:object="${fixedCostBulkUpdateForm}">
                    <div class="card-body">

                        <!-- 一括適用値入力エリア -->
                        <p class="h6">■一括適用値■</p>
                        <p class="card-text">支出項目：<span th:text="${sisyutuItemName}"></span></p>
                        <div class="mb-3">
                            <label class="form-label">■支払日</label>
                            <select class="form-select w20" th:errorclass="is-invalid" th:field="*{shiharaiDay}">
                                <option th:each="item : ${shiharaiDaySelectList.optionList}"
                                        th:value="${item.value}" th:text="${item.text}"></option>
                            </select>
                            <p class="invalid-feedback" th:errors="*{shiharaiDay}"></p>
                        </div>
                        <div class="mb-3">
                            <label class="form-label w10">■支払金額</label>
                            <div class="input-group w10">
                                <input type="text" class="form-control" th:errorclass="is-invalid"
                                       th:field="*{shiharaiKingaku}" required>円
                                <p class="invalid-feedback" th:errors="*{shiharaiKingaku}"></p>
                            </div>
                        </div>

                        <!-- 更新対象選択テーブル -->
                        <p class="h6">■更新対象の固定費一覧■</p>
                        <p class="card-text">更新対象の固定費にチェックを入れてください。</p>
                        <p class="invalid-feedback" style="display: block;" th:errors="*{checkedFixedCostCodeList}"></p>
                        <table class="table table-sm table-bordered">
                            <thead>
                                <tr class="table-secondary">
                                    <th scope="col" class="w3">選択</th>
                                    <th scope="col" class="w25">支払名</th>
                                    <th scope="col" class="w10">支払月</th>
                                    <th scope="col" class="w10">現在の支払日</th>
                                    <th scope="col" class="w10">現在の支払金額</th>
                                    <th scope="col">その他任意詳細</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr th:each="item, itemStat : ${bulkUpdateTargetList}"
                                    th:classappend="${itemStat.even} ? 'table-warning'">
                                    <td class="text-center">
                                        <!--/*
                                         * checkedFixedCostCodeList に item.fixedCostCode が含まれていればチェック状態を復元。
                                         * 初回表示・バリデーションエラー後のチェックなし以外は常にチェック済み。
                                         */-->
                                        <input type="checkbox"
                                               name="checkedFixedCostCodeList"
                                               th:value="${item.fixedCostCode}"
                                               th:checked="${fixedCostBulkUpdateForm.checkedFixedCostCodeList != null
                                                            and #lists.contains(fixedCostBulkUpdateForm.checkedFixedCostCodeList, item.fixedCostCode)}">
                                    </td>
                                    <td th:text="${item.shiharaiName}">支払名</td>
                                    <td th:text="${item.shiharaiTukiDetailContext}">支払月</td>
                                    <td th:text="${item.shiharaiDay}">支払日</td>
                                    <td th:text="${item.shiharaiKingaku}" align="right">支払金額</td>
                                    <td th:text="${item.shiharaiTukiOptionalContext}"></td>
                                </tr>
                            </tbody>
                        </table>

                        <!-- ボタン -->
                        <div class="mb-3">
                            <button class="btn btn-primary" name="actionBulkUpdate" type="submit">一括更新</button>
                            <button class="btn btn-primary" name="actionCancel" type="submit">キャンセル</button>
                        </div>

                        <input type="hidden" th:field="*{baseFixedCostCode}" />
                    </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <div id="footer" th:insert="~{layout/myhacbookfooter::footer}">フッター</div>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
</body>
</html>
```

#### チェックボックス状態の復元（No.3 回答）

バリデーションエラー時、`checkedFixedCostCodeList` にはサブミットされたコードのみ格納される。
テンプレート側で `#lists.contains` を使って「送信値に含まれているかどうか」でチェック状態を再現する。

- **初回表示**: `checkedFixedCostCodeList` が `null` のため全チェックなし（デフォルトはチェック無し）
- **バリデーションエラー後**: フォームのチェック状態をそのまま復元

---

## 7. Controller 仕様

### 7.1 追加・変更するルート

```java
// ★仕様追加★ 更新画面からのキャンセル（POST - actionCancel）
// fixedCostCode の有無でキャンセル先を振り分ける
@PostMapping(value = "/update/", params = "actionCancel")
public ModelAndView postUpdateCancel(
        @RequestParam("action") String action,
        @RequestParam(value = "fixedCostCode", required = false, defaultValue = "") String fixedCostCode) {
    log.debug("postUpdateCancel: action=" + action + ",fixedCostCode=" + fixedCostCode);
    if (MyHouseholdAccountBookContent.ACTION_TYPE_ADD.equals(action)) {
        // 新規追加からのキャンセル → 初期表示画面へ
        return this.inquiryUseCase.readInitInfo(loginUserSession.getLoginUserInfo())
                .setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
                .build();
    } else {
        // 更新からのキャンセル → 処理選択画面へ
        return this.inquiryUseCase.readActSelectItemInfo(loginUserSession.getLoginUserInfo(), fixedCostCode)
                .setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
                .build();
    }
}

// 一括更新画面表示（GET - /bulkupdateload/?baseFixedCostCode=XXXX）
// ActSelect.html の兄弟固定費フォームから GET で遷移する（参照操作のため GET が適切）
@GetMapping("/bulkupdateload/")
public ModelAndView getBulkUpdateLoad(
        @RequestParam("baseFixedCostCode") String baseFixedCostCode) {
    log.debug("getBulkUpdateLoad: baseFixedCostCode=" + baseFixedCostCode);
    return this.inquiryUseCase.readBulkUpdateInfo(loginUserSession.getLoginUserInfo(), baseFixedCostCode)
            .setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
            .build();
}

// 一括更新実行（POST - actionBulkUpdate）
// バリデーションエラー時は readBulkUpdateBindingErrorSetInfo で一括更新画面に戻る
@PostMapping(value = "/bulkupdate/", params = "actionBulkUpdate")
public ModelAndView postBulkUpdate(
        @ModelAttribute @Validated FixedCostBulkUpdateForm inputForm,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes) {
    log.debug("postBulkUpdate: input=" + inputForm);
    if (bindingResult.hasErrors()) {
        // バリデーションエラー → 一括更新画面に戻る（送信済みフォームで入力値・チェック状態を復元）
        return this.inquiryUseCase.readBulkUpdateBindingErrorSetInfo(
                    loginUserSession.getLoginUserInfo(), inputForm)
                .setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
                .build();
    }
    return this.registConfirmUseCase.execBulkUpdate(loginUserSession.getLoginUserInfo(), inputForm)
            .buildRedirect(redirectAttributes);
}

// 一括更新画面のキャンセル（POST - actionCancel、/bulkupdate/ への送信）
@PostMapping(value = "/bulkupdate/", params = "actionCancel")
public ModelAndView postBulkUpdateCancel(
        @RequestParam("baseFixedCostCode") String baseFixedCostCode) {
    log.debug("postBulkUpdateCancel: baseFixedCostCode=" + baseFixedCostCode);
    return this.inquiryUseCase.readActSelectItemInfo(loginUserSession.getLoginUserInfo(), baseFixedCostCode)
            .setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
            .build();
}
```

> **設計の補足**:
> - `ActSelect.html` の兄弟固定費フォームは `method="get"` + アクション `/bulkupdate/` で初回表示（参照操作のため GET が適切）。
> - `BulkUpdate.html` の[一括更新]・[キャンセル]ボタンは同一フォーム内のため、アクションURLは `/bulkupdate/` に統一し、
>   `params` で処理を振り分ける（`method="post"`）。

### 7.2 Controller マッピング全体（変更後）

| HTTPメソッド | パス | params | 処理 |
|---|---|---|---|
| GET | `/initload/` | - | 初期表示 |
| GET | `/select` | - | 処理選択画面 |
| GET | `/addload` | - | 追加画面（支出項目指定） |
| GET | `/updateload/` | `actionAdd` | 追加画面表示 |
| GET | `/updateload/` | `actionUpdate` | 更新画面表示 |
| GET | `/updateload/` | `actionCancel` | 初期画面へキャンセル（既存） |
| POST | `/delete/` | - | 削除実行（既存） |
| POST | `/update/` | `actionAdd` | 追加実行（既存） |
| POST | `/update/` | `actionUpdate` | 更新実行（既存） |
| **POST** | **`/update/`** | **`actionCancel`** | **更新画面からのキャンセル（★新規）** |
| **GET** | **`/bulkupdateload/`** | **-** | **一括更新画面表示（★新規）** |
| **POST** | **`/bulkupdate/`** | **`actionBulkUpdate`** | **一括更新実行（バリデーションエラー時は画面再表示）（★新規）** |
| **POST** | **`/bulkupdate/`** | **`actionCancel`** | **一括更新画面からのキャンセル（★新規）** |
| GET | `/updateComplete/` | - | 完了リダイレクト処理（既存） |

---

## 8. リポジトリ変更

**変更なし。** 既存の以下メソッドを使用する：

- `countByExpenditureItemCode(SearchQueryUserIdAndExpenditureItemCode)` — 兄弟固定費件数チェック
- `findByExpenditureItemCode(SearchQueryUserIdAndExpenditureItemCode)` — 兄弟固定費一覧取得
- `findByPrimaryKey(SearchQueryUserIdAndFixedCostCode)` — 個別固定費取得
- `update(FixedCost)` — 更新実行

---

## 9. プロトタイプ（紙芝居）

紙芝居パス: `C:\develop\【紙芝居】家計簿\紙芝居\ManageBaseInfo\`

| 紙芝居ファイル | 対応する本番HTML | 変更内容 |
|---|---|---|
| `ManageFixedCostActSelect.html` | `FixedCostInfoManageActSelect.html` | 「同一分類の固定費一覧」エリア・[一括更新]ボタン追加 |
| `ManageFixedCostUpd.html` | `FixedCostInfoManageUpdate.html` | [キャンセル]ボタン追加 |
| `ManageFixedCostBulkUpd.html`（新規） | `FixedCostBulkUpdate.html` | 一括更新画面（新規） |

バックアップ: `ManageFixedCostActSelect.html_bk`、`ManageFixedCostUpd.html_bk` を作成済み。⇒プロトタイプのユーザ確認完了しバックアップは削除済み

---

## 10. テスト方針

### 10.1 単体テスト

| テストクラス | テスト内容 |
|---|---|
| `FixedCostBulkUpdateFormTest`（既存クラスに追加） | `@NotBlank`, `@NotNull`, `@Min(1)`, `@NotEmpty` のバリデーション |

### 10.2 UseCase 統合テスト

一括更新機能のテストは、既存クラスへの追加ではなく**専用の新規テストクラス**として分離して作成した。
既存クラスとは異なるテストデータ（兄弟固定費ありの設計）が必要なためである。

#### テストデータ（`FixedCostBulkUpdateIntegrationTest.sql`）

| 固定費コード | 固定費名 | 支出項目コード | 支払日 | 支払金額 | 備考 |
|---|---|---|---|---|---|
| 0001 | 家賃 | 0030（地代家賃＞家賃） | 27日 | 60,000円 | 兄弟固定費（その1） |
| 0002 | 共益費 | 0030（地代家賃＞家賃） | 27日 | 8,000円 | 兄弟固定費（その2） |
| 0003 | 電気代概算 | 0037（電気代） | 27日 | 12,000円 | 兄弟なし |

#### FixedCostInquiryUseCaseBulkUpdateIntegrationTest（新規、6テスト）

| テスト | 内容 |
|---|---|
| ① `readActSelectItemInfo_hasSiblingFixedCostTrue` | 0001指定 → 兄弟あり（siblingList=2件、0001+0002） |
| ② `readActSelectItemInfo_hasSiblingFixedCostFalse` | 0003指定 → 兄弟なし（siblingList=0件） |
| ③ `readBulkUpdateInfo_success` | 0001指定 → フォーム初期値(day=27,kingaku=60000)、targetList=2件、支出項目名確認 |
| ④ `readBulkUpdateInfo_notFound` | 存在しない9999指定 → 例外発生 |
| ⑤ `readBulkUpdateBindingErrorSetInfo_success` | 送信済みフォーム復元確認（チェック状態・入力値保持） |
| ⑥ `readBulkUpdateBindingErrorSetInfo_notFound` | baseFixedCostCode=9999 → 例外発生 |

#### FixedCostRegistConfirmUseCaseBulkUpdateIntegrationTest（新規、3テスト）

| テスト | 内容 |
|---|---|
| ① `testExecBulkUpdate_success_2件` | 0001と0002を一括更新 → DB確認（day=25、kingaku=70000）、0003は変更なし |
| ② `testExecBulkUpdate_success_1件` | 0001のみ更新 → 0002は変更なし |
| ③ `testExecBulkUpdate_targetNotFound` | 存在しない9999指定 → 例外発生確認（DB確認はロールバックテストで実施） |

#### FixedCostBulkUpdateRollbackTest（新規、1テスト）

**ロールバック確認専用テストクラス**。`@Transactional` を付与しないことで、サービス層の `@Transactional` を本物のトランザクション境界とする。

> **分離理由**: `@Transactional` 付きのテストクラスでは、テストとサービスが同一トランザクションを共有する。
> 例外発生後も rollback-only になるだけでトランザクション内のDB変更は可視のままとなるため、
> 例外後のDB確認でロールバックを証明できない。

| テスト | 内容 |
|---|---|
| `testExecBulkUpdate_RollbackOnTargetNotFound` | checkedList=["0001","9999"] → 0001更新後9999で例外 → 0001がロールバックされ元の値に戻ることをDB確認 |

DB初期化は `@Sql` の `BEFORE_TEST_METHOD` / `AFTER_TEST_METHOD` で管理:
- BEFORE: cleanup + `schema_test.sql` + `FixedCostBulkUpdateIntegrationTest.sql`
- AFTER: cleanup（`FixedCostBulkUpdateRollbackTest-cleanup.sql`）

### 10.3 Controller 統合テスト

#### FixedCostInfoManageControllerIntegrationTest（既存クラスに追加、2テスト）

`POST /update/ actionCancel` のテストを既存クラスに追加した（既存SQLデータで対応可能なため）。

| テスト | 内容 |
|---|---|
| ⑭ `testPostUpdateCancel_actionAdd` | action=add → `readInitInfo` → Init.html（fixedCostItemListあり） |
| ⑮ `testPostUpdateCancel_actionUpdate` | action=update、fixedCostCode=0001 → `readActSelectItemInfo` → ActSelect.html |

#### FixedCostInfoManageBulkUpdateControllerIntegrationTest（新規、5テスト）

`/bulkupdate/` | `/bulkupdate/` の各エンドポイントのテストは**専用の新規テストクラス**として分離した（兄弟固定費ありのSQLデータが必要なため）。

| テスト | 内容 |
|---|---|
| ① GET `/bulkupdateload/?baseFixedCostCode=0001` | 一括更新画面表示（bulkUpdateTargetList=2件、sisyutuItemName確認） |
| ② POST `/bulkupdate/ actionBulkUpdate` shiharaiDay空 | `@NotBlank` バリデーションエラー → 一括更新画面に戻る |
| ③ POST `/bulkupdate/ actionBulkUpdate` チェックなし | `@NotEmpty` バリデーションエラー → 一括更新画面に戻る |
| ④ POST `/bulkupdate/ actionBulkUpdate` 成功 | 3xxリダイレクト → `updateComplete/` |
| ⑤ POST `/bulkupdate/ actionCancel` | ActSelect.html → hasSiblingFixedCost=true |
