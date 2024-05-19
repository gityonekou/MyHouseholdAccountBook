/**
 * 商品テーブル:SHOPPING_ITEM_TABLEのデータを登録・更新・参照するリポジトリーです
 * 商品テーブル:SHOPPING_ITEM_TABLEのみの参照を行う場合はこのリポジトリーを使います。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.account.shoppingitem;

import com.yonetani.webapp.accountbook.domain.model.account.shoppingitem.ShoppingItem;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingitem.ShoppingItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryShoppingItemInfoSearchCondition;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShoppingItemCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShoppingItemJanCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemCode;

/**
 *<pre>
 * 商品テーブル:SHOPPING_ITEM_TABLEのデータを登録・更新・参照するリポジトリーです
 * 商品テーブル:SHOPPING_ITEM_TABLEのみの参照を行う場合はこのリポジトリーを使います。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public interface ShoppingItemTableRepository {
	/**
	 *<pre>
	 * 商品テーブル情報を新規登録します。
	 *</pre>
	 * @param data 新規追加データ
	 * @return 登録されたデータの件数
	 *
	 */
	int add(ShoppingItem data);
	
	/**
	 *<pre>
	 * 商品テーブル情報を更新します。
	 * <b>支出項目コードは更新対象外項目なので注意してください</b>
	 *</pre>
	 * @param data 更新データ
	 * @return 更新されたデータの件数
	 *
	 */
	int update(ShoppingItem data);
	
	
	/**
	 *<pre>
	 * ユーザIDと指定の商品コードに対応する商品情報を取得します。
	 *</pre>
	 * @param search 検索対象のユーザID、商品コード
	 * @return 商品情報
	 *
	 */
	ShoppingItem findByIdAndShoppingItemCode(SearchQueryUserIdAndShoppingItemCode search);
	
	/**
	 *<pre>
	 * ユーザIDと指定の支出項目コードに対応する商品情報の検索結果を取得します。
	 *</pre>
	 * @param search 検索対象のユーザID、支出項目コード
	 * @return 商品情報の検索結果
	 *
	 */
	ShoppingItemInquiryList findByIdAndSisyutuItemCode(SearchQueryUserIdAndSisyutuItemCode search);
	
	/**
	 *<pre>
	 * ユーザIDと指定の商品JANコードに対応する商品情報の検索結果を取得します。
	 *</pre>
	 * @param search 検索対象のユーザID、商品JANコード
	 * @return 商品情報の検索結果
	 *
	 */
	ShoppingItemInquiryList findByIdAndShoppingItemJanCode(SearchQueryUserIdAndShoppingItemJanCode search);
	
	/**
	 *<pre>
	 * 指定の商品情報検索条件に一致する商品情報の検索結果を取得します。
	 *</pre>
	 * @param search 商品検索条件
	 * @return 商品情報の検索結果
	 *
	 */
	ShoppingItemInquiryList selectShoppingItemInfoSearchCondition(SearchQueryShoppingItemInfoSearchCondition search);
	
	/**
	 *<pre>
	 * 新規の商品コード発番用にユーザIDに対応する商品情報が何件あるかを取得します。
	 *</pre>
	 * @param userId 検索対象のユーザID
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	int countById(SearchQueryUserId userId);
	
	/**
	 *<pre>
	 * ユーザIDと指定の商品JANコードに対応する商品情報が何件あるかを取得します。
	 *</pre>
	 * @param search 検索対象のユーザID、商品JANコード
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	int countByIdAndShoppingItemJanCode(SearchQueryUserIdAndShoppingItemJanCode search);
	
}
