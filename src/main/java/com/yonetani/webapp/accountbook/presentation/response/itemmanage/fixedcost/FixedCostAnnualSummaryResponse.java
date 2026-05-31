/**
 * 年間固定費合計画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/05/23 : 1.01.00  新規作成
 * 2026/05/27 : 1.01.01  targetMonth フィールド削除（月別固定費一覧タブリンクをセッション管理に変更）
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage.fixedcost;

import java.util.List;

import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *<pre>
 * 年間固定費合計画面表示情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
public class FixedCostAnnualSummaryResponse extends AbstractResponse {

	/**
	 * 年間固定費合計の1行データ（データ行12行 + 合計行1行 = 計13行）
	 */
	@Getter
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class AnnualSummaryRowItem {
		// "01月"〜"12月"、"合計"
		private final String monthLabel;
		// 事業経費（"ー" or "XXX円"）
		private final String jigyouKeihi;
		// 固定費(非課税)
		private final String hikozei;
		// 固定費(生活費)
		private final String seikatsuhi;
		// 積立(投資)
		private final String tsumitateToushi;
		// 積立金
		private final String tsumitateKin;
		// 衣類住居設備
		private final String iruiJukyo;
		// 飲食日用品
		private final String inshoku;
		// 趣味娯楽
		private final String shumiGoraku;
		// 月合計（太字表示）
		private final String monthTotal;
		// "01"〜"12"（合計行は null）
		private final String detailMonth;
		// 偶数月フラグ（table-warning クラス適用用）
		private final boolean evenMonth;
		// 合計行フラグ
		private final boolean totalRow;

		/**
		 *<pre>
		 * 引数の値から年間固定費合計行データを生成して返します（データ行用）。
		 *</pre>
		 * @param monthLabel 月ラベル
		 * @param jigyouKeihi 事業経費
		 * @param hikozei 固定費(非課税)
		 * @param seikatsuhi 固定費(生活費)
		 * @param tsumitateToushi 積立(投資)
		 * @param tsumitateKin 積立金
		 * @param iruiJukyo 衣類住居設備
		 * @param inshoku 飲食日用品
		 * @param shumiGoraku 趣味娯楽
		 * @param monthTotal 月合計
		 * @param detailMonth 月番号文字列（"01"〜"12"）
		 * @return 年間固定費合計行データ
		 *
		 */
		public static AnnualSummaryRowItem createDataRow(
				String monthLabel, String jigyouKeihi, String hikozei, String seikatsuhi,
				String tsumitateToushi, String tsumitateKin, String iruiJukyo,
				String inshoku, String shumiGoraku, String monthTotal, String detailMonth) {
			int month = Integer.parseInt(detailMonth);
			return new AnnualSummaryRowItem(monthLabel, jigyouKeihi, hikozei, seikatsuhi,
					tsumitateToushi, tsumitateKin, iruiJukyo, inshoku, shumiGoraku,
					monthTotal, detailMonth, month % 2 == 0, false);
		}

		/**
		 *<pre>
		 * 引数の値から年間固定費合計行データを生成して返します（合計行用）。
		 *</pre>
		 * @param jigyouKeihi 事業経費合計
		 * @param hikozei 固定費(非課税)合計
		 * @param seikatsuhi 固定費(生活費)合計
		 * @param tsumitateToushi 積立(投資)合計
		 * @param tsumitateKin 積立金合計
		 * @param iruiJukyo 衣類住居設備合計
		 * @param inshoku 飲食日用品合計
		 * @param shumiGoraku 趣味娯楽合計
		 * @param monthTotal 年間合計
		 * @return 年間固定費合計行データ（合計行）
		 *
		 */
		public static AnnualSummaryRowItem createTotalRow(
				String jigyouKeihi, String hikozei, String seikatsuhi,
				String tsumitateToushi, String tsumitateKin, String iruiJukyo,
				String inshoku, String shumiGoraku, String monthTotal) {
			return new AnnualSummaryRowItem("合計", jigyouKeihi, hikozei, seikatsuhi,
					tsumitateToushi, tsumitateKin, iruiJukyo, inshoku, shumiGoraku,
					monthTotal, null, false, true);
		}
	}

	// 年間固定費合計行リスト（12行 + 合計行）
	@Getter
	@Setter
	private List<AnnualSummaryRowItem> annualSummaryRowList;

	/**
	 *<pre>
	 * レスポンス情報を生成して返します。
	 *</pre>
	 * @return 年間固定費合計画面表示情報
	 *
	 */
	public static FixedCostAnnualSummaryResponse getInstance() {
		return new FixedCostAnnualSummaryResponse();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示用のModelAndViewを生成して返す
		ModelAndView modelAndView = createModelAndView("itemmanage/fixedcost/FixedCostAnnualSummary");
		// 年間固定費合計行リストをModelに設定
		modelAndView.addObject("annualSummaryRowList", annualSummaryRowList);
		return modelAndView;
	}
}
