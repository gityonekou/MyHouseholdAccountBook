/**
 * マイ家計簿の年間収支(マージ)画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2023/10/09 : 1.00.00                      新規作成
 * 2026/10/12 : 1.01.00  feature-1.03-dev1   追加リファクタリング対応(MageInquiryListItem.from()の引数をドメインタイプに変更し、表示文字列変換をDTO内部に移動)
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.account.inquiry;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.BalanceTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.ExpectedExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.ExpectedExpenditureTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.ExpenditureTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.RegularIncomeTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.WithdrawingAmount;
import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.WithdrawingTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.common.BalanceAmount;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.RegularIncomeAmount;
import com.yonetani.webapp.accountbook.domain.type.common.TargetMonth;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * マイ家計簿の年間収支(マージ)画面表示情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
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
	 * @since 家計簿アプリ(1.00)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	@EqualsAndHashCode
	public static class MageInquiryListItem {
		// 対象月(表示用。"MM月"形式)
		private final String month;
		// 対象月(リンクパラメータ用。"MM"形式)
		private final String monthValue;
		// 収入金額
		private final String syuunyuuKingaku;
		// 積立金取崩金額
		private final String withdrewKingaku;
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
		 * @param targetMonth 対象月(TARGET_MONTH)
		 * @param syuunyuuKingaku 収入金額(INCOME_KINGAKU)
		 * @param withdrewKingaku 積立金取崩金額(WITHDREW_KINGAKU)
		 * @param sisyutuYoteiKingaku 支出予定金額(EXPENDITURE_ESTIMATE_KINGAKU)
		 * @param sisyutuKingaku 支出金額(EXPENDITURE_KINGAKU)
		 * @param syuusiKingaku 収支(INCOME_AND_EXPENDITURE_KINGAKU)
		 * @return 年間収支(マージ)情報の明細データ
		 *
		 */
		public static MageInquiryListItem from(
				TargetMonth targetMonth,
				RegularIncomeAmount syuunyuuKingaku,
				WithdrawingAmount withdrewKingaku,
				ExpectedExpenditureAmount sisyutuYoteiKingaku,
				ExpenditureAmount sisyutuKingaku,
				BalanceAmount syuusiKingaku) {
			return new MageInquiryListItem(
					targetMonth.toFormatString(),
					targetMonth.getValue(),
					syuunyuuKingaku.toFormatString(),
					withdrewKingaku.toFormatString(),
					sisyutuYoteiKingaku.toFormatString(),
					sisyutuKingaku.toFormatString(),
					syuusiKingaku.toFormatString()) {
			};
		}
	}
	// 表示する年の対象年月情報
	private final AccountYearInquiryTargetYearInfo targetYearInfo;
	// 収入金額合計
	private final String syuunyuuKingakuGoukei;
	// 積立金取崩金額合計
	private final String withdrewKingakuGoukei;
	// 支出予定金額合計
	private final String sisyutuYoteiKingakuGoukei;
	// 支出金額合計
	private final String sisyutuKingakuGoukei;
	// 収支合計
	private final String syuusiKingakuGoukei;

	// 年間収支(マージ)情報のリストです。
	private List<MageInquiryListItem> mageInquiryList = new ArrayList<>();
	
	/**
	 *<pre>
	 * 指定年の収支取得用リクエスト情報から表示金額なしとなるレスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearInfo 表示する対象年月情報
	 * @return マイ家計簿の年間収支(マージ)画面表示情報
	 *
	 */
	public static AccountYearMageInquiryResponse getInstance(AccountYearInquiryTargetYearInfo targetYearInfo) {
		return new AccountYearMageInquiryResponse(
				// 表示する年の対象年月情報
				targetYearInfo,
				// 収入金額合計
				null,
				// 積立金取崩金額合計
				null,
				// 支出予定金額合計
				null,
				// 支出金額合計
				null,
				// 収支合計
				null);
	}

	/**
	 *<pre>
	 * 指定年の収支取得用リクエスト情報と合計金額情報からレスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearInfo 表示する対象年月情報
	 * @param syuunyuuKingakuGoukei 収入金額合計(積立金取崩金額以外の収入金額合計)
	 * @param withdrewKingakuGoukei 積立金取崩金額合計
	 * @param sisyutuYoteiKingakuGoukei 支出予定金額合計
	 * @param sisyutuKingakuGoukei 支出金額合計
	 * @param syuusiKingakuGoukei 収支合計
	 * @return マイ家計簿の年間収支(マージ)画面表示情報
	 *
	 */
	public static AccountYearMageInquiryResponse getInstance(AccountYearInquiryTargetYearInfo targetYearInfo,
			RegularIncomeTotalAmount syuunyuuKingakuGoukei, WithdrawingTotalAmount withdrewKingakuGoukei,
			ExpectedExpenditureTotalAmount sisyutuYoteiKingakuGoukei, ExpenditureTotalAmount sisyutuKingakuGoukei,
			BalanceTotalAmount syuusiKingakuGoukei) {
		return new AccountYearMageInquiryResponse(
				// 表示する年の対象年月情報
				targetYearInfo,
				// 収入金額合計
				syuunyuuKingakuGoukei.toFormatString(),
				// 積立金取崩金額合計
				withdrewKingakuGoukei.toFormatString(),
				// 支出予定金額合計
				sisyutuYoteiKingakuGoukei.toFormatString(),
				// 支出金額合計
				sisyutuKingakuGoukei.toFormatString(),
				// 収支合計
				syuusiKingakuGoukei.toFormatString());
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
		// 積立金取崩金額合計
		modelAndView.addObject("withdrewKingakuGoukei", withdrewKingakuGoukei);
		// 支出予定金額合計
		modelAndView.addObject("sisyutuYoteiKingakuGoukei", sisyutuYoteiKingakuGoukei);
		// 支出金額合計
		modelAndView.addObject("sisyutuKingakuGoukei", sisyutuKingakuGoukei);
		// 収支合計
		modelAndView.addObject("syuusiKingakuGoukei", syuusiKingakuGoukei);
		
		return modelAndView;
	}
}
