/**
 * ShoppingRegistクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.shoppingregist;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.request.account.regist.SimpleShoppingRegistInfoForm;

/**
 *<pre>
 * ShoppingRegistクラスのテストクラスです。
 * createShoppingRegist()経由の生成を中心に検証します(支払方法コードの反映を含む)。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("買い物登録情報(ShoppingRegist)のテスト")
class ShoppingRegistTest {

	private SimpleShoppingRegistInfoForm createValidForm() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setTargetYearMonth("202511");
		form.setShoppingRegistCode("001");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setShoppingDate(LocalDate.of(2025, 11, 5));
		form.setShoppingRemarks("テスト備考");
		form.setPaymentMethodCode("001");
		form.setShoppingFoodExpenses(3000);
		form.setShoppingFoodTaxExpenses(300);
		form.setShoppingFoodBExpenses(1000);
		form.setShoppingFoodBTaxExpenses(100);
		form.setShoppingFoodCExpenses(500);
		form.setShoppingFoodCTaxExpenses(50);
		form.setShoppingDineOutExpenses(0);
		form.setShoppingDineOutTaxExpenses(0);
		form.setShoppingConsumerGoodsExpenses(0);
		form.setShoppingConsumerGoodsTaxExpenses(0);
		form.setShoppingClothesExpenses(0);
		form.setShoppingClothesTaxExpenses(0);
		form.setShoppingWorkExpenses(0);
		form.setShoppingWorkTaxExpenses(0);
		form.setShoppingHouseEquipmentExpenses(0);
		form.setShoppingHouseEquipmentTaxExpenses(0);
		form.setShoppingCouponPrice(0);
		form.setTotalPurchasePrice(4950);
		form.setTaxTotalPurchasePrice(450);
		form.setShoppingTotalAmount(4950);
		return form;
	}

	@Test
	@DisplayName("正常系：createShoppingRegistでフォームの値がドメインに反映される(支払方法コードを含む)")
	void testCreateShoppingRegist_正常系_フォームの値が反映される() {
		SimpleShoppingRegistInfoForm form = createValidForm();

		ShoppingRegist domain = ShoppingRegist.createShoppingRegist(UserId.from("user01"), form);

		assertEquals("user01", domain.getUserId().getValue());
		assertEquals("202511", domain.getTargetYearMonth().getValue());
		assertEquals("001", domain.getShoppingRegistCode().getValue());
		assertEquals("901", domain.getShopKubunCode().getValue());
		assertEquals("001", domain.getShopCode().getValue());
		assertEquals(LocalDate.of(2025, 11, 5), domain.getShoppingDate().getValue());
		assertEquals("テスト備考", domain.getShoppingRemarks().getValue());
		assertEquals("001", domain.getPaymentMethodCode().getValue());
	}

	@Test
	@DisplayName("正常系：createShoppingRegistは金額項目をBigDecimalスケール2に変換する")
	void testCreateShoppingRegist_正常系_金額はスケール2に変換される() {
		SimpleShoppingRegistInfoForm form = createValidForm();

		ShoppingRegist domain = ShoppingRegist.createShoppingRegist(UserId.from("user01"), form);

		assertEquals(0, new java.math.BigDecimal("3000.00").compareTo(domain.getShoppingFoodExpenditureAmount().getValue()));
		assertEquals(0, new java.math.BigDecimal("4950.00").compareTo(domain.getTotalPurchasePrice().getValue()));
		assertEquals(0, new java.math.BigDecimal("4950.00").compareTo(domain.getShoppingTotalAmount().getValue()));
	}

	@Test
	@DisplayName("正常系：買い物集計8項目相当の支払方法がない(999)を保持できる")
	void testCreateShoppingRegist_正常系_支払方法がない設定() {
		SimpleShoppingRegistInfoForm form = createValidForm();
		form.setPaymentMethodCode("999");

		ShoppingRegist domain = ShoppingRegist.createShoppingRegist(UserId.from("user01"), form);

		assertEquals("999", domain.getPaymentMethodCode().getValue());
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		ShoppingRegist domain1 = ShoppingRegist.createShoppingRegist(UserId.from("user01"), createValidForm());
		ShoppingRegist domain2 = ShoppingRegist.createShoppingRegist(UserId.from("user01"), createValidForm());

		assertEquals(domain1, domain2);
		assertEquals(domain1.hashCode(), domain2.hashCode());
	}

	@Test
	@DisplayName("正常系：支払方法コードの違いで等価にならない")
	void testEquals_正常系_支払方法コードの違いで異なる() {
		SimpleShoppingRegistInfoForm form1 = createValidForm();
		SimpleShoppingRegistInfoForm form2 = createValidForm();
		form2.setPaymentMethodCode("002");

		ShoppingRegist domain1 = ShoppingRegist.createShoppingRegist(UserId.from("user01"), form1);
		ShoppingRegist domain2 = ShoppingRegist.createShoppingRegist(UserId.from("user01"), form2);

		assertNotEquals(domain1, domain2);
	}
}
