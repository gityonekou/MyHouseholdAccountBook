/**
 * ExpenditureItemFormクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 * 2026/11/01 : 1.00.01  feature-1.03-dev1   支払方法・銀行口座管理追加対応(支出一覧に支払方法名・引落先口座名を追加)
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

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;

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
		// isPaymentMethodCodeValid()は新規追加(add)時のみ判定対象のため、デフォルトでaddを設定
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
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

	@Test
	@DisplayName("正常系：isPaymentMethodCodeValid_未入力の場合はtrue(@NotBlank側で検出するため対象外)")
	void testIsPaymentMethodCodeValid_未入力() {
		ExpenditureItemForm form = createValidForm();
		form.setPaymentMethodCode(null);

		assertTrue(form.isPaymentMethodCodeValid());
	}

	@Test
	@DisplayName("正常系：isPaymentMethodCodeValid_形式不正な値の場合はtrue(tryFromがOptional.emptyを返すため対象外)")
	void testIsPaymentMethodCodeValid_形式不正() {
		ExpenditureItemForm form = createValidForm();
		form.setPaymentMethodCode("abc");

		assertTrue(form.isPaymentMethodCodeValid());
	}

	@Test
	@DisplayName("正常系：isPaymentMethodCodeValid_システム予約値以外の場合はtrue")
	void testIsPaymentMethodCodeValid_システム予約値以外() {
		ExpenditureItemForm form = createValidForm();
		form.setPaymentMethodCode("001");

		assertTrue(form.isPaymentMethodCodeValid());
	}

	@Test
	@DisplayName("異常系：isPaymentMethodCodeValid_システム予約値(999)の場合はfalse")
	void testIsPaymentMethodCodeValid_システム予約値999() {
		ExpenditureItemForm form = createValidForm();
		form.setPaymentMethodCode("999");

		assertFalse(form.isPaymentMethodCodeValid());
	}

	@Test
	@DisplayName("異常系：isPaymentMethodCodeValid_予約帯内(990)の場合はfalse")
	void testIsPaymentMethodCodeValid_予約帯990() {
		ExpenditureItemForm form = createValidForm();
		form.setPaymentMethodCode("990");

		assertFalse(form.isPaymentMethodCodeValid());
	}

	@Test
	@DisplayName("異常系：支払方法コードにシステム予約値(999)を送信した場合、@AssertTrueバリデーションエラーになる")
	void testValidate_異常系_支払方法コードシステム予約値() {
		ExpenditureItemForm form = createValidForm();
		form.setPaymentMethodCode("999");

		Set<ConstraintViolation<ExpenditureItemForm>> violations = validator.validate(form);
		assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("paymentMethodCodeValid")));
	}

	@Test
	@DisplayName("正常系：isPaymentMethodCodeValid_action=updateの場合はシステム予約値(999)でもtrue"
			+ "(disabled+hidden併記による既存行の訂正・削除操作を妨げないための回帰防止テスト)")
	void testIsPaymentMethodCodeValid_Update_システム予約値999でもtrue() {
		ExpenditureItemForm form = createValidForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		form.setPaymentMethodCode("999");

		assertTrue(form.isPaymentMethodCodeValid());
	}

	@Test
	@DisplayName("正常系：isPaymentMethodCodeValid_actionが未設定(null)の場合はシステム予約値(999)でもtrue"
			+ "(新規追加判定時のみ検証対象とするためのガード確認)")
	void testIsPaymentMethodCodeValid_ActionNull_システム予約値999でもtrue() {
		ExpenditureItemForm form = createValidForm();
		form.setAction(null);
		form.setPaymentMethodCode("999");

		assertTrue(form.isPaymentMethodCodeValid());
	}

	@Test
	@DisplayName("正常系：支払方法コードにシステム予約値(999)を送信してもaction=updateの場合はバリデーションエラーにならない"
			+ "(disabled+hidden併記による既存行の訂正・削除操作を妨げないための回帰防止テスト)")
	void testValidate_正常系_Update_支払方法コードシステム予約値999でもエラーにならない() {
		ExpenditureItemForm form = createValidForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		form.setPaymentMethodCode("999");

		Set<ConstraintViolation<ExpenditureItemForm>> violations = validator.validate(form);
		assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("paymentMethodCodeValid")));
	}
}
