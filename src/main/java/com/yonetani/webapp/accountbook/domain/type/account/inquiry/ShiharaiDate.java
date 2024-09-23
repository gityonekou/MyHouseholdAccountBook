/**
 * 「支払日」項目の値を表すドメインタイプです。
 * 
 * [注意]
 * 支払日項目は支出テーブルの支払日項目がNULL値を許容するため、支払日にnullを設定可能となります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「支払日」項目の値を表すドメインタイプです
 * 
 * [注意]
 * 支払日項目は支出テーブルの支払日項目がNULL値を許容するため、支払日にnullを設定可能となります。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Getter
public class ShiharaiDate {
	// 支払日
	private final LocalDate value;
	
	/**
	 *<pre>
	 * 「支払日」項目の値を表すドメインタイプを生成します。
	 * 
	 * 支払日にnullを指定した場合、null値を保持したドメインタイプが生成されます。
	 * [ガード節]：なし
	 * 
	 *</pre>
	 * @param datetime 支払日
	 * @return 「支払日」項目ドメインタイプ
	 *
	 */
	public static ShiharaiDate from(LocalDate datetime) {
		return new ShiharaiDate(datetime);
	}
	
	/**
	 *<pre>
	 * 対象年月(YYYYMM)と日付(DD)の各文字列から「支払日」項目の値を表すドメインタイプを生成します。
	 * 指定した日付(DD)の値が空(null含む)の場合、支払日がnullのドメインタイプを生成します。
	 * 
	 * [非ガード]
	 * ・日付(DD)がnullか空文字列
	 * [ガード節]
	 * ・対象年月が6桁以外
	 * ・日付(DD)が長さを持ち、かつ、2桁以外
	 * ・対象年月と日付を組み合わせた値がLocalDateに変換できない
	 * 
	 *</pre>
	 * @param yearMonth 対象年月(YYYYMM)
	 * @param day 日付(DD)
	 * @return 「支払日」項目ドメインタイプ
	 *
	 */
	public static ShiharaiDate from(String yearMonth, String day) {
		// 指定した日付が空文字列(null含む)の場合、値nullの支払日(ドメインタイプ)を返却
		if(!StringUtils.hasLength(day)) {
			return new ShiharaiDate(null);
		}
		// ガード節(対象年月が6桁以外)
		if(!StringUtils.hasLength(yearMonth) || yearMonth.length() != 6) {
			throw new MyHouseholdAccountBookRuntimeException("「支払日」項目の設定値が不正です。管理者に問い合わせてください。[yearMonth=" + yearMonth + "]");
		}
		// ガード節(日付(DD)が長さを持ち、かつ、2桁以外)
		if(day.length() != 2) {
			throw new MyHouseholdAccountBookRuntimeException("「支払日」項目の設定値が不正です。管理者に問い合わせてください。[day=" + day + "]");
		}
		// 引数で指定した対象年月、日付の値をもとにLocalDateに変換し、「支払日」項目ドメインタイプを生成
		String dateText = yearMonth + day;
		try {
			return new ShiharaiDate(LocalDate.parse(dateText, DateTimeFormatter.ofPattern("yyyyMMdd")));
		} catch (DateTimeParseException ex) {
			throw new MyHouseholdAccountBookRuntimeException("「支払日」項目の設定値が不正です。管理者に問い合わせてください。[date=" + dateText + "]");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		// YYYY/MM/DD形式で返却
		return DomainCommonUtils.formatyyyySPMMSPdd(value);
	}	
}
