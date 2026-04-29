/**
 * 収支登録・更新画面初期表示機能の統合テストクラスです。
 * 既存機能の動作を保証するために、リファクタリング前の振る舞いを記録します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/01/12 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.incomeandexpenditure;

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
import com.yonetani.webapp.accountbook.presentation.response.account.regist.IncomeAndExpenditureRegistResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.session.ExpenditureRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.IncomeRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

/**
 *<pre>
 * 収支登録・更新画面初期表示機能の統合テストクラスです。
 *
 * [テスト方針]
 * ・既存機能の動作を保証するために、リファクタリング前の振る舞いを記録
 * ・データベースアクセスを含む全レイヤーの結合テスト
 * ・正常系・異常系の両方をカバー
 *
 * [対象メソッド]
 * 1. readInitInfo - 新規登録時の画面初期表示（固定費から自動生成）
 * 2. readUpdateInfo - 更新時の画面初期表示（DB登録済み情報取得）
 * 3. readIncomeAndExpenditureInfoList - 収入・支出一覧の再表示
 * 4. readRegistCheckErrorSetInfo - 内容確認時の入力不備エラー処理（収入未登録）
 *
 * [テストシナリオ]
 * readInitInfo:
 *   1. 正常系: 新規登録画面表示_10月固定費（偶数月）セッションデータ検証 - 202510
 *   2. 正常系: 新規登録画面表示_10月固定費（偶数月）画面表示データ検証 - 202510
 *   3. 正常系: 新規登録画面表示_11月固定費（奇数月）セッションデータ検証 - 202511
 *   4. 正常系: 新規登録画面表示_11月固定費（奇数月）画面表示データ検証 - 202511
 *   5. 正常系: 新規登録画面表示_固定費なし（画面表示データ0件確認含む） - 202512
 *   6. 正常系: 新規登録_必須登録データ未登録時メッセージ確認 - 202510
 *   7. 正常系: 新規登録_全必須登録データ登録済みメッセージ確認 - 202510
 *   8. 正常系: 新規登録_一部必須登録データのみ登録時メッセージ確認 - 202510
 * readUpdateInfo:
 *   9. 正常系: 更新画面表示_セッションデータ検証 - 202511
 *  10. 正常系: 更新画面表示_画面表示データ検証 - 202511
 *  11. 異常系: 更新_収入テーブル情報なし・支出テーブル情報あり → 例外 - 202509
 *  12. 正常系: 更新_収入テーブル情報あり・支出テーブル情報なし → 支出リスト空 - 202508
 * readIncomeAndExpenditureInfoList:
 *  13. 正常系: 収入・支出一覧再表示（削除アクション設定データの除外確認含む）
 * readRegistCheckErrorSetInfo:
 *  14. 正常系: 入力確認エラー_収入未登録
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
    "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("収支管理機能 収支登録・更新画面初期表示のUseCaseテスト（統合テスト）")
class IncomeAndExpenditureInitIntegrationTest {

    @Autowired
    private IncomeAndExpenditureInitUseCase useCase;

    /**
     * テスト用のログインユーザ情報を作成します。
     */
    private LoginUserInfo createLoginUser() {
        return LoginUserInfo.from("user01", "テストユーザ01");
    }

    // ========================================
    // 正常系テスト - readInitInfo
    // ========================================

    /**
     *<pre>
     * テスト①：正常系：新規登録_セッションデータ確認_10月固定費
     *
     * 【検証内容】
     * ・対象年月202510（10月）の固定費データから5件の支出情報が生成されること
     * ・対象となる固定費支払月（毎月/10月/偶数月/その他任意）の固定費のみ取得されること
     * ・支払日変換：'00'→'01'（月初め）、'40'→'31'（10月末日）が正しく行われること
     * ・支出区分変換：固定費名に「無駄遣いB」含む→'2'、「無駄遣いC」含む→'3'、その他→'1'が正しく設定されること
     *   ただし支出区分の判定はFIXED_COST_NAMEから行われること（FIXED_COST_DETAIL_CONTEXTは無関係）
     * ・固定費区分='2'の場合、0円開始設定フラグがtrueになること
     * ・各支出のデータタイプ=新規(new)・アクション=追加(add)・支出コード17桁・イベントコード空文字が設定されること
     *</pre>
     */
    @Test
    @Sql(scripts = {
        "/sql/initsql/schema_test.sql",
        "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest.sql",
        "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest_FixedCost_202510.sql"
    }, config = @SqlConfig(encoding = "UTF-8"))
    @DisplayName("正常系：新規登録_セッションデータ確認_10月固定費 - 202510")
    void testReadInitInfo_NormalCase_SessionData_October() {
        // Given: テストユーザ、対象年月202510（10月）
        // 対象となるFIXED_COST_SHIHARAI_TUKI: '00'（毎月）, '10'（10月）, '30'（偶数月）, '40'（その他任意）
        // 対象外: '08'（8月）, '20'（奇数月）
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202510";

        // When: 新規登録画面を表示
        IncomeAndExpenditureRegistResponse response = useCase.readInitInfo(user, targetYearMonth);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);

        // Then: 固定費から支出情報が自動生成されている（5件）
        assertNotNull(response.getExpenditureRegistItemList());
        assertEquals(5, response.getExpenditureRegistItemList().size());

        // Then: 支出情報の内容を検証（支払日順：00→20→25→31→40）
        //
        // 【1番目】家賃 - FIXED_COST_SHIHARAI_TUKI='30'(偶数月), DAY='00'(月初め), KUBUN='1'
        // 固定費名(FIXED_COST_NAME)の値「家賃」に「無駄遣いB」「無駄遣いC」のどちらも含まれない場合、支出区分は'1'：無駄遣いなしが設定される
        // 固定費支払日(FIXED_COST_SHIHARAI_DAY)='00'の場合、支払日は'01'に変換され設定される
        // 固定費区分(FIXED_COST_KUBUN)='1'の場合、0円開始設定フラグはfalseが設定される
        var firstItem = response.getExpenditureRegistItemList().get(0);
        assertEquals(MyHouseholdAccountBookContent.DATA_TYPE_NEW, firstItem.getDataType()); // データタイプ設定値は常に'new'
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, firstItem.getAction()); // アクション設定値は常に'add'
        assertEquals(17, firstItem.getExpenditureCode().length());  // 支出コード(仮登録値なので長さのみ確認)
        assertEquals("0030", firstItem.getExpenditureItemCode()); // 支出項目コード（家賃）
        assertEquals("", firstItem.getEventCode());  // イベントコード設定値は常に空文字列
        assertEquals("家賃", firstItem.getExpenditureName()); // 支出名
        assertEquals("1", firstItem.getExpenditureCategory());  // 支出区分:'1'(無駄遣いなし)
        assertEquals("家賃支払詳細", firstItem.getExpenditureDetailContext()); // 支出詳細
        assertEquals("01", firstItem.getSiharaiDate()); // 支払日：00→01
        assertEquals(new BigDecimal("62000.00"), firstItem.getExpenditureKingaku()); // 支払金額
        assertEquals(false, firstItem.isClearStartFlg()); // 0円開始設定フラグ

        // 【2番目】水道代 - FIXED_COST_SHIHARAI_TUKI='10'(10月), DAY='20', KUBUN='2'
        // 固定費名(FIXED_COST_NAME)の値「水道代」に「無駄遣いB」「無駄遣いC」のどちらも含まれない場合、支出区分は'1'：無駄遣いなしが設定される
        // 固定費詳細(FIXED_COST_DETAIL_CONTEXT)の値に「無駄遣いB」が含まれているが、支出区分の判定はFIXED_COST_NAMEから行われるため、支出区分は'1'が設定される
        // 固定費支払日(FIXED_COST_SHIHARAI_DAY)='20'の場合、支払日は'20'が設定される
        // 固定費区分(FIXED_COST_KUBUN)='2'の場合、0円開始設定フラグはtrueが設定される
        var secondItem = response.getExpenditureRegistItemList().get(1);
        assertEquals(MyHouseholdAccountBookContent.DATA_TYPE_NEW, secondItem.getDataType()); // データタイプ設定値は常に'new'
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, secondItem.getAction()); // アクション設定値は常に'add'
        assertEquals(17, secondItem.getExpenditureCode().length());  // 支出コード(仮登録値なので長さのみ確認)
        assertEquals("0040", secondItem.getExpenditureItemCode()); // 支出項目コード（水道代）
        assertEquals("", secondItem.getEventCode());  // イベントコード設定値は常に空文字列
        assertEquals("水道代", secondItem.getExpenditureName()); // 支出名
        assertEquals("1", secondItem.getExpenditureCategory());  // 支出区分:'1'(無駄遣いなし)
        assertEquals("水道代詳細(無駄遣いB)", secondItem.getExpenditureDetailContext()); // 支出詳細
        assertEquals("20", secondItem.getSiharaiDate()); // 支払日：20→20
        assertEquals(new BigDecimal("5000.00"), secondItem.getExpenditureKingaku()); // 支払金額
        assertEquals(true, secondItem.isClearStartFlg()); // 0円開始設定フラグ

        // 【3番目】通信費(無駄遣いB) - FIXED_COST_SHIHARAI_TUKI='40'(その他任意), DAY='25', KUBUN='1'
        // 固定費名(FIXED_COST_NAME)の値「通信費(無駄遣いB)」に「無駄遣いB」が含まれる場合、支出区分は'2'：無駄遣い（軽度）が設定される
        // 固定費支払日(FIXED_COST_SHIHARAI_DAY)='25'の場合、支払日は'25'が設定される
        // 固定費区分(FIXED_COST_KUBUN)='1'の場合、0円開始設定フラグはfalseが設定される
        var thirdItem = response.getExpenditureRegistItemList().get(2);
        assertEquals(MyHouseholdAccountBookContent.DATA_TYPE_NEW, thirdItem.getDataType()); // データタイプ設定値は常に'new'
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, thirdItem.getAction()); // アクション設定値は常に'add'
        assertEquals(17, thirdItem.getExpenditureCode().length());  // 支出コード(仮登録値なので長さのみ確認)
        assertEquals("0039", thirdItem.getExpenditureItemCode()); // 支出項目コード（通信費）
        assertEquals("", thirdItem.getEventCode());  // イベントコード設定値は常に空文字列
        assertEquals("通信費(無駄遣いB)", thirdItem.getExpenditureName()); // 支出名
        assertEquals("2", thirdItem.getExpenditureCategory());  // 支出区分:'2'(無駄遣い（軽度）)
        assertEquals("携帯電話・インターネット詳細", thirdItem.getExpenditureDetailContext()); // 支出詳細
        assertEquals("25", thirdItem.getSiharaiDate()); // 支払日：25→25
        assertEquals(new BigDecimal("7000.00"), thirdItem.getExpenditureKingaku()); // 支払金額
        assertEquals(false, thirdItem.isClearStartFlg()); // 0円開始設定フラグ

        // 【4番目】電気代(無駄遣いB) - FIXED_COST_SHIHARAI_TUKI='00'(毎月), DAY='31', KUBUN='2'
        // 固定費名(FIXED_COST_NAME)の値「電気代(無駄遣いB)」に「無駄遣いB」が含まれる場合、支出区分は'2'：無駄遣い（軽度）が設定される
        // 固定費支払日(FIXED_COST_SHIHARAI_DAY)='31'の場合、支払日は'31'が設定される
        // 固定費区分(FIXED_COST_KUBUN)='2'の場合、0円開始設定フラグはtrueが設定される
        var fourthItem = response.getExpenditureRegistItemList().get(3);
        assertEquals(MyHouseholdAccountBookContent.DATA_TYPE_NEW, fourthItem.getDataType()); // データタイプ設定値は常に'new'
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, fourthItem.getAction()); // アクション設定値は常に'add'
        assertEquals(17, fourthItem.getExpenditureCode().length());  // 支出コード(仮登録値なので長さのみ確認)
        assertEquals("0037", fourthItem.getExpenditureItemCode()); // 支出項目コード（電気代）
        assertEquals("", fourthItem.getEventCode());  // イベントコード設定値は常に空文字列
        assertEquals("電気代(無駄遣いB)", fourthItem.getExpenditureName()); // 支出名
        assertEquals("2", fourthItem.getExpenditureCategory());  // 支出区分:'2'(無駄遣い（軽度）)
        assertEquals("電気代詳細", fourthItem.getExpenditureDetailContext()); // 支出詳細
        assertEquals("31", fourthItem.getSiharaiDate()); // 支払日：31→31（10月は31日まで）
        assertEquals(new BigDecimal("10000.00"), fourthItem.getExpenditureKingaku()); // 支払金額
        assertEquals(true, fourthItem.isClearStartFlg()); // 0円開始設定フラグ

        // 【5番目】ガス代(無駄遣いC) - FIXED_COST_SHIHARAI_TUKI='00'(毎月), DAY='40'(月末), KUBUN='1'
        // 固定費名(FIXED_COST_NAME)の値「ガス代(無駄遣いC)」に「無駄遣いC」が含まれる場合、支出区分は'3'：無駄遣い（重度）が設定される
        // 固定費支払日(FIXED_COST_SHIHARAI_DAY)='40'の場合、支払日は'31'が設定される
        // 固定費区分(FIXED_COST_KUBUN)='1'の場合、0円開始設定フラグはfalseが設定される
        // メモ：無駄遣いCとKUBUN='2'の組み合わせは11月固定費テストの「【5番目】ガス代(無駄遣いC)」のデータで確認
        var fifthItem = response.getExpenditureRegistItemList().get(4);
        assertEquals(MyHouseholdAccountBookContent.DATA_TYPE_NEW, fifthItem.getDataType()); // データタイプ設定値は常に'new'
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, fifthItem.getAction()); // アクション設定値は常に'add'
        assertEquals(17, fifthItem.getExpenditureCode().length());  // 支出コード(仮登録値なので長さのみ確認)
        assertEquals("0038", fifthItem.getExpenditureItemCode()); // 支出項目コード（ガス代）
        assertEquals("", fifthItem.getEventCode());  // イベントコード設定値は常に空文字列
        assertEquals("ガス代(無駄遣いC)", fifthItem.getExpenditureName()); // 支出名
        assertEquals("3", fifthItem.getExpenditureCategory());  // 支出区分:'3'(無駄遣い（重度）)
        assertEquals("ガス代詳細", fifthItem.getExpenditureDetailContext()); // 支出詳細
        assertEquals("31", fifthItem.getSiharaiDate()); // 支払日：40→31（10月は31日まで）
        assertEquals(new BigDecimal("8000.00"), fifthItem.getExpenditureKingaku()); // 支払金額
        assertEquals(false, fifthItem.isClearStartFlg()); // 0円開始設定フラグ

    }

    /**
     *<pre>
     * テスト②：正常系：新規登録_画面表示内容確認_10月固定費
     *
     * 【検証内容】
     * ・画面表示用年月（2025年10月）が正しく設定されること
     * ・収入情報フォームが新規追加の初期値（アクション=add、収入区分名=【新規追加】、各項目null）で設定されること
     * ・収入区分選択ボックスが正しく設定されること
     * ・新規登録時は収入一覧情報が空で収入金額合計がnullであること
     * ・支出一覧情報が5件表示されること（支払日順：01日/20日/25日/31日/31日）
     * ・支出区分='2'の場合、支出名の先頭に【無駄遣いB】が付加されること
     * ・支出区分='3'の場合、支出名の先頭に【無駄遣いC】が付加されること
     * ・支出金額合計が92,000円であること
     * ・必須登録データ未登録のためメッセージが8件存在すること
     *</pre>
     */
    @Test
    @Sql(scripts = {
        "/sql/initsql/schema_test.sql",
        "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest.sql",
        "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest_FixedCost_202510.sql"
    }, config = @SqlConfig(encoding = "UTF-8"))
    @DisplayName("正常系：新規登録_画面表示内容確認_10月固定費 - 202510")
    void testReadInitInfo_NormalCase_ViewData_October() {
        // Given: テストユーザ、対象年月202510（10月）
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202510";

        // When: 新規登録画面を表示
        IncomeAndExpenditureRegistResponse response = useCase.readInitInfo(user, targetYearMonth);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);

        // Then: 画面表示用の年月が正しく設定されている
        assertEquals("2025", response.getViewYear()); // 年の値
        assertEquals("10", response.getViewMonth()); // 月の値

        // Then: 収入情報入力フォームが正しく設定されている（新規登録用の初期値）
        assertNotNull(response.getIncomeItemForm());
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, response.getIncomeItemForm().getAction()); // アクション：新規追加
        assertEquals("【新規追加】", response.getIncomeItemForm().getIncomeKubunName()); // 収入区分名
        assertNull(response.getIncomeItemForm().getIncomeCode()); // 収入コード：未設定
        assertNull(response.getIncomeItemForm().getIncomeKubun()); // 収入区分：未設定
        assertNull(response.getIncomeItemForm().getIncomeDetailContext()); // 収入詳細：未設定
        assertNull(response.getIncomeItemForm().getIncomeKingaku()); // 収入金額：未設定

        // Then: 収入区分選択ボックスが正しく設定されている
        assertNotNull(response.getIncomeKubunSelectList());
        assertIterableEquals(createExpectedIncomeKubunSelectList(), response.getIncomeKubunSelectList().getOptionList());

        // Then: 新規登録時は収入情報リストは空であることを確認
        assertTrue(response.getIncomeListInfo().isEmpty());
        assertNull(response.getIncomeSumKingaku());

        // Then: 支出一覧情報（画面表示用）が5件生成されている
        assertEquals(5, response.getExpenditureListInfo().size());

        // Then: 支出一覧情報の内容を検証（支払日順で表示）
        // 【1番目】家賃 - 支出区分='1'の場合、支出名はそのまま表示
        var firstExpenditure = response.getExpenditureListInfo().get(0);
        assertEquals("家賃", firstExpenditure.getSisyutuItemName()); // 支出項目名（SISYUTU_ITEM_CODE='0030'）
        assertEquals(17, firstExpenditure.getExpenditureCode().length()); // 支出コード（仮登録値）
        assertEquals("家賃", firstExpenditure.getExpenditureName()); // 支出名
        assertEquals("家賃支払詳細", firstExpenditure.getExpenditureDetailContext()); // 支出詳細
        assertEquals("01日", firstExpenditure.getSiharaiDate()); // 支払日
        assertEquals("62,000円", firstExpenditure.getShiharaiKingaku()); // 支払金額

        // 【2番目】水道代 - 支出区分='1'の場合、支出名はそのまま表示
        var secondExpenditure = response.getExpenditureListInfo().get(1);
        assertEquals("水道代", secondExpenditure.getSisyutuItemName()); // 支出項目名（SISYUTU_ITEM_CODE='0040'）
        assertEquals("水道代", secondExpenditure.getExpenditureName()); // 支出名
        assertEquals("水道代詳細(無駄遣いB)", secondExpenditure.getExpenditureDetailContext()); // 支出詳細
        assertEquals("20日", secondExpenditure.getSiharaiDate()); // 支払日
        assertEquals("5,000円", secondExpenditure.getShiharaiKingaku()); // 支払金額

        // 【3番目】通信費(無駄遣いB) - 支出区分='2'の場合、支出名の先頭に【無駄遣いB】が付加される
        var thirdExpenditure = response.getExpenditureListInfo().get(2);
        assertEquals("通信費", thirdExpenditure.getSisyutuItemName()); // 支出項目名（SISYUTU_ITEM_CODE='0039'）
        assertEquals("【無駄遣いB】通信費(無駄遣いB)", thirdExpenditure.getExpenditureName()); // 支出名（支出区分名が先頭に付加）
        assertEquals("携帯電話・インターネット詳細", thirdExpenditure.getExpenditureDetailContext()); // 支出詳細
        assertEquals("25日", thirdExpenditure.getSiharaiDate()); // 支払日
        assertEquals("7,000円", thirdExpenditure.getShiharaiKingaku()); // 支払金額

        // 【4番目】電気代(無駄遣いB) - 支出区分='2'の場合、支出名の先頭に【無駄遣いB】が付加される
        var fourthExpenditure = response.getExpenditureListInfo().get(3);
        assertEquals("電気代", fourthExpenditure.getSisyutuItemName()); // 支出項目名（SISYUTU_ITEM_CODE='0037'）
        assertEquals("【無駄遣いB】電気代(無駄遣いB)", fourthExpenditure.getExpenditureName()); // 支出名（支出区分名が先頭に付加）
        assertEquals("電気代詳細", fourthExpenditure.getExpenditureDetailContext()); // 支出詳細
        assertEquals("31日", fourthExpenditure.getSiharaiDate()); // 支払日
        assertEquals("10,000円", fourthExpenditure.getShiharaiKingaku()); // 支払金額

        // 【5番目】ガス代(無駄遣いC) - 支出区分='3'の場合、支出名の先頭に【無駄遣いC】が付加される
        var fifthExpenditure = response.getExpenditureListInfo().get(4);
        assertEquals("ガス代", fifthExpenditure.getSisyutuItemName()); // 支出項目名（SISYUTU_ITEM_CODE='0038'）
        assertEquals("【無駄遣いC】ガス代(無駄遣いC)", fifthExpenditure.getExpenditureName()); // 支出名（支出区分名が先頭に付加）
        assertEquals("ガス代詳細", fifthExpenditure.getExpenditureDetailContext()); // 支出詳細
        assertEquals("31日", fifthExpenditure.getSiharaiDate()); // 支払日
        assertEquals("8,000円", fifthExpenditure.getShiharaiKingaku()); // 支払金額

        // Then: 支出金額合計が正しく計算されている
        // 62,000 + 5,000 + 7,000 + 10,000 + 8,000 = 92,000
        assertEquals("92,000円", response.getExpenditureSumKingaku());

        // Then: 必須登録データ未登録のためメッセージが8件存在
        // ※メッセージ内容の詳細確認は別テストで実施
        assertEquals(8, response.getMessagesList().size());
    }

    /**
     *<pre>
     * テスト③：正常系：新規登録_セッションデータ確認_11月固定費
     *
     * 【検証内容】
     * ・対象年月202511（11月）の固定費データから5件の支出情報が生成されること
     * ・対象となる固定費支払月（毎月/11月/奇数月/その他任意）の固定費のみ取得されること
     * ・支払日変換：'00'→'01'（月初め）、'31'→'30'（11月末日）、'40'→'30'（11月末日）が正しく行われること
     * ・支出詳細生成パターンの確認：パターン1(両方NULL→空文字)・パターン3(OPTIONAL_CONTEXTのみ→任意詳細)・
     *   パターン4(両方あり→詳細値/任意詳細値)が正しく設定されること
     * ・支出区分変換：固定費名による判定（FIXED_COST_DETAIL_CONTEXTの値は無関係）が正しく行われること
     * ・固定費区分='2'の場合、0円開始設定フラグがtrueになること
     *</pre>
     */
    @Test
    @Sql(scripts = {
        "/sql/initsql/schema_test.sql",
        "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest.sql",
        "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest_FixedCost_202511.sql"
    }, config = @SqlConfig(encoding = "UTF-8"))
    @DisplayName("正常系：新規登録_セッションデータ確認_11月固定費 - 202511")
    void testReadInitInfo_NormalCase_SessionData_November() {
        // Given: テストユーザ、対象年月202511（11月）
        // 対象となるFIXED_COST_SHIHARAI_TUKI: '00'（毎月）, '11'（11月）, '20'（奇数月）, '40'（その他任意）
        // 対象外: '09'（9月）, '30'（偶数月）
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        // When: 新規登録画面を表示
        IncomeAndExpenditureRegistResponse response = useCase.readInitInfo(user, targetYearMonth);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);

        // Then: 固定費から支出情報が自動生成されている（5件）
        assertNotNull(response.getExpenditureRegistItemList());
        assertEquals(5, response.getExpenditureRegistItemList().size());

        // Then: 支出情報の内容を検証（支払日順：00→20→25→31→40）
        //
        // 【1番目】家賃 - FIXED_COST_SHIHARAI_TUKI='20'(奇数月), DAY='00'(月初め), KUBUN='1'
        // 固定費名(FIXED_COST_NAME)の値「家賃」に「無駄遣いB」「無駄遣いC」のどちらも含まれない場合、支出区分は'1'：無駄遣いなしが設定される
        // 固定費支払日(FIXED_COST_SHIHARAI_DAY)='00'の場合、支払日は'01'に変換され設定される
        // 固定費区分(FIXED_COST_KUBUN)='1'の場合、0円開始設定フラグはfalseが設定される
        var firstItem = response.getExpenditureRegistItemList().get(0);
        assertEquals(MyHouseholdAccountBookContent.DATA_TYPE_NEW, firstItem.getDataType()); // データタイプ設定値は常に'new'
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, firstItem.getAction()); // アクション設定値は常に'add'
        assertEquals(17, firstItem.getExpenditureCode().length());  // 支出コード(仮登録値なので長さのみ確認)
        assertEquals("0030", firstItem.getExpenditureItemCode()); // 支出項目コード（家賃）
        assertEquals("", firstItem.getEventCode());  // イベントコード設定値は常に空文字列
        assertEquals("家賃", firstItem.getExpenditureName()); // 支出名
        assertEquals("1", firstItem.getExpenditureCategory());  // 支出区分:'1'(無駄遣いなし)
        assertEquals("家賃支払詳細", firstItem.getExpenditureDetailContext()); // 支出詳細
        assertEquals("01", firstItem.getSiharaiDate()); // 支払日：00→01
        assertEquals(new BigDecimal("62000.00"), firstItem.getExpenditureKingaku()); // 支払金額
        assertEquals(false, firstItem.isClearStartFlg()); // 0円開始設定フラグ

        // 【2番目】水道代 - FIXED_COST_SHIHARAI_TUKI='11'(11月), DAY='20', KUBUN='1'
        // 固定費名(FIXED_COST_NAME)の値「水道代」に「無駄遣いB」「無駄遣いC」のどちらも含まれない場合、支出区分は'1'：無駄遣いなしが設定される
        // 固定費詳細(FIXED_COST_DETAIL_CONTEXT)の値に「無駄遣いC」が含まれているが、支出区分の判定はFIXED_COST_NAMEから行われるため、支出区分は'1'が設定される
        // 固定費支払日(FIXED_COST_SHIHARAI_DAY)='20'の場合、支払日は'20'が設定される
        // 固定費区分(FIXED_COST_KUBUN)='1'の場合、0円開始設定フラグはfalseが設定される
        var secondItem = response.getExpenditureRegistItemList().get(1);
        assertEquals(MyHouseholdAccountBookContent.DATA_TYPE_NEW, secondItem.getDataType()); // データタイプ設定値は常に'new'
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, secondItem.getAction()); // アクション設定値は常に'add'
        assertEquals(17, secondItem.getExpenditureCode().length());  // 支出コード(仮登録値なので長さのみ確認)
        assertEquals("0040", secondItem.getExpenditureItemCode()); // 支出項目コード（水道代）
        assertEquals("", secondItem.getEventCode());  // イベントコード設定値は常に空文字列
        assertEquals("水道代", secondItem.getExpenditureName()); // 支出名
        assertEquals("1", secondItem.getExpenditureCategory());  // 支出区分:'1'(無駄遣いなし) ※FIXED_COST_NAMEで判定
        assertEquals("水道代詳細(無駄遣いC)", secondItem.getExpenditureDetailContext()); // 支出詳細
        assertEquals("20", secondItem.getSiharaiDate()); // 支払日：20→20
        assertEquals(new BigDecimal("5000.00"), secondItem.getExpenditureKingaku()); // 支払金額
        assertEquals(false, secondItem.isClearStartFlg()); // 0円開始設定フラグ

        // 【3番目】通信費(無駄遣いB) - FIXED_COST_SHIHARAI_TUKI='40'(その他任意), DAY='25', KUBUN='1'
        // 固定費名(FIXED_COST_NAME)の値「通信費(無駄遣いB)」に「無駄遣いB」が含まれる場合、支出区分は'2'：無駄遣い（軽度）が設定される
        // 固定費支払日(FIXED_COST_SHIHARAI_DAY)='25'の場合、支払日は'25'が設定される
        // 固定費区分(FIXED_COST_KUBUN)='1'の場合、0円開始設定フラグはfalseが設定される
        // メモ：無駄遣いBとKUBUN='2'の組み合わせは10月固定費テストの「【4番目】電気代(無駄遣いB)」のデータで確認
        // 支出詳細：パターン4（DETAIL_CONTEXT=値あり, OPTIONAL_CONTEXT=値あり）→ "詳細値/任意詳細値"
        var thirdItem = response.getExpenditureRegistItemList().get(2);
        assertEquals(MyHouseholdAccountBookContent.DATA_TYPE_NEW, thirdItem.getDataType()); // データタイプ設定値は常に'new'
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, thirdItem.getAction()); // アクション設定値は常に'add'
        assertEquals(17, thirdItem.getExpenditureCode().length());  // 支出コード(仮登録値なので長さのみ確認)
        assertEquals("0039", thirdItem.getExpenditureItemCode()); // 支出項目コード（通信費）
        assertEquals("", thirdItem.getEventCode());  // イベントコード設定値は常に空文字列
        assertEquals("通信費(無駄遣いB)", thirdItem.getExpenditureName()); // 支出名
        assertEquals("2", thirdItem.getExpenditureCategory());  // 支出区分:'2'(無駄遣い（軽度）)
        assertEquals("携帯電話・インターネット詳細/11月通信費追加詳細", thirdItem.getExpenditureDetailContext()); // 支出詳細：パターン4
        assertEquals("25", thirdItem.getSiharaiDate()); // 支払日：25→25
        assertEquals(new BigDecimal("7000.00"), thirdItem.getExpenditureKingaku()); // 支払金額
        assertEquals(false, thirdItem.isClearStartFlg()); // 0円開始設定フラグ

        // 【4番目】電気代(無駄遣いC) - FIXED_COST_SHIHARAI_TUKI='00'(毎月), DAY='31', KUBUN='1'
        // 固定費名(FIXED_COST_NAME)の値「電気代(無駄遣いC)」に「無駄遣いC」が含まれる場合、支出区分は'3'：無駄遣い（重度）が設定される
        // 固定費支払日(FIXED_COST_SHIHARAI_DAY)='31'の場合、支払日は'30'が設定される（11月は30日まで）
        // 固定費区分(FIXED_COST_KUBUN)='1'の場合、0円開始設定フラグはfalseが設定される
        // 支出詳細：パターン3（DETAIL_CONTEXT=null, OPTIONAL_CONTEXT=値あり）→ "任意詳細値"
        var fourthItem = response.getExpenditureRegistItemList().get(3);
        assertEquals(MyHouseholdAccountBookContent.DATA_TYPE_NEW, fourthItem.getDataType()); // データタイプ設定値は常に'new'
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, fourthItem.getAction()); // アクション設定値は常に'add'
        assertEquals(17, fourthItem.getExpenditureCode().length());  // 支出コード(仮登録値なので長さのみ確認)
        assertEquals("0037", fourthItem.getExpenditureItemCode()); // 支出項目コード（電気代）
        assertEquals("", fourthItem.getEventCode());  // イベントコード設定値は常に空文字列
        assertEquals("電気代(無駄遣いC)", fourthItem.getExpenditureName()); // 支出名
        assertEquals("3", fourthItem.getExpenditureCategory());  // 支出区分:'3'(無駄遣い（重度）)
        assertEquals("11月電気代任意詳細", fourthItem.getExpenditureDetailContext()); // 支出詳細：パターン3
        assertEquals("30", fourthItem.getSiharaiDate()); // 支払日：31→30（11月は30日まで）
        assertEquals(new BigDecimal("10000.00"), fourthItem.getExpenditureKingaku()); // 支払金額
        assertEquals(false, fourthItem.isClearStartFlg()); // 0円開始設定フラグ

        // 【5番目】ガス代(無駄遣いC) - FIXED_COST_SHIHARAI_TUKI='00'(毎月), DAY='40'(月末), KUBUN='2'
        // 固定費名(FIXED_COST_NAME)の値「ガス代(無駄遣いC)」に「無駄遣いC」が含まれる場合、支出区分は'3'：無駄遣い（重度）が設定される
        // 固定費支払日(FIXED_COST_SHIHARAI_DAY)='40'の場合、支払日は'30'が設定される（11月は30日まで）
        // 固定費区分(FIXED_COST_KUBUN)='2'の場合、0円開始設定フラグはtrueが設定される
        // 支出詳細：パターン1（DETAIL_CONTEXT=null, OPTIONAL_CONTEXT=null）→ ""
        var fifthItem = response.getExpenditureRegistItemList().get(4);
        assertEquals(MyHouseholdAccountBookContent.DATA_TYPE_NEW, fifthItem.getDataType()); // データタイプ設定値は常に'new'
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, fifthItem.getAction()); // アクション設定値は常に'add'
        assertEquals(17, fifthItem.getExpenditureCode().length());  // 支出コード(仮登録値なので長さのみ確認)
        assertEquals("0038", fifthItem.getExpenditureItemCode()); // 支出項目コード（ガス代）
        assertEquals("", fifthItem.getEventCode());  // イベントコード設定値は常に空文字列
        assertEquals("ガス代(無駄遣いC)", fifthItem.getExpenditureName()); // 支出名
        assertEquals("3", fifthItem.getExpenditureCategory());  // 支出区分:'3'(無駄遣い（重度）)
        assertEquals("", fifthItem.getExpenditureDetailContext()); // 支出詳細：パターン1（両方NULL→空文字）
        assertEquals("30", fifthItem.getSiharaiDate()); // 支払日：40→30（11月は30日まで）
        assertEquals(new BigDecimal("8000.00"), fifthItem.getExpenditureKingaku()); // 支払金額
        assertEquals(true, fifthItem.isClearStartFlg()); // 0円開始設定フラグ
    }

    /**
     *<pre>
     * テスト④：正常系：新規登録_画面表示内容確認_11月固定費
     *
     * 【検証内容】
     * ・画面表示用年月（2025年11月）が正しく設定されること
     * ・収入情報フォームが新規追加の初期値で設定されること
     * ・収入区分選択ボックスが正しく設定されること
     * ・支出一覧情報が5件表示されること（支払日順：01日/20日/25日/30日/30日）
     * ・支出区分='2'の場合、支出名の先頭に【無駄遣いB】が付加されること
     * ・支出区分='3'の場合、支出名の先頭に【無駄遣いC】が付加されること
     * ・支出詳細が各パターン（パターン1～4）に応じて正しく表示されること
     * ・支出金額合計が92,000円であること
     * ・必須登録データ未登録のためメッセージが8件存在すること
     *</pre>
     */
    @Test
    @Sql(scripts = {
        "/sql/initsql/schema_test.sql",
        "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest.sql",
        "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest_FixedCost_202511.sql"
    }, config = @SqlConfig(encoding = "UTF-8"))
    @DisplayName("正常系：新規登録_画面表示内容確認_11月固定費 - 202511")
    void testReadInitInfo_NormalCase_ViewData_November() {
        // Given: テストユーザ、対象年月202511（11月）
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        // When: 新規登録画面を表示
        IncomeAndExpenditureRegistResponse response = useCase.readInitInfo(user, targetYearMonth);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);

        // Then: 画面表示用の年月が正しく設定されている
        assertEquals("2025", response.getViewYear()); // 年の値
        assertEquals("11", response.getViewMonth()); // 月の値

        // Then: 収入情報入力フォームが正しく設定されている（新規登録用の初期値）
        assertNotNull(response.getIncomeItemForm());
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, response.getIncomeItemForm().getAction()); // アクション：新規追加
        assertEquals("【新規追加】", response.getIncomeItemForm().getIncomeKubunName()); // 収入区分名
        assertNull(response.getIncomeItemForm().getIncomeCode()); // 収入コード：未設定
        assertNull(response.getIncomeItemForm().getIncomeKubun()); // 収入区分：未設定
        assertNull(response.getIncomeItemForm().getIncomeDetailContext()); // 収入詳細：未設定
        assertNull(response.getIncomeItemForm().getIncomeKingaku()); // 収入金額：未設定

        // Then: 収入区分選択ボックスが正しく設定されている
        assertNotNull(response.getIncomeKubunSelectList());
        assertIterableEquals(createExpectedIncomeKubunSelectList(), response.getIncomeKubunSelectList().getOptionList());

        // Then: 新規登録時は収入情報リストは空であることを確認
        assertTrue(response.getIncomeListInfo().isEmpty());
        assertNull(response.getIncomeSumKingaku());

        // Then: 支出一覧情報（画面表示用）が5件生成されている
        assertEquals(5, response.getExpenditureListInfo().size());

        // Then: 支出一覧情報の内容を検証（支払日順で表示）
        // 【1番目】家賃 - 支出区分='1'の場合、支出名はそのまま表示
        var firstExpenditure = response.getExpenditureListInfo().get(0);
        assertEquals("家賃", firstExpenditure.getSisyutuItemName()); // 支出項目名（SISYUTU_ITEM_CODE='0030'）
        assertEquals(17, firstExpenditure.getExpenditureCode().length()); // 支出コード（仮登録値）
        assertEquals("家賃", firstExpenditure.getExpenditureName()); // 支出名
        assertEquals("家賃支払詳細", firstExpenditure.getExpenditureDetailContext()); // 支出詳細
        assertEquals("01日", firstExpenditure.getSiharaiDate()); // 支払日
        assertEquals("62,000円", firstExpenditure.getShiharaiKingaku()); // 支払金額

        // 【2番目】水道代 - 支出区分='1'の場合、支出名はそのまま表示
        var secondExpenditure = response.getExpenditureListInfo().get(1);
        assertEquals("水道代", secondExpenditure.getSisyutuItemName()); // 支出項目名（SISYUTU_ITEM_CODE='0040'）
        assertEquals("水道代", secondExpenditure.getExpenditureName()); // 支出名
        assertEquals("水道代詳細(無駄遣いC)", secondExpenditure.getExpenditureDetailContext()); // 支出詳細
        assertEquals("20日", secondExpenditure.getSiharaiDate()); // 支払日
        assertEquals("5,000円", secondExpenditure.getShiharaiKingaku()); // 支払金額

        // 【3番目】通信費(無駄遣いB) - 支出区分='2'の場合、支出名の先頭に【無駄遣いB】が付加される
        // 支出詳細：パターン4（DETAIL_CONTEXT=値あり, OPTIONAL_CONTEXT=値あり）→ "詳細値/任意詳細値"
        var thirdExpenditure = response.getExpenditureListInfo().get(2);
        assertEquals("通信費", thirdExpenditure.getSisyutuItemName()); // 支出項目名（SISYUTU_ITEM_CODE='0039'）
        assertEquals("【無駄遣いB】通信費(無駄遣いB)", thirdExpenditure.getExpenditureName()); // 支出名（支出区分名が先頭に付加）
        assertEquals("携帯電話・インターネット詳細/11月通信費追加詳細", thirdExpenditure.getExpenditureDetailContext()); // 支出詳細：パターン4
        assertEquals("25日", thirdExpenditure.getSiharaiDate()); // 支払日
        assertEquals("7,000円", thirdExpenditure.getShiharaiKingaku()); // 支払金額

        // 【4番目】電気代(無駄遣いC) - 支出区分='3'の場合、支出名の先頭に【無駄遣いC】が付加される
        // 支出詳細：パターン3（DETAIL_CONTEXT=null, OPTIONAL_CONTEXT=値あり）→ "任意詳細値"
        var fourthExpenditure = response.getExpenditureListInfo().get(3);
        assertEquals("電気代", fourthExpenditure.getSisyutuItemName()); // 支出項目名（SISYUTU_ITEM_CODE='0037'）
        assertEquals("【無駄遣いC】電気代(無駄遣いC)", fourthExpenditure.getExpenditureName()); // 支出名（支出区分名が先頭に付加）
        assertEquals("11月電気代任意詳細", fourthExpenditure.getExpenditureDetailContext()); // 支出詳細：パターン3
        assertEquals("30日", fourthExpenditure.getSiharaiDate()); // 支払日（11月は30日まで）
        assertEquals("10,000円", fourthExpenditure.getShiharaiKingaku()); // 支払金額

        // 【5番目】ガス代(無駄遣いC) - 支出区分='3'の場合、支出名の先頭に【無駄遣いC】が付加される
        // 支出詳細：パターン1（DETAIL_CONTEXT=null, OPTIONAL_CONTEXT=null）→ ""
        var fifthExpenditure = response.getExpenditureListInfo().get(4);
        assertEquals("ガス代", fifthExpenditure.getSisyutuItemName()); // 支出項目名（SISYUTU_ITEM_CODE='0038'）
        assertEquals("【無駄遣いC】ガス代(無駄遣いC)", fifthExpenditure.getExpenditureName()); // 支出名（支出区分名が先頭に付加）
        assertEquals("", fifthExpenditure.getExpenditureDetailContext()); // 支出詳細：パターン1（両方NULL→空文字）
        assertEquals("30日", fifthExpenditure.getSiharaiDate()); // 支払日（11月は30日まで）
        assertEquals("8,000円", fifthExpenditure.getShiharaiKingaku()); // 支払金額

        // Then: 支出金額合計が正しく計算されている
        // 62,000 + 5,000 + 7,000 + 10,000 + 8,000 = 92,000
        assertEquals("92,000円", response.getExpenditureSumKingaku());

        // Then: 必須登録データ未登録のためメッセージが8件存在
        // ※メッセージ内容の詳細確認は別テストで実施
        assertEquals(8, response.getMessagesList().size());
    }

    /**
     *<pre>
     * テスト⑤：正常系：新規登録_固定費なし
     *
     * 【検証内容】
     * ・対象年月202512（12月）に該当する固定費が0件の場合、支出情報リストがNULLであること
     * ・「条件に一致する登録済み固定費が0件でした。」のメッセージが設定されること
     * ・新規登録時は収入一覧情報が空で収入金額合計がnullであること
     * ・固定費なしの場合、支出一覧情報も空で支出金額合計もnullであること
     *</pre>
     */
    @Test
    @DisplayName("正常系：新規登録画面表示_固定費なし - 202512")
    @Sql(scripts = {
            "/sql/initsql/schema_test.sql",
            "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest.sql",
            "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest_FixedCost_202512.sql"
        }, config = @SqlConfig(encoding = "UTF-8"))
    void testReadInitInfo_NormalCase_NoFixedCost() {
        // Given: テストユーザ、対象年月202512（12月）
        // 固定費テーブルには202508/202509/202510/202511用のデータが存在するが、
        // どのFIXED_COST_SHIHARAI_TUKIも12月には該当しない（'00'毎月と'40'その他任意以外は対象外）
        // この共通データでは固定費が0件となることを確認
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202512";

        // When: 新規登録画面を表示
        IncomeAndExpenditureRegistResponse response = useCase.readInitInfo(user, targetYearMonth);

        // Then: レスポンスが返却される
        assertNotNull(response);

        // Then: 固定費が0件の場合、支出情報リストはNULLであることを確認
        assertNull(response.getExpenditureRegistItemList());

        // Then: 固定費が0件の場合、「条件に一致する登録済み固定費が0件でした。」のメッセージが設定されていること
        assertFalse(response.getMessagesList().isEmpty());
        assertTrue(response.getMessagesList().stream()
            .anyMatch(msg -> msg.contains("条件に一致する登録済み固定費が0件でした")));

        // Then: 新規登録時は収入一覧情報が0件であることを確認
        assertTrue(response.getIncomeListInfo().isEmpty());

        // Then: 固定費なしの場合、支出一覧情報も0件であることを確認
        assertTrue(response.getExpenditureListInfo().isEmpty());

        // Then: 新規登録時は収入金額合計がnullであることを確認
        assertNull(response.getIncomeSumKingaku());

        // Then: 固定費なしの場合、支出金額合計がnullであることを確認
        assertNull(response.getExpenditureSumKingaku());
    }

    /**
     *<pre>
     * テスト⑥：正常系：新規登録_必須登録データ未登録時メッセージ確認
     *
     * 【検証内容】
     * ・買い物登録に必須の支出情報が固定費に登録されていない場合、警告メッセージが8件返却されること
     * ・メッセージ内容が期待する8件（飲食/外食/日用消耗品/被服費/事業流動経費/住居設備等）と一致すること
     *</pre>
     */
    @Test
    @Sql(scripts = {
        "/sql/initsql/schema_test.sql",
        "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest.sql",
        "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest_FixedCost_202510.sql"
    }, config = @SqlConfig(encoding = "UTF-8"))
    @DisplayName("正常系：新規登録_必須登録データ未登録時メッセージ確認 - 202510")
    void testReadInitInfo_NormalCase_RequiredDataNotRegisteredMessage() {
        // Given: テストユーザ、対象年月202510（10月）
        // 固定費データのみで、買い物登録必須の支出情報は未登録
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202510";

        // When: 新規登録画面を表示
        IncomeAndExpenditureRegistResponse response = useCase.readInitInfo(user, targetYearMonth);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);

        // Then: 必須登録データ未登録のためメッセージが8件存在
        assertEquals(8, response.getMessagesList().size());

        // Then: メッセージ内容を検証（買い物登録に必須の8項目）
        List<String> expectedMessages = createExpectedRequiredDataNotRegisteredMessages();
        assertIterableEquals(expectedMessages, response.getMessagesList());
    }

    /**
     *<pre>
     * テスト⑦：正常系：新規登録_全必須登録データ登録済みメッセージ確認
     *
     * 【検証内容】
     * ・買い物登録に必須の支出情報（8件）が固定費に全て登録済みの場合、警告メッセージが0件であること
     * ・固定費から支出情報が8件自動生成されること
     *</pre>
     */
    @Test
    @Sql(scripts = {
        "/sql/initsql/schema_test.sql",
        "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest.sql",
        "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest_FixedCost_202510_AllRequired.sql"
    }, config = @SqlConfig(encoding = "UTF-8"))
    @DisplayName("正常系：新規登録_全必須登録データ登録済みメッセージ確認 - 202510")
    void testReadInitInfo_NormalCase_AllRequiredDataRegistered() {
        // Given: テストユーザ、対象年月202510（10月）
        // 全ての必須支出情報（8件）が固定費に登録済み
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202510";

        // When: 新規登録画面を表示
        IncomeAndExpenditureRegistResponse response = useCase.readInitInfo(user, targetYearMonth);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);

        // Then: 固定費から支出情報が自動生成されている（8件）
        assertNotNull(response.getExpenditureRegistItemList());
        assertEquals(8, response.getExpenditureRegistItemList().size());

        // Then: 必須登録データが全て登録済みのためメッセージが0件
        assertTrue(response.getMessagesList().isEmpty());
    }

    /**
     *<pre>
     * テスト⑧：正常系：新規登録_一部必須登録データのみ登録時メッセージ確認
     *
     * 【検証内容】
     * ・必須支出情報の一部（4件）のみ固定費に登録済みの場合、固定費から4件の支出情報が生成されること
     * ・未登録の4項目分のメッセージが返却されること
     * ・メッセージ内容が期待する4件（飲食(無駄遣いB)/飲食(無駄遣いC)/被服費/事業流動経費）と一致すること
     *</pre>
     */
    @Test
    @Sql(scripts = {
        "/sql/initsql/schema_test.sql",
        "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest.sql",
        "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest_FixedCost_202510_PartialRequired.sql"
    }, config = @SqlConfig(encoding = "UTF-8"))
    @DisplayName("正常系：新規登録_一部必須登録データのみ登録時メッセージ確認 - 202510")
    void testReadInitInfo_NormalCase_PartialRequiredDataRegistered() {
        // Given: テストユーザ、対象年月202510（10月）
        // 必須支出情報の一部（4件）のみ固定費に登録済み
        // 登録済み: 飲食(無駄遣いなし), 外食, 日用消耗品, 住居設備
        // 未登録: 飲食(無駄遣いB), 飲食(無駄遣いC), 被服費, 事業流動経費
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202510";

        // When: 新規登録画面を表示
        IncomeAndExpenditureRegistResponse response = useCase.readInitInfo(user, targetYearMonth);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);

        // Then: 固定費から支出情報が自動生成されている（4件）
        assertNotNull(response.getExpenditureRegistItemList());
        assertEquals(4, response.getExpenditureRegistItemList().size());

        // Then: 未登録の必須登録データのためメッセージが4件存在
        assertEquals(4, response.getMessagesList().size());

        // Then: メッセージ内容を検証（未登録の4項目）
        List<String> expectedMessages = createExpectedPartialRequiredDataNotRegisteredMessages();
        assertIterableEquals(expectedMessages, response.getMessagesList());
    }

    // ========================================
    // 正常系テスト - readUpdateInfo
    // ========================================

    /**
     *<pre>
     * テスト⑨：正常系：更新_セッションデータ確認
     *
     * 【検証内容】
     * ・対象年月202511のDB登録済み収入情報が2件取得されること（対象外月は除外）
     * ・各収入のデータタイプ=ロード(load)・アクション=更新なし(non-update)・収入コード・収入区分・詳細・金額が正しいこと
     * ・対象年月202511のDB登録済み支出情報が3件取得されること（対象外月は除外）
     * ・各支出のデータタイプ=ロード(load)・アクション=更新なし・支出コード・支出項目コード・支払日（NULL含む）・金額が正しいこと
     *</pre>
     */
    @Test
    @DisplayName("正常系：更新_セッションデータ確認 - 202511")
    void testReadUpdateInfo_NormalCase_SessionData() {
        // Given: テストユーザ、対象年月202511（DB登録済み）
        // 対象年月(202511)のデータのみ取得され、対象外年月(202510)のデータは取得されないこと
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        // When: 更新画面を表示
        IncomeAndExpenditureRegistResponse response = useCase.readUpdateInfo(user, targetYearMonth);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);

        // ========================================
        // 収入情報（セッションデータ）の検証
        // ========================================
        // Then: 収入情報が2件取得されていること（対象外の10月分は含まれない）
        assertNotNull(response.getIncomeRegistItemList());
        assertEquals(2, response.getIncomeRegistItemList().size());

        // 【1件目】収入コード='01': 給与
        var firstIncome = response.getIncomeRegistItemList().get(0);
        assertEquals(MyHouseholdAccountBookContent.DATA_TYPE_LOAD, firstIncome.getDataType()); // データタイプ：DBロード
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE, firstIncome.getAction()); // アクション：更新なし
        assertEquals("01", firstIncome.getIncomeCode()); // 収入コード
        assertEquals("1", firstIncome.getIncomeCategory()); // 収入区分：給与
        assertEquals("11月給与", firstIncome.getIncomeDetailContext()); // 収入詳細
        assertEquals(new BigDecimal("350000.00"), firstIncome.getIncomeKingaku()); // 収入金額

        // 【2件目】収入コード='02': 副業
        var secondIncome = response.getIncomeRegistItemList().get(1);
        assertEquals(MyHouseholdAccountBookContent.DATA_TYPE_LOAD, secondIncome.getDataType()); // データタイプ：DBロード
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE, secondIncome.getAction()); // アクション：更新なし
        assertEquals("02", secondIncome.getIncomeCode()); // 収入コード
        assertEquals("2", secondIncome.getIncomeCategory()); // 収入区分：副業
        assertEquals("11月副業収入", secondIncome.getIncomeDetailContext()); // 収入詳細
        assertEquals(new BigDecimal("30000.00"), secondIncome.getIncomeKingaku()); // 収入金額

        // ========================================
        // 支出情報（セッションデータ）の検証
        // ========================================
        // Then: 支出情報が3件取得されていること（対象外の10月分は含まれない）
        assertNotNull(response.getExpenditureRegistItemList());
        assertEquals(3, response.getExpenditureRegistItemList().size());

        // 【1件目】支出コード='001': 電気代
        // 支出区分='1'（無駄遣いなし）の場合
        var firstExpenditure = response.getExpenditureRegistItemList().get(0);
        assertEquals(MyHouseholdAccountBookContent.DATA_TYPE_LOAD, firstExpenditure.getDataType()); // データタイプ：DBロード
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE, firstExpenditure.getAction()); // アクション：更新なし
        assertEquals("001", firstExpenditure.getExpenditureCode()); // 支出コード
        assertEquals("0037", firstExpenditure.getExpenditureItemCode()); // 支出項目コード（電気代）
        assertNull(firstExpenditure.getEventCode()); // イベントコード（DBでnull→nullが設定される）
        assertEquals("電気代", firstExpenditure.getExpenditureName()); // 支出名
        assertEquals("1", firstExpenditure.getExpenditureCategory()); // 支出区分：無駄遣いなし
        assertEquals("電気代支払", firstExpenditure.getExpenditureDetailContext()); // 支出詳細
        assertEquals("30", firstExpenditure.getSiharaiDate()); // 支払日：30日
        assertEquals(new BigDecimal("12000.00"), firstExpenditure.getExpenditureKingaku()); // 支払金額
        assertEquals(false, firstExpenditure.isClearStartFlg()); // 0円開始設定フラグ

        // 【2件目】支出コード='002': ガス代(無駄遣いB)
        // 支出区分='2'（無駄遣いB）、支払日=NULL のケース
        var secondExpenditure = response.getExpenditureRegistItemList().get(1);
        assertEquals(MyHouseholdAccountBookContent.DATA_TYPE_LOAD, secondExpenditure.getDataType()); // データタイプ：DBロード
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE, secondExpenditure.getAction()); // アクション：更新なし
        assertEquals("002", secondExpenditure.getExpenditureCode()); // 支出コード
        assertEquals("0038", secondExpenditure.getExpenditureItemCode()); // 支出項目コード（ガス代）
        assertNull(secondExpenditure.getEventCode()); // イベントコード（DBでnull→nullが設定される）
        assertEquals("ガス代(無駄遣いB)", secondExpenditure.getExpenditureName()); // 支出名
        assertEquals("2", secondExpenditure.getExpenditureCategory()); // 支出区分：無駄遣いB
        assertEquals("ガス代支払詳細", secondExpenditure.getExpenditureDetailContext()); // 支出詳細
        assertNull(secondExpenditure.getSiharaiDate()); // 支払日：DBでnull→nullが設定される
        assertEquals(new BigDecimal("10000.00"), secondExpenditure.getExpenditureKingaku()); // 支払金額
        assertEquals(false, secondExpenditure.isClearStartFlg()); // 0円開始設定フラグ

        // 【3件目】支出コード='003': 水道代
        var thirdExpenditure = response.getExpenditureRegistItemList().get(2);
        assertEquals(MyHouseholdAccountBookContent.DATA_TYPE_LOAD, thirdExpenditure.getDataType()); // データタイプ：DBロード
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE, thirdExpenditure.getAction()); // アクション：更新なし
        assertEquals("003", thirdExpenditure.getExpenditureCode()); // 支出コード
        assertEquals("0040", thirdExpenditure.getExpenditureItemCode()); // 支出項目コード（水道代）
        assertNull(thirdExpenditure.getEventCode()); // イベントコード（DBでnull→nullが設定される）
        assertEquals("水道代", thirdExpenditure.getExpenditureName()); // 支出名
        assertEquals("1", thirdExpenditure.getExpenditureCategory()); // 支出区分：無駄遣いなし
        assertEquals("水道代支払", thirdExpenditure.getExpenditureDetailContext()); // 支出詳細
        assertEquals("20", thirdExpenditure.getSiharaiDate()); // 支払日：20日
        assertEquals(new BigDecimal("8000.00"), thirdExpenditure.getExpenditureKingaku()); // 支払金額
        assertEquals(false, thirdExpenditure.isClearStartFlg()); // 0円開始設定フラグ
    }

    /**
     *<pre>
     * テスト⑩：正常系：更新_画面表示内容確認
     *
     * 【検証内容】
     * ・画面表示用年月（2025年11月）が正しく設定されること
     * ・更新画面では収入情報フォームおよび収入区分選択ボックスが設定されないこと（nullであること）
     * ・画面表示用収入一覧が2件、各収入の区分名（給料/副業）・詳細・金額（カンマ区切り）が正しいこと
     * ・収入合計金額が「380,000円」であること
     * ・画面表示用支出一覧が3件、各支出の項目名・支出名（無駄遣いB付加あり）・詳細・支払日・金額が正しいこと
     * ・支払日がNULLの場合、画面表示が空文字であること
     * ・支出合計金額が「30,000円」であること
     * ・更新画面（正常系）ではメッセージなしであること
     *</pre>
     */
    @Test
    @DisplayName("正常系：更新_画面表示内容確認 - 202511")
    void testReadUpdateInfo_NormalCase_ViewData() {
        // Given: テストユーザ、対象年月202511（DB登録済み）
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        // When: 更新画面を表示
        IncomeAndExpenditureRegistResponse response = useCase.readUpdateInfo(user, targetYearMonth);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);

        // Then: 画面表示用の年月が正しく設定されている
        assertEquals("2025", response.getViewYear()); // 年の値
        assertEquals("11", response.getViewMonth()); // 月の値

        // Then: 更新画面では収入情報入力フォームは設定されない
        // （readInitInfoとは異なり、readUpdateInfoではIncomeItemFormを設定しない）
        assertNull(response.getIncomeItemForm());

        // Then: 更新画面では収入区分選択ボックスは設定されない
        // （readInitInfoとは異なり、readUpdateInfoではIncomeKubunSelectListを設定しない）
        assertNull(response.getIncomeKubunSelectList());

        // ========================================
        // 収入一覧（画面表示用）の検証
        // ========================================
        // Then: 収入一覧が2件表示されている
        assertEquals(2, response.getIncomeListInfo().size());

        // 【1件目】給与
        var firstIncomeView = response.getIncomeListInfo().get(0);
        assertEquals("01", firstIncomeView.getIncomeCode()); // 収入コード
        assertEquals("給料", firstIncomeView.getIncomeKubunName()); // 収入区分名（コード値から変換）
        assertEquals("11月給与", firstIncomeView.getIncomeDetailContext()); // 収入詳細
        assertEquals("350,000円", firstIncomeView.getIncomeKingaku()); // 収入金額（カンマ区切り）

        // 【2件目】副業
        var secondIncomeView = response.getIncomeListInfo().get(1);
        assertEquals("02", secondIncomeView.getIncomeCode()); // 収入コード
        assertEquals("副業", secondIncomeView.getIncomeKubunName()); // 収入区分名（コード値から変換）
        assertEquals("11月副業収入", secondIncomeView.getIncomeDetailContext()); // 収入詳細
        assertEquals("30,000円", secondIncomeView.getIncomeKingaku()); // 収入金額（カンマ区切り）

        // Then: 収入合計金額が正しく計算されている
        // 350,000 + 30,000 = 380,000
        assertEquals("380,000円", response.getIncomeSumKingaku());

        // ========================================
        // 支出一覧（画面表示用）の検証
        // ========================================
        // Then: 支出一覧が3件表示されている（DBからの取得順：支出コード順）
        // ※setIncomeAndExpenditureInfoListではソートを行わず、expenditureRegistItemListの順序がそのまま使用される
        assertEquals(3, response.getExpenditureListInfo().size());

        // 【1番目】電気代 - 支出コード='001'
        var firstExpenditureView = response.getExpenditureListInfo().get(0);
        assertEquals("電気代", firstExpenditureView.getSisyutuItemName()); // 支出項目名
        assertEquals("001", firstExpenditureView.getExpenditureCode()); // 支出コード
        assertEquals("電気代", firstExpenditureView.getExpenditureName()); // 支出名（支出区分='1'なのでそのまま）
        assertEquals("電気代支払", firstExpenditureView.getExpenditureDetailContext()); // 支出詳細
        assertEquals("30日", firstExpenditureView.getSiharaiDate()); // 支払日
        assertEquals("12,000円", firstExpenditureView.getShiharaiKingaku()); // 支払金額

        // 【2番目】ガス代(無駄遣いB) - 支出コード='002'
        // 支出区分='2'の場合、支出名の先頭に【無駄遣いB】が付加される
        // 支払日=NULLの場合、空文字が表示される
        var secondExpenditureView = response.getExpenditureListInfo().get(1);
        assertEquals("ガス代", secondExpenditureView.getSisyutuItemName()); // 支出項目名
        assertEquals("002", secondExpenditureView.getExpenditureCode()); // 支出コード
        assertEquals("【無駄遣いB】ガス代(無駄遣いB)", secondExpenditureView.getExpenditureName()); // 支出名（支出区分名が先頭に付加）
        assertEquals("ガス代支払詳細", secondExpenditureView.getExpenditureDetailContext()); // 支出詳細
        assertEquals("", secondExpenditureView.getSiharaiDate()); // 支払日：DBでnull→空文字が表示
        assertEquals("10,000円", secondExpenditureView.getShiharaiKingaku()); // 支払金額

        // 【3番目】水道代 - 支出コード='003'
        var thirdExpenditureView = response.getExpenditureListInfo().get(2);
        assertEquals("水道代", thirdExpenditureView.getSisyutuItemName()); // 支出項目名
        assertEquals("003", thirdExpenditureView.getExpenditureCode()); // 支出コード
        assertEquals("水道代", thirdExpenditureView.getExpenditureName()); // 支出名（支出区分='1'なのでそのまま）
        assertEquals("水道代支払", thirdExpenditureView.getExpenditureDetailContext()); // 支出詳細
        assertEquals("20日", thirdExpenditureView.getSiharaiDate()); // 支払日
        assertEquals("8,000円", thirdExpenditureView.getShiharaiKingaku()); // 支払金額

        // Then: 支出合計金額が正しく計算されている
        // 12,000 + 10,000 + 8,000 = 30,000
        assertEquals("30,000円", response.getExpenditureSumKingaku());

        // Then: 更新画面ではメッセージなし（正常系）
        assertTrue(response.getMessagesList().isEmpty());
    }

    /**
     *<pre>
     * テスト⑪：異常系：更新_収入テーブル情報なし・支出テーブル情報あり → 例外
     *
     * 【検証内容】
     * ・対象年月(202509)の収入情報が収入テーブルに存在しない場合、MyHouseholdAccountBookRuntimeExceptionがスローされること
     * ・例外メッセージに「更新対象の収入情報が収入テーブルに存在しません」が含まれること
     *</pre>
     */
    @Test
    @Sql(scripts = {
        "/sql/initsql/schema_test.sql",
        "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest.sql",
        "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest_UpdateInfo_NoIncome.sql"
    }, config = @SqlConfig(encoding = "UTF-8"))
    @DisplayName("異常系：更新_収入テーブル情報なし・支出テーブル情報あり → 例外")
    void testReadUpdateInfo_Exception_NoIncomeWithExpenditure() {
        // Given: テストユーザ、対象年月202509（収入情報なし、支出情報あり）
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202509";

        // When & Then: 収入情報が未登録のため例外が発生する
        MyHouseholdAccountBookRuntimeException exception = assertThrows(
            MyHouseholdAccountBookRuntimeException.class,
            () -> useCase.readUpdateInfo(user, targetYearMonth)
        );

        // Then: 例外メッセージに収入情報未登録の旨が含まれること
        assertTrue(exception.getMessage().contains("更新対象の収入情報が収入テーブルに存在しません"));
    }

    /**
     *<pre>
     * テスト⑫：正常系：更新_収入テーブル情報あり・支出テーブル情報なし → 支出リスト空
     *
     * 【検証内容】
     * ・対象年月(202508)に収入情報(1件)はあるが支出情報がない場合、正常終了すること
     * ・収入登録情報リストが1件（データタイプ=ロード・アクション=更新なし・収入コード・区分・詳細・金額が正しい）で設定されること
     * ・支出登録情報リストが空リストであること
     * ・画面表示用収入一覧が1件・支出一覧が空であること
     * ・支出情報なしの場合、支出金額合計がnullであること
     *</pre>
     */
    @Test
    @Sql(scripts = {
        "/sql/initsql/schema_test.sql",
        "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest.sql",
        "/com/yonetani/webapp/accountbook/application/usecase/account/regist/IncomeAndExpenditureInitIntegrationTest_UpdateInfo_NoExpenditure.sql"
    }, config = @SqlConfig(encoding = "UTF-8"))
    @DisplayName("正常系：更新_収入テーブル情報あり・支出テーブル情報なし → 支出リスト空")
    void testReadUpdateInfo_NormalCase_IncomeOnlyNoExpenditure() {
        // Given: テストユーザ、対象年月202508（収入情報あり、支出情報なし）
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202508";

        // When: 更新画面を表示
        IncomeAndExpenditureRegistResponse response = useCase.readUpdateInfo(user, targetYearMonth);

        // Then: レスポンスが正しく返却される
        assertNotNull(response);

        // Then: 収入情報が1件取得されていること
        assertNotNull(response.getIncomeRegistItemList());
        assertEquals(1, response.getIncomeRegistItemList().size());

        // 収入情報の検証
        var incomeItem = response.getIncomeRegistItemList().get(0);
        assertEquals(MyHouseholdAccountBookContent.DATA_TYPE_LOAD, incomeItem.getDataType()); // データタイプ：DBロード
        assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE, incomeItem.getAction()); // アクション：更新なし
        assertEquals("01", incomeItem.getIncomeCode()); // 収入コード
        assertEquals("1", incomeItem.getIncomeCategory()); // 収入区分：給与
        assertEquals("8月給与（支出なし確認用）", incomeItem.getIncomeDetailContext()); // 収入詳細
        assertEquals(new BigDecimal("280000.00"), incomeItem.getIncomeKingaku()); // 収入金額

        // Then: 支出情報が0件（空リスト）であることを確認
        assertNotNull(response.getExpenditureRegistItemList());
        assertTrue(response.getExpenditureRegistItemList().isEmpty());

        // Then: 画面表示用の収入一覧が1件であることを確認
        assertEquals(1, response.getIncomeListInfo().size());

        // Then: 画面表示用の支出一覧が0件であることを確認
        assertTrue(response.getExpenditureListInfo().isEmpty());

        // Then: 収入金額合計が正しく設定されていること
        assertEquals("280,000円", response.getIncomeSumKingaku());

        // Then: 支出情報なしの場合、支出金額合計がnullであることを確認
        assertNull(response.getExpenditureSumKingaku());
    }

    // ========================================
    // 正常系テスト - readIncomeAndExpenditureInfoList
    // ========================================

    /**
     *<pre>
     * テスト⑬：正常系：収入・支出一覧再表示（削除アクション設定データの除外確認含む）
     *
     * 【検証内容】
     * ・アクション=削除が設定された収入データは画面表示用一覧に含まれないこと
     * ・アクション=削除が設定された支出データは画面表示用一覧に含まれないこと
     * ・削除アクション以外のデータ（収入1件・支出1件）のみ画面表示用一覧に表示されること
     *</pre>
     */
    @Test
    @DisplayName("正常系：収入・支出一覧再表示（削除アクション設定データの除外確認含む）")
    void testReadIncomeAndExpenditureInfoList_NormalCase() {
        // Given: テストユーザ、対象年月、セッションの収入・支出リスト
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        // セッションデータのモック（収入情報リスト）
        List<IncomeRegistItem> incomeRegistItemList = new ArrayList<>();
        incomeRegistItemList.add(IncomeRegistItem.from(
        		MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE, "01", "1", "テスト収入詳細", new BigDecimal("350000")));
        // 削除アクションが設定された収入データ（画面表示から除外されるべき）
        incomeRegistItemList.add(IncomeRegistItem.from(
        		MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_DELETE, "02", "2", "削除対象収入", new BigDecimal("100000")));

        // セッションデータのモック（支出情報リスト）
        List<ExpenditureRegistItem> expenditureRegistItemList = new ArrayList<>();
        expenditureRegistItemList.add(ExpenditureRegistItem.from(
        		MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_NON_UPDATE, "001", "0001", "", "電気代", "1",
            "支払詳細", "30", new BigDecimal("12000"), false));
        // 削除アクションが設定された支出データ（画面表示から除外されるべき）
        expenditureRegistItemList.add(ExpenditureRegistItem.from(
        		MyHouseholdAccountBookContent.DATA_TYPE_LOAD, MyHouseholdAccountBookContent.ACTION_TYPE_DELETE, "002", "0037", "", "削除対象支出", "1",
            "削除対象詳細", "15", new BigDecimal("5000"), false));

        // When: 収入・支出一覧を再表示
        IncomeAndExpenditureRegistResponse response = useCase.readIncomeAndExpenditureInfoList(
            user, targetYearMonth, incomeRegistItemList, expenditureRegistItemList);

        // Then: レスポンスが返却される
        assertNotNull(response);

        // Then: 削除アクションの収入データは画面表示用一覧に含まれないこと
        // セッションデータには2件（表示1件＋削除1件）だが、画面表示用は1件のみ
        assertEquals(1, response.getIncomeListInfo().size());
        assertEquals("01", response.getIncomeListInfo().get(0).getIncomeCode());

        // Then: 削除アクションの支出データは画面表示用一覧に含まれないこと
        // セッションデータには2件（表示1件＋削除1件）だが、画面表示用は1件のみ
        assertEquals(1, response.getExpenditureListInfo().size());
        assertEquals("001", response.getExpenditureListInfo().get(0).getExpenditureCode());
    }

    // ========================================
    // 正常系テスト - readRegistCheckErrorSetInfo
    // ========================================

    /**
     *<pre>
     * テスト⑭：正常系：入力確認エラー_収入未登録
     *
     * 【検証内容】
     * ・セッションの収入登録リストが空の場合、エラーメッセージが設定されること
     * ・エラーメッセージに「収入情報が未登録です。」が含まれること
     *</pre>
     */
    @Test
    @DisplayName("正常系：入力確認エラー_収入未登録")
    void testReadRegistCheckErrorSetInfo_NormalCase_NoIncomeData() {
        // Given: テストユーザ、対象年月、セッションの収入・支出リスト（収入なし）
        LoginUserInfo user = createLoginUser();
        String targetYearMonth = "202511";

        // セッションデータのモック（収入情報リストは空）
        List<IncomeRegistItem> incomeRegistItemList = new ArrayList<>();

        // セッションデータのモック（支出情報リスト）
        List<ExpenditureRegistItem> expenditureRegistItemList = new ArrayList<>();
        expenditureRegistItemList.add(ExpenditureRegistItem.from(
        		MyHouseholdAccountBookContent.DATA_TYPE_NEW, MyHouseholdAccountBookContent.ACTION_TYPE_ADD, "2026011210000001", "0001", "", "電気代", "1",
            "支払詳細", "20251130", new BigDecimal("10000"), false));

        // When: 入力確認時のエラー処理
        IncomeAndExpenditureRegistResponse response = useCase.readRegistCheckErrorSetInfo(
            user, targetYearMonth, incomeRegistItemList, expenditureRegistItemList);

        // Then: レスポンスが返却される
        assertNotNull(response);

        // Then: エラーメッセージが設定されている
        // ※セッションデータ⇒画面表示データの変換検証はreadInitInfo/readUpdateInfoテストで網羅済み
        assertFalse(response.getMessagesList().isEmpty());
        assertTrue(response.getMessagesList().stream()
            .anyMatch(msg -> msg.contains("収入情報が未登録です。")));
    }
    
	/**
	 *<pre>
	 * 必須登録データ未登録時のメッセージ期待値(固定値)を作成
	 *
	 *</pre>
	 * @return 必須登録データ未登録時のメッセージリスト
	 *
	 */
	private List<String> createExpectedRequiredDataNotRegisteredMessages() {
		List<String> messages = new ArrayList<>();
		messages.add("飲食(無駄づかいなし)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で飲食(無駄使いなし)の支出情報を登録してから再度実行してください。");
		messages.add("飲食(無駄遣いB)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で飲食(無駄遣いB)の支出情報を登録してから再度実行してください。");
		messages.add("飲食(無駄使いC)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で飲食(無駄使いC)の支出情報を登録してから再度実行してください。");
		messages.add("一人プチ贅沢・外食(無駄づかいなし)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で一人プチ贅沢・外食(無駄づかいなし)の支出情報を登録してから再度実行してください。");
		messages.add("日用消耗品(無駄づかいなし)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で日用消耗品(無駄づかいなし)の支出情報を登録してから再度実行してください。");
		messages.add("被服費(無駄づかいなし)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で被服費(無駄づかいなし)の支出情報を登録してから再度実行してください。");
		messages.add("仕事(流動経費)(無駄づかいなし)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で仕事(流動経費)(無駄づかいなし)の支出情報を登録してから再度実行してください。");
		messages.add("住居設備(無駄づかいなし)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で住居設備(無駄づかいなし)の支出情報を登録してから再度実行してください。");
		return messages;
	}

	/**
	 *<pre>
	 * 一部必須登録データ未登録時のメッセージ期待値(固定値)を作成
	 *
	 * 登録済み: 飲食(無駄遣いなし), 外食, 日用消耗品, 住居設備
	 * 未登録: 飲食(無駄遣いB), 飲食(無駄遣いC), 被服費, 事業流動経費
	 *
	 *</pre>
	 * @return 一部必須登録データ未登録時のメッセージリスト（4件）
	 *
	 */
	private List<String> createExpectedPartialRequiredDataNotRegisteredMessages() {
		List<String> messages = new ArrayList<>();
		messages.add("飲食(無駄遣いB)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で飲食(無駄遣いB)の支出情報を登録してから再度実行してください。");
		messages.add("飲食(無駄使いC)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で飲食(無駄使いC)の支出情報を登録してから再度実行してください。");
		messages.add("被服費(無駄づかいなし)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で被服費(無駄づかいなし)の支出情報を登録してから再度実行してください。");
		messages.add("仕事(流動経費)(無駄づかいなし)に対応する支出情報が登録されていないか複数登録されています。支出登録画面で仕事(流動経費)(無駄づかいなし)の支出情報を登録してから再度実行してください。");
		return messages;
	}

	/**
	 *<pre>
	 * 収入区分選択ボックスの期待値(固定値)を作成
	 *
	 *</pre>
	 * @return 収入区分選択ボックスの期待値リスト
	 *
	 */
	private List<OptionItem> createExpectedIncomeKubunSelectList() {
		List<OptionItem> optionList = new ArrayList<>();
		optionList.add(OptionItem.from("", "収入区分を選択してください"));
		optionList.add(OptionItem.from("1", "給料"));
		optionList.add(OptionItem.from("2", "副業"));
		optionList.add(OptionItem.from("3", "積立からの取崩し"));
		optionList.add(OptionItem.from("9", "その他任意"));
		
		return optionList;
	}
}
