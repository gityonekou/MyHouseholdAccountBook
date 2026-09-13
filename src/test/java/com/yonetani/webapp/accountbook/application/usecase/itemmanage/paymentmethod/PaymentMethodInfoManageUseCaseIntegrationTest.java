/**
 * 支払方法情報管理ユースケース(PaymentMethodInfoManageUseCase.java)のインテグレーションテストケースクラスです
 * 以下範囲のテストを実施します。
 * service:PaymentMethodInfoManageUseCase⇔レポジトリー:Repository⇔DB
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/08/19 : 1.00.00     新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage.paymentmethod;

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
import com.yonetani.webapp.accountbook.infrastructure.dto.account.paymentmethod.PaymentMethodReadWriteDto;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.PaymentMethodInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.PaymentMethodInfoManageResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.PaymentMethodInfoManageResponse.PaymentMethodKubunOptionItem;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.PaymentMethodInfoManageResponse.PaymentMethodListItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

/**
 *<pre>
 * 支払方法情報管理ユースケース(PaymentMethodInfoManageUseCase.java)のインテグレーションテストケースクラスです。
 * 以下範囲のテストを実施します。
 * service:PaymentMethodInfoManageUseCase⇔レポジトリー:Repository⇔DB
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
class PaymentMethodInfoManageUseCaseIntegrationTest {
	@Autowired
	private PaymentMethodInfoManageUseCase service;
	@Autowired
	private NamedParameterJdbcTemplate namedParamTemplete;

	private final LoginUserInfo TEST_USER = LoginUserInfo.from("TESTUSER001", "テストユーザ01");

	/**
	 * 検索結果なし時、選択肢は5種別分すべて表示され、一覧は0件であることを確認します。
	 */
	@Test
	@Sql(scripts = "ReadPaymentMethodInfoQueryNotFoundTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadPaymentMethodInfoQueryNotFound() {
		PaymentMethodInfoManageResponse res = service.readPaymentMethodInfo(TEST_USER);
		if(res.getMessagesList().size() != 1) {
			fail("検索結果なしのレスポンスメッセージが設定されていない");
		} else {
			assertEquals("支払方法情報取得結果が0件です。", res.getMessagesList().get(0));
		}

		ModelMap modelMap = res.build().getModelMap();
		@SuppressWarnings("unchecked")
		List<PaymentMethodListItem> paymentMethodList = (List<PaymentMethodListItem>)modelMap.getAttribute("paymentMethodList");
		assertNotNull(paymentMethodList);
		assertEquals(0, paymentMethodList.size());

		@SuppressWarnings("unchecked")
		List<PaymentMethodKubunOptionItem> kubunOptions = (List<PaymentMethodKubunOptionItem>)modelMap.getAttribute("paymentMethodKubunOptions");
		assertEquals(5, kubunOptions.size(), "支払方法種別5件分の選択肢が表示されること");
		assertEquals(PaymentMethodKubunOptionItem.from("1", "現金", false, false), kubunOptions.get(0));
		assertEquals(PaymentMethodKubunOptionItem.from("2", "口座振替", true, false), kubunOptions.get(1));
		assertEquals(PaymentMethodKubunOptionItem.from("3", "クレジットカード", true, true), kubunOptions.get(2));
		assertEquals(PaymentMethodKubunOptionItem.from("4", "デビットカード", true, false), kubunOptions.get(3));
		assertEquals(PaymentMethodKubunOptionItem.from("5", "電子マネー(前払い式)", false, false), kubunOptions.get(4));

		PaymentMethodInfoForm form = (PaymentMethodInfoForm)modelMap.getAttribute("paymentMethodInfoForm");
		assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, form.getAction());
	}

	/**
	 * システム予約行(999)は一覧に含まれないことを確認します(確認事項③)。
	 */
	@Test
	@Sql(scripts = "ReadPaymentMethodInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadPaymentMethodInfo_システム予約行は一覧に出ない() {
		PaymentMethodInfoManageResponse res = service.readPaymentMethodInfo(TEST_USER);

		ModelMap modelMap = res.build().getModelMap();
		@SuppressWarnings("unchecked")
		List<PaymentMethodListItem> paymentMethodList = (List<PaymentMethodListItem>)modelMap.getAttribute("paymentMethodList");
		assertEquals(3, paymentMethodList.size(), "システム予約行(999)を除いた3件が表示されること");
		assertTrue(paymentMethodList.stream().noneMatch(item -> "999".equals(item.getPaymentMethodCode())));

		// 銀行口座を持つ支払方法(002)は口座名が解決され、持たない支払方法(001)は「－」になること
		PaymentMethodListItem item001 = paymentMethodList.stream().filter(i -> "001".equals(i.getPaymentMethodCode())).findFirst().orElseThrow();
		assertEquals("－", item001.getBankAccountName());
		PaymentMethodListItem item002 = paymentMethodList.stream().filter(i -> "002".equals(i.getPaymentMethodCode())).findFirst().orElseThrow();
		assertEquals("テストユーザ銀行０１", item002.getBankAccountName());
		assertEquals("口座振替", item002.getPaymentMethodKubunName());
		PaymentMethodListItem item003 = paymentMethodList.stream().filter(i -> "003".equals(i.getPaymentMethodCode())).findFirst().orElseThrow();
		assertFalse(item003.isEnableFlg(), "無効化された支払方法はenableFlg=falseであること");
		assertEquals("15", item003.getClosingDay());
	}

	/**
	 * 指定した支払方法情報がフォームに設定されることを確認します。
	 */
	@Test
	@Sql(scripts = "ReadPaymentMethodInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadPaymentMethodInfoFromPaymentMethodCode() {
		PaymentMethodInfoManageResponse res = service.readPaymentMethodInfo(TEST_USER, "002");
		assertEquals("支払方法名「〇〇銀行引き落とし」の支払方法を更新します。", res.getMessagesList().get(0));

		ModelMap modelMap = res.build().getModelMap();
		PaymentMethodInfoForm form = (PaymentMethodInfoForm)modelMap.getAttribute("paymentMethodInfoForm");
		assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE, form.getAction());
		assertEquals("002", form.getPaymentMethodCode());
		assertEquals("〇〇銀行引き落とし", form.getPaymentMethodName());
		assertNull(form.getPaymentMethodMemo(), "テストデータにメモの設定がないためnullであること");
		assertEquals("2", form.getPaymentMethodKubun());
		assertEquals("01", form.getBankAccountCode());
		assertNull(form.getClosingDay());
		assertEquals(Integer.valueOf(2), form.getPaymentMethodSort());
		assertEquals("002", form.getPaymentMethodSortBefore());
	}

	/**
	 * システム予約行(999)への直接アクセス(URL直叩き等)は更新を許可せず、エラーメッセージ付きで初期表示に戻ることを確認します。
	 */
	@Test
	@Sql(scripts = "ReadPaymentMethodInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadPaymentMethodInfo_システム予約行への直接アクセスは拒否() {
		PaymentMethodInfoManageResponse res = service.readPaymentMethodInfo(TEST_USER, "999");

		assertTrue(res.isErrorResponse());
		ModelMap modelMap = res.build().getModelMap();
		PaymentMethodInfoForm form = (PaymentMethodInfoForm)modelMap.getAttribute("paymentMethodInfoForm");
		assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, form.getAction(), "更新不可のため初期表示(新規登録用フォーム)に戻ること");
	}

	/**
	 * 存在しない支払方法コードを指定した場合、例外となることを確認します。
	 */
	@Test
	@Sql(scripts = "ReadPaymentMethodInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadPaymentMethodInfoNotFound() {
		MyHouseholdAccountBookRuntimeException ex = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> service.readPaymentMethodInfo(TEST_USER, "555"));
		assertEquals("更新対象の支払方法情報が存在しません。管理者に問い合わせてください。paymentMethodCode:555", ex.getLocalizedMessage());
	}

	/**
	 * 現金で新規追加した場合、銀行口座コード・集計開始日がnullで登録されることを確認します(表示順自動採番)。
	 */
	@Test
	@Sql(scripts = "ReadPaymentMethodInfoQueryNotFoundTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecAddAction_現金() {
		PaymentMethodInfoForm form = inputAddForm("1", null, null, null);
		form.setPaymentMethodMemo("ポイント還元率1%");

		PaymentMethodInfoManageResponse res = service.execAction(TEST_USER, form);
		assertTrue(res.isTransactionSuccessFull());
		assertEquals("新規支払方法を追加しました。[code:001]" + form.getPaymentMethodName(), res.getMessagesList().get(0));

		List<PaymentMethodReadWriteDto> resultList = execQueryAllPaymentMethodList();
		assertEquals(1, resultList.size());
		PaymentMethodReadWriteDto dto = resultList.get(0);
		assertEquals("001", dto.getPaymentMethodCode());
		assertEquals("ポイント還元率1%", dto.getPaymentMethodMemo());
		assertNull(dto.getBankAccountCode());
		assertNull(dto.getClosingDay());
		assertEquals("001", dto.getPaymentMethodSort());
		assertTrue(dto.isEnableUpdateFlg());
	}

	/**
	 * 支払方法メモは任意項目のため、未入力(null)のまま新規登録できることを確認します。
	 */
	@Test
	@Sql(scripts = "ReadPaymentMethodInfoQueryNotFoundTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecAddAction_支払方法メモは未入力でも登録できる() {
		PaymentMethodInfoForm form = inputAddForm("1", null, null, null);

		service.execAction(TEST_USER, form);

		List<PaymentMethodReadWriteDto> resultList = execQueryAllPaymentMethodList();
		assertNull(resultList.get(0).getPaymentMethodMemo());
	}

	/**
	 * クレジットカードで新規追加した場合、銀行口座コード・集計開始日が登録されることを確認します。
	 */
	@Test
	@Sql(scripts = "ReadPaymentMethodInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecAddAction_クレジットカード() {
		PaymentMethodInfoForm form = inputAddForm("3", "01", "20", null);

		PaymentMethodInfoManageResponse res = service.execAction(TEST_USER, form);
		assertTrue(res.isTransactionSuccessFull());

		List<PaymentMethodReadWriteDto> resultList = execQueryAllPaymentMethodList();
		// 既存3件(999除く) + 新規1件 = 4件
		PaymentMethodReadWriteDto added = resultList.stream().filter(d -> "004".equals(d.getPaymentMethodCode())).findFirst().orElseThrow();
		assertEquals("01", added.getBankAccountCode());
		assertEquals("20", added.getClosingDay());
	}

	/**
	 * 「不要なのに入力されている」値をUseCase側でnullに正規化することを確認します(突合レビュー指摘F)。
	 * 種別=現金なのに銀行口座コード・集計開始日が送信された場合、登録後はnullになること。
	 */
	@Test
	@Sql(scripts = "ReadPaymentMethodInfoQueryNotFoundTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecAddAction_不要な項目はnullに正規化される() {
		PaymentMethodInfoForm form = inputAddForm("1", "01", "20", null);
		// 現金には銀行口座・集計開始日は不要だが、フォームには値が入っている状態を再現(JS制御をバイパスした想定)

		service.execAction(TEST_USER, form);

		List<PaymentMethodReadWriteDto> resultList = execQueryAllPaymentMethodList();
		PaymentMethodReadWriteDto dto = resultList.get(0);
		assertNull(dto.getBankAccountCode(), "現金には銀行口座コードは不要なためnullに正規化されること");
		assertNull(dto.getClosingDay(), "現金には集計開始日は不要なためnullに正規化されること");
	}

	/**
	 * 新規追加時、表示順を明示指定した場合に既存データ(システム予約行を除く)の表示順がシフトされることを確認します。
	 */
	@Test
	@Sql(scripts = "ReadPaymentMethodInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecAddAction_表示順シフトはシステム予約行を巻き込まない() {
		PaymentMethodInfoForm form = inputAddForm("1", null, null, "001");

		service.execAction(TEST_USER, form);

		List<PaymentMethodReadWriteDto> resultList = execQueryAllPaymentMethodList();
		// 既存001～003が+1シフトされ、999は999のまま(シフト対象に巻き込まれない)
		PaymentMethodReadWriteDto reserved = resultList.stream().filter(d -> "999".equals(d.getPaymentMethodCode())).findFirst().orElseThrow();
		assertEquals("999", reserved.getPaymentMethodSort(), "システム予約行の表示順はシフトされないこと");
		PaymentMethodReadWriteDto shifted001 = resultList.stream().filter(d -> "001".equals(d.getPaymentMethodCode())).findFirst().orElseThrow();
		assertEquals("002", shifted001.getPaymentMethodSort());
	}

	/**
	 * 更新データが登録されることを確認します(表示順変更なし)。
	 */
	@Test
	@Sql(scripts = "ReadPaymentMethodInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecUpdateAction() {
		PaymentMethodInfoForm form = inputUpdateForm("002", "2", "01", null, "002");

		PaymentMethodInfoManageResponse res = service.execAction(TEST_USER, form);
		assertTrue(res.isTransactionSuccessFull());
		assertEquals("支払方法を更新しました。[code:002]" + form.getPaymentMethodName(), res.getMessagesList().get(0));

		Map<String, Object> actualDataMap = namedParamTemplete.queryForMap(
				"SELECT * FROM PAYMENT_METHOD_TABLE WHERE USER_ID=:USER_ID AND PAYMENT_METHOD_CODE=:PAYMENT_METHOD_CODE",
				Map.of("USER_ID", TEST_USER.getUserId().toString(), "PAYMENT_METHOD_CODE", "002"));
		assertEquals("更新後支払方法名", actualDataMap.get("PAYMENT_METHOD_NAME"));
		assertEquals(true, actualDataMap.get("ENABLE_UPDATE_FLG"), "通常の支払方法はENABLE_UPDATE_FLG=trueのまま維持されること");
	}

	/**
	 * 更新の場合、表示順(更新比較用)の値が未設定の場合予期しないエラーとなること
	 */
	@Test
	@Sql(scripts = "ReadPaymentMethodInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecUpdateSortBeforeEmptyAction() {
		PaymentMethodInfoForm form = inputUpdateForm("002", "2", "01", null, "002");
		form.setPaymentMethodSortBefore(null);

		MyHouseholdAccountBookRuntimeException ex = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> service.execAction(TEST_USER, form));
		assertEquals("旧表示順の値が不正です。管理者に問い合わせてください。paymentMethodSortBefore=null", ex.getLocalizedMessage());
	}

	/**
	 * アクションの設定値が新規追加・更新以外の場合予期しないエラーとなること
	 */
	@Test
	@Sql(scripts = "ReadPaymentMethodInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecActionFailValueAction() {
		PaymentMethodInfoForm form = inputUpdateForm("002", "2", "01", null, "002");
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_DELETE);

		MyHouseholdAccountBookRuntimeException ex = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> service.execAction(TEST_USER, form));
		assertEquals("未定義のアクションが設定されています。管理者に問い合わせてください。action=" + MyHouseholdAccountBookContent.ACTION_TYPE_DELETE, ex.getLocalizedMessage());
	}

	private PaymentMethodInfoForm inputAddForm(String kubun, String bankAccountCode, String closingDay, String sort) {
		PaymentMethodInfoForm form = new PaymentMethodInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		form.setPaymentMethodName("新規テスト支払方法");
		form.setPaymentMethodKubun(kubun);
		form.setBankAccountCode(bankAccountCode);
		form.setClosingDay(closingDay);
		if(sort != null) {
			form.setPaymentMethodSort(Integer.parseInt(sort));
		}
		form.setEnableFlg(Boolean.TRUE);
		return form;
	}

	private PaymentMethodInfoForm inputUpdateForm(String code, String kubun, String bankAccountCode, String closingDay, String sortBefore) {
		PaymentMethodInfoForm form = new PaymentMethodInfoForm();
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		form.setPaymentMethodCode(code);
		form.setPaymentMethodName("更新後支払方法名");
		form.setPaymentMethodKubun(kubun);
		form.setBankAccountCode(bankAccountCode);
		form.setClosingDay(closingDay);
		form.setPaymentMethodSort(Integer.parseInt(sortBefore));
		form.setPaymentMethodSortBefore(sortBefore);
		form.setEnableFlg(Boolean.TRUE);
		return form;
	}

	private List<PaymentMethodReadWriteDto> execQueryAllPaymentMethodList() {
		return namedParamTemplete.query(
				"SELECT * FROM PAYMENT_METHOD_TABLE WHERE USER_ID = :USER_ID ORDER BY PAYMENT_METHOD_CODE",
				Map.of("USER_ID", TEST_USER.getUserId().toString()),
				new DataClassRowMapper<>(PaymentMethodReadWriteDto.class));
	}
}
