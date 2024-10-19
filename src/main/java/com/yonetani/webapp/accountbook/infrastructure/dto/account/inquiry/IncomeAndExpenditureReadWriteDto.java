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

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditureItem;

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
	 * 収支テーブル情報ドメインモデルをもとにIncomeAndExpenditureReadWriteDtoを生成して返します。
	 *</pre>
	 * @param domain 収支テーブル情報ドメインモデル
	 * @return 収支テーブル:INCOME_AND_EXPENDITURE_TABLE読込・出力情報
	 *
	 */
	public static IncomeAndExpenditureReadWriteDto from(IncomeAndExpenditureItem domain) {
		return new IncomeAndExpenditureReadWriteDto(
				// ユーザID
				domain.getUserId().getValue(),
				// 対象年
				domain.getTargetYear().getValue(),
				// 対象月
				domain.getTargetMonth().getValue(),
				// 収入金額
				domain.getSyuunyuuKingaku().getValue(),
				// 支出予定金額
				domain.getSisyutuYoteiKingaku().getValue(),
				// 支出金額
				domain.getSisyutuKingaku().getValue(),
				// 収支金額
				domain.getSyuusiKingaku().getValue());
	}
}
