/**
 * ExpenditureDetailContext(支出詳細)のテストクラスです。
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
 * ExpenditureDetailContext(支出詳細)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@DisplayName("支出詳細(ExpenditureDetailContext)のテスト")
class ExpenditureDetailContextTest {

	@Test
	@DisplayName("正常系：通常の文字列で生成できる")
	void testFrom_正常系_通常文字列() {
		ExpenditureDetailContext context = ExpenditureDetailContext.from("詳細情報");
		assertNotNull(context);
		assertEquals("詳細情報", context.getValue());
		assertEquals("詳細情報", context.toString());
	}

	@Test
	@DisplayName("正常系：null値でも生成できる(バリデーションなし)")
	void testFrom_正常系_null値() {
		ExpenditureDetailContext context = ExpenditureDetailContext.from(null);
		assertNotNull(context);
		assertNull(context.getValue());
	}

	@Test
	@DisplayName("正常系：空文字でも生成できる(バリデーションなし)")
	void testFrom_正常系_空文字() {
		ExpenditureDetailContext context = ExpenditureDetailContext.from("");
		assertNotNull(context);
		assertEquals("", context.getValue());
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		ExpenditureDetailContext ctx1 = ExpenditureDetailContext.from("詳細");
		ExpenditureDetailContext ctx2 = ExpenditureDetailContext.from("詳細");
		ExpenditureDetailContext ctx3 = ExpenditureDetailContext.from("他の詳細");
		assertEquals(ctx1, ctx2);
		assertNotEquals(ctx1, ctx3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		ExpenditureDetailContext ctx1 = ExpenditureDetailContext.from("詳細");
		ExpenditureDetailContext ctx2 = ExpenditureDetailContext.from("詳細");
		assertEquals(ctx1.hashCode(), ctx2.hashCode());
	}

	@Test
	@DisplayName("正常系：toStringは値の文字列表現を返す")
	void testToString() {
		assertEquals("詳細情報", ExpenditureDetailContext.from("詳細情報").toString());
	}
}
