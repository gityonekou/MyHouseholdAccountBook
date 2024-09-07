/**
 * マイ家計簿の年間収支(マージ)画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/09 : 1.00.00  新規作成
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
 * マイ家計簿の年間収支(マージ)画面表示情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountYearMageInquiryResponse extends AbstractResponse {
	
	/**
	 *<pre>
	 * 年間収支(マージ)情報の明細データです
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
	public static class MageInquiryListItem {
		// 対象月
		private final String month;
		// 収入金額
		private final String syuunyuuKingaku;
		// 支出予定金額
		private final String sisyutuYoteiKingaku ;
		// 支出金額
		private final String sisyutuKingaku;
		// 収支
		private final String syuusiKingaku;
		/**
		 *<pre>
		 * 引数の値から年間収支(マージ)情報の明細データを生成して返します。
		 *</pre>
		 * @param month 対象月(TARGET_MONTH)
		 * @param syuunyuuKingaku 収入金額(INCOME_KINGAKU)
		 * @param sisyutuYoteiKingaku 支出予定金額(EXPENDITURE_ESTIMATE_KINGAKU)
		 * @param sisyutuKingaku 支出金額(EXPENDITURE_KINGAKU)
		 * @param syuusiKingaku 収支(INCOME_AND_EXPENDITURE_KINGAKU)
		 * @return 年間収支(マージ)情報の明細データ
		 *
		 */
		public static MageInquiryListItem from(
				String month,
				String syuunyuuKingaku,
				String sisyutuYoteiKingaku,
				String sisyutuKingaku,
				String syuusiKingaku) {
			return new MageInquiryListItem(
					month,
					syuunyuuKingaku,
					sisyutuYoteiKingaku,
					sisyutuKingaku,
					syuusiKingaku) {
			};
		}
	}
	// 表示する年の対象年月情報
	private final AccountYearInquiryTargetYearInfo targetYearInfo;
	// 年間収支(マージ)情報のリストです。
	private List<MageInquiryListItem> mageInquiryList = new ArrayList<>();
	
	// 収入金額合計
	@Setter
	private String syuunyuuKingakuGoukei;
	// 支出予定金額合計
	@Setter
	private String sisyutuYoteiKingakuGoukei;
	// 支出金額合計
	@Setter
	private String sisyutuKingakuGoukei;
	// 収支合計
	@Setter
	private String syuusiKingakuGoukei;
	
	/**
	 *<pre>
	 * 指定年の収支取得用リクエスト情報からレスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearInfo 表示する対象年月情報
	 * @return マイ家計簿の年間収支(マージ)画面表示情報
	 *
	 */
	public static AccountYearMageInquiryResponse getInstance(AccountYearInquiryTargetYearInfo targetYearInfo) {
		return new AccountYearMageInquiryResponse(targetYearInfo);
	}

	/**
	 *<pre>
	 * 年間収支(マージ)情報の明細リストを追加します。
	 *</pre>
	 * @param addList 追加する年間収支(マージ)情報のリスト
	 *
	 */
	public void addMageInquiryList(List<MageInquiryListItem> addList) {
		if(!CollectionUtils.isEmpty(addList)) {
			mageInquiryList.addAll(addList);
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
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("account/inquiry/AccountYearMage");
		// 表示する年の対象年月情報を設定
		modelAndView.addObject("targetYearInfo", targetYearInfo);
		// 月毎の支出項目明細リストを追加
		modelAndView.addObject("mageInquiryList", mageInquiryList);
		// 収入金額合計
		modelAndView.addObject("syuunyuuKingakuGoukei", syuunyuuKingakuGoukei);
		// 支出予定金額合計
		modelAndView.addObject("sisyutuYoteiKingakuGoukei", sisyutuYoteiKingakuGoukei);
		// 支出金額合計
		modelAndView.addObject("sisyutuKingakuGoukei", sisyutuKingakuGoukei);
		// 収支合計
		modelAndView.addObject("syuusiKingakuGoukei", syuusiKingakuGoukei);
		
		return modelAndView;
	}
}
