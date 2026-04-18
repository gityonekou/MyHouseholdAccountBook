/**
 * 「固定費支払月(支払月)」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/03 : 1.00.00  新規作成
 * 2026/04/05 : 1.01.00  クラス名をFixedCostShiharaiTukiからFixedCostTargetPaymentMonthにリネーム
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
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class FixedCostTargetPaymentMonth {
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
	public static FixedCostTargetPaymentMonth from(String value) {
		return new FixedCostTargetPaymentMonth(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
