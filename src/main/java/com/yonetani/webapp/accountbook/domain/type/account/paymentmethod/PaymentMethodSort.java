/**
 * 「支払方法表示順」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/18 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.paymentmethod;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.SortOrder;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「支払方法表示順」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class PaymentMethodSort extends SortOrder {

	/**
	 *<pre>
	 * コンストラクタ（privateでファクトリメソッド経由のみ生成可能）
	 *</pre>
	 * @param value 支払方法表示順
	 *
	 */
	private PaymentMethodSort(String value) {
		super(value);
	}

	/**
	 *<pre>
	 * 「支払方法表示順」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・空文字列
	 * ・長さが3桁でない
	 * ・数値に変換できない(数値3桁:0パディング)
	 *</pre>
	 * @param sort 支払方法表示順
	 * @return 「支払方法表示順」項目ドメインタイプ
	 *
	 */
	public static PaymentMethodSort from(String sort) {

		SortOrder.validate(sort, "支払方法表示順");

		if(sort.length() != 3) {
			throw new MyHouseholdAccountBookRuntimeException("「支払方法表示順」項目の設定値が不正です。管理者に問い合わせてください。[sort=" + sort + "]");
		}
		try {
			Integer.parseInt(sort);
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「支払方法表示順」項目の設定値が不正です。管理者に問い合わせてください。[sort=" + sort + "]");
		}

		return new PaymentMethodSort(sort);
	}

	/**
	 *<pre>
	 * 指定の表示順(数値)に対応する、「支払方法表示順」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param sort 表示順の値(数値)
	 * @return 「支払方法表示順」項目ドメインタイプ
	 *
	 */
	public static PaymentMethodSort from(int sort) {
		return new PaymentMethodSort(String.format("%03d", sort));
	}
}
