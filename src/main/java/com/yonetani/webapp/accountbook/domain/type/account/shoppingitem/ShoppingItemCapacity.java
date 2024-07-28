/**
 * 「商品内容量」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/07/28 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingitem;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「商品内容量」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class ShoppingItemCapacity {
	// 商品内容量
	@Getter
	private final Integer value;
	
	/**
	 *<pre>
	 * 「商品内容量」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param capacity 商品内容量
	 * @return 「商品内容量」項目ドメインタイプ
	 *
	 */
	public static ShoppingItemCapacity from(Integer capacity) {
		return new ShoppingItemCapacity(capacity);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
