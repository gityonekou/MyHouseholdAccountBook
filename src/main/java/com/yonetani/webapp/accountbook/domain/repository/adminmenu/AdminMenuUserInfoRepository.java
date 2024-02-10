/**
 * 管理者画面メニュー ユーザ情報管理のDBアクセスを行うリポジトリーです。
 * 以下のIFを提供します。
 * ・ユーザ情報一覧情報を取得
 * ・指定ユーザIDのユーザ情報を取得
 * ・ユーザ情報を登録
 * ・ユーザ情報を更新
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/11/12 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.adminmenu;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookException;
import com.yonetani.webapp.accountbook.domain.model.adminmenu.AdminMenuUserInfo;
import com.yonetani.webapp.accountbook.domain.model.adminmenu.AdminMenuUserInfoItemList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;

/**
 *<pre>
 * 管理者画面メニュー ユーザ情報管理のDBアクセスを行うリポジトリーです。
 * 以下のIFを提供します。
 * ・ユーザ情報一覧情報を取得
 * ・ユーザ情報入力フォームに設定するユーザ情報を取得
 * ・ユーザ情報を登録
 * ・ユーザ情報を更新
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public interface AdminMenuUserInfoRepository {

	
	/**
	 *<pre>
	 * ユーザ情報管理画面のユーザ情報一覧情報を取得します
	 *</pre>
	 * @return ユーザ情報一覧情報のドメインモデル
	 * @throws MyHouseholdAccountBookException 
	 *
	 */
	AdminMenuUserInfoItemList getUserInfoList();
	
	/**
	 *<pre>
	 * ユーザ情報管理画面のユーザ情報入力フォームに設定するユーザ情報を取得します
	 *</pre>
	 * @param userId
	 * @return
	 * @throws MyHouseholdAccountBookException 
	 *
	 */
	AdminMenuUserInfo getUserInfo(SearchQueryUserId userId);
	
	/**
	 *<pre>
	 * ユーザ情報を新規登録します。
	 *</pre>
	 * @param userInfo 登録するユーザ情報
	 * @throws MyHouseholdAccountBookException エラー発生時
	 *
	 */
	void addUserInfo(AdminMenuUserInfo userInfo) throws MyHouseholdAccountBookException;

	/**
	 *<pre>
	 * ユーザ情報を更新します。
	 *</pre>
	 * @param userInfo 更新するユーザ情報
	 * @throws MyHouseholdAccountBookException エラー発生時
	 *
	 */
	void updateUserInfo(AdminMenuUserInfo userInfo) throws MyHouseholdAccountBookException;
	
}
