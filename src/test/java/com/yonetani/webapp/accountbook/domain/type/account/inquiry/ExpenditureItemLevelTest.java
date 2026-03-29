/**
 * ExpenditureItemLevel(支出項目レベル)のテストクラスです。
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
 * ExpenditureItemLevel(支出項目レベル)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@DisplayName("支出項目レベル(ExpenditureItemLevel)のテスト")
class ExpenditureItemLevelTest {

	@Test
	@DisplayName("正常系：数値文字列でレベルを生成できる")
	void testFrom_正常系_レベル1() {
		ExpenditureItemLevel level = ExpenditureItemLevel.from("1");
		assertNotNull(level);
		assertEquals(1, level.getValue());
		assertEquals("1", level.toString());
	}

	@Test
	@DisplayName("正常系：レベル1から5まで生成できる")
	void testFrom_正常系_各レベル() {
		assertEquals(1, ExpenditureItemLevel.from("1").getValue());
		assertEquals(2, ExpenditureItemLevel.from("2").getValue());
		assertEquals(3, ExpenditureItemLevel.from("3").getValue());
		assertEquals(4, ExpenditureItemLevel.from("4").getValue());
		assertEquals(5, ExpenditureItemLevel.from("5").getValue());
	}

	@Test
	@DisplayName("正常系：toStringはint値の文字列表現を返す")
	void testToString() {
		assertEquals("1", ExpenditureItemLevel.from("1").toString());
		assertEquals("5", ExpenditureItemLevel.from("5").toString());
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		ExpenditureItemLevel level1 = ExpenditureItemLevel.from("1");
		ExpenditureItemLevel level2 = ExpenditureItemLevel.from("1");
		ExpenditureItemLevel level3 = ExpenditureItemLevel.from("2");
		assertEquals(level1, level2);
		assertNotEquals(level1, level3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		ExpenditureItemLevel level1 = ExpenditureItemLevel.from("1");
		ExpenditureItemLevel level2 = ExpenditureItemLevel.from("1");
		assertEquals(level1.hashCode(), level2.hashCode());
	}
}
