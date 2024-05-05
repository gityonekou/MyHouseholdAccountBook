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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.application.usecase.account.inquiry.AccountYearInquiryUseCase;
import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.YearInquiryForm;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountYearMageInquiryResponse;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountYearMeisaiInquiryResponse;
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
	 * 現在の決算年の年間収支(マージ)画面表示のPOST要求時マッピングです。
	 * 以下画面リクエスト時の処理を行います。
	 * ・初期表示
	 * ・前年度
	 * ・次年度
	 *</pre>
	 * @param target 表示対象の年度
	 * @param bindingResult バリデーション結果
	 * @return マイ家計簿 年間収支(マージ)画面
	 *
	 */
	@PostMapping("/mage/")
	public ModelAndView postAccountYearMage(@ModelAttribute @Validated YearInquiryForm target, BindingResult bindingResult) {
		log.debug("postAccountYearMage:target="+ target);
		if(bindingResult.hasErrors()) {
			return AccountYearMageInquiryResponse.buildBindingError(loginUserSession.getLoginUserInfo(), target);
		} else {
			// 画面表示データを読込
			return this.usecasee.readMage(loginUserSession.getLoginUserInfo(), target)
					// レスポンスにログインユーザ名を設定
					.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
					// レスポンスからModelAndViewを生成
					.build();
		}
	}
	
	/**
	 *<pre>
	 * 現在の決算年の年間収支(明細)画面表示のPOST要求時マッピングです。
	 * 以下画面リクエスト時の処理を行います。
	 * ・初期表示
	 * ・前年度
	 * ・次年度
	 *</pre>
	 * @param target 表示対象の年度
	 * @param bindingResult バリデーション結果
	 * @return マイ家計簿 年間収支(明細)画面
	 *
	 */
	@PostMapping("/meisai/")
	public ModelAndView postAccountYearMeisai(@ModelAttribute @Validated YearInquiryForm target, BindingResult bindingResult) {
		log.debug("postAccountYearMeisai:target="+ target);
		if(bindingResult.hasErrors()) {
			return AccountYearMeisaiInquiryResponse.buildBindingError(loginUserSession.getLoginUserInfo(), target);
		} else {
			// 画面表示データを読込
			return this.usecasee.readMeisai(loginUserSession.getLoginUserInfo(), target)
					// レスポンスにログインユーザ名を設定
					.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
					// レスポンスからModelAndViewを生成
					.build();
		}
	}
}
