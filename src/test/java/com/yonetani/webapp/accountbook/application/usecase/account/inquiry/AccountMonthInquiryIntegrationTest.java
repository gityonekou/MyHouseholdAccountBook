/**
 * 月次収支照会機能の統合テストクラスです。
 * 既存機能の動作を保証するために、リファクタリング前の振る舞いを記録します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/30 : 1.00.00  新規作成
 * 2025/12/17 : 1.00.00  正常系テストケース追加（積立金取崩金額null）
 * 2026/06/13 : 1.02.00  支出別一覧 viewType 対応のテスト追加
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
import org.springframework.test.context.jdbc.SqlConfig;
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
 * 7. viewType=item → 支出別リストはレスポンスに含まれない
 * 8. viewType=expenditure → 支出別リストがレスポンスに含まれる
 * 9. viewType=expenditure → 支出合計金額が正しい（280,000円）
 * 10. viewType=item → 支出項目別リスト(SisyutuKingaku)が設定される
 * 11. viewType=expenditure → 支出項目別リスト(SisyutuKingaku)が設定されない
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
///////////////////////////////////////////////////////////////////////////////
// メモ(このファイルをコピーしてテストを作成する際は以下のメモはコピー不要です
// 　Eclipseだと@SqlアノテーションでAccountMonthInquiryIntegrationTest.sqlのinsert文の日本語の値をinsertした際は
// 　デフォルトでをUTF-8でinsertするため問題なかったが、VSCodeでMavenコマンドラインから実行すると文字化けしてしまう。
// 　これは、SQLスクリプト（@Sqlアノテーションで実行されるスクリプト）がUTF-8で読み込まれていないことを示している。
// 　そのため、config = @SqlConfig(encoding = "UTF-8")プロパティを追加することで対応
///////////////////////////////////////////////////////////////////////////////
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
    "/sql/initsql/schema_test.sql",
    "/com/yonetani/webapp/accountbook/application/usecase/account/inquiry/AccountMonthInquiryIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("収支管理機能 月次収支照会のUseCaseテスト（統合テスト）")
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

        // Then: 支出項目の詳細検証（支出金額B/C/BCのフォーマット確認）
        // 先頭の支出項目（SISYUTU_ITEM_CODE='0001'）を検証
        // テストデータ: SISYUTU_KINGAKU=210000, B=150000, C=30000, BC=180000, SIHARAI_DATE='2025-11-30'
        var firstItem = response.getExpenditureItemList().get(0);
        assertEquals("食費", firstItem.getSisyutuItemName()); // 支出項目名
        assertEquals("210,000円", firstItem.getSisyutuKingaku()); // 支出金額
        assertEquals("150,000円", firstItem.getSisyutuKingakuB()); // 支出金額B（toFormatString()でフォーマット済み）
        assertEquals("71", firstItem.getPercentageB()); // 支出金額Bの割合 (150000/210000*100≒71%)
        assertEquals("30,000円", firstItem.getSisyutuKingakuC()); // 支出金額C（toFormatString()でフォーマット済み）
        assertEquals("14", firstItem.getPercentageC()); // 支出金額Cの割合 (30000/210000*100≒14%)
        assertEquals("180,000円", firstItem.getSisyutuKingakuBC()); // 支出金額BC（toFormatString()でフォーマット済み）
        assertEquals("86", firstItem.getPercentage()); // 支出金額BCの割合 (180000/210000*100≒86%)
        assertEquals("2025/11/30", firstItem.getSiharaiDate()); // 支払日（toDisplayString()でフォーマット済み）

        // 3番目の支出項目（SISYUTU_ITEM_CODE='0003'、支出金額C=NULL、支払日=NULLのケース）を検証
        // テストデータ: SISYUTU_KINGAKU=30000, B=30000, C=NULL, BC=30000, SIHARAI_DATE=NULL
        var thirdItem = response.getExpenditureItemList().get(2);
        assertEquals("外食", thirdItem.getSisyutuItemName()); // 支出項目名
        assertEquals("30,000円", thirdItem.getSisyutuKingakuB()); // 支出金額B
        assertEquals("100", thirdItem.getPercentageB()); // 支出金額Bの割合 (30000/30000*100=100%)
        assertEquals("", thirdItem.getSisyutuKingakuC()); // 支出金額C=NULLの場合は空文字
        assertEquals("", thirdItem.getPercentageC()); // 支出金額CがNULLの場合、割合も空文字
        assertEquals("30,000円", thirdItem.getSisyutuKingakuBC()); // 支出金額BC
        assertEquals("100", thirdItem.getPercentage()); // 支出金額BCの割合 (30000/30000*100=100%)
        assertEquals("", thirdItem.getSiharaiDate()); // 支払日=NULLなら空文字列

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

    // ========================================
    // viewType 対応テスト
    // ========================================

    @Test
    @DisplayName("viewType=item → 支出別リストはレスポンスに含まれない（expenditureList 空）")
    void testRead_ViewTypeItem_ExpenditureListIsEmpty() {
        // Given: テストユーザ、対象年月202511、viewType=item
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        // When: viewType=item で照会（4引数版で returnYearMonth=targetYearMonth）
        AccountMonthInquiryResponse response = useCase.read(user, targetYearMonth, targetYearMonth, "item");

        // Then: viewType が "item" になっている
        assertEquals("item", response.getViewType());
        // Then: 支出別リストは空
        assertNotNull(response.getExpenditureList());
        assertTrue(response.getExpenditureList().isEmpty());
        // Then: 支出合計金額は null（設定されていない）
        assertNull(response.getExpenditureTotalAmount());
    }

    @Test
    @DisplayName("viewType=expenditure → 支出別リストがレスポンスに含まれる")
    void testRead_ViewTypeExpenditure_ExpenditureListIsNotEmpty() {
        // Given: テストユーザ、対象年月202511、viewType=expenditure
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        // When: viewType=expenditure で照会
        AccountMonthInquiryResponse response = useCase.read(user, targetYearMonth, targetYearMonth, "expenditure");

        // Then: viewType が "expenditure" になっている
        assertEquals("expenditure", response.getViewType());
        // Then: 支出別リストに5件含まれる
        assertNotNull(response.getExpenditureList());
        assertEquals(5, response.getExpenditureList().size());

        // Then: 1件目（001: スーパー買い物、NON_WASTED）の明細検証
        // NON_WASTED のため displayName にプレフィックスなし
        var row1 = response.getExpenditureList().get(0);
        assertEquals("001", row1.getExpenditureCode());
        assertEquals("スーパー買い物", row1.getDisplayName()); // NON_WASTED: プレフィックスなし
        assertEquals("5日", row1.getPaymentDay());
        assertEquals("80,000円", row1.getExpenditureAmount());
        assertEquals("食料品購入", row1.getExpenditureDetailContext());

        // Then: 4件目（004: 映画館、WASTED_B）の明細検証
        // WASTED_B のため displayName に「【無駄遣いB】」プレフィックスあり
        // ※ WASTED_C のプレフィックス検証はドメインテスト（ExpenditureCategoryTest.testToDisplayLabel_WastedC）で実施済み
        var row4 = response.getExpenditureList().get(3);
        assertEquals("004", row4.getExpenditureCode());
        assertEquals("【無駄遣いB】映画館", row4.getDisplayName()); // WASTED_B: プレフィックスあり
        assertEquals("20日", row4.getPaymentDay());
        assertEquals("20,000円", row4.getExpenditureAmount());
        assertEquals("娯楽費", row4.getExpenditureDetailContext());
    }

    @Test
    @DisplayName("viewType=expenditure → 支出合計金額が正しい（280,000円）")
    void testRead_ViewTypeExpenditure_TotalAmountIsCorrect() {
        // Given: テストユーザ、対象年月202511、viewType=expenditure
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        // When: viewType=expenditure で照会
        AccountMonthInquiryResponse response = useCase.read(user, targetYearMonth, targetYearMonth, "expenditure");

        // Then: 支出合計金額が 280,000円（SQLデータの合計: 80000+30000+50000+20000+100000）
        assertEquals("280,000円", response.getExpenditureTotalAmount());
    }

    @Test
    @DisplayName("viewType=item → 支出項目別リスト(SisyutuKingaku)が設定される")
    void testRead_ViewTypeItem_ExpenditureItemListIsPopulated() {
        // Given: テストユーザ、対象年月202511、viewType=item
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        // When: viewType=item で照会
        AccountMonthInquiryResponse response = useCase.read(user, targetYearMonth, targetYearMonth, "item");

        // Then: 支出項目別リスト（SisyutuKingaku）が設定される（7件）
        // ※ 明細内容の詳細検証は testRead_NormalCase_DataExists で実施済みのため、件数のみを確認
        assertNotNull(response.getExpenditureItemList());
        assertEquals(7, response.getExpenditureItemList().size());
    }

    @Test
    @DisplayName("viewType=expenditure → 支出項目別リスト(SisyutuKingaku)が設定されない")
    void testRead_ViewTypeExpenditure_ExpenditureItemListIsNotPopulated() {
        // Given: テストユーザ、対象年月202511、viewType=expenditure
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        // When: viewType=expenditure で照会
        AccountMonthInquiryResponse response = useCase.read(user, targetYearMonth, targetYearMonth, "expenditure");

        // Then: 支出項目別リスト（SisyutuKingaku）は設定されない（空）
        assertTrue(response.getExpenditureItemList() == null || response.getExpenditureItemList().isEmpty());
    }
}
