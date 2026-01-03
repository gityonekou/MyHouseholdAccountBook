/**
 * 「支出金額」項目の値を表すドメインタイプです。
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
 * 「支出金額」項目の値を表すドメインタイプです。
 *
 * [ビジネスルール]
 * ・支出金額は0以上の値である必要があります
 * ・マイナスの支出金額は許可されません
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class ExpenditureAmount extends Money {

	/** 値が0の「支出金額」項目の値 */
	public static final ExpenditureAmount ZERO = new ExpenditureAmount(Money.MONEY_ZERO);

	/**
	 *<pre>
	 * コンストラクタ（privateでファクトリメソッド経由のみ生成可能）
	 *</pre>
	 * @param value 支出金額
	 *
	 */
	private ExpenditureAmount(BigDecimal value) {
		super(value);
	}

	/**
	 *<pre>
	 * 「支出金額」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・null値
	 * ・マイナス値
	 * ・スケール値が2以外
	 *
	 *</pre>
	 * @param value 支出金額
	 * @return 「支出金額」項目ドメインタイプ
	 *
	 */
	public static ExpenditureAmount from(BigDecimal value) {
		// 基底クラスのバリデーションを実行（null非許容、スケール2チェック）
		validate(value, "支出金額");

		// ガード節(マイナス値)
		if(BigDecimal.ZERO.compareTo(value) > 0) {
			throw new MyHouseholdAccountBookRuntimeException(
				"「支出金額」項目の設定値がマイナスです。管理者に問い合わせてください。[value=" + value.intValue() + "]");
		}

		// 「支出金額」項目の値を生成して返却
		return new ExpenditureAmount(value);
	}

	/**
	 *<pre>
	 * 支出金額の値を指定した支出金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する支出金額の値
	 * @return 加算した支出金額の値(this + addValue)
	 *
	 */
	public ExpenditureAmount add(ExpenditureAmount addValue) {
		return ExpenditureAmount.from(super.add(addValue));
	}

	/**
	 *<pre>
	 * 支出金額の値を指定した支出金額の値で減算(this - subtractValue)した値を返します。
	 *
	 * 注意：結果がマイナスになる場合は例外をスローします。
	 *</pre>
	 * @param subtractValue 減算する支出金額の値
	 * @return 減算した支出金額の値(this - subtractValue)
	 *
	 */
	public ExpenditureAmount subtract(ExpenditureAmount subtractValue) {
		// 基底クラスの減算処理を実行
		BigDecimal result = super.subtract(subtractValue);

		// 減算結果がマイナスになる場合はエラー
		if(result.compareTo(BigDecimal.ZERO) < 0) {
			throw new MyHouseholdAccountBookRuntimeException(
				"支出金額の減算結果がマイナスになります。管理者に問い合わせてください。");
		}

		return ExpenditureAmount.from(result);
	}

	/**
	 *<pre>
	 * クーポン金額を適用します。
	 * 支出金額からクーポンによる割引額を差し引いた、実際の支払金額を返却します。
	 *
	 * [ビジネスルール]
	 * ・クーポン適用後の金額が0未満になってはならない
	 * ・クーポン金額は内部的にマイナス値として保持されているため、
	 *   加算することで減算効果を得る
	 *
	 * [使用例]
	 * - 買い物登録時の支払金額計算
	 * - 商品金額合計 + 消費税 - クーポン金額 = 支払金額
	 *
	 *</pre>
	 * @param coupon 適用するクーポン金額
	 * @return クーポン適用後の支払金額
	 * @throws MyHouseholdAccountBookRuntimeException クーポンがnull、または適用後の金額が0未満になる場合
	 *
	 */
	public ExpenditureAmount applyCoupon(CouponAmount coupon) {
		if(coupon == null) {
			throw new MyHouseholdAccountBookRuntimeException(
				"適用対象のクーポン金額がnullです。管理者に問い合わせてください。");
		}

		// CouponAmountは内部的にマイナス値なので、addで減算効果が得られる
		BigDecimal result = this.getValue().add(coupon.getValue());

		// ガード節（クーポン適用後の金額がマイナスは不正）
		if(result.compareTo(BigDecimal.ZERO) < 0) {
			throw new MyHouseholdAccountBookRuntimeException(
				String.format("クーポン適用後の支払金額がマイナスになります。[支出金額=%s, クーポン金額=%s]",
					this.toFormatString(), coupon.toFormatString()));
		}

		return ExpenditureAmount.from(result);
	}
}
