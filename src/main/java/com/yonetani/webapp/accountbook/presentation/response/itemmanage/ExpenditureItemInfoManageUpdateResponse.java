/**
 * 情報管理(支出項目)更新画面表示情報です。支出項目登録・更新時のレスポンスデータとなります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/03/02 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage;

import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ExpenditureItemInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *<pre>
 * 情報管理(支出項目)更新画面表示情報です。支出項目登録・更新時のレスポンスデータとなります。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpenditureItemInfoManageUpdateResponse extends AbstractExpenditureItemInfoManageResponse {
	
	// 支出項目入力フォーム
	@Setter
	private ExpenditureItemInfoForm expenditureItemInfoForm;
	// 親の支出項目名
	@Setter
	private String parentSisyutuItemName;
	// 親に属する子の選択リスト
	private SelectViewItem parentMembersItem;
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @return 情報管理(支出項目)画面表示情報
	 *
	 */
	public static ExpenditureItemInfoManageUpdateResponse getInstance() {
		return new ExpenditureItemInfoManageUpdateResponse();
	}
	
	/**
	 *<pre>
	 * 親に属する子の選択リストを設定します。
	 * 表示順を変更するための選択リストの表示データとなります。
	 *</pre>
	 * @param addList　親に属する子のリスト
	 *
	 */
	public void setParentMembersItemList(List<OptionItem> addList) {
		if(CollectionUtils.isEmpty(addList)) {
			parentMembersItem = SelectViewItem.from(Collections.emptyList());
		} else {
			parentMembersItem = SelectViewItem.from(addList);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("itemmanage/ExpenditureItemInfoManageUpdate");
		// 支出項目入力フォーム
		if(expenditureItemInfoForm == null) {
			expenditureItemInfoForm = new ExpenditureItemInfoForm();
		}
		modelAndView.addObject("expenditureItemInfoForm",expenditureItemInfoForm);
		// 親の支出項目名
		modelAndView.addObject("parentSisyutuItemName",parentSisyutuItemName);
		// 親に属する子のリスト
		if(parentMembersItem == null) {
			parentMembersItem = SelectViewItem.from(Collections.emptyList());
		}
		modelAndView.addObject("parentMembers",parentMembersItem);
		
		return modelAndView;
	}
	
	/**
	 *<pre>
	 * 画面に出力するエラーメッセージから画面返却データのModelAndViewを生成して返します。
	 * 画面表示データはすべて空となるので注意してください。
	 *</pre>
	 * @param loginUserInfo ログインユーザ情報
	 * @param errorMessage 画面に出力するエラーメッセージ
	 * @return 画面返却データのModelAndView
	 *
	 */
	public static ModelAndView buildBindingError(LoginUserInfo loginUserInfo, String errorMessage) {
		ExpenditureItemInfoManageUpdateResponse res = ExpenditureItemInfoManageUpdateResponse.getInstance();
		// エラーメッセージを設定
		res.addErrorMessage(errorMessage);
		// ログインユーザ名を設定
		res.setLoginUserName(loginUserInfo.getUserName());
		// 画面表示のModelとViewを生成して返却
		return res.build();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getRedirectUrl() {
		// 支出項目情報登録完了後、リダイレクトするURL
		return "redirect:/myhacbook/managebaseinfo/expenditeminfo/updateComplete/";
	}
}
