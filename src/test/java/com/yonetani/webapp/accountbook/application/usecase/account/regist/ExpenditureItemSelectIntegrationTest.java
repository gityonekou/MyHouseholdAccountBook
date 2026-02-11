/**
 * 支出項目選択機能の結合テストクラスです。
 *
 * <pre>
 * IncomeAndExpenditureRegistUseCase の以下メソッドをテストします。
 *
 * [対象メソッド]
 * 1. readExpenditureAddSelect - 支出項目選択画面初期表示（支出項目一覧取得）
 * 2. readExpenditureItemActSelect - 支出項目選択後の詳細情報取得
 *
 * [テストシナリオ]
 * ① 正常系：支出項目選択画面初期表示_支出項目一覧確認（readExpenditureAddSelect）
 * ② 正常系：支出項目選択_非イベント系Level3項目（readExpenditureItemActSelect）
 * ③ 正常系：支出項目選択_イベント系支出項目（readExpenditureItemActSelect）
 * ④ 正常系：支出項目選択_Level2項目（readExpenditureItemActSelect）
 * ⑤ 正常系：支出項目選択_Level1項目（readExpenditureItemActSelect）
 *
 * [テストデータ]
 * - ユーザマスタ：user01
 * - 支出項目マスタ：0001～0061（60固定＋1イベント系）
 * - イベントテーブル：0001（イベント費(0058)に紐付くテストイベント）
 * </pre>
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.regist;

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
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.ExpenditureSelectItemForm;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.ExpenditureItemSelectResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

/**
 *<pre>
 * 支出項目選択機能の結合テストクラスです。
 *
 * IncomeAndExpenditureRegistUseCaseの支出項目選択関連メソッドについて、
 * UseCase→Component→Repository→DB の結合動作を検証します。
 *</pre>
 *
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
	"/sql/initsql/schema_test.sql",
	"/com/yonetani/webapp/accountbook/application/usecase/account/regist/ExpenditureItemSelectIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
class ExpenditureItemSelectIntegrationTest {

	@Autowired
	private IncomeAndExpenditureRegistUseCase useCase;

	/**
	 * テスト用のログインユーザ情報を作成します。
	 */
	private LoginUserInfo createLoginUser() {
		return LoginUserInfo.from("user01", "テストユーザ01");
	}

	// ===========================================
	// readExpenditureAddSelect テスト
	// ===========================================

	/**
	 *<pre>
	 * テスト①：正常系：支出項目選択画面初期表示_支出項目一覧確認
	 *
	 * 【検証内容】
	 * ・支出項目一覧が階層構造で取得される
	 * ・トップレベル（Level1）に6カテゴリが存在する
	 * ・メッセージなし（エラーなし）
	 * ・ビュー名が正しい
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支出項目選択画面初期表示_支出項目一覧確認")
	void testReadExpenditureAddSelect_NormalCase() {
		// Given: テストユーザ
		LoginUserInfo user = createLoginUser();

		// When: 支出項目選択画面初期表示
		ExpenditureItemSelectResponse response = useCase.readExpenditureAddSelect(user);

		// Then: レスポンスが正しく返却される
		assertNotNull(response);
		assertFalse(response.hasMessages(), "メッセージが設定されていないこと");

		// Then: ModelAndViewからビュー名と支出項目一覧を検証
		ModelAndView mav = response.build();
		assertEquals("account/regist/ExpenditureItemSelect", mav.getViewName());

		// 支出項目一覧（トップレベルはLevel1の6カテゴリ）
		@SuppressWarnings("unchecked")
		List<Object> expenditureItemList = (List<Object>) mav.getModel().get("expenditureItemList");
		assertNotNull(expenditureItemList, "支出項目一覧が設定されていること");
		// Level1: 事業経費(0001), 固定費(非課税)(0013), 固定費(課税)(0023), 衣類住居設備(0045), 飲食日用品(0049), 趣味娯楽(0055)
		assertEquals(6, expenditureItemList.size(), "トップレベル（Level1）のカテゴリが6件であること");
	}

	// ===========================================
	// readExpenditureItemActSelect テスト
	// ===========================================

	/**
	 *<pre>
	 * テスト②：正常系：支出項目選択_非イベント系Level3項目
	 *
	 * 【検証内容】
	 * ・電気代(0037, Level3)を選択
	 * ・支出項目名が＞区切りの3階層表示になる
	 * ・支出項目詳細内容が設定される
	 * ・フォームのsisyutuItemCodeが正しい
	 * ・イベント関連の設定がない
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支出項目選択_非イベント系Level3項目_電気代(0037)")
	void testReadExpenditureItemActSelect_Level3_NonEvent() {
		// Given: テストユーザ、支出項目コード=0037（電気代, Level3, 親=0036水光熱通費, 祖先=0023固定費(課税)）
		LoginUserInfo user = createLoginUser();
		String sisyutuItemCode = "0037";

		// When: 支出項目選択
		ExpenditureItemSelectResponse response = useCase.readExpenditureItemActSelect(user, sisyutuItemCode);

		// Then: レスポンスが正しく返却される
		assertNotNull(response);

		// Then: ModelAndViewから各フィールドを検証
		ModelAndView mav = response.build();
		assertEquals("account/regist/ExpenditureItemSelect", mav.getViewName());

		// 支出項目名：＞区切りの3階層表示
		assertEquals("固定費(課税)＞水光熱通費＞電気代", mav.getModel().get("sisyutuItemName"));
		// 支出項目詳細内容
		assertEquals("電気代詳細を入力", mav.getModel().get("sisyutuItemDetailContext"));

		// フォームデータ
		ExpenditureSelectItemForm form = (ExpenditureSelectItemForm) mav.getModel().get("expenditureSelectItemForm");
		assertNotNull(form);
		assertEquals("0037", form.getSisyutuItemCode());
		assertFalse(form.isEventCodeRequired(), "非イベント系項目のためeventCodeRequired=false");

		// イベント選択ボックスなし
		assertNull(mav.getModel().get("eventSelectList"), "非イベント系項目のためeventSelectList=null");

		// 支出項目一覧も取得される
		assertNotNull(mav.getModel().get("expenditureItemList"));
	}

	/**
	 *<pre>
	 * テスト③：正常系：支出項目選択_イベント系支出項目
	 *
	 * 【検証内容】
	 * ・イベント費(0058, Level2)を選択
	 * ・支出項目名が＞区切りの2階層表示になる
	 * ・eventCodeRequired=trueが設定される
	 * ・eventCodeに先頭イベントのコードが設定される
	 * ・eventSelectListにイベント選択肢が存在する
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支出項目選択_イベント系支出項目_イベント費(0058)")
	void testReadExpenditureItemActSelect_EventItem() {
		// Given: テストユーザ、支出項目コード=0058（イベント費, Level2, 親=0055趣味娯楽）
		// EVENT_ITEM_TABLEにイベントコード0001が紐付く
		LoginUserInfo user = createLoginUser();
		String sisyutuItemCode = "0058";

		// When: 支出項目選択
		ExpenditureItemSelectResponse response = useCase.readExpenditureItemActSelect(user, sisyutuItemCode);

		// Then: ModelAndViewから各フィールドを検証
		ModelAndView mav = response.build();

		// 支出項目名：＞区切りの2階層表示
		assertEquals("趣味娯楽＞イベント費", mav.getModel().get("sisyutuItemName"));
		// 支出項目詳細内容
		assertEquals("イベント費詳細を入力", mav.getModel().get("sisyutuItemDetailContext"));

		// フォームデータ
		ExpenditureSelectItemForm form = (ExpenditureSelectItemForm) mav.getModel().get("expenditureSelectItemForm");
		assertNotNull(form);
		assertEquals("0058", form.getSisyutuItemCode());
		assertTrue(form.isEventCodeRequired(), "イベント系項目のためeventCodeRequired=true");
		assertEquals("0001", form.getEventCode(), "先頭イベントのコードが設定される");

		// イベント選択ボックスあり
		SelectViewItem eventSelectList = (SelectViewItem) mav.getModel().get("eventSelectList");
		assertNotNull(eventSelectList, "イベント系項目のためeventSelectListが存在する");
	}

	/**
	 *<pre>
	 * テスト④：正常系：支出項目選択_Level2項目
	 *
	 * 【検証内容】
	 * ・水光熱通費(0036, Level2)を選択
	 * ・支出項目名が＞区切りの2階層表示になる
	 * ・イベント関連の設定がない
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支出項目選択_Level2項目_水光熱通費(0036)")
	void testReadExpenditureItemActSelect_Level2() {
		// Given: テストユーザ、支出項目コード=0036（水光熱通費, Level2, 親=0023固定費(課税)）
		LoginUserInfo user = createLoginUser();
		String sisyutuItemCode = "0036";

		// When: 支出項目選択
		ExpenditureItemSelectResponse response = useCase.readExpenditureItemActSelect(user, sisyutuItemCode);

		// Then: ModelAndViewから各フィールドを検証
		ModelAndView mav = response.build();

		// 支出項目名：＞区切りの2階層表示
		assertEquals("固定費(課税)＞水光熱通費", mav.getModel().get("sisyutuItemName"));
		// 支出項目詳細内容
		assertEquals("水光熱通費詳細を入力", mav.getModel().get("sisyutuItemDetailContext"));

		// フォームデータ
		ExpenditureSelectItemForm form = (ExpenditureSelectItemForm) mav.getModel().get("expenditureSelectItemForm");
		assertNotNull(form);
		assertEquals("0036", form.getSisyutuItemCode());
		assertFalse(form.isEventCodeRequired(), "非イベント系項目のためeventCodeRequired=false");

		// イベント選択ボックスなし
		assertNull(mav.getModel().get("eventSelectList"));
	}

	/**
	 *<pre>
	 * テスト⑤：正常系：支出項目選択_Level1項目
	 *
	 * 【検証内容】
	 * ・事業経費(0001, Level1)を選択
	 * ・支出項目名に＞区切りがない（単一名称のみ）
	 * ・イベント関連の設定がない
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支出項目選択_Level1項目_事業経費(0001)")
	void testReadExpenditureItemActSelect_Level1() {
		// Given: テストユーザ、支出項目コード=0001（事業経費, Level1）
		LoginUserInfo user = createLoginUser();
		String sisyutuItemCode = "0001";

		// When: 支出項目選択
		ExpenditureItemSelectResponse response = useCase.readExpenditureItemActSelect(user, sisyutuItemCode);

		// Then: ModelAndViewから各フィールドを検証
		ModelAndView mav = response.build();

		// 支出項目名：Level1なので＞区切りなし
		assertEquals("事業経費", mav.getModel().get("sisyutuItemName"));
		// 支出項目詳細内容
		assertEquals("事業経費詳細を入力", mav.getModel().get("sisyutuItemDetailContext"));

		// フォームデータ
		ExpenditureSelectItemForm form = (ExpenditureSelectItemForm) mav.getModel().get("expenditureSelectItemForm");
		assertNotNull(form);
		assertEquals("0001", form.getSisyutuItemCode());
		assertFalse(form.isEventCodeRequired());

		// イベント選択ボックスなし
		assertNull(mav.getModel().get("eventSelectList"));
	}
}
