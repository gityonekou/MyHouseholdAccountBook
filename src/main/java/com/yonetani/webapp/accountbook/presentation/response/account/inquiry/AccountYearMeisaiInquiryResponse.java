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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.YearInquiryForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
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
	// 対象年度(YYYY)
	@Setter
	private String year;
	
	// 現在の対象年月(YYYYMM:ユーザ情報テーブル設定値)
	@Setter
	private String targetYearMonth;
	
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
	 * @param request 指定年の収支取得用リクエスト情報
	 * @return マイ家計簿の年間収支(明細)画面表示情報
	 *
	 */
	public static AccountYearMeisaiInquiryResponse getInstance(YearInquiryForm request) {
		AccountYearMeisaiInquiryResponse res = new AccountYearMeisaiInquiryResponse();
		res.setYear(request.getTargetYear());
		return res;
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
		// 対象年度、現在の対象年月、前年度、翌年度の値を設定
		setTargetYear(modelAndView);
		// 現在の対象年月(form)
		modelAndView.addObject("targetYearMonth", targetYearMonth);
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
	
	/**
	 *<pre>
	 * 入力値にエラーがある場合のレスポンス情報から画面返却データのModelAndViewを生成して返します。
	 *</pre>
	 * @param target (form入力値:年)
	 * @return 画面返却データのModelAndView
	 *
	 */
	public static ModelAndView buildBindingError(YearInquiryForm target) {
		AccountYearMeisaiInquiryResponse res = new AccountYearMeisaiInquiryResponse();
		// エラーメッセージを設定
		res.addErrorMessage("リクエスト情報が不正です。管理者に問い合わせてください。key=YearInquiryForm");
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = res.build();
		// form入力情報をセット
		modelAndView.addObject("yearInquiryForm", target);
		return modelAndView;
	}
	
	/**
	 *<pre>
	 * 画面返却データに対象年度項目の各値を設定します。
	 * 画面表示項目の以下値を設定します。
	 * ・「yyyy年度」の年の値
	 * ・前年度
	 * ・翌年度
	 *</pre>
	 * @param view　ModelAndView
	 *
	 */
	private void setTargetYear(ModelAndView view) {
		if(StringUtils.hasLength(year)) {
			// 「yyyy年度」の年の値
			view.addObject("targetYear", year);
			
			/* 現在の対象年度からカレンダーを生成し、前年・翌年の値を取得 */
			LocalDate yearCalendar = LocalDate.parse(year + "0101", DateTimeFormatter.ofPattern("yyyyMMdd"));
			DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy");
			
			// 前月(form)
			view.addObject("beforeYear", yearCalendar.minusYears(1).format(format));
			// 翌月(form)
			view.addObject("nextYear", yearCalendar.plusYears(1).format(format));
		}
	}
}
