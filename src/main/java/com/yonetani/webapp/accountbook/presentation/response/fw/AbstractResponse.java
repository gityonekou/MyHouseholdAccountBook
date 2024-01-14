/**
 * 各画面情報のレスポンスの基底クラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.fw;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.servlet.ModelAndView;

/**
 *<pre>
 * 各画面情報のレスポンスの基底クラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public abstract class AbstractResponse {

	// 画面に出力するメッセージ
	private List<String> messages = new ArrayList<>();
	
	// 処理結果
	private boolean transactionSuccessFull = false;
	
	/**
	 *<pre>
	 * 表示メッセージを追加します。
	 *</pre>
	 * @param message
	 *
	 */
	public void addMessage(String message) {
		messages.add(message);
	}
	
	/**
	 *<pre>
	 * メッセージが設定されている場合trueを返します。
	 *</pre>
	 * @return メッセージが設定されている場合：true、未設定の場合：false
	 *
	 */
	public boolean hasMessages() {
		return !messages.isEmpty();
	}
	
	/**
	 *<pre>
	 * 画面表示のModelとViewを生成して返します。
	 * 画面に表示するメッセージのキーと値が設定されます
	 *</pre>
	 * @param viewName 画面へのパス
	 * @return 画面表示のModelとView
	 *
	 */
	protected ModelAndView createModelAndView(String viewName) {
		ModelAndView modelAndView = new ModelAndView(viewName);
		modelAndView.addObject("messages", messages);
		return modelAndView;
	}
	
	/**
	 *<pre>
	 * トランザクション処理がすべて正常に完了したことを設定します。
	 * このフラグが設定されている場合、コントローラーではリダイレクトを呼び出してください。
	 *</pre>
	 *
	 */
	public void setTransactionSuccessFull() {
		this.transactionSuccessFull = true;
	}
	
	/**
	 * トランザクション処理がすべて正常に完了したかどうかのフラグを取得します。
	 * @return transactionSuccessFull
	 */
	protected boolean isTransactionSuccessFull() {
		return transactionSuccessFull;
	}

	/**
	 *<pre>
	 * 現在のレスポンス情報から画面返却データのModelAndViewを生成して返します。
	 *</pre>
	 * @return 画面返却データのModelAndView
	 *
	 */
	public abstract ModelAndView build();
	
	/**
	 *<pre>
	 * リダイレクト処理です。
	 * トランザクション処理完了後、リダイレクトが必要な場合はこのメソッドをオーバーライドして実装してください。
	 *</pre>
	 * @return 画面返却データのModelAndView
	 *
	 */
	public ModelAndView buildRedirect() {
		return build();
	}
	
	/**
	 *<pre>
	 * リダイレクト後の画面返却データのModelAndViewを生成して返します。
	 * 画面表示メッセージは共通で「処理が正常に完了しました。」となります。
	 *</pre>
	 * @return
	 *
	 */
	public ModelAndView buildComplete() {
		addMessage("処理が正常に完了しました。");
		return build();
	}
}
