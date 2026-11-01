/**
 * PaymentMethodクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 * 2026/10/18 : 1.00.01  feature-1.03-dev1   from()型安全性対応に伴うhelper()追加、toggleEnableFlg()のテスト追加
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.paymentmethod;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountCode;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.ClosingDay;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodKubun;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodMemo;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodName;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodSort;
import com.yonetani.webapp.accountbook.domain.type.common.EnableFlg;
import com.yonetani.webapp.accountbook.domain.type.common.EnableUpdateFlg;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

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

	/**
	 *<pre>
	 * プリミティブ引数を受け取り値オブジェクトへ変換してからPaymentMethod.from()を呼び出すテスト用ヘルパーメソッドです。
	 * (06-コーディング規約.md §8.3。from()が値オブジェクト型を要求するため、テストコードから直接プリミティブ値で呼び出せない)
	 *</pre>
	 */
	private PaymentMethod helper(String userId, String paymentMethodCode, String paymentMethodName, String paymentMethodMemo,
			String paymentMethodKubun, String bankAccountCode, String closingDay, String paymentMethodSort,
			boolean enableFlg, boolean enableUpdateFlg) {
		return PaymentMethod.from(
				UserId.from(userId),
				PaymentMethodCode.from(paymentMethodCode),
				PaymentMethodName.from(paymentMethodName),
				PaymentMethodMemo.from(paymentMethodMemo),
				PaymentMethodKubun.from(paymentMethodKubun),
				StringUtils.hasLength(bankAccountCode) ? BankAccountCode.from(bankAccountCode) : null,
				ClosingDay.from(closingDay),
				PaymentMethodSort.from(paymentMethodSort),
				EnableFlg.from(enableFlg),
				EnableUpdateFlg.from(enableUpdateFlg));
	}

	@Test
	@DisplayName("正常系：クレジットカード(銀行口座・集計開始日あり)で生成")
	void testFrom_正常系_クレジットカード() {
		PaymentMethod method = helper(
				"user01", "001", "〇〇カード", "ポイント還元率1%", "3", "01", "15", "001", true, true);

		assertEquals("user01", method.getUserId().getValue());
		assertEquals("001", method.getPaymentMethodCode().getValue());
		assertEquals("〇〇カード", method.getPaymentMethodName().getValue());
		assertEquals("ポイント還元率1%", method.getPaymentMethodMemo().getValue());
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
		PaymentMethod method = helper(
				"user01", "001", "現金", null, "1", null, null, "001", true, true);

		assertEquals(PaymentMethodKubun.CASH, method.getPaymentMethodKubun());
		assertNull(method.getPaymentMethodMemo().getValue());
		assertNull(method.getBankAccountCode());
		assertNull(method.getClosingDay().getValue());
	}

	@Test
	@DisplayName("正常系：電子マネー(前払い式)(銀行口座なし)で生成できる")
	void testFrom_正常系_電子マネー() {
		PaymentMethod method = helper(
				"user01", "001", "PayPay残高", null, "5", null, null, "001", true, true);

		assertEquals(PaymentMethodKubun.PREPAID_EMONEY, method.getPaymentMethodKubun());
		assertNull(method.getBankAccountCode());
	}

	@Test
	@DisplayName("正常系：「支払方法がない」システム行(999固定値・ENABLE_UPDATE_FLG=false)を生成できる")
	void testFrom_正常系_支払方法がないシステム行() {
		PaymentMethod method = helper(
				"user01", "999", "支払方法がない", null, "1", null, null, "999", true, false);

		assertTrue(method.getPaymentMethodCode().isNotApplicable());
		assertFalse(method.getEnableUpdateFlg().getValue());
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		PaymentMethod method1 = helper("user01", "001", "現金", null, "1", null, null, "001", true, true);
		PaymentMethod method2 = helper("user01", "001", "現金", null, "1", null, null, "001", true, true);
		assertEquals(method1, method2);
		assertEquals(method1.hashCode(), method2.hashCode());
	}

	@Test
	@DisplayName("正常系：toggleEnableFlgで有効→無効に反転し、他項目は変化しない")
	void testToggleEnableFlg_正常系_有効から無効() {
		PaymentMethod method = helper("user01", "001", "〇〇カード", "ポイント還元率1%", "3", "01", "15", "001", true, true);
		PaymentMethod toggled = method.toggleEnableFlg();
		// 検証
		assertFalse(toggled.getEnableFlg().getValue());
		assertEquals(method.getUserId(), toggled.getUserId());
		assertEquals(method.getPaymentMethodCode(), toggled.getPaymentMethodCode());
		assertEquals(method.getPaymentMethodName(), toggled.getPaymentMethodName());
		assertEquals(method.getPaymentMethodMemo(), toggled.getPaymentMethodMemo());
		assertEquals(method.getPaymentMethodKubun(), toggled.getPaymentMethodKubun());
		assertEquals(method.getBankAccountCode(), toggled.getBankAccountCode());
		assertEquals(method.getClosingDay(), toggled.getClosingDay());
		assertEquals(method.getPaymentMethodSort(), toggled.getPaymentMethodSort());
		assertEquals(method.getEnableUpdateFlg(), toggled.getEnableUpdateFlg());
	}

	@Test
	@DisplayName("正常系：toggleEnableFlgで無効→有効に反転する")
	void testToggleEnableFlg_正常系_無効から有効() {
		PaymentMethod method = helper("user01", "001", "現金", null, "1", null, null, "001", false, true);
		PaymentMethod toggled = method.toggleEnableFlg();
		// 検証
		assertTrue(toggled.getEnableFlg().getValue());
	}
}
