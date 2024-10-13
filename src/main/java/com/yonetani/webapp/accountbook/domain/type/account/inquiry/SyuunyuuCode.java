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

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

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
	 * 
	 * [ガード節]
	 * ・空文字列
	 * ・長さが2桁でない
	 * ・数値に変換できない(数値4桁:0パディング)
	 * 
	 *</pre>
	 * @param code 収入コード
	 * @return 「収入コード」項目ドメインタイプ
	 *
	 */
	public static SyuunyuuCode from(String code) {
		// ガード節(空文字列)
		if(!StringUtils.hasLength(code)) {
			throw new MyHouseholdAccountBookRuntimeException("「収入コード」項目の設定値が空文字列です。管理者に問い合わせてください。");
		}
		// ガード節(長さが2桁でない)
		if(code.length() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「収入コード」項目の設定値が不正です。管理者に問い合わせてください。[syuunyuuCode=" + code + "]");
		}
		// ガード節(数値に変換できない(数値2桁:0パディング))
		try {
			Integer.parseInt(code);
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「収入コード」項目の設定値が不正です。管理者に問い合わせてください。[syuunyuuCode=" + code + "]");
		}
		
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
