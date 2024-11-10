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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.application.usecase.account.regist.SimpleShoppingRegistUseCase;
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
	
}
