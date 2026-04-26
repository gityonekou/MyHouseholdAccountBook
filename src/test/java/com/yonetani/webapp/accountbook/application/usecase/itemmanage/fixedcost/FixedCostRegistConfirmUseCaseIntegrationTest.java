/**
 * 固定費情報登録・更新・削除処理ユースケースの統合テストクラスです。
 *
 * <pre>
 * FixedCostRegistConfirmUseCase の以下メソッドをテストします。
 *
 * [対象メソッド]
 * 1. execDelete - 固定費削除（論理削除）
 * 2. execAdd    - 固定費追加
 * 3. execUpdate - 固定費更新
 *
 * [テストシナリオ]
 * ① 正常系：execDelete_固定費0003を論理削除、DB確認
 * ② 異常系：execDelete_存在しない固定費コードで例外
 * ③ 正常系：execAdd_新規固定費(0035)を追加、DB確認(4件になること)
 * ④ 正常系：execAdd_上限9999件超え時のエラーメッセージ確認（countByUserId=9999を模倣はできないため件数境界のみ確認）
 * ⑤ 正常系：execUpdate_固定費0002を更新、DB確認
 * ⑥ 異常系：execUpdate_存在しない固定費コードで例外
 *
 * [テストデータ]
 * 固定費4件: 0001:家賃(0030,毎月,27日,60000), 0002:電気代概算(0037,毎月,27日,12000),
 *           0003:国民年金保険(0015,奇数月,月初,16590), 0004:その他任意テスト(0038,その他任意,27日,10000)
 * </pre>
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/04/19 : 1.01.00  新規作成
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

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.FixedCostInfoUpdateForm;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostInfoManageActSelectResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostInfoManageUpdateResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

/**
 *<pre>
 * 固定費情報登録・更新・削除処理ユースケースの統合テストクラスです。
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
@DisplayName("固定費情報登録・更新・削除処理ユースケースのUseCaseテスト（統合テスト）")
class FixedCostRegistConfirmUseCaseIntegrationTest {

	@Autowired
	private FixedCostRegistConfirmUseCase useCase;

	@Autowired
	private NamedParameterJdbcTemplate namedParamTemplate;

	// テスト用ログインユーザ
	private final LoginUserInfo TEST_USER = LoginUserInfo.from("user01", "テストユーザ01");

	// ========== execDelete ==========

	/**
	 *<pre>
	 * テスト①：正常系：execDelete_固定費0003(国民年金保険)の論理削除
	 *
	 * 【検証内容】
	 * ・削除前にDELETE_FLG=falseであること
	 * ・論理削除後もレコードが存在すること（物理削除でないこと）
	 * ・DELETE_FLG=trueに更新されること
	 * ・他の固定費(0001, 0002)のDELETE_FLGが変わらないこと
	 * ・完了メッセージが「指定の固定費を削除しました。[code:0003]国民年金保険」であること
	 *</pre>
	 */
	@Test
	@DisplayName("① execDelete_固定費0003を論理削除")
	void testExecDelete_success() {
		// 削除前確認: 0003が存在すること
		assertEquals(1, countFixedCost("user01", "0003"), "削除前は0003が存在すること");
		assertEquals(Boolean.FALSE, isDeleted("user01", "0003"), "削除前のDELETE_FLGがfalseであること");

		// 実行
		FixedCostInfoManageActSelectResponse response = useCase.execDelete(TEST_USER, "0003");

		// トランザクション完了確認
		assertNotNull(response, "レスポンスがnullでないこと");
		assertTrue(response.isTransactionSuccessFull(), "トランザクション完了フラグがtrueであること");
		assertEquals(1, response.getMessagesList().size(), "完了メッセージが1件であること");
		assertEquals("指定の固定費を削除しました。[code:0003]国民年金保険", response.getMessagesList().get(0),
				"完了メッセージが正しく設定されていること");

		// DB確認: 論理削除されていること
		assertEquals(1, countFixedCost("user01", "0003"), "削除後も論理削除レコードが存在すること");
		assertEquals(Boolean.TRUE, isDeleted("user01", "0003"), "削除後のDELETE_FLGがtrueであること");

		// 他の固定費は影響を受けないこと
		assertEquals(Boolean.FALSE, isDeleted("user01", "0001"), "0001のDELETE_FLGが変わらないこと");
		assertEquals(Boolean.FALSE, isDeleted("user01", "0002"), "0002のDELETE_FLGが変わらないこと");
	}

	/**
	 *<pre>
	 * テスト②：異常系：execDelete_存在しない固定費コードで例外
	 *
	 * 【検証内容】
	 * ・存在しない固定費コード("9999")を指定した場合、MyHouseholdAccountBookRuntimeExceptionが発生すること
	 *</pre>
	 */
	@Test
	@DisplayName("② execDelete_存在しない固定費コードで例外")
	void testExecDelete_notFound() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class,
				() -> useCase.execDelete(TEST_USER, "9999"),
				"存在しない固定費コードで例外が発生すること");
	}

	// ========== execAdd ==========

	/**
	 *<pre>
	 * テスト③：正常系：execAdd_新規固定費(支出項目コード=0035:自由用途積立金)を追加
	 *
	 * 【検証内容】
	 * ・追加前のユーザ固定費件数が4件であること
	 * ・追加後のユーザ固定費件数が5件になること
	 * ・固定費コードが自動採番(countByUserId+1の4桁ゼロ埋め)で「0005」になること
	 * ・追加されたレコードの各フィールド（支払名・支払月・支払日・支払金額・DELETE_FLG）が正しいこと
	 * ・完了メッセージが「新規固定費を追加しました。[code:0005]自由用途積立」であること
	 *</pre>
	 */
	@Test
	@DisplayName("③ execAdd_新規固定費(0035:自由用途積立金)を追加")
	void testExecAdd_success() {
		// 追加前確認: 0035に固定費がないこと
		assertEquals(0, countFixedCostBySisyutuItem("user01", "0035"), "追加前は0035に固定費がないこと");
		// 追加前のユーザ固定費件数: 4件
		assertEquals(4, countAllFixedCost("user01"), "追加前は4件であること");

		// フォームデータ作成
		FixedCostInfoUpdateForm form = new FixedCostInfoUpdateForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setSisyutuItemCode("0035");
		form.setFixedCostName("自由用途積立");
		form.setFixedCostDetailContext("毎月積立");
		form.setFixedCostKubun("1");
		form.setShiharaiTuki("00");
		form.setShiharaiTukiOptionalContext(null);
		form.setShiharaiDay("27");
		form.setShiharaiKingaku(30000);

		// 実行
		FixedCostInfoManageUpdateResponse response = useCase.execAdd(TEST_USER, form);

		// トランザクション完了確認
		assertNotNull(response, "レスポンスがnullでないこと");
		assertTrue(response.isTransactionSuccessFull(), "トランザクション完了フラグがtrueであること");
		assertEquals(1, response.getMessagesList().size(), "完了メッセージが1件であること");
		assertEquals("新規固定費を追加しました。[code:0005]自由用途積立", response.getMessagesList().get(0),
				"完了メッセージが正しく設定されていること");

		// DB確認: 5件になっていること
		assertEquals(5, countAllFixedCost("user01"), "追加後は5件であること");

		// 追加されたレコードの内容確認
		assertEquals(1, countFixedCostBySisyutuItem("user01", "0035"), "0035の固定費が1件追加されること");
		Map<String, Object> added = findFixedCostBySisyutuItem("user01", "0035");
		assertNotNull(added, "追加されたレコードがnullでないこと");
		assertEquals("0005", added.get("FIXED_COST_CODE"), "固定費コードが自動採番されること");
		assertEquals("自由用途積立", added.get("FIXED_COST_NAME"), "固定費名が設定されていること");
		assertEquals("00", added.get("FIXED_COST_SHIHARAI_TUKI"), "支払月が設定されていること");
		assertEquals("27", added.get("FIXED_COST_SHIHARAI_DAY"), "支払日が設定されていること");
		assertEquals(new BigDecimal("30000.00"), added.get("SHIHARAI_KINGAKU"), "支払金額が設定されていること");
		assertEquals(Boolean.FALSE, added.get("DELETE_FLG"), "DELETE_FLGがfalseであること");
	}

	// ========== execUpdate ==========

	/**
	 *<pre>
	 * テスト⑤：正常系：execUpdate_固定費0002(電気代概算)の更新
	 *
	 * 【検証内容】
	 * ・更新前の固定費名が「電気代概算」、支払金額が12,000円であること
	 * ・更新後の固定費名が「電気代(更新後)」、支払金額が15,000円に更新されること
	 * ・固定費区分が更新されること
	 * ・総件数が4件のまま変わらないこと（更新は既存レコードへの上書き）
	 * ・完了メッセージが「固定費を更新しました。[code:0002]電気代(更新後)」であること
	 *</pre>
	 */
	@Test
	@DisplayName("⑤ execUpdate_固定費0002(電気代概算)を更新")
	void testExecUpdate_success() {
		// 更新前確認
		Map<String, Object> before = findFixedCostByCode("user01", "0002");
		assertNotNull(before, "更新前のレコードが存在すること");
		assertEquals("電気代概算", before.get("FIXED_COST_NAME"), "更新前の固定費名が電気代概算であること");
		assertEquals(new BigDecimal("12000.00"), before.get("SHIHARAI_KINGAKU"), "更新前の支払金額が12000であること");

		// フォームデータ作成（支払金額を更新）
		FixedCostInfoUpdateForm form = new FixedCostInfoUpdateForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		form.setFixedCostCode("0002");
		form.setSisyutuItemCode("0037");
		form.setFixedCostName("電気代(更新後)");
		form.setFixedCostDetailContext("更新後の詳細");
		form.setFixedCostKubun("1");
		form.setShiharaiTuki("00");
		form.setShiharaiTukiOptionalContext(null);
		form.setShiharaiDay("27");
		form.setShiharaiKingaku(15000);

		// 実行
		FixedCostInfoManageUpdateResponse response = useCase.execUpdate(TEST_USER, form);

		// トランザクション完了確認
		assertNotNull(response, "レスポンスがnullでないこと");
		assertTrue(response.isTransactionSuccessFull(), "トランザクション完了フラグがtrueであること");
		assertEquals(1, response.getMessagesList().size(), "完了メッセージが1件であること");
		assertEquals("固定費を更新しました。[code:0002]電気代(更新後)", response.getMessagesList().get(0),
				"完了メッセージが正しく設定されていること");

		// DB確認: 更新されていること
		Map<String, Object> after = findFixedCostByCode("user01", "0002");
		assertNotNull(after, "更新後のレコードが存在すること");
		assertEquals("電気代(更新後)", after.get("FIXED_COST_NAME"), "固定費名が更新されていること");
		assertEquals(new BigDecimal("15000.00"), after.get("SHIHARAI_KINGAKU"), "支払金額が15000に更新されていること");
		assertEquals("1", after.get("FIXED_COST_KUBUN"), "固定費区分が更新されていること");

		// 総件数が変わらないこと
		assertEquals(4, countAllFixedCost("user01"), "更新後も4件のままであること");
	}

	/**
	 *<pre>
	 * テスト⑥：異常系：execUpdate_存在しない固定費コードで例外
	 *
	 * 【検証内容】
	 * ・存在しない固定費コード("9999")を指定した場合、MyHouseholdAccountBookRuntimeExceptionが発生すること
	 *</pre>
	 */
	@Test
	@DisplayName("⑥ execUpdate_存在しない固定費コードで例外")
	void testExecUpdate_notFound() {
		FixedCostInfoUpdateForm form = new FixedCostInfoUpdateForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		form.setFixedCostCode("9999");
		form.setSisyutuItemCode("0030");
		form.setFixedCostName("存在しない");
		form.setFixedCostDetailContext("");
		form.setFixedCostKubun("1");
		form.setShiharaiTuki("00");
		form.setShiharaiTukiOptionalContext(null);
		form.setShiharaiDay("27");
		form.setShiharaiKingaku(10000);

		assertThrows(MyHouseholdAccountBookRuntimeException.class,
				() -> useCase.execUpdate(TEST_USER, form),
				"存在しない固定費コードで例外が発生すること");
	}

	// ========== DB確認用ヘルパー ==========

	private int countFixedCost(String userId, String fixedCostCode) {
		return namedParamTemplate.queryForObject(
				"SELECT COUNT(*) FROM FIXED_COST_TABLE WHERE USER_ID = :userId AND FIXED_COST_CODE = :code",
				new MapSqlParameterSource("userId", userId).addValue("code", fixedCostCode),
				Integer.class);
	}

	private boolean isDeleted(String userId, String fixedCostCode) {
		return namedParamTemplate.queryForObject(
				"SELECT DELETE_FLG FROM FIXED_COST_TABLE WHERE USER_ID = :userId AND FIXED_COST_CODE = :code",
				new MapSqlParameterSource("userId", userId).addValue("code", fixedCostCode),
				Boolean.class);
	}

	private int countAllFixedCost(String userId) {
		return namedParamTemplate.queryForObject(
				"SELECT COUNT(*) FROM FIXED_COST_TABLE WHERE USER_ID = :userId AND DELETE_FLG = false",
				new MapSqlParameterSource("userId", userId),
				Integer.class);
	}

	private int countFixedCostBySisyutuItem(String userId, String sisyutuItemCode) {
		return namedParamTemplate.queryForObject(
				"SELECT COUNT(*) FROM FIXED_COST_TABLE WHERE USER_ID = :userId AND SISYUTU_ITEM_CODE = :code AND DELETE_FLG = false",
				new MapSqlParameterSource("userId", userId).addValue("code", sisyutuItemCode),
				Integer.class);
	}

	private Map<String, Object> findFixedCostByCode(String userId, String fixedCostCode) {
		List<Map<String, Object>> results = namedParamTemplate.queryForList(
				"SELECT * FROM FIXED_COST_TABLE WHERE USER_ID = :userId AND FIXED_COST_CODE = :code",
				new MapSqlParameterSource("userId", userId).addValue("code", fixedCostCode));
		return results.isEmpty() ? null : results.get(0);
	}

	private Map<String, Object> findFixedCostBySisyutuItem(String userId, String sisyutuItemCode) {
		List<Map<String, Object>> results = namedParamTemplate.queryForList(
				"SELECT * FROM FIXED_COST_TABLE WHERE USER_ID = :userId AND SISYUTU_ITEM_CODE = :code AND DELETE_FLG = false",
				new MapSqlParameterSource("userId", userId).addValue("code", sisyutuItemCode));
		return results.isEmpty() ? null : results.get(0);
	}
}
