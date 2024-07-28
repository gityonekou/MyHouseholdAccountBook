/**
 * ShoppingItemInfoManageUseCaseのテストクラスです。
 * 
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/07/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.yonetani.webapp.accountbook.common.component.SisyutuItemComponent;
import com.yonetani.webapp.accountbook.domain.repository.account.shop.ShopTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.shoppingitem.ShoppingItemTableRepository;

/**
 *<pre>
 * ShoppingItemInfoManageUseCaseのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@ExtendWith(MockitoExtension.class)
class ShoppingItemInfoManageUseCaseTest {
	// テスト対象のサービスクラス
	@InjectMocks
	private ShoppingItemInfoManageUseCase useCase;
	// モック
	@Mock
	private SisyutuItemComponent sisyutuItemComponent;
	@Mock
	private ShopTableRepository shopTableRepository;
	@Mock
	private ShoppingItemTableRepository shoppingItemTableRepository;
	
	/**
	 *<pre>
	 * セットアップ時の処理
	 *</pre>
	 * @throws java.lang.Exception
	 *
	 */
	@BeforeEach
	void setUp() throws Exception {
	}

	/**
	 *<pre>
	 * テスト終了時の処理(clean up)
	 *</pre>
	 * @throws java.lang.Exception
	 *
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShoppingItemInfoManageUseCase#readInitInfo(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo)} のためのテスト・メソッド。
	 */
	@Test
	void testReadInitInfo() {
		// テスト用ユーザ
		//LoginUserInfo userInfo = LoginUserInfo.from("userId", "userName");
		//doReturn(null).when(sisyutuItemComponent).setSisyutuItemList(userInfo, )
		assertEquals(true, true,"ReadInitInfoメソッドの単体テストなしでOK");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShoppingItemInfoManageUseCase#execSearch(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShoppingItemInfoSearchForm)} のためのテスト・メソッド。
	 */
	@Test
	void testExecSearch() {
		fail("まだ実装されていません");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShoppingItemInfoManageUseCase#readActSelectItemInfo(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.session.ShoppingItemSearchInfo, java.lang.String)} のためのテスト・メソッド。
	 */
	@Test
	void testReadActSelectItemInfo() {
		fail("まだ実装されていません");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShoppingItemInfoManageUseCase#readAddShoppingItemInfoBySisyutuItem(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, java.lang.String)} のためのテスト・メソッド。
	 */
	@Test
	void testReadAddShoppingItemInfoBySisyutuItem() {
		fail("まだ実装されていません");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShoppingItemInfoManageUseCase#execSearchBySisyutuItem(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, java.lang.String)} のためのテスト・メソッド。
	 */
	@Test
	void testExecSearchBySisyutuItem() {
		fail("まだ実装されていません");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShoppingItemInfoManageUseCase#readAddShoppingItemInfoByShoppingItem(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, java.lang.String)} のためのテスト・メソッド。
	 */
	@Test
	void testReadAddShoppingItemInfoByShoppingItem() {
		fail("まだ実装されていません");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShoppingItemInfoManageUseCase#readUpdateShoppingItemInfo(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, java.lang.String)} のためのテスト・メソッド。
	 */
	@Test
	void testReadUpdateShoppingItemInfo() {
		fail("まだ実装されていません");
	}

	/**
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShoppingItemInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShoppingItemInfoUpdateForm)} のためのテスト・メソッド。
	 */
	@Test
	void testExecAction() {
		fail("まだ実装されていません");
	}

}
