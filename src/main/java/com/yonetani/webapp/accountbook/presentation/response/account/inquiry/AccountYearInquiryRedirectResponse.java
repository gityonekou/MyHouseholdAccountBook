/**
 * マイ家計簿の年間収支(マージ、明細)の各画面から遷移する以下画面へのリダイレクト情報です。
 * ・各月の収支画面
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/02/09 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.account.inquiry;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * マイ家計簿の年間収支(マージ、明細)の各画面から遷移する以下画面へのリダイレクト情報です。
 * ・各月の収支画面
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountYearInquiryRedirectResponse extends AbstractResponse {

	// リダイレクト先URL
	private final String redirectUrl;
	// 対象年月
	private final String targetYearMonth;
	
	/**
	 *<pre>
	 * 各月の収支画面にリダイレクトするためのレスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearMonth 対象年月
	 * @return 各月の収支画面にリダイレクトするためのレスポンス情報
	 *
	 */
	public static AccountYearInquiryRedirectResponse getInquiryMonthRedirectInstance(String targetYearMonth) {
		AccountYearInquiryRedirectResponse response = new AccountYearInquiryRedirectResponse(
				// 買い物登録画面リダイレクトURL
				"redirect:/myhacbook/accountinquiry/accountmonth/registComplete/",
				// 対象年月
				targetYearMonth);
		response.setTransactionSuccessFull();
		return response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		throw new UnsupportedOperationException("このクラスではbuildは未サポートです。");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String buildRedirectUrl(RedirectAttributes redirectAttributes) {
		// 表示対象の年月を設定
		redirectAttributes.addAttribute("targetYearMonth", targetYearMonth);
		// リダイレクトするURL
		return redirectUrl;
	}
}
