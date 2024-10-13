/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・対象年
 * ・対象月
 * ・支出項目コード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/10/13 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.searchquery;

import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonthAndSisyutuItemCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・対象年
 * ・対象月
 * ・支出項目コード
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserIdAndYearMonthAndSisyutuItemCodeSearchQueryDto {
	// ユーザID
	private final String userId;
	// 対象年
	private final String targetYear;
	// 対象月
	private final String targetMonth;
	// 支出項目コード
	private final String sisyutuItemCode;
	
	/**
	 *<pre>
	 * 検索条件のドメイン情報をもとにUserIdAndYearMonthAndSisyutuItemCodeSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param search 検索条件(ユーザID、対象年月、支出項目コード)
	 * @return テーブルの検索条件：ユーザID、対象年、対象月、支出項目コード
	 *
	 */
	public static UserIdAndYearMonthAndSisyutuItemCodeSearchQueryDto from(SearchQueryUserIdAndYearMonthAndSisyutuItemCode search) {
		return new UserIdAndYearMonthAndSisyutuItemCodeSearchQueryDto(
				// 検索条件:ユーザID
				search.getUserId().toString(),
				// 検索条件:対象年
				search.getYearMonth().getYear(),
				// 検索条件:対象月
				search.getYearMonth().getMonth(),
				// 検索条件:支出項目コード
				search.getSisyutuItemCode().getValue());
	}
}
