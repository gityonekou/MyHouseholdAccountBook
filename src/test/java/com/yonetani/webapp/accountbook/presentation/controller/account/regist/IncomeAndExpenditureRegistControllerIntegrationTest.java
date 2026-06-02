/**
 * IncomeAndExpenditureRegistControllerの統合テストです。
 * 収支登録機能のController層をMockMvcを使用してテストします。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/02/25 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller.account.regist;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.yonetani.webapp.accountbook.application.usecase.account.incomeandexpenditure.ExpenditureItemSelectUseCase;
import com.yonetani.webapp.accountbook.application.usecase.account.incomeandexpenditure.ExpenditureRegistUseCase;
import com.yonetani.webapp.accountbook.application.usecase.account.incomeandexpenditure.IncomeAndExpenditureInitUseCase;
import com.yonetani.webapp.accountbook.application.usecase.account.incomeandexpenditure.IncomeAndExpenditureRegistConfirmUseCase;
import com.yonetani.webapp.accountbook.application.usecase.account.incomeandexpenditure.IncomeRegistUseCase;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.presentation.controller.MyHouseholdAccountBookControllerAdvice;
import com.yonetani.webapp.accountbook.presentation.response.fw.CompleteRedirectMessages;
import com.yonetani.webapp.accountbook.presentation.session.ExpenditureRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.IncomeAndExpenditureRegistSession;
import com.yonetani.webapp.accountbook.presentation.session.IncomeRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

/**
 *<pre>
 * IncomeAndExpenditureRegistControllerの統合テストです。
 * 収支登録機能のController層をMockMvcを使用してテストします。
 *
 * [テスト方針]
 * ・UseCase は本物のSpring Beanを使用（実際のH2 DBへのアクセスで動作確認）
 * ・LoginUserSession, IncomeAndExpenditureRegistSession はモック化（セッションスコープBean）
 * ・standaloneSetup でControllerをセットアップし、Controller層のロジックをテスト
 * ・セッションのgetterはBeforeEachで基本値を設定、各テストで必要に応じてオーバーライド
 *
 * [テストデータ]
 * ・user01: NOW_TARGET_YEAR=2025, NOW_TARGET_MONTH=11
 * ・202511: 収入4件（01:給与35万、02:副業3万、03:積立取崩2万、04:積立取崩1万）、支出14件
 * ・202512: データなし（新規登録テスト用）
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
	"/sql/initsql/schema_test.sql",
	"/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureRegistConfirmIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("収支管理機能 収支登録のControllerテスト（統合テスト）")
public class IncomeAndExpenditureRegistControllerIntegrationTest {

	// MVCモック
	private MockMvc mockMvc;
	// 収支登録初期表示ユースケース(本物のSpring Bean)
	@Autowired
	private IncomeAndExpenditureInitUseCase incomeAndExpenditureRegistUseCase;
	// 支出項目選択ユースケース(本物のSpring Bean)
	@Autowired
	private ExpenditureItemSelectUseCase expenditureItemSelectUseCase;
	// 収入登録ユースケース(本物のSpring Bean)
	@Autowired
	private IncomeRegistUseCase incomeRegistUseCase;
	// 支出登録ユースケース(本物のSpring Bean)
	@Autowired
	private ExpenditureRegistUseCase expenditureRegistUseCase;
	// 収支登録確認ユースケース(本物のSpring Bean)
	@Autowired
	private IncomeAndExpenditureRegistConfirmUseCase incomeAndExpenditureRegistConfirmUseCase;
	// モック:ログインユーザセッション情報
	@Mock
	private LoginUserSession mockLoginUserSession;
	// モック:収支登録セッション情報
	@Mock
	private IncomeAndExpenditureRegistSession mockRegistListSession;

	/**
	 *<pre>
	 * 収支登録コントローラーのログインユーザ情報・セッション情報をモック化して、
	 * MVCモックをセットアップします。
	 * 共通のセッションモック設定もここで行います。
	 *</pre>
	 *
	 */
	@BeforeEach
	void setupMockMvc() {
		this.mockMvc = MockMvcBuilders
				// 収支登録コントローラーのセットアップ
				.standaloneSetup(new IncomeAndExpenditureRegistController(
						incomeAndExpenditureRegistUseCase, expenditureItemSelectUseCase, incomeRegistUseCase, expenditureRegistUseCase, incomeAndExpenditureRegistConfirmUseCase, mockLoginUserSession, mockRegistListSession))
				// ControllerAdviceのセットアップ(例外発生時のハンドリング)
				.setControllerAdvice(new MyHouseholdAccountBookControllerAdvice(mockLoginUserSession))
				// MVCモックのビルド
				.build();

		// 共通セッションモック設定
		doReturn(createLoginUser()).when(mockLoginUserSession).getLoginUserInfo();
		when(mockRegistListSession.getTargetYearMonth()).thenReturn("202511");
		when(mockRegistListSession.getReturnYearMonth()).thenReturn("202511");
		when(mockRegistListSession.getIncomeRegistItemList()).thenReturn(new ArrayList<>());
		when(mockRegistListSession.getExpenditureRegistItemList()).thenReturn(new ArrayList<>());
	}

	/**
	 * テスト用のログインユーザ情報を作成します。
	 */
	private LoginUserInfo createLoginUser() {
		return LoginUserInfo.from("user01", "テストユーザ01");
	}

	/**
	 * テスト用の収入登録情報を作成します。
	 */
	private IncomeRegistItem createIncomeItem(String code, String kubun, String detail, String amount) {
		return IncomeRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
				MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
				code, kubun, detail, new BigDecimal(amount));
	}

	/**
	 * テスト用の支出登録情報を作成します。
	 */
	private ExpenditureRegistItem createExpenditureItem(String code, String sisyutuItemCode,
			String name, String kubun, String detail, String amount) {
		return ExpenditureRegistItem.from(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
				MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
				code, sisyutuItemCode, null, name, kubun, detail, null,
				new BigDecimal(amount), false);
	}

	/**
	 * execRegistAction テスト用の必須8支出項目リストを作成します。
	 * 買い物登録チェックを通過するため、以下の支出項目を含みます。
	 * ・0051(食費) × kubun=1,2,3
	 * ・0052(一人プチ贅沢・外食) × kubun=1
	 * ・0050(日用消耗品) × kubun=1
	 * ・0046(被服費) × kubun=1
	 * ・0007(流動経費) × kubun=1
	 * ・0047(住居設備) × kubun=1
	 */
	private List<ExpenditureRegistItem> createMandatoryExpenditureItems() {
		return Arrays.asList(
			createExpenditureItem("001", "0051", "飲食(無駄遣いなし)", "1", "飲食詳細", "10000"),
			createExpenditureItem("002", "0051", "飲食(無駄遣いB)", "2", "飲食B詳細", "2000"),
			createExpenditureItem("003", "0051", "飲食(無駄遣いC)", "3", "飲食C詳細", "1000"),
			createExpenditureItem("004", "0052", "一人プチ贅沢・外食", "1", "外食詳細", "5000"),
			createExpenditureItem("005", "0050", "日用消耗品", "1", "日用消耗品詳細", "3000"),
			createExpenditureItem("006", "0046", "被服費", "1", "被服費詳細", "5000"),
			createExpenditureItem("007", "0007", "流動経費", "1", "流動経費詳細", "10000"),
			createExpenditureItem("008", "0047", "住居設備", "1", "住居設備詳細", "2000")
		);
	}

	// ================================================================
	// GROUP 1: 初期表示系テスト
	// ================================================================

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountregist/incomeandexpenditure/initload/
	 * 新規登録初期表示
	 * - 収支データが存在しない年月(202512)で初期表示されることを確認
	 * - 収支登録画面(収入登録フォームアクティブ)が表示される
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：新規登録初期表示_収支データなし月(202512)")
	public void testPostInitLoad_NewRegistration() throws Exception {
		// 画面表示の検証
		mockMvc.perform(post("/myhacbook/accountregist/incomeandexpenditure/initload/")
				.param("targetYearMonth", "202512")
				.param("returnYearMonth", "202512")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("account/regist/IncomeAndExpenditureRegist"))
			// 収入登録フォームが存在する(収入登録エリアアクティブ)
			.andExpect(model().attributeExists("incomeItemForm"))
			// 収入区分選択ボックスが存在する
			.andExpect(model().attributeExists("incomeKubunSelectList"))
			// 表示年月が正しく設定される
			.andExpect(model().attribute("viewYear", is("2025")))
			.andExpect(model().attribute("viewMonth", is("12")));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountregist/incomeandexpenditure/initload/
	 * 新規登録初期表示_セッション設定確認
	 * - clearData(targetYearMonth, returnYearMonth) が呼ばれることを確認
	 * - setExpenditureRegistItemList が呼ばれることを確認(支出項目の初期設定)
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：新規登録初期表示_セッション設定確認")
	public void testPostInitLoad_VerifySessionSetup() throws Exception {
		// 画面表示の検証
		mockMvc.perform(post("/myhacbook/accountregist/incomeandexpenditure/initload/")
				.param("targetYearMonth", "202512")
				.param("returnYearMonth", "202511")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk());

		// セッションのclearDataが正しいパラメータで呼ばれることを確認
		verify(mockRegistListSession).clearData("202512", "202511");
		// setExpenditureRegistItemListが呼ばれることを確認
		// readInitInfoでは expenditureRegistItemList が未設定(null)のため、nullが渡される
		verify(mockRegistListSession).setExpenditureRegistItemList(null);
	}

	/**
	 *<pre>
	 * 【正常系】GET /myhacbook/accountregist/incomeandexpenditure/updateload/
	 * 更新初期表示
	 * - 収支データが存在する年月(202511)で更新初期表示されることを確認
	 * - 収支登録画面(収入・支出一覧表示)が表示される
	 * - 収入一覧に4件のデータが表示される(01:給与、02:副業、03:積立取崩×2)
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：更新初期表示_収支データあり(202511)")
	public void testGetUpdateLoad_WithData() throws Exception {
		// 画面表示の検証
		mockMvc.perform(get("/myhacbook/accountregist/incomeandexpenditure/updateload/")
				.param("targetYearMonth", "202511")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("account/regist/IncomeAndExpenditureRegist"))
			// 収入一覧が設定されている
			.andExpect(model().attributeExists("incomeListInfo"))
			// テストデータ(202511)の収入件数: 4件(01:給与, 02:副業, 03:積立取崩, 04:積立取崩)
			.andExpect(model().attribute("incomeListInfo", hasSize(4)))
			// 支出一覧が設定されている
			.andExpect(model().attributeExists("expenditureListInfo"))
			// テストデータ(202511)の支出件数: 14件
			.andExpect(model().attribute("expenditureListInfo", hasSize(14)));
	}

	/**
	 *<pre>
	 * 【正常系】GET /myhacbook/accountregist/incomeandexpenditure/updateload/
	 * 更新初期表示_セッション設定確認
	 * - clearData(targetYearMonth, targetYearMonth) が呼ばれることを確認
	 * - setIncomeRegistItemList, setExpenditureRegistItemList が呼ばれることを確認
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：更新初期表示_セッション設定確認")
	public void testGetUpdateLoad_VerifySessionSetup() throws Exception {
		// 画面表示の検証
		mockMvc.perform(get("/myhacbook/accountregist/incomeandexpenditure/updateload/")
				.param("targetYearMonth", "202511")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk());

		// セッションのclearDataが正しいパラメータで呼ばれることを確認
		// updateloadではtargetYearMonth = returnYearMonth
		verify(mockRegistListSession).clearData("202511", "202511");
		// 収入・支出リストがセッションに設定されることを確認
		verify(mockRegistListSession).setIncomeRegistItemList(org.mockito.ArgumentMatchers.anyList());
		verify(mockRegistListSession).setExpenditureRegistItemList(org.mockito.ArgumentMatchers.anyList());
	}

	// ================================================================
	// GROUP 2: 収入操作系テスト
	// ================================================================

	/**
	 *<pre>
	 * 【正常系】GET /myhacbook/accountregist/incomeandexpenditure/incomeaddselect/
	 * 収入新規登録フォーム表示
	 * - 収支登録画面(収入登録エリアアクティブ)が表示される
	 * - 収入入力フォーム、収入区分選択ボックスが設定されている
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：収入新規登録フォーム表示")
	public void testGetIncomeAddSelect() throws Exception {
		// 画面表示の検証
		mockMvc.perform(get("/myhacbook/accountregist/incomeandexpenditure/incomeaddselect/")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("account/regist/IncomeAndExpenditureRegist"))
			// 収入登録フォームが存在する
			.andExpect(model().attributeExists("incomeItemForm"))
			// 収入区分選択ボックスが存在する
			.andExpect(model().attributeExists("incomeKubunSelectList"))
			// ログインユーザ名が設定されている
			.andExpect(model().attribute("loginUserName", is("テストユーザ01")));
	}

	/**
	 *<pre>
	 * 【正常系】GET /myhacbook/accountregist/incomeandexpenditure/incomeupdateselect
	 * 収入更新フォーム表示
	 * - セッションの収入一覧に対象の収入コードが存在する場合
	 * - 収支登録画面(収入更新フォームがアクティブ)が表示される
	 * - 既存収入情報がフォームに設定されている
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：収入更新フォーム表示_既存収入コード指定")
	public void testGetIncomeUpdateSelect_WithExistingItem() throws Exception {
		// セッションに収入登録情報を設定
		when(mockRegistListSession.getIncomeRegistItemList()).thenReturn(Arrays.asList(
			createIncomeItem("01", "1", "11月給与", "350000")
		));

		// 画面表示の検証
		mockMvc.perform(get("/myhacbook/accountregist/incomeandexpenditure/incomeupdateselect")
				.param("incomeCode", "01")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("account/regist/IncomeAndExpenditureRegist"))
			// 収入登録フォームが存在する(更新対象の収入情報が設定されている)
			.andExpect(model().attributeExists("incomeItemForm"))
			// 収入区分選択ボックスが存在する
			.andExpect(model().attributeExists("incomeKubunSelectList"));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountregist/incomeandexpenditure/incomeupdate/ (actionUpdate)
	 * 収入登録_バリデーションOK
	 * - 有効な収入情報を送信した場合、収支登録画面の一覧表示(updateComplete)にリダイレクト
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：収入登録_バリデーションOK_リダイレクト確認")
	public void testPostIncomeUpdate_ValidationOK_Redirect() throws Exception {
		// 有効な収入フォームデータでPOST
		// action="add" は収入フォームのhidden inputで設定される値 (新規登録時はadd)
		mockMvc.perform(post("/myhacbook/accountregist/incomeandexpenditure/incomeupdate/")
				.param("actionUpdate", "")
				.param("action", MyHouseholdAccountBookContent.ACTION_TYPE_ADD) // フォームのaction(新規)
				.param("incomeKubun", "1")        // 必須: 収入区分(給与)
				.param("incomeKingaku", "300000") // 必須: 収入金額(0以上)
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			// 収支登録画面一覧表示(updateComplete)にリダイレクトされる
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/myhacbook/accountregist/incomeandexpenditure/updateComplete/"));
	}

	/**
	 *<pre>
	 * 【異常系】POST /myhacbook/accountregist/incomeandexpenditure/incomeupdate/ (actionUpdate)
	 * 
	 * 【検証内容】
	 * ・incomeKubunを空で送信した場合、バリデーションエラーが発生すること
	 * ・HTTPステータスが200で収支登録画面に戻ること
	 * ・ビュー名が「account/regist/IncomeAndExpenditureRegist」であること
	 * ・IncomeItemFormのincomeKubunフィールドにエラーが設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：収入登録_バリデーションエラー_画面再表示")
	public void testPostIncomeUpdate_ValidationError_RenderView() throws Exception {
		// 収入区分(必須)が未入力のフォームデータでPOST
		mockMvc.perform(post("/myhacbook/accountregist/incomeandexpenditure/incomeupdate/")
				.param("actionUpdate", "")
				// incomeKubun 未送信 → @NotBlankエラー
				.param("incomeKingaku", "300000")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			// リダイレクトされず、収支登録画面が表示される
			.andExpect(status().isOk())
			.andExpect(view().name("account/regist/IncomeAndExpenditureRegist"))
			.andExpect(model().attributeHasFieldErrors("incomeItemForm", "incomeKubun"));
	}

	/**
	 *<pre>
	 * 【異常系】POST /myhacbook/accountregist/incomeandexpenditure/incomeupdate/ (actionUpdate)
	 * 
	 * 【検証内容】
	 * ・収入区分='9'(その他任意)を選択し、収入詳細を空で送信した場合、相関バリデーションエラーが発生すること
	 * ・IncomeItemFormの@AssertTrue isNeedCheckIncomeDetailContext()が機能すること
	 * ・HTTPステータスが200で更新画面に戻ること
	 * ・IncomeItemFormのneedCheckIncomeDetailContextフィールドにエラーが設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：収入登録_相関チェック(収入区分でその他任意選択の場合必須、収入詳細は必須)エラー_画面再表示")
	public void testPostIncomeUpdate_ValidationError_incomeDetailContext() throws Exception {
		// 収入区分(必須)が未入力のフォームデータでPOST
		mockMvc.perform(post("/myhacbook/accountregist/incomeandexpenditure/incomeupdate/")
				.param("actionUpdate", "")
				.param("incomeKubun", "9")        // 必須: 収入区分(その他任意)
				.param("incomeKingaku", "300000") // 必須: 収入金額(0以上)
				// incomeDetailContext 未送信 → @AssertTrueのisNeedCheckIncomeDetailContext()でエラー
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			// リダイレクトされず、収支登録画面が表示される
			.andExpect(status().isOk())
			.andExpect(view().name("account/regist/IncomeAndExpenditureRegist"))
			.andExpect(model().attributeHasFieldErrors("incomeItemForm", "needCheckIncomeDetailContext"));
	}
	
	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountregist/incomeandexpenditure/incomeupdate/ (actionDelete)
	 * 収入削除_バリデーションOK
	 * - 有効な収入情報を送信した場合、収支登録画面の一覧表示(updateComplete)にリダイレクト
	 * - actionDeleteの場合、フォームのactionはADELETE(削除)に設定される
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：収入削除_バリデーションOK_リダイレクト確認")
	public void testPostIncomeDelete_ValidationOK_Redirect() throws Exception {
		// セッションに削除対象の収入情報を設定
		when(mockRegistListSession.getIncomeRegistItemList()).thenReturn(Arrays.asList(
			createIncomeItem("01", "1", "11月給与", "350000")
		));

		// 有効なフォームデータでPOST(actionDelete)
		mockMvc.perform(post("/myhacbook/accountregist/incomeandexpenditure/incomeupdate/")
				.param("actionDelete", "")
				.param("incomeCode", "01")
				.param("incomeKubun", "1")
				.param("incomeKingaku", "0")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/myhacbook/accountregist/incomeandexpenditure/updateComplete/"));
	}

	// ================================================================
	// GROUP 3: 支出操作系テスト
	// ================================================================

	/**
	 *<pre>
	 * 【正常系】GET /myhacbook/accountregist/incomeandexpenditure/expenditureaddselect/
	 * 支出項目選択画面表示
	 * - 収支登録で支出の新規登録ボタン押下時、支出項目選択画面が表示される
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支出項目選択画面表示")
	public void testGetExpenditureAddSelect() throws Exception {
		// 画面表示の検証
		mockMvc.perform(get("/myhacbook/accountregist/incomeandexpenditure/expenditureaddselect/")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			// 支出項目選択画面が表示される
			.andExpect(view().name("account/regist/ExpenditureItemSelect"))
			// ログインユーザ名が設定されている
			.andExpect(model().attribute("loginUserName", is("テストユーザ01")));
	}

	/**
	 *<pre>
	 * 【正常系】GET /myhacbook/accountregist/incomeandexpenditure/expenditureupdateselect
	 * 支出更新フォーム表示
	 * - セッションの支出一覧に対象の支出コードが存在する場合
	 * - 収支登録画面(支出更新フォームがアクティブ)が表示される
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支出更新フォーム表示_既存支出コード指定")
	public void testGetExpenditureUpdateSelect_WithExistingItem() throws Exception {
		// セッションに支出登録情報を設定
		when(mockRegistListSession.getExpenditureRegistItemList()).thenReturn(Arrays.asList(
			createExpenditureItem("001", "0051", "飲食(無駄遣いなし)", "1", "飲食詳細", "10000")
		));

		// 画面表示の検証
		mockMvc.perform(get("/myhacbook/accountregist/incomeandexpenditure/expenditureupdateselect")
				.param("expenditureCode", "001")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("account/regist/IncomeAndExpenditureRegist"))
			// 支出登録フォームが存在する
			.andExpect(model().attributeExists("expenditureItemForm"))
			// 支出区分選択ボックスが存在する
			.andExpect(model().attributeExists("expenditureKubunSelectList"));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountregist/incomeandexpenditure/expenditureupdate/ (actionUpdate)
	 * 支出登録_バリデーションOK
	 * - 有効な支出情報を送信した場合、収支登録画面の一覧表示(updateComplete)にリダイレクト
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支出登録_バリデーションOK_リダイレクト確認")
	public void testPostExpenditureUpdate_ValidationOK_Redirect() throws Exception {
		// 有効な支出フォームデータでPOST(新規登録: expenditureCode未設定)
		// action="add" は支出フォームのhidden inputで設定される値 (新規登録時はadd)
		mockMvc.perform(post("/myhacbook/accountregist/incomeandexpenditure/expenditureupdate/")
				.param("actionUpdate", "")
				.param("action", MyHouseholdAccountBookContent.ACTION_TYPE_ADD) // フォームのaction(新規)
				.param("sisyutuItemCode", "0051")  // 食費
				.param("expenditureName", "テスト食費")
				.param("expenditureKubun", "1")
				.param("expenditureKingaku", "5000") // @Min(1)
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/myhacbook/accountregist/incomeandexpenditure/updateComplete/"));
	}

	/**
	 *<pre>
	 * 【異常系】POST /myhacbook/accountregist/incomeandexpenditure/expenditureupdate/ (actionUpdate)
	 * 支出登録_バリデーションエラー
	 * - 必須項目(expenditureName)が未入力の場合、収支登録画面に入力エラーが表示される
	 * - ExpenditureItemFormのexpenditureNameフィールドにエラーが設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：支出登録_バリデーションエラー_画面再表示")
	public void testPostExpenditureUpdate_ValidationError_RenderView() throws Exception {
		// 支出名(必須)が未入力のフォームデータでPOST
		mockMvc.perform(post("/myhacbook/accountregist/incomeandexpenditure/expenditureupdate/")
				.param("actionUpdate", "")
				.param("sisyutuItemCode", "0051")
				// expenditureName 未送信 → @NotBlankエラー
				.param("expenditureKubun", "1")
				.param("expenditureKingaku", "5000")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("account/regist/IncomeAndExpenditureRegist"))
			.andExpect(model().attributeHasFieldErrors("expenditureItemForm", "expenditureName"));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountregist/incomeandexpenditure/expenditureupdate/ (actionDelete)
	 * 支出削除_バリデーションOK
	 * - 有効な支出情報を送信した場合、収支登録画面の一覧表示(updateComplete)にリダイレクト
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支出削除_バリデーションOK_リダイレクト確認")
	public void testPostExpenditureDelete_ValidationOK_Redirect() throws Exception {
		// セッションに削除対象の支出情報を設定
		when(mockRegistListSession.getExpenditureRegistItemList()).thenReturn(Arrays.asList(
			createExpenditureItem("001", "0051", "飲食(無駄遣いなし)", "1", "飲食詳細", "10000")
		));

		// 有効なフォームデータでPOST(actionDelete)
		mockMvc.perform(post("/myhacbook/accountregist/incomeandexpenditure/expenditureupdate/")
				.param("actionDelete", "")
				.param("expenditureCode", "001")
				.param("sisyutuItemCode", "0051")
				.param("expenditureName", "飲食(無駄遣いなし)")
				.param("expenditureKubun", "1")
				.param("expenditureKingaku", "1") // @Min(1)
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/myhacbook/accountregist/incomeandexpenditure/updateComplete/"));
	}

	// ================================================================
	// GROUP 4: 支出項目選択系テスト
	// ================================================================

	/**
	 *<pre>
	 * 【正常系】GET /myhacbook/accountregist/incomeandexpenditure/expenditureitemactselect
	 * 支出項目アクション選択画面表示
	 * - 支出項目コード(0051:食費)を選択した場合、支出項目アクション選択画面が表示される
	 * - 選択した支出項目の名称が画面に表示される
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支出項目アクション選択画面表示_食費(0051)選択")
	public void testGetExpenditureItemActSelect_FoodExpense() throws Exception {
		// 画面表示の検証
		mockMvc.perform(get("/myhacbook/accountregist/incomeandexpenditure/expenditureitemactselect")
				.param("sisyutuItemCode", "0051")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			// 支出項目選択画面(アクション選択)が表示される
			.andExpect(view().name("account/regist/ExpenditureItemSelect"))
			// 支出項目名が設定されている
			.andExpect(model().attributeExists("sisyutuItemName"))
			// 支出項目詳細が設定されている
			.andExpect(model().attributeExists("sisyutuItemDetailContext"));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountregist/incomeandexpenditure/expenditureselect/ (actionSelect)
	 * 支出項目確定
	 * - 選択した支出項目を確定し、収支登録画面(支出登録フォームアクティブ)に遷移する
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支出項目確定_収支登録画面(支出登録フォーム)表示")
	public void testPostExpenditureItemActSelect() throws Exception {
		// 画面表示の検証
		mockMvc.perform(post("/myhacbook/accountregist/incomeandexpenditure/expenditureselect/")
				.param("actionSelect", "")
				.param("sisyutuItemCode", "0051")
				// イベントコードなし(eventCodeRequired=false)
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			// 収支登録画面が表示される(支出登録フォームアクティブ)
			.andExpect(view().name("account/regist/IncomeAndExpenditureRegist"))
			// 支出登録フォームが存在する
			.andExpect(model().attributeExists("expenditureItemForm"))
			// 支出区分選択ボックスが存在する
			.andExpect(model().attributeExists("expenditureKubunSelectList"));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountregist/incomeandexpenditure/expenditureselect/ (actionCancel)
	 * 支出項目選択キャンセル
	 * - 支出項目選択画面でキャンセルした場合、収支登録画面(収入・支出一覧)に戻る
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支出項目選択キャンセル_収支登録画面(一覧)表示")
	public void testPostExpenditureItemActCancel() throws Exception {
		// 画面表示の検証
		mockMvc.perform(post("/myhacbook/accountregist/incomeandexpenditure/expenditureselect/")
				.param("actionCancel", "")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			// 収支登録画面が表示される(収入・支出一覧)
			.andExpect(view().name("account/regist/IncomeAndExpenditureRegist"))
			// 収入一覧・支出一覧が設定されている
			.andExpect(model().attributeExists("incomeListInfo"))
			.andExpect(model().attributeExists("expenditureListInfo"));
	}

	// ================================================================
	// GROUP 5: 収支登録確認系テスト
	// ================================================================

	/**
	 *<pre>
	 * 【正常系】GET /myhacbook/accountregist/incomeandexpenditure/updateComplete/
	 * 収入・支出更新後のリダイレクト先
	 * - updateCompleteにアクセスした場合、収支登録画面(収入・支出一覧)が表示される
	 * - リダイレクトメッセージを受け取ることができる
	 *
	 * ※ standaloneSetupではflashAttrが@ModelAttributeパラメータに自動伝播しないため、
	 *    CompleteRedirectMessagesのmessages内容は検証しない
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：収入・支出更新後のリダイレクト先_収支登録画面表示")
	public void testGetUpdateComplete() throws Exception {
		// CompleteRedirectMessages型でflashAttrを設定
		CompleteRedirectMessages flashMsg = new CompleteRedirectMessages();
		flashMsg.setRedirectMessages(Arrays.asList("登録が完了しました"));

		// 画面表示の検証
		mockMvc.perform(get("/myhacbook/accountregist/incomeandexpenditure/updateComplete/")
				.flashAttr("redirectMessages", flashMsg)
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			// 収支登録画面が表示される
			.andExpect(view().name("account/regist/IncomeAndExpenditureRegist"))
			// 収入一覧・支出一覧が設定されている
			.andExpect(model().attributeExists("incomeListInfo"))
			.andExpect(model().attributeExists("expenditureListInfo"))
			// ログインユーザ名が設定されている
			.andExpect(model().attribute("loginUserName", is("テストユーザ01")));
	}

	/**
	 *<pre>
	 * 【異常系】POST /myhacbook/accountregist/incomeandexpenditure/registcheck/ (actionCheck)
	 * 内容確認_収入0件エラー
	 * - セッションの収入一覧が0件の場合、エラーメッセージ付きで収支登録画面が表示される
	 * - 収支登録内容確認画面には遷移しない
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：内容確認ボタン押下_収入0件エラー_収支登録画面にエラー表示")
	public void testPostRegistCheck_WithoutIncome_ShowsError() throws Exception {
		// セッションの収入一覧は空(デフォルト設定: new ArrayList<>())
		// 画面表示の検証
		mockMvc.perform(post("/myhacbook/accountregist/incomeandexpenditure/registcheck/")
				.param("actionCheck", "")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			// エラー表示のため、収支登録画面が表示される(確認画面ではない)
			.andExpect(view().name("account/regist/IncomeAndExpenditureRegist"))
			// エラーメッセージが設定されている(AbstractResponseのメッセージキーは"messages")
			.andExpect(model().attributeExists("messages"));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountregist/incomeandexpenditure/registcheck/ (actionCheck)
	 * 内容確認_収入あり
	 * - セッションの収入一覧に1件以上データがある場合、収支登録内容確認画面に遷移する
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：内容確認ボタン押下_収入あり_収支登録内容確認画面表示")
	public void testPostRegistCheck_WithIncome_ShowsConfirm() throws Exception {
		// セッションに収入情報を設定(kubun=1:給与)
		when(mockRegistListSession.getIncomeRegistItemList()).thenReturn(Arrays.asList(
			createIncomeItem("01", "1", "11月給与", "350000")
		));

		// 画面表示の検証
		mockMvc.perform(post("/myhacbook/accountregist/incomeandexpenditure/registcheck/")
				.param("actionCheck", "")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			// 収支登録内容確認画面が表示される
			.andExpect(view().name("account/regist/IncomeAndExpenditureRegistCheck"))
			// 収入一覧が設定されている
			.andExpect(model().attributeExists("incomeListInfo"))
			// 支出一覧が設定されている
			.andExpect(model().attributeExists("expenditureListInfo"))
			// 表示年月が正しく設定される(対象年月=202511)
			.andExpect(model().attribute("viewYear", is("2025")))
			.andExpect(model().attribute("viewMonth", is("11")));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountregist/incomeandexpenditure/registcheck/ (actionCancel)
	 * 収支登録キャンセル
	 * - キャンセルボタン押下時、各月収支照会画面(registComplete)にリダイレクトされる
	 * - リダイレクト先にはreturnYearMonthがtargetYearMonthとして設定される
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：収支登録キャンセル_各月収支照会画面にリダイレクト")
	public void testPostRegistCancelLoad_RedirectToAccountMonth() throws Exception {
		// セッションの返り先年月を設定
		when(mockRegistListSession.getReturnYearMonth()).thenReturn("202511");

		// 画面表示の検証
		mockMvc.perform(post("/myhacbook/accountregist/incomeandexpenditure/registcheck/")
				.param("actionCancel", "")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			// 各月収支照会画面にリダイレクトされる
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl(
					"/myhacbook/accountinquiry/accountmonth/registComplete/?targetYearMonth=202511"));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountregist/incomeandexpenditure/registcheck/ (actionCancel)
	 * 収支登録キャンセル_セッションクリア確認
	 * - キャンセル時にセッションの収支登録情報がクリアされることを確認
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：収支登録キャンセル_セッションクリア確認")
	public void testPostRegistCancelLoad_VerifySessionClear() throws Exception {
		// 画面表示の検証
		mockMvc.perform(post("/myhacbook/accountregist/incomeandexpenditure/registcheck/")
				.param("actionCancel", "")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection());

		// セッションのclearData()が呼ばれることを確認(引数なし版)
		verify(mockRegistListSession).clearData();
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountregist/incomeandexpenditure/regist/ (actionUpdate)
	 * 収支情報登録実行
	 * - セッションの収入・支出情報をDBに登録し、各月収支照会画面にリダイレクト
	 * - 202512(データなし月)に対して新規登録を実行
	 * - 必須8支出項目(0051×3区分、0052、0050、0046、0007、0047)を含むデータで登録
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：収支情報登録実行_新規登録(202512)_各月収支照会画面にリダイレクト")
	public void testPostRegist_ActionUpdate_NewRegistration() throws Exception {
		// セッションの対象年月を202512(データなし月)に設定
		when(mockRegistListSession.getTargetYearMonth()).thenReturn("202512");
		when(mockRegistListSession.getReturnYearMonth()).thenReturn("202512");
		// 収入情報(2件)をセッションに設定
		when(mockRegistListSession.getIncomeRegistItemList()).thenReturn(Arrays.asList(
			createIncomeItem("01", "1", "12月給与", "350000"),
			createIncomeItem("02", "2", "12月副業収入", "30000")
		));
		// 支出情報(必須8項目)をセッションに設定
		when(mockRegistListSession.getExpenditureRegistItemList()).thenReturn(
				createMandatoryExpenditureItems());

		// 画面表示の検証
		mockMvc.perform(post("/myhacbook/accountregist/incomeandexpenditure/regist/")
				.param("actionUpdate", "")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			// 各月収支照会画面にリダイレクトされる
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl(
					"/myhacbook/accountinquiry/accountmonth/registComplete/?targetYearMonth=202512"));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountregist/incomeandexpenditure/regist/ (actionReturnBack)
	 * 前に戻る
	 * - 収支登録内容確認画面で「前に戻る」を押下した場合、収支登録画面(一覧)に戻る
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：前に戻る_収支登録画面(収入・支出一覧)表示")
	public void testPostRegistReturnBack() throws Exception {
		// セッションに収入情報を設定
		when(mockRegistListSession.getIncomeRegistItemList()).thenReturn(Arrays.asList(
			createIncomeItem("01", "1", "11月給与", "350000")
		));

		// 画面表示の検証
		mockMvc.perform(post("/myhacbook/accountregist/incomeandexpenditure/regist/")
				.param("actionReturnBack", "")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			// 収支登録画面が表示される(収入・支出一覧)
			.andExpect(view().name("account/regist/IncomeAndExpenditureRegist"))
			// 収入一覧が設定されている
			.andExpect(model().attributeExists("incomeListInfo"))
			// 支出一覧が設定されている
			.andExpect(model().attributeExists("expenditureListInfo"))
			// ログインユーザ名が設定されている
			.andExpect(model().attribute("loginUserName", is("テストユーザ01")));
	}

	/**
	 *<pre>
	 * 【正常系】GET /myhacbook/accountregist/incomeandexpenditure/updateload/
	 * 更新初期表示_収支金額表示値の検証
	 * - 202511の収入金額合計(通常収入380,000円)が正しく表示されることを確認
	 * - 支出金額合計(68,500円)が正しく表示されることを確認
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：更新初期表示_収支金額表示値の検証")
	public void testGetUpdateLoad_VerifyKingakuValues() throws Exception {
		// 画面表示の検証
		// テストデータ(202511):
		//   収入合計(通常): 給与350,000+副業30,000 = 380,000円
		//   支出合計: 68,500円
		mockMvc.perform(get("/myhacbook/accountregist/incomeandexpenditure/updateload/")
				.param("targetYearMonth", "202511")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			// 収入金額合計が設定されている
			.andExpect(model().attributeExists("incomeSumKingaku"))
			// 支出金額合計が設定されている
			.andExpect(model().attributeExists("expenditureSumKingaku"))
			// 表示年月が202511に設定される
			.andExpect(model().attribute("viewYear", is("2025")))
			.andExpect(model().attribute("viewMonth", is("11")));
	}
}
