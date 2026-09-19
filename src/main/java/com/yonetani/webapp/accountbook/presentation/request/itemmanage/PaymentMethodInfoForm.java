/**
 * 情報管理(支払方法)画面の支払方法情報が格納されたフォームデータです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.itemmanage;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodKubun;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *<pre>
 * 情報管理(支払方法)画面の支払方法情報が格納されたフォームデータです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@Data
public class PaymentMethodInfoForm {
	// アクション
	private String action;
	// 支払方法コード
	private String paymentMethodCode;
	// 表示順(更新比較用)
	private String paymentMethodSortBefore;

	// 支払方法名
	@NotBlank
	@Size(min = 1, max = 50)
	private String paymentMethodName;
	// 支払方法メモ(任意項目。dev2のクレジットカードポイント減算で、設定値がある場合のみ画面表示)
	@Size(max = 400)
	private String paymentMethodMemo;
	// 支払方法種別(1～5)
	@NotBlank
	private String paymentMethodKubun;
	// 銀行口座コード(種別により要否が異なる。下記AssertTrueで判定)
	private String bankAccountCode;
	// 集計開始日(クレジットカードのみ必須。下記AssertTrueで判定)
	private String closingDay;
	// 表示順(予約帯990～999は対象外)
	@Min(1)
	@Max(989)
	private Integer paymentMethodSort;
	// 有効/無効フラグ(デフォルトtrue)
	private Boolean enableFlg = Boolean.TRUE;
	// enableUpdateFlgは保持しない(システム行はフォームに読み込まれること自体がない)

	/**
	 *<pre>
	 * 選択した支払方法種別で銀行口座の指定が必要な場合、銀行口座コードが指定されているかを検証します。
	 * 未入力、または不正な形式値(tryFrom()がempty)の場合はここでは素通しする。
	 *</pre>
	 * @return 検証結果
	 *
	 */
	@AssertTrue(message = "選択した支払方法種別では、銀行口座の指定が必要です。")
	private boolean isBankAccountValid() {
		return PaymentMethodKubun.tryFrom(paymentMethodKubun)
				.map(kubun -> !kubun.requiresAccount() || StringUtils.hasLength(bankAccountCode))
				.orElse(true);
	}

	/**
	 *<pre>
	 * 選択した支払方法種別で集計開始日の指定が必要な場合、集計開始日が指定されているかを検証します。
	 *</pre>
	 * @return 検証結果
	 *
	 */
	@AssertTrue(message = "クレジットカードを選択した場合、集計開始日は必須です。")
	private boolean isClosingDayValid() {
		return PaymentMethodKubun.tryFrom(paymentMethodKubun)
				.map(kubun -> !kubun.requiresClosingDay() || StringUtils.hasLength(closingDay))
				.orElse(true);
	}
}
