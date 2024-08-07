/**
 * 商品テーブル:SHOPPING_ITEM_TABLE読込・出力情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.account.shoppingitem;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 商品テーブル:SHOPPING_ITEM_TABLE読込・出力情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ShoppingItemReadWriteDto {
	// ユーザID
	private final String userId;
	// 商品コード
	private final String shoppingItemCode;
	// 商品区分名
	private final String shoppingItemKubunName;
	// 商品名
	private final String shoppingItemName;
	// 商品詳細
	private final String shoppingItemDetailContext;
	// 商品JANコード(13桁 or 8桁 or ISBNコード:10桁)
	private final String shoppingItemJanCode;
	// 支出項目コード
	private final String sisyutuItemCode;
	// 会社名
	private final String companyName;
	// 基準店舗コード
	private final String standardShopCode;
	// 基準価格
	private final BigDecimal standardPrice;
	// 内容量
	private final Integer capacity;
	// 内容量単位
	private final String capacityUnit;
	// カロリー
	private final Integer calories;
	
	/**
	 *<pre>
	 * 引数のパラメータ値をもとにShoppingItemReadWriteDtoを生成して返します。
	 *</pre>
	 * @param userId   ユーザID
	 * @param shoppingItemCode 商品コード
	 * @param shoppingItemKubunName 商品区分名
	 * @param shoppingItemName 商品名
	 * @param shoppingItemDetailContext 商品詳細
	 * @param shoppingItemJanCode 商品JANコード
	 * @param sisyutuItemCode 支出項目コード
	 * @param companyName 会社名
	 * @param standardShopCode 基準店舗コード
	 * @param standardPrice 基準価格
	 * @param capacity 内容量
	 * @param capacityUnit 内容量単位
	 * @param calories カロリー
	 * @return 商品テーブル:SHOPPING_ITEM_TABLE出力情報
	 *
	 */
	public static ShoppingItemReadWriteDto from(
			String userId,
			String shoppingItemCode,
			String shoppingItemKubunName,
			String shoppingItemName,
			String shoppingItemDetailContext,
			String shoppingItemJanCode,
			String sisyutuItemCode,
			String companyName,
			String standardShopCode,
			BigDecimal standardPrice,
			Integer capacity,
			String capacityUnit,
			Integer calories
			) {
		return new ShoppingItemReadWriteDto(
				userId,
				shoppingItemCode,
				shoppingItemKubunName,
				shoppingItemName, 
				shoppingItemDetailContext,
				shoppingItemJanCode,
				sisyutuItemCode,
				companyName,
				standardShopCode,
				standardPrice,
				capacity,
				capacityUnit,
				calories);
	}
}
