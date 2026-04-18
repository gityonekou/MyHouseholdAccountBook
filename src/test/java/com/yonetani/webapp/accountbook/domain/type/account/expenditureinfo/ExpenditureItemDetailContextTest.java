/**
 * ExpenditureItemDetailContext(支出項目詳細内容)のテストクラスです。
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
 * ExpenditureItemDetailContext(支出項目詳細内容)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@DisplayName("支出項目詳細内容(ExpenditureItemDetailContext)のテスト")
class ExpenditureItemDetailContextTest {

	@Test
	@DisplayName("正常系：通常の文字列で生成できる")
	void testFrom_正常系_通常文字列() {
		ExpenditureItemDetailContext context = ExpenditureItemDetailContext.from("項目詳細内容");
		assertNotNull(context);
		assertEquals("項目詳細内容", context.getValue());
		assertEquals("項目詳細内容", context.toString());
	}

	@Test
	@DisplayName("正常系：null値でも生成できる(バリデーションなし)")
	void testFrom_正常系_null値() {
		ExpenditureItemDetailContext context = ExpenditureItemDetailContext.from(null);
		assertNotNull(context);
		assertNull(context.getValue());
	}

	@Test
	@DisplayName("正常系：空文字でも生成できる(バリデーションなし)")
	void testFrom_正常系_空文字() {
		ExpenditureItemDetailContext context = ExpenditureItemDetailContext.from("");
		assertNotNull(context);
		assertEquals("", context.getValue());
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		ExpenditureItemDetailContext ctx1 = ExpenditureItemDetailContext.from("詳細内容");
		ExpenditureItemDetailContext ctx2 = ExpenditureItemDetailContext.from("詳細内容");
		ExpenditureItemDetailContext ctx3 = ExpenditureItemDetailContext.from("他の詳細内容");
		assertEquals(ctx1, ctx2);
		assertNotEquals(ctx1, ctx3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		ExpenditureItemDetailContext ctx1 = ExpenditureItemDetailContext.from("詳細内容");
		ExpenditureItemDetailContext ctx2 = ExpenditureItemDetailContext.from("詳細内容");
		assertEquals(ctx1.hashCode(), ctx2.hashCode());
	}

	@Test
	@DisplayName("正常系：toStringは値の文字列表現を返す")
	void testToString() {
		assertEquals("項目詳細内容", ExpenditureItemDetailContext.from("項目詳細内容").toString());
	}
}
