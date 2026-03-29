/**
 * IncomeDetailContext(収入詳細)のテストクラスです。
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
 * IncomeDetailContext(収入詳細)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@DisplayName("収入詳細(IncomeDetailContext)のテスト")
class IncomeDetailContextTest {

	@Test
	@DisplayName("正常系：通常の文字列で生成できる")
	void testFrom_正常系_通常文字列() {
		IncomeDetailContext context = IncomeDetailContext.from("収入詳細情報");
		assertNotNull(context);
		assertEquals("収入詳細情報", context.getValue());
		assertEquals("収入詳細情報", context.toString());
	}

	@Test
	@DisplayName("正常系：null値でも生成できる(バリデーションなし)")
	void testFrom_正常系_null値() {
		IncomeDetailContext context = IncomeDetailContext.from(null);
		assertNotNull(context);
		assertNull(context.getValue());
	}

	@Test
	@DisplayName("正常系：空文字でも生成できる(バリデーションなし)")
	void testFrom_正常系_空文字() {
		IncomeDetailContext context = IncomeDetailContext.from("");
		assertNotNull(context);
		assertEquals("", context.getValue());
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		IncomeDetailContext ctx1 = IncomeDetailContext.from("詳細");
		IncomeDetailContext ctx2 = IncomeDetailContext.from("詳細");
		IncomeDetailContext ctx3 = IncomeDetailContext.from("他の詳細");
		assertEquals(ctx1, ctx2);
		assertNotEquals(ctx1, ctx3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		IncomeDetailContext ctx1 = IncomeDetailContext.from("詳細");
		IncomeDetailContext ctx2 = IncomeDetailContext.from("詳細");
		assertEquals(ctx1.hashCode(), ctx2.hashCode());
	}

	@Test
	@DisplayName("正常系：toStringは値の文字列表現を返す")
	void testToString() {
		assertEquals("収入詳細情報", IncomeDetailContext.from("収入詳細情報").toString());
	}
}
