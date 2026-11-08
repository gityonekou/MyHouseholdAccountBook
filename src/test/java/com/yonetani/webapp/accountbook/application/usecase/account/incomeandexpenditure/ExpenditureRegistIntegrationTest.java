/**
 * 収支登録・更新画面の支出登録機能の統合テストクラスです。
 * 既存機能の動作を保証するために、リファクタリング前の振る舞いを記録します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/01/25 : 1.00.00  feature-1.00-dev00  新規作成
 * 2026/08/18 : 1.01.00  feature-1.03-dev1   支払方法・銀行口座管理追加対応
 * 2026/11/01 : 1.01.01  feature-1.03-dev1   支払方法・銀行口座管理追加対応(支出一覧に支払方法名・引落先口座名を追加)
 * 2026/11/08 : 1.01.02  feature-1.03-dev1   セッション登録情報(ExpenditureRegistItem)のfrom()引数が値オブジェクト型になったことに伴うテストデータ生成処理の修正(型安全性対応。06_コーディング規約.md §8)
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.incomeandexpenditure;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCategory;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureEventCode;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureName;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.presentation.request.account.regist.ExpenditureItemForm;
import com.yonetani.webapp.accountbook.presentation.request.account.regist.ExpenditureSelectItemForm;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.IncomeAndExpenditureRegistResponse;
import com.yonetani.webapp.accountbook.presentation.session.ExpenditureRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.IncomeRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

/**
 *<pre>
 * 収支登録・更新画面の支出登録機能の統合テストクラスです。
 *
 * [テスト方針]
 * ・既存機能の動作を保証するために、リファクタリング前の振る舞いを記録
 * ・セッション操作を含む全レイヤーの結合テスト
 * ・正常系・異常系の両方をカバー
 *
 * [対象メソッド]
 * 1. readExpenditureUpdateSelect - 支出更新選択画面表示
 * 2. readExpenditureUpdateBindingErrorSetInfo - 支出更新時のバインディングエラー処理
 * 3. readNewExpenditureItem - 選択した支出項目の新規支出情報表示
 * 4. execExpenditureAction - 支出の追加・更新・削除（セッション操作）
 *
 * [テストシナリオ]
 * readExpenditureUpdateSelect:
 *   1. 正常系: 更新画面表示_選択支出情報のフォーム設定確認（支出項目名＞区切り、イベントコード確認含む）
 *   2. 異常系: 更新画面表示_支出コード不存在エラー
 * readExpenditureUpdateBindingErrorSetInfo:
 *   3. 正常系: バインディングエラー時_入力値保持確認
 * readNewExpenditureItem:
 *   4. 正常系: 新規支出項目表示_フォーム初期値確認（支出項目名具体値、イベントコード空文字確認）
 *   5. 正常系: 新規支出項目表示_イベントコード設定あり（支出項目名＞区切り＋イベント名表示確認）
 *   6. 異常系: イベント必須なのにイベントコード未設定エラー
 * execExpenditureAction:
 *   7. 正常系: 支出新規追加_セッションへの追加確認（支出コード17桁、イベントコード具体値確認）
 *   8. 正常系: 支出更新_セッションの更新確認
 *   9. 正常系: 支出削除_新規データタイプ_セッションから完全削除
 *  10. 正常系: 支出削除_ロードデータタイプ_削除アクション設定
 *  11. 異常系: 支出更新_支出コード不存在エラー
 *  12. 異常系: 支出削除_支出コード不存在エラー
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
	"/sql/initsql/schema_test.sql",
	"/com/yonetani/webapp/accountbook/application/usecase/account/regist/ExpenditureRegistIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("収支管理機能 支出登録のUseCaseテスト（統合テスト）")
class ExpenditureRegistIntegrationTest {

	@Autowired
	private ExpenditureRegistUseCase useCase;

	/**
	 * テスト用のログインユーザ情報を作成します。
	 */
	private LoginUserInfo createLoginUser() {
		return LoginUserInfo.from("user01", "テストユーザ01");
	}

	/**
	 * テスト用の空の収入登録情報リストを作成します。
	 */
	private List<IncomeRegistItem> createEmptyIncomeRegistItemList() {
		return new ArrayList<>();
	}

	/**
	 *<pre>
	 * プリミティブ引数を値オブジェクトへ変換してからExpenditureRegistItem.from()を呼び出すヘルパーメソッドです。
	 * from()の引数が値オブジェクト型になったため(06_コーディング規約.md §8)、テストコードから
	 * 直接プリミティブ値で呼び出せるようにするためのものです(同規約§8.3参照)。
	 *</pre>
	 */
	private ExpenditureRegistItem expenditureRegistItemHelper(
			String dataType, String action, String expenditureCode, String expenditureItemCode,
			String eventCode, String expenditureName, String expenditureCategory, String expenditureDetailContext,
			String siharaiDate, String paymentMethodCode, BigDecimal expenditureKingaku, boolean clearStartFlg) {
		return ExpenditureRegistItem.from(
				dataType,
				action,
				expenditureCode,
				ExpenditureItemCode.from(expenditureItemCode),
				ExpenditureEventCode.from(eventCode),
				ExpenditureName.from(expenditureName),
				ExpenditureCategory.from(expenditureCategory),
				ExpenditureDetailContext.from(expenditureDetailContext),
				siharaiDate,
				PaymentMethodCode.from(paymentMethodCode),
				ExpenditureAmount.from(expenditureKingaku.setScale(2)),
				clearStartFlg);
	}

	/**
	 * テスト用の支出登録情報リストを作成します（新規データタイプ）。
	 */
	private List<ExpenditureRegistItem> createTestExpenditureRegistItemList() {
		List<ExpenditureRegistItem> list = new ArrayList<>();
		list.add(expenditureRegistItemHelper(
			MyHouseholdAccountBookContent.DATA_TYPE_NEW,
			MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
			"20260125120000001",
			"0037", // 支出項目コード（電気代: Level3, parent=0036(水光熱通費), grandparent=0023(固定費(課税)))
			"",     // イベントコード（なし）
			"テスト電気代",
			"1",    // 無駄遣いなし
			"テスト電気代詳細",
			"25",   // 支払日（日のみ）
			"001",  // 支払方法
			new BigDecimal("5000.00"),
			false   // clearStartFlg
			
		));
		list.add(expenditureRegistItemHelper(
			MyHouseholdAccountBookContent.DATA_TYPE_NEW,
			MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
			"20260125120000002",
			"0051", // 支出項目コード（食費: Level2, parent=0049(飲食日用品))
			"",     // イベントコード（なし）
			"テスト食費",
			"2",    // 無駄遣いB
			"テスト食費詳細",
			"26",   // 支払日（日のみ）
			"001",  // 支払方法
			new BigDecimal("3000.00"),
			false   // clearStartFlg
		));
		return list;
	}

	/**
	 * テスト用の支出登録情報リストを作成します（ロードデータタイプ）。
	 */
	private List<ExpenditureRegistItem> createTestExpenditureRegistItemListWithLoadType() {
		List<ExpenditureRegistItem> list = new ArrayList<>();
		list.add(expenditureRegistItemHelper(
			MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
			MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE, // DBロード時の初期値はデータ変更なし
			"001", // DBロード時の支出コード形式（EXPENDITURE_CODEはCHAR(3)）
			"0037", // 支出項目コード（電気代）
			"",     // イベントコード（なし）
			"DB登録済み電気代",
			"1",    // 無駄遣いなし
			"DB登録済み電気代詳細",
			"15",   // 支払日（日のみ）
			"001",  // 支払方法
			new BigDecimal("8000.00"),
			false   // clearStartFlg
		));
		list.add(expenditureRegistItemHelper(
			MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
			MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE, // DBロード時の初期値はデータ変更なし
			"002",
			"0051", // 支出項目コード（食費）
			"",     // イベントコード（なし）
			"DB登録済み食費",
			"1",    // 無駄遣いなし
			"DB登録済み食費詳細",
			"20",   // 支払日（日のみ）
			"001",  // 支払方法
			new BigDecimal("2000.00"),
			true    // clearStartFlg=true
		));
		return list;
	}

	// ========================================
	// 正常系テスト - readExpenditureUpdateSelect
	// ========================================

	/**
	 *<pre>
	 * テスト①：正常系：更新画面表示_選択支出情報のフォーム設定確認
	 *
	 * 【検証内容】
	 * ・セッションに存在する支出コード(20260125120000001)の情報がフォームに設定されること
	 * ・アクションが更新(update)であること
	 * ・支出項目名が階層を＞で区切って表示されること（固定費(課税)＞水光熱通費＞電気代）
	 * ・イベントコード（空文字）・支出名・支出区分・詳細・金額・支払日が正しく設定されること
	 * ・画面表示用支出一覧が2件設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：更新画面表示_選択支出情報のフォーム設定確認")
	void testReadExpenditureUpdateSelect_NormalCase() {
		// Given: テストユーザ、対象年月、支出コード、セッションの収入・支出リスト
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";
		String expenditureCode = "20260125120000001";
		List<IncomeRegistItem> incomeList = createEmptyIncomeRegistItemList();
		List<ExpenditureRegistItem> expenditureList = createTestExpenditureRegistItemList();

		// When: 更新画面を表示
		IncomeAndExpenditureRegistResponse response = useCase.readExpenditureUpdateSelect(
			user, targetYearMonth, expenditureCode, incomeList, expenditureList);

		// Then: レスポンスが正しく返却される
		assertNotNull(response);

		// Then: フォームが更新モードで設定されている
		ExpenditureItemForm form = response.getExpenditureItemForm();
		assertNotNull(form);
		assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE, form.getAction());
		assertEquals("20260125120000001", form.getExpenditureCode());
		assertEquals("0037", form.getSisyutuItemCode());
		// 支出項目名：親の階層を＞で区切って表示（固定費(課税)＞水光熱通費＞電気代）
		assertEquals("固定費(課税)＞水光熱通費＞電気代", form.getSisyutuItemName());
		// イベントコード：未設定の場合はnullに正規化される(ExpenditureEventCodeがNullableIdentifierを継承しているため)
		assertNull(form.getEventCode());
		assertEquals("テスト電気代", form.getExpenditureName());
		assertEquals("1", form.getExpenditureKubun());
		assertEquals("テスト電気代詳細", form.getExpenditureDetailContext());
		assertEquals(5000, form.getExpenditureKingaku());
		// 支払日の確認（YYYYMM + DD形式でパース）
		assertEquals(LocalDate.of(2025, 11, 25), form.getSiharaiDate());
		assertFalse(form.isClearStartFlg());

		// Then: 支出一覧が設定されている
		assertNotNull(response.getExpenditureListInfo());
		assertEquals(2, response.getExpenditureListInfo().size());
	}

	/**
	 *<pre>
	 * テスト②：異常系：更新画面表示_支出コード不存在エラー
	 *
	 * 【検証内容】
	 * ・セッションに存在しない支出コードを指定した場合、MyHouseholdAccountBookRuntimeExceptionがスローされること
	 * ・例外メッセージに「更新対象の支出情報がセッションに存在しません」が含まれること
	 * ・例外メッセージに指定した支出コード値が含まれること
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：更新画面表示_支出コード不存在エラー")
	void testReadExpenditureUpdateSelect_ExpenditureCodeNotFound() {
		// Given: テストユーザ、対象年月、存在しない支出コード、セッションの収入・支出リスト
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";
		String nonExistentExpenditureCode = "99999999999999999";
		List<IncomeRegistItem> incomeList = createEmptyIncomeRegistItemList();
		List<ExpenditureRegistItem> expenditureList = createTestExpenditureRegistItemList();

		// When/Then: 存在しない支出コードで更新画面を表示すると例外がスローされる
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> useCase.readExpenditureUpdateSelect(
				user, targetYearMonth, nonExistentExpenditureCode, incomeList, expenditureList)
		);

		// Then: エラーメッセージを確認
		assertTrue(exception.getMessage().contains("更新対象の支出情報がセッションに存在しません"));
		assertTrue(exception.getMessage().contains("expenditureCode=" + nonExistentExpenditureCode));
	}

	// ========================================
	// 正常系テスト - readExpenditureUpdateBindingErrorSetInfo
	// ========================================

	/**
	 *<pre>
	 * テスト③：正常系：バインディングエラー時_入力値保持確認
	 *
	 * 【検証内容】
	 * ・バインディングエラー時に入力フォームの値がそのまま保持されること
	 * ・アクション・支出項目コード・支出項目名・支出名・支出区分・詳細・支払日・金額（負の値）が変更されないこと
	 * ・画面表示用支出一覧が2件設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：バインディングエラー時_入力値保持確認")
	void testReadExpenditureUpdateBindingErrorSetInfo_NormalCase() {
		// Given: テストユーザ、対象年月、入力フォーム、セッションの収入・支出リスト
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";

		ExpenditureItemForm inputForm = new ExpenditureItemForm();
		inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		inputForm.setSisyutuItemCode("0001");
		inputForm.setSisyutuItemName("事業経費");
		inputForm.setExpenditureName("テスト支出名");
		inputForm.setExpenditureKubun("1");
		inputForm.setExpenditureDetailContext("テスト詳細");
		inputForm.setSiharaiDate(LocalDate.of(2025, 11, 25));
		inputForm.setExpenditureKingaku(-100); // バリデーションエラーとなる負の値
		inputForm.setClearStartFlg(false);

		List<IncomeRegistItem> incomeList = createEmptyIncomeRegistItemList();
		List<ExpenditureRegistItem> expenditureList = createTestExpenditureRegistItemList();

		// When: バインディングエラー時の画面表示
		IncomeAndExpenditureRegistResponse response = useCase.readExpenditureUpdateBindingErrorSetInfo(
			user, targetYearMonth, inputForm, incomeList, expenditureList);

		// Then: レスポンスが正しく返却される
		assertNotNull(response);

		// Then: 入力値がそのまま保持されている
		ExpenditureItemForm form = response.getExpenditureItemForm();
		assertNotNull(form);
		assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, form.getAction());
		assertEquals("0001", form.getSisyutuItemCode());
		assertEquals("事業経費", form.getSisyutuItemName());
		assertEquals("テスト支出名", form.getExpenditureName());
		assertEquals("1", form.getExpenditureKubun());
		assertEquals("テスト詳細", form.getExpenditureDetailContext());
		assertEquals(LocalDate.of(2025, 11, 25), form.getSiharaiDate());
		assertEquals(-100, form.getExpenditureKingaku());

		// Then: 支出一覧も設定されている
		assertNotNull(response.getExpenditureListInfo());
		assertEquals(2, response.getExpenditureListInfo().size());
	}

	/**
	 *<pre>
	 * テスト③-2：正常系：バインディングエラー時_支払方法コードが形式不正でも500エラーにならないこと
	 *
	 * 【検証内容】
	 * ・他項目(支払金額)のバリデーションエラーで本メソッドが呼ばれた際、支払方法コードが
	 *   コード体系上不正な値(3桁数値以外)であっても、PaymentMethodCode.from()の例外が伝播せず
	 *   正常にレスポンスが返却されること(tryFrom()による安全な解決。突合レビュー指摘AAと同型の対応)
	 * ・disabled表示用の支払方法名は解決不能のため「－」になること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：バインディングエラー時_支払方法コードが形式不正でも500エラーにならないこと")
	void testReadExpenditureUpdateBindingErrorSetInfo_MalformedPaymentMethodCode() {
		// Given: テストユーザ、対象年月、支払方法コードが形式不正(3桁数値以外)な入力フォーム
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";

		ExpenditureItemForm inputForm = new ExpenditureItemForm();
		inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		inputForm.setSisyutuItemCode("0001");
		inputForm.setSisyutuItemName("事業経費");
		inputForm.setExpenditureName("テスト支出名");
		inputForm.setExpenditureKubun("1");
		inputForm.setPaymentMethodCode("XX"); // コード体系上不正な値(3桁数値ではない)
		inputForm.setExpenditureKingaku(-100); // バリデーションエラーとなる負の値
		inputForm.setClearStartFlg(false);

		List<IncomeRegistItem> incomeList = createEmptyIncomeRegistItemList();
		List<ExpenditureRegistItem> expenditureList = createTestExpenditureRegistItemList();

		// When: バインディングエラー時の画面表示(例外がスローされないことを確認)
		IncomeAndExpenditureRegistResponse response = useCase.readExpenditureUpdateBindingErrorSetInfo(
			user, targetYearMonth, inputForm, incomeList, expenditureList);

		// Then: レスポンスが正しく返却される
		assertNotNull(response);
		// Then: 入力値(不正値)はそのまま保持されている
		assertEquals("XX", response.getExpenditureItemForm().getPaymentMethodCode());
		// Then: disabled表示用の支払方法名は解決不能のため「－」
		assertEquals("－", response.getPaymentMethodCurrentName());
	}

	// ========================================
	// 正常系テスト - readNewExpenditureItem
	// ========================================

	/**
	 *<pre>
	 * テスト④：正常系：新規支出項目表示_フォーム初期値確認
	 *
	 * 【検証内容】
	 * ・アクションが新規追加(add)であること
	 * ・支出項目コード(0001:事業経費, Level1)が設定されること
	 * ・支出項目名がLevel1のため＞区切りなし（「事業経費」のみ）であること
	 * ・イベントコードが空文字であること
	 * ・支出名・支出詳細に支出項目名・項目詳細の初期値が設定されること
	 * ・clearStartFlgがfalseであること
	 * ・画面表示用支出一覧が2件設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：新規支出項目表示_フォーム初期値確認")
	void testReadNewExpenditureItem_NormalCase() {
		// Given: テストユーザ、対象年月、支出項目選択フォーム、セッションの収入・支出リスト
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";

		ExpenditureSelectItemForm selectForm = new ExpenditureSelectItemForm();
		selectForm.setSisyutuItemCode("0001");
		selectForm.setEventCode("");
		selectForm.setEventCodeRequired(false);

		List<IncomeRegistItem> incomeList = createEmptyIncomeRegistItemList();
		List<ExpenditureRegistItem> expenditureList = createTestExpenditureRegistItemList();

		// When: 新規支出項目画面を表示
		IncomeAndExpenditureRegistResponse response = useCase.readNewExpenditureItem(
			user, targetYearMonth, selectForm, incomeList, expenditureList);

		// Then: レスポンスが正しく返却される
		assertNotNull(response);

		// Then: フォームが新規追加モードで設定されている
		ExpenditureItemForm form = response.getExpenditureItemForm();
		assertNotNull(form);
		assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, form.getAction());
		assertEquals("0001", form.getSisyutuItemCode());
		// 支出項目名（＞区切り）：0001はLevel1のため親なし、"事業経費"のみ
		assertEquals("事業経費", form.getSisyutuItemName());
		// イベントコード：空文字が設定されていること
		assertEquals("", form.getEventCode());
		// 支出名には支出項目名が初期値として設定される
		assertEquals("事業経費", form.getExpenditureName());
		// 支出詳細には支出項目詳細が初期値として設定される
		assertEquals("事業経費詳細を入力", form.getExpenditureDetailContext());
		// clearStartFlgはfalse
		assertFalse(form.isClearStartFlg());

		// Then: 支出一覧が設定されている
		assertNotNull(response.getExpenditureListInfo());
		assertEquals(2, response.getExpenditureListInfo().size());
	}

	/**
	 *<pre>
	 * テスト⑤：正常系：新規支出項目表示_イベントコード設定あり
	 *
	 * 【検証内容】
	 * ・イベント系支出項目(0061:コミケ, Level3)を選択した場合、支出項目名に【イベント名】が付加されること
	 * ・支出項目名が「趣味娯楽＞イベント費＞コミケ【テストイベント】」となること
	 * ・イベントコード(0001)が正しく設定されること
	 * ・支出名・支出詳細に支出項目の初期値が設定されること
	 * ・画面表示用支出一覧が2件設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：新規支出項目表示_イベントコード設定あり")
	void testReadNewExpenditureItem_WithEventCode() {
		// Given: テストユーザ、対象年月、支出項目選択フォーム（イベント系支出項目）
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";

		ExpenditureSelectItemForm selectForm = new ExpenditureSelectItemForm();
		selectForm.setSisyutuItemCode("0061"); // コミケ（イベント系支出項目、parent=0058(イベント費))
		selectForm.setEventCode("0001"); // テストイベント
		selectForm.setEventCodeRequired(true); // イベント必須

		List<IncomeRegistItem> incomeList = createEmptyIncomeRegistItemList();
		List<ExpenditureRegistItem> expenditureList = createTestExpenditureRegistItemList();

		// When: 新規支出項目画面を表示
		IncomeAndExpenditureRegistResponse response = useCase.readNewExpenditureItem(
			user, targetYearMonth, selectForm, incomeList, expenditureList);

		// Then: レスポンスが正しく返却される
		assertNotNull(response);

		// Then: フォームが新規追加モードで設定されている
		ExpenditureItemForm form = response.getExpenditureItemForm();
		assertNotNull(form);
		assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, form.getAction());
		assertEquals("0061", form.getSisyutuItemCode());
		// 支出項目名（＞区切り＋イベント名）：趣味娯楽＞イベント費＞コミケ【テストイベント】
		assertEquals("趣味娯楽＞イベント費＞コミケ【テストイベント】", form.getSisyutuItemName());
		// イベントコードが設定されていること
		assertEquals("0001", form.getEventCode());
		// 支出名には支出項目名が初期値として設定される
		assertEquals("コミケ", form.getExpenditureName());
		// 支出詳細には支出項目詳細が初期値として設定される
		assertEquals("コミケイベント詳細を入力", form.getExpenditureDetailContext());
		// clearStartFlgはfalse
		assertFalse(form.isClearStartFlg());

		// Then: 支出一覧が設定されている
		assertNotNull(response.getExpenditureListInfo());
		assertEquals(2, response.getExpenditureListInfo().size());
	}

	/**
	 *<pre>
	 * テスト⑥：異常系：新規支出項目表示_イベント必須未設定エラー
	 *
	 * 【検証内容】
	 * ・eventCodeRequired=trueかつイベントコードが空文字の場合、MyHouseholdAccountBookRuntimeExceptionがスローされること
	 * ・例外メッセージに「イベント情報を必須選択になっていますが」が含まれること
	 * ・例外メッセージに「イベントコードが空です」が含まれること
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：新規支出項目表示_イベント必須未設定エラー")
	void testReadNewExpenditureItem_EventCodeRequired_NotSet() {
		// Given: テストユーザ、対象年月、支出項目選択フォーム（イベント必須なのに未設定）
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";

		ExpenditureSelectItemForm selectForm = new ExpenditureSelectItemForm();
		selectForm.setSisyutuItemCode("0001");
		selectForm.setEventCode(""); // イベントコード未設定
		selectForm.setEventCodeRequired(true); // イベント必須

		List<IncomeRegistItem> incomeList = createEmptyIncomeRegistItemList();
		List<ExpenditureRegistItem> expenditureList = createTestExpenditureRegistItemList();

		// When/Then: イベント必須なのに未設定の場合、例外がスローされる
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> useCase.readNewExpenditureItem(
				user, targetYearMonth, selectForm, incomeList, expenditureList)
		);

		// Then: エラーメッセージを確認
		assertTrue(exception.getMessage().contains("イベント情報を必須選択になっていますが"));
		assertTrue(exception.getMessage().contains("イベントコードが空です"));
	}

	// ========================================
	// 正常系テスト - execExpenditureAction
	// ========================================

	/**
	 *<pre>
	 * テスト⑦：正常系：支出新規追加_セッションへの追加確認
	 *
	 * 【検証内容】
	 * ・セッションの支出リストに1件追加されること（元のサイズ＋1）
	 * ・追加された支出のデータタイプが新規(new)、アクションが追加(add)であること
	 * ・支出コードがyyyyMMddHHmmssSSS形式の17桁自動生成コードであること
	 * ・支出項目コード・イベントコード(0001)・支出名・支出区分・詳細・支払日・金額が正しく設定されること
	 * ・完了メッセージに「仮登録しました」・支出名・金額(7,500円)が含まれること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支出新規追加_セッションへの追加確認")
	void testExecExpenditureAction_Add_NormalCase() {
		// Given: テストユーザ、対象年月、入力フォーム（新規追加）、セッションの支出リスト
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";

		ExpenditureItemForm inputForm = new ExpenditureItemForm();
		inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		inputForm.setSisyutuItemCode("0061"); // イベント系支出項目（コミケ）
		inputForm.setEventCode("0001"); // テストイベント（4桁の具体的なコード値を設定）
		inputForm.setExpenditureName("新規追加テスト支出");
		inputForm.setExpenditureKubun("1"); // 無駄遣いなし
		inputForm.setExpenditureDetailContext("新規追加テスト詳細");
		inputForm.setSiharaiDate(LocalDate.of(2025, 11, 28));
		inputForm.setPaymentMethodCode("001");
		inputForm.setExpenditureKingaku(7500);
		inputForm.setClearStartFlg(false);

		List<ExpenditureRegistItem> expenditureList = createTestExpenditureRegistItemList();
		int originalSize = expenditureList.size();

		// When: 支出を新規追加
		IncomeAndExpenditureRegistResponse response = useCase.execExpenditureAction(
			user, targetYearMonth, inputForm, expenditureList);

		// Then: レスポンスが正しく返却される
		assertNotNull(response);

		// Then: 支出リストに1件追加されている
		List<ExpenditureRegistItem> resultList = response.getExpenditureRegistItemList();
		assertNotNull(resultList);
		assertEquals(originalSize + 1, resultList.size());

		// Then: 追加された支出情報を確認
		ExpenditureRegistItem addedItem = resultList.get(resultList.size() - 1);
		assertEquals(MyHouseholdAccountBookContent.DATA_TYPE_NEW, addedItem.getDataType());
		assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, addedItem.getAction());
		assertEquals(17, addedItem.getExpenditureCode().length()); // 自動生成された支出コード(yyyyMMddHHmmssSSS形式=17桁)
		assertEquals("0061", addedItem.getExpenditureItemCode());
		assertEquals("0001", addedItem.getEventCode()); // イベントコードが設定されていること
		assertEquals("新規追加テスト支出", addedItem.getExpenditureName());
		assertEquals("1", addedItem.getExpenditureCategory());
		assertEquals("新規追加テスト詳細", addedItem.getExpenditureDetailContext());
		assertEquals("28", addedItem.getSiharaiDate()); // 日のみ
		assertEquals(new BigDecimal("7500.00"), addedItem.getExpenditureKingaku());
		assertFalse(addedItem.isClearStartFlg());

		// Then: 完了メッセージを確認
		assertFalse(response.getMessagesList().isEmpty());
		assertTrue(response.getMessagesList().get(0).contains("仮登録しました"));
		assertTrue(response.getMessagesList().get(0).contains("新規追加テスト支出"));
		assertTrue(response.getMessagesList().get(0).contains("7,500円"));
	}

	/**
	 *<pre>
	 * テスト⑧：正常系：支出更新_セッションの更新確認
	 *
	 * 【検証内容】
	 * ・支出リストのサイズが変わらないこと
	 * ・指定した支出コードの支出名・支出区分・詳細・支払日・金額が更新されること
	 * ・完了メッセージに「仮更新しました」・更新後支出名・金額(6,000円)が含まれること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支出更新_セッションの更新確認")
	void testExecExpenditureAction_Update_NormalCase() {
		// Given: テストユーザ、対象年月、入力フォーム（更新）、セッションの支出リスト
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";

		ExpenditureItemForm inputForm = new ExpenditureItemForm();
		inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		inputForm.setExpenditureCode("20260125120000001"); // 既存の支出コード
		inputForm.setSisyutuItemCode("0037"); // 電気代（セッションデータと同一）
		inputForm.setEventCode("");
		inputForm.setExpenditureName("更新後テスト電気代");
		inputForm.setExpenditureKubun("2"); // 無駄遣いBに変更
		inputForm.setExpenditureDetailContext("更新後テスト詳細");
		inputForm.setSiharaiDate(LocalDate.of(2025, 11, 30));
		inputForm.setPaymentMethodCode("001"); // セッションデータと同一(値を変更しない)
		inputForm.setExpenditureKingaku(6000);
		inputForm.setClearStartFlg(false); // 支払金額の0円開始設定フラグは変更不可の項目のため、元の値(false)を設定

		List<ExpenditureRegistItem> expenditureList = createTestExpenditureRegistItemList();
		int originalSize = expenditureList.size();

		// When: 支出を更新
		IncomeAndExpenditureRegistResponse response = useCase.execExpenditureAction(
			user, targetYearMonth, inputForm, expenditureList);

		// Then: レスポンスが正しく返却される
		assertNotNull(response);

		// Then: 支出リストのサイズは変わらない
		List<ExpenditureRegistItem> resultList = response.getExpenditureRegistItemList();
		assertNotNull(resultList);
		assertEquals(originalSize, resultList.size());

		// Then: 更新された支出情報を確認
		ExpenditureRegistItem updatedItem = resultList.get(0);
		assertEquals("20260125120000001", updatedItem.getExpenditureCode());
		assertEquals("更新後テスト電気代", updatedItem.getExpenditureName());
		assertEquals("2", updatedItem.getExpenditureCategory());
		assertEquals("更新後テスト詳細", updatedItem.getExpenditureDetailContext());
		assertEquals("30", updatedItem.getSiharaiDate());
		assertEquals(new BigDecimal("6000.00"), updatedItem.getExpenditureKingaku());

		// Then: 完了メッセージを確認
		assertFalse(response.getMessagesList().isEmpty());
		assertTrue(response.getMessagesList().get(0).contains("仮更新しました"));
		assertTrue(response.getMessagesList().get(0).contains("更新後テスト電気代"));
		assertTrue(response.getMessagesList().get(0).contains("6,000円"));
	}

	/**
	 *<pre>
	 * テスト⑧-2：異常系：支出更新_通常行の支払方法をシステム予約値に変更しようとすると例外になること
	 *
	 * 【検証内容】
	 * ・元々システム予約値でなかった行(paymentMethodCode=001)に対し、支払方法コード999を送信して更新しようとした場合、
	 *   MyHouseholdAccountBookRuntimeExceptionがスローされること(リクエスト改変による不正なデータ更新のガード)
	 * ・例外メッセージに「システム予約値に変更することはできません」が含まれること
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：支出更新_通常行の支払方法をシステム予約値に変更しようとすると例外になること")
	void testExecExpenditureAction_Update_PaymentMethodCodeTamperedToReserved() {
		// Given: テストユーザ、対象年月、入力フォーム(支払方法=999)、セッションの支出リスト(対象行は001)
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";

		ExpenditureItemForm inputForm = new ExpenditureItemForm();
		inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		inputForm.setExpenditureCode("20260125120000001"); // 既存の支出コード(セッション上はpaymentMethodCode=001)
		inputForm.setSisyutuItemCode("0037");
		inputForm.setEventCode("");
		inputForm.setExpenditureName("更新後テスト電気代");
		inputForm.setExpenditureKubun("1");
		inputForm.setExpenditureDetailContext("更新後テスト詳細");
		inputForm.setSiharaiDate(LocalDate.of(2025, 11, 30));
		inputForm.setPaymentMethodCode("999"); // リクエスト改変によりシステム予約値を送信
		inputForm.setExpenditureKingaku(6000);
		inputForm.setClearStartFlg(false);

		List<ExpenditureRegistItem> expenditureList = createTestExpenditureRegistItemList();

		// When/Then: 支払方法をシステム予約値に変更しようとすると例外がスローされる
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> useCase.execExpenditureAction(user, targetYearMonth, inputForm, expenditureList)
		);

		// Then: エラーメッセージを確認
		assertTrue(exception.getMessage().contains("システム予約値に変更することはできません"));
		assertTrue(exception.getMessage().contains("expenditureCode=20260125120000001"));
	}

	/**
	 *<pre>
	 * テスト⑧-3：正常系：支出更新_元々システム予約値だった行を変更せず再送信しても例外にならないこと
	 *
	 * 【検証内容】
	 * ・固定費から引き継いだ買い物集計8項目のような、元々paymentMethodCode=999の行を
	 *   disabled+hidden併記により現在値のまま再送信(値が変化しないケース)した場合は、例外にならず更新できること
	 *   (回帰防止テスト。新しいガード節がこの既存の正当な操作を妨げないことの確認)
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支出更新_元々システム予約値だった行を変更せず再送信しても例外にならないこと")
	void testExecExpenditureAction_Update_ExistingReservedPaymentMethodCodeUnchanged() {
		// Given: テストユーザ、対象年月、セッションに支払方法=999の既存行を設定
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";
		String expenditureCode = "20260125120000099";
		List<ExpenditureRegistItem> expenditureList = new ArrayList<>();
		expenditureList.add(expenditureRegistItemHelper(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW, MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				expenditureCode, "0051", "", "テスト食費(固定費由来)", "1", "テスト詳細", "26",
				"999", new BigDecimal("10000.00"), false));

		// When: 他項目(詳細)のみ変更し、支払方法は999のまま(disabled+hidden併記のため)再送信
		ExpenditureItemForm inputForm = new ExpenditureItemForm();
		inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		inputForm.setExpenditureCode(expenditureCode);
		inputForm.setSisyutuItemCode("0051");
		inputForm.setEventCode("");
		inputForm.setExpenditureName("テスト食費(固定費由来・更新)");
		inputForm.setExpenditureKubun("1");
		inputForm.setExpenditureDetailContext("更新後テスト詳細");
		inputForm.setSiharaiDate(LocalDate.of(2025, 11, 26));
		inputForm.setPaymentMethodCode("999"); // 変更なし(disabled+hidden併記による再送信)
		inputForm.setExpenditureKingaku(10000);
		inputForm.setClearStartFlg(false);

		// Then: 例外がスローされず、正常に更新できる
		IncomeAndExpenditureRegistResponse response = useCase.execExpenditureAction(
			user, targetYearMonth, inputForm, expenditureList);
		assertNotNull(response);
		assertEquals("999", response.getExpenditureRegistItemList().get(0).getPaymentMethodCode());
	}

	/**
	 *<pre>
	 * テスト⑨：正常系：支出削除_新規データタイプ_セッションから完全削除
	 *
	 * 【検証内容】
	 * ・新規データタイプの支出を削除した場合、セッションリストから完全に削除されること（サイズ-1）
	 * ・削除対象の支出コードがリストに存在しないこと
	 * ・完了メッセージに「仮削除しました」・支出名が含まれること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支出削除_新規データタイプ_セッションから完全削除")
	void testExecExpenditureAction_Delete_NewDataType() {
		// Given: テストユーザ、対象年月、入力フォーム（削除）、セッションの支出リスト（新規データタイプ）
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";

		ExpenditureItemForm inputForm = new ExpenditureItemForm();
		inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_DELETE);
		inputForm.setExpenditureCode("20260125120000001"); // 削除対象
		inputForm.setExpenditureName("テスト電気代");
		inputForm.setExpenditureKingaku(5000);

		List<ExpenditureRegistItem> expenditureList = createTestExpenditureRegistItemList();
		int originalSize = expenditureList.size();

		// When: 支出を削除
		IncomeAndExpenditureRegistResponse response = useCase.execExpenditureAction(
			user, targetYearMonth, inputForm, expenditureList);

		// Then: レスポンスが正しく返却される
		assertNotNull(response);

		// Then: 支出リストから1件削除されている（新規データタイプは完全削除）
		List<ExpenditureRegistItem> resultList = response.getExpenditureRegistItemList();
		assertNotNull(resultList);
		assertEquals(originalSize - 1, resultList.size());

		// Then: 削除対象の支出コードがリストに存在しない
		boolean exists = resultList.stream()
			.anyMatch(item -> "20260125120000001".equals(item.getExpenditureCode()));
		assertFalse(exists);

		// Then: 完了メッセージを確認
		assertFalse(response.getMessagesList().isEmpty());
		assertTrue(response.getMessagesList().get(0).contains("仮削除しました"));
		assertTrue(response.getMessagesList().get(0).contains("テスト電気代"));
	}

	/**
	 *<pre>
	 * テスト⑩：正常系：支出削除_ロードデータタイプ_削除アクション設定
	 *
	 * 【検証内容】
	 * ・ロードデータタイプの支出を削除した場合、セッションリストのサイズが変わらないこと
	 * ・削除対象の支出がアクション=削除(delete)・データタイプ=ロード(load)でリストに残ること
	 * ・完了メッセージに「仮削除しました」が含まれること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支出削除_ロードデータタイプ_削除アクション設定")
	void testExecExpenditureAction_Delete_LoadDataType() {
		// Given: テストユーザ、対象年月、入力フォーム（削除）、セッションの支出リスト（ロードデータタイプ）
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";

		ExpenditureItemForm inputForm = new ExpenditureItemForm();
		inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_DELETE);
		inputForm.setExpenditureCode("001"); // 削除対象（ロードデータ）
		inputForm.setExpenditureName("DB登録済み電気代");
		inputForm.setExpenditureKingaku(8000);

		List<ExpenditureRegistItem> expenditureList = createTestExpenditureRegistItemListWithLoadType();
		int originalSize = expenditureList.size();

		// When: 支出を削除
		IncomeAndExpenditureRegistResponse response = useCase.execExpenditureAction(
			user, targetYearMonth, inputForm, expenditureList);

		// Then: レスポンスが正しく返却される
		assertNotNull(response);

		// Then: 支出リストのサイズは変わらない（削除アクション設定で残る）
		List<ExpenditureRegistItem> resultList = response.getExpenditureRegistItemList();
		assertNotNull(resultList);
		assertEquals(originalSize, resultList.size());

		// Then: 削除対象の支出がアクション=削除で存在する
		ExpenditureRegistItem deletedItem = resultList.stream()
			.filter(item -> "001".equals(item.getExpenditureCode()))
			.findFirst()
			.orElse(null);
		assertNotNull(deletedItem);
		assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_DELETE, deletedItem.getAction());
		assertEquals(MyHouseholdAccountBookContent.DATA_TYPE_LOAD, deletedItem.getDataType());

		// Then: 完了メッセージを確認
		assertFalse(response.getMessagesList().isEmpty());
		assertTrue(response.getMessagesList().get(0).contains("仮削除しました"));
	}

	// ========================================
	// 異常系テスト - execExpenditureAction
	// ========================================

	/**
	 *<pre>
	 * テスト⑪：異常系：支出更新_支出コード不存在エラー
	 *
	 * 【検証内容】
	 * ・セッションに存在しない支出コードで更新しようとした場合、MyHouseholdAccountBookRuntimeExceptionがスローされること
	 * ・例外メッセージに「更新対象の支出情報がセッションに存在しません」が含まれること
	 * ・例外メッセージに指定した支出コード値が含まれること
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：支出更新_支出コード不存在エラー")
	void testExecExpenditureAction_Update_ExpenditureCodeNotFound() {
		// Given: テストユーザ、対象年月、入力フォーム（更新、存在しない支出コード）、セッションの支出リスト
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";

		ExpenditureItemForm inputForm = new ExpenditureItemForm();
		inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		inputForm.setExpenditureCode("99999999999999999"); // 存在しない支出コード
		inputForm.setSisyutuItemCode("0001");
		inputForm.setExpenditureName("更新テスト");
		inputForm.setExpenditureKubun("1");
		inputForm.setExpenditureKingaku(10000);

		List<ExpenditureRegistItem> expenditureList = createTestExpenditureRegistItemList();

		// When/Then: 存在しない支出コードで更新すると例外がスローされる
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> useCase.execExpenditureAction(user, targetYearMonth, inputForm, expenditureList)
		);

		// Then: エラーメッセージを確認
		assertTrue(exception.getMessage().contains("更新対象の支出情報がセッションに存在しません"));
		assertTrue(exception.getMessage().contains("expenditureCode=99999999999999999"));
	}

	/**
	 *<pre>
	 * テスト⑫：異常系：支出削除_支出コード不存在エラー
	 *
	 * 【検証内容】
	 * ・セッションに存在しない支出コードで削除しようとした場合、MyHouseholdAccountBookRuntimeExceptionがスローされること
	 * ・例外メッセージに「更新対象の支出情報がセッションに存在しません」が含まれること
	 * ・例外メッセージに指定した支出コード値が含まれること
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：支出削除_支出コード不存在エラー")
	void testExecExpenditureAction_Delete_ExpenditureCodeNotFound() {
		// Given: テストユーザ、対象年月、入力フォーム（削除、存在しない支出コード）、セッションの支出リスト
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";

		ExpenditureItemForm inputForm = new ExpenditureItemForm();
		inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_DELETE);
		inputForm.setExpenditureCode("99999999999999999"); // 存在しない支出コード
		inputForm.setExpenditureName("削除テスト");
		inputForm.setExpenditureKingaku(10000);

		List<ExpenditureRegistItem> expenditureList = createTestExpenditureRegistItemList();

		// When/Then: 存在しない支出コードで削除すると例外がスローされる
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> useCase.execExpenditureAction(user, targetYearMonth, inputForm, expenditureList)
		);

		// Then: エラーメッセージを確認
		assertTrue(exception.getMessage().contains("更新対象の支出情報がセッションに存在しません"));
		assertTrue(exception.getMessage().contains("expenditureCode=99999999999999999"));
	}

	// ========================================
	// 正常系テスト - 支払方法・引落先口座(Feature1.03-dev1)
	// ========================================

	/**
	 *<pre>
	 * テスト⑬：正常系：支払方法選択ボックスにシステム予約値・無効化済み支払方法が含まれないこと
	 *
	 * 【検証内容】
	 * ・支払方法選択ボックス(findSelectableByUserId()ベース)に、有効な支払方法(001・002)が含まれること
	 * ・システム予約値(999)が含まれないこと
	 * ・無効化済み支払方法(003)が含まれないこと
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支払方法選択ボックスにシステム予約値・無効化済み支払方法が含まれないこと")
	void testCreateExpenditureItemFormResponse_PaymentMethodSelectListExcludesReservedAndDisabled() {
		// Given: テストユーザ、対象年月、支出コード、セッションの収入・支出リスト
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";
		String expenditureCode = "20260125120000001";
		List<IncomeRegistItem> incomeList = createEmptyIncomeRegistItemList();
		List<ExpenditureRegistItem> expenditureList = createTestExpenditureRegistItemList();

		// When: 更新画面を表示
		IncomeAndExpenditureRegistResponse response = useCase.readExpenditureUpdateSelect(
			user, targetYearMonth, expenditureCode, incomeList, expenditureList);

		// Then: 支払方法選択ボックスの選択値一覧
		List<String> values = response.getPaymentMethodSelectList().getOptionList().stream()
				.map(item -> item.getValue())
				.toList();
		assertTrue(values.contains("001"));
		assertTrue(values.contains("002"));
		assertFalse(values.contains("999"));
		assertFalse(values.contains("003"));
	}

	/**
	 *<pre>
	 * テスト⑭：正常系：支出一覧の支払方法名・引落先口座名が解決されること
	 *
	 * 【検証内容】
	 * ・銀行口座を持たない支払方法(001:現金)の支払方法名が「現金」、引落先口座名が「－」になること
	 * ・銀行口座を持つ支払方法(002:口座振替)の支払方法名・引落先口座名がそれぞれ解決されること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支出一覧の支払方法名・引落先口座名が解決されること")
	void testReadExpenditureUpdateSelect_ExpenditureListInfoResolvesPaymentMethodAndBankAccountName() {
		// Given: テストユーザ、対象年月、支出コード、セッションの収入・支出リスト
		// (createTestExpenditureRegistItemListは2件とも支払方法コード="001")
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";
		String expenditureCode = "20260125120000001";
		List<IncomeRegistItem> incomeList = createEmptyIncomeRegistItemList();
		List<ExpenditureRegistItem> expenditureList = createTestExpenditureRegistItemList();
		// 2件目(食費)の支払方法を口座振替(002)に変更し、銀行口座名の解決も確認する
		ExpenditureRegistItem foodItem = expenditureList.get(1);
		expenditureList.set(1, expenditureRegistItemHelper(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW, MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				foodItem.getExpenditureCode(), foodItem.getExpenditureItemCode(), foodItem.getEventCode(),
				foodItem.getExpenditureName(), foodItem.getExpenditureCategory(), foodItem.getExpenditureDetailContext(),
				foodItem.getSiharaiDate(), "002", foodItem.getExpenditureKingaku(), foodItem.isClearStartFlg()));

		// When: 更新画面を表示
		IncomeAndExpenditureRegistResponse response = useCase.readExpenditureUpdateSelect(
			user, targetYearMonth, expenditureCode, incomeList, expenditureList);

		// Then: 1件目(電気代・現金)は支払方法名「現金」・引落先口座名「－」
		var electricItem = response.getExpenditureListInfo().stream()
				.filter(item -> "20260125120000001".equals(item.getExpenditureCode())).findFirst().orElseThrow();
		assertEquals("現金", electricItem.getPaymentMethodName());
		assertEquals("－", electricItem.getBankAccountName());

		// Then: 2件目(食費・口座振替)は支払方法名・引落先口座名がそれぞれ解決される
		var foodListItem = response.getExpenditureListInfo().stream()
				.filter(item -> "20260125120000002".equals(item.getExpenditureCode())).findFirst().orElseThrow();
		assertEquals("テスト銀行 口座振替", foodListItem.getPaymentMethodName());
		assertEquals("テスト銀行", foodListItem.getBankAccountName());
	}

	/**
	 *<pre>
	 * テスト⑮：正常系：支払方法コードがシステム予約値の支出情報を表示した場合、フォームの値が維持されること
	 *
	 * 【検証内容】
	 * ・支払方法コードがシステム予約値(999)の支出情報を更新画面で表示した場合、
	 *   フォームの支払方法コードが999のまま維持されること(disabled表示の前提条件)
	 * ・支払方法選択ボックスには999が含まれないこと(選択肢に現在値が含まれない状態であることの確認)
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支払方法コードがシステム予約値の支出情報を表示した場合、フォームの値が維持されること")
	void testReadExpenditureUpdateSelect_SystemReservedPaymentMethodCodeIsKept() {
		// Given: テストユーザ、対象年月、支払方法コードがシステム予約値(999)の支出情報をセッションに設定
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";
		String expenditureCode = "20260125120000099";
		List<IncomeRegistItem> incomeList = createEmptyIncomeRegistItemList();
		List<ExpenditureRegistItem> expenditureList = new ArrayList<>();
		expenditureList.add(expenditureRegistItemHelper(
				MyHouseholdAccountBookContent.DATA_TYPE_NEW, MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
				expenditureCode, "0051", "", "テスト食費(固定費由来)", "1", "テスト詳細", "26",
				"999", new BigDecimal("10000.00"), false));

		// When: 更新画面を表示
		IncomeAndExpenditureRegistResponse response = useCase.readExpenditureUpdateSelect(
			user, targetYearMonth, expenditureCode, incomeList, expenditureList);

		// Then: フォームの支払方法コードは999のまま維持される
		assertEquals("999", response.getExpenditureItemForm().getPaymentMethodCode());
		// Then: 選択肢には999が含まれない(＝テンプレート側でdisabled表示になる前提条件を満たす)
		List<String> values = response.getPaymentMethodSelectList().getOptionList().stream()
				.map(item -> item.getValue())
				.toList();
		assertFalse(values.contains("999"));
		// Then: disabled表示の<option>に出す支払方法名はコード値ではなく解決済み名称「－」(システム予約値は解決不可のため)
		assertEquals("－", response.getPaymentMethodCurrentName());
	}

	/**
	 *<pre>
	 * テスト⑯：正常系：支払方法コードが無効化済みの支出情報を表示した場合、disabled表示の名称が解決されること
	 *
	 * 【検証内容】
	 * ・支払方法コードが無効化済み(ENABLE_FLG=false。ただしシステム予約値ではない)の支出情報を表示した場合、
	 *   disabled表示の<option>に出す支払方法名が、コード値ではなく実際の支払方法名で解決されること
	 *   (無効化済みでも登録名自体は残っているため、一覧表示と同じ名称が表示される)
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：支払方法コードが無効化済みの支出情報を表示した場合、disabled表示の名称が解決されること")
	void testReadExpenditureUpdateSelect_DisabledPaymentMethodCurrentNameIsResolved() {
		// Given: テストユーザ、対象年月、支払方法コードが無効化済み(003)の支出情報をセッションに設定
		LoginUserInfo user = createLoginUser();
		String targetYearMonth = "202511";
		String expenditureCode = "20260125120000098";
		List<IncomeRegistItem> incomeList = createEmptyIncomeRegistItemList();
		List<ExpenditureRegistItem> expenditureList = new ArrayList<>();
		expenditureList.add(expenditureRegistItemHelper(
				MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE,
				expenditureCode, "0051", "", "テスト食費(無効化済み支払方法)", "1", "テスト詳細", "26",
				"003", new BigDecimal("3000.00"), false));

		// When: 更新画面を表示
		IncomeAndExpenditureRegistResponse response = useCase.readExpenditureUpdateSelect(
			user, targetYearMonth, expenditureCode, incomeList, expenditureList);

		// Then: 選択肢には無効化済みの003が含まれない(＝テンプレート側でdisabled表示になる前提条件を満たす)
		List<String> values = response.getPaymentMethodSelectList().getOptionList().stream()
				.map(item -> item.getValue())
				.toList();
		assertFalse(values.contains("003"));
		// Then: disabled表示の<option>に出す支払方法名は実際の名称(一覧表示と同じ解決結果)
		assertEquals("無効化済み支払方法", response.getPaymentMethodCurrentName());
	}
}
