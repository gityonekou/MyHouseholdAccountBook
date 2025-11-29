/**
 * FixedCostNotApplicableExceptionのテストクラスです。
 *
 */
package com.yonetani.webapp.accountbook.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * FixedCostNotApplicableExceptionのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
class FixedCostNotApplicableExceptionTest {

	@Test
	@DisplayName("正常系：メッセージのみで生成")
	void testConstructor_MessageOnly() {
		// 実行
		String message = "この月には固定費は発生しません";
		FixedCostNotApplicableException exception = new FixedCostNotApplicableException(message);

		// 検証
		assertNotNull(exception);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}

	@Test
	@DisplayName("正常系：メッセージと原因例外で生成")
	void testConstructor_MessageAndCause() {
		// 準備
		String message = "この月には固定費は発生しません";
		Exception cause = new RuntimeException("スケジュールエラー");

		// 実行
		FixedCostNotApplicableException exception =
			new FixedCostNotApplicableException(message, cause);

		// 検証
		assertNotNull(exception);
		assertEquals(message, exception.getMessage());
		assertEquals(cause, exception.getCause());
	}

	@Test
	@DisplayName("正常系：DomainExceptionを継承している")
	void testInheritance() {
		// 実行
		FixedCostNotApplicableException exception =
			new FixedCostNotApplicableException("テスト");

		// 検証
		assertTrue(exception instanceof DomainException);
	}

	@Test
	@DisplayName("正常系：例外をスローできる")
	void testThrowException() {
		// 実行 & 検証
		FixedCostNotApplicableException exception = assertThrows(
			FixedCostNotApplicableException.class,
			() -> {
				throw new FixedCostNotApplicableException("固定費が適用できません");
			}
		);
		assertEquals("固定費が適用できません", exception.getMessage());
	}

	@Test
	@DisplayName("正常系：スケジュール不一致のケース")
	void testScheduleMismatch() {
		// 準備
		String targetMonth = "202511";
		String scheduledMonth = "202512";

		// 実行 & 検証
		FixedCostNotApplicableException exception = assertThrows(
			FixedCostNotApplicableException.class,
			() -> {
				if (!targetMonth.equals(scheduledMonth)) {
					throw new FixedCostNotApplicableException(
						String.format("固定費のスケジュールに合致しません。[対象月=%s, スケジュール=%s]",
							targetMonth, scheduledMonth));
				}
			}
		);
		assertTrue(exception.getMessage().contains("202511"));
		assertTrue(exception.getMessage().contains("202512"));
		assertTrue(exception.getMessage().contains("スケジュールに合致しません"));
	}

	@Test
	@DisplayName("正常系：有効期間外のケース")
	void testOutOfValidPeriod() {
		// 準備
		String targetMonth = "202511";
		String validFrom = "202601";

		// 実行 & 検証
		FixedCostNotApplicableException exception = assertThrows(
			FixedCostNotApplicableException.class,
			() -> {
				if (targetMonth.compareTo(validFrom) < 0) {
					throw new FixedCostNotApplicableException(
						String.format("固定費の有効期間外です。[対象月=%s, 有効期間開始=%s]",
							targetMonth, validFrom));
				}
			}
		);
		assertTrue(exception.getMessage().contains("202511"));
		assertTrue(exception.getMessage().contains("202601"));
		assertTrue(exception.getMessage().contains("有効期間外"));
	}

	@Test
	@DisplayName("正常系：FixedCost集約での使用例")
	void testFixedCostUsage() {
		// 実行 & 検証
		FixedCostNotApplicableException exception = assertThrows(
			FixedCostNotApplicableException.class,
			() -> {
				// 固定費が対象月に適用可能かチェック
				boolean shouldGenerate = false; // スケジュールに合致しない
				if (!shouldGenerate) {
					throw new FixedCostNotApplicableException(
						"この月には固定費は発生しません");
				}
			}
		);
		assertEquals("この月には固定費は発生しません", exception.getMessage());
	}
}
