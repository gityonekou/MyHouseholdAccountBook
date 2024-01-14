/**
 * 収支テーブル:INCOME_AND_EXPENSE_TABLEテーブルの各項目のDTOです。
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
 * 収支テーブル:INCOME_AND_EXPENSE_TABLEテーブルの各項目のDTOです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class IncomeAndExpenseReadDto {
	// ユーザID
	private final String userId;
	// 対象年
	private final String targetYear;
	// 対象月
	private final String targetMonth;
	// 収入金額
	private final BigDecimal incomeKingaku;
	// 支出金額
	private final BigDecimal expenseKingaku;
	// 支出予定金額
	private final BigDecimal expenseYoteiKingaku ;
	// 収支
	private final BigDecimal syuusiKingaku;
	// 衣類住居設備予定金額
	private final BigDecimal iruiJyuukyoYoteiKingaku;
	// 飲食日用品予定金額
	private final BigDecimal insyokuNitiyouhinYoteiKingaku;
	// 趣味娯楽予定金額
	private final BigDecimal syumiGotakuYoteiKingaku;
	
}
