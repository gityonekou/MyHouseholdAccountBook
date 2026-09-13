/**
 * 情報管理(固定費)一括更新画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/05/01 : 1.01.00  新規作成
 * 2026/08/18 : 1.03.00  支払方法・銀行口座管理追加対応(Feature1.03 dev1)
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage.fixedcost;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yonetani.webapp.accountbook.application.usecase.account.component.PaymentMethodInfoComponent.ResolvedPaymentMethodName;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostCode;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostName;
import com.yonetani.webapp.accountbook.domain.type.account.fixedcost.FixedCostPaymentAmount;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.FixedCostBulkUpdateForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *<pre>
 * 情報管理(固定費)一括更新画面表示情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.01)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FixedCostBulkUpdateResponse extends AbstractResponse {

	/**
	 *<pre>
	 * 一括更新対象の固定費明細情報です。
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.01)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public static class BulkUpdateTargetItem {
		// 固定費コード
		private final String fixedCostCode;
		// 支払名
		private final String shiharaiName;
		// 支払月（コード変換済み）
		private final String shiharaiTukiDetailContext;
		// 支払月任意詳細（その他任意選択時のみ値あり）
		private final String shiharaiTukiOptionalContext;
		// 現在の支払日（コード変換済み）
		private final String shiharaiDay;
		// 支払方法名（解決済み。表示のみ、一括更新の対象にはしない。5.4.2節）
		private final String paymentMethodName;
		// 現在の支払金額（フォーマット済み）
		private final String shiharaiKingaku;

		/**
		 *<pre>
		 * 引数の値から一括更新対象の固定費明細情報を生成して返します。
		 *</pre>
		 * @param fixedCostCode 固定費コード
		 * @param shiharaiName 支払名
		 * @param shiharaiTukiDetailContext 支払月（コード変換済み）
		 * @param shiharaiTukiOptionalContext 支払月任意詳細
		 * @param shiharaiDay 現在の支払日（コード変換済み）
		 * @param paymentMethodName 支払方法名（解決済み）
		 * @param shiharaiKingaku 現在の支払金額（フォーマット済み）
		 * @return 一括更新対象の固定費明細情報
		 *
		 */
		public static BulkUpdateTargetItem from(FixedCostCode fixedCostCode, FixedCostName shiharaiName,
				String shiharaiTukiDetailContext, String shiharaiTukiOptionalContext, String shiharaiDay,
				ResolvedPaymentMethodName paymentMethodName, FixedCostPaymentAmount shiharaiKingaku) {
			return new BulkUpdateTargetItem(
					// 固定費コード
					fixedCostCode.getValue(),
					// 支払名
					shiharaiName.getValue(),
					// 支払月（コード変換済み）
					shiharaiTukiDetailContext,
					// 支払月任意詳細（その他任意選択時のみ値あり）
					shiharaiTukiOptionalContext,
					// 現在の支払日（コード変換済み）
					shiharaiDay,
					// 支払方法名（解決済み。表示のみ、一括更新の対象にはしない。5.4.2節）
					paymentMethodName.getValue(),
					// 現在の支払金額（フォーマット済み）
					shiharaiKingaku.toFormatString());
		}
	}

	// 入力フォーム（一括適用値の初期値入り）
	@Getter
	private final FixedCostBulkUpdateForm fixedCostBulkUpdateForm;

	// 支払日選択ボックス
	@Getter
	private final SelectViewItem shiharaiDaySelectList;

	// 支出項目名（画面見出し用）
	@Getter
	@Setter
	private String sisyutuItemName;

	// 一括更新対象の固定費一覧
	@Getter
	private final List<BulkUpdateTargetItem> bulkUpdateTargetList = new ArrayList<>();

	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @param form 入力フォーム
	 * @param shiharaiDayOptionList 支払日選択ボックスの表示情報リスト
	 * @return 情報管理(固定費)一括更新画面表示情報
	 *
	 */
	public static FixedCostBulkUpdateResponse getInstance(
			FixedCostBulkUpdateForm form, List<OptionItem> shiharaiDayOptionList) {
		if (form == null) {
			form = new FixedCostBulkUpdateForm();
		}
		// 支払日選択ボックスの表示情報リストを生成（先頭に選択促しの選択肢を追加）
		List<OptionItem> optionDayList = new ArrayList<>();
		optionDayList.add(OptionItem.from("", "支払日を選択してください"));
		if (!CollectionUtils.isEmpty(shiharaiDayOptionList)) {
			optionDayList.addAll(shiharaiDayOptionList);
		}
		// 入力フォームと支払日選択ボックスの表示情報リストを元に情報管理(固定費)一括更新画面表示情報を生成
		return new FixedCostBulkUpdateResponse(form, SelectViewItem.from(optionDayList));
	}

	/**
	 *<pre>
	 * 一括更新対象固定費明細情報のリストを設定します。
	 *</pre>
	 * @param list 一括更新対象固定費明細情報のリスト
	 *
	 */
	public void addBulkUpdateTargetList(List<BulkUpdateTargetItem> list) {
		if (!CollectionUtils.isEmpty(list)) {
			bulkUpdateTargetList.addAll(list);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("itemmanage/fixedcost/FixedCostBulkUpdate");
		// 入力フォーム
		modelAndView.addObject("fixedCostBulkUpdateForm", fixedCostBulkUpdateForm);
		// 支払日選択ボックス
		modelAndView.addObject("shiharaiDaySelectList", shiharaiDaySelectList);
		// 支出項目名
		modelAndView.addObject("sisyutuItemName", sisyutuItemName);
		// 一括更新対象固定費一覧
		modelAndView.addObject("bulkUpdateTargetList", bulkUpdateTargetList);
		return modelAndView;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String buildRedirectUrl(RedirectAttributes redirectAttributes) {
		// 固定費一括更新完了後、リダイレクトするURL
		return "redirect:/myhacbook/managebaseinfo/fixedcostinfo/updateComplete/";
	}
}
