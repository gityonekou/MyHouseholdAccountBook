/**
 * FixedCostTableRepositoryのテストクラスです。
 * 固定費テーブル:FIXED_COST_TABLEのデータ登録・更新・論理削除の各種メソッドテストを実施します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/04/25 : 1.01.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.fixedcost;

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

import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCost;
import com.yonetani.webapp.accountbook.domain.repository.account.fixedcost.FixedCostTableRepository;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.fixedcost.FixedCostTableMapper;

/**
 *<pre>
 * FixedCostTableRepositoryのテストクラスです。
 * 固定費テーブル:FIXED_COST_TABLEのデータ登録・更新・論理削除の各種メソッドテストを実施します。
 *
 * [テスト対象メソッド]
 * ・add()    : 固定費テーブルへの新規登録（全カラムの登録データ確認）
 * ・update() : 固定費テーブルの更新（更新対象カラムと非更新カラムの確認）
 * ・delete() : 固定費テーブルの論理削除（DELETE_FLG=TRUEの確認）
 *
 * [テストの着眼点]
 * ・add   : 全カラムが正しく登録されること。一意制約違反が正しく発生すること。
 *           null可項目(FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT)の登録確認。
 * ・update: FIXED_COST_NAME, FIXED_COST_DETAIL_CONTEXT, FIXED_COST_KUBUN,
 *           FIXED_COST_SHIHARAI_TUKI, FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT,
 *           FIXED_COST_SHIHARAI_DAY, SHIHARAI_KINGAKUのみ更新されること。
 *           SISYUTU_ITEM_CODEは更新されないこと。
 *           対象なしの場合0件が返ること。
 * ・delete: DELETE_FLG=TRUEに論理削除されること。その他カラムは変更されないこと。
 *           対象なしの場合0件が返ること。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.01)
 *
 */
@MybatisTest
@ActiveProfiles("unit-test")
class FixedCostTableDataSourceTest {

	// FixedCostTableRepository
	private FixedCostTableRepository repository;
	// FixedCostTable mapper
	@Autowired
	private FixedCostTableMapper mapper;
	// DBアクセス
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	void setUp() throws Exception {
		repository = new FixedCostTableDataSource(mapper);
	}

	/**
	 * {@link FixedCostTableDataSource#add(FixedCost)} のためのテスト・メソッド。
	 */
	@Test
	@DisplayName("add:固定費テーブルへの新規登録テスト(全カラム確認)")
	void testAdd() {
		/* 全項目の登録チェック（FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXTあり） */
		FixedCost addData = FixedCost.from(
				"TEST-USER-ID", "0001", "家賃", "毎月27日引き落とし",
				"0030", "1", "00", "任意詳細テスト", "27",
				new BigDecimal("60000.00"));
		assertEquals(1, repository.add(addData), "登録データが1件であること");

		Map<String, Object> actual = jdbcTemplate.queryForMap(
				"SELECT * FROM FIXED_COST_TABLE WHERE USER_ID=? AND FIXED_COST_CODE=?",
				"TEST-USER-ID", "0001");

		assertEquals("TEST-USER-ID",          actual.get("USER_ID"),                                  "ユーザID(USER_ID)が正しいこと");
		assertEquals("0001",                   actual.get("FIXED_COST_CODE"),                          "固定費コード(FIXED_COST_CODE)が正しいこと");
		assertEquals("家賃",                   actual.get("FIXED_COST_NAME"),                          "固定費名(FIXED_COST_NAME)が正しいこと");
		assertEquals("毎月27日引き落とし",     actual.get("FIXED_COST_DETAIL_CONTEXT"),                "固定費内容詳細(FIXED_COST_DETAIL_CONTEXT)が正しいこと");
		assertEquals("0030",                   actual.get("SISYUTU_ITEM_CODE"),                        "支出項目コード(SISYUTU_ITEM_CODE)が正しいこと");
		assertEquals("1",                      actual.get("FIXED_COST_KUBUN"),                         "固定費区分(FIXED_COST_KUBUN)が正しいこと");
		assertEquals("00",                     actual.get("FIXED_COST_SHIHARAI_TUKI"),                 "固定費支払月(FIXED_COST_SHIHARAI_TUKI)が正しいこと");
		assertEquals("任意詳細テスト",         actual.get("FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT"), "支払月任意詳細(FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT)が正しいこと");
		assertEquals("27",                     actual.get("FIXED_COST_SHIHARAI_DAY"),                  "固定費支払日(FIXED_COST_SHIHARAI_DAY)が正しいこと");
		assertEquals(new BigDecimal("60000.00"), actual.get("SHIHARAI_KINGAKU"),                       "支払金額(SHIHARAI_KINGAKU)が正しいこと");
		assertEquals(false,                    actual.get("DELETE_FLG"),                               "削除フラグ(DELETE_FLG)がFALSEであること");

		/* 同じデータを登録した場合、一意制約違反となること */
		assertThrows(DuplicateKeyException.class, () -> repository.add(addData),
				"同じデータを登録した場合、一意制約違反となること");

		/* null可項目の登録チェック（FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT=null） */
		FixedCost addNullData = FixedCost.from(
				"TEST-USER-ID", "0002", "電気代概算", "概算で登録",
				"0037", "2", "00", null, "27",
				new BigDecimal("12000.00"));
		assertEquals(1, repository.add(addNullData), "null可項目あり:登録データが1件であること");

		Map<String, Object> actualNull = jdbcTemplate.queryForMap(
				"SELECT * FROM FIXED_COST_TABLE WHERE USER_ID=? AND FIXED_COST_CODE=?",
				"TEST-USER-ID", "0002");

		assertEquals("0002",                   actualNull.get("FIXED_COST_CODE"),                          "固定費コード(FIXED_COST_CODE)が正しいこと(null可)");
		assertEquals("電気代概算",             actualNull.get("FIXED_COST_NAME"),                          "固定費名(FIXED_COST_NAME)が正しいこと(null可)");
		assertEquals("0037",                   actualNull.get("SISYUTU_ITEM_CODE"),                        "支出項目コード(SISYUTU_ITEM_CODE)が正しいこと(null可)");
		assertNull(actualNull.get("FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT"),                             "支払月任意詳細(FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT)がNULLであること");
		assertEquals(new BigDecimal("12000.00"), actualNull.get("SHIHARAI_KINGAKU"),                       "支払金額(SHIHARAI_KINGAKU)が正しいこと(null可)");
	}

