/**
 * 「収入コード」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/08 : 1.00.00  新規作成
 * 2026/03/15 : 1.01.00  クラス名をSyuunyuuCodeからIncomeCodeにリネーム
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.income;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.Identifier;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「収入コード」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class IncomeCode extends Identifier {
	
	/**
	 *<pre>
	 * コンストラクタ（privateでファクトリメソッド経由のみ生成可能）
	 *</pre>
	 * @param value 収入コード
	 *
	 */
	private IncomeCode(String value) {
		super(value);
	}
	
	/**
	 *<pre>
	 * 「収入コード」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・空文字列
	 * ・長さが2桁でない
	 * ・数値に変換できない(数値2桁:0パディング)
	 *
	 *</pre>
	 * @param code 収入コード
	 * @return 「収入コード」項目ドメインタイプ
	 *
	 */
	public static IncomeCode from(String code) {
		// 基本検証（null、空文字）
		Identifier.validate(code, "収入コード");
		
		// ガード節(長さが2桁でない)
		if(code.length() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「収入コード」項目の設定値が不正です。管理者に問い合わせてください。[incomeCode=" + code + "]");
		}
		// ガード節(数値に変換できない(数値2桁:0パディング))
		try {
			Integer.parseInt(code);
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「収入コード」項目の設定値が不正です。管理者に問い合わせてください。[incomeCode=" + code + "]");
		}

		return new IncomeCode(code);
	}

	/**
	 *<pre>
	 * 新規発番する収入コードの値(数値)をもとに、「収入コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param count 新規発番する収入コードの値(数値)
	 * @return 「収入コード」項目ドメインタイプ
	 *
	 */
	public static IncomeCode from(int count) {
		return IncomeCode.from(String.format("%02d", count));
	}

	/**
	 *<pre>
	 * 新規発番する収入コードの値を取得します。
	 *</pre>
	 * @param count 新規発番する収入コードの値(数値)
	 * @return 収入コードの値
	 *
	 */
	public static String getNewCode(int count) {
		return IncomeCode.from(count).getValue();
	}
}
