/**
 * ExpenditureAmountInconsistencyExceptionのテストクラスです。
 *
 */
package com.yonetani.webapp.accountbook.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * ExpenditureAmountInconsistencyExceptionのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
class ExpenditureAmountInconsistencyExceptionTest {

	@Test
	@DisplayName("正常系：メッセージのみで生成")
	void testConstructor_MessageOnly() {
		// 実行
		String message = "支出金額の整合性が取れていません";
		ExpenditureAmountInconsistencyException exception =
			new ExpenditureAmountInconsistencyException(message);

		// 検証
		assertNotNull(exception);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}

	@Test
	@DisplayName("正常系：メッセージと原因例外で生成")
	void testConstructor_MessageAndCause() {
		// 準備
		String message = "支出金額の整合性が取れていません";
		Exception cause = new ArithmeticException("計算エラー");

		// 実行
		ExpenditureAmountInconsistencyException exception =
			new ExpenditureAmountInconsistencyException(message, cause);

		// 検証
		assertNotNull(exception);
		assertEquals(message, exception.getMessage());
		assertEquals(cause, exception.getCause());
	}

	@Test
	@DisplayName("正常系：DomainExceptionを継承している")
	void testInheritance() {
		// 実行
		ExpenditureAmountInconsistencyException exception =
			new ExpenditureAmountInconsistencyException("テスト");

		// 検証
		assertTrue(exception instanceof DomainException);
	}

	@Test
	@DisplayName("正常系：例外をスローできる")
	void testThrowException() {
		// 実行 & 検証
		ExpenditureAmountInconsistencyException exception = assertThrows(
			ExpenditureAmountInconsistencyException.class,
			() -> {
				throw new ExpenditureAmountInconsistencyException(
					"収支テーブルの支出金額 ≠ 支出テーブルの合計金額");
			}
		);
		assertEquals("収支テーブルの支出金額 ≠ 支出テーブルの合計金額", exception.getMessage());
	}

	@Test
	@DisplayName("正常系：支出金額の整合性違反を表現")
	void testExpenditureAmountInconsistency() {
		// 準備
		int incomeAndExpenditureTableExpenditure = 280000;  // 収支テーブルの支出金額
		int expenditureTableSum = 320000;  // 支出テーブルの合計金額（不整合）

		// 実行 & 検証
		ExpenditureAmountInconsistencyException exception = assertThrows(
			ExpenditureAmountInconsistencyException.class,
			() -> {
				if (expenditureTableSum != incomeAndExpenditureTableExpenditure) {
					throw new ExpenditureAmountInconsistencyException(
						String.format("支出金額が一致しません。yearMonth=%s, expected=%d, actual=%d",
							"202511", incomeAndExpenditureTableExpenditure, expenditureTableSum));
				}
			}
		);
		assertTrue(exception.getMessage().contains("支出金額が一致しません"));
		assertTrue(exception.getMessage().contains("202511"));
		assertTrue(exception.getMessage().contains("280000"));  // expected
		assertTrue(exception.getMessage().contains("320000"));  // actual
	}
}
