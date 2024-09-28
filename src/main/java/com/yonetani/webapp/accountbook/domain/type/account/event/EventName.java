/**
 * 「イベント名」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.event;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「イベント名」項目の値を表すドメインタイプです
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
public class EventName {
	// イベント名
	private final String value;
	
	/**
	 *<pre>
	 * 「イベント名」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param name イベント名
	 * @return 「イベント名」項目ドメインタイプ
	 *
	 */
	public static EventName from(String name) {
		return new EventName(name);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
