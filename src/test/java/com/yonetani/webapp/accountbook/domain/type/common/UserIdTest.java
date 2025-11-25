/**
 * UserId(ユーザID)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/25 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * UserId(ユーザID)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("ユーザID(UserId)のテスト")
class UserIdTest {

	@Test
	@DisplayName("正常系：有効なユーザIDで生成できる")
	void testFrom_正常系_有効なユーザID() {
		// 実行
		UserId userId = UserId.from("user001");

		// 検証
		assertNotNull(userId);
		assertEquals("user001", userId.getValue());
		assertEquals("user001", userId.toString());
	}

	@Test
	@DisplayName("正常系：50文字のユーザIDで生成できる")
	void testFrom_正常系_50文字() {
		// 準備（50文字のユーザID）
		String fiftyChars = "12345678901234567890123456789012345678901234567890";

		// 実行
		UserId userId = UserId.from(fiftyChars);

		// 検証
		assertNotNull(userId);
		assertEquals(fiftyChars, userId.getValue());
		assertEquals(50, userId.getValue().length());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> UserId.from(null)
		);
		assertTrue(exception.getMessage().contains("ユーザID"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("異常系：空文字で例外が発生する")
	void testFrom_異常系_空文字() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> UserId.from("")
		);
		assertTrue(exception.getMessage().contains("ユーザID"));
		assertTrue(exception.getMessage().contains("空文字"));
	}

	@Test
	@DisplayName("異常系：51文字以上で例外が発生する")
	void testFrom_異常系_51文字以上() {
		// 準備（51文字のユーザID）
		String fiftyOneChars = "123456789012345678901234567890123456789012345678901";

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> UserId.from(fiftyOneChars)
		);
		assertTrue(exception.getMessage().contains("ユーザID"));
		assertTrue(exception.getMessage().contains("不正"));
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		// 準備
		UserId userId1 = UserId.from("user001");
		UserId userId2 = UserId.from("user001");
		UserId userId3 = UserId.from("user002");

		// 検証
		assertEquals(userId1, userId2);
		assertNotEquals(userId1, userId3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		// 準備
		UserId userId1 = UserId.from("user001");
		UserId userId2 = UserId.from("user001");

		// 検証（同じ値なら同じハッシュコード）
		assertEquals(userId1.hashCode(), userId2.hashCode());
	}

	@Test
	@DisplayName("正常系：toStringは値の文字列表現を返す")
	void testToString() {
		// 準備
		UserId userId = UserId.from("user001");

		// 検証
		assertEquals("user001", userId.toString());
	}
}
