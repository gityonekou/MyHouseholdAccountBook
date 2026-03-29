/**
 * 「支出詳細」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/08 : 1.00.00  新規作成
 * 2026/03/15 : 1.01.00  クラス名をSisyutuDetailContextからExpenditureDetailContextにリネーム
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「支出詳細」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class ExpenditureDetailContext {

	// 支出詳細
	private final String value;

	/**
	 *<pre>
	 * 「支出詳細」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param context 支出詳細
	 * @return 「支出詳細」項目ドメインタイプ
	 *
	 */
	public static ExpenditureDetailContext from(String context) {
		return new ExpenditureDetailContext(context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
