/**
 * BankAccountInfoManageControllerの統合テストです。
 * 銀行口座情報管理機能のController層をMockMvcを使用してテストします。
 *
 * <pre>
 * [テスト方針]
 * ・BankAccountInfoManageUseCase は本物のSpring Beanを使用
 * ・LoginUserSession はモック化（セッションスコープBean）
 * ・standaloneSetup でControllerをセットアップ
 *
 * [テストシナリオ]
 * 1. 正常系：GET /initload/ 銀行口座一覧3件の初期表示
 * 2. 正常系：GET /updateload?bankAccountCode=02 更新対象選択時の表示
 * 3. 異常系：POST /update/ bankName空→@NotBlankバリデーションエラー（画面に戻る）
 * 4. 正常系：POST /update/ action=add 新規追加してリダイレクト
 * 5. 正常系：POST /update/ action=update 更新してリダイレクト
 * 6. 正常系：POST /toggleEnable/ 有効/無効切替してリダイレクト
 * 7. 正常系：GET /updateComplete/ 完了後の初期表示
 *
 * [テストデータ]
 * 銀行口座3件(TESTUSER001): 01:テストユーザ銀行０１(有効), 02:テストユーザ銀行０２(有効), 03:テストユーザ銀行０３(無効)
 * (UseCase層統合テストと同じSQLフィクスチャ: ReadBankAccountInfoQueryResultThreeTest.sql)
 *
 * </pre>
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/11/02 : 1.00.00  feature-1.03-dev1   新規作成
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

import com.yonetani.webapp.accountbook.application.usecase.itemmanage.bankaccount.BankAccountInfoManageUseCase;
import com.yonetani.webapp.accountbook.presentation.controller.MyHouseholdAccountBookControllerAdvice;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

/**
 *<pre>
 * BankAccountInfoManageControllerの統合テストです。
 * 銀行口座情報管理機能のController層をMockMvcを使用してテストします。
 *
 * [テスト方針]
 * ・BankAccountInfoManageUseCase は本物のSpring Beanを使用
 * ・LoginUserSession はモック化（セッションスコープBean）
 * ・standaloneSetup でControllerをセットアップ
 *
 * [テストデータ]
 * 銀行口座3件(TESTUSER001): 01:テストユーザ銀行０１(有効), 02:テストユーザ銀行０２(有効), 03:テストユーザ銀行０３(無効)
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
	"/sql/initsql/schema_test.sql",
	"/com/yonetani/webapp/accountbook/application/usecase/itemmanage/bankaccount/ReadBankAccountInfoQueryResultThreeTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("銀行口座情報管理 Controllerテスト（統合テスト）")
public class BankAccountInfoManageControllerIntegrationTest {

	// MVCモック
	private MockMvc mockMvc;

	// UseCase(本物のSpring Bean)
	@Autowired
	private BankAccountInfoManageUseCase bankAccountInfoManageUseCase;

	// モック:ログインユーザセッション情報
	@Mock
	private LoginUserSession mockLoginUserSession;

	@BeforeEach
	void setupMockMvc() {
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new BankAccountInfoManageController(
						bankAccountInfoManageUseCase,
						mockLoginUserSession))
				.setControllerAdvice(new MyHouseholdAccountBookControllerAdvice(mockLoginUserSession))
				.build();

		doReturn(createLoginUser()).when(mockLoginUserSession).getLoginUserInfo();
	}

	private LoginUserInfo createLoginUser() {
		return LoginUserInfo.from("TESTUSER001", "テストユーザ01");
	}

	// ================================================================
	// GET /initload/
	// ================================================================

	/**
	 *<pre>
	 * テスト1：正常系：GET /initload/ 銀行口座一覧初期表示
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること
	 * ・ビュー名が「itemmanage/BankAccountInfoManage」であること
	 * ・bankAccountListが3件で取得されること
	 * ・loginUserNameに「テストユーザ01」が設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("1 正常系：GET /initload/ 銀行口座一覧3件の初期表示")
	void testGetInitLoad() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/bankaccountinfo/initload/")
				.with(user("TESTUSER001").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/BankAccountInfoManage"))
			.andExpect(model().attribute("bankAccountList", hasSize(3)))
			.andExpect(model().attribute("loginUserName", is("テストユーザ01")));
	}

	// ================================================================
	// GET /updateload
	// ================================================================

	/**
	 *<pre>
	 * テスト2：正常系：GET /updateload?bankAccountCode=02 更新対象選択時の表示
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること
	 * ・ビュー名が「itemmanage/BankAccountInfoManage」であること
	 * ・入力フォームのアクションが更新(UPDATE)、銀行口座コードが02であること
	 * ・更新対象であることのメッセージが設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("2 正常系：GET /updateload?bankAccountCode=02 更新対象選択時の表示")
	void testGetTargetLoad() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/bankaccountinfo/updateload")
				.param("bankAccountCode", "02")
				.with(user("TESTUSER001").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/BankAccountInfoManage"))
			.andExpect(model().attribute("bankAccountInfoForm",
					hasProperty("bankAccountCode", is("02"))))
			.andExpect(model().attribute("messages",
					hasItem(containsString("テストユーザ銀行０２"))));
	}

	// ================================================================
	// POST /update/
	// ================================================================

	/**
	 *<pre>
	 * テスト3：異常系：POST /update/ bankName空→@NotBlankバリデーションエラー
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること（リダイレクトではなく画面に戻る）
	 * ・ビュー名が「itemmanage/BankAccountInfoManage」であること
	 *</pre>
	 */
	@Test
	@DisplayName("3 異常系：POST /update/ bankName空→バリデーションエラーで画面に戻る")
	void testPostUpdate_ValidationError() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/bankaccountinfo/update/")
				.param("action", "add")
				.param("bankName", "")
				.with(user("TESTUSER001").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/BankAccountInfoManage"));
	}

	/**
	 *<pre>
	 * テスト4：正常系：POST /update/ action=add 新規追加してリダイレクト
	 *
	 * 【検証内容】
	 * ・ステータスがリダイレクト(3xx)であること
	 * ・リダイレクト先が「/myhacbook/managebaseinfo/bankaccountinfo/updateComplete/」であること
	 *</pre>
	 */
	@Test
	@DisplayName("4 正常系：POST /update/ action=add 新規追加してリダイレクト")
	void testPostUpdate_Add() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/bankaccountinfo/update/")
				.param("action", "add")
				.param("bankName", "新規テスト銀行")
				.with(user("TESTUSER001").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/myhacbook/managebaseinfo/bankaccountinfo/updateComplete/"));
	}

	/**
	 *<pre>
	 * テスト5：正常系：POST /update/ action=update 更新してリダイレクト
	 *
	 * 【検証内容】
	 * ・ステータスがリダイレクト(3xx)であること
	 * ・リダイレクト先が「/myhacbook/managebaseinfo/bankaccountinfo/updateComplete/」であること
	 *</pre>
	 */
	@Test
	@DisplayName("5 正常系：POST /update/ action=update 更新してリダイレクト")
	void testPostUpdate_Update() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/bankaccountinfo/update/")
				.param("action", "update")
				.param("bankAccountCode", "02")
				.param("bankAccountSortBefore", "02")
				.param("bankAccountSort", "2")
				.param("bankName", "更新後銀行名")
				.with(user("TESTUSER001").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/myhacbook/managebaseinfo/bankaccountinfo/updateComplete/"));
	}

	// ================================================================
	// POST /toggleEnable/
	// ================================================================

	/**
	 *<pre>
	 * テスト6：正常系：POST /toggleEnable/ 有効/無効切替してリダイレクト
	 *
	 * 【検証内容】
	 * ・ステータスがリダイレクト(3xx)であること
	 * ・リダイレクト先が「/myhacbook/managebaseinfo/bankaccountinfo/updateComplete/」であること
	 *</pre>
	 */
	@Test
	@DisplayName("6 正常系：POST /toggleEnable/ 有効/無効切替してリダイレクト")
	void testPostToggleEnable() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/bankaccountinfo/toggleEnable/")
				.param("bankAccountCode", "01")
				.with(user("TESTUSER001").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/myhacbook/managebaseinfo/bankaccountinfo/updateComplete/"));
	}

	// ================================================================
	// GET /updateComplete/
	// ================================================================

	/**
	 *<pre>
	 * テスト7：正常系：GET /updateComplete/ 完了後の初期表示
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること
	 * ・ビュー名が「itemmanage/BankAccountInfoManage」であること
	 *</pre>
	 */
	@Test
	@DisplayName("7 正常系：GET /updateComplete/ 完了後の初期表示")
	void testUpdateComplete() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/bankaccountinfo/updateComplete/")
				.with(user("TESTUSER001").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/BankAccountInfoManage"));
	}
}
