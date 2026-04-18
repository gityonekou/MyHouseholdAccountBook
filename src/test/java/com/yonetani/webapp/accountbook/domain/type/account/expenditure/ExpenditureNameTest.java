/**
 * ExpenditureName(支出名称)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/03/15 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.expenditure;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * ExpenditureName(支出名称)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@DisplayName("支出名称(ExpenditureName)のテスト")
class ExpenditureNameTest {

	@Test
	@DisplayName("正常系：通常の文字列で生成できる")
	void testFrom_正常系_通常文字列() {
		ExpenditureName name = ExpenditureName.from("食費");
		assertNotNull(name);
		assertEquals("食費", name.getValue());
		assertEquals("食費", name.toString());
	}

	@Test
	@DisplayName("正常系：null値でも生成できる(バリデーションなし)")
	void testFrom_正常系_null値() {
		ExpenditureName name = ExpenditureName.from(null);
		assertNotNull(name);
		assertNull(name.getValue());
	}

	@Test
	@DisplayName("正常系：空文字でも生成できる(バリデーションなし)")
	void testFrom_正常系_空文字() {
		ExpenditureName name = ExpenditureName.from("");
		assertNotNull(name);
		assertEquals("", name.getValue());
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		ExpenditureName name1 = ExpenditureName.from("食費");
		ExpenditureName name2 = ExpenditureName.from("食費");
		ExpenditureName name3 = ExpenditureName.from("交通費");
		assertEquals(name1, name2);
		assertNotEquals(name1, name3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		ExpenditureName name1 = ExpenditureName.from("食費");
		ExpenditureName name2 = ExpenditureName.from("食費");
		assertEquals(name1.hashCode(), name2.hashCode());
	}

	@Test
	@DisplayName("正常系：toStringは値の文字列表現を返す")
	void testToString() {
		assertEquals("食費", ExpenditureName.from("食費").toString());
	}
}
