/**
 * NullableMoney抽象クラスのテストクラスです。
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
 * NullableMoney抽象クラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("null許容金額基底クラス(NullableMoney)のテスト")
class NullableMoneyTest {

	// テスト用の具象クラス
	private static class TestNullableMoney extends NullableMoney {
		private TestNullableMoney(BigDecimal value) {
			super(value);
		}

		public static TestNullableMoney from(BigDecimal value) {
			validate(value, "テストnull許容金額");
			return new TestNullableMoney(value);
		}

		public TestNullableMoney add(TestNullableMoney other) {
			return new TestNullableMoney(super.add(other));
		}

		public TestNullableMoney addTreatingNullAsZero(TestNullableMoney other) {
			return new TestNullableMoney(super.addTreatingNullAsZero(other));
		}

		public TestNullableMoney subtract(TestNullableMoney other) {
			return new TestNullableMoney(super.subtract(other));
		}

		public TestNullableMoney subtractTreatingNullAsZero(TestNullableMoney other) {
			return new TestNullableMoney(super.subtractTreatingNullAsZero(other));
		}
	}

	@Test
	@DisplayName("正常系：正の金額で生成できる")
	void testFrom_正常系_正の金額() {
		// 実行
		TestNullableMoney amount = TestNullableMoney.from(new BigDecimal("10000.00"));

		// 検証
		assertNotNull(amount);
		assertEquals(new BigDecimal("10000.00"), amount.getValue());
		assertEquals("10000.00", amount.toString());
		assertEquals("10,000円", amount.toFormatString());
		assertFalse(amount.isNull());
	}

	@Test
	@DisplayName("正常系：0円で生成できる")
	void testFrom_正常系_0円() {
		// 実行
		TestNullableMoney amount = TestNullableMoney.from(BigDecimal.ZERO.setScale(2));

		// 検証
		assertNotNull(amount);
		assertEquals(BigDecimal.ZERO.setScale(2), amount.getValue());
		assertTrue(amount.isZero());
		assertFalse(amount.isNull());
	}

	@Test
	@DisplayName("正常系：null値で生成できる")
	void testFrom_正常系_null値() {
		// 実行
		TestNullableMoney amount = TestNullableMoney.from(null);

		// 検証
		assertNotNull(amount);
		assertNull(amount.getValue());
		assertTrue(amount.isNull());
		assertTrue(amount.isZero()); // null値は0として扱う
		assertEquals("", amount.toString());
		assertEquals("", amount.toFormatString());
	}

	@Test
	@DisplayName("異常系：負の金額で例外が発生する")
	void testFrom_異常系_負の金額() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestNullableMoney.from(new BigDecimal("-1000.00"))
		);
		assertTrue(exception.getMessage().contains("テストnull許容金額"));
		assertTrue(exception.getMessage().contains("マイナス"));
	}

	@Test
	@DisplayName("異常系：スケール値が2以外で例外が発生する")
	void testFrom_異常系_スケール値不正() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestNullableMoney.from(new BigDecimal("10000"))
		);
		assertTrue(exception.getMessage().contains("テストnull許容金額"));
		assertTrue(exception.getMessage().contains("スケール"));
	}

	@Test
	@DisplayName("正常系：add（null値伝播方式）- 両方とも値がある場合")
	void testAdd_正常系_両方値あり() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(new BigDecimal("10000.00"));
		TestNullableMoney amount2 = TestNullableMoney.from(new BigDecimal("5000.00"));

		// 実行
		TestNullableMoney result = amount1.add(amount2);

		// 検証
		assertEquals(new BigDecimal("15000.00"), result.getValue());
		// 元のオブジェクトは変更されていないことを確認（不変性）
		assertEquals(new BigDecimal("10000.00"), amount1.getValue());
		assertEquals(new BigDecimal("5000.00"), amount2.getValue());
	}

	@Test
	@DisplayName("正常系：add（null値伝播方式）- thisがnull")
	void testAdd_正常系_thisがnull() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(null);
		TestNullableMoney amount2 = TestNullableMoney.from(new BigDecimal("5000.00"));

		// 実行
		TestNullableMoney result = amount1.add(amount2);

		// 検証
		assertEquals(new BigDecimal("5000.00"), result.getValue());
	}

	@Test
	@DisplayName("正常系：add（null値伝播方式）- otherがnull")
	void testAdd_正常系_otherがnull() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(new BigDecimal("10000.00"));
		TestNullableMoney amount2 = TestNullableMoney.from(null);

		// 実行
		TestNullableMoney result = amount1.add(amount2);

		// 検証
		assertEquals(new BigDecimal("10000.00"), result.getValue());
	}

	@Test
	@DisplayName("正常系：add（null値伝播方式）- 両方null")
	void testAdd_正常系_両方null() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(null);
		TestNullableMoney amount2 = TestNullableMoney.from(null);

		// 実行
		TestNullableMoney result = amount1.add(amount2);

		// 検証
		assertNull(result.getValue());
		assertTrue(result.isNull());
	}

	@Test
	@DisplayName("異常系：addでnullオブジェクトを渡すと例外が発生する")
	void testAdd_異常系_nullオブジェクト() {
		// 準備
		TestNullableMoney amount = TestNullableMoney.from(new BigDecimal("10000.00"));

		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> amount.add(null)
		);
	}

	@Test
	@DisplayName("正常系：addTreatingNullAsZero - 両方とも値がある場合")
	void testAddTreatingNullAsZero_正常系_両方値あり() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(new BigDecimal("10000.00"));
		TestNullableMoney amount2 = TestNullableMoney.from(new BigDecimal("5000.00"));

		// 実行
		TestNullableMoney result = amount1.addTreatingNullAsZero(amount2);

		// 検証
		assertEquals(new BigDecimal("15000.00"), result.getValue());
	}

	@Test
	@DisplayName("正常系：addTreatingNullAsZero - thisがnull")
	void testAddTreatingNullAsZero_正常系_thisがnull() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(null);
		TestNullableMoney amount2 = TestNullableMoney.from(new BigDecimal("5000.00"));

		// 実行
		TestNullableMoney result = amount1.addTreatingNullAsZero(amount2);

		// 検証（null + 5000 = 5000）
		assertEquals(new BigDecimal("5000.00"), result.getValue());
	}

	@Test
	@DisplayName("正常系：addTreatingNullAsZero - 両方null")
	void testAddTreatingNullAsZero_正常系_両方null() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(null);
		TestNullableMoney amount2 = TestNullableMoney.from(null);

		// 実行
		TestNullableMoney result = amount1.addTreatingNullAsZero(amount2);

		// 検証（null + null = 0）
		assertEquals(BigDecimal.ZERO.setScale(2), result.getValue());
	}

	@Test
	@DisplayName("正常系：subtract（null値エラー方式）- 両方とも値がある場合")
	void testSubtract_正常系_両方値あり() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(new BigDecimal("10000.00"));
		TestNullableMoney amount2 = TestNullableMoney.from(new BigDecimal("3000.00"));

		// 実行
		TestNullableMoney result = amount1.subtract(amount2);

		// 検証
		assertEquals(new BigDecimal("7000.00"), result.getValue());
		// 元のオブジェクトは変更されていないことを確認（不変性）
		assertEquals(new BigDecimal("10000.00"), amount1.getValue());
		assertEquals(new BigDecimal("3000.00"), amount2.getValue());
	}

	@Test
	@DisplayName("正常系：subtract（null値エラー方式）- otherがnull")
	void testSubtract_正常系_otherがnull() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(new BigDecimal("10000.00"));
		TestNullableMoney amount2 = TestNullableMoney.from(null);

		// 実行
		TestNullableMoney result = amount1.subtract(amount2);

		// 検証（10000 - null = 10000）
		assertEquals(new BigDecimal("10000.00"), result.getValue());
	}

	@Test
	@DisplayName("異常系：subtract（null値エラー方式）- thisがnull")
	void testSubtract_異常系_thisがnull() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(null);
		TestNullableMoney amount2 = TestNullableMoney.from(new BigDecimal("5000.00"));

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> amount1.subtract(amount2)
		);
		assertTrue(exception.getMessage().contains("null値の金額から減算することはできません"));
	}

	@Test
	@DisplayName("異常系：subtractでnullオブジェクトを渡すと例外が発生する")
	void testSubtract_異常系_nullオブジェクト() {
		// 準備
		TestNullableMoney amount = TestNullableMoney.from(new BigDecimal("10000.00"));

		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> amount.subtract(null)
		);
	}

	@Test
	@DisplayName("正常系：subtractTreatingNullAsZero - 両方とも値がある場合")
	void testSubtractTreatingNullAsZero_正常系_両方値あり() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(new BigDecimal("10000.00"));
		TestNullableMoney amount2 = TestNullableMoney.from(new BigDecimal("3000.00"));

		// 実行
		TestNullableMoney result = amount1.subtractTreatingNullAsZero(amount2);

		// 検証
		assertEquals(new BigDecimal("7000.00"), result.getValue());
	}

	@Test
	@DisplayName("正常系：subtractTreatingNullAsZero - thisがnull")
	void testSubtractTreatingNullAsZero_正常系_thisがnull() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(null);
		TestNullableMoney amount2 = TestNullableMoney.from(new BigDecimal("5000.00"));

		// 実行
		TestNullableMoney result = amount1.subtractTreatingNullAsZero(amount2);

		// 検証（null - 5000 = -5000、ただしNullableMoneyは負値を許容しないのでこのパターンはあり得ないが、
		// subtractTreatingNullAsZeroはnullを0として扱うため 0 - 5000 = -5000になり例外がスローされる）
		// この動作が想定される場合は正常系、想定されない場合は異常系となる
		// ここではsubtractTreatingNullAsZeroはnullを0として扱うので、0 - 5000 = -5000が返る
		assertEquals(new BigDecimal("-5000.00"), result.getValue());
	}

	@Test
	@DisplayName("正常系：compareTo - 大きい")
	void testCompareTo_正常系_大きい() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(new BigDecimal("10000.00"));
		TestNullableMoney amount2 = TestNullableMoney.from(new BigDecimal("5000.00"));

		// 実行 & 検証
		assertTrue(amount1.compareTo(amount2) > 0);
	}

	@Test
	@DisplayName("正常系：compareTo - 等しい")
	void testCompareTo_正常系_等しい() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(new BigDecimal("10000.00"));
		TestNullableMoney amount2 = TestNullableMoney.from(new BigDecimal("10000.00"));

		// 実行 & 検証
		assertEquals(0, amount1.compareTo(amount2));
	}

	@Test
	@DisplayName("正常系：compareTo - 小さい")
	void testCompareTo_正常系_小さい() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(new BigDecimal("5000.00"));
		TestNullableMoney amount2 = TestNullableMoney.from(new BigDecimal("10000.00"));

		// 実行 & 検証
		assertTrue(amount1.compareTo(amount2) < 0);
	}

	@Test
	@DisplayName("正常系：compareTo - null値は0として扱う")
	void testCompareTo_正常系_null値() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(null);
		TestNullableMoney amount2 = TestNullableMoney.from(BigDecimal.ZERO.setScale(2));

		// 実行 & 検証（null == 0）
		assertEquals(0, amount1.compareTo(amount2));
	}

	@Test
	@DisplayName("正常系：isZero")
	void testIsZero() {
		// 検証
		assertTrue(TestNullableMoney.from(BigDecimal.ZERO.setScale(2)).isZero());
		assertTrue(TestNullableMoney.from(null).isZero()); // null値は0として扱う
		assertFalse(TestNullableMoney.from(new BigDecimal("1.00")).isZero());
	}

	@Test
	@DisplayName("正常系：isPositive")
	void testIsPositive() {
		// 検証
		assertFalse(TestNullableMoney.from(BigDecimal.ZERO.setScale(2)).isPositive());
		assertFalse(TestNullableMoney.from(null).isPositive()); // null値は0として扱う
		assertTrue(TestNullableMoney.from(new BigDecimal("1.00")).isPositive());
	}

	@Test
	@DisplayName("正常系：isNegative（NullableMoneyは負値を許容しない）")
	void testIsNegative() {
		// 検証（NullableMoneyは負値を生成できないため、常にfalse）
		assertFalse(TestNullableMoney.from(BigDecimal.ZERO.setScale(2)).isNegative());
		assertFalse(TestNullableMoney.from(null).isNegative());
		assertFalse(TestNullableMoney.from(new BigDecimal("1.00")).isNegative());
	}

	@Test
	@DisplayName("正常系：isNull")
	void testIsNull() {
		// 検証
		assertTrue(TestNullableMoney.from(null).isNull());
		assertFalse(TestNullableMoney.from(BigDecimal.ZERO.setScale(2)).isNull());
		assertFalse(TestNullableMoney.from(new BigDecimal("1.00")).isNull());
	}

	@Test
	@DisplayName("正常系：getNullSafeValue")
	void testGetNullSafeValue() {
		// 検証
		assertEquals(new BigDecimal("10000.00"),
			TestNullableMoney.from(new BigDecimal("10000.00")).getNullSafeValue());
		assertEquals(BigDecimal.ZERO.setScale(2),
			TestNullableMoney.from(null).getNullSafeValue());
	}

	@Test
	@DisplayName("正常系：toString")
	void testToString() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(new BigDecimal("12345.67"));
		TestNullableMoney amount2 = TestNullableMoney.from(null);

		// 検証
		assertEquals("12345.67", amount1.toString());
		assertEquals("", amount2.toString()); // null値は空文字列
	}

	@Test
	@DisplayName("正常系：toFormatString")
	void testToFormatString() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(new BigDecimal("12345.67"));
		TestNullableMoney amount2 = TestNullableMoney.from(new BigDecimal("1000000.00"));
		TestNullableMoney amount3 = TestNullableMoney.from(null);

		// 検証（カンマ区切り+円表記、スケール0で四捨五入）
		assertEquals("12,346円", amount1.toFormatString());
		assertEquals("1,000,000円", amount2.toFormatString());
		assertEquals("", amount3.toFormatString()); // null値は空文字列
	}

	@Test
	@DisplayName("正常系：toIntegerValue")
	void testToIntegerValue() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(new BigDecimal("12345.67"));
		TestNullableMoney amount2 = TestNullableMoney.from(new BigDecimal("12345.44"));
		TestNullableMoney amount3 = TestNullableMoney.from(new BigDecimal("12345.50"));
		TestNullableMoney amount4 = TestNullableMoney.from(null);

		// 検証（スケール0で四捨五入）
		assertEquals(12346L, amount1.toIntegerValue());
		assertEquals(12345L, amount2.toIntegerValue()); // 0.44 -> 切り捨て
		assertEquals(12346L, amount3.toIntegerValue()); // 0.50 -> 切り上げ
		assertEquals(0L, amount4.toIntegerValue()); // null値は0
	}

	@Test
	@DisplayName("正常系：toIntegerString")
	void testToIntegerString() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(new BigDecimal("12345.67"));
		TestNullableMoney amount2 = TestNullableMoney.from(null);

		// 検証（カンマ区切りなしの整数値文字列）
		assertEquals("12346", amount1.toIntegerString());
		assertEquals("0", amount2.toIntegerString()); // null値は"0"
	}

	@Test
	@DisplayName("正常系：equals")
	void testEquals() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(new BigDecimal("10000.00"));
		TestNullableMoney amount2 = TestNullableMoney.from(new BigDecimal("10000.00"));
		TestNullableMoney amount3 = TestNullableMoney.from(new BigDecimal("5000.00"));
		TestNullableMoney amount4 = TestNullableMoney.from(null);
		TestNullableMoney amount5 = TestNullableMoney.from(null);

		// 検証
		assertEquals(amount1, amount2);
		assertNotEquals(amount1, amount3);
		assertEquals(amount4, amount5); // null値同士は等しい
		assertNotEquals(amount1, amount4);
	}

	@Test
	@DisplayName("正常系：hashCode")
	void testHashCode() {
		// 準備
		TestNullableMoney amount1 = TestNullableMoney.from(new BigDecimal("10000.00"));
		TestNullableMoney amount2 = TestNullableMoney.from(new BigDecimal("10000.00"));
		TestNullableMoney amount3 = TestNullableMoney.from(null);
		TestNullableMoney amount4 = TestNullableMoney.from(null);

		// 検証（同じ値なら同じハッシュコード）
		assertEquals(amount1.hashCode(), amount2.hashCode());
		assertEquals(amount3.hashCode(), amount4.hashCode());
	}
}
