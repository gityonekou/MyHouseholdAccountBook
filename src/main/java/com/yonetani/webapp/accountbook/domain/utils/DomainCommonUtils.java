/**
 * ドメイン層で使用する各種ユーティリティクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/22 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *<pre>
 * ドメイン層で使用する各種ユーティリティクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public class DomainCommonUtils {
	
	// 金額のカンマ区切りフォーマッター
	private static final DecimalFormat kingakuDecimalFormat = new DecimalFormat("#,###");
	// 日付フォーマット(YYYY/MM/DD)
	private static final DateTimeFormatter yyyySPMMSPddformat = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	
	/**
	 *<pre>
	 * 金額の値をスケール0で四捨五入+カンマ編集した文字列を返却します。
	 * nullの場合は空文字列が返却されます。
	 *</pre>
	 * @param value フォーマット編集する金額の値
	 * @return フォーマット編集した金額の値
	 *
	 */
	public static synchronized String formatKingaku(BigDecimal value) {
		// null値の場合、空文字列を返却
		if(value == null) {
			return "";
		}
		// スケール0で四捨五入+カンマ編集した文字列を返却
		return kingakuDecimalFormat.format(value.setScale(0, RoundingMode.HALF_UP));
	}
	
	/**
	 *<pre>
	 * 日付の値を「yyyy/MM/dd」フォーマット編集した文字列の値を返却します。
	 * nullの場合は空文字列が返却されます。
	 *</pre>
	 * @param date フォーマット編集する日付の値
	 * @return フォーマット編集した日付の値
	 *
	 */
	public static synchronized String formatyyyySPMMSPdd(LocalDate date) {
		// YYYY/MM/DD形式で返却
		if(date == null) {
			return "";
		} else {
			return date.format(yyyySPMMSPddformat);
		}
	}
	
	/**
	 *<pre>
	 * BigDecimalのaddメソッドのnullセーフです。
	 *</pre>
	 * @param target 左辺の値
	 * @param augend 右辺(addする)の値
	 * @return target+augendの値
	 *
	 */
	public static BigDecimal addBigDecimalNullSafe(BigDecimal target, BigDecimal augend) {
		if(target == null) {
			return null;
		} else if(augend == null) {
			return target;
		} else {
			return target.add(augend);
		}
		
	}
}
