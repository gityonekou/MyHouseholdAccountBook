/**
 * 「ユーザ名」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/11/05 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「ユーザ名」項目の値を表すドメインタイプです。
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
public class UserName {
	// ユーザ名
	private final String value;
	
	/**
	 *<pre>
	 * 「ユーザ名」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param userName ユーザ名
	 * @return 「ユーザ名」項目ドメインタイプ
	 *
	 */
	public static UserName from(String userName) {
		return new UserName(userName);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
