/**
 * 買い物登録情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/11/23 : 1.00.00                      新規作成
 * 2026/03/20 : 1.01.00  feature-1.00-dev00  リファクタリング対応(DDD適応)
 * 2026/09/21 : 1.02.00  feature-1.03-dev1   支払方法・銀行口座管理追加対応、追加リファクタリング対応(買い物登録のドメイン見直し)
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.shoppingregist;

import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopCode;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopKubunCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingClothesExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingConsumerGoodsExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingCouponPrice;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDate;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDineOutExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodMinorWasteExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodSevereWasteExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingHouseEquipmentExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingRegistCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingRemarks;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingWorkExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.TaxTotalPurchasePrice;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.TotalPurchasePrice;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 買い物登録情報を表すドメインモデルです
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
public class ShoppingRegist {
	// ユーザID
	private final UserId userId;
	// 対象年月(YYYYMM)
	private final TargetYearMonth targetYearMonth;
	// 買い物登録コード
	private final ShoppingRegistCode shoppingRegistCode;
	
	// 登録データ(金額以外)
	// 店舗区分コード
	private final ShopKubunCode shopKubunCode;
	// 店舗コード
	private final ShopCode shopCode;
	// 支払方法コード
	private final PaymentMethodCode paymentMethodCode;
	// 買い物日
	private final ShoppingDate shoppingDate;
	// 備考
	private final ShoppingRemarks shoppingRemarks;
	
	// 登録データ(金額)
	// 食料品(必須)
	private final ShoppingFoodExpenditureItem shoppingFoodExpenditureItem;
	// 食料品B(無駄遣い)
	private final ShoppingFoodMinorWasteExpenditureItem shoppingFoodMinorWasteExpenditureItem;
	// 食料品C(お酒類)
	private final ShoppingFoodSevereWasteExpenditureItem shoppingFoodSevereWasteExpenditureItem;
	// 外食金額
	private final ShoppingDineOutExpenditureItem shoppingDineOutExpenditureItem;
	// 日用品金額
	private final ShoppingConsumerGoodsExpenditureItem shoppingConsumerGoodsExpenditureItem;
	// 衣料品(私服)
	private final ShoppingClothesExpenditureItem shoppingClothesExpenditureItem;
	// 仕事金額
	private final ShoppingWorkExpenditureItem shoppingWorkExpenditureItem;
	// 住居設備
	private final ShoppingHouseEquipmentExpenditureItem shoppingHouseEquipmentExpenditureItem;
	// クーポン金額
	private final ShoppingCouponPrice shoppingCouponPrice;
	// 購入金額合計
	private final TotalPurchasePrice totalPurchasePrice;
	// 消費税合計
	private final TaxTotalPurchasePrice taxTotalPurchasePrice;
	// 買い物合計金額
	private final ShoppingTotalAmount shoppingTotalAmount;
	
	/**
	 *<pre>
	 * 引数の値から買い物登録情報を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYearMonth 対象年月
	 * @param shoppingRegistCode 買い物登録コード
	 * @param shopKubunCode 店舗区分コード
	 * @param shopCode 店舗コード
	 * @param paymentMethodCode 支払方法コード
	 * @param shoppingDate 買い物日
	 * @param shoppingRemarks 備考
	 * @param shoppingFoodExpenditureItem 食料品(必須)
	 * @param shoppingFoodMinorWasteExpenditureItem 食料品B(無駄遣い)
	 * @param shoppingFoodSevereWasteExpenditureItem 食料品C(お酒類)
	 * @param shoppingDineOutExpenditureItem 外食
	 * @param shoppingConsumerGoodsExpenditureItem 日用品
	 * @param shoppingClothesExpenditureItem 衣料品(私服)
	 * @param shoppingWorkExpenditureItem 仕事
	 * @param shoppingHouseEquipmentExpenditureItem 住居設備
	 * @param shoppingCouponPrice クーポン金額
	 * @param totalPurchasePrice 購入金額合計
	 * @param taxTotalPurchasePrice 消費税合計
	 * @param shoppingTotalAmount 買い物合計金額
	 * @return 買い物登録情報を表すドメインモデル
	 *
	 */
	public static ShoppingRegist from(
			UserId userId,
			TargetYearMonth targetYearMonth,
			ShoppingRegistCode shoppingRegistCode,
			ShopKubunCode shopKubunCode,
			ShopCode shopCode,
			PaymentMethodCode paymentMethodCode,
			ShoppingDate shoppingDate,
			ShoppingRemarks shoppingRemarks,
			ShoppingFoodExpenditureItem shoppingFoodExpenditureItem,
			ShoppingFoodMinorWasteExpenditureItem shoppingFoodMinorWasteExpenditureItem,
			ShoppingFoodSevereWasteExpenditureItem shoppingFoodSevereWasteExpenditureItem,
			ShoppingDineOutExpenditureItem shoppingDineOutExpenditureItem,
			ShoppingConsumerGoodsExpenditureItem shoppingConsumerGoodsExpenditureItem,
			ShoppingClothesExpenditureItem shoppingClothesExpenditureItem,
			ShoppingWorkExpenditureItem shoppingWorkExpenditureItem,
			ShoppingHouseEquipmentExpenditureItem shoppingHouseEquipmentExpenditureItem,
			ShoppingCouponPrice shoppingCouponPrice,
			TotalPurchasePrice totalPurchasePrice,
			TaxTotalPurchasePrice taxTotalPurchasePrice,
			ShoppingTotalAmount shoppingTotalAmount) {
		
		return new ShoppingRegist(
				userId,
				targetYearMonth,
				shoppingRegistCode,
				shopKubunCode,
				shopCode,
				paymentMethodCode,
				shoppingDate,
				shoppingRemarks,
				shoppingFoodExpenditureItem,
				shoppingFoodMinorWasteExpenditureItem,
				shoppingFoodSevereWasteExpenditureItem,
				shoppingDineOutExpenditureItem,
				shoppingConsumerGoodsExpenditureItem,
				shoppingClothesExpenditureItem,
				shoppingWorkExpenditureItem,
				shoppingHouseEquipmentExpenditureItem,
				shoppingCouponPrice,
				totalPurchasePrice,
				taxTotalPurchasePrice,
				shoppingTotalAmount);
	}
}
