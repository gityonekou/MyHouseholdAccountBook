/**
 * 「商品詳細」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingitem;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「商品詳細」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class ShoppingItemDetailContext {
	// 商品詳細
	private final String value;
	
	/**
	 *<pre>
	 * 「商品詳細」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param value 商品詳細
	 * @return 「商品詳細」項目ドメインタイプ
	 *
	 */
	public static ShoppingItemDetailContext from(String value) {
		return new ShoppingItemDetailContext(value);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
