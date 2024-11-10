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

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

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
		// 支出金額Bの割合
		private final String percentageB;
		// 支出金額C
		private final String sisyutuKingakuC;
		// 支出金額Cの割合
		private final String percentageC;
		// 支出金額BとC合計値
		private final String sisyutuKingakuBC;
		// 支出金額BとC合計値の割合
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
		 * @param sisyutuKingakuB 支出金額B
		 * @praam percentageB 支出金額B割合
		 * @param sisyutuKingakuC 支出金額C
		 * @praam percentageC 支出金額C割合
		 * @param sisyutuKingakuBC 支出金額BとC合計値
		 * @praam percentage 支出金額BとC合計値の割合
		 * @param siharaiDate 支払日
		 * @return 月毎の支出金額情報明細
		 *
		 */
		public static ExpenditureListItem form(
				int sisyutuItemLevel,
				String sisyutuItemName,
				String sisyutuKingaku,
				String sisyutuKingakuB,
				String percentageB,
				String sisyutuKingakuC,
				String percentageC,
				String sisyutuKingakuBC,
				String percentage,
				String siharaiDate) {
			return new ExpenditureListItem(
					sisyutuItemLevel,
					sisyutuItemName,
					sisyutuKingaku,
					sisyutuKingakuB,
					percentageB,
					sisyutuKingakuC,
					percentageC,
					sisyutuKingakuBC,
					percentage,
					siharaiDate);
		}
	}
	
	// 表示する月の対象年月情報
	private final AccountMonthInquiryTargetYearMonthInfo targetYearMonthInfo;
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
	public static AccountMonthInquiryResponse getInstance(AccountMonthInquiryTargetYearMonthInfo targetYearMonthInfo) {
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
