/**
 * IncomeAmount(収入金額)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * IncomeAmount(収入金額)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("収入金額(IncomeAmount)のテスト")
class IncomeAmountTest {

	@Test
	@DisplayName("正常系：正の金額で生成できる")
	void testFrom_正常系_正の金額() {
		// 実行
		IncomeAmount amount = IncomeAmount.from(new BigDecimal("10000.00"));

		// 検証
		assertNotNull(amount);
		assertEquals(new BigDecimal("10000.00"), amount.getValue());
		assertEquals("10000.00", amount.toString());
		assertEquals("10,000円", amount.toFormatString());
	}

	@Test
	@DisplayName("正常系：ZERO定数が使用できる")
	void testZERO定数() {
		// 検証
		assertNotNull(IncomeAmount.ZERO);
		assertTrue(IncomeAmount.ZERO.isZero());
		assertEquals(BigDecimal.ZERO.setScale(2), IncomeAmount.ZERO.getValue());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> IncomeAmount.from((BigDecimal)null)
		);
		assertTrue(exception.getMessage().contains("収入金額"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("異常系：負の金額で例外が発生する")
	void testFrom_異常系_負の金額() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> IncomeAmount.from(new BigDecimal("-1000.00"))
		);
		assertTrue(exception.getMessage().contains("収入金額"));
		assertTrue(exception.getMessage().contains("マイナス"));
	}

	@Test
	@DisplayName("異常系：スケール値が2以外で例外が発生する")
	void testFrom_異常系_スケール値不正() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> IncomeAmount.from(new BigDecimal("10000"))
		);
		assertTrue(exception.getMessage().contains("収入金額"));
		assertTrue(exception.getMessage().contains("スケール"));
	}

	@Test
	@DisplayName("正常系：加算が正しく動作する")
	void testAdd_正常系() {
		// 準備
		IncomeAmount amount1 = IncomeAmount.from(new BigDecimal("10000.00"));
		IncomeAmount amount2 = IncomeAmount.from(new BigDecimal("5000.00"));

		// 実行
		IncomeAmount result = amount1.add(amount2);

		// 検証
		assertEquals(new BigDecimal("15000.00"), result.getValue());
		// 元のオブジェクトは変更されていないことを確認（不変性）
		assertEquals(new BigDecimal("10000.00"), amount1.getValue());
		assertEquals(new BigDecimal("5000.00"), amount2.getValue());
	}

	@Test
	@DisplayName("異常系：加算でnullを渡すと例外が発生する")
	void testAdd_異常系_null() {
		// 準備
		IncomeAmount amount = IncomeAmount.from(new BigDecimal("10000.00"));

		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> amount.add(null)
		);
	}

	@Test
	@DisplayName("正常系：減算が正しく動作する")
	void testSubtract_正常系() {
		// 準備
		IncomeAmount amount1 = IncomeAmount.from(new BigDecimal("10000.00"));
		IncomeAmount amount2 = IncomeAmount.from(new BigDecimal("3000.00"));

		// 実行
		IncomeAmount result = amount1.subtract(amount2);

		// 検証
		assertEquals(new BigDecimal("7000.00"), result.getValue());
		// 元のオブジェクトは変更されていないことを確認（不変性）
		assertEquals(new BigDecimal("10000.00"), amount1.getValue());
		assertEquals(new BigDecimal("3000.00"), amount2.getValue());
	}

	@Test
	@DisplayName("異常系：減算でnullを渡すと例外が発生する")
	void testSubtract_異常系_null() {
		// 準備
		IncomeAmount amount = IncomeAmount.from(new BigDecimal("10000.00"));

		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> amount.subtract(null)
		);
	}

	@Test
	@DisplayName("異常系：減算で結果がマイナスになる場合は例外が発生する")
	void testSubtract_異常系_結果がマイナス() {
		// 準備
		IncomeAmount amount1 = IncomeAmount.from(new BigDecimal("5000.00"));
		IncomeAmount amount2 = IncomeAmount.from(new BigDecimal("10000.00"));

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> amount1.subtract(amount2)
		);
		assertTrue(exception.getMessage().contains("マイナス"));
	}
}
