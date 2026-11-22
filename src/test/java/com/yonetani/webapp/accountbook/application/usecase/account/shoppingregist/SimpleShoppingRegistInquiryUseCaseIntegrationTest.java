/**
 * 簡易タイプの買い物登録を行うユースケース（照会系）の統合テストクラスです。
 *
 * <pre>
 * SimpleShoppingRegistInquiryUseCase の以下メソッドをテストします。
 *
 * [対象メソッド]
 * 1. read(user, targetYearMonth)                          - 買い物登録(簡易タイプ)画面初期表示
 * 2. read(user, targetYearMonth, shoppingRegistCode)       - 買い物登録(簡易タイプ)画面更新時表示
 * 3. readChangeShopKubun                                  - 店舗区分変更時の画面情報取得
 * 4. readBindingError                                     - バリデーションエラー時の画面情報取得
 * 5. readReturnShoppingTopRedirectInfo                    - 買い物登録方法選択画面(メニュー選択画面)へのリダイレクト情報取得
 * 6. readReturnInquiryMonthRedirectInfo                   - 各月の収支参照画面へのリダイレクト情報取得
 *
 * [テストシナリオ]
 * ① 正常系：初期表示_登録済み買い物一覧に支払方法名が解決されて設定される(read(user, targetYearMonth))
 * ② 正常系：更新時表示_無効化された支払方法を参照する行はフォームに値が維持され、選択肢には含まれない(read(user, targetYearMonth, shoppingRegistCode))
 * ③ 正常系：初期表示_新規登録時のデフォルト値が設定される(read(user, targetYearMonth))
 * ④ 正常系：更新時表示_登録済みの値がフォームへマッピングされる(read(user, targetYearMonth, shoppingRegistCode))
 * ⑤ 異常系：更新時表示_存在しない買い物登録コードで例外が発生する(read(user, targetYearMonth, shoppingRegistCode))
 * ⑥ 正常系：店舗区分変更_店名選択肢が切り替わる(readChangeShopKubun)
 * ⑦ 正常系：店舗区分変更_選択した店舗区分の店舗情報が0件の場合にメッセージが設定される(readChangeShopKubun)
 * ⑧ 正常系：バリデーションエラー時_入力フォームの値を維持したまま画面情報が生成される(readBindingError)
 * ⑨ 正常系：買い物登録方法選択画面へのリダイレクト情報が生成される(readReturnShoppingTopRedirectInfo)
 * ⑩ 正常系：各月の収支参照画面へのリダイレクト情報が生成される(readReturnInquiryMonthRedirectInfo)
 *
 * [テストデータ]
 * ・PAYMENT_METHOD_TABLE: 001現金(有効)、002口座振替(有効)、003無効化済み口座振替(無効)
 * ・SHOP_TABLE: 店舗区分901に2件(テストスーパー、デフォルト未設定店舗)。店舗区分902は0件
 * ・SHOPPING_REGIST_TABLE: 001(支払方法=001、食料品(必須)=3000円)、002(支払方法=003・無効化済みを参照)
 * </pre>
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/09/23 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.shoppingregist;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
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
import com.yonetani.webapp.accountbook.presentation.request.account.regist.SimpleShoppingRegistInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.AbstractSimpleShoppingRegistListResponse.SimpleShoppingRegistListItem;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.ShoppingRegistRedirectResponse;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.SimpleShoppingRegistResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

/**
 *<pre>
 * 簡易タイプの買い物登録を行うユースケース（照会系）の統合テストクラスです。
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
	"/sql/initsql/schema_test.sql",
	"/com/yonetani/webapp/accountbook/application/usecase/account/shoppingregist/SimpleShoppingRegistInquiryIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("簡易タイプの買い物登録ユースケース（照会系）のUseCaseテスト（統合テスト）")
class SimpleShoppingRegistInquiryUseCaseIntegrationTest {

	@Autowired
	private SimpleShoppingRegistInquiryUseCase useCase;

	// テスト用ログインユーザ
	private final LoginUserInfo TEST_USER = LoginUserInfo.from("user01", "テストユーザ01");

	/**
	 *<pre>
	 * テスト①：正常系：read(user, targetYearMonth)_登録済み買い物一覧に支払方法名が解決されて設定される
	 *
	 * 【検証内容】
	 * ・登録済みの買い物一覧が2件で取得されること
	 * ・支払方法コード001(現金・有効)は「現金」に解決されること
	 * ・支払方法コード003(無効化済み口座振替)でも、一覧では実際の支払方法名で表示されること
	 *   (選択肢から除外されるのはフォームのプルダウンのみで、一覧表示には影響しないこと)
	 *</pre>
	 */
	@Test
	@DisplayName("① read(user, targetYearMonth)_登録済み買い物一覧に支払方法名が解決されて設定される")
	void testRead_ShoppingRegistListPaymentMethodName() {
		SimpleShoppingRegistResponse response = useCase.read(TEST_USER, "202511");

		List<SimpleShoppingRegistListItem> shoppingRegistList = response.getSimpleShoppingRegistListItemInfo();
		assertEquals(2, shoppingRegistList.size(), "登録済みの買い物一覧が2件であること");

		SimpleShoppingRegistListItem item001 = shoppingRegistList.stream()
				.filter(i -> "001".equals(i.getShoppingRegistCode())).findFirst().orElseThrow();
		assertEquals("現金", item001.getPaymentMethodName(), "支払方法コード001は「現金」に解決されること");

		SimpleShoppingRegistListItem item002 = shoppingRegistList.stream()
				.filter(i -> "002".equals(i.getShoppingRegistCode())).findFirst().orElseThrow();
		assertEquals("無効化済み口座振替", item002.getPaymentMethodName(),
				"無効化された支払方法(003)でも、一覧では実際の支払方法名で表示されること(選択肢から除外されるのはフォームのプルダウンのみ)");
	}

	/**
	 *<pre>
	 * テスト②：正常系：read(user, targetYearMonth, shoppingRegistCode)_無効化された支払方法を参照する既存行を開いた場合、
	 * フォームには値が維持され、選択肢には含まれない(disabled表示の前提条件)
	 *
	 * 【検証内容】
	 * ・買い物登録コード002(支払方法=003・無効化済み)を開いた場合、フォームのpaymentMethodCodeに"003"が設定されること
	 * ・支払方法選択ボックス(findSelectableByUserId()ベース)には無効化された003が含まれないこと
	 *</pre>
	 */
	@Test
	@DisplayName("② read(user, targetYearMonth, shoppingRegistCode)_無効化された支払方法を参照する行はフォームに値が維持され、選択肢には含まれない")
	void testRead_DisabledPaymentMethod() {
		SimpleShoppingRegistResponse response = useCase.read(TEST_USER, "202511", "002");

		// フォームには無効化された支払方法コードがそのまま維持されること
		assertEquals("003", response.getSimpleShoppingRegistInfoForm().getPaymentMethodCode(),
				"無効化された支払方法コード003がフォームに維持されること");

		// 選択肢一覧(findSelectableByUserId()ベース)には無効化された003が含まれないこと
		List<OptionItem> paymentMethodOptionList = response.getPaymentMethodSelectList().getOptionList();
		boolean containsDisabledCode = paymentMethodOptionList.stream()
				.anyMatch(item -> "003".equals(item.getValue()));
		assertFalse(containsDisabledCode,
				"無効化された支払方法003は選択肢に含まれないこと(テンプレート側でdisabled表示に切り替わる前提条件)");
	}

	/**
	 *<pre>
	 * テスト③：正常系：read(user, targetYearMonth)_新規登録時のデフォルト値が設定される
	 *
	 * 【検証内容】
	 * ・アクションが新規登録(add)であること
	 * ・店舗区分が食品・日用品店舗(901)のデフォルト選択であること
	 * ・対象年月がそのまま設定されること
	 * ・買い物日のデフォルト値が対象年月の1日であること
	 *</pre>
	 */
	@Test
	@DisplayName("③ read(user, targetYearMonth)_新規登録時のデフォルト値が設定される")
	void testRead_NewRegistDefault() {
		SimpleShoppingRegistResponse response = useCase.read(TEST_USER, "202511");
		SimpleShoppingRegistInfoForm form = response.getSimpleShoppingRegistInfoForm();

		assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, form.getAction(), "アクションが新規登録であること");
		assertEquals(MyHouseholdAccountBookContent.SHOP_KUBUN_GROCERIES_SELECTED_VALUE, form.getShopKubunCode(),
				"店舗区分が食品・日用品店舗(901)のデフォルト選択であること");
		assertEquals("202511", form.getTargetYearMonth(), "対象年月が設定されること");
		assertEquals(LocalDate.of(2025, 11, 1), form.getShoppingDate(), "買い物日のデフォルト値が対象年月の1日であること");
	}

	/**
	 *<pre>
	 * テスト④：正常系：read(user, targetYearMonth, shoppingRegistCode)_登録済みの値がフォームへマッピングされる
	 *
	 * 【検証内容】
	 * ・買い物登録コード001の全基本項目(店舗区分・店舗コード・支払方法・買い物日・備考)がフォームへ反映されること
	 * ・登録済みの食料品(必須)購入金額(3000円)がフォームへ反映されること
	 * ・未登録の項目(食料品B等)はnullのままであること
	 * ・購入金額合計・買い物合計金額がフォームへ反映されること
	 *</pre>
	 */
	@Test
	@DisplayName("④ read(user, targetYearMonth, shoppingRegistCode)_登録済みの値がフォームへマッピングされる")
	void testRead_UpdateFieldMapping() {
		SimpleShoppingRegistResponse response = useCase.read(TEST_USER, "202511", "001");
		SimpleShoppingRegistInfoForm form = response.getSimpleShoppingRegistInfoForm();

		assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE, form.getAction(), "アクションが更新であること");
		assertEquals("202511", form.getTargetYearMonth());
		assertEquals("001", form.getShoppingRegistCode());
		assertEquals("901", form.getShopKubunCode());
		assertEquals("001", form.getShopCode());
		assertEquals("001", form.getPaymentMethodCode());
		assertEquals(LocalDate.of(2025, 11, 5), form.getShoppingDate());
		assertEquals("テスト買い物", form.getShoppingRemarks());
		assertEquals(Integer.valueOf(3000), form.getShoppingFoodExpenses(), "食料品(必須)購入金額が3000円であること");
		assertNull(form.getShoppingFoodBExpenses(), "未登録の食料品B(無駄遣い)購入金額はnullであること");
		assertEquals(Integer.valueOf(3000), form.getTotalPurchasePrice(), "購入金額合計が3000円であること");
		assertEquals(Integer.valueOf(3000), form.getShoppingTotalAmount(), "買い物合計金額が3000円であること");
	}

	/**
	 *<pre>
	 * テスト⑤：異常系：read(user, targetYearMonth, shoppingRegistCode)_存在しない買い物登録コードで例外が発生する
	 *
	 * 【検証内容】
	 * ・存在しない買い物登録コードを指定した場合、MyHouseholdAccountBookRuntimeExceptionが発生すること
	 *</pre>
	 */
	@Test
	@DisplayName("⑤ 異常系：read(user, targetYearMonth, shoppingRegistCode)_存在しない買い物登録コードで例外が発生する")
	void testRead_NotFound() {
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> useCase.read(TEST_USER, "202511", "999"));
		assertTrue(exception.getMessage().contains("更新対象の買い物登録情報が存在しません"));
	}

	/**
	 *<pre>
	 * テスト⑥：正常系：readChangeShopKubun_店舗区分変更で店名選択肢が切り替わる
	 *
	 * 【検証内容】
	 * ・店舗区分901を指定した場合、店名選択肢が2件(テストスーパー、デフォルト未設定店舗)であること
	 *</pre>
	 */
	@Test
	@DisplayName("⑥ readChangeShopKubun_店舗区分変更で店名選択肢が切り替わる")
	void testReadChangeShopKubun_ShopNameOptionList() {
		SimpleShoppingRegistInfoForm inputForm = new SimpleShoppingRegistInfoForm();
		inputForm.setTargetYearMonth("202511");
		inputForm.setShopKubunCode("901");

		SimpleShoppingRegistResponse response = useCase.readChangeShopKubun(TEST_USER, inputForm);

		assertEquals(2, response.getShopNameOptionList().size(), "店舗区分901の店名選択肢が2件であること");
		assertFalse(response.hasMessages(), "店舗情報が存在する場合はメッセージが設定されないこと");
	}

	/**
	 *<pre>
	 * テスト⑦：正常系：readChangeShopKubun_選択した店舗区分の店舗情報が0件の場合にメッセージが設定される
	 *
	 * 【検証内容】
	 * ・店舗情報が未登録の店舗区分902を指定した場合、0件メッセージが設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("⑦ readChangeShopKubun_選択した店舗区分の店舗情報が0件の場合にメッセージが設定される")
	void testReadChangeShopKubun_ShopZeroCase() {
		SimpleShoppingRegistInfoForm inputForm = new SimpleShoppingRegistInfoForm();
		inputForm.setTargetYearMonth("202511");
		inputForm.setShopKubunCode("902");

		SimpleShoppingRegistResponse response = useCase.readChangeShopKubun(TEST_USER, inputForm);

		assertTrue(response.hasMessages(), "店舗情報0件メッセージが設定されること");
		assertTrue(response.getMessagesList().stream().anyMatch(msg -> msg.contains("店舗情報が0件です")),
				"店舗情報0件メッセージが含まれること");
	}

	/**
	 *<pre>
	 * テスト⑧：正常系：readBindingError_入力フォームの値を維持したまま画面情報が生成される
	 *
	 * 【検証内容】
	 * ・バリデーションエラー時に渡した入力フォームの値(店舗区分)がそのままレスポンスに反映されること
	 *</pre>
	 */
	@Test
	@DisplayName("⑧ readBindingError_入力フォームの値を維持したまま画面情報が生成される")
	void testReadBindingError() {
		SimpleShoppingRegistInfoForm inputForm = new SimpleShoppingRegistInfoForm();
		inputForm.setTargetYearMonth("202511");
		inputForm.setShopKubunCode("901");

		SimpleShoppingRegistResponse response = useCase.readBindingError(TEST_USER, inputForm);

		assertSame(inputForm, response.getSimpleShoppingRegistInfoForm(), "渡した入力フォームがそのままレスポンスに設定されること");
		assertEquals("901", response.getSimpleShoppingRegistInfoForm().getShopKubunCode());
	}

	/**
	 *<pre>
	 * テスト⑨：正常系：readReturnShoppingTopRedirectInfo_買い物登録方法選択画面へのリダイレクト情報が生成される
	 *
	 * 【検証内容】
	 * ・買い物登録方法選択画面(メニュー選択画面)へのリダイレクトURLが設定されること
	 * ・トランザクション完了フラグがtrueであること
	 *</pre>
	 */
	@Test
	@DisplayName("⑨ readReturnShoppingTopRedirectInfo_買い物登録方法選択画面へのリダイレクト情報が生成される")
	void testReadReturnShoppingTopRedirectInfo() {
		ShoppingRegistRedirectResponse response =
				(ShoppingRegistRedirectResponse) useCase.readReturnShoppingTopRedirectInfo(TEST_USER, "202511");

		assertEquals("redirect:/myhacbook/accountregist/shoppingtopmenu/", response.getRedirectUrl());
		assertTrue(response.isTransactionSuccessFull(), "transactionSuccessFull=true");
	}

	/**
	 *<pre>
	 * テスト⑩：正常系：readReturnInquiryMonthRedirectInfo_各月の収支参照画面へのリダイレクト情報が生成される
	 *
	 * 【検証内容】
	 * ・各月の収支参照画面へのリダイレクトURLが設定されること
	 * ・トランザクション完了フラグがtrueであること
	 *</pre>
	 */
	@Test
	@DisplayName("⑩ readReturnInquiryMonthRedirectInfo_各月の収支参照画面へのリダイレクト情報が生成される")
	void testReadReturnInquiryMonthRedirectInfo() {
		ShoppingRegistRedirectResponse response =
				(ShoppingRegistRedirectResponse) useCase.readReturnInquiryMonthRedirectInfo(TEST_USER, "202511");

		assertEquals("redirect:/myhacbook/accountinquiry/accountmonth/registComplete/", response.getRedirectUrl());
		assertTrue(response.isTransactionSuccessFull(), "transactionSuccessFull=true");
	}
}
