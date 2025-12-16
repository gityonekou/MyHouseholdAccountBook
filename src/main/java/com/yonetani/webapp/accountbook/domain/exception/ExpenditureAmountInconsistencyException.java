/**
 * 支出金額の整合性が取れていない場合にスローされる例外です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/05 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.exception;

/**
 *<pre>
 * 支出金額の整合性が取れていない場合にスローされる例外です。
 *
 * [使用例]
 * ・収支テーブルの支出金額 ≠ 支出テーブルの合計金額
 * ・支出金額の整合性チェックで不整合が検出された場合
 *
 * [責務]
 * ・支出金額の整合性違反を表現
 * ・整合性エラーの詳細情報を提供
 *
 * [業務ルール]
 * ・収支テーブルの支出金額 = 支出テーブルの合計金額
 * （この等式が成り立たない場合にスロー）
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
public class ExpenditureAmountInconsistencyException extends DomainException {

	/**
	 *<pre>
	 * ExpenditureAmountInconsistencyExceptionクラスのコンストラクターです。
	 *</pre>
	 * @param message エラーメッセージ
	 *
	 */
	public ExpenditureAmountInconsistencyException(String message) {
		super(message);
	}

	/**
	 *<pre>
	 * ExpenditureAmountInconsistencyExceptionクラスのコンストラクターです。
	 *</pre>
	 * @param message エラーメッセージ
	 * @param cause 原因となった例外
	 *
	 */
	public ExpenditureAmountInconsistencyException(String message, Exception cause) {
		super(message, cause);
	}
}
