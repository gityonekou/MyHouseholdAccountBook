/**
 * 情報管理(商品)更新画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/09 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShoppingItemInfoUpdateForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *<pre>
 * 情報管理(商品)更新画面表示情報です。
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
	private final ShoppingItemInfoUpdateForm shoppingItemInfoUpdateForm;
	// 店舗グループ
	private final SelectViewItem standardShopsList;
	// 商品内容量単位
	private final SelectViewItem shoppingItemCapacityUnitList;
	
	// 支出項目名
	@Setter
	private String sisyutuItemName;
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @param inputForm  更新商品情報入力フォーム
	 * @param addList 基準店舗選択ボックスの表示情報リスト
	 * @param capacityUnitList 商品内容量単位選択ボックスの表示情報リスト
	 * @return 情報管理(商品)更新情報入力画面情報
	 *
	 */
	public static ShoppingItemInfoManageUpdateResponse getInstance(ShoppingItemInfoUpdateForm inputForm, List<OptionItem> addList,
			List<OptionItem> capacityUnitList) {
		// 更新商品情報入力フォームがnullなら空データを設定
		if(inputForm == null) {
			inputForm = new ShoppingItemInfoUpdateForm();
		}
		// 基準店舗選択ボックスの表示情報リストを生成
		List<OptionItem> standardShopsOptionList = new ArrayList<>();
		standardShopsOptionList.add(OptionItem.from("", "選択なし"));
		if(!CollectionUtils.isEmpty(addList)) {
			standardShopsOptionList.addAll(addList);
		}
		// 商品内容量単位選択ボックスの表示情報リストを生成
		List<OptionItem> capacityUnitOptionList = new ArrayList<>();
		capacityUnitOptionList.add(OptionItem.from("", "選択なし"));
		if(!CollectionUtils.isEmpty(capacityUnitList)) {
			capacityUnitOptionList.addAll(capacityUnitList);
		}
		
		// 入力フォームと基準店舗選択ボックスの表示情報リストを元に情報管理(商品)更新情報入力画面情報を生成
		return new ShoppingItemInfoManageUpdateResponse(
				// 更新商品情報入力フォーム
				inputForm,
				// 店舗グループ選択ボックス
				SelectViewItem.from(standardShopsOptionList),
				// 商品内容量単位選択ボックス
				SelectViewItem.from(capacityUnitOptionList));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("itemmanage/ShoppingItemInfoManageUpdate");
		// 更新商品情報入力フォーム
		modelAndView.addObject("shoppingItemInfoUpdateForm", shoppingItemInfoUpdateForm);
		// 支出項目名
		modelAndView.addObject("sisyutuItemName", sisyutuItemName);
		// 基準店舗選択ボックス
		modelAndView.addObject("standardShopsList", standardShopsList);
		// 商品内容量単位選択ボックス
		modelAndView.addObject("shoppingItemCapacityUnitList", shoppingItemCapacityUnitList);
		
		return modelAndView;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String buildRedirectUrl(RedirectAttributes redirectAttributes) {
		// 商品情報登録完了後、リダイレクトするURL
		return "redirect:/myhacbook/managebaseinfo/shoppingiteminfo/updateComplete/";
	}
}
