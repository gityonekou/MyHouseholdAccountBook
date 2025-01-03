/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・店舗区分コード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/16 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.searchquery;

import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopKubunCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・店舗区分コード
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserIdAndShopKubunCodeSearchQueryDto {
	// ユーザID
	private final String userId;
	// 店舗区分コード
	private final String shopKubunCode;
	
	/**
	 *<pre>
	 * 検索条件のドメイン情報をもとにUserIdAndShopKubunCodeSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param search 検索条件(ユーザID、店舗区分コード)
	 * @return テーブルの検索条件：ユーザID、店舗区分コード
	 *
	 */
	public static UserIdAndShopKubunCodeSearchQueryDto from(SearchQueryUserIdAndShopKubunCode search) {
		return new UserIdAndShopKubunCodeSearchQueryDto(
				// 検索条件:ユーザID
				search.getUserId().getValue(),
				// 検索条件:店舗区分コード
				search.getShopKubunCode().getValue());
	}
}
