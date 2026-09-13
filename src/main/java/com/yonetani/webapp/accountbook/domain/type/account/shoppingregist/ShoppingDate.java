/**
 * 「買い物日」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/24 : 1.00.00  新規作成
 * 2025/12/21 : 1.01.00  リファクタリング対応(DDD適応)
 * 2026/09/12 : 1.03.00  買い物日の表示形式変更(Feature1.03 dev1)
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingregist;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class ShoppingDate {
	// MM/dd形式のフォーマッター
	private static final DateTimeFormatter MM_SP_DD_FORMAT = DateTimeFormatter.ofPattern("MM/dd");
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
	 *<pre>
	 * 日付を表示用形式（MM/dd）の文字列で取得します。
	 *</pre>
	 * @return MM/dd形式の文字列
	 *
	 */
	public String toFormatString() {
		return this.value.format(MM_SP_DD_FORMAT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		// ISO-8601形式（yyyy-MM-dd）で返却（デバッグ用）
		return this.value.toString();
	}
}
