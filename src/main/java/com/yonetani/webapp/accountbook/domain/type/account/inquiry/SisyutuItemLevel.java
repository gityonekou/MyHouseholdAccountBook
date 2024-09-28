/**
 * 「支出項目レベル」項目の値を表すドメインタイプです
 *	数値の１～５までの値が格納されます
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

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
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class SisyutuItemLevel {
	
	// 支出項目レベル(1～5)
	private final int value;
	
	/**
	 *<pre>
	 *  「支出項目レベル」項目の値を表すドメインタイプを生成します
	 *</pre>
	 * @param sisyutuItemLevel 支出項目レベル
	 * @return 「支出項目レベル」項目ドメインタイプ
	 *
	 */
	public static SisyutuItemLevel from(String sisyutuItemLevel) {
		return new SisyutuItemLevel(Integer.parseInt(sisyutuItemLevel));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
