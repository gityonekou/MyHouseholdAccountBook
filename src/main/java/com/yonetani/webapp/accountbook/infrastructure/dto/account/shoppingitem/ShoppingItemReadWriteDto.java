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

import com.yonetani.webapp.accountbook.domain.model.account.shoppingitem.ShoppingItem;

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
	 * 商品テーブル情報ドメインモデルからShoppingItemReadWriteDtoを生成して返します。
	 *</pre>
	 * @param data 商品テーブル情報ドメインモデル
	 * @return 商品テーブル:SHOPPING_ITEM_TABLE読込・出力情報
	 *
	 */
	public static ShoppingItemReadWriteDto from(ShoppingItem data) {
		return new ShoppingItemReadWriteDto(
				// ユーザID
				data.getUserId().getValue(),
				// 商品コード
				data.getShoppingItemCode().getValue(),
				// 商品区分名
				data.getShoppingItemKubunName().getValue(),
				// 商品名
				data.getShoppingItemName().getValue(),
				// 商品詳細
				data.getShoppingItemDetailContext().getValue(),
				// 商品JANコード
				data.getShoppingItemJanCode().getValue(),
				// 支出項目コード
				data.getSisyutuItemCode().getValue(),
				// 会社名
				data.getCompanyName().getValue(),
				// 基準店舗コード
				data.getShopCode().getValue(),
				// 基準価格
				data.getStandardPrice().getValue(),
				// 内容量
				data.getShoppingItemCapacity().getValue(),
				// 内容量単位
				data.getShoppingItemCapacityUnit().getValue(),
				// カロリー
				data.getShoppingItemCalories().getValue()
			);
	}
}
