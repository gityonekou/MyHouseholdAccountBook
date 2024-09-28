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
import lombok.Getter;
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
@Getter
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
	 *<pre>
	 * 新規発番する店舗コードの値(数値)をもとに、「店舗コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param count 新規発番する店舗コードの値(数値)
	 * @return 「店舗コード」項目ドメインタイプ
	 *
	 */
	public static ShopCode from(int count) {
		return new ShopCode(String.format("%03d", count));
	}
	
	/**
	 *<pre>
	 * 新規発番する店舗コードの値を取得します。
	 *</pre>
	 * @param count 新規発番する店舗コードの値(数値)
	 * @return 店舗コードの値
	 *
	 */
	public static String getNewCode(int count) {
		return ShopCode.from(count).getValue();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
