/**
 * 「銀行口座コード」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/18 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.bankaccount;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.Identifier;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「銀行口座コード」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class BankAccountCode extends Identifier {

	/**
	 *<pre>
	 * コンストラクタ（privateでファクトリメソッド経由のみ生成可能）
	 *</pre>
	 * @param value 銀行口座コード
	 *
	 */
	private BankAccountCode(String value) {
		super(value);
	}

	/**
	 *<pre>
	 * 「銀行口座コード」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・空文字列
	 * ・長さが2桁でない
	 * ・数値に変換できない(数値2桁:0パディング)
	 *</pre>
	 * @param bankAccountCode 銀行口座コード
	 * @return 「銀行口座コード」項目ドメインタイプ
	 *
	 */
	public static BankAccountCode from(String bankAccountCode) {

		// 基本検証（null、空文字）
		Identifier.validate(bankAccountCode, "銀行口座コード");

		// ガード節(長さが2桁でない)
		if(bankAccountCode.length() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「銀行口座コード」項目の設定値が不正です。管理者に問い合わせてください。[bankAccountCode=" + bankAccountCode + "]");
		}
		// ガード節(数値に変換できない(数値2桁:0パディング))
		try {
			Integer.parseInt(bankAccountCode);
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「銀行口座コード」項目の設定値が不正です。管理者に問い合わせてください。[bankAccountCode=" + bankAccountCode + "]");
		}

		return new BankAccountCode(bankAccountCode);
	}

	/**
	 *<pre>
	 * 新規発番する銀行口座コードの値(数値)をもとに、「銀行口座コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param count 新規発番する銀行口座コードの値(数値)
	 * @return 「銀行口座コード」項目ドメインタイプ
	 *
	 */
	public static BankAccountCode from(int count) {
		return new BankAccountCode(String.format("%02d", count));
	}

	/**
	 *<pre>
	 * 新規発番する銀行口座コードの値を取得します。
	 *</pre>
	 * @param count 新規発番する銀行口座コードの値(数値)
	 * @return 銀行口座コードの値
	 *
	 */
	public static String getNewCode(int count) {
		return BankAccountCode.from(count).getValue();
	}
}
