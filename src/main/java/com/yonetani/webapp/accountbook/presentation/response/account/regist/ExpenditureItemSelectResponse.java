/**
 * 収支登録で新規の支出を登録時の支出項目選択画面レスポンス情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/14 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.account.regist;

import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.ExpenditureSelectItemForm;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.AbstractExpenditureItemInfoManageResponse;

import lombok.Setter;

/**
 *<pre>
 * 収支登録で新規の支出を登録時の支出項目選択画面レスポンス情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public class ExpenditureItemSelectResponse extends AbstractExpenditureItemInfoManageResponse {

	// 選択した支出項目・イベント情報のフォームデータ
	@Setter
	private ExpenditureSelectItemForm expenditureSelectItemForm;
	// 支出項目名(＞で区切った値)
	@Setter
	private String sisyutuItemName;
	// 支出項目詳細内容
	@Setter
	private String sisyutuItemDetailContext;
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @return 支出項目選択画面情報
	 *
	 */
	public static ExpenditureItemSelectResponse getInstance() {
		return new ExpenditureItemSelectResponse();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("account/regist/ExpenditureItemSelect");
		// 選択した支出項目・イベント情報のフォームデータ
		modelAndView.addObject("expenditureSelectItemForm", expenditureSelectItemForm);
		// 支出項目名(＞で区切った値)
		modelAndView.addObject("sisyutuItemName", sisyutuItemName);
		// 支出項目詳細内容
		modelAndView.addObject("sisyutuItemDetailContext", sisyutuItemDetailContext);
		
		return modelAndView;
	}

}
