/**
 * マイ家計簿の年間収支(明細)画面表示情報です。
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
 * マイ家計簿の年間収支(明細)画面表示情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
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
		// 対象月
		private final String month;
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
		// 支出B
		private final String sisyutuKingakuB;
		// 支出
		private final String sisyutuKingaku;
		// 収支
		private final String syuusiKingaku;

		/**
		 *<pre>
		 * 引数の値から年間収支(明細)情報の明細データを生成して返します。
		 *</pre>
		 * @param month 対象月
		 * @param jigyouKeihiKingaku 事業経費
		 * @param koteiHikazeiKingaku 固定(非課税)
		 * @param koteiKazeiKingaku 固定(課税)
		 * @param iruiJyuukyoSetubiKingaku 衣類住居設備
		 * @param insyokuNitiyouhinKingaku 飲食日用品
		 * @param syumiGotakuKingaku 趣味娯楽
		 * @param sisyutuKingakuB 支出B
		 * @param sisyutuKingaku 支出
		 * @param syuusiKingaku 収支
		 * @return 年間収支(明細)情報の明細データ
		 *
		 */
		public static MeisaiInquiryListItem from(
				String month,
				String jigyouKeihiKingaku,
				String koteiHikazeiKingaku,
				String koteiKazeiKingaku,
				String iruiJyuukyoSetubiKingaku,
				String insyokuNitiyouhinKingaku,
				String syumiGotakuKingaku,
				String sisyutuKingakuB,
				String sisyutuKingaku,
				String syuusiKingaku) {
			return new MeisaiInquiryListItem(
					month,
					jigyouKeihiKingaku,
					koteiHikazeiKingaku,
					koteiKazeiKingaku,
					iruiJyuukyoSetubiKingaku,
					insyokuNitiyouhinKingaku,
					syumiGotakuKingaku,
					sisyutuKingakuB,
					sisyutuKingaku,
					syuusiKingaku);
		}
	}
	// 表示する年の対象年月情報
	private final AccountYearInquiryTargetYearInfo targetYearInfo;
	// 年間収支(明細)情報のリストです。
	private List<MeisaiInquiryListItem> meisaiInquiryList = new ArrayList<>();
	
	// 事業経費合計
	@Setter
	private String jigyouKeihiKingakuGoukei;
	// 固定(非課税)合計
	@Setter
	private String koteiHikazeiKingakuGoukei;
	// 固定(課税)合計
	@Setter
	private String koteiKazeiKingakuGoukei;
	// 衣類住居設備合計
	@Setter
	private String iruiJyuukyoSetubiKingakuGoukei;
	// 飲食日用品合計
	@Setter
	private String insyokuNitiyouhinKingakuGoukei;
	// 趣味娯楽合計
	@Setter
	private String syumiGotakuKingakuGoukei;
	// 支出B合計
	@Setter
	private String sisyutuKingakuBGoukei;
	// 支出合計
	@Setter
	private String sisyutuKingakuGoukei;
	// 収支合計
	@Setter
	private String syuusiKingakuGoukei;
	
	/**
	 *<pre>
	 *  指定年の収支取得用リクエスト情報からレスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearInfo 表示する対象年月情報
	 * @return マイ家計簿の年間収支(明細)画面表示情報
	 *
	 */
	public static AccountYearMeisaiInquiryResponse getInstance(AccountYearInquiryTargetYearInfo targetYearInfo) {
		return new AccountYearMeisaiInquiryResponse(targetYearInfo);
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
		// 支出B合計
		modelAndView.addObject("sisyutuKingakuBGoukei", sisyutuKingakuBGoukei);
		// 支出金額合計
		modelAndView.addObject("sisyutuKingakuGoukei", sisyutuKingakuGoukei);
		// 収支合計
		modelAndView.addObject("syuusiKingakuGoukei", syuusiKingakuGoukei);
		
		return modelAndView;
	}
}
