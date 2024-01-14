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
	 * @param data
	 *
	 */
	void add(Shop data);
}
