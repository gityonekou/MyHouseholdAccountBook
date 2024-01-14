/**
 * 「年」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/12 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「年」項目の値を表すドメインタイプです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class TargetYear {
	
	// 年(yyyy)
	private final String value;
	
	/**
	 *<pre>
	 * 「年」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param year 年
	 * @return 「年」項目ドメインタイプ
	 *
	 */
	public static TargetYear from(String year) {
		return new TargetYear(year);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
