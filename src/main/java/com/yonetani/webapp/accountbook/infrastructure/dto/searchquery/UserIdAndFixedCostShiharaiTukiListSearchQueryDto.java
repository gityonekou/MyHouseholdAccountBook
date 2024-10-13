/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・固定費支払月のリスト
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/06 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.searchquery;

import java.util.List;
import java.util.stream.Collectors;

import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndFixedCostShiharaiTukiList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・固定費支払月のリスト
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserIdAndFixedCostShiharaiTukiListSearchQueryDto {
	// ユーザID
	private final String userId;
	// 固定費支払月のリスト
	private final List<String> fixedCostShiharaiTukiList;
	
	/**
	 *<pre>
	 * 検索条件のドメイン情報をもとにUserIdAndFixedCostShiharaiTukiListSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param search 検索条件(ユーザID、固定費支払月のリスト)
	 * @return テーブルの検索条件：ユーザID、固定費支払月のリスト
	 *
	 */
	public static UserIdAndFixedCostShiharaiTukiListSearchQueryDto from(SearchQueryUserIdAndFixedCostShiharaiTukiList search) {
		return new UserIdAndFixedCostShiharaiTukiListSearchQueryDto(
				// 検索条件:ユーザID
				search.getUserId().getValue(),
				// 検索条件:固定費支払月のリスト
				search.getFixedCostShiharaiTukiList().stream().map(
						model -> model.getValue()).collect(Collectors.toUnmodifiableList()));
	}
}
