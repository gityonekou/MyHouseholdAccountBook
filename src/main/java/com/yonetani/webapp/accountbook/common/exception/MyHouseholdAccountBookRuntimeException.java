/**
 * 家計簿処理で例外発生時にスローする非検査例外です。
 * 上位に画面返却情報を返却する場合その情報をラップします。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/27 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.common.exception;

/**
 *<pre>
 * 家計簿処理で例外発生時にスローする非検査例外です。
 * 上位に画面返却情報を返却する場合その情報をラップします。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public class MyHouseholdAccountBookRuntimeException extends RuntimeException {
	/**
	 *<pre>
	 * MyHouseholdAccountBookRuntimeExceptionクラスコンストラクターです。
	 *</pre>
	 * @param message
	 *
	 */
	public MyHouseholdAccountBookRuntimeException(String message) {
		super(message);
	}
	
	/**
	 *<pre>
	 * MyHouseholdAccountBookRuntimeExceptionクラスコンストラクターです。
	 *</pre>
	 * @param ex
	 *
	 */
	public MyHouseholdAccountBookRuntimeException(Exception ex) {
		super(ex);
	}
	
	/**
	 *<pre>
	 * MyHouseholdAccountBookRuntimeExceptionクラスコンストラクターです。
	 *</pre>
	 * @param message
	 * @param ex
	 *
	 */
	public MyHouseholdAccountBookRuntimeException(String message, Exception ex) {
		super(message, ex);
	}
}
