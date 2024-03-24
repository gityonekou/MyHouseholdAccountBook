/**
 * 支出項目テーブル:SISYUTU_ITEM_TABLEのデータを登録・更新・参照するリポジトリーです
 * 支出項目テーブル:SISYUTU_ITEM_TABLEのみの参照を行う場合はこのリポジトリーを使います。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/10 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.account.inquiry;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemCode;

/**
 *<pre>
 * 支出項目テーブル:SISYUTU_ITEM_TABLEのデータを登録・更新・参照するリポジトリーです
 * 支出項目テーブル:SISYUTU_ITEM_TABLEのみの参照を行う場合はこのリポジトリーを使います。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
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
	int add(SisyutuItem data);
	
	/**
	 *<pre>
	 * 支出項目テーブル情報を更新します。
	 *</pre>
	 * @param data 更新データ
	 * @return 更新件数
	 *
	 */
	int update(SisyutuItem data);

	/**
	 *<pre>
	 * 支出項目テーブル情報のうち、支出項目詳細内容の値を更新します。
	 *</pre>
	 * @param data 更新データ
	 * @return 更新件数
	 *
	 */
	int updateSisyutuItemDetailContext(SisyutuItem data);
	
	/**
	 *<pre>
	 * ユーザIDに対応する支出項目情報を取得します。
	 *</pre>
	 * @param userId 検索対象のユーザID
	 * @return 支出項目情報のリスト
	 *
	 */
	SisyutuItemInquiryList findById(SearchQueryUserId userId);

	/**
	 *<pre>
	 * ユーザID、支出項目コードに対応する支出項目情報を取得します。
	 *</pre>
	 * @param search 検索対象のユーザID、支出項目コード
	 * @return 支出項目情報
	 *
	 */
	SisyutuItem findByIdAndSisyutuItemCode(SearchQueryUserIdAndSisyutuItemCode search);
	
	/**
	 *<pre>
	 * ユーザID、親の支出項目コードを条件に、親の支出項目に属する支出項目情報を取得します。
	 *</pre>
	 * @param search 検索対象のユーザID、親の支出項目コード
	 * @return 親の支出項目に属する支出項目の名称一覧
	 *
	 */
	SisyutuItemInquiryList searchParentMemberSisyutuItemList(SearchQueryUserIdAndSisyutuItemCode search);

	/**
	 *<pre>
	 * 新規の支出項目コード発番用にユーザIDに対応する支出項目情報が何件あるかを取得します。
	 *</pre>
	 * @param userId 検索対象のユーザID
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	int countById(SearchQueryUserId userId);
	
}
