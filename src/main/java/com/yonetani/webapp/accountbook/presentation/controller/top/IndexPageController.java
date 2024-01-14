/**
 * マイ家計簿アクセス時のindexとログイン画面を表示するコントローラーです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/06/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller.top;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * マイ家計簿アクセス時のindexとログイン画面を表示するコントローラーです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Controller
@Log4j2
public class IndexPageController {
	
	/**
	 *<pre>
	 * indexページ表示のGet要求時のマッピングです。
	 *</pre>
	 * @return
	 *
	 */
	@GetMapping
	public String getIndex() {
		log.trace("getIndex:");
		return "index";
	}
	
	/**
	 *<pre>
	 * loginページ表示のGet要求時のマッピングです。
	 *</pre>
	 * @return
	 *
	 */
	@GetMapping("/login/")
	public String getLogin() {
		log.debug("getLogin:");
		return "login";
	}
}
