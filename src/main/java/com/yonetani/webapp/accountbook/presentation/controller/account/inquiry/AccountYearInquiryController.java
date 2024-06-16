/**
 * マイ家計簿の年間収支画面表示を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・指定年の年間収支(マージ)画面表示(POST)
 * ・指定年の年間収支(明細)画面表示(POST)
 * 
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/08/26 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller.account.inquiry;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.application.usecase.account.inquiry.AccountYearInquiryUseCase;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * マイ家計簿の年間収支画面表示を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・指定年の年間収支(マージ)画面表示(POST)
 * ・指定年の年間収支(明細)画面表示(POST)
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/myhacbook/accountinquiry/accountyear/")
public class AccountYearInquiryController {
	
	// usecase
	private final AccountYearInquiryUseCase usecasee;
	// ログインユーザセッションBean
	private final LoginUserSession loginUserSession;
	
	/**
	 *<pre>
	 * 年間収支(マージ)画面表示のPOST要求時マッピングです。他のタブからの遷移時に呼び出されます。
	 * 
	 *</pre>
	 * @param targetYear 表示対象の年度
	 * @param returnYearMonth 各月の収支画面に戻る場合に表示する年月の値
	 * @return マイ家計簿 年間収支(マージ)画面
	 *
	 */
	@PostMapping("/mage/")
	public ModelAndView postAccountYearMage(
			@RequestParam("targetYear") String targetYear,
			@RequestParam("returnYearMonth") String returnYearMonth) {
		log.debug("postAccountYearMage:targetYear="+ targetYear + ",returnYearMonth:=" + returnYearMonth);
		
		// 画面表示データを読込
		return this.usecasee.readMage(loginUserSession.getLoginUserInfo(), targetYear, returnYearMonth)
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 前年度の年間収支(マージ)画面表示のPOST要求時マッピングです。
	 * 
	 *</pre>
	 * @param beforeYear 表示する前年の値(表示対象の年度)
	 * @param returnYearMonth 各月の収支画面に戻る場合に表示する年月の値
	 * @return マイ家計簿 年間収支(マージ)画面
	 *
	 */
	@PostMapping(value="/magetargetcontrol/", params = "targetBeforeBtn")
	public ModelAndView postBeforeAccountYearMage(
			@RequestParam("beforeYear") String beforeYear,
			@RequestParam("returnYearMonth") String returnYearMonth) {
		log.debug("postBeforeAccountYearMage:beforeYear="+ beforeYear + ",returnYearMonth:=" + returnYearMonth);
		
		// 画面表示データを読込
		return this.usecasee.readMage(loginUserSession.getLoginUserInfo(), beforeYear, returnYearMonth)
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 翌年度の年間収支(マージ)画面表示のPOST要求時マッピングです。
	 * 
	 *</pre>
	 * @param nextYear 表示する翌年の値(表示対象の年度)
	 * @param returnYearMonth 各月の収支画面に戻る場合に表示する年月の値
	 * @return マイ家計簿 年間収支(マージ)画面
	 *
	 */
	@PostMapping(value="/magetargetcontrol/", params = "targetNextBtn")
	public ModelAndView postNextAccountYearMage(
			@RequestParam("nextYear") String nextYear,
			@RequestParam("returnYearMonth") String returnYearMonth) {
		log.debug("postNextAccountYearMage:nextYear="+ nextYear + ",returnYearMonth:=" + returnYearMonth);
		
		// 画面表示データを読込
		return this.usecasee.readMage(loginUserSession.getLoginUserInfo(), nextYear, returnYearMonth)
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 年間収支(明細)画面表示のPOST要求時マッピングです。他のタブからの遷移時に呼び出されます。
	 * 
	 *</pre>
	 * @param targetYear 表示対象の年度
	 * @param returnYearMonth 各月の収支画面に戻る場合に表示する年月の値
	 * @return マイ家計簿 年間収支(明細)画面
	 *
	 */
	@PostMapping("/meisai/")
	public ModelAndView postAccountYearMeisai(
			@RequestParam("targetYear") String targetYear,
			@RequestParam("returnYearMonth") String returnYearMonth) {
		log.debug("postAccountYearMeisai:targetYear="+ targetYear + ",returnYearMonth:=" + returnYearMonth);
		
		// 画面表示データを読込
		return this.usecasee.readMeisai(loginUserSession.getLoginUserInfo(), targetYear, returnYearMonth)
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 前年度の年間収支(明細)画面表示のPOST要求時マッピングです。
	 * 
	 *</pre>
	 * @param beforeYear 表示する前年の値(表示対象の年度)
	 * @param returnYearMonth 各月の収支画面に戻る場合に表示する年月の値
	 * @return マイ家計簿 年間収支(明細)画面
	 *
	 */
	@PostMapping(value="/meisaitargetcontrol/", params = "targetBeforeBtn")
	public ModelAndView postBeforeAccountYearMeisai(
			@RequestParam("beforeYear") String beforeYear,
			@RequestParam("returnYearMonth") String returnYearMonth) {
		log.debug("postBeforeAccountYearMeisai:beforeYear="+ beforeYear + ",returnYearMonth:=" + returnYearMonth);
		
		// 画面表示データを読込
		return this.usecasee.readMeisai(loginUserSession.getLoginUserInfo(), beforeYear, returnYearMonth)
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 翌年度の年間収支(明細)画面表示のPOST要求時マッピングです。
	 *</pre>
	 * @param nextYear 表示する翌年の値(表示対象の年度)
	 * @param returnYearMonth 各月の収支画面に戻る場合に表示する年月の値
	 * @return マイ家計簿 年間収支(明細)画面
	 *
	 */
	@PostMapping(value="/meisaitargetcontrol/", params = "targetNextBtn")
	public ModelAndView postNextAccountYearMeisai(
			@RequestParam("nextYear") String nextYear,
			@RequestParam("returnYearMonth") String returnYearMonth) {
		log.debug("postNextAccountYearMeisai:nextYear="+ nextYear + ",returnYearMonth:=" + returnYearMonth);
		
		// 画面表示データを読込
		return this.usecasee.readMeisai(loginUserSession.getLoginUserInfo(), nextYear, returnYearMonth)
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
}
