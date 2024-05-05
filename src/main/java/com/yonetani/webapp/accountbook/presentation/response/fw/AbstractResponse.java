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

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.log4j.Log4j2;

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
@Log4j2
public abstract class AbstractResponse {

	// 画面に出力するメッセージ
	private List<String> messages = new ArrayList<>();
	// ログインユーザ名
	private String loginUserName;
	// 処理結果
	private boolean transactionSuccessFull = false;
	
	// エラー有無フラグ
	private boolean errorResponse = false;
	
	/**
	 *<pre>
	 * 表示メッセージ「エラー」を追加します。
	 *</pre>
	 * @param message
	 *
	 */
	public void addErrorMessage(String message) {
		messages.add(message);
		log.error(message);
		if(!errorResponse) {
			errorResponse = true;
		}
	}
	
	/**
	 *<pre>
	 * メッセージにエラーメッセージが設定されているかどうかを返します。
	 *</pre>
	 * @return メッセージにエラーメッセージが設定されているかどうか<br>
	 * レスポンスにエラーが設定されている場合、true<br>
	 * レスポンスにエラー未設定の場合、false
	 *
	 */
	public boolean isErrorResponse() {
		return errorResponse;
	}
	
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
	 * 現在設定されているメッセージのリストを返します。
	 *</pre>
	 * @return
	 *
	 */
	public List<String> getMessagesList() {
		return messages;
	}
	/**
	 *<pre>
	 * レスポンスにログインユーザ名を設定します。
	 *</pre>
	 * @param loginUserName ログインユーザ名
	 * @return このレスポンス
	 *
	 */
	public AbstractResponse setLoginUserName(String loginUserName) {
		this.loginUserName = loginUserName;
		return this;
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
		modelAndView.addObject("loginUserName", loginUserName);
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
	public boolean isTransactionSuccessFull() {
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
	 * トランザクションリが完了した場合にリダイレクトするURLを返します。
	 * デフォルトはトップ画面(トップメニュー画面)です。
	 * 遷移先を変更する場合はこのメソッドをオーバーライドして実装してください。
	 *</pre>
	 * @return トランザクションリが完了した場合にリダイレクトするURL
	 *
	 */
	protected String getRedirectUrl() {
		return "redirect:/myhacbook/topmenu/";
	}
	
	/**
	 *<pre>
	 * リダイレクト処理です。
	 *</pre>
	 * @param redirectViewName トランザクションリが完了した場合にリダイレクトするURL
	 * @param errorViewName トランザクション処理化完了しなかった場合に遷移するURL
	 * @return 画面返却データのModelAndView
	 *
	 */
	public final ModelAndView buildRedirect(RedirectAttributes redirectAttributes) {
		if(isTransactionSuccessFull()) {
			// メッセージをフラッシュスコープに設定
			CompleteRedirectMessages redirectMessages = new CompleteRedirectMessages();
			redirectMessages.setRedirectMessages(messages);
			redirectAttributes.addFlashAttribute(redirectMessages);
			
			// リダイレクト
			return new ModelAndView(getRedirectUrl());
			
		} else {
			return build();
		}
	}
	
	/**
	 *<pre>
	 * リダイレクト後の画面返却データのModelAndViewを生成して返します。
	 * 
	 *</pre>
	 * @param redirectMessages リダイレクト元から引き継いだメッセージ
	 * @return 画面返却データのModelAndView
	 *
	 */
	public ModelAndView buildComplete(CompleteRedirectMessages redirectMessages) {
		// リダイレクト元から引き継いだメッセージを画面表示メッセージに設定
		if(!CollectionUtils.isEmpty(redirectMessages.getRedirectMessages())) {
			redirectMessages.getRedirectMessages().forEach(data -> addMessage(data));
		}
		return build();
	}
}
