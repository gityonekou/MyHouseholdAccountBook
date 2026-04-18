/**
 * 「支出コード」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/08 : 1.00.00  新規作成
 * 2026/03/15 : 1.01.00  クラス名をSisyutuCodeからExpenditureCodeにリネーム
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.expenditure;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.Identifier;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「支出コード」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class ExpenditureCode extends Identifier {
	
	/**
	 *<pre>
	 * コンストラクタ（privateでファクトリメソッド経由のみ生成可能）
	 *</pre>
	 * @param value 支出コード
	 *
	 */
	private ExpenditureCode(String value) {
		super(value);
	}
	
	/**
	 *<pre>
	 * 「支出コード」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・空文字列
	 * ・長さが3桁でない
	 * ・数値に変換できない(数値3桁:0パディング)
	 *
	 *
	 *</pre>
	 * @param code 支出コード
	 * @return 「支出コード」項目ドメインタイプ
	 *
	 */
	public static ExpenditureCode from(String code) {
		
		// 基本検証（null、空文字）
		Identifier.validate(code, "支出コード");
		
		// ガード節(長さが3桁でない)
		if(code.length() != 3) {
			throw new MyHouseholdAccountBookRuntimeException("「支出コード」項目の設定値が不正です。管理者に問い合わせてください。[expenditureCode=" + code + "]");
		}
		// ガード節(数値に変換できない(数値3桁:0パディング))
		try {
			Integer.parseInt(code);
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「支出コード」項目の設定値が不正です。管理者に問い合わせてください。[expenditureCode=" + code + "]");
		}

		return new ExpenditureCode(code);
	}

	/**
	 *<pre>
	 * 新規発番する支出コードの値(数値)をもとに、「支出コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param count 新規発番する支出コードの値(数値)
	 * @return 「支出コード」項目ドメインタイプ
	 *
	 */
	public static ExpenditureCode from(int count) {
		return ExpenditureCode.from(String.format("%03d", count));
	}

	/**
	 *<pre>
	 * 新規発番する支出コードの値を取得します。
	 *</pre>
	 * @param count 新規発番する支出コードの値(数値)
	 * @return 支出コードの値
	 *
	 */
	public static String getNewCode(int count) {
		return ExpenditureCode.from(count).getValue();
	}
}
