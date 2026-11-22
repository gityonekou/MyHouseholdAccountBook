/**
 * 銀行口座情報管理ユースケース(BankAccountInfoManageUseCase.java)のインテグレーションテストケースクラスです。
 *
 * <pre>
 * BankAccountInfoManageUseCase の以下メソッドをテストします。
 * service:BankAccountInfoManageUseCase⇔レポジトリー:Repository⇔DB
 *
 * [対象メソッド]
 * 1. readBankAccountInfo(user)       - 情報管理(銀行口座)画面の表示情報取得(初期表示)
 * 2. readBankAccountInfo(user,code)  - 情報管理(銀行口座)画面の表示情報取得(対象選択時)
 * 3. readUpdateBindingErrorSetInfo   - バリデーションチェックエラー時の画面表示情報取得
 * 4. execAction                      - 銀行口座情報の追加・更新
 * 5. execToggleEnableFlg             - 銀行口座情報の有効/無効切替
 *
 * [テストシナリオ]
 * readBankAccountInfo(user):
 *   1. 正常系: 検索結果なし(0件)
 *   2. 正常系: 検索結果3件
 * readBankAccountInfo(user,code):
 *   3. 正常系: 指定した銀行口座コードの表示情報取得
 *   4. 異常系: 存在しない銀行口座コード
 * readUpdateBindingErrorSetInfo:
 *   5. 正常系: バリデーションエラー時の表示情報取得
 * execAction:
 *   6. 正常系: 新規追加(表示順自動採番)
 *   7. 正常系: 新規追加(表示順明示指定、既存データの表示順シフト)
 *   8. 正常系: 更新(表示順変更なし)
 *   9. 正常系: 更新(表示順変更あり、既存データの表示順シフト)
 *   10. 異常系: 旧表示順(更新比較用)が未設定
 *   11. 異常系: 未定義のアクション
 * execToggleEnableFlg:
 *   12. 正常系: 有効→無効に切替
 *   13. 正常系: 無効→有効に切替
 *   14. 異常系: 存在しない銀行口座コード
 *
 * [テストデータ]
 * ReadBankAccountInfoQueryNotFoundTest.sql    : 銀行口座情報0件のユーザ
 * ReadBankAccountInfoQueryResultThreeTest.sql : 銀行口座情報3件(01:有効、02:有効、03:無効)のユーザ
 * </pre>
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/18 : 1.00.00  feature-1.03-dev1   新規作成
 * 2026/10/18 : 1.00.01  feature-1.03-dev1   execToggleEnableFlgのテスト追加、from()型安全性対応に伴うlistItemHelper()追加
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage.bankaccount;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountCode;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountMemo;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountSort;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankName;
import com.yonetani.webapp.accountbook.domain.type.common.EnableFlg;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.bankaccount.BankAccountReadWriteDto;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.BankAccountInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.BankAccountInfoManageResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.BankAccountInfoManageResponse.BankAccountListItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

/**
 *<pre>
 * 銀行口座情報管理ユースケース(BankAccountInfoManageUseCase.java)のインテグレーションテストケースクラスです。
 * 以下範囲のテストを実施します。
 * service:BankAccountInfoManageUseCase⇔レポジトリー:Repository⇔DB
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("銀行口座情報管理ユースケースのUseCaseテスト（統合テスト）")
class BankAccountInfoManageUseCaseIntegrationTest {
	@Autowired
	private BankAccountInfoManageUseCase service;
	@Autowired
	private NamedParameterJdbcTemplate namedParamTemplete;

	private final LoginUserInfo TEST_USER = LoginUserInfo.from("TESTUSER001", "テストユーザ01");

	/**
	 *<pre>
	 * テスト1：正常系：readBankAccountInfo_検索結果なし(0件)
	 *
	 * 【検証内容】
	 * ・銀行口座情報が0件の場合、「銀行口座情報取得結果が0件です。」のメッセージが設定されること
	 * ・銀行口座一覧情報の明細データが0件であること
	 * ・入力フォームのアクションが新規登録(ADD)であること
	 * ・入力フォームの銀行口座コード・銀行名・表示順・表示順(更新比較用)がnullであること
	 *</pre>
	 */
	@Test
	@DisplayName("1 正常系：readBankAccountInfo_検索結果なし(0件)")
	@Sql(scripts = "ReadBankAccountInfoQueryNotFoundTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadBankAccountInfoQueryNotFound() {
		BankAccountInfoManageResponse res = service.readBankAccountInfo(TEST_USER);
		if(res.getMessagesList().size() != 1) {
			fail("検索結果なしのレスポンスメッセージが設定されていない");
		} else {
			assertEquals("銀行口座情報取得結果が0件です。", res.getMessagesList().get(0), "検索結果なしのメッセージが設定されていること");
		}

		ModelMap modelMap = res.build().getModelMap();
		@SuppressWarnings("unchecked")
		List<BankAccountListItem> bankAccountList = (List<BankAccountListItem>)modelMap.getAttribute("bankAccountList");
		assertNotNull(bankAccountList, "銀行口座一覧情報の明細データがnullでないこと");
		assertEquals(0, bankAccountList.size(), "銀行口座一覧情報の明細データが0件であること");

		BankAccountInfoForm form = (BankAccountInfoForm)modelMap.getAttribute("bankAccountInfoForm");
		assertNotNull(form, "銀行口座情報入力フォームがnullでないこと");
		assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, form.getAction(), "アクションが新規登録であること");
		assertNull(form.getBankAccountCode(), "銀行口座コードがnullであること");
		assertNull(form.getBankName(), "銀行名がnullであること");
		assertNull(form.getBankAccountSort(), "表示順がnullであること");
		assertNull(form.getBankAccountSortBefore(), "表示順(更新比較用)がnullであること");
	}

	/**
	 *<pre>
	 * テスト2：正常系：readBankAccountInfo_検索結果3件
	 *
	 * 【検証内容】
	 * ・エラーメッセージが設定されていないこと
	 * ・銀行口座一覧情報の明細データが3件であること
	 * ・各明細データ(コード・銀行名・メモ・表示順・有効/無効フラグ)が登録値と一致すること
	 *</pre>
	 */
	@Test
	@DisplayName("2 正常系：readBankAccountInfo_検索結果3件")
	@Sql(scripts = "ReadBankAccountInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadBankAccountInfoQueryResultThree() {
		BankAccountInfoManageResponse res = service.readBankAccountInfo(TEST_USER);
		assertEquals(0, res.getMessagesList().size(), "エラーメッセージが設定されていないこと");

		ModelMap modelMap = res.build().getModelMap();
		@SuppressWarnings("unchecked")
		List<BankAccountListItem> bankAccountList = (List<BankAccountListItem>)modelMap.getAttribute("bankAccountList");
		assertNotNull(bankAccountList, "銀行口座一覧情報の明細データがnullでないこと");
		assertEquals(3, bankAccountList.size(), "銀行口座一覧情報の明細データが3件であること");
		assertEquals(listItemHelper("01", "テストユーザ銀行０１", "生活費口座", "01", true), bankAccountList.get(0));
		assertEquals(listItemHelper("02", "テストユーザ銀行０２", null, "02", true), bankAccountList.get(1));
		assertEquals(listItemHelper("03", "テストユーザ銀行０３", "無効口座", "03", false), bankAccountList.get(2));
	}

	/**
	 *<pre>
	 * テスト3：正常系：readBankAccountInfo(code)_指定した銀行口座コードの表示情報取得
	 *
	 * 【検証内容】
	 * ・「銀行名「○○」の銀行口座を更新します。」のメッセージが設定されること
	 * ・入力フォームのアクションが更新(UPDATE)であること
	 * ・入力フォームの各項目に選択した銀行口座の値が設定されること(表示順(更新比較用)含む)
	 *</pre>
	 */
	@Test
	@DisplayName("3 正常系：readBankAccountInfo(code)_指定した銀行口座コードの表示情報取得")
	@Sql(scripts = "ReadBankAccountInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadBankAccountInfoFromBankAccountCode() {
		BankAccountInfoManageResponse res = service.readBankAccountInfo(TEST_USER, "02");
		if(res.getMessagesList().size() != 1) {
			fail("更新対象銀行名のレスポンスメッセージが設定されていない");
		} else {
			assertEquals("銀行名「テストユーザ銀行０２」の銀行口座を更新します。", res.getMessagesList().get(0));
		}

		ModelMap modelMap = res.build().getModelMap();
		BankAccountInfoForm form = (BankAccountInfoForm)modelMap.getAttribute("bankAccountInfoForm");
		assertNotNull(form, "銀行口座情報入力フォームがnullでないこと");
		assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE, form.getAction());
		assertEquals("02", form.getBankAccountCode());
		assertEquals("テストユーザ銀行０２", form.getBankName());
		assertNull(form.getBankAccountMemo());
		assertEquals(Integer.valueOf(2), form.getBankAccountSort());
		assertEquals("02", form.getBankAccountSortBefore());
		assertEquals(Boolean.TRUE, form.getEnableFlg());
	}

	/**
	 *<pre>
	 * テスト4：異常系：readBankAccountInfo(code)_存在しない銀行口座コード
	 *
	 * 【検証内容】
	 * ・存在しない銀行口座コードを指定した場合、MyHouseholdAccountBookRuntimeExceptionが発生すること
	 * ・例外メッセージに指定した銀行口座コードが含まれること
	 *</pre>
	 */
	@Test
	@DisplayName("4 異常系：readBankAccountInfo(code)_存在しない銀行口座コード")
	@Sql(scripts = "ReadBankAccountInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadBankAccountInfoNotFoundFromBankAccountCode() {
		MyHouseholdAccountBookRuntimeException ex = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> service.readBankAccountInfo(TEST_USER, "99"));
		assertEquals(
				"更新対象の銀行口座情報が存在しません。管理者に問い合わせてください。bankAccountCode:99",
				ex.getLocalizedMessage());
	}

	/**
	 *<pre>
	 * テスト5：正常系：readUpdateBindingErrorSetInfo_バリデーションエラー時の表示情報取得
	 *
	 * 【検証内容】
	 * ・エラーメッセージ(addMessage)は設定されないこと(バリデーションエラー自体はth:errorsで表示するため)
	 * ・transactionSuccessFullがfalseであること(登録・更新は未実行)
	 *</pre>
	 */
	@Test
	@DisplayName("5 正常系：readUpdateBindingErrorSetInfo_バリデーションエラー時の表示情報取得")
	@Sql(scripts = "ReadBankAccountInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadUpdateBindingErrorSetInfo() {
		BankAccountInfoForm form = inputUpdateForm("02", "03");
		BankAccountInfoManageResponse res = service.readUpdateBindingErrorSetInfo(TEST_USER, form);
		assertEquals(0, res.getMessagesList().size());
		assertFalse(res.isTransactionSuccessFull());
	}

	/**
	 *<pre>
	 * テスト6：正常系：execAction_新規追加(表示順自動採番)
	 *
	 * 【検証内容】
	 * ・「新規銀行口座を追加しました。」のメッセージが設定されること
	 * ・transactionSuccessFullがtrueであること
	 * ・銀行口座テーブルに1件追加され、各項目(コード・銀行名・メモ・表示順・有効/無効フラグ)が入力値通りであること
	 * ・表示順未指定の場合、登録件数+1(=1)が自動採番されること
	 *</pre>
	 */
	@Test
	@DisplayName("6 正常系：execAction_新規追加(表示順自動採番)")
	@Sql(scripts = "ReadBankAccountInfoQueryNotFoundTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecAddActionAutoSort() {
		BankAccountInfoForm form = inputAddForm(null);

		BankAccountInfoManageResponse res = service.execAction(TEST_USER, form);
		if(res.getMessagesList().size() != 1) {
			fail("新規銀行口座追加のレスポンスメッセージが設定されていない");
		} else {
			assertEquals("新規銀行口座を追加しました。[code:01]" + form.getBankName(), res.getMessagesList().get(0));
		}
		assertTrue(res.isTransactionSuccessFull());

		List<BankAccountReadWriteDto> resultList = execQueryAllBankAccountList();
		assertEquals(1, resultList.size());
		BankAccountReadWriteDto dto = resultList.get(0);
		assertEquals("TESTUSER001", dto.getUserId());
		assertEquals("01", dto.getBankAccountCode());
		assertEquals("新規テスト銀行", dto.getBankName());
		assertEquals("新規口座メモ", dto.getBankAccountMemo());
		assertEquals("01", dto.getBankAccountSort());
		assertTrue(dto.isEnableFlg());
	}

	/**
	 *<pre>
	 * テスト7：正常系：execAction_新規追加(表示順明示指定、既存データの表示順シフト)
	 *
	 * 【検証内容】
	 * ・transactionSuccessFullがtrueであること
	 * ・新規データが指定した表示順(01)で追加されること
	 * ・割り込み先以降の既存データ(01～03)の表示順が+1シフトされること(02,03,04)
	 *</pre>
	 */
	@Test
	@DisplayName("7 正常系：execAction_新規追加(表示順明示指定、既存データの表示順シフト)")
	@Sql(scripts = "ReadBankAccountInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecAddActionExplicitSortShiftsExisting() {
		BankAccountInfoForm form = inputAddForm("01");

		BankAccountInfoManageResponse res = service.execAction(TEST_USER, form);
		assertTrue(res.isTransactionSuccessFull());

		List<BankAccountReadWriteDto> resultList = execQueryAllBankAccountList();
		assertEquals(4, resultList.size());
		BankAccountReadWriteDto added = resultList.get(3);
		assertEquals("04", added.getBankAccountCode());
		assertEquals("01", added.getBankAccountSort());
		// 既存の01～03は+1シフトされる
		assertEquals("02", resultList.get(0).getBankAccountSort());
		assertEquals("03", resultList.get(1).getBankAccountSort());
		assertEquals("04", resultList.get(2).getBankAccountSort());
	}

	/**
	 *<pre>
	 * テスト8：正常系：execAction_更新(表示順変更なし)
	 *
	 * 【検証内容】
	 * ・「銀行口座を更新しました。」のメッセージが設定されること
	 * ・transactionSuccessFullがtrueであること
	 * ・DB上の対象データ(銀行名・メモ・表示順・有効/無効フラグ)が更新値で反映されること
	 * ・表示順を変更していないため、他データの表示順は変更されないこと
	 *</pre>
	 */
	@Test
	@DisplayName("8 正常系：execAction_更新(表示順変更なし)")
	@Sql(scripts = "ReadBankAccountInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecUpdateActionNoSortChange() {
		BankAccountInfoForm form = inputUpdateForm("02", "02");

		BankAccountInfoManageResponse res = service.execAction(TEST_USER, form);
		if(res.getMessagesList().size() != 1) {
			fail("更新完了のレスポンスメッセージが設定されていない");
		} else {
			assertEquals("銀行口座を更新しました。[code:02]" + form.getBankName(), res.getMessagesList().get(0));
		}
		assertTrue(res.isTransactionSuccessFull());

		Map<String, Object> actualDataMap = namedParamTemplete.queryForMap(
				"SELECT * FROM BANK_ACCOUNT_TABLE WHERE USER_ID=:USER_ID AND BANK_ACCOUNT_CODE=:BANK_ACCOUNT_CODE",
				Map.of("USER_ID", TEST_USER.getUserId().toString(), "BANK_ACCOUNT_CODE", "02"));
		assertEquals("更新後銀行名", actualDataMap.get("BANK_NAME"));
		assertEquals("更新後メモ", actualDataMap.get("BANK_ACCOUNT_MEMO"));
		assertEquals("02", actualDataMap.get("BANK_ACCOUNT_SORT"));
		assertEquals(Boolean.FALSE, actualDataMap.get("ENABLE_FLG"));

		// 他データの表示順が変更されていないこと
		List<BankAccountReadWriteDto> resultList = execQueryAllBankAccountList();
		assertEquals("01", resultList.get(0).getBankAccountSort());
		assertEquals("03", resultList.get(2).getBankAccountSort());
	}

	/**
	 *<pre>
	 * テスト9：正常系：execAction_更新(表示順変更あり、既存データの表示順シフト)
	 *
	 * 【検証内容】
	 * ・transactionSuccessFullがtrueであること
	 * ・口座コード03の表示順を01に変更した場合、変更後～変更前の間の既存データ(01,02)が+1シフトされること(01→02, 02→03, 03→01)
	 *</pre>
	 */
	@Test
	@DisplayName("9 正常系：execAction_更新(表示順変更あり、既存データの表示順シフト)")
	@Sql(scripts = "ReadBankAccountInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecUpdateActionSortChange() {
		BankAccountInfoForm form = inputUpdateForm("03", "01");

		BankAccountInfoManageResponse res = service.execAction(TEST_USER, form);
		assertTrue(res.isTransactionSuccessFull());

		List<BankAccountReadWriteDto> resultList = execQueryAllBankAccountList();
		// 01→02, 02→03, 03→01
		assertEquals("02", resultList.get(0).getBankAccountSort());
		assertEquals("03", resultList.get(1).getBankAccountSort());
		assertEquals("01", resultList.get(2).getBankAccountSort());
	}

	/**
	 *<pre>
	 * テスト10：異常系：execAction_更新時に旧表示順(更新比較用)が未設定
	 *
	 * 【検証内容】
	 * ・表示順(更新比較用)が未設定の場合、MyHouseholdAccountBookRuntimeExceptionが発生すること
	 * ・例外メッセージに不正値であることが示されること
	 *</pre>
	 */
	@Test
	@DisplayName("10 異常系：execAction_更新時に旧表示順(更新比較用)が未設定")
	@Sql(scripts = "ReadBankAccountInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecUpdateSortBeforeEmptyAction() {
		BankAccountInfoForm form = inputUpdateForm("02", "02");
		form.setBankAccountSortBefore(null);

		MyHouseholdAccountBookRuntimeException ex = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> service.execAction(TEST_USER, form));
		assertEquals(
				"旧表示順の値が不正です。管理者に問い合わせてください。bankAccountSortBefore=null",
				ex.getLocalizedMessage());
	}

	/**
	 *<pre>
	 * テスト11：異常系：execAction_未定義のアクション
	 *
	 * 【検証内容】
	 * ・アクションが新規追加(ADD)・更新(UPDATE)以外の場合、MyHouseholdAccountBookRuntimeExceptionが発生すること
	 * ・例外メッセージに指定したアクション値が含まれること
	 *</pre>
	 */
	@Test
	@DisplayName("11 異常系：execAction_未定義のアクション")
	@Sql(scripts = "ReadBankAccountInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecActionFailValueAction() {
		BankAccountInfoForm form = inputUpdateForm("02", "02");
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_DELETE);

		MyHouseholdAccountBookRuntimeException ex = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> service.execAction(TEST_USER, form));
		assertEquals(
				"未定義のアクションが設定されています。管理者に問い合わせてください。action=" + MyHouseholdAccountBookContent.ACTION_TYPE_DELETE,
				ex.getLocalizedMessage());
	}

	private BankAccountInfoForm inputAddForm(String bankAccountSort) {
		BankAccountInfoForm form = new BankAccountInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setBankName("新規テスト銀行");
		form.setBankAccountMemo("新規口座メモ");
		if(bankAccountSort != null) {
			form.setBankAccountSort(Integer.parseInt(bankAccountSort));
		}
		form.setEnableFlg(Boolean.TRUE);
		return form;
	}

	private BankAccountInfoForm inputUpdateForm(String bankAccountCode, String bankAccountSort) {
		BankAccountInfoForm form = new BankAccountInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		form.setBankAccountCode(bankAccountCode);
		form.setBankName("更新後銀行名");
		form.setBankAccountMemo("更新後メモ");
		form.setBankAccountSort(Integer.parseInt(bankAccountSort));
		form.setBankAccountSortBefore(bankAccountCode);
		form.setEnableFlg(Boolean.FALSE);
		return form;
	}

	private List<BankAccountReadWriteDto> execQueryAllBankAccountList() {
		return namedParamTemplete.query(
				"SELECT * FROM BANK_ACCOUNT_TABLE WHERE USER_ID = :USER_ID ORDER BY BANK_ACCOUNT_CODE",
				Map.of("USER_ID", TEST_USER.getUserId().toString()),
				new DataClassRowMapper<>(BankAccountReadWriteDto.class));
	}

	/**
	 * プリミティブ引数を受け取り値オブジェクトへ変換してからBankAccountListItem.from()を呼び出すテスト用ヘルパーメソッドです。
	 * (06-コーディング規約.md §8.4。from()が値オブジェクト型を要求するため、テストコードから直接プリミティブ値で呼び出せない)
	 */
	private BankAccountListItem listItemHelper(String bankAccountCode, String bankName, String bankAccountMemo,
			String bankAccountSort, boolean enableFlg) {
		return BankAccountListItem.from(
				BankAccountCode.from(bankAccountCode),
				BankName.from(bankName),
				BankAccountMemo.from(bankAccountMemo),
				BankAccountSort.from(bankAccountSort),
				EnableFlg.from(enableFlg));
	}

	/**
	 *<pre>
	 * テスト12：正常系：execToggleEnableFlg_有効→無効に切替
	 *
	 * 【検証内容】
	 * ・transactionSuccessFullがtrueであること
	 * ・「銀行名「○○」の銀行口座を無効化しました。」のメッセージが設定されること
	 * ・DB上のENABLE_FLGがfalseに更新されること
	 *</pre>
	 */
	@Test
	@DisplayName("12 正常系：execToggleEnableFlg_有効→無効に切替")
	@Sql(scripts = "ReadBankAccountInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecToggleEnableFlgDisable() {
		BankAccountInfoManageResponse res = service.execToggleEnableFlg(TEST_USER, "01");
		assertTrue(res.isTransactionSuccessFull());
		if(res.getMessagesList().size() != 1) {
			fail("有効/無効切替完了のレスポンスメッセージが設定されていない");
		} else {
			assertEquals("銀行名「テストユーザ銀行０１」の銀行口座を無効化しました。", res.getMessagesList().get(0));
		}

		Map<String, Object> actualDataMap = namedParamTemplete.queryForMap(
				"SELECT * FROM BANK_ACCOUNT_TABLE WHERE USER_ID=:USER_ID AND BANK_ACCOUNT_CODE=:BANK_ACCOUNT_CODE",
				Map.of("USER_ID", TEST_USER.getUserId().toString(), "BANK_ACCOUNT_CODE", "01"));
		assertEquals(Boolean.FALSE, actualDataMap.get("ENABLE_FLG"));
	}

	/**
	 *<pre>
	 * テスト13：正常系：execToggleEnableFlg_無効→有効に切替
	 *
	 * 【検証内容】
	 * ・transactionSuccessFullがtrueであること
	 * ・「銀行名「○○」の銀行口座を有効化しました。」のメッセージが設定されること
	 * ・DB上のENABLE_FLGがtrueに更新されること
	 *</pre>
	 */
	@Test
	@DisplayName("13 正常系：execToggleEnableFlg_無効→有効に切替")
	@Sql(scripts = "ReadBankAccountInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecToggleEnableFlgEnable() {
		BankAccountInfoManageResponse res = service.execToggleEnableFlg(TEST_USER, "03");
		assertTrue(res.isTransactionSuccessFull());
		if(res.getMessagesList().size() != 1) {
			fail("有効/無効切替完了のレスポンスメッセージが設定されていない");
		} else {
			assertEquals("銀行名「テストユーザ銀行０３」の銀行口座を有効化しました。", res.getMessagesList().get(0));
		}

		Map<String, Object> actualDataMap = namedParamTemplete.queryForMap(
				"SELECT * FROM BANK_ACCOUNT_TABLE WHERE USER_ID=:USER_ID AND BANK_ACCOUNT_CODE=:BANK_ACCOUNT_CODE",
				Map.of("USER_ID", TEST_USER.getUserId().toString(), "BANK_ACCOUNT_CODE", "03"));
		assertEquals(Boolean.TRUE, actualDataMap.get("ENABLE_FLG"));
	}

	/**
	 *<pre>
	 * テスト14：異常系：execToggleEnableFlg_存在しない銀行口座コード
	 *
	 * 【検証内容】
	 * ・存在しない銀行口座コードを指定した場合、MyHouseholdAccountBookRuntimeExceptionが発生すること
	 * ・例外メッセージに指定した銀行口座コードが含まれること
	 *</pre>
	 */
	@Test
	@DisplayName("14 異常系：execToggleEnableFlg_存在しない銀行口座コード")
	@Sql(scripts = "ReadBankAccountInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecToggleEnableFlgNotFound() {
		MyHouseholdAccountBookRuntimeException ex = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> service.execToggleEnableFlg(TEST_USER, "99"));
		assertEquals(
				"更新対象の銀行口座情報が存在しません。管理者に問い合わせてください。bankAccountCode:99",
				ex.getLocalizedMessage());
	}
}
