/**
 * PaymentMethodNameResolverクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 * 2026/10/18 : 1.00.01  feature-1.03-dev1   PaymentMethod/BankAccount.from()型安全性対応に伴うhelper()追加
 *
 */
package com.yonetani.webapp.accountbook.domain.service.account.paymentmethod;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.domain.model.account.bankaccount.BankAccount;
import com.yonetani.webapp.accountbook.domain.model.account.bankaccount.BankAccountInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.paymentmethod.PaymentMethod;
import com.yonetani.webapp.accountbook.domain.model.account.paymentmethod.PaymentMethodInquiryList;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountCode;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountMemo;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountSort;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankName;
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
 * PaymentMethodNameResolverクラスのテストクラスです。
 * DBアクセスを伴わないため、マスタの一覧を直接渡してリゾルバを生成します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("PaymentMethodNameResolverのテスト")
class PaymentMethodNameResolverTest {

	/**
	 *<pre>
	 * プリミティブ引数を受け取り値オブジェクトへ変換してからPaymentMethod.from()を呼び出すテスト用ヘルパーメソッドです。
	 * (06-コーディング規約.md §8.3。from()が値オブジェクト型を要求するため、テストコードから直接プリミティブ値で呼び出せない)
	 *</pre>
	 */
	private PaymentMethod paymentMethodHelper(String userId, String paymentMethodCode, String paymentMethodName, String paymentMethodMemo,
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

	/**
	 *<pre>
	 * プリミティブ引数を受け取り値オブジェクトへ変換してからBankAccount.from()を呼び出すテスト用ヘルパーメソッドです。
	 *</pre>
	 */
	private BankAccount bankAccountHelper(String userId, String bankAccountCode, String bankName, String bankAccountMemo,
			String bankAccountSort, boolean enableFlg) {
		return BankAccount.from(
				UserId.from(userId),
				BankAccountCode.from(bankAccountCode),
				BankName.from(bankName),
				BankAccountMemo.from(bankAccountMemo),
				BankAccountSort.from(bankAccountSort),
				EnableFlg.from(enableFlg));
	}

	/**
	 *<pre>
	 * システム予約値(999)は支払方法名・銀行口座名ともに「－」を返すことを確認します(マスタ上の名称をそのまま画面に出さない)。
	 *</pre>
	 */
	@Test
	@DisplayName("システム予約値は「－」を返す")
	void testResolver_システム予約値はハイフンを返す() {
		PaymentMethodNameResolver resolver = new PaymentMethodNameResolver(
				PaymentMethodInquiryList.from(List.of(
						paymentMethodHelper("user01", "999", "支払方法がない", null, "1", null, null, "999", true, false))),
				BankAccountInquiryList.from(List.of()));

		assertEquals("－", resolver.getPaymentMethodName(PaymentMethodCode.from("999")).getValue());
		assertEquals("－", resolver.getBankAccountName(PaymentMethodCode.from("999")).getValue());
	}

	/**
	 *<pre>
	 * 現金・電子マネー(前払い式)のように銀行口座コードがnullの支払方法は、銀行口座名として「－」を返すことを確認します。
	 *</pre>
	 */
	@Test
	@DisplayName("銀行口座を持たない支払方法の銀行口座名は「－」を返す")
	void testResolver_銀行口座なしはハイフンを返す() {
		PaymentMethodNameResolver resolver = new PaymentMethodNameResolver(
				PaymentMethodInquiryList.from(List.of(
						paymentMethodHelper("user01", "001", "現金", null, "1", null, null, "001", true, true))),
				BankAccountInquiryList.from(List.of()));

		assertEquals("現金", resolver.getPaymentMethodName(PaymentMethodCode.from("001")).getValue());
		assertEquals("－", resolver.getBankAccountName(PaymentMethodCode.from("001")).getValue());
	}

	/**
	 *<pre>
	 * 口座振替のように銀行口座コードを持つ支払方法は、対応する銀行口座名を解決することを確認します。
	 *</pre>
	 */
	@Test
	@DisplayName("銀行口座を持つ支払方法は対応する銀行口座名を解決する")
	void testResolver_銀行口座名を解決() {
		PaymentMethodNameResolver resolver = new PaymentMethodNameResolver(
				PaymentMethodInquiryList.from(List.of(
						paymentMethodHelper("user01", "002", "〇〇銀行引き落とし", null, "2", "01", null, "002", true, true))),
				BankAccountInquiryList.from(List.of(
						bankAccountHelper("user01", "01", "〇〇銀行", "生活費口座", "01", true))));

		assertEquals("〇〇銀行引き落とし", resolver.getPaymentMethodName(PaymentMethodCode.from("002")).getValue());
		assertEquals("〇〇銀行", resolver.getBankAccountName(PaymentMethodCode.from("002")).getValue());
	}

	/**
	 *<pre>
	 * マスタに存在しないコードを指定した場合、「－」を返すことを確認します。
	 *</pre>
	 */
	@Test
	@DisplayName("マスタに存在しないコードは「－」を返す")
	void testResolver_未登録コードはハイフンを返す() {
		PaymentMethodNameResolver resolver = new PaymentMethodNameResolver(
				PaymentMethodInquiryList.from(List.of()),
				BankAccountInquiryList.from(List.of()));

		assertEquals("－", resolver.getPaymentMethodName(PaymentMethodCode.from("001")).getValue());
		assertEquals("－", resolver.getBankAccountName(PaymentMethodCode.from("001")).getValue());
	}
}
