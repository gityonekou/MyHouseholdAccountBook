/**
 * 情報管理(固定費)更新画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/22 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.request.itemmanage.FixedCostInfoUpdateForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *<pre>
 * 情報管理(固定費)更新画面表示情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FixedCostInfoManageUpdateResponse extends AbstractResponse {
	
	// 固定費情報が格納されたフォームデータです。
	private final FixedCostInfoUpdateForm fixedCostInfoUpdateForm;
	// 支払月選択ボックス
	private final SelectViewItem shiharaiTukiSelectList;
	// 支払日選択ボックス
	private final SelectViewItem shiharaiDaySelectList;
	// 支出項目名
	@Setter
	private String sisyutuItemName;
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @param inputForm 固定費情報が格納されたフォームデータ
	 * @param addTukiList 支払月選択ボックスの表示情報リスト
	 * @param addDayList 支払日選択ボックスの表示情報リスト
	 * @return 情報管理(固定費)更新画面表示情報
	 *
	 */
	public static FixedCostInfoManageUpdateResponse getInstance(FixedCostInfoUpdateForm inputForm, List<OptionItem> addTukiList,
			List<OptionItem> addDayList) {
		if(inputForm == null) {
			inputForm = new FixedCostInfoUpdateForm();
		}
		// 支払月選択ボックスの表示情報リストを生成
		List<OptionItem> optionTukiList = new ArrayList<>();
		optionTukiList.add(OptionItem.from("", "支払月を選択してください"));
		if(!CollectionUtils.isEmpty(addTukiList)) {
			optionTukiList.addAll(addTukiList);
		}
		// 支払日選択ボックスの表示情報リストを生成
		List<OptionItem> optionDayList = new ArrayList<>();
		optionDayList.add(OptionItem.from("", "支払日を選択してください"));
		if(!CollectionUtils.isEmpty(addDayList)) {
			optionDayList.addAll(addDayList);
		}
		// 入力フォームと支払月選択ボックス・支払日選択ボックスの表示情報リストを元に情報管理(固定費)更新画面表示情報を生成
		return new FixedCostInfoManageUpdateResponse(inputForm, SelectViewItem.from(optionTukiList), SelectViewItem.from(optionDayList));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("itemmanage/FixedCostInfoManageUpdate");
		// 更新商品情報入力フォーム
		modelAndView.addObject("fixedCostInfoUpdateForm", fixedCostInfoUpdateForm);
		// 支払月選択ボックス
		modelAndView.addObject("shiharaiTukiSelectList", shiharaiTukiSelectList);
		// 支払日選択ボックス
		modelAndView.addObject("shiharaiDaySelectList", shiharaiDaySelectList);
		// 支出項目名
		modelAndView.addObject("sisyutuItemName", sisyutuItemName);
		
		return modelAndView;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getRedirectUrl() {
		// 固定費登録完了後、リダイレクトするURL
		return "redirect:/myhacbook/managebaseinfo/fixedcostinfo/updateComplete/";
	}
}
