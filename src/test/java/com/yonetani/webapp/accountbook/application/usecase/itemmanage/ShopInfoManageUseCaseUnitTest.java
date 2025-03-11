/**
 * 店情報管理ユースケース(ShopInfoManageUseCase.java)の単体テストクラスです。
 * インテグレーションテストケースで対応できないテストをユニットテストケースでテストします。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/02/11 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.yonetani.webapp.accountbook.common.component.CodeTableItemComponent;

/**
 *<pre>
 * 店情報管理ユースケース(ShopInfoManageUseCase.java)の単体テストクラスです。
 * インテグレーションテストケースで対応できないテストをユニットテストケースでテストします。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@ExtendWith(MockitoExtension.class)
class ShopInfoManageUseCaseUnitTest {

	// テスト対象のサービスクラス
	@InjectMocks
	private ShopInfoManageUseCase useCase;
	
	// モック:コードテーブルコンポーネント
	@Mock
	private CodeTableItemComponent codeTableItem;
	
	
	/**
	 * コードテーブルに店舗区分情報なしの場合
	 * 
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#readShopInfo(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo)} のためのテスト・メソッド。
	 * 
	 */
	@Test
	void testReadShopInfoShopKubunNotFound() {
		fail("まだ実装されていません");
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
