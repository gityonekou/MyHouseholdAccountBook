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
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
 * @since 家計簿アプリ(1.00.A)
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
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public static class FixedCostItem {
		// 固定費コード
		private final String fixedCostCode;
		// 支出項目名
		private final String sisyutuItemName;
		// 支払名
		private final String shiharaiName;
		// 支払月
		private final String shiharaiTuki;
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
		 * @param shiharaiKingaku 支払金額
		 * @param optionalContext その他任意詳細
		 * @return 固定費一覧情報の明細データ
		 *
		 */
		public static FixedCostItem from(String fixedCostCode, String sisyutuItemName, String shiharaiName,
				String shiharaiTuki, String shiharaiKingaku, String optionalContext) {
			return new FixedCostItem(fixedCostCode, sisyutuItemName, shiharaiName,
					shiharaiTuki, shiharaiKingaku, optionalContext);
		}
	}
	
	// 固定費一覧情報
	private List<FixedCostItem> fixedCostItemList = new ArrayList<>();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ModelAndView createModelAndView(String viewName) {
		ModelAndView modelAndView = super.createModelAndView(viewName);
		
		// 固定費一覧情報を設定
		modelAndView.addObject("fixedCostItemList", fixedCostItemList);
		
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
