/**
 * 「収入日」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import java.time.LocalDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 *<pre>
 * 「収入日」項目の値を表すドメインタイプです。
 *
 * [責務]
 * ・収入日の値を保持
 * ・収入日の妥当性を保証
 *
 * [使用例]
 * ・収入の収入日
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class IncomeDate extends DateValue {

	/**
	 *<pre>
	 * 「収入日」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param value 収入日
	 *
	 */
	private IncomeDate(LocalDate value) {
		super(value);
		validate(value, "収入日");
	}

	/**
	 *<pre>
	 * 「収入日」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・null値
	 *
	 *</pre>
	 * @param value 収入日
	 * @return 「収入日」項目ドメインタイプ
	 *
	 */
	public static IncomeDate from(LocalDate value) {
		return new IncomeDate(value);
	}

	/**
	 *<pre>
	 * yyyyMMdd形式の文字列から「収入日」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・null値または空文字
	 * ・8桁以外
	 * ・日付として不正な値
	 *
	 *</pre>
	 * @param dateString 収入日（yyyyMMdd形式）
	 * @return 「収入日」項目ドメインタイプ
	 *
	 */
	public static IncomeDate from(String dateString) {
		LocalDate parsed = parseDate(dateString, "収入日");
		return new IncomeDate(parsed);
	}

	/**
	 *<pre>
	 * 対象年月(YYYYMM)と日付(DD)から「収入日」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・対象年月が6桁以外
	 * ・日付(DD)がnullまたは空文字
	 * ・日付(DD)が2桁以外
	 * ・対象年月と日付を組み合わせた値がLocalDateに変換できない
	 *
	 *</pre>
	 * @param yearMonth 対象年月(YYYYMM)
	 * @param day 日付(DD)
	 * @return 「収入日」項目ドメインタイプ
	 *
	 */
	public static IncomeDate from(String yearMonth, String day) {
		LocalDate parsed = parseDate(yearMonth, day, "収入日");
		return new IncomeDate(parsed);
	}
}
