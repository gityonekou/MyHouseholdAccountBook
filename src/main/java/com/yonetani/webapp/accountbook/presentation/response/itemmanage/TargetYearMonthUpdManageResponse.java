/**
 * 情報管理(対象年月更新)画面レスポンス情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/01/13 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yonetani.webapp.accountbook.domain.type.common.NextTargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 情報管理(対象年月更新)画面レスポンス情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TargetYearMonthUpdManageResponse extends AbstractResponse {

	// 現在の対象年月の年の値
	private final String viewTargetYear;
	// 現在の対象年月の月の値
	private final String viewTargetMonth;
	// 更新後の対象年月の年の値
	private final String viewNextYear;
	// 更新後の対象年月の月の値
	private final String viewNextMonth;
	// 更新後の対象年月
	private final String nextYearMonth;
	// 対象年月更新不のフラグ:更新可の場合、trueが設定され、登録ボタンは押下可能となります。
	private final boolean updateAcceptFlg;
	
	/**
	 *<pre>
	 * 対象年月更新可の画面表示情報でレスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearMonth 現在の対象年月(YYYMM)
	 * @param nextYearMonth 更新後の対象年月(YYYMM)
	 * @return 情報管理(対象年月更新)画面表示情報
	 *
	 */
	public static TargetYearMonthUpdManageResponse getUpdateAcceptInstance(TargetYearMonth targetYearMonth, NextTargetYearMonth nextYearMonth) {
		TargetYearMonthUpdManageResponse response = new TargetYearMonthUpdManageResponse(
				targetYearMonth.getYear(),
				targetYearMonth.getMonth(),
				nextYearMonth.getYear().getValue(),
				nextYearMonth.getMonth().getValue(),
				nextYearMonth.getValue(),
				true);
		return response;
	}
	
	/**
	 *<pre>
	 * 対象年月更新不可の画面表示情報でレスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearMonth 現在の対象年月(YYYMM)
	 * @return 情報管理(対象年月更新)画面表示情報
	 *
	 */
	public static TargetYearMonthUpdManageResponse getUpdateFailInstance() {
		TargetYearMonthUpdManageResponse response = new TargetYearMonthUpdManageResponse(
				"",
				"",
				"",
				"",
				"",
				false);
		return response;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 情報管理(対象年月更新)画面のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("itemmanage/TargetYearMonthUpdManage");
		// 現在の対象年月の年の値
		modelAndView.addObject("viewTargetYear", viewTargetYear);
		// 現在の対象年月の月の値
		modelAndView.addObject("viewTargetMonth", viewTargetMonth);
		// 更新後の対象年月の年の値
		modelAndView.addObject("viewNextYear", viewNextYear);
		// 更新後の対象年月の月の値
		modelAndView.addObject("viewNextMonth", viewNextMonth);
		// 更新後の対象年月
		modelAndView.addObject("nextYearMonth", nextYearMonth);
		// 対象年月更新不可のフラグ:更新不可の場合、trueが設定され、登録ボタンは押下可能となります
		modelAndView.addObject("updateAcceptFlg", updateAcceptFlg);
		return modelAndView;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String buildRedirectUrl(RedirectAttributes redirectAttributes) {
		// 表示対象の年月(更新後の対象年月の値)を設定
		redirectAttributes.addAttribute("targetYearMonth", nextYearMonth);
		// 登録完了後、リダイレクトするURL(各月の収支参照画面)
		return "redirect:/myhacbook/accountinquiry/accountmonth/registComplete/";
	}
}
