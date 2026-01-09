/**
 * PaymentDateのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/28 : 1.00.00  NullableDateValue継承対応に伴いテストを拡充
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
 * PaymentDateのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
class PaymentDateTest {

	@Test
	@DisplayName("正常系：LocalDateから生成")
	void testFrom_LocalDate_Success() {
		// 実行
		LocalDate date = LocalDate.of(2025, 11, 29);
		PaymentDate result = PaymentDate.from(date);

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
	@DisplayName("正常系：文字列（yyyyMMdd）から生成")
	void testFrom_String_Success() {
		// 実行
		PaymentDate result = PaymentDate.from("20251129");

		// 検証
		assertNotNull(result);
		assertEquals(LocalDate.of(2025, 11, 29), result.getValue());
	}

	@Test
	@DisplayName("正常系：年月+日から生成")
	void testFrom_YearMonthAndDay_Success() {
		// 実行
		PaymentDate result = PaymentDate.from("202511", "29");

		// 検証
		assertNotNull(result);
		assertEquals(LocalDate.of(2025, 11, 29), result.getValue());
	}

	@Test
	@DisplayName("正常系：null値で生成できる")
	void testFrom_正常系_null値() {
		// 実行
		PaymentDate paymentDate = PaymentDate.from((LocalDate) null);

		// 検証
		assertNotNull(paymentDate);
		assertNull(paymentDate.getValue());
		assertTrue(paymentDate.isNull());
		assertEquals("", paymentDate.toString());
		assertEquals("", paymentDate.toCompactString());
		assertEquals("", paymentDate.toDisplayString());
		assertEquals("", paymentDate.toJapaneseDisplayString());
	}

	@Test
	@DisplayName("正常系：null文字列からnull値で生成できる")
	void testFromString_正常系_null値() {
		// 実行
		PaymentDate paymentDate = PaymentDate.from((String) null);

		// 検証
		assertNotNull(paymentDate);
		assertNull(paymentDate.getValue());
		assertTrue(paymentDate.isNull());
	}

	@Test
	@DisplayName("正常系：空文字列からnull値で生成できる")
	void testFromString_正常系_空文字列() {
		// 実行
		PaymentDate paymentDate = PaymentDate.from("");

		// 検証
		assertNotNull(paymentDate);
		assertNull(paymentDate.getValue());
		assertTrue(paymentDate.isNull());
	}

	@Test
	@DisplayName("異常系：8桁以外の文字列からの生成")
	void testFromString_異常系_桁数不正() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> PaymentDate.from("2025122")
		);
		assertTrue(exception.getMessage().contains("支払日"));
	}

	@Test
	@DisplayName("異常系：不正な日付文字列からの生成")
	void testFromString_異常系_日付不正() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> PaymentDate.from("20251332") // 13月32日
		);
		assertTrue(exception.getMessage().contains("支払日"));
	}

	@Test
	@DisplayName("正常系：年月と日が両方nullでnull値生成")
	void testFromYearMonthAndDay_正常系_両方null() {
		// 実行
		PaymentDate paymentDate = PaymentDate.from(null, null);

		// 検証
		assertNotNull(paymentDate);
		assertNull(paymentDate.getValue());
		assertTrue(paymentDate.isNull());
	}

	@Test
	@DisplayName("正常系：年月と日が両方空文字列でnull値生成")
	void testFromYearMonthAndDay_正常系_両方空文字列() {
		// 実行
		PaymentDate paymentDate = PaymentDate.from("", "");

		// 検証
		assertNotNull(paymentDate);
		assertNull(paymentDate.getValue());
		assertTrue(paymentDate.isNull());
	}

	@Test
	@DisplayName("異常系：年月のみnullで生成")
	void testFromYearMonthAndDay_異常系_年月のみnull() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> PaymentDate.from(null, "28")
		);
		assertTrue(exception.getMessage().contains("支払日"));
	}

	@Test
	@DisplayName("異常系：日のみnullで生成")
	void testFromYearMonthAndDay_異常系_日のみnull() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> PaymentDate.from("202512", null)
		);
		assertTrue(exception.getMessage().contains("支払日"));
	}

	@Test
	@DisplayName("正常系：max - 両方とも値がある場合、大きい方を返す")
	void testMax_正常系_両方値あり() {
		// 準備
		PaymentDate date1 = PaymentDate.from(LocalDate.of(2025, 12, 28));
		PaymentDate date2 = PaymentDate.from(LocalDate.of(2025, 12, 29));

		// 実行 & 検証
		assertEquals(date2, date1.max(date2));
		assertEquals(date2, date2.max(date1));
	}

	@Test
	@DisplayName("正常系：max - thisがnullの場合、otherを返す")
	void testMax_正常系_thisがnull() {
		// 準備
		PaymentDate date1 = PaymentDate.from((LocalDate) null);
		PaymentDate date2 = PaymentDate.from(LocalDate.of(2025, 12, 28));

		// 実行 & 検証
		assertEquals(date2, date1.max(date2));
	}

	@Test
	@DisplayName("正常系：max - otherがnullの場合、thisを返す")
	void testMax_正常系_otherがnull() {
		// 準備
		PaymentDate date1 = PaymentDate.from(LocalDate.of(2025, 12, 28));
		PaymentDate date2 = PaymentDate.from((LocalDate) null);

		// 実行 & 検証
		assertEquals(date1, date1.max(date2));
	}

	@Test
	@DisplayName("正常系：max - 両方nullの場合、otherを返す")
	void testMax_正常系_両方null() {
		// 準備
		PaymentDate date1 = PaymentDate.from((LocalDate) null);
		PaymentDate date2 = PaymentDate.from((LocalDate) null);

		// 実行 & 検証
		assertEquals(date2, date1.max(date2));
	}

	@Test
	@DisplayName("異常系：maxで引数がnullの場合、例外が発生する")
	void testMax_異常系_引数null() {
		// 準備
		PaymentDate date = PaymentDate.from(LocalDate.of(2025, 12, 28));

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> date.max(null)
		);
		assertTrue(exception.getMessage().contains("比較対象の支払日がnullです"));
	}

	@Test
	@DisplayName("正常系：compareTo - null値はLocalDate.MINとして扱う")
	void testCompareTo_正常系_null値() {
		// 準備
		PaymentDate date1 = PaymentDate.from((LocalDate) null);
		PaymentDate date2 = PaymentDate.from(LocalDate.of(2025, 12, 28));
		PaymentDate date3 = PaymentDate.from((LocalDate) null);

		// 実行 & 検証（null < 任意の日付）
		assertTrue(date1.compareTo(date2) < 0);
		// null == null
		assertEquals(0, date1.compareTo(date3));
	}

	@Test
	@DisplayName("正常系：isNull")
	void testIsNull() {
		// 検証
		assertTrue(PaymentDate.from((LocalDate) null).isNull());
		assertFalse(PaymentDate.from(LocalDate.of(2025, 12, 28)).isNull());
	}

	@Test
	@DisplayName("正常系：equals")
	void testEquals() {
		// 準備
		PaymentDate date1 = PaymentDate.from(LocalDate.of(2025, 12, 28));
		PaymentDate date2 = PaymentDate.from(LocalDate.of(2025, 12, 28));
		PaymentDate date3 = PaymentDate.from(LocalDate.of(2025, 12, 29));
		PaymentDate date4 = PaymentDate.from((LocalDate) null);
		PaymentDate date5 = PaymentDate.from((LocalDate) null);

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
		PaymentDate date1 = PaymentDate.from(LocalDate.of(2025, 12, 28));
		PaymentDate date2 = PaymentDate.from(LocalDate.of(2025, 12, 28));
		PaymentDate date3 = PaymentDate.from((LocalDate) null);
		PaymentDate date4 = PaymentDate.from((LocalDate) null);

		// 検証（同じ値なら同じハッシュコード）
		assertEquals(date1.hashCode(), date2.hashCode());
		assertEquals(date3.hashCode(), date4.hashCode());
	}

}
