/**
 * PaymentMethodInfoComponentクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/08/19 : 1.00.00     新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.component;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.yonetani.webapp.accountbook.application.usecase.account.component.PaymentMethodInfoComponent.PaymentMethodNameResolver;
import com.yonetani.webapp.accountbook.domain.model.account.bankaccount.BankAccount;
import com.yonetani.webapp.accountbook.domain.model.account.bankaccount.BankAccountInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.paymentmethod.PaymentMethod;
import com.yonetani.webapp.accountbook.domain.model.account.paymentmethod.PaymentMethodInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.repository.account.bankaccount.BankAccountTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.paymentmethod.PaymentMethodTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;

/**
 *<pre>
 * PaymentMethodInfoComponentクラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentMethodInfoComponentのテスト")
class PaymentMethodInfoComponentTest {

	@Mock
	private PaymentMethodTableRepository paymentMethodRepository;
	@Mock
	private BankAccountTableRepository bankAccountRepository;

	private PaymentMethodInfoComponent component;

	private final UserId TEST_USER_ID = UserId.from("user01");

	@BeforeEach
	void setUp() {
		component = new PaymentMethodInfoComponent(paymentMethodRepository, bankAccountRepository);
	}

	/**
	 *<pre>
	 * createResolver()が支払方法マスタ・銀行口座マスタをそれぞれ1回ずつ(合計2回)しか呼び出さないことを確認します(レビュー指摘④)。
	 *</pre>
	 */
	@Test
	@DisplayName("createResolver()はリポジトリを1回ずつしか呼び出さない(N+1回避)")
	void testCreateResolver_DB呼び出し回数() {
		when(paymentMethodRepository.findByUserId(any(SearchQueryUserId.class))).thenReturn(PaymentMethodInquiryList.from(List.of(
				PaymentMethod.from("user01", "001", "現金", null, "1", null, null, "001", true, true))));
		when(bankAccountRepository.findById(any(SearchQueryUserId.class))).thenReturn(BankAccountInquiryList.from(List.of(
				BankAccount.from("user01", "01", "テスト銀行", null, "01", true))));

		PaymentMethodNameResolver resolver = component.createResolver(TEST_USER_ID);
		// 複数回名称解決を行っても、リポジトリ呼び出しは構築時の1回のみであること
		assertEquals("現金", resolver.getPaymentMethodName(PaymentMethodCode.from("001")).getValue());
		assertEquals("現金", resolver.getPaymentMethodName(PaymentMethodCode.from("001")).getValue());
		resolver.getBankAccountName(PaymentMethodCode.from("001"));

		verify(paymentMethodRepository, times(1)).findByUserId(any(SearchQueryUserId.class));
		verify(bankAccountRepository, times(1)).findById(any(SearchQueryUserId.class));
	}

	/**
	 *<pre>
	 * システム予約値(999)は支払方法名・銀行口座名ともに「－」を返すことを確認します(マスタ上の名称をそのまま画面に出さない)。
	 *</pre>
	 */
	@Test
	@DisplayName("システム予約値は「－」を返す")
	void testResolver_システム予約値はハイフンを返す() {
		when(paymentMethodRepository.findByUserId(any(SearchQueryUserId.class))).thenReturn(PaymentMethodInquiryList.from(List.of(
				PaymentMethod.from("user01", "999", "支払方法がない", null, "1", null, null, "999", true, false))));
		when(bankAccountRepository.findById(any(SearchQueryUserId.class))).thenReturn(BankAccountInquiryList.from(List.of()));

		PaymentMethodNameResolver resolver = component.createResolver(TEST_USER_ID);
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
		when(paymentMethodRepository.findByUserId(any(SearchQueryUserId.class))).thenReturn(PaymentMethodInquiryList.from(List.of(
				PaymentMethod.from("user01", "001", "現金", null, "1", null, null, "001", true, true))));
		when(bankAccountRepository.findById(any(SearchQueryUserId.class))).thenReturn(BankAccountInquiryList.from(List.of()));

		PaymentMethodNameResolver resolver = component.createResolver(TEST_USER_ID);
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
		when(paymentMethodRepository.findByUserId(any(SearchQueryUserId.class))).thenReturn(PaymentMethodInquiryList.from(List.of(
				PaymentMethod.from("user01", "002", "〇〇銀行引き落とし", null, "2", "01", null, "002", true, true))));
		when(bankAccountRepository.findById(any(SearchQueryUserId.class))).thenReturn(BankAccountInquiryList.from(List.of(
				BankAccount.from("user01", "01", "〇〇銀行", "生活費口座", "01", true))));

		PaymentMethodNameResolver resolver = component.createResolver(TEST_USER_ID);
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
		when(paymentMethodRepository.findByUserId(any(SearchQueryUserId.class))).thenReturn(PaymentMethodInquiryList.from(List.of()));
		when(bankAccountRepository.findById(any(SearchQueryUserId.class))).thenReturn(BankAccountInquiryList.from(List.of()));

		PaymentMethodNameResolver resolver = component.createResolver(TEST_USER_ID);
		assertEquals("－", resolver.getPaymentMethodName(PaymentMethodCode.from("001")).getValue());
		assertEquals("－", resolver.getBankAccountName(PaymentMethodCode.from("001")).getValue());
	}

	/**
	 *<pre>
	 * getPaymentMethodOptions()がfindSelectableByUserId()経由で選択肢を生成することを確認します。
	 *</pre>
	 */
	@Test
	@DisplayName("getPaymentMethodOptions()はfindSelectableByUserId()の結果から選択肢を生成する")
	void testGetPaymentMethodOptions() {
		when(paymentMethodRepository.findSelectableByUserId(any(SearchQueryUserId.class))).thenReturn(PaymentMethodInquiryList.from(List.of(
				PaymentMethod.from("user01", "001", "現金", null, "1", null, null, "001", true, true))));

		List<OptionItem> options = component.getPaymentMethodOptions(TEST_USER_ID);
		assertEquals(1, options.size());
		assertEquals("001", options.get(0).getValue());
		assertEquals("現金", options.get(0).getText());
	}

	/**
	 *<pre>
	 * getFixedCostPaymentMethodOptions()がfindEnabledByUserId()経由で選択肢を生成することを確認します(固定費登録画面専用)。
	 *</pre>
	 */
	@Test
	@DisplayName("getFixedCostPaymentMethodOptions()はfindEnabledByUserId()の結果から選択肢を生成する")
	void testGetFixedCostPaymentMethodOptions() {
		when(paymentMethodRepository.findEnabledByUserId(any(SearchQueryUserId.class))).thenReturn(PaymentMethodInquiryList.from(List.of(
				PaymentMethod.from("user01", "999", "支払方法がない", null, "1", null, null, "999", true, false))));

		List<OptionItem> options = component.getFixedCostPaymentMethodOptions(TEST_USER_ID);
		assertEquals(1, options.size());
		assertEquals("999", options.get(0).getValue());
	}

	/**
	 *<pre>
	 * getBankAccountOptions()がfindSelectableByUserId()経由で選択肢を生成することを確認します。
	 *</pre>
	 */
	@Test
	@DisplayName("getBankAccountOptions()はfindSelectableByUserId()の結果から選択肢を生成する")
	void testGetBankAccountOptions() {
		when(bankAccountRepository.findSelectableByUserId(any(SearchQueryUserId.class))).thenReturn(BankAccountInquiryList.from(List.of(
				BankAccount.from("user01", "01", "〇〇銀行", null, "01", true))));

		List<OptionItem> options = component.getBankAccountOptions(TEST_USER_ID);
		assertEquals(1, options.size());
		assertEquals("01", options.get(0).getValue());
		assertEquals("〇〇銀行", options.get(0).getText());
	}
}
