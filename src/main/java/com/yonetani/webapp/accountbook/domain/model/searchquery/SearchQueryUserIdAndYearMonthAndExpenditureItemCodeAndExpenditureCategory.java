/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・年月度(YYYYMM)
 * ・支出項目コード
 * ・支出区分
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/01/01 : 1.00.00  新規作成
 * 2026/03/20 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCategory;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ExpenditureItemCode;
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
 * ・支出項目コード
 * ・支出区分
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
public class SearchQueryUserIdAndYearMonthAndExpenditureItemCodeAndExpenditureCategory {
	// ユーザID
	private final UserId userId;
	// 年月(YYYYMM)
	private final TargetYearMonth yearMonth;
	// 支出項目コード
	private final ExpenditureItemCode expenditureItemCode;
	// 支出区分
	private final ExpenditureCategory expenditureCategory;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・年月度(YYYYMM)
	 * ・支出項目コード
	 * ・支出区分
	 *</pre>
	 * @param userId ユーザID
	 * @param yearMonth 年月(YYYYMM)
	 * @param expenditureItemCode 支出項目コード
	 * @param expenditureCategory 支出区分
	 * @return 検索条件(ユーザID, 年月度(YYYYMM), 支出項目コード, 支出区分)
	 *
	 */
	public static SearchQueryUserIdAndYearMonthAndExpenditureItemCodeAndExpenditureCategory from(UserId userId, TargetYearMonth yearMonth, ExpenditureItemCode expenditureItemCode, ExpenditureCategory expenditureCategory) {
		return new SearchQueryUserIdAndYearMonthAndExpenditureItemCodeAndExpenditureCategory(userId, yearMonth, expenditureItemCode, expenditureCategory);
	}
}
