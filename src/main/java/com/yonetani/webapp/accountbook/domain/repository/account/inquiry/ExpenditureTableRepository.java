/**
 * 支出テーブル：EXPENDITURE_TABLEのデータを登録・更新・参照するリポジトリーです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.account.inquiry;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.ExpenditureItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.ExpenditureItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;

/**
 *<pre>
 * 支出テーブル：EXPENDITURE_TABLEのデータを登録・更新・参照するリポジトリーです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public interface ExpenditureTableRepository {
	
	/**
	 *<pre>
	 * 支出テーブル情報を新規登録します。
	 *</pre>
	 * @param data 新規追加データ
	 * @return 登録されたデータの件数
	 *
	 */
	int add(ExpenditureItem data);
	
	/**
	 *<pre>
	 * 支出テーブル情報を更新します。
	 *</pre>
	 * @param data 更新データ
	 * @return 更新されたデータの件数
	 *
	 */
	int update(ExpenditureItem data);
	
	/**
	 *<pre>
	 * 指定した支出情報を論理削除します。
	 * 支出テーブル:EXPENDITURE_TABLEの削除フラグ項目(DELETE_FLG)にtureの値を設定することで
	 * 論理的に削除します。
	 * 
	 *</pre>
	 * @param data 更新データ
	 * @return 更新されたデータの件数
	 *
	 */
	int delete(ExpenditureItem data);
	
	/**
	 *<pre>
	 * ユーザIDに対応する支出情報を取得します。
	 *</pre>
	 * @param userId 検索対象のユーザID
	 * @return 支出情報のリスト
	 *
	 */
	ExpenditureItemInquiryList findById(SearchQueryUserId userId);
	
	/**
	 *<pre>
	 * 新規の支出コード発番用にユーザIDに対応する支出情報が何件あるかを取得します。
	 *</pre>
	 * @param userId 検索対象のユーザID
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	int countById(SearchQueryUserId userId);
	
}
