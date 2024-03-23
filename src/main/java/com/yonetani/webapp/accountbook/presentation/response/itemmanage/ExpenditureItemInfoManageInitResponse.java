/**
 * 情報管理(支出項目)の対象選択画面表示情報です。初期表示時のレスポンスデータとなります。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/03/02 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage;

import org.springframework.web.servlet.ModelAndView;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 情報管理(支出項目)の対象選択画面表示情報です。初期表示時のレスポンスデータとなります。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpenditureItemInfoManageInitResponse extends AbstractExpenditureItemInfoManageResponse {

	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @return 情報管理(支出項目)の対象選択画面表示情報
	 *
	 */
	public static ExpenditureItemInfoManageInitResponse getInstance() {
		return new ExpenditureItemInfoManageInitResponse();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成して返却
		return createModelAndView("itemmanage/ExpenditureItemInfoManageInit");
	}
	
	/**
	 *<pre>
	 * 画面に出力するエラーメッセージから画面返却データのModelAndViewを生成して返します。
	 *</pre>
	 * @param errorMessage 画面に出力するエラーメッセージ
	 * @return 画面返却データのModelAndView
	 *
	 */
	public ModelAndView buildBindingError(String errorMessage) {
		// エラーメッセージを設定
		addErrorMessage(errorMessage);
		// 画面表示のModelとViewを生成して返却
		return build();
	}
}
