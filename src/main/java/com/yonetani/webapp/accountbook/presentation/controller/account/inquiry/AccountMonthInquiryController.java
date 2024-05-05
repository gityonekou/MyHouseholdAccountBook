/**
 * マイ家計簿の各月の収支画面表示を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・現在の決算月の収支画面初期表示(GET)
 * ・指定月の収支画面表示(POST)
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.application.usecase.account.inquiry.AccountMonthInquiryUseCase;
import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.YearMonthInquiryForm;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountMonthInquiryResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * マイ家計簿の各月の収支画面表示を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・現在の決算月の収支画面初期表示(get)
 * ・指定月の収支画面表示(post)
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
@RequestMapping("/myhacbook/accountinquiry/accountmonth/")
public class AccountMonthInquiryController {

	// usecase
	private final AccountMonthInquiryUseCase usecase;
	// ログインユーザセッションBean
	private final LoginUserSession loginUserSession;
	
	/**
	 *<pre>
	 * 現在の決算月の収支画面初期表示のGET要求時マッピングです。
	 *</pre>
	 * @return マイ家計簿(各月の収支)画面
	 *
	 */
	@GetMapping
	public ModelAndView getInitAccountMonth() {
		log.debug("getInitAccountMonth:");
		// 画面表示データ読込
		return this.usecase.read(loginUserSession.getLoginUserInfo())
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 指定月の収支画面表示のPOST要求時マッピングです。
	 * 次月・前月のリンク、および直接年月を指定して表示する場合に対応します。
	 *</pre>
	 * @param target 表示対象の年月
	 * @param bindingResult バリデーション結果
	 * @return マイ家計簿(各月の収支)画面
	 *
	 */
	@PostMapping
	public ModelAndView postAccountMonth(@ModelAttribute @Validated YearMonthInquiryForm target, BindingResult bindingResult) {
		log.debug("postAccountMonth:target="+ target);
		
		if(bindingResult.hasErrors()) {
			return AccountMonthInquiryResponse.buildBindingError(loginUserSession.getLoginUserInfo(), target);
		} else {
			// 画面表示情報読込
			return this.usecase.read(loginUserSession.getLoginUserInfo(), target)
					// レスポンスにログインユーザ名を設定
					.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
					// レスポンスからModelAndViewを生成
					.build();
		}
	}
}
