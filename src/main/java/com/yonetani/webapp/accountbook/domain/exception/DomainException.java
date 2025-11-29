/**
 * ドメイン層の例外の基底クラスです。
 * すべてのドメイン層固有の例外はこのクラスを継承します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.exception;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * ドメイン層の例外の基底クラスです。
 *
 * [責務]
 * ・ドメイン層で発生する例外の統一的な表現
 * ・共通の例外処理基盤の提供
 *
 * [設計方針]
 * ・MyHouseholdAccountBookRuntimeExceptionを継承
 * ・ドメイン固有の例外情報を扱う
 * ・サブクラスで具体的な例外の型を表現
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
public class DomainException extends MyHouseholdAccountBookRuntimeException {

	/**
	 *<pre>
	 * DomainExceptionクラスのコンストラクターです。
	 *</pre>
	 * @param message エラーメッセージ
	 *
	 */
	public DomainException(String message) {
		super(message);
	}

	/**
	 *<pre>
	 * DomainExceptionクラスのコンストラクターです。
	 *</pre>
	 * @param message エラーメッセージ
	 * @param cause 原因となった例外
	 *
	 */
	public DomainException(String message, Exception cause) {
		super(message, cause);
	}

	/**
	 *<pre>
	 * DomainExceptionクラスのコンストラクターです。
	 *</pre>
	 * @param cause 原因となった例外
	 *
	 */
	public DomainException(Exception cause) {
		super(cause);
	}
}
