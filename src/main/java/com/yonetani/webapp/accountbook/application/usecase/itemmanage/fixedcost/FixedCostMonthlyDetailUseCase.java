/**
 * 月別固定費一覧画面のユースケースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/05/27 : 1.01.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage.fixedcost;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.yonetani.webapp.accountbook.application.usecase.common.CodeTableItemComponent;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCostInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCostInquiryList.FixedCostInquiryItem;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.repository.account.fixedcost.FixedCostTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.common.AccountBookUserRepository;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.fixedcost.AbstractFixedCostItemListResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.fixedcost.AbstractFixedCostItemListResponse.FixedCostItem;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.fixedcost.FixedCostMonthlyDetailResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 月別固定費一覧画面のユースケースです。
 * 指定した月に支払いが発生する固定費の一覧と当月合計金額を返します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.01)
 *
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class FixedCostMonthlyDetailUseCase {

	// コードテーブル
	private final CodeTableItemComponent codeTableItem;
	// 固定費テーブル:FIXED_COST_TABLEリポジトリー
	private final FixedCostTableRepository fixedCostRepository;
	// 家計簿ユーザーリポジトリー
	private final AccountBookUserRepository accountBookUserRepository;

	/**
	 *<pre>
	 * 月別固定費一覧画面の表示情報を取得します。
	 * 指定した月に支払いが発生する固定費の一覧と当月合計金額を返します。
	 * monthStr が null の場合は現在の対象月を使用します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param monthStr 表示する月（"MM"形式）。null の場合は現在の対象月を使用
	 * @return 月別固定費一覧画面の表示情報
	 *
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
			// 当月合計（calculateMonthlyTotal は月部分のみ使用するため年は任意値（2000年）を設定
			response.setMonthlyTotal(
					allFixedCosts.calculateMonthlyTotal(
							TargetYearMonth.from(String.format("2000%02d", monthValue))).toFormatString());
		}
		return response;
	}

	/**
	 *<pre>
	 * 固定費一覧明細情報(ドメイン)のリストから、画面表示用の固定費一覧明細情報のリストを生成して返します。
	 *</pre>
	 * @param items 固定費一覧明細情報(ドメイン)のリスト
	 * @return 画面表示用の固定費一覧明細情報のリスト
	 *
	 */
	private List<FixedCostItem> createFixedCostItemList(List<FixedCostInquiryItem> items) {
		return items.stream().map(domain ->
			AbstractFixedCostItemListResponse.FixedCostItem.from(
				// 固定費コード
				domain.getFixedCostCode().getValue(),
				// 支出項目名
				domain.getExpenditureItemName().getValue(),
				// 支払名
				domain.getFixedCostName().getValue(),
				// 支払月：コード変換
				codeTableItem.getCodeValue(
						MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_TUKI,
						domain.getFixedCostTargetPaymentMonth().getValue()),
				// 支払日：コード変換
				codeTableItem.getCodeValue(
						MyHouseholdAccountBookContent.CODE_DEFINES_FIXED_COST_SHIHARAI_DAY,
						domain.getFixedCostPaymentDay().getValue()),
				// 支払金額
				domain.getFixedCostPaymentAmount().toFormatString(),
				// その他任意詳細
				domain.getFixedCostTargetPaymentMonthOptionalContext().getValue())
		).collect(Collectors.toUnmodifiableList());
	}
}
