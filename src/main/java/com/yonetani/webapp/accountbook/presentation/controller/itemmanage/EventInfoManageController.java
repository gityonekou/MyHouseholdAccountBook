/**
 * イベント情報管理画面を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・情報管理(イベント)表示：トップメニューからの遷移(初期表示)、イベント情報登録・更新成功時→リダイレクト(GET)
 * ・新規イベントの対象支出項目選択時(GET)
 * ・イベント情報登録・更新・イベント終了(POST)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/16 : 1.00.00  新規作成
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

import com.yonetani.webapp.accountbook.application.usecase.itemmanage.EventInfoManageUseCase;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.EventInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.CompleteRedirectMessages;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * イベント情報管理画面を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・情報管理(イベント)表示：トップメニューからの遷移(初期表示)、イベント情報登録・更新成功時→リダイレクト(GET)
 * ・新規イベントの対象支出項目選択時(GET)
 * ・イベント情報登録・更新・イベント終了(POST)
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Controller
@RequestMapping("/myhacbook/managebaseinfo/eventinfo/")
@Log4j2
@RequiredArgsConstructor
public class EventInfoManageController {
	// usecase
	private final EventInfoManageUseCase usecase;
	// ログインユーザセッションBean
	private final LoginUserSession loginUserSession;
	
	/**
	 *<pre>
	 * 情報管理(イベント)画面表示のGET要求時マッピングです。
	 * トップメニューからの遷移(初期表示)時のGETリクエストに対応します。
	 *</pre>
	 * @return 情報管理(イベント)画面
	 *
	 */
	@GetMapping("/initload/")
	public ModelAndView getInitLoad() {
		log.debug("getInitLoad:");
		// 画面表示情報を取得
		return this.usecase.readEventInfo(loginUserSession.getLoginUserInfo())
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 対象の支出項目(イベント)を選択してイベントを追加時のGET要求マッピングです。
	 * 情報管理(イベント)更新画面に遷移します。
	 *</pre>
	 * @param sisyutuItemCode 追加するイベントが所属する支出項目の支出項目コード
	 * @return 情報管理(イベント)更新画面
	 *
	 */
	@GetMapping("/addload")
	public ModelAndView getAddLoad(@RequestParam("sisyutuItemCode") String sisyutuItemCode) {
		log.debug("getAddLoad: sisyutuItemCode=" + sisyutuItemCode);
		// 画面表示情報を取得
		return this.usecase.readAddEventInfo(loginUserSession.getLoginUserInfo(), sisyutuItemCode)
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	/**
	 *<pre>
	 * 情報管理(イベント)画面で、更新対象のイベント選択時のGET要求マッピングです。
	 *</pre>
	 * @param eventCode 更新対象のイベントコード
	 * @return 情報管理(イベント)画面
	 *
	 */
	@GetMapping("/updateload")
	public ModelAndView getUpdateLoad(@RequestParam("eventCode") String eventCode) {
		log.debug("getUpdateLoad: eventCode=" + eventCode);
		
		// 画面表示情報を取得
		return this.usecase.readUpdateEventInfo(loginUserSession.getLoginUserInfo(), eventCode)
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * イベント情報登録・更新時のPOST要求マッピングです。
	 *</pre>
	 * @param eventForm 入力フォーム情報
	 * @param bindingResult フォームのバリデーションチェック結果
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 情報管理(イベント)画面
	 *
	 */
	@PostMapping("/update/")
	public ModelAndView postUpdate(@ModelAttribute @Validated EventInfoForm eventForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		log.debug("postUpdate: input=" + eventForm);
		/* 入力フィールドのバリデーションチェック結果を判定 */
		// チェック結果エラーの場合
		if(bindingResult.hasErrors()) {
			// 初期表示情報を取得し、入力チェックエラーを設定
			return this.usecase.readBindingErrorSetInfo(loginUserSession.getLoginUserInfo(), eventForm)
					// レスポンスにログインユーザ名を設定(AbstractResponseの同メソッドをオーバーライド済み)
					.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
					// レスポンスからModelAndViewを生成
					.build();
			
		// チェック結果OKの場合
		} else {
			// actionに従い、処理を実行
			return this.usecase.execAction(loginUserSession.getLoginUserInfo(), eventForm).buildRedirect(redirectAttributes);
		}
	}
	
	/**
	 *<pre>
	 * イベント終了操作時のPOST要求マッピングです。
	 *</pre>
	 * @param eventForm 入力フォーム情報
	 * @param bindingResult フォームのバリデーションチェック結果
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 情報管理(イベント)画面
	 *
	 */
	@PostMapping("/delete/")
	public ModelAndView postDelete(@RequestParam("eventCode") String eventCode, RedirectAttributes redirectAttributes) {
		log.debug("postDelete: eventCode=" + eventCode);
		// actionに従い、処理を実行
		return this.usecase.execDelete(loginUserSession.getLoginUserInfo(), eventCode).buildRedirect(redirectAttributes);
	}
	
	/**
	 *<pre>
	 * イベント情報登録・更新・イベント終了操作完了後のリダイレクト(Get要求時)のマッピングです。
	 *</pre>
	 * @param redirectMessages リダイレクト元から引き継いだメッセージ
	 * @return 情報管理(イベント)画面
	 *
	 */
	@GetMapping("/updateComplete/")
	public ModelAndView updateComplete(@ModelAttribute CompleteRedirectMessages redirectMessages) {
		log.debug("updateComplete: input=" + redirectMessages);
		// 画面表示情報を取得
		return this.usecase.readEventInfo(loginUserSession.getLoginUserInfo())
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.buildComplete(redirectMessages);
	}
}
