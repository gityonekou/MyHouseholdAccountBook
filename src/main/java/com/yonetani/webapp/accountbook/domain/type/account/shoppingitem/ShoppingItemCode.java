/**
 * 「商品コード」項目の値を表すドメインタイプです
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
 * 「商品コード」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class ShoppingItemCode {
	// 商品コード
	private final String value;
	
	/**
	 *<pre>
	 * 「商品コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param code 商品コード
	 * @return 「商品コード」項目ドメインタイプ
	 *
	 */
	public static ShoppingItemCode from(String code) {
		return new ShoppingItemCode(code);
	}
	
	/**
	 *<pre>
	 * 新規発番する商品コードの値(数値)をもとに、「商品コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param count 新規発番する商品コードの値(数値)
	 * @return 「商品コード」項目ドメインタイプ
	 *
	 */
	public static ShoppingItemCode from(int count) {
		return new ShoppingItemCode(String.format("%05d", count));
	}
	
	/**
	 *<pre>
	 * 新規発番する商品コードの値を取得します。
	 *</pre>
	 * @param count 新規発番する商品コードの値(数値)
	 * @return 商品コードの値
	 *
	 */
	public static String getNewCode(int count) {
		return ShoppingItemCode.from(count).toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
