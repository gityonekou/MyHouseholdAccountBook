/**
 * 買い物登録方法選択画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.account.regist;

import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 買い物登録方法選択画面表示情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ShoppingRegistTopMenuResponse extends AbstractResponse {
	
	// 買い物登録を行う対象年月
	private final String targetYearMonth;
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearMonth 買い物登録の対象の年月(YYYMM)
	 * @return 買い物登録方法選択画面表示情報
	 *
	 */
	public static ShoppingRegistTopMenuResponse getInstance(TargetYearMonth targetYearMonth) {
		return new ShoppingRegistTopMenuResponse(targetYearMonth.getValue());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 買い物登録方法選択画面のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("account/regist/ShoppingRegistTopMenu");
		// 対象年月
		modelAndView.addObject("targetYearMonth", targetYearMonth);
		return modelAndView;
	}
}
