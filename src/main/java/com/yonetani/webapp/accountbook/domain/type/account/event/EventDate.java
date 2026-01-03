/**
 * 「イベント日」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/18 : 1.00.00  新規作成
 * 2025/12/30 : 1.01.00  DateValue継承に移行（EventStartDate/EventEndDateを統合）
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.event;

import java.time.LocalDate;

import com.yonetani.webapp.accountbook.domain.type.common.DateValue;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「イベント日」項目の値を表すドメインタイプです
 *
 * このクラスはイベントの開始日・終了日の両方を表現します。
 * フィールド名（eventStartDate、eventEndDate）でコンテキストを区別します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class EventDate extends DateValue {

	/**
	 *<pre>
	 * 「イベント日」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param date イベント日
	 * @return 「イベント日」項目ドメインタイプ
	 *
	 */
	public static EventDate from(LocalDate date) {
		// 基底クラスのバリデーションを実行
		validate(date, "イベント日");
		return new EventDate(date);
	}

	/**
	 *<pre>
	 * コンストラクタ（privateで外部からの直接生成を禁止）
	 *</pre>
	 * @param value イベント日の値
	 *
	 */
	private EventDate(LocalDate value) {
		super(value);
	}
}
