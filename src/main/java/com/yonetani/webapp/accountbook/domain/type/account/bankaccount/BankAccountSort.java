/**
 * 「銀行口座表示順」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/18 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.bankaccount;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.SortOrder;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「銀行口座表示順」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class BankAccountSort extends SortOrder {

	/**
	 *<pre>
	 * コンストラクタ（privateでファクトリメソッド経由のみ生成可能）
	 *</pre>
	 * @param value 銀行口座表示順
	 *
	 */
	private BankAccountSort(String value) {
		super(value);
	}

	/**
	 *<pre>
	 * 「銀行口座表示順」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・空文字列
	 * ・長さが2桁でない
	 * ・数値に変換できない(数値2桁:0パディング)
	 *</pre>
	 * @param sort 銀行口座表示順
	 * @return 「銀行口座表示順」項目ドメインタイプ
	 *
	 */
	public static BankAccountSort from(String sort) {

		// 基本検証（null、空文字）
		SortOrder.validate(sort, "銀行口座表示順");

		// ガード節(長さが2桁でない)
		if(sort.length() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「銀行口座表示順」項目の設定値が不正です。管理者に問い合わせてください。[sort=" + sort + "]");
		}
		// ガード節(数値に変換できない(数値2桁:0パディング))
		try {
			Integer.parseInt(sort);
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「銀行口座表示順」項目の設定値が不正です。管理者に問い合わせてください。[sort=" + sort + "]");
		}

		return new BankAccountSort(sort);
	}

	/**
	 *<pre>
	 * 指定の表示順(数値)に対応する、「銀行口座表示順」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param sort 表示順の値(数値)
	 * @return 「銀行口座表示順」項目ドメインタイプ
	 *
	 */
	public static BankAccountSort from(int sort) {
		return new BankAccountSort(String.format("%02d", sort));
	}
}
