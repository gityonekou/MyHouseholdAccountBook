/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・年度(YYYY)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/12 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import com.yonetani.webapp.accountbook.domain.type.common.TargetYear;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・年度(YYYY)
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
public class SearchQueryUserIdAndYear {
	// ユーザID
	private final UserId userId;
	// 年度(YYYY)
	private final TargetYear year;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・年度(YYYY)
	 *</pre>
	 * @param userId ユーザID
	 * @param year 年(YYYY)
	 * @return 検索条件(ユーザID, 年度(YYYY))
	 *
	 */
	public static SearchQueryUserIdAndYear from(UserId userId, TargetYear year) {
		return new SearchQueryUserIdAndYear(userId, year);
	}
}
