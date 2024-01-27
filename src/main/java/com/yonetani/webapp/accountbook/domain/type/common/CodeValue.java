/**
 * 「コード値」項目の値を表すドメインタイプです。
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
 * 「コード値」項目の値を表すドメインタイプです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class CodeValue {
	// コード値
	private final String value;
	
	/**
	 *<pre>
	 * 「コード値」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param codeValue コード値
	 * @return 「コード値」項目の値を表すドメインタイプ
	 *
	 */
	public static CodeValue from(String codeValue) {
		return new CodeValue(codeValue);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
