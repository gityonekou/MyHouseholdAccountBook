/**
 * 「クーポン金額」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「クーポン金額」項目の値を表すドメインタイプです。
 *
 * [ビジネスルール]
 * ・クーポン金額は割引額を表す
 * ・正の値で指定する
 * ・支出金額から減算される
 * ・0以上の値のみ許可される
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class CouponAmount extends Money {

	/** 値が0の「クーポン金額」項目の値（割引なし） */
	public static final CouponAmount ZERO = new CouponAmount(Money.MONEY_ZERO);

	/**
	 *<pre>
	 * コンストラクタ（privateでファクトリメソッド経由のみ生成可能）
	 *</pre>
	 * @param value クーポン金額
	 *
	 */
	private CouponAmount(BigDecimal value) {
		super(value);
	}

	/**
	 *<pre>
	 * 「クーポン金額」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・null値
	 * ・マイナス値（正の値で指定する必要がある）
	 * ・スケール値が2以外
	 *
	 *</pre>
	 * @param value 割引金額（正の値で指定）
	 * @return 「クーポン金額」項目ドメインタイプ
	 *
	 */
	public static CouponAmount from(BigDecimal value) {
		// 基底クラスのバリデーションを実行（null非許容、スケール2チェック）
		validate(value, "クーポン金額");

		// ガード節(マイナス値 - 正の値のみ受け付ける)
		if(BigDecimal.ZERO.compareTo(value) > 0) {
			throw new MyHouseholdAccountBookRuntimeException(
				"「クーポン金額」項目は正の値で指定してください。管理者に問い合わせてください。[value=" + value.intValue() + "]");
		}

		// 「クーポン金額」項目の値を生成して返却
		return new CouponAmount(value);
	}

	/**
	 *<pre>
	 * クーポンによる割引があるかどうかを判定します。
	 *</pre>
	 * @return 割引がある場合true、割引がない（0円）場合false
	 *
	 */
	public boolean hasDiscount() {
		return !this.isZero();
	}

	/**
	 *<pre>
	 * クーポン金額を加算します（割引額を合算）。
	 *
	 *</pre>
	 * @param addValue 加算するクーポン金額
	 * @return 加算したクーポン金額
	 *
	 */
	public CouponAmount add(CouponAmount addValue) {
		return CouponAmount.from(super.add(addValue));
	}
}
