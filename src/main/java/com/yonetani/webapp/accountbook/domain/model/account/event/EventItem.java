/**
 * イベントテーブル情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/18 : 1.00.00  新規作成
 * 2025/12/28 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.event;

import java.time.LocalDate;

import com.yonetani.webapp.accountbook.domain.type.account.event.EventCode;
import com.yonetani.webapp.accountbook.domain.type.account.event.EventDate;
import com.yonetani.webapp.accountbook.domain.type.account.event.EventDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.event.EventExitFlg;
import com.yonetani.webapp.accountbook.domain.type.account.event.EventName;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * イベントテーブル情報を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
public class EventItem {
	// ユーザID
	private final UserId userId;
	// イベントコード
	private final EventCode eventCode;
	// 支出項目コード
	private final SisyutuItemCode sisyutuItemCode;
	// イベント名
	private final EventName eventName;
	// イベント内容詳細
	private final EventDetailContext eventDetailContext;
	// イベント開始日
	private final EventDate eventStartDate;
	// イベント終了日
	private final EventDate eventEndDate;
	// イベント終了フラグ
	private final EventExitFlg eventExitFlg;
	
	/**
	 *<pre>
	 * 引数の値からイベントテーブル情報を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param eventCode イベントコード
	 * @param sisyutuItemCode 支出項目コード
	 * @param eventName イベント名
	 * @param eventDetailContext イベント内容詳細
	 * @param eventStartDate イベント開始日
	 * @param eventEndDate イベント終了日
	 * @param eventExitFlg イベント終了フラグ
	 * @return イベントテーブル情報を表すドメインモデル
	 *
	 */
	public static EventItem from(String userId, String eventCode, String sisyutuItemCode, String eventName, String eventDetailContext,
			LocalDate eventStartDate, LocalDate eventEndDate, boolean eventExitFlg) {
		return new EventItem(
				UserId.from(userId),
				EventCode.from(eventCode),
				SisyutuItemCode.from(sisyutuItemCode),
				EventName.from(eventName),
				EventDetailContext.from(eventDetailContext),
				EventDate.from(eventStartDate),
				EventDate.from(eventEndDate),
				EventExitFlg.from(eventExitFlg)
				);
	}
}
