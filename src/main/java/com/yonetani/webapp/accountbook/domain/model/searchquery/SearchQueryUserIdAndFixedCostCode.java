/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・固定費コード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostCode;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・固定費コード
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class SearchQueryUserIdAndFixedCostCode {
	// ユーザID
	private final UserId userId;
	// 固定費コード
	private final FixedCostCode fixedCostCode;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・固定費コード
	 *</pre>
	 * @param userId ユーザID
	 * @param shoppingItemCode 固定費コード
	 * @return 検索条件(ユーザID, 固定費コード)
	 *
	 */
	public static SearchQueryUserIdAndFixedCostCode from(UserId userId, FixedCostCode fixedCostCode) {
		return new SearchQueryUserIdAndFixedCostCode(userId, fixedCostCode);
	}
}
