/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・店舗表示順
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/02/06 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.searchquery;

import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopSort;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・店舗表示順
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserIdAndShopSortSearchQueryDto {
	// ユーザID
	private final String userId;
	// 店舗表示順
	private final String shopSort;
	
	/**
	 *<pre>
	 * 検索条件のドメイン情報をもとにUserIdAndShopSortSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param search 検索条件(ユーザID、店舗表示順)
	 * @return テーブルの検索条件：ユーザID、店舗表示順
	 *
	 */
	public static UserIdAndShopSortSearchQueryDto from(SearchQueryUserIdAndShopSort search) {
		return new UserIdAndShopSortSearchQueryDto(
				// 検索条件:ユーザID
				search.getUserId().getValue(),
				// 検索条件:店舗表示順
				search.getShopSort().getValue());
	}
}
