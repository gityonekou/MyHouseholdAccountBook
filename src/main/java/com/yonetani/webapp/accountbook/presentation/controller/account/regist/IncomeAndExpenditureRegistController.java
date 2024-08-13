/**
 * 収支登録画面を担当するコントローラーです。
 * 収支登録画面は以下3つの画面で構成されていますが、そのすべての画面遷移をこのコントローラーで
 * 管理します。
 * ・収支登録画面：指定月の収支登録及び更新を行う画面
 * ・支出項目選択画面：新規の支出に該当する支出項目を選択する画面
 * ・収支登録確認画面：収支登録画面で登録した内容の最終確認を行う。登録OKなら、実際に登録・更新を行う
 * 
 * 画面遷移
 * ・収支登録確認画面からの遷移(指定月の収支情報なし)(POST)→収支登録画面(収入登録エリアをアクティブ)
 * ・各家計簿参照画面から更新ボタン押下(POST)→収支登録画面(収入と支出の一覧表示)
 * ・収支登録画面：収入の新規登録ボタン押下(GET)→収支登録画面(収入登録エリアをアクティブ)
 * ・収支登録画面：任意の収入の訂正ボタン押下(GET)→収支登録画面(収入登録エリアをアクティブ)
 * ・収支登録画面：収入を新規登録(POST)→収支登録画面(収入と支出の一覧表示)
 * ・収支登録画面：収入を訂正(POST)→収支登録画面(収入と支出の一覧表示)
 * ・収支登録画面：収入を削除(POST)→収支登録画面(収入と支出の一覧表示)
 * ・収支登録画面：支出の新規登録ボタン押下(GET)→支出項目選択画面
 * ・支出項目選択画面：任意の支出項目を選択→支出項目アクション選択画面
 * ・支出項目選択画面：キャンセルボタンを押下→収支登録画面(収入と支出の一覧表示)
 * ・支出項目選択画面(アクション選択)：確定ボタン押下→収支登録画面(支出登録エリアをアクティブ)
 * ・支出項目選択画面(アクション選択)：キャンセルボタンを押下→収支登録画面(収入と支出の一覧表示)
 * ・収支登録画面：任意の支出の訂正ボタン押下(GET)→収支登録画面(支出登録エリアをアクティブ)
 * ・収支登録画面：支出を新規登録(POST)→収支登録画面(収入と支出の一覧表示)
 * ・収支登録画面：支出を訂正(POST)→収支登録画面(収入と支出の一覧表示)
 * ・収支登録画面：支出を削除(POST)→収支登録画面(収入と支出の一覧表示)
 * ・収支登録画面：内容確認ボタン押下(POST)→収支登録内容確認画面
 * ・収支登録画面：キャンセルボタン押下(POST)→各月の収支画面に戻る(収支登録画面遷移前の表示月の値で再表示する)
 * ・収支登録画面：登録ボタン押下(POST)→登録・更新成功時→リダイレクト(GET)：登録した月の収支画面
 * ・収支登録画面：前に戻るボタン押下(POST)→収支登録画面(収入と支出の一覧表示)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/16 : 1.00.00  新規作成
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

import com.yonetani.webapp.accountbook.application.usecase.account.regist.IncomeAndExpenditureRegistUseCase;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.ExpenditureItemForm;
import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.IncomeItemForm;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.IncomeAndExpenditureRegistResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.CompleteRedirectMessages;
import com.yonetani.webapp.accountbook.presentation.session.IncomeAndExpenditureRegistSession;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 収支登録画面を担当するコントローラーです。
 * 収支登録画面は以下3つの画面で構成されていますが、そのすべての画面遷移をこのコントローラーで
 * 管理します。
 * ・収支登録画面：指定月の収支登録及び更新を行う画面
 * ・支出項目選択画面：新規の支出に該当する支出項目を選択する画面
 * ・収支登録確認画面：収支登録画面で登録した内容の最終確認を行う。登録OKなら、実際に登録・更新を行う
 * 
 * 画面遷移
 * ・収支登録確認画面からの遷移(指定月の収支情報なし)(POST)→収支登録画面(収入登録エリアをアクティブ)
 * ・各家計簿参照画面から更新ボタン押下(POST)→収支登録画面(収入と支出の一覧表示)
 * ・収支登録画面：収入の新規登録ボタン押下(GET)→収支登録画面(収入登録エリアをアクティブ)
 * ・収支登録画面：任意の収入の訂正ボタン押下(GET)→収支登録画面(収入登録エリアをアクティブ)
 * ・収支登録画面：収入を新規登録(POST)→収支登録画面(収入と支出の一覧表示)
 * ・収支登録画面：収入を訂正(POST)→収支登録画面(収入と支出の一覧表示)
 * ・収支登録画面：収入を削除(POST)→収支登録画面(収入と支出の一覧表示)
 * ・収支登録画面：支出の新規登録ボタン押下(GET)→支出項目選択画面
 * ・支出項目選択画面：任意の支出項目を選択→支出項目アクション選択画面
 * ・支出項目選択画面：キャンセルボタンを押下→収支登録画面(収入と支出の一覧表示)
 * ・支出項目選択画面(アクション選択)：確定ボタン押下→収支登録画面(支出登録エリアをアクティブ)
 * ・支出項目選択画面(アクション選択)：キャンセルボタンを押下→収支登録画面(収入と支出の一覧表示)
 * ・収支登録画面：任意の支出の訂正ボタン押下(GET)→収支登録画面(支出登録エリアをアクティブ)
 * ・収支登録画面：支出を新規登録(POST)→収支登録画面(収入と支出の一覧表示)
 * ・収支登録画面：支出を訂正(POST)→収支登録画面(収入と支出の一覧表示)
 * ・収支登録画面：支出を削除(POST)→収支登録画面(収入と支出の一覧表示)
 * ・収支登録画面：内容確認ボタン押下(POST)→収支登録内容確認画面
 * ・収支登録画面：キャンセルボタン押下(POST)→各月の収支画面に戻る(収支登録画面遷移前の表示月の値で再表示する)
 * ・収支登録内容確認画面：登録ボタン押下(POST)→登録・更新成功時→リダイレクト(GET)：登録した月の収支画面
 * ・収支登録内容確認画面：前に戻るボタン押下(POST)→収支登録画面(収入と支出の一覧表示)
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Controller
@Log4j2
@RequestMapping("/myhacbook/accountregist/incomeandexpenditure/")
@RequiredArgsConstructor
public class IncomeAndExpenditureRegistController {
	// UseCase
	private final IncomeAndExpenditureRegistUseCase usecase;
	// ユーザーセッション
	private final LoginUserSession loginUserSession;
	// 収支一覧セッション
	private final IncomeAndExpenditureRegistSession registListSession;
	
	/**
	 *<pre>
	 * 新規で月度収支を登録する場合の収支登録画面初期表示のPOST要求時マッピングです。
	 * 以下画面遷移に対応します。
	 *・収支登録確認画面からの遷移(指定月の収支情報なし)(POST)→収支登録画面(収入登録エリアをアクティブ)
	 *
	 *</pre>
	 * @param targetYearMonth 収支を新規登録する対象年月の値
	 * @param returnYearMonth 月度収支画面に戻るときに表示する対象年月の値
	 * @return 収支登録画面情報
	 *
	 */
	@PostMapping("/initload/")
	public ModelAndView getInitLoad(
			@RequestParam("targetYearMonth") String targetYearMonth,
			@RequestParam("returnYearMonth") String returnYearMonth) {
		log.debug("getInitLoad:targetYearMonth="+ targetYearMonth + ",returnYearMonth=" + returnYearMonth);
		
		// 収支登録セッション情報をクリア
		registListSession.clearData(targetYearMonth, returnYearMonth);
		// 画面表示データ読込
		IncomeAndExpenditureRegistResponse response = usecase.readInitInfo(loginUserSession.getLoginUserInfo(), targetYearMonth);
		// 新しい支出登録情報をセッションに設定
		registListSession.setExpenditureRegistItemList(response.getExpenditureRegistItemList());
		// 画面表示情報にログインユーザ名を設定
		return response.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 指定した年月の収支を更新する場合の収支登録画面初期表示のPOST要求時マッピングです。
	 * 以下画面遷移に対応します。
	 *・各家計簿参照画面から更新ボタン押下(POST)→収支登録画面(収入と支出の一覧表示)
	 *
	 *</pre>
	 * @param targetYearMonth 更新対象の収支の年月の値
	 * @return 収支登録画面情報
	 *
	 */
	@PostMapping("/updateload/")
	public ModelAndView getUpdateLoad(@RequestParam("targetYearMonth") String targetYearMonth) {
		log.debug("getUpdateLoad:targetYearMonth="+ targetYearMonth);
		
		// 収支登録セッション情報をクリア
		registListSession.clearData(targetYearMonth, targetYearMonth);
		// 画面表示データ読込
		IncomeAndExpenditureRegistResponse response = usecase.readUpdateInfo(loginUserSession.getLoginUserInfo(), targetYearMonth);
		// 収入登録情報をセッションに設定
		registListSession.setIncomeRegistItemList(response.getIncomeRegistItemList());
		// 支出登録情報をセッションに設定
		registListSession.setExpenditureRegistItemList(response.getExpenditureRegistItemList());
		/* 画面表示情報返却 */
		// 画面表示情報にログインユーザ名を設定
		return response.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 収支登録画面で収入情報の新規登録ボタン押下時のGET要求時マッピングです。
	 * 収支登録画面の各収支一覧と新規の収入情報登録フォームを表示します。
	 *</pre>
	 * @return 収支登録画面
	 *
	 */
	@GetMapping("/incomeaddselect/")
	public ModelAndView getIncomeAddSelect() {
		log.debug("getIncomeAddSelect:");
		// 画面表示情報を取得
		return this.usecase.readIncomeAddSelect(
					// ログインユーザ情報
					loginUserSession.getLoginUserInfo(),
					// 収支の対象年月
					registListSession.getTargetYearMonth(),
					// セッションに設定されている収支情報のリスト
					registListSession.getIncomeRegistItemList(),
					// セッションに設定されている支出情報のリスト
					registListSession.getExpenditureRegistItemList())
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 収支登録画面で更新対象の収入情報選択時のGET要求時マッピングです。
	 * 収支登録画面の各収支一覧と更新対象の収入情報を反映した収入情報登録フォームを表示します。
	 *</pre>
	 * @param incomeCode 選択した収入コード
	 * @return 収支登録画面
	 *
	 */
	@GetMapping("/incomeupdateselect")
	public ModelAndView getIncomeUpdateSelect(@RequestParam("incomeCode") String incomeCode) {
		log.debug("getIncomeUpdateSelect:incomeCode=" + incomeCode);
		// 画面表示情報を取得
		return this.usecase.readIncomeUpdateSelect(
					// ログインユーザ情報
					loginUserSession.getLoginUserInfo(),
					// 収支の対象年月
					registListSession.getTargetYearMonth(),
					// 収入コード
					incomeCode,
					// セッションに設定されている収支情報のリスト
					registListSession.getIncomeRegistItemList(),
					// セッションに設定されている支出情報のリスト
					registListSession.getExpenditureRegistItemList())
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 収入情報の登録・更新時のPOST要求時マッピングです。
	 * 以下画面遷移に対応します。
	 * ・収入を新規登録(POST)→収支登録画面(収入と支出の一覧表示)
	 * ・収入を訂正(POST)→収支登録画面(収入と支出の一覧表示)
	 *</pre>
	 * @param inputForm 収入情報入力フォームデータ
	 * @param bindingResult フォームのバリデーションチェック結果
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 収支登録画面情報
	 *
	 */
	@PostMapping(value="/incomeupdate/", params = "actionUpdate")
	public ModelAndView postIncomeUpdate(@ModelAttribute @Validated IncomeItemForm inputForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		log.debug("postIncomeUpdate:input=" + inputForm);
		/* 入力フィールドのバリデーションチェック結果を判定 */
		// チェック結果エラーの場合
		if(bindingResult.hasErrors()) {
			// 初期表示情報を取得し、入力チェックエラーを設定
			return this.usecase.readIncomeUpdateBindingErrorSetInfo(
						loginUserSession.getLoginUserInfo(),
						registListSession.getTargetYearMonth(),
						inputForm,
						registListSession.getIncomeRegistItemList(),
						registListSession.getExpenditureRegistItemList())
					// レスポンスにログインユーザ名を設定
					.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
					// レスポンスからModelAndViewを生成
					.build();
			
		// チェック結果OKの場合
		} else {
			// actionに従い、処理を実行
			IncomeAndExpenditureRegistResponse response = this.usecase.execIncomeAction(
					loginUserSession.getLoginUserInfo(),
					registListSession.getTargetYearMonth(),
					inputForm,
					registListSession.getIncomeRegistItemList());
			// 収入一覧をセッションに設定
			registListSession.setIncomeRegistItemList(response.getIncomeRegistItemList());
			// 収入支出一覧表示にリダイレクト
			return response.buildRedirect(redirectAttributes);
		}
	}
	
	/**
	 *<pre>
	 * 収入情報の削除のPOST要求時マッピングです。
	 * 以下画面遷移に対応します。
	 * ・収入を削除(POST)→収支登録画面(収入と支出の一覧表示)
	 *</pre>
	 * @param inputForm 収入情報入力フォームデータ
	 * @param bindingResult フォームのバリデーションチェック結果
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 収支登録画面情報
	 *
	 */
	@PostMapping(value="/incomeupdate/", params = "actionDelete")
	public ModelAndView postIncomeDelete(@ModelAttribute @Validated IncomeItemForm inputForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		log.debug("postIncomeDelete:input=" + inputForm);
		/* 入力フィールドのバリデーションチェック結果を判定 */
		// チェック結果エラーの場合
		if(bindingResult.hasErrors()) {
			// 初期表示情報を取得し、入力チェックエラーを設定
			return this.usecase.readIncomeUpdateBindingErrorSetInfo(
						loginUserSession.getLoginUserInfo(),
						registListSession.getTargetYearMonth(),
						inputForm,
						registListSession.getIncomeRegistItemList(),
						registListSession.getExpenditureRegistItemList())
					// レスポンスにログインユーザ名を設定
					.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
					// レスポンスからModelAndViewを生成
					.build();
			
		// チェック結果OKの場合
		} else {
			// アクションに削除を設定
			inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_DELETE);
			// actionに従い、処理を実行
			IncomeAndExpenditureRegistResponse response = this.usecase.execIncomeAction(
					loginUserSession.getLoginUserInfo(),
					registListSession.getTargetYearMonth(),
					inputForm,
					registListSession.getIncomeRegistItemList());
			// 収入一覧をセッションに設定
			registListSession.setIncomeRegistItemList(response.getIncomeRegistItemList());
			// 収入支出一覧表示にリダイレクト
			return response.buildRedirect(redirectAttributes);
		}
	}
	
	/**
	 *<pre>
	 * 収支登録画面で支出情報の新規登録ボタン押下時のGET要求時マッピングです。
	 * 支出項目選択画面を表示します。
	 *</pre>
	 * @return 支出項目選択画面
	 *
	 */
	@GetMapping("/expenditureaddselect/")
	public ModelAndView getExpenditureAddSelect() {
		log.debug("getExpenditureAddSelect:");
		// 画面表示情報を取得
		return this.usecase.readExpenditureAddSelect(loginUserSession.getLoginUserInfo())
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 収支登録画面で更新対象の支出情報選択時のGET要求時マッピングです。
	 * 収支登録画面の各収支一覧と更新対象の支出情報を反映した支出情報登録フォームを表示します。
	 *</pre>
	 * @param expenditureCode 選択した支出コード
	 * @return 収支登録画面
	 *
	 */
	@GetMapping("/expenditureupdateselect")
	public ModelAndView getExpenditureUpdateSelect(@RequestParam("expenditureCode") String expenditureCode) {
		log.debug("getExpenditureUpdateSelect:expenditureCode=" + expenditureCode);
		// 画面表示情報を取得
		return this.usecase.readExpenditureUpdateSelect(
					// ログインユーザ情報
					loginUserSession.getLoginUserInfo(),
					// 収支の対象年月
					registListSession.getTargetYearMonth(),
					// 支出コード
					expenditureCode,
					// セッションに設定されている収支情報のリスト
					registListSession.getIncomeRegistItemList(),
					// セッションに設定されている支出情報のリスト
					registListSession.getExpenditureRegistItemList())
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 支出情報の登録・更新時のPOST要求時マッピングです。
	 * 以下画面遷移に対応します。
	 * ・支出を新規登録(POST)→収支登録画面(収入と支出の一覧表示)
	 * ・支出を訂正(POST)→収支登録画面(収入と支出の一覧表示)
	 *</pre>
	 * @param inputForm 支出情報入力フォームデータ
	 * @param bindingResult フォームのバリデーションチェック結果
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 収支登録画面情報
	 *
	 */
	@PostMapping(value="/expenditureupdate/", params = "actionUpdate")
	public ModelAndView postExpenditureUpdate(@ModelAttribute @Validated ExpenditureItemForm inputForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		log.debug("postExpenditureUpdate:input=" + inputForm);
		/* 入力フィールドのバリデーションチェック結果を判定 */
		// チェック結果エラーの場合
		if(bindingResult.hasErrors()) {
			// 初期表示情報を取得し、入力チェックエラーを設定
			return this.usecase.readExpenditureUpdateBindingErrorSetInfo(
						loginUserSession.getLoginUserInfo(),
						registListSession.getTargetYearMonth(),
						inputForm,
						registListSession.getIncomeRegistItemList(),
						registListSession.getExpenditureRegistItemList())
					// レスポンスにログインユーザ名を設定
					.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
					// レスポンスからModelAndViewを生成
					.build();
			
		// チェック結果OKの場合
		} else {
			// actionに従い、処理を実行
			IncomeAndExpenditureRegistResponse response = this.usecase.execExpenditureAction(
					loginUserSession.getLoginUserInfo(),
					registListSession.getTargetYearMonth(),
					inputForm,
					registListSession.getExpenditureRegistItemList());
			// 支出一覧をセッションに設定
			registListSession.setExpenditureRegistItemList(response.getExpenditureRegistItemList());
			// 収入支出一覧表示にリダイレクト
			return response.buildRedirect(redirectAttributes);
		}
	}
	
	/**
	 *<pre>
	 * 支出情報の削除のPOST要求時マッピングです。
	 * 以下画面遷移に対応します。
	 * ・支出を削除(POST)→収支登録画面(収入と支出の一覧表示)
	 *</pre>
	 * @param inputForm 支出情報入力フォームデータ
	 * @param bindingResult フォームのバリデーションチェック結果
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 収支登録画面情報
	 *
	 */
	@PostMapping(value="/expenditureupdate/", params = "actionDelete")
	public ModelAndView postExpenditureDelete(@ModelAttribute @Validated ExpenditureItemForm inputForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		log.debug("postExpenditureDelete:input=" + inputForm);
		/* 入力フィールドのバリデーションチェック結果を判定 */
		// チェック結果エラーの場合
		if(bindingResult.hasErrors()) {
			// 初期表示情報を取得し、入力チェックエラーを設定
			return this.usecase.readExpenditureUpdateBindingErrorSetInfo(
						loginUserSession.getLoginUserInfo(),
						registListSession.getTargetYearMonth(),
						inputForm,
						registListSession.getIncomeRegistItemList(),
						registListSession.getExpenditureRegistItemList())
					// レスポンスにログインユーザ名を設定
					.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
					// レスポンスからModelAndViewを生成
					.build();
			
		// チェック結果OKの場合
		} else {
			// アクションに削除を設定
			inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_DELETE);
			// actionに従い、処理を実行
			IncomeAndExpenditureRegistResponse response = this.usecase.execExpenditureAction(
					loginUserSession.getLoginUserInfo(),
					registListSession.getTargetYearMonth(),
					inputForm,
					registListSession.getExpenditureRegistItemList());
			// 支出一覧をセッションに設定
			registListSession.setExpenditureRegistItemList(response.getExpenditureRegistItemList());
			// 収入支出一覧表示にリダイレクト
			return response.buildRedirect(redirectAttributes);
		}
	}
	
	/**
	 *<pre>
	 * 収支登録画面で収入情報、および、支出情報登録後のリダイレクト(Get要求時)のマッピングです。
	 * 収支登録画面の各収支一覧を表示します。
	 *</pre>
	 * @param redirectMessages リダイレクト元から引き継いだメッセージ
	 * @return 収支登録画面
	 *
	 */
	@GetMapping("/updateComplete/")
	public ModelAndView updateComplete(@ModelAttribute CompleteRedirectMessages redirectMessages) {
		log.debug("updateComplete:input=" + redirectMessages);
		// 画面表示情報を取得
		return this.usecase.readIncomeAndExpenditureInfoList(
					// ログインユーザ情報
					loginUserSession.getLoginUserInfo(),
					// 収支の対象年月
					registListSession.getTargetYearMonth(),
					// セッションに設定されている収支情報のリスト
					registListSession.getIncomeRegistItemList(),
					// セッションに設定されている支出情報のリスト
					registListSession.getExpenditureRegistItemList())
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.buildComplete(redirectMessages);
	}
	
	/**
	 *<pre>
	 * 収支登録内容確認画面で登録完了後のリダイレクト(Get要求時)のマッピングです。
	 * 登録した月の収支画面を表示します。
	 *</pre>
	 * @param redirectMessages リダイレクト元から引き継いだメッセージ
	 * @return 収支登録画面
	 *
	 */
	@GetMapping("/registComplete/")
	public ModelAndView registComplete(@ModelAttribute CompleteRedirectMessages redirectMessages) {
		log.debug("registComplete:input=" + redirectMessages);
		// 画面表示情報を取得
		return this.usecase.readIncomeAndExpenditureInfoList(
				// ログインユーザ情報
				loginUserSession.getLoginUserInfo(),
				// 収支の対象年月
				registListSession.getTargetYearMonth(),
				// セッションに設定されている収支情報のリスト
				registListSession.getIncomeRegistItemList(),
				// セッションに設定されている支出情報のリスト
				registListSession.getExpenditureRegistItemList())
			// レスポンスにログインユーザ名を設定
			.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
			// レスポンスからModelAndViewを生成
			.buildComplete(redirectMessages);
	}
}
