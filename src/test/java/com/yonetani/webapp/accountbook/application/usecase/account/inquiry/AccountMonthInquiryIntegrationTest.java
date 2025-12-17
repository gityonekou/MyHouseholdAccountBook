/**
 * 月次収支照会機能の統合テストクラスです。
 * 既存機能の動作を保証するために、リファクタリング前の振る舞いを記録します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/30 : 1.00.00  新規作成
 * 2025/12/17 : 1.00.00  正常系テストケース追加（積立金取崩金額null）
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.inquiry;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.presentation.response.account.inquiry.AccountMonthInquiryResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

/**
 *<pre>
 * 月次収支照会機能の統合テストクラスです。
 *
 * [テスト方針]
 * ・既存機能の動作を保証するために、リファクタリング前の振る舞いを記録
 * ・データベースアクセスを含む全レイヤーの結合テスト
 * ・正常系・異常系の両方をカバー
 *
 * [テストシナリオ]
 * 1. 正常系: データ存在、整合性OK（202511）
 * 2. 正常系: データなし（202512）
 * 3. 正常系: 積立金取崩金額なし（202601）
 * 4. 異常系: 収入金額不整合（202510）
 * 5. 異常系: 支出金額不整合（202509）
 * 6. 異常系: 収支データなし、支出データあり（202508）
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
    "/sql/initsql/schema_test.sql",
    "/com/yonetani/webapp/accountbook/application/usecase/account/inquiry/AccountMonthInquiryIntegrationTest.sql"
})
class AccountMonthInquiryIntegrationTest {

    @Autowired
    private AccountMonthInquiryUseCase useCase;

    /**
     * テスト用のログインユーザ情報を作成します。
     */
    private LoginUserInfo createLoginUser() {
        return LoginUserInfo.from("user01", "テストユーザ01");
    }

    // ========================================
    // 正常系テスト
    // ========================================

    @Test
    @DisplayName("正常系：データ存在、整合性OK - 202511")
    void testRead_NormalCase_DataExists() {
        // Given: テストユーザ、対象年月202511
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        // When: 月次収支を照会
        AccountMonthInquiryResponse response = useCase.read(user, targetYearMonth);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);
        assertNotNull(response.getTargetYearMonthInfo());
        assertEquals("202511", response.getTargetYearMonthInfo().getTargetYearMonth());
        assertTrue(response.isSyuusiDataFlg());

        // Then: 収支データの検証
        assertEquals("350,000円", response.getSyuunyuuKingaku());
        assertEquals("280,000円", response.getSisyutuKingaku());
        assertEquals("50,000円", response.getWithdrewKingaku());
        assertEquals("300,000円", response.getSisyutuYoteiKingaku());
        assertEquals("120,000円", response.getSyuusiKingaku());

        // Then: 支出項目リストの検証
        assertNotNull(response.getExpenditureItemList());
        assertEquals(7, response.getExpenditureItemList().size());

        // Then: メッセージなし
        assertTrue(response.getMessagesList().isEmpty() || response.getMessagesList().size() == 0);
    }

    @Test
    @DisplayName("正常系：データなし - 202512")
    void testRead_NormalCase_NoData() {
        // Given: テストユーザ、対象年月202512（データなし）
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202512";

        // When: 月次収支を照会
        AccountMonthInquiryResponse response = useCase.read(user, targetYearMonth);

        // Then: レスポンスが返却される
        assertNotNull(response);
        assertEquals("202512", response.getTargetYearMonthInfo().getTargetYearMonth());
        assertFalse(response.isSyuusiDataFlg());

        // Then: 収支データはnull
        assertNull(response.getSyuunyuuKingaku());
        assertNull(response.getSisyutuKingaku());
        assertNull(response.getWithdrewKingaku());
        assertNull(response.getSisyutuYoteiKingaku());
        assertNull(response.getSyuusiKingaku());

        // Then: 支出項目リストは空
        assertTrue(response.getExpenditureItemList() == null || response.getExpenditureItemList().isEmpty());

        // Then: メッセージあり
        assertFalse(response.getMessagesList().isEmpty());
        assertTrue(response.getMessagesList().stream()
            .anyMatch(msg -> msg.contains("該当月の収支データがありません")));
    }

    @Test
    @DisplayName("正常系：積立金取崩金額なし - 202601")
    void testRead_NormalCase_NoWithdrewAmount() {
        // Given: テストユーザ、対象年月202601（積立金取崩金額なし）
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202601";

        // When: 月次収支を照会
        AccountMonthInquiryResponse response = useCase.read(user, targetYearMonth);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);
        assertNotNull(response.getTargetYearMonthInfo());
        assertEquals("202601", response.getTargetYearMonthInfo().getTargetYearMonth());
        assertTrue(response.isSyuusiDataFlg());

        // Then: 収支データの検証
        assertEquals("300,000円", response.getSyuunyuuKingaku());
        assertEquals("240,000円", response.getSisyutuKingaku());
        // WithdrewKingakuがnullの場合は空文字列として返却されることを検証
        assertEquals("", response.getWithdrewKingaku());
        assertEquals("250,000円", response.getSisyutuYoteiKingaku());
        assertEquals("60,000円", response.getSyuusiKingaku());

        // Then: 支出項目リストの検証
        assertNotNull(response.getExpenditureItemList());
        assertEquals(5, response.getExpenditureItemList().size());

        // Then: メッセージなし
        assertTrue(response.getMessagesList().isEmpty() || response.getMessagesList().size() == 0);
    }

    // ========================================
    // 異常系テスト
    // ========================================

    @Test
    @DisplayName("異常系：収入金額不整合 - 202510")
    void testRead_ErrorCase_IncomeInconsistency() {
        // Given: テストユーザ、対象年月202510（収入金額不整合）
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202510";

        // When & Then: 例外がスローされる
        MyHouseholdAccountBookRuntimeException exception = assertThrows(
            MyHouseholdAccountBookRuntimeException.class,
            () -> useCase.read(user, targetYearMonth)
        );

        // Then: エラーメッセージの検証
        assertTrue(exception.getMessage().contains("収入金額が一致しません"));
        assertTrue(exception.getMessage().contains("202510"));
    }

    @Test
    @DisplayName("異常系：支出金額不整合 - 202509")
    void testRead_ErrorCase_ExpenditureInconsistency() {
        // Given: テストユーザ、対象年月202509（支出金額不整合）
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202509";

        // When & Then: 例外がスローされる
        MyHouseholdAccountBookRuntimeException exception = assertThrows(
            MyHouseholdAccountBookRuntimeException.class,
            () -> useCase.read(user, targetYearMonth)
        );

        // Then: エラーメッセージの検証
        assertTrue(exception.getMessage().contains("支出金額が一致しません"));
        assertTrue(exception.getMessage().contains("202509"));
    }

    @Test
    @DisplayName("異常系：収支データなし、支出データあり - 202508")
    void testRead_ErrorCase_NoIncomeDataButExpenditureExists() {
        // Given: テストユーザ、対象年月202508（不正データ状態）
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202508";

        // When & Then: 例外がスローされる
        MyHouseholdAccountBookRuntimeException exception = assertThrows(
            MyHouseholdAccountBookRuntimeException.class,
            () -> useCase.read(user, targetYearMonth)
        );

        // Then: エラーメッセージの検証
        assertTrue(exception.getMessage().contains("該当月の収支データが未登録の状態で支出金額情報が登録済みの状態です"));
        assertTrue(exception.getMessage().contains("202508"));
    }

    // ========================================
    // 境界値テスト
    // ========================================

    @Test
    @DisplayName("境界値：現在の対象年月で照会 - read()オーバーロードメソッド")
    void testRead_CurrentTargetYearMonth() {
        // Given: テストユーザ（現在の対象年月=202511）
        LoginUserInfo user = createLoginUser();

        // When: 対象年月を指定せずに照会
        AccountMonthInquiryResponse response = useCase.read(user);

        // Then: 現在の対象年月（202511）のデータが返却される
        assertNotNull(response);
        assertEquals("202511", response.getTargetYearMonthInfo().getTargetYearMonth());
        assertTrue(response.isSyuusiDataFlg());
        assertEquals("350,000円", response.getSyuunyuuKingaku());
    }

    @Test
    @DisplayName("境界値：戻り先年月を指定して照会 - read()オーバーロードメソッド")
    void testRead_WithReturnYearMonth() {
        // Given: テストユーザ、対象年月202511、戻り先年月202510
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";
        String returnYearMonth = "202510";

        // When: 戻り先年月を指定して照会
        AccountMonthInquiryResponse response = useCase.read(user, targetYearMonth, returnYearMonth);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);
        assertEquals("202511", response.getTargetYearMonthInfo().getTargetYearMonth());
        assertEquals("202510", response.getTargetYearMonthInfo().getReturnYearMonth());
        assertTrue(response.isSyuusiDataFlg());
    }

    // ========================================
    // リダイレクト情報取得テスト
    // ========================================

    @Test
    @DisplayName("正常系：買い物登録画面リダイレクト情報取得")
    void testReadShoppingAddRedirectInfo() {
        // Given: テストユーザ、対象年月202511
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        // When: リダイレクト情報を取得
        var response = useCase.readShoppingAddRedirectInfo(user, targetYearMonth);

        // Then: レスポンスが返却される
        assertNotNull(response);
    }

    @Test
    @DisplayName("正常系：収支登録画面リダイレクト情報取得")
    void testReadAccountMonthUpdateRedirectInfo() {
        // Given: テストユーザ、対象年月202511
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        // When: リダイレクト情報を取得
        var response = useCase.readAccountMonthUpdateRedirectInfo(user, targetYearMonth);

        // Then: レスポンスが返却される
        assertNotNull(response);
    }
}
