/**
 * 収支登録・更新画面の支出登録機能の統合テストクラスです。
 * 既存機能の動作を保証するために、リファクタリング前の振る舞いを記録します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/01/25 : 1.00.00  新規作成
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
     * テスト用の支出登録情報リストを作成します（新規データタイプ）。
     */
    private List<ExpenditureRegistItem> createTestExpenditureRegistItemList() {
        List<ExpenditureRegistItem> list = new ArrayList<>();
        list.add(ExpenditureRegistItem.from(
            MyHouseholdAccountBookContent.DATA_TYPE_NEW,
            MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
            "20260125120000001",
            "0037", // 支出項目コード（電気代: Level3, parent=0036(水光熱通費), grandparent=0023(固定費(課税)))
            "",     // イベントコード（なし）
            "テスト電気代",
            "1",    // 無駄遣いなし
            "テスト電気代詳細",
            "25",   // 支払日（日のみ）
            new BigDecimal("5000.00"),
            false   // clearStartFlg
        ));
        list.add(ExpenditureRegistItem.from(
            MyHouseholdAccountBookContent.DATA_TYPE_NEW,
            MyHouseholdAccountBookContent.ACTION_TYPE_ADD,
            "20260125120000002",
            "0051", // 支出項目コード（食費: Level2, parent=0049(飲食日用品))
            "",     // イベントコード（なし）
            "テスト食費",
            "2",    // 無駄遣いB
            "テスト食費詳細",
            "26",   // 支払日（日のみ）
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
        list.add(ExpenditureRegistItem.from(
            MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
            MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE, // DBロード時の初期値はデータ変更なし
            "001", // DBロード時の支出コード形式（EXPENDITURE_CODEはCHAR(3)）
            "0037", // 支出項目コード（電気代）
            "",     // イベントコード（なし）
            "DB登録済み電気代",
            "1",    // 無駄遣いなし
            "DB登録済み電気代詳細",
            "15",   // 支払日（日のみ）
            new BigDecimal("8000.00"),
            false   // clearStartFlg
        ));
        list.add(ExpenditureRegistItem.from(
            MyHouseholdAccountBookContent.DATA_TYPE_LOAD,
            MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE, // DBロード時の初期値はデータ変更なし
            "002",
            "0051", // 支出項目コード（食費）
            "",     // イベントコード（なし）
            "DB登録済み食費",
            "1",    // 無駄遣いなし
            "DB登録済み食費詳細",
            "20",   // 支払日（日のみ）
            new BigDecimal("2000.00"),
            true    // clearStartFlg=true
        ));
        return list;
    }

    // ========================================
    // 正常系テスト - readExpenditureUpdateSelect
    // ========================================

    /**
     * readExpenditureUpdateSelect 正常系テスト
     * 選択した支出情報がフォームに設定されることを確認します。
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
        // イベントコード：空文字の場合はそのまま設定される
        assertEquals("", form.getEventCode());
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
     * readExpenditureUpdateSelect 異常系テスト
     * 存在しない支出コードを指定した場合、例外がスローされることを確認します。
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
     * readExpenditureUpdateBindingErrorSetInfo 正常系テスト
     * バインディングエラー時に入力値が保持されることを確認します。
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

    // ========================================
    // 正常系テスト - readNewExpenditureItem
    // ========================================

    /**
     * readNewExpenditureItem 正常系テスト
     * 選択した支出項目の新規支出フォームが設定されることを確認します。
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
     * readNewExpenditureItem 正常系テスト - イベントコード設定あり
     * イベント系支出項目でイベントコードが設定されている場合、
     * 支出項目名にイベント名が付加されることを確認します。
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
     * readNewExpenditureItem 異常系テスト
     * イベント必須なのにイベントコードが未設定の場合、例外がスローされることを確認します。
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
     * execExpenditureAction 正常系テスト - 新規追加
     * セッションに支出情報が追加されることを確認します。
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
     * execExpenditureAction 正常系テスト - 更新
     * セッションの支出情報が更新されることを確認します。
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
     * execExpenditureAction 正常系テスト - 削除（新規データタイプ）
     * 新規データタイプの支出がセッションから完全に削除されることを確認します。
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
     * execExpenditureAction 正常系テスト - 削除（ロードデータタイプ）
     * ロードデータタイプの支出が削除アクション設定でセッションに残ることを確認します。
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
     * execExpenditureAction 異常系テスト - 更新時の支出コード不存在
     * 存在しない支出コードで更新しようとした場合、例外がスローされることを確認します。
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
     * execExpenditureAction 異常系テスト - 削除時の支出コード不存在
     * 存在しない支出コードで削除しようとした場合、例外がスローされることを確認します。
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
}
