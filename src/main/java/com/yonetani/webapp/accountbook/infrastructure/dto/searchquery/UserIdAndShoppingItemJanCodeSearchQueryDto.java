/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・商品JANコード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/19 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.searchquery;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * テーブルの検索条件が以下の場合に使用するTDOです。
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
public class UserIdAndShoppingItemJanCodeSearchQueryDto {
	// ユーザID
	private final String userId;
	// 商品JANコード
	private final String shoppingItemJanCode;
	
	/**
	 *<pre>
	 * 引数のパラメータ値をもとにUserIdAndShoppingItemJanCodeSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param shoppingItemJanCode 商品JANコード
	 * @return テーブルの検索条件：ユーザID、商品JANコード
	 *
	 */
	public static UserIdAndShoppingItemJanCodeSearchQueryDto from(String userId, String shoppingItemJanCode) {
		return new UserIdAndShoppingItemJanCodeSearchQueryDto(userId, shoppingItemJanCode);
	}
}
