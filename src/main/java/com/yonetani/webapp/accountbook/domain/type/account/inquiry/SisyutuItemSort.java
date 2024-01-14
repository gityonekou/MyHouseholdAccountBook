/**
 * 「支出項目表示順」項目の値を表すドメインタイプです
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
 * 「支出項目表示順」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class SisyutuItemSort {

	// 支出項目表示順
	private final String value;
	
	/**
	 *<pre>
	 * 「支出項目表示順」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param sisyutuItemSort 支出項目表示順
	 * @return 「支出項目表示順」項目ドメインタイプ
	 *
	 */
	public static SisyutuItemSort from(String sisyutuItemSort) {
		return new SisyutuItemSort(sisyutuItemSort);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
	
}
