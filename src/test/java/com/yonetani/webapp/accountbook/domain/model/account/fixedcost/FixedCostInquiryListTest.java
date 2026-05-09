/**
 * FixedCostInquiryList#calculateMonthlyTotal の単体テストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/05/07 : 1.01.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.fixedcost;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCostInquiryList.FixedCostInquiryItem;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostPaymentTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;

/**
 *<pre>
 * FixedCostInquiryList#calculateMonthlyTotal のC1網羅単体テストクラスです。
 *
 * 対象年月: 2025/11（奇数月）を起点に3か月
 *   ym0 = 2025年11月（奇数月）
 *   ym1 = 2025年12月（偶数月）
 *   ym2 = 2026年01月（奇数月）
 *
 * テストパターン(C1網羅):
 *   ① 空リスト → 全月ZERO
 *   ② 毎月(00) → 全月加算
 *   ③ 奇数月(20) → 11月・01月加算、12月除外
 *   ④ 偶数月(30) → 12月加算、11月・01月除外
 *   ⑤ 11月指定(11) → 11月加算、12月・01月除外
 *   ⑥ 01月指定(01) → 01月加算、11月・12月除外
 *   ⑦ その他任意(40) → 全月加算
 *   ⑧ 複合(全TUKI組み合わせ) → 3か月の各合計値が正しいこと
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.01)
 *
 */
@DisplayName("FixedCostInquiryList#calculateMonthlyTotal の C1 網羅単体テスト")
class FixedCostInquiryListTest {

	// 対象年月: 2025/11(奇)・2025/12(偶)・2026/01(奇)
	private static final TargetYearMonth YM_NOV = TargetYearMonth.from("202511");
	private static final TargetYearMonth YM_DEC = TargetYearMonth.from("202512");
	private static final TargetYearMonth YM_JAN = TargetYearMonth.from("202601");

	private FixedCostInquiryItem item(String tuki, int amount) {
		return FixedCostInquiryItem.from(
				"0001", "テスト固定費", "", "テスト支出項目",
				tuki, null, "27",
				new BigDecimal(amount).setScale(2));
	}

	/**
	 *<pre>
	 * テスト①：空リスト → 全月ZERO
	 *</pre>
	 */
	@Test
	@DisplayName("① 空リスト → 全月ZERO")
	void testEmpty() {
		FixedCostInquiryList list = FixedCostInquiryList.from(Collections.emptyList());
		assertEquals(FixedCostPaymentTotalAmount.ZERO, list.calculateMonthlyTotal(YM_NOV), "11月はZERO");
		assertEquals(FixedCostPaymentTotalAmount.ZERO, list.calculateMonthlyTotal(YM_DEC), "12月はZERO");
		assertEquals(FixedCostPaymentTotalAmount.ZERO, list.calculateMonthlyTotal(YM_JAN), "01月はZERO");
	}

	/**
	 *<pre>
	 * テスト②：毎月(00) → 全月加算
	 *</pre>
	 */
	@Test
	@DisplayName("② 毎月(00) → 全月加算")
	void testEveryMonth() {
		FixedCostInquiryList list = FixedCostInquiryList.from(List.of(item("00", 10000)));
		assertEquals("10,000円", list.calculateMonthlyTotal(YM_NOV).toFormatString(), "11月は加算");
		assertEquals("10,000円", list.calculateMonthlyTotal(YM_DEC).toFormatString(), "12月は加算");
		assertEquals("10,000円", list.calculateMonthlyTotal(YM_JAN).toFormatString(), "01月は加算");
	}

	/**
	 *<pre>
	 * テスト③：奇数月(20) → 11月・01月加算、12月除外
	 *</pre>
	 */
	@Test
	@DisplayName("③ 奇数月(20) → 11月・01月加算、12月除外")
	void testOddMonth() {
		FixedCostInquiryList list = FixedCostInquiryList.from(List.of(item("20", 10000)));
		assertEquals("10,000円", list.calculateMonthlyTotal(YM_NOV).toFormatString(), "11月(奇)は加算");
		assertEquals(FixedCostPaymentTotalAmount.ZERO, list.calculateMonthlyTotal(YM_DEC), "12月(偶)は除外");
		assertEquals("10,000円", list.calculateMonthlyTotal(YM_JAN).toFormatString(), "01月(奇)は加算");
	}

