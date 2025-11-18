/**
 * 「収支金額」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import java.math.BigDecimal;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「収支金額」項目の値を表すドメインタイプです。
 *
 * [ビジネスルール]
 * ・収支金額は収入金額 - 支出金額で計算されます
 * ・マイナス値も許可されます（赤字の場合）
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class BalanceAmount extends Money {

	// 値が0の「収支金額」項目の値
	public static final BalanceAmount ZERO = new BalanceAmount(BigDecimal.ZERO.setScale(2));

	/**
	 *<pre>
	 * コンストラクタ（privateでファクトリメソッド経由のみ生成可能）
	 *</pre>
	 * @param value 収支金額
	 *
	 */
	private BalanceAmount(BigDecimal value) {
		super(value);
	}

	/**
	 *<pre>
	 * 「収支金額」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・null値
	 * ・スケール値が2以外
	 *
	 * 注意：収支金額はマイナス値も許可します（赤字の場合）
	 *</pre>
	 * @param value 収支金額
	 * @return 「収支金額」項目ドメインタイプ
	 *
	 */
	public static BalanceAmount from(BigDecimal value) {
		// 基本検証（null、スケール）
		Money.validate(value, "収支金額");

		// 収支金額はマイナス値も許可
		return new BalanceAmount(value);
	}

	/**
	 *<pre>
	 * 収入金額と支出金額から収支金額を計算します。
	 *
	 * 計算式：収支金額 = 収入金額 - 支出金額
	 *</pre>
	 * @param income 収入金額
	 * @param expenditure 支出金額
	 * @return 計算された収支金額
	 *
	 */
	public static BalanceAmount calculate(IncomeAmount income, ExpenditureAmount expenditure) {
		BigDecimal balance = income.getValue().subtract(expenditure.getValue());
		return new BalanceAmount(balance);
	}

	/**
	 *<pre>
	 * 収支金額が赤字（マイナス）かどうかを判定します。
	 *</pre>
	 * @return 赤字の場合true、黒字または0の場合false
	 *
	 */
	public boolean isDeficit() {
		return this.isNegative();
	}

	/**
	 *<pre>
	 * 収支金額が黒字（プラス）かどうかを判定します。
	 *</pre>
	 * @return 黒字の場合true、赤字または0の場合false
	 *
	 */
	public boolean isSurplus() {
		return this.isPositive();
	}
}
