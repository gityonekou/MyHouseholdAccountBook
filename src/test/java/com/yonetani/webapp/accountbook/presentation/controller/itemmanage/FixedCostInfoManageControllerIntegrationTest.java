/**
 * FixedCostInfoManageControllerの統合テストです。
 * 固定費情報管理機能のController層をMockMvcを使用してテストします。
 *
 * <pre>
 * [テスト方針]
 * ・FixedCostInquiryUseCase / FixedCostRegistConfirmUseCase は本物のSpring Beanを使用
 * ・LoginUserSession はモック化（セッションスコープBean）
 * ・standaloneSetup でControllerをセットアップ
 *
 * [テストシナリオ]
 * ①  正常系：GET /initload/ 固定費一覧5件の初期表示
 * ②  正常系：GET /select?fixedCostCode=0001 処理選択画面表示(家賃)
 * ③  正常系：GET /addload?sisyutuItemCode=0035 固定費未登録→更新画面へ
 * ④  正常系：GET /addload?sisyutuItemCode=0030 固定費登録済み→初期表示(登録済みメッセージ)
 * ⑤  正常系：GET /updateload/ actionAdd 更新画面(追加)へ
 * ⑥  正常系：GET /updateload/ actionUpdate 更新画面(更新)へ
 * ⑦  正常系：GET /updateload/ actionCancel 初期表示画面へ戻る
 * ⑧  正常系：POST /delete/ 固定費0003を削除してリダイレクト
 * ⑨  異常系：POST /update/ fixedCostName空→@NotBlankバリデーションエラー（新規追加時でバリデーション確認）
 * ⑩  異常系：POST /update/ shiharaiTuki='40'+shiharaiTukiOptionalContext空→相関バリデーションエラー（更新時でバリデーション確認）
 * ⑪  正常系：POST /update/ action=add 新規追加してリダイレクト
 * ⑫  正常系：POST /update/ action=update 更新してリダイレクト
 * ⑬  正常系：GET /updateComplete/ 完了後の初期表示
 * ⑭  正常系：POST /update/ actionCancel (action=add) 初期表示画面へ
 * ⑮  正常系：POST /update/ actionCancel (action=update) 処理選択画面へ
 * ⑯  正常系：GET /annualsummary/ 年間固定費合計画面表示（13行）
 * ⑰  正常系：GET /tabload/ 固定費管理タブ（他タブからの遷移）→初期表示画面（セッションはクリアしない）
 * ⑱  正常系：GET /monthlydetail/?month=11 月別固定費一覧（11月・奇数月）4件表示
 *
 * [テストデータ]
 * 固定費4件: 0001:家賃(0030), 0002:電気代概算(0037), 0003:国民年金保険(0015), 0004:その他任意テスト(0038)
 * 
 * </pre>
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/04/19 : 1.01.00  新規作成
 * 2026/05/01 : 1.01.01  テストシナリオ⑭、⑮を追加（更新画面からのキャンセル操作のパターン追加）
 * 2026/05/24 : 1.01.02  テストシナリオ⑯を追加（年間固定費合計画面表示）
 * 2026/05/27 : 1.01.03  テストシナリオ⑰⑱を追加（tabload・月別固定費一覧）
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller.itemmanage;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import com.yonetani.webapp.accountbook.application.usecase.itemmanage.fixedcost.FixedCostAnnualSummaryUseCase;
import com.yonetani.webapp.accountbook.application.usecase.itemmanage.fixedcost.FixedCostInquiryUseCase;
import com.yonetani.webapp.accountbook.application.usecase.itemmanage.fixedcost.FixedCostMonthlyDetailUseCase;
import com.yonetani.webapp.accountbook.application.usecase.itemmanage.fixedcost.FixedCostRegistConfirmUseCase;
import com.yonetani.webapp.accountbook.presentation.controller.MyHouseholdAccountBookControllerAdvice;
import com.yonetani.webapp.accountbook.presentation.session.FixedCostInfoManageSession;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

/**
 *<pre>
 * FixedCostInfoManageControllerの統合テストです。
 * 固定費情報管理機能のController層をMockMvcを使用してテストします。
 *
 * [テスト方針]
 * ・FixedCostInquiryUseCase / FixedCostRegistConfirmUseCase は本物のSpring Beanを使用
 * ・LoginUserSession はモック化（セッションスコープBean）
 * ・standaloneSetup でControllerをセットアップ
 *
 * [テストデータ]
 * 固定費4件: 0001:家賃(0030), 0002:電気代概算(0037), 0003:国民年金保険(0015), 0004:その他任意テスト(0038)
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.01)
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
	"/sql/initsql/schema_test.sql",
	"/com/yonetani/webapp/accountbook/application/usecase/itemmanage/fixedcost/FixedCostInquiryIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("固定費情報管理 Controllerテスト（統合テスト）")
public class FixedCostInfoManageControllerIntegrationTest {

	// MVCモック
	private MockMvc mockMvc;

	// UseCase(参照系)(本物のSpring Bean)
	@Autowired
	private FixedCostInquiryUseCase fixedCostInquiryUseCase;

	// UseCase(更新系)(本物のSpring Bean)
	@Autowired
	private FixedCostRegistConfirmUseCase fixedCostRegistConfirmUseCase;

	// UseCase(年間固定費合計)(本物のSpring Bean)
	@Autowired
	private FixedCostAnnualSummaryUseCase fixedCostAnnualSummaryUseCase;

	// UseCase(月別固定費一覧)(本物のSpring Bean)
	@Autowired
	private FixedCostMonthlyDetailUseCase fixedCostMonthlyDetailUseCase;

	// モック:ログインユーザセッション情報
	@Mock
	private LoginUserSession mockLoginUserSession;

	// モック:固定費情報管理セッション
	@Mock
	private FixedCostInfoManageSession mockFixedCostInfoManageSession;

	@BeforeEach
	void setupMockMvc() {
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new FixedCostInfoManageController(
						fixedCostInquiryUseCase,
						fixedCostRegistConfirmUseCase,
						fixedCostAnnualSummaryUseCase,
						fixedCostMonthlyDetailUseCase,
						mockLoginUserSession,
						mockFixedCostInfoManageSession))
				.setControllerAdvice(new MyHouseholdAccountBookControllerAdvice(mockLoginUserSession))
				.build();

		doReturn(createLoginUser()).when(mockLoginUserSession).getLoginUserInfo();
		// セッションのデフォルト: selectedMonth=null（月パラメータ未指定時にDB fallback する）
		doReturn(null).when(mockFixedCostInfoManageSession).getSelectedMonth();
	}

	private LoginUserInfo createLoginUser() {
		return LoginUserInfo.from("user01", "テストユーザ01");
	}

	// ================================================================
	// GET /initload/
	// ================================================================

	/**
	 *<pre>
	 * テスト①：正常系：GET /initload/ 固定費一覧初期表示
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること
	 * ・ビュー名が「itemmanage/fixedcost/FixedCostInfoManageInit」であること
	 * ・fixedCostItemListが4件で取得されること
	 * ・loginUserNameに「テストユーザ01」が設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：GET /initload/ 固定費一覧5件の初期表示")
	void testGetInitLoad() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/fixedcostinfo/initload/")
				.with(user("user01").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/fixedcost/FixedCostInfoManageInit"))
			.andExpect(model().attribute("fixedCostItemList", hasSize(5)))
			.andExpect(model().attribute("loginUserName", is("テストユーザ01")));
	}

	// ================================================================
	// GET /select
	// ================================================================

	/**
	 *<pre>
	 * テスト②：正常系：GET /select?fixedCostCode=0001 処理選択画面表示(家賃)
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること
	 * ・ビュー名が「itemmanage/fixedcost/FixedCostInfoManageActSelect」であること
	 * ・fixedCostInfoがモデルに設定されること
	 * ・fixedCostItemListが5件で取得されること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：GET /select?fixedCostCode=0001 処理選択画面表示(家賃)")
	void testGetActSelect_0001() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/fixedcostinfo/select")
				.param("fixedCostCode", "0001")
				.with(user("user01").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/fixedcost/FixedCostInfoManageActSelect"))
			.andExpect(model().attributeExists("fixedCostInfo"))
			.andExpect(model().attribute("fixedCostItemList", hasSize(5)));
	}

	// ================================================================
	// GET /addload
	// ================================================================

	/**
	 *<pre>
	 * テスト③：正常系：GET /addload?sisyutuItemCode=0035 固定費未登録→更新画面表示
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること
	 * ・支出項目コード=0035には固定費が未登録のため更新画面に遷移すること
	 * ・ビュー名が「itemmanage/fixedcost/FixedCostInfoManageUpdate」であること
	 * ・fixedCostInfoUpdateFormがモデルに設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：GET /addload?sisyutuItemCode=0035 固定費未登録→更新画面へ")
	void testGetAddLoad_notRegistered() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/fixedcostinfo/addload")
				.param("sisyutuItemCode", "0035")
				.with(user("user01").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/fixedcost/FixedCostInfoManageUpdate"))
			.andExpect(model().attributeExists("fixedCostInfoUpdateForm"));
	}

	/**
	 *<pre>
	 * テスト④：正常系：GET /addload?sisyutuItemCode=0030 固定費登録済み→初期表示(登録済みメッセージ)
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること
	 * ・支出項目コード=0030(家賃)は固定費登録済みのため、初期表示画面に戻ること
	 * ・ビュー名が「itemmanage/fixedcost/FixedCostInfoManageInit」であること
	 * ・registeredFlgがtrueで設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：GET /addload?sisyutuItemCode=0030 固定費登録済み→初期表示(登録済みメッセージ)")
	void testGetAddLoad_alreadyRegistered() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/fixedcostinfo/addload")
				.param("sisyutuItemCode", "0030")
				.with(user("user01").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/fixedcost/FixedCostInfoManageInit"))
			// 登録済みフラグがtrue
			.andExpect(model().attribute("registeredFlg", is(true)));
	}

	// ================================================================
	// POST /updateload/ (params: actionAdd)
	// ================================================================

	/**
	 *<pre>
	 * テスト⑤：正常系：GET /updateload/ actionAdd→更新画面(追加)へ
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること
	 * ・ビュー名が「itemmanage/fixedcost/FixedCostInfoManageUpdate」であること
	 * ・fixedCostInfoUpdateFormがモデルに設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：GET /updateload/ actionAdd 更新画面(追加)へ")
	void testGetActionAddLoad() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/fixedcostinfo/updateload/")
				.param("actionAdd", "")
				.param("sisyutuItemCode", "0030")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/fixedcost/FixedCostInfoManageUpdate"))
			.andExpect(model().attributeExists("fixedCostInfoUpdateForm"));
	}

	// ================================================================
	// POST /updateload/ (params: actionUpdate)
	// ================================================================

	/**
	 *<pre>
	 * テスト⑥：正常系：GET /updateload/ actionUpdate→更新画面(更新)へ
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること
	 * ・fixedCostCode=0001を指定して更新画面に遷移すること
	 * ・ビュー名が「itemmanage/fixedcost/FixedCostInfoManageUpdate」であること
	 * ・fixedCostInfoUpdateFormがモデルに設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：GET /updateload/ actionUpdate 更新画面(更新)へ")
	void testGetActionUpdateLoad() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/fixedcostinfo/updateload/")
				.param("actionUpdate", "")
				.param("fixedCostCode", "0001")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/fixedcost/FixedCostInfoManageUpdate"))
			.andExpect(model().attributeExists("fixedCostInfoUpdateForm"));
	}

	// ================================================================
	// POST /updateload/ (params: actionCancel)
	// ================================================================

	/**
	 *<pre>
	 * テスト⑦：正常系：GET /updateload/ actionCancel→初期表示画面へ戻る
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること
	 * ・キャンセル操作で初期表示画面に戻ること
	 * ・ビュー名が「itemmanage/fixedcost/FixedCostInfoManageInit」であること
	 * ・fixedCostItemListが4件で取得されること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：GET /updateload/ actionCancel 初期表示画面へ戻る")
	void testGetActionCancel() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/fixedcostinfo/updateload/")
				.param("actionCancel", "")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/fixedcost/FixedCostInfoManageInit"))
			.andExpect(model().attribute("fixedCostItemList", hasSize(5)));
	}

	// ================================================================
	// POST /delete/
	// ================================================================

	/**
	 *<pre>
	 * テスト⑧：正常系：POST /delete/ 固定費0003削除→リダイレクト
	 *
	 * 【検証内容】
	 * ・固定費コード=0003(国民年金保険)を削除すると完了画面にリダイレクトすること
	 * ・HTTPステータスが3xxリダイレクトであること
	 * ・リダイレクト先が「/myhacbook/managebaseinfo/fixedcostinfo/updateComplete/」であること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：POST /delete/ 固定費0003を削除してリダイレクト")
	void testPostDelete() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/fixedcostinfo/delete/")
				.param("fixedCostCode", "0003")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/myhacbook/managebaseinfo/fixedcostinfo/updateComplete/"));
	}

	// ================================================================
	// POST /update/ (バリデーションNG)
	// ================================================================

	/**
	 *<pre>
	 * テスト⑨：異常系：POST /update/ fixedCostName空→@NotBlankバリデーションエラー（新規追加時でバリデーション確認）
	 *
	 * 【検証内容】
	 * ・要求は新規追加時のパターンで送信
	 * ・fixedCostNameを空で送信した場合、バリデーションエラーが発生すること
	 * ・HTTPステータスが200で更新画面に戻ること
	 * ・ビュー名が「itemmanage/fixedcost/FixedCostInfoManageUpdate」であること
	 * ・fixedCostInfoUpdateFormのfixedCostNameフィールドにエラーが設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：POST /update/ action=add バリデーションNG(fixedCostName空)→更新画面に戻る")
	void testPostUpdate_validationError() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/fixedcostinfo/update/")
				.param("actionAdd", "")
				.param("action", "add")
				.param("sisyutuItemCode", "0035")
				// fixedCostName を空にしてバリデーションエラーを発生させる
				.param("fixedCostName", "")
				.param("fixedCostKubun", "1")
				.param("shiharaiTuki", "00")
				.param("shiharaiDay", "27")
				.param("shiharaiKingaku", "10000")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/fixedcost/FixedCostInfoManageUpdate"))
			.andExpect(model().attributeHasFieldErrors("fixedCostInfoUpdateForm", "fixedCostName"));
	}

	/**
	 *<pre>
	 * テスト⑩：異常系：POST /update/ shiharaiTuki='40'+shiharaiTukiOptionalContext空→相関バリデーションエラー（更新時でバリデーション確認）
	 *
	 * 【検証内容】
	 * ・要求は更新時のパターンで送信
	 * ・支払月='40'(その他任意)を選択し、支払月任意詳細を空で送信した場合、相関バリデーションエラーが発生すること
	 * ・FixedCostInfoUpdateFormの@AssertTrue isNeedCheckShiharaiTukiOptionalContext()が機能すること
	 * ・HTTPステータスが200で更新画面に戻ること
	 * ・fixedCostInfoUpdateFormのneedCheckShiharaiTukiOptionalContextフィールドにエラーが設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：POST /update/ action=update バリデーションNG(shiharaiTuki=40, OptionalContext空)→相関バリデーションエラー")
	void testPostUpdate_validationError_optionalContext() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/fixedcostinfo/update/")
				.param("actionUpdate", "")
				.param("action", "update")
				.param("sisyutuItemCode", "0035")
				.param("fixedCostName", "テスト固定費")
				.param("fixedCostKubun", "1")
				.param("shiharaiTuki", "40")
				// shiharaiTukiOptionalContext を空にして相関バリデーションエラーを発生させる
				.param("shiharaiTukiOptionalContext", "")
				.param("shiharaiDay", "27")
				.param("shiharaiKingaku", "10000")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/fixedcost/FixedCostInfoManageUpdate"))
			.andExpect(model().attributeHasFieldErrors("fixedCostInfoUpdateForm", "needCheckShiharaiTukiOptionalContext"));
	}

	// ================================================================
	// POST /update/ (ADD成功)
	// ================================================================

	/**
	 *<pre>
	 * テスト⑪：正常系：POST /update/ action=add 新規追加→リダイレクト
	 *
	 * 【検証内容】
	 * ・支出項目コード=0035(自由用途積立金)に新規固定費を追加した場合、完了画面にリダイレクトすること
	 * ・HTTPステータスが3xxリダイレクトであること
	 * ・リダイレクト先が「/myhacbook/managebaseinfo/fixedcostinfo/updateComplete/」であること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：POST /update/ action=add 新規追加してリダイレクト")
	void testPostUpdate_addSuccess() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/fixedcostinfo/update/")
				.param("actionAdd", "")
				.param("action", "add")
				.param("sisyutuItemCode", "0035")
				.param("fixedCostName", "自由用途積立")
				.param("fixedCostDetailContext", "毎月積立")
				.param("fixedCostKubun", "1")
				.param("shiharaiTuki", "00")
				.param("shiharaiTukiOptionalContext", "")
				.param("shiharaiDay", "27")
				.param("shiharaiKingaku", "30000")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/myhacbook/managebaseinfo/fixedcostinfo/updateComplete/"));
	}

	// ================================================================
	// POST /update/ (UPDATE成功)
	// ================================================================

	/**
	 *<pre>
	 * テスト⑫：正常系：POST /update/ action=update 更新→リダイレクト
	 *
	 * 【検証内容】
	 * ・固定費コード=0002(電気代概算)を更新した場合、完了画面にリダイレクトすること
	 * ・HTTPステータスが3xxリダイレクトであること
	 * ・リダイレクト先が「/myhacbook/managebaseinfo/fixedcostinfo/updateComplete/」であること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：POST /update/ action=update 更新してリダイレクト")
	void testPostUpdate_updateSuccess() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/fixedcostinfo/update/")
				.param("actionUpdate", "")
				.param("action", "update")
				.param("fixedCostCode", "0002")
				.param("sisyutuItemCode", "0037")
				.param("fixedCostName", "電気代(更新後)")
				.param("fixedCostDetailContext", "更新後の詳細")
				.param("fixedCostKubun", "1")
				.param("shiharaiTuki", "00")
				.param("shiharaiTukiOptionalContext", "")
				.param("shiharaiDay", "27")
				.param("shiharaiKingaku", "15000")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/myhacbook/managebaseinfo/fixedcostinfo/updateComplete/"));
	}

	// ================================================================
	// GET /updateComplete/
	// ================================================================

	/**
	 *<pre>
	 * テスト⑬：正常系：GET /updateComplete/ 更新完了後の初期表示
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること
	 * ・ビュー名が「itemmanage/fixedcost/FixedCostInfoManageInit」であること
	 * ・fixedCostItemListが4件で取得されること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：GET /updateComplete/ 完了後の初期表示")
	void testGetUpdateComplete() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/fixedcostinfo/updateComplete/")
				.with(user("user01").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/fixedcost/FixedCostInfoManageInit"))
			.andExpect(model().attribute("fixedCostItemList", hasSize(5)));
	}

	// ================================================================
	// POST /update/ (params: actionCancel)
	// ================================================================

	/**
	 *<pre>
	 * テスト⑭：正常系：POST /update/ actionCancel (action=add) → 初期表示画面へ
	 *
	 * 【検証内容】
	 * ・新規追加からキャンセル操作（action=add）の場合、初期表示画面に遷移すること
	 * ・HTTPステータスが200であること
	 * ・ビュー名が「itemmanage/fixedcost/FixedCostInfoManageInit」であること
	 * ・fixedCostItemListが5件で取得されること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：POST /update/ actionCancel (action=add) 初期表示画面へ")
	void testPostUpdateCancel_actionAdd() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/fixedcostinfo/update/")
				.param("actionCancel", "")
				.param("action", "add")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/fixedcost/FixedCostInfoManageInit"))
			.andExpect(model().attribute("fixedCostItemList", hasSize(5)));
	}

	/**
	 *<pre>
	 * テスト⑮：正常系：POST /update/ actionCancel (action=update, fixedCostCode=0001) → 処理選択画面へ
	 *
	 * 【検証内容】
	 * ・更新からキャンセル操作（action=update）の場合、処理選択画面に遷移すること
	 * ・固定費コード=0001(家賃)を指定して処理選択画面に戻ること
	 * ・HTTPステータスが200であること
	 * ・ビュー名が「itemmanage/fixedcost/FixedCostInfoManageActSelect」であること
	 * ・fixedCostInfoがモデルに設定されること
	 * ・固定費コード0001は0030に1件のみのためhasSiblingFixedCostがfalseであること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：POST /update/ actionCancel (action=update, fixedCostCode=0001) 処理選択画面へ")
	void testPostUpdateCancel_actionUpdate() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/fixedcostinfo/update/")
				.param("actionCancel", "")
				.param("action", "update")
				.param("fixedCostCode", "0001")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/fixedcost/FixedCostInfoManageActSelect"))
			.andExpect(model().attributeExists("fixedCostInfo"))
			.andExpect(model().attribute("hasSiblingFixedCost", is(false)));
	}

	// ================================================================
	// GET /annualsummary/
	// ================================================================

	/**
	 *<pre>
	 * テスト⑯：正常系：GET /annualsummary/ 年間固定費合計画面表示
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること
	 * ・ビュー名が「itemmanage/fixedcost/FixedCostAnnualSummary」であること
	 * ・annualSummaryRowListが13件（12か月分のデータ行 + 合計行）で設定されること
	 * ・targetMonthに現在対象月「11」が設定されること
	 * ・loginUserNameに「テストユーザ01」が設定されること
	 *
	 * [テストデータの固定費5件と年間集計]
	 * 0001:家賃(毎月)・0002:電気代概算(毎月)・0004:その他任意(任意) → SEIKATSUHI
	 * 0003:国民年金保険(奇数月) → HIKOZEI
	 * 0005:電気代夏季割増(偶数月) → SEIKATSUHI
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：GET /annualsummary/ 年間固定費合計画面表示（12か月行 + 合計行 = 13行）")
	void testGetAnnualSummary() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/fixedcostinfo/annualsummary/")
				.with(user("user01").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/fixedcost/FixedCostAnnualSummary"))
			.andExpect(model().attribute("annualSummaryRowList", hasSize(13)))
			.andExpect(model().attribute("loginUserName", is("テストユーザ01")));
	}

	// ================================================================
	// GET /tabload/
	// ================================================================

	/**
	 *<pre>
	 * テスト⑰：正常系：GET /tabload/ 固定費管理タブ（他タブからの遷移）
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること
	 * ・ビュー名が「itemmanage/fixedcost/FixedCostInfoManageInit」であること
	 * ・fixedCostItemListが5件で取得されること
	 * ・セッションはクリアされないこと（initload と異なる点）の検証は、@Mockでの検証実現不可のため運用テストにて確認する
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：GET /tabload/ 固定費管理タブ（他タブからの遷移）")
	void testGetTabLoad() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/fixedcostinfo/tabload/")
				.with(user("user01").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/fixedcost/FixedCostInfoManageInit"))
			.andExpect(model().attribute("fixedCostItemList", hasSize(5)))
			.andExpect(model().attribute("loginUserName", is("テストユーザ01")));
	}

	// ================================================================
	// GET /monthlydetail/
	// ================================================================

	/**
	 *<pre>
	 * テスト⑱：正常系：GET /monthlydetail/?month=11 月別固定費一覧（11月・奇数月）
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること
	 * ・ビュー名が「itemmanage/fixedcost/FixedCostMonthlyDetail」であること
	 * ・displayMonthLabelが「11月」であること
	 * ・fixedCostItemListが4件（奇数月対象: 0003,0001,0002,0004）
	 * ・monthlyTotalが「98,590円」であること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：GET /monthlydetail/?month=11 月別固定費一覧（11月・奇数月）4件表示")
	void testGetMonthlyDetail_month11() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/fixedcostinfo/monthlydetail/")
				.param("month", "11")
				.with(user("user01").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/fixedcost/FixedCostMonthlyDetail"))
			.andExpect(model().attribute("displayMonthLabel", is("11月")))
			.andExpect(model().attribute("fixedCostItemList", hasSize(4)))
			.andExpect(model().attribute("monthlyTotal", is("98,590円")))
			.andExpect(model().attribute("loginUserName", is("テストユーザ01")));
	}
}
