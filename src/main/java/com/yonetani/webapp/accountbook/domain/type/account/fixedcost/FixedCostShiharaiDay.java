/**
 * 「固定費支払日(支払日)」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/13 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.fixedcost;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「固定費支払日(支払日)」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class FixedCostShiharaiDay {
	// 固定費支払日(支払日)
	private final String value;
	
	/**
	 *<pre>
	 * 「固定費支払日(支払日)」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param value 固定費支払日(支払日)
	 * @return 「固定費支払日(支払日)」項目ドメインタイプ
	 *
	 */
	public static FixedCostShiharaiDay from(String value) {
		return new FixedCostShiharaiDay(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
