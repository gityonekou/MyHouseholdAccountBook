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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShopInfoManageResponse;
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
class ShopInfoManageUseCaseTest {
	// 店情報管理ユースケースサービスをインジェクション
	@Autowired
	private ShopInfoManageUseCase service;
	
	// ユーザ情報
	private final LoginUserInfo TEST_USER = LoginUserInfo.from("kouki", "米谷 幸城");
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
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#readShopInfo(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo)} のためのテスト・メソッド。
	 */
	@Test
	void testReadShopInfoLoginUserInfo() {
		// 0件データかどうか
		ShopInfoManageResponse res = service.readShopInfo(TEST_USER);
		if(res.getMessagesList().size() != 1) {
			fail("エラーのレスポンスメッセージが設定されていない");
		} else {
			assertEquals("店舗情報取得結果が0件です。", res.getMessagesList().get(0), "0件データのエラーとなるかどうか");	
		}
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#readShopInfo(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, java.lang.String)} のためのテスト・メソッド。
	 */
	@Test
	void testReadShopInfoLoginUserInfoString() {
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
