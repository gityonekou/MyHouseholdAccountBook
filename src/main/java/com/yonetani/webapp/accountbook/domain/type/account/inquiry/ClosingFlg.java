/**
 * 「支払い済みフラグ」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「支払い済みフラグ」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class ClosingFlg {
	// 支払い済みフラグ
	private final boolean value;
	
	/**
	 *<pre>
	 * 「支払い済みフラグ」項目の値を表すドメインタイプを生成します
	 *</pre>
	 * @param closingFlg 支払い済みフラグ
	 * @return 「支払い済みフラグ」項目ドメインタイプ
	 *
	 */
	public static ClosingFlg from(boolean closingFlg) {
		return new ClosingFlg(closingFlg);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Boolean.toString(value);
	}
	
	/**
	 *<pre>
	 * 支払い済みフラグの値を取得します。
	 *</pre>
	 * @return 支払い済みフラグ
	 *
	 */
	public boolean getValue() {
		return value;
	}
}
