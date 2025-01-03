/**
 * 買い物登録方法選択画面(メニュー選択画面)表示を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・買い物登録方法選択画面(メニュー選択画面)表示：トップメニューからの遷移時(GET)
 * ・買い物登録方法選択画面(メニュー選択画面)表示：各月の収支画面からの遷移時(リダイレクト:GET)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/08/27 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller.account.regist;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yonetani.webapp.accountbook.application.usecase.account.regist.ShoppingRegistTopMenuUseCase;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 買い物登録方法選択画面(メニュー選択画面)表示を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・買い物登録方法選択画面(メニュー選択画面)表示：トップメニューからの遷移時(GET)
 * ・買い物登録方法選択画面(メニュー選択画面)表示：各月の収支画面からの遷移時(リダイレクト:GET)
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Controller
@Log4j2
@RequestMapping("/myhacbook/accountregist/shoppingtopmenu/")
@RequiredArgsConstructor
public class ShoppingRegistTopMenuController {
	
	// UseCase
	private final ShoppingRegistTopMenuUseCase usecase;
	// ユーザーセッション
	private final LoginUserSession loginUserSession;
	
	/**
	 *<pre>
	 * 買い物登録方法選択画面表示のGET要求時マッピングです。
	 * 各照会画面からのリダイレクト遷移時のリクエストを処理します。
	 *</pre>
	 * @pararm targetYearMonth 表示対象の年月
	 * @return 買い物登録方法選択画面情報
	 *
	 */
	@GetMapping
	public ModelAndView getSelectRegistLoad(@RequestParam("targetYearMonth") String targetYearMonth) {
		log.debug("getSelectRegistLoad:targetYearMonth=" + targetYearMonth);
		// 画面表示データ読込
		return usecase.read(loginUserSession.getLoginUserInfo(), targetYearMonth)
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 買い物登録方法選択画面表示のGET要求時マッピングです。
	 * トップメニューからの遷移時のリクエストを処理します。
	 *</pre>
	 * @return 買い物登録方法選択画面情報
	 *
	 */
	@GetMapping("/frowtop/")
	public ModelAndView getSelectRegistLoadFromTop() {
		log.debug("getSelectRegistLoadFromTop:");
		// 画面表示データ読込
		return usecase.read(loginUserSession.getLoginUserInfo())
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 買い物登録ボタン押下時のPOST要求時マッピングです。買い物登録画面にリダイレクトします。
	 *</pre>
	 * @param targetYearMonth 買い物登録を行う対象年月
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 買い物登録画面へリダイレクト
	 *
	 */
	@PostMapping(value = "/dispatchaction/", params = "Shopping")
	public ModelAndView getShoppingRegistRedirectLoad(
			@RequestParam("targetYearMonth") String targetYearMonth, RedirectAttributes redirectAttributes) {
		log.debug("getShoppingRegistRedirectLoad:targetYearMonth=" + targetYearMonth);
		
		// 画面表示情報を取得
		return this.usecase.readShoppingRegistRedirectInfo(loginUserSession.getLoginUserInfo(), targetYearMonth)
			// レスポンスにログインユーザ名を設定
			.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
			// 買い物登録画面へリダイレクト
			.buildRedirect(redirectAttributes);
	}
	
	/**
	 *<pre>
	 * 簡易タイプ登録ボタン押下時のPOST要求時マッピングです。買い物登録(簡易タイプ)画面にリダイレクトします。
	 *</pre>
	 * @param targetYearMonth 買い物登録を行う対象年月
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 買い物登録(簡易タイプ)画面へリダイレクト
	 *
	 */
	@PostMapping(value = "/dispatchaction/", params = "SimpleType")
	public ModelAndView getSimpleShoppingRegistRedirectLoad(
			@RequestParam("targetYearMonth") String targetYearMonth, RedirectAttributes redirectAttributes) {
		log.debug("getSimpleShoppingRegistRedirectLoad:targetYearMonth=" + targetYearMonth);
		
		// 画面表示情報を取得
		return this.usecase.readSimpleShoppingRegistRedirectInfo(loginUserSession.getLoginUserInfo(), targetYearMonth)
			// レスポンスにログインユーザ名を設定
			.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
			// 買い物登録画面へリダイレクト
			.buildRedirect(redirectAttributes);
	}
	
}
