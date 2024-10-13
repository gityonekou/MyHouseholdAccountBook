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

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

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
	 * 
	 * [ガード節]
	 * ・空文字列
	 * ・長さが4桁でない
	 * ・数値に変換できない(数値4桁:0パディング)
	 *</pre>
	 * @param sisyutuItemCode 支出項目コード
	 * @return 「支出項目コード」項目ドメインタイプ
	 *
	 */
	public static SisyutuItemCode from(String sisyutuItemCode) {
		// ガード節(空文字列)
		if(!StringUtils.hasLength(sisyutuItemCode)) {
			throw new MyHouseholdAccountBookRuntimeException("「支出項目コード」項目の設定値が空文字列です。管理者に問い合わせてください。");
		}
		// ガード節(長さが4桁でない)
		if(sisyutuItemCode.length() != 4) {
			throw new MyHouseholdAccountBookRuntimeException("「支出項目コード」項目の設定値が不正です。管理者に問い合わせてください。[sisyutuItemCode=" + sisyutuItemCode + "]");
		}
		// ガード節(数値に変換できない(数値4桁:0パディング))
		try {
			Integer.parseInt(sisyutuItemCode);
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「支出項目コード」項目の設定値が不正です。管理者に問い合わせてください。[sisyutuItemCode=" + sisyutuItemCode + "]");
		}
		
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
	 * 「親の支出項目コード」項目の値をもとに、「支出項目コード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param parentSisyutuItemCode 「親の支出項目コード」項目ドメインタイプ
	 * @return 「支出項目コード」項目ドメインタイプ
	 *
	 */
	public static SisyutuItemCode from(ParentSisyutuItemCode parentSisyutuItemCode) {
		return new SisyutuItemCode(parentSisyutuItemCode.getValue());
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
