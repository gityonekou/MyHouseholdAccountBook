/**
 * DomainTestDataFactoryのテストクラスです。
 *
 */
package com.yonetani.webapp.accountbook.domain.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.domain.type.common.BalanceAmount;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.RegularIncomeAmount;
import com.yonetani.webapp.accountbook.domain.type.common.IncomeDate;
import com.yonetani.webapp.accountbook.domain.type.common.PaymentDate;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

/**
 *<pre>
 * DomainTestDataFactoryのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
class DomainTestDataFactoryTest {

	@Test
	@DisplayName("正常系：IncomeAmountを生成")
	void testCreateIncomeAmount() {
		// 実行
		RegularIncomeAmount result = DomainTestDataFactory.createIncomeAmount(100000);

		// 検証
		assertNotNull(result);
		assertEquals(new BigDecimal("100000.00"), result.getValue());
	}

	@Test
	@DisplayName("正常系：ExpenditureAmountを生成")
	void testCreateExpenditureAmount() {
		// 実行
		ExpenditureAmount result = DomainTestDataFactory.createExpenditureAmount(80000);

		// 検証
		assertNotNull(result);
		assertEquals(new BigDecimal("80000.00"), result.getValue());
	}

	@Test
	@DisplayName("正常系：BalanceAmountを生成")
	void testCreateBalanceAmount() {
		// 実行
		BalanceAmount result = DomainTestDataFactory.createBalanceAmount(20000);

		// 検証
		assertNotNull(result);
		assertEquals(new BigDecimal("20000.00"), result.getValue());
	}

	@Test
	@DisplayName("正常系：PaymentDateを年月日から生成")
	void testCreatePaymentDate_YearMonthDay() {
		// 実行
		PaymentDate result = DomainTestDataFactory.createPaymentDate(2025, 11, 29);

		// 検証
		assertNotNull(result);
		assertEquals(LocalDate.of(2025, 11, 29), result.getValue());
	}

	@Test
	@DisplayName("正常系：PaymentDateを文字列から生成")
	void testCreatePaymentDate_String() {
		// 実行
		PaymentDate result = DomainTestDataFactory.createPaymentDate("20251129");

		// 検証
		assertNotNull(result);
		assertEquals(LocalDate.of(2025, 11, 29), result.getValue());
	}

	@Test
	@DisplayName("正常系：IncomeDateを年月日から生成")
	void testCreateIncomeDate_YearMonthDay() {
		// 実行
		IncomeDate result = DomainTestDataFactory.createIncomeDate(2025, 11, 29);

		// 検証
		assertNotNull(result);
		assertEquals(LocalDate.of(2025, 11, 29), result.getValue());
	}

	@Test
	@DisplayName("正常系：IncomeDateを文字列から生成")
	void testCreateIncomeDate_String() {
		// 実行
		IncomeDate result = DomainTestDataFactory.createIncomeDate("20251129");

		// 検証
		assertNotNull(result);
		assertEquals(LocalDate.of(2025, 11, 29), result.getValue());
	}

	@Test
	@DisplayName("正常系：TargetYearMonthを文字列から生成")
	void testCreateTargetYearMonth_String() {
		// 実行
		TargetYearMonth result = DomainTestDataFactory.createTargetYearMonth("202511");

		// 検証
		assertNotNull(result);
		assertEquals("202511", result.getValue());
	}

	@Test
	@DisplayName("正常系：TargetYearMonthを年月から生成")
	void testCreateTargetYearMonth_YearMonth() {
		// 実行
		TargetYearMonth result = DomainTestDataFactory.createTargetYearMonth(2025, 11);

		// 検証
		assertNotNull(result);
		assertEquals("202511", result.getValue());
	}

	@Test
	@DisplayName("正常系：デフォルトのTargetYearMonthを生成")
	void testCreateDefaultTargetYearMonth() {
		// 実行
		TargetYearMonth result = DomainTestDataFactory.createDefaultTargetYearMonth();

		// 検証
		assertNotNull(result);
		assertEquals("202511", result.getValue());
	}

	@Test
	@DisplayName("正常系：UserIdを生成")
	void testCreateUserId() {
		// 実行
		UserId result = DomainTestDataFactory.createUserId("user001");

		// 検証
		assertNotNull(result);
		assertEquals("user001", result.getValue());
	}

	@Test
	@DisplayName("正常系：デフォルトのUserIdを生成")
	void testCreateDefaultUserId() {
		// 実行
		UserId result = DomainTestDataFactory.createDefaultUserId();

		// 検証
		assertNotNull(result);
		assertEquals("testUser001", result.getValue());
	}

	@Test
	@DisplayName("正常系：標準的な収入金額を生成")
	void testCreateStandardIncome() {
		// 実行
		RegularIncomeAmount result = DomainTestDataFactory.createStandardIncome();

		// 検証
		assertNotNull(result);
		assertEquals(new BigDecimal("100000.00"), result.getValue());
	}

	@Test
	@DisplayName("正常系：標準的な支出金額を生成")
	void testCreateStandardExpenditure() {
		// 実行
		ExpenditureAmount result = DomainTestDataFactory.createStandardExpenditure();

		// 検証
		assertNotNull(result);
		assertEquals(new BigDecimal("80000.00"), result.getValue());
	}

	@Test
	@DisplayName("正常系：標準的な収支金額を生成")
	void testCreateStandardBalance() {
		// 実行
		BalanceAmount result = DomainTestDataFactory.createStandardBalance();

		// 検証
		assertNotNull(result);
		assertEquals(new BigDecimal("20000.00"), result.getValue());
	}

	@Test
	@DisplayName("正常系：今日の日付でPaymentDateを生成")
	void testCreateTodayPaymentDate() {
		// 実行
		PaymentDate result = DomainTestDataFactory.createTodayPaymentDate();

		// 検証
		assertNotNull(result);
		assertEquals(LocalDate.now(), result.getValue());
	}

	@Test
	@DisplayName("正常系：今日の日付でIncomeDateを生成")
	void testCreateTodayIncomeDate() {
		// 実行
		IncomeDate result = DomainTestDataFactory.createTodayIncomeDate();

		// 検証
		assertNotNull(result);
		assertEquals(LocalDate.now(), result.getValue());
	}

	@Test
	@DisplayName("異常系：インスタンス化不可")
	void testConstructor_ThrowsException() {
		// 実行 & 検証
		assertThrows(java.lang.reflect.InvocationTargetException.class, () -> {
			// リフレクションでコンストラクタを呼び出そうとする
			java.lang.reflect.Constructor<DomainTestDataFactory> constructor =
				DomainTestDataFactory.class.getDeclaredConstructor();
			constructor.setAccessible(true);
			constructor.newInstance();
		});
	}
}
