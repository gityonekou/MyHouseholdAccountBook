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

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ModelMap;

import com.yonetani.webapp.accountbook.common.component.CodeTableItemComponent;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.shop.ShopInquiryList;
import com.yonetani.webapp.accountbook.domain.model.common.CodeAndValuePair;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.repository.account.shop.ShopTableRepository;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShopInfoManageResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

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
	private ShopInfoManageUseCase service;
	
	// モック:コードテーブルコンポーネント
	@Mock
	private CodeTableItemComponent codeTableItem;
	
	// モック:店舗情報取得リポジトリー
	@Mock
	private ShopTableRepository shopRepository;
	
	// ユーザ情報
	private final LoginUserInfo TEST_USER = LoginUserInfo.from("TESTUSER001", "テストユーザ01");
	
	/**
	 * コードテーブルに店舗区分情報なしの場合
	 * (検索条件に対応する店情報1件あり)
	 * 
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#readShopInfo(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo)} のためのテスト・メソッド。
	 * 
	 */
	@Test
	void testReadShopInfoShopKubunNotFound() {
		doReturn(null).when(codeTableItem).getCodeValues(MyHouseholdAccountBookContent.CODE_DEFINES_SHOP_KUBUN);
		// 検索条件に対応する店情報1件で画面表示情報を取得
		MyHouseholdAccountBookRuntimeException ex = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> service.readShopInfo(TEST_USER),
				"コード定義ファイルに店舗区分情報の登録なしの場合エラーとなること");
		assertEquals(
				ex.getLocalizedMessage(),
				"コード定義ファイルに「店舗区分情報：" + MyHouseholdAccountBookContent.CODE_DEFINES_SHOP_KUBUN + "」が登録されていません。管理者に問い合わせてください",
				"例外メッセージが等しいこと");
	}
	
	/**
	 * コードテーブルに店舗区分情報1件の場合
	 * 
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#readShopInfo(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo)} のためのテスト・メソッド。
	 * 
	 */
	@Test
	void testReadShopInfoShopKubunResultOne() {
		// コードテーブルの店舗区分情報1件を設定
		List<CodeAndValuePair> shopGroupList = new ArrayList<>();
		shopGroupList.add(CodeAndValuePair.from("901", "食品・日用品店舗"));
		doReturn(shopGroupList).when(codeTableItem).getCodeValues(MyHouseholdAccountBookContent.CODE_DEFINES_SHOP_KUBUN);
		// 検索結果0件を登録
		doReturn(ShopInquiryList.from(null)).when(shopRepository).findById(SearchQueryUserId.from(UserId.from(TEST_USER.getUserId())));
		// 検索条件に対応する店情報0件で画面表示情報を取得
		ShopInfoManageResponse res = service.readShopInfo(TEST_USER);
		
		// 画面表示情報からviewを生成し、Modelマップを取得
		ModelMap modelMap = res.build().getModelMap();
		
		// 店舗グループの選択ボックス情報の設定値が想定通りであること
		SelectViewItem shopKubunItem = (SelectViewItem)modelMap.getAttribute("shopKubun");
		assertIterableEquals(shopKubunSelectListOne(), shopKubunItem.getOptionList(), "店舗グループの選択ボックス情報が想定通りであること");
		
	}
	
	/**
	 * コードテーブルに店舗区分情報3件の場合
	 * 
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#readShopInfo(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo)} のためのテスト・メソッド。
	 * 
	 */
	@Test
	void testReadShopInfoShopKubunResultThree() {
		// コードテーブルの店舗区分情報1件を設定
		List<CodeAndValuePair> shopGroupList = new ArrayList<>();
		shopGroupList.add(CodeAndValuePair.from("901", "食品・日用品店舗"));
		shopGroupList.add(CodeAndValuePair.from("902", "ホームセンター"));
		shopGroupList.add(CodeAndValuePair.from("903", "衣類店舗"));
		doReturn(shopGroupList).when(codeTableItem).getCodeValues(MyHouseholdAccountBookContent.CODE_DEFINES_SHOP_KUBUN);
		// 検索結果0件を登録
		doReturn(ShopInquiryList.from(null)).when(shopRepository).findById(SearchQueryUserId.from(UserId.from(TEST_USER.getUserId())));
		// 検索条件に対応する店情報0件で画面表示情報を取得
		ShopInfoManageResponse res = service.readShopInfo(TEST_USER);
		
		// 画面表示情報からviewを生成し、Modelマップを取得
		ModelMap modelMap = res.build().getModelMap();
		
		// 店舗グループの選択ボックス情報の設定値が想定通りであること
		SelectViewItem shopKubunItem = (SelectViewItem)modelMap.getAttribute("shopKubun");
		assertIterableEquals(shopKubunSelectListThree(), shopKubunItem.getOptionList(), "店舗グループの選択ボックス情報が想定通りであること");
		
	}
	
	/**
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	void testExecAction() {
		fail("まだ実装されていません");
	}
	
	/**
	 *<pre>
	 * コードテーブルの店舗区分情報1件の場合の店舗グループの選択ボックス情報(固定値)をリスト形式で取得
	 *</pre>
	 * @return 店舗グループの選択ボックス情報
	 *
	 */
	List<OptionItem> shopKubunSelectListOne() {
		List<OptionItem> optionList = new ArrayList<>();
		optionList.add(OptionItem.from("", "グループメニューを開く"));
		optionList.add(OptionItem.from("901", "食品・日用品店舗"));
		return optionList;
	}
	
	/**
	 *<pre>
	 * コードテーブルの店舗区分情報3件の場合の店舗グループの選択ボックス情報(固定値)をリスト形式で取得
	 *</pre>
	 * @return 店舗グループの選択ボックス情報
	 *
	 */
	List<OptionItem> shopKubunSelectListThree() {
		List<OptionItem> optionList = new ArrayList<>();
		optionList.add(OptionItem.from("", "グループメニューを開く"));
		optionList.add(OptionItem.from("901", "食品・日用品店舗"));
		optionList.add(OptionItem.from("902", "ホームセンター"));
		optionList.add(OptionItem.from("903", "衣類店舗"));
		return optionList;
	}
}
