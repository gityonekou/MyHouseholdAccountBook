/**
 * マイ家計簿　各月の収支と各月の収支詳細画面に表示する対象年月情報です。
 * 対象年、対象月、前月、次月、戻り時の表示年月の値を持ちます
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/10/26 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.account.inquiry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * マイ家計簿　各月の収支と各月の収支詳細画面に表示する対象年月情報です。
 * 対象年、対象月、前月、次月、戻り時の表示年月の値を持ちます
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AccountMonthInquiryTargetYearMonthInfo {
	// 対象年月
	private final String targetYearMonth;
	// 戻り時の表示対象年月
	private final String returnYearMonth;
	// 対象年
	private final String targetYear;
	// 前月
	private final String beforeYearMonth;
	// 翌月
	private final String nextYearMonth;
	// 「yyyy年MM月度」の年の値
	private final String viewYear;
	// 「yyyy年MM月度」の月の値
	private final String viewMonth;
	
	/**
	 *<pre>
	 * 引数の値から表示する月の対象年月情報を生成して返します。
	 *</pre>
	 * @param targetYearMonth 表示対象の年月(YYYMM)
	 * @param returnYearMonth 戻り時の表示対象年月
	 * @return 表示する月の対象年月情報
	 *
	 */
	public static AccountMonthInquiryTargetYearMonthInfo from(String targetYearMonth, String returnYearMonth) {
		// 念のため、ここで入力値チェックを行う
		if(!StringUtils.hasLength(targetYearMonth) || targetYearMonth.length() != 6) {
			throw new MyHouseholdAccountBookRuntimeException("対象年月の値が不正です。管理者に問い合わせてください。[targetYearMonth=" + targetYearMonth + "]");
		}
		// 現在の対象年月からカレンダーを生成(前月・翌月の値取得用)
		LocalDate yearMonthCalendar = LocalDate.parse(targetYearMonth + "01", MyHouseholdAccountBookContent.DATE_TIME_FORMATTER);
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMM");
		
		return new AccountMonthInquiryTargetYearMonthInfo(
				// 対象年月
				targetYearMonth,
				// 戻り時の表示対象年月
				returnYearMonth,
				// 対象年
				targetYearMonth.substring(0, 4),
				// 前月
				yearMonthCalendar.minusMonths(1).format(format),
				// 翌月
				yearMonthCalendar.plusMonths(1).format(format),
				// 「yyyy年MM月度」の年の値
				targetYearMonth.substring(0, 4),
				// 「yyyy年MM月度」の月の値
				targetYearMonth.substring(4)
				);
	}
	
	/**
	 *<pre>
	 * 引数の値から表示する月の対象年月情報を生成して返します。
	 * 戻り時の表示対象年月の値は引数で指定した対象年月と同じ値が設定されます。
	 *</pre>
	 * @param targetYearMonth 表示対象の年月(YYYMM)
	 * @return 表示する月の対象年月情報
	 *
	 */
	public static AccountMonthInquiryTargetYearMonthInfo from(String targetYearMonth) {
		return AccountMonthInquiryTargetYearMonthInfo.from(targetYearMonth, targetYearMonth);
	}
}
