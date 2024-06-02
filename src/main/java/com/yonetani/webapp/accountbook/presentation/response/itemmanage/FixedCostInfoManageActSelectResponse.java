/**
 * 情報管理(固定費)の処理選択画面表示情報です。
 * 固定費情報の一覧と選択した固定費から更新・削除のアクション選択時の画面表示情報を持ちます。
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

import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 情報管理(固定費)の処理選択画面表示情報です。
 * 固定費情報の一覧と選択した固定費から更新・削除のアクション選択時の画面表示情報を持ちます。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FixedCostInfoManageActSelectResponse extends AbstractResponse {
	
	// 登録済み固定費一覧
	private List<String> fixedCostItemList = new ArrayList<>();
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @return 情報管理(固定費)処理選択画面表示情報
	 *
	 */
	public static FixedCostInfoManageActSelectResponse getInstance() {
		return new FixedCostInfoManageActSelectResponse();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("itemmanage/FixedCostInfoManageActSelect");
		// 登録済み固定費一覧を設定
		modelAndView.addObject("fixedCostItemList", fixedCostItemList);
		
		return modelAndView;
	}

}
