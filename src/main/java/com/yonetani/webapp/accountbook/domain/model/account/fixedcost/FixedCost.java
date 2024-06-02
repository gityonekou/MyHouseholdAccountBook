/**
 * 【説明を入力してください】
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/27 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.fixedcost;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 【説明を入力してください】
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FixedCost {
	private final String fixedCostItemCode;
	private final String fixedCostItemName;
	public static FixedCost from(String fixedCostItemCode, String fixedCostItemName) {
		return new FixedCost(fixedCostItemCode, fixedCostItemName);
	}
}
