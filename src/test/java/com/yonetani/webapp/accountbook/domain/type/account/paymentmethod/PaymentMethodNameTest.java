/**
 * PaymentMethodNameクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.paymentmethod;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * PaymentMethodNameクラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("支払方法名(PaymentMethodName)のテスト")
class PaymentMethodNameTest {

	@Test
	@DisplayName("正常系：値から生成")
	void testFrom_正常系_値から生成() {
		PaymentMethodName name = PaymentMethodName.from("〇〇銀行引き落とし");
		assertEquals("〇〇銀行引き落とし", name.getValue());
		assertEquals("〇〇銀行引き落とし", name.toString());
	}

	@Test
	@DisplayName("正常系：50文字ちょうどで生成できる")
	void testFrom_正常系_50文字ちょうど() {
		String value50 = "あ".repeat(50);
		assertEquals(50, PaymentMethodName.from(value50).getValue().length());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> PaymentMethodName.from(null));
	}

	@Test
	@DisplayName("異常系：空文字列で例外が発生する")
	void testFrom_異常系_空文字列値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> PaymentMethodName.from(""));
	}

	@Test
	@DisplayName("異常系：51文字で例外が発生する")
	void testFrom_異常系_51文字() {
		String value51 = "あ".repeat(51);
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> PaymentMethodName.from(value51));
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		assertEquals(PaymentMethodName.from("現金"), PaymentMethodName.from("現金"));
		assertEquals(PaymentMethodName.from("現金").hashCode(), PaymentMethodName.from("現金").hashCode());
	}
}
