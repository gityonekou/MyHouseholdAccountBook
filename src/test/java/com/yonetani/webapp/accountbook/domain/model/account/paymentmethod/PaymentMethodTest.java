/**
 * PaymentMethodクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/08/19 : 1.00.00     新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.paymentmethod;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodKubun;

/**
 *<pre>
 * PaymentMethodクラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("支払方法(PaymentMethod)のテスト")
class PaymentMethodTest {

	@Test
	@DisplayName("正常系：クレジットカード(銀行口座・集計開始日あり)で生成")
	void testFrom_正常系_クレジットカード() {
		PaymentMethod method = PaymentMethod.from(
				"user01", "001", "〇〇カード", "3", "01", "15", "001", true, true);

		assertEquals("user01", method.getUserId().getValue());
		assertEquals("001", method.getPaymentMethodCode().getValue());
		assertEquals("〇〇カード", method.getPaymentMethodName().getValue());
		assertEquals(PaymentMethodKubun.CREDIT_CARD, method.getPaymentMethodKubun());
		assertEquals("01", method.getBankAccountCode().getValue());
		assertEquals("15", method.getClosingDay().getValue());
		assertEquals("001", method.getPaymentMethodSort().getValue());
		assertTrue(method.getEnableFlg().getValue());
		assertTrue(method.getEnableUpdateFlg().getValue());
	}

	@Test
	@DisplayName("正常系：現金(銀行口座・集計開始日ともにnull)で生成できる")
	void testFrom_正常系_現金() {
		PaymentMethod method = PaymentMethod.from(
				"user01", "001", "現金", "1", null, null, "001", true, true);

		assertEquals(PaymentMethodKubun.CASH, method.getPaymentMethodKubun());
		assertNull(method.getBankAccountCode());
		assertNull(method.getClosingDay().getValue());
	}

	@Test
	@DisplayName("正常系：電子マネー(前払い式)(銀行口座なし)で生成できる")
	void testFrom_正常系_電子マネー() {
		PaymentMethod method = PaymentMethod.from(
				"user01", "001", "PayPay残高", "5", null, null, "001", true, true);

		assertEquals(PaymentMethodKubun.PREPAID_EMONEY, method.getPaymentMethodKubun());
		assertNull(method.getBankAccountCode());
	}

	@Test
	@DisplayName("正常系：「支払方法がない」システム行(999固定値・ENABLE_UPDATE_FLG=false)を生成できる")
	void testFrom_正常系_支払方法がないシステム行() {
		PaymentMethod method = PaymentMethod.from(
				"user01", "999", "支払方法がない", "1", null, null, "999", true, false);

		assertTrue(method.getPaymentMethodCode().isNotApplicable());
		assertFalse(method.getEnableUpdateFlg().getValue());
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		PaymentMethod method1 = PaymentMethod.from("user01", "001", "現金", "1", null, null, "001", true, true);
		PaymentMethod method2 = PaymentMethod.from("user01", "001", "現金", "1", null, null, "001", true, true);
		assertEquals(method1, method2);
		assertEquals(method1.hashCode(), method2.hashCode());
	}
}
