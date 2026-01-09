/**
 * 収入金額の値を表すドメインタイプです。
 * 収入テーブル（INCOME_TABLE）の収入金額項目を表現します。
 *
 * 責務：
 * - 収入区分（1,2,3,4）に関わらず、全ての収入金額を表現
 * - 収入区分1,2,4：通常収入（給与、副業、その他収入）
 * - 収入区分3：積立金取崩
 *
 * 注意事項：
 * RegularIncomeAmountクラスとは異なる責務を持ちます。
 * - RegularIncomeAmount：収入区分1,2,4の通常収入金額のみを表す（積立金取崩を除く）
 * - IncomeAmount：収入区分1,2,3,4の全ての収入金額を表す（積立金取崩を含む）
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/01/05 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「収入金額」項目の値を表すドメインタイプです。
 * 収入テーブル（INCOME_TABLE）の収入金額項目を表現します。
 *
 * 収入区分に関わらず、全ての収入金額（通常収入 + 積立金取崩）を表現します。
 *
 * 設計方針：
 * - Moneyクラスを継承し、金額の基本的なバリデーションを利用
 * - 不変オブジェクトとして実装
 * - 負の値を許容しない（収入金額は常に0以上）
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class IncomeAmount extends Money {

	/** 値が0の「収入金額」項目の値 */
	public static final IncomeAmount ZERO = IncomeAmount.from(Money.MONEY_ZERO);

	/**
	 *<pre>
	 * プライベートコンストラクタ
	 *</pre>
	 * @param value 収入金額
	 *
	 */
	private IncomeAmount(BigDecimal value) {
		super(value);
	}

	/**
	 *<pre>
	 * 「収入金額」項目の値を表すドメインタイプを生成します。
	 * [ガード節]
	 * ・null値
	 * ・マイナス値
	 * ・スケール値が2以外
	 *
	 *</pre>
	 * @param value 収入金額
	 * @return 「収入金額」項目ドメインタイプ
	 *
	 */
	public static IncomeAmount from(BigDecimal value) {
		// 基底クラスのバリデーションを実行（null非許容、スケール2チェック）
		validate(value, "収入金額");

		// ガード節(マイナス値)
		if(BigDecimal.ZERO.compareTo(value) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「収入金額」項目の設定値がマイナスです。管理者に問い合わせてください。[value=" + value.intValue() + "]");
		}

		// 「収入金額」項目の値を生成して返却
		return new IncomeAmount(value);
	}

	/**
	 *<pre>
	 * 収入金額の値を指定した収入金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する収入金額の値
	 * @return 加算した収入金額の値(this + addValue)
	 *
	 */
	public IncomeAmount add(IncomeAmount addValue) {
		return IncomeAmount.from(super.add(addValue));
	}

	/**
	 *<pre>
	 * 収入金額の値を指定した収入金額の値で減算(this - subtractValue)した値を返します。
	 * 減算結果がマイナスになる場合は例外をスローします。
	 *</pre>
	 * @param subtractValue 減算する収入金額の値
	 * @return 減算した収入金額の値(this - subtractValue)
	 *
	 */
	public IncomeAmount subtract(IncomeAmount subtractValue) {
		BigDecimal result = super.subtract(subtractValue);
		// 減算結果がマイナスの場合は例外
		if(result.compareTo(BigDecimal.ZERO) < 0) {
			throw new MyHouseholdAccountBookRuntimeException("収入金額の減算結果がマイナスになります。管理者に問い合わせてください。");
		}
		return IncomeAmount.from(result);
	}
}
