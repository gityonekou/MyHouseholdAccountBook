/**
 * ドメイン層のテスト用アサーションヘルパークラスです。
 * 値オブジェクトの検証を簡潔に記述するためのユーティリティメソッドを提供します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.yonetani.webapp.accountbook.domain.type.common.DateValue;
import com.yonetani.webapp.accountbook.domain.type.common.NullableDateValue;
import com.yonetani.webapp.accountbook.domain.type.common.Identifier;
import com.yonetani.webapp.accountbook.domain.type.common.Money;

/**
 *<pre>
 * ドメイン層のテスト用アサーションヘルパークラスです。
 *
 * [責務]
 * ・値オブジェクトの検証を簡潔に記述
 * ・テストコードの可読性向上
 * ・共通的な検証ロジックの集約
 *
 * [使用例]
 * <code>
 * // 金額の検証
 * DomainAssertions.assertMoneyEquals(100000, incomeAmount);
 *
 * // 日付の検証
 * DomainAssertions.assertDateEquals(2025, 11, 29, paymentDate);
 *
 * // IDの検証
 * DomainAssertions.assertIdEquals("user001", userId);
 * </code>
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
public class DomainAssertions {

	/**
	 * コンストラクタをprivateにしてインスタンス化を防止
	 */
	private DomainAssertions() {
		throw new AssertionError("ユーティリティクラスはインスタンス化できません");
	}

	// ========================================
	// 金額系値オブジェクトのアサーション
	// ========================================

	/**
	 *<pre>
	 * Money値オブジェクトの金額が期待値と等しいことを検証します。
	 *</pre>
	 * @param expectedAmount 期待する金額（整数値）
	 * @param actual 検証対象のMoney
	 */
	public static void assertMoneyEquals(long expectedAmount, Money actual) {
		assertNotNull(actual, "Money値オブジェクトがnullです");
		BigDecimal expected = new BigDecimal(expectedAmount).setScale(2);
		assertEquals(expected, actual.getValue(),
			String.format("金額が一致しません。期待値=%s, 実際=%s",
				expected, actual.getValue()));
	}

	/**
	 *<pre>
	 * Money値オブジェクトの金額が期待値と等しいことを検証します（BigDecimal版）。
	 *</pre>
	 * @param expectedAmount 期待する金額（BigDecimal）
	 * @param actual 検証対象のMoney
	 */
	public static void assertMoneyEquals(BigDecimal expectedAmount, Money actual) {
		assertNotNull(actual, "Money値オブジェクトがnullです");
		assertEquals(expectedAmount, actual.getValue(),
			String.format("金額が一致しません。期待値=%s, 実際=%s",
				expectedAmount, actual.getValue()));
	}

	/**
	 *<pre>
	 * Money値オブジェクトが0であることを検証します。
	 *</pre>
	 * @param actual 検証対象のMoney
	 */
	public static void assertMoneyIsZero(Money actual) {
		assertNotNull(actual, "Money値オブジェクトがnullです");
		assertTrue(actual.isZero(),
			String.format("金額が0ではありません。実際=%s", actual.getValue()));
	}

	/**
	 *<pre>
	 * Money値オブジェクトが正の値であることを検証します。
	 *</pre>
	 * @param actual 検証対象のMoney
	 */
	public static void assertMoneyIsPositive(Money actual) {
		assertNotNull(actual, "Money値オブジェクトがnullです");
		assertTrue(actual.isPositive(),
			String.format("金額が正の値ではありません。実際=%s", actual.getValue()));
	}

	/**
	 *<pre>
	 * Money値オブジェクトが負の値であることを検証します。
	 *</pre>
	 * @param actual 検証対象のMoney
	 */
	public static void assertMoneyIsNegative(Money actual) {
		assertNotNull(actual, "Money値オブジェクトがnullです");
		assertTrue(actual.isNegative(),
			String.format("金額が負の値ではありません。実際=%s", actual.getValue()));
	}

	// ========================================
	// 日付系値オブジェクトのアサーション
	// ========================================

	/**
	 *<pre>
	 * DateValue値オブジェクトの日付が期待値と等しいことを検証します。
	 *</pre>
	 * @param year 期待する年
	 * @param month 期待する月
	 * @param day 期待する日
	 * @param actual 検証対象のDateValue
	 */
	public static void assertDateEquals(int year, int month, int day, DateValue actual) {
		assertNotNull(actual, "DateValue値オブジェクトがnullです");
		LocalDate expected = LocalDate.of(year, month, day);
		assertEquals(expected, actual.getValue(),
			String.format("日付が一致しません。期待値=%s, 実際=%s",
				expected, actual.getValue()));
	}

	/**
	 *<pre>
	 * DateValue値オブジェクトの日付が期待値と等しいことを検証します（LocalDate版）。
	 *</pre>
	 * @param expectedDate 期待する日付
	 * @param actual 検証対象のDateValue
	 */
	public static void assertDateEquals(LocalDate expectedDate, DateValue actual) {
		assertNotNull(actual, "DateValue値オブジェクトがnullです");
		assertEquals(expectedDate, actual.getValue(),
			String.format("日付が一致しません。期待値=%s, 実際=%s",
				expectedDate, actual.getValue()));
	}

	/**
	 *<pre>
	 * DateValue値オブジェクトの日付が期待値より前であることを検証します。
	 *</pre>
	 * @param expected 比較対象のDateValue
	 * @param actual 検証対象のDateValue
	 */
	public static void assertDateBefore(DateValue expected, DateValue actual) {
		assertNotNull(actual, "DateValue値オブジェクトがnullです");
		assertNotNull(expected, "比較対象のDateValue値オブジェクトがnullです");
		assertTrue(actual.isBefore(expected),
			String.format("日付が前ではありません。期待値より前=%s, 実際=%s",
				expected.getValue(), actual.getValue()));
	}

	/**
	 *<pre>
	 * DateValue値オブジェクトの日付が期待値より後であることを検証します。
	 *</pre>
	 * @param expected 比較対象のDateValue
	 * @param actual 検証対象のDateValue
	 */
	public static void assertDateAfter(DateValue expected, DateValue actual) {
		assertNotNull(actual, "DateValue値オブジェクトがnullです");
		assertNotNull(expected, "比較対象のDateValue値オブジェクトがnullです");
		assertTrue(actual.isAfter(expected),
			String.format("日付が後ではありません。期待値より後=%s, 実際=%s",
				expected.getValue(), actual.getValue()));
	}

	// ========================================
	// NullableDateValue系値オブジェクトのアサーション
	// ========================================

	/**
	 *<pre>
	 * NullableDateValue値オブジェクトの日付が期待値と等しいことを検証します。
	 *</pre>
	 * @param year 期待する年
	 * @param month 期待する月
	 * @param day 期待する日
	 * @param actual 検証対象のNullableDateValue
	 */
	public static void assertDateEquals(int year, int month, int day, NullableDateValue actual) {
		assertNotNull(actual, "NullableDateValue値オブジェクトがnullです");
		LocalDate expected = LocalDate.of(year, month, day);
		assertEquals(expected, actual.getValue(),
			String.format("日付が一致しません。期待値=%s, 実際=%s",
				expected, actual.getValue()));
	}

	/**
	 *<pre>
	 * NullableDateValue値オブジェクトの日付が期待値と等しいことを検証します（LocalDate版）。
	 *</pre>
	 * @param expectedDate 期待する日付
	 * @param actual 検証対象のNullableDateValue
	 */
	public static void assertDateEquals(LocalDate expectedDate, NullableDateValue actual) {
		assertNotNull(actual, "NullableDateValue値オブジェクトがnullです");
		assertEquals(expectedDate, actual.getValue(),
			String.format("日付が一致しません。期待値=%s, 実際=%s",
				expectedDate, actual.getValue()));
	}

	/**
	 *<pre>
	 * NullableDateValue値オブジェクトの日付が期待値より前であることを検証します。
	 *</pre>
	 * @param expected 比較対象のNullableDateValue
	 * @param actual 検証対象のNullableDateValue
	 */
	public static void assertDateBefore(NullableDateValue expected, NullableDateValue actual) {
		assertNotNull(actual, "NullableDateValue値オブジェクトがnullです");
		assertNotNull(expected, "比較対象のNullableDateValue値オブジェクトがnullです");
		assertTrue(actual.isBefore(expected),
			String.format("日付が前ではありません。期待値より前=%s, 実際=%s",
				expected.getValue(), actual.getValue()));
	}

	/**
	 *<pre>
	 * NullableDateValue値オブジェクトの日付が期待値より後であることを検証します。
	 *</pre>
	 * @param expected 比較対象のNullableDateValue
	 * @param actual 検証対象のNullableDateValue
	 */
	public static void assertDateAfter(NullableDateValue expected, NullableDateValue actual) {
		assertNotNull(actual, "NullableDateValue値オブジェクトがnullです");
		assertNotNull(expected, "比較対象のNullableDateValue値オブジェクトがnullです");
		assertTrue(actual.isAfter(expected),
			String.format("日付が後ではありません。期待値より後=%s, 実際=%s",
				expected.getValue(), actual.getValue()));
	}

	// ========================================
	// ID系値オブジェクトのアサーション
	// ========================================

	/**
	 *<pre>
	 * Identifier値オブジェクトのIDが期待値と等しいことを検証します。
	 *</pre>
	 * @param expectedId 期待するID
	 * @param actual 検証対象のIdentifier
	 */
	public static void assertIdEquals(String expectedId, Identifier actual) {
		assertNotNull(actual, "Identifier値オブジェクトがnullです");
		assertEquals(expectedId, actual.getValue(),
			String.format("IDが一致しません。期待値=%s, 実際=%s",
				expectedId, actual.getValue()));
	}

	// ========================================
	// 複合的なアサーション
	// ========================================

	/**
	 *<pre>
	 * 2つのMoney値オブジェクトが等しいことを検証します。
	 *</pre>
	 * @param expected 期待するMoney
	 * @param actual 検証対象のMoney
	 */
	public static void assertMoneyEquals(Money expected, Money actual) {
		assertNotNull(expected, "期待するMoney値オブジェクトがnullです");
		assertNotNull(actual, "検証対象のMoney値オブジェクトがnullです");
		assertEquals(expected, actual,
			String.format("Money値オブジェクトが一致しません。期待値=%s, 実際=%s",
				expected.getValue(), actual.getValue()));
	}

	/**
	 *<pre>
	 * 2つのDateValue値オブジェクトが等しいことを検証します。
	 *</pre>
	 * @param expected 期待するDateValue
	 * @param actual 検証対象のDateValue
	 */
	public static void assertDateEquals(DateValue expected, DateValue actual) {
		assertNotNull(expected, "期待するDateValue値オブジェクトがnullです");
		assertNotNull(actual, "検証対象のDateValue値オブジェクトがnullです");
		assertEquals(expected, actual,
			String.format("DateValue値オブジェクトが一致しません。期待値=%s, 実際=%s",
				expected.getValue(), actual.getValue()));
	}

	/**
	 *<pre>
	 * 2つのIdentifier値オブジェクトが等しいことを検証します。
	 *</pre>
	 * @param expected 期待するIdentifier
	 * @param actual 検証対象のIdentifier
	 */
	public static void assertIdEquals(Identifier expected, Identifier actual) {
		assertNotNull(expected, "期待するIdentifier値オブジェクトがnullです");
		assertNotNull(actual, "検証対象のIdentifier値オブジェクトがnullです");
		assertEquals(expected, actual,
			String.format("Identifier値オブジェクトが一致しません。期待値=%s, 実際=%s",
				expected.getValue(), actual.getValue()));
	}
}
