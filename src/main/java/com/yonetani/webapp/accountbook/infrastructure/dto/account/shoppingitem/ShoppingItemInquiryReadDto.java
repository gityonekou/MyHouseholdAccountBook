/**
 * 商品情報検索結果情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.account.shoppingitem;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 商品情報検索結果情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ShoppingItemInquiryReadDto {
	// 商品コード
	private final String shoppingItemCode;
	// 商品区分名
	private final String shoppingItemKubunName;
	// 商品名
	private final String shoppingItemName;
	// 商品詳細
	private final String shoppingItemDetailContext;
	// 支出項目名
	private final String sisyutuItemName;
	// 会社名
	private final String companyName;
	// 基準店舗名
	private final String standardShopName;
	// 基準価格
	private final BigDecimal standardPrice;
}
