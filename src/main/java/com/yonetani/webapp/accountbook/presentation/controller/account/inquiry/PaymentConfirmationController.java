/**
 * マイ家計簿の支払い金額確認画面表示を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・指定月の支払い金額確認画面
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/08/26 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller.account.inquiry;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.YearMonthInquiryForm;

import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * マイ家計簿の支払い金額確認画面表示を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・指定月の支払い金額確認画面
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Controller
@Log4j2
@RequestMapping("/myhacbook/accountinquiry/paymentconfirmation/")
public class PaymentConfirmationController {

	/**
	 *<pre>
	 * 現在の決算年の年間収支(明細)画面表示のPOST要求時マッピングです。
	 *</pre>
	 * @param target 表示対象の年月
	 * @param bindingResult バリデーション結果
	 * @return 支払い金額確認画面
	 *
	 */
	@PostMapping
	public String postPaymentConfirmation(@ModelAttribute @Validated YearMonthInquiryForm target, BindingResult bindingResult) {
		log.debug("postPaymentConfirmation:target=" + target);
		if(bindingResult.hasErrors()) {
			return "account/inquiry/PaymentConfirmation";
		} else {
			return "account/inquiry/PaymentConfirmation";
		}
	}
}
