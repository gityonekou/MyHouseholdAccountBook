/**
 * 「消費税合計」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/11/26 : 1.00.00                      新規作成
 * 2026/09/23 : 1.01.00  feature-1.03-dev1   追加リファクタリング対応(NullableMoney継承に変更)
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingregist;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.domain.type.common.NullableMoney;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「消費税合計」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class TaxTotalPurchasePrice extends NullableMoney {

	/**
	 *<pre>
	 * TaxTotalPurchasePriceクラスコンストラクターです。
	 *</pre>
	 * @param value 消費税合計
	 *
	 */
	private TaxTotalPurchasePrice(BigDecimal value) {
		super(value);
	}

	/**
	 *<pre>
	 * 「消費税合計」項目の値を表すドメインタイプを生成します
	 *
	 * [非ガード節]
	 * ・消費税合計がnull値
	 * [ガード節]
	 * ・消費税合計がマイナス値
	 * ・消費税合計がスケール値が2以外
	 *
	 *</pre>
	 * @param price 消費税合計
	 * @return 「消費税合計」項目ドメインタイプ
	 *
	 */
	public static TaxTotalPurchasePrice from(BigDecimal price) {

		// 基底クラスのバリデーションを実行（null許容、スケール2、マイナス値チェック）
		validate(price, "消費税合計");

		// 消費税合計項目ドメインタイプを生成
		return new TaxTotalPurchasePrice(price);

	}

	/**
	 *<pre>
	 * 消費税合計の値を指定した消費税合計の値で加算(this + addValue)した値を返します。
	 *</pre>
	 * @param addValue 加算する消費税合計の値
	 * @return 加算した消費税合計の値(this + addValue)
	 *
	 */
	public TaxTotalPurchasePrice add(TaxTotalPurchasePrice addValue) {
		return TaxTotalPurchasePrice.from(super.add(addValue));
	}
}
