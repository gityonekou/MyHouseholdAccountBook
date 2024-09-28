/**
 *  「支出項目名」項目の値を表すドメインタイプです
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
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「支出項目名」項目の値を表すドメインタイプです
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
public class SisyutuItemName {

	// 支出項目名
	private final String value;
	
	/**
	 *<pre>
	 * 「支出項目名」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param SisyutuItemName 支出項目名
	 * @return 「支出項目名」項目ドメインタイプ
	 *
	 */
	public static SisyutuItemName from(String SisyutuItemName) {
		return new SisyutuItemName(SisyutuItemName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
	
}
