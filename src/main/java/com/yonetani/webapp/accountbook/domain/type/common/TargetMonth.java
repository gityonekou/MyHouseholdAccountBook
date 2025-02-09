/**
 * 「月」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/15 : 1.00.00  新規作成
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
 * 「月」項目の値を表すドメインタイプです。
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
public class TargetMonth {	
	// 月(MM)
	private final String value;
	
	/**
	 *<pre>
	 * 「月」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・空文字列
	 * ・長さが2桁以外
	 * ・設定値が01～12月の間以外
	 *</pre>
	 * @param month 月
	 * @return 「月」項目ドメインタイプ
	 *
	 */
	public static TargetMonth from(String month) {
		// ガード節(空文字列)
		if(!StringUtils.hasLength(month)) {
			throw new MyHouseholdAccountBookRuntimeException("「月」項目の設定値が空文字列です。管理者に問い合わせてください。");
		}
		// ガード節(長さが2桁以外)
		if(month.length() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「月」項目の設定値が不正です。管理者に問い合わせてください。[month=" + month + "]");
		}
		// ガード節(設定値が01～12月の間以外)
		try {
			int monthValue = Integer.parseInt(month);
			if(monthValue < 1 || monthValue > 12) {
				throw new MyHouseholdAccountBookRuntimeException("「月」項目の設定値が不正です。管理者に問い合わせてください。[month=" + month + "]");
			}
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「月」項目の設定値が不正です。管理者に問い合わせてください。[month=" + month + "]", ex);
		}
		return new TargetMonth(month);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
