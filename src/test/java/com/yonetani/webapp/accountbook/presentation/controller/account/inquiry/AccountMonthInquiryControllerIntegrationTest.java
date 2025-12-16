/**
 * AccountMonthInquiryControllerの統合テストです。
 * 月次収支照会機能のController層をMockMvcを使用してテストします。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller.account.inquiry;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

/**
 *<pre>
 * AccountMonthInquiryControllerの統合テストです。
 * 月次収支照会機能のController層をMockMvcを使用してテストします。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
	"/sql/initsql/schema_test.sql",
	"/com/yonetani/webapp/accountbook/application/usecase/account/inquiry/AccountMonthInquiryIntegrationTest.sql"
})
public class AccountMonthInquiryControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	//private final LoginUserSession loginUserSession = new LoginUserSession();
	
	/**
	 *<pre>
	 * 【正常系】GET /myhacbook/accountinquiry/accountmonth/
	 * 現在の決算月の収支画面初期表示
	 * - 収支データが存在する場合、AccountMonth画面を表示
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：初期表示_収支データあり")
	public void testGetInitAccountMonth_WithData() throws Exception {
		// ログインユーザ情報
		LoginUserSession loginUserSession = new LoginUserSession();
		loginUserSession.setLoginUserInfo(LoginUserInfo.from("user01", "テストユーザ01"));
		
		mockMvc.perform(get("/myhacbook/accountinquiry/accountmonth/")
				.sessionAttr("loginUserSession", loginUserSession)
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("account/inquiry/AccountMonth"))
			.andExpect(model().attributeExists("targetYearMonthInfo"))
			.andExpect(model().attributeExists("syuunyuuKingaku"))
			.andExpect(model().attributeExists("sisyutuKingaku"))
			.andExpect(model().attributeExists("expenditureItemList"));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountinquiry/accountmonth/
	 * 指定月の収支画面表示
	 * - 収支データが存在する場合、AccountMonth画面を表示
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：指定月表示_収支データあり")
	public void testPostAccountMonth_WithData() throws Exception {
		mockMvc.perform(post("/myhacbook/accountinquiry/accountmonth/")
				.param("targetYearMonth", "202509")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("account/inquiry/AccountMonth"))
			.andExpect(model().attributeExists("targetYearMonthInfo"))
			.andExpect(model().attributeExists("syuunyuuKingaku"))
			.andExpect(model().attributeExists("sisyutuKingaku"))
			.andExpect(model().attributeExists("expenditureItemList"));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountinquiry/accountmonth/
	 * 指定月の収支画面表示
	 * - 収支データが存在しない場合、AccountMonthRegistCheck画面を表示
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：指定月表示_収支データなし")
	public void testPostAccountMonth_WithoutData() throws Exception {
		mockMvc.perform(post("/myhacbook/accountinquiry/accountmonth/")
				.param("targetYearMonth", "202501")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("account/inquiry/AccountMonthRegistCheck"))
			.andExpect(model().attributeExists("targetYearMonthInfo"));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountinquiry/accountmonth/targetcontrol/ (targetBeforeBtn)
	 * 前月の収支画面表示
	 * - 収支データが存在する場合、AccountMonth画面を表示
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：前月表示_収支データあり")
	public void testPostBeforeAccountMonth_WithData() throws Exception {
		mockMvc.perform(post("/myhacbook/accountinquiry/accountmonth/targetcontrol/")
				.param("targetBeforeBtn", "")
				.param("beforeYearMonth", "202509")
				.param("returnYearMonth", "202511")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("account/inquiry/AccountMonth"))
			.andExpect(model().attributeExists("targetYearMonthInfo"))
			.andExpect(model().attributeExists("syuunyuuKingaku"))
			.andExpect(model().attributeExists("sisyutuKingaku"))
			.andExpect(model().attributeExists("expenditureItemList"));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountinquiry/accountmonth/targetcontrol/ (targetBeforeBtn)
	 * 前月の収支画面表示
	 * - 収支データが存在しない場合、AccountMonthRegistCheck画面を表示
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：前月表示_収支データなし")
	public void testPostBeforeAccountMonth_WithoutData() throws Exception {
		mockMvc.perform(post("/myhacbook/accountinquiry/accountmonth/targetcontrol/")
				.param("targetBeforeBtn", "")
				.param("beforeYearMonth", "202501")
				.param("returnYearMonth", "202509")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("account/inquiry/AccountMonthRegistCheck"))
			.andExpect(model().attributeExists("targetYearMonthInfo"));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountinquiry/accountmonth/targetcontrol/ (targetNextBtn)
	 * 次月の収支画面表示
	 * - 収支データが存在する場合、AccountMonth画面を表示
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：次月表示_収支データあり")
	public void testPostNextAccountMonth_WithData() throws Exception {
		mockMvc.perform(post("/myhacbook/accountinquiry/accountmonth/targetcontrol/")
				.param("targetNextBtn", "")
				.param("nextYearMonth", "202511")
				.param("returnYearMonth", "202509")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("account/inquiry/AccountMonth"))
			.andExpect(model().attributeExists("targetYearMonthInfo"))
			.andExpect(model().attributeExists("syuunyuuKingaku"))
			.andExpect(model().attributeExists("sisyutuKingaku"))
			.andExpect(model().attributeExists("expenditureItemList"));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountinquiry/accountmonth/targetcontrol/ (targetNextBtn)
	 * 次月の収支画面表示
	 * - 収支データが存在しない場合、AccountMonthRegistCheck画面を表示
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：次月表示_収支データなし")
	public void testPostNextAccountMonth_WithoutData() throws Exception {
		mockMvc.perform(post("/myhacbook/accountinquiry/accountmonth/targetcontrol/")
				.param("targetNextBtn", "")
				.param("nextYearMonth", "202501")
				.param("returnYearMonth", "202511")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("account/inquiry/AccountMonthRegistCheck"))
			.andExpect(model().attributeExists("targetYearMonthInfo"));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountinquiry/accountmonth/dispatchaction/ (shoppinAdd)
	 * 買い物登録ボタン押下時のリダイレクト
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：買い物登録ボタン押下")
	public void testGetShoppingAddRedirectLoad() throws Exception {
		mockMvc.perform(post("/myhacbook/accountinquiry/accountmonth/dispatchaction/")
				.param("shoppinAdd", "")
				.param("targetYearMonth", "202511")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection());
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountinquiry/accountmonth/dispatchaction/ (accountMonthUpdate)
	 * 収支更新ボタン押下時のリダイレクト
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：収支更新ボタン押下")
	public void testGetAccountMonthUpdateRedirectLoad() throws Exception {
		mockMvc.perform(post("/myhacbook/accountinquiry/accountmonth/dispatchaction/")
				.param("accountMonthUpdate", "")
				.param("targetYearMonth", "202511")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection());
	}

	/**
	 *<pre>
	 * 【正常系】GET /myhacbook/accountinquiry/accountmonth/registComplete/
	 * 登録完了後のリダイレクト
	 * - 収支データが存在する場合、AccountMonth画面を表示
	 * - リダイレクトメッセージが画面に表示される
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：登録完了後のリダイレクト_収支データあり")
	public void testRegistComplete_WithData() throws Exception {
		mockMvc.perform(get("/myhacbook/accountinquiry/accountmonth/registComplete/")
				.param("targetYearMonth", "202511")
				.flashAttr("redirectMessages", new String[] {"登録が完了しました"})
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("account/inquiry/AccountMonth"))
			.andExpect(model().attributeExists("targetYearMonthInfo"))
			.andExpect(model().attributeExists("syuunyuuKingaku"))
			.andExpect(model().attributeExists("sisyutuKingaku"))
			.andExpect(model().attributeExists("expenditureItemList"));
	}

	/**
	 *<pre>
	 * 【異常系】GET /myhacbook/accountinquiry/accountmonth/registComplete/
	 * 登録完了後のリダイレクト
	 * - 収支データが存在しない場合、MyHouseholdAccountBookRuntimeExceptionがスローされる
	 * - これは登録処理の不具合など、想定外のシステムエラー
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：登録完了後のリダイレクト_収支データなし（システムエラー）")
	public void testRegistComplete_WithoutData_ThrowsException() throws Exception {
		mockMvc.perform(get("/myhacbook/accountinquiry/accountmonth/registComplete/")
				.param("targetYearMonth", "202501")
				.flashAttr("redirectMessages", new String[] {"登録が完了しました"})
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is5xxServerError());
	}
}
