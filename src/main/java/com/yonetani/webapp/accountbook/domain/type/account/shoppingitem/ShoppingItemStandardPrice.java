/**
 * 「商品基準価格」項目の値を表すドメインタイプです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingitem;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class ShoppingItemStandardPrice {
	// 商品基準価格
	@Getter
	private final BigDecimal value;
	
	/**
	 *<pre>
	 * 「商品基準価格」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param price 商品基準価格
	 * @return 「商品基準価格」項目ドメインタイプ
	 *
	 */
	public static ShoppingItemStandardPrice from(BigDecimal price) {
		return new ShoppingItemStandardPrice(price);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		// スケール0で四捨五入+カンマ編集した文字列を返却
		return DomainCommonUtils.formatKingakuAndYen(value);
	}
}
