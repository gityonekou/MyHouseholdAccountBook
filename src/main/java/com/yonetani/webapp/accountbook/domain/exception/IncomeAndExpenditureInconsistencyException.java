/**
 * 収支の整合性が取れていない場合にスローされる例外です。
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
 * 収支の整合性が取れていない場合にスローされる例外です。
 *
 * [使用例]
 * ・収入合計 - 支出合計 ≠ 収支（不変条件違反）
 * ・収支の計算結果が不正な場合
 * ・収入や支出の追加・更新時に整合性が崩れた場合
 *
 * [責務]
 * ・IncomeAndExpenditure集約の不変条件違反を表現
 * ・整合性エラーの詳細情報を提供
 *
 * [業務ルール]
 * ・収支 = 収入合計 - 支出合計（この等式が成り立たない場合にスロー）
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
public class IncomeAndExpenditureInconsistencyException extends DomainException {

	/**
	 *<pre>
	 * IncomeAndExpenditureInconsistencyExceptionクラスのコンストラクターです。
	 *</pre>
	 * @param message エラーメッセージ
	 *
	 */
	public IncomeAndExpenditureInconsistencyException(String message) {
		super(message);
	}

	/**
	 *<pre>
	 * IncomeAndExpenditureInconsistencyExceptionクラスのコンストラクターです。
	 *</pre>
	 * @param message エラーメッセージ
	 * @param cause 原因となった例外
	 *
	 */
	public IncomeAndExpenditureInconsistencyException(String message, Exception cause) {
		super(message, cause);
	}
}
