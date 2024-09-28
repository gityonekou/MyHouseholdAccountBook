/**
 * 「商品JANコード」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/06 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingitem;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「商品JANコード」項目の値を表すドメインタイプです
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
public class ShoppingItemJanCode {
	// 商品JANコード
	private final String value;
	
	/**
	 *<pre>
	 * 「商品JANコード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param code 商品JANコード
	 * @return 「商品JANコード」項目ドメインタイプ
	 *
	 */
	public static ShoppingItemJanCode from(String code) {
		return new ShoppingItemJanCode(code);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
