/**
 * トップページ画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/05 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.top;

import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

/**
 *<pre>
 * トップページ画面表示情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public class TopPageResponse extends AbstractResponse {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("top/topmenu");
		return modelAndView;
	}

}
