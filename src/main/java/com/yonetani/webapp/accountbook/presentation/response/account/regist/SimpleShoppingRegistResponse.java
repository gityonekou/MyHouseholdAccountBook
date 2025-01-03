/**
 * 買い物登録(簡易タイプ)画面レスポンス情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.account.regist;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.SimpleShoppingRegistInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 買い物登録(簡易タイプ)画面レスポンス情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleShoppingRegistResponse extends AbstractSimpleShoppingRegistListResponse {
	

	
	// 店舗区分選択ボックス
	private final SelectViewItem shopKubunSelectList;
	// 店名選択ボックス
	private final SelectViewItem shopNameSelectList;
	// 簡易タイプ買い物登録情報フォームデータ
	private final SimpleShoppingRegistInfoForm simpleShoppingRegistInfoForm;
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @param addShopKubunList 店舗区分選択ボックスの表示情報リスト
	 * @param addShopNameList 店名選択ボックスの表示情報リスト
	 * @param simpleShoppingRegist 簡易タイプ買い物登録情報フォームデータ
	 * @return 買い物登録(簡易タイプ)画面表示情報
	 *
	 */
	public static SimpleShoppingRegistResponse getInstance(
			List<OptionItem> addShopKubunList,
			List<OptionItem> addShopNameList,
			SimpleShoppingRegistInfoForm simpleShoppingRegist) {
		
		// 店名選択ボックスを生成
		List<OptionItem> shopNameOtionList = new ArrayList<>();
		if(CollectionUtils.isEmpty(addShopNameList)) {
			// 店名選択ボックスが空の場合、空用の店名選択ボックスを生成
			shopNameOtionList.add(OptionItem.from("", "店舗区分を再選択してください！"));
		} else {
			// 店名リストありの場合、リスト情報を店名選択ボックスに追加
			shopNameOtionList.addAll(addShopNameList);
		}
		// 買い物登録(簡易タイプ)画面表示情報を生成
		SimpleShoppingRegistResponse  response = new SimpleShoppingRegistResponse(
				// 店舗区分選択ボックス
				SelectViewItem.from(addShopKubunList),
				// 店名選択ボックス
				SelectViewItem.from(shopNameOtionList),
				// フォームデータ
				simpleShoppingRegist);
		// 対象年月を設定
		response.setYearMonth(simpleShoppingRegist.getTargetYearMonth());
		// 画面表示情報を返却
		return response;
	}
	
	/**
	 *<pre>
	 * 更新完了時のリダイレクト用レスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearMonth 対象年月
	 * @return 買い物登録(簡易タイプ)画面リダイレクト情報
	 *
	 */
	public static SimpleShoppingRegistResponse getRedirectInstance(String targetYearMonth) {
		// フォームデータを生成(対象年月のみ値を設定)
		SimpleShoppingRegistInfoForm formData = new SimpleShoppingRegistInfoForm();
		formData.setTargetYearMonth(targetYearMonth);
		// 買い物登録(簡易タイプ)画面表示情報を生成
		SimpleShoppingRegistResponse  response = new SimpleShoppingRegistResponse(null, null, formData);
		// 画面表示情報を返却
		return response;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 簡易タイプの買い物登録入力フォームの値のうち、disabled項目の値に値を設定(入力値が送信されてこないので、ここで値をコピー)
		simpleShoppingRegistInfoForm.setTotalPurchasePriceView(simpleShoppingRegistInfoForm.getTotalPurchasePrice());
		simpleShoppingRegistInfoForm.setTaxTotalPurchasePriceView(simpleShoppingRegistInfoForm.getTaxTotalPurchasePrice());
		simpleShoppingRegistInfoForm.setShoppingTotalAmountView(simpleShoppingRegistInfoForm.getShoppingTotalAmount());
		
		// 買い物登録(簡易タイプ)画面のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("account/regist/SimpleShoppingRegist");
		// 店舗区分選択ボックス
		modelAndView.addObject("shopKubunSelectList", shopKubunSelectList);
		// 店名選択ボックス
		modelAndView.addObject("shopNameSelectList", shopNameSelectList);
		// 簡易タイプの買い物登録入力フォーム
		modelAndView.addObject("simpleShoppingRegistInfoForm", simpleShoppingRegistInfoForm);
		
		return modelAndView;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractResponse addBindingErrorMessage(BindingResult bindingResult) {
		// 店舗区分、店舗コード、買い物日以外のバリデーションエラーがある場合、
		// バリデーションエラーメッセージを表示メッセージに追加
		for(FieldError fieldError : bindingResult.getFieldErrors()) {
			// フィールドエラーが店舗区分、店舗コード、買い物日以外
			if(!fieldError.getField().equals("shopKubunCode")
					&& !fieldError.getField().equals("shopCode")
					&& !fieldError.getField().equals("shoppingDate")
					&& !fieldError.getField().equals("checkedShoppingDate")) {
				// バリデーションエラーメッセージを表示メッセージに追加
				addMessage(fieldError.getDefaultMessage());
			}
		}
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String buildRedirectUrl(RedirectAttributes redirectAttributes) {
		// 表示対象の年月を設定(フォームに対象年月は必須設定されているはずなので、ここでのnullチェックはしない)
		redirectAttributes.addAttribute("targetYearMonth", simpleShoppingRegistInfoForm.getTargetYearMonth());
		// 登録完了後、リダイレクトするURL
		return "redirect:/myhacbook/accountregist/simpleshoppingregist/updateComplete/";
	}
}
