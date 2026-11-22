/**
 * 「購入金額合計」項目の値を表すドメインタイプです
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
 * 「購入金額合計」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class TotalPurchasePrice extends Money {

	/**
	 *<pre>
	 * プライベートコンストラクタ
	 *</pre>
	 * @param value 購入金額合計
	 *
	 */
	private TotalPurchasePrice(BigDecimal value) {
		super(value);
	}

	/**
	 *<pre>
	 * 「購入金額合計」項目の値を表すドメインタイプを生成します
	 *
	 * [ガード節]
	 * ・購入金額合計がnull値
	 * ・購入金額合計がマイナス値
	 * ・購入金額合計がスケール値が2以外
	 *
	 *</pre>
	 * @param price 購入金額合計
	 * @return 「購入金額合計」項目ドメインタイプ
	 *
	 */
	public static TotalPurchasePrice from(BigDecimal price) {

		// 基底クラスのバリデーションを実行（null非許容、スケール2チェック）
		validate(price, "購入金額合計");

		// ガード節(購入金額合計がマイナス値)
		if (BigDecimal.ZERO.compareTo(price) > 0) {
			throw new MyHouseholdAccountBookRuntimeException("「購入金額合計」項目の設定値がマイナスです。管理者に問い合わせてください。[value=" + price.intValue() + "]");
		}

		// 購入金額合計項目ドメインタイプを生成
		return new TotalPurchasePrice(price);

	}

	/**
	 *<pre>
	 * 購入金額合計の値を指定した購入金額合計の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する購入金額合計の値
	 * @return 加算した購入金額合計の値(this + addValue)
	 *
	 */
	public TotalPurchasePrice add(TotalPurchasePrice addValue) {
		return TotalPurchasePrice.from(super.add(addValue));
	}
}
