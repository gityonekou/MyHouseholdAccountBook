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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
public class ShoppingRegistTopMenuResponse extends AbstractSimpleShoppingRegistListResponse {
	
	// 買い物登録を行う対象年月
	private final String targetYearMonth;
	// 未登録の支出項目があるかどうかのフラグ:未登録ありの場合、各種登録ボタンは押下不可となります。
	@Setter
	private boolean btnDisabled = false;
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearMonth 買い物登録の対象の年月(YYYMM)
	 * @return 買い物登録方法選択画面表示情報
	 *
	 */
	public static ShoppingRegistTopMenuResponse getInstance(TargetYearMonth targetYearMonth) {
		// 買い物登録方法選択画面表示情報を生成
		ShoppingRegistTopMenuResponse response = new ShoppingRegistTopMenuResponse(targetYearMonth.getValue());
		// 対象年月を設定
		response.setYearMonth(targetYearMonth.getValue());
		// 画面表示情報を返却
		return response;
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
		// 各種登録ボタンを押下不可とするかどうか(未登録の支出項目があるかどうかのフラグ:未登録ありの場合押下不可とする)
		modelAndView.addObject("btnDisabled", btnDisabled);
		return modelAndView;
	}
}
