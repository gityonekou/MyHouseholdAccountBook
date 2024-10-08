/**
 * 「店舗表示順」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/28 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shop;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「店舗表示順」項目の値を表すドメインタイプです
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
public class ShopSort {
	// 店舗区分コード
	private final String value;
	
	/**
	 *<pre>
	 * 「店舗表示順」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param sort 店舗表示順
	 * @return 「店舗表示順」項目ドメインタイプ
	 *
	 */
	public static ShopSort from(String sort) {
		return new ShopSort(sort);
	}
	
	/**
	 *<pre>
	 * 指定の表示順(数値)に対応する、「店舗表示順」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param count 表示順の値(数値)
	 * @return 「店舗表示順」項目ドメインタイプ
	 *
	 */
	public static ShopSort from(int sort) {
		return new ShopSort(String.format("%03d", sort));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
