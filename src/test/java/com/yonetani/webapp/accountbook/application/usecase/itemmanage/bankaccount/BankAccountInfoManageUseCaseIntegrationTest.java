/**
 * 銀行口座情報管理ユースケース(BankAccountInfoManageUseCase.java)のインテグレーションテストケースクラスです
 * 以下範囲のテストを実施します。
 * service:BankAccountInfoManageUseCase⇔レポジトリー:Repository⇔DB
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/08/18 : 1.00.00     新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage.bankaccount;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

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
class BankAccountInfoManageUseCaseIntegrationTest {
	@Autowired
	private BankAccountInfoManageUseCase service;
	@Autowired
	private NamedParameterJdbcTemplate namedParamTemplete;

	private final LoginUserInfo TEST_USER = LoginUserInfo.from("TESTUSER001", "テストユーザ01");

	/**
	 * 情報管理(銀行口座)画面表示情報取得のインテグレーションテストです。
	 * ユーザIDに対応する銀行口座情報なし(検索結果なし)時の結果情報が想定通りかをテストします。
	 */
	@Test
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
	 * 情報管理(銀行口座)画面表示情報取得のインテグレーションテストです。
	 * ユーザIDに対応する銀行口座情報3件時の結果情報が想定通りかをテストします。
	 */
	@Test
	@Sql(scripts = "ReadBankAccountInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadBankAccountInfoQueryResultThree() {
		BankAccountInfoManageResponse res = service.readBankAccountInfo(TEST_USER);
		assertEquals(0, res.getMessagesList().size(), "エラーメッセージが設定されていないこと");

		ModelMap modelMap = res.build().getModelMap();
		@SuppressWarnings("unchecked")
		List<BankAccountListItem> bankAccountList = (List<BankAccountListItem>)modelMap.getAttribute("bankAccountList");
		assertNotNull(bankAccountList, "銀行口座一覧情報の明細データがnullでないこと");
		assertEquals(3, bankAccountList.size(), "銀行口座一覧情報の明細データが3件であること");
		assertEquals(BankAccountListItem.from("01", "テストユーザ銀行０１", "生活費口座", "01", true), bankAccountList.get(0));
		assertEquals(BankAccountListItem.from("02", "テストユーザ銀行０２", null, "02", true), bankAccountList.get(1));
		assertEquals(BankAccountListItem.from("03", "テストユーザ銀行０３", "無効口座", "03", false), bankAccountList.get(2));
	}

	/**
	 * 指定したユーザIDと銀行口座に応じた情報管理(銀行口座)画面の表示情報取得のインテグレーションテストです。
	 */
	@Test
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
	 * 指定した銀行口座情報が存在しない場合、例外となることをテストします。
	 */
	@Test
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
	 * バリデーションチェックエラー時のレスポンス情報確認
	 */
	@Test
	@Sql(scripts = "ReadBankAccountInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadUpdateBindingErrorSetInfo() {
		BankAccountInfoForm form = inputUpdateForm("02", "03");
		BankAccountInfoManageResponse res = service.readUpdateBindingErrorSetInfo(TEST_USER, form);
		assertEquals(0, res.getMessagesList().size());
		assertFalse(res.isTransactionSuccessFull());
	}

	/**
	 * 新規追加データが登録されることを確認します(表示順自動採番)。
	 */
	@Test
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
	 * 新規追加時、表示順を明示指定した場合に既存データの表示順がシフトされることを確認します。
	 */
	@Test
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
	 * 更新データが登録されることを確認します(表示順変更なし)。
	 */
	@Test
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
	 * 更新データが登録されることを確認します(表示順変更あり)。
	 * 口座コード03の表示順を01に変更 → 01, 02が+1シフトされること
	 */
	@Test
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
	 * 更新の場合、表示順(更新比較用)の値が未設定の場合予期しないエラーとなること
	 */
	@Test
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
	 * アクションの設定値が新規追加・更新以外の場合予期しないエラーとなること
	 */
	@Test
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
}
