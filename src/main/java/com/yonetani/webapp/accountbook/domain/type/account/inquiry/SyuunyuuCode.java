/**
 * 「収入コード」項目の値を表すドメインタイプです
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
 * 「収入コード」項目の値を表すドメインタイプです
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
public class SyuunyuuCode {
	
	// 収入コード
	private final String value;
	
	/**
	 *<pre>
	 * 「収入コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param code 収入コード
	 * @return 「収入コード」項目ドメインタイプ
	 *
	 */
	public static SyuunyuuCode from(String code) {
		return new SyuunyuuCode(code);
	}
	
	/**
	 *<pre>
	 * 新規発番する収入コードの値(数値)をもとに、「収入コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param count 新規発番する収入コードの値(数値)
	 * @return 「収入コード」項目ドメインタイプ
	 *
	 */
	public static SyuunyuuCode from(int count) {
		return new SyuunyuuCode(String.format("%02d", count));
	}
	
	/**
	 *<pre>
	 * 新規発番する収入コードの値を取得します。
	 *</pre>
	 * @param count 新規発番する収入コードの値(数値)
	 * @return 収入コードの値
	 *
	 */
	public static String getNewCode(int count) {
		return SyuunyuuCode.from(count).getValue();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
