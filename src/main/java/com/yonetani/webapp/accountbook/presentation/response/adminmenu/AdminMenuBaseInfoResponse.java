/**
 * 管理者画面メニュー ベース情報管理画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/11/04 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.adminmenu;

import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.presentation.request.adminmenu.AdminMenuUploadBaseInfoFileForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *<pre>
 * 管理者画面メニュー ベース情報管理画面表示情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminMenuBaseInfoResponse extends AbstractResponse {

	// ベース情報ファイル登録フォーム
	@Setter
	private AdminMenuUploadBaseInfoFileForm adminMenuUploadBaseInfoFileForm;
	
	// 各ベーステーブルのカウント結果
	@Setter
	private int countSisyutuItemBaseTable = 0;
	@Setter
	private int countShopBaseTable = 0;
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @return 管理者画面メニュー ベース情報管理画面表示情報
	 *
	 */
	public static AdminMenuBaseInfoResponse getInstance() {
		return new AdminMenuBaseInfoResponse();
	}
	
	/**
	 *<pre>
	 * 現在のレスポンス情報から画面返却データのModelAndViewを生成して返します。
	 *</pre>
	 * @return 画面返却データのModelAndView
	 *
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("adminmenu/baseinfo");
		// ファイルアップロード(ベース情報データ)入力フォーム
		if(adminMenuUploadBaseInfoFileForm == null) {
			adminMenuUploadBaseInfoFileForm = new AdminMenuUploadBaseInfoFileForm();
		}
		modelAndView.addObject("baseInfoFileForm", adminMenuUploadBaseInfoFileForm);
		// 各ベーステーブルのカウント結果を設定
		modelAndView.addObject("countSisyutuItemBaseTable", countSisyutuItemBaseTable);
		modelAndView.addObject("countShopBaseTable", countShopBaseTable);
		modelAndView.addObject("countCodeTable", "???");
		// テーブル名
		modelAndView.addObject("sisyutuItemBaseTableName", MyHouseholdAccountBookContent.SISYUTU_ITEM_BASE_TABLE);
		modelAndView.addObject("shopBaseTableName", MyHouseholdAccountBookContent.SHOP_BASE_TABLE);
		modelAndView.addObject("codeTableName", MyHouseholdAccountBookContent.CODE_TABLE);
		
		return modelAndView;
	}

	/**
	 *<pre>
	 * ベース情報管理初期表示(get要求)にリダイレクトします
	 *</pre>
	 * @return 画面返却データのModelAndView
	 */
	@Override
	public ModelAndView buildRedirect() {
		if(isTransactionSuccessFull()) {
			return new ModelAndView("redirect:/myhacbook/admin/completeUploadBaseInfo/");
		} else {
			return super.buildRedirect();
		}
	}
	
	/**
	 *<pre>
	 * バリデーションチェックを行った入力フォームの値から画面返却データのModelAndViewを生成して返します。
	 *</pre>
	 * @param baseInfoFileForm バリデーションチェックを行った入力フォームの値
	 * @return 画面返却データのModelAndView
	 *
	 */
	public static ModelAndView buildBindingError(AdminMenuUploadBaseInfoFileForm baseInfoFileForm) {
		AdminMenuBaseInfoResponse response = new AdminMenuBaseInfoResponse();
		response.setAdminMenuUploadBaseInfoFileForm(baseInfoFileForm);
		return response.build();
	}
	
	/**
	 *<pre>
	 * バリデーションチェックを行った入力フォームの値から画面返却データのModelAndViewを生成して返します。
	 * 付加情報として、画面に出力するメッセージを設定します。
	 *</pre>
	 * @param baseInfoFileForm バリデーションチェックを行った入力フォームの値
	 * @param message 画面に出力するメッセージ
	 * @return 画面返却データのModelAndView
	 *
	 */
	public static ModelAndView buildBindingError(AdminMenuUploadBaseInfoFileForm baseInfoFileForm, String message) {
		AdminMenuBaseInfoResponse response = new AdminMenuBaseInfoResponse();
		response.setAdminMenuUploadBaseInfoFileForm(baseInfoFileForm);
		response.addMessage(message);
		return response.build();
	}

	/**
	 *<pre>
	 * 指定したエラーメッセージをもとに画面返却データのModelAndViewを生成して返します。
	 *</pre>
	 * @param message 画面に出力するメッセージ
	 * @return 画面返却データのModelAndView
	 *
	 */
	public static ModelAndView buildBindingError(String message) {
		AdminMenuBaseInfoResponse response = new AdminMenuBaseInfoResponse();
		response.addMessage(message);
		return response.build();
	}
	
}
