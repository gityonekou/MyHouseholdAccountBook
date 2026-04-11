/**
 * 収支登録確認・完了機能のロールバック確認テストクラスです。
 *
 * <pre>
 * このクラスは IncomeAndExpenditureRegistConfirmIntegrationTest から分離した
 * ロールバック専用テストクラスです。
 *
 * [分離理由]
 * IncomeAndExpenditureRegistConfirmIntegrationTest には @Transactional が付与されており、
 * テスト全体が単一トランザクション内で実行されます。この場合、サービス層で例外が発生しても
 * テストトランザクションが rollback-only になるだけで、同一トランザクション内の変更は
 * テスト終了まで可視状態のままです。そのため、例外後のDBカウント確認でロールバックを
 * 証明することができません。
 *
 * [解決策]
 * このクラスには @Transactional を付与しません。これにより、サービス層の @Transactional
 * が「本物の」トランザクション境界となります。サービス層で例外が発生すると、
 * サービス層のトランザクションがロールバックされ、その後のDBカウント確認で
 * ロールバックを実際に確認できます。
 *
 * [DB初期化方法]
 * @Transactional がないため、テスト後のDBロールバックが行われません。
 * 代わりに @Sql の executionPhase = BEFORE_TEST_METHOD を使用して
 * 各テストメソッド実行前にスキーマを再作成します。
 *
 * [テストシナリオ]
 * ① 異常系：ロールバック確認_不正アクション例外（execRegistAction）
 *    - DATA_TYPE_LOAD + 未定義アクション("err") で例外発生
 *    - 例外発生前に挿入された収入レコード（3件）がロールバックされていること
 * ② 異常系：ロールバック確認_必須8項目未登録例外（execRegistAction）
 *    - 必須8項目のうち0046(被服費)が未登録で例外発生（全件INSERT後の最終チェック）
 *    - 例外発生前に挿入された収入・支出レコード（全件）がロールバックされていること
 *
 * [テストデータ]
 * - ユーザマスタ：user01
 * - 支出項目マスタ：0001～0060
 * - 202512：データなし（新規登録テスト用）
 * </pre>
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.incomeandexpenditure;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlConfig;

import com.yonetani.webapp.accountbook.application.usecase.account.incomeandexpenditure.IncomeAndExpenditureRegistConfirmUseCase;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.ExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeAndExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeTableRepository;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.session.ExpenditureRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.IncomeRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

/**
 *<pre>
 * 収支登録確認・完了機能のロールバック確認テストクラスです。
 *
 * @Transactional を付与しないことでサービス層の @Transactional を本物のトランザクション境界とし、
 * 例外発生時のロールバック動作をDBカウント確認で検証します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@SpringBootTest
@ActiveProfiles("test")
// @Transactional なし - サービス層の @Transactional をテスト対象トランザクション境界とする
// 各テスト前後に @Sql でDB状態を管理する：
//   BEFORE: cleanup → schema → data（テストデータを新鮮な状態でセットアップ）
//   AFTER:  cleanup（テスト後にコミット済みデータを削除し、後続テストクラスへの影響を防ぐ）
@Sql(scripts = {
	// 1. クリーンアップ: 前テストのコミット済みデータを全テーブルTRUNCATEで削除（@Transactionalなしのため必須）
	"/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureRegistRollbackTest-cleanup.sql",
	// 2. スキーマ定義: CREATE TABLE IF NOT EXISTS（テーブルが存在しない初回のみ有効）
	"/sql/initsql/schema_test.sql",
	// 3. テストデータ: ユーザマスタ+支出項目マスタ(0001-0060)+202511既存データ
	"/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureRegistConfirmIntegrationTest.sql"
}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD,
   config = @SqlConfig(encoding = "UTF-8"))
@Sql(scripts = {
	// テスト後クリーンアップ: @Transactionalなしのためコミット済みデータが残存する。
	// 後続テストクラス（@Transactionalあり）への影響を防ぐため、各テスト後にデータを削除する。
	"/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureRegistRollbackTest-cleanup.sql"
}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD,
   config = @SqlConfig(encoding = "UTF-8"))
class IncomeAndExpenditureRegistRollbackTest {

	@Autowired
	private IncomeAndExpenditureRegistConfirmUseCase useCase;

	@Autowired
	private IncomeTableRepository incomeRepository;

	@Autowired
	private ExpenditureTableRepository expenditureRepository;

	@Autowired
	private IncomeAndExpenditureTableRepository incomeAndExpenditureRepository;

	/**
	 * テスト用のログインユーザ情報を作成します。
	 */
	private LoginUserInfo createLoginUser() {
		return LoginUserInfo.from("user01", "テストユーザ01");
	}

	/**
	 * テスト用のSearchQueryを作成します。
	 */
	private SearchQueryUserIdAndYearMonth createSearchQuery(String yearMonth) {
		return SearchQueryUserIdAndYearMonth.from(
				UserId.from("user01"), TargetYearMonth.from(yearMonth));
	}

	/**
	 * 新規登録テスト用の収入セッションリスト（3件）を作成します。
	 * kubun=1(給与)/kubun=2(副業)/kubun=3(積立取崩)の3種混在。
	 */
	private List<IncomeRegistItem> createNewIncomeList() {
		List<IncomeRegistItem> list = new ArrayList<>();
		list.add(IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000001", "1", "12月給与", new BigDecimal("350000")));
		list.add(IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000002", "2", "12月副業収入", new BigDecimal("30000")));
		list.add(IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000003", "3", "12月積立取崩し", new BigDecimal("15000")));
		return list;
	}

	/**
	 *<pre>
	 * テスト①：異常系：ロールバック確認_不正アクション例外
	 *
	 * 【検証内容】
	 * ・DATA_TYPE_LOADかつ未定義アクション("err")の支出データを渡した場合に例外がスローされる
	 * ・IncomeAndExpenditureRegistUseCase line 1348 のelse節（未定義アクション）で
	 *   MyHouseholdAccountBookRuntimeExceptionがスローされる
	 * ・例外は収入INSERT(3件)後、支出処理中に発生するため、
	 *   サービス層の @Transactional によるロールバックで収入INSERT(3件)も取り消されることを確認
	 *
	 * 【ロールバック確認方法】
	 * @Transactional なしのテストクラスでは、サービス層の @Transactional が本物のトランザクション境界。
	 * 例外発生 → サービス層のトランザクションロールバック → テスト継続 → DBカウント=0を確認
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：ロールバック確認_不正アクション例外")
	void testExecRegistAction_RollbackOnInvalidAction() {
		// Given: テストユーザ、新規月（202512: DBデータなし）
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202512";

		// Given: 有効な収入リスト（3件）- 収入INSERTが先に実行されることを確認するための前提
		List<IncomeRegistItem> incomeList = createNewIncomeList();

		// Given: DATA_TYPE_LOAD + 不正アクション("err") の支出データ
		// → IncomeAndExpenditureRegistUseCase line 1348 で例外発生（収入INSERT3件の後）
		List<ExpenditureRegistItem> expenditureList = new ArrayList<>();
		expenditureList.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
				"err",  // 未定義アクション → else節でMyHouseholdAccountBookRuntimeException
				"001", "0051", "", "テスト食費", "1", "食費詳細", "", new BigDecimal("10000"), false));

		// When: 例外がスローされる
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> {
			useCase.execRegistAction(user, targetYearMonth, incomeList, expenditureList);
		});

		// Then: ロールバック確認（例外発生前に挿入された収入3件が取り消されていること）
		// ※ incomeAndExpenditureRepository.select() はレコードなしでも fromEmpty()(全フィールドnull)を返すため、
		//   assertNull(select()) ではなく getUserId()==null でレコード未登録を確認する
		SearchQueryUserIdAndYearMonth searchQuery = createSearchQuery("202512");
		assertEquals(0, incomeRepository.countById(searchQuery),
				"例外発生前に挿入された収入3件がロールバックされ、INCOME_TABLEが0件であること");
		assertEquals(0, expenditureRepository.countById(searchQuery),
				"EXPENDITURE_TABLEも0件であること（例外は収入INSERT後に発生するため元々0件だが念のため確認）");
		assertNull(incomeAndExpenditureRepository.select(searchQuery).getUserId(),
				"INCOME_AND_EXPENDITURE_TABLEも登録されていないこと（userId=nullでレコードなしを確認）");
	}

	/**
	 *<pre>
	 * テスト②：異常系：ロールバック確認_必須8項目未登録例外
	 *
	 * 【検証内容】
	 * ・必須8項目のうち0046(被服費)が未登録の場合に例外がスローされる
	 * ・IncomeAndExpenditureRegistUseCase line 1432の checkExpenditureAndSisyutuKingaku で
	 *   MyHouseholdAccountBookRuntimeExceptionがスローされる
	 * ・例外は全INSERT処理完了後の最終チェックで発生するため、
	 *   サービス層の @Transactional によるロールバックで収入・支出の全INSERT分が
	 *   取り消されることを確認
	 *
	 * 【ロールバック確認方法】
	 * @Transactional なしのテストクラスでは、サービス層の @Transactional が本物のトランザクション境界。
	 * 例外発生 → サービス層のトランザクションロールバック → テスト継続 → DBカウント=0を確認
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：ロールバック確認_必須8項目未登録例外")
	void testExecRegistAction_RollbackOnMandatoryItemsMissing() {
		// Given: テストユーザ、新規月（202512: DBデータなし）
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202512";

		// Given: 有効な収入リスト（3件）
		List<IncomeRegistItem> incomeList = createNewIncomeList();

		// Given: 必須8項目のうち0046(被服費)を欠いた支出リスト（7件）
		// → 全件INSERT完了後の checkExpenditureAndSisyutuKingaku(line 1432)で必須チェック失敗
		List<ExpenditureRegistItem> expenditureList = new ArrayList<>();
		expenditureList.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW, MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000001", "0051", "", "飲食(無駄遣いなし)", "1", "飲食詳細", "05", new BigDecimal("10000"), false));
		expenditureList.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW, MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000002", "0051", "", "飲食(無駄遣いB)", "2", "飲食B詳細", "05", new BigDecimal("2000"), false));
		expenditureList.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW, MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000003", "0051", "", "飲食(無駄遣いC)", "3", "飲食C詳細", "05", new BigDecimal("1000"), false));
		expenditureList.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW, MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000004", "0052", "", "一人プチ贅沢・外食", "1", "外食詳細", "10", new BigDecimal("5000"), false));
		expenditureList.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW, MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000005", "0050", "", "日用消耗品", "1", "日用消耗品詳細", "15", new BigDecimal("3000"), false));
		// 0046(被服費)は意図的に省略 → checkExpenditureAndSisyutuKingakuで必須チェック失敗
		expenditureList.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW, MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000006", "0007", "", "流動経費", "1", "流動経費詳細", "10", new BigDecimal("10000"), false));
		expenditureList.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW, MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000007", "0047", "", "住居設備", "1", "住居設備詳細", "25", new BigDecimal("2000"), false));

		// When: 例外がスローされる（収入3件+支出7件のINSERT完了後の必須チェックで失敗）
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> {
			useCase.execRegistAction(user, targetYearMonth, incomeList, expenditureList);
		});

		// Then: ロールバック確認（全件INSERT後の例外でも全データが取り消されていること）
		// ※ incomeAndExpenditureRepository.select() はレコードなしでも fromEmpty()(全フィールドnull)を返すため、
		//   assertNull(select()) ではなく getUserId()==null でレコード未登録を確認する
		SearchQueryUserIdAndYearMonth searchQuery = createSearchQuery("202512");
		assertEquals(0, incomeRepository.countById(searchQuery),
				"例外発生前に挿入された収入3件がロールバックされ、INCOME_TABLEが0件であること");
		assertEquals(0, expenditureRepository.countById(searchQuery),
				"例外発生前に挿入された支出7件がロールバックされ、EXPENDITURE_TABLEが0件であること");
		assertNull(incomeAndExpenditureRepository.select(searchQuery).getUserId(),
				"INCOME_AND_EXPENDITURE_TABLEも登録されていないこと（userId=nullでレコードなしを確認）");
	}
}
