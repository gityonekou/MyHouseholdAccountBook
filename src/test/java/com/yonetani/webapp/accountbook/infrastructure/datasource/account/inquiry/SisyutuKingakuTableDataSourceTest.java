/**
 * SisyutuKingakuTableRepositoryのテストクラスです。
 * 支出金額テーブル:SISYUTU_KINGAKU_TABLEのデータ登録・更新の各種メソッドテストを実施します。
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

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuKingakuItem;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.SisyutuKingakuTableRepository;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry.IncomeAndExpenditureTableMapper;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry.SisyutuKingakuTableMapper;

/**
 *<pre>
 * SisyutuKingakuTableRepositoryのテストクラスです。
 * 支出金額テーブル:SISYUTU_KINGAKU_TABLEのデータ登録・更新の各種メソッドテストを実施します。
 *
 * [テスト対象メソッド]
 * ・add()    : 支出金額テーブルへの新規登録(全カラムの登録データ確認)
 * ・update() : 支出金額テーブルの更新(更新対象カラムと非更新カラムの確認)
 * ※deleteメソッドはこのリポジトリーには存在しません。
 *
 * [テストの着眼点]
 * ・add   : 全カラムが正しく登録されること。一意制約違反が正しく発生すること。
 *           null可項目(SISYUTU_KINGAKU_B, SISYUTU_KINGAKU_C, SISYUTU_SIHARAI_DATE)の登録確認。
 * ・update: SISYUTU_KINGAKU, SISYUTU_KINGAKU_B, SISYUTU_KINGAKU_C, SISYUTU_SIHARAI_DATEのみ更新されること。
 *           SISYUTU_YOTEI_KINGAKU, PARENT_SISYUTU_ITEM_CODEは更新されないこと。
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
class SisyutuKingakuTableDataSourceTest {

	// SisyutuKingakuTableRepository
	private SisyutuKingakuTableRepository repository;
	// IncomeAndExpenditureTable mapper(SisyutuKingakuTableDataSourceが内部で使用)
	@Autowired
	private IncomeAndExpenditureTableMapper incomeAndExpenditureMapper;
	// SisyutuKingakuTable mapper
	@Autowired
	private SisyutuKingakuTableMapper sisyutuKingakuTableMapper;
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
		// SisyutuKingakuTableDataSourceはincomeAndExpenditureMapperとsisyutuKingakuTableMapperの2つのマッパーが必要
		repository = new SisyutuKingakuTableDataSource(incomeAndExpenditureMapper, sisyutuKingakuTableMapper);
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.inquiry.SisyutuKingakuTableDataSource#add(com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuKingakuItem)} のためのテスト・メソッド。
	 */
	@Test
	@DisplayName("add:支出金額テーブルへの新規登録テスト(全カラム確認)")
	void testAdd() {
		/* 全項目の登録チェック */
		// テストデータ：全項目あり(SISYUTU_KINGAKU_B, SISYUTU_KINGAKU_C, SISYUTU_SIHARAI_DATEあり)
		SisyutuKingakuItem addData = SisyutuKingakuItem.from(
				"TEST-USER-ID", "2025", "12", "0001", "0000",
				new BigDecimal("50000.00"), new BigDecimal("45000.00"),
				new BigDecimal("5000.00"), new BigDecimal("3000.00"),
				LocalDate.of(2025, 12, 25));
		// データ追加
		assertEquals(1, repository.add(addData), "登録データが1件であること");

		// 登録されたデータをJdbcTemplateでロード
		Map<String, Object> actualDataMap = jdbcTemplate.queryForMap(
				"SELECT * FROM SISYUTU_KINGAKU_TABLE WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=? AND SISYUTU_ITEM_CODE=?",
				"TEST-USER-ID", "2025", "12", "0001");

		// 全カラムの登録データを確認
		assertEquals("TEST-USER-ID",             actualDataMap.get("USER_ID"),                  "ユーザID(USER_ID)が正しいこと");
		assertEquals("2025",                      actualDataMap.get("TARGET_YEAR"),              "対象年(TARGET_YEAR)が正しいこと");
		assertEquals("12",                        actualDataMap.get("TARGET_MONTH"),             "対象月(TARGET_MONTH)が正しいこと");
		assertEquals("0001",                      actualDataMap.get("SISYUTU_ITEM_CODE"),        "支出項目コード(SISYUTU_ITEM_CODE)が正しいこと");
		assertEquals("0000",                      actualDataMap.get("PARENT_SISYUTU_ITEM_CODE"), "親支出項目コード(PARENT_SISYUTU_ITEM_CODE)が正しいこと");
		assertEquals(new BigDecimal("50000.00"),  actualDataMap.get("SISYUTU_YOTEI_KINGAKU"),    "支出予定金額(SISYUTU_YOTEI_KINGAKU)が正しいこと");
		assertEquals(new BigDecimal("45000.00"),  actualDataMap.get("SISYUTU_KINGAKU"),          "支出金額(SISYUTU_KINGAKU)が正しいこと");
		assertEquals(new BigDecimal("5000.00"),   actualDataMap.get("SISYUTU_KINGAKU_B"),        "支出金額B(SISYUTU_KINGAKU_B)が正しいこと");
		assertEquals(new BigDecimal("3000.00"),   actualDataMap.get("SISYUTU_KINGAKU_C"),        "支出金額C(SISYUTU_KINGAKU_C)が正しいこと");
		assertEquals(LocalDate.of(2025, 12, 25),  toLocalDate(actualDataMap.get("SISYUTU_SIHARAI_DATE")), "支出支払日(SISYUTU_SIHARAI_DATE)が正しいこと");

		/* 同じデータを登録した場合、一意制約違反となること */
		assertThrows(DuplicateKeyException.class, () -> repository.add(addData),
				"同じデータを登録した場合、一意制約違反となること");

		/* null可項目の登録チェック(SISYUTU_KINGAKU_B=null, SISYUTU_KINGAKU_C=null, SISYUTU_SIHARAI_DATE=null) */
		SisyutuKingakuItem addNullData = SisyutuKingakuItem.from(
				"TEST-USER-ID", "2025", "12", "0002", "0000",
				new BigDecimal("30000.00"), new BigDecimal("28000.00"),
				null, null, null);
		assertEquals(1, repository.add(addNullData), "null可項目あり:登録データが1件であること");

		// 登録されたデータをロード
		Map<String, Object> actualNullDataMap = jdbcTemplate.queryForMap(
				"SELECT * FROM SISYUTU_KINGAKU_TABLE WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=? AND SISYUTU_ITEM_CODE=?",
				"TEST-USER-ID", "2025", "12", "0002");

		// null可項目の確認
		assertEquals("TEST-USER-ID",             actualNullDataMap.get("USER_ID"),                  "ユーザID(USER_ID)が正しいこと(null可)");
		assertEquals("0002",                      actualNullDataMap.get("SISYUTU_ITEM_CODE"),        "支出項目コード(SISYUTU_ITEM_CODE)が正しいこと(null可)");
		assertEquals("0000",                      actualNullDataMap.get("PARENT_SISYUTU_ITEM_CODE"), "親支出項目コード(PARENT_SISYUTU_ITEM_CODE)が正しいこと(null可)");
		assertEquals(new BigDecimal("30000.00"),  actualNullDataMap.get("SISYUTU_YOTEI_KINGAKU"),    "支出予定金額(SISYUTU_YOTEI_KINGAKU)が正しいこと(null可)");
		assertEquals(new BigDecimal("28000.00"),  actualNullDataMap.get("SISYUTU_KINGAKU"),          "支出金額(SISYUTU_KINGAKU)が正しいこと(null可)");
		assertNull(actualNullDataMap.get("SISYUTU_KINGAKU_B"),                                       "支出金額B(SISYUTU_KINGAKU_B)がNULLであること");
		assertNull(actualNullDataMap.get("SISYUTU_KINGAKU_C"),                                       "支出金額C(SISYUTU_KINGAKU_C)がNULLであること");
		assertNull(actualNullDataMap.get("SISYUTU_SIHARAI_DATE"),                                    "支出支払日(SISYUTU_SIHARAI_DATE)がNULLであること");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.inquiry.SisyutuKingakuTableDataSource#update(com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuKingakuItem)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(value = "SisyutuKingakuTableDataSourceUpdateTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	@DisplayName("update:支出金額テーブルの更新テスト(SISYUTU_YOTEI_KINGAKUとPARENT_SISYUTU_ITEM_CODEが更新されないことを確認)")
	void testUpdate() {
		/* 全項目の更新チェック */
		// 更新前のSISYUTU_YOTEI_KINGAKUとPARENT_SISYUTU_ITEM_CODEを取得(更新されないことを確認するため)
		Map<String, Object> beforeDataMap = jdbcTemplate.queryForMap(
				"SELECT SISYUTU_YOTEI_KINGAKU, PARENT_SISYUTU_ITEM_CODE FROM SISYUTU_KINGAKU_TABLE "
				+ "WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=? AND SISYUTU_ITEM_CODE=?",
				"TEST-USER-ID", "2025", "12", "0001");

		// 更新データ：SISYUTU_KINGAKU, SISYUTU_KINGAKU_B, SISYUTU_KINGAKU_C, SISYUTU_SIHARAI_DATEを変更
		// 意図的にSISYUTU_YOTEI_KINGAKUとPARENT_SISYUTU_ITEM_CODEに別の値をセット(更新されないことを確認)
		SisyutuKingakuItem updateData = SisyutuKingakuItem.from(
				"TEST-USER-ID", "2025", "12", "0001", "9999",
				new BigDecimal("99999.00"), new BigDecimal("48000.00"),
				new BigDecimal("6000.00"), new BigDecimal("2000.00"),
				LocalDate.of(2025, 12, 20));
		// データ更新(対象データあり)
		assertEquals(1, repository.update(updateData), "更新データが1件であること");

		// 更新されたデータをロード
		Map<String, Object> actualDataMap = jdbcTemplate.queryForMap(
				"SELECT * FROM SISYUTU_KINGAKU_TABLE WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=? AND SISYUTU_ITEM_CODE=?",
				"TEST-USER-ID", "2025", "12", "0001");

		// 更新されたカラムの確認
		assertEquals(new BigDecimal("48000.00"),  actualDataMap.get("SISYUTU_KINGAKU"),       "支出金額(SISYUTU_KINGAKU)が更新されていること");
		assertEquals(new BigDecimal("6000.00"),   actualDataMap.get("SISYUTU_KINGAKU_B"),     "支出金額B(SISYUTU_KINGAKU_B)が更新されていること");
		assertEquals(new BigDecimal("2000.00"),   actualDataMap.get("SISYUTU_KINGAKU_C"),     "支出金額C(SISYUTU_KINGAKU_C)が更新されていること");
		assertEquals(LocalDate.of(2025, 12, 20),  toLocalDate(actualDataMap.get("SISYUTU_SIHARAI_DATE")), "支出支払日(SISYUTU_SIHARAI_DATE)が更新されていること");
		// 更新されないカラムの確認
		assertEquals(beforeDataMap.get("SISYUTU_YOTEI_KINGAKU"),      actualDataMap.get("SISYUTU_YOTEI_KINGAKU"),
				"支出予定金額(SISYUTU_YOTEI_KINGAKU)が更新されていないこと");
		assertEquals(beforeDataMap.get("PARENT_SISYUTU_ITEM_CODE"),    actualDataMap.get("PARENT_SISYUTU_ITEM_CODE"),
				"親支出項目コード(PARENT_SISYUTU_ITEM_CODE)が更新されていないこと");

		/* 対象データなしの場合、0件が返ること */
		SisyutuKingakuItem notFoundData = SisyutuKingakuItem.from(
				"TEST-USER-ID", "2025", "12", "9999", "0000",
				new BigDecimal("0.00"), new BigDecimal("0.00"),
				null, null, null);
		assertEquals(0, repository.update(notFoundData), "対象データなしの場合、0件であること");

		/* null可項目の更新チェック(SISYUTU_KINGAKU_B=null, SISYUTU_KINGAKU_C=null, SISYUTU_SIHARAI_DATE=null) */
		SisyutuKingakuItem updateNullData = SisyutuKingakuItem.from(
				"TEST-USER-ID", "2025", "12", "0001", "9999",
				new BigDecimal("99999.00"), new BigDecimal("42000.00"),
				null, null, null);
		assertEquals(1, repository.update(updateNullData), "null可項目更新:更新データが1件であること");

		// 更新されたデータをロード
		Map<String, Object> actualNullDataMap = jdbcTemplate.queryForMap(
				"SELECT * FROM SISYUTU_KINGAKU_TABLE WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=? AND SISYUTU_ITEM_CODE=?",
				"TEST-USER-ID", "2025", "12", "0001");

		// null可項目の確認
		assertEquals(new BigDecimal("42000.00"),  actualNullDataMap.get("SISYUTU_KINGAKU"),   "支出金額(SISYUTU_KINGAKU)が更新されていること(null可)");
		assertNull(actualNullDataMap.get("SISYUTU_KINGAKU_B"),                                "支出金額B(SISYUTU_KINGAKU_B)がNULLに更新されていること");
		assertNull(actualNullDataMap.get("SISYUTU_KINGAKU_C"),                                "支出金額C(SISYUTU_KINGAKU_C)がNULLに更新されていること");
		assertNull(actualNullDataMap.get("SISYUTU_SIHARAI_DATE"),                             "支出支払日(SISYUTU_SIHARAI_DATE)がNULLに更新されていること");
		// SISYUTU_YOTEI_KINGAKUはnull可更新後も変わらないこと
		assertEquals(beforeDataMap.get("SISYUTU_YOTEI_KINGAKU"),  actualNullDataMap.get("SISYUTU_YOTEI_KINGAKU"),
				"支出予定金額(SISYUTU_YOTEI_KINGAKU)が更新されていないこと(null可更新後も)");
		// PARENT_SISYUTU_ITEM_CODEはnull可更新後も変わらないこと
		assertEquals(beforeDataMap.get("PARENT_SISYUTU_ITEM_CODE"), actualNullDataMap.get("PARENT_SISYUTU_ITEM_CODE"),
				"親支出項目コード(PARENT_SISYUTU_ITEM_CODE)が更新されていないこと(null可更新後も)");
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
