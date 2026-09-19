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
 * 日付       : version  ブランチ            コメントなど
 * 2024/06/08 : 1.00.00                      新規作成
 * 2026/05/07 : 1.01.00  feature-1.01-dev2   固定費合計表示変更(奇数月/偶数月合計→3か月合計)
 * 2026/05/23 : 1.02.00  feature-1.01-dev3   月別固定費一覧新規追加対応
 * 2026/05/27 : 1.02.01  feature-1.01-dev3   targetMonthValue フィールド削除（月別固定費一覧タブリンクをセッション管理に変更）
 * 2026/08/18 : 1.03.00  feature-1.03-dev1   支払方法・銀行口座管理追加対応(Feature1.03 dev1)
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage.fixedcost;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.application.usecase.account.component.PaymentMethodInfoComponent.ResolvedPaymentMethodName;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemName;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostCode;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostName;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostPaymentAmount;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostTargetPaymentMonthOptionalContext;
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
		// 支払方法名（解決済み）
		private final String paymentMethodName;
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
		 * @param paymentMethodName 支払方法名（解決済み）
		 * @param shiharaiKingaku 支払金額
		 * @param optionalContext その他任意詳細
		 * @return 固定費一覧情報の明細データ
		 *
		 */
		public static FixedCostItem from(FixedCostCode fixedCostCode, ExpenditureItemName sisyutuItemName,
				FixedCostName shiharaiName, String shiharaiTuki, String shiharaiDay,
				ResolvedPaymentMethodName paymentMethodName, FixedCostPaymentAmount shiharaiKingaku,
				FixedCostTargetPaymentMonthOptionalContext optionalContext) {
			return new FixedCostItem(
					// 固定費コード
					fixedCostCode.getValue(),
					// 支出項目名
					sisyutuItemName.getValue(),
					// 支払名
					shiharaiName.getValue(),
					// 支払月
					shiharaiTuki,
					// 支払日
					shiharaiDay,
					// 支払方法名（解決済み）
					paymentMethodName.getValue(),
					// 支払金額
					shiharaiKingaku.toFormatString(),
					// その他任意詳細
					optionalContext.getValue());
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
