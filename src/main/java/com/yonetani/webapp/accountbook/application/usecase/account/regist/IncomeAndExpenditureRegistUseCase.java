/**
 * 収支登録ユースケースです。
 * ・収支登録画面の表示情報取得(新規登録時)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.regist;

import org.springframework.stereotype.Service;

import com.yonetani.webapp.accountbook.presentation.response.account.regist.IncomeAndExpenditureRegistResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 収支登録ユースケースです。
 * ・収支登録画面の表示情報取得(新規登録時)
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class IncomeAndExpenditureRegistUseCase {
	
	/**
	 *<pre>
	 * 収支登録画面の表示情報取得(新規登録時)
	 * 
	 * 引数で指定した対象年月の収支情報を新規に作成し画面表示情報に設定します。
	 * 
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 収支を新規登録する対象年月の値
	 * @param returnYearMonth 月度収支画面に戻るときに表示する対象年月の値
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse readInitInfo(
			LoginUserInfo user, String targetYearMonth, String returnYearMonth) {
		log.debug("readInitInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth + ",returnYearMonth=" + returnYearMonth);
		
		// TODO 自動生成されたメソッド・スタブ
		
		return IncomeAndExpenditureRegistResponse.getInstance();
	}

	/**
	 *<pre>
	 * 収支登録画面の表示情報取得(更新時)
	 * 
	 * 引数で指定した対象年月の収支情報を取得し画面表示情報に設定します。
	 * 
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 収支更新対象の対象年月の値
	 * @return 収支登録画面の表示情報
	 *
	 */
	public IncomeAndExpenditureRegistResponse readUpdateInfo(LoginUserInfo user, String targetYearMonth) {
		log.debug("readUpdateInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);
		
		// TODO 自動生成されたメソッド・スタブ
		
		return IncomeAndExpenditureRegistResponse.getInstance();
	}

}
