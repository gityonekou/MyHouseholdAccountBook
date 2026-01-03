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
import java.math.RoundingMode;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「クーポン金額」項目の値を表すドメインタイプです。
 *
 * [ビジネスルール]
 * ・クーポン金額は割引額を表す
 * ・正の値で指定し、内部的にマイナス値として保持する
 * ・支出金額から減算される
 * ・0以上の値のみ許可される
 *
 * [設計方針]
 * ・画面入力は正の値（例: 500）
 * ・内部保持はマイナス値（例: -500.00）
 * ・支出金額との計算時に加算することで減算効果を得る
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
	 * @param value クーポン金額（内部的にマイナス値として保持）
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
	 * 注意：引数は正の値で指定し、内部的にマイナス値に変換します。
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

		// 内部的にマイナス値として保持（支出から減算するため）
		return new CouponAmount(value.negate());
	}

	/**
	 *<pre>
	 * 割引額を取得します（正の値）。
	 *
	 * 内部的にマイナス値として保持している値を、正の値に変換して返却します。
	 * 画面表示などで使用します。
	 *</pre>
	 * @return 割引額（正の値）
	 *
	 */
	public BigDecimal getDiscountAmount() {
		return this.getValue().abs();
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
	 * 注意：内部的にはマイナス値なので、加算すると割引額が大きくなります。
	 *</pre>
	 * @param addValue 加算するクーポン金額
	 * @return 加算したクーポン金額
	 *
	 */
	public CouponAmount add(CouponAmount addValue) {
		return CouponAmount.from(super.add(addValue));
	}

	/**
	 *<pre>
	 * toFormatString()のオーバーライド
	 * クーポン金額は割引額として正の値で表示します。
	 *</pre>
	 * @return フォーマット済み文字列（例: "500円"）
	 *
	 */
	@Override
	public String toFormatString() {
		// 割引額（正の値）として表示
		long roundedValue = this.getDiscountAmount().setScale(0, RoundingMode.HALF_UP).longValue();
		return String.format("%,d円", roundedValue);
	}
}
