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

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

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

import com.yonetani.webapp.accountbook.application.usecase.account.inquiry.AccountMonthInquiryUseCase;
import com.yonetani.webapp.accountbook.presentation.controller.MyHouseholdAccountBookControllerAdvice;
import com.yonetani.webapp.accountbook.presentation.response.fw.CompleteRedirectMessages;
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
 * @since 家計簿アプリ(1.00)
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
	"/sql/initsql/schema_test.sql",
	"/com/yonetani/webapp/accountbook/application/usecase/account/inquiry/AccountMonthInquiryIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("収支管理機能 月次収支照会のControllerテスト（統合テスト）")
public class AccountMonthInquiryControllerIntegrationTest {
	
	// MVCモック
	private MockMvc mockMvc;
//	// モック:月次収支照会コントローラー
//	@InjectMocks
//	private AccountMonthInquiryController injectMocksController;
	// 月次収支照会ユースケース
	@Autowired
	private AccountMonthInquiryUseCase accountMonthInquiryUseCase;
	// モック:ログインユーザセッション情報
	@Mock
	private LoginUserSession mockLloginUserSession;
	
	/**
	 *<pre>
	 * 月次収支照会コントローラーのログインユーザ情報をモック化して、MVCモックをセットアップします。
	 *</pre>
	 *
	 */
	@BeforeEach
	void setupMockMvc() {
		//this.mockMvc = MockMvcBuilders.standaloneSetup(injectMocksController).build();
		this.mockMvc = MockMvcBuilders
				// 月次収支照会コントローラーのセットアップ
				.standaloneSetup(new AccountMonthInquiryController(accountMonthInquiryUseCase, mockLloginUserSession))
				// ControllerAdviceのセットアップ(例外発生時のハンドリング)
				.setControllerAdvice(new MyHouseholdAccountBookControllerAdvice(mockLloginUserSession))
				// MVCモックのビルド
				.build();
	}
	
    /**
     * テスト用のログインユーザ情報を作成します。
     */
    private LoginUserInfo createLoginUser() {
        return LoginUserInfo.from("user01", "テストユーザ01");
    }
    
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
		// ユーザ情報をモックに設定
		doReturn(createLoginUser()).when(mockLloginUserSession).getLoginUserInfo();
		
