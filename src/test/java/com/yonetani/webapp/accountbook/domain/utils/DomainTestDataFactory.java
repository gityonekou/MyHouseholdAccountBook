/**
 * ドメイン層のテストデータを生成するファクトリクラスです。
 * テストコードで値オブジェクトを簡単に生成するためのユーティリティメソッドを提供します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.utils;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.yonetani.webapp.accountbook.domain.type.common.BalanceAmount;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.RegularIncomeAmount;
import com.yonetani.webapp.accountbook.domain.type.common.IncomeDate;
import com.yonetani.webapp.accountbook.domain.type.common.PaymentDate;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

/**
 *<pre>
 * ドメイン層のテストデータを生成するファクトリクラスです。
 *
 * [責務]
 * ・テストで使用する値オブジェクトの簡単な生成
 * ・テストデータの一貫性を保証
 * ・テストコードの可読性向上
 *
 * [使用例]
 * <code>
 * // 金額の生成
 * IncomeAmount income = DomainTestDataFactory.createIncomeAmount(100000);
 * ExpenditureAmount expenditure = DomainTestDataFactory.createExpenditureAmount(80000);
 *
 * // 日付の生成
 * PaymentDate paymentDate = DomainTestDataFactory.createPaymentDate(2025, 11, 29);
 *
 * // IDの生成
 * UserId userId = DomainTestDataFactory.createUserId("user001");
 * </code>
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
public class DomainTestDataFactory {

	// デフォルト値
	private static final String DEFAULT_USER_ID = "testUser001";
	private static final String DEFAULT_YEAR_MONTH = "202511";

	/**
	 * コンストラクタをprivateにしてインスタンス化を防止
	 */
	private DomainTestDataFactory() {
		throw new AssertionError("ユーティリティクラスはインスタンス化できません");
	}

	// ========================================
	// 金額系値オブジェクトの生成
	// ========================================

	/**
	 *<pre>
	 * RegularIncomeAmountを生成します。
	 *</pre>
	 * @param amount 金額（整数値）
	 * @return RegularIncomeAmount
	 */
	public static RegularIncomeAmount createIncomeAmount(long amount) {
		return RegularIncomeAmount.from(toBigDecimal(amount));
	}

	/**
	 *<pre>
	 * ExpenditureAmountを生成します。
	 *</pre>
	 * @param amount 金額（整数値）
	 * @return ExpenditureAmount
	 */
	public static ExpenditureAmount createExpenditureAmount(long amount) {
		return ExpenditureAmount.from(toBigDecimal(amount));
	}

	/**
	 *<pre>
	 * BalanceAmountを生成します。
	 *</pre>
	 * @param amount 金額（整数値）
	 * @return BalanceAmount
	 */
	public static BalanceAmount createBalanceAmount(long amount) {
		return BalanceAmount.from(toBigDecimal(amount));
	}

	/**
	 *<pre>
	 * 整数値をBigDecimal（スケール2）に変換します。
	 *</pre>
	 * @param amount 整数値
	 * @return BigDecimal（スケール2）
	 */
	private static BigDecimal toBigDecimal(long amount) {
		return new BigDecimal(amount).setScale(2);
	}

	// ========================================
	// 日付系値オブジェクトの生成
	// ========================================

	/**
	 *<pre>
	 * PaymentDateを生成します。
	 *</pre>
	 * @param year 年
	 * @param month 月
	 * @param day 日
	 * @return PaymentDate
	 */
	public static PaymentDate createPaymentDate(int year, int month, int day) {
		return PaymentDate.from(LocalDate.of(year, month, day));
	}

	/**
	 *<pre>
	 * PaymentDateを文字列から生成します。
	 *</pre>
	 * @param dateString 日付文字列（yyyyMMdd形式）
	 * @return PaymentDate
	 */
	public static PaymentDate createPaymentDate(String dateString) {
		return PaymentDate.from(dateString);
	}

	/**
	 *<pre>
	 * IncomeDateを生成します。
	 *</pre>
	 * @param year 年
	 * @param month 月
	 * @param day 日
	 * @return IncomeDate
	 */
	public static IncomeDate createIncomeDate(int year, int month, int day) {
		return IncomeDate.from(LocalDate.of(year, month, day));
	}

	/**
	 *<pre>
	 * IncomeDateを文字列から生成します。
	 *</pre>
	 * @param dateString 日付文字列（yyyyMMdd形式）
	 * @return IncomeDate
	 */
	public static IncomeDate createIncomeDate(String dateString) {
		return IncomeDate.from(dateString);
	}

	/**
	 *<pre>
	 * TargetYearMonthを生成します。
	 *</pre>
	 * @param yearMonth 対象年月（YYYYMM形式）
	 * @return TargetYearMonth
	 */
	public static TargetYearMonth createTargetYearMonth(String yearMonth) {
		return TargetYearMonth.from(yearMonth);
	}

	/**
	 *<pre>
	 * TargetYearMonthを年と月から生成します。
	 *</pre>
	 * @param year 年
	 * @param month 月
	 * @return TargetYearMonth
	 */
	public static TargetYearMonth createTargetYearMonth(int year, int month) {
		String yearMonth = String.format("%04d%02d", year, month);
		return TargetYearMonth.from(yearMonth);
	}

	/**
	 *<pre>
	 * デフォルトのTargetYearMonthを生成します。
	 *</pre>
	 * @return TargetYearMonth（202511）
	 */
	public static TargetYearMonth createDefaultTargetYearMonth() {
		return TargetYearMonth.from(DEFAULT_YEAR_MONTH);
	}

	// ========================================
	// ID系値オブジェクトの生成
	// ========================================

	/**
	 *<pre>
	 * UserIdを生成します。
	 *</pre>
	 * @param userId ユーザーID
	 * @return UserId
	 */
	public static UserId createUserId(String userId) {
		return UserId.from(userId);
	}

	/**
	 *<pre>
	 * デフォルトのUserIdを生成します。
	 *</pre>
	 * @return UserId（testUser001）
	 */
	public static UserId createDefaultUserId() {
		return UserId.from(DEFAULT_USER_ID);
	}

	// ========================================
	// テストシナリオ用の複合データ生成
	// ========================================

	/**
	 *<pre>
	 * テスト用の標準的な収入金額を生成します。
	 *</pre>
	 * @return RegularIncomeAmount（100,000円）
	 */
	public static RegularIncomeAmount createStandardIncome() {
		return createIncomeAmount(100000);
	}

	/**
	 *<pre>
	 * テスト用の標準的な支出金額を生成します。
	 *</pre>
	 * @return ExpenditureAmount（80,000円）
	 */
	public static ExpenditureAmount createStandardExpenditure() {
		return createExpenditureAmount(80000);
	}

	/**
	 *<pre>
	 * テスト用の標準的な収支金額を生成します。
	 *</pre>
	 * @return BalanceAmount（20,000円）
	 */
	public static BalanceAmount createStandardBalance() {
		return createBalanceAmount(20000);
	}

	/**
	 *<pre>
	 * 今日の日付でPaymentDateを生成します。
	 *</pre>
	 * @return PaymentDate（今日）
	 */
	public static PaymentDate createTodayPaymentDate() {
		return PaymentDate.from(LocalDate.now());
	}

	/**
	 *<pre>
	 * 今日の日付でIncomeDateを生成します。
	 *</pre>
	 * @return IncomeDate（今日）
	 */
	public static IncomeDate createTodayIncomeDate() {
		return IncomeDate.from(LocalDate.now());
	}
}
