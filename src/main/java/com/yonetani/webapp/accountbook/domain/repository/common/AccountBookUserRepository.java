/**
 * マイ家計簿 家計簿利用ユーザ:ACCOUNT_BOOK_USERテーブルの照会・更新を行うリポジトリーです。
 * 以下のIFを提供します。
 * ・指定したユーザIDに対応する現在の対象年・月の値を取得
 * ・現在設定されている有効なすべての家計簿利用ユーザ情報を取得
 * ・家計簿利用ユーザ情報を新規登録
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/08 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.common;

import com.yonetani.webapp.accountbook.domain.model.common.AccountBookAllUsers;
import com.yonetani.webapp.accountbook.domain.model.common.AccountBookUser;
import com.yonetani.webapp.accountbook.domain.model.common.NowTargetYearMonth;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;

/**
 *<pre>
 * マイ家計簿 家計簿利用ユーザ:ACCOUNT_BOOK_USERテーブルの照会・更新を行うリポジトリーです。
 * 以下のIFを提供します。
 * ・指定したユーザIDに対応する現在の対象年・月の値を取得
 * ・現在設定されている有効なすべての家計簿利用ユーザ情報を取得
 * ・家計簿利用ユーザ情報を新規登録
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public interface AccountBookUserRepository {
	
	/**
	 *<pre>
	 * 指定したユーザIDに対応する現在の対象年・月の値を取得します。
	 *</pre>
	 * @param searchQuery 検索条件(ユーザID)
	 * @return ユーザIDに対応する現在の対象年・月の値
	 *
	 */
	NowTargetYearMonth getNowTargetYearMonth(SearchQueryUserId searchQuery);
	
	/**
	 *<pre>
	 * 指定したユーザIDに対応するユーザ情報を取得します。
	 *</pre>
	 * @param searchQuery 検索条件(ユーザID)
	 * @return 家計簿利用ユーザ情報
	 *
	 */
	AccountBookUser getUserInfo(SearchQueryUserId searchQuery);
	
	/**
	 *<pre>
	 * 現在設定されている有効なすべての家計簿利用ユーザ情報を取得します。
	 *</pre>
	 * @return 家計簿利用ユーザ情報のリスト情報
	 *
	 */
	AccountBookAllUsers getAllUsers();
	
	/**
	 *<pre>
	 * 家計簿利用ユーザ情報を新規登録します。
	 *</pre>
	 * @param userInfo 登録する家計簿利用ユーザ情報
	 * @return 登録されたデータの件数
	 *
	 */
	int add(AccountBookUser userInfo);
	
	/**
	 *<pre>
	 * 家計簿利用ユーザ情報を更新します。
	 *</pre>
	 * @param userInfo 更新する家計簿利用ユーザ情報
	 * @return 更新されたデータの件数
	 *
	 */
	int update(AccountBookUser userInfo);
	
}
