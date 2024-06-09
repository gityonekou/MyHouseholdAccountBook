/**
 * 「固定費名(支払名)」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.fixedcost;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「固定費名(支払名)」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class FixedCostName {
	// 固定費名(支払名)
	private final String value;
	
	/**
	 *<pre>
	 * 「固定費名(支払名)」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param name 固定費名(支払名)
	 * @return 「固定費名(支払名)」項目ドメインタイプ
	 *
	 */
	public static FixedCostName from(String name) {
		return new FixedCostName(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
