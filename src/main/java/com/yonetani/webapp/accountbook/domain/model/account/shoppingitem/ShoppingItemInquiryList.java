/**
 * 商品検索結果情報(商品検索結果明細リスト情報)の値を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.shoppingitem;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemName;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemCalories;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemCapacity;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemCapacityUnit;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemCompanyName;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemJanCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemKubunName;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemName;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemStandardPrice;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingitem.ShoppingItemStandardShopName;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 商品検索結果情報(商品検索結果明細リスト情報)の値を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ShoppingItemInquiryList {
	
	/**
	 *<pre>
	 * 商品検索結果明細情報(ドメイン)です
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	@ToString
	@EqualsAndHashCode
	public static class ShoppingItemInquiryItem {
		// 商品コード
		private final ShoppingItemCode shoppingItemCode;
		// 商品区分名
		private final ShoppingItemKubunName shoppingItemKubunName;
		// 商品名
		private final ShoppingItemName shoppingItemName;
		// 商品詳細
		private final ShoppingItemDetailContext shoppingItemDetailContext;
		// 商品JANコード
		private final ShoppingItemJanCode shoppingItemJanCode;
		// 支出項目名
		private final SisyutuItemName sisyutuItemName;
		// 会社名
		private final ShoppingItemCompanyName companyName;
		// 基準店舗名
		private final ShoppingItemStandardShopName standardShopName;
		// 基準価格
		private final ShoppingItemStandardPrice standardPrice;
		// 内容量
		private final ShoppingItemCapacity shoppingItemCapacity;
		// 内容量単位
		private final ShoppingItemCapacityUnit shoppingItemCapacityUnit;
		// カロリー
		private final ShoppingItemCalories shoppingItemCalories;
		
		/**
		 *<pre>
		 * 引数の値から商品検索結果明細情報を表すドメインモデルを生成して返します。
		 *</pre>
		 * @param shoppingItemCode 商品コード
		 * @param shoppingItemKubunName 商品区分名
		 * @param shoppingItemName 商品名
		 * @param shoppingItemDetailContext 商品詳細
		 * @param shoppingItemJanCode 商品JANコード
		 * @param sisyutuItemName 支出項目名
		 * @param companyName 会社名
		 * @param standardShopName 基準店舗名
		 * @param standardPrice 基準価格
		 * @param shoppingItemCapacity 内容量
		 * @param shoppingItemCapacityUnit 内容量単位
		 * @param shoppingItemCalories カロリー
		 * @return 商品検索結果明細情報を表すドメインモデル
		 *
		 */
		public static ShoppingItemInquiryItem from(
				String shoppingItemCode,
				String shoppingItemKubunName,
				String shoppingItemName,
				String shoppingItemDetailContext,
				String shoppingItemJanCode,
				String sisyutuItemName,
				String companyName,
				String standardShopName,
				BigDecimal standardPrice,
				Integer shoppingItemCapacity,
				String shoppingItemCapacityUnit,
				Integer shoppingItemCalories
				) {
			return new ShoppingItemInquiryItem(
					ShoppingItemCode.from(shoppingItemCode),
					ShoppingItemKubunName.from(shoppingItemKubunName),
					ShoppingItemName.from(shoppingItemName),
					ShoppingItemDetailContext.from(shoppingItemDetailContext),
					ShoppingItemJanCode.from(shoppingItemJanCode),
					SisyutuItemName.from(sisyutuItemName),
					ShoppingItemCompanyName.from(companyName),
					ShoppingItemStandardShopName.from(standardShopName),
					ShoppingItemStandardPrice.from(standardPrice),
					ShoppingItemCapacity.from(shoppingItemCapacity),
					ShoppingItemCapacityUnit.from(shoppingItemCapacityUnit),
					ShoppingItemCalories.from(shoppingItemCalories));
		}
	}
	
	// 商品情報検索結果明細情報のリスト
	private final List<ShoppingItemInquiryItem> values;
	
	/**
	 *<pre>
	 * 引数の値から商品検索結果情報(商品情報検索結果明細リスト情報)の値を表すドメインモデルを生成して返します。 
	 *</pre>
	 * @param values 商品情報検索結果明細情報のリスト
	 * @return 商品検索結果情報(商品情報検索結果明細リスト情報)を表すドメインモデル
	 *
	 */
	public static ShoppingItemInquiryList from(List<ShoppingItemInquiryItem> values) {
		if(CollectionUtils.isEmpty(values)) {
			return new ShoppingItemInquiryList(Collections.emptyList());
		} else {
			return new ShoppingItemInquiryList(values);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if(values.size() > 0) {
			StringBuilder buff = new StringBuilder((values.size() + 1) * 470);
			buff.append("商品検索結果:")
			.append(values.size())
			.append("件:");
			for(int i = 0; i < values.size(); i++) {
				buff.append("[[")
				.append(i)
				.append("][")
				.append(values.get(i))
				.append("]]");
			}
			return buff.toString();
		} else {
			return "商品検索結果:0件";
		}
	}
	
	/**
	 *<pre>
	 * 検索結果が設定されているかどうかを判定します。
	 *</pre>
	 * @return 空の場合はtrue、値が設定されている場合はfalse
	 *
	 */
	public boolean isEmpty() {
		return CollectionUtils.isEmpty(values);
	}
}
