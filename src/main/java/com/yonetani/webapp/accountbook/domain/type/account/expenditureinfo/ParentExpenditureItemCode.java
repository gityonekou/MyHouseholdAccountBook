/**
 * 「親支出項目コード」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/03 : 1.00.00  新規作成
 * 2026/03/15 : 1.01.00  クラス名をParentSisyutuItemCodeからParentExpenditureItemCodeにリネーム
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.Identifier;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「親支出項目コード」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class ParentExpenditureItemCode extends Identifier {
	
	/**
	 *<pre>
	 * コンストラクタ（privateでファクトリメソッド経由のみ生成可能）
	 *</pre>
	 * @param value 親支出項目コード
	 *
	 */
	private ParentExpenditureItemCode(String value) {
		super(value);
	}
	
	/**
	 *<pre>
	 * 「親支出項目コード」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・空文字列
	 * ・長さが4桁でない
	 * ・数値に変換できない(数値4桁:0パディング)
	 *</pre>
	 * @param code 親支出項目コード
	 * @return 「親支出項目コード」項目ドメインタイプ
	 *
	 */
	public static ParentExpenditureItemCode from(String code) {
		// 基本検証（null、空文字）
		Identifier.validate(code, "親支出項目コード");
		
		// ガード節(長さが4桁でない)
		if(code.length() != 4) {
			throw new MyHouseholdAccountBookRuntimeException("「親支出項目コード」項目の設定値が不正です。管理者に問い合わせてください。[parentExpenditureItemCode=" + code + "]");
		}
		// ガード節(数値に変換できない(数値4桁:0パディング))
		try {
			Integer.parseInt(code);
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「親支出項目コード」項目の設定値が不正です。管理者に問い合わせてください。[parentExpenditureItemCode=" + code + "]");
		}

		return new ParentExpenditureItemCode(code);
	}
}
