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
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonetani.webapp.accountbook.common.component.CodeTableItemComponent;
import com.yonetani.webapp.accountbook.common.component.SisyutuItemComponent;
import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.model.account.shop.ShopInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingitem.ShoppingItem;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingitem.ShoppingItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.common.CodeAndValuePair;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryShoppingItemInfoSearchCondition;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopKubunCodeList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShoppingItemCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShoppingItemJanCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.repository.account.shop.ShopTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.shoppingitem.ShoppingItemTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemSort;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopKubunCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemCompanyName;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemJanCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemKubunName;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemName;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShoppingItemInfoSearchForm;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShoppingItemInfoUpdateForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.AbstractShoppingItemInfoManageSearchResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShoppingItemInfoManageActSelectResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShoppingItemInfoManageActSelectResponse.SelectShoppingItemInfo;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShoppingItemInfoManageInitResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShoppingItemInfoManageSearchResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShoppingItemInfoManageUpdateResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;
import com.yonetani.webapp.accountbook.presentation.session.ShoppingItemSearchInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
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
	
	// 支出項目情報取得コンポーネント
	private final SisyutuItemComponent sisyutuItemComponent;
	
	// 店舗テーブル:SHOP_TABLEリポジトリー
	private final ShopTableRepository shopRepository;
	
	// 商品テーブル:SHOPPING_ITEM_TABLEリポジトリー
	private final ShoppingItemTableRepository shoppingItemRepository;
	
	// コードテーブル
	private final CodeTableItemComponent codeTableItem;
	
	/**
	 *<pre>
	 * 指定したユーザIDに応じた情報管理(商品) 初期表示画面の表示情報を取得します。
	 * ユーザIDをもとに支出項目一覧情報(日用消耗品と食費)を取得し、画面表示データに設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @return 情報管理(商品) 初期表示画面の表示情報
	 *
	 */
	public ShoppingItemInfoManageInitResponse readInitInfo(LoginUserInfo user) {
		log.debug("readInitInfo:userid=" + user.getUserId());
		// レスポンス
		ShoppingItemInfoManageInitResponse response = ShoppingItemInfoManageInitResponse.getInstance();
		
		// 支出項目：飲食日用品から、日用消耗品と飲食の項目をすべて取得(表示順から取得する)
		sisyutuItemComponent.setSisyutuItemList(
				// ログインユーザ情報
				UserId.from(user.getUserId()),
				// 検索条件:支出項目表示順A：日用消耗品の表示順の値:0501000000 (変更不可の値なので固定値)
				SisyutuItemSort.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_NITIYOU_SYOUMOUHIN_SORT_VALUE),
				// 検索条件:支出項目表示順A：食費の表示順最大値:0502999999
				SisyutuItemSort.from(MyHouseholdAccountBookContent.SISYUTU_ITEM_INSYOKU_SORT_MAX_VALUE),
				// 画面表示情報
				response);
		
		return response;
	}

	/**
	 *<pre>
	 * 入力した商品検索条件をもとに商品情報を取得します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param inputForm 商品検索条件入力フォームの入力値
	 * @return 情報管理(商品)の商品検索画面情報
	 *
	 */
	public ShoppingItemInfoManageSearchResponse execSearch(LoginUserInfo user, ShoppingItemInfoSearchForm inputForm) {
		log.debug("execSearch:userid=" + user.getUserId() + ",inputForm=" + inputForm);
		
		// レスポンスを生成
		ShoppingItemInfoManageSearchResponse response = ShoppingItemInfoManageSearchResponse.getInstance();
		// 検索条件入力フォームの入力値をレスポンスに設定
		response.setShoppingItemInfoSearchForm(inputForm);
		// 検索を実行
		execActSearchShoppingItem(
				// ユーザID
				UserId.from(user.getUserId()),
				// 検索対象(検索条件入力フォーム設定値)
				inputForm.getSearchTargetKubun(),
				// 検索条件(検索条件入力フォーム設定値)
				inputForm.getSearchValue(),
				// 情報管理(商品)画面の商品検索結果画面情報
				response);
		
		return response;
	}
	
	/**
	 *<pre>
	 * 指定したユーザIDと商品コードに応じた情報管理(商品)処理選択画面の表示情報を取得します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param shoppingItemSearchInfo 検索条件情報
	 * @param shoppingItemCodeStr 表示対象商品の商品コード
	 * @return 情報管理(商品)処理選択画面の表示情報
	 *
	 */
	public ShoppingItemInfoManageActSelectResponse readActSelectItemInfo(LoginUserInfo user, 
			ShoppingItemSearchInfo shoppingItemSearchInfo, String shoppingItemCodeStr) {
		log.debug("readActSelectItemInfo:userid=" + user.getUserId() + ",shoppingItemSearchInfo=["
			+ shoppingItemSearchInfo + "],shoppingItemCode=" + shoppingItemCodeStr);
		
		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		// ドメインタイプ:商品コード
		ShoppingItemCode shoppingItemCode = ShoppingItemCode.from(shoppingItemCodeStr);
		
		// セッションに設定されている商品検索条件のnull判定
		if(shoppingItemSearchInfo == null) {
			throw new MyHouseholdAccountBookRuntimeException("セッションに設定されている商品検索条件がnullです。管理者に問い合わせてください。");
		}
		// 選択した商品コードに対応する商品情報を取得
		ShoppingItem searchResult = shoppingItemRepository.findByIdAndShoppingItemCode(
				SearchQueryUserIdAndShoppingItemCode.from(userId, shoppingItemCode));
		if(searchResult == null) {
			throw new MyHouseholdAccountBookRuntimeException("選択した商品が商品テーブル:SHOPPING_ITEM_TABLEに存在しません。管理者に問い合わせてください。[shoppingItemCode=" + shoppingItemCode + "]");
		}
		// 選択した商品情報をもとにレスポンスを生成
		ShoppingItemInfoManageActSelectResponse response = ShoppingItemInfoManageActSelectResponse.getInstance(
				SelectShoppingItemInfo.from(
						// 商品コード
						searchResult.getShoppingItemCode().getValue(),
						// 商品区分名
						searchResult.getShoppingItemKubunName().getValue(),
						// 商品名
						searchResult.getShoppingItemName().getValue(),
						// 商品詳細
						searchResult.getShoppingItemDetailContext().getValue(),
						// 商品JANコード
						searchResult.getShoppingItemJanCode().getValue(),
						// 支出項目名(＞で区切った値)
						sisyutuItemComponent.getSisyutuItemName(userId, searchResult.getSisyutuItemCode()),
						// 会社名
						searchResult.getCompanyName().getValue()));
		
		/* 指定した検索条件に一致する商品一覧を取得 */
		// 検索条件が支出項目コードで商品を検索の場合
		if(Objects.equals(shoppingItemSearchInfo.getSearchActType(), MyHouseholdAccountBookContent.ACT_SEARCH_SISYUTU_ITEM)) {
			
			// 検索を実行
			execActSearchSisyutuItem(
					// ログインユーザ情報
					userId,
					// 支出項目コード
					SisyutuItemCode.from(shoppingItemSearchInfo.getSisyutuItemCode()),
					// 情報管理(商品)画面の商品検索結果画面情報
					response);
			
		// 検索条件が商品検索条件で商品を検索の場合
		} else if (Objects.equals(shoppingItemSearchInfo.getSearchActType(), MyHouseholdAccountBookContent.ACT_SEARCH_SHOPPING_ITEM)) {
			/* 検索条件入力フォームを設定 */
			// 検索条件入力フォームを生成
			ShoppingItemInfoSearchForm searchForm = new ShoppingItemInfoSearchForm();
			// 検索対象
			searchForm.setSearchTargetKubun(shoppingItemSearchInfo.getSearchTargetKubun());
			// 検索条件
			searchForm.setSearchValue(shoppingItemSearchInfo.getSearchValue());
			// 検索条件入力フォームをレスポンスに設定
			response.setShoppingItemInfoSearchForm(searchForm);
			
			/* 検索を実行 */
			execActSearchShoppingItem(
					// ユーザID
					userId,
					// 検索対象(検索条件入力フォーム設定値)
					shoppingItemSearchInfo.getSearchTargetKubun(),
					// 検索条件(検索条件入力フォーム設定値)
					shoppingItemSearchInfo.getSearchValue(),
					// 情報管理(商品)画面の商品検索結果画面情報
					response);
		} else {
			throw new MyHouseholdAccountBookRuntimeException("セッションに設定されている商品検索条件が不正です。管理者に問い合わせてください。[searchActType="
					+ shoppingItemSearchInfo.getSearchActType() + "]");
		}
		
		return response;
	}
	
	/**
	 *<pre>
	 * 指定した支出項目の情報を取得し、追加する商品が属する支出項目の情報として情報管理(商品)更新画面情報の表示情報を取得します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param sisyutuItemCodeStr 追加する商品が所属する支出項目の支出項目コード
	 * @return 情報管理(商品)の更新画面情報
	 *
	 */
	public ShoppingItemInfoManageUpdateResponse readAddShoppingItemInfoBySisyutuItem(LoginUserInfo user,
			String sisyutuItemCodeStr) {
		log.debug("readAddShoppingItemInfoBySisyutuItem:userid=" + user.getUserId() + ",sisyutuItemCode=" + sisyutuItemCodeStr);
		
		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		// ドメインタイプ:支出項目コード
		SisyutuItemCode sisyutuItemCode = SisyutuItemCode.from(sisyutuItemCodeStr);
		
		/* 更新商品情報入力フォームを生成しレスポンスに設定 */
		// 更新商品情報入力フォームを生成
		ShoppingItemInfoUpdateForm addItemForm = new ShoppingItemInfoUpdateForm();
		// アクション：新規登録
		addItemForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		// 属する支出項目コード
		addItemForm.setSisyutuItemCode(sisyutuItemCode.getValue());
		
		// 基準店舗選択ボックス表示情報を設定したレスポンスを生成
		ShoppingItemInfoManageUpdateResponse response = createShoppingItemInfoManageUpdateResponse(userId, addItemForm);
		// 支出項目名を取得(＞で区切った値)しレスポンスに設定
		response.setSisyutuItemName(sisyutuItemComponent.getSisyutuItemName(userId, sisyutuItemCode));
		
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
		// 検索を実行
		execActSearchSisyutuItem(UserId.from(user.getUserId()), SisyutuItemCode.from(sisyutuItemCode), response);
		
		return response;
	}
	
	/**
	 *<pre>
	 * 指定した商品の情報を取得し、追加する商品情報のコピー情報として情報管理(商品)更新画面情報の表示情報を取得します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param shoppingItemCodeStr コピー元となる商品の商品コード
	 * @return 情報管理(商品)の更新画面情報
	 *
	 */
	public ShoppingItemInfoManageUpdateResponse readAddShoppingItemInfoByShoppingItem(LoginUserInfo user,
			String shoppingItemCodeStr) {
		log.debug("readAddShoppingItemInfoByShoppingItem:userid=" + user.getUserId() + ",shoppingItemCode=" + shoppingItemCodeStr);
		
		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		// ドメインタイプ:商品コード
		ShoppingItemCode shoppingItemCode = ShoppingItemCode.from(shoppingItemCodeStr);
		
		// 選択した商品コードに対応する商品情報を取得
		ShoppingItem searchResult = shoppingItemRepository.findByIdAndShoppingItemCode(
				SearchQueryUserIdAndShoppingItemCode.from(userId, shoppingItemCode));
		if(searchResult == null) {
			throw new MyHouseholdAccountBookRuntimeException("選択した商品が商品テーブル:SHOPPING_ITEM_TABLEに存在しません。管理者に問い合わせてください。[shoppingItemCode=" + shoppingItemCode + "]");
		}
		
		/* 更新商品情報入力フォームを生成し取得した商品情報をレスポンスに設定 */
		// 更新商品情報入力フォームを生成
		ShoppingItemInfoUpdateForm addItemForm = new ShoppingItemInfoUpdateForm();
		// アクション：新規登録
		addItemForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		// 属する支出項目コード
		addItemForm.setSisyutuItemCode(searchResult.getSisyutuItemCode().getValue());
		// 商品区分名
		addItemForm.setShoppingItemKubunName(searchResult.getShoppingItemKubunName().getValue());
		// 商品名
		addItemForm.setShoppingItemName("★新規★" + searchResult.getShoppingItemName().getValue());
		// 商品詳細
		addItemForm.setShoppingItemDetailContext(searchResult.getShoppingItemDetailContext().getValue());
		// 会社名
		addItemForm.setCompanyName(searchResult.getCompanyName().getValue());
		
		// 基準店舗選択ボックス表示情報を設定したレスポンスを生成
		ShoppingItemInfoManageUpdateResponse response = createShoppingItemInfoManageUpdateResponse(userId, addItemForm);
		// 支出項目名を取得(＞で区切った値)しレスポンスに設定
		response.setSisyutuItemName(sisyutuItemComponent.getSisyutuItemName(userId, searchResult.getSisyutuItemCode()));
		
		// コピーした情報を新規登録する旨をメッセージ表示
		response.addMessage("「コピーして商品を新規追加」が選択されています。");
		
		return response;
	}
	
	/**
	 *<pre>
	 * 指定したユーザIDと商品コードに応じた情報管理(商品) 更新画面の表示情報取得処理です。
	 * 選択した商品を更新するための情報を取得して画面返却情報に設定します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param shoppingItemCodeStr 更新対象の商品コード
	 * @return 情報管理(商品)の更新画面情報(商品更新時)
	 *
	 */
	public ShoppingItemInfoManageUpdateResponse readUpdateShoppingItemInfo(LoginUserInfo user, String shoppingItemCodeStr) {
		log.debug("readUpdateShoppingItemInfo:userid=" + user.getUserId() + ",shoppingItemCode=" + shoppingItemCodeStr);
		
		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		// ドメインタイプ:商品コード
		ShoppingItemCode shoppingItemCode = ShoppingItemCode.from(shoppingItemCodeStr);
		
		// 選択した商品コードに対応する商品情報を取得
		ShoppingItem searchResult = shoppingItemRepository.findByIdAndShoppingItemCode(
				SearchQueryUserIdAndShoppingItemCode.from(userId, shoppingItemCode));
		if(searchResult == null) {
			throw new MyHouseholdAccountBookRuntimeException("選択した商品が商品テーブル:SHOPPING_ITEM_TABLEに存在しません。管理者に問い合わせてください。[shoppingItemCode=" + shoppingItemCode + "]");
		}
		
		/* 更新商品情報入力フォームを生成し取得した商品情報をレスポンスに設定 */
		// 更新商品情報入力フォームを生成
		ShoppingItemInfoUpdateForm updateItemForm = new ShoppingItemInfoUpdateForm();
		// アクション：更新
		updateItemForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE);
		// 商品コード
		updateItemForm.setShoppingItemCode(searchResult.getShoppingItemCode().getValue());
		// 属する支出項目コード
		updateItemForm.setSisyutuItemCode(searchResult.getSisyutuItemCode().getValue());
		// 商品区分名
		updateItemForm.setShoppingItemKubunName(searchResult.getShoppingItemKubunName().getValue());
		// 商品名
		updateItemForm.setShoppingItemName(searchResult.getShoppingItemName().getValue());
		// 商品詳細
		updateItemForm.setShoppingItemDetailContext(searchResult.getShoppingItemDetailContext().getValue());
		// 商品JANコード
		updateItemForm.setShoppingItemJanCode(searchResult.getShoppingItemJanCode().getValue());
		// 会社名
		updateItemForm.setCompanyName(searchResult.getCompanyName().getValue());
		// 基準店舗コード
		updateItemForm.setStandardShopCode(searchResult.getShopCode().getValue());
		// 基準価格
		updateItemForm.setStandardPrice(DomainCommonUtils.convertInteger(searchResult.getStandardPrice().getValue()));
		// 商品内容量
		updateItemForm.setShoppingItemCapacity(searchResult.getShoppingItemCapacity().getValue());
		// 商品内容量単位
		updateItemForm.setShoppingItemCapacityUnit(searchResult.getShoppingItemCapacityUnit().getValue());
		// カロリー
		updateItemForm.setShoppingItemCalories(searchResult.getShoppingItemCalories().getValue());
		
		// 基準店舗選択ボックス表示情報を設定したレスポンスを生成
		ShoppingItemInfoManageUpdateResponse response = createShoppingItemInfoManageUpdateResponse(userId, updateItemForm);
		// 支出項目名を取得(＞で区切った値)しレスポンスに設定
		response.setSisyutuItemName(sisyutuItemComponent.getSisyutuItemName(userId, searchResult.getSisyutuItemCode()));
		
		return response;
	}
	
	/**
	 *<pre>
	 * 商品情報入力フォームの入力値に従い、アクション(登録 or 更新)を実行します
	 *</pre>
	 * @param user ログインユーザ情報
	 * @param inputForm 商品情報入力フォームの入力値
	 * @return 情報管理(商品)の更新画面情報
	 *
	 */
	@Transactional
	public ShoppingItemInfoManageUpdateResponse execAction(LoginUserInfo user, ShoppingItemInfoUpdateForm inputForm) {
		log.debug("execAction:userid=" + user.getUserId() + ",inputForm=" + inputForm);
		
		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		
		// レスポンスを生成
		ShoppingItemInfoManageUpdateResponse response = createShoppingItemInfoManageUpdateResponse(userId, inputForm);
		
		// 新規登録の場合
		if(Objects.equals(inputForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_ADD)) {
			
			// 商品JANコードが既に登録済みでないかを確認
			if(!checkShoppingItemJanCode(userId, ShoppingItemJanCode.from(inputForm.getShoppingItemJanCode()), response)) {
				// 登録済みの場合、処理終了(エラーメッセージを更新画面に表示
				return response;
			}
			
			// 新規採番する商品コードの値を取得
			int count = shoppingItemRepository.countById(SearchQueryUserId.from(userId));
			count++;
			if(count > 99999) {
				response.addErrorMessage("商品情報は99999件以上登録できません。管理者に問い合わせてください。");
				return response;
			}
			
			// 商品コードを入力フォームに設定
			inputForm.setShoppingItemCode(ShoppingItemCode.getNewCode(count));
			
			// 商品情報を作成
			ShoppingItem addData = createShoppingItem(user.getUserId(), inputForm);
			
			// 商品テーブルに登録
			int addCount = shoppingItemRepository.add(addData);
			// 追加件数が1件以上の場合、業務エラー
			if(addCount != 1) {
				throw new MyHouseholdAccountBookRuntimeException("商品テーブル:SHOPPING_ITEM_TABLEへの追加件数が不正でした。[件数=" + addCount + "][add data:" + addData + "]");
			}
			
			// 完了メッセージ
			response.addMessage("新規商品を追加しました。[code:" + addData.getShoppingItemCode() + "]" + addData.getShoppingItemName());
			
		// 更新の場合
		} else if (Objects.equals(inputForm.getAction(), MyHouseholdAccountBookContent.ACTION_TYPE_UPDATE)) {
			
			/* 商品JANコードが変更されている場合、既に登録済みでないかを確認 */
			// 変更対象の商品情報を取得
			ShoppingItem searchResult = shoppingItemRepository.findByIdAndShoppingItemCode(
					SearchQueryUserIdAndShoppingItemCode.from(userId, ShoppingItemCode.from(inputForm.getShoppingItemCode())));
			if(searchResult == null) {
				throw new MyHouseholdAccountBookRuntimeException("選択した商品が商品テーブル:SHOPPING_ITEM_TABLEに存在しません。管理者に問い合わせてください。[shoppingItemCode=" + inputForm.getShoppingItemCode() + "]");
			}
			// 商品JANコードが変更されている場合
			if(!searchResult.getShoppingItemJanCode().equals(ShoppingItemJanCode.from(inputForm.getShoppingItemJanCode()))) {
				// 商品JANコードが既に登録済みでないかを確認
				if(!checkShoppingItemJanCode(userId, ShoppingItemJanCode.from(inputForm.getShoppingItemJanCode()), response)) {
					// 登録済みの場合、処理終了(エラーメッセージを更新画面に表示
					return response;
				}
			}
			
			/* 商品更新 */
			// 商品情報を作成
			ShoppingItem updateData = createShoppingItem(user.getUserId(), inputForm);
			
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
	 * 情報管理(商品)更新画面で登録実行時のバリデーションチェックNGとなった場合の各画面表示項目を取得します。
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
		
		// ドメインタイプ:ユーザID
		UserId userId = UserId.from(user.getUserId());
		// ドメインタイプ:支出項目コード
		SisyutuItemCode sisyutuItemCode = SisyutuItemCode.from(inputForm.getSisyutuItemCode());
		
		// 基準店舗選択ボックス表示情報を設定したレスポンスを生成
		ShoppingItemInfoManageUpdateResponse response = createShoppingItemInfoManageUpdateResponse(userId, inputForm);
		// 支出項目名を取得(＞で区切った値)しレスポンスに設定
		response.setSisyutuItemName(sisyutuItemComponent.getSisyutuItemName(userId, sisyutuItemCode));
		
		return response;
	}
	
	/**
	 *<pre>
	 * 基準店舗選択ボックス・内容量単位選択ボックスの表示情報を取得し、情報管理(商品)更新画面レスポンス情報を生成して返します。
	 *</pre>
	 * @param userId 表示対象のユーザID
	 * @param inputForm 商品情報入力フォームの入力値
	 * @return 情報管理(商品)の更新画面情報
	 *
	 */
	private ShoppingItemInfoManageUpdateResponse createShoppingItemInfoManageUpdateResponse(UserId userId, ShoppingItemInfoUpdateForm inputForm) {
		
		// コードテーブル情報から内容量単位選択ボックスの表示情報を取得し、リストに設定
		List<CodeAndValuePair> codeValuePairList = codeTableItem.getCodeValues(MyHouseholdAccountBookContent.SHOPPING_ITEM_CAPACITY_UNIT);
		// 内容量単位選択ボックス表示情報が未設定の場合、エラー
		if(codeValuePairList == null) {
			throw new MyHouseholdAccountBookRuntimeException("コード定義ファイルに「商品内容量単位情報："
					+ MyHouseholdAccountBookContent.SHOPPING_ITEM_CAPACITY_UNIT + "」が登録されていません。管理者に問い合わせてください");
		}
		// 内容量単位選択ボックスの表示情報リストはデフォルト値が追加されるので、不変ではなく可変でリストを生成して設定
		List<OptionItem> capacityUnitList = codeValuePairList.stream().map(pair ->
			OptionItem.from(pair.getCode().getValue(), pair.getCodeValue().getValue())).collect(Collectors.toList());
		
		// レスポンス
		ShoppingItemInfoManageUpdateResponse response = null;
		
		// 基準店舗選択ボックスの対象店舗コード検索条件を設定
		List<String> standardShopsList = Arrays.asList(MyHouseholdAccountBookContent.STANDARD_SHOPSLIST_KUBUN_CODE);
		
		// 基準店舗選択ボックスの表示情報を取得
		ShopInquiryList shopSearchResult = shopRepository.findById(SearchQueryUserIdAndShopKubunCodeList.from(
						// ユーザID
						userId,
						// 店舗区分コードのリスト
						standardShopsList.stream().map(item -> ShopKubunCode.from(item)).collect(Collectors.toUnmodifiableList())));
		if(shopSearchResult.isEmpty()) {
			// 店舗情報が0件の場合、メッセージを設定
			response = ShoppingItemInfoManageUpdateResponse.getInstance(inputForm, null, capacityUnitList);
			response.addMessage("店舗情報取得結果が0件です。");
		} else {
			// 店舗情報をレスポンスに設定
			response = ShoppingItemInfoManageUpdateResponse.getInstance(
					// 商品情報入力フォームの入力値
					inputForm,
					// 基準店舗選択ボックスの表示情報リスト
					shopSearchResult.getValues().stream().map(domain ->
						OptionItem.from(
								// 店舗コード
								domain.getShopCode().getValue(),
								// 店舗名
								domain.getShopName().getValue()
						)).collect(Collectors.toUnmodifiableList()),
					// 内容量単位選択ボックスの表示情報リスト
					capacityUnitList
					);
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
	private ShoppingItem createShoppingItem(String userId, ShoppingItemInfoUpdateForm inputForm) {
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
				// 商品JANコード
				inputForm.getShoppingItemJanCode(),
				// 支出項目コード
				inputForm.getSisyutuItemCode(),
				// 会社名
				inputForm.getCompanyName(),
				// 基準店舗コード
				inputForm.getStandardShopCode(),
				// 基準価格
				DomainCommonUtils.convertKingakuBigDecimal(inputForm.getStandardPrice()),
				// 内容量
				inputForm.getShoppingItemCapacity(),
				// 内容量単位
				inputForm.getShoppingItemCapacityUnit(),
				// カロリー
				inputForm.getShoppingItemCalories()
				);
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
		
		return searchResult.getValues().stream().map(domain -> {
			// 内容量とカロリーが設定されている場合、商品名の値に連結(内容量／カロリー)
			StringBuilder nameTextBuff = new StringBuilder();
			// 商品内容量の値が設定されている場合、値を設定
			if(domain.getShoppingItemCapacity().getValue() != null) {
				// 開始文字列
				nameTextBuff.append("（");
				// 商品内容量
				nameTextBuff.append(domain.getShoppingItemCapacity().toString());
				// 商品内容量単位
				nameTextBuff.append(codeTableItem.getCodeValue(
						// コード区分：商品内容量単位
						MyHouseholdAccountBookContent.SHOPPING_ITEM_CAPACITY_UNIT,
						// 商品内容量単位
						domain.getShoppingItemCapacityUnit().getValue()));
			}
			// 商品のカロリー量の値が設定されている場合、値を設定
			if(domain.getShoppingItemCalories().getValue() != null) {
				// 商品内容量が未設定の場合、「（」を出力、設定ありの場合は「/」を出力
				if(nameTextBuff.length() == 0) {
					// 開始文字列を出力
					nameTextBuff.append("（");
				} else {
					// 区切り文字列を出力
					nameTextBuff.append("/");
				}
				nameTextBuff.append(domain.getShoppingItemCalories().toString());
			}
			// 値が設定されている場合、最後の「）」の値を出力
			if(nameTextBuff.length() > 0) {
				// 終了文字列を出力
				nameTextBuff.append("）");
			}
			String shoppingItemNameText = domain.getShoppingItemName().getValue() + nameTextBuff.toString();
			
			// 商品一覧情報の明細データを返却
			return AbstractShoppingItemInfoManageSearchResponse.ShoppingItemListItem.from(
				// 商品コード
				domain.getShoppingItemCode().getValue(),
				// 商品区分名
				domain.getShoppingItemKubunName().getValue(),
				// 商品名(内容量／カロリー)
				shoppingItemNameText,
				// 商品詳細
				domain.getShoppingItemDetailContext().getValue(),
				// 商品JANコード
				domain.getShoppingItemJanCode().getValue(),
				// 支出項目名
				domain.getSisyutuItemName().getValue(),
				// 会社名
				domain.getCompanyName().getValue(),
				// 基準店舗名
				domain.getStandardShopName().getValue(),
				// 基準価格
				domain.getStandardPrice().toString());
		}).collect(Collectors.toUnmodifiableList());
	}
	
	/**
	 *<pre>
	 * 商品情報を検索条件に商品を検索し、結果をレスポンスに設定します。
	 * 情報管理(商品)画面の商品検索結果画面情報にセッションに設定する商品検索条件を設定します。
	 *</pre>
	 * @param userId ユーザID
	 * @param searchTargetKubun 検索対象(検索条件入力フォーム設定値)
	 * @param searchValue 検索条件(検索条件入力フォーム設定値)
	 * @param response 情報管理(商品)画面の商品検索結果画面情報
	 *
	 */
	private void execActSearchShoppingItem(
			UserId userId, String searchTargetKubun, String searchValue, AbstractShoppingItemInfoManageSearchResponse response) {
		
		/* 商品検索条件のドメインと検索名を設定 */
		SearchQueryShoppingItemInfoSearchCondition searchCondition = null;
		String searchResultNameValue = null;
		
		// 検索条件が商品区分名の場合
		if(Objects.equals(searchTargetKubun, MyHouseholdAccountBookContent.SEARCH_TARGET_SHOPPING_ITEM_KUBUN_NAME)) {
			// 検索条件を設定
			searchCondition = SearchQueryShoppingItemInfoSearchCondition.from(
					// ユーザID
					userId,
					// 商品区分名
					ShoppingItemKubunName.from(searchValue),
					// 商品名
					null,
					// 会社名
					null,
					//商品JANコード
					null);
			
			// 商品検索名を設定
			searchResultNameValue = "商品区分名：" + searchValue;
			
		// 検索条件が商品名の場合
		} else if(Objects.equals(searchTargetKubun, MyHouseholdAccountBookContent.SEARCH_TARGET_SHOPPING_ITEM_NAME)) {
			// 検索条件を設定
			searchCondition = SearchQueryShoppingItemInfoSearchCondition.from(
					// ユーザID
					userId,
					// 商品区分名
					null,
					// 商品名
					ShoppingItemName.from(searchValue),
					// 会社名
					null,
					//商品JANコード
					null);
			
			// 商品検索名を設定
			searchResultNameValue = "商品名：" + searchValue;
			
		// 検索条件が会社名の場合
		} else if(Objects.equals(searchTargetKubun, MyHouseholdAccountBookContent.SEARCH_TARGET_COMPANY_NAME)) {
			// 検索条件を設定
			searchCondition = SearchQueryShoppingItemInfoSearchCondition.from(
					// ユーザID
					userId,
					// 商品区分名
					null,
					// 商品名
					null,
					// 会社名
					ShoppingItemCompanyName.from(searchValue),
					// 商品JANコード
					null);
			
			// 商品検索名を設定
			searchResultNameValue = "会社名：" + searchValue;
			
		// 検索条件が商品JANコードの場合
		} else if(Objects.equals(searchTargetKubun, MyHouseholdAccountBookContent.SEARCH_TARGET_SHOPPING_ITEM_JAN_CODE)) {
			// 検索条件を設定
			searchCondition = SearchQueryShoppingItemInfoSearchCondition.from(
					// ユーザID
					userId,
					// 商品区分名
					null,
					// 商品名
					null,
					// 会社名
					null,
					// 商品JANコード
					ShoppingItemJanCode.from(searchValue));
			
			// 商品検索名を設定
			searchResultNameValue = "商品JANコード：" + searchValue;
			
		// 上記以外の場合は不正値としてエラー
		} else {
			throw new MyHouseholdAccountBookRuntimeException("商品検索条件が不正です。管理者に問い合わせてください。[searchTargetKubun="
					+ searchTargetKubun + "]");
		}
		
		// セッションに設定する商品検索条件を設定
		response.setShoppingItemSearchInfo(
				ShoppingItemSearchInfo.from(MyHouseholdAccountBookContent.ACT_SEARCH_SHOPPING_ITEM, searchTargetKubun, searchValue, null));
		
		// 指定した検索条件に一致する商品情報の一覧を取得
		ShoppingItemInquiryList searchResult = shoppingItemRepository.selectShoppingItemInfoSearchCondition(searchCondition);
		if(searchResult.isEmpty()) {
			response.addMessage("指定した検索条件に一致する商品は0件です。");
		} else {
			// 商品検索結果をレスポンスに設定
			response.addShoppingItemList(createShoppingItemList(searchResult));
			// 商品検索結果名を設定
			response.setSearchResultNameValue(searchResultNameValue);
			
		}
	}
	
	/**
	 *<pre>
	 * 支出項目コードを検索条件に商品を検索し、結果をレスポンスに設定します。
	 * 情報管理(商品)画面の商品検索結果画面情報にセッションに設定する商品検索条件を設定します。
	 *</pre>
	 * @param userId ログインユーザID
	 * @param sisyutuItemCode 支出項目コード
	 * @param response 情報管理(商品)画面の商品検索結果画面情報
	 *
	 */
	private void execActSearchSisyutuItem(UserId userId, SisyutuItemCode sisyutuItemCode, AbstractShoppingItemInfoManageSearchResponse response) {
		// 選択した支出項目名を取得(＞で区切った値)
		String sisyutuItemName = sisyutuItemComponent.getSisyutuItemName(userId, sisyutuItemCode);
		
		// 指定した支出項目コードに属する商品一覧を取得
		ShoppingItemInquiryList searchResult = shoppingItemRepository.findByIdAndSisyutuItemCode(
				SearchQueryUserIdAndSisyutuItemCode.from(userId, sisyutuItemCode));
		if(searchResult.isEmpty()) {
			response.addMessage("指定した支出項目「" + sisyutuItemName + "」に登録されている商品は0件です。");
		} else {
			// 商品検索結果をレスポンスに設定
			response.addShoppingItemList(createShoppingItemList(searchResult));
			// 商品検索結果名を設定
			response.setSearchResultNameValue("支出項目：" + sisyutuItemName);
			// セッションに設定する商品検索条件を設定
			response.setShoppingItemSearchInfo(ShoppingItemSearchInfo.from(
					MyHouseholdAccountBookContent.ACT_SEARCH_SISYUTU_ITEM,
					null,
					null,
					sisyutuItemCode.getValue()));
		}
	}
	
	/**
	 *<pre>
	 * 指定した商品JANコードが登録済みの商品JANコードかどうかをチェックします。
	 *</pre>
	 * @param userId ログインユーザID
	 * @param shoppingItemJanCode 商品JANコード
	 * @param response 画面情報
	 * @return 商品JANコードが未登録の場合チェックOK:true、商品JANコードが既に登録済みの場合チェックNG:false
	 *
	 */
	private boolean checkShoppingItemJanCode(UserId userId, ShoppingItemJanCode shoppingItemJanCode, AbstractResponse response) {
		// 商品JANコードで検索するための条件を生成
		SearchQueryUserIdAndShoppingItemJanCode searchJanCode = SearchQueryUserIdAndShoppingItemJanCode.from(
				// ユーザID
				userId,
				// 商品JANコード
				shoppingItemJanCode);
		
		// 商品JANコードに対応する商品の登録件数を取得
		int checkCount = shoppingItemRepository.countByIdAndShoppingItemJanCode(searchJanCode);
		// 商品JANコードが既に登録済みの場合、登録済み(チェックNG:false)を返却
		if(checkCount > 0) {
			// エラーメッセージを設定
			response.addMessage("既に登録済みの商品JANコードが指定されています。[商品JANコード:" + shoppingItemJanCode + "]");
			// 既に登録済みの商品JANコードに対応する商品情報を取得
			ShoppingItemInquiryList searchJanCodeList = shoppingItemRepository.findByIdAndShoppingItemJanCode(searchJanCode);
			if(!searchJanCodeList.isEmpty()) {
				response.addMessage("商品名：" + searchJanCodeList.getValues().get(0).getShoppingItemName());
			}
			return false;
			
		// 商品JANコードが未登録の場合、未登録(チェックOK:true)を返却
		} else {
			return true;
		}
	}
}
