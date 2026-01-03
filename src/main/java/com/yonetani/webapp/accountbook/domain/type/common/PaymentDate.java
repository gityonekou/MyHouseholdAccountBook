/**
 * 「支払日」項目の値を表すドメインタイプです。
 *
 * [注意]
 * 支払日項目は支出テーブルの支払日項目がNULL値を許容するため、支払日にnullを設定可能となります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import java.time.LocalDate;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 *<pre>
 * 「支払日」項目の値を表すドメインタイプです。
 *
 * [注意]
 * 支払日項目は支出テーブルの支払日項目がNULL値を許容するため、支払日にnullを設定可能となります。
 *
 * [責務]
 * ・支払日の値を保持（null値許容）
 * ・支払日の妥当性を保証
 * ・支払日の比較機能（max()メソッド）
 *
 * [使用例]
 * ・支出の支払日
 * ・固定費からの支出生成時の支払日
 * ・月次収支照会における支払日の比較
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class PaymentDate extends NullableDateValue {

	/**
	 *<pre>
	 * 「支払日」項目の値を表すドメインタイプを生成します。
	 *
	 * 支払日にnullを指定した場合、null値を保持したドメインタイプが生成されます。
	 * [ガード節]：なし
	 *</pre>
	 * @param value 支払日（null許容）
	 *
	 */
	private PaymentDate(LocalDate value) {
		super(value);
		validate(value, "支払日");
	}

	/**
	 *<pre>
	 * 「支払日」項目の値を表すドメインタイプを生成します。
	 *
	 * 支払日にnullを指定した場合、null値を保持したドメインタイプが生成されます。
	 * [ガード節]：なし
	 *
	 *</pre>
	 * @param value 支払日（null許容）
	 * @return 「支払日」項目ドメインタイプ
	 *
	 */
	public static PaymentDate from(LocalDate value) {
		return new PaymentDate(value);
	}

	/**
	 *<pre>
	 * yyyyMMdd形式の文字列から「支払日」項目の値を表すドメインタイプを生成します。
	 *
	 * [null許容仕様]
	 * ・dateStringがnullまたは空文字列の場合、null値を保持したドメインタイプを生成
	 *
	 * [ガード節]
	 * ・8桁以外
	 * ・日付として不正な値
	 *
	 *</pre>
	 * @param dateString 支払日（yyyyMMdd形式、null許容）
	 * @return 「支払日」項目ドメインタイプ
	 *
	 */
	public static PaymentDate from(String dateString) {
		LocalDate parsed = parseDate(dateString, "支払日");
		return new PaymentDate(parsed);
	}

	/**
	 *<pre>
	 * 対象年月(YYYYMM)と日付(DD)から「支払日」項目の値を表すドメインタイプを生成します。
	 *
	 * [null許容仕様]
	 * ・yearMonth、dayともにnullまたは空文字列の場合、null値を保持したドメインタイプを生成
	 *
	 * [ガード節]
	 * ・対象年月が6桁以外
	 * ・日付(DD)が2桁以外
	 * ・対象年月と日付を組み合わせた値がLocalDateに変換できない
	 *
	 *</pre>
	 * @param yearMonth 対象年月(YYYYMM、null許容)
	 * @param day 日付(DD、null許容)
	 * @return 「支払日」項目ドメインタイプ
	 *
	 */
	public static PaymentDate from(String yearMonth, String day) {
		LocalDate parsed = parseDate(yearMonth, day, "支払日");
		return new PaymentDate(parsed);
	}

	/**
	 *<pre>
	 * この支払日の値と引数で指定した支払日の値を比較し大きいほうの値を返します。
	 *
	 * [null値の扱い]
	 * ・this.value == null の場合、otherを返却
	 * ・other.value == null の場合、thisを返却
	 * ・両方nullの場合、otherを返却（どちらもnull）
	 *
	 *</pre>
	 * @param other 比較する支払日の値
	 * @return　値が大きいほうの「支払日」項目の値
	 *
	 */
	public PaymentDate max(PaymentDate other) {
		if(other == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				"比較対象の支払日がnullです。管理者に問い合わせてください。");
		}
		// thisがnullの場合はotherを返却
		if(this.getValue() == null) {
			return other;
		}
		// otherがnullの場合はthisを返却
		if(other.getValue() == null) {
			return this;
		}
		// 両方とも値がある場合、大きい方を返却
		if(this.getValue().compareTo(other.getValue()) < 0) {
			return other;
		}
		return this;
	}
}
