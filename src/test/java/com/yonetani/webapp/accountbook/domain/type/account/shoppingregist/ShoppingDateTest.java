/**
 * ShoppingDate(買い物日)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/30 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingregist;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;

/**
 *<pre>
 * ShoppingDate(買い物日)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("買い物日(ShoppingDate)のテスト")
class ShoppingDateTest {

	@Test
	@DisplayName("正常系：有効な日付で生成できる")
	void testFrom_正常系_有効な日付() {
		// 準備
		TargetYearMonth targetYearMonth = TargetYearMonth.from("202408");
		LocalDate date = LocalDate.of(2024, 8, 18);

		// 実行
		ShoppingDate shoppingDate = ShoppingDate.from(date, targetYearMonth);

		// 検証
		assertNotNull(shoppingDate);
		assertEquals(date, shoppingDate.getValue());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		// 準備
		TargetYearMonth targetYearMonth = TargetYearMonth.from("202408");

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> ShoppingDate.from(null, targetYearMonth)
		);
		assertTrue(exception.getMessage().contains("買い物日"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("異常系：対象年月の範囲外の日付で例外が発生する")
	void testFrom_異常系_対象年月範囲外() {
		// 準備
		TargetYearMonth targetYearMonth = TargetYearMonth.from("202408");
		LocalDate date = LocalDate.of(2024, 9, 1); // 9月の日付

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> ShoppingDate.from(date, targetYearMonth)
		);
		assertTrue(exception.getMessage().contains("買い物日"));
		assertTrue(exception.getMessage().contains("範囲"));
	}

	@Test
	@DisplayName("正常系：toDisplayString()でyyyy/MM/dd形式の文字列を取得")
	void testToDisplayString() {
		// 準備
		TargetYearMonth targetYearMonth = TargetYearMonth.from("202408");
		ShoppingDate date = ShoppingDate.from(LocalDate.of(2024, 8, 18), targetYearMonth);

		// 実行
		String result = date.toDisplayString();

		// 検証
		assertEquals("2024/08/18", result);
	}

	@Test
	@DisplayName("正常系：toString()でISO-8601形式の文字列を取得（デバッグ用）")
	void testToString() {
		// 準備
		TargetYearMonth targetYearMonth = TargetYearMonth.from("202408");
		ShoppingDate date = ShoppingDate.from(LocalDate.of(2024, 8, 18), targetYearMonth);

		// 実行
		String result = date.toString();

		// 検証（ISO-8601形式: yyyy-MM-dd）
		assertEquals("2024-08-18", result);
	}

	@Test
	@DisplayName("正常系：equals()で同値性の判定ができる")
	void testEquals() {
		// 準備
		TargetYearMonth targetYearMonth = TargetYearMonth.from("202408");
		ShoppingDate date1 = ShoppingDate.from(LocalDate.of(2024, 8, 18), targetYearMonth);
		ShoppingDate date2 = ShoppingDate.from(LocalDate.of(2024, 8, 18), targetYearMonth);
		ShoppingDate date3 = ShoppingDate.from(LocalDate.of(2024, 8, 19), targetYearMonth);

		// 実行 & 検証
		assertEquals(date1, date2);
		assertNotEquals(date1, date3);
	}

	@Test
	@DisplayName("正常系：hashCode()でハッシュ値が取得できる")
	void testHashCode() {
		// 準備
		TargetYearMonth targetYearMonth = TargetYearMonth.from("202408");
		ShoppingDate date1 = ShoppingDate.from(LocalDate.of(2024, 8, 18), targetYearMonth);
		ShoppingDate date2 = ShoppingDate.from(LocalDate.of(2024, 8, 18), targetYearMonth);

		// 実行 & 検証
		assertEquals(date1.hashCode(), date2.hashCode());
	}
}
