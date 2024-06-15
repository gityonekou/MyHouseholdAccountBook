/**
 * 情報管理(固定費)の処理選択画面表示情報です。
 * 固定費情報の一覧と選択した固定費から更新・削除のアクション選択時の画面表示情報を持ちます。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/22 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage;

import org.springframework.web.servlet.ModelAndView;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 情報管理(固定費)の処理選択画面表示情報です。
 * 固定費情報の一覧と選択した固定費から更新・削除のアクション選択時の画面表示情報を持ちます。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FixedCostInfoManageActSelectResponse extends AbstractFixedCostItemListResponse {
	
	/**
	 *<pre>
	 * 選択固定費の詳細情報です。
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public static class SelectFixedCostInfo {
		// 固定費コード
		private final String fixedCostCode;
		// 支出項目名
		private final String sisyutuItemName;
		// 支払名
		private final String shiharaiName;
		// 支払内容詳細
		private final String shiharaiDetailContext;
		// 支払月詳細：支払月 or その他任意(その他任意の詳細をここに表示)
		private final String shiharaiTukiDetailContext;
		// 支払日
		private final String shiharaiDay;
		// 支払金額
		private final String shiharaiKingaku;
		
		/**
		 *<pre>
		 * 引数の値から選択固定費の詳細情報を生成して返します。
		 *</pre>
		 * @param fixedCostCode 固定費コード
		 * @param sisyutuItemName 支出項目名
		 * @param shiharaiName 支払名
		 * @param shiharaiDetailContext 支払内容詳細
		 * @param shiharaiTukiDetailContext 支払月詳細
		 * @param shiharaiDay 支払日
		 * @param shiharaiKingaku 支払金額
		 * @return 選択固定費の詳細情報
		 *
		 */
		public static SelectFixedCostInfo from(String fixedCostCode, String sisyutuItemName, String shiharaiName,
				String shiharaiDetailContext, String shiharaiTukiDetailContext, String shiharaiDay,
				String shiharaiKingaku) {
			return new SelectFixedCostInfo(fixedCostCode, sisyutuItemName, shiharaiName,
					shiharaiDetailContext, shiharaiTukiDetailContext, shiharaiDay,
					shiharaiKingaku);
		}
	}
	
	// 選択した商品の情報
	private final SelectFixedCostInfo fixedCostInfo;
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @param fixedCostInfo 選択固定費の詳細情報
	 * @return 情報管理(固定費)処理選択画面表示情報
	 *
	 */
	public static FixedCostInfoManageActSelectResponse getInstance(SelectFixedCostInfo fixedCostInfo) {
		return new FixedCostInfoManageActSelectResponse(fixedCostInfo);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("itemmanage/FixedCostInfoManageActSelect");
		// 選択した固定費の情報を設定
		modelAndView.addObject("fixedCostInfo", fixedCostInfo);
		return modelAndView;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getRedirectUrl() {
		// 固定費削除完了後、リダイレクトするURL
		return "redirect:/myhacbook/managebaseinfo/fixedcostinfo/updateComplete/";
	}
}
