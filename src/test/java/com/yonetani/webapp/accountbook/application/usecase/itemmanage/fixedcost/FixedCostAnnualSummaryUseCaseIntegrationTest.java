/**
 * 年間固定費合計ユースケースの統合テストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/05/23 : 1.01.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage.fixedcost;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import com.yonetani.webapp.accountbook.presentation.response.itemmanage.fixedcost.FixedCostAnnualSummaryResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.fixedcost.FixedCostAnnualSummaryResponse.AnnualSummaryRowItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

/**
 *<pre>
 * 年間固定費合計ユースケースの統合テストクラスです。
 *
 * [テストデータ]
 * user01: 固定費4件（全て毎月払い）
 *   0001: 家賃         → SEIKATSUHI  (80,000円/月)
 *   0002: 電気代概算   → SEIKATSUHI  ( 8,000円/月)
 *   0003: 国民年金保険 → HIKOZEI     (16,520円/月)
 *   0004: 積立NISA     → TSUMITATE_TOUSHI (20,000円/月)
 *   NOW_TARGET_MONTH=05 → targetMonth="05"
 *
 * user02: 固定費0件
 *
 * [期待値] 全月共通:
 *   SEIKATSUHI=88,000円, HIKOZEI=16,520円, TSUMITATE_TOUSHI=20,000円
 *   その他列=ー, 月合計=124,520円
 *   年間合計行: SEIKATSUHI=1,056,000円, HIKOZEI=198,240円, TSUMITATE_TOUSHI=240,000円, 合計=1,494,240円
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.01)
 *
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
	"/sql/initsql/schema_test.sql",
	"/com/yonetani/webapp/accountbook/application/usecase/itemmanage/fixedcost/FixedCostAnnualSummaryIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("年間固定費合計ユースケースの統合テスト")
class FixedCostAnnualSummaryUseCaseIntegrationTest {

	@Autowired
	private FixedCostAnnualSummaryUseCase useCase;

	private final LoginUserInfo USER01 = LoginUserInfo.from("user01", "テストユーザ01");
	private final LoginUserInfo USER02 = LoginUserInfo.from("user02", "テストユーザ02");

	/**
	 *<pre>
	 * テスト①：固定費0件の場合、メッセージが設定されること
	 *
	 * 【検証内容】
	 * ・user02（固定費未登録）でreadAnnualSummaryInfoを呼ぶ
	 * ・"登録済み固定費情報が0件です。" メッセージが設定されること
	 * ・annualSummaryRowListがnullであること（0件時は設定されない）
	 * ・targetMonthが設定されること（"05"）
	 *</pre>
	 */
	@Test
	@DisplayName("① 固定費0件 → メッセージが設定されること")
	void testReadAnnualSummaryInfo_0件() {
		FixedCostAnnualSummaryResponse response = useCase.readAnnualSummaryInfo(USER02);

		// 0件メッセージが設定されること
		assertTrue(response.hasMessages(), "0件メッセージが設定されること");
		assertTrue(response.getMessagesList().stream()
				.anyMatch(msg -> msg.contains("登録済み固定費情報が0件です。")),
				"0件メッセージの内容確認");

		// リストは設定されないこと
		assertNull(response.getAnnualSummaryRowList(), "0件時はannualSummaryRowListがnullであること");

		// targetMonthは設定されること
		assertEquals("05", response.getTargetMonth(), "targetMonthが'05'であること");
	}

	/**
	 *<pre>
	 * テスト②：固定費4件の場合、12行+合計行が生成され、月合計・カテゴリ別合計が正しいこと
	 *
	 * 【検証内容】
	 * ・13行（12月分 + 合計行）が生成されること
	 * ・targetMonth = "05" が設定されること
	 * ・全月の各カテゴリ値・月合計が正しいこと（代表月: 1月・6月・12月）
	 * ・偶数月行のevenMonth=true、奇数月行のevenMonth=false
	 * ・合計行のtotalRow=true、各列の年間合計が正しいこと
	 * ・0円列は "ー" で表示されること
	 *</pre>
	 */
	@Test
	@DisplayName("② 固定費4件 → 12行+合計行が生成され月合計・カテゴリ別合計が正しいこと")
	void testReadAnnualSummaryInfo_4件() {
		FixedCostAnnualSummaryResponse response = useCase.readAnnualSummaryInfo(USER01);

		// メッセージなし
		assertFalse(response.hasMessages(), "メッセージが設定されていないこと");

		// targetMonth
		assertEquals("05", response.getTargetMonth(), "targetMonthが'05'であること");

		// 13行生成確認
		List<AnnualSummaryRowItem> rows = response.getAnnualSummaryRowList();
		assertNotNull(rows, "annualSummaryRowListがnullでないこと");
		assertEquals(13, rows.size(), "13行（12月分 + 合計行）であること");

		// ========== データ行の検証（代表: 1月・6月・12月）==========

		// 1月（奇数月）
		AnnualSummaryRowItem row1 = rows.get(0);
		assertEquals("1月",       row1.getMonthLabel(),       "1月: monthLabel");
		assertEquals("01",        row1.getDetailMonth(),      "1月: detailMonth");
		assertFalse(row1.isEvenMonth(),                       "1月: evenMonth=false（奇数月）");
		assertFalse(row1.isTotalRow(),                        "1月: totalRow=false");
		assertEquals("88,000円",  row1.getSeikatsuhi(),       "1月: SEIKATSUHI");
		assertEquals("16,520円",  row1.getHikozei(),          "1月: HIKOZEI");
		assertEquals("20,000円",  row1.getTsumitateToushi(),  "1月: TSUMITATE_TOUSHI");
		assertEquals("ー",        row1.getJigyouKeihi(),      "1月: JIGYOU_KEIHI=0→ー");
		assertEquals("ー",        row1.getTsumitateKin(),     "1月: TSUMITATE_KIN=0→ー");
		assertEquals("ー",        row1.getIruiJukyo(),        "1月: IRUI_JUKYO=0→ー");
		assertEquals("ー",        row1.getInshoku(),          "1月: INSHOKU=0→ー");
		assertEquals("ー",        row1.getShumiGoraku(),      "1月: SHUMI=0→ー");
		assertEquals("124,520円", row1.getMonthTotal(),       "1月: 月合計");

		// 6月（偶数月）
		AnnualSummaryRowItem row6 = rows.get(5);
		assertEquals("6月",       row6.getMonthLabel(),       "6月: monthLabel");
		assertEquals("06",        row6.getDetailMonth(),      "6月: detailMonth");
		assertTrue(row6.isEvenMonth(),                        "6月: evenMonth=true（偶数月）");
		assertFalse(row6.isTotalRow(),                        "6月: totalRow=false");
		assertEquals("88,000円",  row6.getSeikatsuhi(),       "6月: SEIKATSUHI");
		assertEquals("16,520円",  row6.getHikozei(),          "6月: HIKOZEI");
		assertEquals("20,000円",  row6.getTsumitateToushi(),  "6月: TSUMITATE_TOUSHI");
		assertEquals("124,520円", row6.getMonthTotal(),       "6月: 月合計");

		// 12月（偶数月）
		AnnualSummaryRowItem row12 = rows.get(11);
		assertEquals("12月",      row12.getMonthLabel(),      "12月: monthLabel");
		assertEquals("12",        row12.getDetailMonth(),     "12月: detailMonth");
		assertTrue(row12.isEvenMonth(),                       "12月: evenMonth=true（偶数月）");
		assertFalse(row12.isTotalRow(),                       "12月: totalRow=false");
		assertEquals("88,000円",  row12.getSeikatsuhi(),      "12月: SEIKATSUHI");
		assertEquals("124,520円", row12.getMonthTotal(),      "12月: 月合計");

		// ========== 合計行の検証 ==========
		AnnualSummaryRowItem totalRow = rows.get(12);
		assertEquals("合計",          totalRow.getMonthLabel(),      "合計行: monthLabel");
		assertNull(totalRow.getDetailMonth(),                         "合計行: detailMonthがnull");
		assertTrue(totalRow.isTotalRow(),                             "合計行: totalRow=true");
		assertFalse(totalRow.isEvenMonth(),                           "合計行: evenMonth=false");
		assertEquals("1,056,000円",   totalRow.getSeikatsuhi(),      "合計行: SEIKATSUHI年間合計");
		assertEquals("198,240円",     totalRow.getHikozei(),         "合計行: HIKOZEI年間合計");
		assertEquals("240,000円",     totalRow.getTsumitateToushi(), "合計行: TSUMITATE_TOUSHI年間合計");
		assertEquals("ー",            totalRow.getJigyouKeihi(),     "合計行: JIGYOU_KEIHI=0→ー");
		assertEquals("ー",            totalRow.getTsumitateKin(),    "合計行: TSUMITATE_KIN=0→ー");
		assertEquals("ー",            totalRow.getIruiJukyo(),       "合計行: IRUI_JUKYO=0→ー");
		assertEquals("ー",            totalRow.getInshoku(),         "合計行: INSHOKU=0→ー");
		assertEquals("ー",            totalRow.getShumiGoraku(),     "合計行: SHUMI=0→ー");
		assertEquals("1,494,240円",   totalRow.getMonthTotal(),      "合計行: 年間合計");
	}
}
