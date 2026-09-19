/**
 * SimpleShoppingRegistControllerの統合テストです。
 * 買い物登録(簡易タイプ)機能のController層をMockMvcを使用してテストします。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller.account.regist;

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

import com.yonetani.webapp.accountbook.application.usecase.account.shoppingregist.SimpleShoppingRegistUseCase;
import com.yonetani.webapp.accountbook.presentation.controller.MyHouseholdAccountBookControllerAdvice;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

/**
 *<pre>
 * SimpleShoppingRegistControllerの統合テストです。
 * 買い物登録(簡易タイプ)機能のController層をMockMvcを使用してテストします。
 *
 * [テスト方針]
 * ・UseCase は本物のSpring Beanを使用（実際のH2 DBへのアクセスで動作確認）
 * ・LoginUserSession はモック化
 * ・standaloneSetup でControllerをセットアップ
 *
 * [テストデータ]
 * ・user01: NOW_TARGET_YEAR=2025, NOW_TARGET_MONTH=11
 * ・SHOP_TABLE: 店舗区分901に2件（デフォルト支払方法あり/なし）
 * ・PAYMENT_METHOD_TABLE: 001現金(有効)、002口座振替(有効)、003口座振替(無効化済み)
 * ・SHOPPING_REGIST_TABLE: 001(支払方法=001)、002(支払方法=003・無効化済みを参照)
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
	"/com/yonetani/webapp/accountbook/presentation/controller/account/regist/SimpleShoppingRegistControllerIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("買い物登録(簡易タイプ)機能のControllerテスト（統合テスト）")
class SimpleShoppingRegistControllerIntegrationTest {

	// MVCモック
	private MockMvc mockMvc;
	// 買い物登録(簡易タイプ)ユースケース(本物のSpring Bean)
	@Autowired
	private SimpleShoppingRegistUseCase usecase;
	// モック:ログインユーザセッション情報
	@Mock
	private LoginUserSession mockLoginUserSession;

	@BeforeEach
	void setupMockMvc() {
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new SimpleShoppingRegistController(usecase, mockLoginUserSession))
				.setControllerAdvice(new MyHouseholdAccountBookControllerAdvice(mockLoginUserSession))
				.build();

		doReturn(LoginUserInfo.from("user01", "テストユーザ01")).when(mockLoginUserSession).getLoginUserInfo();
	}

	@Test
	@DisplayName("正常系：GET / 初期表示で店舗・支払方法の選択肢が設定される")
	void testGetInitLoad_NormalCase() throws Exception {
		mockMvc.perform(get("/myhacbook/accountregist/simpleshoppingregist/")
				.param("targetYearMonth", "202511")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("account/regist/SimpleShoppingRegist"))
			.andExpect(model().attributeExists("shopNameOptionList"))
			.andExpect(model().attributeExists("paymentMethodSelectList"));
	}

	@Test
	@DisplayName("正常系：GET / 初期表示の登録済み買い物一覧に支払方法名が解決されて設定される")
	void testGetInitLoad_ShoppingRegistListPaymentMethodName() throws Exception {
		var result = mockMvc.perform(get("/myhacbook/accountregist/simpleshoppingregist/")
				.param("targetYearMonth", "202511")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andReturn();

		@SuppressWarnings("unchecked")
		var shoppingRegistList = (java.util.List<com.yonetani.webapp.accountbook.presentation.response.account.regist.AbstractSimpleShoppingRegistListResponse.SimpleShoppingRegistListItem>)
				result.getModelAndView().getModel().get("shoppingRegistList");
		org.junit.jupiter.api.Assertions.assertEquals(2, shoppingRegistList.size());

		var item001 = shoppingRegistList.stream()
				.filter(i -> "001".equals(i.getShoppingRegistCode())).findFirst().orElseThrow();
		org.junit.jupiter.api.Assertions.assertEquals("現金", item001.getPaymentMethodName(), "支払方法コード001は「現金」に解決されること");

		var item002 = shoppingRegistList.stream()
				.filter(i -> "002".equals(i.getShoppingRegistCode())).findFirst().orElseThrow();
		org.junit.jupiter.api.Assertions.assertEquals("無効化済み口座振替", item002.getPaymentMethodName(),
				"無効化された支払方法(003)でも、一覧では実際の支払方法名で表示されること(選択肢から除外されるのはフォームのプルダウンのみ)");
	}

	@Test
	@DisplayName("正常系：GET /updateload で既存の支払方法コードがフォームに反映される")
	void testGetUpdateLoad_NormalCase() throws Exception {
		mockMvc.perform(get("/myhacbook/accountregist/simpleshoppingregist/updateload")
				.param("targetYearMonth", "202511")
				.param("shoppingRegistCode", "001")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(model().attribute("simpleShoppingRegistInfoForm",
					org.hamcrest.Matchers.hasProperty("paymentMethodCode", org.hamcrest.Matchers.is("001"))));
	}

	@Test
	@DisplayName("正常系：無効化された支払方法を参照する既存行を開いた場合、フォームには値が維持され、選択肢には含まれない(disabled表示の前提条件)")
	void testGetUpdateLoad_DisabledPaymentMethod() throws Exception {
		var result = mockMvc.perform(get("/myhacbook/accountregist/simpleshoppingregist/updateload")
				.param("targetYearMonth", "202511")
				.param("shoppingRegistCode", "002")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(model().attribute("simpleShoppingRegistInfoForm",
					org.hamcrest.Matchers.hasProperty("paymentMethodCode", org.hamcrest.Matchers.is("003"))))
			.andReturn();

		// 選択肢一覧(findSelectableByUserId()ベース)には無効化された003が含まれないこと
		var paymentMethodSelectList = (com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem)
				result.getModelAndView().getModel().get("paymentMethodSelectList");
		boolean containsDisabledCode = paymentMethodSelectList.getOptionList().stream()
				.anyMatch(item -> "003".equals(item.getValue()));
		org.junit.jupiter.api.Assertions.assertFalse(containsDisabledCode,
				"無効化された支払方法003は選択肢に含まれないこと(テンプレート側でdisabled表示に切り替わる前提条件)");
	}

	@Test
	@DisplayName("異常系：POST /update/ 支払方法コード未入力の場合、バリデーションエラーで再表示される")
	void testPostUpdate_ValidationError_PaymentMethodCodeMissing() throws Exception {
		mockMvc.perform(post("/myhacbook/accountregist/simpleshoppingregist/update/")
				.param("action", "add")
				.param("targetYearMonth", "202511")
				.param("shopKubunCode", "901")
				.param("shopCode", "001")
				.param("shoppingDate", "2025/11/15")
				.param("totalPurchasePrice", "1000")
				.param("shoppingTotalAmount", "1000")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("account/regist/SimpleShoppingRegist"))
			.andExpect(model().attributeHasFieldErrors("simpleShoppingRegistInfoForm", "paymentMethodCode"));
	}

	@Test
	@DisplayName("正常系：POST /update/ 有効な支払方法コードを送信した場合、登録が完了しリダイレクトされる")
	void testPostUpdate_ValidationOK_Redirect() throws Exception {
		mockMvc.perform(post("/myhacbook/accountregist/simpleshoppingregist/update/")
				.param("action", "add")
				.param("targetYearMonth", "202511")
				.param("shopKubunCode", "901")
				.param("shopCode", "001")
				.param("shoppingDate", "2025/11/15")
				.param("paymentMethodCode", "001")
				// 食料品(必須)に1000円を計上(合計金額と一致させる。実画面ではJSが各項目の合計として自動算出する値)
				.param("shoppingFoodExpenses", "1000")
				.param("totalPurchasePrice", "1000")
				.param("shoppingTotalAmount", "1000")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("/myhacbook/accountregist/simpleshoppingregist/updateComplete/**"));
	}
}
