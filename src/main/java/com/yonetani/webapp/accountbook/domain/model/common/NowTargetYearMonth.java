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


import org.springframework.util.StringUtils;

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
	 *  引数で指定した年、または月の値がnullまたは空文字列の場合はnullを返します。
	 *
	 */
	public static NowTargetYearMonth from(String year, String month) {
		if(!StringUtils.hasLength(year) || !StringUtils.hasLength(month)) {
			return new NowTargetYearMonth(null);
		} else {
			return new NowTargetYearMonth(TargetYearMonth.from(year + month));
		}
	}
	
	/**
	 *<pre>
	 * 対象年・月の値の検索結果が検索できなかったかどうかを判定します。
	 *</pre>
	 * @return
	 * 検索できなかった場合:true、検索出来た場合:false
	 *
	 */
	public boolean isEmpty() {
		// 値がnullの場合trueを返却、null以外の場合falseを返却
		return (yearMonth == null) ? true : false;
	}
}
