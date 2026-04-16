/**
 * IncomeTableRepositoryのテストクラスです。
 * 収入テーブル:INCOME_TABLEのデータ登録・更新・論理削除の各種メソッドテストを実施します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/02/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.income;

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

import com.yonetani.webapp.accountbook.domain.model.account.income.IncomeItem;
import com.yonetani.webapp.accountbook.domain.repository.account.income.IncomeTableRepository;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.income.IncomeTableMapper;

/**
 *<pre>
 * IncomeTableRepositoryのテストクラスです。
 * 収入テーブル:INCOME_TABLEのデータ登録・更新・論理削除の各種メソッドテストを実施します。
 *
 * [テスト対象メソッド]
 * ・add()    : 収入テーブルへの新規登録(全カラムの登録データ確認)
 * ・update() : 収入テーブルの更新(更新対象カラムと非更新カラムの確認)
 * ・delete() : 収入テーブルの論理削除(DELETE_FLG=TRUEの確認)
 *
 * [テストの着眼点]
 * ・add   : 全カラムが正しく登録されること。一意制約違反が正しく発生すること。
 * ・update: INCOME_KUBUN, INCOME_DETAIL_CONTEXT, INCOME_KINGAKUのみ更新されること。
 *           INCOME_CODE(PK)は変更されないこと。対象なしの場合0件が返ること。
 * ・delete: DELETE_FLG=TRUEに論理削除されること。その他カラムは変更されないこと。
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
class IncomeTableDataSourceTest {

	// IncomeTableRepository
	private IncomeTableRepository repository;
	// IncomeTable mapper
	@Autowired
	private IncomeTableMapper mapper;
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
		repository = new IncomeTableDataSource(mapper);
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.income.IncomeTableDataSource#add(com.yonetani.webapp.accountbook.domain.model.account.income.IncomeItem)} のためのテスト・メソッド。
	 */
	@Test
	@DisplayName("add:収入テーブルへの新規登録テスト(全カラム確認)")
	void testAdd() {
		/* 全項目の登録チェック */
		// テストデータ：全項目あり
		IncomeItem addData = IncomeItem.from(
				"TEST-USER-ID", "2025", "12", "01", "1", "給与収入詳細", new BigDecimal("380000.00"), false);
		// データ追加
		assertEquals(1, repository.add(addData), "登録データが1件であること");

		// 登録されたデータをJdbcTemplateでロード
		Map<String, Object> actualDataMap = jdbcTemplate.queryForMap(
				"SELECT * FROM INCOME_TABLE WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=? AND INCOME_CODE=?",
				"TEST-USER-ID", "2025", "12", "01");

		// 全カラムの登録データを確認
		assertEquals("TEST-USER-ID", actualDataMap.get("USER_ID"),         "ユーザID(USER_ID)が正しいこと");
		assertEquals("2025",          actualDataMap.get("TARGET_YEAR"),     "対象年(TARGET_YEAR)が正しいこと");
		assertEquals("12",            actualDataMap.get("TARGET_MONTH"),    "対象月(TARGET_MONTH)が正しいこと");
		assertEquals("01",            actualDataMap.get("INCOME_CODE"),     "収入コード(INCOME_CODE)が正しいこと");
		assertEquals("1",             actualDataMap.get("INCOME_KUBUN"),    "収入区分(INCOME_KUBUN)が正しいこと");
		assertEquals("給与収入詳細",  actualDataMap.get("INCOME_DETAIL_CONTEXT"), "収入詳細(INCOME_DETAIL_CONTEXT)が正しいこと");
		assertEquals(new BigDecimal("380000.00"), actualDataMap.get("INCOME_KINGAKU"), "収入金額(INCOME_KINGAKU)が正しいこと");
		assertEquals(false,           actualDataMap.get("DELETE_FLG"),      "削除フラグ(DELETE_FLG)が正しいこと");

		/* 同じデータを登録した場合、一意制約違反となること */
		assertThrows(DuplicateKeyException.class, () -> repository.add(addData),
				"同じデータを登録した場合、一意制約違反となること");

		/* null可項目の登録チェック(INCOME_DETAIL_CONTEXT=null) */
		IncomeItem addNullData = IncomeItem.from(
				"TEST-USER-ID", "2025", "12", "02", "2", null, new BigDecimal("15000.00"), false);
		// データ追加
		assertEquals(1, repository.add(addNullData), "null可項目あり:登録データが1件であること");

		// 登録されたデータをロード
		Map<String, Object> actualNullDataMap = jdbcTemplate.queryForMap(
				"SELECT * FROM INCOME_TABLE WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=? AND INCOME_CODE=?",
				"TEST-USER-ID", "2025", "12", "02");

		// null可項目の確認
		assertEquals("TEST-USER-ID",            actualNullDataMap.get("USER_ID"),         "ユーザID(USER_ID)が正しいこと(null可)");
		assertEquals("2025",                     actualNullDataMap.get("TARGET_YEAR"),     "対象年(TARGET_YEAR)が正しいこと(null可)");
		assertEquals("12",                       actualNullDataMap.get("TARGET_MONTH"),    "対象月(TARGET_MONTH)が正しいこと(null可)");
		assertEquals("02",                       actualNullDataMap.get("INCOME_CODE"),     "収入コード(INCOME_CODE)が正しいこと(null可)");
		assertEquals("2",                        actualNullDataMap.get("INCOME_KUBUN"),    "収入区分(INCOME_KUBUN)が正しいこと(null可)");
		assertNull(actualNullDataMap.get("INCOME_DETAIL_CONTEXT"),                         "収入詳細(INCOME_DETAIL_CONTEXT)がNULLであること");
		assertEquals(new BigDecimal("15000.00"), actualNullDataMap.get("INCOME_KINGAKU"),  "収入金額(INCOME_KINGAKU)が正しいこと(null可)");
		assertEquals(false,                      actualNullDataMap.get("DELETE_FLG"),      "削除フラグ(DELETE_FLG)が正しいこと(null可)");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.income.IncomeTableDataSource#update(com.yonetani.webapp.accountbook.domain.model.account.income.IncomeItem)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(value = "IncomeTableDataSourceUpdateTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	@DisplayName("update:収入テーブルの更新テスト(更新対象カラムと非更新カラムの確認)")
	void testUpdate() {
		/* 全項目の更新チェック */
		// 更新前の収入コードを取得(更新されないことを確認するため)
		String beforeIncomeCode = jdbcTemplate.queryForObject(
				"SELECT INCOME_CODE FROM INCOME_TABLE WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=? AND INCOME_CODE=?",
				String.class, "TEST-USER-ID", "2025", "12", "01");

		// 更新データ：INCOME_KUBUN, INCOME_DETAIL_CONTEXT, INCOME_KINGAKUを変更
		IncomeItem updateData = IncomeItem.from(
				"TEST-USER-ID", "2025", "12", "01", "2", "更新後詳細", new BigDecimal("200000.00"), false);
		// データ更新(対象データあり)
		assertEquals(1, repository.update(updateData), "更新データが1件であること");

		// 更新されたデータをロード
		Map<String, Object> actualDataMap = jdbcTemplate.queryForMap(
				"SELECT * FROM INCOME_TABLE WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=? AND INCOME_CODE=?",
				"TEST-USER-ID", "2025", "12", "01");

		// 更新されたカラムの確認
		assertEquals("2",             actualDataMap.get("INCOME_KUBUN"),          "収入区分(INCOME_KUBUN)が更新されていること");
		assertEquals("更新後詳細",    actualDataMap.get("INCOME_DETAIL_CONTEXT"), "収入詳細(INCOME_DETAIL_CONTEXT)が更新されていること");
		assertEquals(new BigDecimal("200000.00"), actualDataMap.get("INCOME_KINGAKU"), "収入金額(INCOME_KINGAKU)が更新されていること");
		// 更新されないカラムの確認(USER_ID, TARGET_YEAR, TARGET_MONTH, INCOME_CODEはPKのため変更不可)
		assertEquals(beforeIncomeCode, actualDataMap.get("INCOME_CODE"),          "収入コード(INCOME_CODE)が変更されていないこと");
		assertEquals("TEST-USER-ID",   actualDataMap.get("USER_ID"),              "ユーザID(USER_ID)が変更されていないこと");

		/* 対象データなしの場合、0件が返ること */
		IncomeItem notFoundData = IncomeItem.from(
				"TEST-USER-ID", "2025", "12", "99", "1", null, new BigDecimal("0.00"), false);
		assertEquals(0, repository.update(notFoundData), "対象データなしの場合、0件であること");

		/* null可項目の更新チェック(INCOME_DETAIL_CONTEXT=nullに更新) */
		IncomeItem updateNullData = IncomeItem.from(
				"TEST-USER-ID", "2025", "12", "01", "3", null, new BigDecimal("100000.00"), false);
		assertEquals(1, repository.update(updateNullData), "null可項目更新:更新データが1件であること");

		// 更新されたデータをロード
		Map<String, Object> actualNullDataMap = jdbcTemplate.queryForMap(
				"SELECT * FROM INCOME_TABLE WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=? AND INCOME_CODE=?",
				"TEST-USER-ID", "2025", "12", "01");

		// null可項目の確認
		assertEquals("3",              actualNullDataMap.get("INCOME_KUBUN"),          "収入区分(INCOME_KUBUN)がnullで更新されていること");
		assertNull(actualNullDataMap.get("INCOME_DETAIL_CONTEXT"),                     "収入詳細(INCOME_DETAIL_CONTEXT)がNULLに更新されていること");
		assertEquals(new BigDecimal("100000.00"), actualNullDataMap.get("INCOME_KINGAKU"), "収入金額(INCOME_KINGAKU)が更新されていること(null可)");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.infrastructure.datasource.account.income.IncomeTableDataSource#delete(com.yonetani.webapp.accountbook.domain.model.account.income.IncomeItem)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(value = "IncomeTableDataSourceDeleteTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	@DisplayName("delete:収入テーブルの論理削除テスト(DELETE_FLG=TRUEの確認)")
	void testDelete() {
		/* 論理削除チェック */
		// 削除対象データ
		IncomeItem deleteData = IncomeItem.from(
				"TEST-USER-ID", "2025", "12", "01", "1", "削除対象詳細", new BigDecimal("100000.00"), false);
		// データ論理削除
		assertEquals(1, repository.delete(deleteData), "削除データが1件であること");

		// 削除後のデータをJdbcTemplateでロード
		Map<String, Object> actualDataMap = jdbcTemplate.queryForMap(
				"SELECT * FROM INCOME_TABLE WHERE USER_ID=? AND TARGET_YEAR=? AND TARGET_MONTH=? AND INCOME_CODE=?",
				"TEST-USER-ID", "2025", "12", "01");

		// DELETE_FLG=TRUEであること
		assertEquals(true,   actualDataMap.get("DELETE_FLG"),          "削除フラグ(DELETE_FLG)がTRUEであること");
		// 論理削除のため他のカラムは変更されていないこと
		assertEquals("1",    actualDataMap.get("INCOME_KUBUN"),         "収入区分(INCOME_KUBUN)が変更されていないこと");
		assertEquals("削除対象詳細", actualDataMap.get("INCOME_DETAIL_CONTEXT"), "収入詳細(INCOME_DETAIL_CONTEXT)が変更されていないこと");
		assertEquals(new BigDecimal("100000.00"), actualDataMap.get("INCOME_KINGAKU"), "収入金額(INCOME_KINGAKU)が変更されていないこと");

		/* 対象データなしの場合、0件が返ること */
		IncomeItem notFoundData = IncomeItem.from(
				"TEST-USER-ID", "2025", "12", "99", "1", null, new BigDecimal("0.00"), false);
		assertEquals(0, repository.delete(notFoundData), "対象データなしの場合、0件であること");
	}
}