	/**
	 * {@link FixedCostTableDataSource#update(FixedCost)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(value = "FixedCostTableDataSourceUpdateTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	@DisplayName("update:固定費テーブルの更新テスト(更新対象カラムと非更新カラムの確認)")
	void testUpdate() {
		/* 更新前のSISYUTU_ITEM_CODEを取得（更新されないことを確認するため） */
		Map<String, Object> before = jdbcTemplate.queryForMap(
				"SELECT SISYUTU_ITEM_CODE FROM FIXED_COST_TABLE WHERE USER_ID=? AND FIXED_COST_CODE=?",
				"TEST-USER-ID", "0001");

		/* 全項目の更新チェック
		 * 意図的にSISYUTU_ITEM_CODEに別の値("9999")をセット（更新されないことを確認）
		 * FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXTに値をセット（更新されることを確認） */
		FixedCost updateData = FixedCost.from(
				"TEST-USER-ID", "0001", "更新後支払名", "更新後詳細",
				"9999", "2", "20", "更新後任意詳細", "00",
				new BigDecimal("35000.00"));
		assertEquals(1, repository.update(updateData), "更新データが1件であること");

		Map<String, Object> actual = jdbcTemplate.queryForMap(
				"SELECT * FROM FIXED_COST_TABLE WHERE USER_ID=? AND FIXED_COST_CODE=?",
				"TEST-USER-ID", "0001");

		// 更新されたカラムの確認
		assertEquals("更新後支払名",   actual.get("FIXED_COST_NAME"),                          "固定費名(FIXED_COST_NAME)が更新されていること");
		assertEquals("更新後詳細",     actual.get("FIXED_COST_DETAIL_CONTEXT"),                "固定費内容詳細(FIXED_COST_DETAIL_CONTEXT)が更新されていること");
		assertEquals("2",              actual.get("FIXED_COST_KUBUN"),                         "固定費区分(FIXED_COST_KUBUN)が更新されていること");
		assertEquals("20",             actual.get("FIXED_COST_SHIHARAI_TUKI"),                 "固定費支払月(FIXED_COST_SHIHARAI_TUKI)が更新されていること");
		assertEquals("更新後任意詳細", actual.get("FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT"), "支払月任意詳細(FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT)が更新されていること");
		assertEquals("00",             actual.get("FIXED_COST_SHIHARAI_DAY"),                  "固定費支払日(FIXED_COST_SHIHARAI_DAY)が更新されていること");
		assertEquals(new BigDecimal("35000.00"), actual.get("SHIHARAI_KINGAKU"),               "支払金額(SHIHARAI_KINGAKU)が更新されていること");
		// 更新されないカラムの確認
		assertEquals(before.get("SISYUTU_ITEM_CODE"), actual.get("SISYUTU_ITEM_CODE"),
				"支出項目コード(SISYUTU_ITEM_CODE)が更新されていないこと");

