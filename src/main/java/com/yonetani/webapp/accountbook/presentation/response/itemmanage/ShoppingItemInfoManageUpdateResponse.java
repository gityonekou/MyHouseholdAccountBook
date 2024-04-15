/**
 * 情報管理(商品)の更新情報入力画面情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/09 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage;

import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShoppingItemInfoUpdateForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *<pre>
 * 情報管理(商品)の更新情報入力画面情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ShoppingItemInfoManageUpdateResponse extends AbstractResponse {
	
	// 更新商品情報入力フォーム
	@Setter
	private ShoppingItemInfoUpdateForm shoppingItemInfoUpdateForm;
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @return 情報管理(商品)更新情報入力画面情報
	 *
	 */
	public static ShoppingItemInfoManageUpdateResponse getInstance() {
		return new ShoppingItemInfoManageUpdateResponse();
	}
	
	/**
	 *<pre>
	 * バリデーションチェックを行った入力フォームの値から画面返却データのModelAndViewを生成して返します。
	 *</pre>
	 * @param inputForm バリデーションチェックを行った入力フォームの値
	 * @return 画面返却データのModelAndView
	 *
	 */
	public static ModelAndView buildBindingError(ShoppingItemInfoUpdateForm inputForm) {
		ShoppingItemInfoManageUpdateResponse response = ShoppingItemInfoManageUpdateResponse.getInstance();
		response.setShoppingItemInfoUpdateForm(inputForm);
		return response.build();
	}
	
	/**
	 *<pre>
	 * 画面に出力するエラーメッセージから画面返却データのModelAndViewを生成して返します。
	 * 画面表示データはすべて空となるので注意してください。
	 *</pre>
	 * @param errorMessage 画面に出力するエラーメッセージ
	 * @return 画面返却データのModelAndView
	 *
	 */
	public static ModelAndView buildBindingError(String errorMessage) {
		ShoppingItemInfoManageUpdateResponse res = ShoppingItemInfoManageUpdateResponse.getInstance();
		// エラーメッセージを設定
		res.addErrorMessage(errorMessage);
		// 画面表示のModelとViewを生成して返却
		return res.build();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("itemmanage/ShoppingItemInfoManageUpdate");
		// 更新商品情報入力フォーム
		if(shoppingItemInfoUpdateForm == null) {
			shoppingItemInfoUpdateForm = new ShoppingItemInfoUpdateForm();
		}
// TODO:
shoppingItemInfoUpdateForm.setAction("add");
		
		modelAndView.addObject("shoppingItemInfoUpdateForm", shoppingItemInfoUpdateForm);
		
		
		return modelAndView;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getRedirectUrl() {
		// 支出項目情報登録完了後、リダイレクトするURL
		return "redirect:/myhacbook/managebaseinfo/shoppingiteminfo/updateComplete/";
	}
}
