/**
 * マイ家計簿の各月の収支画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.account.inquiry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *<pre>
 * マイ家計簿の各月の収支画面表示情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountMonthInquiryResponse extends AbstractResponse {

	/**
	 *<pre>
	 * 表示する月の対象年月情報です。
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
	public static class TargetYearMonthInfo {
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
		public static TargetYearMonthInfo from(String targetYearMonth, String returnYearMonth) {
			// 念のため、ここで入力値チェックを行う
			if(!StringUtils.hasLength(targetYearMonth) || targetYearMonth.length() != 6) {
				throw new MyHouseholdAccountBookRuntimeException("対象年月の値が不正です。管理者に問い合わせてください。[targetYearMonth=" + targetYearMonth + "]");
			}
			// 現在の対象年月からカレンダーを生成(前月・翌月の値取得用)
			LocalDate yearMonthCalendar = LocalDate.parse(targetYearMonth + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
			DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMM");
			
			return new TargetYearMonthInfo(
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
		public static TargetYearMonthInfo from(String targetYearMonth) {
			return TargetYearMonthInfo.from(targetYearMonth, targetYearMonth);
		}
	}
	
	/**
	 *<pre>
	 * 月毎の支出金額情報明細です
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
	public static class ExpenditureListItem {
		// 支出項目レベル(1～5)
		private final int sisyutuItemLevel;
		// 支出項目名
		private final String sisyutuItemName;
		// 支出金額
		private final String sisyutuKingaku;
		// 支出金額B
		private final String sisyutuKingakuB;
		// 支出金額B割合
		private final String percentage;
		// 支払日
		private final String siharaiDate;
		
		/**
		 *<pre>
		 * 月毎の支出金額情報明細を生成して返します。
		 *</pre>
		 * @param sisyutuItemLevel 支出項目レベル
		 * @param sisyutuItemName 支出項目名
		 * @param sisyutuKingaku 支出金額
		 * @param sisyutuKingakuB 支出金額b
		 * @praam percentage 支出金額B割合
		 * @param siharaiDate 支払日
		 * @return 月毎の支出金額情報明細
		 *
		 */
		public static ExpenditureListItem form(
				int sisyutuItemLevel,
				String sisyutuItemName,
				String sisyutuKingaku,
				String sisyutuKingakuB,
				String percentage,
				String siharaiDate) {
			return new ExpenditureListItem(
					sisyutuItemLevel,
					sisyutuItemName,
					sisyutuKingaku,
					sisyutuKingakuB,
					percentage,
					siharaiDate);
		}
	}
	
	// 表示する月の対象年月情報
	private final TargetYearMonthInfo targetYearMonthInfo;
	// 月毎の支出金額情報明細のリストです。
	private List<ExpenditureListItem> expenditureItemList = new ArrayList<>();
	
	// 指定月の収支情報
	// 表示する月の収支情報が登録済みかどうかのフラグ(デフォルトはデータあり)
	@Setter
	private boolean syuusiDataFlg = true;
	// 収入金額
	@Setter
	private String syuunyuuKingaku;
	// 支出金額
	@Setter
	private String sisyutuKingaku;
	// 支出予定金額
	@Setter
	private String sisyutuYoteiKingaku;
	// 収支金額
	@Setter
	private String syuusiKingaku;
	
	/**
	 *<pre>
	 * 表示する月の対象年月情報からレスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearMonthInfo 表示する月の対象年月情報
	 * @return マイ家計簿の各月の収支画面表示情報
	 *
	 */
	public static AccountMonthInquiryResponse getInstance(TargetYearMonthInfo targetYearMonthInfo) {
		return new AccountMonthInquiryResponse(targetYearMonthInfo);
	}
	
	/**
	 *<pre>
	 * 月毎の支出項目明細を追加します。
	 *</pre>
	 * @param addList 追加する月毎の支出項目明細のリスト
	 *
	 */
	public void addExpenditureItemList(List<ExpenditureListItem> addList) {
		if(!CollectionUtils.isEmpty(addList)) {
			expenditureItemList.addAll(addList);
		}
	}
	
	/**
	 *<pre>
	 * 現在のレスポンス情報から画面返却データのModelAndViewを生成して返します。
	 * 
	 *</pre>
	 * @return 画面返却データのModelAndView
	 *
	 */
	@Override
	public ModelAndView build() {
		if(syuusiDataFlg) {
			// 指定月の収支情報ありの場合、マイ家計簿(各月の収支)画面のModelとViewを生成
			ModelAndView modelAndView = createModelAndView("account/inquiry/AccountMonth");
			// 対象年、対象月、前月、次月の値を設定
			modelAndView.addObject("targetYearMonthInfo", targetYearMonthInfo);
			// 収入金額
			modelAndView.addObject("syuunyuuKingaku", syuunyuuKingaku);
			// 支出金額
			modelAndView.addObject("sisyutuKingaku", sisyutuKingaku);
			// 支出予定金額
			modelAndView.addObject("sisyutuYoteiKingaku", sisyutuYoteiKingaku);
			// 収支金額
			modelAndView.addObject("syuusiKingaku", syuusiKingaku);
			// 月毎の支出項目明細リストを追加
			modelAndView.addObject("expenditureItemList", expenditureItemList);
			return modelAndView;
		} else {
			// 指定月の収支情報なしの場合、マイ家計簿(各月の収支：収支登録確認)画面のModelとViewを生成
			ModelAndView modelAndView = createModelAndView("account/inquiry/AccountMonthRegistCheck");
			// 対象年、対象月、前月、次月の値を設定
			modelAndView.addObject("targetYearMonthInfo", targetYearMonthInfo);
			return modelAndView;
		}
	}
}
