/**
 * 【説明を入力してください】
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/16 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller.account.regist;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 【説明を入力してください】
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Controller
@Log4j2
@RequestMapping("/myhacbook/accountregist/incomeandexpense/")
public class IncomeAndExpenseRegistController {
	@PostMapping
	public String test() {
		log.debug("test:");
		return "account/regist/ShoppingRegist";
	}
}
