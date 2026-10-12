/**
 * マイ家計簿の年間収支(明細)画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2023/10/09 : 1.00.00                      新規作成
 * 2026/10/12 : 1.01.00  feature-1.03-dev1   追加リファクタリング対応(MeisaiInquiryListItem.from()の引数をドメインタイプに変更し、表示文字列変換・割合算出呼び出しをDTO内部に移動)
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.account.inquiry;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.BalanceTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.ExpenditureTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.RegularIncomeTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.WithdrawingAmount;
import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.WithdrawingTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.InsyokuNitiyouhinKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.IruiJyuukyoSetubiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.JigyouKeihiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.KoteiHikazeiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.KoteiKazeiKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyumiGotakuKingaku;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.TotalWasteExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.WasteExpenditureTotalAmount;
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
 * マイ家計簿の年間収支(明細)画面表示情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountYearMeisaiInquiryResponse extends AbstractResponse {

	/**
	 *<pre>
	 * 年間収支(明細)情報の明細データです
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
	public static class MeisaiInquiryListItem {
		// 対象月(表示用。"MM月"形式)
		private final String month;
		// 対象月(リンクパラメータ用。"MM"形式)
		private final String monthValue;
		// 収入金額
		private final String syuunyuuKingaku;
		// 積立金取崩金額
		private final String withdrewKingaku;
		// 事業経費
		private final String jigyouKeihiKingaku;
		// 固定(非課税)
		private final String koteiHikazeiKingaku;
		// 固定(課税)
		private final String koteiKazeiKingaku;
		// 衣類住居設備
		private final String iruiJyuukyoSetubiKingaku;
		// 飲食日用品
		private final String insyokuNitiyouhinKingaku;
		// 趣味娯楽
		private final String syumiGotakuKingaku;
		// 支出BC
		private final String sisyutuKingakuBC;
		// 支出Bの割合
		private final String percentageB;
		// 支出
		private final String sisyutuKingaku;
		// 収支
		private final String syuusiKingaku;

		/**
		 *<pre>
		 * 引数の値から年間収支(明細)情報の明細データを生成して返します。
		 *</pre>
		 * @param month 対象月
		 * @param syuunyuuKingaku 収入金額
		 * @param withdrewKingaku 積立金取崩金額
		 * @param jigyouKeihiKingaku 事業経費
		 * @param koteiHikazeiKingaku 固定(非課税)
		 * @param koteiKazeiKingaku 固定(課税)
		 * @param iruiJyuukyoSetubiKingaku 衣類住居設備
		 * @param insyokuNitiyouhinKingaku 飲食日用品
		 * @param syumiGotakuKingaku 趣味娯楽
		 * @param sisyutuKingakuBC 支出BC(無駄遣い合計支出金額)
		 * @param sisyutuKingaku 支出
		 * @param syuusiKingaku 収支
		 * @return 年間収支(明細)情報の明細データ
		 *
		 */
		public static MeisaiInquiryListItem from(
				TargetMonth month,
				RegularIncomeAmount syuunyuuKingaku,
				WithdrawingAmount withdrewKingaku,
				JigyouKeihiKingaku jigyouKeihiKingaku,
				KoteiHikazeiKingaku koteiHikazeiKingaku,
				KoteiKazeiKingaku koteiKazeiKingaku,
				IruiJyuukyoSetubiKingaku iruiJyuukyoSetubiKingaku,
				InsyokuNitiyouhinKingaku insyokuNitiyouhinKingaku,
				SyumiGotakuKingaku syumiGotakuKingaku,
				TotalWasteExpenditureAmount sisyutuKingakuBC,
				ExpenditureAmount sisyutuKingaku,
				BalanceAmount syuusiKingaku) {
			return new MeisaiInquiryListItem(
					month.toFormatString(),
					month.getValue(),
					syuunyuuKingaku.toFormatString(),
					withdrewKingaku.toFormatString(),
					jigyouKeihiKingaku.toString(),
					koteiHikazeiKingaku.toString(),
					koteiKazeiKingaku.toString(),
					iruiJyuukyoSetubiKingaku.toString(),
					insyokuNitiyouhinKingaku.toString(),
					syumiGotakuKingaku.toString(),
					sisyutuKingakuBC.toFormatString(),
					sisyutuKingakuBC.getMinorWasteExpenditurePercentage(),
					sisyutuKingaku.toFormatString(),
					syuusiKingaku.toFormatString());
		}
	}
	// 表示する年の対象年月情報
	private final AccountYearInquiryTargetYearInfo targetYearInfo;
	// 収入金額合計
	private final String syuunyuuKingakuGoukei;
	// 積立金取崩金額合計
	private final String withdrewKingakuGoukei;
	// 事業経費合計
	private final String jigyouKeihiKingakuGoukei;
	// 固定(非課税)合計
	private final String koteiHikazeiKingakuGoukei;
	// 固定(課税)合計
	private final String koteiKazeiKingakuGoukei;
	// 衣類住居設備合計
	private final String iruiJyuukyoSetubiKingakuGoukei;
	// 飲食日用品合計
	private final String insyokuNitiyouhinKingakuGoukei;
	// 趣味娯楽合計
	private final String syumiGotakuKingakuGoukei;
	// 支出BC合計
	private final String sisyutuKingakuBCGoukei;
	// 支出BC合計のうち、支出B合計の割合
	private final String percentageBGoukei;
	// 支出合計
	private final String sisyutuKingakuGoukei;
	// 収支合計
	private final String syuusiKingakuGoukei;

	// 年間収支(明細)情報のリストです。
	private List<MeisaiInquiryListItem> meisaiInquiryList = new ArrayList<>();
	
	/**
	 *<pre>
	 *  指定年の収支取得用リクエスト情報から表示金額なしとなるレスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearInfo 表示する対象年月情報
	 * @return マイ家計簿の年間収支(明細)画面表示情報
	 *
	 */
	public static AccountYearMeisaiInquiryResponse getInstance(AccountYearInquiryTargetYearInfo targetYearInfo) {
		return new AccountYearMeisaiInquiryResponse(
				// 表示する年の対象年月情報
				targetYearInfo,
				// 収入金額合計
				null,
				// 積立金取崩金額合計
				null,
				// 事業経費合計
				null,
				// 固定(非課税)合計
				null,
				// 固定(課税)合計
				null,
				// 衣類住居設備合計
				null,
				// 飲食日用品合計
				null,
				// 趣味娯楽合計
				null,
				// 支出BC合計
				null,
				// 支出BC合計のうち、支出B合計の割合
				null,
				// 支出合計
				null,
				// 収支合計
				null);
	}

	/**
	 *<pre>
	 *  指定年の収支取得用リクエスト情報と合計金額情報からレスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearInfo 表示する対象年月情報
	 * @param syuunyuuKingakuGoukei 収入金額合計(積立金取崩金額以外の収入金額合計)
	 * @param withdrewKingakuGoukei 積立金取崩金額合計
	 * @param jigyouKeihiKingakuGoukei 事業経費合計
	 * @param koteiHikazeiKingakuGoukei 固定(非課税)合計
	 * @param koteiKazeiKingakuGoukei 固定(課税)合計
	 * @param iruiJyuukyoSetubiKingakuGoukei 衣類住居設備合計
	 * @param insyokuNitiyouhinKingakuGoukei 飲食日用品合計
	 * @param syumiGotakuKingakuGoukei 趣味娯楽合計
	 * @param wasteExpenditureTotalAmount 無駄遣い合計支出金額合計(支出BC合計・支出B合計の割合)
	 * @param sisyutuKingakuGoukei 支出金額合計
	 * @param syuusiKingakuGoukei 収支金額合計
	 * @return マイ家計簿の年間収支(明細)画面表示情報
	 *
	 */
	public static AccountYearMeisaiInquiryResponse getInstance(AccountYearInquiryTargetYearInfo targetYearInfo,
			RegularIncomeTotalAmount syuunyuuKingakuGoukei, WithdrawingTotalAmount withdrewKingakuGoukei,
			JigyouKeihiKingaku jigyouKeihiKingakuGoukei, KoteiHikazeiKingaku koteiHikazeiKingakuGoukei,
			KoteiKazeiKingaku koteiKazeiKingakuGoukei, IruiJyuukyoSetubiKingaku iruiJyuukyoSetubiKingakuGoukei,
			InsyokuNitiyouhinKingaku insyokuNitiyouhinKingakuGoukei, SyumiGotakuKingaku syumiGotakuKingakuGoukei,
			WasteExpenditureTotalAmount wasteExpenditureTotalAmount, ExpenditureTotalAmount sisyutuKingakuGoukei,
			BalanceTotalAmount syuusiKingakuGoukei) {
		return new AccountYearMeisaiInquiryResponse(
				// 表示する年の対象年月情報
				targetYearInfo,
				// 収入金額合計
				syuunyuuKingakuGoukei.toFormatString(),
				// 積立金取崩金額合計
				withdrewKingakuGoukei.toFormatString(),
				// 事業経費合計
				jigyouKeihiKingakuGoukei.toString(),
				// 固定(非課税)合計
				koteiHikazeiKingakuGoukei.toString(),
				// 固定(課税)合計
				koteiKazeiKingakuGoukei.toString(),
				// 衣類住居設備合計
				iruiJyuukyoSetubiKingakuGoukei.toString(),
				// 飲食日用品合計
				insyokuNitiyouhinKingakuGoukei.toString(),
				// 趣味娯楽合計
				syumiGotakuKingakuGoukei.toString(),
				// 支出BC合計
				wasteExpenditureTotalAmount.toFormatString(),
				// 支出BC合計のうち、支出B合計の割合
				wasteExpenditureTotalAmount.getMinorWasteExpenditurePercentage(),
				// 支出合計
				sisyutuKingakuGoukei.toFormatString(),
				// 収支合計
				syuusiKingakuGoukei.toFormatString());
	}
	
	/**
	 *<pre>
	 * 年間収支(明細)情報の明細リストを追加します。
	 *</pre>
	 * @param addList 追加する年間収支(明細)情報のリスト
	 *
	 */
	public void addMeisaiInquiryList(List<MeisaiInquiryListItem> addList) {
		if(!CollectionUtils.isEmpty(addList)) {
			meisaiInquiryList.addAll(addList);
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
		ModelAndView modelAndView = createModelAndView("account/inquiry/AccountYearMeisai");
		// 表示する年の対象年月情報を設定
		modelAndView.addObject("targetYearInfo", targetYearInfo);
		// 年間収支(明細)リストを追加
		modelAndView.addObject("meisaiInquiryList", meisaiInquiryList);
		// 収入金額合計
		modelAndView.addObject("syuunyuuKingakuGoukei", syuunyuuKingakuGoukei);
		// 積立金取崩金額合計
		modelAndView.addObject("withdrewKingakuGoukei", withdrewKingakuGoukei);
		// 事業経費合計
		modelAndView.addObject("jigyouKeihiKingakuGoukei", jigyouKeihiKingakuGoukei);
		// 固定(非課税)合計
		modelAndView.addObject("koteiHikazeiKingakuGoukei", koteiHikazeiKingakuGoukei);
		// 固定(課税)合計
		modelAndView.addObject("koteiKazeiKingakuGoukei", koteiKazeiKingakuGoukei);
		// 衣類住居設備合計
		modelAndView.addObject("iruiJyuukyoSetubiKingakuGoukei", iruiJyuukyoSetubiKingakuGoukei);
		// 飲食日用品合計
		modelAndView.addObject("insyokuNitiyouhinKingakuGoukei", insyokuNitiyouhinKingakuGoukei);
		// 趣味娯楽合計
		modelAndView.addObject("syumiGotakuKingakuGoukei", syumiGotakuKingakuGoukei);
		// 支出BC合計
		modelAndView.addObject("sisyutuKingakuBCGoukei", sisyutuKingakuBCGoukei);
		// 支出BC合計のうち、支出B合計の割合
		modelAndView.addObject("percentageBGoukei", percentageBGoukei);
		// 支出金額合計
		modelAndView.addObject("sisyutuKingakuGoukei", sisyutuKingakuGoukei);
		// 収支合計
		modelAndView.addObject("syuusiKingakuGoukei", syuusiKingakuGoukei);
		
		return modelAndView;
	}
}
