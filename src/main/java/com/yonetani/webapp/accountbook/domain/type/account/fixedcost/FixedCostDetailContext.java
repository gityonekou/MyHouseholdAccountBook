/**
 * 「固定費内容詳細(支払内容詳細)」項目の値を表すドメインタイプです
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
 * 「固定費内容詳細(支払内容詳細)」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class FixedCostDetailContext {
	// 固定費内容詳細(支払内容詳細)
	private final String value;
	
	/**
	 *<pre>
	 * 「固定費内容詳細(支払内容詳細)」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param detailContext 固定費内容詳細(支払内容詳細)
	 * @return 「固定費内容詳細(支払内容詳細)」項目ドメインタイプ
	 *
	 */
	public static FixedCostDetailContext from(String detailContext) {
		return new FixedCostDetailContext(detailContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
