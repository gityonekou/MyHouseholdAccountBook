/**
 * 買い物登録方法選択画面の情報取得を行うユースケースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.regist;

import org.springframework.stereotype.Service;

import com.yonetani.webapp.accountbook.common.component.AccountBookUserInquiryUseCase;
import com.yonetani.webapp.accountbook.domain.model.common.NowTargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.ShoppingRegistRedirectResponse;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.ShoppingRegistTopMenuResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 買い物登録方法選択画面の情報取得を行うユースケースです。
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
public class ShoppingRegistTopMenuUseCase {
	
	// ユーザ情報照会ユースケース
	private final AccountBookUserInquiryUseCase userInquiry;
	
	/**
	 *<pre>
	 * 現在の決算月情報を元に買い物登録方法選択画面のレスポンス情報を生成し返却します	。
	 *</pre>
	 * @param user ユーザ情報
	 * @return 買い物登録方法選択画面情報(レスポンス)
	 *
	 */
	public ShoppingRegistTopMenuResponse read(LoginUserInfo user) {
		log.debug("read:userid=" + user.getUserId());
		
		// ユーザIDに対応する現在の対象年月の値を取得
		NowTargetYearMonth yearMonth = userInquiry.getNowTargetYearMonth(UserId.from(user.getUserId()));
		return ShoppingRegistTopMenuResponse.getInstance(yearMonth.getYearMonth());
		
	}
	
	/**
	 *<pre>
	 * 指定された対象年月の値を元に買い物登録方法選択画面のレスポンス情報を生成し返却します	。
	 *</pre>
	 * @param targetYearMonth ユーザ情報
	 * @return 買い物登録方法選択画面情報(レスポンス)
	 *
	 */
	public ShoppingRegistTopMenuResponse read(String targetYearMonth) {
		log.debug("read:targetYearMonth=" + targetYearMonth);
		
		// 買い物登録方法選択画面情報を返却
		return ShoppingRegistTopMenuResponse.getInstance(TargetYearMonth.from(targetYearMonth));
		
	}
	
	/**
	 *<pre>
	 * 買い物登録画面にリダイレクトするための情報を設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 買い物登録を行う対象年月
	 * @return 買い物登録画面リダイレクト情報
	 *
	 */
	public AbstractResponse readShoppingRegistRedirectInfo(LoginUserInfo user, String targetYearMonth) {
		log.debug("readShoppingRegistRedirectInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);
		ShoppingRegistRedirectResponse response
			= ShoppingRegistRedirectResponse.getShoppingRegistRedirectInstance(targetYearMonth);
		return response;
	}
	
	/**
	 *<pre>
	 * 買い物登録(簡易タイプ)画面にリダイレクトするための情報を設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 買い物登録を行う対象年月
	 * @return 買い物登録(簡易タイプ)画面リダイレクト情報
	 *
	 */
	public AbstractResponse readSimpleShoppingRegistRedirectInfo(LoginUserInfo user, String targetYearMonth) {
		log.debug("readSimpleShoppingRegistRedirectInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);
		ShoppingRegistRedirectResponse response
			= ShoppingRegistRedirectResponse.getSimpleShoppingRegistRedirectInstance(targetYearMonth);
		return response;
	}
}
