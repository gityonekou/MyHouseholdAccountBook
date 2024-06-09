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

import org.springframework.web.servlet.ModelAndView;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

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
	// 登録済み表示エリアを表示するかどうかのフラグ
	private final boolean registeredFlg;
	
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
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("itemmanage/FixedCostInfoManageInit");
		// 固定費一覧
		modelAndView.addObject("registeredFlg", registeredFlg);
		return modelAndView;
	}

}
