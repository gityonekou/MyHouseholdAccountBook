/**
 * 月別固定費一覧画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/05/27 : 1.01.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage.fixedcost;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *<pre>
 * 月別固定費一覧画面表示情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.01)
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
	// 固定費一覧情報
	@Getter
	@Setter
	private List<AbstractFixedCostItemListResponse.FixedCostItem> fixedCostItemList = new ArrayList<>();
	// 当月合計金額
	@Getter
	@Setter
	private String monthlyTotal;

	/**
	 *<pre>
	 * 引数の値からレスポンス情報を生成して返します。
	 *</pre>
	 * @param displayMonthLabel 表示中の月ラベル（"M月" 形式）
	 * @param currentMonth 表示中の月番号（"MM" 形式、セッション更新用）
	 * @param prevMonth 前月番号（"MM" 形式）
	 * @param nextMonth 次月番号（"MM" 形式）
	 * @return 月別固定費一覧画面表示情報
	 *
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
