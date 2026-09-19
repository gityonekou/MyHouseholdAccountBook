/**
 * 情報管理(お店)画面の店舗情報が格納されたフォームデータです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/02/03 : 1.00.00                      新規作成
 * 2026/08/18 : 1.01.00  feature-1.03-dev1   支払方法・銀行口座管理追加対応
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.itemmanage;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *<pre>
 * 情報管理(お店)画面の店舗情報が格納されたフォームデータです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@Data
public class ShopInfoForm {
	// アクション
	private String action;
	// 店舗コード
	private String shopCode;
	// 表示順(更新比較用)
	private String shopSortBefore;
	
	// 店舗区分
	@NotBlank
	private String shopKubun;
	// 店舗名
	@NotBlank
	@Size(min = 1, max = 50)
	private String shopName;
	// 表示順
	@Min(1)
	@Max(899)
	private Integer shopSort;
	// デフォルト支払方法コード(任意)
	private String defaultPaymentMethodCode;

	/**
	 *<pre>
	 * デフォルト支払方法にシステム予約値が指定されていないかを検証します。
	 * 任意項目のため、未入力の場合は許容します。
	 *</pre>
	 * @return 検証結果
	 *
	 */
	@AssertTrue(message = "デフォルト支払方法にシステム予約値は指定できません。")
	private boolean isDefaultPaymentMethodCodeValid() {
		if(!StringUtils.hasLength(defaultPaymentMethodCode)) {
			return true;
		}
		return PaymentMethodCode.tryFrom(defaultPaymentMethodCode)
				.map(code -> !code.isSystemReserved())
				.orElse(true);
	}
}
