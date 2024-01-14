/**
 * マイ家計簿の収支詳細画面表示を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・指定月の収支詳細画面表示(POST)
 * ・指定年の年間収支(明細)詳細画面表示(POST)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/08/27 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller.account.inquiry;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * マイ家計簿の収支詳細画面表示を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・指定月の収支詳細画面表示(POST)
 * ・指定年の年間収支(明細)詳細画面表示(POST)
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Controller
@Log4j2
@RequestMapping("/myhacbook/accountinquiry/accountdetail/")
public class AccountDetailInquiryController {
	
	/**
	 *<pre>
	 * 指定月の収支詳細画面表示のPOST要求時マッピングです。
	 *</pre>
	 * @return
	 *
	 */
	@PostMapping("/month/")
	public String postAccountMonthDetail() {
		log.debug("postAccountMonthDetail:");
		return "account/inquiry/AccountMonthDetail";
	}
	
	/**
	 *<pre>
	 * 指定年の年間収支(明細)詳細画面表示
	 *</pre>
	 * @return
	 *
	 */
	@PostMapping("/year/")
	public String postAccountYearMeisaiDetail() {
		log.debug("postAccountYearMeisaiDetail:");
		return "account/inquiry/AccountYearMeisaiDetail";
	}

}
