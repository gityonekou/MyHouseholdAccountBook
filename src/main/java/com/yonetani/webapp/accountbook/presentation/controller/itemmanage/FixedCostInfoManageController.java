/**
 * 固定費情報管理画面を担当するコントローラーです。
 * 固定費情報管理画面は以下3つの画面で構成されていますが、そのすべての画面遷移をこのコントローラーで
 * 管理します。
 * ・情報管理(固定費)初期表示画面:
 * ・情報管理(固定費)処理選択画面
 * ・情報管理(固定費)更新画面(追加・更新)
 * 
 * 画面遷移
 * ・トップメニューからの遷移(初期表示)(GET)→情報管理(固定費)初期表示画面
 * ・初期表示画面で固定費一覧から対象の明細を選択(GET)→情報管理(固定費)処理選択画面
 * ・初期表示画面で追加対象の支出項目選択時(GET)
 *   →選択した支出項目に対応する固定費が未登録の場合：情報管理(固定費)更新画面(追加)
 *   →選択した支出項目の固定費が既に登録済みの場合：初期表示画面でメッセージ確認(OK/NGで対応する画面に遷移)
 *   　→OK:情報管理(固定費)更新画面(追加)
 *   　→NG:初期表示画面
 * ・情報管理(固定費)処理選択画面で更新ボタン押下(POST)→情報管理(固定費)更新画面(更新)
 * ・情報管理(固定費)処理選択画面でキャンセルボタン押下(POST)→初期表示画面
 * ・情報管理(固定費)処理選択画面で削除ボタン押下(POST)→OK/キャンセル選択後、対応する処理を実行
 * ・情報管理(固定費)更新画面(追加・更新)で登録ボタン押下時(POST)
 * 　→登録成功：初期表示画面
 * 　→登録失敗：情報管理(固定費)更新画面(追加・更新)
 * ・固定費情報登録・更新成功時→リダイレクト(GET)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/13 : 1.00.00  新規作成
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

import com.yonetani.webapp.accountbook.application.usecase.itemmanage.FixedCostInfoManageUseCase;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.FixedCostInfoUpdateForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.CompleteRedirectMessages;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 固定費情報管理画面を担当するコントローラーです。
 * 固定費情報管理画面は以下3つの画面で構成されていますが、そのすべての画面遷移をこのコントローラーで
 * 管理します。
 * ・情報管理(固定費)初期表示画面:
 * ・情報管理(固定費)処理選択画面
 * ・情報管理(固定費)更新画面(追加・更新)
 * 
 * 画面遷移
 * ・トップメニューからの遷移(初期表示)(GET)→情報管理(固定費)初期表示画面
 * ・初期表示画面で固定費一覧から対象の明細を選択(GET)→情報管理(固定費)処理選択画面
 * ・初期表示画面で追加対象の支出項目選択時(GET)
 *   →選択した支出項目に対応する固定費が未登録の場合：情報管理(固定費)更新画面(追加)
 *   →選択した支出項目の固定費が既に登録済みの場合：初期表示画面でメッセージ確認(OK/NGで対応する画面に遷移)
 *   　→OK:情報管理(固定費)更新画面(追加)　POSTで要求
 *   　→NG:初期表示画面
 * ・情報管理(固定費)処理選択画面で更新ボタン押下(POST)→情報管理(固定費)更新画面(更新)
 * ・情報管理(固定費)処理選択画面でキャンセルボタン押下(POST)→初期表示画面
 * ・情報管理(固定費)処理選択画面で削除ボタン押下(POST)→OK/キャンセル選択後、対応する処理を実行
 * ・情報管理(固定費)更新画面(追加・更新)で登録ボタン押下時(POST)
 * 　→登録成功：初期表示画面
 * 　→登録失敗：情報管理(固定費)更新画面(追加・更新)
 * ・固定費情報登録・更新成功時→リダイレクト(GET)
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Controller
@RequestMapping("/myhacbook/managebaseinfo/fixedcostinfo/")
@Log4j2
@RequiredArgsConstructor
public class FixedCostInfoManageController {
	// UseCase
	private final FixedCostInfoManageUseCase usecase;
	// ログインユーザセッションBean
	private final LoginUserSession loginUserSession;
	
	/**
	 *<pre>
	 * 情報管理(固定費)初期表示のGET要求時マッピングです。
	 * トップメニューからの遷移(初期表示)時のGETリクエストに対応します。
	 *</pre>
	 * @return 情情報管理(固定費)初期表示画面
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
	 * 固定費一覧から任意の明細を選択時のGET要求マッピングです。
	 * 情報管理(固定費)処理選択画面に遷移します。
	 *</pre>
	 * @param fixedCostCode 表示対象の固定費コード
	 * @return 情報管理(固定費)処理選択画面
	 *
	 */
	@GetMapping("/select")
	public ModelAndView getActSelect(@RequestParam("fixedCostCode") String fixedCostCode) {
		log.debug("getActSelect: fixedCostCode=" + fixedCostCode);
		
		// 画面表示情報を取得
		return this.usecase.readActSelectItemInfo(loginUserSession.getLoginUserInfo(), fixedCostCode)
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 対象の支出項目を選択して固定費を追加時のGET要求マッピングです。
	 * 情報管理(固定費)更新画面に遷移します。
	 * 選択した支出項目に属する固定費が登録済みの場合、初期表示画面に該当メッセージを表示しOK・NGを選択します。
	 *</pre>
	 * @param sisyutuItemCode 判定対象の支出項目コード
	 * @return 支出項目にすでに固定費が未登録の場合は情報管理(固定費)更新画面、支出項目にすでに固定費が登録済みの場合は初期表示画面(メッセージ表示)
	 *
	 */
	@GetMapping("/addload")
	public ModelAndView getAddLoad(@RequestParam("sisyutuItemCode") String sisyutuItemCode) {
		log.debug("getAddLoad: sisyutuItemCode=" + sisyutuItemCode);
		
		// 固定費が登録済みかどうかを判定
		if(this.usecase.hasFixedCostInfoBySisyutuItem(loginUserSession.getLoginUserInfo(), sisyutuItemCode)) {
			// 固定費が登録済みの場合、初期画面に遷移
			return this.usecase.readRegisteredFixedCostInfoBySisyutuItem(loginUserSession.getLoginUserInfo(), sisyutuItemCode)
			// レスポンスにログインユーザ名を設定
			.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
			// レスポンスからModelAndViewを生成
			.build();
			
		} else {
			// 固定費が未登録の場合、情報管理(固定費)更新画面に遷移
			return this.usecase.readAddFixedCostInfoBySisyutuItem(loginUserSession.getLoginUserInfo(), sisyutuItemCode)
					// レスポンスにログインユーザ名を設定
					.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
					// レスポンスからModelAndViewを生成
					.build();
		}
	}
	
	/**
	 *<pre>
	 * 選択した支出項目に属する固定費を追加登録時のPOST要求マッピングです。
	 * 選択した支出項目に属する固定費が既に登録済みの場合で、新たに同分類の支出項目に属する固定費を登録する場合に
	 * このアクションが呼ばれます。
	 * 情報管理(固定費)更新画面に遷移します。
	 *</pre>
	 * @param sisyutuItemCode 追加する固定費が所属する支出項目の支出項目コード
	 * @return 情報管理(固定費)更新画面
	 *
	 */
	@PostMapping(value = "/updateload/", params = "actionAdd")
	public ModelAndView postActionAddLoad(@RequestParam("sisyutuItemCode") String sisyutuItemCode) {
		log.debug("postActionAddLoad: sisyutuItemCode=" + sisyutuItemCode);
		// 画面表示情報を取得
		return this.usecase.readAddFixedCostInfoBySisyutuItem(loginUserSession.getLoginUserInfo(), sisyutuItemCode)
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 選択した固定費に対して、更新操作を選択時のPOST要求マッピングです。
	 * 情報管理(固定費)更新画面に遷移します。
	 *</pre>
	 * @param fixedCostCode 更新対象の固定費コード
	 * @return 情報管理(固定費)更新画面
	 *
	 */
	@PostMapping(value="/updateload/", params = "actionUpdate")
	public ModelAndView postActionUpdateLoad(@RequestParam("fixedCostCode") String fixedCostCode) {
		log.debug("postActionUpdateLoad: fixedCostCode=" + fixedCostCode);
		// 画面表示情報を取得
		return this.usecase.readUpdateFixedCostInfo(loginUserSession.getLoginUserInfo(), fixedCostCode)
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 選択した固定費に対して、削除操作を選択時のPOST要求マッピングです。
	 * 削除が完了した場合、情報管理(固定費)初期表示画面に遷移します。
	 *</pre>
	 * @param fixedCostCode 削除対象の固定費コード
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 情報管理(固定費)初期表示画面
	 *
	 */
	@PostMapping(value="/delete/")
	public ModelAndView postDelete(@RequestParam("fixedCostCode") String fixedCostCode, RedirectAttributes redirectAttributes) {
		log.debug("postDelete: fixedCostCode=" + fixedCostCode);
		// 画面表示情報を取得
		return this.usecase.execDelete(loginUserSession.getLoginUserInfo(), fixedCostCode).buildRedirect(redirectAttributes);
	}
	
	/**
	 *<pre>
	 * 選択した固定費に対して、キャンセルアクションを選択時のPOST要求マッピングです。
	 * 情報管理(固定費)初期表示画面に遷移します。
	 *</pre>
	 * @return 情報管理(固定費)初期表示画面
	 *
	 */
	@PostMapping(value = "/updateload/", params = "actionCancel")
	public ModelAndView postActionCancel() {
		log.debug("postActionCancel:");
		// 画面表示データ読込
		return this.usecase.readInitInfo(loginUserSession.getLoginUserInfo())
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 固定費情報登録・更新のPOST要求時マッピングです。
	 *</pre>
	 * @param inputForm 入力フォーム情報
	 * @param bindingResult フォームのバリデーションチェック結果
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 登録成功時：リダイレクト、登録失敗時:情報管理(固定費)更新画面
	 *
	 */
	@PostMapping("/update/")
	public ModelAndView postUpdate(@ModelAttribute @Validated FixedCostInfoUpdateForm inputForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		log.debug("postUpdate: input=" + inputForm);
		/* 入力フィールドのバリデーションチェック結果を判定 */
		// チェック結果エラーの場合
		if(bindingResult.hasErrors()) {
			// 初期表示情報を取得し、入力チェックエラーを設定
			return this.usecase.readUpdateBindingErrorSetInfo(loginUserSession.getLoginUserInfo(), inputForm)
					// レスポンスにログインユーザ名を設定
					.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
					// レスポンスからModelAndViewを生成
					.build();
			
		// チェック結果OKの場合
		} else {
			// actionに従い、処理を実行
			return this.usecase.execAction(loginUserSession.getLoginUserInfo(), inputForm).buildRedirect(redirectAttributes);
		}
	}
	
	/**
	 *<pre>
	 * 固定費情報登録・更新完了後のリダイレクト(Get要求時)のマッピングです。
	 *</pre>
	 * @param redirectMessages リダイレクト元から引き継いだメッセージ
	 * @return 情報管理(固定費)初期表示画面
	 *
	 */
	@GetMapping("/updateComplete/")
	public ModelAndView updateComplete(@ModelAttribute CompleteRedirectMessages redirectMessages) {
		log.debug("updateComplete: input=" + redirectMessages);
		// 画面表示情報を取得
		return this.usecase.readInitInfo(loginUserSession.getLoginUserInfo())
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.buildComplete(redirectMessages);
	}
}
