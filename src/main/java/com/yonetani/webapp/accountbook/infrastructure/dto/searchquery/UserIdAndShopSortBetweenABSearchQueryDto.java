/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・店舗表示順A
 * ・店舗表示順B
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/02/10 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.searchquery;

import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopSortBetweenAB;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・店舗表示順A
 * ・店舗表示順B
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserIdAndShopSortBetweenABSearchQueryDto {
	// ユーザID
	private final String userId;
	// 店舗表示順A
	private final String shopSortA;
	// 店舗表示順B
	private final String shopSortB;
	/**
	 *<pre>
	 * 検索条件のドメイン情報をもとにUserIdAndShopSortABSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param search 検索条件(ユーザID、店舗表示順A、店舗表示順B)
	 * @return テーブルの検索条件：ユーザID、店舗表示順A、店舗表示順B
	 *
	 */
	public static UserIdAndShopSortBetweenABSearchQueryDto from(SearchQueryUserIdAndShopSortBetweenAB search) {
		return new UserIdAndShopSortBetweenABSearchQueryDto(
				// 検索条件:ユーザID
				search.getUserId().getValue(),
				// 検索条件:店舗表示順A
				search.getShopSortA().getValue(),
				// 検索条件:店舗表示順B
				search.getShopSortB().getValue());
	}
}
