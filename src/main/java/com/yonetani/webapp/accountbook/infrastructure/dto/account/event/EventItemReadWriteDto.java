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
	 * 引数のパラメータ値をもとにEventItemReadWriteDtoを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param eventCode イベントコード
	 * @param sisyutuItemCode 支出項目コード
	 * @param eventName イベント名
	 * @param eventDetailContext イベント内容詳細
	 * @param eventStartDate イベント開始日
	 * @param eventEndDate イベント終了日
	 * @param eventExitFlg イベント終了フラグ
	 * @return イベントテーブル:EVENT_ITEM_TABLE読込・出力情報
	 *
	 */
	public static EventItemReadWriteDto from(String userId, String eventCode, String sisyutuItemCode, String eventName,
			String eventDetailContext, LocalDate eventStartDate, LocalDate eventEndDate, boolean eventExitFlg) {
		return new EventItemReadWriteDto(userId, eventCode, sisyutuItemCode, eventName, eventDetailContext,
				eventStartDate, eventEndDate, eventExitFlg);
	}
}
