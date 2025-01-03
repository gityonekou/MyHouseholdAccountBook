/**
 * 家計簿の登録系画面でデータ登録対象の対象年月情報を定義した基底クラスです。
 * 収支登録系画面、買い物登録系画面の画面情報はこのクラスを継承して実装してください。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/12/01 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.account.regist;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 家計簿の登録系画面でデータ登録対象の対象年月情報を定義した基底クラスです。
 * 収支登録系画面、買い物登録系画面の画面情報はこのクラスを継承して実装してください。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractRegistResponse extends AbstractResponse {
	
	// 「yyyy年MM月度」の年の値
	private String viewYear;
	// 「yyyy年MM月度」の月の値
	private String viewMonth;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ModelAndView createModelAndView(String viewName) {
		ModelAndView modelAndView = super.createModelAndView(viewName);
		// 「yyyy年MM月度」の年の値
		modelAndView.addObject("viewYear", viewYear);
		// 「yyyy年MM月度」の月の値
		modelAndView.addObject("viewMonth", viewMonth);
		return modelAndView;
	}
	
	/**
	 *<pre>
	 * 対象年月(yyyyMM)の値を年と月に分割して「yyyy年MM月度」の年の値、「yyyy年MM月度」の月の値に設定します。
	 *</pre>
	 * @param targetYearMonth 対象年月(yyyyMM)
	 *
	 */
	protected void setYearMonth(String targetYearMonth) {
		if(!StringUtils.hasLength(targetYearMonth) || targetYearMonth.length() != 6) {
			throw new MyHouseholdAccountBookRuntimeException("対象年月の値が不正です。管理者に問い合わせてください。[targetYearMonth=" + targetYearMonth + "]");
		}
		// yyyyMMのyyyyの値を設定(年の値を設定)
		viewYear = targetYearMonth.substring(0, 4);
		// yyyyMMのMMの値を設定(月の値を設定)
		viewMonth = targetYearMonth.substring(4);
	}
}
