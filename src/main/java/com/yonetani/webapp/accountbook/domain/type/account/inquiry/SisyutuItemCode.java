/**
 * 「支出項目コード」項目の値を表すドメインタイプです
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
 * 「支出項目コード」項目の値を表すドメインタイプです
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
public class SisyutuItemCode {

	// 支出項目コード
	private final String value;
	
	/**
	 *<pre>
	 * 「支出項目コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param sisyutuItemCode 支出項目コード
	 * @return 「支出項目コード」項目ドメインタイプ
	 *
	 */
	public static SisyutuItemCode from(String sisyutuItemCode) {
		return new SisyutuItemCode(sisyutuItemCode);
	}
	
	/**
	 *<pre>
	 * 新規発番する支出項目コードの値(数値)をもとに、「支出項目コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param count 新規発番する支出項目コードの値(数値)
	 * @return 「支出項目コード」項目ドメインタイプ
	 *
	 */
	public static SisyutuItemCode from(int count) {
		return new SisyutuItemCode(String.format("%04d", count));
	}
	
	/**
	 *<pre>
	 * 新規発番する支出項目コードの値を取得します。
	 *</pre>
	 * @param count 新規発番する支出項目コードの値(数値)
	 * @return 支出項目コードの値
	 *
	 */
	public static String getNewCode(int count) {
		return SisyutuItemCode.from(count).getValue();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
