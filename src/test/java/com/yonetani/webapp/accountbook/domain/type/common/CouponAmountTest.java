/**
 * CouponAmount(クーポン金額)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/23 : 1.00.00  新規作成
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
 * CouponAmount(クーポン金額)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("クーポン金額(CouponAmount)のテスト")
class CouponAmountTest {

	@Test
	@DisplayName("正常系：正の金額で生成できる")
	void testFrom_正常系_正の金額() {
		// 実行（正の値500で指定）
		CouponAmount amount = CouponAmount.from(new BigDecimal("1500.00"));

		// 検証
		assertNotNull(amount);
		assertEquals(new BigDecimal("1500.00"), amount.getValue());
		assertEquals("1500.00", amount.toString());
		assertEquals("1,500円", amount.toFormatString());
	}

	@Test
	@DisplayName("正常系：ZERO定数が使用できる")
	void testZERO定数() {
		// 検証
		assertNotNull(CouponAmount.ZERO);
		assertTrue(CouponAmount.ZERO.isZero());
		assertEquals(BigDecimal.ZERO.setScale(2), CouponAmount.ZERO.getValue());
		assertFalse(CouponAmount.ZERO.hasDiscount());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> CouponAmount.from(null)
		);
		assertTrue(exception.getMessage().contains("クーポン金額"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("異常系：負の金額で例外が発生する")
	void testFrom_異常系_負の金額() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> CouponAmount.from(new BigDecimal("-500.00"))
		);
		assertTrue(exception.getMessage().contains("クーポン金額"));
		assertTrue(exception.getMessage().contains("正の値"));
	}

	@Test
	@DisplayName("異常系：スケール値が2以外で例外が発生する")
	void testFrom_異常系_スケール値不正() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> CouponAmount.from(new BigDecimal("500"))
		);
		assertTrue(exception.getMessage().contains("クーポン金額"));
		assertTrue(exception.getMessage().contains("スケール"));
	}

	@Test
	@DisplayName("正常系：hasDiscount()が正しく動作する")
	void testHasDiscount() {
		// 準備
		CouponAmount withDiscount = CouponAmount.from(new BigDecimal("500.00"));
		CouponAmount noDiscount = CouponAmount.ZERO;

		// 検証
		assertTrue(withDiscount.hasDiscount());
		assertFalse(noDiscount.hasDiscount());
	}

	@Test
	@DisplayName("正常系：加算が正しく動作する（割引額が合算される）")
	void testAdd_正常系() {
		// 準備
		CouponAmount amount1 = CouponAmount.from(new BigDecimal("500.00"));
		CouponAmount amount2 = CouponAmount.from(new BigDecimal("300.00"));

		// 実行
		CouponAmount result = amount1.add(amount2);

		// 検証（500 + 300 = 800）
		assertEquals(new BigDecimal("800.00"), result.getValue());
		// 元のオブジェクトは変更されていないことを確認（不変性）
		assertEquals(new BigDecimal("500.00"), amount1.getValue());
		assertEquals(new BigDecimal("300.00"), amount2.getValue());
	}
}
