/**
 * 固定費情報管理ユースケース（参照系）の統合テストクラスです。
 *
 * <pre>
 * FixedCostInfoManageUseCase の以下メソッドをテストします。
 *
 * [対象メソッド]
 * 1. readInitInfo                       - 固定費一覧初期表示
 * 2. readActSelectItemInfo              - 処理選択画面表示
 * 3. hasFixedCostInfoBySisyutuItem      - 登録済み判定
 * 4. readRegisteredFixedCostInfoBySisyutuItem - 登録済み支出項目の固定費一覧
 * 5. readAddFixedCostInfoBySisyutuItem  - 追加画面表示
 * 6. readUpdateFixedCostInfo            - 更新画面表示
 * 7. readUpdateBindingErrorSetInfo      - バリデーションエラー時の画面表示
 *
 * [テストシナリオ]
 * ⓪ 正常系：初期表示_固定費0件（固定費未登録ユーザ）、支出項目一覧0件（支出項目未登録ユーザ）（readInitInfo）
 * ① 正常系：初期表示_固定費4件一覧表示、支出項目ツリーレベル1階層6件表示（readInitInfo）
 * ② 正常系：処理選択画面_固定費0001(家賃)の詳細表示（readActSelectItemInfo）
 * ② 正常系：処理選択画面_固定費0004(その他任意)_支払月任意コンテキスト付加を確認（readActSelectItemInfo）
 * ③ 異常系：処理選択画面_存在しない固定費コード（readActSelectItemInfo）
 * ④ 正常系：登録済み判定_登録あり(0030:家賃)（hasFixedCostInfoBySisyutuItem）
 * ⑤ 正常系：登録済み判定_登録なし(0035:自由用途積立金)（hasFixedCostInfoBySisyutuItem）
 * ⑥ 正常系：登録済み初期表示_0030の固定費1件（readRegisteredFixedCostInfoBySisyutuItem）
 * ⑦ 正常系：追加画面_0035の支出項目情報設定（readAddFixedCostInfoBySisyutuItem）
 * ⑧ 正常系：更新画面_固定費0001の値設定（readUpdateFixedCostInfo）
 * ⑨ 異常系：更新画面_存在しない固定費コード（readUpdateFixedCostInfo）
 * ⑩ 正常系：バリデーションエラー時画面表示（readUpdateBindingErrorSetInfo）
 *
 * [テストデータ]
 * 固定費4件: 0001:家賃(0030,毎月,27日,60000), 0002:電気代概算(0037,毎月,27日,12000),
 *            0003:国民年金保険(0015,奇数月,月初,16590), 0004:その他任意テスト(0038,その他任意,27日,10000)
 * 奇数月合計: 98,590円、偶数月合計: 82,000円
 * </pre>
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/04/19 : 1.01.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage;

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

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.FixedCostInfoUpdateForm;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.AbstractFixedCostItemListResponse.FixedCostItem;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostInfoManageActSelectResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostInfoManageActSelectResponse.SelectFixedCostInfo;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostInfoManageInitResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostInfoManageUpdateResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

/**
 *<pre>
 * 固定費情報管理ユースケース（参照系）の統合テストクラスです。
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.01)
 *
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
	"/sql/initsql/schema_test.sql",
	"/com/yonetani/webapp/accountbook/application/usecase/itemmanage/FixedCostInfoManageIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("固定費情報管理ユースケース（参照系）のUseCaseテスト（統合テスト）")
class FixedCostInfoManageUseCaseIntegrationTest {

	@Autowired
	private FixedCostInfoManageUseCase useCase;

	// テスト用ログインユーザ
	private final LoginUserInfo TEST_USER = LoginUserInfo.from("user01", "テストユーザ01");

	// ========== readInitInfo ==========

	@Test
	@DisplayName("⓪ readInitInfo_固定費0件（固定費未登録ユーザ）、支出項目一覧0件（支出項目未登録ユーザ）")
	void testReadInitInfo_0件() {
		// user02はSISYUTU_ITEM_TABLE・FIXED_COST_TABLEともに登録なし
		LoginUserInfo user02 = LoginUserInfo.from("user02", "テストユーザ02");
		FixedCostInfoManageInitResponse response = useCase.readInitInfo(user02);

		// 登録済み表示フラグ
		assertFalse(response.isRegisteredFlg(), "登録済みフラグがfalseであること");
		
		// 支出項目一覧：0件（user02にはSISYUTU_ITEM_TABLEのデータがないため）
		assertNotNull(response.getExpenditureItemList(), "支出項目一覧がnullでないこと");
		assertEquals(0, response.getExpenditureItemList().size(), "支出項目一覧が0件であること");

		// 固定費一覧：0件
		assertNotNull(response.getFixedCostItemList(), "固定費一覧がnullでないこと");
		assertEquals(0, response.getFixedCostItemList().size(), "固定費一覧が0件であること");

		// 0件の場合、合計はnull（設定されない）
		assertNull(response.getOddMonthGoukei(), "奇数月合計がnullであること");
		assertNull(response.getAnEvenMonthGoukei(), "偶数月合計がnullであること");

		// 0件の場合、両方のメッセージが設定されること
		assertTrue(response.hasMessages(), "0件メッセージが設定されていること");
		assertTrue(response.getMessagesList().stream().anyMatch(msg -> msg.contains("支出項目情報取得結果が0件です")),
				"支出項目0件メッセージが含まれること");
		assertTrue(response.getMessagesList().stream().anyMatch(msg -> msg.contains("登録済み固定費情報が0件です")),
				"固定費0件メッセージが含まれること");
	}

	@Test
	@DisplayName("① readInitInfo_固定費4件一覧表示、支出項目ツリーレベル1階層6件表示）")
	void testReadInitInfo_4件() {
		FixedCostInfoManageInitResponse response = useCase.readInitInfo(TEST_USER);

		// 登録済み表示フラグ
		assertFalse(response.isRegisteredFlg(), "登録済みフラグがfalseであること");
		
		// 支出項目一覧：Level1のトップカテゴリが6件（SISYUTU_ITEM_SORT昇順）
		assertNotNull(response.getExpenditureItemList(), "支出項目一覧がnullでないこと");
		assertEquals(6, response.getExpenditureItemList().size(), "支出項目一覧のトップレベルが6件であること");
		// 各Level1カテゴリの支出項目コードを確認（SISYUTU_ITEM_SORT昇順）
		assertEquals("0001", response.getExpenditureItemList().get(0).getSisyutuItemCode(), "1番目: 事業経費");
		assertEquals("0013", response.getExpenditureItemList().get(1).getSisyutuItemCode(), "2番目: 固定費(非課税)");
		assertEquals("0023", response.getExpenditureItemList().get(2).getSisyutuItemCode(), "3番目: 固定費(課税)");
		assertEquals("0045", response.getExpenditureItemList().get(3).getSisyutuItemCode(), "4番目: 衣類住居設備");
		assertEquals("0049", response.getExpenditureItemList().get(4).getSisyutuItemCode(), "5番目: 飲食日用品");
		assertEquals("0055", response.getExpenditureItemList().get(5).getSisyutuItemCode(), "6番目: 趣味娯楽");

		// 固定費一覧
		List<FixedCostItem> fixedCostItemList = response.getFixedCostItemList();
		assertNotNull(fixedCostItemList, "固定費一覧がnullでないこと");
		assertEquals(4, fixedCostItemList.size(), "固定費一覧が4件であること");

		// DB順序はSISYUTU_ITEM_SORTでソート:
		// 1件目：国民年金保険(0015, sort=0201010000)
		FixedCostItem item0 = fixedCostItemList.get(0);
		assertEquals("0003", item0.getFixedCostCode(), "1件目の固定費コードが0003であること");
		assertEquals("国民年金保険", item0.getShiharaiName(), "1件目の支払名が国民年金保険であること");
		assertEquals("奇数月", item0.getShiharaiTuki(), "1件目の支払月が奇数月であること");
		assertEquals("月初営業日", item0.getShiharaiDay(), "1件目の支払日が月初営業日であること");
		assertEquals("16,590円", item0.getShiharaiKingaku(), "1件目の支払金額が16,590円であること");

		// 2件目：家賃(0030, sort=0303010000)
		FixedCostItem item1 = fixedCostItemList.get(1);
		assertEquals("0001", item1.getFixedCostCode(), "2件目の固定費コードが0001であること");
		assertEquals("家賃", item1.getShiharaiName(), "2件目の支払名が家賃であること");
		assertEquals("毎月", item1.getShiharaiTuki(), "2件目の支払月が毎月であること");
		assertEquals("27日", item1.getShiharaiDay(), "2件目の支払日が27日であること");
		assertEquals("60,000円", item1.getShiharaiKingaku(), "2件目の支払金額が60,000円であること");

		// 3件目：電気代概算(0037, sort=0306010000)
		FixedCostItem item2 = fixedCostItemList.get(2);
		assertEquals("0002", item2.getFixedCostCode(), "3件目の固定費コードが0002であること");
		assertEquals("電気代概算", item2.getShiharaiName(), "3件目の支払名が電気代概算であること");
		assertEquals("毎月", item2.getShiharaiTuki(), "3件目の支払月が毎月であること");

		// 4件目：その他任意テスト(0038:ガス代, sort=0306020000)
		FixedCostItem item3 = fixedCostItemList.get(3);
		assertEquals("0004", item3.getFixedCostCode(), "4件目の固定費コードが0004であること");
		assertEquals("その他任意テスト", item3.getShiharaiName(), "4件目の支払名がその他任意テストであること");
		assertEquals("その他任意", item3.getShiharaiTuki(), "4件目の支払月がその他任意であること");
		assertEquals("27日", item3.getShiharaiDay(), "4件目の支払日が27日であること");
		assertEquals("10,000円", item3.getShiharaiKingaku(), "4件目の支払金額が10,000円であること");

		// 合計金額確認
		// 奇数月: 60,000(毎月) + 12,000(毎月) + 16,590(奇数月) + 10,000(その他任意=毎月扱い) = 98,590
		// 偶数月: 60,000(毎月) + 12,000(毎月) + 10,000(その他任意=毎月扱い) = 82,000
		assertEquals("98,590円", response.getOddMonthGoukei(), "奇数月合計が98,590円であること");
		assertEquals("82,000円", response.getAnEvenMonthGoukei(), "偶数月合計が82,000円であること");
	}

	// ========== readActSelectItemInfo ==========

	@Test
	@DisplayName("② readActSelectItemInfo_固定費0001(家賃)の詳細表示")
	void testReadActSelectItemInfo_0001() {
		FixedCostInfoManageActSelectResponse response = useCase.readActSelectItemInfo(TEST_USER, "0001");

		SelectFixedCostInfo fixedCostInfo = response.getFixedCostInfo();
		assertNotNull(fixedCostInfo, "固定費詳細情報がnullでないこと");
		assertEquals("0001", fixedCostInfo.getFixedCostCode(), "固定費コードが0001であること");
		assertEquals("家賃", fixedCostInfo.getShiharaiName(), "支払名が家賃であること");
		assertEquals("毎月27日引き落とし", fixedCostInfo.getShiharaiDetailContext(), "支払内容詳細が設定されていること");
		assertEquals("毎月", fixedCostInfo.getShiharaiTukiDetailContext(), "支払月詳細が毎月であること");
		assertEquals("27日", fixedCostInfo.getShiharaiDay(), "支払日が27日であること");
		assertEquals("60,000円", fixedCostInfo.getShiharaiKingaku(), "支払金額が60,000円であること");
		// 支出項目名（固定費(課税)＞地代家賃＞家賃）
		assertEquals("固定費(課税)＞地代家賃＞家賃", fixedCostInfo.getSisyutuItemName(),
				"支出項目名が正しい階層構造で設定されること");

		// 固定費一覧も表示される
		List<FixedCostItem> fixedCostItemList = response.getFixedCostItemList();
		assertNotNull(fixedCostItemList, "固定費一覧がnullでないこと");
		assertEquals(4, fixedCostItemList.size(), "固定費一覧が4件であること");
	}

	@Test
	@DisplayName("readActSelectItemInfo_固定費0004(その他任意)_支払月任意コンテキスト付加を確認")
	void testReadActSelectItemInfo_0004_その他任意() {
		FixedCostInfoManageActSelectResponse response = useCase.readActSelectItemInfo(TEST_USER, "0004");

		SelectFixedCostInfo fixedCostInfo = response.getFixedCostInfo();
		assertNotNull(fixedCostInfo, "固定費詳細情報がnullでないこと");
		assertEquals("0004", fixedCostInfo.getFixedCostCode(), "固定費コードが0004であること");
		assertEquals("その他任意テスト", fixedCostInfo.getShiharaiName(), "支払名がその他任意テストであること");
		assertEquals("その他任意テスト詳細内容", fixedCostInfo.getShiharaiDetailContext(), "支払内容詳細が設定されていること");
		// TUKI='40'(その他任意)の場合、コード変換値に"(任意コンテキスト)"が付加されること
		assertEquals("その他任意(不定期の支払です)", fixedCostInfo.getShiharaiTukiDetailContext(),
				"その他任意の支払月詳細に任意コンテキストが付加されること");
		assertEquals("27日", fixedCostInfo.getShiharaiDay(), "支払日が27日であること");
		assertEquals("10,000円", fixedCostInfo.getShiharaiKingaku(), "支払金額が10,000円であること");
		// 支出項目名（固定費(課税)＞水光熱通費＞ガス代）
		assertEquals("固定費(課税)＞水光熱通費＞ガス代", fixedCostInfo.getSisyutuItemName(),
				"支出項目名が正しい階層構造で設定されること");

		// 固定費一覧も表示される
		List<FixedCostItem> fixedCostItemList = response.getFixedCostItemList();
		assertNotNull(fixedCostItemList, "固定費一覧がnullでないこと");
		assertEquals(4, fixedCostItemList.size(), "固定費一覧が4件であること");
	}

	@Test
	@DisplayName("③ readActSelectItemInfo_存在しない固定費コードで例外")
	void testReadActSelectItemInfo_notFound() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class,
				() -> useCase.readActSelectItemInfo(TEST_USER, "9999"),
				"存在しない固定費コードで例外が発生すること");
	}

	// ========== hasFixedCostInfoBySisyutuItem ==========

	@Test
	@DisplayName("④ hasFixedCostInfoBySisyutuItem_登録あり(0030:家賃)")
	void testHasFixedCostInfoBySisyutuItem_true() {
		assertTrue(useCase.hasFixedCostInfoBySisyutuItem(TEST_USER, "0030"),
				"0030(家賃)に固定費が登録済みであること");
	}

	@Test
	@DisplayName("⑤ hasFixedCostInfoBySisyutuItem_登録なし(0035:自由用途積立金)")
	void testHasFixedCostInfoBySisyutuItem_false() {
		assertFalse(useCase.hasFixedCostInfoBySisyutuItem(TEST_USER, "0035"),
				"0035(自由用途積立金)に固定費が未登録であること");
	}

	// ========== readRegisteredFixedCostInfoBySisyutuItem ==========

	@Test
	@DisplayName("⑥ readRegisteredFixedCostInfoBySisyutuItem_0030の固定費1件")
	void testReadRegisteredFixedCostInfoBySisyutuItem() {
		FixedCostInfoManageInitResponse response =
				useCase.readRegisteredFixedCostInfoBySisyutuItem(TEST_USER, "0030");

		// 登録済み表示フラグ
		assertTrue(response.isRegisteredFlg(), "登録済みフラグがtrueであること");

		// 登録済み固定費一覧(0030に属する固定費: 0001のみ)
		List<FixedCostItem> registeredList = response.getRegisteredFixedCostInfoList();
		assertNotNull(registeredList, "登録済み固定費一覧がnullでないこと");
		assertEquals(1, registeredList.size(), "0030の登録済み固定費が1件であること");
		assertEquals("0001", registeredList.get(0).getFixedCostCode(), "登録済み固定費コードが0001であること");

		// 支出項目コード情報が設定されていること
		assertNotNull(response.getSisyutuItemCodeInfo(), "支出項目コード情報がnullでないこと");
	}

	// ========== readAddFixedCostInfoBySisyutuItem ==========

	@Test
	@DisplayName("⑦ readAddFixedCostInfoBySisyutuItem_0035の支出項目情報設定")
	void testReadAddFixedCostInfoBySisyutuItem() {
		FixedCostInfoManageUpdateResponse response =
				useCase.readAddFixedCostInfoBySisyutuItem(TEST_USER, "0035");

		FixedCostInfoUpdateForm form = response.getFixedCostInfoUpdateForm();
		assertNotNull(form, "フォームがnullでないこと");
		assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, form.getAction(),
				"アクションがADDであること");
		assertEquals("0035", form.getSisyutuItemCode(), "支出項目コードが0035であること");
		assertEquals(MyHouseholdAccountBookContent.FIXED_COST_FIX_SELECTED_VALUE, form.getFixedCostKubun(),
				"固定費区分のデフォルト値が支払い金額確定であること");

		// 支出項目名が設定されていること（固定費(課税)＞積立金＞自由用途積立金）
		assertEquals("固定費(課税)＞積立金＞自由用途積立金", response.getSisyutuItemName(),
				"支出項目名が正しい階層構造で設定されること");

		// 選択ボックスが設定されていること
		assertNotNull(response.getFixedCostKubunSelectList(), "固定費区分選択ボックスがnullでないこと");
		assertNotNull(response.getShiharaiTukiSelectList(), "支払月選択ボックスがnullでないこと");
		assertNotNull(response.getShiharaiDaySelectList(), "支払日選択ボックスがnullでないこと");
	}

	// ========== readUpdateFixedCostInfo ==========

	@Test
	@DisplayName("⑧ readUpdateFixedCostInfo_固定費0004の値設定（支払月任意詳細含む）")
	void testReadUpdateFixedCostInfo_0004() {
		FixedCostInfoManageUpdateResponse response =
				useCase.readUpdateFixedCostInfo(TEST_USER, "0004");

		FixedCostInfoUpdateForm form = response.getFixedCostInfoUpdateForm();
		assertNotNull(form, "フォームがnullでないこと");
		assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE, form.getAction(),
				"アクションがUPDATEであること");
		assertEquals("0004", form.getFixedCostCode(), "固定費コードが0004であること");
		assertEquals("0038", form.getSisyutuItemCode(), "支出項目コードが0038であること");
		assertEquals("その他任意テスト", form.getFixedCostName(), "固定費名がその他任意テストであること");
		assertEquals("その他任意テスト詳細内容", form.getFixedCostDetailContext(), "固定費内容詳細が設定されていること");
		assertEquals("1", form.getFixedCostKubun(), "固定費区分が1(確定)であること");
		assertEquals("40", form.getShiharaiTuki(), "支払月が40(その他任意)であること");
		assertEquals("不定期の支払です", form.getShiharaiTukiOptionalContext(), "支払月任意詳細が設定されていること");
		assertEquals("27", form.getShiharaiDay(), "支払日が27であること");
		assertEquals(Integer.valueOf(10000), form.getShiharaiKingaku(), "支払金額が10000であること");
	}

	@Test
	@DisplayName("⑨ readUpdateFixedCostInfo_存在しない固定費コードで例外")
	void testReadUpdateFixedCostInfo_notFound() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class,
				() -> useCase.readUpdateFixedCostInfo(TEST_USER, "9999"),
				"存在しない固定費コードで例外が発生すること");
	}

	// ========== readUpdateBindingErrorSetInfo ==========

	@Test
	@DisplayName("⑩ readUpdateBindingErrorSetInfo_エラーフォームの画面表示")
	void testReadUpdateBindingErrorSetInfo() {
		// バリデーションエラー時のフォーム（支出項目コード:0035を指定）
		FixedCostInfoUpdateForm errorForm = new FixedCostInfoUpdateForm();
		errorForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		errorForm.setSisyutuItemCode("0035");
		errorForm.setFixedCostName("テスト固定費");

		FixedCostInfoManageUpdateResponse response =
				useCase.readUpdateBindingErrorSetInfo(TEST_USER, errorForm);

		// フォームの値が維持されていること
		FixedCostInfoUpdateForm form = response.getFixedCostInfoUpdateForm();
		assertNotNull(form, "フォームがnullでないこと");
		assertEquals("テスト固定費", form.getFixedCostName(), "固定費名がそのまま返されること");

		// 選択ボックスが設定されていること
		assertNotNull(response.getFixedCostKubunSelectList(), "固定費区分選択ボックスがnullでないこと");
		assertNotNull(response.getShiharaiTukiSelectList(), "支払月選択ボックスがnullでないこと");
		assertNotNull(response.getShiharaiDaySelectList(), "支払日選択ボックスがnullでないこと");
	}
}
