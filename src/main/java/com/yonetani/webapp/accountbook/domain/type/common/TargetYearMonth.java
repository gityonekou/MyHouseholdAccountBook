/**
 * 「年月」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/24 : 1.00.00  新規作成
 * 2026/05/07 : 1.01.00  固定費合計表示変更対応(対象年月の加算メソッドとラベル取得メソッドを追加)  
 * 2026/05/09 : 1.01.01  リファクタリング追加対応(対象年月ドメインの集約)
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import java.time.LocalDate;
import java.time.YearMonth;
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
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class TargetYearMonth {
	
	// 年月(yyyyMM)
	private final String value;
	// 「年」項目の値
	private final TargetYear targetYear;
	// 「月」項目の値
	private final TargetMonth targetMonth;
	
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
		// カレンダーの日付として有効かどうか(対象年月の値に01日を付けて、日付として有効かチェックする(01日なので、厳密チェックは不要となる)
		try {
			LocalDate.parse(yearMonth + "01", MyHouseholdAccountBookContent.DATE_TIME_FORMATTER);
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
	 * 「年月」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・空文字列
	 * ・長さが6桁以外
	 * ・年項目、月項目に変換できない
	 * 
	 *</pre>
	 * @param year 対象年(YYYY)
	 * @param month 対象月(MM)
	 * @return 「年月」項目ドメインタイプ
	 *
	 */
	public static TargetYearMonth from(String year, String month){
		// 年項目
		TargetYear yearIns = TargetYear.from(year);
		// 月項目
		TargetMonth monthIns = TargetMonth.from(month);
		// 年月文字列
		String yearMonth = yearIns.getValue() + monthIns.getValue();
		
		// カレンダーの日付として有効かどうか(対象年月の値に01日を付けて、日付として有効かチェックする(01日なので、厳密チェックは不要となる)
		try {
			LocalDate.parse(yearMonth + "01", MyHouseholdAccountBookContent.DATE_TIME_FORMATTER);
		} catch (DateTimeParseException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「年月」項目の設定値が不正です。管理者に問い合わせてください。[yearMonth=" + yearMonth + "]");
		}

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
		return targetYear.getValue();
	}
	
	/**
	 *<pre>
	 * パッケージしている月(MM)の値を文字列で返します。
	 *</pre>
	 * @return getYear 月(MM)の値
	 *
	 */
	public String getMonth() {
		return targetMonth.getValue();
	}
	
	/**
	 *<pre>
	 * 指定した月数後の TargetYearMonth を返します。
	 *</pre>
	 * @param months 加算する月数
	 * @return 指定した月数後の TargetYearMonth
	 *
	 */
	public TargetYearMonth plusMonths(int months) {
		YearMonth ym = YearMonth.of(Integer.parseInt(targetYear.getValue()), Integer.parseInt(targetMonth.getValue()));
		YearMonth result = ym.plusMonths(months);
		return TargetYearMonth.from(String.valueOf(result.getYear()), String.format("%02d", result.getMonthValue()));
	}

	/**
	 *<pre>
	 * "YYYY年MM月" 形式の表示ラベルを返します（月の先頭0あり）。
	 * 例: 2025年11月、2026年01月
	 *</pre>
	 * @return 表示ラベル
	 *
	 */
	public String toDisplayLabel() {
		return targetYear.getValue() + "年" + targetMonth.getValue() + "月";
	}

	@Override
	public String toString() {
		return value;
	}

}
