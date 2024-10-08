/**
 * 「店舗区分コード」項目の値を表すドメインタイプです
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
 * 「店舗区分コード」項目の値を表すドメインタイプです
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
public class ShopKubunCode {
	// 店舗区分コード
	private final String value;
	
	/**
	 *<pre>
	 * 「店舗区分コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param code 店舗区分コード
	 * @return 「店舗区分コード」項目ドメインタイプ
	 *
	 */
	public static ShopKubunCode from(String code) {
		return new ShopKubunCode(code);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
