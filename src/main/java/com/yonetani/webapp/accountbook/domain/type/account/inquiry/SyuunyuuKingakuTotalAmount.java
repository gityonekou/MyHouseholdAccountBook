/**
 * 収入金額合計の値を表すドメインタイプです。
 * 「収入金額」項目と「積立金取崩金額」項目の合算値が収入金額合計の値となります。
 * 
 * 注意事項：
 * 画面の「収入金額合計」項目（IncomeTotalAmountクラス）とは別の責務を持ちます。
 * IncomeTotalAmountクラスは収入区分1,2,4の収入金額を表すドメインタイプであり、
 * SyuunyuuKingakuTotalAmountクラスは収入金額合計（収入区分1,2,4の収入金額合計 + 収入区分3の積立金取崩金額）を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/20 : 1.00.00  新規作成
 * 2025/12/21 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.IncomeAmount;
import com.yonetani.webapp.accountbook.domain.type.common.Money;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「収入金額合計」項目の値を表すドメインタイプです。
 * 「収入金額」項目と「積立金取崩金額」項目の合算値が「収入金額合計」項目の値となります。
 *
 * 注意事項：
 * 画面の「収入金額合計」項目（IncomeTotalAmountクラス）とは別の責務を持ちます。
 * IncomeTotalAmountクラスは収入区分1,2,4の収入金額を表すドメインタイプであり、
 * SyuunyuuKingakuTotalAmountクラスは収入金額合計（収入区分1,2,4の収入金額合計 + 収入区分3の積立金取崩金額）を表すドメインタイプです。
 * 
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class SyuunyuuKingakuTotalAmount extends Money {

	/** 値が0の「収入金額合計」項目の値 */
	public static final SyuunyuuKingakuTotalAmount ZERO = SyuunyuuKingakuTotalAmount.from(Money.MONEY_ZERO);

	/**
	 *<pre>
	 * プライベートコンストラクタ
	 *</pre>
	 * @param value 収入金額合計
	 *
	 */
	private SyuunyuuKingakuTotalAmount(BigDecimal value) {
		super(value);
	}

	/**
	 *<pre>
	 * 「収入金額合計」項目の値を表すドメインタイプを生成します。
	 * [ガード節]
	 * ・null値
	 * ・マイナス値
	 * ・スケール値が2以外
	 *
	 *</pre>
	 * @param totalAmount 合計金額
	 * @return 「収入金額合計」項目ドメインタイプ
	 *
	 */
	public static SyuunyuuKingakuTotalAmount from(BigDecimal totalAmount) {
		// 基底クラスのバリデーションを実行（null非許容、スケール2チェック）
		validate(totalAmount, "収入金額合計");
		
		// ガード節(マイナス値)
		if(BigDecimal.ZERO.compareTo(totalAmount) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「収入金額合計」項目の設定値がマイナスです。管理者に問い合わせてください。[value=" + totalAmount.intValue() + "]");
		}
		
		// 「収入金額合計」項目の値を生成して返却
		return new SyuunyuuKingakuTotalAmount(totalAmount);
	}

	/**
	 *<pre>
	 * 収入金額と積立金取崩金額から「収入金額合計」項目の値を表すドメインタイプを生成します。
	 * 収入金額合計 = 収入金額 + 積立金取崩金額
	 *
	 * [ガード節]
	 * ・収入金額がnull
	 * ・積立金取崩金額がnull
	 *
	 * [注意]
	 * ・積立金取崩金額は内部でnull値を持つ可能性があるため、getNullSafeValue()を使用
	 *
	 *</pre>
	 * @param income 収入金額
	 * @param withdrawing 積立金取崩金額
	 * @return 「収入金額合計」項目ドメインタイプ
	 *
	 */
	public static SyuunyuuKingakuTotalAmount from(IncomeAmount income, WithdrawingAmount withdrawing) {
		// ガード節(収入金額がnull)
		if(income == null) {
			throw new MyHouseholdAccountBookRuntimeException("収入金額の設定値がnullです。管理者に問い合わせてください。");
		}
		// ガード節(積立金取崩金額がnull)
		if(withdrawing == null) {
			throw new MyHouseholdAccountBookRuntimeException("積立金取崩金額の設定値がnullです。管理者に問い合わせてください。");
		}

		// 収入金額と積立金取崩金額を加算して収入金額合計を生成
		// 積立金取崩金額はnull値を持つ可能性があるため、getNullSafeValue()を使用
		BigDecimal totalAmount = income.getValue().add(withdrawing.getNullSafeValue());

		// 「収入金額合計」項目の値を生成して返却
		return new SyuunyuuKingakuTotalAmount(totalAmount);
	}
	
	/**
	 *<pre>
	 * 収入金額合計の値を指定した収入金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する収入金額の値
	 * @return 加算した収入金額合計の値(this + addValue)
	 *
	 */
	public SyuunyuuKingakuTotalAmount add(IncomeAmount addValue) {
		return SyuunyuuKingakuTotalAmount.from(super.add(addValue));
	}
}
