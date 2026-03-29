/**
 * 「支出項目詳細内容」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/03 : 1.00.00  新規作成
 * 2026/03/15 : 1.01.00  クラス名をSisyutuItemDetailContextからExpenditureItemDetailContextにリネーム
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「支出項目詳細内容」項目の値を表すドメインタイプです
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
public class ExpenditureItemDetailContext {

	// 支出項目詳細内容
	private final String value;

	/**
	 *<pre>
	 * 「支出項目詳細内容」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param expenditureItemDetailContext 支出項目詳細内容
	 * @return 「支出項目詳細内容」項目ドメインタイプ
	 *
	 */
	public static ExpenditureItemDetailContext from(String expenditureItemDetailContext) {
		return new ExpenditureItemDetailContext(expenditureItemDetailContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
