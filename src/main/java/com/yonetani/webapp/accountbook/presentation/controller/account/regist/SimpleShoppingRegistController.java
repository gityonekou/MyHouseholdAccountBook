/**
 * 買い物登録(簡易タイプ)画面表示を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・買い物登録方法選択画面から簡易タイプを選択：リダイレクトされて買い物登録(簡易タイプ)画面へ:GET
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller.account.regist;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yonetani.webapp.accountbook.application.usecase.account.regist.SimpleShoppingRegistUseCase;
import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.SimpleShoppingRegistInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.CompleteRedirectMessages;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 買い物登録(簡易タイプ)画面表示を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・買い物登録方法選択画面から簡易タイプを選択：リダイレクトされて買い物登録(簡易タイプ)画面へ:GET
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Controller
@Log4j2
@RequestMapping("/myhacbook/accountregist/simpleshoppingregist/")
@RequiredArgsConstructor
public class SimpleShoppingRegistController {
	
	// UseCase
	private final SimpleShoppingRegistUseCase usecase;
	// ユーザーセッション
	private final LoginUserSession loginUserSession;
	
	/**
	 *<pre>
	 * 買い物登録(簡易タイプ)画面表示のGET要求時マッピングです。
	 * 買い物登録方法選択画面表示で簡易タイプボタン押下時のリダイレクト遷移リクエストを処理します。
	 *</pre>
	 * @pararm targetYearMonth 表示対象の年月
	 * @return 買い物登録(簡易タイプ)画面情報
	 *
	 */
	@GetMapping
	public ModelAndView getInitLoad(@RequestParam("targetYearMonth") String targetYearMonth) {
		log.debug("getInitLoad:targetYearMonth="+ targetYearMonth);
		// 画面表示データ読込
		return usecase.read(loginUserSession.getLoginUserInfo(), targetYearMonth)
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 買い物登録(簡易タイプ)画面で更新対象の買い物情報を選択時のGET要求時マッピングです。
	 *</pre>
	 *　@pararm targetYearMonth 表示対象の年月
	 * @pararm shoppingRegistCode 買い物登録コード
	 * @return 買い物登録(簡易タイプ)画面情報
	 *
	 */
	@GetMapping("/updateload")
	public ModelAndView getUpdateLoad(
			@RequestParam("targetYearMonth") String targetYearMonth,
			@RequestParam("shoppingRegistCode") String shoppingRegistCode) {
		log.debug("getUpdateLoad:targetYearMonth="+ targetYearMonth + ",shoppingRegistCode=" + shoppingRegistCode);
		// 画面表示データ読込
		return usecase.read(loginUserSession.getLoginUserInfo(), targetYearMonth, shoppingRegistCode)
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 店舗区分変更時のPOST要求時マッピングです。
	 *</pre>
	 * @param registInfoForm 入力フォーム情報
	 * @return 買い物登録(簡易タイプ)画面
	 *
	 */
	@PostMapping
	public ModelAndView changeShopKubun(@ModelAttribute SimpleShoppingRegistInfoForm registInfoForm) {
		log.debug("changeShopKubun: input=" + registInfoForm);
		// 画面表示データ読込
		return this.usecase.readChangeShopKubun(loginUserSession.getLoginUserInfo(), registInfoForm)
				// レスポンスにログインユーザ名を設定(AbstractResponseの同メソッドをオーバーライド済み)
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 買い物情報登録・更新のPOST要求時マッピングです。
	 *</pre>
	 * @param registInfoForm 入力フォーム情報
	 * @param bindingResult フォームのバリデーションチェック結果
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 買い物登録(簡易タイプ)画面
	 *
	 */
	@PostMapping("/update/")
	public ModelAndView postUpdate(
			@ModelAttribute @Validated SimpleShoppingRegistInfoForm registInfoForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		log.debug("postUpdate: input=" + registInfoForm);
		/* 入力フィールドのバリデーションチェック結果を判定 */
		// チェック結果エラーの場合
		if(bindingResult.hasErrors()) {
			// 初期表示情報を取得し、入力チェックエラーを設定
			return this.usecase.readBindingError(loginUserSession.getLoginUserInfo(), registInfoForm)
					// バリデーションチェック結果でデフォルト表示されないメッセージをメッセージ表示エリアに追加
					.addBindingErrorMessage(bindingResult)
					// レスポンスにログインユーザ名を設定(AbstractResponseの同メソッドをオーバーライド済み)
					.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
					// レスポンスからModelAndViewを生成
					.build();
			
		// チェック結果OKの場合
		} else {
			// actionに従い、処理を実行
			return this.usecase.execAction(loginUserSession.getLoginUserInfo(), registInfoForm).buildRedirect(redirectAttributes);
		}
	}
	
	/**
	 *<pre>
	 * 買い物情報登録・更新完了後のリダイレクト(Get要求時)のマッピングです。
	 *</pre>
	 * @param targetYearMonth 表示対象の年月
	 * @param redirectMessages リダイレクト元から引き継いだメッセージ
	 * @return 買い物登録(簡易タイプ)画面
	 *
	 */
	@GetMapping("/updateComplete/")
	public ModelAndView updateComplete(
			@RequestParam("targetYearMonth") String targetYearMonth,
			@ModelAttribute CompleteRedirectMessages redirectMessages) {
		log.debug("updateComplete: targetYearMonth=" + targetYearMonth + ",message=" + redirectMessages);
		// 画面表示情報を取得
		return usecase.read(loginUserSession.getLoginUserInfo(), targetYearMonth)
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.buildComplete(redirectMessages);
	}
	
	/**
	 *<pre>
	 * 買い物登録トップへボタン押下時のPOST要求時マッピングです。買い物登録方法選択画面(メニュー選択画面)にリダイレクトします。
	 *</pre>
	 * @param targetYearMonth 表示対象の対象年月
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 買い物登録方法選択画面(メニュー選択画面)へリダイレクト
	 *
	 */
	@PostMapping(value = "/returndispatchaction/", params = "ReturnShoppingTop")
	public ModelAndView getReturnShoppingTopRedirectLoad(
			@RequestParam("targetYearMonth") String targetYearMonth, RedirectAttributes redirectAttributes) {
		log.debug("getReturnShoppingTopRedirectLoad:targetYearMonth=" + targetYearMonth);
		
		// 画面表示情報を取得
		return this.usecase.readReturnShoppingTopRedirectInfo(loginUserSession.getLoginUserInfo(), targetYearMonth)
			// レスポンスにログインユーザ名を設定
			.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
			// 買い物登録画面へリダイレクト
			.buildRedirect(redirectAttributes);
	}
	
	/**
	 *<pre>
	 * 各月の家計簿参照に戻るボタン押下時のPOST要求時マッピングです。各月の収支参照画面にリダイレクトします。
	 *</pre>
	 * @param targetYearMonth 表示対象の対象年月
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 各月の収支参照画面へリダイレクト
	 *
	 */
	@PostMapping(value = "/returndispatchaction/", params = "ReturnMonth")
	public ModelAndView getReturnInquiryMonthRedirectLoad(
			@RequestParam("targetYearMonth") String targetYearMonth, RedirectAttributes redirectAttributes) {
		log.debug("getReturnInquiryMonthRedirectLoad:targetYearMonth=" + targetYearMonth);
		
		// 画面表示情報を取得
		return this.usecase.readReturnInquiryMonthRedirectInfo(loginUserSession.getLoginUserInfo(), targetYearMonth)
			// レスポンスにログインユーザ名を設定
			.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
			// 買い物登録画面へリダイレクト
			.buildRedirect(redirectAttributes);
	}
}
