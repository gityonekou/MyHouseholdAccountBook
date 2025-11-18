/**
 * 「収入金額」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「収入金額」項目の値を表すドメインタイプです。
 *
 * [ビジネスルール]
 * ・収入金額は0以上の値である必要があります
 * ・マイナスの収入金額は許可されません
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class IncomeAmount extends Money {

	// 値が0の「収入金額」項目の値
	public static final IncomeAmount ZERO = new IncomeAmount(BigDecimal.ZERO.setScale(2));

	/**
	 *<pre>
	 * コンストラクタ（privateでファクトリメソッド経由のみ生成可能）
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
	 *
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
		// 基本検証（null、スケール）
		Money.validate(value, "収入金額");

		// ガード節(マイナス値)
		if(BigDecimal.ZERO.compareTo(value) > 0) {
			throw new MyHouseholdAccountBookRuntimeException(
				"「収入金額」項目の設定値がマイナスです。管理者に問い合わせてください。[value=" + value.intValue() + "]");
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
		if(addValue == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				"加算対象の収入金額がnullです。管理者に問い合わせてください。");
		}
		return new IncomeAmount(super.add(addValue));
	}

	/**
	 *<pre>
	 * 収入金額の値を指定した収入金額の値で減算(this - subtractValue)した値を返します。
	 *
	 * 注意：結果がマイナスになる場合は例外をスローします。
	 *</pre>
	 * @param subtractValue 減算する収入金額の値
	 * @return 減算した収入金額の値(this - subtractValue)
	 *
	 */
	public IncomeAmount subtract(IncomeAmount subtractValue) {
		if(subtractValue == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				"減算対象の収入金額がnullです。管理者に問い合わせてください。");
		}
		BigDecimal result = super.subtract(subtractValue);

		// 結果がマイナスになる場合はエラー
		if(result.compareTo(BigDecimal.ZERO) < 0) {
			throw new MyHouseholdAccountBookRuntimeException(
				"収入金額の減算結果がマイナスになります。管理者に問い合わせてください。");
		}

		return new IncomeAmount(result);
	}
}
