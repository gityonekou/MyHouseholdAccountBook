/**
 * 店情報管理ユースケース(ShopInfoManageUseCase.java)の単体テストクラスです。
 * インテグレーションテストケースで対応できないテストをユニットテストケースでテストします。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2025/02/11 : 2.00.00(B)  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ModelMap;

import com.yonetani.webapp.accountbook.common.component.CodeTableItemComponent;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.shop.Shop;
import com.yonetani.webapp.accountbook.domain.model.account.shop.ShopInquiryList;
import com.yonetani.webapp.accountbook.domain.model.common.CodeAndValuePair;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopSort;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopSortBetweenAB;
import com.yonetani.webapp.accountbook.domain.repository.account.shop.ShopTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopSort;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm;
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
 * @since 家計簿アプリ(2.00.B)
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
				"コード定義ファイルに「店舗区分情報：" + MyHouseholdAccountBookContent.CODE_DEFINES_SHOP_KUBUN + "」が登録されていません。管理者に問い合わせてください",
				ex.getLocalizedMessage(),
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
	 * 現在の900番以下のデータ件数が899件の場合、エラーとなることを確認(新規追加時はインクリメントされて900件で比較されるのでエラーとする)
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	void testExecAddTypeCount899Action() {
		// テストユーザのユーザID
		UserId userID = UserId.from(TEST_USER.getUserId());
		// 現在の900番以下の件数に899件を設定
		doReturn(899).when(shopRepository).countByIdAndLessThanNineHundred(SearchQueryUserId.from(userID));
		
		// 新規登録処理
		ShopInfoManageResponse res = service.execAction(TEST_USER, inputAddShopInfoForm());
		// 「店舗は900件以上登録できません。管理者に問い合わせてください。」のエラーメッセージが設定されていること
		if(res.getMessagesList().size() != 1) {
			fail("「店舗は900件以上登録できません」のエラーメッセージが設定されていない");
		} else {
			assertEquals("店舗は900件以上登録できません。管理者に問い合わせてください。", res.getMessagesList().get(0), "「店舗は900件以上登録できません」のエラーメッセージが設定されていること");	
		}
		// トランザクションが完了のステータスになっていないこと
		assertFalse(res.isTransactionSuccessFull(), "トランザクションが完了のステータスになっていないこと");
	}
	
	/**
	 * 現在の900番以下のデータ件数が900件の場合、エラーとなることを確認(新規追加時)
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	void testExecAddTypeCount900Action() {
		// テストユーザのユーザID
		UserId userID = UserId.from(TEST_USER.getUserId());
		// 現在の900番以下の件数に900件を設定
		doReturn(900).when(shopRepository).countByIdAndLessThanNineHundred(SearchQueryUserId.from(userID));
		
		// 新規登録処理
		ShopInfoManageResponse res = service.execAction(TEST_USER, inputAddShopInfoForm());
		// 「店舗は900件以上登録できません。管理者に問い合わせてください。」のエラーメッセージが設定されていること
		if(res.getMessagesList().size() != 1) {
			fail("「店舗は900件以上登録できません」のエラーメッセージが設定されていない");
		} else {
			assertEquals("店舗は900件以上登録できません。管理者に問い合わせてください。", res.getMessagesList().get(0), "「店舗は900件以上登録できません」のエラーメッセージが設定されていること");	
		}
		// トランザクションが完了のステータスになっていないこと
		assertFalse(res.isTransactionSuccessFull(), "トランザクションが完了のステータスになっていないこと");
	}
	
	/**
	 * 現在の900番以下のデータ件数が898件の場合、トランザクションが完了となることを確認(新規追加時)
	 * sortList.isEmpty()の結果が空の場合に既存の店舗データの表示順更新が呼ばれないことを確認。
	 * 
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	void testExecAddTypeCount898Action() {
		// テストユーザのユーザID
		UserId userID = UserId.from(TEST_USER.getUserId());
		// 新規追加テスト用フォームデータを作成(表示順=null:表示順は自動採番)
		ShopInfoForm form = inputAddShopInfoForm();
		// 店舗情報取得リポジトリー(ShopTableRepository)のaddメソッドに渡された引数の値をキャプチャー
		ArgumentCaptor<Shop> addCaptor = ArgumentCaptor.forClass(Shop.class);
		// 現在の900番以下の件数に898件を設定
		doReturn(898).when(shopRepository).countByIdAndLessThanNineHundred(SearchQueryUserId.from(userID));
		// 指定した表示順より大きい表示順の値の店舗情報を取得で空を返却(sortList.isEmpty()の結果が空の場合のカバレッジもここで行う)
		doReturn(ShopInquiryList.from(null)).when(shopRepository).findById(
				SearchQueryUserIdAndShopSort.from(userID, ShopSort.from(form.getShopSort())));
		// 新規追加時の戻り値に1を指定、引数で渡された値をキャプチャーする
		doReturn(1).when(shopRepository).add(addCaptor.capture());
		
		// 新規登録処理
		ShopInfoManageResponse res = service.execAction(TEST_USER, form);
		// 新規追加完了のメッセージが設定されていること
		if(res.getMessagesList().size() != 1) {
			fail("新規店舗追加のレスポンスメッセージが設定されていない");
		} else {
			assertEquals("新規店舗を追加しました。[code:899]" + form.getShopName(), res.getMessagesList().get(0), "新規店舗追加のメッセージが設定されていること");	
		}
		// トランザクションが完了のステータスになっていること
		assertTrue(res.isTransactionSuccessFull(), "トランザクションが完了のステータスになっていること");
		
		// 表示順の更新処理が呼ばれないこと
		verify(shopRepository, never()).updateShopSort(any());
		
		// addメソッドに渡された引数の値を確認
		Shop addShop = addCaptor.getValue();
		assertEquals("TESTUSER001", addShop.getUserId().getValue(), "ユーザIDがTESTUSER001であること");
		assertEquals("899", addShop.getShopCode().getValue(), "店舗コードが899であること");
		assertEquals("904", addShop.getShopKubunCode().getValue(), "店舗区分コードが904であること");
		assertEquals("靴店舗(新規追加)", addShop.getShopName().getValue(), "店舗名が靴店舗(新規追加)であること");
		assertEquals("003", addShop.getShopSort().getValue(), "店舗表示順が003であること");
	}
	
	/**
	 * 新規データの追加結果が0件の場合、業務エラーとなることを確認
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	void testExecAddTypeAddResultZeroAction() {
		// テストユーザのユーザID
		UserId userID = UserId.from(TEST_USER.getUserId());
		// 店舗情報取得リポジトリー(ShopTableRepository)のaddメソッドに渡された引数の値をキャプチャー
		ArgumentCaptor<Shop> addCaptor = ArgumentCaptor.forClass(Shop.class);
		// 現在の900番以下の件数に0件を設定
		doReturn(0).when(shopRepository).countByIdAndLessThanNineHundred(SearchQueryUserId.from(userID));
		// 新規追加時の戻り値に0を指定
		doReturn(0).when(shopRepository).add(addCaptor.capture());
		// 追加件数が1件以外の場合、業務エラーとなること
		MyHouseholdAccountBookRuntimeException ex = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> service.execAction(TEST_USER, inputAddShopInfoForm()),
				"追加件数が1件以外の場合、業務エラーとなること");
		assertEquals(
				"店舗テーブルへの追加件数が不正でした。[件数=0][add data:" + addCaptor.getValue() + "]",
				ex.getLocalizedMessage(),
				"例外メッセージが等しいこと");
		
	}
	
	/**
	 * 現在の900番以下のデータ件数が900件の場合、エラーとなることを確認(更新時)
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	void testExecUpdTypeCount900Action() {
		// テストユーザのユーザID
		UserId userID = UserId.from(TEST_USER.getUserId());
		// 現在の900番以下の件数に900件を設定
		doReturn(900).when(shopRepository).countByIdAndLessThanNineHundred(SearchQueryUserId.from(userID));
		// 更新処理
		ShopInfoManageResponse res = service.execAction(TEST_USER, inputUpdShopInfoForm(null));
		// 「店舗が900件以上登録されているため店舗情報を更新できません」のエラーメッセージが設定されていること
		if(res.getMessagesList().size() != 1) {
			fail("「店舗が900件以上登録されているため店舗情報を更新できません」のエラーメッセージが設定されていない");
		} else {
			assertEquals("店舗が900件以上登録されているため店舗情報を更新できません。管理者に問い合わせてください。", res.getMessagesList().get(0), "「店舗が900件以上登録されているため店舗情報を更新できません」のエラーメッセージが設定されていること");	
		}
		// トランザクションが完了のステータスになっていないこと
		assertFalse(res.isTransactionSuccessFull(), "トランザクションが完了のステータスになっていないこと");
	}
	
	/**
	 * 現在の900番以下のデータ件数が901件の場合、エラーとなることを確認(更新時)
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	void testExecUpdTypeCount901Action() {
		// テストユーザのユーザID
		UserId userID = UserId.from(TEST_USER.getUserId());
		// 現在の900番以下の件数に901件を設定
		doReturn(901).when(shopRepository).countByIdAndLessThanNineHundred(SearchQueryUserId.from(userID));
		// 更新処理
		ShopInfoManageResponse res = service.execAction(TEST_USER, inputUpdShopInfoForm(null));
		// 「店舗が900件以上登録されているため店舗情報を更新できません」のエラーメッセージが設定されていること
		if(res.getMessagesList().size() != 1) {
			fail("「店舗が900件以上登録されているため店舗情報を更新できません」のエラーメッセージが設定されていない");
		} else {
			assertEquals("店舗が900件以上登録されているため店舗情報を更新できません。管理者に問い合わせてください。", res.getMessagesList().get(0), "「店舗が900件以上登録されているため店舗情報を更新できません」のエラーメッセージが設定されていること");	
		}
		// トランザクションが完了のステータスになっていないこと
		assertFalse(res.isTransactionSuccessFull(), "トランザクションが完了のステータスになっていないこと");
	}
	
	/**
	 * 現在の900番以下のデータ件数が899件の場合、トランザクションが完了となることを確認(更新時)
	 * 旧表示順＞新表示順の場合のsortList.isEmpty()の結果が空の場合に既存の店舗データの表示順更新が呼ばれないことを確認。
	 * 
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	void testExecUpdTypeCount899ShopSortDownAction() {
		// テストユーザのユーザID
		UserId userID = UserId.from(TEST_USER.getUserId());
		// 更新テスト用フォームデータを作成
		ShopInfoForm form = inputUpdShopInfoForm("003");
		// 店舗情報取得リポジトリー(ShopTableRepository)のupdateメソッドに渡された引数の値をキャプチャー
		ArgumentCaptor<Shop> updateCaptor = ArgumentCaptor.forClass(Shop.class);
		// 現在の900番以下の件数に899件を設定
		doReturn(899).when(shopRepository).countByIdAndLessThanNineHundred(SearchQueryUserId.from(userID));
		// 指定した店舗表示順A～店舗表示順B間の店舗情報を取得で空を返却(sortList.isEmpty()の結果が空の場合のカバレッジもここで行う)
		int searchBIntVal = Integer.parseInt(form.getShopSortBefore()) - 1;
		doReturn(ShopInquiryList.from(null)).when(shopRepository).findById(
				SearchQueryUserIdAndShopSortBetweenAB.from(
						userID,
						ShopSort.from(form.getShopSort()),
						ShopSort.from(searchBIntVal)));
		// 更新の戻り値に1を指定、引数で渡された値をキャプチャーする
		doReturn(1).when(shopRepository).update(updateCaptor.capture());
		
		// 更新処理
		ShopInfoManageResponse res = service.execAction(TEST_USER, form);
		// 更新完了のメッセージが設定されていること
		if(res.getMessagesList().size() != 1) {
			fail("更新完了のレスポンスメッセージが設定されていない");
		} else {
			assertEquals("店舗を更新しました。[code:" + form.getShopCode() + "]" + form.getShopName(), res.getMessagesList().get(0), "更新完了のメッセージが設定されていること");	
		}
		// トランザクションが完了のステータスになっていること
		assertTrue(res.isTransactionSuccessFull(), "トランザクションが完了のステータスになっていること");
		
		// 表示順の更新処理が呼ばれないこと
		verify(shopRepository, never()).updateShopSort(any());
		
		// updateメソッドに渡された引数の値を確認
		Shop updShop = updateCaptor.getValue();
		assertEquals("TESTUSER001", updShop.getUserId().getValue(), "ユーザIDがTESTUSER001であること");
		assertEquals("002", updShop.getShopCode().getValue(), "店舗コードが002であること");
		assertEquals("904", updShop.getShopKubunCode().getValue(), "店舗区分コードが904であること");
		assertEquals("靴店舗に更新(ホームセンター更新後)", updShop.getShopName().getValue(), "店舗名が「靴店舗に更新(ホームセンター更新後)」であること");
		assertEquals("003", updShop.getShopSort().getValue(), "店舗表示順が003であること");
	}
	
	/**
	 * 現在の900番以下のデータ件数が899件の場合、トランザクションが完了となることを確認(更新時)
	 * 旧表示順＜新表示順の場合のsortList.isEmpty()の結果が空の場合に既存の店舗データの表示順更新が呼ばれないことを確認。
	 * 
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	void testExecUpdTypeCount899ShopSortUpAction() {
		// テストユーザのユーザID
		UserId userID = UserId.from(TEST_USER.getUserId());
		// 更新テスト用フォームデータを作成
		ShopInfoForm form = inputUpdShopInfoForm("005");
		// 店舗情報取得リポジトリー(ShopTableRepository)のupdateメソッドに渡された引数の値をキャプチャー
		ArgumentCaptor<Shop> updateCaptor = ArgumentCaptor.forClass(Shop.class);
		// 現在の900番以下の件数に899件を設定
		doReturn(899).when(shopRepository).countByIdAndLessThanNineHundred(SearchQueryUserId.from(userID));
		// 指定した店舗表示順A～店舗表示順B間の店舗情報を取得で空を返却(sortList.isEmpty()の結果が空の場合のカバレッジもここで行う)
		int searchBIntVal = Integer.parseInt(form.getShopSortBefore()) + 1;
		doReturn(ShopInquiryList.from(null)).when(shopRepository).findById(
				SearchQueryUserIdAndShopSortBetweenAB.from(
						userID,
						ShopSort.from(searchBIntVal),
						ShopSort.from(form.getShopSort())
						));
		// 更新の戻り値に1を指定、引数で渡された値をキャプチャーする
		doReturn(1).when(shopRepository).update(updateCaptor.capture());
		
		// 更新処理
		ShopInfoManageResponse res = service.execAction(TEST_USER, form);
		// 更新完了のメッセージが設定されていること
		if(res.getMessagesList().size() != 1) {
			fail("更新完了のレスポンスメッセージが設定されていない");
		} else {
			assertEquals("店舗を更新しました。[code:" + form.getShopCode() + "]" + form.getShopName(), res.getMessagesList().get(0), "更新完了のメッセージが設定されていること");	
		}
		// トランザクションが完了のステータスになっていること
		assertTrue(res.isTransactionSuccessFull(), "トランザクションが完了のステータスになっていること");
		
		// 表示順の更新処理が呼ばれないこと
		verify(shopRepository, never()).updateShopSort(any());
		
		// updateメソッドに渡された引数の値を確認
		Shop addShop = updateCaptor.getValue();
		assertEquals("TESTUSER001", addShop.getUserId().getValue(), "ユーザIDがTESTUSER001であること");
		assertEquals("002", addShop.getShopCode().getValue(), "店舗コードが002であること");
		assertEquals("904", addShop.getShopKubunCode().getValue(), "店舗区分コードが904であること");
		assertEquals("靴店舗に更新(ホームセンター更新後)", addShop.getShopName().getValue(), "店舗名が「靴店舗に更新(ホームセンター更新後)」であること");
		assertEquals("005", addShop.getShopSort().getValue(), "店舗表示順が005であること");
	}
	
	/**
	 * 店舗情報の更新結果が0件の場合、業務エラーとなることを確認
	 * 旧表示順＜新表示順の条件を設定
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	void testExecUpdTypeUpdateResultZeroAction() {
		// テストユーザのユーザID
		UserId userID = UserId.from(TEST_USER.getUserId());
		// 更新テスト用フォームデータを作成
		ShopInfoForm form = inputUpdShopInfoForm("005");
		// 店舗情報取得リポジトリー(ShopTableRepository)のupdateメソッドに渡された引数の値をキャプチャー
		ArgumentCaptor<Shop> updateCaptor = ArgumentCaptor.forClass(Shop.class);
		// 現在の900番以下の件数に10件を設定
		doReturn(10).when(shopRepository).countByIdAndLessThanNineHundred(SearchQueryUserId.from(userID));
		// 指定した店舗表示順A～店舗表示順B間の店舗情報を取得で空を返却(sortList.isEmpty()の結果が空の場合のカバレッジもここで行う)
		int searchBIntVal = Integer.parseInt(form.getShopSortBefore()) + 1;
		doReturn(ShopInquiryList.from(null)).when(shopRepository).findById(
				SearchQueryUserIdAndShopSortBetweenAB.from(
						userID,
						ShopSort.from(searchBIntVal),
						ShopSort.from(form.getShopSort())
						));
		// 更新時の戻り値に0を指定
		doReturn(0).when(shopRepository).update(updateCaptor.capture());
		
		// 更新件数が1件以外の場合、業務エラーとなること
		MyHouseholdAccountBookRuntimeException ex = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> service.execAction(TEST_USER, form),
				"更新件数が1件以外の場合、業務エラーとなること");
		assertEquals(
				"店舗テーブルへの更新件数が不正でした。[件数=0][update data:" + updateCaptor.getValue() + "]",
				ex.getLocalizedMessage(),
				"例外メッセージが等しいこと");
	}
	
	/**
	 * 既存データのソート順の更新結果が0件の場合、業務エラーとなることを確認
	 * 旧表示順のデータ取得は「旧表示順＞新表示順」でテストデータを設定する
	 * {@link com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase#execAction(com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo, com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm)} のためのテスト・メソッド。
	 */
	@Test
	void testExecUpdateShopSortResultZeroAction() {
		// テストユーザのユーザID
		UserId userID = UserId.from(TEST_USER.getUserId());
		// 更新テスト用フォームデータを作成
		ShopInfoForm form = inputUpdShopInfoForm("002");
		// 店舗情報取得リポジトリー(ShopTableRepository)のupdateShopSortメソッドに渡された引数の値をキャプチャー
		ArgumentCaptor<Shop> updateShopSortCaptor = ArgumentCaptor.forClass(Shop.class);
		// 現在の900番以下の件数に10件を設定
		doReturn(10).when(shopRepository).countByIdAndLessThanNineHundred(SearchQueryUserId.from(userID));
		// 指定した店舗表示順A～店舗表示順B間の店舗情報を取得で空を返却(sortList.isEmpty()の結果が空の場合のカバレッジもここで行う)
		// 「旧表示順＞新表示順」の場合の検索条件を設定
		int searchBIntVal = Integer.parseInt(form.getShopSortBefore()) - 1;
		// 表示順検索結果のリストを設定
		List<Shop> shopList = new ArrayList<>();
		shopList.add(Shop.from(
				"TESTUSER001",
				form.getShopCode(),
				form.getShopKubun(),
				form.getShopName(), 
				"003"));
		// 店舗表示順A～店舗表示順B間の検索処理のモックを設定
		doReturn(ShopInquiryList.from(shopList)).when(shopRepository).findById(
				SearchQueryUserIdAndShopSortBetweenAB.from(
						userID,
						ShopSort.from(form.getShopSort()),
						ShopSort.from(searchBIntVal)));
		// 更新時の戻り値に1を指定
		doReturn(1).when(shopRepository).update(any());
		// 既存データのソート順の更新の戻り値に0を指定
		doReturn(0).when(shopRepository).updateShopSort(updateShopSortCaptor.capture());
		
		// 既存店舗情報の更新件数が1件以外の場合、業務エラーとなること
		MyHouseholdAccountBookRuntimeException ex = assertThrows(
				MyHouseholdAccountBookRuntimeException.class,
				() -> service.execAction(TEST_USER, form),
				"更新件数が1件以外の場合、業務エラーとなること");
		assertEquals(
				"店舗テーブルへの更新件数が不正でした。[件数=0][update data:" + updateShopSortCaptor.getValue() + "]",
				ex.getLocalizedMessage(),
				"例外メッセージが等しいこと");
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
	
	/**
	 *<pre>
	 * 追加テスト用の店舗情報フォームデータを取得
	 *</pre>
	 * @return
	 *
	 */
	private ShopInfoForm inputAddShopInfoForm() {
		// 店舗情報のformデータ
		ShopInfoForm form = new ShopInfoForm();
		// アクション
		form.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		// 店舗区分
		form.setShopKubun("904");
		// 店舗名
		form.setShopName("靴店舗(新規追加)");
		// 表示順
		form.setShopSort(Integer.parseInt("003"));
		
		return form;
	}
	
	/**
	 *<pre>
	 * 更新テスト用の店舗情報フォームデータを取得
	 *</pre>
	 * @param shopSort
	 * @return
	 *
	 */
	private ShopInfoForm inputUpdShopInfoForm(String shopSort) {
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
		form.setShopSortBefore("004");
		
		return form;
	}
}
