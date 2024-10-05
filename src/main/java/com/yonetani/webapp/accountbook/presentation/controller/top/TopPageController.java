/**
 * マイ家計簿のログイン後のトップ画面表示を担当するコントローラーです。
 * マイ家計簿トップ画面の以下画面遷移を担当します。
 * ・マイ家計簿メニュー表示
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/06/04 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller.top;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.common.component.AccountBookUserInquiryUseCase;
import com.yonetani.webapp.accountbook.domain.model.common.AccountBookUser;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.response.top.TopPageResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * マイ家計簿のログイン後のトップ画面表示を担当するコントローラーです。
 * マイ家計簿トップ画面の以下画面遷移を担当します。
 * ・マイ家計簿メニュー表示
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
@RequestMapping("/myhacbook/topmenu/")
public class TopPageController {
	
	// ユーザ情報照会ユースケース
	private final AccountBookUserInquiryUseCase userInquiry;
	// ログインユーザセッションBean
	private final LoginUserSession loginUserSession;
	
	/**
	 *<pre>
	 * トップページメニュー表示のGet要求時のマッピングです。
	 *</pre>
	 * @return トップページ画面情報
	 *
	 */
	@GetMapping
	public ModelAndView getTopMenu() {
		log.info("getTopMenu:");
		
		// ログインユーザIDをホルダーから取得
		String loginUserId = SecurityContextHolder.getContext().getAuthentication().getName();
		
		// ログインユーザのユーザ情報を取得
		AccountBookUser loginUserInfo = userInquiry.getUserInfo(UserId.from(loginUserId));
		
		// ユーザ情報をセッションに設定
		loginUserSession.setLoginUserInfo(LoginUserInfo.from(
				// ログインユーザID
				loginUserInfo.getUserId().toString(),
				// ログインユーザ名
				loginUserInfo.getUserName().toString()));
		log.info("session:" + loginUserSession);
		
		// レスポンスを生成
		TopPageResponse response = new TopPageResponse();
		// ログインユーザ名を画面情報に設定しModelAndViewを返却
		return response.setLoginUserName(loginUserInfo.getUserName().toString()).build();
	}
}
