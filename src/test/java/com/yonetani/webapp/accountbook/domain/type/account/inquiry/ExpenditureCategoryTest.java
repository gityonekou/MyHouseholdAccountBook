/**
 * ExpenditureCategory(支出区分)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/03/15 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostName;

/**
 *<pre>
 * ExpenditureCategory(支出区分)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@DisplayName("支出区分(ExpenditureCategory)のテスト")
class ExpenditureCategoryTest {

	@Test
	@DisplayName("正常系：無駄遣いなし(1)で生成できる")
	void testFromString_正常系_無駄遣いなし() {
		ExpenditureCategory category = ExpenditureCategory.from("1");
		assertNotNull(category);
		assertEquals("1", category.getValue());
	}

	@Test
	@DisplayName("正常系：無駄遣い軽度(2)で生成できる")
	void testFromString_正常系_無駄遣い軽度() {
		ExpenditureCategory category = ExpenditureCategory.from("2");
		assertNotNull(category);
		assertEquals("2", category.getValue());
	}

	@Test
	@DisplayName("正常系：無駄遣い重度(3)で生成できる")
	void testFromString_正常系_無駄遣い重度() {
		ExpenditureCategory category = ExpenditureCategory.from("3");
		assertNotNull(category);
		assertEquals("3", category.getValue());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFromString_異常系_null値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ExpenditureCategory.from((String) null));
	}

	@Test
	@DisplayName("異常系：空文字で例外が発生する")
	void testFromString_異常系_空文字() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ExpenditureCategory.from(""));
	}

	@Test
	@DisplayName("異常系：不正値(1,2,3以外)で例外が発生する")
	void testFromString_異常系_不正値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ExpenditureCategory.from("4"));
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ExpenditureCategory.from("0"));
	}

	@Test
	@DisplayName("正常系：固定費名に無駄遣いBが含まれる場合はWASTED_Bを返す")
	void testFromFixedCostName_無駄遣いB() {
		ExpenditureCategory category = ExpenditureCategory.from(FixedCostName.from("定期購読費(無駄遣いB)"));
		assertEquals(ExpenditureCategory.WASTED_B, category);
	}

	@Test
	@DisplayName("正常系：固定費名に無駄遣いCが含まれる場合はWASTED_Cを返す")
	void testFromFixedCostName_無駄遣いC() {
		ExpenditureCategory category = ExpenditureCategory.from(FixedCostName.from("サブスク費(無駄遣いC)"));
		assertEquals(ExpenditureCategory.WASTED_C, category);
	}

	@Test
	@DisplayName("正常系：固定費名に無駄遣い文字列がない場合はNON_WASTEDを返す")
	void testFromFixedCostName_無駄遣いなし() {
		ExpenditureCategory category = ExpenditureCategory.from(FixedCostName.from("家賃"));
		assertEquals(ExpenditureCategory.NON_WASTED, category);
	}

	@Test
	@DisplayName("正常系：静的定数NON_WASTEDは値1を持つ")
	void testStaticConstant_NON_WASTED() {
		assertEquals("1", ExpenditureCategory.NON_WASTED.getValue());
	}

	@Test
	@DisplayName("正常系：静的定数WASTED_Bは値2を持つ")
	void testStaticConstant_WASTED_B() {
		assertEquals("2", ExpenditureCategory.WASTED_B.getValue());
	}

	@Test
	@DisplayName("正常系：静的定数WASTED_Cは値3を持つ")
	void testStaticConstant_WASTED_C() {
		assertEquals("3", ExpenditureCategory.WASTED_C.getValue());
	}

	@Test
	@DisplayName("正常系：isNonWastedが正しく判定する")
	void testIsNonWasted() {
		assertTrue(ExpenditureCategory.isNonWasted(ExpenditureCategory.NON_WASTED));
		assertFalse(ExpenditureCategory.isNonWasted(ExpenditureCategory.WASTED_B));
		assertFalse(ExpenditureCategory.isNonWasted(ExpenditureCategory.WASTED_C));
	}

	@Test
	@DisplayName("正常系：isWastedBが正しく判定する")
	void testIsWastedB() {
		assertFalse(ExpenditureCategory.isWastedB(ExpenditureCategory.NON_WASTED));
		assertTrue(ExpenditureCategory.isWastedB(ExpenditureCategory.WASTED_B));
		assertFalse(ExpenditureCategory.isWastedB(ExpenditureCategory.WASTED_C));
	}

	@Test
	@DisplayName("正常系：isWastedCが正しく判定する")
	void testIsWastedC() {
		assertFalse(ExpenditureCategory.isWastedC(ExpenditureCategory.NON_WASTED));
		assertFalse(ExpenditureCategory.isWastedC(ExpenditureCategory.WASTED_B));
		assertTrue(ExpenditureCategory.isWastedC(ExpenditureCategory.WASTED_C));
	}

	@Test
	@DisplayName("正常系：isWastedBOrCが正しく判定する")
	void testIsWastedBOrC() {
		assertFalse(ExpenditureCategory.NON_WASTED.isWastedBOrC());
		assertTrue(ExpenditureCategory.WASTED_B.isWastedBOrC());
		assertTrue(ExpenditureCategory.WASTED_C.isWastedBOrC());
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		ExpenditureCategory cat1 = ExpenditureCategory.from("1");
		ExpenditureCategory cat2 = ExpenditureCategory.from("1");
		ExpenditureCategory cat3 = ExpenditureCategory.from("2");
		assertEquals(cat1, cat2);
		assertNotEquals(cat1, cat3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		ExpenditureCategory cat1 = ExpenditureCategory.from("1");
		ExpenditureCategory cat2 = ExpenditureCategory.from("1");
		assertEquals(cat1.hashCode(), cat2.hashCode());
	}

	@Test
	@DisplayName("正常系：toStringは値の文字列表現を返す")
	void testToString() {
		assertEquals("1", ExpenditureCategory.from("1").toString());
		assertEquals("2", ExpenditureCategory.from("2").toString());
	}
}
