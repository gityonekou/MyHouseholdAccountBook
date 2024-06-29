/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・年月度(YYYYMM)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/24 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・年月度(YYYYMM)
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class SearchQueryUserIdAndYearMonth {
	
	// ユーザID
	private final UserId userId;
	// 年月(YYYYMM)
	private final TargetYearMonth yearMonth;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・年月度(YYYYMM)
	 *</pre>
	 * @param userId ユーザID
	 * @param yearMonth 年月(YYYYMM)
	 * @return 検索条件(ユーザID, 年月度(YYYYMM))
	 *
	 */
	public static SearchQueryUserIdAndYearMonth from(String userId, String yearMonth) {
		return new SearchQueryUserIdAndYearMonth(
				UserId.from(userId),
				TargetYearMonth.from(yearMonth));
	}
}
