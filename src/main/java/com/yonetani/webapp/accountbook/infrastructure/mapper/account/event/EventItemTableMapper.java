/**
 * イベントテーブル:EVENT_ITEM_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.account.event;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yonetani.webapp.accountbook.infrastructure.dto.account.event.EventItemInquiryReadDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.event.EventItemReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndEventCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdSearchQueryDto;

/**
 *<pre>
 * イベントテーブル:EVENT_ITEM_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Mapper
public interface EventItemTableMapper {
	
	/**
	 *<pre>
	 * イベントテーブル:EVENT_ITEM_TABLEにデータを追加します。
	 *</pre>
	 * @param writeDto イベントテーブル:EVENT_ITEM_TABLE出力情報
	 * @return イベントテーブルに追加されたデータ件数
	 *
	 */
	@Insert("sql/account/event/EventItemTableInsertSql01.sql")
	public int insert(@Param("dto") EventItemReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 指定のイベント情報でイベントテーブル:EVENT_ITEM_TABLEを更新します。
	 *</pre>
	 * @param writeDto イベントテーブル:EVENT_ITEM_TABLE出力情報
	 * @return イベントテーブルのデータ更新件数
	 *
	 */
	@Update("sql/account/event/EventItemTableUpdateSql01.sql")
	public int update(@Param("dto") EventItemReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 指定のイベント情報をイベントテーブル:EVENT_ITEM_TABLEから論理削除します。
	 * イベントテーブル:EVENT_ITEM_TABLEの削除フラグ項目(EVENT_EXIT_FLG)にtureの値を設定することで
	 * 論理的に削除します
	 * 
	 *</pre>
	 * @param writeDto イベントテーブル:EVENT_ITEM_TABLE出力情報
	 * @return イベントテーブルのデータ更新件数
	 *
	 */
	@Update("sql/account/event/EventItemTableUpdateSql02.sql")
	public int delete(@Param("dto") EventItemReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 指定のユーザID、イベントコードを条件にイベントテーブル:EVENT_ITEM_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID、イベントコード
	 * @return イベントテーブル:EVENT_ITEM_TABLE参照結果
	 *
	 */
	@Select("sql/account/event/EventItemTableSelectSql01.sql")
	public EventItemReadWriteDto findByIdAndEventCode(@Param("dto") UserIdAndEventCodeSearchQueryDto search);
	
	/**
	 *<pre>
	 * イベントテーブル:EVENT_ITEM_TABLE、支出項目テーブル:SISYUTU_ITEM_TABLEから指定のユーザIDを条件に
	 * イベント情報を参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID
	 * @return イベント情報参照結果のリスト
	 *
	 */
	@Select("sql/account/event/EventItemInquirySelectSql01.sql")
	public List<EventItemInquiryReadDto> findById(@Param("dto") UserIdSearchQueryDto search);
	
	/**
	 *<pre>
	 * 指定のユーザIDに対応するイベント情報が何件あるかを取得します。
	 *</pre>
	 * @param userId 検索条件:ユーザID
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	@Select("sql/account/event/EventItemTableCountSql01.sql")
	public int countById(@Param("dto") UserIdSearchQueryDto userId);
}
