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

import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.SimpleShoppingRegistInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

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
public class SimpleShoppingRegistResponse extends AbstractResponse {
	
	// 簡易タイプ買い物登録情報フォームデータ
	private final SimpleShoppingRegistInfoForm simpleShoppingRegist;
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @param simpleShoppingRegist 簡易タイプ買い物登録情報フォームデータ
	 * @return 買い物登録(簡易タイプ)画面表示情報
	 *
	 */
	public static SimpleShoppingRegistResponse getInstance(SimpleShoppingRegistInfoForm simpleShoppingRegist) {
		return new SimpleShoppingRegistResponse(simpleShoppingRegist);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 買い物登録(簡易タイプ)画面のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("account/regist/SimpleShoppingRegist");
		// 簡易タイプの買い物登録入力フォーム
		modelAndView.addObject("simpleShoppingRegist", simpleShoppingRegist);
		return modelAndView;
	}

}
