/**
 * 「支出コード」項目の値を表すドメインタイプです
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
 * 「支出コード」項目の値を表すドメインタイプです
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
public class SisyutuCode {
	
	// 支出コード
	private final String value;
	
	/**
	 *<pre>
	 * 「支出コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param code 支出コード
	 * @return 「支出コード」項目ドメインタイプ
	 *
	 */
	public static SisyutuCode from(String code) {
		return new SisyutuCode(code);
	}
	
	/**
	 *<pre>
	 * 新規発番する支出コードの値(数値)をもとに、「支出コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param count 新規発番する支出コードの値(数値)
	 * @return 「支出コード」項目ドメインタイプ
	 *
	 */
	public static SisyutuCode from(int count) {
		return new SisyutuCode(String.format("%03d", count));
	}
	
	/**
	 *<pre>
	 * 新規発番する支出コードの値を取得します。
	 *</pre>
	 * @param count 新規発番する支出コードの値(数値)
	 * @return 支出コードの値
	 *
	 */
	public static String getNewCode(int count) {
		return SisyutuCode.from(count).getValue();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
