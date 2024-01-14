/**
 * マイ家計簿の買い物登録画面表示を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・買い物登録画面初期表示(GET)
 * ・買い物登録画面初期表示(POST)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/08/27 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller.account.regist;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * マイ家計簿の買い物登録画面表示を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・買い物登録画面初期表示(GET)
 * ・買い物登録画面初期表示(POST)
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Controller
@Log4j2
@RequestMapping("/myhacbook/accountregist/shoppingregist/")
public class ShoppingRegistController {
	
	/**
	 *<pre>
	 * 買い物登録画面初期表示のGET要求時マッピングです。
	 *</pre>
	 * @return
	 *
	 */
	@GetMapping
	public String getShoppingRegist() {
		log.debug("getShoppingRegist:");
		return "account/regist/ShoppingRegist";
	}

	/**
	 *<pre>
	 * 買い物登録画面初期表示のPOST要求時マッピングです。
	 *</pre>
	 * @return
	 *
	 */
	@PostMapping
	public String postShoppingRegist() {
		log.debug("postShoppingRegist:");
		return "account/regist/ShoppingRegist";
	}
	
}
