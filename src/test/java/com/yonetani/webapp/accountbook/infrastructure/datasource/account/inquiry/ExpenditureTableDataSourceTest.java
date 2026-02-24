/**
 * ExpenditureTableRepositoryのテストクラスです。
 * 支出テーブル:EXPENDITURE_TABLEのデータ登録・更新・論理削除の各種メソッドテストを実施します。
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
import java.time.LocalDate;
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

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.ExpenditureItem;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.ExpenditureTableRepository;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry.ExpenditureTableMapper;

/**
 *<pre>
 * ExpenditureTableRepositoryのテストクラスです。
 * 支出テーブル:EXPENDITURE_TABLEのデータ登録・更新・論理削除の各種メソッドテストを実施します。
 *
 * [テスト対象メソッド]
 * ・add()    : 支出テーブルへの新規登録(全カラムの登録データ確認)
 * ・update() : 支出テーブルの更新(更新対象カラムと非更新カラムの確認)
 * ・delete() : 支出テーブルの論理削除(DELETE_FLG=TRUEの確認)
 *
 * [テストの着眼点]
 * ・add   : 全カラムが正しく登録されること。一意制約違反が正しく発生すること。
 *           null可項目(EVENT_CODE, EXPENDITURE_DETAIL_CONTEXT, SIHARAI_DATE)の登録確認。
 * ・update: EXPENDITURE_NAME, EXPENDITURE_KUBUN, EXPENDITURE_DETAIL_CONTEXT, SIHARAI_DATE,
 *           EXPENDITURE_KINGAKUのみ更新されること。
 *           SISYUTU_ITEM_CODE, EVENT_CODE, EXPENDITURE_ESTIMATE_KINGAKUは更新されないこと。
 *           対象なしの場合0件が返ること。
 * ・delete: DELETE_FLG=TRUEに論理削除されること。その他カラムは変更されないこと。
 *           対象なしの場合0件が返ること。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
// MyBatis関連のコンフィグレーションをインジェクションします
@MybatisTest
// SpringBootアプリケーション設定ファイルにapplication-unit-test.ymlを設定
@ActiveProfiles("unit-test")
class ExpenditureTableDataSourceTest {

	// ExpenditureTableRepository
	private ExpenditureTableRepository repository;
	// ExpenditureTable mapper
	@Autowired
	private ExpenditureTableMapper mapper;
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
		repository = new ExpenditureTableDataSource(mapper);
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.inquiry.ExpenditureTableDataSource#add(com.yonetani.webapp.accountbook.domain.model.account.inquiry.ExpenditureItem)} のためのテスト・メソッド。
	 */
	@Test
	@DisplayName("add:支出テーブルへの新規登録テスト(全カラム確認)")
	void testAdd() {
		/* 全項目の登録チェック */
		// テストデータ：全項目あり
		ExpenditureItem addData = ExpenditureItem.from(
				"TEST-USER-ID", "2025", "12", "001", "0001", "0001",
				"食費（外食）", "1", "外食詳細", LocalDate.of(2025, 12, 10),
				new BigDecimal("12000.00"), new BigDecimal("10000.00"), false);
		// データ追加
		assertEquals(1, repository.add(addData), "登録データが1件であること");

		// 登録されたデータをJdbcTemplateでロード
		Map<String, Object> actualDataMap = jdbcTemplate.queryForMap(
				"SELECT * FROM EXPENDITURE_TABLE WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=? AND EXPENDITURE_CODE=?",
				"TEST-USER-ID", "2025", "12", "001");

		// 全カラムの登録データを確認
		assertEquals("TEST-USER-ID",               actualDataMap.get("USER_ID"),                      "ユーザID(USER_ID)が正しいこと");
		assertEquals("2025",                        actualDataMap.get("TARGET_YEAR"),                  "対象年(TARGET_YEAR)が正しいこと");
		assertEquals("12",                          actualDataMap.get("TARGET_MONTH"),                 "対象月(TARGET_MONTH)が正しいこと");
		assertEquals("001",                         actualDataMap.get("EXPENDITURE_CODE"),             "支出コード(EXPENDITURE_CODE)が正しいこと");
		assertEquals("0001",                        actualDataMap.get("SISYUTU_ITEM_CODE"),            "支出項目コード(SISYUTU_ITEM_CODE)が正しいこと");
		assertEquals("0001",                        actualDataMap.get("EVENT_CODE"),                   "イベントコード(EVENT_CODE)が正しいこと");
		assertEquals("食費（外食）",                actualDataMap.get("EXPENDITURE_NAME"),             "支出名称(EXPENDITURE_NAME)が正しいこと");
		assertEquals("1",                           actualDataMap.get("EXPENDITURE_KUBUN"),            "支出区分(EXPENDITURE_KUBUN)が正しいこと");
		assertEquals("外食詳細",                    actualDataMap.get("EXPENDITURE_DETAIL_CONTEXT"),   "支出詳細(EXPENDITURE_DETAIL_CONTEXT)が正しいこと");
		assertEquals(LocalDate.of(2025, 12, 10),    toLocalDate(actualDataMap.get("SIHARAI_DATE")),    "支払日(SIHARAI_DATE)が正しいこと");
		assertEquals(new BigDecimal("12000.00"),    actualDataMap.get("EXPENDITURE_ESTIMATE_KINGAKU"), "支出予定金額(EXPENDITURE_ESTIMATE_KINGAKU)が正しいこと");
		assertEquals(new BigDecimal("10000.00"),    actualDataMap.get("EXPENDITURE_KINGAKU"),          "支出金額(EXPENDITURE_KINGAKU)が正しいこと");
		assertEquals(false,                         actualDataMap.get("DELETE_FLG"),                   "削除フラグ(DELETE_FLG)が正しいこと");

		/* 同じデータを登録した場合、一意制約違反となること */
		assertThrows(DuplicateKeyException.class, () -> repository.add(addData),
				"同じデータを登録した場合、一意制約違反となること");

		/* null可項目の登録チェック(EVENT_CODE=null, EXPENDITURE_DETAIL_CONTEXT=null, SIHARAI_DATE=null) */
		ExpenditureItem addNullData = ExpenditureItem.from(
				"TEST-USER-ID", "2025", "12", "002", "0001", null,
				"食費（日用品）", "1", null, null,
				new BigDecimal("5000.00"), new BigDecimal("4800.00"), false);
		assertEquals(1, repository.add(addNullData), "null可項目あり:登録データが1件であること");

		// 登録されたデータをロード
		Map<String, Object> actualNullDataMap = jdbcTemplate.queryForMap(
				"SELECT * FROM EXPENDITURE_TABLE WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=? AND EXPENDITURE_CODE=?",
				"TEST-USER-ID", "2025", "12", "002");

		// null可項目の確認
		assertEquals("TEST-USER-ID",            actualNullDataMap.get("USER_ID"),                      "ユーザID(USER_ID)が正しいこと(null可)");
		assertEquals("002",                      actualNullDataMap.get("EXPENDITURE_CODE"),             "支出コード(EXPENDITURE_CODE)が正しいこと(null可)");
		assertEquals("0001",                     actualNullDataMap.get("SISYUTU_ITEM_CODE"),            "支出項目コード(SISYUTU_ITEM_CODE)が正しいこと(null可)");
		assertNull(actualNullDataMap.get("EVENT_CODE"),                                                  "イベントコード(EVENT_CODE)がNULLであること");
		assertEquals("食費（日用品）",           actualNullDataMap.get("EXPENDITURE_NAME"),             "支出名称(EXPENDITURE_NAME)が正しいこと(null可)");
		assertNull(actualNullDataMap.get("EXPENDITURE_DETAIL_CONTEXT"),                                  "支出詳細(EXPENDITURE_DETAIL_CONTEXT)がNULLであること");
		assertNull(actualNullDataMap.get("SIHARAI_DATE"),                                                "支払日(SIHARAI_DATE)がNULLであること");
		assertEquals(new BigDecimal("5000.00"), actualNullDataMap.get("EXPENDITURE_ESTIMATE_KINGAKU"),  "支出予定金額(EXPENDITURE_ESTIMATE_KINGAKU)が正しいこと(null可)");
		assertEquals(new BigDecimal("4800.00"), actualNullDataMap.get("EXPENDITURE_KINGAKU"),           "支出金額(EXPENDITURE_KINGAKU)が正しいこと(null可)");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.inquiry.ExpenditureTableDataSource#update(com.yonetani.webapp.accountbook.domain.model.account.inquiry.ExpenditureItem)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(value = "ExpenditureTableDataSourceUpdateTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	@DisplayName("update:支出テーブルの更新テスト(更新対象カラムと非更新カラムの確認)")
	void testUpdate() {
		/* 全項目の更新チェック */
		// 更新前のSISYUTU_ITEM_CODE, EVENT_CODE, EXPENDITURE_ESTIMATE_KINGAKUを取得(更新されないことを確認するため)
		Map<String, Object> beforeDataMap = jdbcTemplate.queryForMap(
				"SELECT SISYUTU_ITEM_CODE, EVENT_CODE, EXPENDITURE_ESTIMATE_KINGAKU FROM EXPENDITURE_TABLE "
				+ "WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=? AND EXPENDITURE_CODE=?",
				"TEST-USER-ID", "2025", "12", "001");

		// 更新データ：EXPENDITURE_NAME, EXPENDITURE_KUBUN, EXPENDITURE_DETAIL_CONTEXT, SIHARAI_DATE, EXPENDITURE_KINGAKUを変更
		// 意図的にSISYUTU_ITEM_CODE, EVENT_CODE, EXPENDITURE_ESTIMATE_KINGAKUに別の値をセット(更新されないことを確認)
		ExpenditureItem updateData = ExpenditureItem.from(
				"TEST-USER-ID", "2025", "12", "001", "9999", "9999",
				"更新後支出名", "2", "更新後詳細", LocalDate.of(2025, 12, 20),
				new BigDecimal("99999.00"), new BigDecimal("35000.00"), false);
		// データ更新(対象データあり)
		assertEquals(1, repository.update(updateData), "更新データが1件であること");

		// 更新されたデータをロード
		Map<String, Object> actualDataMap = jdbcTemplate.queryForMap(
				"SELECT * FROM EXPENDITURE_TABLE WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=? AND EXPENDITURE_CODE=?",
				"TEST-USER-ID", "2025", "12", "001");

		// 更新されたカラムの確認
		assertEquals("更新後支出名",                    actualDataMap.get("EXPENDITURE_NAME"),           "支出名称(EXPENDITURE_NAME)が更新されていること");
		assertEquals("2",                               actualDataMap.get("EXPENDITURE_KUBUN"),          "支出区分(EXPENDITURE_KUBUN)が更新されていること");
		assertEquals("更新後詳細",                      actualDataMap.get("EXPENDITURE_DETAIL_CONTEXT"), "支出詳細(EXPENDITURE_DETAIL_CONTEXT)が更新されていること");
		assertEquals(LocalDate.of(2025, 12, 20),        toLocalDate(actualDataMap.get("SIHARAI_DATE")),  "支払日(SIHARAI_DATE)が更新されていること");
		assertEquals(new BigDecimal("35000.00"),         actualDataMap.get("EXPENDITURE_KINGAKU"),        "支出金額(EXPENDITURE_KINGAKU)が更新されていること");
		// 更新されないカラムの確認
		assertEquals(beforeDataMap.get("SISYUTU_ITEM_CODE"),          actualDataMap.get("SISYUTU_ITEM_CODE"),
				"支出項目コード(SISYUTU_ITEM_CODE)が更新されていないこと");
		assertEquals(beforeDataMap.get("EVENT_CODE"),                  actualDataMap.get("EVENT_CODE"),
				"イベントコード(EVENT_CODE)が更新されていないこと");
		assertEquals(beforeDataMap.get("EXPENDITURE_ESTIMATE_KINGAKU"), actualDataMap.get("EXPENDITURE_ESTIMATE_KINGAKU"),
				"支出予定金額(EXPENDITURE_ESTIMATE_KINGAKU)が更新されていないこと");

		/* 対象データなしの場合、0件が返ること */
		ExpenditureItem notFoundData = ExpenditureItem.from(
				"TEST-USER-ID", "2025", "12", "999", "0001", null,
				"対象なし", "1", null, null,
				new BigDecimal("0.00"), new BigDecimal("0.00"), false);
		assertEquals(0, repository.update(notFoundData), "対象データなしの場合、0件であること");

		/* null可項目の更新チェック(EXPENDITURE_DETAIL_CONTEXT=null, SIHARAI_DATE=null) */
		ExpenditureItem updateNullData = ExpenditureItem.from(
				"TEST-USER-ID", "2025", "12", "001", "9999", "9999",
				"更新後支出名(null可)", "3", null, null,
				new BigDecimal("99999.00"), new BigDecimal("30000.00"), false);
		assertEquals(1, repository.update(updateNullData), "null可項目更新:更新データが1件であること");

		// 更新されたデータをロード
		Map<String, Object> actualNullDataMap = jdbcTemplate.queryForMap(
				"SELECT * FROM EXPENDITURE_TABLE WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=? AND EXPENDITURE_CODE=?",
				"TEST-USER-ID", "2025", "12", "001");

		// null可項目の確認
		assertEquals("更新後支出名(null可)", actualNullDataMap.get("EXPENDITURE_NAME"),           "支出名称(EXPENDITURE_NAME)がnull可更新されていること");
		assertNull(actualNullDataMap.get("EXPENDITURE_DETAIL_CONTEXT"),                           "支出詳細(EXPENDITURE_DETAIL_CONTEXT)がNULLに更新されていること");
		assertNull(actualNullDataMap.get("SIHARAI_DATE"),                                         "支払日(SIHARAI_DATE)がNULLに更新されていること");
		// 支出予定金額は依然更新されないこと
		assertEquals(beforeDataMap.get("EXPENDITURE_ESTIMATE_KINGAKU"), actualNullDataMap.get("EXPENDITURE_ESTIMATE_KINGAKU"),
				"支出予定金額(EXPENDITURE_ESTIMATE_KINGAKU)が更新されていないこと(null可更新後も)");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.inquiry.ExpenditureTableDataSource#delete(com.yonetani.webapp.accountbook.domain.model.account.inquiry.ExpenditureItem)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(value = "ExpenditureTableDataSourceDeleteTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	@DisplayName("delete:支出テーブルの論理削除テスト(DELETE_FLG=TRUEの確認)")
	void testDelete() {
		/* 論理削除チェック */
		// 削除対象データ
		ExpenditureItem deleteData = ExpenditureItem.from(
				"TEST-USER-ID", "2025", "12", "001", "0001", "0001",
				"削除対象支出名", "1", "削除対象詳細", LocalDate.of(2025, 12, 5),
				new BigDecimal("50000.00"), new BigDecimal("40000.00"), false);
		// データ論理削除
		assertEquals(1, repository.delete(deleteData), "削除データが1件であること");

		// 削除後のデータをJdbcTemplateでロード
		Map<String, Object> actualDataMap = jdbcTemplate.queryForMap(
				"SELECT * FROM EXPENDITURE_TABLE WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=? AND EXPENDITURE_CODE=?",
				"TEST-USER-ID", "2025", "12", "001");

		// DELETE_FLG=TRUEであること
		assertEquals(true, actualDataMap.get("DELETE_FLG"), "削除フラグ(DELETE_FLG)がTRUEであること");
		// 論理削除のため他のカラムは変更されていないこと
		assertEquals("削除対象支出名",              actualDataMap.get("EXPENDITURE_NAME"),           "支出名称(EXPENDITURE_NAME)が変更されていないこと");
		assertEquals("1",                           actualDataMap.get("EXPENDITURE_KUBUN"),          "支出区分(EXPENDITURE_KUBUN)が変更されていないこと");
		assertEquals("削除対象詳細",                actualDataMap.get("EXPENDITURE_DETAIL_CONTEXT"), "支出詳細(EXPENDITURE_DETAIL_CONTEXT)が変更されていないこと");
		assertEquals(new BigDecimal("50000.00"),    actualDataMap.get("EXPENDITURE_ESTIMATE_KINGAKU"), "支出予定金額(EXPENDITURE_ESTIMATE_KINGAKU)が変更されていないこと");
		assertEquals(new BigDecimal("40000.00"),    actualDataMap.get("EXPENDITURE_KINGAKU"),        "支出金額(EXPENDITURE_KINGAKU)が変更されていないこと");

		/* 対象データなしの場合、0件が返ること */
		ExpenditureItem notFoundData = ExpenditureItem.from(
				"TEST-USER-ID", "2025", "12", "999", "0001", null,
				"対象なし", "1", null, null,
				new BigDecimal("0.00"), new BigDecimal("0.00"), false);
		assertEquals(0, repository.delete(notFoundData), "対象データなしの場合、0件であること");
	}

	/**
	 *<pre>
	 * JdbcTemplateのqueryForMapで取得したDATE型カラムの値(java.sql.Date)をLocalDateに変換して返します。
	 *</pre>
	 * @param value JdbcTemplateから取得したDATE型カラムの値
	 * @return LocalDate(valueがnullの場合はnull)
	 *
	 */
	private static LocalDate toLocalDate(Object value) {
		return value == null ? null : ((java.sql.Date) value).toLocalDate();
	}
}
