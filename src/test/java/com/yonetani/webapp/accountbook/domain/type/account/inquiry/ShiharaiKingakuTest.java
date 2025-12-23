/**
 * ShiharaiKingaku(支払金額)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * ShiharaiKingaku(支払金額)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("支払金額(ShiharaiKingaku)のテスト")
class ShiharaiKingakuTest {

	@Test
	@DisplayName("正常系：正の金額で生成できる")
	void testFrom_正常系_正の金額() {
		// 実行
		ShiharaiKingaku amount = ShiharaiKingaku.from(new BigDecimal("10000.00"));

		// 検証
		assertNotNull(amount);
		assertEquals(new BigDecimal("10000.00"), amount.getValue());
		assertEquals("10000.00", amount.toString());
		assertEquals("10,000円", amount.toFormatString());
	}

	@Test
	@DisplayName("正常系：0円で生成できる")
	void testFrom_正常系_0円() {
		// 実行
		ShiharaiKingaku amount = ShiharaiKingaku.from(BigDecimal.ZERO.setScale(2));

		// 検証
		assertNotNull(amount);
		assertEquals(BigDecimal.ZERO.setScale(2), amount.getValue());
		assertTrue(amount.isZero());
	}

	@Test
	@DisplayName("正常系：ZERO定数が使用できる")
	void testZERO定数() {
		// 検証
		assertNotNull(ShiharaiKingaku.ZERO);
		assertTrue(ShiharaiKingaku.ZERO.isZero());
		assertEquals(BigDecimal.ZERO.setScale(2), ShiharaiKingaku.ZERO.getValue());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> ShiharaiKingaku.from((BigDecimal)null)
		);
		assertTrue(exception.getMessage().contains("支払金額"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("異常系：負の金額で例外が発生する")
	void testFrom_異常系_負の金額() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> ShiharaiKingaku.from(new BigDecimal("-1000.00"))
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
			() -> ShiharaiKingaku.from(new BigDecimal("10000"))
		);
		assertTrue(exception.getMessage().contains("支払金額"));
		assertTrue(exception.getMessage().contains("スケール"));
	}

	@Test
	@DisplayName("正常系：比較が正しく動作する（大きい）")
	void testCompareTo_正常系_大きい() {
		// 準備
		ShiharaiKingaku amount1 = ShiharaiKingaku.from(new BigDecimal("10000.00"));
		ShiharaiKingaku amount2 = ShiharaiKingaku.from(new BigDecimal("5000.00"));

		// 実行 & 検証
		assertTrue(amount1.compareTo(amount2) > 0);
	}

	@Test
	@DisplayName("正常系：比較が正しく動作する（等しい）")
	void testCompareTo_正常系_等しい() {
		// 準備
		ShiharaiKingaku amount1 = ShiharaiKingaku.from(new BigDecimal("10000.00"));
		ShiharaiKingaku amount2 = ShiharaiKingaku.from(new BigDecimal("10000.00"));

		// 実行 & 検証
		assertEquals(0, amount1.compareTo(amount2));
	}

	@Test
	@DisplayName("正常系：比較が正しく動作する（小さい）")
	void testCompareTo_正常系_小さい() {
		// 準備
		ShiharaiKingaku amount1 = ShiharaiKingaku.from(new BigDecimal("5000.00"));
		ShiharaiKingaku amount2 = ShiharaiKingaku.from(new BigDecimal("10000.00"));

		// 実行 & 検証
		assertTrue(amount1.compareTo(amount2) < 0);
	}

	@Test
	@DisplayName("正常系：isZeroが正しく動作する")
	void testIsZero() {
		// 検証
		assertTrue(ShiharaiKingaku.ZERO.isZero());
		assertFalse(ShiharaiKingaku.from(new BigDecimal("1.00")).isZero());
	}

	@Test
	@DisplayName("正常系：isPositiveが正しく動作する")
	void testIsPositive() {
		// 検証
		assertFalse(ShiharaiKingaku.ZERO.isPositive());
		assertTrue(ShiharaiKingaku.from(new BigDecimal("1.00")).isPositive());
	}

	@Test
	@DisplayName("正常系：isNegativeが正しく動作する（支払金額は常にfalse）")
	void testIsNegative() {
		// 検証（支払金額は負の値を持てないため、常にfalse）
		assertFalse(ShiharaiKingaku.ZERO.isNegative());
		assertFalse(ShiharaiKingaku.from(new BigDecimal("1.00")).isNegative());
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		// 準備
		ShiharaiKingaku amount1 = ShiharaiKingaku.from(new BigDecimal("10000.00"));
		ShiharaiKingaku amount2 = ShiharaiKingaku.from(new BigDecimal("10000.00"));
		ShiharaiKingaku amount3 = ShiharaiKingaku.from(new BigDecimal("5000.00"));

		// 検証
		assertEquals(amount1, amount2);
		assertNotEquals(amount1, amount3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		// 準備
		ShiharaiKingaku amount1 = ShiharaiKingaku.from(new BigDecimal("10000.00"));
		ShiharaiKingaku amount2 = ShiharaiKingaku.from(new BigDecimal("10000.00"));

		// 検証（同じ値なら同じハッシュコード）
		assertEquals(amount1.hashCode(), amount2.hashCode());
	}

	@Test
	@DisplayName("正常系：toStringは値の文字列表現を返す")
	void testToString() {
		// 準備
		ShiharaiKingaku amount = ShiharaiKingaku.from(new BigDecimal("12345.67"));

		// 検証（BigDecimalの文字列表現）
		assertEquals("12345.67", amount.toString());
	}

	@Test
	@DisplayName("正常系：toFormatStringはフォーマット済み文字列を返す")
	void testToFormatString() {
		// 準備
		ShiharaiKingaku amount1 = ShiharaiKingaku.from(new BigDecimal("12345.67"));
		ShiharaiKingaku amount2 = ShiharaiKingaku.from(new BigDecimal("1000000.00"));

		// 検証（カンマ区切り+円表記、スケール0で四捨五入）
		assertEquals("12,346円", amount1.toFormatString());
		assertEquals("1,000,000円", amount2.toFormatString());
	}

	@Test
	@DisplayName("正常系：toIntegerValueは整数値を返す")
	void testToIntegerValue() {
		// 準備
		ShiharaiKingaku amount1 = ShiharaiKingaku.from(new BigDecimal("12345.67"));
		ShiharaiKingaku amount2 = ShiharaiKingaku.from(new BigDecimal("1000000.00"));
		ShiharaiKingaku amount3 = ShiharaiKingaku.from(new BigDecimal("12345.44"));
		ShiharaiKingaku amount4 = ShiharaiKingaku.from(new BigDecimal("12345.50"));

		// 検証（スケール0で四捨五入）
		assertEquals(12346L, amount1.toIntegerValue());
		assertEquals(1000000L, amount2.toIntegerValue());
		assertEquals(12345L, amount3.toIntegerValue()); // 0.44 -> 切り捨て
		assertEquals(12346L, amount4.toIntegerValue()); // 0.50 -> 切り上げ
	}

	@Test
	@DisplayName("正常系：toIntegerStringは整数の文字列を返す")
	void testToIntegerString() {
		// 準備
		ShiharaiKingaku amount1 = ShiharaiKingaku.from(new BigDecimal("12345.67"));
		ShiharaiKingaku amount2 = ShiharaiKingaku.from(new BigDecimal("1000000.00"));

		// 検証（カンマ区切りなしの整数値文字列）
		assertEquals("12346", amount1.toIntegerString());
		assertEquals("1000000", amount2.toIntegerString());
	}
}
