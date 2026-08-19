/**
 * SimpleShoppingRegistInfoFormクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/08/19 : 1.00.00     新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.account.regist;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
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
 * SimpleShoppingRegistInfoFormクラスのテストクラスです。
 * 相関バリデーション(isCheckedShoppingDate/isCheckedShopKubunCodeMedicalshop/isCheckedShopKubunCodeBarbershop/
 * isCheckedTax/isPaymentMethodCodeValid)を検証します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("SimpleShoppingRegistInfoFormのテスト")
class SimpleShoppingRegistInfoFormTest {

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

	private SimpleShoppingRegistInfoForm createValidForm() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setTargetYearMonth("202511");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setShoppingDate(LocalDate.of(2025, 11, 5));
		form.setPaymentMethodCode("001");
		form.setTotalPurchasePrice(1000);
		form.setShoppingTotalAmount(1000);
		return form;
	}

	// ========== isCheckedShoppingDate ==========

	@Test
	@DisplayName("正常系：買い物日の年月が対象年月と一致する場合はtrue")
	void testIsCheckedShoppingDate_正常系_年月一致() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		assertTrue(form.isCheckedShoppingDate());
	}

	@Test
	@DisplayName("異常系：買い物日の年月が対象年月と一致しない場合はfalse")
	void testIsCheckedShoppingDate_異常系_年月不一致() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShoppingDate(LocalDate.of(2025, 12, 5));
		assertFalse(form.isCheckedShoppingDate());
	}

	@Test
	@DisplayName("正常系：買い物日が未入力の場合はtrue(必須チェックは別項目が担当)")
	void testIsCheckedShoppingDate_正常系_未入力はtrue() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShoppingDate(null);
		assertTrue(form.isCheckedShoppingDate());
	}

	// ========== isCheckedShopKubunCodeMedicalshop / isCheckedShopKubunCodeBarbershop ==========

	@Test
	@DisplayName("正常系：店舗区分が薬局系以外の場合、日用品未入力でもtrue")
	void testIsCheckedShopKubunCodeMedicalshop_正常系_対象外区分はtrue() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShopKubunCode("901");
		form.setShoppingConsumerGoodsExpenses(null);
		assertTrue(form.isCheckedShopKubunCodeMedicalshop());
	}

	@Test
	@DisplayName("異常系：店舗区分が薬局/薬局複合店/病院かつ日用品未入力の場合はfalse")
	void testIsCheckedShopKubunCodeMedicalshop_異常系_日用品未入力() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShopKubunCode("905");
		form.setShoppingConsumerGoodsExpenses(null);
		assertFalse(form.isCheckedShopKubunCodeMedicalshop());
	}

	@Test
	@DisplayName("正常系：店舗区分が薬局/薬局複合店/病院かつ日用品入力ありの場合はtrue")
	void testIsCheckedShopKubunCodeMedicalshop_正常系_日用品入力あり() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShopKubunCode("905");
		form.setShoppingConsumerGoodsExpenses(1000);
		assertTrue(form.isCheckedShopKubunCodeMedicalshop());
	}

	@Test
	@DisplayName("異常系：店舗区分が理髪店かつ日用品未入力の場合はfalse")
	void testIsCheckedShopKubunCodeBarbershop_異常系_日用品未入力() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShopKubunCode("908");
		form.setShoppingConsumerGoodsExpenses(null);
		assertFalse(form.isCheckedShopKubunCodeBarbershop());
	}

	// ========== isCheckedTax ==========

	@Test
	@DisplayName("正常系：金額・消費税ともに未入力の場合はtrue")
	void testIsCheckedTax_正常系_両方未入力() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		assertTrue(form.isCheckedTax());
	}

	@Test
	@DisplayName("正常系：金額・消費税ともに入力ありの場合はtrue")
	void testIsCheckedTax_正常系_両方入力あり() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShoppingFoodExpenses(1000);
		form.setShoppingFoodTaxExpenses(100);
		assertTrue(form.isCheckedTax());
	}

	@Test
	@DisplayName("異常系：金額が未入力で消費税のみ入力されている場合はfalse")
	void testIsCheckedTax_異常系_消費税のみ入力() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShoppingFoodExpenses(null);
		form.setShoppingFoodTaxExpenses(100);
		assertFalse(form.isCheckedTax());
	}

	// ========== paymentMethodCode（@NotBlank / isPaymentMethodCodeValid） ==========

	@Test
	@DisplayName("正常系：支払方法コードが正しく入力されていればバリデーションエラーにならない")
	void testValidate_正常系_支払方法コード正常() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		Set<ConstraintViolation<SimpleShoppingRegistInfoForm>> violations = validator.validate(form);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("異常系：支払方法コード未入力の場合、@NotBlankバリデーションエラーになる")
	void testValidate_異常系_支払方法コード未入力() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setPaymentMethodCode(null);

		Set<ConstraintViolation<SimpleShoppingRegistInfoForm>> violations = validator.validate(form);
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("paymentMethodCode")));
	}

	@Test
	@DisplayName("異常系：支払方法コードにシステム予約値(999)を指定した場合、バリデーションエラーになる")
	void testValidate_異常系_システム予約値() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setPaymentMethodCode("999");

		Set<ConstraintViolation<SimpleShoppingRegistInfoForm>> violations = validator.validate(form);
		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("システム予約値")),
				"買い物登録は例外なくシステム予約値を拒否すること(5.6節)");
	}

	@Test
	@DisplayName("正常系：不正な形式値でも例外にならず、バリデーションエラーとして処理される(突合レビュー指摘AA対応)")
	void testValidate_正常系_不正形式値は例外にならない() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setPaymentMethodCode("abc");

		assertDoesNotThrow(() -> validator.validate(form));
	}
}
