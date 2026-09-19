/**
 * PaymentMethodKubunクラスのテストクラスです。
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
 * PaymentMethodKubunクラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("支払方法種別(PaymentMethodKubun)のテスト")
class PaymentMethodKubunTest {

	@Test
	@DisplayName("正常系：現金(1)はrequiresAccount=false, requiresClosingDay=false")
	void testFrom_現金() {
		PaymentMethodKubun kubun = PaymentMethodKubun.from("1");
		assertEquals(PaymentMethodKubun.CASH, kubun);
		assertEquals("1", kubun.getValue());
		assertFalse(kubun.requiresAccount());
		assertFalse(kubun.requiresClosingDay());
	}

	@Test
	@DisplayName("正常系：口座振替(2)はrequiresAccount=true, requiresClosingDay=false")
	void testFrom_口座振替() {
		PaymentMethodKubun kubun = PaymentMethodKubun.from("2");
		assertEquals(PaymentMethodKubun.BANK_TRANSFER, kubun);
		assertTrue(kubun.requiresAccount());
		assertFalse(kubun.requiresClosingDay());
	}

	@Test
	@DisplayName("正常系：クレジットカード(3)はrequiresAccount=true, requiresClosingDay=true")
	void testFrom_クレジットカード() {
		PaymentMethodKubun kubun = PaymentMethodKubun.from("3");
		assertEquals(PaymentMethodKubun.CREDIT_CARD, kubun);
		assertTrue(kubun.requiresAccount());
		assertTrue(kubun.requiresClosingDay());
	}

	@Test
	@DisplayName("正常系：デビットカード(4)はrequiresAccount=true, requiresClosingDay=false")
	void testFrom_デビットカード() {
		PaymentMethodKubun kubun = PaymentMethodKubun.from("4");
		assertEquals(PaymentMethodKubun.DEBIT_CARD, kubun);
		assertTrue(kubun.requiresAccount());
		assertFalse(kubun.requiresClosingDay());
	}

	@Test
	@DisplayName("正常系：電子マネー(前払い式)(5)はrequiresAccount=false, requiresClosingDay=false")
	void testFrom_電子マネー前払い式() {
		PaymentMethodKubun kubun = PaymentMethodKubun.from("5");
		assertEquals(PaymentMethodKubun.PREPAID_EMONEY, kubun);
		assertFalse(kubun.requiresAccount());
		assertFalse(kubun.requiresClosingDay());
	}

	@Test
	@DisplayName("異常系：未定義値で例外が発生する")
	void testFrom_異常系_未定義値() {
		MyHouseholdAccountBookRuntimeException ex = assertThrows(
				MyHouseholdAccountBookRuntimeException.class, () -> PaymentMethodKubun.from("6"));
		assertTrue(ex.getMessage().contains("支払方法種別"));
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> PaymentMethodKubun.from(null));
	}

	@Test
	@DisplayName("正常系：tryFromは正常値でOptionalに値を返す")
	void testTryFrom_正常系() {
		Optional<PaymentMethodKubun> result = PaymentMethodKubun.tryFrom("3");
		assertTrue(result.isPresent());
		assertEquals(PaymentMethodKubun.CREDIT_CARD, result.get());
	}

	@Test
	@DisplayName("異常系：tryFromは未定義値・null値でOptional.emptyを返す(例外を投げない)")
	void testTryFrom_異常系_不正値でempty() {
		assertTrue(PaymentMethodKubun.tryFrom("9").isEmpty());
		assertTrue(PaymentMethodKubun.tryFrom(null).isEmpty());
	}
}
