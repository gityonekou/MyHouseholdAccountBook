/**
 * FixedCostTargetPaymentMonth の単体テストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/10/18 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.fixedcost;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * FixedCostTargetPaymentMonth の単体テストクラスです。
 *
 * [対象メソッド]
 * 1. appliesToMonth - 指定した月に支払いが発生するかどうかを判定する
 * 2. isOptional     - 支払月の値が「その他任意」かどうかを判定する
 *
 * [テストシナリオ(appliesToMonth)]
 *   ① 毎月(00) → 全月true
 *   ② 奇数月(20) → 奇数月true・偶数月false
 *   ③ 偶数月(30) → 偶数月true・奇数月false
 *   ④ 特定月(11) → 11月true・他月false
 *   ⑤ 特定月(01) → 01月true・他月false
 *   ⑥ その他任意(40) → 全月true
 * [テストシナリオ(isOptional)]
 *   ⑦ その他任意(40) → true
 *   ⑧ その他任意以外(00/20/30/11) → false
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("FixedCostTargetPaymentMonth の C1 網羅単体テスト")
class FixedCostTargetPaymentMonthTest {

	/**
	 *<pre>
	 * テスト①：毎月(00) → 全月true
	 *</pre>
	 */
	@Test
	@DisplayName("① appliesToMonth: 毎月(00) → 全月true")
	void testAppliesToMonthEveryMonth() {
		FixedCostTargetPaymentMonth target = FixedCostTargetPaymentMonth.from("00");
		assertTrue(target.appliesToMonth(11), "11月はtrue");
		assertTrue(target.appliesToMonth(12), "12月はtrue");
		assertTrue(target.appliesToMonth(1), "01月はtrue");
	}

	/**
	 *<pre>
	 * テスト②：奇数月(20) → 奇数月true・偶数月false
	 *</pre>
	 */
	@Test
	@DisplayName("② appliesToMonth: 奇数月(20) → 奇数月true・偶数月false")
	void testAppliesToMonthOddMonth() {
		FixedCostTargetPaymentMonth target = FixedCostTargetPaymentMonth.from("20");
		assertTrue(target.appliesToMonth(11), "11月(奇)はtrue");
		assertFalse(target.appliesToMonth(12), "12月(偶)はfalse");
		assertTrue(target.appliesToMonth(1), "01月(奇)はtrue");
	}

	/**
	 *<pre>
	 * テスト③：偶数月(30) → 偶数月true・奇数月false
	 *</pre>
	 */
	@Test
	@DisplayName("③ appliesToMonth: 偶数月(30) → 偶数月true・奇数月false")
	void testAppliesToMonthEvenMonth() {
		FixedCostTargetPaymentMonth target = FixedCostTargetPaymentMonth.from("30");
		assertFalse(target.appliesToMonth(11), "11月(奇)はfalse");
		assertTrue(target.appliesToMonth(12), "12月(偶)はtrue");
		assertFalse(target.appliesToMonth(1), "01月(奇)はfalse");
	}

	/**
	 *<pre>
	 * テスト④：特定月(11) → 11月true・他月false
	 *</pre>
	 */
	@Test
	@DisplayName("④ appliesToMonth: 特定月(11) → 11月true・他月false")
	void testAppliesToMonthSpecific11() {
		FixedCostTargetPaymentMonth target = FixedCostTargetPaymentMonth.from("11");
		assertTrue(target.appliesToMonth(11), "11月はtrue");
		assertFalse(target.appliesToMonth(12), "12月はfalse");
		assertFalse(target.appliesToMonth(1), "01月はfalse");
	}

	/**
	 *<pre>
	 * テスト⑤：特定月(01) → 01月true・他月false
	 *</pre>
	 */
	@Test
	@DisplayName("⑤ appliesToMonth: 特定月(01) → 01月true・他月false")
	void testAppliesToMonthSpecific01() {
		FixedCostTargetPaymentMonth target = FixedCostTargetPaymentMonth.from("01");
		assertFalse(target.appliesToMonth(11), "11月はfalse");
		assertFalse(target.appliesToMonth(12), "12月はfalse");
		assertTrue(target.appliesToMonth(1), "01月はtrue");
	}

	/**
	 *<pre>
	 * テスト⑥：その他任意(40) → 全月true
	 *</pre>
	 */
	@Test
	@DisplayName("⑥ appliesToMonth: その他任意(40) → 全月true")
	void testAppliesToMonthOptional() {
		FixedCostTargetPaymentMonth target = FixedCostTargetPaymentMonth.from("40");
		assertTrue(target.appliesToMonth(11), "11月はtrue");
		assertTrue(target.appliesToMonth(12), "12月はtrue");
		assertTrue(target.appliesToMonth(1), "01月はtrue");
	}

	/**
	 *<pre>
	 * テスト⑦：その他任意(40) → isOptionalはtrue
	 *</pre>
	 */
	@Test
	@DisplayName("⑦ isOptional: その他任意(40) → true")
	void testIsOptionalTrue() {
		assertTrue(FixedCostTargetPaymentMonth.from("40").isOptional(), "その他任意(40)はtrue");
	}

	/**
	 *<pre>
	 * テスト⑧：その他任意以外(00/20/30/11) → isOptionalはfalse
	 *</pre>
	 */
	@Test
	@DisplayName("⑧ isOptional: その他任意以外(00/20/30/11) → false")
	void testIsOptionalFalse() {
		assertFalse(FixedCostTargetPaymentMonth.from("00").isOptional(), "毎月(00)はfalse");
		assertFalse(FixedCostTargetPaymentMonth.from("20").isOptional(), "奇数月(20)はfalse");
		assertFalse(FixedCostTargetPaymentMonth.from("30").isOptional(), "偶数月(30)はfalse");
		assertFalse(FixedCostTargetPaymentMonth.from("11").isOptional(), "特定月(11)はfalse");
	}
}
