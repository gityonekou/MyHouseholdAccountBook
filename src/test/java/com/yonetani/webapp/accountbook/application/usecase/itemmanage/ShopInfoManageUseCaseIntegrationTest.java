/**
 * 店情報管理ユースケース(ShopInfoManageUseCase.java)のインテグレーションテストケースクラスです
 * 以下範囲のテストを実施します。
 * service:ShopInfoManageUseCase⇔レポジトリー:Repository⇔DB
 * 
 * 
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2025/01/19 : 2.00.00(B)  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import com.yonetani.webapp.accountbook.infrastructure.dto.account.shop.ShopReadWriteDto;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm;
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
 * @since 家計簿アプリ(2.00.B)
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
	// DBアクセス
	@Autowired
	private NamedParameterJdbcTemplate namedParamTemplete;
	
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
	@Sql(scripts = "ReadShopInfoQueryNotFoundTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadShopInfoQueryNotFound() {
		// 検索結果なしとなる条件で画面表示情報を取得
		ShopInfoManageResponse res = service.readShopInfo(TEST_USER);
		if(res.getMessagesList().size() != 1) {
			fail("検索結果なしのレスポンスメッセージが設定されていない");
		} else {
			assertEquals("店舗情報取得結果が0件です。", res.getMessagesList().get(0), "検索結果なしのメッセージが設定されていること");	
		}
		
		// 画面表示情報からviewを生成し、Modelマップを取得
		ModelMap modelMap = res.build().getModelMap();
		
		// 店舗グループの選択ボックス情報の設定値が想定通りであること
		SelectViewItem shopKubunItem = (SelectViewItem)modelMap.getAttribute("shopKubun");
		assertIterableEquals(shopKubunSelectList(), shopKubunItem.getOptionList(), "店舗グループの選択ボックス情報がコード定義内容と等しいこと");
		
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
		// 店舗情報入力フォームに空データが設定されていること
		ShopInfoForm shopInfoForm = (ShopInfoForm)modelMap.getAttribute("shopInfoForm");
		assertNotNull(shopInfoForm, "店舗情報入力フォームがnullでないこと");
		assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD, shopInfoForm.getAction(), "アクションが新規登録であること");
		assertNull(shopInfoForm.getShopCode(), "店舗コードがnullであること");
		assertNull(shopInfoForm.getShopKubun(), "店舗区分がnullであること");
		assertNull(shopInfoForm.getShopName(), "店舗名がnullであること");
		assertNull(shopInfoForm.getShopSort(), "表示順がnullであること");
		assertNull(shopInfoForm.getShopSortBefore(), "表示順(更新比較用)がnullであること");
		
	}
	
	/**
	 * 情報管理(お店)画面表示情報取得のインテグレーションテストです。
	 * ユーザIDに対応する店情報1件(変更可能分1件、変更不可分0件)時の結果情報が想定通りかをテストします。
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#readShopInfo(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryResultOneTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadShopInfoQueryResultOne() {
		// 検索条件に対応する店舗情報1件で画面表示情報を取得
		ShopInfoManageResponse res = service.readShopInfo(TEST_USER);
		assertEquals(0, res.getMessagesList().size(), "エラーメッセージが設定されていないこと");
		
		// 画面表示情報からviewを生成し、Modelマップを取得
		ModelMap modelMap = res.build().getModelMap();
		// 店舗一覧情報の明細リストを取得
		@SuppressWarnings("unchecked")
		List<ShopListItem> shopList = (List<ShopListItem>)modelMap.getAttribute("shopList");
		@SuppressWarnings("unchecked")
		List<ShopListItem> nonEditShopList = (List<ShopListItem>)modelMap.getAttribute("nonEditShopList");
		// 店舗一覧情報の明細リストが1件であること
		assertNotNull(shopList, "店舗一覧情報の明細データ(変更可能分)がnullでないこと");
		assertIterableEquals(shopListOne(), shopList, "店舗一覧情報の明細データ1件の内容が等しいこと");
		assertNotNull(nonEditShopList, "店舗一覧情報の明細データ(変更不可分)がnullでないこと");
		assertEquals(0, nonEditShopList.size(), "舗一覧情報の明細データ(変更不可分)が0件であること");
	}
	
	/**
	 * 情報管理(お店)画面表示情報取得のインテグレーションテストです。
	 * ユーザIDに対応する店情報1件(変更可能分0件、変更不可分1件)時の結果情報が想定通りかをテストします。
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#readShopInfo(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryNonEditResultOneTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadShopInfoQueryNonEditResultOne() {
		// 検索条件に対応する店舗情報1件で画面表示情報を取得
		ShopInfoManageResponse res = service.readShopInfo(TEST_USER);
		assertEquals(0, res.getMessagesList().size(), "エラーメッセージが設定されていないこと");
		
		// 画面表示情報からviewを生成し、Modelマップを取得
		ModelMap modelMap = res.build().getModelMap();
		// 店舗一覧情報の明細リストを取得
		@SuppressWarnings("unchecked")
		List<ShopListItem> shopList = (List<ShopListItem>)modelMap.getAttribute("shopList");
		@SuppressWarnings("unchecked")
		List<ShopListItem> nonEditShopList = (List<ShopListItem>)modelMap.getAttribute("nonEditShopList");
		// 店舗一覧情報の明細リストが1件であること
		assertNotNull(shopList, "店舗一覧情報の明細データ(変更可能分)がnullでないこと");
		assertEquals(0, shopList.size(), "店舗一覧情報の明細データ(変更可能分)が0件であること");
		assertNotNull(nonEditShopList, "店舗一覧情報の明細データ(変更不可分)がnullでないこと");
		assertIterableEquals(nonEditShopListOne(), nonEditShopList, "店舗一覧情報の明細データ1件の内容が等しいこと");
	}
	
	/**
	 * 情報管理(お店)画面表示情報取得のインテグレーションテストです。
	 * ユーザIDに対応する店情報6件(変更可能分3件、変更不可分3件)時の結果情報が想定通りかをテストします。
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#readShopInfo(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryResultSixTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadShopInfoQueryResultSix() {
		// 検索条件に対応する店舗情報6件で画面表示情報を取得
		ShopInfoManageResponse res = service.readShopInfo(TEST_USER);
		assertEquals(0, res.getMessagesList().size(), "エラーメッセージが設定されていないこと");
		
		// 画面表示情報からviewを生成し、Modelマップを取得
		ModelMap modelMap = res.build().getModelMap();
		// 店舗一覧情報の明細リストを取得
		@SuppressWarnings("unchecked")
		List<ShopListItem> shopList = (List<ShopListItem>)modelMap.getAttribute("shopList");
		@SuppressWarnings("unchecked")
		List<ShopListItem> nonEditShopList = (List<ShopListItem>)modelMap.getAttribute("nonEditShopList");
		// 店舗一覧情報の明細リストが1件であること
		assertNotNull(shopList, "店舗一覧情報の明細データ(変更可能分)がnullでないこと");
		assertIterableEquals(shopListThree(), shopList, "店舗一覧情報(変更可能分)の明細データ3件の内容が等しいこと");
		assertNotNull(nonEditShopList, "店舗一覧情報の明細データ(変更不可分)がnullでないこと");
		assertIterableEquals(nonEditShopListThree(), nonEditShopList, "店舗一覧情報(変更不可分)の明細データ3件の内容が等しいこと");
	}
	
	/**
	 * 指定したユーザIDと店舗に応じた情報管理(お店)画面の表示情報取得のインテグレーションテストです。
	 * 指定した店舗情報の情報がメッセージとフォームデータに設定されているかをテストします。
	 * 
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#readShopInfo(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, java.lang.String)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryResultSixTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadShopInfoFromShopCode() {
		// 検索条件に対応する店舗情報6件、店舗コード(002)に対応する店舗情報ありで画面表示情報を取得
		ShopInfoManageResponse res = service.readShopInfo(TEST_USER, "002");
		if(res.getMessagesList().size() != 1) {
			fail("更新対象店舗名のレスポンスメッセージが設定されていない");
		} else {
			assertEquals("店舗名「テストユーザ登録店舗０２」の店舗を更新します。", res.getMessagesList().get(0), "更新対象店舗名のレスポンスメッセージが設定されていること");	
		}
		
		// 画面表示情報からviewを生成し、Modelマップを取得
		ModelMap modelMap = res.build().getModelMap();
		// 店舗情報入力フォームに店舗コード(002)に対応するデータが設定されていること
		ShopInfoForm shopInfoForm = (ShopInfoForm)modelMap.getAttribute("shopInfoForm");
		assertNotNull(shopInfoForm, "店舗情報入力フォームがnullでないこと");
		assertEquals(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE, shopInfoForm.getAction(), "アクションが更新であること");
		assertEquals("002", shopInfoForm.getShopCode(), "店舗コードが002であること");
		assertEquals("902",shopInfoForm.getShopKubun(), "店舗区分が902であること");
		assertEquals("テストユーザ登録店舗０２",shopInfoForm.getShopName(), "店舗名が「テストユーザ登録店舗０２」であること");
		assertEquals(Integer.parseInt("002"),shopInfoForm.getShopSort(), "表示順が2であること");
		assertEquals("002",shopInfoForm.getShopSortBefore(), "表示順(更新比較用)が002であること");
		
	}
	
	/**
	 * 指定したユーザIDと店舗に応じた情報管理(お店)画面の表示情報取得のインテグレーションテストです。
	 * 指定した店舗情報が存在しない場合、例外となることをテストします。
	 * 
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#readShopInfo(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, java.lang.String)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryResultSixTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadShopInfoNotFoundFromShopCode() {
		// 検索条件に対応する店舗情報1件で画面表示情報を取得
		MyHouseholdAccountBookRuntimeException ex = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> service.readShopInfo(TEST_USER, "900"),
				"更新対象の店舗情報なしの場合エラーとなること");
		assertEquals(
				"更新対象の店舗情報が存在しません。管理者に問い合わせてください。shopCode:900",
				ex.getLocalizedMessage(),
				"例外メッセージが等しいこと");
	}
	
	/**
	 * バリデーションチェックエラー時のレスポンス情報確認
	 * 画面表示情報の妥当性確認はコントローラーのテストで行う(トランザクションが完了となっていないことの確認のみここで行う)
	 * 
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#readUpdateBindingErrorSetInfo(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryResultSixTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testReadUpdateBindingErrorSetInfo() {
		// テスト用フォームデータを作成
		ShopInfoForm form = inputUpdateShopInfoForm(null);
		
		// 検索条件に対応する店舗情報6件でバリデーションチェックエラー時画面表示情報を取得
		ShopInfoManageResponse res = service.readUpdateBindingErrorSetInfo(TEST_USER, form);
		assertEquals(0, res.getMessagesList().size(), "エラーメッセージが設定されていないこと");
		// トランザクションが完了のステータスになっていないこと
		assertFalse(res.isTransactionSuccessFull(), "トランザクションが完了のステータスになっていないこと");
		
	}
	
	/**
	 * 新規追加データが登録されることを確認します。
	 * 0件→1件
	 * 表示順入力なし
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryNotFoundTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecAddType1Action() {
		// 新規追加テスト用フォームデータを作成(表示順=null:表示順は自動採番)
		ShopInfoForm form = inputAddShopInfoForm(null);
		
		// 検索条件に対応する店舗情報6件で新規の店舗情報を登録
		ShopInfoManageResponse res = service.execAction(TEST_USER, form);
		// 新規追加完了のメッセージが設定されていること
		if(res.getMessagesList().size() != 1) {
			fail("新規店舗追加のレスポンスメッセージが設定されていない");
		} else {
			assertEquals("新規店舗を追加しました。[code:001]" + form.getShopName(), res.getMessagesList().get(0), "新規店舗追加のメッセージが設定されていること");	
		}
		// トランザクションが完了のステータスになっていること
		assertTrue(res.isTransactionSuccessFull(), "トランザクションが完了のステータスになっていること");
		
		// TEST_USERを検索条件とし、条件に一致するデータが1件であること
		// 登録されたデータをロード
		List<ShopReadWriteDto> resultList = execQueryAllShopList();
		// 1件のデータが登録したデータと等しいこと
		assertEquals(1, resultList.size(), "検索結果が1件であること");
		ShopReadWriteDto dto = resultList.get(0);
		assertEquals("TESTUSER001", dto.getUserId(), "ユーザIDがTESTUSER001であること");
		assertEquals("001", dto.getShopCode(), "店舗コードが001であること");
		assertEquals("904", dto.getShopKubunCode(), "店舗区分コードが904であること");
		assertEquals("靴店舗(新規追加)", dto.getShopName(), "店舗名が靴店舗(新規追加)であること");
		assertEquals("001", dto.getShopSort(), "店舗表示順が001であること");
	}

	/**
	 * 新規追加データが登録されることを確認します。
	 * (登録済みデータに変更不可データあり)
	 * 0件→1件
	 * 表示順入力(002)→DBの格納値は001
	 * 
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryNonEditResultOneTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecAddType2Action() {
		// 新規追加テスト用フォームデータを作成(表示順=002)
		ShopInfoForm form = inputAddShopInfoForm("002");
		
		// 検索条件に対応する店舗情報6件で新規の店舗情報を登録
		ShopInfoManageResponse res = service.execAction(TEST_USER, form);
		// トランザクションが完了のステータスになっていること
		assertTrue(res.isTransactionSuccessFull(), "トランザクションが完了のステータスになっていること");
		
		// TEST_USERを検索条件とし、条件に一致するデータが1件であること
		// 登録されたデータをロード
		List<ShopReadWriteDto> resultList = execQueryAllShopList();
		// 1件目のデータが登録したデータと等しいこと
		assertEquals(2, resultList.size(), "検索結果が2件であること");
		ShopReadWriteDto dto = resultList.get(0);
		assertEquals("TESTUSER001", dto.getUserId(), "ユーザIDがTESTUSER001であること");
		assertEquals("001", dto.getShopCode(), "店舗コードが001であること");
		assertEquals("904", dto.getShopKubunCode(), "店舗区分コードが904であること");
		assertEquals("靴店舗(新規追加)", dto.getShopName(), "店舗名が靴店舗(新規追加)であること");
		assertEquals("001", dto.getShopSort(), "店舗表示順が001であること");
	}
	
	/**
	 * 新規追加データが登録されることを確認します。
	 * ユーザIDに対応する店情報6件(変更可能分3件、変更不可分3件)
	 * 6件→7件に件数が増えること
	 * 
	 * 表示順入力(なし)→DBの格納値は004
	 * 
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryResultSixTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecAddType3Action() {
		// 新規追加テスト用フォームデータを作成(表示順=null:表示順は自動採番)
		ShopInfoForm form = inputAddShopInfoForm(null);
		
		// 検索条件に対応する店舗情報6件で新規の店舗情報を登録
		ShopInfoManageResponse res = service.execAction(TEST_USER, form);
		// トランザクションが完了のステータスになっていること
		assertTrue(res.isTransactionSuccessFull(), "トランザクションが完了のステータスになっていること");
		
		// TEST_USERを検索条件とし、条件に一致するデータが1件であること
		// 登録されたデータをロード
		List<ShopReadWriteDto> resultList = execQueryAllShopList();
		// 4件目のデータが登録したデータと等しいこと
		assertEquals(7, resultList.size(), "検索結果が7件であること");
		ShopReadWriteDto dto = resultList.get(3);
		assertEquals("TESTUSER001", dto.getUserId(), "ユーザIDがTESTUSER001であること");
		assertEquals("004", dto.getShopCode(), "店舗コードが004であること");
		assertEquals("904", dto.getShopKubunCode(), "店舗区分コードが904であること");
		assertEquals("靴店舗(新規追加)", dto.getShopName(), "店舗名が靴店舗(新規追加)であること");
		assertEquals("004", dto.getShopSort(), "店舗表示順が004であること");
		// 3件目、5件目の表示順の値が更新されていないこと
		assertEquals("003", resultList.get(2).getShopSort(), "店舗表示順が003であること");
		assertEquals("901", resultList.get(4).getShopSort(), "店舗表示順が901であること");
	}
	
	/**
	 * 新規追加データが登録されることを確認します。
	 * ユーザIDに対応する店情報6件(変更可能分3件、変更不可分3件)
	 * 6件→7件に件数が増えること
	 * 
	 * 表示順入力(001)→DBの格納値は001、変更可能分データの表示順がインクリメントされていること
	 * 
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryResultSixTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecAddType4Action() {
		// 新規追加テスト用フォームデータを作成(表示順=001)
		ShopInfoForm form = inputAddShopInfoForm("001");
		
		// 検索条件に対応する店舗情報6件で新規の店舗情報を登録
		ShopInfoManageResponse res = service.execAction(TEST_USER, form);
		// トランザクションが完了のステータスになっていること
		assertTrue(res.isTransactionSuccessFull(), "トランザクションが完了のステータスになっていること");
		
		// TEST_USERを検索条件とし、条件に一致するデータが1件であること
		// 登録されたデータをロード
		List<ShopReadWriteDto> resultList = execQueryAllShopList();
		// 4件目のデータが登録したデータと等しいこと
		assertEquals(7, resultList.size(), "検索結果が7件であること");
		ShopReadWriteDto dto = resultList.get(3);
		assertEquals("004", dto.getShopCode(), "店舗コードが004であること");
		assertEquals("001", dto.getShopSort(), "店舗表示順が001であること");
		// 1件目～3件目の表示順が+1され、5件目の表示順の値が更新されていないこと
		assertEquals("002", resultList.get(0).getShopSort(), "店舗表示順が002であること");
		assertEquals("003", resultList.get(1).getShopSort(), "店舗表示順が003であること");
		assertEquals("004", resultList.get(2).getShopSort(), "店舗表示順が004であること");
		assertEquals("901", resultList.get(4).getShopSort(), "店舗表示順が901であること");
	}
	
	/**
	 * 新規追加データが登録されることを確認します。
	 * ユーザIDに対応する店情報6件(変更可能分3件、変更不可分3件)
	 * 6件→7件に件数が増えること
	 * 
	 * 表示順入力(002)→DBの格納値は002、変更可能分データのうち、先頭のデータの表示順は変更されず、
	 * 2件目以降の表示順がインクリメントされていること
	 * 
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryResultSixTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecAddType5Action() {
		// 新規追加テスト用フォームデータを作成(表示順=002)
		ShopInfoForm form = inputAddShopInfoForm("002");
		
		// 検索条件に対応する店舗情報6件で新規の店舗情報を登録
		ShopInfoManageResponse res = service.execAction(TEST_USER, form);
		// トランザクションが完了のステータスになっていること
		assertTrue(res.isTransactionSuccessFull(), "トランザクションが完了のステータスになっていること");
		
		// TEST_USERを検索条件とし、条件に一致するデータが1件であること
		// 登録されたデータをロード
		List<ShopReadWriteDto> resultList = execQueryAllShopList();
		// 4件目のデータが登録したデータと等しいこと
		assertEquals(7, resultList.size(), "検索結果が7件であること");
		ShopReadWriteDto dto = resultList.get(3);
		assertEquals("002", dto.getShopSort(), "店舗表示順が002であること");
		// 2件目～3件目の表示順が+1され、5件目の表示順の値が更新されていないこと
		assertEquals("001", resultList.get(0).getShopSort(), "店舗表示順が001であること");
		assertEquals("003", resultList.get(1).getShopSort(), "店舗表示順が003であること");
		assertEquals("004", resultList.get(2).getShopSort(), "店舗表示順が004であること");
		assertEquals("002", resultList.get(3).getShopSort(), "店舗表示順が002であること");
		assertEquals("901", resultList.get(4).getShopSort(), "店舗表示順が901であること");
	}
	
	/**
	 * 新規追加データが登録されることを確認します。
	 * ユーザIDに対応する店情報6件(変更可能分3件、変更不可分3件)
	 * 6件→7件に件数が増えること
	 * 
	 * 表示順入力(004)→DBの格納値は004
	 * 
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryResultSixTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecAddType6Action() {
		// 新規追加テスト用フォームデータを作成(表示順=004)
		ShopInfoForm form = inputAddShopInfoForm("004");
		
		// 検索条件に対応する店舗情報6件で新規の店舗情報を登録
		ShopInfoManageResponse res = service.execAction(TEST_USER, form);
		// トランザクションが完了のステータスになっていること
		assertTrue(res.isTransactionSuccessFull(), "トランザクションが完了のステータスになっていること");
		
		// TEST_USERを検索条件とし、条件に一致するデータが1件であること
		// 登録されたデータをロード
		List<ShopReadWriteDto> resultList = execQueryAllShopList();
		// 4件目のデータが登録したデータと等しいこと
		assertEquals(7, resultList.size(), "検索結果が7件であること");
		ShopReadWriteDto dto = resultList.get(3);
		assertEquals("004", dto.getShopCode(), "店舗コードが004であること");
		assertEquals("004", dto.getShopSort(), "店舗表示順が004であること");
		// 3件目、5件目の表示順の値が更新されていないこと
		assertEquals("003", resultList.get(2).getShopSort(), "店舗表示順が003であること");
		assertEquals("901", resultList.get(4).getShopSort(), "店舗表示順が901であること");
	}
	
	/**
	 * 新規追加データが登録されることを確認します。
	 * ユーザIDに対応する店情報6件(変更可能分3件、変更不可分3件)
	 * 6件→7件に件数が増えること
	 * 
	 * 表示順入力(005)→DBの格納値は004
	 * 
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryResultSixTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecAddType7Action() {
		// 新規追加テスト用フォームデータを作成(表示順=005)
		ShopInfoForm form = inputAddShopInfoForm("005");
		
		// 検索条件に対応する店舗情報6件で新規の店舗情報を登録
		ShopInfoManageResponse res = service.execAction(TEST_USER, form);
		// トランザクションが完了のステータスになっていること
		assertTrue(res.isTransactionSuccessFull(), "トランザクションが完了のステータスになっていること");
		
		// TEST_USERを検索条件とし、条件に一致するデータが1件であること
		// 登録されたデータをロード
		List<ShopReadWriteDto> resultList = execQueryAllShopList();
		// 4件目のデータが登録したデータと等しいこと
		assertEquals(7, resultList.size(), "検索結果が7件であること");
		ShopReadWriteDto dto = resultList.get(3);
		assertEquals("004", dto.getShopCode(), "店舗コードが004であること");
		assertEquals("004", dto.getShopSort(), "店舗表示順が004であること");
		// 3件目、5件目の表示順の値が更新されていないこと
		assertEquals("003", resultList.get(2).getShopSort(), "店舗表示順が003であること");
		assertEquals("901", resultList.get(4).getShopSort(), "店舗表示順が901であること");
	}
	
	/**
	 * 更新データが登録されることを確認します。
	 * (更新項目：店舗区分、店舗名、表示順の変更なし)
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryResultSixTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecUpdateType1Action() {
		// 更新テスト用フォームデータを作成
		ShopInfoForm form = inputUpdateShopInfoForm("002");
		
		// 検索条件に対応する店舗情報6件で指定の店舗情報を更新
		ShopInfoManageResponse res = service.execAction(TEST_USER, form);
		// 更新完了のメッセージが設定されていること
		if(res.getMessagesList().size() != 1) {
			fail("更新完了のレスポンスメッセージが設定されていない");
		} else {
			assertEquals("店舗を更新しました。[code:" + form.getShopCode() + "]" + form.getShopName(), res.getMessagesList().get(0), "更新完了のメッセージが設定されていること");	
		}
		// トランザクションが完了のステータスになっていること
		assertTrue(res.isTransactionSuccessFull(), "トランザクションが完了のステータスになっていること");
		
		// 更新されたデータをロード
		// 本来はDataClassRowMapperを使うのがベストと思う。こちらは参考で使ってみた(基本の使い方はShoppingItemTableDataSourceTestを参照
		// DataClassRowMapperを使う方はtestExecUpdateType2Actionを参照
		Map<String, Object> actualDataMap = namedParamTemplete.queryForMap(
				// 商品テーブル検索SQL(unique data)
				"SELECT * FROM SHOP_TABLE WHERE USER_ID=:USER_ID AND SHOP_CODE=:SHOP_CODE",
				// パラメータ
				Map.of(
					// ユーザID
					"USER_ID", TEST_USER.getUserId().toString(),
					// 店舗コード
					"SHOP_CODE", form.getShopCode()
				));
		// 更新データと等しいこと：ユーザID
		assertEquals("TESTUSER001", actualDataMap.get("USER_ID"), "ユーザID(USER_ID)の更新後の値が正しいこと");
		// 更新データと等しいこと：店舗コード
		assertEquals("002", actualDataMap.get("SHOP_CODE"), "店舗コード(SHOP_CODE)の更新後の値が正しいこと");
		// 更新データと等しいこと：店舗区分コード
		assertEquals("904", actualDataMap.get("SHOP_KUBUN_CODE"), "店舗区分コード(SHOP_KUBUN_CODE)の更新後の値が正しいこと");
		// 更新データと等しいこと：店舗名
		assertEquals("靴店舗に更新(ホームセンター更新後)", actualDataMap.get("SHOP_NAME"), "店舗名(SHOP_NAME)の更新後の値が正しいこと");
		// 更新データと等しいこと：店舗表示順
		assertEquals("002", actualDataMap.get("SHOP_SORT"), "店舗表示順(SHOP_SORT)の更新後の値が正しいこと");
		
		// その他データの表示順が変更されていないことを確認
		// 登録されたデータをロード
		List<ShopReadWriteDto> resultList = execQueryAllShopList();
		resultList.forEach(data -> {
			assertEquals(data.getShopCode(), data.getShopSort(), "店舗コードと店舗表示順の値が等しいこと");
		});
	}
	
	/**
	 * 更新データが登録されることを確認します。
	 * (更新項目：表示順(002)→(001))
	 * (※変更前の店舗コード001の表示順が002に更新されていること)
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryResultSixTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecUpdateType2Action() {
		// 更新テスト用フォームデータを作成(表示順:001)
		ShopInfoForm form = inputUpdateShopInfoForm("001");
		// 検索条件に対応する店舗情報6件で指定の店舗情報を更新
		ShopInfoManageResponse res = service.execAction(TEST_USER, form);
		// 更新完了のメッセージが設定されていること
		if(res.getMessagesList().size() != 1) {
			fail("更新完了のレスポンスメッセージが設定されていない");
		} else {
			assertEquals("店舗を更新しました。[code:" + form.getShopCode() + "]" + form.getShopName(), res.getMessagesList().get(0), "更新完了のメッセージが設定されていること");	
		}
		// トランザクションが完了のステータスになっていること
		assertTrue(res.isTransactionSuccessFull(), "トランザクションが完了のステータスになっていること");
		
		// 更新されたデータをロード
		ShopReadWriteDto actualData = execQueryUpdateShop();
		
		// 更新データと等しいこと：ユーザID
		assertEquals("TESTUSER001", actualData.getUserId(), "ユーザID(USER_ID)の更新後の値が正しいこと");
		// 更新データと等しいこと：店舗コード
		assertEquals("002", actualData.getShopCode(), "店舗コード(SHOP_CODE)の更新後の値が正しいこと");
		// 更新データと等しいこと：店舗区分コード
		assertEquals("904", actualData.getShopKubunCode(), "店舗区分コード(SHOP_KUBUN_CODE)の更新後の値が正しいこと");
		// 更新データと等しいこと：店舗名
		assertEquals("靴店舗に更新(ホームセンター更新後)", actualData.getShopName(), "店舗名(SHOP_NAME)の更新後の値が正しいこと");
		// 更新データと等しいこと：店舗表示順(=001)
		assertEquals("001", actualData.getShopSort(), "店舗表示順(SHOP_SORT)の更新後の値が正しいこと");
		
		// DBデータの表示順の値更新結果が正しいこと
		// 登録されたデータをロード
		List<ShopReadWriteDto> resultList = execQueryAllShopList();
		// 1件目データと2件目データの表示順が変わっていること
		assertEquals("002", resultList.get(0).getShopSort(), "店舗表示順が002であること");
		assertEquals("001", resultList.get(1).getShopSort(), "店舗表示順が001であること");
		// 3件目以降の表示順の変更がないこと
		assertEquals("003", resultList.get(2).getShopSort(), "店舗表示順が003であること");
		assertEquals("901", resultList.get(3).getShopSort(), "店舗表示順が901であること");
	}
	
	/**
	 * 表示順の更新結果が正しいことを確認します。
	 * (更新項目：表示順(002)→(003))
	 * (変更前の店舗コード003の表示順が002に更新されていること)
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryResultSixTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecUpdateType3Action() {
		// 更新テスト用フォームデータを作成(表示順:003)
		ShopInfoForm form = inputUpdateShopInfoForm("003");
		// 検索条件に対応する店舗情報6件で指定の店舗情報を更新
		ShopInfoManageResponse res = service.execAction(TEST_USER, form);
		// 更新完了のメッセージが設定されていること
		if(res.getMessagesList().size() != 1) {
			fail("更新完了のレスポンスメッセージが設定されていない");
		} else {
			assertEquals("店舗を更新しました。[code:" + form.getShopCode() + "]" + form.getShopName(), res.getMessagesList().get(0), "更新完了のメッセージが設定されていること");	
		}
		// トランザクションが完了のステータスになっていること
		assertTrue(res.isTransactionSuccessFull(), "トランザクションが完了のステータスになっていること");
		
		// DBデータの表示順の値更新結果が正しいこと
		// 登録されたデータをロード
		List<ShopReadWriteDto> resultList = execQueryAllShopList();
		// 2件目データと3件目データの表示順が変わっていること
		assertEquals("001", resultList.get(0).getShopSort(), "店舗表示順が001であること");
		assertEquals("003", resultList.get(1).getShopSort(), "店舗表示順が003であること");
		assertEquals("002", resultList.get(2).getShopSort(), "店舗表示順が002であること");
		// 4件目以降の表示順の変更がないこと
		assertEquals("901", resultList.get(3).getShopSort(), "店舗表示順が901であること");
	}
	
	/**
	 * 表示順の更新結果が正しいことを確認します。
	 * (更新項目：表示順(002)→(004) :DB格納値は003であること)
	 * (変更前の店舗コード003の表示順が002に更新されていること)
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryResultSixTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecUpdateType4Action() {
		// 更新テスト用フォームデータを作成(表示順:004)
		ShopInfoForm form = inputUpdateShopInfoForm("004");
		// 検索条件に対応する店舗情報6件で指定の店舗情報を更新
		ShopInfoManageResponse res = service.execAction(TEST_USER, form);
		// 更新完了のメッセージが設定されていること
		if(res.getMessagesList().size() != 1) {
			fail("更新完了のレスポンスメッセージが設定されていない");
		} else {
			assertEquals("店舗を更新しました。[code:" + form.getShopCode() + "]" + form.getShopName(), res.getMessagesList().get(0), "更新完了のメッセージが設定されていること");	
		}
		// トランザクションが完了のステータスになっていること
		assertTrue(res.isTransactionSuccessFull(), "トランザクションが完了のステータスになっていること");
		
		// DBデータの表示順の値更新結果が正しいこと
		// 登録されたデータをロード
		List<ShopReadWriteDto> resultList = execQueryAllShopList();
		// 2件目データと3件目データの表示順が変わっていること
		assertEquals("001", resultList.get(0).getShopSort(), "店舗表示順が001であること");
		assertEquals("003", resultList.get(1).getShopSort(), "店舗表示順が003であること");
		assertEquals("002", resultList.get(2).getShopSort(), "店舗表示順が002であること");
		// 4件目以降の表示順の変更がないこと
		assertEquals("901", resultList.get(3).getShopSort(), "店舗表示順が901であること");
	}
	
	/**
	 * 表示順の更新結果が正しいことを確認します。
	 * (更新項目：表示順(002)→(null) :DB格納値は003であること)
	 * (変更前の店舗コード003の表示順が002に更新されていること)
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryResultSixTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecUpdateType5Action() {
		// 更新テスト用フォームデータを作成(表示順:null)
		ShopInfoForm form = inputUpdateShopInfoForm(null);
		// 検索条件に対応する店舗情報6件で指定の店舗情報を更新
		ShopInfoManageResponse res = service.execAction(TEST_USER, form);
		// 更新完了のメッセージが設定されていること
		if(res.getMessagesList().size() != 1) {
			fail("更新完了のレスポンスメッセージが設定されていない");
		} else {
			assertEquals("店舗を更新しました。[code:" + form.getShopCode() + "]" + form.getShopName(), res.getMessagesList().get(0), "更新完了のメッセージが設定されていること");	
		}
		// トランザクションが完了のステータスになっていること
		assertTrue(res.isTransactionSuccessFull(), "トランザクションが完了のステータスになっていること");
		
		// DBデータの表示順の値更新結果が正しいこと
		// 登録されたデータをロード
		List<ShopReadWriteDto> resultList = execQueryAllShopList();
		// 2件目データと3件目データの表示順が変わっていること
		assertEquals("001", resultList.get(0).getShopSort(), "店舗表示順が001であること");
		assertEquals("003", resultList.get(1).getShopSort(), "店舗表示順が003であること");
		assertEquals("002", resultList.get(2).getShopSort(), "店舗表示順が002であること");
		// 4件目以降の表示順の変更がないこと
		assertEquals("901", resultList.get(3).getShopSort(), "店舗表示順が901であること");
	}
	
	/**
	 * 表示順の更新結果が正しいことを確認します。
	 * (店舗コード001の表示順を003に変更。002と003のデータの表示順が-1されていること)
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryResultSixTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecUpdateType6Action() {
		// 更新テスト用フォームデータを作成(表示順:003)
		ShopInfoForm form = inputUpdateShopInfoForm("003");
		// 更新データの店舗コードを001に変更
		form.setShopCode("001");
		// 更新データの旧表示順を001に変更
		form.setShopSortBefore("001");
		// 検索条件に対応する店舗情報6件で指定の店舗情報を更新
		service.execAction(TEST_USER, form);
		
		// DBデータの表示順の値更新結果が正しいこと
		// 登録されたデータをロード
		List<ShopReadWriteDto> resultList = execQueryAllShopList();
		// 1件目～3件目データの表示順が変わっていること
		assertEquals("003", resultList.get(0).getShopSort(), "店舗表示順が003であること");
		assertEquals("001", resultList.get(1).getShopSort(), "店舗表示順が001であること");
		assertEquals("002", resultList.get(2).getShopSort(), "店舗表示順が002であること");
		// 4件目以降の表示順の変更がないこと
		assertEquals("901", resultList.get(3).getShopSort(), "店舗表示順が901であること");
	}
	
	/**
	 * 表示順の更新結果が正しいことを確認します。
	 * (店舗コード003の表示順を001に変更。001と002のデータの表示順が+1されていること)
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryResultSixTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecUpdateType7Action() {
		// 更新テスト用フォームデータを作成(表示順:001)
		ShopInfoForm form = inputUpdateShopInfoForm("001");
		// 更新データの店舗コードを003に変更
		form.setShopCode("003");
		// 更新データの旧表示順を003に変更
		form.setShopSortBefore("003");
		// 検索条件に対応する店舗情報6件で指定の店舗情報を更新
		service.execAction(TEST_USER, form);
		
		// DBデータの表示順の値更新結果が正しいこと
		// 登録されたデータをロード
		List<ShopReadWriteDto> resultList = execQueryAllShopList();
		// 1件目～3件目データの表示順が変わっていること
		assertEquals("002", resultList.get(0).getShopSort(), "店舗表示順が002であること");
		assertEquals("003", resultList.get(1).getShopSort(), "店舗表示順が003であること");
		assertEquals("001", resultList.get(2).getShopSort(), "店舗表示順が001であること");
		// 4件目以降の表示順の変更がないこと
		assertEquals("901", resultList.get(3).getShopSort(), "店舗表示順が901であること");
	}
	
	
	/**
	 * 更新の場合、店舗情報の表示順(更新比較用)の値が未設定の場合予期しないエラーとなること
	 * 
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryResultSixTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecUpdateShopSortBeforeEmptyAction() {
		// 更新テスト用フォームデータを作成
		ShopInfoForm form = inputUpdateShopInfoForm("002");
		// 更新前表示順の値にnullを設定
		form.setShopSortBefore(null);
		
		MyHouseholdAccountBookRuntimeException ex = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> service.execAction(TEST_USER, form),
				"更新前表示順の値なしの場合、予期しないエラーとなること");
		assertEquals(
				"旧表示順の値が不正です。管理者に問い合わせてください。ShopSortBefore=null",
				ex.getLocalizedMessage(),
				"例外メッセージが等しいこと");
		
	}
	
	/**
	 * アクションの設定値が新規追加・更新以外の場合予期しないエラーとなること
	 * 
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	@Sql(scripts = "ReadShopInfoQueryResultSixTest.sql", config = @SqlConfig(encoding = "UTF-8"))
	void testExecUpdateActionFailValueAction() {
		// 更新テスト用フォームデータを作成
		ShopInfoForm form = inputUpdateShopInfoForm("002");
		// アクションに削除を設定
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_DELETE);
		// 更新前表示順の値にnullを設定
		form.setShopSortBefore(null);
		
		MyHouseholdAccountBookRuntimeException ex = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> service.execAction(TEST_USER, form),
				"アクションの設定値が新規追加・更新以外の場合、予期しないエラーとなること");
		assertEquals(
				"未定義のアクションが設定されています。管理者に問い合わせてください。action=" + MyHouseholdAccountBookContent.ACTION_TYPE_DELETE,
				ex.getLocalizedMessage(),
				"例外メッセージが等しいこと");
	}
	
	/**
	 *<pre>
	 * 店舗グループの選択ボックス情報(固定値)をリスト形式で取得
	 *</pre>
	 * @return 店舗グループの選択ボックス情報
	 *
	 */
	private List<OptionItem> shopKubunSelectList() {
		List<OptionItem> optionList = new ArrayList<>();
		optionList.add(OptionItem.from("", "グループメニューを開く"));
		optionList.add(OptionItem.from("901", "食品・日用品店舗"));
		optionList.add(OptionItem.from("902", "ホームセンター"));
		optionList.add(OptionItem.from("903", "衣類店舗"));
		optionList.add(OptionItem.from("904", "靴店舗"));
		optionList.add(OptionItem.from("905", "薬局/薬局複合店/病院"));
		optionList.add(OptionItem.from("906", "家電量販店"));
		optionList.add(OptionItem.from("907", "複合店舗"));
		optionList.add(OptionItem.from("908", "理髪店"));
		optionList.add(OptionItem.from("909", "書店/書店複合店"));
		optionList.add(OptionItem.from("910", "外食"));
		optionList.add(OptionItem.from("911", "メディアショップ(レンタルなど)"));
		optionList.add(OptionItem.from("912", "リユースショップ"));
		optionList.add(OptionItem.from("913", "公共交通機関"));
		optionList.add(OptionItem.from("914", "ネットショップ"));
		optionList.add(OptionItem.from("915", "コインランドリー"));
		optionList.add(OptionItem.from("916", "イベント"));
		optionList.add(OptionItem.from("999", "その他"));
		
		return optionList;
	}
	
	/**
	 *<pre>
	 * 店舗一覧情報の明細テストデータ1件(変更可能分)を取得
	 *</pre>
	 * @return
	 *
	 */
	private List<ShopListItem> shopListOne() {
		List<ShopListItem> shopList = new ArrayList<>();
		shopList.add(ShopListItem.from("001", "テストユーザ登録店舗０１", "食品・日用品店舗", "001"));
		return shopList;
	}
	
	/**
	 *<pre>
	 * 店舗一覧情報の明細テストデータ3件(変更可能分)を取得
	 *</pre>
	 * @return
	 *
	 */
	private List<ShopListItem> shopListThree() {
		List<ShopListItem> shopList = new ArrayList<>();
		shopList.add(ShopListItem.from("001", "テストユーザ登録店舗０１", "食品・日用品店舗", "001"));
		shopList.add(ShopListItem.from("002", "テストユーザ登録店舗０２", "ホームセンター", "002"));
		shopList.add(ShopListItem.from("003", "テストユーザ登録店舗０３", "衣類店舗", "003"));
		return shopList;
	}
	
	/**
	 *<pre>
	 * 店舗一覧情報の明細テストデータ1件(変更不可分)を取得
	 *</pre>
	 * @return
	 *
	 */
	private List<ShopListItem> nonEditShopListOne() {
		List<ShopListItem> shopList = new ArrayList<>();
		shopList.add(ShopListItem.from("901", "食品・日用品店舗(その他)", "食品・日用品店舗", "901"));
		return shopList;
	}
	
	/**
	 *<pre>
	 * 店舗一覧情報の明細テストデータ3件(変更不可分)を取得
	 *</pre>
	 * @return
	 *
	 */
	private List<ShopListItem> nonEditShopListThree() {
		List<ShopListItem> shopList = new ArrayList<>();
		shopList.add(ShopListItem.from("901", "食品・日用品店舗(その他)", "食品・日用品店舗", "901"));
		shopList.add(ShopListItem.from("902", "ホームセンター(その他)", "ホームセンター", "902"));
		shopList.add(ShopListItem.from("903", "衣類店舗(その他)", "衣類店舗", "903"));
		return shopList;
	}
	
	/**
	 *<pre>
	 * 追加テスト用の店舗情報フォームデータを取得
	 *</pre>
	 * @param shopSort
	 * @return
	 *
	 */
	private ShopInfoForm inputAddShopInfoForm(String shopSort) {
		// 店舗情報のformデータ
		ShopInfoForm form = new ShopInfoForm();
		// アクション
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		// 店舗コード:設定値なし(新規で自動採番される)
		// 店舗区分
		form.setShopKubun("904");
		// 店舗名
		form.setShopName("靴店舗(新規追加)");
		// 表示順:値がnullの場合、表示順の値は自動採番される
		if(shopSort != null) {
			form.setShopSort(Integer.parseInt(shopSort));
		}
		// 表示順(更新比較用):設定値なし
		
		return form;
	}
	
	/**
	 *<pre>
	 * 更新テスト用の店舗情報フォームデータを取得
	 * (更新項目：店舗区分、店舗名、表示順(値nullの場合、値の変更なし)
	 *</pre>
	 * @param shopSort
	 * @return
	 *
	 */
	private ShopInfoForm inputUpdateShopInfoForm(String shopSort) {
		// 店舗情報のformデータ
		ShopInfoForm form = new ShopInfoForm();
		// アクション
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		// 店舗コード
		form.setShopCode("002");
		// 店舗区分
		form.setShopKubun("904");
		// 店舗名
		form.setShopName("靴店舗に更新(ホームセンター更新後)");
		// 表示順
		if(shopSort != null) {
			form.setShopSort(Integer.parseInt(shopSort));
		} else {
			form.setShopSort(null);
		}
		// 表示順(更新比較用)
		form.setShopSortBefore("002");
		
		return form;
	}
	
	/**
	 *<pre>
	 * テストユーザを条件に、店舗テーブルを検索した結果を返します。
	 *</pre>
	 * @return
	 *
	 */
	private List<ShopReadWriteDto> execQueryAllShopList() {
		// テストユーザで店舗テーブルを検索
		return namedParamTemplete.query(
				// 店舗テーブル検索SQL(unique data)
				"SELECT * FROM SHOP_TABLE WHERE USER_ID = :USER_ID ORDER BY SHOP_CODE",
				// ユーザID
				Map.of("USER_ID", TEST_USER.getUserId().toString()),
				// ShopReadWriteDtoのRowMapper
				new DataClassRowMapper<>(ShopReadWriteDto.class));
	}
	
	/**
	 *<pre>
	 * 更新テストで実施したDBに格納された更新データを取得します。
	 * テストユーザと店舗コード002を条件に、店舗テーブルを検索した結果を返します。
	 *</pre>
	 * @return
	 *
	 */
	private ShopReadWriteDto execQueryUpdateShop() {
		// テストユーザで店舗テーブルを検索
		return namedParamTemplete.queryForObject(
				// 商品テーブル検索SQL(unique data)
				"SELECT * FROM SHOP_TABLE WHERE USER_ID=:USER_ID AND SHOP_CODE=:SHOP_CODE",
				// パラメータ
				Map.of(
					// ユーザID
					"USER_ID", TEST_USER.getUserId().toString(),
					// 店舗コード
					"SHOP_CODE", "002"
				),
				// ShopReadWriteDtoのRowMapper
				new DataClassRowMapper<>(ShopReadWriteDto.class));
	}
	
}
