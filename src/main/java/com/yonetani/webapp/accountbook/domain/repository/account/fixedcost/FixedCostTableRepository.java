/**
 * 固定費テーブル:FIXED_COST_TABLEのデータを登録・更新・参照するリポジトリーです
 * 固定費テーブル:FIXED_COST_TABLEのみの参照を行う場合はこのリポジトリーを使います。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/04 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.account.fixedcost;

import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCost;
import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCostInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndFixedCostCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemCode;

/**
 *<pre>
 * 固定費テーブル:FIXED_COST_TABLEのデータを登録・更新・参照するリポジトリーです
 * 固定費テーブル:FIXED_COST_TABLEのみの参照を行う場合はこのリポジトリーを使います。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public interface FixedCostTableRepository {
	/**
	 *<pre>
	 * 固定費テーブル情報を新規登録します。
	 *</pre>
	 * @param data 新規追加データ
	 * @return 登録されたデータの件数
	 *
	 */
	int add(FixedCost data);
	
	/**
	 *<pre>
	 * 固定費テーブル情報を更新します。
	 * <b>支出項目コードは更新対象外項目なので注意してください</b>
	 *</pre>
	 * @param data 更新データ
	 * @return 更新されたデータの件数
	 *
	 */
	int update(FixedCost data);
	
	/**
	 *<pre>
	 * 固定費テーブル情報から指定した固定費の情報を論理削除します。
	 *</pre>
	 * @param data 削除データ
	 * @return 削除されたデータの件数
	 *
	 */
	int delete(FixedCost data);
	
	/**
	 *<pre>
	 * ユーザIDと指定の固定費コードに対応する固定費情報を取得します。
	 *</pre>
	 * @param search 検索対象のユーザID、固定費コード
	 * @return 固定費情報
	 *
	 */
	FixedCost findByIdAndFixedCostCode(SearchQueryUserIdAndFixedCostCode search);
	
	/**
	 *<pre>
	 * ユーザIDに対応する固定費情報の検索結果を取得します。
	 *</pre>
	 * @param search 検索対象のユーザID
	 * @return 固定費一覧情報の検索結果
	 *
	 */
	FixedCostInquiryList findById(SearchQueryUserId userId);
	
	/**
	 *<pre>
	 * 検索条件に該当する固定費情報の検索結果を取得します。
	 *</pre>
	 * @param search 検索対象のユーザID、支出項目コード
	 * @return 固定費一覧情報の検索結果
	 *
	 */
	FixedCostInquiryList findByIdAndSisyutuItemCode(SearchQueryUserIdAndSisyutuItemCode search);
	
	/**
	 *<pre>
	 * 新規の固定費コード発番用にユーザIDに対応する固定費情報が何件あるかを取得します。
	 *</pre>
	 * @param userId 検索対象のユーザID
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	int countById(SearchQueryUserId userId);

	/**
	 *<pre>
	 * 検索条件に該当する固定費情報が何件あるかを取得します。
	 *</pre>
	 * @param search 検索対象のユーザID、支出項目コード
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	int countBySisyutuItemCode(SearchQueryUserIdAndSisyutuItemCode search);
	
}
