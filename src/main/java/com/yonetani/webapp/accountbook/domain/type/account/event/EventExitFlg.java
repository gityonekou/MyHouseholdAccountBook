/**
 * 「イベント終了フラグ」項目の値を表すドメインタイプです
 * 値がtrueの時はイベント終了、falseの時は開催中(未終了)を表す値となります。
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
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「イベント終了フラグ」項目の値を表すドメインタイプです
 * 値がtrueの時はイベント終了、falseの時は開催中(未終了)を表す値となります。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class EventExitFlg {
	// イベント終了フラグ
	private final boolean value;
	
	/**
	 *<pre>
	 * 「イベント終了フラグ」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param value イベント終了フラグ trueの時はイベント終了、falseの時は開催中(未終了)
	 * @return 「イベント終了フラグ」項目ドメインタイプ
	 *
	 */
	public static EventExitFlg from(boolean value) {
		return new EventExitFlg(value);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Boolean.toString(value);
	}
	
	/**
	 *<pre>
	 * 「イベント終了フラグ」項目の値を取得します。
	 *</pre>
	 * @return イベント終了フラグ
	 *
	 */
	public boolean getValue() {
		return value;
	}
}
