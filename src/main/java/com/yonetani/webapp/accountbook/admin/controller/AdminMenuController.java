/**
 * 管理者画面表示を担当するコントローラーです。
 * 管理者メニューの以下画面遷移を担当します。
 * ・管理者メニュー表示
 * ・マイ家計簿ユーザ登録画面表示
 * ・マイ家計簿ユーザ登録処理
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/06/11 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *<pre>
 * 管理者画面表示を担当するコントローラーです。
 * 管理者メニューの以下画面遷移を担当します。
 * ・管理者メニュー表示
 * ・マイ家計簿ユーザ登録画面表示
 * ・マイ家計簿ユーザ登録処理
 * 
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Controller
@RequestMapping("/myhacbook/admin/")
public class AdminMenuController {
	
	/**
	 *<pre>
	 * 管理者画面メニュー表示のGet要求時のマッピングです。
	 *</pre>
	 * @return
	 *
	 */
	@GetMapping
	public String getAdminMenu() {
		return "admin/adminmenu";
	}
	
	/**
	 *<pre>
	 * マイ家計簿ユーザ登録画面初期表示のGet要求時のマッピングです。
	 *</pre>
	 * @return
	 *
	 */
	@GetMapping("/useradd")
	public String getUserAdd() {
		return "admin/useradd";
	}
	
	/**
	 *<pre>
	 * マイ家計簿ユーザ登録処理のマッピングです。
	 *</pre>
	 * @return
	 *
	 */
	@PostMapping("/useradd")
	public String postUserAdd() {
		return "admin/useradd";
	}
}
