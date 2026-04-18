/**
 * FixedCostPaymentAmount(支払金額)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.fixedcost;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * FixedCostPaymentAmount(支払金額)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@DisplayName("支払金額(FixedCostPaymentAmount)のテスト")
class FixedCostPaymentAmountTest {

	@Test
	@DisplayName("正常系：BigDecimalの正の金額で生成できる")
	void testFrom_正常系_正の金額_BigDecimal() {
		// 実行(BigDecimal)
		FixedCostPaymentAmount amount = FixedCostPaymentAmount.from(new BigDecimal("10000.00"));

		// 検証
		assertNotNull(amount);
		assertEquals(new BigDecimal("10000.00"), amount.getValue());
		assertEquals("10000.00", amount.toString());
		assertEquals("10,000円", amount.toFormatString());
	}

	@Test
	@DisplayName("正常系：Integerの正の金額で生成できる")
	void testFrom_正常系_正の金額_Integer() {
		// 実行(BigDecimal)
		FixedCostPaymentAmount amount = FixedCostPaymentAmount.from(10000);

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
		assertNotNull(FixedCostPaymentAmount.ZERO);
		assertTrue(FixedCostPaymentAmount.ZERO.isZero());
		assertEquals(BigDecimal.ZERO.setScale(2), FixedCostPaymentAmount.ZERO.getValue());
	}

	@Test
	@DisplayName("異常系：(BigDecimal)null値で例外が発生する")
	void testFrom_異常系_BigDecimal_null値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> FixedCostPaymentAmount.from((BigDecimal)null)
		);
		assertTrue(exception.getMessage().contains("支払金額"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("異常系：(Integer)null値で例外が発生する")
	void testFrom_異常系_Integer_null値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> FixedCostPaymentAmount.from((Integer)null)
		);
		assertTrue(exception.getMessage().contains("支払金額"));
		assertTrue(exception.getMessage().contains("null"));
	}
	
	@Test
	@DisplayName("異常系：BigDecimalの負の金額で例外が発生する")
	void testFrom_異常系_負の金額_BigDecimal() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> FixedCostPaymentAmount.from(new BigDecimal("-1000.00"))
		);
		assertTrue(exception.getMessage().contains("支払金額"));
		assertTrue(exception.getMessage().contains("マイナス"));
	}

	@Test
	@DisplayName("異常系：Integerの負の金額で例外が発生する")
	void testFrom_異常系_負の金額_Integer() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> FixedCostPaymentAmount.from(-1000)
		);
		assertTrue(exception.getMessage().contains("支払金額"));
		assertTrue(exception.getMessage().contains("マイナス"));
	}
	
	@Test
	@DisplayName("異常系：スケール値が2以外で例外が発生する")
	void testFrom_異常系_スケール値不正() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> FixedCostPaymentAmount.from(new BigDecimal("10000"))
		);
		assertTrue(exception.getMessage().contains("支払金額"));
		assertTrue(exception.getMessage().contains("スケール"));
	}
}
