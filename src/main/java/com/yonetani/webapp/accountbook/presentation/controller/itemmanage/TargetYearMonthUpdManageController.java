/**
 * 情報管理(対象年月更新)画面を担当するコントローラーです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2025/01/13 : 1.01.00(A)  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller.itemmanage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yonetani.webapp.accountbook.application.usecase.itemmanage.TargetYearMonthUpdManageUseCase;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 情報管理(対象年月更新)画面を担当するコントローラーです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.01.A)
 *
 */
@Controller
@RequestMapping("/myhacbook/managebaseinfo/targetyearmonthupd/")
@Log4j2
@RequiredArgsConstructor
public class TargetYearMonthUpdManageController {
	// UseCase
	private final TargetYearMonthUpdManageUseCase usecase;
	// ログインユーザセッションBean
	private final LoginUserSession loginUserSession;
	
	/**
	 *<pre>
	 * 情報管理(対象年月更新)画面初期表示のGET要求時マッピングです。
	 * トップメニューからの遷移(初期表示)時のGETリクエストに対応します。
	 *</pre>
	 * @return 情情報管理(対象年月更新)初期表示画面
	 *
	 */
	@GetMapping("/initload/")
	public ModelAndView getInitLoad() {
		log.debug("getInitLoad:");
		
		// 画面表示データ読込
		return this.usecase.readInitInfo(loginUserSession.getLoginUserInfo())
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 対象年月更新のPOST要求マッピングです。
	 *</pre>
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 各月の収支参照画面(更新後の対象年月で各月の収支参照画面を表示)
	 *
	 */
	@PostMapping("/update/")
	public ModelAndView postUpdate(RedirectAttributes redirectAttributes) {
		log.debug("postUpdate:");
		// actionに従い、処理を実行
		return this.usecase.execAction(loginUserSession.getLoginUserInfo())
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// 各月の収支参照画面にリダイレクト
				.buildRedirect(redirectAttributes);
	}
}
