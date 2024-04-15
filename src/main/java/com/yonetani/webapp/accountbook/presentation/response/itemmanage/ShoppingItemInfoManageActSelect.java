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

import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
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
public class ShoppingItemInfoManageActSelect extends AbstractResponse {
	
	// TODO:
	private class SelectShoppingItemInfo {
		
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
