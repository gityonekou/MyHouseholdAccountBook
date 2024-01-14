/**
 * 「親支出項目コード」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「親支出項目コード」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class ParentSisyutuItemCode {
	
	// 親支出項目コード
	private final String value;
	
	/**
	 *<pre>
	 * 「親支出項目コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param code 親支出項目コード
	 * @return 「親支出項目コード」項目ドメインタイプ
	 *
	 */
	public static ParentSisyutuItemCode from(String code) {
		return new ParentSisyutuItemCode(code);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
