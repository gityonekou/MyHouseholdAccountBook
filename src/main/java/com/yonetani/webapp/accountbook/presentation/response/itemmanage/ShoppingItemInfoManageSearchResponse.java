/**
 * 情報管理(商品)の商品検索画面情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/13 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage;

import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShoppingItemInfoSearchForm;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 情報管理(商品)の商品検索画面情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ShoppingItemInfoManageSearchResponse extends AbstractShoppingItemInfoManageSearchResponse {
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @return 情報管理(商品)商品検索画面情報
	 *
	 */
	public static ShoppingItemInfoManageSearchResponse getInstance() {
		return new ShoppingItemInfoManageSearchResponse();
	}
	
	/**
	 *<pre>
	 * バリデーションチェックを行った入力フォームの値から画面返却データのModelAndViewを生成して返します。
	 *</pre>
	 * @param loginUserInfo ログインユーザ情報
	 * @param inputForm バリデーションチェックを行った入力フォームの値
	 * @return 画面返却データのModelAndView
	 *
	 */
	public static ModelAndView buildBindingError(LoginUserInfo loginUserInfo, ShoppingItemInfoSearchForm inputForm) {
		ShoppingItemInfoManageSearchResponse response = ShoppingItemInfoManageSearchResponse.getInstance();
		// ログインユーザ名を設定
		response.setLoginUserName(loginUserInfo.getUserName());
		// 入力フォームの値を設定
		response.setShoppingItemInfoSearchForm(inputForm);
		// レスポンスからModelAndViewを生成
		return response.build();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		return createModelAndView("itemmanage/ShoppingItemInfoManageSearch");
	}
}
