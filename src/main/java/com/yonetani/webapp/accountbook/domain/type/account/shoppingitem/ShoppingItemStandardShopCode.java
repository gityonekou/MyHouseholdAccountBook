/**
 * 「基準店舗コード」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingitem;

import org.springframework.util.StringUtils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「基準店舗コード」項目の値を表すドメインタイプです
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
public class ShoppingItemStandardShopCode {
	// 基準店舗コード
	private final String value;
	
	/**
	 *<pre>
	 * 「基準店舗コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param code 基準店舗コード
	 * @return 「基準店舗コード」項目ドメインタイプ
	 *
	 */
	public static ShoppingItemStandardShopCode from(String code) {
		// 基準店舗コード値が空文字列(未選択)の場合、nullを設定
		if(!StringUtils.hasLength(code)) {
			code = null;
		}
		return new ShoppingItemStandardShopCode(code);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
