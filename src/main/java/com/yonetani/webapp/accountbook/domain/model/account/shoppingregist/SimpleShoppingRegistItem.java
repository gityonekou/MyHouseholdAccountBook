/**
 * 買い物登録一覧情報(簡易タイプ)を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/12/02 : 1.00.00                      新規作成
 * 2026/08/18 : 1.01.00  feature-1.03-dev1   支払方法・銀行口座管理追加対応
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.shoppingregist;

import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopName;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingClothesExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingConsumerGoodsExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingCouponPrice;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDate;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDineOutExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodMinorWasteExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodSevereWasteExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingHouseEquipmentExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingRegistCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingWorkExpenditureItem;
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
 * @since 家計簿アプリ(1.00)
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
	// 支払方法コード
	private final PaymentMethodCode paymentMethodCode;
	// 買い物日
	private final ShoppingDate shoppingDate;
	// 一覧項目:食料品(必須)
	private final ShoppingFoodExpenditureItem shoppingFoodExpenditureItem;
	// 一覧項目:食料品B(無駄遣い)
	private final ShoppingFoodMinorWasteExpenditureItem shoppingFoodMinorWasteExpenditureItem;
	// 一覧項目:食料品C(お酒類)
	private final ShoppingFoodSevereWasteExpenditureItem shoppingFoodSevereWasteExpenditureItem;
	// 一覧項目:外食
	private final ShoppingDineOutExpenditureItem shoppingDineOutExpenditureItem;
	// 一覧項目:日用品
	private final ShoppingConsumerGoodsExpenditureItem shoppingConsumerGoodsExpenditureItem;
	// 一覧項目:衣料品(私服)
	private final ShoppingClothesExpenditureItem shoppingClothesExpenditureItem;
	// 一覧項目:仕事
	private final ShoppingWorkExpenditureItem shoppingWorkExpenditureItem;
	// 一覧項目:住居設備
	private final ShoppingHouseEquipmentExpenditureItem shoppingHouseEquipmentExpenditureItem;
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
	 * @param paymentMethodCode 支払方法コード
	 * @param shoppingDate 買い物日
	 * @param shoppingFoodExpenditureItem 食料品(必須)
	 * @param shoppingFoodMinorWasteExpenditureItem 食料品B(無駄遣い)
	 * @param shoppingFoodSevereWasteExpenditureItem 食料品C(お酒類)
	 * @param shoppingDineOutExpenditureItem 外食
	 * @param shoppingConsumerGoodsExpenditureItem 日用品
	 * @param shoppingClothesExpenditureItem 衣料品(私服)
	 * @param shoppingWorkExpenditureItem 仕事
	 * @param shoppingHouseEquipmentExpenditureItem 住居設備
	 * @param shoppingCouponPrice クーポン金額
	 * @param shoppingTotalAmount 買い物合計金額
	 * @return 買い物登録一覧情報(簡易タイプ)を表すドメインモデル
	 *
	 */
	public static SimpleShoppingRegistItem from(
			TargetYearMonth targetYearMonth,
			ShoppingRegistCode shoppingRegistCode,
			ShopName shopName,
			PaymentMethodCode paymentMethodCode,
			ShoppingDate shoppingDate,
			ShoppingFoodExpenditureItem shoppingFoodExpenditureItem,
			ShoppingFoodMinorWasteExpenditureItem shoppingFoodMinorWasteExpenditureItem,
			ShoppingFoodSevereWasteExpenditureItem shoppingFoodSevereWasteExpenditureItem,
			ShoppingDineOutExpenditureItem shoppingDineOutExpenditureItem,
			ShoppingConsumerGoodsExpenditureItem shoppingConsumerGoodsExpenditureItem,
			ShoppingClothesExpenditureItem shoppingClothesExpenditureItem,
			ShoppingWorkExpenditureItem shoppingWorkExpenditureItem,
			ShoppingHouseEquipmentExpenditureItem shoppingHouseEquipmentExpenditureItem,
			ShoppingCouponPrice shoppingCouponPrice,
			ShoppingTotalAmount shoppingTotalAmount) {

		return new SimpleShoppingRegistItem(
				targetYearMonth,
				shoppingRegistCode,
				shopName,
				paymentMethodCode,
				shoppingDate,
				shoppingFoodExpenditureItem,
				shoppingFoodMinorWasteExpenditureItem,
				shoppingFoodSevereWasteExpenditureItem,
				shoppingDineOutExpenditureItem,
				shoppingConsumerGoodsExpenditureItem,
				shoppingClothesExpenditureItem,
				shoppingWorkExpenditureItem,
				shoppingHouseEquipmentExpenditureItem,
				shoppingCouponPrice,
				shoppingTotalAmount);
	}
}
