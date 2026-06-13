/**
 * マイ家計簿の各月の収支画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/23 : 1.00.00  新規作成
 * 2026/03/20 : 1.01.00  リファクタリング対応(DDD適応)
 * 2026/06/13 : 1.02.00  支出別一覧追加対応(viewType・ExpenditureRow・expenditureList追加)
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
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
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
	
	/**
	 *<pre>
	 * 月毎の支出情報(支出別一覧)明細です
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.02)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	@EqualsAndHashCode
	public static class ExpenditureRow {
		// 支出コード（訂正ボタンの POST パラメータ用）
		private final String expenditureCode;
		// 支出名（区分プレフィックス付き：「【無駄遣いC】」「【無駄遣いB】」または支出名のみ）
		private final String displayName;
		// 支払日（"DD日"形式、支払日なしの場合は空文字）
		private final String paymentDay;
		// 支出金額（フォーマット済み）
		private final String expenditureAmount;
		// 支出詳細
		private final String expenditureDetailContext;

		/**
		 *<pre>
		 * 月毎の支出情報(支出別一覧)明細からレスポンスDTOを生成して返します。
		 * ドメインモデルからの変換はユースケース層で行い、変換済みの値を引数に渡してください。
		 *</pre>
		 * @param expenditureCode 支出コード
		 * @param displayName 表示名（支出区分プレフィックス付き）
		 * @param paymentDay 支払日（"DD日"形式、支払日なしの場合は空文字）
		 * @param expenditureAmount 支出金額（フォーマット済み）
		 * @param expenditureDetailContext 支出詳細
		 * @return 月毎の支出情報(支出別一覧)明細レスポンスDTO
		 *
		 */
		public static ExpenditureRow from(String expenditureCode, String displayName, String paymentDay,
				String expenditureAmount, String expenditureDetailContext) {
			return new ExpenditureRow(expenditureCode, displayName, paymentDay, expenditureAmount, expenditureDetailContext);
		}
	}

	// 表示する月の対象年月情報
	private final AccountMonthInquiryTargetYearMonthInfo targetYearMonthInfo;
	// 月毎の支出金額情報明細のリストです。
	private List<ExpenditureListItem> expenditureItemList = new ArrayList<>();
	// 月毎の支出情報(支出別一覧)明細のリストです。
	private List<ExpenditureRow> expenditureList = new ArrayList<>();

	// 指定月の収支情報
	// 表示する月の収支情報が登録済みかどうかのフラグ(デフォルトはデータあり)
	@Setter
	private boolean syuusiDataFlg = true;
	// 収入金額
	@Setter
	private String syuunyuuKingaku;
	// 積立金取崩金額
	@Setter
	private String withdrewKingaku;
	// 支出金額
	@Setter
	private String sisyutuKingaku;
	// 支出予定金額
	@Setter
	private String sisyutuYoteiKingaku;
	// 収支金額
	@Setter
	private String syuusiKingaku;
	// 表示種別（"item"=支出項目別、"expenditure"=支出別）
	@Setter
	private String viewType = "item";
	// 支出合計金額（フォーマット済み）
	@Setter
	private String expenditureTotalAmount;
	
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
	 * 月毎の支出情報(支出別一覧)明細を追加します。
	 *</pre>
	 * @param addList 追加する月毎の支出情報(支出別一覧)明細のリスト
	 *
	 */
	public void addExpenditureList(List<ExpenditureRow> addList) {
		if(!CollectionUtils.isEmpty(addList)) {
			expenditureList.addAll(addList);
		}
	}
	
	/**
	 *<pre>
	 * 現在のレスポンス情報から画面返却データのModelAndViewを生成して返します。
	 * Controller層でsyuusiDataFlgをチェックし、適切なビルドメソッドを呼び出してください。
	 *
	 * 【非推奨】このメソッドは互換性のために残されていますが、新規実装では使用しないでください。
	 * 代わりにbuildWithData()またはbuildRegistCheck()を使用してください。
	 *</pre>
	 * @return 画面返却データのModelAndView
	 *
	 */
	@Override
	@Deprecated
	public ModelAndView build() {
		if(syuusiDataFlg) {
			return buildWithData();
		} else {
			return buildRegistCheck();
		}
	}

	/**
	 *<pre>
	 * 収支データありの場合の画面返却データ（AccountMonth画面）を生成して返します。
	 *</pre>
	 * @return マイ家計簿(各月の収支)画面のModelAndView
	 *
	 */
	public ModelAndView buildWithData() {
		// マイ家計簿(各月の収支)画面のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("account/inquiry/AccountMonth");
		// 対象年、対象月、前月、次月の値を設定
		modelAndView.addObject("targetYearMonthInfo", targetYearMonthInfo);
		// 収入金額
		modelAndView.addObject("syuunyuuKingaku", syuunyuuKingaku);
		// 支出金額
		modelAndView.addObject("sisyutuKingaku", sisyutuKingaku);
		// 積立金取崩金額
		modelAndView.addObject("withdrewKingaku", withdrewKingaku);
		// 支出予定金額
		modelAndView.addObject("sisyutuYoteiKingaku", sisyutuYoteiKingaku);
		// 収支金額
		modelAndView.addObject("syuusiKingaku", syuusiKingaku);
		// 月毎の支出項目明細リストを追加
		modelAndView.addObject("expenditureItemList", expenditureItemList);
		// 表示種別
		modelAndView.addObject("viewType", viewType);
		// 月毎の支出情報(支出別一覧)明細リスト
		modelAndView.addObject("expenditureList", expenditureList);
		// 支出合計金額
		modelAndView.addObject("expenditureTotalAmount", expenditureTotalAmount);
		return modelAndView;
	}

	/**
	 *<pre>
	 * 収支データなしの場合の画面返却データ（AccountMonthRegistCheck画面）を生成して返します。
	 *</pre>
	 * @return マイ家計簿(各月の収支：収支登録確認)画面のModelAndView
	 *
	 */
	public ModelAndView buildRegistCheck() {
		// マイ家計簿(各月の収支：収支登録確認)画面のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("account/inquiry/AccountMonthRegistCheck");
		// 対象年、対象月、前月、次月の値を設定
		modelAndView.addObject("targetYearMonthInfo", targetYearMonthInfo);
		return modelAndView;
	}
}
