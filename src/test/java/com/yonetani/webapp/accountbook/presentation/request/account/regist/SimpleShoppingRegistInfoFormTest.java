/**
 * SimpleShoppingRegistInfoFormクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 * 2026/11/01 : 1.00.01  feature-1.03-dev1   相関チェック(@AssertTrue)のドメイン層移動に伴うテストを追加・更新(isCheckedShoppingDate/isCheckedShopKubunCodeConsumerGoods/isCheckedTax)
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
 * 相関バリデーション(isCheckedShoppingDate/isCheckedShopKubunCodeConsumerGoods/
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

	@Test
	@DisplayName("異常系：targetYearMonthが未入力(null)の場合はfalse(他項目に形式チェックがないため、ここで検出する)")
	void testIsCheckedShoppingDate_異常系_targetYearMonth未入力() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setTargetYearMonth(null);
		assertFalse(form.isCheckedShoppingDate());
	}

	@Test
	@DisplayName("異常系：targetYearMonthが不正な形式値の場合はfalse(他項目に形式チェックがないため、ここで検出する)")
	void testIsCheckedShoppingDate_異常系_targetYearMonth不正形式() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setTargetYearMonth("2025");
		assertFalse(form.isCheckedShoppingDate());
	}

	// ========== isCheckedShopKubunCodeConsumerGoods ==========

	@Test
	@DisplayName("正常系：店舗区分が対象外区分(901)の場合、日用品未入力でもtrue")
	void testIsCheckedShopKubunCodeConsumerGoods_正常系_対象外区分はtrue() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShopKubunCode("901");
		form.setShoppingConsumerGoodsExpenses(null);
		assertTrue(form.isCheckedShopKubunCodeConsumerGoods());
	}

	@Test
	@DisplayName("異常系：店舗区分が薬局/薬局複合店/病院(905)かつ日用品未入力の場合はfalse")
	void testIsCheckedShopKubunCodeConsumerGoods_異常系_医療系_日用品未入力() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShopKubunCode("905");
		form.setShoppingConsumerGoodsExpenses(null);
		assertFalse(form.isCheckedShopKubunCodeConsumerGoods());
	}

	@Test
	@DisplayName("正常系：店舗区分が薬局/薬局複合店/病院(905)かつ日用品入力ありの場合はtrue")
	void testIsCheckedShopKubunCodeConsumerGoods_正常系_医療系_日用品入力あり() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShopKubunCode("905");
		form.setShoppingConsumerGoodsExpenses(1000);
		assertTrue(form.isCheckedShopKubunCodeConsumerGoods());
	}

	@Test
	@DisplayName("異常系：店舗区分が理髪店(908)かつ日用品未入力の場合はfalse")
	void testIsCheckedShopKubunCodeConsumerGoods_異常系_理髪店_日用品未入力() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShopKubunCode("908");
		form.setShoppingConsumerGoodsExpenses(null);
		assertFalse(form.isCheckedShopKubunCodeConsumerGoods());
	}

	@Test
	@DisplayName("正常系：店舗区分が理髪店(908)かつ日用品入力ありの場合はtrue")
	void testIsCheckedShopKubunCodeConsumerGoods_正常系_理髪店_日用品入力あり() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShopKubunCode("908");
		form.setShoppingConsumerGoodsExpenses(1000);
		assertTrue(form.isCheckedShopKubunCodeConsumerGoods());
	}

	@Test
	@DisplayName("正常系：店舗区分が未入力(空文字)の場合はtrue(@NotBlank側で検出するためチェック対象外)")
	void testIsCheckedShopKubunCodeConsumerGoods_正常系_未入力() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShopKubunCode("");
		form.setShoppingConsumerGoodsExpenses(null);
		assertTrue(form.isCheckedShopKubunCodeConsumerGoods());
	}

	@Test
	@DisplayName("正常系：店舗区分が不正な形式値の場合はtrue(tryFrom()でOptional.empty()のためチェック対象外)")
	void testIsCheckedShopKubunCodeConsumerGoods_正常系_不正形式値() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShopKubunCode("ABC");
		form.setShoppingConsumerGoodsExpenses(null);
		assertTrue(form.isCheckedShopKubunCodeConsumerGoods());
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
	@DisplayName("異常系：金額が未入力で消費税のみ入力されている場合はfalse(食料品(必須))")
	void testIsCheckedTax_異常系_消費税のみ入力() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShoppingFoodExpenses(null);
		form.setShoppingFoodTaxExpenses(100);
		assertFalse(form.isCheckedTax());
	}

	@Test
	@DisplayName("異常系：金額が未入力で消費税のみ入力されている場合はfalse(食料品B(無駄遣い))")
	void testIsCheckedTax_異常系_消費税のみ入力_食料品B() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShoppingFoodBExpenses(null);
		form.setShoppingFoodBTaxExpenses(100);
		assertFalse(form.isCheckedTax());
	}

	@Test
	@DisplayName("異常系：金額が未入力で消費税のみ入力されている場合はfalse(食料品C(お酒類))")
	void testIsCheckedTax_異常系_消費税のみ入力_食料品C() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShoppingFoodCExpenses(null);
		form.setShoppingFoodCTaxExpenses(100);
		assertFalse(form.isCheckedTax());
	}

	@Test
	@DisplayName("異常系：金額が未入力で消費税のみ入力されている場合はfalse(外食)")
	void testIsCheckedTax_異常系_消費税のみ入力_外食() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShoppingDineOutExpenses(null);
		form.setShoppingDineOutTaxExpenses(100);
		assertFalse(form.isCheckedTax());
	}

	@Test
	@DisplayName("異常系：金額が未入力で消費税のみ入力されている場合はfalse(日用品)")
	void testIsCheckedTax_異常系_消費税のみ入力_日用品() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShoppingConsumerGoodsExpenses(null);
		form.setShoppingConsumerGoodsTaxExpenses(100);
		assertFalse(form.isCheckedTax());
	}

	@Test
	@DisplayName("異常系：金額が未入力で消費税のみ入力されている場合はfalse(衣料品(私服))")
	void testIsCheckedTax_異常系_消費税のみ入力_衣料品() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShoppingClothesExpenses(null);
		form.setShoppingClothesTaxExpenses(100);
		assertFalse(form.isCheckedTax());
	}

	@Test
	@DisplayName("異常系：金額が未入力で消費税のみ入力されている場合はfalse(仕事)")
	void testIsCheckedTax_異常系_消費税のみ入力_仕事() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShoppingWorkExpenses(null);
		form.setShoppingWorkTaxExpenses(100);
		assertFalse(form.isCheckedTax());
	}

	@Test
	@DisplayName("異常系：金額が未入力で消費税のみ入力されている場合はfalse(住居設備)")
	void testIsCheckedTax_異常系_消費税のみ入力_住居設備() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShoppingHouseEquipmentExpenses(null);
		form.setShoppingHouseEquipmentTaxExpenses(100);
		assertFalse(form.isCheckedTax());
	}

	@Test
	@DisplayName("正常系：金額がマイナス値(不正形式)で消費税入力ありの場合はtrue(tryFrom()でOptional.empty()のため@Min側で検出)")
	void testIsCheckedTax_正常系_金額不正形式() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setShoppingFoodExpenses(-1);
		form.setShoppingFoodTaxExpenses(100);
		assertTrue(form.isCheckedTax());
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
