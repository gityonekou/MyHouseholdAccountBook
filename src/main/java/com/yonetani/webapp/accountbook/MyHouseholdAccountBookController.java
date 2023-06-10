/**
 * マイ家計簿アクセス時のindexとログイン画面表示、ログイン後のトップページを表示するコントローラーです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/06/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *<pre>
 * マイ家計簿アクセス時のindexとログイン画面表示、ログイン後のトップページを表示するコントローラーです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Controller
public class MyHouseholdAccountBookController {
	
	/**
	 *<pre>
	 * indexページ表示のGet要求時のマッピングです。
	 *</pre>
	 * @return
	 *
	 */
	@GetMapping
	public String getIndex() {
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
		return "login";
	}
	
	/**
	 *<pre>
	 * トップページメニュー表示のGet要求時のマッピングです。
	 *</pre>
	 * @return
	 *
	 */
	@GetMapping("/myhacbook/topmenu/")
	public String getTopMenu() {
		return "top/topmenu";
	}
}
