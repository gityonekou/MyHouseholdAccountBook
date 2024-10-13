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

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

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
	 * 
	 * [ガード節]
	 * ・空文字列
	 * ・長さが3桁でない
	 * ・数値に変換できない(数値4桁:0パディング)
	 * 
	 * 
	 *</pre>
	 * @param code 支出コード
	 * @return 「支出コード」項目ドメインタイプ
	 *
	 */
	public static SisyutuCode from(String code) {
		
		// ガード節(空文字列)
		if(!StringUtils.hasLength(code)) {
			throw new MyHouseholdAccountBookRuntimeException("「支出コード」項目の設定値が空文字列です。管理者に問い合わせてください。");
		}
		// ガード節(長さが3桁でない)
		if(code.length() != 3) {
			throw new MyHouseholdAccountBookRuntimeException("「支出コード」項目の設定値が不正です。管理者に問い合わせてください。[sisyutuCode=" + code + "]");
		}
		// ガード節(数値に変換できない(数値3桁:0パディング))
		try {
			Integer.parseInt(code);
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「支出コード」項目の設定値が不正です。管理者に問い合わせてください。[sisyutuCode=" + code + "]");
		}
		
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
