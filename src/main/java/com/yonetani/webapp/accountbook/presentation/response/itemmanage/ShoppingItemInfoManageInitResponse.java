/**
 * 情報管理(商品)の初期表示画面レスポンス情報です。
 * 商品情報の検索条件入力、商品を登録する対象の支出項目一覧情報を持ちます。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage;

import org.springframework.web.servlet.ModelAndView;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 情報管理(商品)の初期表示画面レスポンス情報です。
 * 商品情報の検索条件入力、商品を登録する対象の支出項目一覧情報を持ちます。
 * 
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ShoppingItemInfoManageInitResponse extends AbstractExpenditureItemInfoManageResponse {

	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @return 情報管理(商品)初期表示画面情報
	 *
	 */
	public static ShoppingItemInfoManageInitResponse getInstance() {
		return new ShoppingItemInfoManageInitResponse();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("itemmanage/ShoppingItemInfoManageInit");
		// TODO 自動生成されたメソッド・スタブ
		// 各画面表示情報を設定
		
		
		return modelAndView;
	}

}
