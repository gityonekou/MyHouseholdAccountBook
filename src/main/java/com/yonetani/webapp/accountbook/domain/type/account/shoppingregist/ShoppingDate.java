/**
 * 「買い物日」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/11/24 : 1.00.00                      新規作成
 * 2025/12/21 : 1.01.00  feature-1.00-dev00  リファクタリング対応(DDD適応)
 * 2026/09/12 : 1.02.00  feature-1.03-dev1   買い物日の表示形式変更(MM日⇒MM/dd)
 * 2026/09/23 : 1.02.01  feature-1.03-dev1   追加リファクタリング対応(DateValue継承に変更)
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingregist;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.DateValue;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;

import lombok.EqualsAndHashCode;

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
@EqualsAndHashCode(callSuper = true)
public class ShoppingDate extends DateValue {

	// MM/dd形式のフォーマッター
	private static final DateTimeFormatter MM_SP_DD_FORMAT = DateTimeFormatter.ofPattern("MM/dd");
	
	/**
	 *<pre>
	 * ShoppingDateクラスコンストラクターです。
	 *</pre>
	 * @param value 買い物日
	 *
	 */
	protected ShoppingDate(LocalDate value) {
		super(value);
	}
	
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
		// 基底クラスのバリデーションを実行
		validate(date, "買い物日");
		
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
		return getValue().format(MM_SP_DD_FORMAT);
	}
}
