/**
 * 「買い物合計金額」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/11/26 : 1.00.00                      新規作成
 * 2026/09/23 : 1.01.00  feature-1.03-dev1   追加リファクタリング対応(Money継承に変更)
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingregist;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.Money;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「買い物合計金額」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class ShoppingTotalAmount extends Money {

	/** 値が0の「買い物合計金額」項目の値 */
	public static final ShoppingTotalAmount ZERO = ShoppingTotalAmount.from(Money.MONEY_ZERO);

	/**
	 *<pre>
	 * プライベートコンストラクタ
	 *</pre>
	 * @param value 買い物合計金額
	 *
	 */
	private ShoppingTotalAmount(BigDecimal value) {
		super(value);
	}

	/**
	 *<pre>
	 * 「買い物合計金額」項目の値を表すドメインタイプを生成します
	 *
	 * [ガード節]
	 * ・買い物合計金額がnull値
	 * ・買い物合計金額がマイナス値
	 * ・買い物合計金額がスケール値が2以外
	 *
	 *</pre>
	 * @param price 買い物合計金額
	 * @return 「買い物合計金額」項目ドメインタイプ
	 *
	 */
	public static ShoppingTotalAmount from(BigDecimal price) {

		// 基底クラスのバリデーションを実行（null非許容、スケール2チェック）
		validate(price, "買い物合計金額");

		// ガード節(買い物合計金額がマイナス値)
		if (BigDecimal.ZERO.compareTo(price) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「買い物合計金額」項目の設定値がマイナスです。管理者に問い合わせてください。[value=" + price.intValue() + "]");
		}

		// 買い物合計金額項目ドメインタイプを生成
		return new ShoppingTotalAmount(price);

	}

	/**
	 *<pre>
	 * 買い物合計金額の値を指定した買い物合計金額の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する買い物合計金額の値
	 * @return 加算した買い物合計金額の値(this + addValue)
	 *
	 */
	public ShoppingTotalAmount add(ShoppingTotalAmount addValue) {
		return ShoppingTotalAmount.from(super.add(addValue));
	}
}
