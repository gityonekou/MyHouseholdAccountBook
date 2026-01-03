/**
 * 収支テーブル情報の値を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/18 : 1.00.00  新規作成
 * 2025/12/28 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.inquiry;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ExpectedExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ExpectedExpenditureTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.WithdrawingAmount;
import com.yonetani.webapp.accountbook.domain.type.common.BalanceAmount;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.IncomeAmount;
import com.yonetani.webapp.accountbook.domain.type.common.TargetMonth;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYear;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 収支テーブル情報の値を表すドメインモデルです
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
@EqualsAndHashCode
public class IncomeAndExpenditureItem {
	
	// ユーザID
	private final UserId userId;
	// 対象年
	private final TargetYear targetYear;
	// 対象月
	private final TargetMonth targetMonth;
	// 収入金額(積立金取崩金額以外の収入金額)
	private final IncomeAmount incomeAmount;
	// 積立金取崩金額
	private final WithdrawingAmount withdrawingAmount;
	// 支出予定金額
	private final ExpectedExpenditureAmount expectedExpenditureAmount;
	// 支出金額
	private final ExpenditureAmount expenditureAmount;
	// 収支金額
	private final BalanceAmount balanceAmount;
	
	/**
	 *<pre>
	 * 引数の値から収支テーブル情報のドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYear 対象年
	 * @param targetMonth 対象月
	 * @param incomeAmount 収入金額(積立金取崩金額以外の収入金額)
	 * @param withdrawingAmount 積立金取崩金額
	 * @param sisyutuYoteiKingaku 支出予定金額
	 * @param expenditureAmount 支出金額
	 * @param balanceAmount 収支金額
	 * @return 収支テーブル情報を表すドメインモデル
	 *
	 */
	public static IncomeAndExpenditureItem from(
			String userId,
			String targetYear,
			String targetMonth,
			BigDecimal incomeAmount,
			BigDecimal withdrawingAmount,
			BigDecimal sisyutuYoteiKingaku,
			BigDecimal expenditureAmount,
			BigDecimal balanceAmount) {
		return new IncomeAndExpenditureItem(
				UserId.from(userId),
				TargetYear.from(targetYear),
				TargetMonth.from(targetMonth),
				IncomeAmount.from(incomeAmount),
				WithdrawingAmount.from(withdrawingAmount),
				ExpectedExpenditureAmount.from(sisyutuYoteiKingaku),
				ExpenditureAmount.from(expenditureAmount),
				BalanceAmount.from(balanceAmount));
	}
	
	/**
	 *<pre>
	 * 値が空となるドメインモデルを生成して返します。
	 *</pre>
	 * @return
	 *
	 */
	public static IncomeAndExpenditureItem fromEmpty() {
		return new IncomeAndExpenditureItem(null, null, null, null, null, null, null, null);
	}
	
	/**
	 *<pre>
	 * 引数の情報から対象月の収支テーブル情報を新規追加する場合の収支テーブル情報(ドメイン)を生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param yearMonth 対象年月(ドメイン)
	 * @param incomeAmount 対象月の収入金額(積立金取崩金額以外の収入金額)
	 * @param withdrawingAmount 対象月の積立金取崩金額
	 * @param expectedExpenditureAmount 対象月の支出予定金額
	 * @param expenditureAmount 対象月の支出金額
	 * @return 収支テーブル情報(ドメイン)
	 *
	 */
	public static IncomeAndExpenditureItem createAddTypeIncomeAndExpenditureItem(
			UserId userId,
			TargetYearMonth yearMonth,
			IncomeAmount incomeAmount,
			WithdrawingAmount withdrawingAmount,
			ExpectedExpenditureAmount expectedExpenditureAmount,
			ExpenditureAmount expenditureAmount) {
		
		// 収支金額 = 収入金額(積立金取崩金額以外の収入金額) + 積立金取崩金額 - 支出金額
		BalanceAmount balance = BalanceAmount.calculate(incomeAmount, withdrawingAmount, expenditureAmount);
		
		// 支出テーブル情報(ドメイン)を生成して返却
		return IncomeAndExpenditureItem.from(
				// ユーザID
				userId.getValue(),
				//対象年
				yearMonth.getYear(),
				// 対象月
				yearMonth.getMonth(),
				// 収入金額(積立金取崩金額以外の収入金額)
				incomeAmount.getValue(),
				// 積立金取崩金額
				withdrawingAmount.getValue(),
				// 支出予定金額
				expectedExpenditureAmount.getValue(),
				// 支出金額
				expenditureAmount.getValue(),
				// 収支金額
				balance.getValue());
	}
	
	/**
	 *<pre>
	 * 引数の情報から収支テーブルを更新する場合の収支テーブル情報(ドメイン)を生成して返します。
	 * 
	 * 注意：支出予定金額は新規登録以降は更新不可となるため、引数には含めていません。
	 * (支出予定金額は0円で設定され、DB更新時に該当項目を更新しません)
	 *</pre>
	 * @param userId ユーザID
	 * @param yearMonth 対象年月(ドメイン)
	 * @param incomeAmount 対象月の収入金額(積立金取崩金額以外の収入金額)
	 * @param withdrawingAmount 対象月の積立金取崩金額
	 * @param expenditureAmount 対象月の支出金額
	 * @return 収支テーブル情報(ドメイン)
	 *
	 */
	public static IncomeAndExpenditureItem createUpdTypeIncomeAndExpenditureItem(
			UserId userId,
			TargetYearMonth yearMonth,
			IncomeAmount incomeAmount,
			WithdrawingAmount withdrawingAmount,
			ExpenditureAmount expenditureAmount) {
		
		// 収支金額 = 収入金額(積立金取崩金額以外の収入金額) + 積立金取崩金額 - 支出金額
		BalanceAmount balance = BalanceAmount.calculate(incomeAmount, withdrawingAmount, expenditureAmount);
		
		// 支出テーブル情報(ドメイン)を生成して返却
		return IncomeAndExpenditureItem.from(
				// ユーザID
				userId.getValue(),
				//対象年
				yearMonth.getYear(),
				// 対象月
				yearMonth.getMonth(),
				// 収入金額
				incomeAmount.getValue(),
				// 積立金取崩金額
				withdrawingAmount.getValue(),
				// 支出予定金額
				ExpectedExpenditureTotalAmount.ZERO.getValue(),
				// 支出金額
				expenditureAmount.getValue(),
				// 収支金額
				balance.getValue());
	}
	
	/**
	 *<pre>
	 * 検索結果が設定されているかどうかを判定します。
	 *</pre>
	 * @return 空の場合はtrue、値が設定されている場合はfalse
	 *
	 */
	public boolean isEmpty() {
		if(userId == null) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 *<pre>
	 * 現在の収支テーブル情報(ドメイン)の支出金額の値に引数で指定した支出金額を加算した結果を返します。
	 *</pre>
	 * @param addValue　加算する支出金額の値
	 * @return 支出金額を加算した収支テーブル情報(ドメイン)
	 *
	 */
	public IncomeAndExpenditureItem addSisyutuKingaku(ExpenditureAmount addValue) {

		// 新しい支出金額
		ExpenditureAmount updExpenditureAmount = expenditureAmount.add(addValue);
		
		// 収支金額 = 収入金額(積立金取崩金額以外の収入金額) + 積立金取崩金額 - 支出金額
		BalanceAmount balance = BalanceAmount.calculate(incomeAmount, withdrawingAmount, updExpenditureAmount);
		
		// 支出テーブル情報(ドメイン)を生成して返却
		return IncomeAndExpenditureItem.from(
				// ユーザID
				userId.getValue(),
				//対象年
				targetYear.getValue(),
				// 対象月
				targetMonth.getValue(),
				// 収入金額
				incomeAmount.getValue(),
				// 積立金取崩金額
				withdrawingAmount.getValue(),
				// 支出予定金額
				expectedExpenditureAmount.getValue(),
				// 支出金額
				updExpenditureAmount.getValue(),
				// 収支金額
				balance.getValue());
	}
	
	/**
	 *<pre>
	 * 現在の収支テーブル情報(ドメイン)の支出金額の値に引数で指定した支出金額を減算した結果を返します。
	 *</pre>
	 * @param subtractValue　減算する支出金額の値
	 * @return 支出金額を減算した収支テーブル情報(ドメイン)
	 *
	 */
	public IncomeAndExpenditureItem subtractSisyutuKingaku(ExpenditureAmount subtractValue) {

		// 新しい支出金額
		ExpenditureAmount updExpenditureAmount = expenditureAmount.subtract(subtractValue);
		
		// 収支金額 = 収入金額(積立金取崩金額以外の収入金額) + 積立金取崩金額 - 支出金額
		BalanceAmount balance = BalanceAmount.calculate(incomeAmount, withdrawingAmount, updExpenditureAmount);
		
		// 支出テーブル情報(ドメイン)を生成して返却
		return IncomeAndExpenditureItem.from(
				// ユーザID
				userId.getValue(),
				//対象年
				targetYear.getValue(),
				// 対象月
				targetMonth.getValue(),
				// 収入金額
				incomeAmount.getValue(),
				// 積立金取崩金額
				withdrawingAmount.getValue(),
				// 支出予定金額
				expectedExpenditureAmount.getValue(),
				// 支出金額
				updExpenditureAmount.getValue(),
				// 収支金額
				balance.getValue());
	}
	
	
}
