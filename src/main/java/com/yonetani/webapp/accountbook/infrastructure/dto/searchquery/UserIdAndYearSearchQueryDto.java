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
	 * 引数のパラメータ値をもとにUserIdAndYearSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param userId 検索条件:ユーザID
	 * @param targetYear 検索条件:対象年
	 * @return テーブルの検索条件：ユーザID、対象年
	 *
	 */
	public static UserIdAndYearSearchQueryDto from(String userId, String targetYear) {
		return new UserIdAndYearSearchQueryDto(userId, targetYear);
	}
	
}
