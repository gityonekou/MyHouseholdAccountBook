/**
 * 「イベント開始日」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.event;

import java.time.LocalDate;

import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「イベント開始日」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class EventStartDate {
	// イベント開始日
	private final LocalDate value;
	
	/**
	 *<pre>
	 * 「イベント開始日」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param date イベント開始日
	 * @return 「イベント開始日」項目ドメインタイプ
	 *
	 */
	public static EventStartDate from(LocalDate date) {
		
		return new EventStartDate(date);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return DomainCommonUtils.formatyyyySPMMSPdd(value);
	}
}
