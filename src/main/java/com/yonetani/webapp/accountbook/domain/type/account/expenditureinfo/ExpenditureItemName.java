/**
 *  「支出項目名」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/07 : 1.00.00  新規作成
 * 2026/03/15 : 1.01.00  クラス名をSisyutuItemNameからExpenditureItemNameにリネーム
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「支出項目名」項目の値を表すドメインタイプです
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
public class ExpenditureItemName {

	// 支出項目名
	private final String value;

	/**
	 *<pre>
	 * 「支出項目名」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param expenditureItemName 支出項目名
	 * @return 「支出項目名」項目ドメインタイプ
	 *
	 */
	public static ExpenditureItemName from(String expenditureItemName) {
		return new ExpenditureItemName(expenditureItemName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}

}
