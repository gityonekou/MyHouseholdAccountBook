/**
 * PaymentMethodInfoFormクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/11/01 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.itemmanage;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * PaymentMethodInfoFormクラスのテストクラスです。
 * 主に銀行口座コードの相関チェック(isBankAccountValid())・集計開始日の相関チェック(isClosingDayValid())をテストします。
 * 判定ロジックの実体はドメイン層のPaymentMethodKubun#requiresAccount()/requiresClosingDay()に集約されているため、
 * ここではForm自身が持つ分岐(種別の形式不正ガード、ドメインメソッドの戻り値による分岐)のみをC1網羅する(07-テスト方針書.md §3.5.1)。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("PaymentMethodInfoFormのテスト")
class PaymentMethodInfoFormTest {

	private static ValidatorFactory factory;
	private static Validator validator;

	@BeforeAll
	static void setUpBeforeClass() {
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@AfterAll
	static void tearDownAfterClass() {
		factory.close();
	}

	private PaymentMethodInfoForm createValidForm() {
		PaymentMethodInfoForm form = new PaymentMethodInfoForm();
		form.setPaymentMethodName("テスト支払方法");
		form.setPaymentMethodKubun("1");
		form.setPaymentMethodSort(1);
		return form;
	}

	// ------ isBankAccountValid ------

	@Test
	@DisplayName("正常系：支払方法種別が不正な形式値でも例外にならない(素通し)")
	void testBankAccountValid_正常系_種別が不正形式値() {
		PaymentMethodInfoForm form = createValidForm();
		form.setPaymentMethodKubun("9");
		form.setBankAccountCode(null);

		// tryFrom()がOptional.emptyを返し、orElse(true)で素通しされるため、例外にもエラーにもならない
		assertDoesNotThrow(() -> validator.validate(form));
	}

	@Test
	@DisplayName("正常系：現金(銀行口座不要な種別)は銀行口座未設定でもバリデーションエラーにならない")
	void testBankAccountValid_正常系_現金は銀行口座不要() {
		PaymentMethodInfoForm form = createValidForm();
		form.setPaymentMethodKubun("1"); // CASH: requiresAccount=false
		form.setBankAccountCode(null);

		Set<ConstraintViolation<PaymentMethodInfoForm>> violations = validator.validate(form);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("正常系：口座振替(銀行口座必須な種別)で銀行口座が設定済みの場合はバリデーションエラーにならない")
	void testBankAccountValid_正常系_口座振替で銀行口座が設定済み() {
		PaymentMethodInfoForm form = createValidForm();
		form.setPaymentMethodKubun("2"); // BANK_TRANSFER: requiresAccount=true
		form.setBankAccountCode("01");

		Set<ConstraintViolation<PaymentMethodInfoForm>> violations = validator.validate(form);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("異常系：口座振替(銀行口座必須な種別)で銀行口座が未設定の場合はバリデーションエラーになる")
	void testBankAccountValid_異常系_口座振替で銀行口座が未設定() {
		PaymentMethodInfoForm form = createValidForm();
		form.setPaymentMethodKubun("2"); // BANK_TRANSFER: requiresAccount=true
		form.setBankAccountCode(null);

		Set<ConstraintViolation<PaymentMethodInfoForm>> violations = validator.validate(form);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("銀行口座の指定が必要です")));
	}

	// ------ isClosingDayValid ------

	@Test
	@DisplayName("正常系：支払方法種別が不正な形式値でも例外にならない(素通し)")
	void testClosingDayValid_正常系_種別が不正形式値() {
		PaymentMethodInfoForm form = createValidForm();
		form.setPaymentMethodKubun("9");
		form.setClosingDay(null);

		// tryFrom()がOptional.emptyを返し、orElse(true)で素通しされるため、例外にもエラーにもならない
		assertDoesNotThrow(() -> validator.validate(form));
	}

	@Test
	@DisplayName("正常系：現金(集計開始日不要な種別)は集計開始日未設定でもバリデーションエラーにならない")
	void testClosingDayValid_正常系_現金は集計開始日不要() {
		PaymentMethodInfoForm form = createValidForm();
		form.setPaymentMethodKubun("1"); // CASH: requiresClosingDay=false
		form.setClosingDay(null);

		Set<ConstraintViolation<PaymentMethodInfoForm>> violations = validator.validate(form);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("正常系：クレジットカード(集計開始日必須な種別)で集計開始日が設定済みの場合はバリデーションエラーにならない")
	void testClosingDayValid_正常系_クレジットカードで集計開始日が設定済み() {
		PaymentMethodInfoForm form = createValidForm();
		form.setPaymentMethodKubun("3"); // CREDIT_CARD: requiresAccount=true, requiresClosingDay=true
		form.setBankAccountCode("01");
		form.setClosingDay("15");

		Set<ConstraintViolation<PaymentMethodInfoForm>> violations = validator.validate(form);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("異常系：クレジットカード(集計開始日必須な種別)で集計開始日が未設定の場合はバリデーションエラーになる")
	void testClosingDayValid_異常系_クレジットカードで集計開始日が未設定() {
		PaymentMethodInfoForm form = createValidForm();
		form.setPaymentMethodKubun("3"); // CREDIT_CARD: requiresAccount=true, requiresClosingDay=true
		form.setBankAccountCode("01");
		form.setClosingDay(null);

		Set<ConstraintViolation<PaymentMethodInfoForm>> violations = validator.validate(form);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("集計開始日は必須です")));
	}
}
