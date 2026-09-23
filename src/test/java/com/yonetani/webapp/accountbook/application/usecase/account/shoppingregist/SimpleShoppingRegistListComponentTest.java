/**
 * 簡易タイプ買い物リスト取得コンポーネント(SimpleShoppingRegistListComponent)の統合テストクラスです。
 *
 * <pre>
 * SimpleShoppingRegistListComponent の以下メソッドをテストします。
 *
 * [対象メソッド]
 * 1. setSimpleShoppingRegistList(search, response) - 簡易タイプ買い物リスト情報の取得・画面情報への設定
 *
 * [テストシナリオ]
 * ① 正常系：登録済み買い物情報が2件の場合、明細の各項目(店舗名・支払方法名・買い物日・各カテゴリ金額・クーポン・合計金額)が
 *   正しく設定されること。支払方法が無効化されていても一覧では実際の名称で解決されること
 * ② 正常系：カテゴリ別の合計値(8項目＋クーポン＋月度合計)が明細の合計として正しく計算されること
 * ③ 正常系：全明細で未登録のカテゴリ(食料品C)の合計は空文字(null)のままであること
 * ④ 正常系：対象月の買い物情報が0件の場合、0件メッセージが設定され明細が空のままであること
 *
 * [テストデータ]
 * ・PAYMENT_METHOD_TABLE: 001現金(有効)、003無効化済み口座振替(無効)
 * ・買い物合計金額 = 購入金額合計 - クーポン金額 の関係で登録している
 * ・SHOPPING_REGIST_TABLE(202511): 001(支払方法=001、食料品(必須)1000円/食料品B500円/外食2000円/購入金額合計3500円/クーポン100円/買い物合計金額3400円)、
 *                                  002(支払方法=003・無効化済み、日用品800円/衣料品1200円/仕事300円/住居設備100円/購入金額合計2400円/クーポン50円/買い物合計金額2350円)
 * </pre>
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/09/23 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.shoppingregist;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.AbstractSimpleShoppingRegistListResponse.SimpleShoppingRegistListItem;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.SimpleShoppingRegistResponse;

/**
 *<pre>
 * 簡易タイプ買い物リスト取得コンポーネント(SimpleShoppingRegistListComponent)の統合テストクラスです。
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
	"/com/yonetani/webapp/accountbook/application/usecase/account/shoppingregist/SimpleShoppingRegistListComponentTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("簡易タイプ買い物リスト取得コンポーネント(SimpleShoppingRegistListComponent)のテスト（統合テスト）")
class SimpleShoppingRegistListComponentTest {

	@Autowired
	private SimpleShoppingRegistListComponent component;

	private final UserId TEST_USER_ID = UserId.from("user01");

	/**
	 *<pre>
	 * テスト①：正常系：登録済み買い物情報が2件の場合、明細の各項目が正しく設定される
	 *
	 * 【検証内容】
	 * ・明細が2件であること
	 * ・1件目：店舗名・買い物日・食料品(必須)・食料品B(無駄遣い)・外食・クーポン・買い物合計金額が正しいこと
	 * ・2件目：支払方法が無効化済み(003)でも、実際の支払方法名「無効化済み口座振替」で解決されること
	 *</pre>
	 */
	@Test
	@DisplayName("① 登録済み買い物情報が2件の場合、明細の各項目が正しく設定される")
	void testSetSimpleShoppingRegistList_ItemDetail() {
		SimpleShoppingRegistResponse response = SimpleShoppingRegistResponse.getRedirectInstance("202511");
		SearchQueryUserIdAndYearMonth search = SearchQueryUserIdAndYearMonth.from(TEST_USER_ID, TargetYearMonth.from("202511"));

		component.setSimpleShoppingRegistList(search, response);

		List<SimpleShoppingRegistListItem> list = response.getSimpleShoppingRegistListItemInfo();
		assertEquals(2, list.size(), "明細が2件であること");

		SimpleShoppingRegistListItem item001 = list.stream()
				.filter(i -> "001".equals(i.getShoppingRegistCode())).findFirst().orElseThrow();
		assertEquals("202511", item001.getTargetYearMonth());
		assertEquals("11/05", item001.getShoppingDay(), "買い物日がMM/dd形式であること");
		assertEquals("テストスーパー", item001.getShopName());
		assertEquals("現金", item001.getPaymentMethodName());
		assertEquals("1,000円", item001.getShoppingFood());
		assertEquals("500円", item001.getShoppingFoodB());
		assertEquals("2,000円", item001.getShoppingDineOut());
		assertEquals("100円", item001.getShoppingCoupon());
		assertEquals("3,400円", item001.getShoppingTotalAmount(), "買い物合計金額=購入金額合計3,500円-クーポン100円");

		SimpleShoppingRegistListItem item002 = list.stream()
				.filter(i -> "002".equals(i.getShoppingRegistCode())).findFirst().orElseThrow();
		assertEquals("無効化済み口座振替", item002.getPaymentMethodName(),
				"無効化された支払方法でも一覧では実際の支払方法名で表示されること");
		assertEquals("800円", item002.getShoppingConsumerGoods());
		assertEquals("1,200円", item002.getShoppingClothes());
		assertEquals("300円", item002.getShoppingWork());
		assertEquals("100円", item002.getShoppingHouseEquipment());
	}

	/**
	 *<pre>
	 * テスト②：正常系：カテゴリ別の合計値(8項目＋クーポン＋月度合計)が明細の合計として正しく計算される
	 *
	 * 【検証内容】
	 * ・食料品(必須)合計=1,000円(001のみ)
	 * ・食料品B(無駄遣い)合計=500円(001のみ)
	 * ・外食合計=2,000円(001のみ)
	 * ・日用品合計=800円(002のみ)
	 * ・衣料品(私服)合計=1,200円(002のみ)
	 * ・仕事合計=300円(002のみ)
	 * ・住居設備合計=100円(002のみ)
	 * ・クーポン金額合計=150円(100円+50円)
	 * ・月度買い物合計金額=5,750円(3,400円+2,350円)
	 *</pre>
	 */
	@Test
	@DisplayName("② カテゴリ別の合計値(8項目＋クーポン＋月度合計)が正しく計算される")
	void testSetSimpleShoppingRegistList_CategoryTotal() {
		SimpleShoppingRegistResponse response = SimpleShoppingRegistResponse.getRedirectInstance("202511");
		SearchQueryUserIdAndYearMonth search = SearchQueryUserIdAndYearMonth.from(TEST_USER_ID, TargetYearMonth.from("202511"));

		component.setSimpleShoppingRegistList(search, response);

		assertEquals("1,000円", response.getTotalShoppingFood(), "食料品(必須)合計");
		assertEquals("500円", response.getTotalShoppingFoodB(), "食料品B(無駄遣い)合計");
		assertEquals("2,000円", response.getTotalShoppingDineOut(), "外食合計");
		assertEquals("800円", response.getTotalShoppingConsumerGoods(), "日用品合計");
		assertEquals("1,200円", response.getTotalShoppingClothes(), "衣料品(私服)合計");
		assertEquals("300円", response.getTotalShoppingWork(), "仕事合計");
		assertEquals("100円", response.getTotalShoppingHouseEquipment(), "住居設備合計");
		assertEquals("150円", response.getTotalShoppingCouponPrice(), "クーポン金額合計(100円+50円)");
		assertEquals("5,750円", response.getShoppingMonthTotalAmount(), "月度買い物合計金額(3,400円+2,350円)");
	}

	/**
	 *<pre>
	 * テスト③：正常系：全明細で未登録のカテゴリ(食料品C)の合計は空文字のままであること
	 *
	 * 【検証内容】
	 * ・001・002ともに食料品C(お酒類)は未登録のため、合計が空文字であること
	 *</pre>
	 */
	@Test
	@DisplayName("③ 全明細で未登録のカテゴリ(食料品C)の合計は空文字のままであること")
	void testSetSimpleShoppingRegistList_UnregisteredCategoryTotalIsEmpty() {
		SimpleShoppingRegistResponse response = SimpleShoppingRegistResponse.getRedirectInstance("202511");
		SearchQueryUserIdAndYearMonth search = SearchQueryUserIdAndYearMonth.from(TEST_USER_ID, TargetYearMonth.from("202511"));

		component.setSimpleShoppingRegistList(search, response);

		assertEquals("", response.getTotalShoppingFoodC(), "食料品C(お酒類)合計は未登録のため空文字であること");

		List<SimpleShoppingRegistListItem> list = response.getSimpleShoppingRegistListItemInfo();
		assertTrue(list.stream().allMatch(item -> "".equals(item.getShoppingFoodC())),
				"各明細の食料品C(お酒類)も未登録のため空文字であること");
	}

	/**
	 *<pre>
	 * テスト④：正常系：対象月の買い物情報が0件の場合、0件メッセージが設定され明細が空のままであること
	 *
	 * 【検証内容】
	 * ・登録なしの対象月(202510)を指定した場合、明細が0件であること
	 * ・0件メッセージが設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("④ 対象月の買い物情報が0件の場合、0件メッセージが設定され明細が空のままであること")
	void testSetSimpleShoppingRegistList_ZeroCase() {
		SimpleShoppingRegistResponse response = SimpleShoppingRegistResponse.getRedirectInstance("202510");
		SearchQueryUserIdAndYearMonth search = SearchQueryUserIdAndYearMonth.from(TEST_USER_ID, TargetYearMonth.from("202510"));

		component.setSimpleShoppingRegistList(search, response);

		assertTrue(response.getSimpleShoppingRegistListItemInfo().isEmpty(), "明細が0件であること");
		assertTrue(response.hasMessages(), "0件メッセージが設定されること");
		assertTrue(response.getMessagesList().stream().anyMatch(msg -> msg.contains("登録済みの買い物情報は0件です")));
	}
}