		// 画面表示の検証
		mockMvc.perform(get("/myhacbook/accountinquiry/accountmonth/")
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
		// ユーザ情報をモックに設定
		doReturn(createLoginUser()).when(mockLloginUserSession).getLoginUserInfo();
		// 画面表示の検証
		mockMvc.perform(post("/myhacbook/accountinquiry/accountmonth/")
				.param("targetYearMonth", "202511")
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
		// ユーザ情報をモックに設定
		doReturn(createLoginUser()).when(mockLloginUserSession).getLoginUserInfo();
		// 画面表示の検証
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
		// ユーザ情報をモックに設定
		doReturn(createLoginUser()).when(mockLloginUserSession).getLoginUserInfo();
		// 画面表示の検証
		mockMvc.perform(post("/myhacbook/accountinquiry/accountmonth/targetcontrol/")
				.param("targetBeforeBtn", "")
				.param("beforeYearMonth", "202511")
				.param("returnYearMonth", "202512")
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
		// ユーザ情報をモックに設定
		doReturn(createLoginUser()).when(mockLloginUserSession).getLoginUserInfo();
		// 画面表示の検証
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
		// ユーザ情報をモックに設定
		doReturn(createLoginUser()).when(mockLloginUserSession).getLoginUserInfo();
		// 画面表示の検証
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
		// ユーザ情報をモックに設定
		doReturn(createLoginUser()).when(mockLloginUserSession).getLoginUserInfo();
		// 画面表示の検証
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
		// ユーザ情報をモックに設定
		doReturn(createLoginUser()).when(mockLloginUserSession).getLoginUserInfo();
		// 画面表示の検証
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
		// ユーザ情報をモックに設定
		doReturn(createLoginUser()).when(mockLloginUserSession).getLoginUserInfo();
		// 画面表示の検証
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
		// ユーザ情報をモックに設定
		doReturn(createLoginUser()).when(mockLloginUserSession).getLoginUserInfo();
		// 画面表示の検証
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
	 * - ControllerAdviceによりエラーページが表示される
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：登録完了後のリダイレクト_収支データなし（システムエラー）")
	public void testRegistComplete_WithoutData_ThrowsException() throws Exception {
		// ユーザ情報をモックに設定
		doReturn(createLoginUser()).when(mockLloginUserSession).getLoginUserInfo();
		// 画面表示の検証
		mockMvc.perform(get("/myhacbook/accountinquiry/accountmonth/registComplete/")
				.param("targetYearMonth", "202501")
				.flashAttr("redirectMessages", new String[] {"登録が完了しました"})
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isInternalServerError())
			.andExpect(view().name("error"))
			.andExpect(model().attributeExists("errorMessage"))
			.andExpect(model().attributeExists("errorTimestamp"))
			.andExpect(model().attributeExists("loginUserName"));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountinquiry/accountmonth/
	 * 指定月表示_targetYearMonthInfoの各フィールド値の検証
	 * - 対象年月、前月、翌月、表示用年・月、戻り先年月が正しく計算・設定されることを確認
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：指定月表示_targetYearMonthInfoの各フィールド値の検証")
	public void testPostAccountMonth_VerifyTargetYearMonthInfo() throws Exception {
		// ユーザ情報をモックに設定
		doReturn(createLoginUser()).when(mockLloginUserSession).getLoginUserInfo();
		// 画面表示の検証
		mockMvc.perform(post("/myhacbook/accountinquiry/accountmonth/")
				.param("targetYearMonth", "202511")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			// 対象年月
			.andExpect(model().attribute("targetYearMonthInfo", hasProperty("targetYearMonth", is("202511"))))
			// 戻り先年月(指定月表示の場合は対象年月と同値)
			.andExpect(model().attribute("targetYearMonthInfo", hasProperty("returnYearMonth", is("202511"))))
			// 前月(202511の前月=202510)
			.andExpect(model().attribute("targetYearMonthInfo", hasProperty("beforeYearMonth", is("202510"))))
			// 翌月(202511の翌月=202512)
			.andExpect(model().attribute("targetYearMonthInfo", hasProperty("nextYearMonth", is("202512"))))
			// 表示用年
			.andExpect(model().attribute("targetYearMonthInfo", hasProperty("viewYear", is("2025"))))
			// 表示用月
			.andExpect(model().attribute("targetYearMonthInfo", hasProperty("viewMonth", is("11"))));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountinquiry/accountmonth/
	 * 指定月表示_収支金額値の検証
	 * - 収入金額・支出金額・積立金取崩金額・支出予定金額・収支金額が
	 *   DBデータから正しくフォーマット("x,xxx円"形式)されていることを確認
	 * - withdrewKingaku, sisyutuYoteiKingaku, syuusiKingaku の存在確認も兼ねる
	 *   (既存テストでは attributeExists 未確認)
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：指定月表示_収支金額値の検証（withdrewKingaku・sisyutuYoteiKingaku・syuusiKingaku含む）")
	public void testPostAccountMonth_VerifyKingakuValues() throws Exception {
		// ユーザ情報をモックに設定
		doReturn(createLoginUser()).when(mockLloginUserSession).getLoginUserInfo();
		// 画面表示の検証
		// テストデータ(202511): INCOME_KINGAKU=350000, WITHDREW_KINGAKU=50000,
		//   EXPENDITURE_ESTIMATE_KINGAKU=300000, EXPENDITURE_KINGAKU=280000,
		//   INCOME_AND_EXPENDITURE_KINGAKU=120000
		mockMvc.perform(post("/myhacbook/accountinquiry/accountmonth/")
				.param("targetYearMonth", "202511")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			// 収入金額(積立金取崩金額以外): 350,000円
			.andExpect(model().attribute("syuunyuuKingaku", is("350,000円")))
			// 支出金額: 280,000円
			.andExpect(model().attribute("sisyutuKingaku", is("280,000円")))
			// 積立金取崩金額: 50,000円(attributeExists も兼ねて確認)
			.andExpect(model().attribute("withdrewKingaku", is("50,000円")))
			// 支出予定金額: 300,000円(attributeExists も兼ねて確認)
			.andExpect(model().attribute("sisyutuYoteiKingaku", is("300,000円")))
			// 収支金額: 120,000円(attributeExists も兼ねて確認)
			.andExpect(model().attribute("syuusiKingaku", is("120,000円")));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountinquiry/accountmonth/
	 * 指定月表示_支出項目リストのサイズ検証
	 * - expenditureItemListの件数がSISYUTU_KINGAKU_TABLEのレコード数と一致することを確認
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：指定月表示_支出項目リストのサイズ検証")
	public void testPostAccountMonth_VerifyExpenditureItemListSize() throws Exception {
		// ユーザ情報をモックに設定
		doReturn(createLoginUser()).when(mockLloginUserSession).getLoginUserInfo();
		// 画面表示の検証
		// テストデータ(202511): SISYUTU_KINGAKU_TABLE に 7件のレコード(0001〜0007)
		mockMvc.perform(post("/myhacbook/accountinquiry/accountmonth/")
				.param("targetYearMonth", "202511")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(model().attribute("expenditureItemList", hasSize(7)));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountinquiry/accountmonth/targetcontrol/ (targetBeforeBtn)
	 * 前月表示_returnYearMonthの引き継ぎ確認
	 * - 前月ボタン押下時、戻り先年月(returnYearMonth)が前画面の年月として正しく保持されることを確認
	 * - キャンセル時に前画面に戻るための重要な値の検証
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：前月表示_returnYearMonthの引き継ぎ確認")
	public void testPostBeforeAccountMonth_VerifyReturnYearMonth() throws Exception {
		// ユーザ情報をモックに設定
		doReturn(createLoginUser()).when(mockLloginUserSession).getLoginUserInfo();
		// beforeYearMonth=202511(表示対象), returnYearMonth=202512(遷移元の表示年月)
		mockMvc.perform(post("/myhacbook/accountinquiry/accountmonth/targetcontrol/")
				.param("targetBeforeBtn", "")
				.param("beforeYearMonth", "202511")
				.param("returnYearMonth", "202512")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			// 表示対象の年月は前月(202511)
			.andExpect(model().attribute("targetYearMonthInfo", hasProperty("targetYearMonth", is("202511"))))
			// 戻り先年月は遷移元の年月(202512)が引き継がれていること
			.andExpect(model().attribute("targetYearMonthInfo", hasProperty("returnYearMonth", is("202512"))));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountinquiry/accountmonth/targetcontrol/ (targetNextBtn)
	 * 次月表示_returnYearMonthの引き継ぎ確認
	 * - 次月ボタン押下時、戻り先年月(returnYearMonth)が前画面の年月として正しく保持されることを確認
	 * - キャンセル時に前画面に戻るための重要な値の検証
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：次月表示_returnYearMonthの引き継ぎ確認")
	public void testPostNextAccountMonth_VerifyReturnYearMonth() throws Exception {
		// ユーザ情報をモックに設定
		doReturn(createLoginUser()).when(mockLloginUserSession).getLoginUserInfo();
		// nextYearMonth=202511(表示対象), returnYearMonth=202509(遷移元の表示年月)
		mockMvc.perform(post("/myhacbook/accountinquiry/accountmonth/targetcontrol/")
				.param("targetNextBtn", "")
				.param("nextYearMonth", "202511")
				.param("returnYearMonth", "202509")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			// 表示対象の年月は次月(202511)
			.andExpect(model().attribute("targetYearMonthInfo", hasProperty("targetYearMonth", is("202511"))))
			// 戻り先年月は遷移元の年月(202509)が引き継がれていること
			.andExpect(model().attribute("targetYearMonthInfo", hasProperty("returnYearMonth", is("202509"))));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountinquiry/accountmonth/dispatchaction/ (shoppinAdd)
	 * 買い物登録ボタン押下_リダイレクト先URLの確認
	 * - 買い物登録画面への正しいリダイレクトURLが設定されていることを確認
	 * - targetYearMonthがクエリパラメータとして引き継がれることを確認
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：買い物登録ボタン押下_リダイレクト先URLの確認")
	public void testGetShoppingAddRedirectLoad_VerifyRedirectUrl() throws Exception {
		// ユーザ情報をモックに設定
		doReturn(createLoginUser()).when(mockLloginUserSession).getLoginUserInfo();
		// 画面表示の検証
		mockMvc.perform(post("/myhacbook/accountinquiry/accountmonth/dispatchaction/")
				.param("shoppinAdd", "")
				.param("targetYearMonth", "202511")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/myhacbook/accountregist/shoppingtopmenu/?targetYearMonth=202511"));
	}

	/**
	 *<pre>
	 * 【正常系】POST /myhacbook/accountinquiry/accountmonth/dispatchaction/ (accountMonthUpdate)
	 * 収支更新ボタン押下_リダイレクト先URLの確認
	 * - 収支登録(更新)画面への正しいリダイレクトURLが設定されていることを確認
	 * - targetYearMonthがクエリパラメータとして引き継がれることを確認
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：収支更新ボタン押下_リダイレクト先URLの確認")
	public void testGetAccountMonthUpdateRedirectLoad_VerifyRedirectUrl() throws Exception {
		// ユーザ情報をモックに設定
		doReturn(createLoginUser()).when(mockLloginUserSession).getLoginUserInfo();
		// 画面表示の検証
		mockMvc.perform(post("/myhacbook/accountinquiry/accountmonth/dispatchaction/")
				.param("accountMonthUpdate", "")
				.param("targetYearMonth", "202511")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/myhacbook/accountregist/incomeandexpenditure/updateload/?targetYearMonth=202511"));
	}

	/**
	 *<pre>
	 * 【正常系】GET /myhacbook/accountinquiry/accountmonth/registComplete/
	 * 登録完了後のリダイレクト_CompleteRedirectMessages型でのflashAttr設定確認
	 * - Controller の @ModelAttribute CompleteRedirectMessages に適切な型を渡した場合も
	 *   正常にAccountMonth画面が表示されることを確認
	 * - 既存の testRegistComplete_WithData では String[] で flashAttr を渡していたが、
	 *   本テストでは正しい型 CompleteRedirectMessages を使用する
	 *
	 * ※ standaloneSetup ではフラッシュ属性が @ModelAttribute パラメータに自動伝播しないため、
	 *    messages 属性の内容検証は行わない（messages は空リストとなる）
	 *    本番の画面遷移では redirect → GET のフロー全体でメッセージが正しく表示される
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：登録完了後のリダイレクト_CompleteRedirectMessages型でのflashAttr設定確認")
	public void testRegistComplete_WithCompleteRedirectMessages() throws Exception {
		// ユーザ情報をモックに設定
		doReturn(createLoginUser()).when(mockLloginUserSession).getLoginUserInfo();
		// CompleteRedirectMessages 型で flashAttr を設定(正しい型での渡し方の確認)
		CompleteRedirectMessages flashMsg = new CompleteRedirectMessages();
		flashMsg.setRedirectMessages(Arrays.asList("登録が完了しました"));
		// 画面表示の検証
		mockMvc.perform(get("/myhacbook/accountinquiry/accountmonth/registComplete/")
				.param("targetYearMonth", "202511")
				.flashAttr("redirectMessages", flashMsg)
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("account/inquiry/AccountMonth"))
			// 収支データが取得されていること
			.andExpect(model().attribute("targetYearMonthInfo", hasProperty("targetYearMonth", is("202511"))))
			.andExpect(model().attribute("syuunyuuKingaku", is("350,000円")))
			.andExpect(model().attribute("sisyutuKingaku", is("280,000円")))
			// loginUserName がモデルに設定されていること
			.andExpect(model().attribute("loginUserName", is("テストユーザ01")));
	}

	/**
	 *<pre>
	 * 【正常系】GET /myhacbook/accountinquiry/accountmonth/
	 * 初期表示_loginUserNameのモデル設定確認
	 * - ControllerがレスポンスにログインユーザIDを設定することを確認
	 * - 全エンドポイントでセットされるが、初期表示で代表して確認
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：初期表示_loginUserNameのモデル設定確認")
	public void testGetInitAccountMonth_VerifyLoginUserName() throws Exception {
		// ユーザ情報をモックに設定
		doReturn(createLoginUser()).when(mockLloginUserSession).getLoginUserInfo();
		// 画面表示の検証
		mockMvc.perform(get("/myhacbook/accountinquiry/accountmonth/")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			// ログインユーザ名がモデルに設定されていること
			.andExpect(model().attribute("loginUserName", is("テストユーザ01")));
	}
}
