/**
 * 値オブジェクトに不正な値が設定された場合にスローされる例外です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.exception;

/**
 *<pre>
 * 値オブジェクトに不正な値が設定された場合にスローされる例外です。
 *
 * [使用例]
 * ・金額が負の値の場合
 * ・日付のフォーマットが不正な場合
 * ・IDが空文字の場合
 * ・必須項目がnullの場合
 * ・値の範囲が不正な場合
 *
 * [責務]
 * ・値オブジェクトのバリデーションエラーを表現
 * ・エラーメッセージで不正な値の詳細を提供
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
public class InvalidValueException extends DomainException {

	/**
	 *<pre>
	 * InvalidValueExceptionクラスのコンストラクターです。
	 *</pre>
	 * @param message エラーメッセージ
	 *
	 */
	public InvalidValueException(String message) {
		super(message);
	}

	/**
	 *<pre>
	 * InvalidValueExceptionクラスのコンストラクターです。
	 *</pre>
	 * @param message エラーメッセージ
	 * @param cause 原因となった例外
	 *
	 */
	public InvalidValueException(String message, Exception cause) {
		super(message, cause);
	}
}
