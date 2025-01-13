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
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
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
	// 簡易タイプ買い物リスト取得コンポーネント
	private final SimpleShoppingRegistListComponent simpleShoppingRegistListComponent;
	// 買い物登録時の支出項目に対応する支出テーブル情報と支出金額テーブル情報にアクセスするコンポーネント
	private final ShoppingRegistExpenditureAndSisyutuKingakuComponent checkComponent;
	
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
		
		// ユーザーIDのドメインタイプを生成
		UserId userId = UserId.from(user.getUserId());
		
		// ユーザIDに対応する現在の対象年月の値を取得
		NowTargetYearMonth yearMonth = userInquiry.getNowTargetYearMonth(UserId.from(user.getUserId()));
		
		// レスポンス情報を作成
		ShoppingRegistTopMenuResponse response = ShoppingRegistTopMenuResponse.getInstance(yearMonth.getYearMonth());
		// 対象月の登録されている買い物情報を取得しレスポンスに設定
		simpleShoppingRegistListComponent.setSimpleShoppingRegistList(
				// 検索条件:ユーザID、対象年月
				SearchQueryUserIdAndYearMonth.from(userId, yearMonth.getYearMonth()),
				// 値を設定するレスポンス
				response);
		
		// 簡易タイプ買い物リストの項目に対応する支出テーブル情報と支出金額テーブル情報が登録されてるかどうかをチェック
		checkExpenditureAndSisyutuKingaku(userId, yearMonth.getYearMonth(), response);
		
		// 買い物登録方法選択画面情報を返却
		return response;
		
	}
	
	/**
	 *<pre>
	 * 指定された対象年月の値を元に買い物登録方法選択画面のレスポンス情報を生成し返却します	。
	 *</pre>
	 * @param user ユーザ情報
	 * @param targetYearMonth ユーザ情報
	 * @return 買い物登録方法選択画面情報(レスポンス)
	 *
	 */
	public ShoppingRegistTopMenuResponse read(LoginUserInfo user, String targetYearMonth) {
		log.debug("read:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);
		
		// ユーザーIDのドメインタイプを生成
		UserId userId = UserId.from(user.getUserId());
		// 対象年月のドメインタイプを生成
		TargetYearMonth domainTargetYearMonth = TargetYearMonth.from(targetYearMonth);
		
		// レスポンス情報を作成
		ShoppingRegistTopMenuResponse response = ShoppingRegistTopMenuResponse.getInstance(domainTargetYearMonth);
		// 対象月の登録されている買い物情報を取得しレスポンスに設定
		simpleShoppingRegistListComponent.setSimpleShoppingRegistList(
				// 検索条件:ユーザID、対象年月
				SearchQueryUserIdAndYearMonth.from(userId, domainTargetYearMonth),
				// 値を設定するレスポンス
				response);
		
		// 簡易タイプ買い物リストの項目に対応する支出テーブル情報と支出金額テーブル情報が登録されてるかどうかをチェック
		checkExpenditureAndSisyutuKingaku(userId, domainTargetYearMonth, response);
		
		// 買い物登録方法選択画面情報を返却
		return response;
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
	
	/**
	 *<pre>
	 * 各月の収支参照画面にリダイレクトするための情報を設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param targetYearMonth 表示対象の対象年月
	 * @return 各月の収支参照画面リダイレクト情報
	 *
	 */
	public AbstractResponse readReturnInquiryMonthRedirectInfo(LoginUserInfo user, String targetYearMonth) {
		log.debug("readReturnInquiryMonthRedirectInfo:userid=" + user.getUserId() + ",targetYearMonth=" + targetYearMonth);
		ShoppingRegistRedirectResponse response
			= ShoppingRegistRedirectResponse.getReturnInquiryMonthRedirectInstance(targetYearMonth);
		return response;
	}
	
	/**
	 *<pre>
	 * 簡易タイプ買い物リストの項目に対応する支出テーブル情報と支出金額テーブル情報が登録されてるかどうかをチェックします。
	 * 未登録の場合、買い物情報の登録は不可となります。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYearMonth 対象年月(YYYYMM)
	 * @param response 買い物登録方法選択画面情報
	 *
	 */
	private void checkExpenditureAndSisyutuKingaku(UserId userId, TargetYearMonth targetYearMonth, ShoppingRegistTopMenuResponse response) {
		
		// 必須項目登録チェック
		checkComponent.checkExpenditureAndSisyutuKingaku(userId, targetYearMonth).forEach(message -> {
			// エラーメッセージを追加
			response.setBtnDisabled(true);
			response.addErrorMessage(message);
		});
	}
}
