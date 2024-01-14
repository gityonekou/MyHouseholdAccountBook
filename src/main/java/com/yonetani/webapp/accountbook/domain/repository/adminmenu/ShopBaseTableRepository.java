/**
 * 店舗テーブル(BASE):SHOP_BASE_TABLEを登録/全件取得するリポジトリーです
 * 店舗テーブル:SHOP_TABLEのベースデータとなります
 * 
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.adminmenu;

import com.yonetani.webapp.accountbook.domain.model.adminmenu.ShopBase;
import com.yonetani.webapp.accountbook.domain.model.adminmenu.ShopBaseList;

/**
 *<pre>
 * 店舗テーブル(BASE):SHOP_BASE_TABLEを登録/全件取得するリポジトリーです
 * 店舗テーブル:SHOP_TABLEのベースデータとなります
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public interface ShopBaseTableRepository {
	/**
	 *<pre>
	 * 店舗テーブル(BASE)情報を新規登録します。
	 *</pre>
	 * @param data
	 *
	 */
	void add(ShopBase data);
	
	/**
	 *<pre>
	 * 店舗テーブル(BASE)情報を全件取得します。
	 *</pre>
	 * @param data
	 *
	 */
	ShopBaseList findAll();

	/**
	 *<pre>
	 * 店舗テーブル(BASE)情報の全件数を取得します。
	 *</pre>
	 * @return
	 *
	 */
	int countAll();
}
