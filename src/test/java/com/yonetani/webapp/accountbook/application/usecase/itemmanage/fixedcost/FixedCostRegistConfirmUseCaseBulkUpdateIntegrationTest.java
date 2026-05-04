/**
 * 固定費情報登録・更新・削除処理ユースケースの一括更新メソッドの統合テストクラスです。
 *
 * <pre>
 * FixedCostRegistConfirmUseCase の以下メソッドをテストします。
 *
 * [対象メソッド]
 * 1. execBulkUpdate - 固定費一括更新
 *
 * [テストシナリオ]
 * ① 正常系：execBulkUpdate_2件を一括更新、DB確認（支払日・支払金額が更新されること、他は変更なし）
 * ② 正常系：execBulkUpdate_1件のみ選択して更新
 * ③ 異常系：execBulkUpdate_チェックリストに存在しない固定費コードが含まれる場合に例外
 *
 * [テストデータ]
 * 固定費3件:
 *   0001: 家賃   (0030:家賃, 確定, 毎月, 27日, 60,000円) ← 兄弟固定費(その1)
 *   0002: 共益費 (0030:家賃, 確定, 毎月, 27日,  8,000円) ← 兄弟固定費(その2)
 *   0003: 電気代概算 (0037:電気代, 予定, 毎月, 27日, 12,000円) ← 対象外
 * </pre>
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/05/02 : 1.01.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage.fixedcost;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.FixedCostBulkUpdateForm;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostBulkUpdateResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

/**
 *<pre>
 * 固定費情報登録・更新・削除処理ユースケースの一括更新メソッドの統合テストクラスです。
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
	"/com/yonetani/webapp/accountbook/application/usecase/itemmanage/fixedcost/FixedCostBulkUpdateIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("固定費情報登録・更新・削除処理ユースケース 一括更新メソッドのUseCaseテスト（統合テスト）")
class FixedCostRegistConfirmUseCaseBulkUpdateIntegrationTest {

	@Autowired
	private FixedCostRegistConfirmUseCase useCase;

	@Autowired
	private NamedParameterJdbcTemplate namedParamTemplate;

	// テスト用ログインユーザ
	private final LoginUserInfo TEST_USER = LoginUserInfo.from("user01", "テストユーザ01");

	// ========== execBulkUpdate ==========

	/**
	 *<pre>
	 * テスト①：正常系：execBulkUpdate_2件を一括更新
	 *
	 * 【検証内容】
	 * ・チェックリストに0001, 0002を指定して支払日=25、支払金額=70000で一括更新
	 * ・更新前: 0001=27日/60000, 0002=27日/8000
	 * ・更新後: 0001=25日/70000, 0002=25日/70000 となること
	 * ・固定費名・支払月など他フィールドは変更されないこと
	 * ・対象外の0003は変更されないこと
	 * ・完了メッセージが「固定費を一括更新しました。[2件]」であること
	 * ・トランザクション完了フラグがtrueであること
	 *</pre>
	 */
	@Test
	@DisplayName("① execBulkUpdate_0001と0002を一括更新(支払日=25、支払金額=70000)")
	void testExecBulkUpdate_success_2件() {
		// 更新前確認
		Map<String, Object> before0001 = findFixedCostByCode("user01", "0001");
		assertNotNull(before0001, "更新前に0001が存在すること");
		assertEquals("家賃", before0001.get("FIXED_COST_NAME"), "更新前の0001の固定費名が家賃であること");
		assertEquals("27", before0001.get("FIXED_COST_SHIHARAI_DAY"), "更新前の0001の支払日が27であること");
		assertEquals(new BigDecimal("60000.00"), before0001.get("SHIHARAI_KINGAKU"),
				"更新前の0001の支払金額が60000であること");

		Map<String, Object> before0002 = findFixedCostByCode("user01", "0002");
		assertNotNull(before0002, "更新前に0002が存在すること");
		assertEquals("共益費", before0002.get("FIXED_COST_NAME"), "更新前の0002の固定費名が共益費であること");
		assertEquals("27", before0002.get("FIXED_COST_SHIHARAI_DAY"), "更新前の0002の支払日が27であること");
		assertEquals(new BigDecimal("8000.00"), before0002.get("SHIHARAI_KINGAKU"),
				"更新前の0002の支払金額が8000であること");

		// フォームデータ作成（0001, 0002を選択して一括更新）
		FixedCostBulkUpdateForm form = new FixedCostBulkUpdateForm();
		form.setBaseFixedCostCode("0001");
		form.setShiharaiDay("25");
		form.setShiharaiKingaku(70000);
		form.setCheckedFixedCostCodeList(List.of("0001", "0002"));

		// 実行
		FixedCostBulkUpdateResponse response = useCase.execBulkUpdate(TEST_USER, form);

		// トランザクション完了確認
		assertNotNull(response, "レスポンスがnullでないこと");
		assertTrue(response.isTransactionSuccessFull(), "トランザクション完了フラグがtrueであること");
		assertEquals(1, response.getMessagesList().size(), "完了メッセージが1件であること");
		assertEquals("固定費を一括更新しました。[2件]", response.getMessagesList().get(0),
				"完了メッセージが正しく設定されていること");

		// DB確認: 0001が更新されていること
		Map<String, Object> after0001 = findFixedCostByCode("user01", "0001");
		assertEquals("25", after0001.get("FIXED_COST_SHIHARAI_DAY"), "0001の支払日が25に更新されていること");
		assertEquals(new BigDecimal("70000.00"), after0001.get("SHIHARAI_KINGAKU"),
				"0001の支払金額が70000に更新されていること");
		// 変更対象外フィールドが維持されていること
		assertEquals("家賃", after0001.get("FIXED_COST_NAME"), "0001の固定費名が変わらないこと");
		assertEquals("0030", after0001.get("SISYUTU_ITEM_CODE"), "0001の支出項目コードが変わらないこと");
		assertEquals("00", after0001.get("FIXED_COST_SHIHARAI_TUKI"), "0001の支払月が変わらないこと");

		// DB確認: 0002が更新されていること
		Map<String, Object> after0002 = findFixedCostByCode("user01", "0002");
		assertEquals("25", after0002.get("FIXED_COST_SHIHARAI_DAY"), "0002の支払日が25に更新されていること");
		assertEquals(new BigDecimal("70000.00"), after0002.get("SHIHARAI_KINGAKU"),
				"0002の支払金額が70000に更新されていること");
		assertEquals("共益費", after0002.get("FIXED_COST_NAME"), "0002の固定費名が変わらないこと");

		// DB確認: 0003は更新されていないこと
		Map<String, Object> after0003 = findFixedCostByCode("user01", "0003");
		assertEquals("27", after0003.get("FIXED_COST_SHIHARAI_DAY"), "0003の支払日が変わらないこと");
		assertEquals(new BigDecimal("12000.00"), after0003.get("SHIHARAI_KINGAKU"),
				"0003の支払金額が変わらないこと");
	}

	/**
	 *<pre>
	 * テスト②：正常系：execBulkUpdate_1件のみ選択して更新
	 *
	 * 【検証内容】
	 * ・チェックリストに0001のみを指定して一括更新
	 * ・0001のみが更新され、0002は変更されないこと
	 * ・完了メッセージが「固定費を一括更新しました。[1件]」であること
	 *</pre>
	 */
	@Test
	@DisplayName("② execBulkUpdate_0001のみ選択(1件)更新")
	void testExecBulkUpdate_success_1件() {
		// フォームデータ作成（0001のみ選択）
		FixedCostBulkUpdateForm form = new FixedCostBulkUpdateForm();
		form.setBaseFixedCostCode("0001");
		form.setShiharaiDay("20");
		form.setShiharaiKingaku(65000);
		form.setCheckedFixedCostCodeList(List.of("0001"));

		// 実行
		FixedCostBulkUpdateResponse response = useCase.execBulkUpdate(TEST_USER, form);

		// トランザクション完了確認
		assertTrue(response.isTransactionSuccessFull(), "トランザクション完了フラグがtrueであること");
		assertEquals("固定費を一括更新しました。[1件]", response.getMessagesList().get(0),
				"1件更新の完了メッセージが正しいこと");

		// DB確認: 0001が更新されていること
		Map<String, Object> after0001 = findFixedCostByCode("user01", "0001");
		assertEquals("20", after0001.get("FIXED_COST_SHIHARAI_DAY"), "0001の支払日が20に更新されていること");
		assertEquals(new BigDecimal("65000.00"), after0001.get("SHIHARAI_KINGAKU"),
				"0001の支払金額が65000に更新されていること");

		// DB確認: 0002は変更されていないこと
		Map<String, Object> after0002 = findFixedCostByCode("user01", "0002");
		assertEquals("27", after0002.get("FIXED_COST_SHIHARAI_DAY"), "0002の支払日が変わらないこと");
		assertEquals(new BigDecimal("8000.00"), after0002.get("SHIHARAI_KINGAKU"),
				"0002の支払金額が変わらないこと");
	}

	/**
	 *<pre>
	 * テスト③：異常系：execBulkUpdate_チェックリストに存在しない固定費コードで例外
	 *
	 * 【検証内容】
	 * ・チェックリストに存在しない固定費コード("9999")のみを指定した場合、
	 *   MyHouseholdAccountBookRuntimeExceptionが発生すること
	 *
	 * 【注意】ロールバック確認はこのクラスでは行わない
	 * このクラスには @Transactional が付与されており、テストとサービスが同一トランザクションを共有する。
	 * 例外発生後もトランザクションは rollback-only になるだけで、同一トランザクション内のDB変更は
	 * テスト終了まで可視のままであるため、例外後のロールバック確認ができない。
	 * ロールバック動作は FixedCostBulkUpdateRollbackTest（@Transactional なし）で検証している。
	 *</pre>
	 */
	@Test
	@DisplayName("③ execBulkUpdate_チェックリストに存在しないコード→例外")
	void testExecBulkUpdate_targetNotFound() {
		// フォームデータ作成（9999は存在しない）
		FixedCostBulkUpdateForm form = new FixedCostBulkUpdateForm();
		form.setBaseFixedCostCode("0001");
		form.setShiharaiDay("25");
		form.setShiharaiKingaku(70000);
		form.setCheckedFixedCostCodeList(List.of("9999"));

		// 例外が発生すること
		assertThrows(MyHouseholdAccountBookRuntimeException.class,
				() -> useCase.execBulkUpdate(TEST_USER, form),
				"存在しない固定費コードを含む場合に例外が発生すること");
	}

	// ========== DB確認用ヘルパー ==========

	private Map<String, Object> findFixedCostByCode(String userId, String fixedCostCode) {
		List<Map<String, Object>> results = namedParamTemplate.queryForList(
				"SELECT * FROM FIXED_COST_TABLE WHERE USER_ID = :userId AND FIXED_COST_CODE = :code",
				new MapSqlParameterSource("userId", userId).addValue("code", fixedCostCode));
		return results.isEmpty() ? null : results.get(0);
	}
}
