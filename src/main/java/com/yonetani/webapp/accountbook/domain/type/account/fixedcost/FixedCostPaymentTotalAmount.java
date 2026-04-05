/**
 * 「支払金額合計」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/04/05 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.fixedcost;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.Money;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「支払金額合計」項目の値を表すドメインタイプです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class FixedCostPaymentTotalAmount extends Money {

	/** 値が0の「支払金額合計」項目の値 */
	public static final FixedCostPaymentTotalAmount ZERO = FixedCostPaymentTotalAmount.from(Money.MONEY_ZERO);
	
	/**
	 * コンストラクタ
	 * @param value 支払金額合計
	 */
	private FixedCostPaymentTotalAmount(BigDecimal value) {
		super(value);
	}
	
	/**
	 *<pre>
	 * 「支払金額合計」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・null値
	 * ・マイナス値
	 * ・スケール値が2以外
	 * 
	 *</pre>
	 * @param totalAmount 合計金額
	 * @return 「支払金額合計」項目ドメインタイプ
	 *
	 */
	public static FixedCostPaymentTotalAmount from(BigDecimal totalAmount) {
		
		// 基底クラスのバリデーションを実行（null非許容、スケール2チェック）
		validate(totalAmount, "支払金額合計");
		
		// ガード節(マイナス値) - 支払金額合計は0以上である必要がある
		if(BigDecimal.ZERO.compareTo(totalAmount) > 0) {
			throw new MyHouseholdAccountBookRuntimeException(
				String.format("「支払金額合計」項目の設定値がマイナスです。管理者に問い合わせてください。[value=%d]",
						totalAmount.intValue()));
		}
		
		// 「支払金額合計」項目の値を生成して返却
		return new FixedCostPaymentTotalAmount(totalAmount);

	}
	
	/**
	 *<pre>
	 * 支払金額合計の値を指定した支払金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する支払金額の値
	 * @return 加算した支払金額合計の値(this + addValue)
	 *
	 */
	public FixedCostPaymentTotalAmount add(FixedCostPaymentAmount addValue) {
		return new FixedCostPaymentTotalAmount(super.add(addValue));
	}
}
