/**
 * IncomeDateのテストクラスです。
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * IncomeDateのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
class IncomeDateTest {

	@Test
	@DisplayName("正常系：LocalDateから生成")
	void testFrom_LocalDate_Success() {
		// 実行
		LocalDate date = LocalDate.of(2025, 11, 29);
		IncomeDate result = IncomeDate.from(date);

		// 検証
		assertNotNull(result);
		assertEquals(date, result.getValue());
		assertEquals("2025/11/29", result.toString());
	}

	@Test
	@DisplayName("異常系：null値からの生成")
	void testFrom_LocalDate_Null() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> IncomeDate.from((LocalDate) null)
		);
		assertTrue(exception.getMessage().contains("収入日"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("正常系：文字列（yyyyMMdd）から生成")
	void testFrom_String_Success() {
		// 実行
		IncomeDate result = IncomeDate.from("20251129");

		// 検証
		assertNotNull(result);
		assertEquals(LocalDate.of(2025, 11, 29), result.getValue());
		assertEquals("20251129", result.toStringFormatyyyyMMdd());
		assertEquals("2025/11/29", result.toStringFormatyyyySPMMSPdd());
	}

	@Test
	@DisplayName("異常系：null文字列からの生成")
	void testFrom_String_Null() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> IncomeDate.from((String) null)
		);
		assertTrue(exception.getMessage().contains("収入日"));
	}

	@Test
	@DisplayName("異常系：空文字列からの生成")
	void testFrom_String_Empty() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> IncomeDate.from("")
		);
		assertTrue(exception.getMessage().contains("収入日"));
	}

	@Test
	@DisplayName("異常系：8桁以外の文字列からの生成")
	void testFrom_String_InvalidLength() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> IncomeDate.from("2025112")
		);
		assertTrue(exception.getMessage().contains("収入日"));
	}

	@Test
	@DisplayName("異常系：不正な日付文字列からの生成")
	void testFrom_String_InvalidDate() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> IncomeDate.from("20251332") // 13月32日
		);
		assertTrue(exception.getMessage().contains("収入日"));
	}

	@Test
	@DisplayName("正常系：年月+日から生成")
	void testFrom_YearMonthAndDay_Success() {
		// 実行
		IncomeDate result = IncomeDate.from("202511", "29");

		// 検証
		assertNotNull(result);
		assertEquals(LocalDate.of(2025, 11, 29), result.getValue());
	}

	@Test
	@DisplayName("異常系：年月がnullで生成")
	void testFrom_YearMonthAndDay_YearMonthNull() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> IncomeDate.from(null, "29")
		);
		assertTrue(exception.getMessage().contains("収入日"));
	}

	@Test
	@DisplayName("異常系：年月が6桁以外で生成")
	void testFrom_YearMonthAndDay_YearMonthInvalidLength() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> IncomeDate.from("20251", "29")
		);
		assertTrue(exception.getMessage().contains("収入日"));
	}

	@Test
	@DisplayName("異常系：日がnullで生成")
	void testFrom_YearMonthAndDay_DayNull() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> IncomeDate.from("202511", null)
		);
		assertTrue(exception.getMessage().contains("収入日"));
	}

	@Test
	@DisplayName("異常系：日が空文字で生成")
	void testFrom_YearMonthAndDay_DayEmpty() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> IncomeDate.from("202511", "")
		);
		assertTrue(exception.getMessage().contains("収入日"));
	}

	@Test
	@DisplayName("異常系：日が2桁以外で生成")
	void testFrom_YearMonthAndDay_DayInvalidLength() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> IncomeDate.from("202511", "9")
		);
		assertTrue(exception.getMessage().contains("収入日"));
	}

	@Test
	@DisplayName("正常系：日付の比較 - isBefore")
	void testIsBefore() {
		// 準備
		IncomeDate date1 = IncomeDate.from("20251128");
		IncomeDate date2 = IncomeDate.from("20251129");

		// 実行 & 検証
		assertTrue(date1.isBefore(date2));
		assertFalse(date2.isBefore(date1));
	}

	@Test
	@DisplayName("正常系：日付の比較 - isAfter")
	void testIsAfter() {
		// 準備
		IncomeDate date1 = IncomeDate.from("20251130");
		IncomeDate date2 = IncomeDate.from("20251129");

		// 実行 & 検証
		assertTrue(date1.isAfter(date2));
		assertFalse(date2.isAfter(date1));
	}

	@Test
	@DisplayName("正常系：equals - 同じ値")
	void testEquals_SameValue() {
		// 準備
		IncomeDate date1 = IncomeDate.from("20251129");
		IncomeDate date2 = IncomeDate.from("20251129");

		// 実行 & 検証
		assertEquals(date1, date2);
		assertEquals(date1.hashCode(), date2.hashCode());
	}

	@Test
	@DisplayName("正常系：equals - 異なる値")
	void testEquals_DifferentValue() {
		// 準備
		IncomeDate date1 = IncomeDate.from("20251128");
		IncomeDate date2 = IncomeDate.from("20251129");

		// 実行 & 検証
		assertNotEquals(date1, date2);
	}

	@Test
	@DisplayName("正常系：境界値 - 月末")
	void testBoundaryValue_EndOfMonth() {
		// 実行：2月末日（閏年）
		IncomeDate leapYear = IncomeDate.from("20240229");
		assertEquals(LocalDate.of(2024, 2, 29), leapYear.getValue());

		// 実行：2月末日（平年）
		IncomeDate normalYear = IncomeDate.from("20250228");
		assertEquals(LocalDate.of(2025, 2, 28), normalYear.getValue());

		// 実行：12月末日
		IncomeDate endOfYear = IncomeDate.from("20251231");
		assertEquals(LocalDate.of(2025, 12, 31), endOfYear.getValue());
	}

	@Test
	@DisplayName("正常系：境界値 - 月初")
	void testBoundaryValue_StartOfMonth() {
		// 実行
		IncomeDate result = IncomeDate.from("20251101");

		// 検証
		assertEquals(LocalDate.of(2025, 11, 1), result.getValue());
	}
}
