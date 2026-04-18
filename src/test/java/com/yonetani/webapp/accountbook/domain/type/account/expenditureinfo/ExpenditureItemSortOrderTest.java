/**
 * ExpenditureItemSortOrder(支出項目表示順)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/03/15 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * ExpenditureItemSortOrder(支出項目表示順)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@DisplayName("支出項目表示順(ExpenditureItemSortOrder)のテスト")
class ExpenditureItemSortOrderTest {

	@Test
	@DisplayName("正常系：通常の文字列で生成できる")
	void testFrom_正常系_通常文字列() {
		ExpenditureItemSortOrder sortOrder = ExpenditureItemSortOrder.from("001");
		assertNotNull(sortOrder);
		assertEquals("001", sortOrder.getValue());
		assertEquals("001", sortOrder.toString());
	}

	@Test
	@DisplayName("正常系：null値でも生成できる(バリデーションなし)")
	void testFrom_正常系_null値() {
		ExpenditureItemSortOrder sortOrder = ExpenditureItemSortOrder.from(null);
		assertNotNull(sortOrder);
		assertNull(sortOrder.getValue());
	}

	@Test
	@DisplayName("正常系：空文字でも生成できる(バリデーションなし)")
	void testFrom_正常系_空文字() {
		ExpenditureItemSortOrder sortOrder = ExpenditureItemSortOrder.from("");
		assertNotNull(sortOrder);
		assertEquals("", sortOrder.getValue());
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		ExpenditureItemSortOrder sort1 = ExpenditureItemSortOrder.from("001");
		ExpenditureItemSortOrder sort2 = ExpenditureItemSortOrder.from("001");
		ExpenditureItemSortOrder sort3 = ExpenditureItemSortOrder.from("002");
		assertEquals(sort1, sort2);
		assertNotEquals(sort1, sort3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		ExpenditureItemSortOrder sort1 = ExpenditureItemSortOrder.from("001");
		ExpenditureItemSortOrder sort2 = ExpenditureItemSortOrder.from("001");
		assertEquals(sort1.hashCode(), sort2.hashCode());
	}

	@Test
	@DisplayName("正常系：toStringは値の文字列表現を返す")
	void testToString() {
		assertEquals("001", ExpenditureItemSortOrder.from("001").toString());
	}
}
