/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・対象年
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/14 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.searchquery;

import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYear;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・対象年
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserIdAndYearSearchQueryDto {
	// ユーザID
	private final String userId;
	// 対象年
	private final String targetYear;
	
	/**
	 *<pre>
	 * 検索条件のドメイン情報をもとにUserIdAndYearSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param search 検索条件(ユーザID、対象年)
	 * @return テーブルの検索条件：ユーザID、対象年
	 *
	 */
	public static UserIdAndYearSearchQueryDto from(SearchQueryUserIdAndYear search) {
		return new UserIdAndYearSearchQueryDto(
				// 検索条件:ユーザID
				search.getUserId().getValue(),
				// 検索条件:対象年
				search.getYear().getValue());
	}
	
}
