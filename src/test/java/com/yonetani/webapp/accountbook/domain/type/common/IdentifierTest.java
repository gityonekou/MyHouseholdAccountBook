/**
 * Identifier抽象クラスのテストクラスです。
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
 * Identifier抽象クラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@DisplayName("ID（識別子）基底クラス(Identifier)のテスト")
class IdentifierTest {
	
	// テスト用の具象クラス
	private static class TestIdentifier extends Identifier {
		private TestIdentifier(String value) {
			super(value);
		}

		public static TestIdentifier from(String value) {
			validate(value, "テストID");
			return new TestIdentifier(value);
		}
	}
	
	@Test
	@DisplayName("正常系：IDから生成")
	void testFrom_正常系_IDから生成() {
		TestIdentifier id = TestIdentifier.from("test-id-123");
		// 検証
		assertNotNull(id);
		assertEquals("test-id-123", id.getValue());
		assertEquals("test-id-123", id.toString());
	}
	
	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestIdentifier.from(null)
		);
		assertTrue(exception.getMessage().contains("テストID"));
		assertTrue(exception.getMessage().contains("null"));
	}
	
	@Test
	@DisplayName("異常系：空文字列で例外が発生する")
	void testFrom_異常系_空文字列値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestIdentifier.from("")
		);
		assertTrue(exception.getMessage().contains("テストID"));
		assertTrue(exception.getMessage().contains("空文字"));
	}
}
