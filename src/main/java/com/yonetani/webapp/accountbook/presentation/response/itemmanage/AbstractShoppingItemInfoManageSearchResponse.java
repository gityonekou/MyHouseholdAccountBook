/**
 * 情報管理(商品)画面の商品検索結果を表示する以下画面の商品検索結果一覧表示エリア情報です。
 * 以下画面で商品検索結果一覧情報を使用しますので、このクラスを継承してレスポンスを生成してください。
 * ・情報管理(商品)検索結果画面：ShoppingItemInfoManageSearchResponse
 * ・情報管理(商品)処理選択画面：ShoppingItemInfoManageActSelect
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/02 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShoppingItemInfoSearchForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;
import com.yonetani.webapp.accountbook.presentation.session.ShoppingItemSearchInfo;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *<pre>
 * 情報管理(商品)画面の商品検索結果を表示する以下画面の商品検索結果一覧表示エリア情報です。
 * 以下画面で商品検索結果一覧情報を使用しますので、このクラスを継承してレスポンスを生成してください。
 * ・情報管理(商品)検索結果画面：ShoppingItemInfoManageSearchResponse
 * ・情報管理(商品)処理選択画面：ShoppingItemInfoManageActSelect
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractShoppingItemInfoManageSearchResponse extends AbstractResponse {

	/**
	 *<pre>
	 * 情報管理(商品)の商品検索結果画面に表示する商品一覧の明細情報です。
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	@EqualsAndHashCode
	public static class ShoppingItemListItem {
		// 商品コード
		private final String shoppingItemCode;
		// 商品区分名
		private final String shoppingItemKubunName;
		// 商品名
		private final String shoppingItemName;
		// 商品詳細
		private final String shoppingItemDetailContext;
		// 商品JANコード
		private final String shoppingItemJanCode;
		// 支出項目名
		private final String sisyutuItemName;
		// 会社名
		private final String companyName;
		// 基準店舗名
		private final String standardShopName;
		// 基準価格
		private final String standardPrice;
		
		/**
		 *<pre>
		 * 引数の値から商品一覧情報の明細データを生成して返します。
		 *</pre>
		 * @param shoppingItemCode 商品コード
		 * @param shoppingItemKubunName 商品区分名
		 * @param shoppingItemName 商品名
		 * @param shoppingItemDetailContext 商品詳細
		 * @param shoppingItemJanCode 商品JANコード
		 * @param sisyutuItemName 支出項目名
		 * @param companyName 会社名
		 * @param standardShopName 基準店舗名
		 * @param standardPrice 基準価格
		 * @return 店舗一覧情報の明細データ
		 *
		 */
		public static ShoppingItemListItem from(String shoppingItemCode, String shoppingItemKubunName, String shoppingItemName,
				String shoppingItemDetailContext, String shoppingItemJanCode, String sisyutuItemName, 
				String companyName, String standardShopName, String standardPrice) {
			return new ShoppingItemListItem(shoppingItemCode, shoppingItemKubunName, shoppingItemName,
					shoppingItemDetailContext, shoppingItemJanCode, sisyutuItemName,
					companyName, standardShopName, standardPrice);
		}
	}
	
	// 商品検索条件情報入力フォーム
	@Setter
	private ShoppingItemInfoSearchForm shoppingItemInfoSearchForm;
	
	// 商品検索結果の一覧
	private List<ShoppingItemListItem> shoppingItemList = new ArrayList<>();
	
	// 商品検索結果名
	@Setter
	private String searchResultNameValue;
	// セッションに設定する商品検索条件
	@Setter
	@Getter
	private ShoppingItemSearchInfo shoppingItemSearchInfo;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ModelAndView createModelAndView(String viewName) {
		ModelAndView modelAndView = super.createModelAndView(viewName);
		
		// 商品検索条件情報入力フォームを設定
		if(shoppingItemInfoSearchForm == null) {
			shoppingItemInfoSearchForm = new ShoppingItemInfoSearchForm();
		}
		modelAndView.addObject("shoppingItemInfoSearchForm", shoppingItemInfoSearchForm);
		
		// 商品一覧検索結果を設定
		modelAndView.addObject("shoppingItemList", shoppingItemList);
		// 商品検索結果名を設定
		modelAndView.addObject("searchResultNameValue", searchResultNameValue);
		
		return modelAndView;
	}
	
	/**
	 *<pre>
	 * 商品検索結果情報の明細リストを追加します。
	 *</pre>
	 * @param addList 追加する商品検索結果情報の明細リスト
	 *
	 */
	public void addShoppingItemList(List<ShoppingItemListItem> addList) {
		if(!CollectionUtils.isEmpty(addList)) {
			shoppingItemList.addAll(addList);
		}
	}
	
	/**
	 *<pre>
	 * 商品検索結果が設定されているかどうかを判定します。
	 *</pre>
	 * @return 空の場合はtrue、値が設定されている場合はfalse
	 *
	 */
	public boolean isShoppingItemListEmpty() {
		return CollectionUtils.isEmpty(shoppingItemList);
	}

}
