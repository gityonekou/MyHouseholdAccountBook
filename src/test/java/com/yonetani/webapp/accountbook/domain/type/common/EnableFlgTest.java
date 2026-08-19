/**
 * EnableFlgクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/08/18 : 1.00.00     新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * EnableFlgクラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("有効/無効フラグ(EnableFlg)のテスト")
class EnableFlgTest {

	@Test
	@DisplayName("正常系：trueから生成")
	void testFrom_正常系_trueから生成() {
		EnableFlg flg = EnableFlg.from(true);
		// 検証
		assertNotNull(flg);
		assertTrue(flg.getValue());
		assertEquals("true", flg.toString());
	}

	@Test
	@DisplayName("正常系：falseから生成")
	void testFrom_正常系_falseから生成() {
		EnableFlg flg = EnableFlg.from(false);
		// 検証
		assertNotNull(flg);
		assertFalse(flg.getValue());
		assertEquals("false", flg.toString());
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		EnableFlg flg1 = EnableFlg.from(true);
		EnableFlg flg2 = EnableFlg.from(true);
		EnableFlg flg3 = EnableFlg.from(false);
		// 検証
		assertEquals(flg1, flg2);
		assertEquals(flg1.hashCode(), flg2.hashCode());
		assertNotEquals(flg1, flg3);
	}
}
