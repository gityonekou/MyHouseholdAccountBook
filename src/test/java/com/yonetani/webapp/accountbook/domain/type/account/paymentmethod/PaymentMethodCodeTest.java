/**
 * PaymentMethodCodeクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.paymentmethod;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * PaymentMethodCodeクラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("支払方法コード(PaymentMethodCode)のテスト")
class PaymentMethodCodeTest {

	@Test
	@DisplayName("正常系：文字列から生成")
	void testFrom_正常系_文字列から生成() {
		PaymentMethodCode code = PaymentMethodCode.from("001");
		assertEquals("001", code.getValue());
		assertEquals("001", code.toString());
	}

	@Test
	@DisplayName("正常系：数値(発番用)から生成")
	void testFrom_正常系_数値から生成() {
		PaymentMethodCode code = PaymentMethodCode.from(5);
		assertEquals("005", code.getValue());
		assertEquals("005", PaymentMethodCode.getNewCode(5));
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> PaymentMethodCode.from((String) null));
	}

	@Test
	@DisplayName("異常系：空文字列で例外が発生する")
	void testFrom_異常系_空文字列値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> PaymentMethodCode.from(""));
	}

	@Test
	@DisplayName("異常系：3桁でない場合に例外が発生する")
	void testFrom_異常系_桁数不正() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> PaymentMethodCode.from("1"));
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> PaymentMethodCode.from("1000"));
	}

	@Test
	@DisplayName("異常系：数値に変換できない場合に例外が発生する")
	void testFrom_異常系_数値変換不可() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> PaymentMethodCode.from("abc"));
	}

	@Test
	@DisplayName("正常系：tryFromは正常値でOptionalに値を返す")
	void testTryFrom_正常系() {
		Optional<PaymentMethodCode> result = PaymentMethodCode.tryFrom("001");
		assertTrue(result.isPresent());
		assertEquals("001", result.get().getValue());
	}

	@Test
	@DisplayName("異常系：tryFromは不正値でOptional.emptyを返す(例外を投げない)")
	void testTryFrom_異常系_不正値でempty() {
		assertTrue(PaymentMethodCode.tryFrom("abc").isEmpty());
		assertTrue(PaymentMethodCode.tryFrom("").isEmpty());
		assertTrue(PaymentMethodCode.tryFrom(null).isEmpty());
	}

	@Test
	@DisplayName("境界値：989はシステム予約帯でない")
	void testIsSystemReserved_境界値_989() {
		PaymentMethodCode code = PaymentMethodCode.from("989");
		assertFalse(code.isSystemReserved());
		assertFalse(code.isNotApplicable());
	}

	@Test
	@DisplayName("境界値：990はシステム予約帯である")
	void testIsSystemReserved_境界値_990() {
		PaymentMethodCode code = PaymentMethodCode.from("990");
		assertTrue(code.isSystemReserved());
		assertFalse(code.isNotApplicable());
	}

	@Test
	@DisplayName("境界値：999はシステム予約帯かつ「支払方法がない」固定値である")
	void testIsSystemReserved_境界値_999() {
		PaymentMethodCode code = PaymentMethodCode.from("999");
		assertTrue(code.isSystemReserved());
		assertTrue(code.isNotApplicable());
	}

	@Test
	@DisplayName("正常系：991はシステム予約帯だが「支払方法がない」固定値ではない")
	void testIsNotApplicable_正常系_991() {
		PaymentMethodCode code = PaymentMethodCode.from("991");
		assertTrue(code.isSystemReserved());
		assertFalse(code.isNotApplicable());
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		PaymentMethodCode code1 = PaymentMethodCode.from("001");
		PaymentMethodCode code2 = PaymentMethodCode.from("001");
		assertEquals(code1, code2);
		assertEquals(code1.hashCode(), code2.hashCode());
	}
}
