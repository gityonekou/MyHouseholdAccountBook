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
 * 2026/03/20 : 1.01.00  リファクタリング対応(DDD適応)
 * 2026/06/13 : 1.02.00  支出別一覧追加対応(viewType対応・表示切替・訂正エンドポイント追加)
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller.account.inquiry;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yonetani.webapp.accountbook.application.usecase.account.inquiry.AccountMonthInquiryUseCase;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountMonthInquiryResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.CompleteRedirectMessages;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * マイ家計簿の各月の収支画面表示を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・トップメニューからの遷移(初期表示)(GET)→各月の収支初期表示画面
 * ・他タブからの遷移時(post)
 * ・前の月の収支表示時(post)
 * ・次の月の収支表示時(post)
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
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
	 * トップメニューからの遷移(初期表示)時に呼び出されます。
	 *</pre>
	 * @return マイ家計簿(各月の収支)画面 または マイ家計簿(各月の収支：収支登録確認)画面
	 *
	 */
	@GetMapping
	public ModelAndView getInitAccountMonth() {
		log.debug("getInitAccountMonth:");
		// 画面表示データ読込
		AccountMonthInquiryResponse response = this.usecase.read(loginUserSession.getLoginUserInfo());
		// レスポンスにログインユーザ名を設定
		response.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName());

		// 収支データの有無でビューを切り替え
		if (response.isSyuusiDataFlg()) {
			return response.buildWithData();
		} else {
			return response.buildRegistCheck();
		}
	}
	
	/**
	 *<pre>
	 * 指定月の収支画面表示のPOST要求時マッピングです。他のタブからの遷移時に呼び出されます。
	 *</pre>
	 * @param targetYearMonth 表示対象の年月
	 * @return マイ家計簿(各月の収支)画面 または マイ家計簿(各月の収支：収支登録確認)画面
	 *
	 */
	@PostMapping
	public ModelAndView postAccountMonth(@RequestParam("targetYearMonth") String targetYearMonth) {
		log.debug("postAccountMonth:targetYearMonth="+ targetYearMonth);

		// 画面表示情報読込
		AccountMonthInquiryResponse response = this.usecase.read(loginUserSession.getLoginUserInfo(), targetYearMonth);
		// レスポンスにログインユーザ名を設定
		response.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName());

		// 収支データの有無でビューを切り替え
		if (response.isSyuusiDataFlg()) {
			return response.buildWithData();
		} else {
			return response.buildRegistCheck();
		}
	}
	
	/**
	 *<pre>
	 * 前月の収支画面表示のPOST要求時マッピングです。
	 *</pre>
	 * @param beforeYearMonth 前月の年月(表示対象の年月の値)
	 * @param returnYearMonth 戻り時の年月(遷移元画面で表示した年月の値)
	 * @param viewType 表示種別（"item"=支出項目別、"expenditure"=支出別）
	 * @return マイ家計簿(各月の収支)画面 または マイ家計簿(各月の収支：収支登録確認)画面
	 *
	 */
	@PostMapping(value="/targetcontrol/", params = "targetBeforeBtn")
	public ModelAndView postBeforeAccountMonth(
			@RequestParam("beforeYearMonth") String beforeYearMonth,
			@RequestParam("returnYearMonth") String returnYearMonth,
			@RequestParam(value="viewType", defaultValue="item") String viewType) {
		log.debug("postBeforeAccountMonth:beforeYearMonth=" + beforeYearMonth
				+ ",returnYearMonth=" + returnYearMonth + ",viewType=" + viewType);

		// 画面表示情報読込
		AccountMonthInquiryResponse response = this.usecase.read(
				loginUserSession.getLoginUserInfo(), beforeYearMonth, returnYearMonth, viewType);
		// レスポンスにログインユーザ名を設定
		response.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName());

		// 収支データの有無でビューを切り替え
		if (response.isSyuusiDataFlg()) {
			return response.buildWithData();
		} else {
			return response.buildRegistCheck();
		}
	}

	/**
	 *<pre>
	 * 次月の収支画面表示のPOST要求時マッピングです。
	 *</pre>
	 * @param nextYearMonth 次月の年月(表示対象の年月の値)
	 * @param returnYearMonth 戻り時の年月(遷移元画面で表示した年月の値)
	 * @param viewType 表示種別（"item"=支出項目別、"expenditure"=支出別）
	 * @return マイ家計簿(各月の収支)画面 または マイ家計簿(各月の収支：収支登録確認)画面
	 *
	 */
	@PostMapping(value="/targetcontrol/", params = "targetNextBtn")
	public ModelAndView postNextAccountMonth(
			@RequestParam("nextYearMonth") String nextYearMonth,
			@RequestParam("returnYearMonth") String returnYearMonth,
			@RequestParam(value="viewType", defaultValue="item") String viewType) {
		log.debug("postNextAccountMonth:nextYearMonth=" + nextYearMonth
				+ ",returnYearMonth=" + returnYearMonth + ",viewType=" + viewType);

		// 画面表示情報読込
		AccountMonthInquiryResponse response = this.usecase.read(
				loginUserSession.getLoginUserInfo(), nextYearMonth, returnYearMonth, viewType);
		// レスポンスにログインユーザ名を設定
		response.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName());

		// 収支データの有無でビューを切り替え
		if (response.isSyuusiDataFlg()) {
			return response.buildWithData();
		} else {
			return response.buildRegistCheck();
		}
	}

	/**
	 *<pre>
	 * 支出一覧の表示切替ボタン押下時のPOST要求時マッピングです。
	 * 支出項目別 ↔ 支出別の表示を切り替えます。
	 *
	 * 【前提条件】表示切替は収支データが存在する月のみ（ボタンは syuusiDataFlg=true の場合のみ表示）
	 *</pre>
	 * @param targetYearMonth 表示対象の年月
	 * @param viewType 切り替え先の表示種別（"item"=支出項目別、"expenditure"=支出別）
	 * @return マイ家計簿(各月の収支)画面
	 *
	 */
	@PostMapping(value="/targetcontrol/", params = "viewTypeSwitch")
	public ModelAndView postViewTypeSwitch(
			@RequestParam("targetYearMonth") String targetYearMonth,
			@RequestParam("viewType") String viewType) {
		log.debug("postViewTypeSwitch:targetYearMonth=" + targetYearMonth + ",viewType=" + viewType);

		// 画面表示情報読込（表示切替は同月内の操作のため returnYearMonth=targetYearMonth）
		AccountMonthInquiryResponse response = this.usecase.read(
				loginUserSession.getLoginUserInfo(), targetYearMonth, targetYearMonth, viewType);
		// レスポンスにログインユーザ名を設定
		response.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName());
		return response.buildWithData();
	}
	
	/**
	 *<pre>
	 * 買い物登録ボタン押下時のPOST要求時マッピングです。買い物登録画面にリダイレクトします。
	 *</pre>
	 * @param targetYearMonth 表示対象の年月
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 買い物登録画面へリダイレクト
	 *
	 */
	@PostMapping(value = "/dispatchaction/", params = "shoppinAdd")
	public ModelAndView getShoppingAddRedirectLoad(
			@RequestParam("targetYearMonth") String targetYearMonth, RedirectAttributes redirectAttributes) {
		log.debug("getShoppingAddRedirectLoad:targetYearMonth=" + targetYearMonth);
		
		// 画面表示情報を取得
		return this.usecase.readShoppingAddRedirectInfo(loginUserSession.getLoginUserInfo(), targetYearMonth)
			// レスポンスにログインユーザ名を設定
			.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
			// 買い物登録画面へリダイレクト
			.buildRedirect(redirectAttributes);
	}
	
	/**
	 *<pre>
	 * 収支更新ボタン押下時のPOST要求時マッピングです。収支登録画面(更新)にリダイレクトします。
	 *</pre>
	 * @param targetYearMonth 表示対象の年月
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 収支登録画面(更新)へリダイレクト
	 *
	 */
	@PostMapping(value = "/dispatchaction/", params = "accountMonthUpdate")
	public ModelAndView getAccountMonthUpdateRedirectLoad(
			@RequestParam("targetYearMonth") String targetYearMonth, RedirectAttributes redirectAttributes) {
		log.debug("getAccountMonthUpdateRedirectLoad:targetYearMonth=" + targetYearMonth);
		
		// 画面表示情報を取得
		return this.usecase.readAccountMonthUpdateRedirectInfo(loginUserSession.getLoginUserInfo(), targetYearMonth)
			// レスポンスにログインユーザ名を設定
			.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
			// 収支登録画面(更新)へリダイレクト
			.buildRedirect(redirectAttributes);
	}
	
	/**
	 *<pre>
	 * 支出別一覧の訂正ボタン押下時のPOST要求時マッピングです。
	 * 収支登録画面（支出訂正フォーム）にリダイレクトします。
	 *</pre>
	 * @param targetYearMonth 表示対象の年月
	 * @param expenditureCode 訂正対象の支出コード
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 収支登録画面（支出訂正フォーム）へリダイレクト
	 *
	 */
	@PostMapping(value = "/dispatchaction/", params = "expenditureCorrect")
	public ModelAndView postExpenditureCorrect(
			@RequestParam("targetYearMonth") String targetYearMonth,
			@RequestParam("expenditureCode") String expenditureCode,
			RedirectAttributes redirectAttributes) {
		log.debug("postExpenditureCorrect:targetYearMonth=" + targetYearMonth + ",expenditureCode=" + expenditureCode);

		redirectAttributes.addAttribute("targetYearMonth", targetYearMonth);
		redirectAttributes.addAttribute("expenditureCode", expenditureCode);
		return new ModelAndView(
				"redirect:/myhacbook/accountregist/incomeandexpenditure/expenditurecorrectload/");
	}

	/**
	 *<pre>
	 * 各種登録処理で、各月の収支画面にリダイレクト(Get要求時)のマッピングです。
	 * 対象月の収支画面を表示します。
	 *
	 * 【前提条件】登録・更新完了後のリダイレクトのため、収支データは必ず存在する
	 *</pre>
	 * @param targetYearMonth 表示対象の年月
	 * @param redirectMessages リダイレクト元から引き継いだメッセージ
	 * @return マイ家計簿(各月の収支)画面
	 *
	 */
	@GetMapping("/registComplete/")
	public ModelAndView registComplete(
			@RequestParam("targetYearMonth") String targetYearMonth, @ModelAttribute CompleteRedirectMessages redirectMessages) {
		log.debug("registComplete:targetYearMonth="+ targetYearMonth + ",input=" + redirectMessages);

		// 画面表示情報を取得
		AccountMonthInquiryResponse response = this.usecase.read(loginUserSession.getLoginUserInfo(), targetYearMonth);
		// レスポンスにログインユーザ名を設定
		response.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName());

		// リダイレクト元から引き継いだメッセージを画面表示メッセージに設定
		if (redirectMessages.getRedirectMessages() != null && !redirectMessages.getRedirectMessages().isEmpty()) {
			redirectMessages.getRedirectMessages().forEach(response::addMessage);
		}

		// 登録・更新完了後なので収支データは必ず存在するはず
		// データが存在しない場合はシステムエラー（登録処理の不具合など）
		if (!response.isSyuusiDataFlg()) {
			log.error("登録完了後に収支データが存在しません。yearMonth={}", targetYearMonth);
			throw new MyHouseholdAccountBookRuntimeException(
				"データの整合性エラーが発生しました。管理者に連絡してください。");
		}

		return response.buildWithData();
	}
}
