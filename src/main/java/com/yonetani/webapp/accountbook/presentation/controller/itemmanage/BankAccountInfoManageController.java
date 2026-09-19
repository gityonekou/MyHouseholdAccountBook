/**
 * 銀行口座情報管理画面を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・情報管理(銀行口座)表示：トップメニューからの遷移(初期表示)、銀行口座情報登録・更新成功時→リダイレクト(GET)
 * ・指定(リストから対象の銀行口座を選択)の銀行口座情報を表示(GET)
 * ・銀行口座情報登録・更新(POST)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/18 : 1.00.00  feature-1.03-dev1   新規作成
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

import com.yonetani.webapp.accountbook.application.usecase.itemmanage.bankaccount.BankAccountInfoManageUseCase;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.BankAccountInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.CompleteRedirectMessages;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 銀行口座情報管理画面を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・情報管理(銀行口座)表示：トップメニューからの遷移(初期表示)、銀行口座情報登録・更新成功時→リダイレクト(GET)
 * ・指定(リストから対象の銀行口座を選択)の銀行口座情報を表示(GET)
 * ・銀行口座情報登録・更新(POST)
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@Controller
@RequestMapping("/myhacbook/managebaseinfo/bankaccountinfo/")
@Log4j2
@RequiredArgsConstructor
public class BankAccountInfoManageController {
	// usecase
	private final BankAccountInfoManageUseCase usecase;
	// ログインユーザセッションBean
	private final LoginUserSession loginUserSession;

	/**
	 *<pre>
	 * 情報管理(銀行口座)画面表示のGET要求時マッピングです。
	 * トップメニューからの遷移(初期表示)時のGETリクエストに対応します。
	 *</pre>
	 * @return 情報管理(銀行口座)画面
	 *
	 */
	@GetMapping("/initload/")
	public ModelAndView getInitLoad() {
		log.debug("getInitLoad:");
		return this.usecase.readBankAccountInfo(loginUserSession.getLoginUserInfo())
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				.build();
	}

	/**
	 *<pre>
	 * 情報管理(銀行口座)画面で、更新対象の銀行口座選択のGET要求時マッピングです。
	 *</pre>
	 * @param bankAccountCode 表示対象の銀行口座コード
	 * @return 情報管理(銀行口座)画面
	 *
	 */
	@GetMapping("/updateload")
	public ModelAndView getTargetLoad(@RequestParam("bankAccountCode") String bankAccountCode) {
		log.debug("getTargetLoad: bankAccountCode=" + bankAccountCode);

		return this.usecase.readBankAccountInfo(loginUserSession.getLoginUserInfo(), bankAccountCode)
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				.build();
	}

	/**
	 *<pre>
	 * 銀行口座情報登録・更新のPOST要求時マッピングです。
	 *</pre>
	 * @param bankAccountForm 入力フォーム情報
	 * @param bindingResult フォームのバリデーションチェック結果
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 情報管理(銀行口座)画面
	 *
	 */
	@PostMapping("/update/")
	public ModelAndView postUpdate(@ModelAttribute @Validated BankAccountInfoForm bankAccountForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		log.debug("postUpdate: input=" + bankAccountForm);
		if(bindingResult.hasErrors()) {
			return this.usecase.readUpdateBindingErrorSetInfo(loginUserSession.getLoginUserInfo(), bankAccountForm)
					.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
					.build();

		} else {
			return this.usecase.execAction(loginUserSession.getLoginUserInfo(), bankAccountForm).buildRedirect(redirectAttributes);
		}
	}

	/**
	 *<pre>
	 * 銀行口座情報登録・更新完了後のリダイレクト(Get要求時)のマッピングです。
	 *</pre>
	 * @param redirectMessages リダイレクト元から引き継いだメッセージ
	 * @return 情報管理(銀行口座)画面
	 *
	 */
	@GetMapping("/updateComplete/")
	public ModelAndView updateComplete(@ModelAttribute CompleteRedirectMessages redirectMessages) {
		log.debug("updateComplete: input=" + redirectMessages);
		return this.usecase.readBankAccountInfo(loginUserSession.getLoginUserInfo())
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				.buildComplete(redirectMessages);
	}
}
