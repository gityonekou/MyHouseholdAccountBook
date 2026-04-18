/**
 * 支出項目テーブル:SISYUTU_ITEM_TABLEのデータを登録・更新・参照するリポジトリーです
 * 支出項目テーブル:SISYUTU_ITEM_TABLEのみの参照を行う場合はこのリポジトリーを使います。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/10 : 1.00.00  新規作成
 * 2026/03/20 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.account.expenditureinfo;

import com.yonetani.webapp.accountbook.domain.model.account.expenditureinfo.ExpenditureItemInfo;
import com.yonetani.webapp.accountbook.domain.model.account.expenditureinfo.ExpenditureItemInfoInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndExpenditureItemSortOrderBetweenAB;

/**
 *<pre>
 * 支出項目テーブル:SISYUTU_ITEM_TABLEのデータを登録・更新・参照するリポジトリーです
 * 支出項目テーブル:SISYUTU_ITEM_TABLEのみの参照を行う場合はこのリポジトリーを使います。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
public interface SisyutuItemTableRepository {
	
	/**
	 *<pre>
	 * 支出項目テーブル情報を新規登録します。
	 *</pre>
	 * @param data 追加する支出項目テーブル情報
	 * @return データ追加件数
	 *
	 */
	int add(ExpenditureItemInfo data);
	
	/**
	 *<pre>
	 * 支出項目テーブル情報を更新します。
	 *</pre>
	 * @param data 更新データ
	 * @return 更新件数
	 *
	 */
	int update(ExpenditureItemInfo data);

	/**
	 *<pre>
	 * 支出項目テーブル情報のうち、支出項目詳細内容の値を更新します。
	 *</pre>
	 * @param data 更新データ
	 * @return 更新件数
	 *
	 */
	int updateExpenditureItemDetailContext(ExpenditureItemInfo data);
	
	/**
	 *<pre>
	 * 支出項目テーブル情報のうち、支出項目表示順の値を更新します。
	 *</pre>
	 * @param data 更新データ
	 * @return 更新件数
	 *
	 */
	int updateExpenditureItemSortOrder(ExpenditureItemInfo data);
	
	/**
	 *<pre>
	 * ユーザIDに対応する支出項目情報を取得します。
	 *</pre>
	 * @param userId 検索対象のユーザID
	 * @return 支出項目情報のリスト
	 *
	 */
	ExpenditureItemInfoInquiryList findByUserId(SearchQueryUserId userId);
	
	/**
	 *<pre>
	 * ユーザID、支出項目表示順A～支出項目表示順Bに対応する支出項目情報を取得します。
	 *</pre>
	 * @param search 検索対象のユーザID、支出項目表示順A、支出項目表示順B
	 * @return 支出項目情報のリスト
	 *
	 */
	ExpenditureItemInfoInquiryList findBySortOrderBetween(SearchQueryUserIdAndExpenditureItemSortOrderBetweenAB from);
	
	/**
	 *<pre>
	 * 支出項目テーブル:SISYUTU_ITEM_TABLEの主キー（ユーザID、支出項目コード）で支出項目テーブルを検索し、結果を取得します。
	 *</pre>
	 * @param search 検索対象のユーザID、支出項目コード
	 * @return 支出項目情報
	 *
	 */
	ExpenditureItemInfo findByPrimaryKey(SearchQueryUserIdAndExpenditureItemCode search);
	
	/**
	 *<pre>
	 * ユーザID、親の支出項目コードを条件に、親の支出項目に属する支出項目情報を取得します。
	 *</pre>
	 * @param search 検索対象のユーザID、親の支出項目コード
	 * @return 親の支出項目に属する支出項目の名称一覧
	 *
	 */
	ExpenditureItemInfoInquiryList searchParentMemberSisyutuItemList(SearchQueryUserIdAndExpenditureItemCode search);

	/**
	 *<pre>
	 * 新規の支出項目コード発番用にユーザIDに対応する支出項目情報が何件あるかを取得します。
	 *</pre>
	 * @param userId 検索対象のユーザID
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	int countByUserId(SearchQueryUserId userId);
	
}
