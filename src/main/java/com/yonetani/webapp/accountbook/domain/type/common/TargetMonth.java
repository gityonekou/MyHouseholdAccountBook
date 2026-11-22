/**
 * 「月」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2023/10/15 : 1.00.00                      新規作成
 * 2026/09/09 : 1.01.00  feature-1.03-dev1   リファクタリング対応(月項目ロジックの集約)
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
 * @since 家計簿アプリ(1.00)
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
	 *<pre>
	 * 「月」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 *
	 * [ガード節]
	 * ・設定値が01～12月の間以外
	 *</pre>
	 * @param month 月
	 * @return 「月」項目ドメインタイプ
	 *
	 */
	public static TargetMonth from(int month) {
		// ガード節(設定値が01～12月の間以外)
		try {
			if(month < 1 || month > 12) {
				throw new MyHouseholdAccountBookRuntimeException("「月」項目の設定値が不正です。管理者に問い合わせてください。[month=" + month + "]");
			}
		} catch(NumberFormatException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「月」項目の設定値が不正です。管理者に問い合わせてください。[month=" + month + "]", ex);
		}
		return new TargetMonth(String.format("%02d", month));
	}
	
	/**
	 *<pre>
	 * この月に指定した月数を加算した TargetMonth を返します。
	 * 年をまたぐ加算の場合でも、月の値のみを1～12の範囲でラップして返します(年の繰り上げは行いません)。
	 * monthsに負数を指定した場合は減算として扱います。
	 *</pre>
	 * @param months 加算する月数（負数の場合は減算）
	 * @return この月に指定した月数を加算した TargetMonth
	 *
	 */
	public TargetMonth plus(int months) {
		// 1〜12の範囲に収める(Math.floorModで負数・大きな月数でも正しくラップさせる)
		int newMonth = Math.floorMod(intValue() - 1 + months, 12) + 1;
		return TargetMonth.from(newMonth);
	}

	/**
	 *<pre>
	 * この月から指定した月数を減算した TargetMonth を返します。
	 * 年をまたぐ減算の場合でも、月の値のみを1～12の範囲でラップして返します(年の繰り下げは行いません)。
	 * monthsに負数を指定した場合は加算として扱います。
	 *</pre>
	 * @param months 減算する月数（負数の場合は加算）
	 * @return この月から指定した月数を減算した TargetMonth
	 *
	 */
	public TargetMonth minus(int months) {
		return plus(-months);
	}
	
	/**
	 *<pre>
	 * "MM月" 形式のフォーマット値を返します。（月の先頭0あり）。
	 * 例: 01月、11月
	 *</pre>
	 * @return MM月 形式のフォーマット値
	 *
	 */
	public String toFormatString() {
		return value + "月";
	}
	
	/**
	 *<pre>
	 * 「月」項目の値を整数に変換して返します。
	 *</pre>
	 * @return 月の値の整数値（1～12）
	 *
	 */
	public int intValue() {
		return Integer.parseInt(value);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
