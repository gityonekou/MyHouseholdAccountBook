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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.application.usecase.account.regist.IncomeAndExpenditureRegistUseCase;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.IncomeAndExpenditureRegistResponse;
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
 * ・収支登録画面：登録ボタン押下(POST)→登録・更新成功時→リダイレクト(GET)：登録した月の収支画面
 * ・収支登録画面：前に戻るボタン押下(POST)→収支登録画面(収入と支出の一覧表示)
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
		registListSession.clearData();
		// 画面表示データ読込
		IncomeAndExpenditureRegistResponse response = this.usecase.readInitInfo(loginUserSession.getLoginUserInfo(), targetYearMonth, returnYearMonth);
		// 新しい収支登録情報をセッションに設定
		registListSession.setIncomeAndExpenditureRegistInfo(response.getIncomeAndExpenditureRegistInfo());
		/* 画面表示情報返却 */
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
		registListSession.clearData();
		// 画面表示データ読込
		IncomeAndExpenditureRegistResponse response = this.usecase.readUpdateInfo(loginUserSession.getLoginUserInfo(), targetYearMonth);
		// 新しい収支登録情報をセッションに設定
		registListSession.setIncomeAndExpenditureRegistInfo(response.getIncomeAndExpenditureRegistInfo());
		/* 画面表示情報返却 */
		// 画面表示情報にログインユーザ名を設定
		return response.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
		// レスポンスからModelAndViewを生成
		.build();
	}
}
