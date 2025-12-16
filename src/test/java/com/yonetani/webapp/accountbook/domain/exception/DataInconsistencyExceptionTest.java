/**
 * DataInconsistencyExceptionのテストクラスです。
 *
 */
package com.yonetani.webapp.accountbook.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * DataInconsistencyExceptionのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
class DataInconsistencyExceptionTest {

	@Test
	@DisplayName("正常系：メッセージのみで生成")
	void testConstructor_MessageOnly() {
		// 実行
		String message = "データの整合性が取れていません";
		DataInconsistencyException exception =
			new DataInconsistencyException(message);

		// 検証
		assertNotNull(exception);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}

	@Test
	@DisplayName("正常系：メッセージと原因例外で生成")
	void testConstructor_MessageAndCause() {
		// 準備
		String message = "データの整合性が取れていません";
		Exception cause = new IllegalStateException("不正な状態");

		// 実行
		DataInconsistencyException exception =
			new DataInconsistencyException(message, cause);

		// 検証
		assertNotNull(exception);
		assertEquals(message, exception.getMessage());
		assertEquals(cause, exception.getCause());
	}

	@Test
	@DisplayName("正常系：DomainExceptionを継承している")
	void testInheritance() {
		// 実行
		DataInconsistencyException exception =
			new DataInconsistencyException("テスト");

		// 検証
		assertTrue(exception instanceof DomainException);
	}

	@Test
	@DisplayName("正常系：例外をスローできる")
	void testThrowException() {
		// 実行 & 検証
		DataInconsistencyException exception = assertThrows(
			DataInconsistencyException.class,
			() -> {
				throw new DataInconsistencyException(
					"収支データが未登録の状態で支出金額情報が登録済みの状態です");
			}
		);
		assertEquals("収支データが未登録の状態で支出金額情報が登録済みの状態です", exception.getMessage());
	}

	@Test
	@DisplayName("正常系：データ存在の整合性違反を表現")
	void testDataExistenceInconsistency() {
		// 準備
		String yearMonth = "202508";
		boolean incomeAndExpenditureExists = false;  // 収支データなし
		int expenditureItemCount = 3;  // 支出項目データが存在（不整合）

		// 実行 & 検証
		DataInconsistencyException exception = assertThrows(
			DataInconsistencyException.class,
			() -> {
				if (!incomeAndExpenditureExists && expenditureItemCount > 0) {
					throw new DataInconsistencyException(
						String.format(
							"該当月の収支データが未登録の状態で支出金額情報が登録済みの状態です。管理者に問い合わせてください。[yearMonth=%s]",
							yearMonth));
				}
			}
		);
		assertTrue(exception.getMessage().contains("収支データが未登録"));
		assertTrue(exception.getMessage().contains("支出金額情報が登録済み"));
		assertTrue(exception.getMessage().contains("202508"));
	}

	@Test
	@DisplayName("正常系：業務ルール違反のメッセージフォーマット")
	void testBusinessRuleViolationMessage() {
		// 準備
		String yearMonth = "202501";

		// 実行
		DataInconsistencyException exception = new DataInconsistencyException(
			String.format(
				"該当月の収支データが未登録の状態で支出金額情報が登録済みの状態です。管理者に問い合わせてください。[yearMonth=%s]",
				yearMonth)
		);

		// 検証
		assertNotNull(exception.getMessage());
		assertTrue(exception.getMessage().contains("yearMonth=202501"));
		assertTrue(exception.getMessage().contains("管理者に問い合わせてください"));
	}
}
