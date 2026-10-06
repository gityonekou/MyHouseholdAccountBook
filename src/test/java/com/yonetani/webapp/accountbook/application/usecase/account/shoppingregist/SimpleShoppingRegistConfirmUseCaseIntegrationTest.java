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
 * 各カテゴリについて、以下4パターンを確認する。
 * ・新規登録_単一カテゴリ、クーポンなし
 * ・新規登録_クーポンが次カテゴリ(クーポン充当順:食料品(必須)→食料品B→食料品C→外食→日用品→衣料品→仕事→住居設備)へ跨って充当される
 * ・更新_増額(差額のみが反映される)
 * ・更新_減額(差額のみが反映される)
 * (住居設備はクーポン充当順の最後のため、次カテゴリへの繰越ではなく、充当しきれない残クーポンがそのまま捨てられるケースを確認する)
 *
 * [食料品(必須)]
 * ① 正常系：新規登録_単一カテゴリ、クーポンなし
 * ② 正常系：新規登録_クーポンが食料品Bへ跨って充当される
 * ③ 正常系：更新_増額
 * ④ 正常系：更新_減額
 *
 * [食料品B(無駄遣い)]
 * ⑤ 正常系：新規登録_単一カテゴリ、クーポンなし
 * ⑥ 正常系：新規登録_クーポンが食料品Cへ跨って充当される
 * ⑦ 正常系：更新_増額
 * ⑧ 正常系：更新_減額
 *
 * [食料品C(お酒類)]
 * ⑨ 正常系：新規登録_単一カテゴリ、クーポンなし
 * ⑩ 正常系：新規登録_クーポンが外食へ跨って充当される
 * ⑪ 正常系：更新_増額
 * ⑫ 正常系：更新_減額
 *
 * [外食]
 * ⑬ 正常系：新規登録_単一カテゴリ、クーポンなし
 * ⑭ 正常系：新規登録_クーポンが日用品へ跨って充当される
 * ⑮ 正常系：更新_増額
 * ⑯ 正常系：更新_減額
 *
 * [日用品]
 * ⑰ 正常系：新規登録_単一カテゴリ、クーポンなし
 * ⑱ 正常系：新規登録_クーポンが衣料品(私服)へ跨って充当される
 * ⑲ 正常系：更新_増額
 * ⑳ 正常系：更新_減額
 *
 * [衣料品(私服)]
 * ㉑ 正常系：新規登録_単一カテゴリ、クーポンなし
 * ㉒ 正常系：新規登録_クーポンが仕事へ跨って充当される
 * ㉓ 正常系：更新_増額
 * ㉔ 正常系：更新_減額
 *
 * [仕事]
 * ㉕ 正常系：新規登録_単一カテゴリ、クーポンなし
 * ㉖ 正常系：新規登録_クーポンが住居設備へ跨って充当される
 * ㉗ 正常系：更新_増額
 * ㉘ 正常系：更新_減額
 *
 * [住居設備]
 * ㉙ 正常系：新規登録_単一カテゴリ、クーポンなし
 * ㉚ 異常系：新規登録_クーポン充当順の最後のため、充当しきれない残クーポンが残ると例外が発生する
 * ㉛ 正常系：更新_増額
 * ㉜ 正常系：更新_減額
 *
 * [全カテゴリ(クーポンで全額相殺)]
 * ㉝ 正常系：新規登録_クーポンが全8カテゴリの合計額とちょうど一致し、残クーポンなく全額相殺される。
 *   全カテゴリが充当後0円のため、支出テーブル・支出金額テーブル・収支テーブルがプラマイゼロで更新されないこと
 *
 * [異常系]
 * ㉞ 異常系：未定義のアクションが指定された場合に例外が発生すること
 * ㉟ 異常系：更新_存在しない買い物登録コードで例外が発生すること
 *
 * [テストデータ]
 * ・EXPENDITURE_TABLE(202511)：飲食(無駄遣いなし)10,000円/飲食(無駄遣いB)2,000円/飲食(無駄遣いC)1,000円/
 *   外食5,000円/日用消耗品3,000円/被服費5,000円/流動経費10,000円/住居設備2,000円(合計38,000円)
 * ・SISYUTU_KINGAKU_TABLE(202511)：0051(食費、無駄遣いなし/B/C集約)=13,000円/親0049=21,000円、
 *   0052(外食)=5,000円(親0049)、0050(日用消耗品)=3,000円(親0049)、
 *   0046(被服費)=5,000円/親0045=7,000円、0047(住居設備)=2,000円(親0045)、
 *   0007(流動経費)=10,000円/親0001=10,000円
 *   (0049は食費・外食・日用消耗品の共通親、0045は被服費・住居設備の共通親)
 * ・SHOPPING_REGIST_TABLE：001(8カテゴリ全てに初期値設定。合計10,000円、クーポンなし。更新系テストの対象データ)
 *   食料品(必須)2,000円/食料品B 1,000円/食料品C 500円/外食1,500円/日用品800円/衣料品1,200円/仕事2,000円/住居設備1,000円
 *   新規登録系テストでは次コード="002"の起点となる
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
import java.time.LocalDate;

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
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingRegistCode;
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

	// 支出項目コード(飲食：食料品(必須)/食料品B/食料品Cの3カテゴリが同一項目コードに集約される)
	private static final String ITEM_FOOD = "0051";
	private static final String ITEM_DINE_OUT = "0052";
	private static final String ITEM_CONSUMER_GOODS = "0050";
	private static final String ITEM_CLOTHES = "0046";
	private static final String ITEM_WORK = "0007";
	private static final String ITEM_HOUSE_EQUIPMENT = "0047";
	// 親支出項目コード(0049：食料品(必須)/食料品B/食料品C/外食/日用品の共通親、0045：衣料品(私服)/住居設備の共通親、0001：仕事の親)
	private static final String PARENT_FOOD_GROUP = "0049";
	private static final String PARENT_CLOTHES_GROUP = "0045";
	private static final String PARENT_WORK_GROUP = "0001";

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
	 * 期待値(BigDecimal)と実際の値をスケールを無視して比較します。
	 *</pre>
	 */
	private void assertAmount(String label, String expected, BigDecimal actual) {
		assertEquals(0, new BigDecimal(expected).compareTo(actual), label);
	}

	/**
	 *<pre>
	 * 買い物登録情報テーブル:SHOPPING_REGIST_TABLEから指定の買い物登録コードのデータを取得します。
	 *</pre>
	 */
	private ShoppingRegist findShoppingRegist(String shoppingRegistCode) {
		return shoppingRegistRepository.findByPrimaryKey(
				SearchQueryUserIdAndYearMonthAndShoppingRegistCode.from(TEST_USER_ID, TARGET_YEAR_MONTH, ShoppingRegistCode.from(shoppingRegistCode)));
	}

	/**
	 *<pre>
	 * 支出テーブル情報(EXPENDITURE_KINGAKU)を支出項目コードのみで取得します(1項目コードに1行のみのカテゴリ用)。
	 *</pre>
	 */
	private BigDecimal getExpenditureKingaku(String itemCode) {
		return expenditureRepository.findByExpenditureItemCode(
				SearchQueryUserIdAndYearMonthAndExpenditureItemCode.from(TEST_USER_ID, TARGET_YEAR_MONTH, ExpenditureItemCode.from(itemCode)))
				.getValues().get(0).getExpenditureAmount().getValue();
	}

	/**
	 *<pre>
	 * 支出テーブル情報(EXPENDITURE_KINGAKU)を支出項目コード・支出区分で取得します(飲食の3カテゴリ用)。
	 *</pre>
	 */
	private BigDecimal getExpenditureKingaku(String itemCode, ExpenditureCategory category) {
		return expenditureRepository.findByExpenditureItemCodeAndCategory(
				SearchQueryUserIdAndYearMonthAndExpenditureItemCodeAndExpenditureCategory.from(TEST_USER_ID, TARGET_YEAR_MONTH, ExpenditureItemCode.from(itemCode), category))
				.getValues().get(0).getExpenditureAmount().getValue();
	}

	/**
	 *<pre>
	 * 支出金額テーブル情報(SISYUTU_KINGAKU、合計値)を取得します。
	 *</pre>
	 */
	private BigDecimal getSisyutuKingaku(String itemCode) {
		return sisyutuKingakuTableRepository.findByPrimaryKey(
				SearchQueryUserIdAndYearMonthAndExpenditureItemCode.from(TEST_USER_ID, TARGET_YEAR_MONTH, ExpenditureItemCode.from(itemCode)))
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
	 * 更新系テストの土台となる入力フォームを生成します。
	 * 買い物登録コード001(8カテゴリ全てに初期値設定済み)の値をそのまま引き継いだ状態で返すため、
	 * 呼び出し元は変更したいカテゴリの項目と購入金額合計・買い物合計金額のみを上書きしてください。
	 *</pre>
	 */
	private SimpleShoppingRegistInfoForm createUpdateBaseForm() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		form.setTargetYearMonth("202511");
		form.setShoppingRegistCode("001");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(LocalDate.of(2025, 11, 5));
		form.setShoppingRemarks("更新対象");
		form.setShoppingFoodExpenses(2000);
		form.setShoppingFoodBExpenses(1000);
		form.setShoppingFoodCExpenses(500);
		form.setShoppingDineOutExpenses(1500);
		form.setShoppingConsumerGoodsExpenses(800);
		form.setShoppingClothesExpenses(1200);
		form.setShoppingWorkExpenses(2000);
		form.setShoppingHouseEquipmentExpenses(1000);
		return form;
	}

	// ========================================================================
	// [食料品(必須)]
	// ========================================================================

	/**
	 *<pre>
	 * テスト①：正常系：新規登録_単一カテゴリ(食料品(必須))、クーポンなし
	 *
	 * 【検証内容】
	 * ・買い物登録コードが自動採番(countBy+1の3桁ゼロ埋め)で"002"になること
	 * ・買い物登録情報テーブルに食料品(必須)1,000円が登録されること
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
		form.setShoppingDate(LocalDate.of(2025, 11, 20));
		form.setShoppingFoodExpenses(1000);
		form.setTotalPurchasePrice(1000);
		form.setShoppingTotalAmount(1000);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull(), "transactionSuccessFull=true");
		assertTrue(response.hasMessages());
		assertTrue(response.getMessagesList().stream().anyMatch(msg -> msg.contains("新規登録しました") && msg.contains("002")));

		ShoppingRegist added = findShoppingRegist("002");
		assertNotNull(added, "買い物登録コード002が新規登録されていること");
		assertAmount("食料品(必須)が1,000円で登録されること", "1000.00", added.getShoppingFoodExpenditureItem().getShoppingFoodExpenditureAmount().getValue());

		assertAmount("飲食(無駄遣いなし)が10,000円→11,000円に更新されること", "11000.00", getExpenditureKingaku(ITEM_FOOD, ExpenditureCategory.NON_WASTED));
		assertAmount("飲食(0051)の支出金額合計が13,000円→14,000円に更新されること", "14000.00", getSisyutuKingaku(ITEM_FOOD));
		assertAmount("飲食日用品(0049)の支出金額合計が21,000円→22,000円に更新されること", "22000.00", getSisyutuKingaku(PARENT_FOOD_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→39,000円に更新されること", "39000.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト②：正常系：新規登録_クーポンが食料品Bへ跨って充当される
	 *
	 * 【検証内容】
	 * ・食料品(必須)1,000円にクーポン1,200円を充当すると、食料品(必須)は0円(充当しきれない200円は残クーポンへ)
	 * ・残クーポン200円が食料品B(無駄遣い)500円に充当され、実質充当額は300円となること
	 * ・買い物登録情報テーブルに食料品(必須)1,000円・食料品B 500円が登録されること
	 * ・食料品(必須)は充当後0円のため支出テーブルは更新されないこと(hasExpenditureAmount()==falseでスキップ)
	 * ・食料品B(無駄遣い)の支出テーブル情報が2,000円→2,300円に更新されること
	 * ・飲食(0051)・飲食日用品(0049、親階層)の支出金額テーブル情報が食料品Bの実質充当額(300円)分増加すること
	 * ・収支テーブルの支出金額が38,000円→38,300円に更新されること(買い物合計金額=1,500円-1,200円=300円)
	 *</pre>
	 */
	@Test
	@DisplayName("② 新規登録_クーポンが食料品Bへ跨って充当される")
	void testExecAction_Add_CouponAcrossCategories() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setTargetYearMonth("202511");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(LocalDate.of(2025, 11, 21));
		form.setShoppingFoodExpenses(1000);
		form.setShoppingFoodBExpenses(500);
		form.setShoppingCouponPrice(1200);
		form.setTotalPurchasePrice(1500);
		form.setShoppingTotalAmount(300);

		BigDecimal beforeFood = getExpenditureKingaku(ITEM_FOOD, ExpenditureCategory.NON_WASTED);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull(), "transactionSuccessFull=true");

		ShoppingRegist added = findShoppingRegist("002");
		assertAmount("食料品(必須)が1,000円で登録されること", "1000.00", added.getShoppingFoodExpenditureItem().getShoppingFoodExpenditureAmount().getValue());
		assertAmount("食料品Bが500円で登録されること", "500.00", added.getShoppingFoodMinorWasteExpenditureItem().getShoppingFoodMinorWasteExpenses().getValue());

		// 食料品(必須)はクーポンで充当しきれてゼロになるため、支出テーブルは更新されないこと
		assertEquals(0, beforeFood.compareTo(getExpenditureKingaku(ITEM_FOOD, ExpenditureCategory.NON_WASTED)), "飲食(無駄遣いなし)は更新されないこと(充当後0円のため)");
		// 食料品B(無駄遣い)には残クーポン200円を差し引いた300円のみが充当されること
		assertAmount("飲食(無駄遣いB)が2,000円→2,300円に更新されること(500円-残クーポン200円)", "2300.00", getExpenditureKingaku(ITEM_FOOD, ExpenditureCategory.WASTED_B));
		assertAmount("飲食(0051)の支出金額合計が13,000円→13,300円に更新されること", "13300.00", getSisyutuKingaku(ITEM_FOOD));
		assertAmount("飲食日用品(0049)の支出金額合計が21,000円→21,300円に更新されること", "21300.00", getSisyutuKingaku(PARENT_FOOD_GROUP));
		// 収支テーブルの支出金額は買い物合計金額(300円)分のみ増加すること
		assertAmount("収支テーブルの支出金額が38,000円→38,300円に更新されること", "38300.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト③：正常系：更新_増額(食料品(必須))
	 *
	 * 【検証内容】
	 * ・買い物登録コード001(食料品(必須)2,000円)を3,000円に更新すること
	 * ・差額1,000円のみが飲食(無駄遣いなし)の支出テーブル情報に反映されること(10,000円→11,000円)
	 * ・飲食(0051)・飲食日用品(0049、親階層)の支出金額テーブル情報が差額(1,000円)分増加すること
	 * ・収支テーブルの支出金額が差額分(1,000円)増加すること(38,000円→39,000円)
	 *</pre>
	 */
	@Test
	@DisplayName("③ 更新_増額(食料品(必須))")
	void testExecAction_Update_IncreaseAmount() {
		SimpleShoppingRegistInfoForm form = createUpdateBaseForm();
		form.setShoppingFoodExpenses(3000);
		form.setTotalPurchasePrice(11000);
		form.setShoppingTotalAmount(11000);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull(), "transactionSuccessFull=true");
		assertTrue(response.getMessagesList().stream().anyMatch(msg -> msg.contains("更新しました") && msg.contains("001")));

		ShoppingRegist updated = findShoppingRegist("001");
		assertAmount("食料品(必須)が3,000円に更新されること", "3000.00", updated.getShoppingFoodExpenditureItem().getShoppingFoodExpenditureAmount().getValue());

		// 差額(1,000円)のみが支出テーブル・支出金額テーブル・収支テーブルへ反映されること
		assertAmount("飲食(無駄遣いなし)が10,000円→11,000円に更新されること(差額分のみ)", "11000.00", getExpenditureKingaku(ITEM_FOOD, ExpenditureCategory.NON_WASTED));
		assertAmount("飲食(0051)の支出金額合計が13,000円→14,000円に更新されること", "14000.00", getSisyutuKingaku(ITEM_FOOD));
		assertAmount("飲食日用品(0049)の支出金額合計が21,000円→22,000円に更新されること", "22000.00", getSisyutuKingaku(PARENT_FOOD_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→39,000円に更新されること(差額分のみ)", "39000.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト④：正常系：更新_減額(食料品(必須))
	 *
	 * 【検証内容】
	 * ・買い物登録コード001(食料品(必須)2,000円)を1,200円に更新すること
	 * ・差額800円のみが飲食(無駄遣いなし)の支出テーブル情報に反映されること(10,000円→9,200円)
	 * ・飲食(0051)・飲食日用品(0049、親階層)の支出金額テーブル情報が差額(800円)分減少すること
	 * ・収支テーブルの支出金額が差額分(800円)減少すること(38,000円→37,200円)
	 *</pre>
	 */
	@Test
	@DisplayName("④ 更新_減額(食料品(必須))")
	void testExecAction_Update_DecreaseAmount() {
		SimpleShoppingRegistInfoForm form = createUpdateBaseForm();
		form.setShoppingFoodExpenses(1200);
		form.setTotalPurchasePrice(9200);
		form.setShoppingTotalAmount(9200);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull(), "transactionSuccessFull=true");
		assertTrue(response.getMessagesList().stream().anyMatch(msg -> msg.contains("更新しました") && msg.contains("001")));

		ShoppingRegist updated = findShoppingRegist("001");
		assertAmount("食料品(必須)が1,200円に更新されること", "1200.00", updated.getShoppingFoodExpenditureItem().getShoppingFoodExpenditureAmount().getValue());

		assertAmount("飲食(無駄遣いなし)が10,000円→9,200円に更新されること(差額分のみ)", "9200.00", getExpenditureKingaku(ITEM_FOOD, ExpenditureCategory.NON_WASTED));
		assertAmount("飲食(0051)の支出金額合計が13,000円→12,200円に更新されること", "12200.00", getSisyutuKingaku(ITEM_FOOD));
		assertAmount("飲食日用品(0049)の支出金額合計が21,000円→20,200円に更新されること", "20200.00", getSisyutuKingaku(PARENT_FOOD_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→37,200円に更新されること(差額分のみ)", "37200.00", getIncomeAndExpenditureKingaku());
	}

	// ========================================================================
	// [食料品B(無駄遣い)]
	// ========================================================================

	/**
	 *<pre>
	 * テスト⑤：正常系：新規登録_単一カテゴリ(食料品B)、クーポンなし
	 *
	 * 【検証内容】食料品(必須)のテスト①と同型。飲食(無駄遣いB)の支出テーブル情報が2,000円→3,000円に更新されること
	 *</pre>
	 */
	@Test
	@DisplayName("⑤ 新規登録_単一カテゴリ(食料品B)、クーポンなし")
	void testExecAction_Add_SingleCategory_FoodB() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setTargetYearMonth("202511");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(LocalDate.of(2025, 11, 20));
		form.setShoppingFoodBExpenses(1000);
		form.setTotalPurchasePrice(1000);
		form.setShoppingTotalAmount(1000);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());
		assertTrue(response.getMessagesList().stream().anyMatch(msg -> msg.contains("新規登録しました") && msg.contains("002")));

		ShoppingRegist added = findShoppingRegist("002");
		assertAmount("食料品Bが1,000円で登録されること", "1000.00", added.getShoppingFoodMinorWasteExpenditureItem().getShoppingFoodMinorWasteExpenses().getValue());

		assertAmount("飲食(無駄遣いB)が2,000円→3,000円に更新されること", "3000.00", getExpenditureKingaku(ITEM_FOOD, ExpenditureCategory.WASTED_B));
		assertAmount("飲食(0051)の支出金額合計が13,000円→14,000円に更新されること", "14000.00", getSisyutuKingaku(ITEM_FOOD));
		assertAmount("飲食日用品(0049)の支出金額合計が21,000円→22,000円に更新されること", "22000.00", getSisyutuKingaku(PARENT_FOOD_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→39,000円に更新されること", "39000.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト⑥：正常系：新規登録_クーポンが食料品Cへ跨って充当される
	 *
	 * 【検証内容】食料品(必須)のテスト②と同型。食料品Bが充当後0円のため飲食(無駄遣いB)は更新されず、
	 * 食料品Cに実質300円が充当され、飲食(無駄遣いC)が1,000円→1,300円に更新されること
	 *</pre>
	 */
	@Test
	@DisplayName("⑥ 新規登録_クーポンが食料品Cへ跨って充当される")
	void testExecAction_Add_CouponAcrossCategories_FoodBToFoodC() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setTargetYearMonth("202511");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(LocalDate.of(2025, 11, 21));
		form.setShoppingFoodBExpenses(1000);
		form.setShoppingFoodCExpenses(500);
		form.setShoppingCouponPrice(1200);
		form.setTotalPurchasePrice(1500);
		form.setShoppingTotalAmount(300);

		BigDecimal beforeFoodB = getExpenditureKingaku(ITEM_FOOD, ExpenditureCategory.WASTED_B);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());

		ShoppingRegist added = findShoppingRegist("002");
		assertAmount("食料品Bが1,000円で登録されること", "1000.00", added.getShoppingFoodMinorWasteExpenditureItem().getShoppingFoodMinorWasteExpenses().getValue());
		assertAmount("食料品Cが500円で登録されること", "500.00", added.getShoppingFoodSevereWasteExpenditureItem().getShoppingFoodSevereWasteExpenses().getValue());

		assertEquals(0, beforeFoodB.compareTo(getExpenditureKingaku(ITEM_FOOD, ExpenditureCategory.WASTED_B)), "飲食(無駄遣いB)は更新されないこと(充当後0円のため)");
		assertAmount("飲食(無駄遣いC)が1,000円→1,300円に更新されること(500円-残クーポン200円)", "1300.00", getExpenditureKingaku(ITEM_FOOD, ExpenditureCategory.WASTED_C));
		assertAmount("飲食(0051)の支出金額合計が13,000円→13,300円に更新されること", "13300.00", getSisyutuKingaku(ITEM_FOOD));
		assertAmount("飲食日用品(0049)の支出金額合計が21,000円→21,300円に更新されること", "21300.00", getSisyutuKingaku(PARENT_FOOD_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→38,300円に更新されること", "38300.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト⑦：正常系：更新_増額(食料品B)
	 *
	 * 【検証内容】食料品(必須)のテスト③と同型。食料品Bを1,000円→1,500円に更新すること
	 *</pre>
	 */
	@Test
	@DisplayName("⑦ 更新_増額(食料品B)")
	void testExecAction_Update_IncreaseAmount_FoodB() {
		SimpleShoppingRegistInfoForm form = createUpdateBaseForm();
		form.setShoppingFoodBExpenses(1500);
		form.setTotalPurchasePrice(10500);
		form.setShoppingTotalAmount(10500);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());

		ShoppingRegist updated = findShoppingRegist("001");
		assertAmount("食料品Bが1,500円に更新されること", "1500.00", updated.getShoppingFoodMinorWasteExpenditureItem().getShoppingFoodMinorWasteExpenses().getValue());

		assertAmount("飲食(無駄遣いB)が2,000円→2,500円に更新されること(差額分のみ)", "2500.00", getExpenditureKingaku(ITEM_FOOD, ExpenditureCategory.WASTED_B));
		assertAmount("飲食(0051)の支出金額合計が13,000円→13,500円に更新されること", "13500.00", getSisyutuKingaku(ITEM_FOOD));
		assertAmount("飲食日用品(0049)の支出金額合計が21,000円→21,500円に更新されること", "21500.00", getSisyutuKingaku(PARENT_FOOD_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→38,500円に更新されること", "38500.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト⑧：正常系：更新_減額(食料品B)
	 *
	 * 【検証内容】食料品(必須)のテスト④と同型。食料品Bを1,000円→700円に更新すること
	 *</pre>
	 */
	@Test
	@DisplayName("⑧ 更新_減額(食料品B)")
	void testExecAction_Update_DecreaseAmount_FoodB() {
		SimpleShoppingRegistInfoForm form = createUpdateBaseForm();
		form.setShoppingFoodBExpenses(700);
		form.setTotalPurchasePrice(9700);
		form.setShoppingTotalAmount(9700);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());

		ShoppingRegist updated = findShoppingRegist("001");
		assertAmount("食料品Bが700円に更新されること", "700.00", updated.getShoppingFoodMinorWasteExpenditureItem().getShoppingFoodMinorWasteExpenses().getValue());

		assertAmount("飲食(無駄遣いB)が2,000円→1,700円に更新されること(差額分のみ)", "1700.00", getExpenditureKingaku(ITEM_FOOD, ExpenditureCategory.WASTED_B));
		assertAmount("飲食(0051)の支出金額合計が13,000円→12,700円に更新されること", "12700.00", getSisyutuKingaku(ITEM_FOOD));
		assertAmount("飲食日用品(0049)の支出金額合計が21,000円→20,700円に更新されること", "20700.00", getSisyutuKingaku(PARENT_FOOD_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→37,700円に更新されること", "37700.00", getIncomeAndExpenditureKingaku());
	}

	// ========================================================================
	// [食料品C(お酒類)]
	// ========================================================================

	/**
	 *<pre>
	 * テスト⑨：正常系：新規登録_単一カテゴリ(食料品C)、クーポンなし
	 *</pre>
	 */
	@Test
	@DisplayName("⑨ 新規登録_単一カテゴリ(食料品C)、クーポンなし")
	void testExecAction_Add_SingleCategory_FoodC() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setTargetYearMonth("202511");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(LocalDate.of(2025, 11, 20));
		form.setShoppingFoodCExpenses(1000);
		form.setTotalPurchasePrice(1000);
		form.setShoppingTotalAmount(1000);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());
		assertTrue(response.getMessagesList().stream().anyMatch(msg -> msg.contains("新規登録しました") && msg.contains("002")));

		ShoppingRegist added = findShoppingRegist("002");
		assertAmount("食料品Cが1,000円で登録されること", "1000.00", added.getShoppingFoodSevereWasteExpenditureItem().getShoppingFoodSevereWasteExpenses().getValue());

		assertAmount("飲食(無駄遣いC)が1,000円→2,000円に更新されること", "2000.00", getExpenditureKingaku(ITEM_FOOD, ExpenditureCategory.WASTED_C));
		assertAmount("飲食(0051)の支出金額合計が13,000円→14,000円に更新されること", "14000.00", getSisyutuKingaku(ITEM_FOOD));
		assertAmount("飲食日用品(0049)の支出金額合計が21,000円→22,000円に更新されること", "22000.00", getSisyutuKingaku(PARENT_FOOD_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→39,000円に更新されること", "39000.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト⑩：正常系：新規登録_クーポンが外食へ跨って充当される
	 *
	 * 【検証内容】食料品Cが充当後0円のため飲食(無駄遣いC)は更新されず、外食に実質300円が充当され、
	 * 一人プチ贅沢・外食が5,000円→5,300円に更新されること
	 *</pre>
	 */
	@Test
	@DisplayName("⑩ 新規登録_クーポンが外食へ跨って充当される")
	void testExecAction_Add_CouponAcrossCategories_FoodCToDineOut() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setTargetYearMonth("202511");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(LocalDate.of(2025, 11, 21));
		form.setShoppingFoodCExpenses(1000);
		form.setShoppingDineOutExpenses(500);
		form.setShoppingCouponPrice(1200);
		form.setTotalPurchasePrice(1500);
		form.setShoppingTotalAmount(300);

		BigDecimal beforeFoodC = getExpenditureKingaku(ITEM_FOOD, ExpenditureCategory.WASTED_C);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());

		ShoppingRegist added = findShoppingRegist("002");
		assertAmount("食料品Cが1,000円で登録されること", "1000.00", added.getShoppingFoodSevereWasteExpenditureItem().getShoppingFoodSevereWasteExpenses().getValue());
		assertAmount("外食が500円で登録されること", "500.00", added.getShoppingDineOutExpenditureItem().getShoppingDineOutExpenditureAmount().getValue());

		assertEquals(0, beforeFoodC.compareTo(getExpenditureKingaku(ITEM_FOOD, ExpenditureCategory.WASTED_C)), "飲食(無駄遣いC)は更新されないこと(充当後0円のため)");
		assertAmount("一人プチ贅沢・外食が5,000円→5,300円に更新されること(500円-残クーポン200円)", "5300.00", getExpenditureKingaku(ITEM_DINE_OUT));
		assertAmount("外食(0052)の支出金額合計が5,000円→5,300円に更新されること", "5300.00", getSisyutuKingaku(ITEM_DINE_OUT));
		assertAmount("飲食日用品(0049)の支出金額合計が21,000円→21,300円に更新されること", "21300.00", getSisyutuKingaku(PARENT_FOOD_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→38,300円に更新されること", "38300.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト⑪：正常系：更新_増額(食料品C)
	 *</pre>
	 */
	@Test
	@DisplayName("⑪ 更新_増額(食料品C)")
	void testExecAction_Update_IncreaseAmount_FoodC() {
		SimpleShoppingRegistInfoForm form = createUpdateBaseForm();
		form.setShoppingFoodCExpenses(800);
		form.setTotalPurchasePrice(10300);
		form.setShoppingTotalAmount(10300);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());

		ShoppingRegist updated = findShoppingRegist("001");
		assertAmount("食料品Cが800円に更新されること", "800.00", updated.getShoppingFoodSevereWasteExpenditureItem().getShoppingFoodSevereWasteExpenses().getValue());

		assertAmount("飲食(無駄遣いC)が1,000円→1,300円に更新されること(差額分のみ)", "1300.00", getExpenditureKingaku(ITEM_FOOD, ExpenditureCategory.WASTED_C));
		assertAmount("飲食(0051)の支出金額合計が13,000円→13,300円に更新されること", "13300.00", getSisyutuKingaku(ITEM_FOOD));
		assertAmount("飲食日用品(0049)の支出金額合計が21,000円→21,300円に更新されること", "21300.00", getSisyutuKingaku(PARENT_FOOD_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→38,300円に更新されること", "38300.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト⑫：正常系：更新_減額(食料品C)
	 *</pre>
	 */
	@Test
	@DisplayName("⑫ 更新_減額(食料品C)")
	void testExecAction_Update_DecreaseAmount_FoodC() {
		SimpleShoppingRegistInfoForm form = createUpdateBaseForm();
		form.setShoppingFoodCExpenses(200);
		form.setTotalPurchasePrice(9700);
		form.setShoppingTotalAmount(9700);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());

		ShoppingRegist updated = findShoppingRegist("001");
		assertAmount("食料品Cが200円に更新されること", "200.00", updated.getShoppingFoodSevereWasteExpenditureItem().getShoppingFoodSevereWasteExpenses().getValue());

		assertAmount("飲食(無駄遣いC)が1,000円→700円に更新されること(差額分のみ)", "700.00", getExpenditureKingaku(ITEM_FOOD, ExpenditureCategory.WASTED_C));
		assertAmount("飲食(0051)の支出金額合計が13,000円→12,700円に更新されること", "12700.00", getSisyutuKingaku(ITEM_FOOD));
		assertAmount("飲食日用品(0049)の支出金額合計が21,000円→20,700円に更新されること", "20700.00", getSisyutuKingaku(PARENT_FOOD_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→37,700円に更新されること", "37700.00", getIncomeAndExpenditureKingaku());
	}

	// ========================================================================
	// [外食]
	// ========================================================================

	/**
	 *<pre>
	 * テスト⑬：正常系：新規登録_単一カテゴリ(外食)、クーポンなし
	 *</pre>
	 */
	@Test
	@DisplayName("⑬ 新規登録_単一カテゴリ(外食)、クーポンなし")
	void testExecAction_Add_SingleCategory_DineOut() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setTargetYearMonth("202511");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(LocalDate.of(2025, 11, 20));
		form.setShoppingDineOutExpenses(1000);
		form.setTotalPurchasePrice(1000);
		form.setShoppingTotalAmount(1000);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());
		assertTrue(response.getMessagesList().stream().anyMatch(msg -> msg.contains("新規登録しました") && msg.contains("002")));

		ShoppingRegist added = findShoppingRegist("002");
		assertAmount("外食が1,000円で登録されること", "1000.00", added.getShoppingDineOutExpenditureItem().getShoppingDineOutExpenditureAmount().getValue());

		assertAmount("一人プチ贅沢・外食が5,000円→6,000円に更新されること", "6000.00", getExpenditureKingaku(ITEM_DINE_OUT));
		assertAmount("外食(0052)の支出金額合計が5,000円→6,000円に更新されること", "6000.00", getSisyutuKingaku(ITEM_DINE_OUT));
		assertAmount("飲食日用品(0049)の支出金額合計が21,000円→22,000円に更新されること", "22000.00", getSisyutuKingaku(PARENT_FOOD_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→39,000円に更新されること", "39000.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト⑭：正常系：新規登録_クーポンが日用品へ跨って充当される
	 *
	 * 【検証内容】外食が充当後0円のため一人プチ贅沢・外食は更新されず、日用品に実質300円が充当され、
	 * 日用消耗品が3,000円→3,300円に更新されること
	 *</pre>
	 */
	@Test
	@DisplayName("⑭ 新規登録_クーポンが日用品へ跨って充当される")
	void testExecAction_Add_CouponAcrossCategories_DineOutToConsumerGoods() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setTargetYearMonth("202511");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(LocalDate.of(2025, 11, 21));
		form.setShoppingDineOutExpenses(1000);
		form.setShoppingConsumerGoodsExpenses(500);
		form.setShoppingCouponPrice(1200);
		form.setTotalPurchasePrice(1500);
		form.setShoppingTotalAmount(300);

		BigDecimal beforeDineOut = getExpenditureKingaku(ITEM_DINE_OUT);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());

		ShoppingRegist added = findShoppingRegist("002");
		assertAmount("外食が1,000円で登録されること", "1000.00", added.getShoppingDineOutExpenditureItem().getShoppingDineOutExpenditureAmount().getValue());
		assertAmount("日用品が500円で登録されること", "500.00", added.getShoppingConsumerGoodsExpenditureItem().getShoppingConsumerGoodsExpenditureAmount().getValue());

		assertEquals(0, beforeDineOut.compareTo(getExpenditureKingaku(ITEM_DINE_OUT)), "一人プチ贅沢・外食は更新されないこと(充当後0円のため)");
		assertAmount("日用消耗品が3,000円→3,300円に更新されること(500円-残クーポン200円)", "3300.00", getExpenditureKingaku(ITEM_CONSUMER_GOODS));
		assertAmount("日用消耗品(0050)の支出金額合計が3,000円→3,300円に更新されること", "3300.00", getSisyutuKingaku(ITEM_CONSUMER_GOODS));
		assertAmount("飲食日用品(0049)の支出金額合計が21,000円→21,300円に更新されること", "21300.00", getSisyutuKingaku(PARENT_FOOD_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→38,300円に更新されること", "38300.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト⑮：正常系：更新_増額(外食)
	 *</pre>
	 */
	@Test
	@DisplayName("⑮ 更新_増額(外食)")
	void testExecAction_Update_IncreaseAmount_DineOut() {
		SimpleShoppingRegistInfoForm form = createUpdateBaseForm();
		form.setShoppingDineOutExpenses(2000);
		form.setTotalPurchasePrice(10500);
		form.setShoppingTotalAmount(10500);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());

		ShoppingRegist updated = findShoppingRegist("001");
		assertAmount("外食が2,000円に更新されること", "2000.00", updated.getShoppingDineOutExpenditureItem().getShoppingDineOutExpenditureAmount().getValue());

		assertAmount("一人プチ贅沢・外食が5,000円→5,500円に更新されること(差額分のみ)", "5500.00", getExpenditureKingaku(ITEM_DINE_OUT));
		assertAmount("外食(0052)の支出金額合計が5,000円→5,500円に更新されること", "5500.00", getSisyutuKingaku(ITEM_DINE_OUT));
		assertAmount("飲食日用品(0049)の支出金額合計が21,000円→21,500円に更新されること", "21500.00", getSisyutuKingaku(PARENT_FOOD_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→38,500円に更新されること", "38500.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト⑯：正常系：更新_減額(外食)
	 *</pre>
	 */
	@Test
	@DisplayName("⑯ 更新_減額(外食)")
	void testExecAction_Update_DecreaseAmount_DineOut() {
		SimpleShoppingRegistInfoForm form = createUpdateBaseForm();
		form.setShoppingDineOutExpenses(1000);
		form.setTotalPurchasePrice(9500);
		form.setShoppingTotalAmount(9500);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());

		ShoppingRegist updated = findShoppingRegist("001");
		assertAmount("外食が1,000円に更新されること", "1000.00", updated.getShoppingDineOutExpenditureItem().getShoppingDineOutExpenditureAmount().getValue());

		assertAmount("一人プチ贅沢・外食が5,000円→4,500円に更新されること(差額分のみ)", "4500.00", getExpenditureKingaku(ITEM_DINE_OUT));
		assertAmount("外食(0052)の支出金額合計が5,000円→4,500円に更新されること", "4500.00", getSisyutuKingaku(ITEM_DINE_OUT));
		assertAmount("飲食日用品(0049)の支出金額合計が21,000円→20,500円に更新されること", "20500.00", getSisyutuKingaku(PARENT_FOOD_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→37,500円に更新されること", "37500.00", getIncomeAndExpenditureKingaku());
	}

	// ========================================================================
	// [日用品]
	// ========================================================================

	/**
	 *<pre>
	 * テスト⑰：正常系：新規登録_単一カテゴリ(日用品)、クーポンなし
	 *</pre>
	 */
	@Test
	@DisplayName("⑰ 新規登録_単一カテゴリ(日用品)、クーポンなし")
	void testExecAction_Add_SingleCategory_ConsumerGoods() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setTargetYearMonth("202511");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(LocalDate.of(2025, 11, 20));
		form.setShoppingConsumerGoodsExpenses(1000);
		form.setTotalPurchasePrice(1000);
		form.setShoppingTotalAmount(1000);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());
		assertTrue(response.getMessagesList().stream().anyMatch(msg -> msg.contains("新規登録しました") && msg.contains("002")));

		ShoppingRegist added = findShoppingRegist("002");
		assertAmount("日用品が1,000円で登録されること", "1000.00", added.getShoppingConsumerGoodsExpenditureItem().getShoppingConsumerGoodsExpenditureAmount().getValue());

		assertAmount("日用消耗品が3,000円→4,000円に更新されること", "4000.00", getExpenditureKingaku(ITEM_CONSUMER_GOODS));
		assertAmount("日用消耗品(0050)の支出金額合計が3,000円→4,000円に更新されること", "4000.00", getSisyutuKingaku(ITEM_CONSUMER_GOODS));
		assertAmount("飲食日用品(0049)の支出金額合計が21,000円→22,000円に更新されること", "22000.00", getSisyutuKingaku(PARENT_FOOD_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→39,000円に更新されること", "39000.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト⑱：正常系：新規登録_クーポンが衣料品(私服)へ跨って充当される
	 *
	 * 【検証内容】日用品が充当後0円のため日用消耗品は更新されず、衣料品(私服)に実質300円が充当され、
	 * 被服費が5,000円→5,300円に更新されること
	 *</pre>
	 */
	@Test
	@DisplayName("⑱ 新規登録_クーポンが衣料品(私服)へ跨って充当される")
	void testExecAction_Add_CouponAcrossCategories_ConsumerGoodsToClothes() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setTargetYearMonth("202511");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(LocalDate.of(2025, 11, 21));
		form.setShoppingConsumerGoodsExpenses(1000);
		form.setShoppingClothesExpenses(500);
		form.setShoppingCouponPrice(1200);
		form.setTotalPurchasePrice(1500);
		form.setShoppingTotalAmount(300);

		BigDecimal beforeConsumerGoods = getExpenditureKingaku(ITEM_CONSUMER_GOODS);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());

		ShoppingRegist added = findShoppingRegist("002");
		assertAmount("日用品が1,000円で登録されること", "1000.00", added.getShoppingConsumerGoodsExpenditureItem().getShoppingConsumerGoodsExpenditureAmount().getValue());
		assertAmount("衣料品(私服)が500円で登録されること", "500.00", added.getShoppingClothesExpenditureItem().getShoppingClothesExpenditureAmount().getValue());

		assertEquals(0, beforeConsumerGoods.compareTo(getExpenditureKingaku(ITEM_CONSUMER_GOODS)), "日用消耗品は更新されないこと(充当後0円のため)");
		assertAmount("被服費が5,000円→5,300円に更新されること(500円-残クーポン200円)", "5300.00", getExpenditureKingaku(ITEM_CLOTHES));
		assertAmount("被服費(0046)の支出金額合計が5,000円→5,300円に更新されること", "5300.00", getSisyutuKingaku(ITEM_CLOTHES));
		assertAmount("衣類住居設備(0045)の支出金額合計が7,000円→7,300円に更新されること", "7300.00", getSisyutuKingaku(PARENT_CLOTHES_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→38,300円に更新されること", "38300.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト⑲：正常系：更新_増額(日用品)
	 *</pre>
	 */
	@Test
	@DisplayName("⑲ 更新_増額(日用品)")
	void testExecAction_Update_IncreaseAmount_ConsumerGoods() {
		SimpleShoppingRegistInfoForm form = createUpdateBaseForm();
		form.setShoppingConsumerGoodsExpenses(1200);
		form.setTotalPurchasePrice(10400);
		form.setShoppingTotalAmount(10400);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());

		ShoppingRegist updated = findShoppingRegist("001");
		assertAmount("日用品が1,200円に更新されること", "1200.00", updated.getShoppingConsumerGoodsExpenditureItem().getShoppingConsumerGoodsExpenditureAmount().getValue());

		assertAmount("日用消耗品が3,000円→3,400円に更新されること(差額分のみ)", "3400.00", getExpenditureKingaku(ITEM_CONSUMER_GOODS));
		assertAmount("日用消耗品(0050)の支出金額合計が3,000円→3,400円に更新されること", "3400.00", getSisyutuKingaku(ITEM_CONSUMER_GOODS));
		assertAmount("飲食日用品(0049)の支出金額合計が21,000円→21,400円に更新されること", "21400.00", getSisyutuKingaku(PARENT_FOOD_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→38,400円に更新されること", "38400.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト⑳：正常系：更新_減額(日用品)
	 *</pre>
	 */
	@Test
	@DisplayName("⑳ 更新_減額(日用品)")
	void testExecAction_Update_DecreaseAmount_ConsumerGoods() {
		SimpleShoppingRegistInfoForm form = createUpdateBaseForm();
		form.setShoppingConsumerGoodsExpenses(500);
		form.setTotalPurchasePrice(9700);
		form.setShoppingTotalAmount(9700);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());

		ShoppingRegist updated = findShoppingRegist("001");
		assertAmount("日用品が500円に更新されること", "500.00", updated.getShoppingConsumerGoodsExpenditureItem().getShoppingConsumerGoodsExpenditureAmount().getValue());

		assertAmount("日用消耗品が3,000円→2,700円に更新されること(差額分のみ)", "2700.00", getExpenditureKingaku(ITEM_CONSUMER_GOODS));
		assertAmount("日用消耗品(0050)の支出金額合計が3,000円→2,700円に更新されること", "2700.00", getSisyutuKingaku(ITEM_CONSUMER_GOODS));
		assertAmount("飲食日用品(0049)の支出金額合計が21,000円→20,700円に更新されること", "20700.00", getSisyutuKingaku(PARENT_FOOD_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→37,700円に更新されること", "37700.00", getIncomeAndExpenditureKingaku());
	}

	// ========================================================================
	// [衣料品(私服)]
	// ========================================================================

	/**
	 *<pre>
	 * テスト㉑：正常系：新規登録_単一カテゴリ(衣料品(私服))、クーポンなし
	 *</pre>
	 */
	@Test
	@DisplayName("㉑ 新規登録_単一カテゴリ(衣料品(私服))、クーポンなし")
	void testExecAction_Add_SingleCategory_Clothes() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setTargetYearMonth("202511");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(LocalDate.of(2025, 11, 20));
		form.setShoppingClothesExpenses(1000);
		form.setTotalPurchasePrice(1000);
		form.setShoppingTotalAmount(1000);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());
		assertTrue(response.getMessagesList().stream().anyMatch(msg -> msg.contains("新規登録しました") && msg.contains("002")));

		ShoppingRegist added = findShoppingRegist("002");
		assertAmount("衣料品(私服)が1,000円で登録されること", "1000.00", added.getShoppingClothesExpenditureItem().getShoppingClothesExpenditureAmount().getValue());

		assertAmount("被服費が5,000円→6,000円に更新されること", "6000.00", getExpenditureKingaku(ITEM_CLOTHES));
		assertAmount("被服費(0046)の支出金額合計が5,000円→6,000円に更新されること", "6000.00", getSisyutuKingaku(ITEM_CLOTHES));
		assertAmount("衣類住居設備(0045)の支出金額合計が7,000円→8,000円に更新されること", "8000.00", getSisyutuKingaku(PARENT_CLOTHES_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→39,000円に更新されること", "39000.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト㉒：正常系：新規登録_クーポンが仕事へ跨って充当される
	 *
	 * 【検証内容】衣料品(私服)が充当後0円のため被服費は更新されず、仕事に実質300円が充当され、
	 * 流動経費が10,000円→10,300円に更新されること
	 *</pre>
	 */
	@Test
	@DisplayName("㉒ 新規登録_クーポンが仕事へ跨って充当される")
	void testExecAction_Add_CouponAcrossCategories_ClothesToWork() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setTargetYearMonth("202511");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(LocalDate.of(2025, 11, 21));
		form.setShoppingClothesExpenses(1000);
		form.setShoppingWorkExpenses(500);
		form.setShoppingCouponPrice(1200);
		form.setTotalPurchasePrice(1500);
		form.setShoppingTotalAmount(300);

		BigDecimal beforeClothes = getExpenditureKingaku(ITEM_CLOTHES);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());

		ShoppingRegist added = findShoppingRegist("002");
		assertAmount("衣料品(私服)が1,000円で登録されること", "1000.00", added.getShoppingClothesExpenditureItem().getShoppingClothesExpenditureAmount().getValue());
		assertAmount("仕事が500円で登録されること", "500.00", added.getShoppingWorkExpenditureItem().getShoppingWorkExpenditureAmount().getValue());

		assertEquals(0, beforeClothes.compareTo(getExpenditureKingaku(ITEM_CLOTHES)), "被服費は更新されないこと(充当後0円のため)");
		assertAmount("流動経費が10,000円→10,300円に更新されること(500円-残クーポン200円)", "10300.00", getExpenditureKingaku(ITEM_WORK));
		assertAmount("流動経費(0007)の支出金額合計が10,000円→10,300円に更新されること", "10300.00", getSisyutuKingaku(ITEM_WORK));
		assertAmount("事業経費(0001)の支出金額合計が10,000円→10,300円に更新されること", "10300.00", getSisyutuKingaku(PARENT_WORK_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→38,300円に更新されること", "38300.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト㉓：正常系：更新_増額(衣料品(私服))
	 *</pre>
	 */
	@Test
	@DisplayName("㉓ 更新_増額(衣料品(私服))")
	void testExecAction_Update_IncreaseAmount_Clothes() {
		SimpleShoppingRegistInfoForm form = createUpdateBaseForm();
		form.setShoppingClothesExpenses(1700);
		form.setTotalPurchasePrice(10500);
		form.setShoppingTotalAmount(10500);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());

		ShoppingRegist updated = findShoppingRegist("001");
		assertAmount("衣料品(私服)が1,700円に更新されること", "1700.00", updated.getShoppingClothesExpenditureItem().getShoppingClothesExpenditureAmount().getValue());

		assertAmount("被服費が5,000円→5,500円に更新されること(差額分のみ)", "5500.00", getExpenditureKingaku(ITEM_CLOTHES));
		assertAmount("被服費(0046)の支出金額合計が5,000円→5,500円に更新されること", "5500.00", getSisyutuKingaku(ITEM_CLOTHES));
		assertAmount("衣類住居設備(0045)の支出金額合計が7,000円→7,500円に更新されること", "7500.00", getSisyutuKingaku(PARENT_CLOTHES_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→38,500円に更新されること", "38500.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト㉔：正常系：更新_減額(衣料品(私服))
	 *</pre>
	 */
	@Test
	@DisplayName("㉔ 更新_減額(衣料品(私服))")
	void testExecAction_Update_DecreaseAmount_Clothes() {
		SimpleShoppingRegistInfoForm form = createUpdateBaseForm();
		form.setShoppingClothesExpenses(700);
		form.setTotalPurchasePrice(9500);
		form.setShoppingTotalAmount(9500);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());

		ShoppingRegist updated = findShoppingRegist("001");
		assertAmount("衣料品(私服)が700円に更新されること", "700.00", updated.getShoppingClothesExpenditureItem().getShoppingClothesExpenditureAmount().getValue());

		assertAmount("被服費が5,000円→4,500円に更新されること(差額分のみ)", "4500.00", getExpenditureKingaku(ITEM_CLOTHES));
		assertAmount("被服費(0046)の支出金額合計が5,000円→4,500円に更新されること", "4500.00", getSisyutuKingaku(ITEM_CLOTHES));
		assertAmount("衣類住居設備(0045)の支出金額合計が7,000円→6,500円に更新されること", "6500.00", getSisyutuKingaku(PARENT_CLOTHES_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→37,500円に更新されること", "37500.00", getIncomeAndExpenditureKingaku());
	}

	// ========================================================================
	// [仕事]
	// ========================================================================

	/**
	 *<pre>
	 * テスト㉕：正常系：新規登録_単一カテゴリ(仕事)、クーポンなし
	 *</pre>
	 */
	@Test
	@DisplayName("㉕ 新規登録_単一カテゴリ(仕事)、クーポンなし")
	void testExecAction_Add_SingleCategory_Work() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setTargetYearMonth("202511");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(LocalDate.of(2025, 11, 20));
		form.setShoppingWorkExpenses(1000);
		form.setTotalPurchasePrice(1000);
		form.setShoppingTotalAmount(1000);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());
		assertTrue(response.getMessagesList().stream().anyMatch(msg -> msg.contains("新規登録しました") && msg.contains("002")));

		ShoppingRegist added = findShoppingRegist("002");
		assertAmount("仕事が1,000円で登録されること", "1000.00", added.getShoppingWorkExpenditureItem().getShoppingWorkExpenditureAmount().getValue());

		assertAmount("流動経費が10,000円→11,000円に更新されること", "11000.00", getExpenditureKingaku(ITEM_WORK));
		assertAmount("流動経費(0007)の支出金額合計が10,000円→11,000円に更新されること", "11000.00", getSisyutuKingaku(ITEM_WORK));
		assertAmount("事業経費(0001)の支出金額合計が10,000円→11,000円に更新されること", "11000.00", getSisyutuKingaku(PARENT_WORK_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→39,000円に更新されること", "39000.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト㉖：正常系：新規登録_クーポンが住居設備へ跨って充当される
	 *
	 * 【検証内容】仕事が充当後0円のため流動経費は更新されず、住居設備に実質300円が充当され、
	 * 住居設備が2,000円→2,300円に更新されること
	 *</pre>
	 */
	@Test
	@DisplayName("㉖ 新規登録_クーポンが住居設備へ跨って充当される")
	void testExecAction_Add_CouponAcrossCategories_WorkToHouseEquipment() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setTargetYearMonth("202511");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(LocalDate.of(2025, 11, 21));
		form.setShoppingWorkExpenses(1000);
		form.setShoppingHouseEquipmentExpenses(500);
		form.setShoppingCouponPrice(1200);
		form.setTotalPurchasePrice(1500);
		form.setShoppingTotalAmount(300);

		BigDecimal beforeWork = getExpenditureKingaku(ITEM_WORK);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());

		ShoppingRegist added = findShoppingRegist("002");
		assertAmount("仕事が1,000円で登録されること", "1000.00", added.getShoppingWorkExpenditureItem().getShoppingWorkExpenditureAmount().getValue());
		assertAmount("住居設備が500円で登録されること", "500.00", added.getShoppingHouseEquipmentExpenditureItem().getShoppingHouseEquipmentExpenditureAmount().getValue());

		assertEquals(0, beforeWork.compareTo(getExpenditureKingaku(ITEM_WORK)), "流動経費は更新されないこと(充当後0円のため)");
		assertAmount("住居設備が2,000円→2,300円に更新されること(500円-残クーポン200円)", "2300.00", getExpenditureKingaku(ITEM_HOUSE_EQUIPMENT));
		assertAmount("住居設備(0047)の支出金額合計が2,000円→2,300円に更新されること", "2300.00", getSisyutuKingaku(ITEM_HOUSE_EQUIPMENT));
		assertAmount("衣類住居設備(0045)の支出金額合計が7,000円→7,300円に更新されること", "7300.00", getSisyutuKingaku(PARENT_CLOTHES_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→38,300円に更新されること", "38300.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト㉗：正常系：更新_増額(仕事)
	 *</pre>
	 */
	@Test
	@DisplayName("㉗ 更新_増額(仕事)")
	void testExecAction_Update_IncreaseAmount_Work() {
		SimpleShoppingRegistInfoForm form = createUpdateBaseForm();
		form.setShoppingWorkExpenses(2500);
		form.setTotalPurchasePrice(10500);
		form.setShoppingTotalAmount(10500);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());

		ShoppingRegist updated = findShoppingRegist("001");
		assertAmount("仕事が2,500円に更新されること", "2500.00", updated.getShoppingWorkExpenditureItem().getShoppingWorkExpenditureAmount().getValue());

		assertAmount("流動経費が10,000円→10,500円に更新されること(差額分のみ)", "10500.00", getExpenditureKingaku(ITEM_WORK));
		assertAmount("流動経費(0007)の支出金額合計が10,000円→10,500円に更新されること", "10500.00", getSisyutuKingaku(ITEM_WORK));
		assertAmount("事業経費(0001)の支出金額合計が10,000円→10,500円に更新されること", "10500.00", getSisyutuKingaku(PARENT_WORK_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→38,500円に更新されること", "38500.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト㉘：正常系：更新_減額(仕事)
	 *</pre>
	 */
	@Test
	@DisplayName("㉘ 更新_減額(仕事)")
	void testExecAction_Update_DecreaseAmount_Work() {
		SimpleShoppingRegistInfoForm form = createUpdateBaseForm();
		form.setShoppingWorkExpenses(1500);
		form.setTotalPurchasePrice(9500);
		form.setShoppingTotalAmount(9500);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());

		ShoppingRegist updated = findShoppingRegist("001");
		assertAmount("仕事が1,500円に更新されること", "1500.00", updated.getShoppingWorkExpenditureItem().getShoppingWorkExpenditureAmount().getValue());

		assertAmount("流動経費が10,000円→9,500円に更新されること(差額分のみ)", "9500.00", getExpenditureKingaku(ITEM_WORK));
		assertAmount("流動経費(0007)の支出金額合計が10,000円→9,500円に更新されること", "9500.00", getSisyutuKingaku(ITEM_WORK));
		assertAmount("事業経費(0001)の支出金額合計が10,000円→9,500円に更新されること", "9500.00", getSisyutuKingaku(PARENT_WORK_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→37,500円に更新されること", "37500.00", getIncomeAndExpenditureKingaku());
	}

	// ========================================================================
	// [住居設備]
	// ========================================================================

	/**
	 *<pre>
	 * テスト㉙：正常系：新規登録_単一カテゴリ(住居設備)、クーポンなし
	 *</pre>
	 */
	@Test
	@DisplayName("㉙ 新規登録_単一カテゴリ(住居設備)、クーポンなし")
	void testExecAction_Add_SingleCategory_HouseEquipment() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setTargetYearMonth("202511");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(LocalDate.of(2025, 11, 20));
		form.setShoppingHouseEquipmentExpenses(1000);
		form.setTotalPurchasePrice(1000);
		form.setShoppingTotalAmount(1000);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());
		assertTrue(response.getMessagesList().stream().anyMatch(msg -> msg.contains("新規登録しました") && msg.contains("002")));

		ShoppingRegist added = findShoppingRegist("002");
		assertAmount("住居設備が1,000円で登録されること", "1000.00", added.getShoppingHouseEquipmentExpenditureItem().getShoppingHouseEquipmentExpenditureAmount().getValue());

		assertAmount("住居設備が2,000円→3,000円に更新されること", "3000.00", getExpenditureKingaku(ITEM_HOUSE_EQUIPMENT));
		assertAmount("住居設備(0047)の支出金額合計が2,000円→3,000円に更新されること", "3000.00", getSisyutuKingaku(ITEM_HOUSE_EQUIPMENT));
		assertAmount("衣類住居設備(0045)の支出金額合計が7,000円→8,000円に更新されること", "8000.00", getSisyutuKingaku(PARENT_CLOTHES_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→39,000円に更新されること", "39000.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト㉚：異常系：新規登録_クーポン充当順の最後のため、充当しきれない残クーポンが残ると例外が発生する
	 *
	 * 【検証内容】
	 * ・住居設備はクーポン充当順の最後のカテゴリであるため、繰り越す先がないこと
	 * ・住居設備1,000円にクーポン1,200円を充当すると200円が充当しきれず残クーポンとして残ること
	 * ・画面側のバリデーションチェック(js側処理)では本来発生しない状態のため、MyHouseholdAccountBookRuntimeExceptionが発生すること
	 *</pre>
	 */
	@Test
	@DisplayName("㉚ 異常系：新規登録_クーポン充当順の最後のため、充当しきれない残クーポンが残ると例外が発生する")
	void testExecAction_Add_Coupon_HouseEquipment_Terminal() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setTargetYearMonth("202511");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(LocalDate.of(2025, 11, 21));
		form.setShoppingHouseEquipmentExpenses(1000);
		form.setShoppingCouponPrice(1200);
		form.setTotalPurchasePrice(1000);
		form.setShoppingTotalAmount(0);

		MyHouseholdAccountBookRuntimeException exception = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> useCase.execAction(TEST_USER, form));
		assertTrue(exception.getMessage().contains("予期しない不整合"));
	}

	/**
	 *<pre>
	 * テスト㉛：正常系：更新_増額(住居設備)
	 *</pre>
	 */
	@Test
	@DisplayName("㉛ 更新_増額(住居設備)")
	void testExecAction_Update_IncreaseAmount_HouseEquipment() {
		SimpleShoppingRegistInfoForm form = createUpdateBaseForm();
		form.setShoppingHouseEquipmentExpenses(1400);
		form.setTotalPurchasePrice(10400);
		form.setShoppingTotalAmount(10400);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());

		ShoppingRegist updated = findShoppingRegist("001");
		assertAmount("住居設備が1,400円に更新されること", "1400.00", updated.getShoppingHouseEquipmentExpenditureItem().getShoppingHouseEquipmentExpenditureAmount().getValue());

		assertAmount("住居設備が2,000円→2,400円に更新されること(差額分のみ)", "2400.00", getExpenditureKingaku(ITEM_HOUSE_EQUIPMENT));
		assertAmount("住居設備(0047)の支出金額合計が2,000円→2,400円に更新されること", "2400.00", getSisyutuKingaku(ITEM_HOUSE_EQUIPMENT));
		assertAmount("衣類住居設備(0045)の支出金額合計が7,000円→7,400円に更新されること", "7400.00", getSisyutuKingaku(PARENT_CLOTHES_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→38,400円に更新されること", "38400.00", getIncomeAndExpenditureKingaku());
	}

	/**
	 *<pre>
	 * テスト㉜：正常系：更新_減額(住居設備)
	 *</pre>
	 */
	@Test
	@DisplayName("㉜ 更新_減額(住居設備)")
	void testExecAction_Update_DecreaseAmount_HouseEquipment() {
		SimpleShoppingRegistInfoForm form = createUpdateBaseForm();
		form.setShoppingHouseEquipmentExpenses(600);
		form.setTotalPurchasePrice(9600);
		form.setShoppingTotalAmount(9600);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull());

		ShoppingRegist updated = findShoppingRegist("001");
		assertAmount("住居設備が600円に更新されること", "600.00", updated.getShoppingHouseEquipmentExpenditureItem().getShoppingHouseEquipmentExpenditureAmount().getValue());

		assertAmount("住居設備が2,000円→1,600円に更新されること(差額分のみ)", "1600.00", getExpenditureKingaku(ITEM_HOUSE_EQUIPMENT));
		assertAmount("住居設備(0047)の支出金額合計が2,000円→1,600円に更新されること", "1600.00", getSisyutuKingaku(ITEM_HOUSE_EQUIPMENT));
		assertAmount("衣類住居設備(0045)の支出金額合計が7,000円→6,600円に更新されること", "6600.00", getSisyutuKingaku(PARENT_CLOTHES_GROUP));
		assertAmount("収支テーブルの支出金額が38,000円→37,600円に更新されること", "37600.00", getIncomeAndExpenditureKingaku());
	}

	// ========================================================================
	// [全カテゴリ(クーポンで全額相殺)]
	// ========================================================================

	/**
	 *<pre>
	 * テスト㉝：正常系：新規登録_クーポンが全8カテゴリの合計額とちょうど一致し、残クーポンなく全額相殺される
	 *
	 * 【検証内容】
	 * ・食料品(必須)1,000円/食料品B 500円/食料品C 300円/外食800円/日用品400円/衣料品600円/仕事700円/住居設備200円(合計4,500円)に
	 *   クーポン4,500円を充当すると、全カテゴリが0円となり残クーポンもちょうど0円になること(最後の住居設備で割引後の金額がちょうど0円)
	 * ・全カテゴリが充当後0円のため、どの支出テーブル情報も更新されないこと(8項目全て)
	 * ・どの支出金額テーブル情報(各項目・各親階層)も更新されないこと
	 * ・買い物合計金額が0円のため、収支テーブルの支出金額も更新されない(プラマイゼロ)こと
	 *</pre>
	 */
	@Test
	@DisplayName("㉝ 新規登録_クーポンが全8カテゴリの合計額とちょうど一致し、プラマイゼロで更新されない")
	void testExecAction_Add_Coupon_AllCategoriesOffsetToZero() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setTargetYearMonth("202511");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(LocalDate.of(2025, 11, 22));
		form.setShoppingFoodExpenses(1000);
		form.setShoppingFoodBExpenses(500);
		form.setShoppingFoodCExpenses(300);
		form.setShoppingDineOutExpenses(800);
		form.setShoppingConsumerGoodsExpenses(400);
		form.setShoppingClothesExpenses(600);
		form.setShoppingWorkExpenses(700);
		form.setShoppingHouseEquipmentExpenses(200);
		form.setShoppingCouponPrice(4500);
		form.setTotalPurchasePrice(4500);
		form.setShoppingTotalAmount(0);

		SimpleShoppingRegistResponse response = useCase.execAction(TEST_USER, form);

		assertTrue(response.isTransactionSuccessFull(), "transactionSuccessFull=true");
		assertTrue(response.getMessagesList().stream().anyMatch(msg -> msg.contains("新規登録しました") && msg.contains("002")));

		ShoppingRegist added = findShoppingRegist("002");
		assertAmount("食料品(必須)が1,000円で登録されること", "1000.00", added.getShoppingFoodExpenditureItem().getShoppingFoodExpenditureAmount().getValue());
		assertAmount("住居設備が200円で登録されること", "200.00", added.getShoppingHouseEquipmentExpenditureItem().getShoppingHouseEquipmentExpenditureAmount().getValue());

		// 支出テーブル：全8項目が更新されないこと(プラマイゼロ)
		assertAmount("飲食(無駄遣いなし)が10,000円のまま更新されないこと", "10000.00", getExpenditureKingaku(ITEM_FOOD, ExpenditureCategory.NON_WASTED));
		assertAmount("飲食(無駄遣いB)が2,000円のまま更新されないこと", "2000.00", getExpenditureKingaku(ITEM_FOOD, ExpenditureCategory.WASTED_B));
		assertAmount("飲食(無駄遣いC)が1,000円のまま更新されないこと", "1000.00", getExpenditureKingaku(ITEM_FOOD, ExpenditureCategory.WASTED_C));
		assertAmount("一人プチ贅沢・外食が5,000円のまま更新されないこと", "5000.00", getExpenditureKingaku(ITEM_DINE_OUT));
		assertAmount("日用消耗品が3,000円のまま更新されないこと", "3000.00", getExpenditureKingaku(ITEM_CONSUMER_GOODS));
		assertAmount("被服費が5,000円のまま更新されないこと", "5000.00", getExpenditureKingaku(ITEM_CLOTHES));
		assertAmount("流動経費が10,000円のまま更新されないこと", "10000.00", getExpenditureKingaku(ITEM_WORK));
		assertAmount("住居設備が2,000円のまま更新されないこと", "2000.00", getExpenditureKingaku(ITEM_HOUSE_EQUIPMENT));

		// 支出金額テーブル：対象項目・親階層が更新されないこと
		assertAmount("飲食(0051)の支出金額合計が13,000円のまま更新されないこと", "13000.00", getSisyutuKingaku(ITEM_FOOD));
		assertAmount("外食(0052)の支出金額合計が5,000円のまま更新されないこと", "5000.00", getSisyutuKingaku(ITEM_DINE_OUT));
		assertAmount("日用消耗品(0050)の支出金額合計が3,000円のまま更新されないこと", "3000.00", getSisyutuKingaku(ITEM_CONSUMER_GOODS));
		assertAmount("飲食日用品(0049)の支出金額合計が21,000円のまま更新されないこと", "21000.00", getSisyutuKingaku(PARENT_FOOD_GROUP));
		assertAmount("被服費(0046)の支出金額合計が5,000円のまま更新されないこと", "5000.00", getSisyutuKingaku(ITEM_CLOTHES));
		assertAmount("住居設備(0047)の支出金額合計が2,000円のまま更新されないこと", "2000.00", getSisyutuKingaku(ITEM_HOUSE_EQUIPMENT));
		assertAmount("衣類住居設備(0045)の支出金額合計が7,000円のまま更新されないこと", "7000.00", getSisyutuKingaku(PARENT_CLOTHES_GROUP));
		assertAmount("流動経費(0007)の支出金額合計が10,000円のまま更新されないこと", "10000.00", getSisyutuKingaku(ITEM_WORK));
		assertAmount("事業経費(0001)の支出金額合計が10,000円のまま更新されないこと", "10000.00", getSisyutuKingaku(PARENT_WORK_GROUP));

		// 収支テーブル：買い物合計金額0円のため更新されないこと(プラマイゼロ)
		assertAmount("収支テーブルの支出金額が38,000円のまま更新されないこと", "38000.00", getIncomeAndExpenditureKingaku());
	}

	// ========================================================================
	// [異常系]
	// ========================================================================

	/**
	 *<pre>
	 * テスト㉞：異常系：未定義のアクションが指定された場合に例外が発生する
	 *
	 * 【検証内容】
	 * ・action="invalid"を指定した場合、MyHouseholdAccountBookRuntimeExceptionが発生すること
	 *</pre>
	 */
	@Test
	@DisplayName("㉞ 異常系：未定義のアクションが指定された場合に例外が発生する")
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
	 * テスト㉟：異常系：更新_存在しない買い物登録コードで例外が発生する
	 *
	 * 【検証内容】
	 * ・更新対象の買い物登録コード"999"が存在しない場合、MyHouseholdAccountBookRuntimeExceptionが発生すること
	 *</pre>
	 */
	@Test
	@DisplayName("㉟ 異常系：更新_存在しない買い物登録コードで例外が発生する")
	void testExecAction_Update_NotFound() {
		SimpleShoppingRegistInfoForm form = new SimpleShoppingRegistInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		form.setTargetYearMonth("202511");
		form.setShoppingRegistCode("999");
		form.setShopKubunCode("901");
		form.setShopCode("001");
		form.setPaymentMethodCode("001");
		form.setShoppingDate(LocalDate.of(2025, 11, 5));
		form.setShoppingFoodExpenses(1000);
		form.setTotalPurchasePrice(1000);
		form.setShoppingTotalAmount(1000);

		MyHouseholdAccountBookRuntimeException exception = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> useCase.execAction(TEST_USER, form));
		assertTrue(exception.getMessage().contains("更新対象の買い物登録情報が存在しません"));
	}
}
