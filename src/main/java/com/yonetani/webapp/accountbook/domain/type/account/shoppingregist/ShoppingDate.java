/**
 * 「買い物日」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/24 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingregist;

import java.time.LocalDate;
import java.util.Objects;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「買い物日」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class ShoppingDate {
	// 買い物日
	private final LocalDate value;
	
	/**
	 *<pre>
	 * 「買い物日」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・null
	 * ・対象年月の範囲内でない
	 *</pre>
	 * @param date 買い物日
	 * @return 「買い物日」項目ドメインタイプ
	 *
	 */
	public static ShoppingDate from(LocalDate date, TargetYearMonth targetYearMonth) {
		// ガード節(空文字列)
		if(date == null) {
			throw new MyHouseholdAccountBookRuntimeException("「買い物日」項目の設定値がnullです。管理者に問い合わせてください。");
		}
		/*
		 *  以下の方法では最終日の確認でdateの値に時間が含まれる場合にNGとなるので、Dataの値の年月の部分が等しいかどうかで判定するように修正
		 */
//		// 対象年月の月初めの日を取得
//		LocalDate firstDayOfMonth = LocalDate.parse(checkYearMonth.getValue() + "01", MyHouseholdAccountBookContent.DATE_TIME_FORMATTER);
//		// 対象月の最終日を取得
//		LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());
//		// ガード節(対象年月の範囲内でない)
//		if(firstDayOfMonth.isBefore(date) || date.isAfter(lastDayOfMonth)) {
//			throw new MyHouseholdAccountBookRuntimeException("「買い物日」項目の設定値の範囲が対象年月の範囲と一致しません。管理者に問い合わせてください。[TargetYearMonth=" 
//					+ checkYearMonth + "][value=" + DomainCommonUtils.formatyyyyMMdd(date) +"]");
//		}
		// 入力した買い物日のyyyyMMの値を取得
		String yearMonth = date.format(MyHouseholdAccountBookContent.YEAR_MONTH_FORMATTER);
		// ガード節(対象年月の範囲内でない)
		if(!Objects.equals(targetYearMonth.getValue(), yearMonth)) {
			throw new MyHouseholdAccountBookRuntimeException("「買い物日」項目の設定値の範囲が対象年月の範囲と一致しません。管理者に問い合わせてください。[TargetYearMonth=" 
					+ targetYearMonth.getValue() + "][value=" + DomainCommonUtils.formatyyyyMMdd(date) +"]");
		}
		return new ShoppingDate(date);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return DomainCommonUtils.formatyyyySPMMSPdd(value);
	}
}
