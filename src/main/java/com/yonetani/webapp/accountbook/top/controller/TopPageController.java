/**
 * マイ家計簿のログイン後のトップ画面表示を担当するコントローラーです。
 * マイ家計簿トップ画面の以下画面遷移を担当します。
 * ・マイ家計簿メニュー表示
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/06/04 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.top.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *<pre>
 * マイ家計簿のログイン後のトップ画面表示を担当するコントローラーです。
 * マイ家計簿トップ画面の以下画面遷移を担当します。
 * ・マイ家計簿メニュー表示
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Controller
public class TopPageController {
	
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
