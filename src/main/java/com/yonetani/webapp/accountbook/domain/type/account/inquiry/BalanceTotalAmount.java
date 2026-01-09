/**
 * 「収支金額合計」項目の値を表すドメインタイプです。
 * リファクタリングにより、クラス名変更しました(SyuusiKingakuTotalAmount → BalanceTotalAmount)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/01/02 : 1.00.00  新規作成(リファクタリング対応)
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.domain.type.common.BalanceAmount;
import com.yonetani.webapp.accountbook.domain.type.common.Money;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「収支金額合計」項目の値を表すドメインタイプです
 * ・収支金額は収入金額合計(「収入金額」項目と「積立金取崩金額」項目の合算値) - 支出金額で計算されます
 * ・マイナス値も許可されます（赤字の場合）
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class BalanceTotalAmount extends Money {

	/** 値が0の「収支金額合計」項目の値 */
	public static final BalanceTotalAmount ZERO = BalanceTotalAmount.from(Money.MONEY_ZERO);
	
	
	/**
	 *<pre>
	 * コンストラクタ（privateでファクトリメソッド経由のみ生成可能）
	 *</pre>
	 * @param value 収支金額合計
	 *
	 */
	private BalanceTotalAmount(BigDecimal value) {
		super(value);
	}
	
	/**
	 *<pre>
	 * 「収支金額合計」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・null値
	 * ・スケール値が2以外
	 * 
	 *</pre>
	 * @param value 収支金額合計
	 * @return 「収支金額合計」項目ドメインタイプ
	 *
	 */
	public static BalanceTotalAmount from(BigDecimal value) {
		// 基底クラスのバリデーションを実行（null非許容、スケール2チェック）
		validate(value, "収支金額合計");

		// 収支金額合計はマイナス値も許可
		return new BalanceTotalAmount(value);
	}
	
	/**
	 *<pre>
	 * 収支金額合計の値を指定した収支金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する収支金額の値
	 * @return 加算した収支金額合計の値(this + addValue)
	 *
	 */
	public BalanceTotalAmount add(BalanceAmount addValue) {
		return BalanceTotalAmount.from(super.add(addValue));
	}
	
	/**
	 *<pre>
	 * 収支金額合計が赤字（マイナス）かどうかを判定します。
	 *</pre>
	 * @return 赤字の場合true、黒字または0の場合false
	 *
	 */
	public boolean isDeficit() {
		return this.isNegative();
	}

	/**
	 *<pre>
	 * 収支金額合計が黒字（プラス）かどうかを判定します。
	 *</pre>
	 * @return 黒字の場合true、赤字または0の場合false
	 *
	 */
	public boolean isSurplus() {
		return this.isPositive();
	}
}
