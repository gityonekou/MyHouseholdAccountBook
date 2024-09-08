/**
 * 「イベントコード」項目の値を表すドメインタイプです
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
 * 「イベントコード」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class EventCode {
	// イベントコード
	private final String value;
	
	/**
	 *<pre>
	 * 「イベントコード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param code イベントコード
	 * @return 「イベントコード」項目ドメインタイプ
	 *
	 */
	public static EventCode from(String code) {
		return new EventCode(code);
	}
	
	/**
	 *<pre>
	 * 新規発番するイベントコードの値(数値)をもとに、「イベントコード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param count 新規発番するイベントコードの値(数値)
	 * @return 「イベントコード」項目ドメインタイプ
	 *
	 */
	public static EventCode from(int count) {
		return new EventCode(String.format("%04d", count));
	}
	
	/**
	 *<pre>
	 * 新規発番するイベントコードの値を取得します。
	 *</pre>
	 * @param count 新規発番するイベントコードの値(数値)
	 * @return イベントコードの値
	 *
	 */
	public static String getNewCode(int count) {
		return EventCode.from(count).toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
