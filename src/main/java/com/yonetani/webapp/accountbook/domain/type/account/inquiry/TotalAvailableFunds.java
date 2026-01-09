/**
 * 「利用可能資金合計」の値を表すドメインタイプです
 * リファクタリングにより、クラス名変更しました(SyuunyuuKingakuTotalAmount → TotalAvailableFunds)
 *
 * 注意事項：
 * 画面の「収入金額合計」項目（RegularIncomeTotalAmountクラス）とは別の責務を持ちます。
 * RegularIncomeTotalAmountクラスは収入区分1,2,4の収入金額を表すドメインタイプであり、
 * TotalAvailableFundsクラスは利用可能資金合計（収入区分1,2,4の収入金額合計 + 収入区分3の積立金取崩金額）を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/01/06 : 1.00.00  リファクタリング対応(DDD適応)により新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.Money;
import com.yonetani.webapp.accountbook.domain.type.common.RegularIncomeAmount;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「利用可能資金合計」の値を表すドメインタイプです
 *
 * 支出を減算する元となる、使用可能な資金の合計金額を表します。
 * 現在は「通常収入金額」と「積立金取崩金額」の合算値となります。
 * 将来的には各種ローンなどの借入金も加算対象となる可能性があります。
 *
 * 注意事項：
 * 画面の「収入金額合計」項目（RegularIncomeTotalAmountクラス）とは別の責務を持ちます。
 * - RegularIncomeTotalAmount：収入区分1,2,4の収入金額を表す（積立金取崩を除く）
 * - TotalAvailableFunds：利用可能資金合計（通常収入金額 + 積立金取崩金額）を表す
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class TotalAvailableFunds extends Money {

	/** 値が0の「利用可能資金合計」の値 */
	public static final TotalAvailableFunds ZERO = TotalAvailableFunds.from(Money.MONEY_ZERO);

	/**
	 *<pre>
	 * プライベートコンストラクタ
	 *</pre>
	 * @param value 利用可能資金合計
	 *
	 */
	private TotalAvailableFunds(BigDecimal value) {
		super(value);
	}

	/**
	 *<pre>
	 * 「利用可能資金合計」の値を表すドメインタイプを生成します。
	 * [ガード節]
	 * ・null値
	 * ・マイナス値
	 * ・スケール値が2以外
	 *
	 *</pre>
	 * @param totalAmount 合計金額
	 * @return 「利用可能資金合計」ドメインタイプ
	 *
	 */
	public static TotalAvailableFunds from(BigDecimal totalAmount) {
		// 基底クラスのバリデーションを実行（null非許容、スケール2チェック）
		validate(totalAmount, "利用可能資金合計");

		// ガード節(マイナス値)
		if(BigDecimal.ZERO.compareTo(totalAmount) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「利用可能資金合計」の設定値がマイナスです。管理者に問い合わせてください。[value=" + totalAmount.intValue() + "]");
		}

		// 「利用可能資金合計」の値を生成して返却
		return new TotalAvailableFunds(totalAmount);
	}

	/**
	 *<pre>
	 * 通常収入金額と積立金取崩金額から「利用可能資金合計」の値を表すドメインタイプを生成します。
	 * 利用可能資金合計 = 通常収入金額 + 積立金取崩金額
	 *
	 * [ガード節]
	 * ・通常収入金額がnull
	 * ・積立金取崩金額がnull
	 *
	 * [注意]
	 * ・積立金取崩金額は内部でnull値を持つ可能性があるため、getNullSafeValue()を使用
	 *
	 *</pre>
	 * @param income 通常収入金額
	 * @param withdrawing 積立金取崩金額
	 * @return 「利用可能資金合計」ドメインタイプ
	 *
	 */
	public static TotalAvailableFunds from(RegularIncomeAmount income, WithdrawingAmount withdrawing) {
		// ガード節(通常収入金額がnull)
		if(income == null) {
			throw new MyHouseholdAccountBookRuntimeException("通常収入金額の設定値がnullです。管理者に問い合わせてください。");
		}
		// ガード節(積立金取崩金額がnull)
		if(withdrawing == null) {
			throw new MyHouseholdAccountBookRuntimeException("積立金取崩金額の設定値がnullです。管理者に問い合わせてください。");
		}

		// 通常収入金額と積立金取崩金額を加算して利用可能資金合計を生成
		// 積立金取崩金額はnull値を持つ可能性があるため、getNullSafeValue()を使用
		BigDecimal totalAmount = income.getValue().add(withdrawing.getNullSafeValue());

		// 「利用可能資金合計」の値を生成して返却
		return new TotalAvailableFunds(totalAmount);
	}

	/**
	 *<pre>
	 * 利用可能資金合計の値を指定した通常収入金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する通常収入金額の値
	 * @return 加算した利用可能資金合計の値(this + addValue)
	 *
	 */
	public TotalAvailableFunds add(RegularIncomeAmount addValue) {
		return TotalAvailableFunds.from(super.add(addValue));
	}
}
