/**
 * 支出項目テーブル(BASE):SISYUTU_ITEM_BASE_TABLEを登録/全件取得するリポジトリーです
 * 支出項目テーブル:SISYUTU_ITEM_TABLEのベースデータとなります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.adminmenu;

import com.yonetani.webapp.accountbook.domain.model.adminmenu.SisyutuItemBase;
import com.yonetani.webapp.accountbook.domain.model.adminmenu.SisyutuItemBaseList;

/**
 *<pre>
 * 支出項目テーブル(BASE):SISYUTU_ITEM_BASE_TABLEを登録/全件取得するリポジトリーです
 * 支出項目テーブル:SISYUTU_ITEM_TABLEのベースデータとなります。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public interface SisyutuItemBaseTableRepository {
	
	/**
	 *<pre>
	 * 支出項目テーブル(BASE)情報を新規登録します。
	 *</pre>
	 * @param data 追加する支出項目テーブル(BASE)情報
	 * @return 追加されたデータの件数
	 *
	 */
	int add(SisyutuItemBase data);
	
	/**
	 *<pre>
	 * 支出項目テーブル(BASE)情報を全件取得します。
	 *</pre>
	 * @return 支出項目テーブル(BASE)情報
	 *
	 */
	SisyutuItemBaseList findAll();

	/**
	 *<pre>
	 * 支出項目テーブル(BASE)情報の全件数を取得します。
	 *</pre>
	 * @return 支出項目テーブル(BASE)情報の全件数
	 *
	 */
	int countAll();
}
