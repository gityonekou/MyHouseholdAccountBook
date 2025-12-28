/**
 * null値を許容する日付を表す値オブジェクトの抽象基底クラスです。
 * null許容日付系ドメインタイプはこのクラスを継承します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/28 : 1.00.00  新規作成
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
 * null値を許容する日付を表す値オブジェクトの抽象基底クラスです。
 *
 * [責務]
 * ・null許容日付の基本的なバリデーション（null値は許容）
 * ・null安全な日付の比較機能
 * ・null値を考慮した日付の表示形式の統一
 *
 * [設計方針]
 * ・不変性：生成後は値を変更できない
 * ・自己検証：不正な値は生成時に検証（null値は許容）
 * ・型安全性：サブクラスで具体的な日付の型を表現
 * ・null安全性：null値を含む操作を安全に処理
 *
 * [DateValueクラスとの違い]
 * ・DateValueクラス：null非許容、null値はエラー
 * ・NullableDateValueクラス：null許容、null値は空文字として扱う表示を提供
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public abstract class NullableDateValue {

	// yyyyMMdd形式のフォーマッター
	private static final DateTimeFormatter YYYYMMDD_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
	// yyyy/MM/dd形式のフォーマッター
	private static final DateTimeFormatter YYYY_SP_MM_SP_DD_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	// yyyy年MM月dd日形式のフォーマッター（日本語表示用）
	private static final DateTimeFormatter JAPANESE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
	// yyyy年MM月形式のフォーマッター（日本語年月表示用）
	private static final DateTimeFormatter JAPANESE_YEAR_MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy年MM月");

	// 日付の値（null許容）
	private final LocalDate value;

	/**
	 *<pre>
	 * 日付の値を検証します。
	 *
	 * [検証内容]
	 * ・現時点では特に検証なし（null値を許容）
	 *
	 * [非検証内容]
	 * ・null値チェック - null値を許容するため検証しない
	 *
	 * サブクラスで追加の検証が必要な場合は、コンストラクタでこのメソッドを呼び出した後に
	 * 追加の検証を実行してください。
	 *</pre>
	 * @param value 検証対象の日付（null許容）
	 * @param typeName 日付の型名（エラーメッセージ用）
	 *
	 */
	protected static void validate(LocalDate value, String typeName) {
		// null値を許容するため、現時点では検証なし
	}

	/**
	 *<pre>
	 * 文字列から日付をパースします。
	 *
	 * [フォーマット]
	 * ・yyyyMMdd形式
	 *
	 * [null許容仕様]
	 * ・dateStringがnullまたは空文字列の場合、nullを返却（エラーにしない）
	 *
	 *</pre>
	 * @param dateString 日付文字列（yyyyMMdd形式、null許容）
	 * @param typeName 日付の型名（エラーメッセージ用）
	 * @return パースされたLocalDate。dateStringがnullまたは空文字列の場合はnull
	 * @throws MyHouseholdAccountBookRuntimeException パースエラー時（形式不正、8桁以外、不正な日付）
	 *
	 */
	protected static LocalDate parseDate(String dateString, String typeName) {
		// null値または空文字列の場合はnullを返却（null許容）
		if(dateString == null || dateString.isEmpty()) {
			return null;
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
	 * [null許容仕様]
	 * ・yearMonth、dayともにnullまたは空文字列の場合、nullを返却（エラーにしない）
	 *
	 *</pre>
	 * @param yearMonth 対象年月（YYYYMM形式、null許容）
	 * @param day 日付（DD形式、null許容）
	 * @param typeName 日付の型名（エラーメッセージ用）
	 * @return パースされたLocalDate。yearMonth、dayともにnullまたは空文字列の場合はnull
	 * @throws MyHouseholdAccountBookRuntimeException パースエラー時（yearMonthが6桁以外、dayが2桁以外、不正な日付）
	 *
	 */
	protected static LocalDate parseDate(String yearMonth, String day, String typeName) {
		// 両方ともnull値または空文字列の場合はnullを返却（null許容）
		boolean yearMonthEmpty = yearMonth == null || yearMonth.isEmpty();
		boolean dayEmpty = day == null || day.isEmpty();
		if(yearMonthEmpty && dayEmpty) {
			return null;
		}
		// ガード節(対象年月がnullまたは空、または6桁以外)
		if(yearMonth == null || yearMonth.isEmpty() || yearMonth.length() != 6) {
			throw new MyHouseholdAccountBookRuntimeException(
				String.format("「%s」項目の設定値が不正です。管理者に問い合わせてください。[yearMonth=%s]",
					typeName, yearMonth));
		}
		// ガード節(日付(DD)がnullまたは空、または2桁以外)
		if(day == null || day.isEmpty() || day.length() != 2) {
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
	 * null値の場合は、LocalDate.MIN（最小値）として扱います。
	 *</pre>
	 * @param other 比較対象の日付
	 * @return this < other の場合は負の値、this == other の場合は0、this > other の場合は正の値
	 *
	 */
	public int compareTo(NullableDateValue other) {
		if(other == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				"比較対象の日付がnullです。管理者に問い合わせてください。");
		}
		// null値をLocalDate.MINとして扱う
		LocalDate thisValue = this.value != null ? this.value : LocalDate.MIN;
		LocalDate otherValue = other.value != null ? other.value : LocalDate.MIN;
		return thisValue.compareTo(otherValue);
	}

	/**
	 *<pre>
	 * 指定した日付より前かどうかを判定します。
	 * null値の場合は、LocalDate.MIN（最小値）として扱います。
	 *</pre>
	 * @param other 比較対象の日付
	 * @return 指定した日付より前の場合true、それ以外の場合false
	 *
	 */
	public boolean isBefore(NullableDateValue other) {
		if(other == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				"比較対象の日付がnullです。管理者に問い合わせてください。");
		}
		// null値をLocalDate.MINとして扱う
		LocalDate thisValue = this.value != null ? this.value : LocalDate.MIN;
		LocalDate otherValue = other.value != null ? other.value : LocalDate.MIN;
		return thisValue.isBefore(otherValue);
	}

	/**
	 *<pre>
	 * 指定した日付より後かどうかを判定します。
	 * null値の場合は、LocalDate.MIN（最小値）として扱います。
	 *</pre>
	 * @param other 比較対象の日付
	 * @return 指定した日付より後の場合true、それ以外の場合false
	 *
	 */
	public boolean isAfter(NullableDateValue other) {
		if(other == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				"比較対象の日付がnullです。管理者に問い合わせてください。");
		}
		// null値をLocalDate.MINとして扱う
		LocalDate thisValue = this.value != null ? this.value : LocalDate.MIN;
		LocalDate otherValue = other.value != null ? other.value : LocalDate.MIN;
		return thisValue.isAfter(otherValue);
	}

	/**
	 *<pre>
	 * 日付がnullかどうかを判定します。
	 *</pre>
	 * @return null値の場合true、それ以外の場合false
	 *
	 */
	public boolean isNull() {
		return this.value == null;
	}

	/**
	 *<pre>
	 * 日付をコンパクト形式（yyyyMMdd）の文字列で取得します。
	 * null値の場合は空文字列を返却します。
	 *</pre>
	 * @return yyyyMMdd形式の文字列。null値の場合は空文字列
	 *
	 */
	public String toCompactString() {
		if(value == null) {
			return "";
		}
		return this.value.format(YYYYMMDD_FORMAT);
	}

	/**
	 *<pre>
	 * 日付を表示用形式（yyyy/MM/dd）の文字列で取得します。
	 * null値の場合は空文字列を返却します。
	 *</pre>
	 * @return yyyy/MM/dd形式の文字列。null値の場合は空文字列
	 *
	 */
	public String toDisplayString() {
		if(value == null) {
			return "";
		}
		return this.value.format(YYYY_SP_MM_SP_DD_FORMAT);
	}

	/**
	 *<pre>
	 * 日付を日本語表示用形式（yyyy年MM月dd日）の文字列で取得します。
	 * null値の場合は空文字列を返却します。
	 *</pre>
	 * @return yyyy年MM月dd日形式の文字列。null値の場合は空文字列
	 *
	 */
	public String toJapaneseDisplayString() {
		if(value == null) {
			return "";
		}
		return this.value.format(JAPANESE_DATE_FORMAT);
	}

	/**
	 *<pre>
	 * 日付を日本語年月表示用形式（yyyy年MM月）の文字列で取得します。
	 * null値の場合は空文字列を返却します。
	 *</pre>
	 * @return yyyy年MM月形式の文字列。null値の場合は空文字列
	 *
	 */
	public String toJapaneseYearMonthString() {
		if(value == null) {
			return "";
		}
		return this.value.format(JAPANESE_YEAR_MONTH_FORMAT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		// 値の文字列表現を返却（デバッグ用、ISO-8601形式：yyyy-MM-dd）
		// null値の場合は空文字列を返却
		if(value == null) {
			return "";
		}
		return this.value.toString();
	}
}
