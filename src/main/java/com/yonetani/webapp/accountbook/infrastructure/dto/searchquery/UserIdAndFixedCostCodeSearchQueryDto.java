/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・固定費コード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/08 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.searchquery;

import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndFixedCostCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・固定費コード
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserIdAndFixedCostCodeSearchQueryDto {
	// ユーザID
	private final String userId;
	// 固定費コード
	private final String fixedCostCode;
	
	/**
	 *<pre>
	 * 検索条件のドメイン情報をもとにUserIdAndFixedCostCodeSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param search 検索条件(ユーザID、固定費コード)
	 * @return テーブルの検索条件：ユーザID、固定費コード
	 *
	 */
	public static UserIdAndFixedCostCodeSearchQueryDto from(SearchQueryUserIdAndFixedCostCode search) {
		return new UserIdAndFixedCostCodeSearchQueryDto(
				// 検索条件:ユーザID
				search.getUserId().getValue(),
				// 検索条件:固定費コード
				search.getFixedCostCode().getValue());
	}
}