		/* 対象データなしの場合、0件が返ること */
		FixedCost notFound = FixedCost.from(
				"TEST-USER-ID", "9999", "対象なし", "",
				"0001", "1", "00", null, "27",
				new BigDecimal("0.00"));
		assertEquals(0, repository.update(notFound), "対象データなしの場合、0件であること");

		/* null可項目の更新チェック（FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT=nullに更新） */
		FixedCost updateNullData = FixedCost.from(
				"TEST-USER-ID", "0001", "更新後支払名(null可)", "更新後詳細(null可)",
				"9999", "1", "00", null, "27",
				new BigDecimal("40000.00"));
		assertEquals(1, repository.update(updateNullData), "null可項目更新:更新データが1件であること");

		Map<String, Object> actualNull = jdbcTemplate.queryForMap(
				"SELECT * FROM FIXED_COST_TABLE WHERE USER_ID=? AND FIXED_COST_CODE=?",
				"TEST-USER-ID", "0001");

		assertNull(actualNull.get("FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT"),
				"支払月任意詳細(FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT)がNULLに更新されていること");
		assertEquals(before.get("SISYUTU_ITEM_CODE"), actualNull.get("SISYUTU_ITEM_CODE"),
				"支出項目コード(SISYUTU_ITEM_CODE)がnull可更新後も変更されていないこと");
	}

	/**
	 * {@link FixedCostTableDataSource#delete(FixedCost)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(value = "FixedCostTableDataSourceDeleteTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	@DisplayName("delete:固定費テーブルの論理削除テスト(DELETE_FLG=TRUEの確認)")
	void testDelete() {
		/* 論理削除チェック */
		FixedCost deleteData = FixedCost.from(
				"TEST-USER-ID", "0001", "削除対象支払名", "削除対象詳細",
				"0002", "1", "40", "削除対象任意詳細", "15",
				new BigDecimal("30000.00"));
		assertEquals(1, repository.delete(deleteData), "削除データが1件であること");

		Map<String, Object> actual = jdbcTemplate.queryForMap(
				"SELECT * FROM FIXED_COST_TABLE WHERE USER_ID=? AND FIXED_COST_CODE=?",
				"TEST-USER-ID", "0001");

		// DELETE_FLG=TRUEであること
		assertEquals(true, actual.get("DELETE_FLG"), "削除フラグ(DELETE_FLG)がTRUEであること");
		// 論理削除のため他のカラムは変更されていないこと
		assertEquals("削除対象支払名",     actual.get("FIXED_COST_NAME"),                          "固定費名(FIXED_COST_NAME)が変更されていないこと");
		assertEquals("削除対象詳細",       actual.get("FIXED_COST_DETAIL_CONTEXT"),                "固定費内容詳細(FIXED_COST_DETAIL_CONTEXT)が変更されていないこと");
		assertEquals("0002",               actual.get("SISYUTU_ITEM_CODE"),                        "支出項目コード(SISYUTU_ITEM_CODE)が変更されていないこと");
		assertEquals("1",                  actual.get("FIXED_COST_KUBUN"),                         "固定費区分(FIXED_COST_KUBUN)が変更されていないこと");
		assertEquals("40",                 actual.get("FIXED_COST_SHIHARAI_TUKI"),                 "固定費支払月(FIXED_COST_SHIHARAI_TUKI)が変更されていないこと");
		assertEquals("削除対象任意詳細",   actual.get("FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT"), "支払月任意詳細(FIXED_COST_SHIHARAI_TUKI_OPTIONAL_CONTEXT)が変更されていないこと");
		assertEquals("15",                 actual.get("FIXED_COST_SHIHARAI_DAY"),                  "固定費支払日(FIXED_COST_SHIHARAI_DAY)が変更されていないこと");
		assertEquals(new BigDecimal("30000.00"), actual.get("SHIHARAI_KINGAKU"),                   "支払金額(SHIHARAI_KINGAKU)が変更されていないこと");

		/* 対象データなしの場合、0件が返ること */
		FixedCost notFound = FixedCost.from(
				"TEST-USER-ID", "9999", "対象なし", "",
				"0001", "1", "00", null, "27",
				new BigDecimal("0.00"));
		assertEquals(0, repository.delete(notFound), "対象データなしの場合、0件であること");
	}
}
