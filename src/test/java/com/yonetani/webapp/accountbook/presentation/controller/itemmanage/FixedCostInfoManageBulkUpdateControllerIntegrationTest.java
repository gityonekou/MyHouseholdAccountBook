/**
 * FixedCostInfoManageController の一括更新エンドポイント統合テストです
 * 以下の動作を確認します。
 * GET ：/bulkupdateload/
 * POST：/bulkupdatead/
 *
 * <pre>
 * [テスト方針]
 * ・FixedCostInquiryUseCase / FixedCostRegistConfirmUseCase は本物のSpring Beanを使用
 * ・LoginUserSession はモック化（セッションスコープBean）
 * ・standaloneSetup でControllerをセットアップ
 *
 * [テストシナリオ]
 * ①  正常系：GET /bulkupdateload/?baseFixedCostCode=0001 → 一括更新画面表示（対象2件）
 * ②  異常系：POST /bulkupdate/ actionBulkUpdate バリデーションNG(shiharaiDay空) → 一括更新画面に戻る
 * ③  異常系：POST /bulkupdate/ actionBulkUpdate バリデーションNG(チェックボックス未選択) → 一括更新画面に戻る
 * ④  正常系：POST /bulkupdate/ actionBulkUpdate 成功 → updateComplete/ にリダイレクト
 * ⑤  正常系：POST /bulkupdate/ actionCancel → 処理選択画面(0001)へ（兄弟固定費あり）
 *
 * [テストデータ]
 * 固定費3件:
 *   0001: 家賃   (0030:家賃, 確定, 毎月, 27日, 60,000円) ← 兄弟固定費(その1)
 *   0002: 共益費 (0030:家賃, 確定, 毎月, 27日,  8,000円) ← 兄弟固定費(その2)
 *   0003: 電気代概算 (0037:電気代, 予定, 毎月, 27日, 12,000円) ← 兄弟なし
 * </pre>
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/05/01 : 1.01.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller.itemmanage;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.yonetani.webapp.accountbook.application.usecase.itemmanage.fixedcost.FixedCostInquiryUseCase;
import com.yonetani.webapp.accountbook.application.usecase.itemmanage.fixedcost.FixedCostRegistConfirmUseCase;
import com.yonetani.webapp.accountbook.presentation.controller.MyHouseholdAccountBookControllerAdvice;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

/**
 *<pre>
 * FixedCostInfoManageController の一括更新エンドポイント統合テストです。
 *
 * [テストデータ]
 * 固定費3件: 0001:家賃(0030), 0002:共益費(0030) ← 兄弟, 0003:電気代概算(0037)
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.01)
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Sql(scripts = {
	"/sql/initsql/schema_test.sql",
	"/com/yonetani/webapp/accountbook/application/usecase/itemmanage/fixedcost/FixedCostBulkUpdateIntegrationTest.sql"
}, config = @SqlConfig(encoding = "UTF-8"))
@DisplayName("固定費情報管理 一括更新 Controllerテスト（統合テスト）")
public class FixedCostInfoManageBulkUpdateControllerIntegrationTest {

	// MVCモック
	private MockMvc mockMvc;

	// UseCase(参照系)(本物のSpring Bean)
	@Autowired
	private FixedCostInquiryUseCase fixedCostInquiryUseCase;

	// UseCase(更新系)(本物のSpring Bean)
	@Autowired
	private FixedCostRegistConfirmUseCase fixedCostRegistConfirmUseCase;

	// モック:ログインユーザセッション情報
	@Mock
	private LoginUserSession mockLoginUserSession;

	@BeforeEach
	void setupMockMvc() {
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(new FixedCostInfoManageController(
						fixedCostInquiryUseCase,
						fixedCostRegistConfirmUseCase,
						mockLoginUserSession))
				.setControllerAdvice(new MyHouseholdAccountBookControllerAdvice(mockLoginUserSession))
				.build();

		doReturn(createLoginUser()).when(mockLoginUserSession).getLoginUserInfo();
	}

	private LoginUserInfo createLoginUser() {
		return LoginUserInfo.from("user01", "テストユーザ01");
	}

	// ================================================================
	// GET /bulkupdate/
	// ================================================================

	/**
	 *<pre>
	 * テスト①：正常系：GET /bulkupdateload/?baseFixedCostCode=0001 → 一括更新画面表示
	 *
	 * 【検証内容】
	 * ・HTTPステータスが200であること
	 * ・ビュー名が「itemmanage/FixedCostBulkUpdate」であること
	 * ・fixedCostBulkUpdateFormがモデルに設定されること
	 * ・bulkUpdateTargetListが2件（0001:家賃、0002:共益費）で取得されること
	 * ・sisyutuItemNameが「固定費(課税)＞地代家賃＞家賃」で設定されること
	 * ・shiharaiDaySelectListがモデルに設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：GET /bulkupdateload/?baseFixedCostCode=0001 一括更新画面表示（対象2件）")
	void testGetBulkUpdateLoad() throws Exception {
		mockMvc.perform(get("/myhacbook/managebaseinfo/fixedcostinfo/bulkupdateload/")
				.param("baseFixedCostCode", "0001")
				.with(user("user01").password("password").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/FixedCostBulkUpdate"))
			.andExpect(model().attributeExists("fixedCostBulkUpdateForm"))
			.andExpect(model().attribute("bulkUpdateTargetList", hasSize(2)))
			.andExpect(model().attribute("sisyutuItemName", is("固定費(課税)＞地代家賃＞家賃")))
			.andExpect(model().attributeExists("shiharaiDaySelectList"));
	}

	// ================================================================
	// POST /bulkupdate/ (params: actionBulkUpdate) バリデーションNG
	// ================================================================

	/**
	 *<pre>
	 * テスト②：異常系：POST /bulkupdate/ actionBulkUpdate バリデーションNG（shiharaiDay空）
	 *
	 * 【検証内容】
	 * ・支払日を空にして送信した場合、@NotBlankバリデーションエラーが発生すること
	 * ・HTTPステータスが200で一括更新画面に戻ること
	 * ・ビュー名が「itemmanage/FixedCostBulkUpdate」であること
	 * ・fixedCostBulkUpdateFormのshiparaiDayフィールドにエラーが設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：POST /bulkupdate/ actionBulkUpdate バリデーションNG(shiharaiDay空)→一括更新画面に戻る")
	void testPostBulkUpdate_validationError_shiharaiDayBlank() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/fixedcostinfo/bulkupdate/")
				.param("actionBulkUpdate", "")
				.param("baseFixedCostCode", "0001")
				// shiharaiDay を空にしてバリデーションエラーを発生させる
				.param("shiharaiDay", "")
				.param("shiharaiKingaku", "70000")
				.param("checkedFixedCostCodeList", "0001")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/FixedCostBulkUpdate"))
			.andExpect(model().attributeHasFieldErrors("fixedCostBulkUpdateForm", "shiharaiDay"));
	}

	/**
	 *<pre>
	 * テスト③：異常系：POST /bulkupdate/ actionBulkUpdate バリデーションNG（チェックボックス未選択）
	 *
	 * 【検証内容】
	 * ・checkedFixedCostCodeListを送信しない場合、@NotEmptyバリデーションエラーが発生すること
	 * ・HTTPステータスが200で一括更新画面に戻ること
	 * ・ビュー名が「itemmanage/FixedCostBulkUpdate」であること
	 * ・fixedCostBulkUpdateFormのcheckedFixedCostCodeListフィールドにエラーが設定されること
	 *</pre>
	 */
	@Test
	@DisplayName("異常系：POST /bulkupdate/ actionBulkUpdate バリデーションNG(チェックボックス未選択)→一括更新画面に戻る")
	void testPostBulkUpdate_validationError_noCheckbox() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/fixedcostinfo/bulkupdate/")
				.param("actionBulkUpdate", "")
				.param("baseFixedCostCode", "0001")
				.param("shiharaiDay", "25")
				.param("shiharaiKingaku", "70000")
				// checkedFixedCostCodeList を送信しない → @NotEmpty エラー
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/FixedCostBulkUpdate"))
			.andExpect(model().attributeHasFieldErrors("fixedCostBulkUpdateForm", "checkedFixedCostCodeList"));
	}

	// ================================================================
	// POST /bulkupdate/ (params: actionBulkUpdate) 成功
	// ================================================================

	/**
	 *<pre>
	 * テスト④：正常系：POST /bulkupdate/ actionBulkUpdate 成功 → リダイレクト
	 *
	 * 【検証内容】
	 * ・0001と0002を選択して支払日=25、支払金額=70000で一括更新した場合、完了画面にリダイレクトすること
	 * ・HTTPステータスが3xxリダイレクトであること
	 * ・リダイレクト先が「/myhacbook/managebaseinfo/fixedcostinfo/updateComplete/」であること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：POST /bulkupdate/ actionBulkUpdate 成功 → updateComplete/ にリダイレクト")
	void testPostBulkUpdate_success() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/fixedcostinfo/bulkupdate/")
				.param("actionBulkUpdate", "")
				.param("baseFixedCostCode", "0001")
				.param("shiharaiDay", "25")
				.param("shiharaiKingaku", "70000")
				.param("checkedFixedCostCodeList", "0001")
				.param("checkedFixedCostCodeList", "0002")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/myhacbook/managebaseinfo/fixedcostinfo/updateComplete/"));
	}

	// ================================================================
	// POST /bulkupdate/ (params: actionCancel)
	// ================================================================

	/**
	 *<pre>
	 * テスト⑤：正常系：POST /bulkupdate/ actionCancel → 処理選択画面(0001)へ
	 *
	 * 【検証内容】
	 * ・一括更新画面からキャンセル操作の場合、処理選択画面に遷移すること
	 * ・baseFixedCostCode=0001を指定して処理選択画面(家賃)に戻ること
	 * ・HTTPステータスが200であること
	 * ・ビュー名が「itemmanage/FixedCostInfoManageActSelect」であること
	 * ・fixedCostInfoがモデルに設定されること
	 * ・0001と0002が同じ0030に属するため hasSiblingFixedCost=true であること
	 *</pre>
	 */
	@Test
	@DisplayName("正常系：POST /bulkupdate/ actionCancel baseFixedCostCode=0001 → 処理選択画面(兄弟あり)")
	void testPostBulkUpdateCancel() throws Exception {
		mockMvc.perform(post("/myhacbook/managebaseinfo/fixedcostinfo/bulkupdate/")
				.param("actionCancel", "")
				.param("baseFixedCostCode", "0001")
				.with(user("user01").password("password").roles("USER"))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("itemmanage/FixedCostInfoManageActSelect"))
			.andExpect(model().attributeExists("fixedCostInfo"))
			.andExpect(model().attribute("hasSiblingFixedCost", is(true)));
	}
}
