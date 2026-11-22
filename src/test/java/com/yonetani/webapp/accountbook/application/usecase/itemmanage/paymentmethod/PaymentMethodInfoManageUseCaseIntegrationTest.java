/**
 * 支払方法情報管理ユースケース(PaymentMethodInfoManageUseCase.java)のインテグレーションテストケースクラスです。
 *
 * <pre>
 * PaymentMethodInfoManageUseCase の以下メソッドをテストします。
 * service:PaymentMethodInfoManageUseCase⇔レポジトリー:Repository⇔DB
 *
 * [対象メソッド]
 * 1. readPaymentMethodInfo(user)       - 情報管理(支払方法)画面の表示情報取得(初期表示)
 * 2. readPaymentMethodInfo(user,code)  - 情報管理(支払方法)画面の表示情報取得(対象選択時)
 * 3. execAction                        - 支払方法情報の追加・更新
 * 4. execToggleEnableFlg               - 支払方法情報の有効/無効切替
 *
 * [テストシナリオ]
 * readPaymentMethodInfo(user):
 *   1. 正常系: 検索結果なし(0件、種別選択肢5件は表示)
 *   2. 正常系: システム予約行(999)は一覧に出ない、口座名解決・集計期間表示を確認
 * readPaymentMethodInfo(user,code):
 *   3. 正常系: 指定した支払方法コードの表示情報取得
 *   4. 正常系: システム予約行(999)への直接アクセスは拒否され初期表示に戻る
 *   5. 異常系: 存在しない支払方法コード
 * execAction:
 *   6. 正常系: 新規追加(現金、銀行口座コード・集計開始日はnull)
 *   7. 正常系: 新規追加(支払方法メモは任意項目のため未入力でも登録できる)
 *   8. 正常系: 新規追加(クレジットカード、銀行口座コード・集計開始日が登録される)
 *   9. 正常系: 新規追加(種別に不要な項目はnullに正規化される)
 *   10. 正常系: 新規追加(表示順シフトはシステム予約行を巻き込まない)
 *   11. 正常系: 更新(表示順変更なし)
 *   12. 異常系: 旧表示順(更新比較用)が未設定
 *   13. 異常系: 未定義のアクション
 * execToggleEnableFlg:
 *   14. 正常系: 有効→無効に切替
 *   15. 正常系: 無効→有効に切替
 *   16. 異常系: 存在しない支払方法コード
 *   17. 異常系: システム予約行(999)への直接アクセスは拒否され初期表示に戻る
 *
 * [テストデータ]
 * ReadPaymentMethodInfoQueryNotFoundTest.sql    : 支払方法情報0件のユーザ
 * ReadPaymentMethodInfoQueryResultThreeTest.sql : 支払方法情報3件(001:現金,002:口座振替有効,003:クレジットカード無効)
 *                                                  ＋システム予約行(999)を含むユーザ
 * </pre>
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 * 2026/10/18 : 1.00.01  feature-1.03-dev1   execToggleEnableFlgのテスト追加、PaymentMethodKubunOptionItem.from()型安全性対応
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage.paymentmethod;

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
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodKubun;
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
@DisplayName("支払方法情報管理ユースケースのUseCaseテスト（統合テスト）")
class PaymentMethodInfoManageUseCaseIntegrationTest {
	@Autowired
	private PaymentMethodInfoManageUseCase service;
	@Autowired
	private NamedParameterJdbcTemplate namedParamTemplete;

	private final LoginUserInfo TEST_USER = LoginUserInfo.from("TESTUSER001", "テストユーザ01");

	/**
	 *<pre>
	 * テスト1：正常系：readPaymentMethodInfo_検索結果なし(0件、種別選択肢5件は表示)
	 *
	 * 【検証内容】
	 * ・支払方法情報が0件の場合、「支払方法情報取得結果が0件です。」のメッセージが設定されること
	 * ・支払方法一覧情報の明細データが0件であること
	 * ・支払方法種別選択肢が5種別分(現金・口座振替・クレジットカード・デビットカード・電子マネー(前払い式))表示されること
	 * ・各種別のrequiresAccount/requiresClosingDayが期待通りであること
	 * ・入力フォームのアクションが新規登録(ADD)であること
	 *</pre>
	 */
	@Test
	@DisplayName("1 正常系：readPaymentMethodInfo_検索結果なし(0件、種別選択肢5件は表示)")
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
		assertEquals(PaymentMethodKubunOptionItem.from(PaymentMethodKubun.CASH, "現金", false, false), kubunOptions.get(0));
		assertEquals(PaymentMethodKubunOptionItem.from(PaymentMethodKubun.BANK_TRANSFER, "口座振替", true, false), kubunOptions.get(1));
		assertEquals(PaymentMethodKubunOptionItem.from(PaymentMethodKubun.CREDIT_CARD, "クレジットカード", true, true), kubunOptions.get(2));
		assertEquals(PaymentMethodKubunOptionItem.from(PaymentMethodKubun.DEBIT_CARD, "デビットカード", true, false), kubunOptions.get(3));
		assertEquals(PaymentMethodKubunOptionItem.from(PaymentMethodKubun.PREPAID_EMONEY, "電子マネー(前払い式)", false, false), kubunOptions.get(4));

		PaymentMethodInfoForm form = (PaymentMethodInfoForm)modelMap.getAttribute("paymentMethodInfoForm");
		assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, form.getAction());
	}

	/**
	 *<pre>
	 * テスト2：正常系：readPaymentMethodInfo_システム予約行は一覧に出ない、口座名解決・集計期間表示を確認
	 *
	 * 【検証内容】
	 * ・システム予約行(999)を除いた3件が一覧表示されること(確認事項③)
	 * ・銀行口座を持つ支払方法(002)は口座名が解決され、持たない支払方法(001)は「－」になること
	 * ・無効化された支払方法(003)はenableFlg=falseであること
	 * ・集計開始日は集計期間(開始日～終了日)の表示用文字列であること
	 *</pre>
	 */
	@Test
	@DisplayName("2 正常系：readPaymentMethodInfo_システム予約行は一覧に出ない、口座名解決・集計期間表示を確認")
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
		assertEquals("15日～翌月14日", item003.getClosingDay(), "集計開始日は集計期間(開始日～終了日)の表示用文字列であること");
	}

	/**
	 *<pre>
	 * テスト3：正常系：readPaymentMethodInfo(code)_指定した支払方法コードの表示情報取得
	 *
	 * 【検証内容】
	 * ・「支払方法名「○○」の支払方法を更新します。」のメッセージが設定されること
	 * ・入力フォームのアクションが更新(UPDATE)であること
	 * ・入力フォームの各項目に選択した支払方法の値が設定されること(表示順(更新比較用)含む)
	 * ・テストデータにメモの設定がない場合はnullであること
	 *</pre>
	 */
	@Test
	@DisplayName("3 正常系：readPaymentMethodInfo(code)_指定した支払方法コードの表示情報取得")
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
	 *<pre>
	 * テスト4：正常系：readPaymentMethodInfo(code)_システム予約行(999)への直接アクセスは拒否され初期表示に戻る
	 *
	 * 【検証内容】
	 * ・URL直叩き等でシステム予約行(999)が指定された場合、更新を許可せずエラーレスポンスになること
	 * ・更新不可のため初期表示(新規登録用フォーム)に戻ること
	 *</pre>
	 */
	@Test
	@DisplayName("4 正常系：readPaymentMethodInfo(code)_システム予約行(999)への直接アクセスは拒否され初期表示に戻る")
	@Sql(scripts = "ReadPaymentMethodInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadPaymentMethodInfo_システム予約行への直接アクセスは拒否() {
		PaymentMethodInfoManageResponse res = service.readPaymentMethodInfo(TEST_USER, "999");

		assertTrue(res.isErrorResponse());
		ModelMap modelMap = res.build().getModelMap();
		PaymentMethodInfoForm form = (PaymentMethodInfoForm)modelMap.getAttribute("paymentMethodInfoForm");
		assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, form.getAction(), "更新不可のため初期表示(新規登録用フォーム)に戻ること");
	}

	/**
	 *<pre>
	 * テスト5：異常系：readPaymentMethodInfo(code)_存在しない支払方法コード
	 *
	 * 【検証内容】
	 * ・存在しない支払方法コードを指定した場合、MyHouseholdAccountBookRuntimeExceptionが発生すること
	 * ・例外メッセージに指定した支払方法コードが含まれること
	 *</pre>
	 */
	@Test
	@DisplayName("5 異常系：readPaymentMethodInfo(code)_存在しない支払方法コード")
	@Sql(scripts = "ReadPaymentMethodInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadPaymentMethodInfoNotFound() {
		MyHouseholdAccountBookRuntimeException ex = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> service.readPaymentMethodInfo(TEST_USER, "555"));
		assertEquals("更新対象の支払方法情報が存在しません。管理者に問い合わせてください。paymentMethodCode:555", ex.getLocalizedMessage());
	}

	/**
	 *<pre>
	 * テスト6：正常系：execAction_新規追加(現金、銀行口座コード・集計開始日はnull)
	 *
	 * 【検証内容】
	 * ・「新規支払方法を追加しました。」のメッセージが設定されること
	 * ・transactionSuccessFullがtrueであること
	 * ・支払方法テーブルに1件追加され、各項目(コード・メモ・表示順)が入力値通りであること
	 * ・現金種別のため銀行口座コード・集計開始日はnullであること
	 * ・表示順未指定の場合、登録件数+1(=1)が自動採番されること
	 * ・更新可否フラグ(ENABLE_UPDATE_FLG)がtrueであること
	 *</pre>
	 */
	@Test
	@DisplayName("6 正常系：execAction_新規追加(現金、銀行口座コード・集計開始日はnull)")
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
	 *<pre>
	 * テスト7：正常系：execAction_新規追加(支払方法メモは任意項目のため未入力でも登録できる)
	 *
	 * 【検証内容】
	 * ・支払方法メモ未入力(null)のまま新規登録でき、エラーにならないこと
	 * ・DB上のPAYMENT_METHOD_MEMOがnullであること
	 *</pre>
	 */
	@Test
	@DisplayName("7 正常系：execAction_新規追加(支払方法メモは任意項目のため未入力でも登録できる)")
	@Sql(scripts = "ReadPaymentMethodInfoQueryNotFoundTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecAddAction_支払方法メモは未入力でも登録できる() {
		PaymentMethodInfoForm form = inputAddForm("1", null, null, null);

		service.execAction(TEST_USER, form);

		List<PaymentMethodReadWriteDto> resultList = execQueryAllPaymentMethodList();
		assertNull(resultList.get(0).getPaymentMethodMemo());
	}

	/**
	 *<pre>
	 * テスト8：正常系：execAction_新規追加(クレジットカード、銀行口座コード・集計開始日が登録される)
	 *
	 * 【検証内容】
	 * ・transactionSuccessFullがtrueであること
	 * ・クレジットカード種別のため、入力した銀行口座コード・集計開始日がDBに登録されること
	 *</pre>
	 */
	@Test
	@DisplayName("8 正常系：execAction_新規追加(クレジットカード、銀行口座コード・集計開始日が登録される)")
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
	 *<pre>
	 * テスト9：正常系：execAction_新規追加(種別に不要な項目はnullに正規化される)
	 *
	 * 【検証内容】
	 * ・種別=現金なのに銀行口座コード・集計開始日が送信された場合(JS制御をバイパスした想定)、
	 *   UseCase側でnullに正規化されて登録されること(突合レビュー指摘F)
	 *</pre>
	 */
	@Test
	@DisplayName("9 正常系：execAction_新規追加(種別に不要な項目はnullに正規化される)")
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
	 *<pre>
	 * テスト10：正常系：execAction_新規追加(表示順シフトはシステム予約行を巻き込まない)
	 *
	 * 【検証内容】
	 * ・表示順を明示指定した場合、既存データ(001～003)の表示順が+1シフトされること
	 * ・システム予約行(999)は表示順シフトの対象に巻き込まれず、999のまま維持されること
	 *</pre>
	 */
	@Test
	@DisplayName("10 正常系：execAction_新規追加(表示順シフトはシステム予約行を巻き込まない)")
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
	 *<pre>
	 * テスト11：正常系：execAction_更新(表示順変更なし)
	 *
	 * 【検証内容】
	 * ・「支払方法を更新しました。」のメッセージが設定されること
	 * ・transactionSuccessFullがtrueであること
	 * ・DB上の対象データ(支払方法名)が更新値で反映されること
	 * ・通常の支払方法はENABLE_UPDATE_FLG=trueのまま維持されること
	 *</pre>
	 */
	@Test
	@DisplayName("11 正常系：execAction_更新(表示順変更なし)")
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
	 *<pre>
	 * テスト12：異常系：execAction_更新時に旧表示順(更新比較用)が未設定
	 *
	 * 【検証内容】
	 * ・表示順(更新比較用)が未設定の場合、MyHouseholdAccountBookRuntimeExceptionが発生すること
	 * ・例外メッセージに不正値であることが示されること
	 *</pre>
	 */
	@Test
	@DisplayName("12 異常系：execAction_更新時に旧表示順(更新比較用)が未設定")
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
	 *<pre>
	 * テスト13：異常系：execAction_未定義のアクション
	 *
	 * 【検証内容】
	 * ・アクションが新規追加(ADD)・更新(UPDATE)以外の場合、MyHouseholdAccountBookRuntimeExceptionが発生すること
	 * ・例外メッセージに指定したアクション値が含まれること
	 *</pre>
	 */
	@Test
	@DisplayName("13 異常系：execAction_未定義のアクション")
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

	/**
	 *<pre>
	 * テスト14：正常系：execToggleEnableFlg_有効→無効に切替
	 *
	 * 【検証内容】
	 * ・transactionSuccessFullがtrueであること
	 * ・「支払方法名「○○」の支払方法を無効化しました。」のメッセージが設定されること
	 * ・DB上のENABLE_FLGがfalseに更新されること
	 *</pre>
	 */
	@Test
	@DisplayName("14 正常系：execToggleEnableFlg_有効→無効に切替")
	@Sql(scripts = "ReadPaymentMethodInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecToggleEnableFlgDisable() {
		PaymentMethodInfoManageResponse res = service.execToggleEnableFlg(TEST_USER, "002");
		assertTrue(res.isTransactionSuccessFull());
		assertEquals("支払方法名「〇〇銀行引き落とし」の支払方法を無効化しました。", res.getMessagesList().get(0));

		Map<String, Object> actualDataMap = namedParamTemplete.queryForMap(
				"SELECT * FROM PAYMENT_METHOD_TABLE WHERE USER_ID=:USER_ID AND PAYMENT_METHOD_CODE=:PAYMENT_METHOD_CODE",
				Map.of("USER_ID", TEST_USER.getUserId().toString(), "PAYMENT_METHOD_CODE", "002"));
		assertEquals(Boolean.FALSE, actualDataMap.get("ENABLE_FLG"));
	}

	/**
	 *<pre>
	 * テスト15：正常系：execToggleEnableFlg_無効→有効に切替
	 *
	 * 【検証内容】
	 * ・transactionSuccessFullがtrueであること
	 * ・「支払方法名「○○」の支払方法を有効化しました。」のメッセージが設定されること
	 * ・DB上のENABLE_FLGがtrueに更新されること
	 *</pre>
	 */
	@Test
	@DisplayName("15 正常系：execToggleEnableFlg_無効→有効に切替")
	@Sql(scripts = "ReadPaymentMethodInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecToggleEnableFlgEnable() {
		PaymentMethodInfoManageResponse res = service.execToggleEnableFlg(TEST_USER, "003");
		assertTrue(res.isTransactionSuccessFull());
		assertEquals("支払方法名「△△カード」の支払方法を有効化しました。", res.getMessagesList().get(0));

		Map<String, Object> actualDataMap = namedParamTemplete.queryForMap(
				"SELECT * FROM PAYMENT_METHOD_TABLE WHERE USER_ID=:USER_ID AND PAYMENT_METHOD_CODE=:PAYMENT_METHOD_CODE",
				Map.of("USER_ID", TEST_USER.getUserId().toString(), "PAYMENT_METHOD_CODE", "003"));
		assertEquals(Boolean.TRUE, actualDataMap.get("ENABLE_FLG"));
	}

	/**
	 *<pre>
	 * テスト16：異常系：execToggleEnableFlg_存在しない支払方法コード
	 *
	 * 【検証内容】
	 * ・存在しない支払方法コードを指定した場合、MyHouseholdAccountBookRuntimeExceptionが発生すること
	 * ・例外メッセージに指定した支払方法コードが含まれること
	 *</pre>
	 */
	@Test
	@DisplayName("16 異常系：execToggleEnableFlg_存在しない支払方法コード")
	@Sql(scripts = "ReadPaymentMethodInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecToggleEnableFlgNotFound() {
		MyHouseholdAccountBookRuntimeException ex = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> service.execToggleEnableFlg(TEST_USER, "555"));
		assertEquals("更新対象の支払方法情報が存在しません。管理者に問い合わせてください。paymentMethodCode:555", ex.getLocalizedMessage());
	}

	/**
	 *<pre>
	 * テスト17：異常系：execToggleEnableFlg_システム予約行(999)への直接アクセスは拒否され初期表示に戻る
	 *
	 * 【検証内容】
	 * ・URL直叩き等でシステム予約行(999)が指定された場合、更新を拒否してエラーレスポンスになること
	 * ・更新不可のため初期表示(新規登録用フォーム)に戻ること
	 * ・更新が拒否され、DB上のENABLE_FLGは変化しないこと
	 *</pre>
	 */
	@Test
	@DisplayName("17 異常系：execToggleEnableFlg_システム予約行(999)への直接アクセスは拒否され初期表示に戻る")
	@Sql(scripts = "ReadPaymentMethodInfoQueryResultThreeTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecToggleEnableFlg_システム予約行への直接アクセスは拒否() {
		PaymentMethodInfoManageResponse res = service.execToggleEnableFlg(TEST_USER, "999");

		assertTrue(res.isErrorResponse());
		ModelMap modelMap = res.build().getModelMap();
		PaymentMethodInfoForm form = (PaymentMethodInfoForm)modelMap.getAttribute("paymentMethodInfoForm");
		assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, form.getAction(), "更新不可のため初期表示(新規登録用フォーム)に戻ること");

		Map<String, Object> actualDataMap = namedParamTemplete.queryForMap(
				"SELECT * FROM PAYMENT_METHOD_TABLE WHERE USER_ID=:USER_ID AND PAYMENT_METHOD_CODE=:PAYMENT_METHOD_CODE",
				Map.of("USER_ID", TEST_USER.getUserId().toString(), "PAYMENT_METHOD_CODE", "999"));
		assertEquals(Boolean.TRUE, actualDataMap.get("ENABLE_FLG"), "更新が拒否され、ENABLE_FLGは変化しないこと");
	}
}
