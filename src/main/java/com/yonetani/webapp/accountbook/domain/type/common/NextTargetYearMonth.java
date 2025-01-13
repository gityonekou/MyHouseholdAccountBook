/**
 * 対象年月の次の月の「年月」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/01/13 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 対象年月の次の月の「年月」項目の値を表すドメインタイプです。
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
public class NextTargetYearMonth {
	// 年月(yyyyMM)
	private final String value;
	// 「年」項目の値
	private final TargetYear year;
	// 「月」項目の値
	private final TargetMonth month;
	
	/**
	 *<pre>
	 * 対象年月の次の月の「年月」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・空文字列
	 * ・長さが6桁以外
	 * ・年項目、月項目に変換できない
	 * 
	 *</pre>
	 * @param yearMonth 年月
	 * @return 対象年月の次の月の「年月」項目ドメインタイプ
	 *
	 */
	public static NextTargetYearMonth from(TargetYearMonth yearMonth){
		// 現在の対象年月からカレンダーを生成(前月・翌月の値取得用)
		LocalDate yearMonthCalendar = LocalDate.parse(yearMonth.getValue() + "01", MyHouseholdAccountBookContent.DATE_TIME_FORMATTER);
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMM");
		// 翌月
		String valueIns = yearMonthCalendar.plusMonths(1).format(format);
		
		// 年項目
		TargetYear yearIns = TargetYear.from(valueIns.substring(0, 4));
		// 月項目
		TargetMonth monthIns = TargetMonth.from(valueIns.substring(4));
		// 対象年月の次の月の「年月」項目ドメインタイプを返却
		return new NextTargetYearMonth(valueIns, yearIns, monthIns);
	}
	
	/**
	 *<pre>
	 * パッケージしている年(YYYY)の値を文字列で返します。
	 *</pre>
	 * @return 年(YYYY)の値
	 *
	 */
	public TargetYear getYear() {
		return year;
	}
	
	/**
	 *<pre>
	 * パッケージしている月(MM)の値を文字列で返します。
	 *</pre>
	 * @return getYear 月(MM)の値
	 *
	 */
	public TargetMonth getMonth() {
		return month;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
