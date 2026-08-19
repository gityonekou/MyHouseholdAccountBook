/**
 * ExpenditureItemFormクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/08/19 : 1.00.00     新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.account.regist;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * ExpenditureItemFormクラスのテストクラスです。
 * 主に支払方法コード(paymentMethodCode)の必須バリデーションをテストします。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("ExpenditureItemFormのテスト")
class ExpenditureItemFormTest {

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

	private ExpenditureItemForm createValidForm() {
		ExpenditureItemForm form = new ExpenditureItemForm();
		form.setExpenditureName("テスト支出");
		form.setExpenditureKubun("1");
		form.setExpenditureKingaku(10000);
		form.setPaymentMethodCode("001");
		return form;
	}

	@Test
	@DisplayName("正常系：全項目正しく入力されていればバリデーションエラーにならない")
	void testValidate_正常系_全項目正しい() {
		ExpenditureItemForm form = createValidForm();

		Set<ConstraintViolation<ExpenditureItemForm>> violations = validator.validate(form);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("異常系：支払方法コード未入力の場合、@NotBlankバリデーションエラーになる")
	void testValidate_異常系_支払方法コード未入力() {
		ExpenditureItemForm form = createValidForm();
		form.setPaymentMethodCode(null);

		Set<ConstraintViolation<ExpenditureItemForm>> violations = validator.validate(form);
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("paymentMethodCode")),
				"支払方法コード未入力はバリデーションエラーになること");
	}

	@Test
	@DisplayName("異常系：支払方法コードが空文字の場合、@NotBlankバリデーションエラーになる")
	void testValidate_異常系_支払方法コード空文字() {
		ExpenditureItemForm form = createValidForm();
		form.setPaymentMethodCode("");

		Set<ConstraintViolation<ExpenditureItemForm>> violations = validator.validate(form);
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("paymentMethodCode")));
	}

	@Test
	@DisplayName("異常系：支出名未入力の場合、@NotBlankバリデーションエラーになる")
	void testValidate_異常系_支出名未入力() {
		ExpenditureItemForm form = createValidForm();
		form.setExpenditureName(null);

		Set<ConstraintViolation<ExpenditureItemForm>> violations = validator.validate(form);
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("expenditureName")));
	}

	@Test
	@DisplayName("異常系：支払金額が0円以下の場合、@Min(1)バリデーションエラーになる")
	void testValidate_異常系_支払金額0円() {
		ExpenditureItemForm form = createValidForm();
		form.setExpenditureKingaku(0);

		Set<ConstraintViolation<ExpenditureItemForm>> violations = validator.validate(form);
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("expenditureKingaku")));
	}
}
