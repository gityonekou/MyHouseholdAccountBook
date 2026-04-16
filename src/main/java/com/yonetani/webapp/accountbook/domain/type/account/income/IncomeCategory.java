/**
 * 「収入区分」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/08 : 1.00.00  新規作成
 * 2026/03/15 : 1.01.00  クラス名をSyuunyuuKubunからIncomeCategoryにリネーム
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.income;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「収入区分」項目の値を表すドメインタイプです
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
public class IncomeCategory {

	// 収入区分
	private final String value;

	/**
	 *<pre>
	 * 「収入区分」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param category 収入区分
	 * @return 「収入区分」項目ドメインタイプ
	 *
	 */
	public static IncomeCategory from(String category) {
		return new IncomeCategory(category);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
