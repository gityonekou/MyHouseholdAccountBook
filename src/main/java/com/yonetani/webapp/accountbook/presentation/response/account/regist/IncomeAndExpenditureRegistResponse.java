/**
 * 収支登録画面レスポンス情報です。
 * 収入一覧情報、支出一覧情報を持ちます。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.account.regist;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 収支登録画面レスポンス情報です。
 * 収入一覧情報、支出一覧情報を持ちます。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class IncomeAndExpenditureRegistResponse extends AbstractResponse {
	
	// 収入一覧情報
	private List<String> incomeListInfo = new ArrayList<>();
	// 支出一覧情報
	private List<String> expenditureListInfo = new ArrayList<>();
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @return 収支登録画面表示情報
	 *
	 */
	public static IncomeAndExpenditureRegistResponse getInstance() {
		return new IncomeAndExpenditureRegistResponse();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("account/regist/IncomeAndExpenditureRegist");
		// 収入一覧情報
		modelAndView.addObject("incomeListInfo", incomeListInfo);
		// 支出一覧情報
		modelAndView.addObject("expenditureListInfo", expenditureListInfo);
		
		return modelAndView;
	}

}
