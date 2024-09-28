/**
 * 「固定費支払月(支払月)」項目の値を表すドメインタイプです
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
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「固定費支払月(支払月)」項目の値を表すドメインタイプです
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
public class FixedCostShiharaiTuki {
	// 固定費支払月(支払月)
	private final String value;
	
	/**
	 *<pre>
	 * 「固定費支払月(支払月)」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param value 固定費支払月(支払月)
	 * @return 「固定費支払月(支払月)」項目ドメインタイプ
	 *
	 */
	public static FixedCostShiharaiTuki from(String value) {
		return new FixedCostShiharaiTuki(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
