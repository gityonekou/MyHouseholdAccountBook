/**
 * 商品情報管理ユースケースです。
 * ・情報管理(商品)初期表示画面情報取得(支出項目一覧情報を取得)
 * ・情報管理(商品)検索結果画面情報取得(商品検索結果を取得)
 * ・情報管理(商品)処理選択画面情報取得(選択商品へのアクション選択)
 * ・情報管理(商品)更新画面情報取得(選択した支出項目の詳細情報取得)
 * ・情報管理(商品)検索結果画面情報取得(指定した支出項目に属する商品情報(一覧)を取得)
 * ・情報管理(商品)更新画面情報取得(選択した商品をもとに新規商品を追加)
 * ・情報管理(商品)更新画面情報取得(選択した商品を更新)
 * ・指定の商品情報取得
 * ・商品情報の追加・更新
 * ・商品情報の追加・更新のバリデーションチェックNG時
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonetani.webapp.accountbook.common.component.SisyutuItemComponent;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.shop.ShopInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopKubunCodeList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemSortBetweenAB;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.SisyutuItemTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.shop.ShopTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopKubunCode;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShoppingItemInfoSearchForm;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShoppingItemInfoUpdateForm;
import com.yonetani.webapp.accountbook.presentation.request.session.UserSession;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.AbstractExpenditureItemInfoManageResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShoppingItemInfoManageActSelect;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShoppingItemInfoManageInitResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShoppingItemInfoManageSearchResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShoppingItemInfoManageUpdateResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * ・情報管理(商品)初期表示画面情報取得(支出項目一覧情報を取得)
 * ・情報管理(商品)検索結果画面情報取得(商品検索結果を取得)
 * ・情報管理(商品)処理選択画面情報取得(選択商品へのアクション選択)
 * ・情報管理(商品)更新画面情報取得(選択した支出項目の詳細情報取得)
 * ・情報管理(商品)検索結果画面情報取得(指定した支出項目に属する商品情報(一覧)を取得)
 * ・情報管理(商品)更新画面情報取得(選択した商品をもとに新規商品を追加)
 * ・情報管理(商品)更新画面情報取得(選択した商品を更新)
 * ・指定の商品情報取得
 * ・商品情報の追加・更新
 * ・商品情報の追加・更新のバリデーションチェックNG時
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class ShoppingItemInfoManageUseCase {

	// 支出項目テーブル:SISYUTU_ITEM_TABLE参照リポジトリー
	private final SisyutuItemTableRepository sisyutuItemRepository;
	
	// 支出項目情報取得コンポーネント
	private final SisyutuItemComponent sisyutuItemComponent;
	
	// 店舗情報取得リポジトリー
	private final ShopTableRepository shopRepository;
	
	/**
	 *<pre>
	 * 指定したユーザIDに応じた情報管理(商品) 初期表示画面の表示情報を取得します。
	 * ユーザIDをもとに支出項目一覧情報(日用消耗品と食費)を取得し、画面表示データに設定します。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @return 情報管理(商品) 初期表示画面の表示情報
	 *
	 */
	public ShoppingItemInfoManageInitResponse readInitInfo(UserSession user) {
		log.debug("readInitInfo:userid=" + user.getUserId());
		// レスポンス
		ShoppingItemInfoManageInitResponse response = ShoppingItemInfoManageInitResponse.getInstance();
		
		// 支出項目：飲食日用品から、日用消耗品と飲食の項目をすべて取得(表示順から取得する)
		SisyutuItemInquiryList sisyutuItemSearchResult = sisyutuItemRepository.findByIdAndSisyutuItemSortBetween(
				SearchQueryUserIdAndSisyutuItemSortBetweenAB.from(
						// 検索条件:ユーザID
						user.getUserId(),
						// 検索条件:支出項目表示順A：日用消耗品の表示順の値:0502000000 (変更不可の値なので固定値)
						MyHouseholdAccountBookContent.SISYUTU_ITEM_NITIYOU_SYOUMOUHIN_SORT_VALUE,
						// 検索条件:支出項目表示順A：食費の表示順最大値:0503999999
						MyHouseholdAccountBookContent.SISYUTU_ITEM_INSYOKU_SORT_MAX_VALUE));
		
		if(sisyutuItemSearchResult.isEmpty()) {
			// 支出項目情報が0件の場合、メッセージを設定
			response.addMessage("支出項目情報取得結果が0件です。");
		} else {		
			// 支出項目情報をレスポンス(SisyutuItem)に設定
			// ExpenditureItemは画面出力用の親子関係を意識したクラスなので、間違えないように注意
			// ここでは単純なリストを設定し、画面表示のための親子関係への再設定はプレゼン層で行う
			response.addSisyutuItemResponseList(sisyutuItemSearchResult.getValues().stream().map(domain -> 
			AbstractExpenditureItemInfoManageResponse.SisyutuItemInfo.from(
					domain.getSisyutuItemCode().toString(),
					domain.getSisyutuItemName().toString(),
					domain.getSisyutuItemDetailContext().toString(),
					domain.getParentSisyutuItemCode().toString(),
					domain.getSisyutuItemLevel().toString(),
					domain.getEnableUpdateFlg().getValue())
			).collect(Collectors.toUnmodifiableList()));
		}
		
		return response;
	}

	/**
	 *<pre>
	 * 入力した商品検索条件をもとに商品情報を取得します。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @param inputForm 商品検索条件入力フォームの入力値
	 * @return 情報管理(商品)の商品検索画面情報
	 *
	 */
	public ShoppingItemInfoManageSearchResponse execSearch(UserSession user, ShoppingItemInfoSearchForm inputForm) {
		log.debug("execSearch:userid=" + user.getUserId() + ",inputForm=" + inputForm);
		
		// レスポンスを生成
		ShoppingItemInfoManageSearchResponse response = ShoppingItemInfoManageSearchResponse.getInstance();
		
		
		return response;
	}
	
	/**
	 *<pre>
	 * 指定したユーザIDと商品コードに応じた情報管理(商品)処理選択画面の表示情報を取得します。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @param shoppingItemCode 表示対象商品の商品コード
	 * @return 情報管理(商品)処理選択画面の表示情報
	 *
	 */
	public ShoppingItemInfoManageActSelect readActSelectItemInfo(UserSession user, String shoppingItemCode) {
		log.debug("readActSelectItemInfo:userid=" + user.getUserId() + ",shoppingItemCode=" + shoppingItemCode);
		
		// レスポンスを生成
		ShoppingItemInfoManageActSelect response = ShoppingItemInfoManageActSelect.getInstance(null);
		
		return response;
	}
	
	/**
	 *<pre>
	 * 指定した支出項目の情報を取得し、追加する商品が属する支出項目の情報として情報管理(商品)更新画面情報の表示情報を取得します。
	 *</pre>
	 * @param user ログインユーザID
	 * @param sisyutuItemCode 追加する商品が所属する支出項目の支出項目コード
	 * @return 情報管理(商品)の更新画面情報
	 *
	 */
	public ShoppingItemInfoManageUpdateResponse readAddShoppingItemInfoBySisyutuItem(UserSession user,
			String sisyutuItemCode) {
		log.debug("readAddShoppingItemInfoBySisyutuItem:userid=" + user.getUserId() + ",sisyutuItemCode=" + sisyutuItemCode);
		
		// 基準店舗選択ボックス表示情報を設定したレスポンスを生成
		ShoppingItemInfoManageUpdateResponse response = createShoppingItemInfoManageUpdateResponse(user.getUserId());
		
		// 支出項目名を取得(＞で区切った値)しレスポンスに設定
		response.setSisyutuItemName(sisyutuItemComponent.getSisyutuItemName(user, sisyutuItemCode));
		
		/* 更新商品情報入力フォームを生成しレスポンスに設定 */
		// 更新商品情報入力フォームを生成
		ShoppingItemInfoUpdateForm addItemForm = new ShoppingItemInfoUpdateForm();
		// 新規追加
		addItemForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		// 属する支出項目コード
		addItemForm.setSisyutuItemCode(sisyutuItemCode);
		
		// 更新商品情報入力フォームをレスポンスに設定
		response.setShoppingItemInfoUpdateForm(addItemForm);
		
		return response;
	}
	
	/**
	 *<pre>
	 * 指定した支出項目に属する商品情報(一覧)を取得します。
	 *</pre>
	 * @param user ログインユーザID
	 * @param sisyutuItemCode 検索条件:商品が属する支出項目の支出項目コード
	 * @return 情報管理(商品)の商品検索画面情報
	 *
	 */
	public ShoppingItemInfoManageSearchResponse execSearchBySisyutuItem(UserSession user, String sisyutuItemCode) {
		log.debug("execSearchBySisyutuItem:userid=" + user.getUserId() + ",sisyutuItemCode=" + sisyutuItemCode);
		
		// レスポンスを生成
		ShoppingItemInfoManageSearchResponse response = ShoppingItemInfoManageSearchResponse.getInstance();
		
		
		return response;
	}
	
	/**
	 *<pre>
	 * 指定した商品の情報を取得し、追加する商品情報のコピー情報として情報管理(商品)更新画面情報の表示情報を取得します。
	 *</pre>
	 * @param user ログインユーザID
	 * @param shoppingItemCode コピー元となる商品の商品コード
	 * @return 情報管理(商品)の更新画面情報
	 *
	 */
	public ShoppingItemInfoManageUpdateResponse readAddShoppingItemInfoByShoppingItem(UserSession user,
			String shoppingItemCode) {
		log.debug("readAddShoppingItemInfoByShoppingItem:userid=" + user.getUserId() + ",shoppingItemCode=" + shoppingItemCode);
		
		// レスポンスを生成
		ShoppingItemInfoManageUpdateResponse response = ShoppingItemInfoManageUpdateResponse.getInstance(null);
		return response;
	}
	
	/**
	 *<pre>
	 * 指定したユーザIDと商品コードに応じた情報管理(商品) 更新画面の表示情報取得処理です。
	 * 選択した商品を更新するための情報を取得して画面返却情報に設定します。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @param shoppingItemCode 更新対象の商品コード
	 * @return 情報管理(商品)の更新画面情報(商品更新時)
	 *
	 */
	public ShoppingItemInfoManageUpdateResponse readUpdateShoppingItemInfo(UserSession user, String shoppingItemCode) {
		log.debug("readUpdateShoppingItemInfo:userid=" + user.getUserId() + ",shoppingItemCode=" + shoppingItemCode);
		
		// レスポンスを生成
		ShoppingItemInfoManageUpdateResponse response = ShoppingItemInfoManageUpdateResponse.getInstance(null);
		return response;
	}
	
	/**
	 *<pre>
	 * 商品情報入力フォームの入力値に従い、アクション(登録 or 更新)を実行します
	 *</pre>
	 * @param user ログインユーザID
	 * @param inputForm 商品情報入力フォームの入力値
	 * @return 情報管理(商品)の更新画面情報
	 *
	 */
	@Transactional
	public ShoppingItemInfoManageUpdateResponse execAction(UserSession user, ShoppingItemInfoUpdateForm inputForm) {
		log.debug("execAction:userid=" + user.getUserId() + ",inputForm=" + inputForm);
		
		// レスポンスを生成
		ShoppingItemInfoManageUpdateResponse response = ShoppingItemInfoManageUpdateResponse.getInstance(null);
		
		// 新規登録の場合
		if(inputForm.getAction().equals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD)) {
			
		// 更新の場合
		} else if (inputForm.getAction().equals(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE)) {
			
		} else {
			throw new MyHouseholdAccountBookRuntimeException("未定義のアクションが設定されています。管理者に問い合わせてください。action=" + inputForm.getAction());
		}
		
		// トランザクション完了
		response.setTransactionSuccessFull();
		
		return response;
	}

	/**
	 *<pre>
	 * 情報管理(商品)更新画面で登録実行時にバリデーションチェックNGとなった場合の各画面表示項目を取得します。
	 * バリデーションチェック結果でNGの場合に呼び出してください。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @param inputForm 商品情報入力フォームの入力値
	 * @return 情報管理(商品)の更新画面情報
	 *
	 */
	public ShoppingItemInfoManageUpdateResponse readUpdateBindingErrorSetInfo(UserSession user,
			ShoppingItemInfoUpdateForm inputForm) {
		log.debug("readUpdateBindingErrorSetInfo:userid=" + user.getUserId() + ",inputForm=" + inputForm);
		
		// 基準店舗選択ボックス表示情報を設定したレスポンスを生成
		ShoppingItemInfoManageUpdateResponse response = createShoppingItemInfoManageUpdateResponse(user.getUserId());
		// レスポンスに入力フォームを設定
		response.setShoppingItemInfoUpdateForm(inputForm);
		// 支出項目名を取得(＞で区切った値)しレスポンスに設定
		response.setSisyutuItemName(sisyutuItemComponent.getSisyutuItemName(user, inputForm.getSisyutuItemCode()));
		
		return response;
	}
	
	
	/**
	 *<pre>
	 * 基準店舗選択ボックスの表示情報を取得し、情報管理(商品)更新画面レスポンス情報を生成して返します。
	 *</pre>
	 * @param userId 表示対象のユーザID
	 * @return 情報管理(商品)の更新画面情報
	 *
	 */
	private ShoppingItemInfoManageUpdateResponse createShoppingItemInfoManageUpdateResponse(String userId) {
		
		// レスポンス
		ShoppingItemInfoManageUpdateResponse response = null;
		
		// 基準店舗選択ボックスの対象店舗コード検索条件を設定
		List<String> standardShopsList = Arrays.asList(MyHouseholdAccountBookContent.STANDARD_SHOPSLIST_KUBUN_CODE);
		
		// 基準店舗選択ボックスの表示情報を取得
		ShopInquiryList shopSearchResult = shopRepository.findByIdAndShopKubunCodeList(
				SearchQueryUserIdAndShopKubunCodeList.from(
						// ユーザID
						userId,
						// 店舗区分コードのリスト
						standardShopsList.stream().map(item -> ShopKubunCode.from(item)).collect(Collectors.toUnmodifiableList())));
		if(shopSearchResult.isEmpty()) {
			// 店舗情報が0件の場合、メッセージを設定
			response = ShoppingItemInfoManageUpdateResponse.getInstance(null);
			response.addMessage("店舗情報取得結果が0件です。");
		} else {
			// 店舗情報をレスポンスに設定
			response = ShoppingItemInfoManageUpdateResponse.getInstance(shopSearchResult.getValues().stream().map(domain ->
				OptionItem.from(
						// 店舗コード
						domain.getShopCode().toString(),
						// 店舗名
						domain.getShopName().toString()
				)).collect(Collectors.toUnmodifiableList()));
		}
		
		return response;
	}
}
