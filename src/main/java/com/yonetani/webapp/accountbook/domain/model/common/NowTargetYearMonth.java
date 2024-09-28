/**
 * 現在の対象年・月の値を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/08 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.common;


import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 現在の対象年・月の値を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
public class NowTargetYearMonth {
	// 年月
	private final TargetYearMonth yearMonth;
	
	/**
	 *<pre>
	 * 引数の年月の値から現在の対象年・月の値を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param year 年
	 * @param month 月
	 * @return 現在の対象年・月の値を表すドメインモデル
	 *
	 */
	public static NowTargetYearMonth from(String year, String month) {
		return new NowTargetYearMonth(TargetYearMonth.from(year + month));
	}
}
