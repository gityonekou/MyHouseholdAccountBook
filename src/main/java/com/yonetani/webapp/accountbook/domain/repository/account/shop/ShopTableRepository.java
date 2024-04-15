/**
 * 店舗テーブル:SHOP_TABLEのデータを登録・更新・参照するリポジトリーです
 * 店舗テーブル:SHOP_TABLEのみの参照を行う場合はこのリポジトリーを使います。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/10 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.account.shop;

import com.yonetani.webapp.accountbook.domain.model.account.shop.Shop;
import com.yonetani.webapp.accountbook.domain.model.account.shop.ShopInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopSort;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopSortBetweenAB;

/**
 *<pre>
 * 店舗テーブル:SHOP_TABLEのデータを登録・更新・参照するリポジトリーです
 * 店舗テーブル:SHOP_TABLEのみの参照を行う場合はこのリポジトリーを使います。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public interface ShopTableRepository {
	/**
	 *<pre>
	 * 店舗テーブル情報を新規登録します。
	 *</pre>
	 * @param data 新規追加データ
	 * @return 登録されたデータの件数
	 *
	 */
	int add(Shop data);
	
	/**
	 *<pre>
	 * 店舗テーブル情報を更新します。
	 *</pre>
	 * @param shop 更新データ
	 * @return 更新されたデータの件数
	 *
	 */
	int update(Shop shop);
	
	/**
	 *<pre>
	 * 店舗テーブル情報のうち、店舗表示順の値を更新します。
	 *</pre>
	 * @param shop 更新データ
	 * @return 更新されたデータの件数
	 *
	 */
	int updateShopSort(Shop shop);
	
	/**
	 *<pre>
	 * ユーザIDに対応する店舗情報を取得します。
	 *</pre>
	 * @param userId 検索対象のユーザID
	 * @return 店舗情報のリスト
	 *
	 */
	ShopInquiryList findById(SearchQueryUserId userId);
	
	/**
	 *<pre>
	 * ユーザIDと指定の店舗表示順以降の店舗情報を取得します。
	 *</pre>
	 * @param search 検索対象のユーザIDと店舗表示順
	 * @return 店舗情報のリスト
	 *
	 */
	ShopInquiryList findByIdAndShopSort(SearchQueryUserIdAndShopSort search);
	
	/**
	 *<pre>
	 * ユーザIDと指定の店舗表示順A～店舗表示順Bまでの間の店舗情報を取得します。
	 *</pre>
	 * @param userId 検索対象のユーザIDと店舗表示順
	 * @return 店舗情報のリスト
	 * 
	 *</pre>
	 * @param search 検索対象のユーザIDと店舗表示順A～店舗表示順B
	 * @return 店舗情報のリスト
	 *
	 */
	ShopInquiryList findByIdAndShopSortBetween(SearchQueryUserIdAndShopSortBetweenAB search);
	
	/**
	 *<pre>
	 * ユーザID、店舗コードに対応する店舗情報を取得します。
	 *</pre>
	 * @param search ユーザID、店舗コードの検索条件
	 * @return 店舗情報
	 *
	 */
	Shop findByIdAndShopCode(SearchQueryUserIdAndShopCode search);
	
	/**
	 *<pre>
	 * 新規の店舗コード発番用にユーザIDに対応する店舗情報が何件あるかを取得します。
	 *</pre>
	 * @param userId 検索対象のユーザID
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	int countByIdAndLessThanNineHundred(SearchQueryUserId userId);
	
}
