/**
 * 「削除フラグ」項目の値を表すドメインタイプです
 * 値がtrueの時は論理削除済みデータ、falseの時は有効データを表す値となります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/10 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「削除フラグ」項目の値を表すドメインタイプです
 * 値がtrueの時は論理削除済みデータ、falseの時は有効データを表す値となります。
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
public class DeleteFlg {
	// 削除フラグ
	private final boolean value;
	
	/**
	 *<pre>
	 * 「削除フラグ」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param deleteFlg 値がtrueの時は論理削除済みデータ、falseの時は有効データ
	 * @return 「削除フラグ」項目ドメインタイプ
	 *
	 */
	public static DeleteFlg from(boolean deleteFlg) {
		return new DeleteFlg(deleteFlg);
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
	 * 「削除フラグ」項目の値を取得します。
	 *</pre>
	 * @return 削除フラグ
	 *
	 */
	public boolean getValue() {
		return value;
	}
}
