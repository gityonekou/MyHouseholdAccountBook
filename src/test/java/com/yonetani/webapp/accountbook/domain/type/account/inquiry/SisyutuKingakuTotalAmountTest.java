/**
 * SisyutuKingakuTotalAmount(支出金額合計)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/15 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * SisyutuKingakuTotalAmount(支出金額合計)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("支出金額合計(SisyutuKingakuTotalAmount)のテスト")
class SisyutuKingakuTotalAmountTest {

	@Test
	@DisplayName("正常系：正の金額で生成できる")
	void testFrom_正常系_正の金額() {
		// 実行
		SisyutuKingakuTotalAmount amount = SisyutuKingakuTotalAmount.from(new BigDecimal("10000.00"));

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
		SisyutuKingakuTotalAmount amount = SisyutuKingakuTotalAmount.from(BigDecimal.ZERO.setScale(2));

		// 検証
		assertNotNull(amount);
		assertEquals(BigDecimal.ZERO.setScale(2), amount.getValue());
		assertTrue(amount.isZero());
	}

	@Test
	@DisplayName("正常系：ZERO定数が使用できる")
	void testZERO定数() {
		// 検証
		assertNotNull(SisyutuKingakuTotalAmount.ZERO);
		assertTrue(SisyutuKingakuTotalAmount.ZERO.isZero());
		assertEquals(BigDecimal.ZERO.setScale(2), SisyutuKingakuTotalAmount.ZERO.getValue());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> SisyutuKingakuTotalAmount.from((BigDecimal)null)
		);
		assertTrue(exception.getMessage().contains("支出金額合計"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("異常系：負の金額で例外が発生する")
	void testFrom_異常系_負の金額() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> SisyutuKingakuTotalAmount.from(new BigDecimal("-1000.00"))
		);
		assertTrue(exception.getMessage().contains("支出金額合計"));
		assertTrue(exception.getMessage().contains("マイナス"));
	}

	@Test
	@DisplayName("異常系：スケール値が2以外で例外が発生する")
	void testFrom_異常系_スケール値不正() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> SisyutuKingakuTotalAmount.from(new BigDecimal("10000"))
		);
		assertTrue(exception.getMessage().contains("支出金額合計"));
		assertTrue(exception.getMessage().contains("スケール"));
	}

	@Test
	@DisplayName("正常系：支出金額から生成できる")
	void testFrom_正常系_支出金額から() {
		// 準備
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("10000.00"));

		// 実行
		SisyutuKingakuTotalAmount total = SisyutuKingakuTotalAmount.from(expenditure);

		// 検証
		assertNotNull(total);
		assertEquals(new BigDecimal("10000.00"), total.getValue());
	}

	@Test
	@DisplayName("正常系：加算が正しく動作する")
	void testAdd_正常系() {
		// 準備
		SisyutuKingakuTotalAmount total = SisyutuKingakuTotalAmount.from(new BigDecimal("10000.00"));
		ExpenditureAmount add = ExpenditureAmount.from(new BigDecimal("5000.00"));

		// 実行
		SisyutuKingakuTotalAmount result = total.add(add);

		// 検証
		assertEquals(new BigDecimal("15000.00"), result.getValue());
		// 不変性の確認
		assertEquals(new BigDecimal("10000.00"), total.getValue());
	}

	@Test
	@DisplayName("異常系：加算でnullを渡すと例外が発生する")
	void testAdd_異常系_null() {
		// 準備
		SisyutuKingakuTotalAmount total = SisyutuKingakuTotalAmount.from(new BigDecimal("10000.00"));

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> total.add(null)
		);
		assertTrue(exception.getMessage().contains("加算対象の金額"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("正常系：比較が正しく動作する（大きい）")
	void testCompareTo_正常系_大きい() {
		// 準備
		SisyutuKingakuTotalAmount amount1 = SisyutuKingakuTotalAmount.from(new BigDecimal("10000.00"));
		SisyutuKingakuTotalAmount amount2 = SisyutuKingakuTotalAmount.from(new BigDecimal("5000.00"));

		// 実行 & 検証
		assertTrue(amount1.compareTo(amount2) > 0);
	}

	@Test
	@DisplayName("正常系：比較が正しく動作する（等しい）")
	void testCompareTo_正常系_等しい() {
		// 準備
		SisyutuKingakuTotalAmount amount1 = SisyutuKingakuTotalAmount.from(new BigDecimal("10000.00"));
		SisyutuKingakuTotalAmount amount2 = SisyutuKingakuTotalAmount.from(new BigDecimal("10000.00"));

		// 実行 & 検証
		assertEquals(0, amount1.compareTo(amount2));
	}

	@Test
	@DisplayName("正常系：比較が正しく動作する（小さい）")
	void testCompareTo_正常系_小さい() {
		// 準備
		SisyutuKingakuTotalAmount amount1 = SisyutuKingakuTotalAmount.from(new BigDecimal("5000.00"));
		SisyutuKingakuTotalAmount amount2 = SisyutuKingakuTotalAmount.from(new BigDecimal("10000.00"));

		// 実行 & 検証
		assertTrue(amount1.compareTo(amount2) < 0);
	}

	@Test
	@DisplayName("正常系：isZeroが正しく動作する")
	void testIsZero() {
		// 検証
		assertTrue(SisyutuKingakuTotalAmount.ZERO.isZero());
		assertFalse(SisyutuKingakuTotalAmount.from(new BigDecimal("1.00")).isZero());
	}

	@Test
	@DisplayName("正常系：isPositiveが正しく動作する")
	void testIsPositive() {
		// 検証
		assertFalse(SisyutuKingakuTotalAmount.ZERO.isPositive());
		assertTrue(SisyutuKingakuTotalAmount.from(new BigDecimal("1.00")).isPositive());
	}

	@Test
	@DisplayName("正常系：isNegativeが正しく動作する（支出金額合計は常にfalse）")
	void testIsNegative() {
		// 検証（支出金額合計は負の値を持てないため、常にfalse）
		assertFalse(SisyutuKingakuTotalAmount.ZERO.isNegative());
		assertFalse(SisyutuKingakuTotalAmount.from(new BigDecimal("1.00")).isNegative());
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		// 準備
		SisyutuKingakuTotalAmount amount1 = SisyutuKingakuTotalAmount.from(new BigDecimal("10000.00"));
		SisyutuKingakuTotalAmount amount2 = SisyutuKingakuTotalAmount.from(new BigDecimal("10000.00"));
		SisyutuKingakuTotalAmount amount3 = SisyutuKingakuTotalAmount.from(new BigDecimal("5000.00"));

		// 検証
		assertEquals(amount1, amount2);
		assertNotEquals(amount1, amount3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		// 準備
		SisyutuKingakuTotalAmount amount1 = SisyutuKingakuTotalAmount.from(new BigDecimal("10000.00"));
		SisyutuKingakuTotalAmount amount2 = SisyutuKingakuTotalAmount.from(new BigDecimal("10000.00"));

		// 検証（同じ値なら同じハッシュコード）
		assertEquals(amount1.hashCode(), amount2.hashCode());
	}

	@Test
	@DisplayName("正常系：toStringは値の文字列表現を返す")
	void testToString() {
		// 準備
		SisyutuKingakuTotalAmount amount = SisyutuKingakuTotalAmount.from(new BigDecimal("12345.67"));

		// 検証（BigDecimalの文字列表現）
		assertEquals("12345.67", amount.toString());
	}

	@Test
	@DisplayName("正常系：toFormatStringはフォーマット済み文字列を返す")
	void testToFormatString() {
		// 準備
		SisyutuKingakuTotalAmount amount1 = SisyutuKingakuTotalAmount.from(new BigDecimal("12345.67"));
		SisyutuKingakuTotalAmount amount2 = SisyutuKingakuTotalAmount.from(new BigDecimal("1000000.00"));

		// 検証（カンマ区切り+円表記、スケール0で四捨五入）
		assertEquals("12,346円", amount1.toFormatString());
		assertEquals("1,000,000円", amount2.toFormatString());
	}

	@Test
	@DisplayName("正常系：toIntegerValueは整数値を返す")
	void testToIntegerValue() {
		// 準備
		SisyutuKingakuTotalAmount amount1 = SisyutuKingakuTotalAmount.from(new BigDecimal("12345.67"));
		SisyutuKingakuTotalAmount amount2 = SisyutuKingakuTotalAmount.from(new BigDecimal("1000000.00"));

		// 検証（スケール0で四捨五入）
		assertEquals(12346L, amount1.toIntegerValue());
		assertEquals(1000000L, amount2.toIntegerValue());
	}

	@Test
	@DisplayName("正常系：toIntegerStringは整数の文字列を返す")
	void testToIntegerString() {
		// 準備
		SisyutuKingakuTotalAmount amount1 = SisyutuKingakuTotalAmount.from(new BigDecimal("12345.67"));
		SisyutuKingakuTotalAmount amount2 = SisyutuKingakuTotalAmount.from(new BigDecimal("1000000.00"));

		// 検証（カンマ区切りなしの整数値文字列）
		assertEquals("12346", amount1.toIntegerString());
		assertEquals("1000000", amount2.toIntegerString());
	}
}
