/**
 * 年間固定費合計データのドメインモデルです。
 * 固定費情報を月別・カテゴリ別に集計し、年間固定費合計画面の表示データを提供します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/05/23 : 1.01.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.fixedcost;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostPaymentAmount;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostPaymentTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostTargetPaymentMonth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 年間固定費合計データのドメインモデルです。
 * 固定費情報を月別・カテゴリ別に集計し、年間固定費合計画面の表示データを提供します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.01)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FixedCostAnnualSummaryList {

	/**
	 * 年間固定費合計の列分類を表す列挙型です。
	 */
	public enum AnnualSummaryColumn {
		JIGYOU_KEIHI,     // 事業経費
		HIKOZEI,          // 固定費(非課税)
		SEIKATSUHI,       // 固定費(生活費) ※固定費(課税)から積立(投資)・積立金を除いた値
		TSUMITATE_TOUSHI, // 積立(投資)
		TSUMITATE_KIN,    // 積立金
		IRUI_JUKYO,       // 衣類住居設備
		INSHOKU,          // 飲食日用品
		SHUMI             // 趣味娯楽
	}

	/**
	 * 固定費情報を月別・カテゴリ別に集計するための明細情報のドメイン表現です。
	 * 
	 * このクラスは固定費テーブルと支出項目テーブルを結合し、Level-1とLevel-2の祖先コード(親コード)を含む情報を保持します。
	 * この情報をもとに、月別・カテゴリ別の集計処理を行い、年間固定費合計画面の表示データが生成されます。
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public static class FixedCostAnnualSummaryItem {
		// 支払月コード
		private final FixedCostTargetPaymentMonth fixedCostTargetPaymentMonth;
		// 支払金額
		private final FixedCostPaymentAmount fixedCostPaymentAmount;
		// Level-1 祖先支出項目コード（SQL の ANCLVL1 から取得）
		private final String level1SisyutuItemCode;
		// Level-2 祖先支出項目コード（Level-1 項目の場合は null）
		private final String level2SisyutuItemCode;

		/**
		 *<pre>
		 * 引数の値から固定費情報を月別・カテゴリ別に集計するための明細情報のドメインモデルを生成して返します。
		 * 
		 *</pre>
		 * @param fixedCostTargetPaymentMonth 支払月コード
		 * @param fixedCostPaymentAmount 支払金額
		 * @param level1SisyutuItemCode Level-1 祖先支出項目コード
		 * @param level2SisyutuItemCode Level-2 祖先支出項目コード（null許容）
		 * @return 固定費年間合計明細データ
		 *
		 */
		public static FixedCostAnnualSummaryItem from(
				FixedCostTargetPaymentMonth fixedCostTargetPaymentMonth,
				FixedCostPaymentAmount fixedCostPaymentAmount,
				String level1SisyutuItemCode,
				String level2SisyutuItemCode) {
			return new FixedCostAnnualSummaryItem(
					fixedCostTargetPaymentMonth,
					fixedCostPaymentAmount,
					level1SisyutuItemCode,
					level2SisyutuItemCode);
		}
	}

	/**
	 * 月別の固定費合計を表すクラスです。
	 * 月（1〜12）と、列ごとの金額合計を保持します。
	 * 具体的な列項目は外部クラス（FixedCostAnnualSummaryList）で定義した列挙型（AnnualSummaryColumn）を参照してください。
	 * 
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class MonthlyRow {
		// 月（1〜12）
		@Getter
		private final int month;
		// 列ごとの金額合計
		private final Map<AnnualSummaryColumn, FixedCostPaymentTotalAmount> columnAmounts;

		/**
		 *<pre>
		 * 指定した列の金額合計を返します。
		 *</pre>
		 * @param column 列分類
		 * @return 金額合計
		 *
		 */
		public FixedCostPaymentTotalAmount getAmount(AnnualSummaryColumn column) {
			return columnAmounts.getOrDefault(column, FixedCostPaymentTotalAmount.ZERO);
		}

		/**
		 *<pre>
		 * 全列の金額合計を返します。
		 *</pre>
		 * @return 全列の金額合計
		 *
		 */
		public FixedCostPaymentTotalAmount getMonthTotal() {
			FixedCostPaymentTotalAmount total = FixedCostPaymentTotalAmount.ZERO;
			for (FixedCostPaymentTotalAmount amount : columnAmounts.values()) {
				total = total.add(amount);
			}
			return total;
		}
	}

	/**
	 * 年間の固定費合計を表すクラスです。
	 * 12か月分のMonthlyRowを集計した、列ごとの年間金額合計を保持します。
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class YearlyRow {
		// 列ごとの年間金額合計
		private final Map<AnnualSummaryColumn, FixedCostPaymentTotalAmount> columnAmounts;

		/**
		 *<pre>
		 * 指定した列の年間金額合計を返します。
		 *</pre>
		 * @param column 列分類
		 * @return 年間金額合計
		 *
		 */
		public FixedCostPaymentTotalAmount getAmount(AnnualSummaryColumn column) {
			return columnAmounts.getOrDefault(column, FixedCostPaymentTotalAmount.ZERO);
		}

		/**
		 *<pre>
		 * 全列の年間金額合計を返します。
		 *</pre>
		 * @return 全列の年間金額合計
		 *
		 */
		public FixedCostPaymentTotalAmount getYearTotal() {
			FixedCostPaymentTotalAmount total = FixedCostPaymentTotalAmount.ZERO;
			for (FixedCostPaymentTotalAmount amount : columnAmounts.values()) {
				total = total.add(amount);
			}
			return total;
		}
	}

	// 固定費情報集計用の明細データのリスト
	private final List<FixedCostAnnualSummaryItem> values;
	
	// 月別固定費合計(MonthlyRow)リスト
	@Getter
	private List<MonthlyRow> monthlyRows;
	
	// 年間固定費合計(YearlyRow)
	@Getter
	private YearlyRow yearlyRow;
	
	/**
	 *<pre>
	 * 引数の値から年間固定費合計データのドメインモデルを生成して返します。
	 *</pre>
	 * @param values 年間固定費合計を求めるための集計用明細データのリスト（null許容）
	 * @return 年間固定費合計データのドメインモデル
	 *
	 */
	public static FixedCostAnnualSummaryList from(List<FixedCostAnnualSummaryItem> values) {
		// 自分自身を生成
		FixedCostAnnualSummaryList summaryList = null;
		if (CollectionUtils.isEmpty(values)) {
			summaryList = new FixedCostAnnualSummaryList(new ArrayList<>());
		} else {
			summaryList = new FixedCostAnnualSummaryList(values);
		}
		
		// 月別固定費合計(MonthlyRow)リストを生成してセット
		summaryList.buildMonthlyRows();
		// 年間固定費合計(YearlyRow)を生成してセット
		summaryList.buildYearlyRow(summaryList.getMonthlyRows());
		// 生成したドメインモデルを返す
		return summaryList;
	}

	/**
	 *<pre>
	 * 固定費年間合計明細データが0件かどうかを返します。
	 *</pre>
	 * @return 0件の場合true
	 *
	 */
	public boolean isEmpty() {
		return CollectionUtils.isEmpty(values);
	}

	/**
	 *<pre>
	 * 月別固定費合計(MonthlyRow)リストを生成してフィールドの同項目に設定します
	 *</pre>
	 *
	 */
	private void buildMonthlyRows() {
		this.monthlyRows = new ArrayList<>();
		for (int month = 1; month <= 12; month++) {
			this.monthlyRows.add(buildMonthlyRow(month));
		}
	}

	/**
	 *<pre>
	 * 指定した月の月別固定費合計データを生成して返します。
	 *</pre>
	 * @param month 固定費合計データを求める対象の月（1〜12）
	 * @return 指定した月の月別固定費合計データ
	 *
	 */
	private MonthlyRow buildMonthlyRow(int month) {
		// 固定費合計明細項目の列挙型(AnnualSummaryColumn)に対応するEnumMapを生成
		Map<AnnualSummaryColumn, FixedCostPaymentTotalAmount> amounts = new EnumMap<>(AnnualSummaryColumn.class);
		
		// 固定費合計明細項目を0で初期化
		for (AnnualSummaryColumn col : AnnualSummaryColumn.values()) {
			amounts.put(col, FixedCostPaymentTotalAmount.ZERO);
		}
		// 固定費情報集計用の明細データのリストをループして、指定した月に加算すべき固定費情報の場合は
		// 該当項目を列挙型(AnnualSummaryColumn)で判定し、金額を加算
		for (FixedCostAnnualSummaryItem item : values) {
			// 指定した月に加算すべき固定費情報かどうかを判定
			if (shouldAdd(item.getFixedCostTargetPaymentMonth().getValue(), month)) {
				// 列挙型(AnnualSummaryColumn)で判定して金額を加算
				AnnualSummaryColumn col = determineColumn(item);
				amounts.put(col, amounts.get(col).add(item.getFixedCostPaymentAmount()));
			}
		}
		return new MonthlyRow(month, amounts);
	}
	
	/**
	 *<pre>
	 * 年間固定費合計データ(YearlyRow)を生成してフィールドの同項目に設定します。
	 * 12か月分のMonthlyRowを集計し、カテゴリ別の年間合計金額を計算します。
	 *</pre>
	 * @param monthlyRows 月別固定費合計データのリスト（12件）
	 *
	 */
	private void buildYearlyRow(List<MonthlyRow> monthlyRows) {
		Map<AnnualSummaryColumn, FixedCostPaymentTotalAmount> totals = new EnumMap<>(AnnualSummaryColumn.class);
		for (AnnualSummaryColumn col : AnnualSummaryColumn.values()) {
			totals.put(col, FixedCostPaymentTotalAmount.ZERO);
		}
		for (MonthlyRow monthlyRow : monthlyRows) {
			for (AnnualSummaryColumn col : AnnualSummaryColumn.values()) {
				totals.merge(col, monthlyRow.getAmount(col), FixedCostPaymentTotalAmount::add);
			}
		}
		this.yearlyRow = new YearlyRow(totals);
	}
	
	/**
	 *<pre>
	 * 固定費情報集計用の明細データの支出項目コード(祖先コード)から、列挙型(AnnualSummaryColumn)の列分類を判定して返します。
	 *</pre>
	 * @param item 固定費情報集計用の明細データ
	 * @return 列挙型(AnnualSummaryColumn)の列分類
	 *
	 */
	private AnnualSummaryColumn determineColumn(FixedCostAnnualSummaryItem item) {
		// レベル1の支出項目コード(祖先コード)を取得
		String level1Code = item.getLevel1SisyutuItemCode();
		// レベル2の支出項目コード(祖先コード)を取得（レベル1項目の場合はレベル1コードを使用）
		String level2Code = (item.getLevel2SisyutuItemCode() != null)
				? item.getLevel2SisyutuItemCode()
				: level1Code;
		
		// レベル1の支出項目コード(祖先コード)をもとに、列挙型(AnnualSummaryColumn)の列分類を判定
		switch (level1Code) {
		
			// 事業経費(0001)
			case MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_JIGYOU_KEIHI:
				return AnnualSummaryColumn.JIGYOU_KEIHI;
				
			// 固定費(非課税)(0013)
			case MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_FIXED_COST_HIKOZEI:
				return AnnualSummaryColumn.HIKOZEI;
				
			// 固定費(課税)(0023)
			case MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_FIXED_COST_KAZEI:
				
				// 積立(投資)(0031)
				if (MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_TSUMITATE_TOUSHI.equals(level2Code)) {
					return AnnualSummaryColumn.TSUMITATE_TOUSHI;
				}
				// 積立金(0033)
				if (MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_TSUMITATE_KIN.equals(level2Code)) {
					return AnnualSummaryColumn.TSUMITATE_KIN;
				}
				// 上記以外の固定費(課税)は生活費として分類
				return AnnualSummaryColumn.SEIKATSUHI;
				
			// 衣類住居設備(0045)
			case MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_IRUI_JUKYO_SETSUBI:
				return AnnualSummaryColumn.IRUI_JUKYO;
				
			// 飲食日用品(0049)
			case MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_INSHOKU_NICHIYOHIN:
				return AnnualSummaryColumn.INSHOKU;
			
			// 趣味娯楽(0056)
			case MyHouseholdAccountBookContent.SISYUTU_ITEM_CODE_SHUMI_GORAKU:
				return AnnualSummaryColumn.SHUMI;
				
			// 想定外コード(予防的フォールバック): 生活費として分類
			default:
				return AnnualSummaryColumn.SEIKATSUHI;
		}
	}
	
	/**
	 *<pre>
	 * 固定費支払月コードと対象月の値から、その月に支払いが発生するかどうかを返します。
	 * FixedCostInquiryList.shouldAdd() と同一ロジックになります。
	 *</pre>
	 * @param shiharaiTukiCode 固定費支払月コード（"00"=毎月, "20"=奇数月, "30"=偶数月, "40"=任意, "01"〜"12"=特定月）
	 * @param monthValue 対象月（1〜12）
	 * @return 指定した月に支払いが発生する場合true、そうでない場合false
	 *
	 */
	private static boolean shouldAdd(String shiharaiTukiCode, int monthValue) {
		
		// 固定費支払月コードに応じて、指定した月に支払いが発生するかどうかを判定
		switch (shiharaiTukiCode) {
		
			// "00" 毎月 と "40" その他任意 は常に支払いが発生(trueを返す)
			case MyHouseholdAccountBookContent.SHIHARAI_TUKI_EVERY_SELECTED_VALUE:
			case MyHouseholdAccountBookContent.SHIHARAI_TUKI_OPTIONAL_SELECTED_VALUE:
				return true;
				
			// "20" 奇数月
			case MyHouseholdAccountBookContent.SHIHARAI_TUKI_ODD_SELECTED_VALUE:
				return monthValue % 2 == 1;
				
			// "30" 偶数月
			case MyHouseholdAccountBookContent.SHIHARAI_TUKI_AN_EVEN_SELECTED_VALUE:
				return monthValue % 2 == 0;
				
			// それ以外は "01"〜"12" の特定月指定と想定し、整数値に変換して比較
			default:
				return Integer.parseInt(shiharaiTukiCode) == monthValue;
		}
	}
}
