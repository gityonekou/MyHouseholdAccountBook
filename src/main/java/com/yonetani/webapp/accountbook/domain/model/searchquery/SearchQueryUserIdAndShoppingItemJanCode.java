/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・商品JANコード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/19 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemJanCode;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・商品JANコード
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
@EqualsAndHashCode
public class SearchQueryUserIdAndShoppingItemJanCode {
	// ユーザID
	private final UserId userId;
	// 商品JANコード
	private final ShoppingItemJanCode shoppingItemJanCode;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・商品JANコード
	 *</pre>
	 * @param userId ユーザID
	 * @param shoppingItemJanCode 商品JANコード
	 * @return 検索条件(ユーザID, 商品JANコード)
	 *
	 */
	public static SearchQueryUserIdAndShoppingItemJanCode from(UserId userId, ShoppingItemJanCode shoppingItemJanCode) {
		return new SearchQueryUserIdAndShoppingItemJanCode(userId, shoppingItemJanCode);
	}
}
