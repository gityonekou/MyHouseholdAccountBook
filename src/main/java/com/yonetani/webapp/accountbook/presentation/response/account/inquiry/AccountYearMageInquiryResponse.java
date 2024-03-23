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
	public static class MageInquiryListItem {
		// 対象月
		private final String month;
		// 収入金額
		private final String syuunyuuKingaku;
		// 支出金額
		private final String sisyutuKingaku;
		// 支出予定金額
		private final String sisyutuYoteiKingaku ;
		// 収支
		private final String syuusiKingaku;
		/**
		 *<pre>
		 * 引数の値から年間収支(マージ)情報の明細データを生成して返します。
		 *</pre>
		 * @param month 対象月(TARGET_MONTH)
		 * @param syuunyuuKingaku 収入金額(INCOME_KINGAKU)
		 * @param sisyutuKingaku 支出金額(EXPENSE_KINGAKU)
		 * @param sisyutuYoteiKingaku 支出予定金額(EXPENSE_YOTEI_KINGAKU)
		 * @param syuusiKingaku 収支(SYUUSI_KINGAKU)
		 * @return 年間収支(マージ)情報の明細データ
		 *
		 */
		public static MageInquiryListItem from(
				String month,
				String syuunyuuKingaku,
				String sisyutuKingaku,
				String sisyutuYoteiKingaku,
				String syuusiKingaku) {
			return new MageInquiryListItem(
					month,
					syuunyuuKingaku,
					sisyutuKingaku,
					sisyutuYoteiKingaku,
					syuusiKingaku) {
			};
		}
	}
	// 対象年度(YYYY)
	@Setter
	private String year;
	
	// 現在の対象年月(YYYYMM:ユーザ情報テーブル設定値)
	@Setter
	private String targetYearMonth;
	
	// 年間収支(マージ)情報のリストです。
	private List<MageInquiryListItem> mageInquiryList = new ArrayList<>();
	
	// 収入金額合計
	@Setter
	private String syuunyuuKingakuGoukei;
	// 支出金額合計
	@Setter
	private String sisyutuKingakuGoukei;
	// 支出予定金額合計
	@Setter
	private String sisyutuYoteiKingakuGoukei;
	// 収支合計
	@Setter
	private String syuusiKingakuGoukei;
	
	/**
	 *<pre>
	 * 指定年の収支取得用リクエスト情報からレスポンス情報を生成して返します。
	 *</pre>
	 * @param request 指定年の収支取得用リクエスト情報
	 * @return マイ家計簿の年間収支(マージ)画面表示情報
	 *
	 */
	public static AccountYearMageInquiryResponse getInstance(YearInquiryForm request) {
		AccountYearMageInquiryResponse res = new AccountYearMageInquiryResponse();
		res.setYear(request.getTargetYear());
		return res;
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
		// 対象年度、現在の対象年月、前年度、翌年度の値を設定
		setTargetYear(modelAndView);
		// 現在の対象年月(form)
		modelAndView.addObject("targetYearMonth", targetYearMonth);
		// 月毎の支出項目明細リストを追加
		modelAndView.addObject("mageInquiryList", mageInquiryList);
		// 収入金額合計
		modelAndView.addObject("syuunyuuKingakuGoukei", syuunyuuKingakuGoukei);
		// 支出金額合計
		modelAndView.addObject("sisyutuKingakuGoukei", sisyutuKingakuGoukei);
		// 支出予定金額合計
		modelAndView.addObject("sisyutuYoteiKingakuGoukei", sisyutuYoteiKingakuGoukei);
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
		AccountYearMageInquiryResponse res = new AccountYearMageInquiryResponse();
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
