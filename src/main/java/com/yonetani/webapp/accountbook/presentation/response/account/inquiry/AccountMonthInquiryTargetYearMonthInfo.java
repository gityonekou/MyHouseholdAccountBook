/**
 * マイ家計簿　各月の収支と各月の収支詳細画面に表示する対象年月情報です。
 * 対象年、対象月、前月、次月、戻り時の表示年月の値を持ちます
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/10/26 : 1.00.00                      新規作成
 * 2026/09/23 : 1.01.00  feature-1.03-dev1   追加リファクタリング対応(ファクトリの引数をドメインタイプに変更)
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.account.inquiry;

import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;

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
 * @since 家計簿アプリ(1.00)
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
	public static AccountMonthInquiryTargetYearMonthInfo from(TargetYearMonth targetYearMonth, TargetYearMonth returnYearMonth) {
		return new AccountMonthInquiryTargetYearMonthInfo(
				// 対象年月
				targetYearMonth.getValue(),
				// 戻り時の表示対象年月
				returnYearMonth.getValue(),
				// 対象年
				targetYearMonth.getTargetYear().getValue(),
				// 前月
				//yearMonthCalendar.minusMonths(1).format(format),
				targetYearMonth.plusMonths(-1).getValue(),
				// 翌月
				targetYearMonth.plusMonths(1).getValue(),
				// 「yyyy年MM月度」の年の値
				targetYearMonth.getTargetYear().getValue(),
				// 「yyyy年MM月度」の月の値
				targetYearMonth.getTargetMonth().getValue());
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
	public static AccountMonthInquiryTargetYearMonthInfo from(TargetYearMonth targetYearMonth) {
		return AccountMonthInquiryTargetYearMonthInfo.from(targetYearMonth, targetYearMonth);
	}
}
