/**
 * IncomeAndExpenditureInconsistencyExceptionのテストクラスです。
 *
 */
package com.yonetani.webapp.accountbook.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * IncomeAndExpenditureInconsistencyExceptionのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
class IncomeAndExpenditureInconsistencyExceptionTest {

	@Test
	@DisplayName("正常系：メッセージのみで生成")
	void testConstructor_MessageOnly() {
		// 実行
		String message = "収支の整合性が取れていません";
		IncomeAndExpenditureInconsistencyException exception =
			new IncomeAndExpenditureInconsistencyException(message);

		// 検証
		assertNotNull(exception);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}

	@Test
	@DisplayName("正常系：メッセージと原因例外で生成")
	void testConstructor_MessageAndCause() {
		// 準備
		String message = "収支の整合性が取れていません";
		Exception cause = new ArithmeticException("計算エラー");

		// 実行
		IncomeAndExpenditureInconsistencyException exception =
			new IncomeAndExpenditureInconsistencyException(message, cause);

		// 検証
		assertNotNull(exception);
		assertEquals(message, exception.getMessage());
		assertEquals(cause, exception.getCause());
	}

	@Test
	@DisplayName("正常系：DomainExceptionを継承している")
	void testInheritance() {
		// 実行
		IncomeAndExpenditureInconsistencyException exception =
			new IncomeAndExpenditureInconsistencyException("テスト");

		// 検証
		assertTrue(exception instanceof DomainException);
	}

	@Test
	@DisplayName("正常系：例外をスローできる")
	void testThrowException() {
		// 実行 & 検証
		IncomeAndExpenditureInconsistencyException exception = assertThrows(
			IncomeAndExpenditureInconsistencyException.class,
			() -> {
				throw new IncomeAndExpenditureInconsistencyException(
					"収支 ≠ 収入合計 - 支出合計");
			}
		);
		assertEquals("収支 ≠ 収入合計 - 支出合計", exception.getMessage());
	}

	@Test
	@DisplayName("正常系：不変条件違反を表現")
	void testInvariantViolation() {
		// 準備
		int income = 100000;
		int expenditure = 80000;
		int balance = 25000; // 不正な収支（正しくは20000）

		// 実行 & 検証
		IncomeAndExpenditureInconsistencyException exception = assertThrows(
			IncomeAndExpenditureInconsistencyException.class,
			() -> {
				int expected = income - expenditure;
				if (balance != expected) {
					throw new IncomeAndExpenditureInconsistencyException(
						String.format("収支の不整合を検出しました。[収入=%d, 支出=%d, 収支=%d, 期待値=%d]",
							income, expenditure, balance, expected));
				}
			}
		);
		assertTrue(exception.getMessage().contains("収支の不整合を検出しました"));
		assertTrue(exception.getMessage().contains("100000"));
		assertTrue(exception.getMessage().contains("80000"));
		assertTrue(exception.getMessage().contains("25000"));
		assertTrue(exception.getMessage().contains("20000"));
	}
}
