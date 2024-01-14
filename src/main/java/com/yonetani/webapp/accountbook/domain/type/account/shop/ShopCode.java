/**
 * 「店舗コード」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shop;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「店舗コード」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class ShopCode {
	// 店舗コード
	private final String value;
	
	/**
	 *<pre>
	 * 「店舗コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param code 店舗コード
	 * @return 「店舗コード」項目ドメインタイプ
	 *
	 */
	public static ShopCode from(String code) {
		return new ShopCode(code);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
