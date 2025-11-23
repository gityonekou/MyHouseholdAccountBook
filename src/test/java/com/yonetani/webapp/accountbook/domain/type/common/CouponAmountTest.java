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
		CouponAmount amount = CouponAmount.from(new BigDecimal("500.00"));

		// 検証（内部的にはマイナス値として保持）
		assertNotNull(amount);
		assertEquals(new BigDecimal("-500.00"), amount.getValue());
		assertEquals("-500.00", amount.toString());
		// getDiscountAmount()は正の値を返す
		assertEquals(new BigDecimal("500.00"), amount.getDiscountAmount());
		// toFormatString()も正の値で表示
		assertEquals("500円", amount.toFormatString());
	}

	@Test
	@DisplayName("正常系：0円で生成できる")
	void testFrom_正常系_0円() {
		// 実行
		CouponAmount amount = CouponAmount.from(BigDecimal.ZERO.setScale(2));

		// 検証
		assertNotNull(amount);
		assertEquals(BigDecimal.ZERO.setScale(2), amount.getValue());
		assertTrue(amount.isZero());
		assertFalse(amount.hasDiscount());
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
	@DisplayName("正常系：getDiscountAmount()が正の値を返す")
	void testGetDiscountAmount() {
		// 準備
		CouponAmount amount = CouponAmount.from(new BigDecimal("1000.00"));

		// 実行
		BigDecimal discountAmount = amount.getDiscountAmount();

		// 検証（内部はマイナスだが、getDiscountAmount()は正の値を返す）
		assertEquals(new BigDecimal("1000.00"), discountAmount);
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

		// 検証（内部的には-500 + -300 = -800）
		assertEquals(new BigDecimal("-800.00"), result.getValue());
		assertEquals(new BigDecimal("800.00"), result.getDiscountAmount());
		// 元のオブジェクトは変更されていないことを確認（不変性）
		assertEquals(new BigDecimal("-500.00"), amount1.getValue());
		assertEquals(new BigDecimal("-300.00"), amount2.getValue());
	}

	@Test
	@DisplayName("異常系：加算でnullを渡すと例外が発生する")
	void testAdd_異常系_null() {
		// 準備
		CouponAmount amount = CouponAmount.from(new BigDecimal("500.00"));

		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> amount.add(null)
		);
	}

	@Test
	@DisplayName("正常系：比較が正しく動作する（大きい）")
	void testCompareTo_正常系_大きい() {
		// 準備（内部的には-300 > -500）
		CouponAmount amount1 = CouponAmount.from(new BigDecimal("300.00"));
		CouponAmount amount2 = CouponAmount.from(new BigDecimal("500.00"));

		// 実行 & 検証（割引額が小さい方が「大きい」）
		assertTrue(amount1.compareTo(amount2) > 0);
	}

	@Test
	@DisplayName("正常系：比較が正しく動作する（等しい）")
	void testCompareTo_正常系_等しい() {
		// 準備
		CouponAmount amount1 = CouponAmount.from(new BigDecimal("500.00"));
		CouponAmount amount2 = CouponAmount.from(new BigDecimal("500.00"));

		// 実行 & 検証
		assertEquals(0, amount1.compareTo(amount2));
	}

	@Test
	@DisplayName("正常系：比較が正しく動作する（小さい）")
	void testCompareTo_正常系_小さい() {
		// 準備（内部的には-500 < -300）
		CouponAmount amount1 = CouponAmount.from(new BigDecimal("500.00"));
		CouponAmount amount2 = CouponAmount.from(new BigDecimal("300.00"));

		// 実行 & 検証（割引額が大きい方が「小さい」）
		assertTrue(amount1.compareTo(amount2) < 0);
	}

	@Test
	@DisplayName("正常系：isZeroが正しく動作する")
	void testIsZero() {
		// 検証
		assertTrue(CouponAmount.ZERO.isZero());
		assertFalse(CouponAmount.from(new BigDecimal("1.00")).isZero());
	}

	@Test
	@DisplayName("正常系：isPositiveが正しく動作する（常にfalse）")
	void testIsPositive() {
		// 検証（内部的にマイナス値なので、常にfalse）
		assertFalse(CouponAmount.ZERO.isPositive());
		assertFalse(CouponAmount.from(new BigDecimal("500.00")).isPositive());
	}

	@Test
	@DisplayName("正常系：isNegativeが正しく動作する")
	void testIsNegative() {
		// 検証（内部的にマイナス値なので、0以外はtrue）
		assertFalse(CouponAmount.ZERO.isNegative());
		assertTrue(CouponAmount.from(new BigDecimal("500.00")).isNegative());
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		// 準備
		CouponAmount amount1 = CouponAmount.from(new BigDecimal("500.00"));
		CouponAmount amount2 = CouponAmount.from(new BigDecimal("500.00"));
		CouponAmount amount3 = CouponAmount.from(new BigDecimal("300.00"));

		// 検証
		assertEquals(amount1, amount2);
		assertNotEquals(amount1, amount3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		// 準備
		CouponAmount amount1 = CouponAmount.from(new BigDecimal("500.00"));
		CouponAmount amount2 = CouponAmount.from(new BigDecimal("500.00"));

		// 検証（同じ値なら同じハッシュコード）
		assertEquals(amount1.hashCode(), amount2.hashCode());
	}

	@Test
	@DisplayName("正常系：toStringは内部値（マイナス値）の文字列表現を返す")
	void testToString() {
		// 準備
		CouponAmount amount = CouponAmount.from(new BigDecimal("500.00"));

		// 検証（内部的にはマイナス値として保持）
		assertEquals("-500.00", amount.toString());
	}

	@Test
	@DisplayName("正常系：toFormatStringは割引額（正の値）のフォーマット済み文字列を返す")
	void testToFormatString() {
		// 準備
		CouponAmount amount1 = CouponAmount.from(new BigDecimal("500.00"));
		CouponAmount amount2 = CouponAmount.from(new BigDecimal("1234.56"));
		CouponAmount amount3 = CouponAmount.ZERO;

		// 検証（カンマ区切り+円表記、スケール0で四捨五入、正の値で表示）
		assertEquals("500円", amount1.toFormatString());
		assertEquals("1,235円", amount2.toFormatString());
		assertEquals("0円", amount3.toFormatString());
	}
}
