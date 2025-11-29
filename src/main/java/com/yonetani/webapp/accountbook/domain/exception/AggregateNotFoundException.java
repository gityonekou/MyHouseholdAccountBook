/**
 * 集約が見つからない場合にスローされる例外です。
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
 * 集約が見つからない場合にスローされる例外です。
 *
 * [使用例]
 * ・リポジトリから集約を取得しようとしたが存在しない場合
 * ・指定したIDの収支が見つからない場合
 * ・指定したIDの買い物が見つからない場合
 * ・指定したIDの固定費が見つからない場合
 *
 * [責務]
 * ・集約の存在チェックエラーを表現
 * ・見つからなかった集約の種類とIDを提供
 *
 * [設計方針]
 * ・リポジトリ層からスローされることを想定
 * ・エラーメッセージに集約の型とIDを含める
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
public class AggregateNotFoundException extends DomainException {

	/**
	 *<pre>
	 * AggregateNotFoundExceptionクラスのコンストラクターです。
	 *</pre>
	 * @param message エラーメッセージ
	 *
	 */
	public AggregateNotFoundException(String message) {
		super(message);
	}

	/**
	 *<pre>
	 * AggregateNotFoundExceptionクラスのコンストラクターです。
	 *</pre>
	 * @param message エラーメッセージ
	 * @param cause 原因となった例外
	 *
	 */
	public AggregateNotFoundException(String message, Exception cause) {
		super(message, cause);
	}

	/**
	 *<pre>
	 * 集約の型とIDを指定してAggregateNotFoundExceptionを生成します。
	 *</pre>
	 * @param aggregateType 集約の型名（例: "IncomeAndExpenditure"）
	 * @param aggregateId 集約のID
	 * @return AggregateNotFoundException
	 *
	 */
	public static AggregateNotFoundException of(String aggregateType, String aggregateId) {
		String message = String.format(
			"指定された%sが見つかりません。[ID=%s]",
			aggregateType,
			aggregateId
		);
		return new AggregateNotFoundException(message);
	}
}
