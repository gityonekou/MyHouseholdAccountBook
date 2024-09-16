/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・対象年
 * ・対象月
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/30 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.searchquery;

import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・対象年
 * ・対象月
 * 
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserIdAndYearMonthSearchQueryDto {
	// ユーザID
	private final String userId;
	// 対象年
	private final String targetYear;
	// 対象月
	private final String targetMonth;
	
	/**
	 *<pre>
	 * 引数のパラメータ値をもとにUserIdAndYearMonthSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param userId 検索条件:ユーザID
	 * @param year 検索条件:対象年
	 * @param month 検索条件:対象月
	 * @return テーブルの検索条件：ユーザID、対象年、対象月
	 *
	 */
	public static UserIdAndYearMonthSearchQueryDto from(String userId, String year, String month) {
		return new UserIdAndYearMonthSearchQueryDto(userId, year, month);
	}
	
	/**
	 *<pre>
	 * 検索条件：ユーザID、対象年月のドメインタイプをもとにUserIdAndYearMonthSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param searchQuery 検索条件：ユーザID、対象年月のドメインタイプ
	 * @return テーブルの検索条件：ユーザID、対象年、対象月
	 *
	 */
	public static UserIdAndYearMonthSearchQueryDto from(SearchQueryUserIdAndYearMonth searchQuery) {
		return UserIdAndYearMonthSearchQueryDto.from(
				// 検索条件:ユーザID
				searchQuery.getUserId().toString(),
				// 検索条件:対象年
				searchQuery.getYearMonth().getYear(),
				// 検索条件:対象月
				searchQuery.getYearMonth().getMonth());
	}
}
