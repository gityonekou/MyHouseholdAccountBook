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

import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryShoppingItemInfoSearchCondition;

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
	 * 検索条件のドメイン情報をもとにShoppingItemInfoSearchConditionSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param search 商品情報検索条件
	 * @return テーブルの検索条件：ユーザID、商品区分名、商品名、会社名、商品JANコード
	 *
	 */
	public static ShoppingItemInfoSearchConditionSearchQueryDto from(SearchQueryShoppingItemInfoSearchCondition search) {
		return new ShoppingItemInfoSearchConditionSearchQueryDto(
				// 検索条件:ユーザID
				search.getUserId().getValue(),
				// 検索条件:商品区分名
				(search.getShoppingItemKubunName() != null) ? search.getShoppingItemKubunName().getValue() : null,
				// 検索条件:商品名
				(search.getShoppingItemName() != null) ? search.getShoppingItemName().getValue() : null,
				// 検索条件:会社名
				(search.getCompanyName() != null) ? search.getCompanyName().getValue() : null,
				// 検索条件:商品JANコード
				(search.getShoppingItemJanCode() != null) ? search.getShoppingItemJanCode().getValue() : null);
	}
}
