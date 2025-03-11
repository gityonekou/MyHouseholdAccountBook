/**
 * 店情報管理ユースケース(ShopInfoManageUseCase.java)のインテグレーションテストケースクラスです
 * 以下範囲のテストを実施します。
 * service:ShopInfoManageUseCase⇔レポジトリー:Repository⇔DB
 * 
 * 
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/01/19 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShopInfoManageResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShopInfoManageResponse.ShopListItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

/**
 *<pre>
 * 店情報管理ユースケース(ShopInfoManageUseCase.java)のインテグレーションテストケースクラスです。
 * 以下範囲のテストを実施します。
 * service:ShopInfoManageUseCase⇔レポジトリー:Repository⇔DB
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
// Web周りのコンフィグレーション以外(controllerは対象に含む)をインジェクションする場合に指定
// ただし、Spring Securityの以下@ConfigurationクラスのBean登録が動いてコケるのですべてのBean登録が必要となる
// 「com.yonetani.webapp.accountbook.presentation.security.config.SecurityConfig.java」
//@SpringBootTest(webEnvironment = WebEnvironment.NONE)

// すべてのBeanをDIコンテナに登録する:Spring Securityの@Configurationクラス作成している場合はすべて必要(少なくとも、限定するとエラーになる)
@SpringBootTest
// SpringBootアプリケーション設定ファイルにapplication-test.ymlを設定
@ActiveProfiles("test")
// 各テスト開始前にロールバックする
@Transactional
class ShopInfoManageUseCaseIntegrationTest {
	// 店情報管理ユースケースサービスをインジェクション
	@Autowired
	private ShopInfoManageUseCase service;
	
	// ユーザ情報
	private final LoginUserInfo TEST_USER = LoginUserInfo.from("TESTUSER001", "テストユーザ01");
	
	/**
	 *<pre>
	 * 【メソッドの説明を入力してください】
	 *</pre>
	 * @throws java.lang.Exception
	 *
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	/**
	 *<pre>
	 * 【メソッドの説明を入力してください】
	 *</pre>
	 * @throws java.lang.Exception
	 *
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	/**
	 *<pre>
	 * 【メソッドの説明を入力してください】
	 *</pre>
	 * @throws java.lang.Exception
	 *
	 */
	@BeforeEach
	void setUp() throws Exception {
	}

	/**
	 *<pre>
	 * 【メソッドの説明を入力してください】
	 *</pre>
	 * @throws java.lang.Exception
	 *
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * 情報管理(お店)画面表示情報取得のインテグレーションテストです。
	 * ユーザIDに対応する店情報なし(検索結果なし)時の結果情報が想定通りかをテストします。
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#readShopInfo(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo)} のためのテスト・メソッド。
	 */
	@Test
	@Sql("ReadShopInfoQueryNotFoundTest.sql")
	void testReadShopInfoQueryNotFound() {
		// 検索結果なしとなる条件で画面表示情報を取得
		ShopInfoManageResponse res = service.readShopInfo(TEST_USER);
		if(res.getMessagesList().size() != 1) {
			fail("検索結果なしのレスポンスメッセージが設定されていない");
		} else {
			assertEquals("店舗情報取得結果が0件です。", res.getMessagesList().get(0), "0件データのエラーとなるかどうか");	
		}
		
		// 画面表示情報からviewを生成し、Modelマップを取得
		ModelMap modelMap = res.build().getModelMap();
		
		// 店舗グループの選択ボックス情報を取得
		SelectViewItem shopKubunItem = (SelectViewItem)modelMap.getAttribute("shopKubun");
		List<OptionItem> shopKubunSelectList = shopKubunItem.getOptionList();
		//選択ボックスの表示値が正しいことを確認
		
		// 店舗一覧情報の明細リストを取得
		@SuppressWarnings("unchecked")
		List<ShopListItem> shopList = (List<ShopListItem>)modelMap.getAttribute("shopList");
		@SuppressWarnings("unchecked")
		List<ShopListItem> nonEditShopList = (List<ShopListItem>)modelMap.getAttribute("nonEditShopList");
		// 店舗一覧情報の明細リストが0件であること
		assertNotNull(shopList, "店舗一覧情報の明細データ(変更可能分)がnullでないこと");
		assertEquals(0, shopList.size(), "店舗一覧情報の明細データ(変更可能分)が0件であること");
		assertNotNull(nonEditShopList, "店舗一覧情報の明細データ(変更不可分)がnullでないこと");
		assertEquals(0, nonEditShopList.size(), "舗一覧情報の明細データ(変更不可分)が0件であること");
	}

	/**
	 * 指定したユーザIDと店舗に応じた情報管理(お店)画面の表示情報取得のインテグレーションテストです。
	 * ××の場合の結果情報が想定通りかをテストします。
	 * 
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#readShopInfo(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, java.lang.String)} のためのテスト・メソッド。
	 */
	@Test
	void testReadShopInfoFromShopCode() {
		fail("まだ実装されていません");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	void testExecAction() {
		fail("まだ実装されていません");
	}

}
