/**
 * 買い物登録一覧情報(簡易タイプ)を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/12/02 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.shoppingregist;

import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopName;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingClothesItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingConsumerGoodsItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingCouponPrice;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDate;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDineOutItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodBItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodCItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingHouseEquipmentItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingRegistCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingWorkItem;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 買い物登録一覧情報(簡易タイプ)を表すドメインモデルです
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
public class SimpleShoppingRegistItem {
	// 対象年月(YYYYMM)
	private final TargetYearMonth targetYearMonth;
	// 買い物登録コード
	private final ShoppingRegistCode shoppingRegistCode;
	// 店舗名
	private final ShopName shopName;
	// 買い物日
	private final ShoppingDate shoppingDate;
	// 一覧項目:食料品(必須)
	private final ShoppingFoodItem shoppingFoodItem;
	// 一覧項目:食料品B(無駄遣い)
	private final ShoppingFoodBItem shoppingFoodBItem;
	// 一覧項目:食料品C(お酒類)
	private final ShoppingFoodCItem shoppingFoodCItem;
	// 一覧項目:外食
	private final ShoppingDineOutItem shoppingDineOutItem;
	// 一覧項目:日用品
	private final ShoppingConsumerGoodsItem shoppingConsumerGoodsItem;
	// 一覧項目:衣料品(私服)
	private final ShoppingClothesItem shoppingClothesItem;
	// 一覧項目:仕事
	private final ShoppingWorkItem shoppingWorkItem;
	// 一覧項目:住居設備
	private final ShoppingHouseEquipmentItem shoppingHouseEquipmentItem;
	// クーポン金額
	private final ShoppingCouponPrice shoppingCouponPrice;
	// 買い物合計金額
	private final ShoppingTotalAmount shoppingTotalAmount;
	
	/**
	 *<pre>
	 * 引数の値から買い物登録一覧情報(簡易タイプ)を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param targetYearMonth 対象年月
	 * @param shoppingRegistCode 買い物登録コード
	 * @param shopName 店舗名
	 * @param shoppingDate 買い物日
	 * @param shoppingFoodItem 食料品(必須)
	 * @param shoppingFoodBItem 食料品B(無駄遣い)
	 * @param shoppingFoodCItem 食料品C(お酒類)
	 * @param shoppingDineOutItem 外食
	 * @param shoppingConsumerGoodsItem 日用品
	 * @param shoppingClothesItem 衣料品(私服)
	 * @param shoppingWorkItem 仕事
	 * @param shoppingHouseEquipmentItem 住居設備
	 * @param shoppingCouponPrice クーポン金額
	 * @param shoppingTotalAmount 買い物合計金額
	 * @return 買い物登録一覧情報(簡易タイプ)を表すドメインモデル
	 *
	 */
	public static SimpleShoppingRegistItem from(
			TargetYearMonth targetYearMonth,
			ShoppingRegistCode shoppingRegistCode,
			ShopName shopName,
			ShoppingDate shoppingDate,
			ShoppingFoodItem shoppingFoodItem,
			ShoppingFoodBItem shoppingFoodBItem,
			ShoppingFoodCItem shoppingFoodCItem,
			ShoppingDineOutItem shoppingDineOutItem,
			ShoppingConsumerGoodsItem shoppingConsumerGoodsItem,
			ShoppingClothesItem shoppingClothesItem,
			ShoppingWorkItem shoppingWorkItem,
			ShoppingHouseEquipmentItem shoppingHouseEquipmentItem,
			ShoppingCouponPrice shoppingCouponPrice,
			ShoppingTotalAmount shoppingTotalAmount) {
		
		return new SimpleShoppingRegistItem(
				targetYearMonth,
				shoppingRegistCode,
				shopName,
				shoppingDate,
				shoppingFoodItem,
				shoppingFoodBItem,
				shoppingFoodCItem,
				shoppingDineOutItem,
				shoppingConsumerGoodsItem,
				shoppingClothesItem,
				shoppingWorkItem,
				shoppingHouseEquipmentItem,
				shoppingCouponPrice,
				shoppingTotalAmount);
	}
}
