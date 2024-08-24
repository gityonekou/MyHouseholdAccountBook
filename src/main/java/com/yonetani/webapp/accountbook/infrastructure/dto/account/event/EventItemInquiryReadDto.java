/**
 * イベント情報一覧の明細情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/20 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.account.event;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * イベント情報一覧の明細情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class EventItemInquiryReadDto {
	// イベントコード
	private final String eventCode;
	// 支出項目名
	private final String sisyutuItemCode;
	// イベント名
	private final String eventName;
	// イベント内容詳細
	private final String eventDetailContext;
	// イベント開始日
	private final LocalDate eventStartDate;
	// イベント終了日
	private final LocalDate eventEndDate;
}
