/**
 * IncomeCategory(収入区分)のテストクラスです。
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

/**
 *<pre>
 * IncomeCategory(収入区分)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@DisplayName("収入区分(IncomeCategory)のテスト")
class IncomeCategoryTest {

	@Test
	@DisplayName("正常系：通常の文字列で生成できる")
	void testFrom_正常系_通常文字列() {
		IncomeCategory category = IncomeCategory.from("1");
		assertNotNull(category);
		assertEquals("1", category.getValue());
		assertEquals("1", category.toString());
	}

	@Test
	@DisplayName("正常系：null値でも生成できる(バリデーションなし)")
	void testFrom_正常系_null値() {
		IncomeCategory category = IncomeCategory.from(null);
		assertNotNull(category);
		assertNull(category.getValue());
	}

	@Test
	@DisplayName("正常系：空文字でも生成できる(バリデーションなし)")
	void testFrom_正常系_空文字() {
		IncomeCategory category = IncomeCategory.from("");
		assertNotNull(category);
		assertEquals("", category.getValue());
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		IncomeCategory cat1 = IncomeCategory.from("1");
		IncomeCategory cat2 = IncomeCategory.from("1");
		IncomeCategory cat3 = IncomeCategory.from("2");
		assertEquals(cat1, cat2);
		assertNotEquals(cat1, cat3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		IncomeCategory cat1 = IncomeCategory.from("1");
		IncomeCategory cat2 = IncomeCategory.from("1");
		assertEquals(cat1.hashCode(), cat2.hashCode());
	}

	@Test
	@DisplayName("正常系：toStringは値の文字列表現を返す")
	void testToString() {
		assertEquals("1", IncomeCategory.from("1").toString());
	}
}
