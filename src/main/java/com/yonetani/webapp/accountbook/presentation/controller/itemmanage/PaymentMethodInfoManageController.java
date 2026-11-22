/**
 * 支払方法情報管理画面を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・情報管理(支払方法)表示：トップメニューからの遷移(初期表示)、支払方法情報登録・更新成功時→リダイレクト(GET)
 * ・指定(リストから対象の支払方法を選択)の支払方法情報を表示(GET)
 * ・支払方法情報登録・更新(POST)
 * ・支払方法情報の有効/無効切替(POST)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 * 2026/10/18 : 1.00.01  feature-1.03-dev1   紙芝居準拠の有効/無効切替対応(toggleEnableエンドポイント追加)
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller.itemmanage;

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

import com.yonetani.webapp.accountbook.application.usecase.itemmanage.paymentmethod.PaymentMethodInfoManageUseCase;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.PaymentMethodInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.CompleteRedirectMessages;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 支払方法情報管理画面を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・情報管理(支払方法)表示：トップメニューからの遷移(初期表示)、支払方法情報登録・更新成功時→リダイレクト(GET)
 * ・指定(リストから対象の支払方法を選択)の支払方法情報を表示(GET)
 * ・支払方法情報登録・更新(POST)
 * ・支払方法情報の有効/無効切替(POST)
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@Controller
@RequestMapping("/myhacbook/managebaseinfo/paymentmethodinfo/")
@Log4j2
@RequiredArgsConstructor
public class PaymentMethodInfoManageController {
	// usecase
	private final PaymentMethodInfoManageUseCase usecase;
	// ログインユーザセッションBean
	private final LoginUserSession loginUserSession;

	/**
	 *<pre>
	 * 情報管理(支払方法)画面表示のGET要求時マッピングです。
	 * トップメニューからの遷移(初期表示)時のGETリクエストに対応します。
	 *</pre>
	 * @return 情報管理(支払方法)画面
	 *
	 */
	@GetMapping("/initload/")
	public ModelAndView getInitLoad() {
		log.debug("getInitLoad:");
		// 画面表示情報を取得
		return this.usecase.readPaymentMethodInfo(loginUserSession.getLoginUserInfo())
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}

	/**
	 *<pre>
	 * 情報管理(支払方法)画面で、更新対象の支払方法選択のGET要求時マッピングです。
	 *</pre>
	 * @param paymentMethodCode 表示対象の支払方法コード
	 * @return 情報管理(支払方法)画面
	 *
	 */
	@GetMapping("/updateload")
	public ModelAndView getTargetLoad(@RequestParam("paymentMethodCode") String paymentMethodCode) {
		log.debug("getTargetLoad: paymentMethodCode=" + paymentMethodCode);

		// 画面表示情報を取得
		return this.usecase.readPaymentMethodInfo(loginUserSession.getLoginUserInfo(), paymentMethodCode)
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}

	/**
	 *<pre>
	 * 支払方法情報登録・更新のPOST要求時マッピングです。
	 *</pre>
	 * @param paymentMethodForm 入力フォーム情報
	 * @param bindingResult フォームのバリデーションチェック結果
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 情報管理(支払方法)画面
	 *
	 */
	@PostMapping("/update/")
	public ModelAndView postUpdate(@ModelAttribute @Validated PaymentMethodInfoForm paymentMethodForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		log.debug("postUpdate: input=" + paymentMethodForm);
		/* 入力フィールドのバリデーションチェック結果を判定 */
		// チェック結果エラーの場合
		if(bindingResult.hasErrors()) {
			// 画面表示情報を取得し、入力チェックエラーを設定
			return this.usecase.readUpdateBindingErrorSetInfo(loginUserSession.getLoginUserInfo(), paymentMethodForm)
					// レスポンスにログインユーザ名を設定
					.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
					// レスポンスからModelAndViewを生成
					.build();

		} else {
			// チェック結果OKの場合 → 登録・更新処理を実行
			return this.usecase.execAction(loginUserSession.getLoginUserInfo(), paymentMethodForm).buildRedirect(redirectAttributes);
		}
	}

	/**
	 *<pre>
	 * 支払方法情報の有効/無効切替操作時のPOST要求時マッピングです。
	 *</pre>
	 * @param paymentMethodCode 対象の支払方法コード
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 情報管理(支払方法)画面
	 *
	 */
	@PostMapping("/toggleEnable/")
	public ModelAndView postToggleEnable(@RequestParam("paymentMethodCode") String paymentMethodCode,
			RedirectAttributes redirectAttributes) {
		log.debug("postToggleEnable: paymentMethodCode=" + paymentMethodCode);
		// 有効/無効フラグの切替処理を実行
		return this.usecase.execToggleEnableFlg(loginUserSession.getLoginUserInfo(), paymentMethodCode).buildRedirect(redirectAttributes);
	}

	/**
	 *<pre>
	 * 支払方法情報登録・更新完了後のリダイレクト(Get要求時)のマッピングです。
	 *</pre>
	 * @param redirectMessages リダイレクト元から引き継いだメッセージ
	 * @return 情報管理(支払方法)画面
	 *
	 */
	@GetMapping("/updateComplete/")
	public ModelAndView updateComplete(@ModelAttribute CompleteRedirectMessages redirectMessages) {
		log.debug("updateComplete: input=" + redirectMessages);
		// 画面表示情報を取得
		return this.usecase.readPaymentMethodInfo(loginUserSession.getLoginUserInfo())
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成(リダイレクト元から引き継いだメッセージを設定)
				.buildComplete(redirectMessages);
	}
}
