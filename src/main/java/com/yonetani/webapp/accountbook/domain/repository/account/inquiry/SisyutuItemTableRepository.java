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
}
