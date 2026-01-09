/**
 * IncomeAmountInconsistencyExceptionのテストクラスです。
 *
 */
package com.yonetani.webapp.accountbook.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * IncomeAmountInconsistencyExceptionのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
class IncomeAmountInconsistencyExceptionTest {

	@Test
	@DisplayName("正常系：メッセージのみで生成")
	void testConstructor_MessageOnly() {
		// 実行
		String message = "収入金額の整合性が取れていません";
		IncomeAmountInconsistencyException exception =
			new IncomeAmountInconsistencyException(message);

		// 検証
		assertNotNull(exception);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}

	@Test
	@DisplayName("正常系：メッセージと原因例外で生成")
	void testConstructor_MessageAndCause() {
		// 準備
		String message = "収入金額の整合性が取れていません";
		Exception cause = new ArithmeticException("計算エラー");

		// 実行
		IncomeAmountInconsistencyException exception =
			new IncomeAmountInconsistencyException(message, cause);

		// 検証
		assertNotNull(exception);
		assertEquals(message, exception.getMessage());
		assertEquals(cause, exception.getCause());
	}

	@Test
	@DisplayName("正常系：DomainExceptionを継承している")
	void testInheritance() {
		// 実行
		IncomeAmountInconsistencyException exception =
			new IncomeAmountInconsistencyException("テスト");

		// 検証
		assertTrue(exception instanceof DomainException);
	}

	@Test
	@DisplayName("正常系：例外をスローできる")
	void testThrowException() {
		// 実行 & 検証
		IncomeAmountInconsistencyException exception = assertThrows(
			IncomeAmountInconsistencyException.class,
			() -> {
				throw new IncomeAmountInconsistencyException(
					"収支テーブルの収入金額 ≠ 収入テーブルの合計金額");
			}
		);
		assertEquals("収支テーブルの収入金額 ≠ 収入テーブルの合計金額", exception.getMessage());
	}

	@Test
	@DisplayName("正常系：収入金額の整合性違反を表現")
	void testIncomeAmountInconsistency() {
		// 準備
		int incomeAndExpenditureTableIncome = 350000;  // 収支テーブルの収入金額
		int incomeAndExpenditureTableWithdrew = 50000;  // 収支テーブルの積立取崩金額
		int incomeTableSum = 420000;  // 収入テーブルの合計金額（不整合）

		// 実行 & 検証
		IncomeAmountInconsistencyException exception = assertThrows(
			IncomeAmountInconsistencyException.class,
			() -> {
				int expected = incomeAndExpenditureTableIncome + incomeAndExpenditureTableWithdrew;
				if (incomeTableSum != expected) {
					throw new IncomeAmountInconsistencyException(
						String.format("収入金額が一致しません。yearMonth=%s, expected=%d, actual=%d",
							"202511", expected, incomeTableSum));
				}
			}
		);
		assertTrue(exception.getMessage().contains("収入金額が一致しません"));
		assertTrue(exception.getMessage().contains("202511"));
		assertTrue(exception.getMessage().contains("400000"));  // expected = 350000 + 50000
		assertTrue(exception.getMessage().contains("420000"));  // actual
	}
}
