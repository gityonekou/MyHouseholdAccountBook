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
	// 円
	private static final String YEN = "円";
	
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
		// 値がnullの場合空文字列を返却、null以外の場合はスケール0で四捨五入+カンマ編集した文字列を返却
		return (value == null) ? "" : kingakuDecimalFormat.format(value.setScale(0, RoundingMode.HALF_UP));
	}
	
	/**
	 *<pre>
	 * 金額の値をスケール0で四捨五入+カンマ編集した文字列を返却します。値の最後には円を付与します。
	 * nullの場合は空文字列が返却されます。
	 *</pre>
	 * @param value フォーマット編集する金額の値
	 * @return フォーマット編集した金額の値(円を付与)、nullの場合は空文字列
	 *
	 */
	public static synchronized String formatKingakuAndYen(BigDecimal value) {
		// 値がnullの場合空文字列を返却、null以外の場合はスケール0で四捨五入+カンマ編集し、最後に円を付与した文字列を返却
		return (value == null) ? "" : kingakuDecimalFormat.format(value.setScale(0, RoundingMode.HALF_UP)) + YEN;
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
	public static String formatyyyySPMMSPdd(LocalDate date) {
		// 値がnullの場合は空文字列を返却、null以外の場合はYYYY/MM/DD形式に変換した値を返却
		return (date == null) ? "" : date.format(yyyySPMMSPddformat);
	}
	
	/**
	 *<pre>
	 * 指定されたLocalDateの日付の値を0パディングした文字列で返します。
	 *</pre>
	 * @param date 取得対象の日付
	 * @return 0パディング編集した日付の値。nullの場合は空文字列
	 *
	 */
	public static String getDateStr(LocalDate date) {
		if(date == null) {
			return "";
		} else {
			return String.format("%02d", date.getDayOfMonth());
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
	
	/**
	 *<pre>
	 * 引数で指定した整数値をBigDecimalに変換して返します。値がnullの場合、nullを返却します。
	 * 指定したスケールで小数点以下切り上げを実施します。
	 *</pre>
	 * @param value BigDecimalに変換する整数値
	 * @param scale 小数点以下桁数
	 * @return 引数の値をBigDecimalに変換した値
	 *
	 */
	public static BigDecimal convertBigDecimal(Integer value, int scale) {
		// 値がnullの場合、nullを返します。
		if(value == null) {
			return null;
		}
		// 整数の値に指定したスケール分0を追加
		StringBuilder numBuff = new StringBuilder(value.toString());
		if(scale > 0) {
			numBuff.append(".");
			for(int i = 0; i < scale; i++) {
				numBuff.append("0");
			}
		}
		
		// 整数の値を文字列変換
		BigDecimal bigVal = new BigDecimal(numBuff.toString());
		// スケールを設定(小数点以下切り上げ)
		bigVal.setScale(scale, RoundingMode.HALF_UP);
		
		return bigVal;
	}
	
	/**
	 *<pre>
	 * 引数で指定したBigDecimalの値をIntegerに変換して返します。値がnullの場合、nullを返却します。
	 * 小数点以下は切り捨てて、整数値に変換した値を返します。
	 * 
	 *</pre>
	 * @param value Integerに変換する値
	 * @return 引数の値をIntegerに変換した値
	 *
	 */
	public static Integer convertInteger(BigDecimal value) {
		// 値がnullの場合、nullを返します。
		if(value == null) {
			return null;
		}
		// スケールを0に設定、小数点以下は切り捨て
		value = value.setScale(0, RoundingMode.HALF_DOWN);
		
		// 整数値に変換して返却
		return Integer.valueOf(value.intValueExact());
	}
}
