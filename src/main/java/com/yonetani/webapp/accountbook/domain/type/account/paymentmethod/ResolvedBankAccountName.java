/**
 * 支払方法コードに対応する銀行口座名を解決した結果を保持する値オブジェクトです。
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
 * 支払方法コードに対応する銀行口座名を解決した結果を保持する値オブジェクトです。
 * 解決できない場合（システム予約値・銀行口座なし・未登録）は「－」を格納した UNRESOLVED を使用します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ResolvedBankAccountName {
	// 解決できない場合の値オブジェクト
	public static final ResolvedBankAccountName UNRESOLVED = new ResolvedBankAccountName("－");
	// 銀行口座名（解決済み）
	private final String value;

	/**
	 *<pre>
	 * 銀行口座名（解決済み）の値オブジェクトを生成します。
	 *</pre>
	 * @param value 銀行口座名（解決済み）
	 * @return 銀行口座名（解決済み）値オブジェクト「ResolvedBankAccountName」
	 *
	 */
	public static ResolvedBankAccountName from(String value) {
		return new ResolvedBankAccountName(value);
	}
}
