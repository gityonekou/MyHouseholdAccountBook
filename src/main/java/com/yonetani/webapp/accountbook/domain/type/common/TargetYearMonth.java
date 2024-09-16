/**
 * 「年月」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/24 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「年月」項目の値を表すドメインタイプです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class TargetYearMonth {
	
	// 年月(yyyyMM)
	private final String value;
	
	/**
	 *<pre>
	 * 「年月」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param yearMonth 年月
	 * @return 「年月」項目ドメインタイプ
	 *
	 */
	public static TargetYearMonth from(String yearMonth){
		if(!StringUtils.hasLength(yearMonth) || yearMonth.length() != 6) {
			throw new MyHouseholdAccountBookRuntimeException("「年月」項目の値が不正です。管理者に問い合わせてください。[yearMonth=" + yearMonth + "]");
		}
		return new TargetYearMonth(yearMonth);
	}
	
	/**
	 *<pre>
	 * パッケージしている年月(YYYYMM)の値を文字列で返します。
	 *</pre>
	 * @return 年月(YYYYMM)の値
	 *
	 */
	public String getTargetYearMonth() {
		return value;
	}
	
	/**
	 *<pre>
	 * パッケージしている年(YYYY)の値を文字列で返します。
	 *</pre>
	 * @return 年(YYYY)の値
	 *
	 */
	public String getYear() {
		return value.substring(0, 4);
	}
	
	/**
	 *<pre>
	 * パッケージしている月(MM)の値を文字列で返します。
	 *</pre>
	 * @return getYear 月(MM)の値
	 *
	 */
	public String getMonth() {
		return value.substring(4);
	}
	
	@Override
	public String toString() {
		return value;
	}
	
}
