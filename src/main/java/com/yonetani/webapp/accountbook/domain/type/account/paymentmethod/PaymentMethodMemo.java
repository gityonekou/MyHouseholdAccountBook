/**
 * 「支払方法メモ」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/08/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.paymentmethod;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「支払方法メモ」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class PaymentMethodMemo {
	// 支払方法メモ
	private final String value;

	/**
	 *<pre>
	 * 「支払方法メモ」項目の値を表すドメインタイプを生成します。
	 *
	 * [非ガード節]
	 * ・null
	 * [ガード節]
	 * ・長さが400文字より大きい
	 *</pre>
	 * @param paymentMethodMemo 支払方法メモ
	 * @return 「支払方法メモ」項目ドメインタイプ
	 *
	 */
	public static PaymentMethodMemo from(String paymentMethodMemo) {
		// ガード節(長さが400文字より大きい(文字列長がある場合のみチェック))
		if(StringUtils.hasLength(paymentMethodMemo) && paymentMethodMemo.length() > 400) {
			throw new MyHouseholdAccountBookRuntimeException("「支払方法メモ」項目の設定値が400文字より大きい値で指定されています。管理者に問い合わせてください。[length=" + paymentMethodMemo.length() + "]");
		}
		return new PaymentMethodMemo(paymentMethodMemo);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
