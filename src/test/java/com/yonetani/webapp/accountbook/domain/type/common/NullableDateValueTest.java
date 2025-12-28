/**
 * NullableDateValue抽象クラスのテストクラスです。
 * テスト対象の抽象クラスをテストするため、テスト用の具象クラスを使用します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/28 : 1.00.00  新規作成
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
 * NullableDateValue抽象クラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("null許容日付基底クラス(NullableDateValue)のテスト")
class NullableDateValueTest {

	// テスト用の具象クラス
	private static class TestNullableDateValue extends NullableDateValue {
		private TestNullableDateValue(LocalDate value) {
			super(value);
		}

		public static TestNullableDateValue from(LocalDate value) {
			validate(value, "テストnull許容日付");
			return new TestNullableDateValue(value);
		}

		public static TestNullableDateValue from(String dateString) {
			return new TestNullableDateValue(parseDate(dateString, "テストnull許容日付"));
		}

		public static TestNullableDateValue from(String yearMonth, String day) {
			return new TestNullableDateValue(parseDate(yearMonth, day, "テストnull許容日付"));
		}
	}

	@Test
	@DisplayName("正常系：日付で生成できる")
	void testFrom_正常系_日付() {
		// 実行
		LocalDate date = LocalDate.of(2025, 11, 29);
		TestNullableDateValue dateValue = TestNullableDateValue.from(date);

		// 検証
		assertNotNull(dateValue);
		assertEquals(date, dateValue.getValue());
		assertEquals("2025-11-29", dateValue.toString());
		assertEquals("20251129", dateValue.toCompactString());
		assertEquals("2025/11/29", dateValue.toDisplayString());
		assertEquals("2025年11月29日", dateValue.toJapaneseDisplayString());
		assertEquals("2025年11月", dateValue.toJapaneseYearMonthString());
		assertFalse(dateValue.isNull());
	}

	@Test
	@DisplayName("正常系：null値で生成できる")
	void testFrom_正常系_null値() {
		// 実行
		TestNullableDateValue dateValue = TestNullableDateValue.from((LocalDate) null);

		// 検証
		assertNotNull(dateValue);
		assertNull(dateValue.getValue());
		assertTrue(dateValue.isNull());
		assertEquals("", dateValue.toString());
		assertEquals("", dateValue.toCompactString());
		assertEquals("", dateValue.toDisplayString());
		assertEquals("", dateValue.toJapaneseDisplayString());
		assertEquals("", dateValue.toJapaneseYearMonthString());
	}

	@Test
	@DisplayName("正常系：compareTo - 後の日付")
	void testCompareTo_正常系_後の日付() {
		// 準備
		TestNullableDateValue date1 = TestNullableDateValue.from(LocalDate.of(2025, 11, 30));
		TestNullableDateValue date2 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));

		// 実行 & 検証
		assertTrue(date1.compareTo(date2) > 0);
	}

	@Test
	@DisplayName("正常系：compareTo - 等しい")
	void testCompareTo_正常系_等しい() {
		// 準備
		TestNullableDateValue date1 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));
		TestNullableDateValue date2 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));

		// 実行 & 検証
		assertEquals(0, date1.compareTo(date2));
	}

	@Test
	@DisplayName("正常系：compareTo - 前の日付")
	void testCompareTo_正常系_前の日付() {
		// 準備
		TestNullableDateValue date1 = TestNullableDateValue.from(LocalDate.of(2025, 11, 28));
		TestNullableDateValue date2 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));

		// 実行 & 検証
		assertTrue(date1.compareTo(date2) < 0);
	}

	@Test
	@DisplayName("正常系：compareTo - null値はLocalDate.MINとして扱う")
	void testCompareTo_正常系_null値() {
		// 準備
		TestNullableDateValue date1 = TestNullableDateValue.from((LocalDate) null);
		TestNullableDateValue date2 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));
		TestNullableDateValue date3 = TestNullableDateValue.from((LocalDate) null);

		// 実行 & 検証（null < 任意の日付）
		assertTrue(date1.compareTo(date2) < 0);
		// null == null
		assertEquals(0, date1.compareTo(date3));
	}

	@Test
	@DisplayName("異常系：compareToでnullオブジェクトを渡すと例外が発生する")
	void testCompareTo_異常系_nullオブジェクト() {
		// 準備
		TestNullableDateValue date = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));

		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> date.compareTo(null)
		);
	}

	@Test
	@DisplayName("正常系：isBefore - 前の日付")
	void testIsBefore_正常系_前の日付() {
		// 準備
		TestNullableDateValue date1 = TestNullableDateValue.from(LocalDate.of(2025, 11, 28));
		TestNullableDateValue date2 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));

		// 実行 & 検証
		assertTrue(date1.isBefore(date2));
		assertFalse(date2.isBefore(date1));
	}

	@Test
	@DisplayName("正常系：isBefore - 等しい日付")
	void testIsBefore_正常系_等しい日付() {
		// 準備
		TestNullableDateValue date1 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));
		TestNullableDateValue date2 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));

		// 実行 & 検証
		assertFalse(date1.isBefore(date2));
	}

	@Test
	@DisplayName("正常系：isBefore - null値")
	void testIsBefore_正常系_null値() {
		// 準備
		TestNullableDateValue date1 = TestNullableDateValue.from((LocalDate) null);
		TestNullableDateValue date2 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));

		// 実行 & 検証（null < 任意の日付）
		assertTrue(date1.isBefore(date2));
		assertFalse(date2.isBefore(date1));
	}

	@Test
	@DisplayName("異常系：isBeforeでnullオブジェクトを渡すと例外が発生する")
	void testIsBefore_異常系_nullオブジェクト() {
		// 準備
		TestNullableDateValue date = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));

		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> date.isBefore(null)
		);
	}

	@Test
	@DisplayName("正常系：isAfter - 後の日付")
	void testIsAfter_正常系_後の日付() {
		// 準備
		TestNullableDateValue date1 = TestNullableDateValue.from(LocalDate.of(2025, 11, 30));
		TestNullableDateValue date2 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));

		// 実行 & 検証
		assertTrue(date1.isAfter(date2));
		assertFalse(date2.isAfter(date1));
	}

	@Test
	@DisplayName("正常系：isAfter - 等しい日付")
	void testIsAfter_正常系_等しい日付() {
		// 準備
		TestNullableDateValue date1 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));
		TestNullableDateValue date2 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));

		// 実行 & 検証
		assertFalse(date1.isAfter(date2));
	}

	@Test
	@DisplayName("正常系：isAfter - null値")
	void testIsAfter_正常系_null値() {
		// 準備
		TestNullableDateValue date1 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));
		TestNullableDateValue date2 = TestNullableDateValue.from((LocalDate) null);

		// 実行 & 検証（任意の日付 > null）
		assertTrue(date1.isAfter(date2));
		assertFalse(date2.isAfter(date1));
	}

	@Test
	@DisplayName("異常系：isAfterでnullオブジェクトを渡すと例外が発生する")
	void testIsAfter_異常系_nullオブジェクト() {
		// 準備
		TestNullableDateValue date = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));

		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> date.isAfter(null)
		);
	}

	@Test
	@DisplayName("正常系：isNull")
	void testIsNull() {
		// 検証
		assertTrue(TestNullableDateValue.from((LocalDate) null).isNull());
		assertFalse(TestNullableDateValue.from(LocalDate.of(2025, 11, 29)).isNull());
	}

	@Test
	@DisplayName("正常系：toCompactString")
	void testToCompactString() {
		// 準備
		TestNullableDateValue date1 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));
		TestNullableDateValue date2 = TestNullableDateValue.from((LocalDate) null);

		// 検証
		assertEquals("20251129", date1.toCompactString());
		assertEquals("", date2.toCompactString()); // null値は空文字列
	}

	@Test
	@DisplayName("正常系：toDisplayString")
	void testToDisplayString() {
		// 準備
		TestNullableDateValue date1 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));
		TestNullableDateValue date2 = TestNullableDateValue.from((LocalDate) null);

		// 検証
		assertEquals("2025/11/29", date1.toDisplayString());
		assertEquals("", date2.toDisplayString()); // null値は空文字列
	}

	@Test
	@DisplayName("正常系：toJapaneseDisplayString")
	void testToJapaneseDisplayString() {
		// 準備
		TestNullableDateValue date1 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));
		TestNullableDateValue date2 = TestNullableDateValue.from((LocalDate) null);

		// 検証
		assertEquals("2025年11月29日", date1.toJapaneseDisplayString());
		assertEquals("", date2.toJapaneseDisplayString()); // null値は空文字列
	}

	@Test
	@DisplayName("正常系：toJapaneseYearMonthString")
	void testToJapaneseYearMonthString() {
		// 準備
		TestNullableDateValue date1 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));
		TestNullableDateValue date2 = TestNullableDateValue.from((LocalDate) null);

		// 検証
		assertEquals("2025年11月", date1.toJapaneseYearMonthString());
		assertEquals("", date2.toJapaneseYearMonthString()); // null値は空文字列
	}

	@Test
	@DisplayName("正常系：toString")
	void testToString() {
		// 準備
		TestNullableDateValue date1 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));
		TestNullableDateValue date2 = TestNullableDateValue.from((LocalDate) null);

		// 検証
		assertEquals("2025-11-29", date1.toString());
		assertEquals("", date2.toString()); // null値は空文字列
	}

	@Test
	@DisplayName("正常系：境界値 - 月末")
	void testBoundaryValue_EndOfMonth() {
		// 実行：2月末日（閏年）
		TestNullableDateValue leapYear = TestNullableDateValue.from(LocalDate.of(2024, 2, 29));
		assertEquals(LocalDate.of(2024, 2, 29), leapYear.getValue());
		assertEquals("20240229", leapYear.toCompactString());

		// 実行：2月末日（平年）
		TestNullableDateValue normalYear = TestNullableDateValue.from(LocalDate.of(2025, 2, 28));
		assertEquals(LocalDate.of(2025, 2, 28), normalYear.getValue());
		assertEquals("20250228", normalYear.toCompactString());

		// 実行：12月末日
		TestNullableDateValue endOfYear = TestNullableDateValue.from(LocalDate.of(2025, 12, 31));
		assertEquals(LocalDate.of(2025, 12, 31), endOfYear.getValue());
		assertEquals("20251231", endOfYear.toCompactString());
	}

	@Test
	@DisplayName("正常系：境界値 - 月初")
	void testBoundaryValue_StartOfMonth() {
		TestNullableDateValue result = TestNullableDateValue.from(LocalDate.of(2025, 11, 1));
		assertEquals(LocalDate.of(2025, 11, 1), result.getValue());
		assertEquals("20251101", result.toCompactString());
	}

	@Test
	@DisplayName("正常系：equals")
	void testEquals() {
		// 準備
		TestNullableDateValue date1 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));
		TestNullableDateValue date2 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));
		TestNullableDateValue date3 = TestNullableDateValue.from(LocalDate.of(2025, 11, 30));
		TestNullableDateValue date4 = TestNullableDateValue.from((LocalDate) null);
		TestNullableDateValue date5 = TestNullableDateValue.from((LocalDate) null);

		// 検証
		assertEquals(date1, date2);
		assertNotEquals(date1, date3);
		assertEquals(date4, date5); // null値同士は等しい
		assertNotEquals(date1, date4);
	}

	@Test
	@DisplayName("正常系：hashCode")
	void testHashCode() {
		// 準備
		TestNullableDateValue date1 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));
		TestNullableDateValue date2 = TestNullableDateValue.from(LocalDate.of(2025, 11, 29));
		TestNullableDateValue date3 = TestNullableDateValue.from((LocalDate) null);
		TestNullableDateValue date4 = TestNullableDateValue.from((LocalDate) null);

		// 検証（同じ値なら同じハッシュコード）
		assertEquals(date1.hashCode(), date2.hashCode());
		assertEquals(date3.hashCode(), date4.hashCode());
	}

	@Test
	@DisplayName("正常系：文字列（yyyyMMdd）から生成")
	void testFromString_Success() {
		// 実行
		TestNullableDateValue result = TestNullableDateValue.from("20251129");

		// 検証
		assertNotNull(result);
		assertEquals(LocalDate.of(2025, 11, 29), result.getValue());
		assertEquals("20251129", result.toCompactString());
		assertEquals("2025/11/29", result.toDisplayString());
	}

	@Test
	@DisplayName("正常系：null文字列からnull値で生成")
	void testFromString_Null() {
		// 実行
		TestNullableDateValue result = TestNullableDateValue.from((String) null);

		// 検証（null文字列はnull値として生成される）
		assertNotNull(result);
		assertNull(result.getValue());
		assertTrue(result.isNull());
	}

	@Test
	@DisplayName("正常系：空文字列からnull値で生成")
	void testFromString_Empty() {
		// 実行
		TestNullableDateValue result = TestNullableDateValue.from("");

		// 検証（空文字列はnull値として生成される）
		assertNotNull(result);
		assertNull(result.getValue());
		assertTrue(result.isNull());
	}

	@Test
	@DisplayName("異常系：8桁以外の文字列からの生成")
	void testFromString_InvalidLength() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestNullableDateValue.from("2025112")
		);
		assertTrue(exception.getMessage().contains("テストnull許容日付"));
	}

	@Test
	@DisplayName("異常系：不正な日付文字列からの生成")
	void testFromString_InvalidDate() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestNullableDateValue.from("20251332") // 13月32日
		);
		assertTrue(exception.getMessage().contains("テストnull許容日付"));
	}

	@Test
	@DisplayName("正常系：年月+日から生成")
	void testFromYearMonthAndDay_Success() {
		// 実行
		TestNullableDateValue result = TestNullableDateValue.from("202511", "29");

		// 検証
		assertNotNull(result);
		assertEquals(LocalDate.of(2025, 11, 29), result.getValue());
	}

	@Test
	@DisplayName("正常系：年月と日が両方nullでnull値生成")
	void testFromYearMonthAndDay_BothNull() {
		// 実行
		TestNullableDateValue result = TestNullableDateValue.from(null, null);

		// 検証（両方nullの場合はnull値として生成される）
		assertNotNull(result);
		assertNull(result.getValue());
		assertTrue(result.isNull());
	}

	@Test
	@DisplayName("正常系：年月と日が両方空文字列でnull値生成")
	void testFromYearMonthAndDay_BothEmpty() {
		// 実行
		TestNullableDateValue result = TestNullableDateValue.from("", "");

		// 検証（両方空文字列の場合はnull値として生成される）
		assertNotNull(result);
		assertNull(result.getValue());
		assertTrue(result.isNull());
	}

	@Test
	@DisplayName("異常系：年月のみnullで生成")
	void testFromYearMonthAndDay_YearMonthNull() {
		// 実行 & 検証（年月のみnullは片方だけnullなのでエラー）
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestNullableDateValue.from(null, "29")
		);
		assertTrue(exception.getMessage().contains("テストnull許容日付"));
	}

	@Test
	@DisplayName("異常系：日のみnullで生成")
	void testFromYearMonthAndDay_DayNull() {
		// 実行 & 検証（日のみnullは片方だけnullなのでエラー）
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestNullableDateValue.from("202511", null)
		);
		assertTrue(exception.getMessage().contains("テストnull許容日付"));
	}

	@Test
	@DisplayName("異常系：年月が6桁以外で生成")
	void testFromYearMonthAndDay_YearMonthInvalidLength() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestNullableDateValue.from("20251", "29")
		);
		assertTrue(exception.getMessage().contains("テストnull許容日付"));
	}

	@Test
	@DisplayName("異常系：日が2桁以外で生成")
	void testFromYearMonthAndDay_DayInvalidLength() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestNullableDateValue.from("202511", "9")
		);
		assertTrue(exception.getMessage().contains("テストnull許容日付"));
	}
}
