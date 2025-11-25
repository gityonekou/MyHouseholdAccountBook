/**
 * BalanceAmount(収支金額)のテストクラスです。
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
 * BalanceAmount(収支金額)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("収支金額(BalanceAmount)のテスト")
class BalanceAmountTest {

	@Test
	@DisplayName("正常系：正の金額で生成できる")
	void testFrom_正常系_正の金額() {
		// 実行
		BalanceAmount amount = BalanceAmount.from(new BigDecimal("10000.00"));

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
		BalanceAmount amount = BalanceAmount.from(BigDecimal.ZERO.setScale(2));

		// 検証
		assertNotNull(amount);
		assertEquals(BigDecimal.ZERO.setScale(2), amount.getValue());
		assertTrue(amount.isZero());
	}

	@Test
	@DisplayName("正常系：負の金額で生成できる（赤字）")
	void testFrom_正常系_負の金額() {
		// 実行
		BalanceAmount amount = BalanceAmount.from(new BigDecimal("-5000.00"));

		// 検証
		assertNotNull(amount);
		assertEquals(new BigDecimal("-5000.00"), amount.getValue());
		assertTrue(amount.isNegative());
		assertTrue(amount.isDeficit());
	}

	@Test
	@DisplayName("正常系：ZERO定数が使用できる")
	void testZERO定数() {
		// 検証
		assertNotNull(BalanceAmount.ZERO);
		assertTrue(BalanceAmount.ZERO.isZero());
		assertEquals(BigDecimal.ZERO.setScale(2), BalanceAmount.ZERO.getValue());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> BalanceAmount.from(null)
		);
		assertTrue(exception.getMessage().contains("収支金額"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("異常系：スケール値が2以外で例外が発生する")
	void testFrom_異常系_スケール値不正() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> BalanceAmount.from(new BigDecimal("10000"))
		);
		assertTrue(exception.getMessage().contains("収支金額"));
		assertTrue(exception.getMessage().contains("スケール"));
	}

	@Test
	@DisplayName("正常系：calculate()で黒字が正しく計算される")
	void testCalculate_正常系_黒字() {
		// 準備
		IncomeAmount income = IncomeAmount.from(new BigDecimal("100000.00"));
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("80000.00"));

		// 実行
		BalanceAmount balance = BalanceAmount.calculate(income, expenditure);

		// 検証
		assertEquals(new BigDecimal("20000.00"), balance.getValue());
		assertTrue(balance.isSurplus());
		assertFalse(balance.isDeficit());
	}

	@Test
	@DisplayName("正常系：calculate()で赤字が正しく計算される")
	void testCalculate_正常系_赤字() {
		// 準備
		IncomeAmount income = IncomeAmount.from(new BigDecimal("80000.00"));
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("100000.00"));

		// 実行
		BalanceAmount balance = BalanceAmount.calculate(income, expenditure);

		// 検証
		assertEquals(new BigDecimal("-20000.00"), balance.getValue());
		assertTrue(balance.isDeficit());
		assertFalse(balance.isSurplus());
	}

	@Test
	@DisplayName("正常系：calculate()で収支0が正しく計算される")
	void testCalculate_正常系_収支0() {
		// 準備
		IncomeAmount income = IncomeAmount.from(new BigDecimal("100000.00"));
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("100000.00"));

		// 実行
		BalanceAmount balance = BalanceAmount.calculate(income, expenditure);

		// 検証
		assertEquals(BigDecimal.ZERO.setScale(2), balance.getValue());
		assertTrue(balance.isZero());
		assertFalse(balance.isSurplus());
		assertFalse(balance.isDeficit());
	}

	@Test
	@DisplayName("正常系：isDeficitが正しく動作する")
	void testIsDeficit() {
		// 準備
		BalanceAmount deficit = BalanceAmount.from(new BigDecimal("-1000.00"));
		BalanceAmount surplus = BalanceAmount.from(new BigDecimal("1000.00"));
		BalanceAmount zero = BalanceAmount.ZERO;

		// 検証
		assertTrue(deficit.isDeficit());
		assertFalse(surplus.isDeficit());
		assertFalse(zero.isDeficit());
	}

	@Test
	@DisplayName("正常系：isSurplusが正しく動作する")
	void testIsSurplus() {
		// 準備
		BalanceAmount deficit = BalanceAmount.from(new BigDecimal("-1000.00"));
		BalanceAmount surplus = BalanceAmount.from(new BigDecimal("1000.00"));
		BalanceAmount zero = BalanceAmount.ZERO;

		// 検証
		assertFalse(deficit.isSurplus());
		assertTrue(surplus.isSurplus());
		assertFalse(zero.isSurplus());
	}

	@Test
	@DisplayName("正常系：比較が正しく動作する（大きい）")
	void testCompareTo_正常系_大きい() {
		// 準備
		BalanceAmount amount1 = BalanceAmount.from(new BigDecimal("10000.00"));
		BalanceAmount amount2 = BalanceAmount.from(new BigDecimal("5000.00"));

		// 実行 & 検証
		assertTrue(amount1.compareTo(amount2) > 0);
	}

	@Test
	@DisplayName("正常系：比較が正しく動作する（等しい）")
	void testCompareTo_正常系_等しい() {
		// 準備
		BalanceAmount amount1 = BalanceAmount.from(new BigDecimal("10000.00"));
		BalanceAmount amount2 = BalanceAmount.from(new BigDecimal("10000.00"));

		// 実行 & 検証
		assertEquals(0, amount1.compareTo(amount2));
	}

	@Test
	@DisplayName("正常系：比較が正しく動作する（小さい）")
	void testCompareTo_正常系_小さい() {
		// 準備
		BalanceAmount amount1 = BalanceAmount.from(new BigDecimal("5000.00"));
		BalanceAmount amount2 = BalanceAmount.from(new BigDecimal("10000.00"));

		// 実行 & 検証
		assertTrue(amount1.compareTo(amount2) < 0);
	}

	@Test
	@DisplayName("正常系：負の値同士の比較が正しく動作する")
	void testCompareTo_正常系_負の値同士() {
		// 準備
		BalanceAmount amount1 = BalanceAmount.from(new BigDecimal("-5000.00"));
		BalanceAmount amount2 = BalanceAmount.from(new BigDecimal("-10000.00"));

		// 実行 & 検証（-5000 > -10000）
		assertTrue(amount1.compareTo(amount2) > 0);
	}

	@Test
	@DisplayName("正常系：isZeroが正しく動作する")
	void testIsZero() {
		// 検証
		assertTrue(BalanceAmount.ZERO.isZero());
		assertFalse(BalanceAmount.from(new BigDecimal("1.00")).isZero());
		assertFalse(BalanceAmount.from(new BigDecimal("-1.00")).isZero());
	}

	@Test
	@DisplayName("正常系：isPositiveが正しく動作する")
	void testIsPositive() {
		// 検証
		assertFalse(BalanceAmount.ZERO.isPositive());
		assertTrue(BalanceAmount.from(new BigDecimal("1.00")).isPositive());
		assertFalse(BalanceAmount.from(new BigDecimal("-1.00")).isPositive());
	}

	@Test
	@DisplayName("正常系：isNegativeが正しく動作する")
	void testIsNegative() {
		// 検証
		assertFalse(BalanceAmount.ZERO.isNegative());
		assertFalse(BalanceAmount.from(new BigDecimal("1.00")).isNegative());
		assertTrue(BalanceAmount.from(new BigDecimal("-1.00")).isNegative());
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		// 準備
		BalanceAmount amount1 = BalanceAmount.from(new BigDecimal("10000.00"));
		BalanceAmount amount2 = BalanceAmount.from(new BigDecimal("10000.00"));
		BalanceAmount amount3 = BalanceAmount.from(new BigDecimal("5000.00"));

		// 検証
		assertEquals(amount1, amount2);
		assertNotEquals(amount1, amount3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		// 準備
		BalanceAmount amount1 = BalanceAmount.from(new BigDecimal("10000.00"));
		BalanceAmount amount2 = BalanceAmount.from(new BigDecimal("10000.00"));

		// 検証（同じ値なら同じハッシュコード）
		assertEquals(amount1.hashCode(), amount2.hashCode());
	}

	@Test
	@DisplayName("正常系：toStringは値の文字列表現を返す")
	void testToString() {
		// 準備
		BalanceAmount positiveAmount = BalanceAmount.from(new BigDecimal("12345.67"));
		BalanceAmount negativeAmount = BalanceAmount.from(new BigDecimal("-12345.67"));

		// 検証（BigDecimalの文字列表現）
		assertEquals("12345.67", positiveAmount.toString());
		assertEquals("-12345.67", negativeAmount.toString());
	}

	@Test
	@DisplayName("正常系：toFormatStringはフォーマット済み文字列を返す")
	void testToFormatString() {
		// 準備
		BalanceAmount amount1 = BalanceAmount.from(new BigDecimal("12345.67"));
		BalanceAmount amount2 = BalanceAmount.from(new BigDecimal("1000000.00"));
		BalanceAmount amount3 = BalanceAmount.from(new BigDecimal("-12345.67"));

		// 検証（カンマ区切り+円表記、スケール0で四捨五入）
		assertEquals("12,346円", amount1.toFormatString());
		assertEquals("1,000,000円", amount2.toFormatString());
		assertEquals("-12,346円", amount3.toFormatString());
	}

	@Test
	@DisplayName("正常系：toIntegerValueは整数値を返す")
	void testToIntegerValue() {
		// 準備
		BalanceAmount amount1 = BalanceAmount.from(new BigDecimal("12345.67"));
		BalanceAmount amount2 = BalanceAmount.from(new BigDecimal("-12345.67"));
		BalanceAmount amount3 = BalanceAmount.from(new BigDecimal("12345.44"));
		BalanceAmount amount4 = BalanceAmount.from(new BigDecimal("-12345.50"));

		// 検証（スケール0で四捨五入）
		assertEquals(12346L, amount1.toIntegerValue());
		assertEquals(-12346L, amount2.toIntegerValue());
		assertEquals(12345L, amount3.toIntegerValue()); // 0.44 -> 切り捨て
		assertEquals(-12346L, amount4.toIntegerValue()); // -0.50 -> 切り上げ
	}

	@Test
	@DisplayName("正常系：toIntegerStringは整数の文字列を返す")
	void testToIntegerString() {
		// 準備
		BalanceAmount amount1 = BalanceAmount.from(new BigDecimal("12345.67"));
		BalanceAmount amount2 = BalanceAmount.from(new BigDecimal("-12345.67"));

		// 検証（カンマ区切りなしの整数値文字列）
		assertEquals("12346", amount1.toIntegerString());
		assertEquals("-12346", amount2.toIntegerString());
	}
}
