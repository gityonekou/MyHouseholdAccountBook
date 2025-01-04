/**
 * 買い物登録の各画面(トップ画面を含む)から遷移する以下画面へのリダイレクト情報です。
 * ・買い物登録(簡易タイプ)画面
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.account.regist;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 買い物登録の各画面(トップ画面を含む)から遷移する以下画面へのリダイレクト情報です。
 * ・買い物登録(簡易タイプ)画面
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ShoppingRegistRedirectResponse extends AbstractResponse {
	
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
	public static ShoppingRegistRedirectResponse getShoppingRegistRedirectInstance(String targetYearMonth) {
		ShoppingRegistRedirectResponse response = new ShoppingRegistRedirectResponse(
				// 買い物登録(簡易タイプ)画面リダイレクトURL
				"redirect:/myhacbook/accountregist/shoppingregist/",
				// 対象年月
				targetYearMonth);
		response.setTransactionSuccessFull();
		return response;
	}
	
	/**
	 *<pre>
	 * 買い物登録(簡易タイプ)画面にリダイレクトするためのレスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearMonth 対象年月
	 * @return 買い物登録(簡易タイプ)画面にリダイレクトするためのレスポンス情報
	 *
	 */
	public static ShoppingRegistRedirectResponse getSimpleShoppingRegistRedirectInstance(String targetYearMonth) {
		ShoppingRegistRedirectResponse response = new ShoppingRegistRedirectResponse(
				// 買い物登録(簡易タイプ)画面リダイレクトURL
				"redirect:/myhacbook/accountregist/simpleshoppingregist/",
				// 対象年月
				targetYearMonth);
		response.setTransactionSuccessFull();
		return response;
	}
	
	/**
	 *<pre>
	 * 買い物登録方法選択画面(メニュー選択画面)にリダイレクトするためのレスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearMonth 対象年月
	 * @return 買い物登録方法選択画面(メニュー選択画面)にリダイレクトするためのレスポンス情報
	 *
	 */
	public static ShoppingRegistRedirectResponse getReturnShoppingTopRedirectInstance(String targetYearMonth) {
		ShoppingRegistRedirectResponse response = new ShoppingRegistRedirectResponse(
				// 買い物登録方法選択画面(メニュー選択画面)リダイレクトURL
				"redirect:/myhacbook/accountregist/shoppingtopmenu/",
				// 対象年月
				targetYearMonth);
		response.setTransactionSuccessFull();
		return response;
	}
	
	/**
	 *<pre>
	 * 各月の収支参照画面にリダイレクトするためのレスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearMonth 対象年月
	 * @return 各月の収支参照画面にリダイレクトするためのレスポンス情報
	 *
	 */
	public static ShoppingRegistRedirectResponse getReturnInquiryMonthRedirectInstance(String targetYearMonth) {
		ShoppingRegistRedirectResponse response = new ShoppingRegistRedirectResponse(
				// 各月の収支参照画面リダイレクトURL
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
