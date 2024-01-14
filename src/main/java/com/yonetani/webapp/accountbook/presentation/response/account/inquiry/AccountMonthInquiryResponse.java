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

import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.YearMonthInquiryForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
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
	 * 月毎の支出項目明細です
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public static class ExpenditureItem {
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
		// 支払い済みフラグ
		private final boolean closingFlg;
		
		/**
		 *<pre>
		 * 月毎の支出項目明細を生成して返します。
		 *</pre>
		 * @param sisyutuItemLevel 支出項目レベル
		 * @param sisyutuItemName 支出項目名
		 * @param sisyutuKingaku 支出金額
		 * @param sisyutuKingakuB 支出金額b
		 * @praam percentage 支出金額B割合
		 * @param siharaiDate 支払日
		 * @param closingFlg 支払い済みフラグ
		 * @return 月毎の支出項目明細
		 *
		 */
		public static ExpenditureItem form(
				int sisyutuItemLevel,
				String sisyutuItemName,
				String sisyutuKingaku,
				String sisyutuKingakuB,
				String percentage,
				String siharaiDate,
				boolean closingFlg) {
			return new ExpenditureItem(
					sisyutuItemLevel,
					sisyutuItemName,
					sisyutuKingaku,
					sisyutuKingakuB,
					percentage,
					siharaiDate,
					closingFlg);
		}
	}
	// 対象年月度(YYYYMM)
	@Setter
	private String yearMonth;
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
	// 月毎の支出項目明細のリストです。
	private List<ExpenditureItem> expenditureItemList = new ArrayList<>();
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @return マイ家計簿の各月の収支画面表示情報
	 *
	 */
	public static AccountMonthInquiryResponse getInstance() {
		return new AccountMonthInquiryResponse();
	}
	
	/**
	 *<pre>
	 * 指定月の収支取得用リクエスト情報からレスポンス情報を生成して返します。
	 *</pre>
	 * @param request 指定月の収支取得用リクエスト情報
	 * @return マイ家計簿の各月の収支画面表示情報
	 *
	 */
	public static synchronized AccountMonthInquiryResponse getInstance(YearMonthInquiryForm request) {
		AccountMonthInquiryResponse res = new AccountMonthInquiryResponse();
		res.setYearMonth(request.getTargetYearMonth());
		return res;
	}
	
	/**
	 *<pre>
	 * 月毎の支出項目明細を追加します。
	 *</pre>
	 * @param addList 追加する月毎の支出項目明細のリスト
	 *
	 */
	public void addExpenditureItemList(List<ExpenditureItem> addList) {
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
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("account/inquiry/AccountMonth");
		// 対象年、対象月、前月、次月の値を設定
		setTargetYearMonth(modelAndView);
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
	}
	
	/**
	 *<pre>
	 * 入力値にエラーがある場合のレスポンス情報から画面返却データのModelAndViewを生成して返します。
	 *</pre>
	 * @param target (form入力値:年月)
	 * @return 画面返却データのModelAndView
	 *
	 */
	public static ModelAndView buildBindingError(YearMonthInquiryForm target) {
		AccountMonthInquiryResponse res = new AccountMonthInquiryResponse();
		// エラーメッセージを設定
		res.addMessage("リクエスト情報が不正です。管理者に問い合わせてください。key=YearMonthInquiryForm");
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = res.build();
		// バリデーションチェックを行ったform入力情報をセット
		modelAndView.addObject("yearMonthInquiryForm", target);
		return modelAndView;
	}
	
	/**
	 *<pre>
	 * 画面返却データに対象年月項目の各値を設定します。
	 * 画面表示項目の以下値を設定します。
	 * ・「yyyy年MM月度」の年の値
	 * ・「yyyy年MM月度」の月の値
	 * ・現在の対象年月
	 * ・前月の対象年月
	 * ・翌月の対象年月
	 *</pre>
	 * @param view　ModelAndView
	 *
	 */
	private void setTargetYearMonth(ModelAndView view) {
		if(StringUtils.hasLength(yearMonth)) {
			// 「yyyy年MM月度」の年の値
			view.addObject("viewYear", yearMonth.substring(0, 4));
			// 「yyyy年MM月度」の月の値
			view.addObject("viewMonth", yearMonth.substring(4));
			// 対象年月(form)
			view.addObject("targetYearMonth", yearMonth);
			// 対象年(form)
			view.addObject("targetYear", yearMonth.substring(0, 4));
			
			/* 現在の対象年月からカレンダーを生成し、前月・翌月の値を取得 */
			LocalDate yearMonthCalendar = LocalDate.parse(yearMonth + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
			DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMM");
			
			// 前月(form)
			view.addObject("beforeYearMonth", yearMonthCalendar.minusMonths(1).format(format));
			// 翌月(form)
			view.addObject("nextYearMonth", yearMonthCalendar.plusMonths(1).format(format));
		}
	}
}
