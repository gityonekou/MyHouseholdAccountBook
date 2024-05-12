/**
 * 情報管理(支出項目)画面の初期表示時、情報更新時に表示する支出項目一覧表示エリア表示情報です。
 * 以下画面で支出項目一覧表示エリア表示情報を使用しますので、このクラスを継承してレスポンスを生成してください。
 * ・情報管理(支出項目)画面(初期表示)
 * ・情報管理(支出項目)画面(支出項目登録・更新)
 * ・情報管理(商品)の初期表示画面
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 情報管理(支出項目)画面の初期表示時、情報更新時に表示する支出項目一覧表示エリア表示情報です。
 * 以下画面で支出項目一覧表示エリア表示情報を使用しますので、このクラスを継承してレスポンスを生成してください。
 * ・情報管理(支出項目)画面(初期表示)
 * ・情報管理(支出項目)画面(支出項目登録・更新)
 * ・情報管理(商品)の初期表示画面
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractExpenditureItemInfoManageResponse extends AbstractResponse {
	
	/**
	 *<pre>
	 * 画面表示する支出項目一覧情報の明細データです。
	 * このデータは、画面に出力するデータとなります。サービスから設定する支出項目一覧情報はSisyutuItemクラスを使用してください。
	 * 支出項目一覧の1行のデータと、子に属する支出項目一覧がある場合はその明細情報を含んでいます。
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	private static class ExpenditureItem {
		// 支出項目コード
		private final String sisyutuItemCode;
		// 支出項目名
		private final String sisyutuItemName;
		// 支出項目詳細内容
		private final String sisyutuItemDetailContext;
		// 支出項目レベル
		private final String sisyutuItemLevel;
		// 更新可否フラグ
		private final boolean enableUpdateFlg;
		// 子アイテムのリスト
		private List<ExpenditureItem> childItemList = new ArrayList<>();
		
		/**
		 *<pre>
		 * 引数の値から支出項目一覧情報の明細データを生成して返します。
		 *</pre>
		 * @param sisyutuItemCode 支出項目コード
		 * @param sisyutuItemName 支出項目名
		 * @param sisyutuItemDetailContext 支出項目詳細内容
		 * @param sisyutuItemLevel 支出項目レベル
		 * @param enableUpdateFlg 更新可否フラグ
		 * @return 支出項目一覧情報の明細データ
		 *
		 */
		private static ExpenditureItem from(
				String sisyutuItemCode,
				String sisyutuItemName,
				String sisyutuItemDetailContext,
				String sisyutuItemLevel,
				boolean enableUpdateFlg) {
			ExpenditureItem returnItem = new ExpenditureItem(sisyutuItemCode, sisyutuItemName, sisyutuItemDetailContext,
					sisyutuItemLevel, enableUpdateFlg);
			return returnItem;
		}
		
		/**
		 *<pre>
		 * 支出項目情報の子データを追加します
		 *</pre>
		 * @param addItem 追加する支出項目情報
		 *
		 */
		private void addChildItem(ExpenditureItem addItem) {
			childItemList.add(addItem);
		}
	}
	
	/**
	 *<pre>
	 * 支出項目の明細データです。
	 * このデータはサービスから設定する支出項目一覧情報となります。画面に出力する支出項目一覧情報はExpenditureItemを使用してください。
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public static class SisyutuItemInfo {
		// 支出項目コード
		private final String sisyutuItemCode;
		// 支出項目名
		private final String sisyutuItemName;
		// 支出項目詳細内容
		private final String sisyutuItemDetailContext;
		// 親支出項目コード
		private final String parentSisyutuItemCode;
		// 支出項目レベル
		private final String sisyutuItemLevel;
		// 更新可否フラグ
		private final boolean enableUpdateFlg;
		
		/**
		 *<pre>
		 * 引数の値から支出項目一覧情報の明細データを生成して返します。
		 *</pre>
		 * @param sisyutuItemCode 支出項目コード
		 * @param sisyutuItemName 支出項目名
		 * @param sisyutuItemDetailContext 支出項目詳細内容
		 * @param parentSisyutuItemCode 親支出項目コード
		 * @param sisyutuItemLevel 支出項目レベル
		 * @param enableUpdateFlg 更新可否フラグ
		 * @return 支出項目一覧情報の明細データ
		 *
		 */
		public static SisyutuItemInfo from(
				String sisyutuItemCode,
				String sisyutuItemName,
				String sisyutuItemDetailContext,
				String parentSisyutuItemCode,
				String sisyutuItemLevel,
				boolean enableUpdateFlg) {
			return new SisyutuItemInfo(sisyutuItemCode, sisyutuItemName, sisyutuItemDetailContext,
					parentSisyutuItemCode, sisyutuItemLevel, enableUpdateFlg);
		}
	}
	
	// 支出項目一覧情報の明細データ
	private List<ExpenditureItem> expenditureItemList = new ArrayList<>();
	
	/**
	 *<pre>
	 * 支出項目一覧情報の明細リストを追加します。
	 *</pre>
	 * @param addList 追加する支出項目一覧情報(SisyutuItemResponse)の明細リスト
	 *
	 */
	public void addSisyutuItemResponseList(List<SisyutuItemInfo> addList) {
		if(!CollectionUtils.isEmpty(addList)) {
			
			// 各支出項目に応じたマップを作成
			HashMap<String, ExpenditureItem> expenditureItemMap = new HashMap<>();
			
			// 各レベルに応じて、親子関係を意識したExpenditureItemのデータとして登録します。
			for(SisyutuItemInfo item : addList) {
				// 画面出力する支出項目データを作成
				ExpenditureItem outItem = ExpenditureItem.from(
						item.getSisyutuItemCode(),
						item.getSisyutuItemName(),
						item.getSisyutuItemDetailContext(),
						item.getSisyutuItemLevel(), 
						item.isEnableUpdateFlg());
				
				// マップから親のデータを取得
				ExpenditureItem parentItem = expenditureItemMap.get(item.getParentSisyutuItemCode());
				if(parentItem != null) {
					// マップに登録済みデータ(何らかの子データである場合)
					// 親コードがマップに登録済みデータ=(レベル2以降のデータ)なので、対応する親データに子データを追加
					parentItem.addChildItem(outItem);
				} else {
					// マップに未登録(レベル1のデータ)の場合、expenditureItemListにデータを追加
					expenditureItemList.add(outItem);
				}
				// 自分自身の支出項目コードでマップに登録
				expenditureItemMap.put(outItem.getSisyutuItemCode(), outItem);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 支出項目一覧情報をModelAndViewに追加
	 * 
	 */
	@Override
	protected ModelAndView createModelAndView(String viewName) {
		ModelAndView modelAndView = super.createModelAndView(viewName);
		// 支出項目一覧情報を設定
		modelAndView.addObject("expenditureItemList", expenditureItemList);
		return modelAndView;
	}
}
