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
 *    - 収入：kubun=1/2/3混在(3件)、積立取崩金額(WITHDREW_KINGAKU)確認
 *    - 支出：9件INSERT(clearStartFlg=false×8/clearStartFlg=true×1)
 *    - EXPENDITURE_TABLE：clearStartFlg=true→YOTEI=入力値/KINGAKU=0、clearStartFlg=false→YOTEI=KINGAKU=入力値確認
 *    - 支出予定金額(EXPENDITURE_ESTIMATE_KINGAKU)確認
 * ⑤ 正常系：更新_混合アクション（execRegistAction）
 *    - 収入：kubun=3を含む各操作（NON_UPDATE/UPDATE/ADD/DELETE/kubun変更）
 *    - 支出：NON_UPDATE×8+UPDATE/DELETE/ADD+無駄遣いB/C(NON_UPDATE/UPDATE増額/UPDATE減額/DELETE/ADD)
 *    - EXPENDITURE_TABLE：更新月ADD→YOTEI=0/KINGAKU=入力値確認
 *    - SISYUTU_KINGAKU_TABLE：YOTEIは変更なし、SIHARAI_DATE=MAX、無駄遣いB/C各操作確認
 *    - 支出予定金額(EXPENDITURE_ESTIMATE_KINGAKU)が変更されないことを確認
 * ⑥ 正常系：変更なし_全件NON_UPDATE（execRegistAction）
 * ⑦ 異常系：収入リスト空エラー（execRegistAction）
 * ⑧ 異常系：収入リストnullエラー（execRegistAction）
 * ⑨ 異常系：必須8項目未登録例外（execRegistAction）
 *    ※ロールバック確認は IncomeAndExpenditureRegistRollbackTest で実施
 *
 * [テストデータ]
 * - ユーザマスタ：user01
 * - 支出項目マスタ：0001～0060
 * - 202511：既存データあり（更新テスト用）
 *   - 収入4件（給与/副業/積立取崩×2）
 *   - 支出14件（必須8項目+電気代+ガス代+交際費B×2+趣味娯楽C×2）
 * - 202512：データなし（新規登録テスト用）
 * </pre>
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.incomeandexpenditure;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import com.yonetani.webapp.accountbook.domain.model.account.expenditure.ExpenditureAmountItem;
import com.yonetani.webapp.accountbook.domain.model.account.expenditure.ExpenditureItem;
import com.yonetani.webapp.accountbook.domain.model.account.expenditure.ExpenditureItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.incomeandexpenditure.IncomeAndExpenditureItem;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonthAndExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.repository.account.expenditure.ExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.expenditure.SisyutuKingakuTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.income.IncomeTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.incomeandexpenditure.IncomeAndExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemCode;
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
	private IncomeAndExpenditureRegistConfirmUseCase useCase;

	@Autowired
	private IncomeTableRepository incomeRepository;

	@Autowired
	private ExpenditureTableRepository expenditureRepository;

	@Autowired
	private IncomeAndExpenditureTableRepository incomeAndExpenditureRepository;

	@Autowired
	private SisyutuKingakuTableRepository sisyutuKingakuTableRepository;

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
	 * SISYUTU_KINGAKU_TABLE検索用のSearchQueryを作成します。
	 */
	private SearchQueryUserIdAndYearMonthAndExpenditureItemCode createExpenditureAmountSearchQuery(
			String yearMonth, String expenditureItemCode) {
		return SearchQueryUserIdAndYearMonthAndExpenditureItemCode.from(
				UserId.from("user01"),
				TargetYearMonth.from(yearMonth),
				ExpenditureItemCode.from(expenditureItemCode));
	}

	/**
	 * 新規登録テスト用の収入セッションリスト（3件）を作成します。
	 * 全件 DATA_TYPE_NEW / ACTION_TYPE_ADD
	 * kubun=1(給与)/kubun=2(副業)/kubun=3(積立取崩)の3種混在。
	 * kubun=3はREGULAR_INCOMEではなくWITHDREW_KINGAKUに計上されることを確認する。
	 */
	private List<IncomeRegistItem> createNewIncomeList() {
		List<IncomeRegistItem> list = new ArrayList<>();
		// 給与(kubun=1) 350,000円 → INCOME_KINGAKU加算
		list.add(IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000001", "1", "12月給与", new BigDecimal("350000")));
		// 副業(kubun=2) 30,000円 → INCOME_KINGAKU加算
		list.add(IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000002", "2", "12月副業収入", new BigDecimal("30000")));
		// 積立取崩し(kubun=3) 15,000円 → WITHDREW_KINGAKU加算（INCOME_KINGAKUには加算しない）
		list.add(IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000003", "3", "12月積立取崩し", new BigDecimal("15000")));
		return list;
	}

	/**
	 * 新規登録テスト用の支出セッションリスト（9件：必須項目全網羅＋clearStartFlg=true）を作成します。
	 * 全件 DATA_TYPE_NEW / ACTION_TYPE_ADD
	 * 買い物登録チェックで必要な8項目＋clearStartFlg=true確認用の電気代(0037)を含みます。
	 * ・clearStartFlg=false(8件)：EXPENDITURE_ESTIMATE_KINGAKU=EXPENDITURE_KINGAKU=入力値
	 * ・clearStartFlg=true(1件: 電気代0037)：EXPENDITURE_ESTIMATE_KINGAKU=入力値、EXPENDITURE_KINGAKU=0円
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
		// 電気代 0037 kubun=1 clearStartFlg=true (0円開始)
		// → EXPENDITURE_ESTIMATE_KINGAKU=12000(入力値)、EXPENDITURE_KINGAKU=0円(clearStartFlg=true)
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000009", "0037", "", "電気代", "1", "電気代詳細", "15", new BigDecimal("12000"), true));
		return list;
	}

	/**
	 * 更新テスト用の収入セッションリスト（5件：NON_UPDATE/UPDATE(kubun変更)/ADD/UPDATE(kubun変更)/DELETE混合）を作成します。
	 *
	 * <pre>
	 * DBの初期データ(202511):
	 *   01: kubun=1(給与) 350,000
	 *   02: kubun=2(副業) 30,000
	 *   03: kubun=3(積立取崩) 20,000
	 *   04: kubun=3(積立取崩) 10,000
	 *
	 * 操作:
	 *   01: NON_UPDATE (変更なし) kubun=1 → regularIncome: +350,000
	 *   02: UPDATE 副業→積立取崩(kubun=2→3) 40,000 → withdrew: +40,000
	 *   new: ADD 積立取崩(kubun=3) 15,000 → withdrew: +15,000
	 *   03: UPDATE 積立取崩→副業(kubun=3→2) 25,000 → regularIncome: +25,000
	 *   04: DELETE 積立取崩(kubun=3) 10,000 → 除外
	 *
	 * 期待結果:
	 *   INCOME_KINGAKU(regularIncome) = 350,000 + 25,000 = 375,000
	 *   WITHDREW_KINGAKU(withdrew) = 40,000 + 15,000 = 55,000
	 *   INCOME_TABLE件数 = 4(既存) + 1(ADD) = 5件（DELETE_FLGフィルタなし）
	 * </pre>
	 */
	private List<IncomeRegistItem> createUpdateIncomeList() {
		List<IncomeRegistItem> list = new ArrayList<>();
		// NON_UPDATE: code=01, 給与350,000（変更なし）→ regularIncome加算
		list.add(IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
				MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
				"01", "1", "11月給与", new BigDecimal("350000")));
		// UPDATE: code=02, 副業→積立取崩し(kubun=2→3) 40,000 → withdrew加算
		list.add(IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
				MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE,
				"02", "3", "11月副業→積立取崩し", new BigDecimal("40000")));
		// ADD: 新規追加 積立取崩し(kubun=3) 15,000 → withdrew加算
		list.add(IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW,
				MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251101120000001", "3", "臨時積立取崩し", new BigDecimal("15000")));
		// UPDATE: code=03, 積立取崩し→副業(kubun=3→2) 25,000 → regularIncome加算
		list.add(IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
				MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE,
				"03", "2", "11月積立取崩し→副業", new BigDecimal("25000")));
		// DELETE: code=04, 積立取崩し削除 → 収支計算から除外
		list.add(IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
				MyHouseholdAccountBookContent.ACTION_TYPE_DELETE,
				"04", "3", "11月積立取崩し(削除)", new BigDecimal("10000")));
		return list;
	}

	/**
	 * 更新テスト用の支出セッションリスト（16件：NON_UPDATE×9/UPDATE×3/DELETE×2/ADD×2混合）を作成します。
	 * 必須8項目はNON_UPDATEのまま（チェック通過のため）。
	 *
	 * <pre>
	 * 水光熱通費(0036) 操作:
	 *   009(0037 電気代) UPDATE 12,000→15,000(siharaiDate="30")
	 *   010(0038 ガス代) DELETE
	 *   新規(0040 水道代) ADD 8,000(siharaiDate="20")
	 *
	 * 趣味娯楽(0055) 無駄遣いB(kubun=2)/C(kubun=3) 操作:
	 *   011(0056 交際費B) NON_UPDATE kubun=2 3,000
	 *   012(0056 交際費B) UPDATE増額 kubun=2 2,000→4,000(siharaiDate="25")
	 *   013(0057 趣味娯楽C) UPDATE減額 kubun=3 1,500→500
	 *   014(0057 趣味娯楽C) DELETE kubun=3 2,000
	 *   新規(0059 趣味娯楽その他B) ADD kubun=2 1,000
	 *
	 * 期待結果（支出計算）:
	 *   必須8項目: 38,000(NON_UPDATE)
	 *   電気代: 15,000(UPDATE), ガス代: 0(DELETE), 水道代: 8,000(ADD)
	 *   交際費B: 3,000(NON_UPDATE) + 4,000(UPDATE) = 7,000
	 *   趣味娯楽C: 500(UPDATE) + 0(DELETE) = 500
	 *   趣味娯楽その他B: 1,000(ADD)
	 *   EXPENDITURE_KINGAKU = 38,000+15,000+8,000+7,000+500+1,000 = 69,500
	 *   EXPENDITURE_TABLE件数 = 14(既存) + 2(ADD) = 16件（DELETE_FLGフィルタなし）
	 * </pre>
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
		// UPDATE: 電気代(009) 0037 kubun=1 12,000→15,000 siharaiDate=2025-11-30
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE,
				"009", "0037", "", "電気代(更新後)", "1", "電気代支払詳細(更新)", "30", new BigDecimal("15000"), false));
		// DELETE: ガス代(010) 0038 kubun=1 10,000
		// ※siharaiDate:DB値はnullだが、createExpenditureItemでPaymentDate.from(yearMonth,day)が呼ばれるため
		//   空文字列は不可（[day=]エラー）。"15"等の有効な2桁日付が必要。
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_DELETE,
				"010", "0038", "", "ガス代", "1", "ガス代支払詳細", "15", new BigDecimal("10000"), false));
		// ADD: 水道代 0040 kubun=1 8,000 siharaiDate=2025-11-20
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW, MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251101120000001", "0040", "", "水道代", "1", "水道代支払詳細", "20", new BigDecimal("8000"), false));
		// NON_UPDATE: 交際費B(011) 0056 kubun=2 3,000
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
				"011", "0056", "", "交際費B(NON_UPDATE)", "2", "交際費B詳細", "", new BigDecimal("3000"), false));
		// UPDATE増額: 交際費B(012) 0056 kubun=2 2,000→4,000 siharaiDate=2025-11-25
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE,
				"012", "0056", "", "交際費B(UPDATE増額)", "2", "交際費B詳細(更新)", "25", new BigDecimal("4000"), false));
		// UPDATE減額: 趣味娯楽C(013) 0057 kubun=3 1,500→500 siharaiDate=2025-11-01
		// ※siharaiDate:UPDATE/DELETEはPaymentDate.from(yearMonth,day)が呼ばれるため空文字列は不可。
		//   "01"を設定 → 0057 SIHARAI_DATE=MAX(null, 2025-11-01)=2025-11-01
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE,
				"013", "0057", "", "趣味娯楽C(UPDATE減額)", "3", "趣味娯楽C詳細(更新)", "01", new BigDecimal("500"), false));
		// DELETE: 趣味娯楽C(014) 0057 kubun=3 2,000
		// ※DELETE: SIHARAI_DATEはholderの現在値(2025-11-01 from "013" UPDATE)を保持
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_DELETE,
				"014", "0057", "", "趣味娯楽C(DELETE)", "3", "趣味娯楽C詳細", "01", new BigDecimal("2000"), false));
		// ADD: 趣味娯楽その他B 0059 kubun=2 1,000 siharaiDate=2025-11-10
		// ※siharaiDate:ADD処理でもcreateExpenditureItemでPaymentDate.from(yearMonth,day)が呼ばれるため
		//   空文字列は不可。"10"を設定 → 0059 SIHARAI_DATE=2025-11-10
		list.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW, MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251101120000002", "0059", "", "趣味娯楽その他B(ADD)", "2", "趣味娯楽その他B詳細", "10", new BigDecimal("1000"), false));
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
	 * ・INCOME_TABLE: 3件INSERT（kubun=1/2/3混在）
	 * ・EXPENDITURE_TABLE: 9件INSERT（clearStartFlg=false×8、clearStartFlg=true×1）
	 *   - clearStartFlg=false(代表: 0052): EXPENDITURE_ESTIMATE_KINGAKU=EXPENDITURE_KINGAKU=入力値
	 *   - clearStartFlg=true(0037電気代): EXPENDITURE_ESTIMATE_KINGAKU=入力値、EXPENDITURE_KINGAKU=0円
	 * ・INCOME_AND_EXPENDITURE_TABLE: 1件INSERT、金額計算の整合性
	 *   - INCOME_KINGAKU(regularIncome) = 350,000+30,000 = 380,000（kubun=1/2）
	 *   - WITHDREW_KINGAKU(withdrawing) = 15,000（kubun=3）
	 *   - EXPENDITURE_ESTIMATE_KINGAKU = 50,000（clearStartFlg=false:38,000 + clearStartFlg=true:12,000）
	 *   - EXPENDITURE_KINGAKU = 38,000（clearStartFlg=trueの0037はKINGAKU=0のため合計に含まれない）
	 *   - INCOME_AND_EXPENDITURE_KINGAKU = 357,000（(380,000+15,000)-38,000）
	 * ・SISYUTU_KINGAKU_TABLE:
	 *   - 0051(食費): 支出金額=13,000, B=2,000, C=1,000, YOTEI=13,000, SIHARAI_DATE=2025-12-05
	 *   - 0049(飲食日用品Level1): 支出金額=21,000, B=2,000, C=1,000, YOTEI=21,000, SIHARAI_DATE=2025-12-15
	 *   - 0037(電気代): YOTEI=12,000, KINGAKU=0(clearStartFlg=true), SIHARAI_DATE=2025-12-15
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

		// Given: 新規登録用セッションデータ（収入3件: kubun=1/2/3、支出9件: clearStartFlg=false×8/true×1）
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

		// Then: INCOME_TABLE検証（3件INSERT：kubun=1/2/3）
		SearchQueryUserIdAndYearMonth searchQuery = createSearchQuery("202512");
		int incomeCount = incomeRepository.countBy(searchQuery);
		assertEquals(3, incomeCount, "INCOME_TABLEに3件登録されていること(kubun=1/2/3)");

		// Then: EXPENDITURE_TABLE検証（9件INSERT: clearStartFlg=false×8、clearStartFlg=true×1）
		int expenditureCount = expenditureRepository.countBy(searchQuery);
		assertEquals(9, expenditureCount, "EXPENDITURE_TABLEに9件登録されていること(clearStartFlg=false×8+true×1)");

		// Then: INCOME_AND_EXPENDITURE_TABLE検証
		IncomeAndExpenditureItem syuusiItem = incomeAndExpenditureRepository.select(searchQuery);
		assertNotNull(syuusiItem, "INCOME_AND_EXPENDITURE_TABLEに1件登録されていること");
		// 通常収入金額 = 350,000(kubun=1) + 30,000(kubun=2) = 380,000（kubun=3は含まない）
		assertEquals(new BigDecimal("380000.00"), syuusiItem.getRegularIncomeAmount().getValue(),
				"INCOME_KINGAKU=380000(kubun=1:350000+kubun=2:30000)");
		// 積立金取崩金額 = 15,000（kubun=3のみ）
		assertEquals(new BigDecimal("15000.00"), syuusiItem.getWithdrawingAmount().getValue(),
				"WITHDREW_KINGAKU=15000(kubun=3:15000)");
		// 支出金額 = 10000+2000+1000+5000+3000+5000+10000+2000 = 38,000
		// (clearStartFlg=trueの0037はKINGAKU=0のため合計に含まれない)
		assertEquals(new BigDecimal("38000.00"), syuusiItem.getExpenditureAmount().getValue(),
				"EXPENDITURE_KINGAKU=38000(clearStartFlg=trueの0037はKINGAKU=0のため含まれない)");
		// 支出予定金額 = clearStartFlg=false:38,000 + clearStartFlg=true:12,000 = 50,000
		// (新規登録月はclearStartFlgに関わらず入力値がYOTEIに設定される)
		assertEquals(new BigDecimal("50000.00"), syuusiItem.getExpectedExpenditureAmount().getValue(),
				"EXPENDITURE_ESTIMATE_KINGAKU=50000(38000+12000: 新規登録のためYOTEI=入力値)");
		// 収支金額 = (通常収入380,000 + 積立取崩15,000) - 支出38,000 = 357,000
		assertEquals(new BigDecimal("357000.00"), syuusiItem.getBalanceAmount().getValue(),
				"INCOME_AND_EXPENDITURE_KINGAKU=357000((380000+15000)-38000)");

		// Then: EXPENDITURE_TABLE直接確認（代表的なclearStartFlgパターン）
		// clearStartFlg=true パターン: EXPENDITURE_ESTIMATE_KINGAKU=入力値、EXPENDITURE_KINGAKU=0円
		// ※0037は1件のみのため isOne() で確認
		ExpenditureItemInquiryList expList0037 = expenditureRepository.findByExpenditureItemCode(
				createExpenditureAmountSearchQuery("202512", "0037"));
		assertTrue(expList0037.isOne(), "0037(電気代)のEXPENDITURE_TABLEが1件存在すること");
		ExpenditureItem expItem0037 = expList0037.getValues().get(0);
		assertEquals(new BigDecimal("12000.00"), expItem0037.getExpectedExpenditureAmount().getValue(),
				"0037:EXPENDITURE_ESTIMATE_KINGAKU=12000(新規登録:YOTEI=入力値, clearStartFlg=true)");
		assertEquals(new BigDecimal("0.00"), expItem0037.getExpenditureAmount().getValue(),
				"0037:EXPENDITURE_KINGAKU=0(clearStartFlg=true:0円開始)");

		// clearStartFlg=false パターン(代表: 0052一人プチ贅沢外食): EXPENDITURE_ESTIMATE_KINGAKU=EXPENDITURE_KINGAKU=入力値
		ExpenditureItemInquiryList expList0052 = expenditureRepository.findByExpenditureItemCode(
				createExpenditureAmountSearchQuery("202512", "0052"));
		assertTrue(expList0052.isOne(), "0052(一人プチ贅沢外食)のEXPENDITURE_TABLEが1件存在すること");
		ExpenditureItem expItem0052 = expList0052.getValues().get(0);
		assertEquals(new BigDecimal("5000.00"), expItem0052.getExpectedExpenditureAmount().getValue(),
				"0052:EXPENDITURE_ESTIMATE_KINGAKU=5000(新規登録:YOTEI=入力値, clearStartFlg=false)");
		assertEquals(new BigDecimal("5000.00"), expItem0052.getExpenditureAmount().getValue(),
				"0052:EXPENDITURE_KINGAKU=5000(clearStartFlg=false:入力値をそのまま設定)");

		// Then: SISYUTU_KINGAKU_TABLE検証
		// 0051(食費): 3件登録(kubun=1:10000, kubun=2:2000, kubun=3:1000)の集計確認
		// YOTEI=13000(新規登録のためYOTEI=支出金額)、SIHARAI_DATE=MAX(05,05,05)=2025-12-05
		ExpenditureAmountItem item0051 = sisyutuKingakuTableRepository.findByPrimaryKey(
				createExpenditureAmountSearchQuery("202512", "0051"));
		assertNotNull(item0051, "0051(食費)の支出金額テーブルが登録されていること");
		assertEquals(new BigDecimal("13000.00"), item0051.getExpenditureAmount().getValue(),
				"0051:支出金額=13000(kubun1:10000+kubun2:2000+kubun3:1000)");
		assertEquals(new BigDecimal("2000.00"), item0051.getMinorWasteExpenditureAmount().getValue(),
				"0051:無駄遣い(軽度)B=2000(kubun=2)");
		assertEquals(new BigDecimal("1000.00"), item0051.getSevereWasteExpenditureAmount().getValue(),
				"0051:無駄遣い(重度)C=1000(kubun=3)");
		assertEquals(new BigDecimal("13000.00"), item0051.getExpectedExpenditureAmount().getValue(),
				"0051:支出予定金額(YOTEI)=13000(新規登録のためYOTEI=支出金額)");
		assertEquals(LocalDate.of(2025, 12, 5), item0051.getPaymentDate().getValue(),
				"0051:SIHARAI_DATE=2025-12-05(3件全て同日)");

		// 0049(飲食日用品, Level1): 0051+0052+0050の親Level1集計値の伝搬確認
		// YOTEI=21000、SIHARAI_DATE=MAX(0051:12-05, 0052:12-10, 0050:12-15)=2025-12-15
		ExpenditureAmountItem item0049 = sisyutuKingakuTableRepository.findByPrimaryKey(
				createExpenditureAmountSearchQuery("202512", "0049"));
		assertNotNull(item0049, "0049(飲食日用品)の支出金額テーブルが登録されていること");
		assertEquals(new BigDecimal("21000.00"), item0049.getExpenditureAmount().getValue(),
				"0049:支出金額=21000(0051:13000+0052:5000+0050:3000)");
		assertEquals(new BigDecimal("2000.00"), item0049.getMinorWasteExpenditureAmount().getValue(),
				"0049:無駄遣い(軽度)B=2000(0051より伝搬)");
		assertEquals(new BigDecimal("1000.00"), item0049.getSevereWasteExpenditureAmount().getValue(),
				"0049:無駄遣い(重度)C=1000(0051より伝搬)");
		assertEquals(new BigDecimal("21000.00"), item0049.getExpectedExpenditureAmount().getValue(),
				"0049:支出予定金額(YOTEI)=21000(新規登録のためYOTEI=支出金額)");
		assertEquals(LocalDate.of(2025, 12, 15), item0049.getPaymentDate().getValue(),
				"0049:SIHARAI_DATE=2025-12-15(MAX(0051:12-05,0052:12-10,0050:12-15)=12-15)");

		// 0037(電気代, Level3, parent=0036): clearStartFlg=true 12,000円
		// YOTEI=12000(新規登録: YOTEI=入力値)、KINGAKU=0(clearStartFlg=true:0円開始)
		// SIHARAI_DATE=2025-12-15(siharaiDate="15")
		ExpenditureAmountItem item0037 = sisyutuKingakuTableRepository.findByPrimaryKey(
				createExpenditureAmountSearchQuery("202512", "0037"));
		assertNotNull(item0037, "0037(電気代)の支出金額テーブルが登録されていること");
		assertEquals(new BigDecimal("12000.00"), item0037.getExpectedExpenditureAmount().getValue(),
				"0037:YOTEI=12000(新規登録: YOTEI=入力値, clearStartFlg=trueでも変わらない)");
		assertEquals(new BigDecimal("0.00"), item0037.getExpenditureAmount().getValue(),
				"0037:支出金額=0(clearStartFlg=true:0円開始)");
		assertEquals(LocalDate.of(2025, 12, 15), item0037.getPaymentDate().getValue(),
				"0037:SIHARAI_DATE=2025-12-15(siharaiDate=\"15\")");
	}

	/**
	 *<pre>
	 * テスト⑤：正常系：更新_混合アクション（NON_UPDATE/UPDATE/ADD/DELETE + kubun=3 + 無駄遣いB/C）
	 *
	 * 【検証内容】
	 * ・202511月（既存データ）を更新
	 * ・収入: NON_UPDATE(01) + UPDATE(02:副業→積立取崩) + ADD(新規:積立取崩) + UPDATE(03:積立取崩→副業) + DELETE(04)
	 *   - INCOME_KINGAKU(regularIncome) = 350,000(NON_UPDATE) + 25,000(UPDATE:03) = 375,000
	 *   - WITHDREW_KINGAKU(withdrew) = 40,000(UPDATE:02) + 15,000(ADD) = 55,000
	 *   - INCOME_TABLE件数 = 4(既存) + 1(ADD) = 5件（DELETE_FLGフィルタなし）
	 * ・支出: NON_UPDATE×8+UPDATE(009:+3000)+DELETE(010)+ADD(0040:+8000)+
	 *         NON_UPDATE(011:0056-B)+UPDATE増額(012:0056-B +2000)+UPDATE減額(013:0057-C -1000)+
	 *         DELETE(014:0057-C 2000)+ADD(0059-B:+1000)
	 *   - EXPENDITURE_KINGAKU = 38,000+15,000+8,000+3,000+4,000+500+1,000 = 69,500
	 *   - EXPENDITURE_TABLE件数 = 14(既存) + 2(ADD) = 16件
	 * ・INCOME_AND_EXPENDITURE_TABLE:
	 *   - EXPENDITURE_ESTIMATE_KINGAKU = 68,500（更新SQLでは変更されない）
	 *   - INCOME_AND_EXPENDITURE_KINGAKU = 360,500（(375,000+55,000)-69,500）
	 * ・SISYUTU_KINGAKU_TABLE:
	 *   - YOTEI（支出予定金額）は更新・削除操作で変更されないことを確認
	 *   - SIHARAI_DATE はMAX値が使用されることを確認
	 *   - 無駄遣いB/C(KINGAKU_B/C)はkubun=2/3の各操作（NON_UPDATE/UPDATE増額/UPDATE減額/DELETE/ADD）で正しく計算されることを確認
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

		// Then: INCOME_TABLE検証（元4件 + ADD1件 = 5件、DELETE_FLGフィルタなし）
		SearchQueryUserIdAndYearMonth searchQuery = createSearchQuery("202511");
		int incomeCount = incomeRepository.countBy(searchQuery);
		assertEquals(5, incomeCount, "INCOME_TABLEが5件であること（4件既存＋1件ADD）");

		// Then: EXPENDITURE_TABLE検証（元14件 + ADD2件 = 16件、DELETE_FLGフィルタなし）
		int expenditureCount = expenditureRepository.countBy(searchQuery);
		assertEquals(16, expenditureCount, "EXPENDITURE_TABLEが16件であること（14件既存＋2件ADD）");

		// Then: EXPENDITURE_TABLE直接確認（更新月の新規ADDパターン: YOTEI=0、KINGAKU=入力値）
		// 更新月の新規ADDはinitFlg=false → EXPENDITURE_ESTIMATE_KINGAKU=0
		// clearStartFlg=false → EXPENDITURE_KINGAKU=入力値
		// 0040(水道代): ADD 8,000
		ExpenditureItemInquiryList expList0040 = expenditureRepository.findByExpenditureItemCode(
				createExpenditureAmountSearchQuery("202511", "0040"));
		assertTrue(expList0040.isOne(), "0040(水道代)のEXPENDITURE_TABLEが1件存在すること");
		ExpenditureItem expItem0040 = expList0040.getValues().get(0);
		assertEquals(new BigDecimal("0.00"), expItem0040.getExpectedExpenditureAmount().getValue(),
				"0040:EXPENDITURE_ESTIMATE_KINGAKU=0(更新月の新規ADD:YOTEI=0)");
		assertEquals(new BigDecimal("8000.00"), expItem0040.getExpenditureAmount().getValue(),
				"0040:EXPENDITURE_KINGAKU=8000(clearStartFlg=false:入力値)");
		// 0059(趣味娯楽その他B): ADD 1,000
		ExpenditureItemInquiryList expList0059 = expenditureRepository.findByExpenditureItemCode(
				createExpenditureAmountSearchQuery("202511", "0059"));
		assertTrue(expList0059.isOne(), "0059(趣味娯楽その他B)のEXPENDITURE_TABLEが1件存在すること");
		ExpenditureItem expItem0059 = expList0059.getValues().get(0);
		assertEquals(new BigDecimal("0.00"), expItem0059.getExpectedExpenditureAmount().getValue(),
				"0059:EXPENDITURE_ESTIMATE_KINGAKU=0(更新月の新規ADD:YOTEI=0)");
		assertEquals(new BigDecimal("1000.00"), expItem0059.getExpenditureAmount().getValue(),
				"0059:EXPENDITURE_KINGAKU=1000(clearStartFlg=false:入力値)");

		// Then: INCOME_AND_EXPENDITURE_TABLE検証
		IncomeAndExpenditureItem syuusiItem = incomeAndExpenditureRepository.select(searchQuery);
		assertNotNull(syuusiItem);
		// 通常収入金額 = NON_UPDATE(01:350,000) + UPDATE(03:積立取崩→副業 25,000) = 375,000
		assertEquals(new BigDecimal("375000.00"), syuusiItem.getRegularIncomeAmount().getValue(),
				"INCOME_KINGAKU=375000(NON_UPDATE:01=350000+UPDATE:03=25000)");
		// 積立金取崩金額 = UPDATE(02:副業→積立取崩 40,000) + ADD(15,000) = 55,000（DELETE:04=10000は除外）
		assertEquals(new BigDecimal("55000.00"), syuusiItem.getWithdrawingAmount().getValue(),
				"WITHDREW_KINGAKU=55000(UPDATE:02=40000+ADD=15000)");
		// 支出金額 = NON_UPDATE38000+UPDATE09-15000+ADD0040-8000+NON_UPDATE011-3000+UPDATE012-4000+UPDATE013-500+ADD0059-1000 = 69,500
		assertEquals(new BigDecimal("69500.00"), syuusiItem.getExpenditureAmount().getValue(),
				"EXPENDITURE_KINGAKU=69500(DELETE除外)");
		// 支出予定金額 = 68,500（更新SQLはEXPENDITURE_ESTIMATE_KINGAKUを更新しないため初期値のまま）
		assertEquals(new BigDecimal("68500.00"), syuusiItem.getExpectedExpenditureAmount().getValue(),
				"EXPENDITURE_ESTIMATE_KINGAKU=68500(更新SQLでは変更されず初期値のまま)");
		// 収支金額 = (通常収入375,000 + 積立取崩55,000) - 支出69,500 = 360,500
		assertEquals(new BigDecimal("360500.00"), syuusiItem.getBalanceAmount().getValue(),
				"INCOME_AND_EXPENDITURE_KINGAKU=360500((375000+55000)-69500)");

		// Then: SISYUTU_KINGAKU_TABLE検証（UPDATE/DELETE/ADDの各操作結果を確認）

		// 0037(電気代, Level3): UPDATE 12,000→15,000 siharaiDate=2025-11-30
		// YOTEI=12,000(更新操作では変更なし), SIHARAI_DATE=MAX(2025-11-30, 2025-11-30)=2025-11-30
		ExpenditureAmountItem item0037 = sisyutuKingakuTableRepository.findByPrimaryKey(
				createExpenditureAmountSearchQuery("202511", "0037"));
		assertNotNull(item0037, "0037(電気代)の支出金額テーブルが存在すること");
		assertEquals(new BigDecimal("15000.00"), item0037.getExpenditureAmount().getValue(),
				"0037:支出金額=15000(UPDATE 12000→15000)");
		assertEquals(new BigDecimal("12000.00"), item0037.getExpectedExpenditureAmount().getValue(),
				"0037:YOTEI=12000(更新操作で変更なし)");
		assertEquals(LocalDate.of(2025, 11, 30), item0037.getPaymentDate().getValue(),
				"0037:SIHARAI_DATE=2025-11-30(MAX(before:11-30, after:11-30)=11-30)");

		// 0038(ガス代, Level3): DELETE 10,000→0
		// YOTEI=10,000(削除操作では変更なし), SIHARAI_DATE=null(削除操作では変更なし)
		ExpenditureAmountItem item0038 = sisyutuKingakuTableRepository.findByPrimaryKey(
				createExpenditureAmountSearchQuery("202511", "0038"));
		assertNotNull(item0038, "0038(ガス代)の支出金額テーブルが存在すること");
		assertEquals(new BigDecimal("0.00"), item0038.getExpenditureAmount().getValue(),
				"0038:支出金額=0(DELETE 10000→0)");
		assertEquals(new BigDecimal("10000.00"), item0038.getExpectedExpenditureAmount().getValue(),
				"0038:YOTEI=10000(削除操作で変更なし)");
		assertNull(item0038.getPaymentDate().getValue(),
				"0038:SIHARAI_DATE=null(削除操作では変更なし)");

		// 0040(水道代, Level3): 新規ADD 8,000 siharaiDate=2025-11-20
		// YOTEI=0(更新月の新規ADDはinitFlg=false → YOTEI=0)
		ExpenditureAmountItem item0040 = sisyutuKingakuTableRepository.findByPrimaryKey(
				createExpenditureAmountSearchQuery("202511", "0040"));
		assertNotNull(item0040, "0040(水道代)の支出金額テーブルが新規追加されていること");
		assertEquals(new BigDecimal("8000.00"), item0040.getExpenditureAmount().getValue(),
				"0040:支出金額=8000(新規ADD)");
		assertEquals(new BigDecimal("0.00"), item0040.getExpectedExpenditureAmount().getValue(),
				"0040:YOTEI=0(更新月の新規ADDはinitFlg=false)");
		assertEquals(LocalDate.of(2025, 11, 20), item0040.getPaymentDate().getValue(),
				"0040:SIHARAI_DATE=2025-11-20(新規ADD)");

		// 0036(水光熱通費, Level2): 0037 UPDATE+0038 DELETE+0040 ADD を親に伝搬
		// KINGAKU: 22000(初期) +3000(0037 UPDATE) -10000(0038 DELETE) +8000(0040 ADD) = 23000
		// YOTEI: 22000(更新・削除・追加操作では変更なし)
		// SIHARAI_DATE: MAX(null初期, 2025-11-30 from 0037 UPDATE, null from 0038 DELETE, 2025-11-20 from 0040 ADD) = 2025-11-30
		ExpenditureAmountItem item0036 = sisyutuKingakuTableRepository.findByPrimaryKey(
				createExpenditureAmountSearchQuery("202511", "0036"));
		assertNotNull(item0036, "0036(水光熱通費)の支出金額テーブルが存在すること");
		assertEquals(new BigDecimal("23000.00"), item0036.getExpenditureAmount().getValue(),
				"0036:支出金額=23000(22000+3000-10000+8000)");
		assertEquals(new BigDecimal("22000.00"), item0036.getExpectedExpenditureAmount().getValue(),
				"0036:YOTEI=22000(更新・削除・追加操作で変更なし)");
		assertEquals(LocalDate.of(2025, 11, 30), item0036.getPaymentDate().getValue(),
				"0036:SIHARAI_DATE=2025-11-30(MAX(0037:11-30, 0040:11-20)=11-30)");

		// 0056(交際費, Level2, parent=0055): NON_UPDATE+UPDATE増額 操作
		// KINGAKU: 5000(初期) +0(NON_UPDATE:011) +2000(UPDATE:012 2000→4000) = 7000
		// KINGAKU_B: 5000(初期) +0(NON_UPDATE:011) +2000(UPDATE:012) = 7000
		// YOTEI: 5000(更新操作で変更なし)
		// SIHARAI_DATE: MAX(null初期, null from 011 NON_UPDATE, 2025-11-25 from 012 UPDATE) = 2025-11-25
		ExpenditureAmountItem item0056 = sisyutuKingakuTableRepository.findByPrimaryKey(
				createExpenditureAmountSearchQuery("202511", "0056"));
		assertNotNull(item0056, "0056(交際費)の支出金額テーブルが存在すること");
		assertEquals(new BigDecimal("7000.00"), item0056.getExpenditureAmount().getValue(),
				"0056:支出金額=7000(5000初期+0 NON_UPDATE+2000 UPDATE増額)");
		assertEquals(new BigDecimal("7000.00"), item0056.getMinorWasteExpenditureAmount().getValue(),
				"0056:KINGAKU_B=7000(kubun=2: 5000+2000(UPDATE増額))");
		assertEquals(new BigDecimal("0.00"), item0056.getSevereWasteExpenditureAmount().getValue(),
				"0056:KINGAKU_C=0(kubun=3なし)");
		assertEquals(new BigDecimal("5000.00"), item0056.getExpectedExpenditureAmount().getValue(),
				"0056:YOTEI=5000(更新操作で変更なし)");
		assertEquals(LocalDate.of(2025, 11, 25), item0056.getPaymentDate().getValue(),
				"0056:SIHARAI_DATE=2025-11-25(MAX(null, 2025-11-25)=2025-11-25)");

		// 0057(趣味娯楽費, Level2, parent=0055): UPDATE減額+DELETE 操作
		// KINGAKU: 3500(初期) -1000(UPDATE:013 1500→500) -2000(DELETE:014 2000) = 500
		// KINGAKU_C: 3500(初期) -1000(UPDATE:013) -2000(DELETE:014) = 500
		// YOTEI: 3500(更新・削除操作で変更なし)
		// SIHARAI_DATE:
		//   UPDATE:013 siharaiDate="01" → MAX(null初期, 2025-11-01) = 2025-11-01
		//   DELETE:014 → DELETEはholderの現在値(2025-11-01)を保持
		//   → 2025-11-01
		// ※注意: UPDATE/DELETE処理ではcreateExpenditureItemでPaymentDate.from(yearMonth,day)を
		//   呼ぶため、空文字列は不可。"01"等の有効な2桁日付が必要。
		ExpenditureAmountItem item0057 = sisyutuKingakuTableRepository.findByPrimaryKey(
				createExpenditureAmountSearchQuery("202511", "0057"));
		assertNotNull(item0057, "0057(趣味娯楽費)の支出金額テーブルが存在すること");
		assertEquals(new BigDecimal("500.00"), item0057.getExpenditureAmount().getValue(),
				"0057:支出金額=500(3500初期-1000 UPDATE減額-2000 DELETE)");
		assertEquals(new BigDecimal("0.00"), item0057.getMinorWasteExpenditureAmount().getValue(),
				"0057:KINGAKU_B=0(kubun=2なし)");
		assertEquals(new BigDecimal("500.00"), item0057.getSevereWasteExpenditureAmount().getValue(),
				"0057:KINGAKU_C=500(kubun=3: 3500-1000 UPDATE減額-2000 DELETE)");
		assertEquals(new BigDecimal("3500.00"), item0057.getExpectedExpenditureAmount().getValue(),
				"0057:YOTEI=3500(更新・削除操作で変更なし)");
		assertEquals(LocalDate.of(2025, 11, 1), item0057.getPaymentDate().getValue(),
				"0057:SIHARAI_DATE=2025-11-01(UPDATE:013 MAX(null,01)=11-01, DELETE:014はholderの現在値保持)");

		// 0059(趣味娯楽その他, Level2, parent=0055): 新規ADD 1,000 kubun=2 siharaiDate=2025-11-10
		// YOTEI=0(更新月の新規ADDはinitFlg=false)
		// ※ADD処理もPaymentDate.from(yearMonth,day)を呼ぶため空文字列は不可。"10"を設定。
		ExpenditureAmountItem item0059 = sisyutuKingakuTableRepository.findByPrimaryKey(
				createExpenditureAmountSearchQuery("202511", "0059"));
		assertNotNull(item0059, "0059(趣味娯楽その他)の支出金額テーブルが新規追加されていること");
		assertEquals(new BigDecimal("1000.00"), item0059.getExpenditureAmount().getValue(),
				"0059:支出金額=1000(新規ADD kubun=2)");
		assertEquals(new BigDecimal("1000.00"), item0059.getMinorWasteExpenditureAmount().getValue(),
				"0059:KINGAKU_B=1000(kubun=2)");
		// ※新規ADDかつkubun=2のため、createSisyutuKingakuItem()でKINGAKU_C=nullとなる(null値伝播)
		assertNull(item0059.getSevereWasteExpenditureAmount().getValue(),
				"0059:KINGAKU_C=null(新規ADD, kubun=2のためKINGAKU_C未設定)");
		assertEquals(new BigDecimal("0.00"), item0059.getExpectedExpenditureAmount().getValue(),
				"0059:YOTEI=0(更新月の新規ADDはinitFlg=false)");
		assertEquals(LocalDate.of(2025, 11, 10), item0059.getPaymentDate().getValue(),
				"0059:SIHARAI_DATE=2025-11-10(siharaiDate=\"10\"を設定)");

		// 0055(趣味娯楽, Level1, parent=self): 0056/0057/0059 の各操作を親に集約
		// KINGAKU: 8500(初期) +2000(0056 UPDATE増額) -3000(0057 UPDATE減額+DELETE) +1000(0059 ADD) = 8500
		// KINGAKU_B: 5000(初期) +2000(0056 UPDATE増額) +1000(0059 ADD) = 8000
		// KINGAKU_C: 3500(初期) -3000(0057 UPDATE減額+DELETE) = 500
		// YOTEI: 8500(更新・削除操作で変更なし)
		// SIHARAI_DATE: MAX(null初期, 2025-11-25 from 0056, null from 0057, null from 0059) = 2025-11-25
		ExpenditureAmountItem item0055 = sisyutuKingakuTableRepository.findByPrimaryKey(
				createExpenditureAmountSearchQuery("202511", "0055"));
		assertNotNull(item0055, "0055(趣味娯楽)の支出金額テーブルが存在すること");
		assertEquals(new BigDecimal("8500.00"), item0055.getExpenditureAmount().getValue(),
				"0055:支出金額=8500(8500初期+2000-3000+1000)");
		assertEquals(new BigDecimal("8000.00"), item0055.getMinorWasteExpenditureAmount().getValue(),
				"0055:KINGAKU_B=8000(5000初期+2000(0056)+1000(0059))");
		assertEquals(new BigDecimal("500.00"), item0055.getSevereWasteExpenditureAmount().getValue(),
				"0055:KINGAKU_C=500(3500初期-3000(0057))");
		assertEquals(new BigDecimal("8500.00"), item0055.getExpectedExpenditureAmount().getValue(),
				"0055:YOTEI=8500(更新・削除操作で変更なし)");
		assertEquals(LocalDate.of(2025, 11, 25), item0055.getPaymentDate().getValue(),
				"0055:SIHARAI_DATE=2025-11-25(MAX(null, 2025-11-25 from 0056, null, null))");
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

	/**
	 *<pre>
	 * テスト⑨：異常系：必須8項目未登録例外
	 *
	 * 【検証内容】
	 * ・買い物登録チェックで必要な8項目のうち1項目(0046: 被服費)が未登録の場合に例外がスローされる
	 * ・IncomeAndExpenditureRegistUseCase line 1432の checkExpenditureAndSisyutuKingaku で
	 *   MyHouseholdAccountBookRuntimeExceptionがスローされる
	 * ※ロールバック確認は IncomeAndExpenditureRegistRollbackTest.testExecRegistAction_RollbackOnMandatoryItemsMissing で実施
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：必須8項目未登録例外")
	void testExecRegistAction_MandatoryItemsMissing() {
		// Given: テストユーザ、新規月（202512: DBデータなし）
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202512";

		// Given: 有効な収入リスト（3件）
		List<IncomeRegistItem> incomeList = createNewIncomeList();

		// Given: 必須8項目のうち0046(被服費)を欠いた支出リスト（7件）
		// → checkExpenditureAndSisyutuKingaku(line 1432)で必須チェック失敗
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
		// 0046(被服費)は意図的に省略
		expenditureList.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW, MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000006", "0007", "", "流動経費", "1", "流動経費詳細", "10", new BigDecimal("10000"), false));
		expenditureList.add(ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW, MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				"20251201120000007", "0047", "", "住居設備", "1", "住居設備詳細", "25", new BigDecimal("2000"), false));

		// When & Then: 例外がスローされる（line 1432: checkExpenditureAndSisyutuKingakuの必須チェック失敗）
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> {
			useCase.execRegistAction(user, targetYearMonth, incomeList, expenditureList);
		});
		// ※ロールバック確認について:
		// このテストクラスには@Transactionalが付与されているため、テスト全体が単一のトランザクション内で実行される。
		// checkExpenditureAndSisyutuKingakuの必須チェック失敗で例外が発生するとrollback-onlyに設定されるが、
		// テスト終了時にSpringが自動的にロールバックを行う。
		// このためassertThrows後のDBカウント確認では例外発生前の挿入データが見えてしまうため、
		// ここではassertThrowsでの例外発生確認のみを行い、DBカウント確認は実施しない。
	}
}
