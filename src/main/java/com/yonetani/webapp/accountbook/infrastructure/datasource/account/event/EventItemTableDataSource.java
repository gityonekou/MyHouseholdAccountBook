/**
 * EventItemTableRepository(イベントテーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.event;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.domain.model.account.event.EventItem;
import com.yonetani.webapp.accountbook.domain.model.account.event.EventItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.event.EventItemInquiryList.EventInquiryItem;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndEventCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.repository.account.event.EventItemTableRepository;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.event.EventItemInquiryReadDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.event.EventItemReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndEventCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndSisyutuItemCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.event.EventItemTableMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 *  EventItemTableRepository(イベントテーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Repository
@RequiredArgsConstructor
public class EventItemTableDataSource implements EventItemTableRepository {

	// マッパー
	private final EventItemTableMapper mapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(EventItem data) {
		// イベント情報をイベントテーブルに出力
		return mapper.insert(EventItemReadWriteDto.from(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(EventItem data) {
		// イベントテーブル:EVENT_ITEM_TABLEを更新
		return mapper.update(EventItemReadWriteDto.from(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int delete(EventItem data) {
		// イベントテーブル:EVENT_ITEM_TABLEから指定のイベント情報を論理削除
		return mapper.delete(EventItemReadWriteDto.from(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventItemInquiryList findById(SearchQueryUserId userId) {
		// 検索結果を取得
		List<EventItemInquiryReadDto> searchResult = mapper.findById(UserIdSearchQueryDto.from(userId));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return EventItemInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return EventItemInquiryList.from(searchResult.stream().map(dto -> createEventItemInquiryItem(dto))
					.collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventItemInquiryList findByIdAndSisyutuItemCode(SearchQueryUserIdAndSisyutuItemCode search) {
		// 検索結果を取得
		List<EventItemInquiryReadDto> searchResult = mapper.findByIdAndSisyutuItemCode(
				UserIdAndSisyutuItemCodeSearchQueryDto.from(search));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return EventItemInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return EventItemInquiryList.from(searchResult.stream().map(dto -> createEventItemInquiryItem(dto))
					.collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventItem findByIdAndEventCode(SearchQueryUserIdAndEventCode search) {
		// 指定条件でイベント情報を取得
		EventItemReadWriteDto result = mapper.findByIdAndEventCode(UserIdAndEventCodeSearchQueryDto.from(search));
		if(result == null) {
			// 対象データなしの場合、nullを返却
			return null;
		} else {
			// 検索結果をドメインに変換して返却
			return createEventItem(result);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countById(SearchQueryUserId userId) {
		// ユーザIDに対応するイベント情報の件数を返します。
		return mapper.countById(UserIdSearchQueryDto.from(userId));
	}
	
	/**
	 *<pre>
	 * 引数で指定したベントテーブル:EVENT_ITEM_TABLE読込・出力情報からイベントテーブル情報ドメインモデルを生成して返します。
	 *</pre>
	 * @param dto ベントテーブル:EVENT_ITEM_TABLE読込・出力情報
	 * @return イベントテーブル情報ドメインモデル
	 *
	 */
	private EventItem createEventItem(EventItemReadWriteDto dto) {
		return EventItem.from(
				// ユーザID
				dto.getUserId(),
				// イベントコード
				dto.getEventCode(),
				// 支出項目コード
				dto.getSisyutuItemCode(),
				// イベント名
				dto.getEventName(),
				// イベント内容詳細
				dto.getEventDetailContext(),
				// イベント開始日
				dto.getEventStartDate(),
				// ベント終了日
				dto.getEventEndDate(),
				// イベント終了フラグ
				dto.isEventExitFlg());
	}
	
	/**
	 *<pre>
	 * 引数で指定したイベント情報一覧の明細情報からイベント一覧明細情報(ドメイン)を生成して返します。
	 *</pre>
	 * @param dto イベント情報一覧の明細情報
	 * @return イベント一覧明細情報(ドメイン)
	 *
	 */
	private EventInquiryItem createEventItemInquiryItem(EventItemInquiryReadDto dto) {
		return EventInquiryItem.from(
				// イベントコード
				dto.getEventCode(),
				// 支出項目コード
				dto.getSisyutuItemCode(),
				// イベント名
				dto.getEventName(),
				// イベント内容詳細
				dto.getEventDetailContext(),
				// イベント開始日
				dto.getEventStartDate(),
				// ベント終了日
				dto.getEventEndDate());
	}
}
