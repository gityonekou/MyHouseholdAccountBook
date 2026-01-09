/**
 * 「通常収入金額合計」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/01/02 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.Money;
import com.yonetani.webapp.accountbook.domain.type.common.RegularIncomeAmount;

/**
 *<pre>
 * 「通常収入金額合計」項目の値を表すドメインタイプです。
 * 「収入金額」項目の合算値が「通常収入金額合計」項目の値となります。
 * （※収支登録時に収入区分(004)の区分「1,2,4」で登録した金額の合算値で、積立金取崩金額は含みません）
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
public class RegularIncomeTotalAmount extends Money {

	/** 値が0の「通常収入金額合計」項目の値 */
	public static final RegularIncomeTotalAmount ZERO = new RegularIncomeTotalAmount(Money.MONEY_ZERO);
	/**
	 *<pre>
	 * プライベートコンストラクタ
	 *</pre>
	 * @param value 通常収入金額合計
	 *
	 */
	private RegularIncomeTotalAmount(BigDecimal value) {
		super(value);
	}

	/**
	 *<pre>
	 * 「通常収入金額合計」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・null値
	 * ・マイナス値
	 * ・スケール値が2以外
	 *
	 *</pre>
	 * @param value 通常収入金額合計
	 * @return 「通常収入金額合計」項目ドメインタイプ
	 *
	 */
	public static RegularIncomeTotalAmount from(BigDecimal value) {
		// 基底クラスのバリデーションを実行（null非許容、スケール2チェック）
		validate(value, "通常収入金額合計");

		// ガード節(マイナス値)
		if(BigDecimal.ZERO.compareTo(value) > 0) {
			throw new MyHouseholdAccountBookRuntimeException(
				"「通常収入金額合計」項目の設定値がマイナスです。管理者に問い合わせてください。[value=" + value.intValue() + "]");
		}

		// 「通常収入金額合計」項目の値を生成して返却
		return new RegularIncomeTotalAmount(value);
	}

	/**
	 *<pre>
	 * 通常収入金額合計の値を指定した通常収入金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する通常収入金額の値
	 * @return 加算した通常収入金額合計の値(this + addValue)
	 *
	 */
	public RegularIncomeTotalAmount add(RegularIncomeAmount addValue) {
		return RegularIncomeTotalAmount.from(super.add(addValue));
	}
}
