/**
 * 「店舗表示順」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/28 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shop;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「店舗表示順」項目の値を表すドメインタイプです
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
public class ShopSort {
	// 店舗区分コード
	private final String value;
	
	/**
	 *<pre>
	 * 「店舗表示順」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・空文字列
	 * ・長さが3桁でない
	 * ・数値に変換できない(数値3桁:0パディング)
	 *</pre>
	 * @param sort 店舗表示順
	 * @return 「店舗表示順」項目ドメインタイプ
	 *
	 */
	public static ShopSort from(String sort) {
		// ガード節(空文字列)
		if(!StringUtils.hasLength(sort)) {
			throw new MyHouseholdAccountBookRuntimeException("「店舗表示順」項目の設定値が空文字列です。管理者に問い合わせてください。");
		}
		// ガード節(長さが3桁でない)
		if(sort.length() != 3) {
			throw new MyHouseholdAccountBookRuntimeException("「店舗表示順」項目の設定値が不正です。管理者に問い合わせてください。[sort=" + sort + "]");
		}
		// ガード節(数値に変換できない(数値3桁:0パディング))
		try {
			Integer.parseInt(sort);
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「店舗表示順」項目の設定値が不正です。管理者に問い合わせてください。[sort=" + sort + "]");
		}
		return new ShopSort(sort);
	}
	
	/**
	 *<pre>
	 * 指定の表示順(数値)に対応する、「店舗表示順」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param count 表示順の値(数値)
	 * @return 「店舗表示順」項目ドメインタイプ
	 *
	 */
	public static ShopSort from(int sort) {
		return new ShopSort(String.format("%03d", sort));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
