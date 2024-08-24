/**
 * イベントテーブル:EVENT_ITEM_TABLEのデータを登録・更新・参照するリポジトリーです
 * イベントテーブル:EVENT_ITEM_TABLEのみの参照を行う場合はこのリポジトリーを使います。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.account.event;

import com.yonetani.webapp.accountbook.domain.model.account.event.EventItem;
import com.yonetani.webapp.accountbook.domain.model.account.event.EventItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndEventCode;

/**
 *<pre>
 * イベントテーブル:EVENT_ITEM_TABLEのデータを登録・更新・参照するリポジトリーです
 * イベントテーブル:EVENT_ITEM_TABLEのみの参照を行う場合はこのリポジトリーを使います。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public interface EventItemTableRepository {
	
	/**
	 *<pre>
	 * イベントテーブル情報を新規登録します。
	 *</pre>
	 * @param data 新規追加データ
	 * @return 登録されたデータの件数
	 *
	 */
	int add(EventItem data);
	
	/**
	 *<pre>
	 * イベントテーブル情報を更新します。
	 *</pre>
	 * @param data 更新データ
	 * @return 更新されたデータの件数
	 *
	 */
	int update(EventItem data);
	
	/**
	 *<pre>
	 * 指定したイベント情報を論理削除します。
	 * イベントテーブル:EVENT_ITEM_TABLEの削除フラグ項目(EVENT_EXIT_FLG)にtureの値を設定することで
	 * 論理的に削除します。
	 * 
	 *</pre>
	 * @param data 更新データ
	 * @return 更新されたデータの件数
	 *
	 */
	int delete(EventItem data);
	
	/**
	 *<pre>
	 * ユーザIDに対応するイベント情報を取得します。
	 *</pre>
	 * @param userId 検索対象のユーザID
	 * @return イベント情報のリスト
	 *
	 */
	EventItemInquiryList findById(SearchQueryUserId userId);
	
	/**
	 *<pre>
	 * ユーザID、イベントコードに対応するイベント情報を取得します。
	 *</pre>
	 * @param search ユーザID、イベントコードの検索条件
	 * @return イベント情報
	 *
	 */
	EventItem findByIdAndEventCode(SearchQueryUserIdAndEventCode search);
	
	/**
	 *<pre>
	 * 新規のイベントコード発番用にユーザIDに対応するイベント情報が何件あるかを取得します。
	 *</pre>
	 * @param userId 検索対象のユーザID
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	int countById(SearchQueryUserId userId);
}
