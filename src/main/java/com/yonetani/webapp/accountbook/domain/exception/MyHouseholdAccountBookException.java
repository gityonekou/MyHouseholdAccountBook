/**
 * 家計簿処理で例外発生時にスローする例外です。
 * 上位に画面返却情報を返却する場合その情報をラップします。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/12/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.exception;

/**
 *<pre>
 *  家計簿処理で例外発生時にスローする例外です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public class MyHouseholdAccountBookException extends Exception {
	
	/**
	 *<pre>
	 * MyHouseholdAccountBookExceptionクラスコンストラクターです。
	 *</pre>
	 * @param message
	 *
	 */
	public MyHouseholdAccountBookException(String message) {
		super(message);
	}
	
	/**
	 *<pre>
	 * MyHouseholdAccountBookExceptionクラスコンストラクターです。
	 *</pre>
	 * @param ex
	 *
	 */
	public MyHouseholdAccountBookException(Exception ex) {
		super(ex);
	}
	
	/**
	 *<pre>
	 * MyHouseholdAccountBookExceptionクラスコンストラクターです。
	 *</pre>
	 * @param message
	 * @param ex
	 *
	 */
	public MyHouseholdAccountBookException(String message, Exception ex) {
		super(message, ex);
	}
}
