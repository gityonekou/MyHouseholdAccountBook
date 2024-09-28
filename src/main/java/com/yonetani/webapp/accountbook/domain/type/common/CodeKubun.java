/**
 * 「コード区分」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/21 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「コード区分」項目の値を表すドメインタイプです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class CodeKubun {
	// コード区分
	private final String value;
	
	/**
	 *<pre>
	 * 「コード区分」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param codeKubun コード区分
	 * @return 「コード区分」項目の値を表すドメインタイプ
	 *
	 */
	public static CodeKubun from(String codeKubun) {
		return new CodeKubun(codeKubun);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
