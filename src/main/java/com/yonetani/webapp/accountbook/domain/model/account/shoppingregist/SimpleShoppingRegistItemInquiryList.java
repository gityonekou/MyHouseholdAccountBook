/**
 * 簡易タイプ買い物登録情報(リスト情報)の値を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/11/23 : 1.00.00                      新規作成
 * 2026/08/18 : 1.01.00  feature-1.03-dev1   追加リファクタリング対応(買い物登録のドメイン見直し)
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.shoppingregist;

import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingClothesExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingConsumerGoodsExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingCouponPrice;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDineOutExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodMinorWasteExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodSevereWasteExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingHouseEquipmentExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingWorkExpenditureItem;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 簡易タイプ買い物登録情報(リスト情報)の値を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SimpleShoppingRegistItemInquiryList {
	
	// 買い物登録情報のリスト
	private final List<SimpleShoppingRegistItem> values;
	
	// 食料品(必須)合計
	private final ShoppingFoodExpenditureItem totalShoppingFoodItem;
	// 食料品B(無駄遣い)合計
	private final ShoppingFoodMinorWasteExpenditureItem totalShoppingFoodMinorWasteItem;
	// 食料品C(お酒類)合計
	private final ShoppingFoodSevereWasteExpenditureItem totalShoppingFoodSevereWasteItem;
	// 外食合計
	private final ShoppingDineOutExpenditureItem totalShoppingDineOutItem;
	// 日用品合計
	private final ShoppingConsumerGoodsExpenditureItem totalShoppingConsumerGoodsItem;
	// 衣料品(私服)合計
	private final ShoppingClothesExpenditureItem totalShoppingClothesItem;
	// 仕事合計
	private final ShoppingWorkExpenditureItem totalShoppingWorkItem;
	// 住居設備合計
	private final ShoppingHouseEquipmentExpenditureItem totalShoppingHouseEquipmentItem;
	// クーポン金額合計
	private final ShoppingCouponPrice totalShoppingCouponPrice;
	// 月度買い物合計金額
	private final ShoppingTotalAmount shoppingMonthTotalAmount;
	
	/**
	 *<pre>
	 * 引数の値から簡易タイプ買い物登録情報(リスト情報)を表すドメインモデルを生成して返します。 
	 *</pre>
	 * @param values 簡易タイプ買い物登録情報のリスト
	 * @return 簡易タイプ買い物登録情報(リスト情報)を表すドメインモデル
	 *
	 */
	public static SimpleShoppingRegistItemInquiryList from(List<SimpleShoppingRegistItem> values) {
		if(CollectionUtils.isEmpty(values)) {
			return new SimpleShoppingRegistItemInquiryList(
					// 買い物登録情報のリスト(空)
					Collections.emptyList(),
					// 食料品(必須)合計
					ShoppingFoodExpenditureItem.NULL,
					// 食料品B(無駄遣い)合計
					ShoppingFoodMinorWasteExpenditureItem.NULL,
					// 食料品C(お酒類)合計
					ShoppingFoodSevereWasteExpenditureItem.NULL,
					// 外食合計
					ShoppingDineOutExpenditureItem.NULL,
					// 日用品合計
					ShoppingConsumerGoodsExpenditureItem.NULL,
					// 衣料品(私服)合計
					ShoppingClothesExpenditureItem.NULL,
					// 仕事合計
					ShoppingWorkExpenditureItem.NULL,
					// 住居設備合計
					ShoppingHouseEquipmentExpenditureItem.NULL,
					// クーポン金額合計
					ShoppingCouponPrice.NULL,
					// 月度買い物合計金額
					ShoppingTotalAmount.ZERO);
		} else {
			// 食料品(必須)合計
			ShoppingFoodExpenditureItem foodSum = ShoppingFoodExpenditureItem.NULL;
			// 食料品B(無駄遣い)合計
			ShoppingFoodMinorWasteExpenditureItem foodBSum = ShoppingFoodMinorWasteExpenditureItem.NULL;
			// 食料品C(お酒類)合計
			ShoppingFoodSevereWasteExpenditureItem foodCSum = ShoppingFoodSevereWasteExpenditureItem.NULL;
			// 外食合計
			ShoppingDineOutExpenditureItem dineOutSum = ShoppingDineOutExpenditureItem.NULL;
			// 日用品合計
			ShoppingConsumerGoodsExpenditureItem consumerGoodsSum = ShoppingConsumerGoodsExpenditureItem.NULL;
			// 衣料品(私服)合計
			ShoppingClothesExpenditureItem clothesSum = ShoppingClothesExpenditureItem.NULL;
			// 仕事合計
			ShoppingWorkExpenditureItem workSum = ShoppingWorkExpenditureItem.NULL;
			// 住居設備合計
			ShoppingHouseEquipmentExpenditureItem houseEquipmentSum = ShoppingHouseEquipmentExpenditureItem.NULL;
			// クーポン金額合計
			ShoppingCouponPrice couponPriceSum = ShoppingCouponPrice.NULL;
			// 月度買い物合計金額
			ShoppingTotalAmount monthTotalAmountSum = ShoppingTotalAmount.ZERO;
			
			// 対象データありの場合、各種項目の合計値を加算
			for(SimpleShoppingRegistItem item : values) {
				foodSum = foodSum.add(item.getShoppingFoodExpenditureItem());
				foodBSum = foodBSum.add(item.getShoppingFoodMinorWasteExpenditureItem());
				foodCSum = foodCSum.add(item.getShoppingFoodSevereWasteExpenditureItem());
				dineOutSum = dineOutSum.add(item.getShoppingDineOutExpenditureItem());
				consumerGoodsSum = consumerGoodsSum.add(item.getShoppingConsumerGoodsExpenditureItem());
				clothesSum = clothesSum.add(item.getShoppingClothesExpenditureItem());
				workSum = workSum.add(item.getShoppingWorkExpenditureItem());
				houseEquipmentSum = houseEquipmentSum.add(item.getShoppingHouseEquipmentExpenditureItem());
				couponPriceSum = couponPriceSum.add(item.getShoppingCouponPrice());
				monthTotalAmountSum = monthTotalAmountSum.add(item.getShoppingTotalAmount());
			}
			// 簡易タイプ買い物登録情報(リスト情報)ドメインモデルを生成して返却
			return new SimpleShoppingRegistItemInquiryList(
					// 買い物登録情報のリスト
					values,
					// 食料品(必須)合計
					foodSum,
					// 食料品B(無駄遣い)合計
					foodBSum,
					// 食料品C(お酒類)合計
					foodCSum,
					// 外食合計
					dineOutSum,
					// 日用品合計
					consumerGoodsSum,
					// 衣料品(私服)合計
					clothesSum,
					// 仕事合計
					workSum,
					// 住居設備合計
					houseEquipmentSum,
					// クーポン金額合計
					couponPriceSum,
					// 月度買い物合計金額
					monthTotalAmountSum);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if(values.size() > 0) {
			StringBuilder buff = new StringBuilder((values.size() + 1) * 180);
			buff.append("買い物登録情報:")
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
			return "買い物登録情報:0件";
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
