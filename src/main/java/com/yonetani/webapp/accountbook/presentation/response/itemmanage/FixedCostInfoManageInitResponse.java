/**
 * 情報管理(固定費)の初期表示画面レスポンス情報です。
 * 固定費情報の一覧、固定費を登録する対象の支出項目一覧情報を持ちます。
 * 支出項目一覧から選択した支出項目に属する固定費が登録済みの場合、その旨を注意喚起する
 * 表示エリアを持ちます。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/21 : 1.00.00  新規作成
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
import lombok.Setter;

/**
 *<pre>
 * 情報管理(固定費)の初期表示画面レスポンス情報です。
 * 固定費情報の一覧、固定費を登録する対象の支出項目一覧情報を持ちます。
 * 支出項目一覧から選択した支出項目に属する固定費が登録済みの場合、その旨を注意喚起する
 * 表示エリアを持ちます。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FixedCostInfoManageInitResponse extends AbstractFixedCostItemListResponse {
	
	/**
	 *<pre>
	 * 支出項目コード情報(選択した支出項目コードに属する固定費が登録済みの場合、値を設定)
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public static class SisyutuItemCodeInfo {
		// 支出項目コード
		private final String sisyutuItemCode;
		
		/**
		 *<pre>
		 * 引数の値から支出項目コード情報を生成して返します。
		 *</pre>
		 * @param sisyutuItemCode 支出項目コード
		 * @return 支出項目コード情報
		 *
		 */
		public static SisyutuItemCodeInfo from(String sisyutuItemCode) {
			return new SisyutuItemCodeInfo(sisyutuItemCode);
		}
	}
	
	// 登録済み表示エリアを表示するかどうかのフラグ
	private final boolean registeredFlg;
	
	// 選択した支出項目コード
	@Setter
	private SisyutuItemCodeInfo sisyutuItemCodeInfo;
	
	// 既に登録済みの支出項目の固定費一覧
	private List<FixedCostItem> registeredFixedCostInfoList = new ArrayList<>(); 
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @param registeredFlg 登録済み表示エリアを表示するかどうかのフラグ
	 * @return 情報管理(更新)初期表示画面情報
	 *
	 */
	public static FixedCostInfoManageInitResponse getInstance(boolean registeredFlg) {
		return new FixedCostInfoManageInitResponse(registeredFlg);
	}
	
	/**
	 *<pre>
	 * 既に登録済みの支出項目の固定費一覧明細リストを追加します。
	 *</pre>
	 * @param addList 追加する固定費一覧明細リスト
	 *
	 */
	public void addRegisteredFixedCostInfoList(List<FixedCostItem> addList) {
		if(!CollectionUtils.isEmpty(addList)) {
			registeredFixedCostInfoList.addAll(addList);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("itemmanage/FixedCostInfoManageInit");
		// 登録済み表示エリアを表示するかどうかのフラグ
		modelAndView.addObject("registeredFlg", registeredFlg);
		// 選択した支出項目コード情報
		modelAndView.addObject("sisyutuItemCodeInfo", sisyutuItemCodeInfo);
		// 既に登録済みの支出項目の固定費一覧情報を設定
		modelAndView.addObject("registeredFixedCostInfoList", registeredFixedCostInfoList);
		
		return modelAndView;
	}

}
