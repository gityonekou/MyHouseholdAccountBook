/**
 * 「支払方法名」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/08/19 : 1.00.00  新規作成
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
 * 「支払方法名」項目の値を表すドメインタイプです
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
public class PaymentMethodName {
	// 支払方法名
	private final String value;

	/**
	 *<pre>
	 * 「支払方法名」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・空文字列
	 * ・長さが50文字より大きい
	 *</pre>
	 * @param paymentMethodName 支払方法名
	 * @return 「支払方法名」項目ドメインタイプ
	 *
	 */
	public static PaymentMethodName from(String paymentMethodName) {
		if(!StringUtils.hasLength(paymentMethodName)) {
			throw new MyHouseholdAccountBookRuntimeException("「支払方法名」項目の設定値が空文字列です。管理者に問い合わせてください。");
		}
		if(paymentMethodName.length() > 50) {
			throw new MyHouseholdAccountBookRuntimeException("「支払方法名」項目の設定値が50文字より大きい値で指定されています。管理者に問い合わせてください。[length=" + paymentMethodName.length() + "]");
		}
		return new PaymentMethodName(paymentMethodName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
