/**
 * PaymentMethodInfoManageControllerの統合テストです。
 * 支払方法情報管理機能のController層をMockMvcを使用してテストします。
 *
 * <pre>
 * [テスト方針]
 * ・PaymentMethodInfoManageUseCase は本物のSpring Beanを使用
 * ・LoginUserSession はモック化（セッションスコープBean）
 * ・standaloneSetup でControllerをセットアップ
 *
 * [テストシナリオ]
 * 1. 正常系：GET /initload/ 支払方法一覧3件の初期表示
 * 2. 正常系：GET /updateload?paymentMethodCode=002 更新対象選択時の表示
 * 3. 正常系：GET /updateload?paymentMethodCode=999 システム予約行は更新不可で初期表示に戻る
 * 4. 異常系：POST /update/ paymentMethodName空→@NotBlankバリデーションエラー（画面に戻る）
 * 5. 正常系：POST /update/ action=add 新規追加してリダイレクト
 * 6. 正常系：POST /update/ action=update 更新してリダイレクト
 * 7. 正常系：POST /toggleEnable/ 有効/無効切替してリダイレクト
 * 8. 正常系：POST /toggleEnable/ システム予約行は更新拒否され画面に戻る（リダイレクトしない）
 * 9. 正常系：GET /updateComplete/ 完了後の初期表示
 *
 * [テストデータ]
 * 支払方法3件(TESTUSER001)＋システム予約行1件: 001:現金, 002:〇〇銀行引き落とし(口座振替), 003:△△カード(無効),
 * 999:支払方法がない(システム予約行、更新不可)
 * (UseCase層統合テストと同じSQLフィクスチャ: ReadPaymentMethodInfoQueryResultThreeTest.sql)
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

import com.yonetani.webapp.accountbook.application.usecase.itemmanage.paymentmethod.PaymentMethodInfoManageUseCase;
import com.yonetani.webapp.accountbook.presentation.controller.MyHouseholdAccountBookControllerAdvice;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

/**
 *<pre>
 * PaymentMethodInfoManageControllerの統合テストです。
 * 支払方法情報管理機能のController層をMockMvcを使用してテストします。
 *
 * [テスト方針]
 * ・PaymentMethodInfoManageUseCase は本物のSpring Beanを使用
 * ・LoginUserSession はモック化（セッションスコープBean）
 * ・standaloneSetup でControllerをセットアップ
 *
 * [テストデータ]
 * 支払方法3件(TESTUSER001)＋システム予約行1件: 001:現金, 002:〇〇銀行引き落とし(口座振替), 003:△△カード(無効),
 * 999:支払方法がない(システム予約行、更新不可)
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
	"/com/yonetani/webapp/accountbook/application/usecase/itemmanage/paymentmethod/ReadPaymentMethodInfoQueryResultThreeTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("支払方法情報管理 Controllerテスト（統合テスト）")
public class PaymentMethodInfoManageControllerIntegrationTest {

	// MVCモック
	private MockMvc mockMvc;

	// UseCase(本物のSpring Bean)
	@Autowired
	private PaymentMethodInfoManageUseCase paymentMethodInfoManageUseCase;

	// モック:ログインユーザセッション情報
	@Mock
	private LoginUserSession mockLoginUserSession;

	@BeforeEach
	void setupMockMvc() {
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new PaymentMethodInfoManageController(
						paymentMethodInfoManageUseCase,
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
	 * テスト1：正常系：GET /initload/ 支払方法一覧初期表示
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること
	 * ・ビュー名が「itemmanage/PaymentMethodInfoManage」であること
	 * ・paymentMethodListがシステム予約行を除いた3件で取得されること
	 * ・paymentMethodKubunOptionsが5種別分取得されること
	 * ・loginUserNameに「テストユーザ01」が設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("1 正常系：GET /initload/ 支払方法一覧3件の初期表示")
	void testGetInitLoad() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/paymentmethodinfo/initload/")
				.with(user("TESTUSER001").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/PaymentMethodInfoManage"))
			.andExpect(model().attribute("paymentMethodList", hasSize(3)))
			.andExpect(model().attribute("paymentMethodKubunOptions", hasSize(5)))
			.andExpect(model().attribute("loginUserName", is("テストユーザ01")));
	}

	// ================================================================
	// GET /updateload
	// ================================================================

	/**
	 *<pre>
	 * テスト2：正常系：GET /updateload?paymentMethodCode=002 更新対象選択時の表示
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること
	 * ・ビュー名が「itemmanage/PaymentMethodInfoManage」であること
	 * ・入力フォームのアクションが更新(UPDATE)、支払方法コードが002であること
	 * ・更新対象であることのメッセージが設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("2 正常系：GET /updateload?paymentMethodCode=002 更新対象選択時の表示")
	void testGetTargetLoad() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/paymentmethodinfo/updateload")
				.param("paymentMethodCode", "002")
				.with(user("TESTUSER001").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/PaymentMethodInfoManage"))
			.andExpect(model().attribute("paymentMethodInfoForm",
					hasProperty("paymentMethodCode", is("002"))))
			.andExpect(model().attribute("messages",
					hasItem(containsString("〇〇銀行引き落とし"))));
	}

	/**
	 *<pre>
	 * テスト3：正常系：GET /updateload?paymentMethodCode=999 システム予約行は更新不可で初期表示に戻る
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること
	 * ・ビュー名が「itemmanage/PaymentMethodInfoManage」であること
	 * ・更新不可のため、入力フォームのアクションが新規登録(ADD)に戻ること
	 *</pre>
	 */
	@Test
	@DisplayName("3 正常系：GET /updateload?paymentMethodCode=999 システム予約行は更新不可で初期表示に戻る")
	void testGetTargetLoad_SystemReserved() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/paymentmethodinfo/updateload")
				.param("paymentMethodCode", "999")
				.with(user("TESTUSER001").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/PaymentMethodInfoManage"))
			.andExpect(model().attribute("paymentMethodInfoForm",
					hasProperty("action", is("add"))));
	}

	// ================================================================
	// POST /update/
	// ================================================================

	/**
	 *<pre>
	 * テスト4：異常系：POST /update/ paymentMethodName空→@NotBlankバリデーションエラー
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること（リダイレクトではなく画面に戻る）
	 * ・ビュー名が「itemmanage/PaymentMethodInfoManage」であること
	 *</pre>
	 */
	@Test
	@DisplayName("4 異常系：POST /update/ paymentMethodName空→バリデーションエラーで画面に戻る")
	void testPostUpdate_ValidationError() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/paymentmethodinfo/update/")
				.param("action", "add")
				.param("paymentMethodName", "")
				.param("paymentMethodKubun", "1")
				.with(user("TESTUSER001").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/PaymentMethodInfoManage"));
	}

	/**
	 *<pre>
	 * テスト5：正常系：POST /update/ action=add 新規追加してリダイレクト
	 *
	 * 【検証内容】
	 * ・ステータスがリダイレクト(3xx)であること
	 * ・リダイレクト先が「/myhacbook/managebaseinfo/paymentmethodinfo/updateComplete/」であること
	 *</pre>
	 */
	@Test
	@DisplayName("5 正常系：POST /update/ action=add 新規追加してリダイレクト")
	void testPostUpdate_Add() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/paymentmethodinfo/update/")
				.param("action", "add")
				.param("paymentMethodName", "新規テスト支払方法")
				.param("paymentMethodKubun", "1")
				.with(user("TESTUSER001").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/myhacbook/managebaseinfo/paymentmethodinfo/updateComplete/"));
	}

	/**
	 *<pre>
	 * テスト6：正常系：POST /update/ action=update 更新してリダイレクト
	 *
	 * 【検証内容】
	 * ・ステータスがリダイレクト(3xx)であること
	 * ・リダイレクト先が「/myhacbook/managebaseinfo/paymentmethodinfo/updateComplete/」であること
	 *</pre>
	 */
	@Test
	@DisplayName("6 正常系：POST /update/ action=update 更新してリダイレクト")
	void testPostUpdate_Update() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/paymentmethodinfo/update/")
				.param("action", "update")
				.param("paymentMethodCode", "002")
				.param("paymentMethodSortBefore", "002")
				.param("paymentMethodSort", "2")
				.param("paymentMethodName", "更新後支払方法名")
				.param("paymentMethodKubun", "2")
				.param("bankAccountCode", "01")
				.with(user("TESTUSER001").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/myhacbook/managebaseinfo/paymentmethodinfo/updateComplete/"));
	}

	// ================================================================
	// POST /toggleEnable/
	// ================================================================

	/**
	 *<pre>
	 * テスト7：正常系：POST /toggleEnable/ 有効/無効切替してリダイレクト
	 *
	 * 【検証内容】
	 * ・ステータスがリダイレクト(3xx)であること
	 * ・リダイレクト先が「/myhacbook/managebaseinfo/paymentmethodinfo/updateComplete/」であること
	 *</pre>
	 */
	@Test
	@DisplayName("7 正常系：POST /toggleEnable/ 有効/無効切替してリダイレクト")
	void testPostToggleEnable() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/paymentmethodinfo/toggleEnable/")
				.param("paymentMethodCode", "002")
				.with(user("TESTUSER001").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/myhacbook/managebaseinfo/paymentmethodinfo/updateComplete/"));
	}

	/**
	 *<pre>
	 * テスト8：正常系：POST /toggleEnable/ システム予約行は更新拒否され画面に戻る（リダイレクトしない）
	 *
	 * 【検証内容】
	 * ・transactionSuccessFullがfalseのため、リダイレクトではなくHTTPステータス200で画面に戻ること
	 * ・ビュー名が「itemmanage/PaymentMethodInfoManage」であること
	 *</pre>
	 */
	@Test
	@DisplayName("8 正常系：POST /toggleEnable/ システム予約行は更新拒否され画面に戻る（リダイレクトしない）")
	void testPostToggleEnable_SystemReserved() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/paymentmethodinfo/toggleEnable/")
				.param("paymentMethodCode", "999")
				.with(user("TESTUSER001").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/PaymentMethodInfoManage"));
	}

	// ================================================================
	// GET /updateComplete/
	// ================================================================

	/**
	 *<pre>
	 * テスト9：正常系：GET /updateComplete/ 完了後の初期表示
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること
	 * ・ビュー名が「itemmanage/PaymentMethodInfoManage」であること
	 *</pre>
	 */
	@Test
	@DisplayName("9 正常系：GET /updateComplete/ 完了後の初期表示")
	void testUpdateComplete() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/paymentmethodinfo/updateComplete/")
				.with(user("TESTUSER001").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/PaymentMethodInfoManage"));
	}
}
