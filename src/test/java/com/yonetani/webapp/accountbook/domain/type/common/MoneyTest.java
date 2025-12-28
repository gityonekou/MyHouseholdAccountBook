/**
 * Money抽象クラスのテストクラスです。
 * テスト対象の抽象クラスをテストするため、テスト用の具象クラスを使用します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/27 : 1.00.00  新規作成
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
 * Money抽象クラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("金額基底クラス(Money)のテスト")
class MoneyTest {

	// テスト用の具象クラス
	private static class TestMoney extends Money {
		private TestMoney(BigDecimal value) {
			super(value);
		}

		public static TestMoney from(BigDecimal value) {
			validate(value, "テスト金額");
			return new TestMoney(value);
		}

		public TestMoney add(TestMoney other) {
			return new TestMoney(super.add(other));
		}

		public TestMoney subtract(TestMoney other) {
			return new TestMoney(super.subtract(other));
		}
	}

	@Test
	@DisplayName("正常系：正の金額で生成できる")
	void testFrom_正常系_正の金額() {
		// 実行
		TestMoney amount = TestMoney.from(new BigDecimal("10000.00"));

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
		TestMoney amount = TestMoney.from(BigDecimal.ZERO.setScale(2));

		// 検証
		assertNotNull(amount);
		assertEquals(BigDecimal.ZERO.setScale(2), amount.getValue());
		assertTrue(amount.isZero());
	}

	@Test
	@DisplayName("正常系：負の金額で生成できる")
	void testFrom_正常系_負の金額() {
		// 実行
		TestMoney amount = TestMoney.from(new BigDecimal("-5000.00"));

		// 検証
		assertNotNull(amount);
		assertEquals(new BigDecimal("-5000.00"), amount.getValue());
		assertTrue(amount.isNegative());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestMoney.from(null)
		);
		assertTrue(exception.getMessage().contains("テスト金額"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("異常系：スケール値が2以外で例外が発生する")
	void testFrom_異常系_スケール値不正() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestMoney.from(new BigDecimal("10000"))
		);
		assertTrue(exception.getMessage().contains("テスト金額"));
		assertTrue(exception.getMessage().contains("スケール"));
	}

	@Test
	@DisplayName("正常系：加算が正しく動作する")
	void testAdd_正常系() {
		// 準備
		TestMoney amount1 = TestMoney.from(new BigDecimal("10000.00"));
		TestMoney amount2 = TestMoney.from(new BigDecimal("5000.00"));

		// 実行
		TestMoney result = amount1.add(amount2);

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
		TestMoney amount = TestMoney.from(new BigDecimal("10000.00"));

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
		TestMoney amount1 = TestMoney.from(new BigDecimal("10000.00"));
		TestMoney amount2 = TestMoney.from(new BigDecimal("3000.00"));

		// 実行
		TestMoney result = amount1.subtract(amount2);

		// 検証
		assertEquals(new BigDecimal("7000.00"), result.getValue());
		// 元のオブジェクトは変更されていないことを確認（不変性）
		assertEquals(new BigDecimal("10000.00"), amount1.getValue());
		assertEquals(new BigDecimal("3000.00"), amount2.getValue());
	}

	@Test
	@DisplayName("正常系：減算で結果がマイナスになる")
	void testSubtract_正常系_結果がマイナス() {
		// 準備
		TestMoney amount1 = TestMoney.from(new BigDecimal("5000.00"));
		TestMoney amount2 = TestMoney.from(new BigDecimal("10000.00"));

		// 実行
		TestMoney result = amount1.subtract(amount2);

		// 検証（Moneyは負の値を許容する）
		assertEquals(new BigDecimal("-5000.00"), result.getValue());
		assertTrue(result.isNegative());
	}

	@Test
	@DisplayName("異常系：減算でnullを渡すと例外が発生する")
	void testSubtract_異常系_null() {
		// 準備
		TestMoney amount = TestMoney.from(new BigDecimal("10000.00"));

		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> amount.subtract(null)
		);
	}

	@Test
	@DisplayName("正常系：比較が正しく動作する（大きい）")
	void testCompareTo_正常系_大きい() {
		// 準備
		TestMoney amount1 = TestMoney.from(new BigDecimal("10000.00"));
		TestMoney amount2 = TestMoney.from(new BigDecimal("5000.00"));

		// 実行 & 検証
		assertTrue(amount1.compareTo(amount2) > 0);
	}

	@Test
	@DisplayName("正常系：比較が正しく動作する（等しい）")
	void testCompareTo_正常系_等しい() {
		// 準備
		TestMoney amount1 = TestMoney.from(new BigDecimal("10000.00"));
		TestMoney amount2 = TestMoney.from(new BigDecimal("10000.00"));

		// 実行 & 検証
		assertEquals(0, amount1.compareTo(amount2));
	}

	@Test
	@DisplayName("正常系：比較が正しく動作する（小さい）")
	void testCompareTo_正常系_小さい() {
		// 準備
		TestMoney amount1 = TestMoney.from(new BigDecimal("5000.00"));
		TestMoney amount2 = TestMoney.from(new BigDecimal("10000.00"));

		// 実行 & 検証
		assertTrue(amount1.compareTo(amount2) < 0);
	}

	@Test
	@DisplayName("正常系：負の値同士の比較が正しく動作する")
	void testCompareTo_正常系_負の値同士() {
		// 準備
		TestMoney amount1 = TestMoney.from(new BigDecimal("-5000.00"));
		TestMoney amount2 = TestMoney.from(new BigDecimal("-10000.00"));

		// 実行 & 検証（-5000 > -10000）
		assertTrue(amount1.compareTo(amount2) > 0);
	}

	@Test
	@DisplayName("異常系：compareTo - null比較")
	void testCompareTo_異常系_null() {
		// 準備
		TestMoney amount = TestMoney.from(new BigDecimal("10000.00"));

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> amount.compareTo(null)
		);
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("正常系：isZeroが正しく動作する")
	void testIsZero() {
		// 検証
		assertTrue(TestMoney.from(BigDecimal.ZERO.setScale(2)).isZero());
		assertFalse(TestMoney.from(new BigDecimal("1.00")).isZero());
		assertFalse(TestMoney.from(new BigDecimal("-1.00")).isZero());
	}

	@Test
	@DisplayName("正常系：isPositiveが正しく動作する")
	void testIsPositive() {
		// 検証
		assertFalse(TestMoney.from(BigDecimal.ZERO.setScale(2)).isPositive());
		assertTrue(TestMoney.from(new BigDecimal("1.00")).isPositive());
		assertFalse(TestMoney.from(new BigDecimal("-1.00")).isPositive());
	}

	@Test
	@DisplayName("正常系：isNegativeが正しく動作する")
	void testIsNegative() {
		// 検証
		assertFalse(TestMoney.from(BigDecimal.ZERO.setScale(2)).isNegative());
		assertFalse(TestMoney.from(new BigDecimal("1.00")).isNegative());
		assertTrue(TestMoney.from(new BigDecimal("-1.00")).isNegative());
	}

	@Test
	@DisplayName("正常系：toStringは値の文字列表現を返す")
	void testToString() {
		// 準備
		TestMoney positiveAmount = TestMoney.from(new BigDecimal("12345.67"));
		TestMoney negativeAmount = TestMoney.from(new BigDecimal("-12345.67"));

		// 検証（BigDecimalの文字列表現）
		assertEquals("12345.67", positiveAmount.toString());
		assertEquals("-12345.67", negativeAmount.toString());
	}

	@Test
	@DisplayName("正常系：toFormatStringはフォーマット済み文字列を返す")
	void testToFormatString() {
		// 準備
		TestMoney amount1 = TestMoney.from(new BigDecimal("12345.67"));
		TestMoney amount2 = TestMoney.from(new BigDecimal("1000000.00"));
		TestMoney amount3 = TestMoney.from(new BigDecimal("-12345.67"));

		// 検証（カンマ区切り+円表記、スケール0で四捨五入）
		assertEquals("12,346円", amount1.toFormatString());
		assertEquals("1,000,000円", amount2.toFormatString());
		assertEquals("-12,346円", amount3.toFormatString());
	}

	@Test
	@DisplayName("正常系：toIntegerValueは整数値を返す")
	void testToIntegerValue() {
		// 準備
		TestMoney amount1 = TestMoney.from(new BigDecimal("12345.67"));
		TestMoney amount2 = TestMoney.from(new BigDecimal("-12345.67"));
		TestMoney amount3 = TestMoney.from(new BigDecimal("12345.44"));
		TestMoney amount4 = TestMoney.from(new BigDecimal("-12345.50"));

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
		TestMoney amount1 = TestMoney.from(new BigDecimal("12345.67"));
		TestMoney amount2 = TestMoney.from(new BigDecimal("-12345.67"));

		// 検証（カンマ区切りなしの整数値文字列）
		assertEquals("12346", amount1.toIntegerString());
		assertEquals("-12346", amount2.toIntegerString());
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		// 準備
		TestMoney amount1 = TestMoney.from(new BigDecimal("10000.00"));
		TestMoney amount2 = TestMoney.from(new BigDecimal("10000.00"));
		TestMoney amount3 = TestMoney.from(new BigDecimal("5000.00"));

		// 検証
		assertEquals(amount1, amount2);
		assertNotEquals(amount1, amount3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		// 準備
		TestMoney amount1 = TestMoney.from(new BigDecimal("10000.00"));
		TestMoney amount2 = TestMoney.from(new BigDecimal("10000.00"));

		// 検証（同じ値なら同じハッシュコード）
		assertEquals(amount1.hashCode(), amount2.hashCode());
	}
}
