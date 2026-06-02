/**
 * FixedCostAnnualSummaryList#buildMonthlyRows および buildYearlyRow の単体テストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/05/23 : 1.01.00  新規作成
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

import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCostAnnualSummaryList.AnnualSummaryColumn;
import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCostAnnualSummaryList.FixedCostAnnualSummaryItem;
import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCostAnnualSummaryList.MonthlyRow;
import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCostAnnualSummaryList.YearlyRow;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostPaymentAmount;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostPaymentTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostTargetPaymentMonth;

/**
 *<pre>
 * FixedCostAnnualSummaryList#buildMonthlyRows および #buildYearlyRow のC1網羅単体テストクラスです。
 *
 * テストパターン(C1網羅):
 * [buildMonthlyRows]
 *   ① 空リスト → 全月・全列ZERO
 *   ② Level-1:0023、Level-2:null（Level-1項目直接）→ SEIKATSUHI列に加算
 *   ③ Level-1:0023、Level-2:0029（地代家賃）→ SEIKATSUHI列に加算
 *   ④ Level-1:0023、Level-2:0031（積立(投資)）→ TSUMITATE_TOUSHI列に加算
 *   ⑤ Level-1:0023、Level-2:0033（積立金）→ TSUMITATE_KIN列に加算
 *   ⑥ Level-1:0013（固定費(非課税)）→ HIKOZEI列に加算
 *   ⑦ Level-1:0001（事業経費）→ JIGYOU_KEIHI列に加算
 *   ⑧ Level-1:0045（衣類住居設備）→ IRUI_JUKYO列に加算
 *   ⑨ Level-1:0049（飲食日用品）→ INSHOKU列に加算
 *   ⑩ Level-1:0056（趣味娯楽）→ SHUMI列に加算
 *   ⑪ 支払月コード全種（毎月/奇数月/偶数月/特定月/その他任意）→ shouldAdd C1網羅
 *   ⑫ 月合計・列合計が正しく計算されること
 * [buildYearlyRow]
 *   ⑬ 空リスト → 全列ZERO・年合計ZERO
 *   ⑭ 毎月払い → 年間12倍になること
 *   ⑮ 奇数月払い → 年間6倍になること
 *   ⑯ 偶数月払い → 年間6倍になること
 *   ⑰ 特定月払い → 年間1倍になること
 *   ⑱ 複数カテゴリ・月種別混在 → 年間カテゴリ別合計・年合計が正しいこと
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.01)
 *
 */
@DisplayName("FixedCostAnnualSummaryList#buildMonthlyRows / buildYearlyRow の C1 網羅単体テスト")
class FixedCostAnnualSummaryListTest {

	private FixedCostAnnualSummaryItem item(String tuki, int amount, String level1Code, String level2Code) {
		return FixedCostAnnualSummaryItem.from(
				FixedCostTargetPaymentMonth.from(tuki),
				FixedCostPaymentAmount.from(new BigDecimal(amount).setScale(2)),
				level1Code,
				level2Code);
	}

