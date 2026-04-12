/**
 * NullableIdentifier抽象クラスのテストクラスです。
 * テスト対象の抽象クラスをテストするため、テスト用の具象クラスを使用します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/04/12 : 1.00.00     新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * NullableIdentifier抽象クラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@DisplayName("null値許容ID（識別子）基底クラス(NullableIdentifier)のテスト")
class NullableIdentifierTest {
	
	// テスト用の具象クラス
	private static class TestNullableIdentifier extends NullableIdentifier {
		private TestNullableIdentifier(String value) {
			super(value);
		}

		public static TestNullableIdentifier from(String value) {
			validate(value, "テストnull許容ID");
			return new TestNullableIdentifier(value);
		}
	}
	
	@Test
	@DisplayName("正常系：IDから生成")
	void testFrom_正常系_IDから生成() {
		TestNullableIdentifier id = TestNullableIdentifier.from("test-id-123");
		// 検証
		assertNotNull(id);
		assertEquals("test-id-123", id.getValue());
		assertEquals("test-id-123", id.toString());
	}
	
	@Test
	@DisplayName("正常系：nullから生成")
	void testFrom_正常系_nullから生成() {
		TestNullableIdentifier id = TestNullableIdentifier.from(null);
		// 検証
		assertNotNull(id);
		assertEquals(null, id.getValue());
		assertEquals("", id.toString());
	}
	
	@Test
	@DisplayName("異常系：空文字列で例外が発生する")
	void testFrom_異常系_空文字列値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestNullableIdentifier.from("")
		);
		assertTrue(exception.getMessage().contains("テストnull許容ID"));
		assertTrue(exception.getMessage().contains("空文字"));
	}
}
