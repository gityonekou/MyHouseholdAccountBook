/**
 * 「更新可否フラグ」項目の値を表すドメインタイプです
 * 値がtrueの時は更新可能、falseの時は更新不可を表す値となります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/02/12 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「更新可否フラグ」項目の値を表すドメインタイプです
 * 値がtrueの時は更新可能、falseの時は更新不可を表す値となります。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class EnableUpdateFlg {
	private final boolean value;
	
	/**
	 *<pre>
	 * 「更新可否フラグ」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param enableUpdateFlg 更新可否フラグ trueの時は更新可能、falseの時は更新不可
	 * @return 「更新可否フラグ」項目ドメインタイプ
	 *
	 */
	public static EnableUpdateFlg from(boolean enableUpdateFlg) {
		return new EnableUpdateFlg(enableUpdateFlg);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Boolean.toString(value);
	}
	
	/**
	 *<pre>
	 * 「更新可否フラグ」項目の値を取得します。
	 *</pre>
	 * @return 更新可否フラグ
	 *
	 */
	public boolean getValue() {
		return value;
	}
}
