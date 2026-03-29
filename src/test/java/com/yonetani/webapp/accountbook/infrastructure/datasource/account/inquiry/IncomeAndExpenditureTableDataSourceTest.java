/**
 * IncomeAndExpenditureTableRepositoryのテストクラスです。
 * 収支テーブル:INCOME_AND_EXPENDITURE_TABLEのデータ登録・更新の各種メソッドテストを実施します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/02/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.inquiry;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditureItem;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeAndExpenditureTableRepository;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry.IncomeAndExpenditureTableMapper;

/**
 *<pre>
 * IncomeAndExpenditureTableRepositoryのテストクラスです。
 * 収支テーブル:INCOME_AND_EXPENDITURE_TABLEのデータ登録・更新の各種メソッドテストを実施します。
 *
 * [テスト対象メソッド]
 * ・add()    : 収支テーブルへの新規登録(全カラムの登録データ確認)
 * ・update() : 収支テーブルの更新(更新対象カラムと非更新カラムの確認)
 * ※deleteメソッドはこのリポジトリーには存在しません。
 *
 * [テストの着眼点]
 * ・add   : 全カラムが正しく登録されること。一意制約違反が正しく発生すること。
 *           null可項目(WITHDREW_KINGAKU)の登録確認。
 * ・update: INCOME_KINGAKU, WITHDREW_KINGAKU, EXPENDITURE_KINGAKU, INCOME_AND_EXPENDITURE_KINGAKUのみ更新されること。
 *           EXPENDITURE_ESTIMATE_KINGAKUは更新されないこと。
 *           対象なしの場合0件が返ること。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
// MyBatis関連のコンフィグレーションをインジェクションします
@MybatisTest
// SpringBootアプリケーション設定ファイルにapplication-unit-test.ymlを設定
@ActiveProfiles("unit-test")
class IncomeAndExpenditureTableDataSourceTest {

	// IncomeAndExpenditureTableRepository
	private IncomeAndExpenditureTableRepository repository;
	// IncomeAndExpenditureTable mapper
	@Autowired
	private IncomeAndExpenditureTableMapper mapper;
	// DBアクセス
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 *<pre>
	 * セットアップ時の処理
	 *</pre>
	 * @throws java.lang.Exception
	 *
	 */
	@BeforeEach
	void setUp() throws Exception {
		// テスト対象のリポジトリーをテストごとに作成
		repository = new IncomeAndExpenditureTableDataSource(mapper);
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.inquiry.IncomeAndExpenditureTableDataSource#add(com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditureItem)} のためのテスト・メソッド。
	 */
	@Test
	@DisplayName("add:収支テーブルへの新規登録テスト(全カラム確認)")
	void testAdd() {
		/* 全項目の登録チェック */
		// テストデータ：全項目あり(WITHDREW_KINGAKU=15000:取崩しあり)
		// 収支金額 = (380000 + 15000) - 38000 = 357000
		IncomeAndExpenditureItem addData = IncomeAndExpenditureItem.from(
				"TEST-USER-ID", "2025", "12",
				new BigDecimal("380000.00"), new BigDecimal("15000.00"),
				new BigDecimal("50000.00"), new BigDecimal("38000.00"),
				new BigDecimal("357000.00"));
		// データ追加
		assertEquals(1, repository.add(addData), "登録データが1件であること");

		// 登録されたデータをJdbcTemplateでロード
		Map<String, Object> actualDataMap = jdbcTemplate.queryForMap(
				"SELECT * FROM INCOME_AND_EXPENDITURE_TABLE WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=?",
				"TEST-USER-ID", "2025", "12");

		// 全カラムの登録データを確認
		assertEquals("TEST-USER-ID",               actualDataMap.get("USER_ID"),                          "ユーザID(USER_ID)が正しいこと");
		assertEquals("2025",                        actualDataMap.get("TARGET_YEAR"),                     "対象年(TARGET_YEAR)が正しいこと");
		assertEquals("12",                          actualDataMap.get("TARGET_MONTH"),                    "対象月(TARGET_MONTH)が正しいこと");
		assertEquals(new BigDecimal("380000.00"),   actualDataMap.get("INCOME_KINGAKU"),                  "収入金額(INCOME_KINGAKU)が正しいこと");
		assertEquals(new BigDecimal("15000.00"),    actualDataMap.get("WITHDREW_KINGAKU"),                "積立金取崩金額(WITHDREW_KINGAKU)が正しいこと");
		assertEquals(new BigDecimal("50000.00"),    actualDataMap.get("EXPENDITURE_ESTIMATE_KINGAKU"),    "支出予定金額(EXPENDITURE_ESTIMATE_KINGAKU)が正しいこと");
		assertEquals(new BigDecimal("38000.00"),    actualDataMap.get("EXPENDITURE_KINGAKU"),             "支出金額(EXPENDITURE_KINGAKU)が正しいこと");
		assertEquals(new BigDecimal("357000.00"),   actualDataMap.get("INCOME_AND_EXPENDITURE_KINGAKU"),  "収支金額(INCOME_AND_EXPENDITURE_KINGAKU)が正しいこと");

		/* 同じデータを登録した場合、一意制約違反となること */
		assertThrows(DuplicateKeyException.class, () -> repository.add(addData),
				"同じデータを登録した場合、一意制約違反となること");

		/* null可項目の登録チェック(WITHDREW_KINGAKU=null:取崩しなし) */
		// 収支金額 = 250000 - 30000 = 220000
		IncomeAndExpenditureItem addNullData = IncomeAndExpenditureItem.from(
				"TEST-USER-ID", "2025", "11",
				new BigDecimal("250000.00"), null,
				new BigDecimal("40000.00"), new BigDecimal("30000.00"),
				new BigDecimal("220000.00"));
		assertEquals(1, repository.add(addNullData), "null可項目あり(WITHDREW_KINGAKU=null):登録データが1件であること");

		// 登録されたデータをロード
		Map<String, Object> actualNullDataMap = jdbcTemplate.queryForMap(
				"SELECT * FROM INCOME_AND_EXPENDITURE_TABLE WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=?",
				"TEST-USER-ID", "2025", "11");

		// null可項目の確認
		assertEquals("TEST-USER-ID",             actualNullDataMap.get("USER_ID"),                        "ユーザID(USER_ID)が正しいこと(null可)");
		assertEquals(new BigDecimal("250000.00"), actualNullDataMap.get("INCOME_KINGAKU"),                 "収入金額(INCOME_KINGAKU)が正しいこと(null可)");
		assertNull(actualNullDataMap.get("WITHDREW_KINGAKU"),                                              "積立金取崩金額(WITHDREW_KINGAKU)がNULLであること");
		assertEquals(new BigDecimal("40000.00"), actualNullDataMap.get("EXPENDITURE_ESTIMATE_KINGAKU"),    "支出予定金額(EXPENDITURE_ESTIMATE_KINGAKU)が正しいこと(null可)");
		assertEquals(new BigDecimal("30000.00"), actualNullDataMap.get("EXPENDITURE_KINGAKU"),             "支出金額(EXPENDITURE_KINGAKU)が正しいこと(null可)");
		assertEquals(new BigDecimal("220000.00"), actualNullDataMap.get("INCOME_AND_EXPENDITURE_KINGAKU"), "収支金額(INCOME_AND_EXPENDITURE_KINGAKU)が正しいこと(null可)");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.inquiry.IncomeAndExpenditureTableDataSource#update(com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditureItem)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(value = "IncomeAndExpenditureTableDataSourceUpdateTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	@DisplayName("update:収支テーブルの更新テスト(EXPENDITURE_ESTIMATE_KINGAKUが更新されないことを確認)")
	void testUpdate() {
		/* 全項目の更新チェック */
		// 更新前のEXPENDITURE_ESTIMATE_KINGAKUを取得(更新されないことを確認するため)
		BigDecimal beforeEstimateKingaku = jdbcTemplate.queryForObject(
				"SELECT EXPENDITURE_ESTIMATE_KINGAKU FROM INCOME_AND_EXPENDITURE_TABLE "
				+ "WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=?",
				BigDecimal.class, "TEST-USER-ID", "2025", "12");

		// 更新データ：INCOME_KINGAKU, WITHDREW_KINGAKU, EXPENDITURE_KINGAKU, INCOME_AND_EXPENDITURE_KINGAKUを変更
		// 意図的にEXPENDITURE_ESTIMATE_KINGAKUに別の値をセット(更新されないことを確認)
		// 収支金額 = (375000 + 55000) - 69500 = 360500
		IncomeAndExpenditureItem updateData = IncomeAndExpenditureItem.from(
				"TEST-USER-ID", "2025", "12",
				new BigDecimal("375000.00"), new BigDecimal("55000.00"),
				new BigDecimal("99999.00"), new BigDecimal("69500.00"),
				new BigDecimal("360500.00"));
		// データ更新(対象データあり)
		assertEquals(1, repository.update(updateData), "更新データが1件であること");

		// 更新されたデータをロード
		Map<String, Object> actualDataMap = jdbcTemplate.queryForMap(
				"SELECT * FROM INCOME_AND_EXPENDITURE_TABLE WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=?",
				"TEST-USER-ID", "2025", "12");

		// 更新されたカラムの確認
		assertEquals(new BigDecimal("375000.00"),  actualDataMap.get("INCOME_KINGAKU"),                  "収入金額(INCOME_KINGAKU)が更新されていること");
		assertEquals(new BigDecimal("55000.00"),   actualDataMap.get("WITHDREW_KINGAKU"),                "積立金取崩金額(WITHDREW_KINGAKU)が更新されていること");
		assertEquals(new BigDecimal("69500.00"),   actualDataMap.get("EXPENDITURE_KINGAKU"),             "支出金額(EXPENDITURE_KINGAKU)が更新されていること");
		assertEquals(new BigDecimal("360500.00"),  actualDataMap.get("INCOME_AND_EXPENDITURE_KINGAKU"),  "収支金額(INCOME_AND_EXPENDITURE_KINGAKU)が更新されていること");
		// 更新されないカラムの確認: EXPENDITURE_ESTIMATE_KINGAKUは更新対象外
		assertEquals(beforeEstimateKingaku,         actualDataMap.get("EXPENDITURE_ESTIMATE_KINGAKU"),
				"支出予定金額(EXPENDITURE_ESTIMATE_KINGAKU)が更新されていないこと");

		/* 対象データなしの場合、0件が返ること */
		IncomeAndExpenditureItem notFoundData = IncomeAndExpenditureItem.from(
				"TEST-USER-ID", "2025", "01",
				new BigDecimal("0.00"), null,
				new BigDecimal("0.00"), new BigDecimal("0.00"),
				new BigDecimal("0.00"));
		assertEquals(0, repository.update(notFoundData), "対象データなしの場合、0件であること");

		/* null可項目の更新チェック(WITHDREW_KINGAKU=nullに更新) */
		// 収支金額 = 350000 - 40000 = 310000
		IncomeAndExpenditureItem updateNullData = IncomeAndExpenditureItem.from(
				"TEST-USER-ID", "2025", "12",
				new BigDecimal("350000.00"), null,
				new BigDecimal("99999.00"), new BigDecimal("40000.00"),
				new BigDecimal("310000.00"));
		assertEquals(1, repository.update(updateNullData), "null可項目更新(WITHDREW_KINGAKU=null):更新データが1件であること");

		// 更新されたデータをロード
		Map<String, Object> actualNullDataMap = jdbcTemplate.queryForMap(
				"SELECT * FROM INCOME_AND_EXPENDITURE_TABLE WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=?",
				"TEST-USER-ID", "2025", "12");

		// null可項目の確認
		assertEquals(new BigDecimal("350000.00"),  actualNullDataMap.get("INCOME_KINGAKU"),                 "収入金額(INCOME_KINGAKU)が更新されていること(null可)");
		assertNull(actualNullDataMap.get("WITHDREW_KINGAKU"),                                               "積立金取崩金額(WITHDREW_KINGAKU)がNULLに更新されていること");
		assertEquals(new BigDecimal("40000.00"),   actualNullDataMap.get("EXPENDITURE_KINGAKU"),            "支出金額(EXPENDITURE_KINGAKU)が更新されていること(null可)");
		assertEquals(new BigDecimal("310000.00"),  actualNullDataMap.get("INCOME_AND_EXPENDITURE_KINGAKU"), "収支金額(INCOME_AND_EXPENDITURE_KINGAKU)が更新されていること(null可)");
		// EXPENDITURE_ESTIMATE_KINGAKUはnull可更新後も変わらないこと
		assertEquals(beforeEstimateKingaku, actualNullDataMap.get("EXPENDITURE_ESTIMATE_KINGAKU"),
				"支出予定金額(EXPENDITURE_ESTIMATE_KINGAKU)が更新されていないこと(null可更新後も)");
	}
}
