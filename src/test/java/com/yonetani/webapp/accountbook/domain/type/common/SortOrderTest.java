/**
 * SortOrder抽象クラスのテストクラスです。
 * テスト対象の抽象クラスをテストするため、テスト用の具象クラスを使用します。
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

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * SortOrder抽象クラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("表示順基底クラス(SortOrder)のテスト")
class SortOrderTest {

	// テスト用の具象クラス(3桁の表示順を想定)
	private static class TestSortOrder extends SortOrder {
		private TestSortOrder(String value) {
			super(value);
		}

		public static TestSortOrder from(String value) {
			validate(value, "テスト表示順");
			if(value.length() != 3) {
				throw new MyHouseholdAccountBookRuntimeException("「テスト表示順」項目の設定値が不正です。管理者に問い合わせてください。[value=" + value + "]");
			}
			try {
				Integer.parseInt(value);
			} catch(NumberFormatException ex) {
				throw new MyHouseholdAccountBookRuntimeException("「テスト表示順」項目の設定値が不正です。管理者に問い合わせてください。[value=" + value + "]");
			}
			return new TestSortOrder(value);
		}
	}

	@Test
	@DisplayName("正常系：表示順から生成")
	void testFrom_正常系_表示順から生成() {
		TestSortOrder sortOrder = TestSortOrder.from("001");
		// 検証
		assertNotNull(sortOrder);
		assertEquals("001", sortOrder.getValue());
		assertEquals("001", sortOrder.toString());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestSortOrder.from(null)
		);
		assertTrue(exception.getMessage().contains("テスト表示順"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("異常系：空文字列で例外が発生する")
	void testFrom_異常系_空文字列値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestSortOrder.from("")
		);
		assertTrue(exception.getMessage().contains("テスト表示順"));
		assertTrue(exception.getMessage().contains("未設定"));
	}

	@Test
	@DisplayName("異常系：サブクラス独自の桁数チェックで例外が発生する")
	void testFrom_異常系_桁数不正() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TestSortOrder.from("1")
		);
		assertTrue(exception.getMessage().contains("テスト表示順"));
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		TestSortOrder sortOrder1 = TestSortOrder.from("001");
		TestSortOrder sortOrder2 = TestSortOrder.from("001");
		// 検証
		assertEquals(sortOrder1, sortOrder2);
		assertEquals(sortOrder1.hashCode(), sortOrder2.hashCode());
	}
}
