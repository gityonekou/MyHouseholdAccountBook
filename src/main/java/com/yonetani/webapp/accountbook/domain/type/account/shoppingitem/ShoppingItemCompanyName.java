/**
 * 「商品の製造会社名」項目の値を表すドメインタイプです
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
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「商品の製造会社名」項目の値を表すドメインタイプです
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
public class ShoppingItemCompanyName {
	// 商品の製造会社名
	private final String value;
	
	/**
	 *<pre>
	 * 「商品の製造会社名」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param name 商品の製造会社名
	 * @return 「商品の製造会社名」項目ドメインタイプ
	 *
	 */
	public static ShoppingItemCompanyName from(String name) {
		return new ShoppingItemCompanyName(name);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
