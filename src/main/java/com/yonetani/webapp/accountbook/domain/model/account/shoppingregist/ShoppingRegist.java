/**
 * 買い物登録情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.shoppingregist;

import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopCode;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopKubunCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingClothesExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingClothesTaxExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingConsumerGoodsExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingConsumerGoodsTaxExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingCouponPrice;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDate;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDineOutExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDineOutTaxExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodBExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodBTaxExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodCExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodCTaxExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodTaxExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingHouseEquipmentExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingHouseEquipmentTaxExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingRegistCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingRemarks;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingWorkExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingWorkTaxExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.TaxTotalPurchasePrice;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.TotalPurchasePrice;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;
import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.SimpleShoppingRegistInfoForm;

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
 * @since 家計簿アプリ(1.00.A)
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
	// 買い物日
	private final ShoppingDate shoppingDate;
	// 備考
	private final ShoppingRemarks shoppingRemarks;
	
	// 登録データ(金額)
	// 食料品(必須)金額
	private final ShoppingFoodExpenses shoppingFoodExpenses;
	// 消費税:食料品(必須)金額
	private final ShoppingFoodTaxExpenses shoppingFoodTaxExpenses;
	// 食料品B(無駄遣い)金額
	private final ShoppingFoodBExpenses shoppingFoodBExpenses;
	// 消費税:食料品B(無駄遣い)金額
	private final ShoppingFoodBTaxExpenses shoppingFoodBTaxExpenses;
	// 食料品C(お酒類)金額
	private final ShoppingFoodCExpenses shoppingFoodCExpenses;
	// 消費税:食料品C(お酒類)金額
	private final ShoppingFoodCTaxExpenses shoppingFoodCTaxExpenses;
	// 外食金額
	private final ShoppingDineOutExpenses shoppingDineOutExpenses;
	// 消費税:外食金額
	private final ShoppingDineOutTaxExpenses shoppingDineOutTaxExpenses;
	// 日用品金額
	private final ShoppingConsumerGoodsExpenses shoppingConsumerGoodsExpenses;
	// 消費税:日用品金額
	private final ShoppingConsumerGoodsTaxExpenses shoppingConsumerGoodsTaxExpenses;
	// 衣料品(私服)金額
	private final ShoppingClothesExpenses shoppingClothesExpenses;
	// 消費税:衣料品(私服)金額
	private final ShoppingClothesTaxExpenses shoppingClothesTaxExpenses;
	// 仕事金額
	private final ShoppingWorkExpenses shoppingWorkExpenses;
	// 消費税:仕事金額
	private final ShoppingWorkTaxExpenses shoppingWorkTaxExpenses;
	// 住居設備金額
	private final ShoppingHouseEquipmentExpenses shoppingHouseEquipmentExpenses;
	// 消費税:住居設備金額
	private final ShoppingHouseEquipmentTaxExpenses shoppingHouseEquipmentTaxExpenses;
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
	 * @param shoppingDate 買い物日
	 * @param shoppingRemarks 備考
	 * @param shoppingFoodExpenses 食料品(必須)金額
	 * @param shoppingFoodTaxExpenses 消費税:食料品(必須)金額
	 * @param shoppingFoodBExpenses 食料品B(無駄遣い)金額
	 * @param shoppingFoodBTaxExpenses 消費税:食料品B(無駄遣い)金額
	 * @param shoppingFoodCExpenses 食料品C(お酒類)金額
	 * @param shoppingFoodCTaxExpenses 消費税:食料品C(お酒類)金額
	 * @param shoppingDineOutExpenses 外食金額
	 * @param shoppingDineOutTaxExpenses 消費税:外食金額
	 * @param shoppingConsumerGoodsExpenses 日用品金額
	 * @param shoppingConsumerGoodsTaxExpenses 消費税:日用品金額
	 * @param shoppingClothesExpenses 衣料品(私服)金額
	 * @param shoppingClothesTaxExpenses 消費税:衣料品(私服)金額
	 * @param shoppingWorkExpenses 仕事金額
	 * @param shoppingWorkTaxExpenses 消費税:仕事金額
	 * @param shoppingHouseEquipmentExpenses 住居設備金額
	 * @param shoppingHouseEquipmentTaxExpenses 消費税:住居設備金額
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
			ShoppingDate shoppingDate,
			ShoppingRemarks shoppingRemarks,
			ShoppingFoodExpenses shoppingFoodExpenses,
			ShoppingFoodTaxExpenses shoppingFoodTaxExpenses,
			ShoppingFoodBExpenses shoppingFoodBExpenses,
			ShoppingFoodBTaxExpenses shoppingFoodBTaxExpenses,
			ShoppingFoodCExpenses shoppingFoodCExpenses,
			ShoppingFoodCTaxExpenses shoppingFoodCTaxExpenses,
			ShoppingDineOutExpenses shoppingDineOutExpenses,
			ShoppingDineOutTaxExpenses shoppingDineOutTaxExpenses,
			ShoppingConsumerGoodsExpenses shoppingConsumerGoodsExpenses,
			ShoppingConsumerGoodsTaxExpenses shoppingConsumerGoodsTaxExpenses,
			ShoppingClothesExpenses shoppingClothesExpenses,
			ShoppingClothesTaxExpenses shoppingClothesTaxExpenses,
			ShoppingWorkExpenses shoppingWorkExpenses,
			ShoppingWorkTaxExpenses shoppingWorkTaxExpenses,
			ShoppingHouseEquipmentExpenses shoppingHouseEquipmentExpenses,
			ShoppingHouseEquipmentTaxExpenses shoppingHouseEquipmentTaxExpenses,
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
				shoppingDate,
				shoppingRemarks,
				shoppingFoodExpenses,
				shoppingFoodTaxExpenses,
				shoppingFoodBExpenses,
				shoppingFoodBTaxExpenses,
				shoppingFoodCExpenses,
				shoppingFoodCTaxExpenses,
				shoppingDineOutExpenses,
				shoppingDineOutTaxExpenses,
				shoppingConsumerGoodsExpenses,
				shoppingConsumerGoodsTaxExpenses,
				shoppingClothesExpenses,
				shoppingClothesTaxExpenses,
				shoppingWorkExpenses,
				shoppingWorkTaxExpenses,
				shoppingHouseEquipmentExpenses,
				shoppingHouseEquipmentTaxExpenses,
				shoppingCouponPrice,
				totalPurchasePrice,
				taxTotalPurchasePrice,
				shoppingTotalAmount);
	}
	
	/**
	 *<pre>
	 * 買い物登録フォームデータから買い物登録情報(ドメイン)を生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param inputForm 買い物登録フォームデータ
	 * @return 買い物登録情報(ドメイン)
	 *
	 */
	public static ShoppingRegist createShoppingRegist(UserId userId,  SimpleShoppingRegistInfoForm inputForm) {
		
		// 対象年月(YYYYMM)のドメインを生成
		TargetYearMonth targetYearMonth = TargetYearMonth.from(inputForm.getTargetYearMonth());
		
		return ShoppingRegist.from(
				// ユーザID
				userId,
				// 対象年月(YYYYMM)
				targetYearMonth,
				// 買い物登録コード
				ShoppingRegistCode.from(inputForm.getShoppingRegistCode()),
				// 店舗区分コード
				ShopKubunCode.from(inputForm.getShopKubunCode()),
				// 店舗コード
				ShopCode.from(inputForm.getShopCode()),
				// 買い物日
				ShoppingDate.from(inputForm.getShoppingDate(), targetYearMonth),
				// 備考
				ShoppingRemarks.from(inputForm.getShoppingRemarks()),
				// 食料品(必須)金額
				ShoppingFoodExpenses.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getShoppingFoodExpenses())),
				// 消費税:食料品(必須)金額
				ShoppingFoodTaxExpenses.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getShoppingFoodTaxExpenses())),
				// 食料品B(無駄遣い)金額
				ShoppingFoodBExpenses.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getShoppingFoodBExpenses())),
				// 消費税:食料品B(無駄遣い)金額
				ShoppingFoodBTaxExpenses.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getShoppingFoodBTaxExpenses())),
				// 食料品C(お酒類)金額
				ShoppingFoodCExpenses.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getShoppingFoodCExpenses())),
				// 消費税:食料品C(お酒類)金額
				ShoppingFoodCTaxExpenses.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getShoppingFoodCTaxExpenses())),
				// 外食金額
				ShoppingDineOutExpenses.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getShoppingDineOutExpenses())),
				// 消費税:外食金額
				ShoppingDineOutTaxExpenses.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getShoppingDineOutTaxExpenses())),
				// 日用品金額
				ShoppingConsumerGoodsExpenses.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getShoppingConsumerGoodsExpenses())),
				// 消費税:日用品金額
				ShoppingConsumerGoodsTaxExpenses.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getShoppingConsumerGoodsTaxExpenses())),
				// 衣料品(私服)金額
				ShoppingClothesExpenses.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getShoppingClothesExpenses())),
				// 消費税:衣料品(私服)金額
				ShoppingClothesTaxExpenses.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getShoppingClothesTaxExpenses())),
				// 仕事金額
				ShoppingWorkExpenses.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getShoppingWorkExpenses())),
				// 消費税:仕事金額
				ShoppingWorkTaxExpenses.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getShoppingWorkTaxExpenses())),
				// 住居設備金額
				ShoppingHouseEquipmentExpenses.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getShoppingHouseEquipmentExpenses())),
				// 消費税:住居設備金額
				ShoppingHouseEquipmentTaxExpenses.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getShoppingHouseEquipmentTaxExpenses())),
				// クーポン金額
				ShoppingCouponPrice.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getShoppingCouponPrice())),
				// 購入金額合計
				TotalPurchasePrice.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getTotalPurchasePrice())),
				// 消費税合計
				TaxTotalPurchasePrice.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getTaxTotalPurchasePrice())),
				// 買い物合計金額
				ShoppingTotalAmount.from(DomainCommonUtils.convertKingakuBigDecimal(inputForm.getShoppingTotalAmount())));
	}
}
