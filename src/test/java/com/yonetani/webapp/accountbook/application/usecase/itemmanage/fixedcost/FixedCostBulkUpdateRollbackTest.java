/**
 * 固定費一括更新のロールバック確認テストクラスです。
 *
 * <pre>
 * このクラスは FixedCostRegistConfirmUseCaseBulkUpdateIntegrationTest から分離した
 * ロールバック専用テストクラスです。
 *
 * [分離理由]
 * FixedCostRegistConfirmUseCaseBulkUpdateIntegrationTest には @Transactional が付与されており、
 * テスト全体が単一トランザクション内で実行されます。この場合、サービス層で例外が発生しても
 * テストトランザクションが rollback-only になるだけで、同一トランザクション内の変更は
 * テスト終了まで可視状態のままです。そのため、例外後のDB確認でロールバックを
 * 証明することができません。
 *
 * [解決策]
 * このクラスには @Transactional を付与しません。これにより、サービス層の @Transactional
 * が「本物の」トランザクション境界となります。サービス層で例外が発生すると、
 * サービス層のトランザクションがロールバックされ、その後のDB確認で
 * ロールバックを実際に確認できます。
 *
 * [DB初期化方法]
 * @Transactional がないため、テスト後のDBロールバックが行われません。
 * 代わりに @Sql の executionPhase = BEFORE_TEST_METHOD を使用して
 * 各テストメソッド実行前にスキーマを再作成します。
 *
 * [テストシナリオ]
 * ① 異常系：ロールバック確認_チェックリストに存在しない固定費コードで例外発生
 *    - 0001を更新後、存在しない9999を処理しようとして例外発生
 *    - 例外発生前に更新された0001がロールバックされ元の値に戻ることを確認
 *
 * [テストデータ]
 * 固定費3件:
 *   0001: 家賃   (0030:家賃, 確定, 毎月, 27日, 60,000円) ← ロールバック対象
 *   0002: 共益費 (0030:家賃, 確定, 毎月, 27日,  8,000円)
 *   0003: 電気代概算 (0037:電気代, 予定, 毎月, 27日, 12,000円)
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
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlConfig;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.FixedCostBulkUpdateForm;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

/**
 *<pre>
 * 固定費一括更新のロールバック確認テストクラスです。
 *
 * @Transactional を付与しないことでサービス層の @Transactional を本物のトランザクション境界とし、
 * 例外発生時のロールバック動作をDB確認で検証します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.01)
 *
 */
@SpringBootTest
@ActiveProfiles("test")
// @Transactional なし - サービス層の @Transactional をテスト対象トランザクション境界とする
// 各テスト前後に @Sql でDB状態を管理する：
//   BEFORE: cleanup → schema → data（テストデータを新鮮な状態でセットアップ）
//   AFTER:  cleanup（テスト後にコミット済みデータを削除し、後続テストクラスへの影響を防ぐ）
@Sql(scripts = {
	// 1. クリーンアップ: 前テストのコミット済みデータをTRUNCATEで削除（@Transactionalなしのため必須）
	"/com/yonetani/webapp/accountbook/application/usecase/itemmanage/fixedcost/FixedCostBulkUpdateRollbackTest-cleanup.sql",
	// 2. スキーマ定義
	"/sql/initsql/schema_test.sql",
	// 3. テストデータ（兄弟固定費データ）
	"/com/yonetani/webapp/accountbook/application/usecase/itemmanage/fixedcost/FixedCostBulkUpdateIntegrationTest.sql"
}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,
   config = @SqlConfig(encoding = "UTF-8"))
@Sql(scripts = {
	// テスト後クリーンアップ: @Transactionalなしのためコミット済みデータが残存する。
	// 後続テストクラス（@Transactionalあり）への影響を防ぐため、各テスト後にデータを削除する。
	"/com/yonetani/webapp/accountbook/application/usecase/itemmanage/fixedcost/FixedCostBulkUpdateRollbackTest-cleanup.sql"
}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD,
   config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("固定費一括更新 ロールバック確認テスト")
class FixedCostBulkUpdateRollbackTest {

	@Autowired
	private FixedCostRegistConfirmUseCase useCase;

	@Autowired
	private NamedParameterJdbcTemplate namedParamTemplate;

	// テスト用ログインユーザ
	private final LoginUserInfo TEST_USER = LoginUserInfo.from("user01", "テストユーザ01");

	/**
	 *<pre>
	 * テスト①：異常系：ロールバック確認_チェックリストに存在しない固定費コードで例外
	 *
	 * 【検証内容】
	 * ・チェックリストに ["0001", "9999"] を指定した場合：
	 *   1. 0001 の UPDATE が実行される（27日/60000 → 25日/70000）
	 *   2. 9999 が見つからず MyHouseholdAccountBookRuntimeException がスローされる
	 *   3. サービス層の @Transactional によりロールバックが発生
	 *   4. 0001 の値が元の 27日/60000 に戻ること
	 *
	 * 【ロールバック確認方法】
	 * @Transactional なしのテストクラスでは、サービス層の @Transactional が本物のトランザクション境界。
	 * 例外発生 → サービス層のトランザクションがロールバック → テスト継続 → 元の値であることをDB確認
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：ロールバック確認_一括更新中に存在しないコードで例外→0001の更新がロールバックされること")
	void testExecBulkUpdate_RollbackOnTargetNotFound() {
		// 更新前確認（DBに正しいテストデータが入っていること）
		Map<String, Object> before0001 = findFixedCostByCode("user01", "0001");
		assertNotNull(before0001, "更新前に0001が存在すること");
		assertEquals("27", before0001.get("FIXED_COST_SHIHARAI_DAY"), "更新前の0001の支払日が27であること");
		assertEquals(new BigDecimal("60000.00"), before0001.get("SHIHARAI_KINGAKU"),
				"更新前の0001の支払金額が60000であること");

		// フォームデータ作成（0001は存在するが9999は存在しない）
		FixedCostBulkUpdateForm form = new FixedCostBulkUpdateForm();
		form.setBaseFixedCostCode("0001");
		form.setShiharaiDay("25");
		form.setShiharaiKingaku(70000);
		form.setCheckedFixedCostCodeList(List.of("0001", "9999"));

		// 例外が発生すること
		assertThrows(MyHouseholdAccountBookRuntimeException.class,
				() -> useCase.execBulkUpdate(TEST_USER, form),
				"存在しない固定費コードを含む場合に例外が発生すること");

		// ロールバック確認: 0001の更新が取り消されていること
		Map<String, Object> after0001 = findFixedCostByCode("user01", "0001");
		assertEquals("27", after0001.get("FIXED_COST_SHIHARAI_DAY"),
				"ロールバックにより0001の支払日が元の27に戻っていること");
		assertEquals(new BigDecimal("60000.00"), after0001.get("SHIHARAI_KINGAKU"),
				"ロールバックにより0001の支払金額が元の60000に戻っていること");
	}

	// ========== DB確認用ヘルパー ==========

	private Map<String, Object> findFixedCostByCode(String userId, String fixedCostCode) {
		List<Map<String, Object>> results = namedParamTemplate.queryForList(
				"SELECT * FROM FIXED_COST_TABLE WHERE USER_ID = :userId AND FIXED_COST_CODE = :code",
				new MapSqlParameterSource("userId", userId).addValue("code", fixedCostCode));
		return results.isEmpty() ? null : results.get(0);
	}
}
