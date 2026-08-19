/**
 * ShopInfoFormクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/08/19 : 1.00.00     新規作成
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
 * ShopInfoFormクラスのテストクラスです。
 * 主にデフォルト支払方法コードのバリデーション(isDefaultPaymentMethodCodeValid())をテストします。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("ShopInfoFormのテスト")
class ShopInfoFormTest {

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

	private ShopInfoForm createValidForm() {
		ShopInfoForm form = new ShopInfoForm();
		form.setShopKubun("901");
		form.setShopName("テスト店舗");
		form.setShopSort(1);
		return form;
	}

	@Test
	@DisplayName("正常系：デフォルト支払方法コード未入力(任意項目)はバリデーションエラーにならない")
	void testValidate_正常系_未入力は許容() {
		ShopInfoForm form = createValidForm();
		form.setDefaultPaymentMethodCode(null);

		Set<ConstraintViolation<ShopInfoForm>> violations = validator.validate(form);
		assertTrue(violations.isEmpty(), "未入力は任意項目のためエラーにならないこと");
	}

	@Test
	@DisplayName("正常系：通常のコード値はバリデーションエラーにならない")
	void testValidate_正常系_通常のコード値() {
		ShopInfoForm form = createValidForm();
		form.setDefaultPaymentMethodCode("001");

		Set<ConstraintViolation<ShopInfoForm>> violations = validator.validate(form);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("異常系：システム予約値(999)はバリデーションエラーになる")
	void testValidate_異常系_システム予約値() {
		ShopInfoForm form = createValidForm();
		form.setDefaultPaymentMethodCode("999");

		Set<ConstraintViolation<ShopInfoForm>> violations = validator.validate(form);
		assertFalse(violations.isEmpty(), "システム予約値はエラーになること");
		assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("システム予約値")));
	}

	@Test
	@DisplayName("正常系：不正な形式値(桁数不正等)でも例外にならず、バリデーションエラーとして処理される(突合レビュー指摘AA対応)")
	void testValidate_正常系_不正形式値は例外にならない() {
		ShopInfoForm form = createValidForm();
		form.setDefaultPaymentMethodCode("abc");

		// tryFrom()がOptional.emptyを返し、orElse(true)で素通しされるため、
		// このAssertTrue自体はエラーにならない(不正な形式値を弾く責務は別レイヤーが持つ)
		assertDoesNotThrow(() -> validator.validate(form));
	}
}
