/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・商品コード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/30 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.searchquery;

import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShoppingItemCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * テーブルの検索条件が以下の場合に使用するTDOです。
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
public class UserIdAndShoppingItemCodeSearchQueryDto {
	// ユーザID
	private final String userId;
	// 商品コード
	private final String shoppingItemCode;
	
	/**
	 *<pre>
	 * 検索条件のドメイン情報をもとにUserIdAndShoppingItemCodeSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param search 検索条件(ユーザID、商品コード)
	 * @return テーブルの検索条件：ユーザID、商品コード
	 *
	 */
	public static UserIdAndShoppingItemCodeSearchQueryDto from(SearchQueryUserIdAndShoppingItemCode search) {
		return new UserIdAndShoppingItemCodeSearchQueryDto(
				// 検索条件:ユーザID
				search.getUserId().getValue(),
				// 検索条件:商品コード
				search.getShoppingItemCode().getValue());
	}
}
