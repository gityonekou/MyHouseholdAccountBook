/**
 * マイ家計簿の年間収支(マージ/明細)の各画面に表示する対象年月情報です。
 * 対象年、前年、次年、戻り時の表示年月の値を持ちます
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/16 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.account.inquiry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * マイ家計簿の年間収支(マージ/明細)の各画面に表示する対象年月情報です。
 * 対象年、前年、次年、戻り時の表示年月の値を持ちます
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AccountYearInquiryTargetYearInfo {
	// 対象年
	private final String targetYear;
	// 対象年月
	private final String targetYearMonth;
	// 各月の収支画面に戻る場合の表示対象年月
	private final String returnYearMonth;
	// 前年
	private final String beforeYear;
	// 翌年
	private final String nextYear;
	
	/**
	 *<pre>
	 * 引数の値から表示する年の対象年月情報を生成して返します。
	 *</pre>
	 * @param targetYear 表示対象の年(YYYY) 
	 * @param targetYearMonth 各月の収支画面に戻る場合に表示する年月の値
	 * @return 表示する月の対象年月情報
	 *
	 */
	public static AccountYearInquiryTargetYearInfo from(String targetYear, String targetYearMonth) {
		
		// 現在の対象年月からカレンダーを生成(前年・翌年の値取得用)
		LocalDate yearCalendar = LocalDate.parse(targetYear + "0101", DateTimeFormatter.ofPattern("yyyyMMdd"));
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy");
		
		return new AccountYearInquiryTargetYearInfo(
				// 対象年
				targetYear,
				// 対象年月
				targetYearMonth,
				// 各月の収支画面に戻る場合の表示対象年月
				targetYearMonth,
				// 前年
				yearCalendar.minusYears(1).format(format),
				// 翌年
				yearCalendar.plusYears(1).format(format)
				);
	}
}
