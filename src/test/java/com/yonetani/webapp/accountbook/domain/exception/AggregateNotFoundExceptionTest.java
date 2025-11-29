/**
 * AggregateNotFoundExceptionのテストクラスです。
 *
 */
package com.yonetani.webapp.accountbook.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * AggregateNotFoundExceptionのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
class AggregateNotFoundExceptionTest {

	@Test
	@DisplayName("正常系：メッセージのみで生成")
	void testConstructor_MessageOnly() {
		// 実行
		String message = "集約が見つかりません";
		AggregateNotFoundException exception = new AggregateNotFoundException(message);

		// 検証
		assertNotNull(exception);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}

	@Test
	@DisplayName("正常系：メッセージと原因例外で生成")
	void testConstructor_MessageAndCause() {
		// 準備
		String message = "集約が見つかりません";
		Exception cause = new RuntimeException("データベースエラー");

		// 実行
		AggregateNotFoundException exception = new AggregateNotFoundException(message, cause);

		// 検証
		assertNotNull(exception);
		assertEquals(message, exception.getMessage());
		assertEquals(cause, exception.getCause());
	}

	@Test
	@DisplayName("正常系：DomainExceptionを継承している")
	void testInheritance() {
		// 実行
		AggregateNotFoundException exception = new AggregateNotFoundException("テスト");

		// 検証
		assertTrue(exception instanceof DomainException);
	}

	@Test
	@DisplayName("正常系：例外をスローできる")
	void testThrowException() {
		// 実行 & 検証
		AggregateNotFoundException exception = assertThrows(
			AggregateNotFoundException.class,
			() -> {
				throw new AggregateNotFoundException("指定された収支が見つかりません");
			}
		);
		assertEquals("指定された収支が見つかりません", exception.getMessage());
	}

	@Test
	@DisplayName("正常系：of()で集約型とIDを指定して生成")
	void testOf() {
		// 実行
		AggregateNotFoundException exception =
			AggregateNotFoundException.of("収支", "user001_202511");

		// 検証
		assertNotNull(exception);
		assertTrue(exception.getMessage().contains("収支"));
		assertTrue(exception.getMessage().contains("user001_202511"));
		assertTrue(exception.getMessage().contains("見つかりません"));
	}

	@Test
	@DisplayName("正常系：of()で異なる集約型とIDを指定して生成")
	void testOf_DifferentAggregate() {
		// 実行
		AggregateNotFoundException exception =
			AggregateNotFoundException.of("買い物", "shopping123");

		// 検証
		assertNotNull(exception);
		assertTrue(exception.getMessage().contains("買い物"));
		assertTrue(exception.getMessage().contains("shopping123"));
	}

	@Test
	@DisplayName("正常系：リポジトリでの使用例")
	void testRepositoryUsage() {
		// 実行 & 検証
		String aggregateId = "user001_202511";
		AggregateNotFoundException exception = assertThrows(
			AggregateNotFoundException.class,
			() -> {
				// リポジトリから集約を取得（存在しない想定）
				boolean found = false;
				if (!found) {
					throw AggregateNotFoundException.of("収支", aggregateId);
				}
			}
		);
		assertTrue(exception.getMessage().contains("収支"));
		assertTrue(exception.getMessage().contains(aggregateId));
	}
}
