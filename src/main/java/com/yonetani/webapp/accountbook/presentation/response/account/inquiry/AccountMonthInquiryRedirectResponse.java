/**
 * マイ家計簿の各月の収支画面から遷移する以下画面へのリダイレクト情報です。
 * ・買い物登録画面
 * ・各月の収支詳細表示画面
 * ・収支登録画面(更新)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/16 : 1.00.00  新規作成
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
 * マイ家計簿の各月の収支画面から遷移する以下画面へのリダイレクト情報です。
 * ・買い物登録画面
 * ・各月の収支詳細表示画面
 * ・収支登録画面(更新)
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountMonthInquiryRedirectResponse extends AbstractResponse {
	
	// リダイレクト先URL
	private final String redirectUrl;
	// 対象年月
	private final String targetYearMonth;
	
	/**
	 *<pre>
	 * 買い物登録画面にリダイレクトするためのレスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearMonth 対象年月
	 * @return 買い物登録画面にリダイレクトするためのレスポンス情報
	 *
	 */
	public static AccountMonthInquiryRedirectResponse getShoppinAddRedirectInstance(String targetYearMonth) {
		AccountMonthInquiryRedirectResponse response = new AccountMonthInquiryRedirectResponse(
				// 買い物登録画面リダイレクトURL
				"redirect:/myhacbook/accountregist/shopping/",
				// 対象年月
				targetYearMonth);
		response.setTransactionSuccessFull();
		return response;
	}
	
	/**
	 *<pre>
	 * 各月の収支詳細表示画面にリダイレクトするためのレスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearMonth 対象年月
	 * @return 各月の収支詳細表示画面にリダイレクトするためのレスポンス情報
	 *
	 */
	public static AccountMonthInquiryRedirectResponse getAccountMonthDetailRedirectInstance(String targetYearMonth) {
		AccountMonthInquiryRedirectResponse response = new AccountMonthInquiryRedirectResponse(
				// 各月の収支詳細表示画面リダイレクトURL
				"redirect:/myhacbook/accountinquiry/accountdetail/month/",
				// 対象年月
				targetYearMonth);
		response.setTransactionSuccessFull();
		return response;
	}
	
	/**
	 *<pre>
	 * 収支登録画面(更新)にリダイレクトするためのレスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearMonth 対象年月
	 * @return 収支登録画面(更新)にリダイレクトするためのレスポンス情報
	 *
	 */
	public static AccountMonthInquiryRedirectResponse getAccountMonthUpdateRedirectInstance(String targetYearMonth) {
		AccountMonthInquiryRedirectResponse response = new AccountMonthInquiryRedirectResponse(
				// 収支登録画面(更新)リダイレクトURL
				"redirect:/myhacbook/accountregist/incomeandexpenditure/updateload/",
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
