/**
 * 「固定費支払日(支払日)」項目の値を表すドメインタイプです
 * 「固定費支払日(支払日)」項目の値は固定費支払日(003)のコード値が設定されます。
 * 　・00(月初め)
 * 　・01(1日)～31(31日)
 * 　・40(月末)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/13 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.fixedcost;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「固定費支払日(支払日)」項目の値を表すドメインタイプです
 * 「固定費支払日(支払日)」項目の値は固定費支払日(003)のコード値が設定されます。
 * 　・00(月初め)
 * 　・01(1日)～31(31日)
 * 　・40(月末)
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
public class FixedCostShiharaiDay {
	// 固定費支払日(支払日)
	private final String value;
	
	/**
	 *<pre>
	 * 「固定費支払日(支払日)」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param value 固定費支払日(支払日)＝固定費支払日(003)のコード値
	 * @return 「固定費支払日(支払日)」項目ドメインタイプ
	 *
	 */
	public static FixedCostShiharaiDay from(String value) {
		return new FixedCostShiharaiDay(value);
	}

	/**
	 *<pre>
	 * 指定した年月に対応する支払日(DD)の値を返します。
	 * 「固定費支払日(支払日)」項目の値にコード値(月初営業日)、コード値(月初営業日)が設定されている場合、
	 * 指定年月に対応する支払日の値を返却します。
	 *</pre>
	 * @param targetYearMonth 指定年月
	 * @return 指定年月に対応する支払日(DD)
	 *
	 */
	public String getShiharaiDayValue(TargetYearMonth targetYearMonth) {
		// 支払日の値がnullの場合、空文字列を返却
		if(Objects.isNull(value)) {
			return "";
		// 支払日の値が月初営業日の場合、01日を返却
		} else if(Objects.equals(value, "00")) {
			return "01";
		// 支払日の値が28日以前の場合、支払日の値を返却
		} else if(value.compareTo("28") <= 0) {
			return value;
		}
		
		// 支払日の値が月末営業日の場合、支払日に31日を設定
		String shiharaiDayValue;
		if(value.equals("40")) {
			shiharaiDayValue = "31";
		} else {
			shiharaiDayValue = value;
		}
		// 指定年月の月初めの日にちカレンダーを取得
		LocalDate firstDayOfMonth = LocalDate.parse(targetYearMonth.getValue() + "01", MyHouseholdAccountBookContent.DATE_TIME_FORMATTER);
		// 対象月の最終日を取得
		String lastDayStr =String.format("%02d", firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth());
		
		// shiharaiDayValueの値が大きければ、最終日の値を返却
		if(shiharaiDayValue.compareTo(lastDayStr) > 0) {
			return lastDayStr;
		} else {
			return shiharaiDayValue;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
