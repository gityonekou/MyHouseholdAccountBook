/**
 * 固定費情報管理ユースケース（参照系）の一括更新関連メソッドの統合テストクラスです。
 *
 * <pre>
 * FixedCostInquiryUseCase の以下メソッドをテストします。
 *
 * [対象メソッド]
 * 1. readActSelectItemInfo - 兄弟固定費あり/なしの分岐（hasSiblingFixedCost）
 * 2. readBulkUpdateInfo   - 一括更新画面初回表示（正常 / 固定費コード不在で例外）
 * 3. readBulkUpdateBindingErrorSetInfo - バリデーションエラー時再表示（正常 / 基準コード不在で例外）
 *
 * [テストシナリオ]
 * ① 正常系：readActSelectItemInfo_0001_兄弟固定費あり（hasSiblingFixedCost=true）
 * ② 正常系：readActSelectItemInfo_0003_兄弟固定費なし（hasSiblingFixedCost=false）
 * ③ 正常系：readBulkUpdateInfo_正常（フォーム初期値・対象リスト2件確認）
 * ④ 異常系：readBulkUpdateInfo_存在しない固定費コードで例外
 * ⑤ 正常系：readBulkUpdateBindingErrorSetInfo_正常（入力値保持・対象リスト2件確認）
 * ⑥ 異常系：readBulkUpdateBindingErrorSetInfo_存在しない基準固定費コードで例外
 *
 * [テストデータ]
 * 固定費3件:
 *   0001: 家賃   (0030:家賃, 確定, 毎月, 27日, 60,000円) ← 兄弟固定費(その1)
 *   0002: 共益費 (0030:家賃, 確定, 毎月, 27日,  8,000円) ← 兄弟固定費(その2)
 *   0003: 電気代概算 (0037:電気代, 予定, 毎月, 27日, 12,000円) ← 兄弟なし
 * </pre>
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/05/02 : 1.01.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage.fixedcost;

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

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.FixedCostBulkUpdateForm;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostBulkUpdateResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostBulkUpdateResponse.BulkUpdateTargetItem;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostInfoManageActSelectResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.FixedCostInfoManageActSelectResponse.SiblingFixedCostItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

/**
 *<pre>
 * 固定費情報管理ユースケース（参照系）の一括更新関連メソッドの統合テストクラスです。
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
	"/com/yonetani/webapp/accountbook/application/usecase/itemmanage/fixedcost/FixedCostBulkUpdateIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("固定費情報管理ユースケース（参照系）一括更新メソッドのUseCaseテスト（統合テスト）")
class FixedCostInquiryUseCaseBulkUpdateIntegrationTest {

	@Autowired
	private FixedCostInquiryUseCase useCase;

	// テスト用ログインユーザ
	private final LoginUserInfo TEST_USER = LoginUserInfo.from("user01", "テストユーザ01");

	// ========== readActSelectItemInfo（兄弟固定費あり/なしの分岐） ==========

	/**
	 *<pre>
	 * テスト①：正常系：readActSelectItemInfo_0001_兄弟固定費あり（hasSiblingFixedCost=true）
	 *
	 * 【検証内容】
	 * ・固定費0001(家賃)と0002(共益費)が同じ支出項目コード0030に属するため
	 *   hasSiblingFixedCost=true となること
	 * ・siblingFixedCostItemList が2件で取得されること
	 * ・兄弟固定費の内容（支払名・支払月・支払金額）が正しく設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("① readActSelectItemInfo_0001_同一分類に2件→hasSiblingFixedCost=true")
	void testReadActSelectItemInfo_0001_hasSiblingFixedCostTrue() {
		FixedCostInfoManageActSelectResponse response = useCase.readActSelectItemInfo(TEST_USER, "0001");

		// hasSiblingFixedCost が true であること
		assertTrue(response.isHasSiblingFixedCost(), "同一支出項目に2件以上の固定費があるためtrueであること");

		// 兄弟固定費リストが2件であること
		List<SiblingFixedCostItem> siblingList = response.getSiblingFixedCostItemList();
		assertNotNull(siblingList, "兄弟固定費リストがnullでないこと");
		assertEquals(2, siblingList.size(), "兄弟固定費リストが2件であること");

		// 兄弟リストに 0001(家賃) と 0002(共益費) が含まれること
		boolean has0001 = siblingList.stream().anyMatch(item -> "0001".equals(item.getFixedCostCode()));
		boolean has0002 = siblingList.stream().anyMatch(item -> "0002".equals(item.getFixedCostCode()));
		assertTrue(has0001, "兄弟リストに固定費コード0001(家賃)が含まれること");
		assertTrue(has0002, "兄弟リストに固定費コード0002(共益費)が含まれること");

		// 各兄弟固定費のフィールドを確認（0001:家賃）
		SiblingFixedCostItem item0001 = siblingList.stream()
				.filter(item -> "0001".equals(item.getFixedCostCode()))
				.findFirst().orElseThrow();
		assertEquals("家賃", item0001.getShiharaiName(), "0001の支払名が家賃であること");
		assertEquals("毎月", item0001.getShiharaiTukiDetailContext(), "0001の支払月が毎月であること");
		assertEquals("60,000円", item0001.getShiharaiKingaku(), "0001の支払金額が60,000円であること");
		assertEquals("", item0001.getShiharaiTukiOptionalContext(), "0001の任意詳細が空であること");

		// 各兄弟固定費のフィールドを確認（0002:共益費）
		SiblingFixedCostItem item0002 = siblingList.stream()
				.filter(item -> "0002".equals(item.getFixedCostCode()))
				.findFirst().orElseThrow();
		assertEquals("共益費", item0002.getShiharaiName(), "0002の支払名が共益費であること");
		assertEquals("毎月", item0002.getShiharaiTukiDetailContext(), "0002の支払月が毎月であること");
		assertEquals("8,000円", item0002.getShiharaiKingaku(), "0002の支払金額が8,000円であること");

		// 選択固定費の情報が正しく設定されていること
		assertEquals("0001", response.getFixedCostInfo().getFixedCostCode(),
				"選択固定費コードが0001であること");
	}

	/**
	 *<pre>
	 * テスト②：正常系：readActSelectItemInfo_0003_兄弟固定費なし（hasSiblingFixedCost=false）
	 *
	 * 【検証内容】
	 * ・固定費0003(電気代概算)は支出項目コード0037に1件のみ属するため
	 *   hasSiblingFixedCost=false となること
	 * ・siblingFixedCostItemList が空リストであること
	 *</pre>
	 */
	@Test
	@DisplayName("② readActSelectItemInfo_0003_同一分類に1件→hasSiblingFixedCost=false")
	void testReadActSelectItemInfo_0003_hasSiblingFixedCostFalse() {
		FixedCostInfoManageActSelectResponse response = useCase.readActSelectItemInfo(TEST_USER, "0003");

		// hasSiblingFixedCost が false であること
		assertFalse(response.isHasSiblingFixedCost(), "同一支出項目が1件のみのためfalseであること");

		// 兄弟固定費リストが空であること
		List<SiblingFixedCostItem> siblingList = response.getSiblingFixedCostItemList();
		assertNotNull(siblingList, "兄弟固定費リストがnullでないこと");
		assertTrue(siblingList.isEmpty(), "兄弟固定費リストが空であること");

		// 選択固定費の情報が正しく設定されていること
		assertEquals("0003", response.getFixedCostInfo().getFixedCostCode(),
				"選択固定費コードが0003であること");
		assertEquals("電気代概算", response.getFixedCostInfo().getShiharaiName(),
				"選択固定費名が電気代概算であること");
	}

	// ========== readBulkUpdateInfo ==========

	/**
	 *<pre>
	 * テスト③：正常系：readBulkUpdateInfo_正常（フォーム初期値・対象リスト確認）
	 *
	 * 【検証内容】
	 * ・基準固定費コード=0001(家賃, 27日, 60,000円)を指定して一括更新画面情報を取得
	 * ・フォームの初期値として基準固定費の支払日(27)・支払金額(60000)が設定されること
	 * ・フォームのbaseFixedCostCodeに0001が設定されること
	 * ・一括更新対象リストとして同一支出項目(0030)に属する2件が取得されること
	 * ・支出項目名が「固定費(課税)＞地代家賃＞家賃」で設定されること
	 * ・支払日選択ボックスが設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("③ readBulkUpdateInfo_基準コード=0001→フォーム初期値(27日/60000)・対象リスト2件")
	void testReadBulkUpdateInfo_success() {
		FixedCostBulkUpdateResponse response = useCase.readBulkUpdateInfo(TEST_USER, "0001");

		// フォームの初期値確認
		FixedCostBulkUpdateForm form = response.getFixedCostBulkUpdateForm();
		assertNotNull(form, "フォームがnullでないこと");
		assertEquals("0001", form.getBaseFixedCostCode(), "基準固定費コードが0001であること");
		assertEquals("27", form.getShiharaiDay(), "支払日初期値が27(基準固定費0001の値)であること");
		assertEquals(Integer.valueOf(60000), form.getShiharaiKingaku(),
				"支払金額初期値が60000(基準固定費0001の値)であること");

		// 一括更新対象リストが2件（0001:家賃、0002:共益費）
		List<BulkUpdateTargetItem> targetList = response.getBulkUpdateTargetList();
		assertNotNull(targetList, "一括更新対象リストがnullでないこと");
		assertEquals(2, targetList.size(), "一括更新対象リストが2件(同一0030に属する全固定費)であること");

		// 対象リストに0001と0002が含まれること
		boolean has0001 = targetList.stream().anyMatch(item -> "0001".equals(item.getFixedCostCode()));
		boolean has0002 = targetList.stream().anyMatch(item -> "0002".equals(item.getFixedCostCode()));
		assertTrue(has0001, "対象リストに0001(家賃)が含まれること");
		assertTrue(has0002, "対象リストに0002(共益費)が含まれること");

		// 対象リストの内容確認（0001:家賃）
		BulkUpdateTargetItem target0001 = targetList.stream()
				.filter(item -> "0001".equals(item.getFixedCostCode()))
				.findFirst().orElseThrow();
		assertEquals("家賃", target0001.getShiharaiName(), "0001の支払名が家賃であること");
		assertEquals("毎月", target0001.getShiharaiTukiDetailContext(), "0001の支払月が毎月であること");
		assertEquals("27日", target0001.getShiharaiDay(), "0001の現在の支払日が27日であること");
		assertEquals("60,000円", target0001.getShiharaiKingaku(), "0001の現在の支払金額が60,000円であること");

		// 支出項目名が階層表示で設定されていること（固定費(課税)＞地代家賃＞家賃）
		assertEquals("固定費(課税)＞地代家賃＞家賃", response.getSisyutuItemName(),
				"支出項目名が正しい階層構造(固定費(課税)＞地代家賃＞家賃)で設定されること");

		// 支払日選択ボックスが設定されていること
		assertNotNull(response.getShiharaiDaySelectList(), "支払日選択ボックスがnullでないこと");
		assertFalse(response.getShiharaiDaySelectList().getOptionList().isEmpty(),
				"支払日選択ボックスの選択肢が空でないこと");
	}

	/**
	 *<pre>
	 * テスト④：異常系：readBulkUpdateInfo_存在しない固定費コードで例外
	 *
	 * 【検証内容】
	 * ・存在しない固定費コード("9999")を指定した場合、MyHouseholdAccountBookRuntimeExceptionが発生すること
	 *</pre>
	 */
	@Test
	@DisplayName("④ readBulkUpdateInfo_存在しない固定費コードで例外")
	void testReadBulkUpdateInfo_notFound() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class,
				() -> useCase.readBulkUpdateInfo(TEST_USER, "9999"),
				"存在しない固定費コードで例外が発生すること");
	}

	// ========== readBulkUpdateBindingErrorSetInfo ==========

	/**
	 *<pre>
	 * テスト⑤：正常系：readBulkUpdateBindingErrorSetInfo_正常（入力値保持・対象リスト確認）
	 *
	 * 【検証内容】
	 * ・バリデーションエラー時に送信済みフォームの入力値が維持されること
	 * ・shiharaiDay/shiharaiKingakuが送信値のままで返されること
	 * ・一括更新対象リストとして同一支出項目(0030)に属する2件が取得されること
	 * ・支払日選択ボックスが設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("⑤ readBulkUpdateBindingErrorSetInfo_入力値保持・対象リスト2件")
	void testReadBulkUpdateBindingErrorSetInfo_success() {
		// バリデーションエラー時のフォーム（ユーザが入力した値）
		FixedCostBulkUpdateForm inputForm = new FixedCostBulkUpdateForm();
		inputForm.setBaseFixedCostCode("0001");
		inputForm.setShiharaiDay("25");       // ユーザが変更した値
		inputForm.setShiharaiKingaku(70000);   // ユーザが変更した値
		// checkedFixedCostCodeList は空（これがバリデーションエラーの原因）

		FixedCostBulkUpdateResponse response = useCase.readBulkUpdateBindingErrorSetInfo(TEST_USER, inputForm);

		// フォームの入力値が維持されていること
		FixedCostBulkUpdateForm resultForm = response.getFixedCostBulkUpdateForm();
		assertNotNull(resultForm, "フォームがnullでないこと");
		assertEquals("0001", resultForm.getBaseFixedCostCode(), "基準固定費コードが維持されること");
		assertEquals("25", resultForm.getShiharaiDay(), "ユーザが入力した支払日(25)が維持されること");
		assertEquals(Integer.valueOf(70000), resultForm.getShiharaiKingaku(),
				"ユーザが入力した支払金額(70000)が維持されること");

		// 一括更新対象リストが2件取得されること
		List<BulkUpdateTargetItem> targetList = response.getBulkUpdateTargetList();
		assertNotNull(targetList, "一括更新対象リストがnullでないこと");
		assertEquals(2, targetList.size(), "一括更新対象リストが2件であること");

		// 支払日選択ボックスが設定されていること
		assertNotNull(response.getShiharaiDaySelectList(), "支払日選択ボックスがnullでないこと");
	}

	/**
	 *<pre>
	 * テスト⑥：異常系：readBulkUpdateBindingErrorSetInfo_存在しない基準固定費コードで例外
	 *
	 * 【検証内容】
	 * ・フォームの baseFixedCostCode に存在しないコード("9999")が設定されている場合、
	 *   MyHouseholdAccountBookRuntimeExceptionが発生すること
	 *</pre>
	 */
	@Test
	@DisplayName("⑥ readBulkUpdateBindingErrorSetInfo_存在しない基準固定費コードで例外")
	void testReadBulkUpdateBindingErrorSetInfo_notFound() {
		FixedCostBulkUpdateForm inputForm = new FixedCostBulkUpdateForm();
		inputForm.setBaseFixedCostCode("9999");
		inputForm.setShiharaiDay("25");
		inputForm.setShiharaiKingaku(70000);

		assertThrows(MyHouseholdAccountBookRuntimeException.class,
				() -> useCase.readBulkUpdateBindingErrorSetInfo(TEST_USER, inputForm),
				"存在しない基準固定費コードで例外が発生すること");
	}
}
