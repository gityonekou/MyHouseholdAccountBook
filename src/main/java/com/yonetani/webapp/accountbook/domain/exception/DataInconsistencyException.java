/**
 * データ不整合例外クラス
 *
 * 収支データが存在しないにも関わらず支出金額データが存在する場合など、
 * データ間の整合性が取れていない状態を表現します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/06 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.exception;

/**
 *<pre>
 * データ不整合例外クラス
 *
 * 収支データが存在しないにも関わらず支出金額データが存在する場合など、
 * データ間の整合性が取れていない状態を表現します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
public class DataInconsistencyException extends DomainException {

	private static final long serialVersionUID = 1L;

	/**
	 *<pre>
	 * デフォルトコンストラクタ
	 *</pre>
	 * @param message エラーメッセージ
	 *
	 */
	public DataInconsistencyException(String message) {
		super(message);
	}

	/**
	 *<pre>
	 * デフォルトコンストラクタ
	 *</pre>
	 * @param message エラーメッセージ
	 * @param cause 原因となった例外
	 *
	 */
	public DataInconsistencyException(String message, Exception cause) {
		super(message, cause);
	}
}
