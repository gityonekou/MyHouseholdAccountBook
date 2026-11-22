/**
 * SimpleShoppingRegistControllerの統合テストです。
 * 買い物登録(簡易タイプ)機能のController層をMockMvcを使用してテストします。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 * 2026/09/23 : 1.00.01  feature-1.03-dev1   追加リファクタリング対応(SimpleShoppingRegistUseCaseの照会系・登録系分割に追従し、UseCaseで実施すべきテストを移設。Controller層で実施すべき不足テストの追加、SQLをUseCase層テストのものに統一)
 * 2026/11/01 : 1.00.02  feature-1.03-dev1   相関チェック統合(isCheckedShopKubunCodeMedicalshop/Barbershop→isCheckedShopKubunCodeConsumerGoods)に伴うフィールド名変更に追従
 * 2026/11/01 : 1.00.03  feature-1.03-dev1   相関チェックの分岐網羅テストはSimpleShoppingRegistInfoFormTestに一元化し、本クラスは代表ケース(相関チェック毎に正常系1件・異常系1件)に削減(07_テスト方針書.md §3.5.1準拠)
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

import com.yonetani.webapp.accountbook.application.usecase.account.shoppingregist.SimpleShoppingRegistConfirmUseCase;
import com.yonetani.webapp.accountbook.application.usecase.account.shoppingregist.SimpleShoppingRegistInquiryUseCase;
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
	// UseCase層テスト(SimpleShoppingRegistInquiryUseCaseIntegrationTest)のSQLを再利用(結合テストガイドライン10.9)
	"/com/yonetani/webapp/accountbook/application/usecase/account/shoppingregist/SimpleShoppingRegistInquiryIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("買い物登録(簡易タイプ)機能のControllerテスト（統合テスト）")
class SimpleShoppingRegistControllerIntegrationTest {

	// MVCモック
	private MockMvc mockMvc;
	// 買い物登録(簡易タイプ)ユースケース(照会系。本物のSpring Bean)
	@Autowired
	private SimpleShoppingRegistInquiryUseCase inquiryUseCase;
	// 買い物登録(簡易タイプ)ユースケース(登録系。本物のSpring Bean)
	@Autowired
	private SimpleShoppingRegistConfirmUseCase confirmUseCase;
	// モック:ログインユーザセッション情報
	@Mock
	private LoginUserSession mockLoginUserSession;

	@BeforeEach
	void setupMockMvc() {
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new SimpleShoppingRegistController(inquiryUseCase, confirmUseCase, mockLoginUserSession))
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

	@Test
	@DisplayName("正常系：POST / 店舗区分変更で店名選択肢が切り替わる")
	void testPostChangeShopKubun_NormalCase() throws Exception {
		mockMvc.perform(post("/myhacbook/accountregist/simpleshoppingregist/")
				.param("targetYearMonth", "202511")
				.param("shopKubunCode", "901")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("account/regist/SimpleShoppingRegist"))
			.andExpect(model().attributeExists("shopNameOptionList"));
	}

	@Test
	@DisplayName("正常系：GET /updateComplete/ 登録完了後の画面が表示される")
	void testGetUpdateComplete_NormalCase() throws Exception {
		mockMvc.perform(get("/myhacbook/accountregist/simpleshoppingregist/updateComplete/")
				.param("targetYearMonth", "202511")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("account/regist/SimpleShoppingRegist"))
			.andExpect(model().attributeExists("messages"));
	}

	@Test
	@DisplayName("正常系：POST /returndispatchaction/ (ReturnShoppingTop) 買い物登録方法選択画面へリダイレクトされる")
	void testPostReturnShoppingTop_Redirect() throws Exception {
		mockMvc.perform(post("/myhacbook/accountregist/simpleshoppingregist/returndispatchaction/")
				.param("targetYearMonth", "202511")
				.param("ReturnShoppingTop", "")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("/myhacbook/accountregist/shoppingtopmenu/**"));
	}

	@Test
	@DisplayName("正常系：POST /returndispatchaction/ (ReturnMonth) 各月の収支参照画面へリダイレクトされる")
	void testPostReturnInquiryMonth_Redirect() throws Exception {
		mockMvc.perform(post("/myhacbook/accountregist/simpleshoppingregist/returndispatchaction/")
				.param("targetYearMonth", "202511")
				.param("ReturnMonth", "")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("/myhacbook/accountinquiry/accountmonth/registComplete/**"));
	}

	/*
	 * ここから、SimpleShoppingRegistInfoFormの相関チェック(@AssertTrue)4件の代表ケース確認。
	 * 各相関チェックの分岐網羅(C1)はSimpleShoppingRegistInfoFormTest（Form単体テスト）および
	 * 各ドメイン層テストで実施済み(07_テスト方針書.md §3.5.1参照)。
	 * ここでは、HTTPパラメータバインディング、および@AssertTrueの合成フィールド名
	 * （例：isCheckedShoppingDate→"checkedShoppingDate"）へのエラーバインディングが正しく機能することを、
	 * 相関チェック毎に正常系1件・異常系1件の代表ケースで確認する（分岐網羅の代替ではない）。
	 * SimpleShoppingRegistResponse.addBindingErrorMessage()の除外リストに入っていないため、
	 * これらのエラーメッセージはmodelの"messages"ではなくBindingResultのフィールドエラーとして検証する。
	 * 「〇〇のみ正常」等、他項目のバリデーションを意図的に起こしてexecAction()（実データ更新）に進ませない
	 * ことで、合格分岐(true)の確認時にDB上に存在しない店舗区分・支払方法コードでの実登録を避けている。
	 */

	// ------ isCheckedShoppingDate ------

	@Test
	@DisplayName("相関チェック：買い物日が対象年月内の場合、checkedShoppingDateは合格(登録完了)")
	void testPostUpdate_CheckedShoppingDate_WithinTargetMonth_Valid() throws Exception {
		mockMvc.perform(post("/myhacbook/accountregist/simpleshoppingregist/update/")
				.param("action", "add")
				.param("targetYearMonth", "202511")
				.param("shopKubunCode", "901")
				.param("shopCode", "001")
				.param("shoppingDate", "2025/11/20")
				.param("paymentMethodCode", "001")
				// 食料品(必須)に1000円を計上(合計金額と一致させる。実画面ではJSが各項目の合計として自動算出する値)
				.param("shoppingFoodExpenses", "1000")
				.param("totalPurchasePrice", "1000")
				.param("shoppingTotalAmount", "1000")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	@DisplayName("相関チェック：買い物日が対象年月外の場合、checkedShoppingDateがエラーになる")
	void testPostUpdate_CheckedShoppingDate_OutsideTargetMonth_ValidationError() throws Exception {
		mockMvc.perform(post("/myhacbook/accountregist/simpleshoppingregist/update/")
				.param("action", "add")
				.param("targetYearMonth", "202511")
				.param("shopKubunCode", "901")
				.param("shopCode", "001")
				.param("shoppingDate", "2025/12/01")
				.param("paymentMethodCode", "001")
				.param("totalPurchasePrice", "1000")
				.param("shoppingTotalAmount", "1000")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("simpleShoppingRegistInfoForm", "checkedShoppingDate"))
			.andExpect(model().attributeErrorCount("simpleShoppingRegistInfoForm", 1));
	}

	// ------ isCheckedShopKubunCodeConsumerGoods ------

	@Test
	@DisplayName("相関チェック：薬局/薬局複合店/病院で日用品に値がある場合、checkedShopKubunCodeConsumerGoodsは合格")
	void testPostUpdate_MedicalShopWithConsumerGoods_Valid() throws Exception {
		// totalPurchasePriceを未入力にし、意図的に別項目のエラーを起こしてexecAction()への到達を避ける
		// (shopKubunCode=905に対応する実店舗データが無いため、実登録まで進めない)
		mockMvc.perform(post("/myhacbook/accountregist/simpleshoppingregist/update/")
				.param("action", "add")
				.param("targetYearMonth", "202511")
				.param("shopKubunCode", "905")
				.param("shopCode", "001")
				.param("shoppingDate", "2025/11/15")
				.param("paymentMethodCode", "001")
				.param("shoppingConsumerGoodsExpenses", "500")
				.param("shoppingTotalAmount", "500")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("simpleShoppingRegistInfoForm", "totalPurchasePrice"))
			.andExpect(model().attributeErrorCount("simpleShoppingRegistInfoForm", 1));
	}

	@Test
	@DisplayName("相関チェック：薬局/薬局複合店/病院で日用品が未入力の場合、checkedShopKubunCodeConsumerGoodsがエラーになる")
	void testPostUpdate_MedicalShopWithoutConsumerGoods_ValidationError() throws Exception {
		mockMvc.perform(post("/myhacbook/accountregist/simpleshoppingregist/update/")
				.param("action", "add")
				.param("targetYearMonth", "202511")
				.param("shopKubunCode", "905")
				.param("shopCode", "001")
				.param("shoppingDate", "2025/11/15")
				.param("paymentMethodCode", "001")
				.param("totalPurchasePrice", "1000")
				.param("shoppingTotalAmount", "1000")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("simpleShoppingRegistInfoForm", "checkedShopKubunCodeConsumerGoods"))
			.andExpect(model().attributeErrorCount("simpleShoppingRegistInfoForm", 1));
	}

	// ------ isCheckedTax（代表1項目のみ確認。8カテゴリ分の分岐網羅はSimpleShoppingRegistInfoFormTestで実施済み。
	//        全項目が違反しない正常系はtestPostUpdate_ValidationOK_Redirectで確認済み） ------

	@Test
	@DisplayName("相関チェック：食料品(必須)が未入力で消費税のみ入力の場合、checkedTaxがエラーになる")
	void testPostUpdate_CheckedTax_FoodOnly_ValidationError() throws Exception {
		mockMvc.perform(post("/myhacbook/accountregist/simpleshoppingregist/update/")
				.param("action", "add")
				.param("targetYearMonth", "202511")
				.param("shopKubunCode", "901")
				.param("shopCode", "001")
				.param("shoppingDate", "2025/11/15")
				.param("paymentMethodCode", "001")
				.param("totalPurchasePrice", "1000")
				.param("shoppingTotalAmount", "1000")
				.param("shoppingFoodTaxExpenses", "10")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("simpleShoppingRegistInfoForm", "checkedTax"))
			.andExpect(model().attributeErrorCount("simpleShoppingRegistInfoForm", 1));
	}

	@Test
	@DisplayName("相関チェック：食料品B(無駄遣い)が未入力で消費税のみ入力の場合、checkedTaxがエラーになる")
	void testPostUpdate_CheckedTax_FoodBOnly_ValidationError() throws Exception {
		mockMvc.perform(post("/myhacbook/accountregist/simpleshoppingregist/update/")
				.param("action", "add")
				.param("targetYearMonth", "202511")
				.param("shopKubunCode", "901")
				.param("shopCode", "001")
				.param("shoppingDate", "2025/11/15")
				.param("paymentMethodCode", "001")
				.param("totalPurchasePrice", "1000")
				.param("shoppingTotalAmount", "1000")
				.param("shoppingFoodBTaxExpenses", "10")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("simpleShoppingRegistInfoForm", "checkedTax"))
			.andExpect(model().attributeErrorCount("simpleShoppingRegistInfoForm", 1));
	}

	// ------ isPaymentMethodCodeValid ------

	@Test
	@DisplayName("相関チェック：支払方法コードにシステム予約値(999)を指定した場合、paymentMethodCodeValidがエラーになる")
	void testPostUpdate_PaymentMethodCodeSystemReserved_ValidationError() throws Exception {
		mockMvc.perform(post("/myhacbook/accountregist/simpleshoppingregist/update/")
				.param("action", "add")
				.param("targetYearMonth", "202511")
				.param("shopKubunCode", "901")
				.param("shopCode", "001")
				.param("shoppingDate", "2025/11/15")
				.param("paymentMethodCode", "999")
				.param("totalPurchasePrice", "1000")
				.param("shoppingTotalAmount", "1000")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("simpleShoppingRegistInfoForm", "paymentMethodCodeValid"))
			.andExpect(model().attributeErrorCount("simpleShoppingRegistInfoForm", 1));
	}
}
