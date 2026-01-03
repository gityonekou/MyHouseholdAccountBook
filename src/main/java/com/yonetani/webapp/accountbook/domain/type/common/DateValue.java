/**
 * 日付を表す値オブジェクトの抽象基底クラスです。
 * すべての日付系ドメインタイプはこのクラスを継承します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 日付を表す値オブジェクトの抽象基底クラスです。
 *
 * [責務]
 * ・日付の基本的なバリデーション（null、形式チェック）
 * ・日付の比較機能
 * ・日付の表示形式の統一
 *
 * [設計方針]
 * ・不変性：生成後は値を変更できない
 * ・自己検証：不正な値は生成時に検証
 * ・型安全性：サブクラスで具体的な日付の型を表現
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public abstract class DateValue {

	// yyyyMMdd形式のフォーマッター
	private static final DateTimeFormatter YYYYMMDD_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
	// yyyy/MM/dd形式のフォーマッター
	private static final DateTimeFormatter YYYY_SP_MM_SP_DD_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	// yyyy年MM月dd日形式のフォーマッター（日本語表示用）
	private static final DateTimeFormatter JAPANESE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
	// yyyy年MM月形式のフォーマッター（日本語年月表示用）
	private static final DateTimeFormatter JAPANESE_YEAR_MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy年MM月");

	// 日付の値
	private final LocalDate value;

	/**
	 *<pre>
	 * 日付の値を検証します。
	 *
	 * [検証内容]
	 * ・null値チェック
	 *
	 * サブクラスで追加の検証が必要な場合は、コンストラクタでこのメソッドを呼び出した後に
	 * 追加の検証を実行してください。
	 *</pre>
	 * @param value 検証対象の日付
	 * @param typeName 日付の型名（エラーメッセージ用）
	 * @throws MyHouseholdAccountBookRuntimeException 検証エラー時
	 *
	 */
	protected static void validate(LocalDate value, String typeName) {
		// ガード節(null)
		if(value == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				String.format("「%s」項目の設定値がnullです。管理者に問い合わせてください。", typeName));
		}
	}

	/**
	 *<pre>
	 * 文字列から日付をパースします。
	 *
	 * [フォーマット]
	 * ・yyyyMMdd形式
	 *
	 *</pre>
	 * @param dateString 日付文字列（yyyyMMdd形式）
	 * @param typeName 日付の型名（エラーメッセージ用）
	 * @return パースされたLocalDate
	 * @throws MyHouseholdAccountBookRuntimeException パースエラー時
	 *
	 */
	protected static LocalDate parseDate(String dateString, String typeName) {
		if(dateString == null || dateString.isEmpty()) {
			throw new MyHouseholdAccountBookRuntimeException(
				String.format("「%s」項目の設定値がnullまたは空文字です。管理者に問い合わせてください。", typeName));
		}
		if(dateString.length() != 8) {
			throw new MyHouseholdAccountBookRuntimeException(
				String.format("「%s」項目の設定値が不正です。管理者に問い合わせてください。[dateString=%s]",
					typeName, dateString));
		}
		try {
			return LocalDate.parse(dateString, YYYYMMDD_FORMAT);
		} catch (DateTimeParseException ex) {
			throw new MyHouseholdAccountBookRuntimeException(
				String.format("「%s」項目の設定値が不正です。管理者に問い合わせてください。[dateString=%s]",
					typeName, dateString));
		}
	}

	/**
	 *<pre>
	 * 対象年月(YYYYMM)と日付(DD)から日付をパースします。
	 *
	 *</pre>
	 * @param yearMonth 対象年月（YYYYMM形式）
	 * @param day 日付（DD形式）
	 * @param typeName 日付の型名（エラーメッセージ用）
	 * @return パースされたLocalDate
	 * @throws MyHouseholdAccountBookRuntimeException パースエラー時
	 *
	 */
	protected static LocalDate parseDate(String yearMonth, String day, String typeName) {
		// ガード節(対象年月が6桁以外)
		if(yearMonth == null || yearMonth.isEmpty() || yearMonth.length() != 6) {
			throw new MyHouseholdAccountBookRuntimeException(
				String.format("「%s」項目の設定値が不正です。管理者に問い合わせてください。[yearMonth=%s]",
					typeName, yearMonth));
		}
		// ガード節(日付(DD)がnullまたは空)
		if(day == null || day.isEmpty()) {
			throw new MyHouseholdAccountBookRuntimeException(
				String.format("「%s」項目の設定値が不正です。管理者に問い合わせてください。[day=%s]",
					typeName, day));
		}
		// ガード節(日付(DD)が2桁以外)
		if(day.length() != 2) {
			throw new MyHouseholdAccountBookRuntimeException(
				String.format("「%s」項目の設定値が不正です。管理者に問い合わせてください。[day=%s]",
					typeName, day));
		}
		// 日付文字列を組み立ててパース
		return parseDate(yearMonth + day, typeName);
	}

	/**
	 *<pre>
	 * 日付の比較を行います。
	 *</pre>
	 * @param other 比較対象の日付
	 * @return this < other の場合は負の値、this == other の場合は0、this > other の場合は正の値
	 *
	 */
	public int compareTo(DateValue other) {
		if(other == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				"比較対象の日付がnullです。管理者に問い合わせてください。");
		}
		return this.value.compareTo(other.value);
	}

	/**
	 *<pre>
	 * 指定した日付より前かどうかを判定します。
	 *</pre>
	 * @param other 比較対象の日付
	 * @return 指定した日付より前の場合true、それ以外の場合false
	 *
	 */
	public boolean isBefore(DateValue other) {
		if(other == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				"比較対象の日付がnullです。管理者に問い合わせてください。");
		}
		return this.value.isBefore(other.value);
	}

	/**
	 *<pre>
	 * 指定した日付より後かどうかを判定します。
	 *</pre>
	 * @param other 比較対象の日付
	 * @return 指定した日付より後の場合true、それ以外の場合false
	 *
	 */
	public boolean isAfter(DateValue other) {
		if(other == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				"比較対象の日付がnullです。管理者に問い合わせてください。");
		}
		return this.value.isAfter(other.value);
	}

	/**
	 *<pre>
	 * 日付をコンパクト形式（yyyyMMdd）の文字列で取得します。
	 *</pre>
	 * @return yyyyMMdd形式の文字列
	 *
	 */
	public String toCompactString() {
		return this.value.format(YYYYMMDD_FORMAT);
	}

	/**
	 *<pre>
	 * 日付を表示用形式（yyyy/MM/dd）の文字列で取得します。
	 *</pre>
	 * @return yyyy/MM/dd形式の文字列
	 *
	 */
	public String toDisplayString() {
		return this.value.format(YYYY_SP_MM_SP_DD_FORMAT);
	}

	/**
	 *<pre>
	 * 日付を日本語表示用形式（yyyy年MM月dd日）の文字列で取得します。
	 *</pre>
	 * @return yyyy年MM月dd日形式の文字列
	 *
	 */
	public String toJapaneseDisplayString() {
		return this.value.format(JAPANESE_DATE_FORMAT);
	}

	/**
	 *<pre>
	 * 日付を日本語年月表示用形式（yyyy年MM月）の文字列で取得します。
	 *</pre>
	 * @return yyyy年MM月形式の文字列
	 *
	 */
	public String toJapaneseYearMonthString() {
		return this.value.format(JAPANESE_YEAR_MONTH_FORMAT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		// ISO-8601形式（yyyy-MM-dd）で返却（デバッグ用）
		return this.value.toString();
	}
}
