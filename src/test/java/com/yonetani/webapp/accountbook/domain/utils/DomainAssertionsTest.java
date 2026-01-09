/**
 * DomainAssertionsのテストクラスです。
 *
 */
package com.yonetani.webapp.accountbook.domain.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.domain.type.common.RegularIncomeAmount;
import com.yonetani.webapp.accountbook.domain.type.common.PaymentDate;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

/**
 *<pre>
 * DomainAssertionsのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
class DomainAssertionsTest {

	@Test
	@DisplayName("正常系：Money値の検証 - 整数値")
	void testAssertMoneyEquals_Long() {
		// 準備
		RegularIncomeAmount income = DomainTestDataFactory.createIncomeAmount(100000);

		// 実行 & 検証（例外がスローされないことを確認）
		assertDoesNotThrow(() ->
			DomainAssertions.assertMoneyEquals(100000, income)
		);
	}

	@Test
	@DisplayName("正常系：Money値の検証 - BigDecimal")
	void testAssertMoneyEquals_BigDecimal() {
		// 準備
		RegularIncomeAmount income = DomainTestDataFactory.createIncomeAmount(100000);

		// 実行 & 検証
		assertDoesNotThrow(() ->
			DomainAssertions.assertMoneyEquals(new BigDecimal("100000.00"), income)
		);
	}

	@Test
	@DisplayName("正常系：Money値の検証 - Money同士")
	void testAssertMoneyEquals_Money() {
		// 準備
		RegularIncomeAmount income1 = DomainTestDataFactory.createIncomeAmount(100000);
		RegularIncomeAmount income2 = DomainTestDataFactory.createIncomeAmount(100000);

		// 実行 & 検証
		assertDoesNotThrow(() ->
			DomainAssertions.assertMoneyEquals(income1, income2)
		);
	}

	@Test
	@DisplayName("異常系：Money値の検証 - 金額不一致")
	void testAssertMoneyEquals_Mismatch() {
		// 準備
		RegularIncomeAmount income = DomainTestDataFactory.createIncomeAmount(100000);

		// 実行 & 検証
		assertThrows(AssertionError.class, () ->
			DomainAssertions.assertMoneyEquals(80000, income)
		);
	}

	@Test
	@DisplayName("正常系：Money値が0であることを検証")
	void testAssertMoneyIsZero() {
		// 準備
		RegularIncomeAmount income = DomainTestDataFactory.createIncomeAmount(0);

		// 実行 & 検証
		assertDoesNotThrow(() ->
			DomainAssertions.assertMoneyIsZero(income)
		);
	}

	@Test
	@DisplayName("異常系：Money値が0でない")
	void testAssertMoneyIsZero_NotZero() {
		// 準備
		RegularIncomeAmount income = DomainTestDataFactory.createIncomeAmount(100);

		// 実行 & 検証
		assertThrows(AssertionError.class, () ->
			DomainAssertions.assertMoneyIsZero(income)
		);
	}

	@Test
	@DisplayName("正常系：Money値が正の値であることを検証")
	void testAssertMoneyIsPositive() {
		// 準備
		RegularIncomeAmount income = DomainTestDataFactory.createIncomeAmount(100000);

		// 実行 & 検証
		assertDoesNotThrow(() ->
			DomainAssertions.assertMoneyIsPositive(income)
		);
	}

	@Test
	@DisplayName("正常系：日付の検証 - 年月日")
	void testAssertDateEquals_YearMonthDay() {
		// 準備
		PaymentDate date = DomainTestDataFactory.createPaymentDate(2025, 11, 29);

		// 実行 & 検証
		assertDoesNotThrow(() ->
			DomainAssertions.assertDateEquals(2025, 11, 29, date)
		);
	}

	@Test
	@DisplayName("異常系：日付の検証 - 日付不一致")
	void testAssertDateEquals_Mismatch() {
		// 準備
		PaymentDate date = DomainTestDataFactory.createPaymentDate(2025, 11, 29);

		// 実行 & 検証
		assertThrows(AssertionError.class, () ->
			DomainAssertions.assertDateEquals(2025, 11, 30, date)
		);
	}

	@Test
	@DisplayName("正常系：日付の前後関係の検証 - isBefore")
	void testAssertDateBefore() {
		// 準備
		PaymentDate date1 = DomainTestDataFactory.createPaymentDate(2025, 11, 28);
		PaymentDate date2 = DomainTestDataFactory.createPaymentDate(2025, 11, 29);

		// 実行 & 検証
		assertDoesNotThrow(() ->
			DomainAssertions.assertDateBefore(date2, date1)
		);
	}

	@Test
	@DisplayName("正常系：日付の前後関係の検証 - isAfter")
	void testAssertDateAfter() {
		// 準備
		PaymentDate date1 = DomainTestDataFactory.createPaymentDate(2025, 11, 30);
		PaymentDate date2 = DomainTestDataFactory.createPaymentDate(2025, 11, 29);

		// 実行 & 検証
		assertDoesNotThrow(() ->
			DomainAssertions.assertDateAfter(date2, date1)
		);
	}

	@Test
	@DisplayName("正常系：IDの検証 - 文字列")
	void testAssertIdEquals_String() {
		// 準備
		UserId userId = DomainTestDataFactory.createUserId("user001");

		// 実行 & 検証
		assertDoesNotThrow(() ->
			DomainAssertions.assertIdEquals("user001", userId)
		);
	}

	@Test
	@DisplayName("異常系：IDの検証 - ID不一致")
	void testAssertIdEquals_Mismatch() {
		// 準備
		UserId userId = DomainTestDataFactory.createUserId("user001");

		// 実行 & 検証
		assertThrows(AssertionError.class, () ->
			DomainAssertions.assertIdEquals("user002", userId)
		);
	}

	@Test
	@DisplayName("正常系：IDの検証 - Identifier同士")
	void testAssertIdEquals_Identifier() {
		// 準備
		UserId userId1 = DomainTestDataFactory.createUserId("user001");
		UserId userId2 = DomainTestDataFactory.createUserId("user001");

		// 実行 & 検証
		assertDoesNotThrow(() ->
			DomainAssertions.assertIdEquals(userId1, userId2)
		);
	}

	@Test
	@DisplayName("異常系：インスタンス化不可")
	void testConstructor_ThrowsException() {
		// 実行 & 検証
		assertThrows(java.lang.reflect.InvocationTargetException.class, () -> {
			// リフレクションでコンストラクタを呼び出そうとする
			java.lang.reflect.Constructor<DomainAssertions> constructor =
				DomainAssertions.class.getDeclaredConstructor();
			constructor.setAccessible(true);
			constructor.newInstance();
		});
	}
}