	/**
	 *<pre>
	 * テスト④：偶数月(30) → 12月加算、11月・01月除外
	 *</pre>
	 */
	@Test
	@DisplayName("④ 偶数月(30) → 12月加算、11月・01月除外")
	void testEvenMonth() {
		FixedCostInquiryList list = FixedCostInquiryList.from(List.of(item("30", 10000)));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, list.calculateMonthlyTotal(YM_NOV), "11月(奇)は除外");
		assertEquals("10,000円", list.calculateMonthlyTotal(YM_DEC).toFormatString(), "12月(偶)は加算");
		assertEquals(FixedCostPaymentTotalAmount.ZERO, list.calculateMonthlyTotal(YM_JAN), "01月(奇)は除外");
	}

	/**
	 *<pre>
	 * テスト⑤：11月指定(11) → 11月加算、12月・01月除外
	 *</pre>
	 */
	@Test
	@DisplayName("⑤ 11月指定(11) → 11月加算、12月・01月除外")
	void testSpecificMonth11() {
		FixedCostInquiryList list = FixedCostInquiryList.from(List.of(item("11", 10000)));
		assertEquals("10,000円", list.calculateMonthlyTotal(YM_NOV).toFormatString(), "11月は加算");
		assertEquals(FixedCostPaymentTotalAmount.ZERO, list.calculateMonthlyTotal(YM_DEC), "12月は除外");
		assertEquals(FixedCostPaymentTotalAmount.ZERO, list.calculateMonthlyTotal(YM_JAN), "01月は除外");
	}

	/**
	 *<pre>
	 * テスト⑥：01月指定(01) → 01月加算、11月・12月除外
	 *</pre>
	 */
	@Test
	@DisplayName("⑥ 01月指定(01) → 01月加算、11月・12月除外")
	void testSpecificMonth01() {
		FixedCostInquiryList list = FixedCostInquiryList.from(List.of(item("01", 10000)));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, list.calculateMonthlyTotal(YM_NOV), "11月は除外");
		assertEquals(FixedCostPaymentTotalAmount.ZERO, list.calculateMonthlyTotal(YM_DEC), "12月は除外");
		assertEquals("10,000円", list.calculateMonthlyTotal(YM_JAN).toFormatString(), "01月は加算");
	}

	/**
	 *<pre>
	 * テスト⑦：その他任意(40) → 全月加算
	 *</pre>
	 */
	@Test
	@DisplayName("⑦ その他任意(40) → 全月加算")
	void testOptional() {
		FixedCostInquiryList list = FixedCostInquiryList.from(List.of(item("40", 10000)));
		assertEquals("10,000円", list.calculateMonthlyTotal(YM_NOV).toFormatString(), "11月は加算");
		assertEquals("10,000円", list.calculateMonthlyTotal(YM_DEC).toFormatString(), "12月は加算");
		assertEquals("10,000円", list.calculateMonthlyTotal(YM_JAN).toFormatString(), "01月は加算");
	}

	/**
	 *<pre>
	 * テスト⑧：複合(全TUKI組み合わせ) → 3か月の各合計値が正しいこと
	 *
	 * データ:
	 *   毎月(00):      60,000円
	 *   毎月(00):      12,000円
	 *   奇数月(20):    16,590円
	 *   偶数月(30):     8,000円
	 *   その他任意(40): 10,000円
	 *   11月指定(11):   5,000円
	 *
	 * 期待値:
	 *   11月(奇): 60,000+12,000+16,590+0+10,000+5,000 = 103,590円
	 *   12月(偶): 60,000+12,000+0+8,000+10,000+0      =  90,000円
	 *   01月(奇): 60,000+12,000+16,590+0+10,000+0     =  98,590円
	 *</pre>
	 */
	@Test
	@DisplayName("⑧ 複合(全TUKI組み合わせ) → 3か月の各合計値が正しいこと")
	void testComplex() {
		List<FixedCostInquiryItem> items = Arrays.asList(
				item("00", 60000),
				item("00", 12000),
				item("20", 16590),
				item("30", 8000),
				item("40", 10000),
				item("11", 5000));
		FixedCostInquiryList list = FixedCostInquiryList.from(items);
		assertEquals("103,590円", list.calculateMonthlyTotal(YM_NOV).toFormatString(), "11月合計");
		assertEquals("90,000円",  list.calculateMonthlyTotal(YM_DEC).toFormatString(), "12月合計");
		assertEquals("98,590円",  list.calculateMonthlyTotal(YM_JAN).toFormatString(), "01月合計");
	}
}
