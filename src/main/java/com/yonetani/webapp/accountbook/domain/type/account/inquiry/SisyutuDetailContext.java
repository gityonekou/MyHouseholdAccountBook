/**
 * 「支出詳細」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/08 : 1.00.00  新規作成
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
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class SisyutuDetailContext {
	
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
	public static SisyutuDetailContext from(String context) {
		return new SisyutuDetailContext(context);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
