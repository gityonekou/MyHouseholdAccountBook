/**
 * 簡易タイプ買い物登録情報(リスト情報)の値を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.shoppingregist;

import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingClothesItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingConsumerGoodsItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingCouponPrice;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDineOutItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodBItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodCItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingHouseEquipmentItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingWorkItem;

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
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SimpleShoppingRegistItemInquiryList {
	
	// 買い物登録情報のリスト
	private final List<SimpleShoppingRegistItem> values;
	
	// 食料品(必須)合計
	private final ShoppingFoodItem totalShoppingFoodItem;
	// 食料品B(無駄遣い)合計
	private final ShoppingFoodBItem totalShoppingFoodBItem;
	// 食料品C(お酒類)合計
	private final ShoppingFoodCItem totalShoppingFoodCItem;
	// 外食合計
	private final ShoppingDineOutItem totalShoppingDineOutItem;
	// 日用品合計
	private final ShoppingConsumerGoodsItem totalShoppingConsumerGoodsItem;
	// 衣料品(私服)合計
	private final ShoppingClothesItem totalShoppingClothesItem;
	// 仕事合計
	private final ShoppingWorkItem totalShoppingWorkItem;
	// 住居設備合計
	private final ShoppingHouseEquipmentItem totalShoppingHouseEquipmentItem;
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
					ShoppingFoodItem.nullValue(),
					// 食料品B(無駄遣い)合計
					ShoppingFoodBItem.nullValue(),
					// 食料品C(お酒類)合計
					ShoppingFoodCItem.nullValue(),
					// 外食合計
					ShoppingDineOutItem.nullValue(),
					// 日用品合計
					ShoppingConsumerGoodsItem.nullValue(),
					// 衣料品(私服)合計
					ShoppingClothesItem.nullValue(),
					// 仕事合計
					ShoppingWorkItem.nullValue(),
					// 住居設備合計
					ShoppingHouseEquipmentItem.nullValue(),
					// クーポン金額合計
					ShoppingCouponPrice.from(null),
					// 月度買い物合計金額
					ShoppingTotalAmount.ZERO);
		} else {
			// 食料品(必須)合計
			ShoppingFoodItem foodSum = ShoppingFoodItem.nullValue();
			// 食料品B(無駄遣い)合計
			ShoppingFoodBItem foodBSum = ShoppingFoodBItem.nullValue();
			// 食料品C(お酒類)合計
			ShoppingFoodCItem foodCSum = ShoppingFoodCItem.nullValue();
			// 外食合計
			ShoppingDineOutItem dineOutSum = ShoppingDineOutItem.nullValue();
			// 日用品合計
			ShoppingConsumerGoodsItem consumerGoodsSum = ShoppingConsumerGoodsItem.nullValue();
			// 衣料品(私服)合計
			ShoppingClothesItem clothesSum = ShoppingClothesItem.nullValue();
			// 仕事合計
			ShoppingWorkItem workSum = ShoppingWorkItem.nullValue();
			// 住居設備合計
			ShoppingHouseEquipmentItem houseEquipmentSum = ShoppingHouseEquipmentItem.nullValue();
			// クーポン金額合計
			ShoppingCouponPrice couponPriceSum = ShoppingCouponPrice.from(null);
			// 月度買い物合計金額
			ShoppingTotalAmount monthTotalAmountSum = ShoppingTotalAmount.ZERO;
			
			// 対象データありの場合、各種項目の合計値を加算
			for(SimpleShoppingRegistItem item : values) {
				foodSum = foodSum.add(item.getShoppingFoodItem());
				foodBSum = foodBSum.add(item.getShoppingFoodBItem());
				foodCSum = foodCSum.add(item.getShoppingFoodCItem());
				dineOutSum = dineOutSum.add(item.getShoppingDineOutItem());
				consumerGoodsSum = consumerGoodsSum.add(item.getShoppingConsumerGoodsItem());
				clothesSum = clothesSum.add(item.getShoppingClothesItem());
				workSum = workSum.add(item.getShoppingWorkItem());
				houseEquipmentSum = houseEquipmentSum.add(item.getShoppingHouseEquipmentItem());
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
