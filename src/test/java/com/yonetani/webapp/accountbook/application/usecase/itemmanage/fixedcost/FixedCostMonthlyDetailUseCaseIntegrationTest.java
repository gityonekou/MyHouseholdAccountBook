/**
 * 月別固定費一覧ユースケースの統合テストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/05/27 : 1.01.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage.fixedcost;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import com.yonetani.webapp.accountbook.presentation.response.itemmanage.fixedcost.AbstractFixedCostItemListResponse.FixedCostItem;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.fixedcost.FixedCostMonthlyDetailResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

/**
 *<pre>
 * 月別固定費一覧ユースケースの統合テストクラスです。
 *
 * [対象メソッド]
 * 1. readMonthlyDetail - 月別固定費一覧画面の表示情報を取得する
 *
 * [テストシナリオ]
 * ① 正常系：month="11"（奇数月） → 4件・DB順・合計・月ナビ確認
 * ② 正常系：month="12"（偶数月・年末ラップ） → 4件・DB順・合計・nextMonth=01確認
 * ③ 正常系：month=null → DB の NOW_TARGET_MONTH=11 を使用して11月表示
 * ④ 正常系：month="01"（年初ラップ） → prevMonth=12・displayMonthLabel=1月（先頭0なし）
 * ⑤ 正常系：month="05"（奇数月） → displayMonthLabel=5月（先頭0なし）・月ナビ確認
 *
 * [テストデータ] FixedCostInquiryIntegrationTest.sql（UseCase テストと共用）
 * user01: NOW_TARGET_MONTH=11、固定費5件
 *   DB取得順: 0003(奇数月20), 0001(毎月00), 0002(毎月00), 0005(偶数月30), 0004(任意40)
 *
 * [月別フィルタ結果]
 *   奇数月(01,03,05,07,09,11): [0003,0001,0002,0004] 4件 合計=98,590円
 *   偶数月(02,04,06,08,10,12): [0001,0002,0005,0004] 4件 合計=90,000円
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
	"/com/yonetani/webapp/accountbook/application/usecase/itemmanage/fixedcost/FixedCostInquiryIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("月別固定費一覧ユースケースの統合テスト")
class FixedCostMonthlyDetailUseCaseIntegrationTest {

	@Autowired
	private FixedCostMonthlyDetailUseCase useCase;

	private final LoginUserInfo USER01 = LoginUserInfo.from("user01", "テストユーザ01");

	/**
	 *<pre>
	 * テスト①：month="11"（奇数月）→ 4件・DB取得順・合計・月ナビが正しいこと
	 *
	 * 【検証内容】
	 * ・displayMonthLabel="11月", currentMonth="11", prevMonth="10", nextMonth="12"
	 * ・4件: [0003国民年金保険, 0001家賃, 0002電気代概算, 0004その他任意テスト]
	 * ・monthlyTotal="98,590円"
	 *</pre>
	 */
	@Test
	@DisplayName("① month=11（奇数月） → 4件・DB順・合計・月ナビ確認")
	void testReadMonthlyDetail_奇数月11() {
		FixedCostMonthlyDetailResponse response = useCase.readMonthlyDetail(USER01, "11");

		// 月ラベル・ナビ
		assertFalse(response.hasMessages(), "メッセージなし");
		assertEquals("11月", response.getDisplayMonthLabel(), "displayMonthLabel");
		assertEquals("11",   response.getCurrentMonth(),      "currentMonth");
		assertEquals("10",   response.getPrevMonth(),          "prevMonth");
		assertEquals("12",   response.getNextMonth(),          "nextMonth");

		// 4件・DB取得順の確認
		List<FixedCostItem> items = response.getFixedCostItemList();
		assertEquals(4, items.size(), "11月: 4件");
		assertEquals("0003", items.get(0).getFixedCostCode(), "index0=国民年金保険");
		assertEquals("0001", items.get(1).getFixedCostCode(), "index1=家賃");
		assertEquals("0002", items.get(2).getFixedCostCode(), "index2=電気代概算");
		assertEquals("0004", items.get(3).getFixedCostCode(), "index3=その他任意テスト");

		// 当月合計
		assertEquals("98,590円", response.getMonthlyTotal(), "11月合計");
	}

	/**
	 *<pre>
	 * テスト②：month="12"（偶数月）→ 4件・DB取得順・合計・月ナビが正しいこと
	 *
	 * 【検証内容】
	 * ・displayMonthLabel="12月", prevMonth="11", nextMonth="01"（年末ラップ）
	 * ・4件: [0001家賃, 0002電気代概算, 0005電気代夏季割増, 0004その他任意テスト]
	 * ・monthlyTotal="90,000円"
	 *</pre>
	 */
	@Test
	@DisplayName("② month=12（偶数月・年末ラップ） → 4件・DB順・合計・nextMonth=01確認")
	void testReadMonthlyDetail_偶数月12() {
		FixedCostMonthlyDetailResponse response = useCase.readMonthlyDetail(USER01, "12");

		// 月ラベル・ナビ（12月→次月は01月にラップ）
		assertFalse(response.hasMessages(), "メッセージなし");
		assertEquals("12月", response.getDisplayMonthLabel(), "displayMonthLabel");
		assertEquals("12",   response.getCurrentMonth(),      "currentMonth");
		assertEquals("11",   response.getPrevMonth(),          "prevMonth");
		assertEquals("01",   response.getNextMonth(),          "nextMonth（年末ラップ）");

		// 4件・DB取得順の確認（奇数月の0003は除外、偶数月の0005が含まれる）
		List<FixedCostItem> items = response.getFixedCostItemList();
		assertEquals(4, items.size(), "12月: 4件");
		assertEquals("0001", items.get(0).getFixedCostCode(), "index0=家賃");
		assertEquals("0002", items.get(1).getFixedCostCode(), "index1=電気代概算");
		assertEquals("0005", items.get(2).getFixedCostCode(), "index2=電気代夏季割増");
		assertEquals("0004", items.get(3).getFixedCostCode(), "index3=その他任意テスト");

		// 当月合計
		assertEquals("90,000円", response.getMonthlyTotal(), "12月合計");
	}

	/**
	 *<pre>
	 * テスト③：month=null → DB の NOW_TARGET_MONTH=11 を使用し11月と同じ結果になること
	 *
	 * 【検証内容】
	 * ・month が null の場合、DB の現在対象月（11月）を使用すること
	 * ・currentMonth="11", displayMonthLabel="11月"
	 * ・4件、合計=98,590円（テスト①と同じ結果）
	 *</pre>
	 */
	@Test
	@DisplayName("③ month=null → DB の NOW_TARGET_MONTH=11 を使用して11月表示")
	void testReadMonthlyDetail_月パラメータなし() {
		FixedCostMonthlyDetailResponse response = useCase.readMonthlyDetail(USER01, null);

		// NOW_TARGET_MONTH=11 のため11月として処理されること
		assertFalse(response.hasMessages(), "メッセージなし");
		assertEquals("11月", response.getDisplayMonthLabel(), "DB現在月=11月として表示");
		assertEquals("11",   response.getCurrentMonth(),      "currentMonth=11");

		// 4件・合計はテスト①と同じ
		List<FixedCostItem> items = response.getFixedCostItemList();
		assertEquals(4, items.size(), "4件");
		assertEquals("98,590円", response.getMonthlyTotal(), "合計=98,590円");
	}

	/**
	 *<pre>
	 * テスト④：month="01"（奇数月・年初ラップ）→ prevMonth="12" になること
	 *
	 * 【検証内容】
	 * ・1月の前月が12月にラップすること（prevMonth="12"）
	 * ・nextMonth="02"
	 * ・displayMonthLabel="1月"（先頭0なし）
	 * ・4件、合計=98,590円
	 *</pre>
	 */
	@Test
	@DisplayName("④ month=01（年初ラップ） → prevMonth=12・displayMonthLabel=1月（先頭0なし）")
	void testReadMonthlyDetail_年初ラップ() {
		FixedCostMonthlyDetailResponse response = useCase.readMonthlyDetail(USER01, "01");

		// 1月の前月は12月にラップ
		assertFalse(response.hasMessages(), "メッセージなし");
		assertEquals("1月", response.getDisplayMonthLabel(), "displayMonthLabel=1月（先頭0なし）");
		assertEquals("01",  response.getCurrentMonth(),      "currentMonth=01");
		assertEquals("12",  response.getPrevMonth(),          "prevMonth=12（年初ラップ）");
		assertEquals("02",  response.getNextMonth(),          "nextMonth=02");

		// 01月は奇数月: 0003+0001+0002+0004の4件
		List<FixedCostItem> items = response.getFixedCostItemList();
		assertEquals(4, items.size(), "01月: 4件");
		assertEquals("98,590円", response.getMonthlyTotal(), "01月合計");
	}

	/**
	 *<pre>
	 * テスト⑤：month="05"（奇数月）→ displayMonthLabel="5月"（先頭0なし）、月ナビ・合計が正しいこと
	 *
	 * 【検証内容】
	 * ・displayMonthLabel="5月"（"05月"ではなく先頭0なし）
	 * ・currentMonth="05", prevMonth="04", nextMonth="06"
	 * ・4件、合計=98,590円
	 *</pre>
	 */
	@Test
	@DisplayName("⑤ month=05（奇数月）→ displayMonthLabel=5月（先頭0なし）・月ナビ確認")
	void testReadMonthlyDetail_5月() {
		FixedCostMonthlyDetailResponse response = useCase.readMonthlyDetail(USER01, "05");

		assertFalse(response.hasMessages(), "メッセージなし");
		assertEquals("5月", response.getDisplayMonthLabel(), "displayMonthLabel=5月（先頭0なし）");
		assertEquals("05",  response.getCurrentMonth(),      "currentMonth=05");
		assertEquals("04",  response.getPrevMonth(),          "prevMonth=04");
		assertEquals("06",  response.getNextMonth(),          "nextMonth=06");

		// 5月は奇数月: 4件
		List<FixedCostItem> items = response.getFixedCostItemList();
		assertEquals(4, items.size(), "05月: 4件");
		assertEquals("98,590円", response.getMonthlyTotal(), "05月合計");
	}
}
