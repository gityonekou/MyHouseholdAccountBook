/**
 * 「イベント内容詳細」項目(任意入力項目)の値を表すドメインタイプです
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
 * 「イベント内容詳細」項目(任意入力項目)の値を表すドメインタイプです
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
public class EventDetailContext {
	// イベント内容詳細(任意入力項目)
	private final String value;
	
	/**
	 *<pre>
	 * 「イベント内容詳細」項目(任意入力項目)の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param value イベント内容詳細(任意入力項目)
	 * @return 「イベント内容詳細」項目(任意入力項目)ドメインタイプ
	 *
	 */
	public static EventDetailContext from(String value) {
		return new EventDetailContext(value);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
