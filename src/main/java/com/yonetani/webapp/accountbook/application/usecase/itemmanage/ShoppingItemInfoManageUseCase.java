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
import com.yonetani.webapp.accountbook.domain.model.account.shoppingitem.ShoppingItem;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingitem.ShoppingItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopKubunCodeList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShoppingItemCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemSortBetweenAB;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.SisyutuItemTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.shop.ShopTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.shoppingitem.ShoppingItemTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopKubunCode;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShoppingItemInfoSearchForm;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShoppingItemInfoUpdateForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.AbstractExpenditureItemInfoManageResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.AbstractShoppingItemInfoManageSearchResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShoppingItemInfoManageActSelect;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShoppingItemInfoManageActSelect.SelectShoppingItemInfo;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShoppingItemInfoManageInitResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShoppingItemInfoManageSearchResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShoppingItemInfoManageUpdateResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

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

	// 支出項目テーブル:SISYUTU_ITEM_TABLEリポジトリー
	private final SisyutuItemTableRepository sisyutuItemRepository;
	
	// 支出項目情報取得コンポーネント
	private final SisyutuItemComponent sisyutuItemComponent;
	
	// 店舗テーブル:SHOP_TABLEリポジトリー
	private final ShopTableRepository shopRepository;
	
	// 商品テーブル:SHOPPING_ITEM_TABLEリポジトリー
	private final ShoppingItemTableRepository shoppingItemRepository;
	
	/**
	 *<pre>
	 * 指定したユーザIDに応じた情報管理(商品) 初期表示画面の表示情報を取得します。
	 * ユーザIDをもとに支出項目一覧情報(日用消耗品と食費)を取得し、画面表示データに設定します。
	 *</pre>
	 * @param user 表示対象のユーザID
	 * @return 情報管理(商品) 初期表示画面の表示情報
	 *
	 */
	public ShoppingItemInfoManageInitResponse readInitInfo(LoginUserInfo user) {
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
	public ShoppingItemInfoManageSearchResponse execSearch(LoginUserInfo user, ShoppingItemInfoSearchForm inputForm) {
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
	public ShoppingItemInfoManageActSelect readActSelectItemInfo(LoginUserInfo user, String shoppingItemCode) {
		log.debug("readActSelectItemInfo:userid=" + user.getUserId() + ",shoppingItemCode=" + shoppingItemCode);
		
		// 選択した商品コードに対応する商品情報を取得
		ShoppingItem searchResult = shoppingItemRepository.findByIdAndShoppingItemCode(
				SearchQueryUserIdAndShoppingItemCode.from(user.getUserId(), shoppingItemCode));
		if(searchResult == null) {
			throw new MyHouseholdAccountBookRuntimeException("選択した商品が商品テーブル:SHOPPING_ITEM_TABLEに存在しません。管理者に問い合わせてください。[shoppingItemCode=" + shoppingItemCode + "]");
		}
		// 選択した商品情報をもとにレスポンスを生成
		ShoppingItemInfoManageActSelect response = ShoppingItemInfoManageActSelect.getInstance(
				SelectShoppingItemInfo.from(
						// 商品コード
						searchResult.getShoppingItemCode().toString(),
						// 商品区分名
						searchResult.getShoppingItemKubunName().toString(),
						// 商品名
						searchResult.getShoppingItemName().toString(),
						// 商品詳細
						searchResult.getShoppingItemDetailContext().toString(),
						// 支出項目名(＞で区切った値)
						sisyutuItemComponent.getSisyutuItemName(user, searchResult.getSisyutuItemCode().toString()),
						/// 会社名
						searchResult.getCompanyName().toString()));
		
		// 指定した検索条件に一致する商品一覧を取得
		// TODO:セッションのデータをコントローラーから渡してもらう必要あり
		
		// 商品検索結果情報の明細リストを設定
		
		// 商品検索結果名を設定
		response.setSearchResultNameValue("対応中：");
		
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
	public ShoppingItemInfoManageUpdateResponse readAddShoppingItemInfoBySisyutuItem(LoginUserInfo user,
			String sisyutuItemCode) {
		log.debug("readAddShoppingItemInfoBySisyutuItem:userid=" + user.getUserId() + ",sisyutuItemCode=" + sisyutuItemCode);
		
		// 基準店舗選択ボックス表示情報を設定したレスポンスを生成
		ShoppingItemInfoManageUpdateResponse response = createShoppingItemInfoManageUpdateResponse(user.getUserId());
		
		// 支出項目名を取得(＞で区切った値)しレスポンスに設定
		response.setSisyutuItemName(sisyutuItemComponent.getSisyutuItemName(user, sisyutuItemCode));
		
		/* 更新商品情報入力フォームを生成しレスポンスに設定 */
		// 更新商品情報入力フォームを生成
		ShoppingItemInfoUpdateForm addItemForm = new ShoppingItemInfoUpdateForm();
		// アクション：新規登録
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
	public ShoppingItemInfoManageSearchResponse execSearchBySisyutuItem(LoginUserInfo user, String sisyutuItemCode) {
		log.debug("execSearchBySisyutuItem:userid=" + user.getUserId() + ",sisyutuItemCode=" + sisyutuItemCode);
		
		// レスポンスを生成
		ShoppingItemInfoManageSearchResponse response = ShoppingItemInfoManageSearchResponse.getInstance();
		
		// 選択した支出項目名を取得(＞で区切った値)
		String sisyutuItemName = sisyutuItemComponent.getSisyutuItemName(user, sisyutuItemCode);
		
		// 指定した支出項目コードに属する商品一覧を取得
		ShoppingItemInquiryList searchResult = shoppingItemRepository.findByIdAndSisyutuItemCode(
				SearchQueryUserIdAndSisyutuItemCode.from(user.getUserId(), sisyutuItemCode));
		if(searchResult.isEmpty()) {
			response.addMessage("指定した支出項目「" + sisyutuItemName + "」に登録されている商品は0件です。");
		} else {
			// 商品検索結果をレスポンスに設定
			response.addShoppingItemList(createShoppingItemList(searchResult));
			// 商品検索結果名を設定
			response.setSearchResultNameValue("支出項目：" + sisyutuItemName);
			
		}
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
	public ShoppingItemInfoManageUpdateResponse readAddShoppingItemInfoByShoppingItem(LoginUserInfo user,
			String shoppingItemCode) {
		log.debug("readAddShoppingItemInfoByShoppingItem:userid=" + user.getUserId() + ",shoppingItemCode=" + shoppingItemCode);
		
		// 基準店舗選択ボックス表示情報を設定したレスポンスを生成
		ShoppingItemInfoManageUpdateResponse response = createShoppingItemInfoManageUpdateResponse(user.getUserId());
		
		// 選択した商品コードに対応する商品情報を取得
		ShoppingItem searchResult = shoppingItemRepository.findByIdAndShoppingItemCode(
				SearchQueryUserIdAndShoppingItemCode.from(user.getUserId(), shoppingItemCode));
		if(searchResult == null) {
			throw new MyHouseholdAccountBookRuntimeException("選択した商品が商品テーブル:SHOPPING_ITEM_TABLEに存在しません。管理者に問い合わせてください。[shoppingItemCode=" + shoppingItemCode + "]");
		}
		
		// 支出項目名を取得(＞で区切った値)しレスポンスに設定
		response.setSisyutuItemName(sisyutuItemComponent.getSisyutuItemName(user, searchResult.getSisyutuItemCode().toString()));
		
		/* 更新商品情報入力フォームを生成し取得した商品情報をレスポンスに設定 */
		// 更新商品情報入力フォームを生成
		ShoppingItemInfoUpdateForm addItemForm = new ShoppingItemInfoUpdateForm();
		// アクション：新規登録
		addItemForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		// 属する支出項目コード
		addItemForm.setSisyutuItemCode(searchResult.getSisyutuItemCode().toString());
		// 商品区分名
		addItemForm.setShoppingItemKubunName(searchResult.getShoppingItemKubunName().toString());
		// 商品名
		addItemForm.setShoppingItemName("★新規★" + searchResult.getShoppingItemName().toString());
		// 商品詳細
		addItemForm.setShoppingItemDetailContext(searchResult.getShoppingItemDetailContext().toString());
		// 会社名
		addItemForm.setCompanyName(searchResult.getCompanyName().toString());
		
		// 更新商品情報入力フォームをレスポンスに設定
		response.setShoppingItemInfoUpdateForm(addItemForm);
		
		// コピーした情報を新規登録する旨をメッセージ表示
		response.addMessage("「コピーして商品を新規追加」が選択されています。");
		
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
	public ShoppingItemInfoManageUpdateResponse readUpdateShoppingItemInfo(LoginUserInfo user, String shoppingItemCode) {
		log.debug("readUpdateShoppingItemInfo:userid=" + user.getUserId() + ",shoppingItemCode=" + shoppingItemCode);
		
		// 基準店舗選択ボックス表示情報を設定したレスポンスを生成
		ShoppingItemInfoManageUpdateResponse response = createShoppingItemInfoManageUpdateResponse(user.getUserId());
		
		// 選択した商品コードに対応する商品情報を取得
		ShoppingItem searchResult = shoppingItemRepository.findByIdAndShoppingItemCode(
				SearchQueryUserIdAndShoppingItemCode.from(user.getUserId(), shoppingItemCode));
		if(searchResult == null) {
			throw new MyHouseholdAccountBookRuntimeException("選択した商品が商品テーブル:SHOPPING_ITEM_TABLEに存在しません。管理者に問い合わせてください。[shoppingItemCode=" + shoppingItemCode + "]");
		}
		
		// 支出項目名を取得(＞で区切った値)しレスポンスに設定
		response.setSisyutuItemName(sisyutuItemComponent.getSisyutuItemName(user, searchResult.getSisyutuItemCode().toString()));
		
		/* 更新商品情報入力フォームを生成し取得した商品情報をレスポンスに設定 */
		// 更新商品情報入力フォームを生成
		ShoppingItemInfoUpdateForm addItemForm = new ShoppingItemInfoUpdateForm();
		// アクション：更新
		addItemForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		// 商品コード
		addItemForm.setShoppingItemCode(searchResult.getShoppingItemCode().toString());
		// 属する支出項目コード
		addItemForm.setSisyutuItemCode(searchResult.getSisyutuItemCode().toString());
		// 商品区分名
		addItemForm.setShoppingItemKubunName(searchResult.getShoppingItemKubunName().toString());
		// 商品名
		addItemForm.setShoppingItemName(searchResult.getShoppingItemName().toString());
		// 商品詳細
		addItemForm.setShoppingItemDetailContext(searchResult.getShoppingItemDetailContext().toString());
		// 会社名
		addItemForm.setCompanyName(searchResult.getCompanyName().toString());
		// 基準店舗コード
		addItemForm.setStandardShopCode(searchResult.getShopCode().toString());
		// 基準価格
		addItemForm.setStandardPrice(DomainCommonUtils.convertInteger(searchResult.getStandardPrice().getValue()));
		
		// 更新商品情報入力フォームをレスポンスに設定
		response.setShoppingItemInfoUpdateForm(addItemForm);
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
	public ShoppingItemInfoManageUpdateResponse execAction(LoginUserInfo user, ShoppingItemInfoUpdateForm inputForm) {
		log.debug("execAction:userid=" + user.getUserId() + ",inputForm=" + inputForm);
		
		// レスポンスを生成
		ShoppingItemInfoManageUpdateResponse response = ShoppingItemInfoManageUpdateResponse.getInstance(null);
		
		// 新規登録の場合
		if(inputForm.getAction().equals(MyHouseholdAccountBookContent.ACTION_TYPE_ADD)) {
			
			// 新規採番する商品コードの値を取得
			int count = shoppingItemRepository.countById(SearchQueryUserId.from(user.getUserId()));
			count++;
			if(count > 99999) {
				response.addErrorMessage("商品情報は99999件以上登録できません。管理者に問い合わせてください。");
				return response;
			}
			
			// 商品コードを入力フォームに設定
			inputForm.setShoppingItemCode(String.format("%05d", count));
			
			// 商品情報を作成
			ShoppingItem addData = createSisyutuItem(user.getUserId().toString(), inputForm);
			
			// 商品テーブルに登録
			int addCount = shoppingItemRepository.add(addData);
			// 追加件数が1件以上の場合、業務エラー
			if(addCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("商品テーブル:SHOPPING_ITEM_TABLEへの追加件数が不正でした。[件数=" + addCount + "][add data:" + addData + "]");
			}
			
			// 完了メッセージ
			response.addMessage("新規商品を追加しました。[code:" + addData.getShoppingItemCode() + "]" + addData.getShoppingItemName());
			
		// 更新の場合
		} else if (inputForm.getAction().equals(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE)) {
			// 商品情報を作成
			ShoppingItem updateData = createSisyutuItem(user.getUserId().toString(), inputForm);
			
			// 商品テーブルに登録(支出項目コードは更新対象外項目なので注意)
			int updateCount = shoppingItemRepository.update(updateData);
			// 更新件数が1件以上の場合、業務エラー
			if(updateCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("商品テーブル:SHOPPING_ITEM_TABLEへの更新件数が不正でした。[件数=" + updateCount + "][update data:" + updateData + "]");
			}
			
			// 完了メッセージ
			response.addMessage("商品を更新しました。[code:" + updateData.getShoppingItemCode() + "]" + updateData.getShoppingItemName());
			
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
	public ShoppingItemInfoManageUpdateResponse readUpdateBindingErrorSetInfo(LoginUserInfo user,
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
	
	/**
	 *<pre>
	 * 引数のフォームデータから商品情報(ドメイン)を生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param inputForm フォームデータ
	 * @return 商品情報(ドメイン)
	 *
	 */
	private ShoppingItem createSisyutuItem(String userId, ShoppingItemInfoUpdateForm inputForm) {
		return ShoppingItem.from(
				// ユーザID
				userId,
				// 商品コード
				inputForm.getShoppingItemCode(),
				// 商品区分名
				inputForm.getShoppingItemKubunName(),
				// 商品名
				inputForm.getShoppingItemName(),
				// 商品詳細
				inputForm.getShoppingItemDetailContext(),
				// 支出項目コード
				inputForm.getSisyutuItemCode(),
				// 会社名
				inputForm.getCompanyName(),
				// 基準店舗コード
				inputForm.getStandardShopCode(),
				// 基準価格
				DomainCommonUtils.convertBigDecimal(inputForm.getStandardPrice(), 0));
	}
	
	/**
	 *<pre>
	 * 引数の商品検索結果情報(ドメイン)から商品一覧明細情報を生成して返します。
	 * 
	 *</pre>
	 * @param searchResult 商品検索結果情報(ドメイン)
	 * @return 商品検索結果画面の商品一覧明細情報
	 *
	 */
	private List<AbstractShoppingItemInfoManageSearchResponse.ShoppingItemListItem> createShoppingItemList(ShoppingItemInquiryList searchResult) {
		return searchResult.getValues().stream().map(domain -> 
			AbstractShoppingItemInfoManageSearchResponse.ShoppingItemListItem.from(
				// 商品コード
				domain.getShoppingItemCode().toString(),
				// 商品区分名
				domain.getShoppingItemKubunName().toString(),
				// 商品名
				domain.getShoppingItemName().toString(),
				// 商品詳細
				domain.getShoppingItemDetailContext().toString(),
				// 支出項目名
				domain.getSisyutuItemName().toString(),
				// 会社名
				domain.getCompanyName().toString(),
				// 基準店舗名
				domain.getStandardShopName().toString(),
				// 基準価格
				domain.getStandardPrice().toString())
		).collect(Collectors.toUnmodifiableList());
	}
}
