/**
 * 商品テーブル情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.shoppingitem;

import java.math.BigDecimal;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemCompanyName;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemKubunName;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemName;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemStandardPrice;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemStandardShopCode;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 商品テーブル情報を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ShoppingItem {
	// ユーザID
	private final UserId userId;
	// 商品コード
	private final ShoppingItemCode shoppingItemCode;
	// 商品区分名
	private final ShoppingItemKubunName shoppingItemKubunName;
	// 商品名
	private final ShoppingItemName shoppingItemName;
	// 商品詳細
	private final ShoppingItemDetailContext shoppingItemDetailContext;
	// 支出項目コード
	private final SisyutuItemCode sisyutuItemCode;
	// 会社名
	private final ShoppingItemCompanyName companyName;
	// 基準店舗コード
	private final ShoppingItemStandardShopCode shopCode;
	// 基準価格
	private final ShoppingItemStandardPrice standardPrice;
	
	/**
	 *<pre>
	 * 引数の値から商品テーブル情報を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param shoppingItemCode 商品コード
	 * @param shoppingItemKubunName 商品区分名
	 * @param shoppingItemName 商品名
	 * @param shoppingItemDetailContext 商品詳細
	 * @param sisyutuItemCode 支出項目コード
	 * @param companyName 会社名
	 * @param shopCode 基準店舗コード
	 * @param standardPrice 基準価格
	 * @return 商品テーブル情報を表すドメインモデル
	 *
	 */
	public static ShoppingItem from(
			String userId,
			String shoppingItemCode,
			String shoppingItemKubunName,
			String shoppingItemName,
			String shoppingItemDetailContext,
			String sisyutuItemCode,
			String companyName,
			String shopCode,
			BigDecimal standardPrice
			) {
		return new ShoppingItem(
				UserId.from(userId),
				ShoppingItemCode.from(shoppingItemCode),
				ShoppingItemKubunName.from(shoppingItemKubunName),
				ShoppingItemName.from(shoppingItemName),
				ShoppingItemDetailContext.from(shoppingItemDetailContext),
				SisyutuItemCode.from(sisyutuItemCode),
				ShoppingItemCompanyName.from(companyName),
				ShoppingItemStandardShopCode.from(shopCode),
				ShoppingItemStandardPrice.from(standardPrice));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder(300);
		buff.append("userId:")
		.append(userId)
		.append(",shoppingItemCode:")
		.append(shoppingItemCode)
		.append(",shoppingItemKubunName:")
		.append(shoppingItemKubunName)
		.append(",shoppingItemName:")
		.append(shoppingItemName)
		.append(",shoppingItemDetailContext:")
		.append(shoppingItemDetailContext)
		.append(",sisyutuItemCode:")
		.append(sisyutuItemCode)
		.append(",companyName:")
		.append(companyName)
		.append(",shopCode:")
		.append(shopCode)
		.append(",standardPrice:")
		.append(standardPrice);
		return buff.toString();
	}
}
