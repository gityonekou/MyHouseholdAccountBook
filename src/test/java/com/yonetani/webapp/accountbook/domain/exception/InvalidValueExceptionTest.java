/**
 * InvalidValueExceptionのテストクラスです。
 *
 */
package com.yonetani.webapp.accountbook.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * InvalidValueExceptionのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
class InvalidValueExceptionTest {

	@Test
	@DisplayName("正常系：メッセージのみで生成")
	void testConstructor_MessageOnly() {
		// 実行
		String message = "不正な値が設定されました";
		InvalidValueException exception = new InvalidValueException(message);

		// 検証
		assertNotNull(exception);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}

	@Test
	@DisplayName("正常系：メッセージと原因例外で生成")
	void testConstructor_MessageAndCause() {
		// 準備
		String message = "不正な値が設定されました";
		Exception cause = new NumberFormatException("数値変換エラー");

		// 実行
		InvalidValueException exception = new InvalidValueException(message, cause);

		// 検証
		assertNotNull(exception);
		assertEquals(message, exception.getMessage());
		assertEquals(cause, exception.getCause());
	}

	@Test
	@DisplayName("正常系：DomainExceptionを継承している")
	void testInheritance() {
		// 実行
		InvalidValueException exception = new InvalidValueException("テスト");

		// 検証
		assertTrue(exception instanceof DomainException);
	}

	@Test
	@DisplayName("正常系：例外をスローできる")
	void testThrowException() {
		// 実行 & 検証
		InvalidValueException exception = assertThrows(
			InvalidValueException.class,
			() -> {
				throw new InvalidValueException("金額が負の値です");
			}
		);
		assertEquals("金額が負の値です", exception.getMessage());
	}

	@Test
	@DisplayName("正常系：値オブジェクトのバリデーションエラーを表現")
	void testValidationError() {
		// 実行 & 検証
		InvalidValueException exception = assertThrows(
			InvalidValueException.class,
			() -> {
				String value = "";
				if (value.isEmpty()) {
					throw new InvalidValueException("IDは必須です");
				}
			}
		);
		assertTrue(exception.getMessage().contains("IDは必須です"));
	}
}
