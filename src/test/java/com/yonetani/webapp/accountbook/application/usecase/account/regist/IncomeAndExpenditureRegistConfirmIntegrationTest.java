/**
 * 収支登録確認・完了機能の結合テストクラスです。
 *
 * <pre>
 * IncomeAndExpenditureRegistUseCase の以下メソッドをテストします。
 *
 * [対象メソッド]
 * 1. readRegistCheckInfo - 登録内容確認画面表示
 * 2. readRegistCancelInfo - 登録キャンセル
 * 3. execRegistAction - 収支情報DB登録（4テーブル更新）
 *
 * [テストシナリオ]
 * ① 正常系：確認画面表示_収入支出一覧・削除アクション除外確認（readRegistCheckInfo）
 * ② 正常系：キャンセル_メッセージとリダイレクト設定確認（readRegistCancelInfo）
 * ③ 異常系：キャンセル_不正な対象年月（readRegistCancelInfo）
 * ④ 正常系：新規登録_全テーブルINSERT確認（execRegistAction）
 * ⑤ 正常系：更新_混合アクション（execRegistAction）
 * ⑥ 正常系：変更なし_全件NON_UPDATE（execRegistAction）
 * ⑦ 異常系：収入リスト空エラー（execRegistAction）
 * ⑧ 異常系：収入リストnullエラー（execRegistAction）
 *
 * [テストデータ]
 * - ユーザマスタ：user01
 * - 支出項目マスタ：0001～0060
 * - 202511：既存データあり（更新テスト用）
 * - 202512：データなし（新規登録テスト用）
 * </pre>
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.regist;

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
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditureItem;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.ExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeAndExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeTableRepository;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.IncomeAndExpenditureRegistCheckResponse;
import com.yonetani.webapp.accountbook.presentation.session.ExpenditureRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.IncomeRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

/**
 *<pre>
 * 収支登録確認・完了機能の結合テストクラスです。
 *
 * IncomeAndExpenditureRegistUseCaseの登録確認・完了関連メソッドについて、
 * UseCase→Repository→DB の結合動作を検証します。
 * execRegistActionでは4テーブル（収支、収入、支出、支出金額）への更新を検証します。
 *</pre>
 *
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
	"/sql/initsql/schema_test.sql",
	"/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureRegistConfirmIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
class IncomeAndExpenditureRegistConfirmIntegrationTest {

	@Autowired
	private IncomeAndExpenditureRegistUseCase useCase;

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
	 * 新規登録テスト用の収入セッションリスト（2件）を作成します。
	 * 全件 DATA_TYPE_NEW / ACTION_TYPE_ADD
	 */
	private List<IncomeRegistItem> createNewIncomeList() {
		List<IncomeRegistItem> list = new ArrayList<>();
		// 給与 350,000円
		list.add(IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000001", "1", "12月給与", new BigDecimal("350000")));
		// 副業 30,000円
		list.add(IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000002", "2", "12月副業収入", new BigDecimal("30000")));
		return list;
	}

	/**
	 * 新規登録テスト用の支出セッションリスト（8件：必須項目全網羅）を作成します。
	 * 全件 DATA_TYPE_NEW / ACTION_TYPE_ADD
	 * 買い物登録チェックで必要な8項目を全て含みます。
	 */
	private List<ExpenditureRegistItem> createNewExpenditureList() {
		List<ExpenditureRegistItem> list = new ArrayList<>();
		// 飲食(無駄遣いなし) 0051 kubun=1
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000001", "0051", "", "飲食(無駄遣いなし)", "1", "飲食詳細", "05", new BigDecimal("10000"), false));
		// 飲食(無駄遣いB) 0051 kubun=2
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000002", "0051", "", "飲食(無駄遣いB)", "2", "飲食B詳細", "05", new BigDecimal("2000"), false));
		// 飲食(無駄遣いC) 0051 kubun=3
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000003", "0051", "", "飲食(無駄遣いC)", "3", "飲食C詳細", "05", new BigDecimal("1000"), false));
		// 一人プチ贅沢・外食 0052
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000004", "0052", "", "一人プチ贅沢・外食", "1", "外食詳細", "10", new BigDecimal("5000"), false));
		// 日用消耗品 0050
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000005", "0050", "", "日用消耗品", "1", "日用消耗品詳細", "15", new BigDecimal("3000"), false));
		// 被服費 0046
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000006", "0046", "", "被服費", "1", "被服費詳細", "20", new BigDecimal("5000"), false));
		// 流動経費 0007
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000007", "0007", "", "流動経費", "1", "流動経費詳細", "10", new BigDecimal("10000"), false));
		// 住居設備 0047
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000008", "0047", "", "住居設備", "1", "住居設備詳細", "25", new BigDecimal("2000"), false));
		return list;
	}

	/**
	 * 更新テスト用の収入セッションリスト（3件：NON_UPDATE/UPDATE/ADD混合）を作成します。
	 */
	private List<IncomeRegistItem> createUpdateIncomeList() {
		List<IncomeRegistItem> list = new ArrayList<>();
		// NON_UPDATE: code=01, 給与350,000（変更なし）
		list.add(IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
				MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
				"01", "1", "11月給与", new BigDecimal("350000")));
		// UPDATE: code=02, 副業 30,000→50,000に変更
		list.add(IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
				MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE,
				"02", "2", "11月副業収入(増額)", new BigDecimal("50000")));
		// ADD: 新規追加 臨時収入20,000
		list.add(IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251101120000001", "1", "臨時収入", new BigDecimal("20000")));
		return list;
	}

	/**
	 * 更新テスト用の支出セッションリスト（11件：NON_UPDATE×8/UPDATE/DELETE/ADD混合）を作成します。
	 * 必須8項目はNON_UPDATEのまま（チェック通過のため）。
	 * 電気代(009)をUPDATE、ガス代(010)をDELETE、水道代を新規ADD。
	 */
	private List<ExpenditureRegistItem> createUpdateExpenditureList() {
		List<ExpenditureRegistItem> list = new ArrayList<>();
		// 必須8項目: 全てNON_UPDATE
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
				"001", "0051", "", "飲食(無駄遣いなし)", "1", "飲食詳細", "", new BigDecimal("10000"), false));
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
				"002", "0051", "", "飲食(無駄遣いB)", "2", "飲食B詳細", "", new BigDecimal("2000"), false));
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
				"003", "0051", "", "飲食(無駄遣いC)", "3", "飲食C詳細", "", new BigDecimal("1000"), false));
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
				"004", "0052", "", "一人プチ贅沢・外食", "1", "外食詳細", "", new BigDecimal("5000"), false));
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
				"005", "0050", "", "日用消耗品", "1", "日用消耗品詳細", "", new BigDecimal("3000"), false));
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
				"006", "0046", "", "被服費", "1", "被服費詳細", "", new BigDecimal("5000"), false));
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
				"007", "0007", "", "流動経費", "1", "流動経費詳細", "", new BigDecimal("10000"), false));
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
				"008", "0047", "", "住居設備", "1", "住居設備詳細", "", new BigDecimal("2000"), false));
		// UPDATE: 電気代(009) 12,000→15,000
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE,
				"009", "0037", "", "電気代(更新後)", "1", "電気代支払詳細(更新)", "30", new BigDecimal("15000"), false));
		// DELETE: ガス代(010)
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_DELETE,
				"010", "0038", "", "ガス代", "1", "ガス代支払詳細", "15", new BigDecimal("10000"), false));
		// ADD: 水道代 新規追加
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW, MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251101120000001", "0040", "", "水道代", "1", "水道代支払詳細", "20", new BigDecimal("8000"), false));
		return list;
	}

	// ===========================================
	// readRegistCheckInfo テスト
	// ===========================================

	/**
	 *<pre>
	 * テスト①：正常系：確認画面表示_収入支出一覧・削除アクション除外確認
	 *
	 * 【検証内容】
	 * ・セッションの収入2件＋支出3件（うち1件DELETE）を渡す
	 * ・削除アクション設定データが一覧から除外される
	 * ・表示用収入一覧2件、支出一覧2件（DELETE除外）
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：確認画面表示_収入支出一覧・削除アクション除外確認")
	void testReadRegistCheckInfo_NormalCase() {
		// Given: テストユーザ
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";

		// Given: 収入セッションリスト（2件）
		List<IncomeRegistItem> incomeList = new ArrayList<>();
		incomeList.add(IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251101120000001", "1", "テスト給与", new BigDecimal("300000")));
		incomeList.add(IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251101120000002", "2", "テスト副業", new BigDecimal("50000")));

		// Given: 支出セッションリスト（3件、うち1件DELETE）
		List<ExpenditureRegistItem> expenditureList = new ArrayList<>();
		expenditureList.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251101120000001", "0037", "", "テスト電気代", "1", "電気代詳細", "30", new BigDecimal("12000"), false));
		expenditureList.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
				MyHouseholdAccountBookContent.ACTION_TYPE_DELETE,
				"009", "0037", "", "削除対象電気代", "1", "削除対象", "", new BigDecimal("5000"), false));
		expenditureList.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251101120000002", "0051", "", "テスト食費", "1", "食費詳細", "", new BigDecimal("8000"), false));

		// When: 確認画面表示
		IncomeAndExpenditureRegistCheckResponse response = useCase.readRegistCheckInfo(
				user, targetYearMonth, incomeList, expenditureList);

		// Then: レスポンスが正しく返却される
		assertNotNull(response);

		// Then: 収入一覧（2件：全件表示）
		assertEquals(2, response.getIncomeListInfo().size(), "収入一覧は2件");

		// Then: 支出一覧（2件：DELETE除外で3件→2件）
		assertEquals(2, response.getExpenditureListInfo().size(), "支出一覧はDELETE除外で2件");
	}

	// ===========================================
	// readRegistCancelInfo テスト
	// ===========================================

	/**
	 *<pre>
	 * テスト②：正常系：キャンセル_メッセージとリダイレクト設定確認
	 *
	 * 【検証内容】
	 * ・キャンセルメッセージが正しく設定される
	 * ・transactionSuccessFullがtrueに設定される
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：キャンセル_メッセージとリダイレクト設定確認")
	void testReadRegistCancelInfo_NormalCase() {
		// Given: テストユーザ
		LoginUserInfo user = createLoginUser();

		// When: キャンセル実行
		IncomeAndExpenditureRegistCheckResponse response = useCase.readRegistCancelInfo(
				user, "202511", "202511");

		// Then: キャンセルメッセージ
		assertTrue(response.hasMessages());
		assertEquals("2025年11月度の収支登録をキャンセルしました。", response.getMessagesList().get(0));

		// Then: トランザクション完了フラグ
		assertTrue(response.isTransactionSuccessFull(), "transactionSuccessFull=true");
	}

	/**
	 *<pre>
	 * テスト③：異常系：キャンセル_不正な対象年月
	 *
	 * 【検証内容】
	 * ・不正なreturnYearMonth指定時に例外がスローされる
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：キャンセル_不正な対象年月")
	void testReadRegistCancelInfo_InvalidYearMonth() {
		// Given: テストユーザ、不正な年月
		LoginUserInfo user = createLoginUser();

		// When & Then: 例外がスローされる
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> {
			useCase.readRegistCancelInfo(user, "202511", "abc");
		});
	}

	// ===========================================
	// execRegistAction テスト
	// ===========================================

	/**
	 *<pre>
	 * テスト④：正常系：新規登録_全テーブルINSERT確認
	 *
	 * 【検証内容】
	 * ・202512月（DB未登録）へ新規登録
	 * ・INCOME_TABLE: 2件INSERT
	 * ・EXPENDITURE_TABLE: 8件INSERT
	 * ・INCOME_AND_EXPENDITURE_TABLE: 1件INSERT、金額計算の整合性
	 * ・transactionSuccessFull設定
	 * ・正常メッセージ
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：新規登録_全テーブルINSERT確認(202512)")
	void testExecRegistAction_NewRegistration() {
		// Given: テストユーザ、新規月
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202512";

		// Given: 新規登録用セッションデータ
		List<IncomeRegistItem> incomeList = createNewIncomeList();
		List<ExpenditureRegistItem> expenditureList = createNewExpenditureList();

		// When: DB登録実行
		IncomeAndExpenditureRegistCheckResponse response = useCase.execRegistAction(
				user, targetYearMonth, incomeList, expenditureList);

		// Then: レスポンスが正しく返却される
		assertNotNull(response);
		assertTrue(response.isTransactionSuccessFull(), "transactionSuccessFull=true");
		assertTrue(response.hasMessages());
		assertEquals("2025年12月度の収支情報を登録しました。", response.getMessagesList().get(0));

		// Then: INCOME_TABLE検証（2件INSERT）
		SearchQueryUserIdAndYearMonth searchQuery = createSearchQuery("202512");
		int incomeCount = incomeRepository.countById(searchQuery);
		assertEquals(2, incomeCount, "INCOME_TABLEに2件登録されていること");

		// Then: EXPENDITURE_TABLE検証（8件INSERT）
		int expenditureCount = expenditureRepository.countById(searchQuery);
		assertEquals(8, expenditureCount, "EXPENDITURE_TABLEに8件登録されていること");

		// Then: INCOME_AND_EXPENDITURE_TABLE検証
		IncomeAndExpenditureItem syuusiItem = incomeAndExpenditureRepository.select(searchQuery);
		assertNotNull(syuusiItem, "INCOME_AND_EXPENDITURE_TABLEに1件登録されていること");
		// 収入金額 = 350,000 + 30,000 = 380,000（kubun=1,2はregularIncome）
		assertEquals(new BigDecimal("380000.00"), syuusiItem.getRegularIncomeAmount().getValue());
		// 支出金額 = 10000+2000+1000+5000+3000+5000+10000+2000 = 38,000
		assertEquals(new BigDecimal("38000.00"), syuusiItem.getExpenditureAmount().getValue());
	}

	/**
	 *<pre>
	 * テスト⑤：正常系：更新_混合アクション（NON_UPDATE/UPDATE/ADD/DELETE）
	 *
	 * 【検証内容】
	 * ・202511月（既存データ）を更新
	 * ・収入: NON_UPDATE(01) + UPDATE(02: 30000→50000) + ADD(新規: 20000)
	 * ・支出: NON_UPDATE×8 + UPDATE(009: 12000→15000) + DELETE(010) + ADD(新規: 8000)
	 * ・INCOME_AND_EXPENDITURE_TABLEの金額が再計算される
	 * ・transactionSuccessFull設定
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：更新_混合アクション(NON_UPDATE/UPDATE/ADD/DELETE)")
	void testExecRegistAction_UpdateMixedActions() {
		// Given: テストユーザ、既存月
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";

		// Given: 更新用セッションデータ
		List<IncomeRegistItem> incomeList = createUpdateIncomeList();
		List<ExpenditureRegistItem> expenditureList = createUpdateExpenditureList();

		// When: DB登録実行
		IncomeAndExpenditureRegistCheckResponse response = useCase.execRegistAction(
				user, targetYearMonth, incomeList, expenditureList);

		// Then: レスポンスが正しく返却される
		assertNotNull(response);
		assertTrue(response.isTransactionSuccessFull(), "transactionSuccessFull=true");
		assertTrue(response.hasMessages());
		assertEquals("2025年11月度の収支情報を登録しました。", response.getMessagesList().get(0));

		// Then: INCOME_TABLE検証（元2件 + 1件追加 = 3件）
		SearchQueryUserIdAndYearMonth searchQuery = createSearchQuery("202511");
		int incomeCount = incomeRepository.countById(searchQuery);
		assertEquals(3, incomeCount, "INCOME_TABLEが3件であること（2件既存＋1件追加）");

		// Then: EXPENDITURE_TABLE検証（元10件 + 1件追加 = 11件、論理削除1件含む）
		int expenditureCount = expenditureRepository.countById(searchQuery);
		assertEquals(11, expenditureCount, "EXPENDITURE_TABLEが11件であること（10件既存＋1件追加）");

		// Then: INCOME_AND_EXPENDITURE_TABLE検証
		IncomeAndExpenditureItem syuusiItem = incomeAndExpenditureRepository.select(searchQuery);
		assertNotNull(syuusiItem);
		// 収入金額 = NON_UPDATE(350000) + UPDATE(50000) + ADD(20000) = 420,000
		assertEquals(new BigDecimal("420000.00"), syuusiItem.getRegularIncomeAmount().getValue(),
				"収入金額=NON_UPDATE350000+UPDATE50000+ADD20000=420000");
		// 支出金額 = NON_UPDATE(10000+2000+1000+5000+3000+5000+10000+2000) + UPDATE(15000) + ADD(8000) = 61,000
		// DELETE(10000)は収支合計に含まない
		assertEquals(new BigDecimal("61000.00"), syuusiItem.getExpenditureAmount().getValue(),
				"支出金額=NON_UPDATE38000+UPDATE15000+ADD8000=61000(DELETE除外)");
	}

	/**
	 *<pre>
	 * テスト⑥：正常系：変更なし_全件NON_UPDATE
	 *
	 * 【検証内容】
	 * ・全セッションデータがNON_UPDATEの場合
	 * ・"変更箇所がありませんでした"メッセージが設定される
	 * ・transactionSuccessFull設定
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：変更なし_全件NON_UPDATE")
	void testExecRegistAction_NoChanges() {
		// Given: テストユーザ、既存月
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";

		// Given: 全件NON_UPDATEの収入セッションリスト
		List<IncomeRegistItem> incomeList = new ArrayList<>();
		incomeList.add(IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
				MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
				"01", "1", "11月給与", new BigDecimal("350000")));
		incomeList.add(IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
				MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
				"02", "2", "11月副業収入", new BigDecimal("30000")));

		// Given: 全件NON_UPDATEの支出セッションリスト（必須8項目）
		List<ExpenditureRegistItem> expenditureList = new ArrayList<>();
		expenditureList.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
				"001", "0051", "", "飲食(無駄遣いなし)", "1", "飲食詳細", "", new BigDecimal("10000"), false));
		expenditureList.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
				"002", "0051", "", "飲食(無駄遣いB)", "2", "飲食B詳細", "", new BigDecimal("2000"), false));

		// When: DB登録実行
		IncomeAndExpenditureRegistCheckResponse response = useCase.execRegistAction(
				user, targetYearMonth, incomeList, expenditureList);

		// Then: 変更なしメッセージ
		assertNotNull(response);
		assertTrue(response.isTransactionSuccessFull(), "transactionSuccessFull=true");
		assertTrue(response.hasMessages());
		assertEquals("【注意】2025年11月度の収支情報の変更箇所がありませんでした。", response.getMessagesList().get(0));
	}

	/**
	 *<pre>
	 * テスト⑦：異常系：収入リスト空エラー
	 *
	 * 【検証内容】
	 * ・空の収入リストを渡した場合に例外がスローされる
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：収入リスト空エラー")
	void testExecRegistAction_EmptyIncomeList() {
		// Given: テストユーザ
		LoginUserInfo user = createLoginUser();

		// When & Then: 空リストで例外
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> {
			useCase.execRegistAction(user, "202511", new ArrayList<>(), new ArrayList<>());
		});
	}

	/**
	 *<pre>
	 * テスト⑧：異常系：収入リストnullエラー
	 *
	 * 【検証内容】
	 * ・nullの収入リストを渡した場合に例外がスローされる
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：収入リストnullエラー")
	void testExecRegistAction_NullIncomeList() {
		// Given: テストユーザ
		LoginUserInfo user = createLoginUser();

		// When & Then: nullで例外
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> {
			useCase.execRegistAction(user, "202511", null, new ArrayList<>());
		});
	}
}
