/**
 * 「積立金取崩金額合計」項目の値を表すドメインタイプです。
 * リファクタリングにより、クラス名変更しました(WithdrewKingakuTotalAmount → WithdrawingTotalAmount)
 * 
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/28 : 1.00.00  リファクタリング対応(DDD適応)により新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.domain.type.common.NullableMoney;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「積立金取崩金額合計」項目の値を表すドメインタイプです。
 * 「積立金取崩金額」項目の合算値が「積立金取崩金額合計」項目の値となります。	
 * （※収支登録時に収入区分(004)の区分「3」で登録した金額の合算値です）
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class WithdrawingTotalAmount extends NullableMoney {
	/** 値が0の「積立金取崩金額合計」項目の値 */
	public static final WithdrawingTotalAmount ZERO = WithdrawingTotalAmount.from(NullableMoney.NULLABLE_MONEY_ZERO);
	/** 値がnullの「積立金取崩金額合計」項目の値 */
	public static final WithdrawingTotalAmount NULL = WithdrawingTotalAmount.from(null);
	
	/**
	 * コンストラクタ
	 * @param value 積立金取崩金額
	 */
	private WithdrawingTotalAmount(BigDecimal value) {
		super(value);
	}
	
	/**
	 *<pre>
	 * 「積立金取崩金額合計」項目の値を表すドメインタイプを生成します。
	 * 
	 * [非ガード節]
	 * ・null値
	 * [ガード節]
	 * ・マイナス値
	 * ・スケール値が2以外
	 * 
	 *</pre>
	 * @param totalAmount 合計金額
	 * @return 「積立金取崩金額合計」項目ドメインタイプ
	 *
	 */
	public static WithdrawingTotalAmount from(BigDecimal totalAmount) {
		// 基底クラスのバリデーションを実行（null許容、スケール2、マイナス値チェック）
		validate(totalAmount, "積立金取崩金額");
		
		// 「積立金取崩金額合計」項目の値を生成して返却
		return new WithdrawingTotalAmount(totalAmount);
	}
	
	/**
	 *<pre>
	 * 積立金取崩金額合計の値を指定した積立金取崩金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する積立金取崩金額の値
	 * @return 加算した積立金取崩金額合計の値(this + addValue)
	 *
	 */
	public WithdrawingTotalAmount add(WithdrawingAmount addValue) {
		return WithdrawingTotalAmount.from(super.add(addValue));
	}
}
