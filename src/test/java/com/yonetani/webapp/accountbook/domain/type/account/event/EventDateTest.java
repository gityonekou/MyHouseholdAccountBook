/**
 * EventDate(イベント日)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/30 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.event;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * EventDate(イベント日)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("イベント日(EventDate)のテスト")
class EventDateTest {

	@Test
	@DisplayName("正常系：有効な日付で生成できる")
	void testFrom_正常系_有効な日付() {
		// 実行
		EventDate date = EventDate.from(LocalDate.of(2024, 8, 18));

		// 検証
		assertNotNull(date);
		assertEquals(LocalDate.of(2024, 8, 18), date.getValue());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> EventDate.from(null)
		);
		assertTrue(exception.getMessage().contains("イベント日"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("正常系：toCompactString()でyyyyMMdd形式の文字列を取得")
	void testToCompactString() {
		// 準備
		EventDate date = EventDate.from(LocalDate.of(2024, 8, 18));

		// 実行
		String result = date.toCompactString();

		// 検証
		assertEquals("20240818", result);
	}

	@Test
	@DisplayName("正常系：toDisplayString()でyyyy/MM/dd形式の文字列を取得")
	void testToDisplayString() {
		// 準備
		EventDate date = EventDate.from(LocalDate.of(2024, 8, 18));

		// 実行
		String result = date.toDisplayString();

		// 検証
		assertEquals("2024/08/18", result);
	}

	@Test
	@DisplayName("正常系：toJapaneseDisplayString()でyyyy年MM月dd日形式の文字列を取得")
	void testToJapaneseDisplayString() {
		// 準備
		EventDate date = EventDate.from(LocalDate.of(2024, 8, 18));

		// 実行
		String result = date.toJapaneseDisplayString();

		// 検証
		assertEquals("2024年08月18日", result);
	}

	@Test
	@DisplayName("正常系：compareTo()で日付の比較ができる")
	void testCompareTo() {
		// 準備
		EventDate date1 = EventDate.from(LocalDate.of(2024, 8, 18));
		EventDate date2 = EventDate.from(LocalDate.of(2024, 8, 19));
		EventDate date3 = EventDate.from(LocalDate.of(2024, 8, 18));

		// 実行 & 検証
		assertTrue(date1.compareTo(date2) < 0); // date1 < date2
		assertTrue(date2.compareTo(date1) > 0); // date2 > date1
		assertEquals(0, date1.compareTo(date3)); // date1 == date3
	}

	@Test
	@DisplayName("正常系：isBefore()で日付の前後判定ができる")
	void testIsBefore() {
		// 準備
		EventDate date1 = EventDate.from(LocalDate.of(2024, 8, 18));
		EventDate date2 = EventDate.from(LocalDate.of(2024, 8, 19));

		// 実行 & 検証
		assertTrue(date1.isBefore(date2));
		assertFalse(date2.isBefore(date1));
		assertFalse(date1.isBefore(date1));
	}

	@Test
	@DisplayName("正常系：isAfter()で日付の前後判定ができる")
	void testIsAfter() {
		// 準備
		EventDate date1 = EventDate.from(LocalDate.of(2024, 8, 18));
		EventDate date2 = EventDate.from(LocalDate.of(2024, 8, 19));

		// 実行 & 検証
		assertFalse(date1.isAfter(date2));
		assertTrue(date2.isAfter(date1));
		assertFalse(date1.isAfter(date1));
	}

	@Test
	@DisplayName("正常系：equals()で同値性の判定ができる")
	void testEquals() {
		// 準備
		EventDate date1 = EventDate.from(LocalDate.of(2024, 8, 18));
		EventDate date2 = EventDate.from(LocalDate.of(2024, 8, 18));
		EventDate date3 = EventDate.from(LocalDate.of(2024, 8, 19));

		// 実行 & 検証
		assertEquals(date1, date2);
		assertNotEquals(date1, date3);
	}

	@Test
	@DisplayName("正常系：hashCode()でハッシュ値が取得できる")
	void testHashCode() {
		// 準備
		EventDate date1 = EventDate.from(LocalDate.of(2024, 8, 18));
		EventDate date2 = EventDate.from(LocalDate.of(2024, 8, 18));

		// 実行 & 検証
		assertEquals(date1.hashCode(), date2.hashCode());
	}
}
