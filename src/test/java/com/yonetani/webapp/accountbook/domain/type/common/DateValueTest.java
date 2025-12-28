/**
 * DateValue抽象クラスのテストクラスです。
 * テスト対象の抽象クラスをテストするため、テスト用の具象クラスを使用します。
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
 * DateValue抽象クラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
 @DisplayName("日付基底クラス(DateValue)のテスト")
class DateValueTest {

	// テスト用の具象クラス
	private static class TestDateValue extends DateValue {
		private TestDateValue(LocalDate value) {
			super(value);
		}

		public static TestDateValue from(LocalDate value) {
			validate(value, "テスト日付");
			return new TestDateValue(value);
		}

		public static TestDateValue from(String dateString) {
			LocalDate parsed = parseDate(dateString, "テスト日付");
			return new TestDateValue(parsed);
		}

		public static TestDateValue from(String yearMonth, String day) {
			LocalDate parsed = parseDate(yearMonth, day, "テスト日付");
			return new TestDateValue(parsed);
		}
	}

	@Test
	@DisplayName("正常系：LocalDateから生成")
	void testFromLocalDate_Success() {
		// 実行
		LocalDate date = LocalDate.of(2025, 11, 29);
		TestDateValue result = TestDateValue.from(date);

		// 検証
		assertNotNull(result);
		assertEquals(date, result.getValue());
		assertEquals("2025-11-29", result.toString());
		assertEquals("20251129", result.toCompactString());
		assertEquals("2025/11/29", result.toDisplayString());
		assertEquals("2025年11月29日", result.toJapaneseDisplayString());
		assertEquals("2025年11月", result.toJapaneseYearMonthString());
	}

	@Test
	@DisplayName("異常系：null値からの生成")
	void testFromLocalDate_Null() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestDateValue.from((LocalDate) null)
		);
		assertTrue(exception.getMessage().contains("テスト日付"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("正常系：文字列（yyyyMMdd）から生成")
	void testFromString_Success() {
		// 実行
		TestDateValue result = TestDateValue.from("20251129");

		// 検証
		assertNotNull(result);
		assertEquals(LocalDate.of(2025, 11, 29), result.getValue());
		assertEquals("20251129", result.toCompactString());
		assertEquals("2025/11/29", result.toDisplayString());
	}

	@Test
	@DisplayName("異常系：null文字列からの生成")
	void testFromString_Null() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestDateValue.from((String) null)
		);
		assertTrue(exception.getMessage().contains("テスト日付"));
	}

	@Test
	@DisplayName("異常系：空文字列からの生成")
	void testFromString_Empty() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestDateValue.from("")
		);
		assertTrue(exception.getMessage().contains("テスト日付"));
	}

	@Test
	@DisplayName("異常系：8桁以外の文字列からの生成")
	void testFromString_InvalidLength() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestDateValue.from("2025112")
		);
		assertTrue(exception.getMessage().contains("テスト日付"));
	}

	@Test
	@DisplayName("異常系：不正な日付文字列からの生成")
	void testFromString_InvalidDate() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestDateValue.from("20251332") // 13月32日
		);
		assertTrue(exception.getMessage().contains("テスト日付"));
	}

	@Test
	@DisplayName("正常系：年月+日から生成")
	void testFromYearMonthAndDay_Success() {
		// 実行
		TestDateValue result = TestDateValue.from("202511", "29");

		// 検証
		assertNotNull(result);
		assertEquals(LocalDate.of(2025, 11, 29), result.getValue());
	}

	@Test
	@DisplayName("異常系：年月がnullで生成")
	void testFromYearMonthAndDay_YearMonthNull() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestDateValue.from(null, "29")
		);
		assertTrue(exception.getMessage().contains("テスト日付"));
	}

	@Test
	@DisplayName("異常系：年月が6桁以外で生成")
	void testFromYearMonthAndDay_YearMonthInvalidLength() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestDateValue.from("20251", "29")
		);
		assertTrue(exception.getMessage().contains("テスト日付"));
	}

	@Test
	@DisplayName("異常系：日がnullで生成")
	void testFromYearMonthAndDay_DayNull() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestDateValue.from("202511", null)
		);
		assertTrue(exception.getMessage().contains("テスト日付"));
	}

	@Test
	@DisplayName("異常系：日が空文字で生成")
	void testFromYearMonthAndDay_DayEmpty() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestDateValue.from("202511", "")
		);
		assertTrue(exception.getMessage().contains("テスト日付"));
	}

	@Test
	@DisplayName("異常系：日が2桁以外で生成")
	void testFromYearMonthAndDay_DayInvalidLength() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestDateValue.from("202511", "9")
		);
		assertTrue(exception.getMessage().contains("テスト日付"));
	}

	@Test
	@DisplayName("正常系：compareTo - 同じ日付")
	void testCompareTo_Equal() {
		// 準備
		TestDateValue date1 = TestDateValue.from("20251129");
		TestDateValue date2 = TestDateValue.from("20251129");

		// 実行 & 検証
		assertEquals(0, date1.compareTo(date2));
	}

	@Test
	@DisplayName("正常系：compareTo - 前の日付")
	void testCompareTo_Before() {
		// 準備
		TestDateValue date1 = TestDateValue.from("20251128");
		TestDateValue date2 = TestDateValue.from("20251129");

		// 実行 & 検証
		assertTrue(date1.compareTo(date2) < 0);
	}

	@Test
	@DisplayName("正常系：compareTo - 後の日付")
	void testCompareTo_After() {
		// 準備
		TestDateValue date1 = TestDateValue.from("20251130");
		TestDateValue date2 = TestDateValue.from("20251129");

		// 実行 & 検証
		assertTrue(date1.compareTo(date2) > 0);
	}

	@Test
	@DisplayName("異常系：compareTo - null比較")
	void testCompareTo_Null() {
		// 準備
		TestDateValue date = TestDateValue.from("20251129");

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> date.compareTo(null)
		);
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("正常系：isBefore")
	void testIsBefore() {
		// 準備
		TestDateValue date1 = TestDateValue.from("20251128");
		TestDateValue date2 = TestDateValue.from("20251129");

		// 実行 & 検証
		assertTrue(date1.isBefore(date2));
		assertFalse(date2.isBefore(date1));
		assertFalse(date1.isBefore(date1));
	}

	@Test
	@DisplayName("正常系：isAfter")
	void testIsAfter() {
		// 準備
		TestDateValue date1 = TestDateValue.from("20251130");
		TestDateValue date2 = TestDateValue.from("20251129");

		// 実行 & 検証
		assertTrue(date1.isAfter(date2));
		assertFalse(date2.isAfter(date1));
		assertFalse(date1.isAfter(date1));
	}

	@Test
	@DisplayName("正常系：toCompactString - yyyyMMdd形式")
	void testToCompactString() {
		// 実行
		TestDateValue result = TestDateValue.from("20251129");

		// 検証
		assertEquals("20251129", result.toCompactString());
	}

	@Test
	@DisplayName("正常系：toDisplayString - yyyy/MM/dd形式")
	void testToDisplayString() {
		// 実行
		TestDateValue result = TestDateValue.from("20251129");

		// 検証
		assertEquals("2025/11/29", result.toDisplayString());
	}

	@Test
	@DisplayName("正常系：toJapaneseDisplayString - yyyy年MM月dd日形式")
	void testToJapaneseDisplayString() {
		// 実行
		TestDateValue result = TestDateValue.from("20251129");

		// 検証
		assertEquals("2025年11月29日", result.toJapaneseDisplayString());
	}

	@Test
	@DisplayName("正常系：toJapaneseYearMonthString - yyyy年MM月形式")
	void testToJapaneseYearMonthString() {
		// 実行
		TestDateValue result = TestDateValue.from("20251129");

		// 検証
		assertEquals("2025年11月", result.toJapaneseYearMonthString());
	}

	@Test
	@DisplayName("正常系：toString - ISO-8601形式（yyyy-MM-dd）")
	void testToString() {
		// 実行
		TestDateValue result = TestDateValue.from("20251129");

		// 検証
		assertEquals("2025-11-29", result.toString());
	}

	@Test
	@DisplayName("正常系：equals - 同じ値")
	void testEquals_SameValue() {
		// 準備
		TestDateValue date1 = TestDateValue.from("20251129");
		TestDateValue date2 = TestDateValue.from("20251129");

		// 実行 & 検証
		assertEquals(date1, date2);
		assertEquals(date1.hashCode(), date2.hashCode());
	}

	@Test
	@DisplayName("正常系：equals - 異なる値")
	void testEquals_DifferentValue() {
		// 準備
		TestDateValue date1 = TestDateValue.from("20251128");
		TestDateValue date2 = TestDateValue.from("20251129");

		// 実行 & 検証
		assertNotEquals(date1, date2);
	}

	@Test
	@DisplayName("正常系：境界値 - 月末")
	void testBoundaryValue_EndOfMonth() {
		// 実行：2月末日（閏年）
		TestDateValue leapYear = TestDateValue.from("20240229");
		assertEquals(LocalDate.of(2024, 2, 29), leapYear.getValue());

		// 実行：2月末日（平年）
		TestDateValue normalYear = TestDateValue.from("20250228");
		assertEquals(LocalDate.of(2025, 2, 28), normalYear.getValue());

		// 実行：12月末日
		TestDateValue endOfYear = TestDateValue.from("20251231");
		assertEquals(LocalDate.of(2025, 12, 31), endOfYear.getValue());
	}

	@Test
	@DisplayName("正常系：境界値 - 月初")
	void testBoundaryValue_StartOfMonth() {
		// 実行
		TestDateValue result = TestDateValue.from("20251101");

		// 検証
		assertEquals(LocalDate.of(2025, 11, 1), result.getValue());
	}
}
