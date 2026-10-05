/**
 * 支払方法コードに対応する支払方法名を解決した結果を保持する値オブジェクトです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.paymentmethod;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 支払方法コードに対応する支払方法名を解決した結果を保持する値オブジェクトです。
 * 解決できない場合（システム予約値・未登録）は「－」を格納した UNRESOLVED を使用します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ResolvedPaymentMethodName {
	// 解決できない場合の値オブジェクト
	public static final ResolvedPaymentMethodName UNRESOLVED = new ResolvedPaymentMethodName("－");
	// 支払方法名（解決済み）
	private final String value;

	/**
	 *<pre>
	 * 支払方法名（解決済み）の値オブジェクトを生成します。
	 *</pre>
	 * @param value 支払方法名（解決済み）
	 * @return 支払方法名（解決済み）値オブジェクト「ResolvedPaymentMethodName」
	 *
	 */
	public static ResolvedPaymentMethodName from(String value) {
		return new ResolvedPaymentMethodName(value);
	}
}
