/**
 * FixedCostInfoManageControllerの統合テストです。
 * 固定費情報管理機能のController層をMockMvcを使用してテストします。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/04/19 : 1.01.00  新規作成
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

import com.yonetani.webapp.accountbook.application.usecase.itemmanage.FixedCostInfoManageUseCase;
import com.yonetani.webapp.accountbook.application.usecase.itemmanage.FixedCostRegistConfirmUseCase;
import com.yonetani.webapp.accountbook.presentation.controller.MyHouseholdAccountBookControllerAdvice;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

/**
 *<pre>
 * FixedCostInfoManageControllerの統合テストです。
 * 固定費情報管理機能のController層をMockMvcを使用してテストします。
 *
 * [テスト方針]
 * ・FixedCostInfoManageUseCase / FixedCostRegistConfirmUseCase は本物のSpring Beanを使用
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
	"/com/yonetani/webapp/accountbook/application/usecase/itemmanage/FixedCostInfoManageIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("固定費情報管理 Controllerテスト（統合テスト）")
public class FixedCostInfoManageControllerIntegrationTest {

	// MVCモック
	private MockMvc mockMvc;

	// UseCase(参照系)(本物のSpring Bean)
	@Autowired
	private FixedCostInfoManageUseCase fixedCostInfoManageUseCase;

	// UseCase(更新系)(本物のSpring Bean)
	@Autowired
	private FixedCostRegistConfirmUseCase fixedCostRegistConfirmUseCase;

	// モック:ログインユーザセッション情報
	@Mock
	private LoginUserSession mockLoginUserSession;

	@BeforeEach
	void setupMockMvc() {
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new FixedCostInfoManageController(
						fixedCostInfoManageUseCase,
						fixedCostRegistConfirmUseCase,
						mockLoginUserSession))
				.setControllerAdvice(new MyHouseholdAccountBookControllerAdvice(mockLoginUserSession))
				.build();

		doReturn(createLoginUser()).when(mockLoginUserSession).getLoginUserInfo();
	}

	private LoginUserInfo createLoginUser() {
		return LoginUserInfo.from("user01", "テストユーザ01");
	}

	// ================================================================
	// GET /initload/
	// ================================================================

	@Test
	@DisplayName("正常系：GET /initload/ 固定費一覧4件の初期表示")
	void testGetInitLoad() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/fixedcostinfo/initload/")
				.with(user("user01").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/FixedCostInfoManageInit"))
			.andExpect(model().attribute("fixedCostItemList", hasSize(4)))
			.andExpect(model().attribute("loginUserName", is("テストユーザ01")));
	}

	// ================================================================
	// GET /select
	// ================================================================

	@Test
	@DisplayName("正常系：GET /select?fixedCostCode=0001 処理選択画面表示(家賃)")
	void testGetActSelect_0001() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/fixedcostinfo/select")
				.param("fixedCostCode", "0001")
				.with(user("user01").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/FixedCostInfoManageActSelect"))
			.andExpect(model().attribute("fixedCostInfo", notNullValue()))
			.andExpect(model().attribute("fixedCostItemList", hasSize(4)));
	}

	// ================================================================
	// GET /addload
	// ================================================================

	@Test
	@DisplayName("正常系：GET /addload?sisyutuItemCode=0035 固定費未登録→更新画面へ")
	void testGetAddLoad_notRegistered() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/fixedcostinfo/addload")
				.param("sisyutuItemCode", "0035")
				.with(user("user01").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/FixedCostInfoManageUpdate"))
			.andExpect(model().attribute("fixedCostInfoUpdateForm", notNullValue()));
	}

	@Test
	@DisplayName("正常系：GET /addload?sisyutuItemCode=0030 固定費登録済み→初期表示(登録済みメッセージ)")
	void testGetAddLoad_alreadyRegistered() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/fixedcostinfo/addload")
				.param("sisyutuItemCode", "0030")
				.with(user("user01").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/FixedCostInfoManageInit"))
			// 登録済みフラグがtrue
			.andExpect(model().attribute("registeredFlg", is(true)));
	}

	// ================================================================
	// POST /updateload/ (params: actionAdd)
	// ================================================================

	@Test
	@DisplayName("正常系：POST /updateload/ actionAdd 更新画面(追加)へ")
	void testPostActionAddLoad() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/fixedcostinfo/updateload/")
				.param("actionAdd", "")
				.param("sisyutuItemCode", "0030")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/FixedCostInfoManageUpdate"))
			.andExpect(model().attribute("fixedCostInfoUpdateForm", notNullValue()));
	}

	// ================================================================
	// POST /updateload/ (params: actionUpdate)
	// ================================================================

	@Test
	@DisplayName("正常系：POST /updateload/ actionUpdate 更新画面(更新)へ")
	void testPostActionUpdateLoad() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/fixedcostinfo/updateload/")
				.param("actionUpdate", "")
				.param("fixedCostCode", "0001")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/FixedCostInfoManageUpdate"))
			.andExpect(model().attribute("fixedCostInfoUpdateForm", notNullValue()));
	}

	// ================================================================
	// POST /updateload/ (params: actionCancel)
	// ================================================================

	@Test
	@DisplayName("正常系：POST /updateload/ actionCancel 初期表示画面へ戻る")
	void testPostActionCancel() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/fixedcostinfo/updateload/")
				.param("actionCancel", "")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/FixedCostInfoManageInit"))
			.andExpect(model().attribute("fixedCostItemList", hasSize(4)));
	}

	// ================================================================
	// POST /delete/
	// ================================================================

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

	@Test
	@DisplayName("正常系：POST /update/ バリデーションNG 更新画面に戻る")
	void testPostUpdate_validationError() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/fixedcostinfo/update/")
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
			.andExpect(view().name("itemmanage/FixedCostInfoManageUpdate"));
	}

	// ================================================================
	// POST /update/ (ADD成功)
	// ================================================================

	@Test
	@DisplayName("正常系：POST /update/ action=add 新規追加してリダイレクト")
	void testPostUpdate_addSuccess() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/fixedcostinfo/update/")
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

	@Test
	@DisplayName("正常系：POST /update/ action=update 更新してリダイレクト")
	void testPostUpdate_updateSuccess() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/fixedcostinfo/update/")
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

	@Test
	@DisplayName("正常系：GET /updateComplete/ 完了後の初期表示")
	void testGetUpdateComplete() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/fixedcostinfo/updateComplete/")
				.with(user("user01").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/FixedCostInfoManageInit"))
			.andExpect(model().attribute("fixedCostItemList", hasSize(4)));
	}
}
