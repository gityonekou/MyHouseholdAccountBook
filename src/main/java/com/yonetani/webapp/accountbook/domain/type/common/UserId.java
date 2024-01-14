/**
 * 「ユーザID」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/24 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「ユーザID」項目の値を表すドメインタイプです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class UserId {
	// ユーザID
	private final String value;
	
	/**
	 *<pre>
	 * 「ユーザID」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param userId ユーザID
	 * @return 「ユーザID」項目ドメインタイプ
	 *
	 */
	public static UserId from(String userId) {
		return new UserId(userId);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
