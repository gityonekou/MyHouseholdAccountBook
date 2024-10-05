/**
 * イベントテーブル:EVENT_ITEM_TABLE読込・出力情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/19 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.account.event;

import java.time.LocalDate;

import com.yonetani.webapp.accountbook.domain.model.account.event.EventItem;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * イベントテーブル:EVENT_ITEM_TABLE読込・出力情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class EventItemReadWriteDto {
	// ユーザID
	private final String userId;
	// イベントコード
	private final String eventCode;
	// 支出項目コード
	private final String sisyutuItemCode;
	// イベント名
	private final String eventName;
	// イベント内容詳細
	private final String eventDetailContext;
	// イベント開始日
	private final LocalDate eventStartDate;
	// イベント終了日
	private final LocalDate eventEndDate;
	// イベント終了フラグ
	private final boolean eventExitFlg;
	
	/**
	 *<pre>
	 * 引数で指定したイベントテーブル情報ドメインモデルからベントテーブル:EVENT_ITEM_TABLE読込・出力情報を生成して返します。
	 *</pre>
	 * @param dto イベントテーブル情報ドメインモデル
	 * @return ベントテーブル:EVENT_ITEM_TABLE読込・出力情報
	 *
	 */
	public static EventItemReadWriteDto from(EventItem domain) {
		return new EventItemReadWriteDto(
				// ユーザID
				domain.getUserId().getValue(),
				// イベントコード
				domain.getEventCode().getValue(),
				// 支出項目コード
				domain.getSisyutuItemCode().getValue(),
				// イベント名
				domain.getEventName().getValue(),
				// イベント内容詳細
				domain.getEventDetailContext().getValue(),
				// イベント開始日
				domain.getEventStartDate().getValue(),
				// ベント終了日
				domain.getEventEndDate().getValue(),
				// イベント終了フラグ
				domain.getEventExitFlg().getValue());
	}
}
