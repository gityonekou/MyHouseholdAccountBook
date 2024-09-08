/**
 * 「支出区分」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/08 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「支出区分」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class SisyutuKubun {
	
	// 支出区分
	private final String value;
	
	/**
	 *<pre>
	 * 「支出区分」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param kubun 支出区分
	 * @return 「支出区分」項目ドメインタイプ
	 *
	 */
	public static SisyutuKubun from(String kubun) {
		return new SisyutuKubun(kubun);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
