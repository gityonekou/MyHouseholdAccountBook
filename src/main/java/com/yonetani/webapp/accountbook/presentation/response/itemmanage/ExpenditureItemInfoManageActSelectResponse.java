/**
 * 情報管理(支出項目)の処理選択画面表示情報です。選択した支出項目から追加・更新のアクション選択時のレスポンスデータとなります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/03/02 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 情報管理(支出項目)の処理選択画面表示情報です。選択した支出項目から追加・更新のアクション選択時のレスポンスデータとなります。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpenditureItemInfoManageActSelectResponse extends AbstractResponse {
	
	/**
	 *<pre>
	 * 情報管理(支出項目)の処理選択画面に表示するユーザが選択した支出項目情報です。
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public static class SelectExpenditureItemInfo {
		// 支出項目コード
		private final String sisyutuItemCode;
		// 支出項目名
		private final String sisyutuItemName;
		// 支出項目詳細内容
		private final String sisyutuItemDetailContext;
		// 親の支出項目名称(各、親を＞区切りで表した文字列を設定)
		private final String parentSisyutuItemName;
		// 支出項目レベル
		private final int sisyutuItemLevel;
		// 更新可否フラグ
		private final boolean enableUpdateFlg;
		
		/**
		 *<pre>
		 * 引数の値からユーザが選択した支出項目情報を生成して返します。
		 *</pre>
		 * @param sisyutuItemCode 支出項目コード
		 * @param sisyutuItemName 支出項目名
		 * @param sisyutuItemDetailContext 支出項目詳細内容
		 * @param parentSisyutuItemName 親の支出項目名称(各、親を＞区切りで表した文字列を設定)
		 * @param sisyutuItemLevel 支出項目レベル
		 * @param enableUpdateFlg 更新可否フラグ
		 * @return ユーザが選択した支出項目情報
		 *
		 */
		public static SelectExpenditureItemInfo from(
				String sisyutuItemCode,
				String sisyutuItemName,
				String sisyutuItemDetailContext,
				String parentSisyutuItemName,
				int sisyutuItemLevel,
				boolean enableUpdateFlg) {
			return new SelectExpenditureItemInfo(sisyutuItemCode, sisyutuItemName, sisyutuItemDetailContext,
					parentSisyutuItemName, sisyutuItemLevel, enableUpdateFlg);
			
		}
	}
	// 選択した支出項目の詳細情報
	@Getter
	private final SelectExpenditureItemInfo sisyutuItem;
	
	// 親の支出項目に属する支出項目一覧
	private List<String> parentSisyutuItemMemberNameList = new ArrayList<>();
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @param sisyutuItem 選択した支出項目の詳細情報
	 * @return 情報管理(支出項目)の処理選択画面表示情報
	 *
	 */
	public static ExpenditureItemInfoManageActSelectResponse getInstance(SelectExpenditureItemInfo sisyutuItem) {
		return new ExpenditureItemInfoManageActSelectResponse(sisyutuItem);
	}
	
	/**
	 *<pre>
	 * 親の支出項目に属する支出項目一覧を追加します。
	 *</pre>
	 * @param addList 追加する親の支出項目に属する支出項目一覧
	 *
	 */
	public void addParentSisyutuItemMemberNameList(List<String> addList) {
		if(!CollectionUtils.isEmpty(addList)) {
			parentSisyutuItemMemberNameList.addAll(addList);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("itemmanage/ExpenditureItemInfoManageActSelect");
		// 選択した支出項目の詳細情報
		modelAndView.addObject("sisyutuItem", sisyutuItem);
		// 親の支出項目に属する支出項目一覧
		modelAndView.addObject("parentSisyutuItemMemberNameList", parentSisyutuItemMemberNameList);
		// 新規追加ボタンのアクティブ・非アクティブを設定
		if(sisyutuItem != null && sisyutuItem.getSisyutuItemLevel() != 1 && sisyutuItem.getSisyutuItemLevel() != 5) {
			// nullでない、かつ、支出項目レベルが1、5以外の場合は新規にデータを追加可能
			modelAndView.addObject("addBtnEnabled", Boolean.TRUE);
		} else {
			// 上記以外は追加不可
			modelAndView.addObject("addBtnEnabled", Boolean.FALSE);
		}
		return modelAndView;
	}

	/**
	 *<pre>
	 * 画面に出力するエラーメッセージから画面返却データのModelAndViewを生成して返します。
	 *</pre>
	 * @param errorMessage 画面に出力するエラーメッセージ
	 * @return 画面返却データのModelAndView
	 *
	 */
	public ModelAndView buildBindingError(String errorMessage) {
		// エラーメッセージを設定
		addErrorMessage(errorMessage);
		// 画面表示のModelとViewを生成して返却
		return build();
	}
	
}
