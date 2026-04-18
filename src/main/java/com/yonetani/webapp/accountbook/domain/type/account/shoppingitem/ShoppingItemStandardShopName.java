/**
 * 「基準店舗名」項目の値を表すドメインタイプです。
 * 基準店舗は必須入力ではないので、NULL可の項目となります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2025/04/27 : 2.00.00(B)  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingitem;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「基準店舗名」項目の値を表すドメインタイプです。
 * 基準店舗は必須入力ではないので、NULL可の項目となります。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(2.00.B)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class ShoppingItemStandardShopName {
	// 基準店舗名
	private final String value;
	
	/**
	 *<pre>
	 * 「基準店舗名」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param name 基準店舗名
	 * @return 「基準店舗名」項目ドメインタイプ
	 *
	 */
	public static ShoppingItemStandardShopName from(String name) {
		return new ShoppingItemStandardShopName(name);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
