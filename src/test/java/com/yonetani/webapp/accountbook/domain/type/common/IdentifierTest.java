/**
 * Identifier抽象クラスのテストクラスです。
 * テスト対象の抽象クラスをテストするため、テスト用の具象クラスを使用します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/04/12 : 1.00.00  feature-1.00-dev00  新規作成
 * 2026/09/23 : 1.01.00  feature-1.03-dev1   追加リファクタリング対応(桁数チェックを行うvalidateメソッドを追加)
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
	
	// テスト用の具象クラス(桁数チェックなし)
	private static class TestIdentifier extends Identifier {
		private TestIdentifier(String value) {
			super(value);
		}

		public static TestIdentifier from(String value) {
			validate(value, "テストID");
			return new TestIdentifier(value);
		}
	}
	
	// テスト用の具象クラス(桁数チェックあり)
	private static class TestCheckLengthIdentifier extends Identifier {
		private TestCheckLengthIdentifier(String value) {
			super(value);
		}

		public static TestCheckLengthIdentifier from(String value) {
			validate(value, 3, "テストID");
			return new TestCheckLengthIdentifier(value);
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
	@DisplayName("正常系：IDから生成(桁数チェックあり)")
	void testFrom_正常系_IDから生成_LengthCheck() {
		TestCheckLengthIdentifier id = TestCheckLengthIdentifier.from("123");
		// 検証
		assertNotNull(id);
		assertEquals("123", id.getValue());
		assertEquals("123", id.toString());
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
	
	@Test
	@DisplayName("異常系：桁数不正で例外が発生する_low")
	void testFrom_異常系_桁数不正値_low() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestCheckLengthIdentifier.from("12")
		);
		assertTrue(exception.getMessage().contains("テストID"));
		assertTrue(exception.getMessage().contains("桁数"));
		assertTrue(exception.getMessage().contains("length=2"));
	}
	
	@Test
	@DisplayName("異常系：桁数不正で例外が発生する_high")
	void testFrom_異常系_桁数不正値_high() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestCheckLengthIdentifier.from("1234")
		);
		assertTrue(exception.getMessage().contains("テストID"));
		assertTrue(exception.getMessage().contains("桁数"));
		assertTrue(exception.getMessage().contains("length=4"));
	}
}
