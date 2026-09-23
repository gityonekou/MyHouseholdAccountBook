/**
 * 簡易タイプの買い物登録を行うユースケース（登録系）の統合テストクラスです。
 *
 * <pre>
 * SimpleShoppingRegistConfirmUseCase の以下メソッドをテストします。
 *
 * [対象メソッド]
 * 1. execAction(user, inputForm) - 買い物登録入力フォームの入力値に従ったアクション(登録 or 更新)実行処理
 *
 * [テストシナリオ]
 * ① 正常系：新規登録_単一カテゴリ(食料品(必須))、クーポンなし。支出テーブル・支出金額テーブル・収支テーブルへ反映されること
 * ② 正常系：新規登録_クーポン金額が複数カテゴリに跨って充当される(食料品(必須)を超過した残額が食料品Bへ繰越)。
 *   食料品(必須)は充当後0円のため支出テーブルは更新されないこと(hasExpenditureAmount()==falseでスキップ)
 * ③ 正常系：更新_登録済み買い物情報の金額を増額し、差額のみが支出テーブル・収支テーブルへ反映されること
 * ④ 異常系：未定義のアクションが指定された場合に例外が発生すること
 * ⑤ 異常系：更新_存在しない買い物登録コードで例外が発生すること
 *
 * [テストデータ]
 * ・EXPENDITURE_TABLE(202511)：飲食(無駄遣いなし)10,000円/飲食(無駄遣いB)2,000円/飲食(無駄遣いC)1,000円/
 *   外食5,000円/日用消耗品3,000円/被服費5,000円/流動経費10,000円/住居設備2,000円(合計38,000円)
 * ・SHOPPING_REGIST_TABLE：001(食料品(必須)2,000円。更新系テストの対象データ、新規登録系テストでは次コード="002"の起点)
 * </pre>
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/09/23 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.shoppingregist;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

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
import com.yonetani.webapp.accountbook.domain.model.account.expenditure.ExpenditureItem;
import com.yonetani.webapp.accountbook.domain.model.account.incomeandexpenditure.IncomeAndExpenditure;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.ShoppingRegist;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonthAndExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonthAndExpenditureItemCodeAndExpenditureCategory;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonthAndShoppingRegistCode;
import com.yonetani.webapp.accountbook.domain.repository.account.expenditure.ExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.expenditure.SisyutuKingakuTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.incomeandexpenditure.IncomeAndExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.shoppingregist.ShoppingRegistTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCategory;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.request.account.regist.SimpleShoppingRegistInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.SimpleShoppingRegistResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

/**
 *<pre>
 * 簡易タイプの買い物登録を行うユースケース（登録系）の統合テストクラスです。
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
	"/com/yonetani/webapp/accountbook/application/usecase/account/shoppingregist/SimpleShoppingRegistConfirmIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("簡易タイプの買い物登録ユースケース（登録系）のUseCaseテスト（統合テスト）")
class SimpleShoppingRegistConfirmUseCaseIntegrationTest {

	@Autowired
	private SimpleShoppingRegistConfirmUseCase useCase;
	@Autowired
	private ShoppingRegistTableRepository shoppingRegistRepository;
	@Autowired
	private ExpenditureTableRepository expenditureRepository;
	@Autowired
	private SisyutuKingakuTableRepository sisyutuKingakuTableRepository;
	@Autowired
	private IncomeAndExpenditureTableRepository incomeAndExpenditureRepository;

	// テスト用ログインユーザ
	private final LoginUserInfo TEST_USER = LoginUserInfo.from("user01", "テストユーザ01");
	private final UserId TEST_USER_ID = UserId.from("user01");
	private final TargetYearMonth TARGET_YEAR_MONTH = TargetYearMonth.from("202511");

	/**
	 *<pre>
	 * 飲食(無駄遣いなし)の支出テーブル情報(EXPENDITURE_KINGAKU)を取得します。
	 *</pre>
	 */
	private BigDecimal getFoodExpenditureKingaku() {
		ExpenditureItem item = expenditureRepository.findByExpenditureItemCodeAndCategory(
				SearchQueryUserIdAndYearMonthAndExpenditureItemCodeAndExpenditureCategory.from(
						TEST_USER_ID, TARGET_YEAR_MONTH, ExpenditureItemCode.from("0051"), ExpenditureCategory.NON_WASTED))
				.getValues().get(0);
		return item.getExpenditureAmount().getValue();
	}

	/**
	 *<pre>
	 * 飲食(無駄遣いB)の支出テーブル情報(EXPENDITURE_KINGAKU)を取得します。
	 *</pre>
	 */
	private BigDecimal getFoodBExpenditureKingaku() {
		ExpenditureItem item = expenditureRepository.findByExpenditureItemCodeAndCategory(
				SearchQueryUserIdAndYearMonthAndExpenditureItemCodeAndExpenditureCategory.from(
						TEST_USER_ID, TARGET_YEAR_MONTH, ExpenditureItemCode.from("0051"), ExpenditureCategory.WASTED_B))
				.getValues().get(0);
		return item.getExpenditureAmount().getValue();
	}

	/**
	 *<pre>
	 * 飲食(0051)の支出金額テーブル情報(SISYUTU_KINGAKU、合計値)を取得します。
	 *</pre>
	 */
	private BigDecimal getFoodSisyutuKingaku() {
		return sisyutuKingakuTableRepository.findByPrimaryKey(
				SearchQueryUserIdAndYearMonthAndExpenditureItemCode.from(TEST_USER_ID, TARGET_YEAR_MONTH, ExpenditureItemCode.from("0051")))
				.getExpenditureAmount().getValue();
	}

	/**
	 *<pre>
	 * 飲食日用品(0049、親階層)の支出金額テーブル情報(SISYUTU_KINGAKU、合計値)を取得します。
	 *</pre>
	 */
	private BigDecimal getFoodParentSisyutuKingaku() {
		return sisyutuKingakuTableRepository.findByPrimaryKey(
				SearchQueryUserIdAndYearMonthAndExpenditureItemCode.from(TEST_USER_ID, TARGET_YEAR_MONTH, ExpenditureItemCode.from("0049")))
				.getExpenditureAmount().getValue();
	}

	/**
	 *<pre>
	 * 収支テーブル(202511)の支出金額(EXPENDITURE_KINGAKU)を取得します。
	 *</pre>
	 */
	private BigDecimal getIncomeAndExpenditureKingaku() {
		IncomeAndExpenditure data = incomeAndExpenditureRepository.findByPrimaryKey(
				SearchQueryUserIdAndYearMonth.from(TEST_USER_ID, TARGET_YEAR_MONTH));
		return data.getExpenditureAmount().getValue();
	}

	/**
	 *<pre>
	 * テスト①：正常系：新規登録_単一カテゴリ(食料品(必須))、クーポンなし
	 *
	 * 【検証内容】
	 * ・買い物登録コードが自動採番(countBy+1の3桁ゼロ埋め)で"002"になること
	 * ・飲食(無駄遣いなし)の支出テーブル情報が10,000円→11,000円に更新されること
	 * ・飲食(0051)・飲食日用品(0049、親階層)の支出金額テーブル情報が1,000円分増加すること
	 * ・収支テーブルの支出金額が38,000円→39,000円に更新されること
	 * ・完了メッセージ・トランザクション完了フラグが設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("① 新規登録_単一カテゴリ(食料品(必須))、クーポンなし")
	void testExecAction_Add_SingleCategory() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setTargetYearMonth("202511");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(java.time.LocalDate.of(2025, 11, 20));
		form.setShoppingFoodExpenses(1000);
		form.setTotalPurchasePrice(1000);
		form.setShoppingTotalAmount(1000);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull(), "transactionSuccessFull=true");
		assertTrue(response.hasMessages());
		assertTrue(response.getMessagesList().stream().anyMatch(msg -> msg.contains("新規登録しました") && msg.contains("002")));

		// 買い物登録情報テーブル
		ShoppingRegist added = shoppingRegistRepository.findByPrimaryKey(
				SearchQueryUserIdAndYearMonthAndShoppingRegistCode.from(TEST_USER_ID, TARGET_YEAR_MONTH,
						com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingRegistCode.from("002")));
		assertNotNull(added, "買い物登録コード002が新規登録されていること");
		assertEquals(0, new BigDecimal("1000.00").compareTo(added.getShoppingFoodExpenditureItem().getShoppingFoodExpenditureAmount().getValue()));

		// 支出テーブル・支出金額テーブル・収支テーブル
		assertEquals(0, new BigDecimal("11000.00").compareTo(getFoodExpenditureKingaku()), "飲食(無駄遣いなし)が10,000円→11,000円に更新されること");
		assertEquals(0, new BigDecimal("14000.00").compareTo(getFoodSisyutuKingaku()), "飲食(0051)の支出金額合計が13,000円→14,000円に更新されること");
		assertEquals(0, new BigDecimal("22000.00").compareTo(getFoodParentSisyutuKingaku()), "飲食日用品(0049)の支出金額合計が21,000円→22,000円に更新されること");
		assertEquals(0, new BigDecimal("39000.00").compareTo(getIncomeAndExpenditureKingaku()), "収支テーブルの支出金額が38,000円→39,000円に更新されること");
	}

	/**
	 *<pre>
	 * テスト②：正常系：新規登録_クーポン金額が複数カテゴリに跨って充当される
	 *
	 * 【検証内容】
	 * ・食料品(必須)1,000円にクーポン1,200円を充当すると、食料品(必須)は0円(充当しきれない200円は残クーポンへ)
	 * ・残クーポン200円が食料品B(無駄遣い)500円に充当され、実質充当額は300円となること
	 * ・食料品(必須)は充当後0円のため支出テーブルは更新されないこと(hasExpenditureAmount()==falseでスキップ)
	 * ・食料品B(無駄遣い)の支出テーブル情報が2,000円→2,300円に更新されること
	 * ・収支テーブルの支出金額が38,000円→38,300円に更新されること(買い物合計金額=1,500円-1,200円=300円)
	 *</pre>
	 */
	@Test
	@DisplayName("② 新規登録_クーポン金額が複数カテゴリに跨って充当される(食料品(必須)超過分が食料品Bへ繰越)")
	void testExecAction_Add_CouponAcrossCategories() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setTargetYearMonth("202511");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(java.time.LocalDate.of(2025, 11, 21));
		form.setShoppingFoodExpenses(1000);
		form.setShoppingFoodBExpenses(500);
		form.setShoppingCouponPrice(1200);
		form.setTotalPurchasePrice(1500);
		form.setShoppingTotalAmount(300);

		BigDecimal beforeFood = getFoodExpenditureKingaku();

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull(), "transactionSuccessFull=true");

		// 食料品(必須)はクーポンで充当しきれてゼロになるため、支出テーブルは更新されないこと
		assertEquals(0, beforeFood.compareTo(getFoodExpenditureKingaku()), "飲食(無駄遣いなし)は更新されないこと(充当後0円のため)");
		// 食料品B(無駄遣い)には残クーポン200円を差し引いた300円のみが充当されること
		assertEquals(0, new BigDecimal("2300.00").compareTo(getFoodBExpenditureKingaku()), "飲食(無駄遣いB)が2,000円→2,300円に更新されること(500円-残クーポン200円)");
		// 収支テーブルの支出金額は買い物合計金額(300円)分のみ増加すること
		assertEquals(0, new BigDecimal("38300.00").compareTo(getIncomeAndExpenditureKingaku()), "収支テーブルの支出金額が38,000円→38,300円に更新されること");
	}

	/**
	 *<pre>
	 * テスト③：正常系：更新_登録済み買い物情報の金額を増額し、差額のみが反映される
	 *
	 * 【検証内容】
	 * ・買い物登録コード001(食料品(必須)2,000円)を3,000円に更新すること
	 * ・差額1,000円のみが飲食(無駄遣いなし)の支出テーブル情報に反映されること(10,000円→11,000円)
	 * ・収支テーブルの支出金額が差額分(1,000円)増加すること(38,000円→39,000円)
	 *</pre>
	 */
	@Test
	@DisplayName("③ 更新_登録済み買い物情報の金額を増額し、差額のみが反映される")
	void testExecAction_Update_IncreaseAmount() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		form.setTargetYearMonth("202511");
		form.setShoppingRegistCode("001");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(java.time.LocalDate.of(2025, 11, 5));
		form.setShoppingRemarks("更新対象");
		form.setShoppingFoodExpenses(3000);
		form.setTotalPurchasePrice(3000);
		form.setShoppingTotalAmount(3000);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull(), "transactionSuccessFull=true");
		assertTrue(response.getMessagesList().stream().anyMatch(msg -> msg.contains("更新しました") && msg.contains("001")));

		// 買い物登録情報テーブルが更新されていること
		ShoppingRegist updated = shoppingRegistRepository.findByPrimaryKey(
				SearchQueryUserIdAndYearMonthAndShoppingRegistCode.from(TEST_USER_ID, TARGET_YEAR_MONTH,
						com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingRegistCode.from("001")));
		assertEquals(0, new BigDecimal("3000.00").compareTo(updated.getShoppingFoodExpenditureItem().getShoppingFoodExpenditureAmount().getValue()));

		// 差額(1,000円)のみが支出テーブル・収支テーブルへ反映されること
		assertEquals(0, new BigDecimal("11000.00").compareTo(getFoodExpenditureKingaku()), "飲食(無駄遣いなし)が10,000円→11,000円に更新されること(差額分のみ)");
		assertEquals(0, new BigDecimal("39000.00").compareTo(getIncomeAndExpenditureKingaku()), "収支テーブルの支出金額が38,000円→39,000円に更新されること(差額分のみ)");
	}

	/**
	 *<pre>
	 * テスト④：異常系：未定義のアクションが指定された場合に例外が発生する
	 *
	 * 【検証内容】
	 * ・action="invalid"を指定した場合、MyHouseholdAccountBookRuntimeExceptionが発生すること
	 *</pre>
	 */
	@Test
	@DisplayName("④ 異常系：未定義のアクションが指定された場合に例外が発生する")
	void testExecAction_UndefinedAction() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction("invalid");
		form.setTargetYearMonth("202511");
		form.setTotalPurchasePrice(1000);
		form.setShoppingTotalAmount(1000);

		MyHouseholdAccountBookRuntimeException exception = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> useCase.execAction(TEST_USER, form));
		assertTrue(exception.getMessage().contains("未定義のアクション"));
	}

	/**
	 *<pre>
	 * テスト⑤：異常系：更新_存在しない買い物登録コードで例外が発生する
	 *
	 * 【検証内容】
	 * ・更新対象の買い物登録コード"999"が存在しない場合、MyHouseholdAccountBookRuntimeExceptionが発生すること
	 *</pre>
	 */
	@Test
	@DisplayName("⑤ 異常系：更新_存在しない買い物登録コードで例外が発生する")
	void testExecAction_Update_NotFound() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		form.setTargetYearMonth("202511");
		form.setShoppingRegistCode("999");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(java.time.LocalDate.of(2025, 11, 5));
		form.setShoppingFoodExpenses(1000);
		form.setTotalPurchasePrice(1000);
		form.setShoppingTotalAmount(1000);

		MyHouseholdAccountBookRuntimeException exception = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> useCase.execAction(TEST_USER, form));
		assertTrue(exception.getMessage().contains("更新対象の買い物登録情報が存在しません"));
	}
}