	/**
	 *<pre>
	 * テスト①：空リスト → 全月・全列ZERO
	 *</pre>
	 */
	@Test
	@DisplayName("① 空リスト → 全月・全列ZERO")
	void testEmpty() {
		FixedCostAnnualSummaryList list = FixedCostAnnualSummaryList.from(Collections.emptyList());
		List<MonthlyRow> rows = list.getMonthlyRows();
		assertEquals(12, rows.size(), "12行生成されること");
		for (int i = 0; i < 12; i++) {
			MonthlyRow row = rows.get(i);
			assertEquals(i + 1, row.getMonth(), (i + 1) + "月のmonth値");
			for (AnnualSummaryColumn col : AnnualSummaryColumn.values()) {
				assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(col), (i + 1) + "月 " + col + " はZERO");
			}
			assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getMonthTotal(), (i + 1) + "月の月合計はZERO");
		}
	}

	/**
	 *<pre>
	 * テスト②：Level-1:0023、Level-2:null（Level-1項目直接）→ SEIKATSUHI列に加算
	 *</pre>
	 */
	@Test
	@DisplayName("② Level-1:0023、Level-2:null（Level-1項目直接）→ SEIKATSUHI列に加算されること")
	void testColumnSeikatsuhi_level1Direct() {
		FixedCostAnnualSummaryList list = FixedCostAnnualSummaryList.from(
				List.of(item("00", 10000, "0023", null)));
		MonthlyRow row = list.getMonthlyRows().get(0); // 1月で代表
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.JIGYOU_KEIHI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.HIKOZEI));
		assertEquals("10,000円", row.getAmount(AnnualSummaryColumn.SEIKATSUHI).toFormatString());
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.TSUMITATE_TOUSHI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.TSUMITATE_KIN));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.IRUI_JUKYO));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.INSHOKU));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.SHUMI));
	}

	/**
	 *<pre>
	 * テスト③：Level-1:0023、Level-2:0029（地代家賃）→ SEIKATSUHI列に加算
	 *</pre>
	 */
	@Test
	@DisplayName("③ Level-1:0023、Level-2:0029（地代家賃）→ SEIKATSUHI列に加算されること")
	void testColumnSeikatsuhi_level2Other() {
		FixedCostAnnualSummaryList list = FixedCostAnnualSummaryList.from(
				List.of(item("00", 10000, "0023", "0029")));
		MonthlyRow row = list.getMonthlyRows().get(0); // 1月で代表
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.JIGYOU_KEIHI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.HIKOZEI));
		assertEquals("10,000円", row.getAmount(AnnualSummaryColumn.SEIKATSUHI).toFormatString());
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.TSUMITATE_TOUSHI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.TSUMITATE_KIN));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.IRUI_JUKYO));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.INSHOKU));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.SHUMI));
	}

	/**
	 *<pre>
	 * テスト④：Level-1:0023、Level-2:0031（積立(投資)）→ TSUMITATE_TOUSHI列に加算
	 *</pre>
	 */
	@Test
	@DisplayName("④ Level-1:0023、Level-2:0031（積立(投資)）→ TSUMITATE_TOUSHI列に加算されること")
	void testColumnTsumitateToushi() {
		FixedCostAnnualSummaryList list = FixedCostAnnualSummaryList.from(
				List.of(item("00", 10000, "0023", "0031")));
		MonthlyRow row = list.getMonthlyRows().get(0); // 1月で代表
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.JIGYOU_KEIHI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.HIKOZEI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.SEIKATSUHI));
		assertEquals("10,000円", row.getAmount(AnnualSummaryColumn.TSUMITATE_TOUSHI).toFormatString());
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.TSUMITATE_KIN));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.IRUI_JUKYO));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.INSHOKU));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.SHUMI));
	}

	/**
	 *<pre>
	 * テスト⑤：Level-1:0023、Level-2:0033（積立金）→ TSUMITATE_KIN列に加算
	 *</pre>
	 */
	@Test
	@DisplayName("⑤ Level-1:0023、Level-2:0033（積立金）→ TSUMITATE_KIN列に加算されること")
	void testColumnTsumitateKin() {
		FixedCostAnnualSummaryList list = FixedCostAnnualSummaryList.from(
				List.of(item("00", 10000, "0023", "0033")));
		MonthlyRow row = list.getMonthlyRows().get(0); // 1月で代表
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.JIGYOU_KEIHI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.HIKOZEI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.SEIKATSUHI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.TSUMITATE_TOUSHI));
		assertEquals("10,000円", row.getAmount(AnnualSummaryColumn.TSUMITATE_KIN).toFormatString());
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.IRUI_JUKYO));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.INSHOKU));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.SHUMI));
	}

	/**
	 *<pre>
	 * テスト⑥：Level-1:0013（固定費(非課税)）→ HIKOZEI列に加算
	 *</pre>
	 */
	@Test
	@DisplayName("⑥ Level-1:0013（固定費(非課税)）→ HIKOZEI列に加算されること")
	void testColumnHikozei() {
		FixedCostAnnualSummaryList list = FixedCostAnnualSummaryList.from(
				List.of(item("00", 10000, "0013", null)));
		MonthlyRow row = list.getMonthlyRows().get(0); // 1月で代表
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.JIGYOU_KEIHI));
		assertEquals("10,000円", row.getAmount(AnnualSummaryColumn.HIKOZEI).toFormatString());
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.SEIKATSUHI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.TSUMITATE_TOUSHI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.TSUMITATE_KIN));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.IRUI_JUKYO));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.INSHOKU));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.SHUMI));
	}

	/**
	 *<pre>
	 * テスト⑦：Level-1:0001（事業経費）→ JIGYOU_KEIHI列に加算
	 *</pre>
	 */
	@Test
	@DisplayName("⑦ Level-1:0001（事業経費）→ JIGYOU_KEIHI列に加算されること")
	void testColumnJigyouKeihi() {
		FixedCostAnnualSummaryList list = FixedCostAnnualSummaryList.from(
				List.of(item("00", 10000, "0001", null)));
		MonthlyRow row = list.getMonthlyRows().get(0); // 1月で代表
		assertEquals("10,000円", row.getAmount(AnnualSummaryColumn.JIGYOU_KEIHI).toFormatString());
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.HIKOZEI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.SEIKATSUHI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.TSUMITATE_TOUSHI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.TSUMITATE_KIN));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.IRUI_JUKYO));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.INSHOKU));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.SHUMI));
	}

	/**
	 *<pre>
	 * テスト⑧：Level-1:0045（衣類住居設備）→ IRUI_JUKYO列に加算
	 *</pre>
	 */
	@Test
	@DisplayName("⑧ Level-1:0045（衣類住居設備）→ IRUI_JUKYO列に加算されること")
	void testColumnIruiJukyo() {
		FixedCostAnnualSummaryList list = FixedCostAnnualSummaryList.from(
				List.of(item("00", 10000, "0045", null)));
		MonthlyRow row = list.getMonthlyRows().get(0); // 1月で代表
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.JIGYOU_KEIHI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.HIKOZEI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.SEIKATSUHI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.TSUMITATE_TOUSHI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.TSUMITATE_KIN));
		assertEquals("10,000円", row.getAmount(AnnualSummaryColumn.IRUI_JUKYO).toFormatString());
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.INSHOKU));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.SHUMI));
	}

	/**
	 *<pre>
	 * テスト⑨：Level-1:0049（飲食日用品）→ INSHOKU列に加算
	 *</pre>
	 */
	@Test
	@DisplayName("⑨ Level-1:0049（飲食日用品）→ INSHOKU列に加算されること")
	void testColumnInshoku() {
		FixedCostAnnualSummaryList list = FixedCostAnnualSummaryList.from(
				List.of(item("00", 10000, "0049", null)));
		MonthlyRow row = list.getMonthlyRows().get(0); // 1月で代表
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.JIGYOU_KEIHI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.HIKOZEI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.SEIKATSUHI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.TSUMITATE_TOUSHI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.TSUMITATE_KIN));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.IRUI_JUKYO));
		assertEquals("10,000円", row.getAmount(AnnualSummaryColumn.INSHOKU).toFormatString());
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.SHUMI));
	}

	/**
	 *<pre>
	 * テスト⑩：Level-1:0056（趣味娯楽）→ SHUMI列に加算
	 *</pre>
	 */
	@Test
	@DisplayName("⑩ Level-1:0056（趣味娯楽）→ SHUMI列に加算されること")
	void testColumnShumi() {
		FixedCostAnnualSummaryList list = FixedCostAnnualSummaryList.from(
				List.of(item("00", 10000, "0056", null)));
		MonthlyRow row = list.getMonthlyRows().get(0); // 1月で代表
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.JIGYOU_KEIHI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.HIKOZEI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.SEIKATSUHI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.TSUMITATE_TOUSHI));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.TSUMITATE_KIN));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.IRUI_JUKYO));
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.INSHOKU));
		assertEquals("10,000円", row.getAmount(AnnualSummaryColumn.SHUMI).toFormatString());
	}

	/**
	 *<pre>
	 * テスト⑪：支払月コード全種（毎月/奇数月/偶数月/特定月/その他任意）→ shouldAdd C1網羅
	 *
	 * データ（全て SEIKATSUHI 列 level1=0023/level2=0029）:
	 *   毎月(00):      10,000円
	 *   奇数月(20):     5,000円
	 *   偶数月(30):     3,000円
	 *   5月指定(05):   20,000円
	 *   その他任意(40): 1,000円
	 *
	 * 期待値:
	 *   奇数月(1,3,7,9,11): 00+20+40 = 16,000円
	 *   偶数月(2,4,6,8,10,12): 00+30+40 = 14,000円
	 *   5月(奇+特定): 00+20+05+40 = 36,000円
	 *</pre>
	 */
	@Test
	@DisplayName("⑪ 支払月コード全種 → 各月で正しく加算・除外されること（shouldAdd C1網羅）")
	void testShouldAdd_allPatterns() {
		List<FixedCostAnnualSummaryItem> items = Arrays.asList(
				item("00", 10000, "0023", "0029"),  // 毎月
				item("20",  5000, "0023", "0029"),  // 奇数月
				item("30",  3000, "0023", "0029"),  // 偶数月
				item("05", 20000, "0023", "0029"),  // 5月指定
				item("40",  1000, "0023", "0029")); // その他任意
		FixedCostAnnualSummaryList list = FixedCostAnnualSummaryList.from(items);
		List<MonthlyRow> rows = list.getMonthlyRows();

		assertEquals("16,000円", rows.get(0).getAmount(AnnualSummaryColumn.SEIKATSUHI).toFormatString(), "1月(奇)");
		assertEquals("14,000円", rows.get(1).getAmount(AnnualSummaryColumn.SEIKATSUHI).toFormatString(), "2月(偶)");
		assertEquals("16,000円", rows.get(2).getAmount(AnnualSummaryColumn.SEIKATSUHI).toFormatString(), "3月(奇)");
		assertEquals("14,000円", rows.get(3).getAmount(AnnualSummaryColumn.SEIKATSUHI).toFormatString(), "4月(偶)");
		assertEquals("36,000円", rows.get(4).getAmount(AnnualSummaryColumn.SEIKATSUHI).toFormatString(), "5月(奇+特定)");
		assertEquals("14,000円", rows.get(5).getAmount(AnnualSummaryColumn.SEIKATSUHI).toFormatString(), "6月(偶)");
		assertEquals("16,000円", rows.get(6).getAmount(AnnualSummaryColumn.SEIKATSUHI).toFormatString(), "7月(奇)");
		assertEquals("14,000円", rows.get(7).getAmount(AnnualSummaryColumn.SEIKATSUHI).toFormatString(), "8月(偶)");
		assertEquals("16,000円", rows.get(8).getAmount(AnnualSummaryColumn.SEIKATSUHI).toFormatString(), "9月(奇)");
		assertEquals("14,000円", rows.get(9).getAmount(AnnualSummaryColumn.SEIKATSUHI).toFormatString(), "10月(偶)");
		assertEquals("16,000円", rows.get(10).getAmount(AnnualSummaryColumn.SEIKATSUHI).toFormatString(), "11月(奇)");
		assertEquals("14,000円", rows.get(11).getAmount(AnnualSummaryColumn.SEIKATSUHI).toFormatString(), "12月(偶)");
	}

	/**
	 *<pre>
	 * テスト⑫：月合計・列合計が正しく計算されること
	 *
	 * データ（全て毎月(00)）:
	 *   JIGYOU_KEIHI (0001/null):  10,000円
	 *   SEIKATSUHI   (0023/0029):  20,000円
	 *   HIKOZEI      (0013/null):   5,000円
	 *
	 * 期待値 (任意月・全月同値):
	 *   JIGYOU_KEIHI=10,000円, SEIKATSUHI=20,000円, HIKOZEI=5,000円
	 *   その他列 = ZERO、月合計 = 35,000円
	 *</pre>
	 */
	@Test
	@DisplayName("⑫ 月合計・列合計が正しく計算されること")
	void testMonthlyTotal() {
		List<FixedCostAnnualSummaryItem> items = Arrays.asList(
				item("00", 10000, "0001", null),   // JIGYOU_KEIHI
				item("00", 20000, "0023", "0029"), // SEIKATSUHI
				item("00",  5000, "0013", null));  // HIKOZEI
		FixedCostAnnualSummaryList list = FixedCostAnnualSummaryList.from(items);
		List<MonthlyRow> rows = list.getMonthlyRows();

		// 1月・6月・12月で代表検証（全12月同値）
		for (int idx : new int[]{0, 5, 11}) {
			MonthlyRow row = rows.get(idx);
			int month = idx + 1;
			assertEquals("10,000円", row.getAmount(AnnualSummaryColumn.JIGYOU_KEIHI).toFormatString(), month + "月 JIGYOU_KEIHI");
			assertEquals("20,000円", row.getAmount(AnnualSummaryColumn.SEIKATSUHI).toFormatString(),   month + "月 SEIKATSUHI");
			assertEquals("5,000円",  row.getAmount(AnnualSummaryColumn.HIKOZEI).toFormatString(),      month + "月 HIKOZEI");
			assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.TSUMITATE_TOUSHI), month + "月 TSUMITATE_TOUSHI");
			assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.TSUMITATE_KIN),    month + "月 TSUMITATE_KIN");
			assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.IRUI_JUKYO),       month + "月 IRUI_JUKYO");
			assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.INSHOKU),          month + "月 INSHOKU");
			assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.SHUMI),            month + "月 SHUMI");
			assertEquals("35,000円", row.getMonthTotal().toFormatString(), month + "月 月合計");
		}
	}

	// ================================================================
	// buildYearlyRow のテスト（⑬〜⑱）
	// ================================================================

	/**
	 *<pre>
	 * テスト⑬：buildYearlyRow 空リスト → 全列ZERO・年合計ZERO
	 *</pre>
	 */
	@Test
	@DisplayName("⑬ buildYearlyRow: 空リスト → 全列ZERO・年合計ZERO")
	void testBuildYearlyRow_empty() {
		FixedCostAnnualSummaryList list = FixedCostAnnualSummaryList.from(Collections.emptyList());
		YearlyRow row = list.getYearlyRow();
		for (AnnualSummaryColumn col : AnnualSummaryColumn.values()) {
			assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(col), col + " はZERO");
		}
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getYearTotal(), "年合計はZERO");
	}

	/**
	 *<pre>
	 * テスト⑭：buildYearlyRow 毎月払い10,000円 → 年間120,000円になること
	 *
	 * 毎月(00)=12か月分 → 10,000 × 12 = 120,000円
	 *</pre>
	 */
	@Test
	@DisplayName("⑭ buildYearlyRow: 毎月払い10,000円 → 年間120,000円になること")
	void testBuildYearlyRow_monthly() {
		FixedCostAnnualSummaryList list = FixedCostAnnualSummaryList.from(
				List.of(item("00", 10000, "0023", "0029")));  // SEIKATSUHI, 毎月
		YearlyRow row = list.getYearlyRow();
		assertEquals("120,000円", row.getAmount(AnnualSummaryColumn.SEIKATSUHI).toFormatString(), "SEIKATSUHI年間合計");
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.JIGYOU_KEIHI), "JIGYOU_KEIHI=ZERO");
		assertEquals("120,000円", row.getYearTotal().toFormatString(), "年合計");
	}

	/**
	 *<pre>
	 * テスト⑮：buildYearlyRow 奇数月払い10,000円 → 年間60,000円になること
	 *
	 * 奇数月(20)=6か月分(1,3,5,7,9,11月) → 10,000 × 6 = 60,000円
	 *</pre>
	 */
	@Test
	@DisplayName("⑮ buildYearlyRow: 奇数月払い10,000円 → 年間60,000円になること")
	void testBuildYearlyRow_oddMonth() {
		FixedCostAnnualSummaryList list = FixedCostAnnualSummaryList.from(
				List.of(item("20", 10000, "0013", null)));  // HIKOZEI, 奇数月
		YearlyRow row = list.getYearlyRow();
		assertEquals("60,000円", row.getAmount(AnnualSummaryColumn.HIKOZEI).toFormatString(), "HIKOZEI年間合計");
		assertEquals("60,000円", row.getYearTotal().toFormatString(), "年合計");
	}

	/**
	 *<pre>
	 * テスト⑯：buildYearlyRow 偶数月払い5,000円 → 年間30,000円になること
	 *
	 * 偶数月(30)=6か月分(2,4,6,8,10,12月) → 5,000 × 6 = 30,000円
	 *</pre>
	 */
	@Test
	@DisplayName("⑯ buildYearlyRow: 偶数月払い5,000円 → 年間30,000円になること")
	void testBuildYearlyRow_evenMonth() {
		FixedCostAnnualSummaryList list = FixedCostAnnualSummaryList.from(
				List.of(item("30", 5000, "0023", "0031")));  // TSUMITATE_TOUSHI, 偶数月
		YearlyRow row = list.getYearlyRow();
		assertEquals("30,000円", row.getAmount(AnnualSummaryColumn.TSUMITATE_TOUSHI).toFormatString(), "TSUMITATE_TOUSHI年間合計");
		assertEquals("30,000円", row.getYearTotal().toFormatString(), "年合計");
	}

	/**
	 *<pre>
	 * テスト⑰：buildYearlyRow 5月指定払い20,000円 → 年間20,000円になること
	 *
	 * 特定月(05)=1か月分(5月のみ) → 20,000 × 1 = 20,000円
	 *</pre>
	 */
	@Test
	@DisplayName("⑰ buildYearlyRow: 5月指定払い20,000円 → 年間20,000円になること")
	void testBuildYearlyRow_specificMonth() {
		FixedCostAnnualSummaryList list = FixedCostAnnualSummaryList.from(
				List.of(item("05", 20000, "0023", "0033")));  // TSUMITATE_KIN, 5月のみ
		YearlyRow row = list.getYearlyRow();
		assertEquals("20,000円", row.getAmount(AnnualSummaryColumn.TSUMITATE_KIN).toFormatString(), "TSUMITATE_KIN年間合計");
		assertEquals("20,000円", row.getYearTotal().toFormatString(), "年合計");
	}

	/**
	 *<pre>
	 * テスト⑱：buildYearlyRow 複数カテゴリ・月種別混在 → 年間カテゴリ別合計・年合計が正しいこと
	 *
	 * データ（全て毎月(00)）:
	 *   JIGYOU_KEIHI (0001/null):  10,000円 × 12か月 = 120,000円
	 *   SEIKATSUHI   (0023/0029):  20,000円 × 12か月 = 240,000円
	 *   HIKOZEI      (0013/null):   5,000円 × 12か月 =  60,000円
	 *   年合計: 420,000円
	 *</pre>
	 */
	@Test
	@DisplayName("⑱ buildYearlyRow: 複数カテゴリ混在 → 年間カテゴリ別合計・年合計が正しいこと")
	void testBuildYearlyRow_multipleCategories() {
		List<FixedCostAnnualSummaryItem> items = Arrays.asList(
				item("00", 10000, "0001", null),   // JIGYOU_KEIHI, 毎月
				item("00", 20000, "0023", "0029"), // SEIKATSUHI,   毎月
				item("00",  5000, "0013", null));  // HIKOZEI,       毎月
		FixedCostAnnualSummaryList list = FixedCostAnnualSummaryList.from(items);
		YearlyRow row = list.getYearlyRow();

		assertEquals("120,000円", row.getAmount(AnnualSummaryColumn.JIGYOU_KEIHI).toFormatString(), "JIGYOU_KEIHI年間合計");
		assertEquals("240,000円", row.getAmount(AnnualSummaryColumn.SEIKATSUHI).toFormatString(),   "SEIKATSUHI年間合計");
		assertEquals("60,000円",  row.getAmount(AnnualSummaryColumn.HIKOZEI).toFormatString(),      "HIKOZEI年間合計");
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.TSUMITATE_TOUSHI), "TSUMITATE_TOUSHI=ZERO");
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.TSUMITATE_KIN),    "TSUMITATE_KIN=ZERO");
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.IRUI_JUKYO),       "IRUI_JUKYO=ZERO");
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.INSHOKU),          "INSHOKU=ZERO");
		assertEquals(FixedCostPaymentTotalAmount.ZERO, row.getAmount(AnnualSummaryColumn.SHUMI),            "SHUMI=ZERO");
		assertEquals("420,000円", row.getYearTotal().toFormatString(), "年合計");
	}
}
