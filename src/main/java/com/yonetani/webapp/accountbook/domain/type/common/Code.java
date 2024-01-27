/**
 * 「コード」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/21 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「コード」項目の値を表すドメインタイプです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Code {
	// コード
	private final String value;
	
	/**
	 *<pre>
	 * 「コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param code コード
	 * @return 「コード」項目の値を表すドメインタイプ
	 *
	 */
	public static Code from(String code) {
		return new Code(code);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
