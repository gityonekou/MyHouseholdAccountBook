/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・商品コード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/30 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemCode;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・商品コード
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
public class SearchQueryUserIdAndShoppingItemCode {
	// ユーザID
	private final UserId userId;
	// 商品コード
	private final ShoppingItemCode shoppingItemCode;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・商品コード
	 *</pre>
	 * @param userId ユーザID
	 * @param shoppingItemCode 商品コード
	 * @return 検索条件(ユーザID, 商品コード)
	 *
	 */
	public static SearchQueryUserIdAndShoppingItemCode from(UserId userId, ShoppingItemCode shoppingItemCode) {
		return new SearchQueryUserIdAndShoppingItemCode(userId, shoppingItemCode);
	}
}
