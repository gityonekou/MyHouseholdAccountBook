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

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
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
@Getter
@EqualsAndHashCode
public class TargetYearMonth {
	
	// 年月(yyyyMM)
	private final String value;
	// 「年」項目の値
	private final TargetYear year;
	// 「月」項目の値
	private final TargetMonth month;
	
	/**
	 *<pre>
	 * 「年月」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・空文字列
	 * ・長さが6桁以外
	 * ・年項目、月項目に変換できない
	 * 
	 *</pre>
	 * @param yearMonth 年月
	 * @return 「年月」項目ドメインタイプ
	 *
	 */
	public static TargetYearMonth from(String yearMonth){
		// ガード節(空文字列 or 長さが6桁以外)
		if(!StringUtils.hasLength(yearMonth) || yearMonth.length() != 6) {
			throw new MyHouseholdAccountBookRuntimeException("「年月」項目の値が不正です。管理者に問い合わせてください。[yearMonth=" + yearMonth + "]");
		}
		// カレンダーの日付として有効かどうか
		try {
			LocalDate.parse(yearMonth + "01", MyHouseholdAccountBookContent.STRICT_DATE_TIME_FORMATTER);
		} catch (DateTimeParseException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「年月」項目の設定値が不正です。管理者に問い合わせてください。[yearMonth=" + yearMonth + "]");
		}
		// 年項目
		TargetYear yearIns = TargetYear.from(yearMonth.substring(0, 4));
		// 月項目
		TargetMonth monthIns = TargetMonth.from(yearMonth.substring(4));
		// 「年月」項目ドメインタイプを返却
		return new TargetYearMonth(yearMonth, yearIns, monthIns);
	}
	
	/**
	 *<pre>
	 * パッケージしている年(YYYY)の値を文字列で返します。
	 *</pre>
	 * @return 年(YYYY)の値
	 *
	 */
	public String getYear() {
		return year.getValue();
	}
	
	/**
	 *<pre>
	 * パッケージしている月(MM)の値を文字列で返します。
	 *</pre>
	 * @return getYear 月(MM)の値
	 *
	 */
	public String getMonth() {
		return month.getValue();
	}
	
	@Override
	public String toString() {
		return value;
	}
	
}
