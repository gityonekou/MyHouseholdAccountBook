/**
 * ShoppingClothesExpenditureItem(衣料品(私服))のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/09/23 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingregist;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingClothesExpenditureItem.ShoppingClothesItemExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.CouponAmount;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;

/**
 *<pre>
 * ShoppingClothesExpenditureItem(衣料品(私服))のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("衣料品(私服)(ShoppingClothesExpenditureItem)のテスト")
class ShoppingClothesExpenditureItemTest {

	@Test
	@DisplayName("正常系：from(ShoppingExpenditureAmount, ShoppingTaxExpenses)メソッドで正常に生成")
	void testFrom_Success() {
		// 準備
		ShoppingExpenditureAmount expenditureAmount = ShoppingExpenditureAmount.from(new BigDecimal("3000.00"));
		ShoppingTaxExpenses taxExpenses = ShoppingTaxExpenses.from(new BigDecimal("7000.00"));

		// 実行
		ShoppingClothesExpenditureItem result = ShoppingClothesExpenditureItem.from(expenditureAmount, taxExpenses);

		// 検証
		assertNotNull(result);
		assertEquals(new BigDecimal("10000.00"), result.getValue()); // 購入金額 + 消費税
		assertEquals(expenditureAmount, result.getShoppingClothesExpenditureAmount());
		assertEquals(taxExpenses, result.getShoppingClothesTaxExpenses());
		assertEquals("10000.00", result.toString());
		assertEquals("10,000円", result.toFormatString());
	}

	@Test
	@DisplayName("正常系：NULL定数が使用できる")
	void testNULL() {
		// 準備
		ShoppingClothesExpenditureItem nullValue = ShoppingClothesExpenditureItem.from(ShoppingExpenditureAmount.NULL, ShoppingTaxExpenses.NULL);

		// 検証
		assertNotNull(ShoppingClothesExpenditureItem.NULL);
		assertNull(ShoppingClothesExpenditureItem.NULL.getValue());
		assertEquals(ShoppingClothesExpenditureItem.NULL, nullValue);
	}

	@Test
	@DisplayName("異常系：expenditureAmount値がnullで例外発生")
	void testFrom_MinorNull() {
		// 準備
		ShoppingTaxExpenses taxExpenses = ShoppingTaxExpenses.from(new BigDecimal("5000.00"));

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> ShoppingClothesExpenditureItem.from(null, taxExpenses));

		// エラーメッセージ検証
		assertTrue(exception.getMessage().contains("衣料品(私服)購入金額"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("異常系：taxExpenses値がnullで例外発生")
	void testFrom_SevereNull() {
		// 準備
		ShoppingExpenditureAmount expenditureAmount = ShoppingExpenditureAmount.from(new BigDecimal("3000.00"));

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> ShoppingClothesExpenditureItem.from(expenditureAmount, null));

		// エラーメッセージ検証
		assertTrue(exception.getMessage().contains("衣料品(私服)消費税"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("異常系：購入金額がnull値、消費税が値ありで例外発生")
	void testFrom_ExpenditureNullValue_TaxHasValue() {
		// 準備
		ShoppingExpenditureAmount expenditureAmount = ShoppingExpenditureAmount.NULL;
		ShoppingTaxExpenses taxExpenses = ShoppingTaxExpenses.from(new BigDecimal("5000.00"));

		// 実行
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> ShoppingClothesExpenditureItem.from(expenditureAmount, taxExpenses));

		// エラーメッセージ検証
		assertTrue(exception.getMessage().contains("「衣料品(私服)」項目"));
		assertTrue(exception.getMessage().contains("設定値が不正"));
	}

	@Test
	@DisplayName("正常系：購入金額が値あり、消費税がnull値で生成")
	void testFrom_MinorHasValue_SevereNullValue() {
		// 準備
		ShoppingExpenditureAmount expenditureAmount = ShoppingExpenditureAmount.from(new BigDecimal("10000.00"));
		ShoppingTaxExpenses taxExpenses = ShoppingTaxExpenses.NULL;

		// 実行
		ShoppingClothesExpenditureItem result = ShoppingClothesExpenditureItem.from(expenditureAmount, taxExpenses);

		// 検証
		assertNotNull(result);
		assertEquals(new BigDecimal("10000.00"), result.getValue()); // 購入金額の値
		assertEquals(expenditureAmount, result.getShoppingClothesExpenditureAmount());
		assertEquals(taxExpenses, result.getShoppingClothesTaxExpenses());
	}

	@Test
	@DisplayName("正常系：購入金額と消費税両方がnull値で生成")
	void testFrom_BothNullValue() {
		// 準備
		ShoppingExpenditureAmount expenditureAmount = ShoppingExpenditureAmount.NULL;
		ShoppingTaxExpenses taxExpenses = ShoppingTaxExpenses.NULL;

		// 実行
		ShoppingClothesExpenditureItem result = ShoppingClothesExpenditureItem.from(expenditureAmount, taxExpenses);

		// 検証
		assertNotNull(result);
		assertNull(result.getValue());
		assertEquals(expenditureAmount, result.getShoppingClothesExpenditureAmount());
		assertEquals(taxExpenses, result.getShoppingClothesTaxExpenses());
		assertEquals("", result.toString());
		assertEquals("", result.toFormatString());
	}

	@Test
	@DisplayName("正常系：add(ShoppingClothesExpenditureItem)メソッドで加算")
	void testAdd_Success() {
		// 準備
		ShoppingClothesExpenditureItem base = ShoppingClothesExpenditureItem.from(
			ShoppingExpenditureAmount.from(new BigDecimal("2000.00")),
			ShoppingTaxExpenses.from(new BigDecimal("3000.00"))
		);
		ShoppingClothesExpenditureItem addValue = ShoppingClothesExpenditureItem.from(
			ShoppingExpenditureAmount.from(new BigDecimal("1000.00")),
			ShoppingTaxExpenses.from(new BigDecimal("2000.00"))
		);

		// 実行
		ShoppingClothesExpenditureItem result = base.add(addValue);

		// 検証
		assertNotNull(result);
		assertEquals(0, new BigDecimal("8000.00").compareTo(result.getValue()));
		assertEquals(0, new BigDecimal("3000.00").compareTo(result.getShoppingClothesExpenditureAmount().getValue()));
		assertEquals(0, new BigDecimal("5000.00").compareTo(result.getShoppingClothesTaxExpenses().getValue()));
	}

	@Test
	@DisplayName("正常系：add(ShoppingClothesExpenditureItem)でnullオブジェクトを加算")
	void testAdd_NullObject() {
		// 準備
		ShoppingClothesExpenditureItem base = ShoppingClothesExpenditureItem.from(
			ShoppingExpenditureAmount.from(new BigDecimal("5000.00")),
			ShoppingTaxExpenses.from(new BigDecimal("3000.00"))
		);

		// 実行
		ShoppingClothesExpenditureItem result = base.add(null);

		// 検証（nullの場合は元の値がそのまま返される）
		assertNotNull(result);
		assertEquals(0, new BigDecimal("8000.00").compareTo(result.getValue()));
	}


	@Test
	@DisplayName("正常系：add()でnull値との加算")
	void testAdd_WithNullValue() {
		// 準備
		ShoppingClothesExpenditureItem base = ShoppingClothesExpenditureItem.from(
			ShoppingExpenditureAmount.from(new BigDecimal("10000.00")),
			ShoppingTaxExpenses.from(new BigDecimal("5000.00"))
		);
		ShoppingClothesExpenditureItem nullBase = ShoppingClothesExpenditureItem.from(
			ShoppingExpenditureAmount.NULL,
			ShoppingTaxExpenses.NULL
		);
		ShoppingClothesExpenditureItem addValue = ShoppingClothesExpenditureItem.from(
			ShoppingExpenditureAmount.from(new BigDecimal("10000.00")),
			ShoppingTaxExpenses.from(new BigDecimal("5000.00"))
		);
		ShoppingClothesExpenditureItem addNullValue = ShoppingClothesExpenditureItem.from(
			ShoppingExpenditureAmount.NULL,
			ShoppingTaxExpenses.NULL
		);

		// 実行
		ShoppingClothesExpenditureItem result1 = base.add(addNullValue);
		ShoppingClothesExpenditureItem result2 = nullBase.add(addValue);
		ShoppingClothesExpenditureItem result3 = nullBase.add(addNullValue);

		// 検証（null値は0として扱われる）
		assertEquals(new BigDecimal("15000.00"), result1.getValue());
		assertEquals(new BigDecimal("15000.00"), result2.getValue());
		assertTrue(result3.isNull());
		assertTrue(result3.getShoppingClothesExpenditureAmount().isNull());
		assertTrue(result3.getShoppingClothesTaxExpenses().isNull());
	}

	@Test
	@DisplayName("正常系：equalsメソッドのテスト")
	void testEquals() {
		// 準備
		ShoppingClothesExpenditureItem value1 = ShoppingClothesExpenditureItem.from(
			ShoppingExpenditureAmount.from(new BigDecimal("5000.00")),
			ShoppingTaxExpenses.from(new BigDecimal("5000.00"))
		);
		ShoppingClothesExpenditureItem value2 = ShoppingClothesExpenditureItem.from(
			ShoppingExpenditureAmount.from(new BigDecimal("5000.00")),
			ShoppingTaxExpenses.from(new BigDecimal("5000.00"))
		);
		ShoppingClothesExpenditureItem value3 = ShoppingClothesExpenditureItem.from(
			ShoppingExpenditureAmount.from(new BigDecimal("3000.00")),
			ShoppingTaxExpenses.from(new BigDecimal("7000.00"))
		);

		// 検証
		assertEquals(value1, value2);
		assertNotEquals(value1, value3);
	}

	@Test
	@DisplayName("異常系：applyCoupon(CouponAmount)でクーポンがnullで例外発生")
	void testApplyCoupon_CouponNull() {
		// 準備
		ShoppingClothesExpenditureItem base = ShoppingClothesExpenditureItem.from(
			ShoppingExpenditureAmount.from(new BigDecimal("8000.00")),
			ShoppingTaxExpenses.from(new BigDecimal("2000.00"))
		);

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> base.applyCoupon(null));

		// エラーメッセージ検証
		assertTrue(exception.getMessage().contains("クーポン金額"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("正常系：applyCoupon(CouponAmount)で衣料品(私服)金額が0円の場合は支出金額0円・クーポンはそのまま残る")
	void testApplyCoupon_ItemZero() {
		// 準備
		ShoppingClothesExpenditureItem base = ShoppingClothesExpenditureItem.from(
			ShoppingExpenditureAmount.NULL,
			ShoppingTaxExpenses.NULL
		);
		CouponAmount coupon = CouponAmount.from(new BigDecimal("500.00"));

		// 実行
		ShoppingClothesItemExpenditureAmount result = base.applyCoupon(coupon);

		// 検証
		assertEquals(ExpenditureAmount.ZERO, result.getExpenditureAmount());
		assertEquals(coupon, result.getResidualCouponAmount());
		assertFalse(result.hasExpenditureAmount());
	}

	@Test
	@DisplayName("正常系：applyCoupon(CouponAmount)でクーポンが0円の場合は割引なしで衣料品(私服)金額がそのまま支出金額となる")
	void testApplyCoupon_CouponZero() {
		// 準備
		ShoppingClothesExpenditureItem base = ShoppingClothesExpenditureItem.from(
			ShoppingExpenditureAmount.from(new BigDecimal("8000.00")),
			ShoppingTaxExpenses.from(new BigDecimal("2000.00"))
		);

		// 実行
		ShoppingClothesItemExpenditureAmount result = base.applyCoupon(CouponAmount.ZERO);

		// 検証
		assertEquals(ExpenditureAmount.from(new BigDecimal("10000.00")), result.getExpenditureAmount());
		assertEquals(CouponAmount.ZERO, result.getResidualCouponAmount());
		assertTrue(result.hasExpenditureAmount());
	}

	@Test
	@DisplayName("正常系：applyCoupon(CouponAmount)でクーポン金額が衣料品(私服)金額を上回る場合は支出金額0円・差額が残クーポン額となる")
	void testApplyCoupon_CouponGreaterThanItem() {
		// 準備
		ShoppingClothesExpenditureItem base = ShoppingClothesExpenditureItem.from(
			ShoppingExpenditureAmount.from(new BigDecimal("4000.00")),
			ShoppingTaxExpenses.from(new BigDecimal("1000.00"))
		);
		CouponAmount coupon = CouponAmount.from(new BigDecimal("8000.00"));

		// 実行
		ShoppingClothesItemExpenditureAmount result = base.applyCoupon(coupon);

		// 検証
		assertEquals(ExpenditureAmount.ZERO, result.getExpenditureAmount());
		assertEquals(CouponAmount.from(new BigDecimal("3000.00")), result.getResidualCouponAmount());
		assertFalse(result.hasExpenditureAmount());
	}

	@Test
	@DisplayName("正常系：applyCoupon(CouponAmount)でクーポン金額が衣料品(私服)金額とちょうど同額の場合は支出金額0円・残クーポン額もなし")
	void testApplyCoupon_CouponEqualsItem() {
		// 準備
		ShoppingClothesExpenditureItem base = ShoppingClothesExpenditureItem.from(
			ShoppingExpenditureAmount.from(new BigDecimal("4000.00")),
			ShoppingTaxExpenses.from(new BigDecimal("1000.00"))
		);
		CouponAmount coupon = CouponAmount.from(new BigDecimal("5000.00"));

		// 実行
		ShoppingClothesItemExpenditureAmount result = base.applyCoupon(coupon);

		// 検証
		assertEquals(ExpenditureAmount.ZERO, result.getExpenditureAmount());
		assertEquals(CouponAmount.ZERO, result.getResidualCouponAmount());
		assertFalse(result.hasExpenditureAmount());
	}

	@Test
	@DisplayName("正常系：applyCoupon(CouponAmount)でクーポン金額が衣料品(私服)金額を下回る場合は割引後の金額が支出金額となり残クーポン額はなし")
	void testApplyCoupon_CouponLessThanItem() {
		// 準備
		ShoppingClothesExpenditureItem base = ShoppingClothesExpenditureItem.from(
			ShoppingExpenditureAmount.from(new BigDecimal("8000.00")),
			ShoppingTaxExpenses.from(new BigDecimal("2000.00"))
		);
		CouponAmount coupon = CouponAmount.from(new BigDecimal("3000.00"));

		// 実行
		ShoppingClothesItemExpenditureAmount result = base.applyCoupon(coupon);

		// 検証
		assertEquals(ExpenditureAmount.from(new BigDecimal("7000.00")), result.getExpenditureAmount());
		assertEquals(CouponAmount.ZERO, result.getResidualCouponAmount());
		assertTrue(result.hasExpenditureAmount());
	}
}
