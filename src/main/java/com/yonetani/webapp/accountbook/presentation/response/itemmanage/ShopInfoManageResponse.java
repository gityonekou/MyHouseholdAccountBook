/**
 * 情報管理(お店)画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 情報管理(お店)画面表示情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ShopInfoManageResponse extends AbstractResponse {

	// 店舗グループ
	private final SelectViewItem shopGroupItem;
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @param addList 店舗グループ表示情報のリスト
	 * @return 情報管理(お店)画面表示情報
	 *
	 */
	public static ShopInfoManageResponse getInstance(List<OptionItem> addList) {
		List<OptionItem> optionList = new ArrayList<>();
		optionList.add(OptionItem.from("", "グループメニューを開く"));
		if(!CollectionUtils.isEmpty(addList)) {
			optionList.addAll(addList);
		}
		// グループメニューを開くを選択状態で情報管理(お店)画面表示情報を生成
		return new ShopInfoManageResponse(SelectViewItem.from(optionList, ""));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("itemmanage/ShopInfoManage");
		// 店舗グループを設定
		modelAndView.addObject("shopGroup", shopGroupItem);
		// 各画面表示情報を設定
		
		
		return modelAndView;
	}

}
