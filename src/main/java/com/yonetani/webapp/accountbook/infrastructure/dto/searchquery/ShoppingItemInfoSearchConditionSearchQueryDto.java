/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・商品区分名
 * ・商品名
 * ・会社名
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/12 : 1.00.00  新規作成
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
 * ・商品区分名
 * ・商品名
 * ・会社名
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ShoppingItemInfoSearchConditionSearchQueryDto {
	// ユーザID
	private final String userId;
	// 商品区分名
	private final String shoppingItemKubunName;
	// 商品名
	private final String shoppingItemName;
	// 会社名
	private final String companyName;
	// 商品JANコード
	private final String shoppingItemJanCode;
	
	/**
	 *<pre>
	 * 引数のパラメータ値をもとにShoppingItemInfoSearchConditionSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param shoppingItemKubunName 商品区分名
	 * @param shoppingItemName 商品名
	 * @param companyName 会社名
	 * @param shoppingItemJanCode 商品JANコード
	 * @return テーブルの検索条件：ユーザID、商品区分名、商品名、会社名、商品JANコード
	 *
	 */
	public static ShoppingItemInfoSearchConditionSearchQueryDto from(String userId, String shoppingItemKubunName, 
			String shoppingItemName, String companyName, String shoppingItemJanCode) {
		return new ShoppingItemInfoSearchConditionSearchQueryDto(userId, shoppingItemKubunName, 
				shoppingItemName, companyName, shoppingItemJanCode);
	}
}
