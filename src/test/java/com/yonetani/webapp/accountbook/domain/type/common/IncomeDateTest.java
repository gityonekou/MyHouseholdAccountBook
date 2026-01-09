/**
 * IncomeDateのテストクラスです。
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
		IncomeDate result = IncomeDate.from("20251129");

		// 検証
		assertNotNull(result);
		assertEquals(LocalDate.of(2025, 11, 29), result.getValue());
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

}
