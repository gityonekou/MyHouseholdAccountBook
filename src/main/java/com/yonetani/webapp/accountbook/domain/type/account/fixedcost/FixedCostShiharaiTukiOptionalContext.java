/**
 * 「固定費支払月任意詳細」項目の値を表すドメインタイプです
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
 * 「固定費支払月任意詳細」項目の値を表すドメインタイプです
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
public class FixedCostShiharaiTukiOptionalContext {
	// 固定費支払月任意詳細
	private final String value;
	
	/**
	 *<pre>
	 * 「固定費支払月任意詳細」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param optionalContext 固定費支払月任意詳細
	 * @return 「固定費支払月任意詳細」項目ドメインタイプ
	 *
	 */
	public static FixedCostShiharaiTukiOptionalContext from(String optionalContext) {
		return new FixedCostShiharaiTukiOptionalContext(optionalContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
