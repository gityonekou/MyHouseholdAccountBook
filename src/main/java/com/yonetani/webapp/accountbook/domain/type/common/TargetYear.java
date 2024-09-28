/**
 * 「年」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/12 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「年」項目の値を表すドメインタイプです。
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
public class TargetYear {
	
	// 年(yyyy)
	private final String value;
	
	/**
	 *<pre>
	 *「年」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・空文字列
	 * ・長さが4桁以外
	 * ・設定値が1900～2200年の間以外
	 *</pre>
	 * @param year 年
	 * @return 「年」項目ドメインタイプ
	 *
	 */
	public static TargetYear from(String year) {
		// ガード節(空文字列 or 長さが4桁以外)
		if(!StringUtils.hasLength(year) || year.length() != 4) {
			throw new MyHouseholdAccountBookRuntimeException("「年」項目の設定値が不正です。管理者に問い合わせてください。[year=" + year + "]");
		}
		// ガード節(設定値が1900～2200年の間以外)
		try {
			int yearValue = Integer.parseInt(year);
			if(yearValue < 1900 || yearValue > 2200) {
				throw new MyHouseholdAccountBookRuntimeException("「年」項目の設定値が不正です。管理者に問い合わせてください。[year=" + year + "]");
			}
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「年」項目の設定値が不正です。管理者に問い合わせてください。[year=" + year + "]", ex);
		}
		
		// 「年」項目ドメインタイプを返却
		return new TargetYear(year);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
