/**
 * 収支テーブル:INCOME_AND_EXPENDITURE_TABLEテーブルの各項目のDTOです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/14 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 収支テーブル:INCOME_AND_EXPENDITURE_TABLEテーブルの各項目のDTOです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class IncomeAndExpenditureReadWriteDto {
	// ユーザID
	private final String userId;
	// 対象年
	private final String targetYear;
	// 対象月
	private final String targetMonth;
	// 収入金額
	private final BigDecimal incomeKingaku;
	// 支出予定金額
	private final BigDecimal expenditureEstimateKingaku;
	// 支出金額
	private final BigDecimal expenditureKingaku;
	// 収支金額
	private final BigDecimal incomeAndExpenditureKingaku;
	
	/**
	 *<pre>
	 * 引数のパラメータ値をもとにIncomeAndExpenditureReadWriteDtoを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYear 対象年
	 * @param targetMonth 対象月
	 * @param incomeKingaku 収入金額
	 * @param expenditureEstimateKingaku 支出予定金額
	 * @param expenditureKingaku 支出金額
	 * @param incomeAndExpenditureKingaku 収支金額
	 * @return 収支テーブル:INCOME_AND_EXPENDITURE_TABLE出力情報
	 *
	 */
	public static IncomeAndExpenditureReadWriteDto from(
			String userId,
			String targetYear,
			String targetMonth,
			BigDecimal incomeKingaku,
			BigDecimal expenditureEstimateKingaku,
			BigDecimal expenditureKingaku,
			BigDecimal incomeAndExpenditureKingaku) {
		return new IncomeAndExpenditureReadWriteDto(
				userId,
				targetYear,
				targetMonth,
				incomeKingaku,
				expenditureEstimateKingaku,
				expenditureKingaku,
				incomeAndExpenditureKingaku);
	}
}
