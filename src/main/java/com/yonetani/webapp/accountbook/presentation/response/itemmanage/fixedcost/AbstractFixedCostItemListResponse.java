/**
 * 情報管理(固定費)画面の固定費一覧を表示する以下画面の固定費一覧表示エリア情報です。
 * 初期表示画面が支出項目一覧を表示するので、AbstractExpenditureItemInfoManageResponseを継承しますが、
 * 以下の情報管理(固定費)の処理選択画面では支出項目一覧は表示しない情報となります。
 * 
 * 以下画面で固定費一覧を使用しますので、このクラスを継承してレスポンスを生成してください。
 * ・情報管理(固定費)初期表示画面：FixedCostInfoManageInitResponse
 * ・情報管理(固定費)処理選択画面：FixedCostInfoManageActSelectResponse
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/08 : 1.00.00  新規作成
 * 2026/05/07 : 1.01.00  固定費合計表示変更(奇数月/偶数月合計→3か月合計)
 * 2026/05/23 : 1.01.01  月別固定費一覧新規追加対応
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage.fixedcost;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.response.itemmanage.AbstractExpenditureItemInfoManageResponse;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *<pre>
 * 情報管理(固定費)画面の固定費一覧を表示する以下画面の固定費一覧表示エリア情報です。
 * 初期表示画面が支出項目一覧を表示するので、AbstractExpenditureItemInfoManageResponseを継承しますが、
 * 以下の情報管理(固定費)の処理選択画面では支出項目一覧は表示しない情報となります。
 * 
 * 以下画面で固定費一覧を使用しますので、このクラスを継承してレスポンスを生成してください。
 * ・情報管理(固定費)初期表示画面：FixedCostInfoManageInitResponse
 * ・情報管理(固定費)処理選択画面：FixedCostInfoManageActSelectResponse
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractFixedCostItemListResponse extends AbstractExpenditureItemInfoManageResponse {
	
	/**
	 *<pre>
	 * 情報管理(固定費)に表示する固定費一覧の明細情報です
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
	public static class FixedCostItem {
		// 固定費コード
		private final String fixedCostCode;
		// 支出項目名
		private final String sisyutuItemName;
		// 支払名
		private final String shiharaiName;
		// 支払月
		private final String shiharaiTuki;
		// 支払日
		private final String shiharaiDay;
		// 支払金額
		private final String shiharaiKingaku;
		// その他任意詳細
		private final String optionalContext;
		
		/**
		 *<pre>
		 * 引数の値から固定費一覧情報の明細データを生成して返します。
		 *</pre>
		 * @param fixedCostCode 固定費コード
		 * @param sisyutuItemName 支出項目名
		 * @param shiharaiName 支払名
		 * @param shiharaiTuki 支払月
		 * @param shiharaiDay 支払日
		 * @param shiharaiKingaku 支払金額
		 * @param optionalContext その他任意詳細
		 * @return 固定費一覧情報の明細データ
		 *
		 */
		public static FixedCostItem from(String fixedCostCode, String sisyutuItemName, String shiharaiName,
				String shiharaiTuki, String shiharaiDay, String shiharaiKingaku, String optionalContext) {
			return new FixedCostItem(fixedCostCode, sisyutuItemName, shiharaiName,
					shiharaiTuki, shiharaiDay, shiharaiKingaku, optionalContext);
		}
	}
	
	// 固定費一覧情報
	@Getter
	private List<FixedCostItem> fixedCostItemList = new ArrayList<>();
	// 対象月ラベル
	@Getter
	@Setter
	private String targetMonthLabel;
	// 対象月+1ラベル
	@Getter
	@Setter
	private String targetMonthPlus1Label;
	// 対象月+2ラベル
	@Getter
	@Setter
	private String targetMonthPlus2Label;
	// 対象月合計
	@Getter
	@Setter
	private String targetMonthGoukei;
	// 対象月+1合計
	@Getter
	@Setter
	private String targetMonthPlus1Goukei;
	// 対象月+2合計
	@Getter
	@Setter
	private String targetMonthPlus2Goukei;
	// 現在の対象月（月別固定費一覧タブリンク用、"MM"形式 例:"05"）
	@Getter
	@Setter
	private String targetMonthValue;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ModelAndView createModelAndView(String viewName) {
		ModelAndView modelAndView = super.createModelAndView(viewName);

		// 固定費一覧情報を設定
		modelAndView.addObject("fixedCostItemList", fixedCostItemList);
		// 対象月ラベルを設定
		modelAndView.addObject("targetMonthLabel", targetMonthLabel);
		// 対象月+1ラベルを設定
		modelAndView.addObject("targetMonthPlus1Label", targetMonthPlus1Label);
		// 対象月+2ラベルを設定
		modelAndView.addObject("targetMonthPlus2Label", targetMonthPlus2Label);
		// 対象月合計を設定
		modelAndView.addObject("targetMonthGoukei", targetMonthGoukei);
		// 対象月+1合計を設定
		modelAndView.addObject("targetMonthPlus1Goukei", targetMonthPlus1Goukei);
		// 対象月+2合計を設定
		modelAndView.addObject("targetMonthPlus2Goukei", targetMonthPlus2Goukei);
		// 月別固定費一覧タブリンク用の現在対象月を設定
		modelAndView.addObject("targetMonthValue", targetMonthValue);

		return modelAndView;
	}
	
	/**
	 *<pre>
	 * 固定費一覧明細リストを追加します。
	 *</pre>
	 * @param addList 追加する固定費一覧明細リスト
	 *
	 */
	public void addFixedCostItemList(List<FixedCostItem> addList) {
		if(!CollectionUtils.isEmpty(addList)) {
			fixedCostItemList.addAll(addList);
		}
	}
}
