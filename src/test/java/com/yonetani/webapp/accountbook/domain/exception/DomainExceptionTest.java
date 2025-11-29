/**
 * DomainExceptionのテストクラスです。
 *
 */
package com.yonetani.webapp.accountbook.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * DomainExceptionのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
class DomainExceptionTest {

	@Test
	@DisplayName("正常系：メッセージのみで生成")
	void testConstructor_MessageOnly() {
		// 実行
		String message = "ドメインエラーが発生しました";
		DomainException exception = new DomainException(message);

		// 検証
		assertNotNull(exception);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}

	@Test
	@DisplayName("正常系：メッセージと原因例外で生成")
	void testConstructor_MessageAndCause() {
		// 準備
		String message = "ドメインエラーが発生しました";
		Exception cause = new RuntimeException("原因例外");

		// 実行
		DomainException exception = new DomainException(message, cause);

		// 検証
		assertNotNull(exception);
		assertEquals(message, exception.getMessage());
		assertEquals(cause, exception.getCause());
	}

	@Test
	@DisplayName("正常系：原因例外のみで生成")
	void testConstructor_CauseOnly() {
		// 準備
		Exception cause = new RuntimeException("原因例外");

		// 実行
		DomainException exception = new DomainException(cause);

		// 検証
		assertNotNull(exception);
		assertEquals(cause, exception.getCause());
	}

	@Test
	@DisplayName("正常系：MyHouseholdAccountBookRuntimeExceptionを継承している")
	void testInheritance() {
		// 実行
		DomainException exception = new DomainException("テスト");

		// 検証
		assertTrue(exception instanceof MyHouseholdAccountBookRuntimeException);
		assertTrue(exception instanceof RuntimeException);
	}

	@Test
	@DisplayName("正常系：例外をスローできる")
	void testThrowException() {
		// 実行 & 検証
		DomainException exception = assertThrows(
			DomainException.class,
			() -> {
				throw new DomainException("テストエラー");
			}
		);
		assertEquals("テストエラー", exception.getMessage());
	}
}
