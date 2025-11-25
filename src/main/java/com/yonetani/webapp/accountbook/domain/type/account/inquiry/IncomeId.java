/**
 * 「収入コード」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/25 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import com.yonetani.webapp.accountbook.domain.type.common.Identifier;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「収入コード」項目の値を表すドメインタイプです。
 *
 * [ビジネスルール]
 * ・収入コードは必須項目
 * ・空文字は許可されない
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class IncomeId extends Identifier {

	/**
	 *<pre>
	 * コンストラクタ（privateでファクトリメソッド経由のみ生成可能）
	 *</pre>
	 * @param value 収入コード
	 *
	 */
	private IncomeId(String value) {
		super(value);
	}

	/**
	 *<pre>
	 *「収入コード」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・null値
	 * ・空文字列
	 *</pre>
	 * @param incomeCode 収入コード
	 * @return 「収入コード」項目ドメインタイプ
	 *
	 */
	public static IncomeId from(String incomeCode) {
		// 基本検証（null、空文字）
		Identifier.validate(incomeCode, "収入コード");

		// 「収入コード」項目の値を生成して返却
		return new IncomeId(incomeCode);
	}
}
