/**
 * WithdrewKingaku(積立金取崩金額)のテストクラスです。
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

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * WithdrewKingaku(積立金取崩金額)のテストクラスです。
 *
 * [注意]
 * WithdrewKingakuは特殊なクラスで、null値を許容します。
 * これは積立金取崩しがない場合を表現するためです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("積立金取崩金額(WithdrewKingaku)のテスト")
class WithdrewKingakuTest {

	@Test
	@DisplayName("正常系：正の金額で生成できる")
	void testFrom_正常系_正の金額() {
		// 実行
		WithdrewKingaku amount = WithdrewKingaku.from(new BigDecimal("10000.00"));

		// 検証
		assertNotNull(amount);
		assertEquals(new BigDecimal("10000.00"), amount.getValue());
		assertNotEquals(WithdrewKingaku.NULL, amount);
	}

	@Test
	@DisplayName("正常系：0円で生成できる")
	void testFrom_正常系_0円() {
		// 実行
		WithdrewKingaku amount = WithdrewKingaku.from(BigDecimal.ZERO.setScale(2));

		// 検証
		assertNotNull(amount);
		assertEquals(BigDecimal.ZERO.setScale(2), amount.getValue());
	}

	@Test
	@DisplayName("正常系：ZERO定数が使用できる")
	void testZERO定数() {
		// 検証
		assertNotNull(WithdrewKingaku.ZERO);
		assertEquals(BigDecimal.ZERO.setScale(2), WithdrewKingaku.ZERO.getValue());
	}

	@Test
	@DisplayName("正常系：null値で生成できる（積立金取崩しがない場合）")
	void testFrom_正常系_null値() {
		// 実行
		WithdrewKingaku amount = WithdrewKingaku.from((BigDecimal)null);

		// 検証
		assertNotNull(amount);
		assertNull(amount.getValue());
	}

	@Test
	@DisplayName("正常系：NULL定数が使用できる")
	void testNULL定数() {
		// 検証
		assertNotNull(WithdrewKingaku.NULL);
		assertNull(WithdrewKingaku.NULL.getValue());
	}

	@Test
	@DisplayName("正常系：getNullSafeValue()はnullの場合0を返す")
	void testGetNullSafeValue_null値の場合() {
		// 準備
		WithdrewKingaku nullAmount = WithdrewKingaku.NULL;

		// 実行
		BigDecimal safeValue = nullAmount.getNullSafeValue();

		// 検証
		assertNotNull(safeValue);
		assertEquals(WithdrewKingaku.ZERO.getValue(), safeValue);
	}

	@Test
	@DisplayName("正常系：getNullSafeValue()は非nullの場合そのまま返す")
	void testGetNullSafeValue_非null値の場合() {
		// 準備
		WithdrewKingaku amount = WithdrewKingaku.from(new BigDecimal("10000.00"));

		// 実行
		BigDecimal safeValue = amount.getNullSafeValue();

		// 検証
		assertEquals(new BigDecimal("10000.00"), safeValue);
	}

	@Test
	@DisplayName("異常系：負の金額で例外が発生する")
	void testFrom_異常系_負の金額() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> WithdrewKingaku.from(new BigDecimal("-1000.00"))
		);
		assertTrue(exception.getMessage().contains("積立金取崩金額"));
		assertTrue(exception.getMessage().contains("マイナス"));
	}

	@Test
	@DisplayName("異常系：スケール値が2以外で例外が発生する")
	void testFrom_異常系_スケール値不正() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> WithdrewKingaku.from(new BigDecimal("10000"))
		);
		assertTrue(exception.getMessage().contains("積立金取崩金額"));
		assertTrue(exception.getMessage().contains("スケール"));
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する（通常の値）")
	void testEquals_通常の値() {
		// 準備
		WithdrewKingaku amount1 = WithdrewKingaku.from(new BigDecimal("10000.00"));
		WithdrewKingaku amount2 = WithdrewKingaku.from(new BigDecimal("10000.00"));
		WithdrewKingaku amount3 = WithdrewKingaku.from(new BigDecimal("5000.00"));

		// 検証
		assertEquals(amount1, amount2);
		assertNotEquals(amount1, amount3);
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する（null値同士）")
	void testEquals_null値同士() {
		// 準備
		WithdrewKingaku null1 = WithdrewKingaku.from((BigDecimal)null);
		WithdrewKingaku null2 = WithdrewKingaku.from((BigDecimal)null);

		// 検証
		assertEquals(null1, null2);
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する（null値とNULL定数）")
	void testEquals_null値とNULL定数() {
		// 準備
		WithdrewKingaku nullAmount = WithdrewKingaku.from((BigDecimal)null);

		// 検証
		assertEquals(WithdrewKingaku.NULL, nullAmount);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する（通常の値）")
	void testHashCode_通常の値() {
		// 準備
		WithdrewKingaku amount1 = WithdrewKingaku.from(new BigDecimal("10000.00"));
		WithdrewKingaku amount2 = WithdrewKingaku.from(new BigDecimal("10000.00"));

		// 検証（同じ値なら同じハッシュコード）
		assertEquals(amount1.hashCode(), amount2.hashCode());
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する（null値）")
	void testHashCode_null値() {
		// 準備
		WithdrewKingaku null1 = WithdrewKingaku.from((BigDecimal)null);
		WithdrewKingaku null2 = WithdrewKingaku.from((BigDecimal)null);

		// 検証（同じnull値なら同じハッシュコード）
		assertEquals(null1.hashCode(), null2.hashCode());
	}

	@Test
	@DisplayName("正常系：toStringは値の文字列表現を返す（デバッグ用）")
	void testToString_通常の値() {
		// 準備
		WithdrewKingaku amount = WithdrewKingaku.from(new BigDecimal("12345.67"));

		// 検証（BigDecimalの文字列表現）
		assertEquals("12345.67", amount.toString());
	}

	@Test
	@DisplayName("正常系：toStringはnullの場合空文字列を返す")
	void testToString_null値() {
		// 準備
		WithdrewKingaku nullAmount = WithdrewKingaku.NULL;

		// 実行
		String result = nullAmount.toString();

		// 検証（nullの場合は空文字列）
		assertEquals("", result);
	}

	@Test
	@DisplayName("正常系：toFormatStringはフォーマット済み文字列を返す（通常の値）")
	void testToFormatString_通常の値() {
		// 準備
		WithdrewKingaku amount = WithdrewKingaku.from(new BigDecimal("12345.67"));

		// 検証（カンマ区切り+円表記、スケール0で四捨五入）
		assertEquals("12,346円", amount.toFormatString());
	}

	@Test
	@DisplayName("正常系：toFormatStringは0円の場合0円を返す")
	void testToFormatString_0円() {
		// 準備
		WithdrewKingaku amount = WithdrewKingaku.ZERO;

		// 検証
		assertEquals("0円", amount.toFormatString());
	}

	@Test
	@DisplayName("正常系：toFormatStringはnullの場合空文字列を返す")
	void testToFormatString_null値() {
		// 準備
		WithdrewKingaku nullAmount = WithdrewKingaku.NULL;

		// 実行
		String result = nullAmount.toFormatString();

		// 検証（nullの場合は空文字列）
		assertEquals("", result);
	}

	@Test
	@DisplayName("正常系：toFormatStringは大きな金額も正しくフォーマットする")
	void testToFormatString_大きな金額() {
		// 準備
		WithdrewKingaku amount = WithdrewKingaku.from(new BigDecimal("1234567.89"));

		// 検証（カンマ区切り、四捨五入）
		assertEquals("1,234,568円", amount.toFormatString());
	}

	@Test
	@DisplayName("正常系：ZERO定数とNULL定数は異なる")
	void testZERO定数とNULL定数の違い() {
		// 検証
		assertNotEquals(WithdrewKingaku.ZERO, WithdrewKingaku.NULL);
		assertNotNull(WithdrewKingaku.ZERO.getValue());
		assertNull(WithdrewKingaku.NULL.getValue());
		assertEquals(BigDecimal.ZERO.setScale(2), WithdrewKingaku.ZERO.getValue());
		assertEquals(BigDecimal.ZERO.setScale(2), WithdrewKingaku.NULL.getNullSafeValue());
	}

	@Test
	@DisplayName("正常系：複数回null生成しても同じ内容")
	void testFrom_null値を複数回生成() {
		// 実行
		WithdrewKingaku null1 = WithdrewKingaku.from((BigDecimal)null);
		WithdrewKingaku null2 = WithdrewKingaku.from((BigDecimal)null);
		WithdrewKingaku null3 = WithdrewKingaku.NULL;

		// 検証（すべて等しい）
		assertEquals(null1, null2);
		assertEquals(null2, null3);
		assertEquals(null1, null3);
	}
}
