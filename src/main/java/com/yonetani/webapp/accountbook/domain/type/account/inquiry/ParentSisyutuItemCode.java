/**
 * 「親支出項目コード」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/03 : 1.00.00  新規作成
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
 * 「親支出項目コード」項目の値を表すドメインタイプです
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
public class ParentSisyutuItemCode {
	
	// 親支出項目コード
	private final String value;
	
	/**
	 *<pre>
	 * 「親支出項目コード」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・空文字列
	 * ・長さが4桁でない
	 * ・数値に変換できない(数値4桁:0パディング)
	 *</pre>
	 * @param code 親支出項目コード
	 * @return 「親支出項目コード」項目ドメインタイプ
	 *
	 */
	public static ParentSisyutuItemCode from(String code) {
		// ガード節(空文字列)
		if(!StringUtils.hasLength(code)) {
			throw new MyHouseholdAccountBookRuntimeException("「親支出項目コード」項目の設定値が空文字列です。管理者に問い合わせてください。");
		}
		// ガード節(長さが4桁でない)
		if(code.length() != 4) {
			throw new MyHouseholdAccountBookRuntimeException("「親支出項目コード」項目の設定値が不正です。管理者に問い合わせてください。[parentSisyutuItemCode=" + code + "]");
		}
		// ガード節(数値に変換できない(数値4桁:0パディング))
		try {
			Integer.parseInt(code);
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「親支出項目コード」項目の設定値が不正です。管理者に問い合わせてください。[parentSisyutuItemCode=" + code + "]");
		}
		
		return new ParentSisyutuItemCode(code);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
