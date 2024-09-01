/**
 * 収支登録内容確認画面レスポンス情報です。
 * 収入一覧情報、支出一覧情報を持ちます。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/31 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.account.regist;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 収支登録内容確認画面レスポンス情報です。
 * 収入一覧情報、支出一覧情報を持ちます。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class IncomeAndExpenditureRegistCheckResponse extends AbstractIncomeAndExpenditureRegistResponse {
	// 収支の対象年月
	private final String targetYearMonth;

	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearMonth 収支対象の年月(YYYMM)
	 * @return 収支登録内容確認画面表示情報
	 *
	 */
	public static IncomeAndExpenditureRegistCheckResponse getInstance(String targetYearMonth) {
		// 収入入力フォーム、収入区分選択ボックス、支出入力フォーム、支出区分選択ボックスなしで画面を表示
		IncomeAndExpenditureRegistCheckResponse response = new IncomeAndExpenditureRegistCheckResponse(targetYearMonth);
		response.setYearMonth(targetYearMonth);
		return response;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		return createModelAndView("account/regist/IncomeAndExpenditureRegistCheck");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String buildRedirectUrl(RedirectAttributes redirectAttributes) {
		
		// 表示対象の年月を設定
		redirectAttributes.addAttribute("targetYearMonth", targetYearMonth);
		// 登録完了後、リダイレクトするURL(各月の収支参照画面)
		return "redirect:/myhacbook/accountinquiry/accountmonth/registComplete/";
	}
	
}
