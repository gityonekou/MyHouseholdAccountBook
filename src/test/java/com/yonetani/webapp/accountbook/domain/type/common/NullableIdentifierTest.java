/**
 * NullableIdentifier抽象クラスのテストクラスです。
 * テスト対象の抽象クラスをテストするため、テスト用の具象クラスを使用します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/04/12 : 1.00.00  feature-1.00-dev00  新規作成
 * 2026/09/23 : 1.01.00  feature-1.03-dev1   追加リファクタリング対応(桁数チェック追加と空文字列許容に変更)
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
			validate(value, 3, "テストnull許容ID");
			return new TestNullableIdentifier(value);
		}
	}
	
	@Test
	@DisplayName("正常系：IDから生成(桁数チェックあり)")
	void testFrom_正常系_IDから生成_LengthCheck() {
		TestNullableIdentifier id = TestNullableIdentifier.from("123");
		// 検証
		assertNotNull(id);
		assertEquals("123", id.getValue());
		assertEquals("123", id.toString());
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
	@DisplayName("異常系：空文字列から生成")
	void testFrom_異常系_空文字列値() {
		TestNullableIdentifier id = TestNullableIdentifier.from("");
		// 検証
		assertNotNull(id);
		assertEquals(null, id.getValue());
		assertEquals("", id.toString());;
	}
	
	@Test
	@DisplayName("異常系：桁数不正で例外が発生する_low")
	void testFrom_異常系_桁数不正値_low() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestNullableIdentifier.from("12")
		);
		assertTrue(exception.getMessage().contains("テストnull許容ID"));
		assertTrue(exception.getMessage().contains("桁数"));
		assertTrue(exception.getMessage().contains("length=2"));
	}
	
	@Test
	@DisplayName("異常系：桁数不正で例外が発生する_high")
	void testFrom_異常系_桁数不正値_high() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestNullableIdentifier.from("1234")
		);
		assertTrue(exception.getMessage().contains("テストnull許容ID"));
		assertTrue(exception.getMessage().contains("桁数"));
		assertTrue(exception.getMessage().contains("length=4"));
	}
	
	@Test
	@DisplayName("正常系：isNull")
	void testIsNull() {
		// 検証
		assertTrue(TestNullableIdentifier.from(null).isNull());
		assertTrue(TestNullableIdentifier.from("").isNull());
		assertFalse(TestNullableIdentifier.from("123").isNull());
	}
	
	@Test
	@DisplayName("正常系：toString")
	void testToString() {
		// 準備
		TestNullableIdentifier data1 = TestNullableIdentifier.from(null);
		TestNullableIdentifier data2 = TestNullableIdentifier.from("");
		TestNullableIdentifier data3 = TestNullableIdentifier.from("123");

		// 検証
		assertEquals("", data1.toString()); // null値は空文字列
		assertEquals("", data2.toString());
		assertEquals("123", data3.toString());
	}
}
