/**
 * 「支出項目レベル」項目の値を表すドメインタイプです
 *	数値の１～５までの値が格納されます
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/07 : 1.00.00  新規作成
 * 2026/03/15 : 1.01.00  クラス名をSisyutuItemLevelからExpenditureItemLevelにリネーム
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「支出項目レベル」項目の値を表すドメインタイプです
 * 	数値の１～５までの値が格納されます
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
public class ExpenditureItemLevel {

	// 支出項目レベル(1～5)
	private final int value;

	/**
	 *<pre>
	 *  「支出項目レベル」項目の値を表すドメインタイプを生成します
	 *</pre>
	 * @param expenditureItemLevel 支出項目レベル
	 * @return 「支出項目レベル」項目ドメインタイプ
	 *
	 */
	public static ExpenditureItemLevel from(String expenditureItemLevel) {
		return new ExpenditureItemLevel(Integer.parseInt(expenditureItemLevel));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
