/**
 * 「商品基準価格」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/29 : 1.00.00  新規作成
 * 2026/03/20 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingitem;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.domain.type.common.NullableMoney;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「商品基準価格」項目の値を表すドメインタイプです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class ShoppingItemStandardPrice extends NullableMoney {
	
	/**
	 * コンストラクタ
	 * @param value 商品基準価格
	 */
	private ShoppingItemStandardPrice(BigDecimal value) {
		super(value);
	}
	
	/**
	 *<pre>
	 * 「商品基準価格」項目の値を表すドメインタイプを生成します。
	 *
	 * [非ガード節]
	 * ・null値
	 * [ガード節]
	 * ・マイナス値
	 * ・スケール値が2以外
	 *</pre>
	 * @param price 商品基準価格
	 * @return 「商品基準価格」項目ドメインタイプ
	 *
	 */
	public static ShoppingItemStandardPrice from(BigDecimal price) {
		// 基底クラスのバリデーションを実行（null許容、スケール2、マイナス値チェック）
		validate(price, "商品基準価格");
		
		// 「商品基準価格」項目の値を生成して返却
		return new ShoppingItemStandardPrice(price);
	}
}
