/**
 * 買い物登録を行うユースケースです。買い物登録画面の情報取得、及び、画面入力された買い物情報を登録します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.regist;

import org.springframework.stereotype.Service;

import com.yonetani.webapp.accountbook.presentation.response.account.regist.ShoppingRegistResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 買い物登録を行うユースケースです。買い物登録画面の情報取得、及び、画面入力された買い物情報を登録します。
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
public class ShoppingRegistUseCase {
	
	/**
	 *<pre>
	 * 買い物登録画面情報取得
	 * 
	 * 指定した対象年月に応じた買い物登録画面の表示情報を取得します。
	 * 
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth  買い物登録を行う対象年月
	 * @return 買い物登録画面の表示情報
	 *
	 */
	public ShoppingRegistResponse read(LoginUserInfo user, String targetYearMonth) {
		log.debug("read:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);
		return ShoppingRegistResponse.getInstance();
	}

}
