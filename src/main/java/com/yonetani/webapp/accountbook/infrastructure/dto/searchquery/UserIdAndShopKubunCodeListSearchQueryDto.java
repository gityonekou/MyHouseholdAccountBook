/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・店舗区分コードのリスト
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/26 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.searchquery;

import java.util.List;
import java.util.stream.Collectors;

import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopKubunCodeList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・店舗区分コードのリスト
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserIdAndShopKubunCodeListSearchQueryDto {
	// ユーザID
	private final String userId;
	// 店舗区分コードのリスト
	private final List<String> shopKubunCodeList;
	
	/**
	 *<pre>
	 * 検索条件のドメイン情報をもとにUserIdAndShopKubunCodeListSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param search 検索条件(ユーザID、店舗区分コードのリスト)
	 * @return テーブルの検索条件：ユーザID、店舗区分コードのリスト
	 *
	 */
	public static UserIdAndShopKubunCodeListSearchQueryDto from(SearchQueryUserIdAndShopKubunCodeList search) {
		return new UserIdAndShopKubunCodeListSearchQueryDto(
				// 検索条件:ユーザID
				search.getUserId().getValue(),
				// 検索条件:固定費支払月のリスト
				search.getShopKubunCodeList().stream().map(model -> model.getValue()).collect(Collectors.toUnmodifiableList()));
	}
}
