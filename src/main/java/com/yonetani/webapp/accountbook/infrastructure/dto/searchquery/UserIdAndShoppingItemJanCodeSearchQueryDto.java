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

import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShoppingItemJanCode;

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
	 * 検索条件のドメイン情報をもとにUserIdAndShoppingItemJanCodeSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param search 検索条件(ユーザID、商品JANコード)
	 * @return テーブルの検索条件：ユーザID、商品JANコード
	 *
	 */
	public static UserIdAndShoppingItemJanCodeSearchQueryDto from(SearchQueryUserIdAndShoppingItemJanCode search) {
		return new UserIdAndShoppingItemJanCodeSearchQueryDto(
				// 検索条件:ユーザID
				search.getUserId().getValue(),
				// 検索条件:商品JANコード
				search.getShoppingItemJanCode().getValue());
	}
}
