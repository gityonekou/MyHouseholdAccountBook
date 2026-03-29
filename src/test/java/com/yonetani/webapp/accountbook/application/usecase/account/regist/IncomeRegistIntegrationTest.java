/**
 * 収支登録・更新画面の収入登録機能の統合テストクラスです。
 * 既存機能の動作を保証するために、リファクタリング前の振る舞いを記録します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/01/25 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.regist;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
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
import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.IncomeItemForm;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.IncomeAndExpenditureRegistResponse;
import com.yonetani.webapp.accountbook.presentation.session.ExpenditureRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.IncomeRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

/**
 *<pre>
 * 収支登録・更新画面の収入登録機能の統合テストクラスです。
 *
 * [テスト方針]
 * ・IncomeRegistUseCaseの動作を保証するために、リファクタリング後の振る舞いを検証
 * ・セッション操作を含む全レイヤーの結合テスト
 * ・正常系・異常系の両方をカバー
 *
 * [対象メソッド]
 * 1. readIncomeAddSelect - 収入新規追加選択画面表示
 * 2. readIncomeUpdateSelect - 収入更新選択画面表示
 * 3. readIncomeUpdateBindingErrorSetInfo - 収入更新時のバインディングエラー処理
 * 4. execIncomeAction - 収入の追加・更新・削除（セッション操作）
 *
 * [テストシナリオ]
 * readIncomeAddSelect:
 *   1. 正常系: 新規追加画面表示_フォーム初期値確認
 * readIncomeUpdateSelect:
 *   2. 正常系: 更新画面表示_選択収入情報のフォーム設定確認
 *   3. 異常系: 更新画面表示_収入コード不存在エラー
 * readIncomeUpdateBindingErrorSetInfo:
 *   4. 正常系: バインディングエラー時_入力値保持確認
 * execIncomeAction:
 *   5. 正常系: 収入新規追加_セッションへの追加確認
 *   6. 正常系: 収入更新_セッションの更新確認
 *   7. 正常系: 収入削除_新規データタイプ_セッションから完全削除
 *   8. 正常系: 収入削除_ロードデータタイプ_削除アクション設定
 *   9. 異常系: 収入更新_収入コード不存在エラー
 *  10. 異常系: 収入削除_収入コード不存在エラー
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
    "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeRegistIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("収支管理機能 収入登録のUseCaseテスト（統合テスト）")
class IncomeRegistIntegrationTest {

    @Autowired
    private IncomeRegistUseCase useCase;

    /**
     * テスト用のログインユーザ情報を作成します。
     */
    private LoginUserInfo createLoginUser() {
        return LoginUserInfo.from("user01", "テストユーザ01");
    }

    /**
     * テスト用の収入登録情報リストを作成します（新規データタイプ）。
     */
    private List<IncomeRegistItem> createTestIncomeRegistItemList() {
        List<IncomeRegistItem> list = new ArrayList<>();
        list.add(IncomeRegistItem.from(
            MyHouseholdAccountBookContent.DATA_TYPE_NEW,
            MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
            "20260125120000001",
            "1", // 給料
            "テスト給料詳細",
            new BigDecimal("300000.00")
        ));
        list.add(IncomeRegistItem.from(
            MyHouseholdAccountBookContent.DATA_TYPE_NEW,
            MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
            "20260125120000002",
            "2", // 副業
            "テスト副業詳細",
            new BigDecimal("500000.00")
        ));
        return list;
    }

    /**
     * テスト用の収入登録情報リストを作成します（ロードデータタイプ）。
     */
    private List<IncomeRegistItem> createTestIncomeRegistItemListWithLoadType() {
        List<IncomeRegistItem> list = new ArrayList<>();
        list.add(IncomeRegistItem.from(
            MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
            MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE, // DBロード時の初期値はデータ変更なし
            "01", // DBロード時の収入コード形式（INCOME_CODEはCHAR(2)）
            "1", // 給料
            "DB登録済み給料詳細",
            new BigDecimal("350000.00")
        ));
        list.add(IncomeRegistItem.from(
            MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
            MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE, // DBロード時の初期値はデータ変更なし
            "02", // INCOME_CODEはCHAR(2)
            "3", // 積立からの取崩し
            "DB登録済み積立取崩詳細",
            new BigDecimal("100000.00")
        ));
        return list;
    }

    /**
     * テスト用の空の支出登録情報リストを作成します。
     */
    private List<ExpenditureRegistItem> createEmptyExpenditureRegistItemList() {
        return new ArrayList<>();
    }

    // ========================================
    // 正常系テスト - readIncomeAddSelect
    // ========================================

    /**
     * readIncomeAddSelect 正常系テスト
     * 新規追加画面表示時のフォーム初期値を確認します。
     */
    @Test
    @DisplayName("正常系：新規追加画面表示_フォーム初期値確認")
    void testReadIncomeAddSelect_NormalCase() {
        // Given: テストユーザ、対象年月、セッションの収入・支出リスト
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";
        List<IncomeRegistItem> incomeList = createTestIncomeRegistItemList();
        List<ExpenditureRegistItem> expenditureList = createEmptyExpenditureRegistItemList();

        // When: 新規追加画面を表示
        IncomeAndExpenditureRegistResponse response = useCase.readIncomeAddSelect(
            user, targetYearMonth, incomeList, expenditureList);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);

        // Then: フォームが新規追加モードで設定されている
        IncomeItemForm form = response.getIncomeItemForm();
        assertNotNull(form);
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, form.getAction());
        assertEquals("【新規追加】", form.getIncomeKubunName());
        assertNull(form.getIncomeCode());
        assertNull(form.getIncomeKubun());
        assertNull(form.getIncomeDetailContext());
        assertNull(form.getIncomeKingaku());

        // Then: 収入一覧が設定されている
        assertNotNull(response.getIncomeListInfo());
        assertEquals(2, response.getIncomeListInfo().size());
    }

    // ========================================
    // 正常系テスト - readIncomeUpdateSelect
    // ========================================

    /**
     * readIncomeUpdateSelect 正常系テスト
     * 選択した収入情報がフォームに設定されることを確認します。
     */
    @Test
    @DisplayName("正常系：更新画面表示_選択収入情報のフォーム設定確認")
    void testReadIncomeUpdateSelect_NormalCase() {
        // Given: テストユーザ、対象年月、収入コード、セッションの収入・支出リスト
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";
        String incomeCode = "20260125120000001";
        List<IncomeRegistItem> incomeList = createTestIncomeRegistItemList();
        List<ExpenditureRegistItem> expenditureList = createEmptyExpenditureRegistItemList();

        // When: 更新画面を表示
        IncomeAndExpenditureRegistResponse response = useCase.readIncomeUpdateSelect(
            user, targetYearMonth, incomeCode, incomeList, expenditureList);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);

        // Then: フォームが更新モードで設定されている
        IncomeItemForm form = response.getIncomeItemForm();
        assertNotNull(form);
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE, form.getAction());
        assertEquals("20260125120000001", form.getIncomeCode());
        assertEquals("【給料】", form.getIncomeKubunName());
        assertEquals("1", form.getIncomeKubun());
        assertEquals("テスト給料詳細", form.getIncomeDetailContext());
        assertEquals(300000, form.getIncomeKingaku());
    }

    /**
     * readIncomeUpdateSelect 異常系テスト
     * 存在しない収入コードを指定した場合、例外がスローされることを確認します。
     */
    @Test
    @DisplayName("異常系：更新画面表示_収入コード不存在エラー")
    void testReadIncomeUpdateSelect_IncomeCodeNotFound() {
        // Given: テストユーザ、対象年月、存在しない収入コード、セッションの収入・支出リスト
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";
        String nonExistentIncomeCode = "99999999999999999";
        List<IncomeRegistItem> incomeList = createTestIncomeRegistItemList();
        List<ExpenditureRegistItem> expenditureList = createEmptyExpenditureRegistItemList();

        // When/Then: 存在しない収入コードで更新画面を表示すると例外がスローされる
        MyHouseholdAccountBookRuntimeException exception = assertThrows(
            MyHouseholdAccountBookRuntimeException.class,
            () -> useCase.readIncomeUpdateSelect(
                user, targetYearMonth, nonExistentIncomeCode, incomeList, expenditureList)
        );

        // Then: エラーメッセージを確認
        assertTrue(exception.getMessage().contains("更新対象の収入情報がセッションに存在しません"));
        assertTrue(exception.getMessage().contains("incomeCode=" + nonExistentIncomeCode));
    }

    // ========================================
    // 正常系テスト - readIncomeUpdateBindingErrorSetInfo
    // ========================================

    /**
     * readIncomeUpdateBindingErrorSetInfo 正常系テスト
     * バインディングエラー時に入力値が保持されることを確認します。
     */
    @Test
    @DisplayName("正常系：バインディングエラー時_入力値保持確認")
    void testReadIncomeUpdateBindingErrorSetInfo_NormalCase() {
        // Given: テストユーザ、対象年月、入力フォーム、セッションの収入・支出リスト
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        IncomeItemForm inputForm = new IncomeItemForm();
        inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
        inputForm.setIncomeKubunName("【新規追加】");
        inputForm.setIncomeKubun("1");
        inputForm.setIncomeDetailContext("テスト入力詳細");
        inputForm.setIncomeKingaku(-100); // バリデーションエラーとなる負の値

        List<IncomeRegistItem> incomeList = createTestIncomeRegistItemList();
        List<ExpenditureRegistItem> expenditureList = createEmptyExpenditureRegistItemList();

        // When: バインディングエラー時の画面表示
        IncomeAndExpenditureRegistResponse response = useCase.readIncomeUpdateBindingErrorSetInfo(
            user, targetYearMonth, inputForm, incomeList, expenditureList);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);

        // Then: 入力値がそのまま保持されている
        IncomeItemForm form = response.getIncomeItemForm();
        assertNotNull(form);
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, form.getAction());
        assertEquals("【新規追加】", form.getIncomeKubunName());
        assertEquals("1", form.getIncomeKubun());
        assertEquals("テスト入力詳細", form.getIncomeDetailContext());
        assertEquals(-100, form.getIncomeKingaku());

        // Then: 収入一覧も設定されている
        assertNotNull(response.getIncomeListInfo());
        assertEquals(2, response.getIncomeListInfo().size());
    }

    // ========================================
    // 正常系テスト - execIncomeAction
    // ========================================

    /**
     * execIncomeAction 正常系テスト - 新規追加
     * セッションに収入情報が追加されることを確認します。
     */
    @Test
    @DisplayName("正常系：収入新規追加_セッションへの追加確認")
    void testExecIncomeAction_Add_NormalCase() {
        // Given: テストユーザ、対象年月、入力フォーム（新規追加）、セッションの収入リスト
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        IncomeItemForm inputForm = new IncomeItemForm();
        inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
        inputForm.setIncomeKubun("3"); // 積立からの取崩し
        inputForm.setIncomeDetailContext("新規追加テスト詳細");
        inputForm.setIncomeKingaku(50000);

        List<IncomeRegistItem> incomeList = createTestIncomeRegistItemList();
        int originalSize = incomeList.size();

        // When: 収入を新規追加
        IncomeAndExpenditureRegistResponse response = useCase.execIncomeAction(
            user, targetYearMonth, inputForm, incomeList);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);

        // Then: 収入リストに1件追加されている
        List<IncomeRegistItem> resultList = response.getIncomeRegistItemList();
        assertNotNull(resultList);
        assertEquals(originalSize + 1, resultList.size());

        // Then: 追加された収入情報を確認
        IncomeRegistItem addedItem = resultList.get(resultList.size() - 1);
        assertEquals(MyHouseholdAccountBookContent.DATA_TYPE_NEW, addedItem.getDataType());
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, addedItem.getAction());
        assertEquals(17, addedItem.getIncomeCode().length()); // 自動生成された収入コード(yyyyMMddHHmmssSSS形式=17桁)
        assertEquals("3", addedItem.getIncomeCategory());
        assertEquals("新規追加テスト詳細", addedItem.getIncomeDetailContext());
        assertEquals(new BigDecimal("50000.00"), addedItem.getIncomeKingaku());

        // Then: 完了メッセージを確認
        assertFalse(response.getMessagesList().isEmpty());
        assertTrue(response.getMessagesList().get(0).contains("仮登録しました"));
        assertTrue(response.getMessagesList().get(0).contains("積立からの取崩し"));
        assertTrue(response.getMessagesList().get(0).contains("50,000円"));
    }

    /**
     * execIncomeAction 正常系テスト - 更新
     * セッションの収入情報が更新されることを確認します。
     */
    @Test
    @DisplayName("正常系：収入更新_セッションの更新確認")
    void testExecIncomeAction_Update_NormalCase() {
        // Given: テストユーザ、対象年月、入力フォーム（更新）、セッションの収入リスト
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        IncomeItemForm inputForm = new IncomeItemForm();
        inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
        inputForm.setIncomeCode("20260125120000001"); // 既存の収入コード
        inputForm.setIncomeKubun("2"); // 給料→副業に変更
        inputForm.setIncomeDetailContext("更新後のテスト詳細");
        inputForm.setIncomeKingaku(350000);

        List<IncomeRegistItem> incomeList = createTestIncomeRegistItemList();
        int originalSize = incomeList.size();

        // When: 収入を更新
        IncomeAndExpenditureRegistResponse response = useCase.execIncomeAction(
            user, targetYearMonth, inputForm, incomeList);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);

        // Then: 収入リストのサイズは変わらない
        List<IncomeRegistItem> resultList = response.getIncomeRegistItemList();
        assertNotNull(resultList);
        assertEquals(originalSize, resultList.size());

        // Then: 更新された収入情報を確認
        IncomeRegistItem updatedItem = resultList.get(0);
        assertEquals("20260125120000001", updatedItem.getIncomeCode());
        assertEquals("2", updatedItem.getIncomeCategory()); // 収入区分が"1"→"2"に更新されていること
        assertEquals("更新後のテスト詳細", updatedItem.getIncomeDetailContext());
        assertEquals(new BigDecimal("350000.00"), updatedItem.getIncomeKingaku());

        // Then: 完了メッセージを確認
        assertFalse(response.getMessagesList().isEmpty());
        assertTrue(response.getMessagesList().get(0).contains("仮更新しました"));
        assertTrue(response.getMessagesList().get(0).contains("副業")); // 収入区分を副業に変更
        assertTrue(response.getMessagesList().get(0).contains("350,000円"));
    }

    /**
     * execIncomeAction 正常系テスト - 削除（新規データタイプ）
     * 新規データタイプの収入がセッションから完全に削除されることを確認します。
     */
    @Test
    @DisplayName("正常系：収入削除_新規データタイプ_セッションから完全削除")
    void testExecIncomeAction_Delete_NewDataType() {
        // Given: テストユーザ、対象年月、入力フォーム（削除）、セッションの収入リスト（新規データタイプ）
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        IncomeItemForm inputForm = new IncomeItemForm();
        inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_DELETE);
        inputForm.setIncomeCode("20260125120000001"); // 削除対象
        inputForm.setIncomeKubun("1");
        inputForm.setIncomeKingaku(300000);

        List<IncomeRegistItem> incomeList = createTestIncomeRegistItemList();
        int originalSize = incomeList.size();

        // When: 収入を削除
        IncomeAndExpenditureRegistResponse response = useCase.execIncomeAction(
            user, targetYearMonth, inputForm, incomeList);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);

        // Then: 収入リストから1件削除されている（新規データタイプは完全削除）
        List<IncomeRegistItem> resultList = response.getIncomeRegistItemList();
        assertNotNull(resultList);
        assertEquals(originalSize - 1, resultList.size());

        // Then: 削除対象の収入コードがリストに存在しない
        boolean exists = resultList.stream()
            .anyMatch(item -> "20260125120000001".equals(item.getIncomeCode()));
        assertFalse(exists);

        // Then: 完了メッセージを確認
        assertFalse(response.getMessagesList().isEmpty());
        assertTrue(response.getMessagesList().get(0).contains("仮削除しました"));
        assertTrue(response.getMessagesList().get(0).contains("給料"));
    }

    /**
     * execIncomeAction 正常系テスト - 削除（ロードデータタイプ）
     * ロードデータタイプの収入が削除アクション設定でセッションに残ることを確認します。
     */
    @Test
    @DisplayName("正常系：収入削除_ロードデータタイプ_削除アクション設定")
    void testExecIncomeAction_Delete_LoadDataType() {
        // Given: テストユーザ、対象年月、入力フォーム（削除）、セッションの収入リスト（ロードデータタイプ）
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        IncomeItemForm inputForm = new IncomeItemForm();
        inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_DELETE);
        inputForm.setIncomeCode("01"); // 削除対象（ロードデータ、INCOME_CODEはCHAR(2)）
        inputForm.setIncomeKubun("1");
        inputForm.setIncomeKingaku(350000);

        List<IncomeRegistItem> incomeList = createTestIncomeRegistItemListWithLoadType();
        int originalSize = incomeList.size();

        // When: 収入を削除
        IncomeAndExpenditureRegistResponse response = useCase.execIncomeAction(
            user, targetYearMonth, inputForm, incomeList);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);

        // Then: 収入リストのサイズは変わらない（削除アクション設定で残る）
        List<IncomeRegistItem> resultList = response.getIncomeRegistItemList();
        assertNotNull(resultList);
        assertEquals(originalSize, resultList.size());

        // Then: 削除対象の収入がアクション=削除で存在する
        IncomeRegistItem deletedItem = resultList.stream()
            .filter(item -> "01".equals(item.getIncomeCode()))
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
    // 異常系テスト - execIncomeAction
    // ========================================

    /**
     * execIncomeAction 異常系テスト - 更新時の収入コード不存在
     * 存在しない収入コードで更新しようとした場合、例外がスローされることを確認します。
     */
    @Test
    @DisplayName("異常系：収入更新_収入コード不存在エラー")
    void testExecIncomeAction_Update_IncomeCodeNotFound() {
        // Given: テストユーザ、対象年月、入力フォーム（更新、存在しない収入コード）、セッションの収入リスト
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        IncomeItemForm inputForm = new IncomeItemForm();
        inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
        inputForm.setIncomeCode("99999999999999999"); // 存在しない収入コード
        inputForm.setIncomeKubun("1");
        inputForm.setIncomeDetailContext("更新テスト");
        inputForm.setIncomeKingaku(100000);

        List<IncomeRegistItem> incomeList = createTestIncomeRegistItemList();

        // When/Then: 存在しない収入コードで更新すると例外がスローされる
        MyHouseholdAccountBookRuntimeException exception = assertThrows(
            MyHouseholdAccountBookRuntimeException.class,
            () -> useCase.execIncomeAction(user, targetYearMonth, inputForm, incomeList)
        );

        // Then: エラーメッセージを確認
        assertTrue(exception.getMessage().contains("更新対象の収入情報がセッションに存在しません"));
        assertTrue(exception.getMessage().contains("incomeCode=99999999999999999"));
    }

    /**
     * execIncomeAction 異常系テスト - 削除時の収入コード不存在
     * 存在しない収入コードで削除しようとした場合、例外がスローされることを確認します。
     */
    @Test
    @DisplayName("異常系：収入削除_収入コード不存在エラー")
    void testExecIncomeAction_Delete_IncomeCodeNotFound() {
        // Given: テストユーザ、対象年月、入力フォーム（削除、存在しない収入コード）、セッションの収入リスト
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        IncomeItemForm inputForm = new IncomeItemForm();
        inputForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_DELETE);
        inputForm.setIncomeCode("99999999999999999"); // 存在しない収入コード
        inputForm.setIncomeKubun("1");
        inputForm.setIncomeKingaku(100000);

        List<IncomeRegistItem> incomeList = createTestIncomeRegistItemList();

        // When/Then: 存在しない収入コードで削除すると例外がスローされる
        MyHouseholdAccountBookRuntimeException exception = assertThrows(
            MyHouseholdAccountBookRuntimeException.class,
            () -> useCase.execIncomeAction(user, targetYearMonth, inputForm, incomeList)
        );

        // Then: エラーメッセージを確認
        assertTrue(exception.getMessage().contains("更新対象の収入情報がセッションに存在しません"));
        assertTrue(exception.getMessage().contains("incomeCode=99999999999999999"));
    }
}
