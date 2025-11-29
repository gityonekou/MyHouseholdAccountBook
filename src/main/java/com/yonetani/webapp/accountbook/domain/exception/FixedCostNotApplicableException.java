/**
 * 固定費が適用できない場合にスローされる例外です。
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
 * 固定費が適用できない場合にスローされる例外です。
 *
 * [使用例]
 * ・指定月に固定費が発生しない場合（スケジュールに合致しない）
 * ・有効期間外の月に固定費を適用しようとした場合
 * ・固定費から支出を生成できない状態の場合
 *
 * [責務]
 * ・FixedCost集約のビジネスルール違反を表現
 * ・適用できない理由を提供
 *
 * [業務ルール]
 * ・固定費は有効期間内かつスケジュールに合致する月のみ適用可能
 * ・この条件を満たさない場合にスロー
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
public class FixedCostNotApplicableException extends DomainException {

	/**
	 *<pre>
	 * FixedCostNotApplicableExceptionクラスのコンストラクターです。
	 *</pre>
	 * @param message エラーメッセージ
	 *
	 */
	public FixedCostNotApplicableException(String message) {
		super(message);
	}

	/**
	 *<pre>
	 * FixedCostNotApplicableExceptionクラスのコンストラクターです。
	 *</pre>
	 * @param message エラーメッセージ
	 * @param cause 原因となった例外
	 *
	 */
	public FixedCostNotApplicableException(String message, Exception cause) {
		super(message, cause);
	}
}
