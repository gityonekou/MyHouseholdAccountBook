/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・年月度(YYYYMM)
 * ・支出コード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/10/18 : 1.00.00  新規作成
 * 2026/03/20 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCode;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・年月度(YYYYMM)
 * ・支出コード
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
public class SearchQueryUserIdAndYearMonthAndExpenditureCode {
	// ユーザID
	private final UserId userId;
	// 年月(YYYYMM)
	private final TargetYearMonth yearMonth;
	// 支出コード
	private final ExpenditureCode expenditureCode;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・年月度(YYYYMM)
	 * ・支出コード
	 *</pre>
	 * @param userId ユーザID
	 * @param yearMonth 年月(YYYYMM)
	 * @param expenditureCode 支出コード
	 * @return 検索条件(ユーザID, 年月度(YYYYMM), 支出コード)
	 *
	 */
	public static SearchQueryUserIdAndYearMonthAndExpenditureCode from(UserId userId, TargetYearMonth yearMonth, ExpenditureCode expenditureCode) {
		return new SearchQueryUserIdAndYearMonthAndExpenditureCode(userId, yearMonth, expenditureCode);
	}
}
