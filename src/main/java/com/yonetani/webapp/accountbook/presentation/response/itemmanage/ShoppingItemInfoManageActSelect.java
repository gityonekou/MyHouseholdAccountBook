/**
 * 情報管理(商品)の処理選択画面表示情報です。
 * 選択した商品から追加・更新のアクション選択時のレスポンスデータとなります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/13 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage;

import org.springframework.web.servlet.ModelAndView;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 情報管理(商品)の処理選択画面表示情報です。
 * 選択した商品から追加・更新のアクション選択時のレスポンスデータとなります。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ShoppingItemInfoManageActSelect extends AbstractShoppingItemInfoManageSearchResponse {
	
	/**
	 *<pre>
	 * 選択商品の詳細情報です。
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public static class SelectShoppingItemInfo {
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
		
		/**
		 *<pre>
		 * 引数の値から選択商品の詳細情報を生成して返します。
		 *</pre>
		 * @param shoppingItemCode 商品コード
		 * @param shoppingItemKubunName 商品区分名
		 * @param shoppingItemName 商品名
		 * @param shoppingItemDetailContext 商品詳細
		 * @param shoppingItemJanCode 商品JANコード
		 * @param sisyutuItemName 支出項目名
		 * @param companyName 会社名
		 * @return 選択商品の詳細情報
		 *
		 */
		public static SelectShoppingItemInfo from(String shoppingItemCode, String shoppingItemKubunName, String shoppingItemName,
				String shoppingItemDetailContext, String shoppingItemJanCode, String sisyutuItemName, String companyName) {
			return new SelectShoppingItemInfo(shoppingItemCode, shoppingItemKubunName, shoppingItemName,
					shoppingItemDetailContext, shoppingItemJanCode, sisyutuItemName, companyName);
		}
	}
	
	// 選択した商品の情報
	private final SelectShoppingItemInfo shoppingItemInfo;
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @param selectItemInfo 選択した商品の情報
	 * @return 情報管理(商品)処理選択画面情報
	 *
	 */
	public static ShoppingItemInfoManageActSelect getInstance(SelectShoppingItemInfo selectItemInfo) {
		return new ShoppingItemInfoManageActSelect(selectItemInfo);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("itemmanage/ShoppingItemInfoManageActSelect");
		// 更新商品情報入力フォーム
		modelAndView.addObject("shoppingItemInfo", shoppingItemInfo);
		
		return modelAndView;
	}

}
