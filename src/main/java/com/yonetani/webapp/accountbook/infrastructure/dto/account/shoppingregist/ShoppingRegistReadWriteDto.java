/**
 * 買い物登録情報テーブル:SHOPPING_REGIST_TABLE読込・出力情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.account.shoppingregist;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.ShoppingRegist;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 買い物登録情報テーブル:SHOPPING_REGIST_TABLE読込・出力情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ShoppingRegistReadWriteDto {
	// ユニークキー
	// ユーザID
	private final String userId;
	// 対象年
	private final String targetYear;
	// 対象月
	private final String targetMonth;
	// 買い物登録コード
	private final String shoppingRegistCode;
	// 登録データ(金額以外)
	// 店舗区分コード
	private final String shopKubunCode;
	// 店舗コード
	private final String shopCode;
	// 買い物日
	private final LocalDate shoppingDate;
	// 備考
	private final String shoppingRemarks;
	// 登録データ(金額)
	// 食料品(必須)金額
	private final BigDecimal shoppingFoodExpenses;
	// 消費税:食料品(必須)金額
	private final BigDecimal shoppingFoodTaxExpenses;
	// 食料品B(無駄遣い)金額
	private final BigDecimal shoppingFoodBExpenses;
	// 消費税:食料品B(無駄遣い)金額
	private final BigDecimal shoppingFoodBTaxExpenses;
	// 食料品C(お酒類)金額
	private final BigDecimal shoppingFoodCExpenses;
	// 消費税:食料品C(お酒類)金額
	private final BigDecimal shoppingFoodCTaxExpenses;
	// 外食金額
	private final BigDecimal shoppingDineOutExpenses;
	// 消費税:外食金額
	private final BigDecimal shoppingDineOutTaxExpenses;
	// 日用品金額
	private final BigDecimal shoppingConsumerGoodsExpenses;
	// 消費税:日用品金額
	private final BigDecimal shoppingConsumerGoodsTaxExpenses;
	// 衣料品(私服)金額
	private final BigDecimal shoppingClothesExpenses;
	// 消費税:衣料品(私服)金額
	private final BigDecimal shoppingClothesTaxExpenses;
	// 仕事金額
	private final BigDecimal shoppingWorkExpenses;
	// 消費税:仕事金額
	private final BigDecimal shoppingWorkTaxExpenses;
	// 住居設備金額
	private final BigDecimal shoppingHouseEquipmentExpenses;
	// 消費税:住居設備金額
	private final BigDecimal shoppingHouseEquipmentTaxExpenses;
	// クーポン
	private final BigDecimal shoppingCouponPrice;
	// 購入金額合計
	private final BigDecimal totalPurchasePrice;
	// 消費税合計
	private final BigDecimal taxTotalPurchasePrice;
	// 買い物合計金額
	private final BigDecimal shoppingTotalAmount;
	
	/**
	 *<pre>
	 * 買い物登録情報ドメインモデルをもとにShoppingRegistReadWriteDtoを生成して返します。
	 *</pre>
	 * @param domain 買い物登録情報ドメインモデル
	 * @return 買い物登録情報テーブル:SHOPPING_REGIST_TABLE読込・出力情報
	 *
	 */
	public static ShoppingRegistReadWriteDto from(ShoppingRegist domain) {
		
		return new ShoppingRegistReadWriteDto(
				// ユーザID
				domain.getUserId().getValue(),
				// 対象年
				domain.getTargetYearMonth().getYear(),
				// 対象月
				domain.getTargetYearMonth().getMonth(),
				// 買い物登録コード
				domain.getShoppingRegistCode().getValue(),
				// 店舗区分コード
				domain.getShopKubunCode().getValue(),
				// 店舗コード
				domain.getShopCode().getValue(),
				// 買い物日
				domain.getShoppingDate().getValue(),
				// 備考
				domain.getShoppingRemarks().getValue(),
				// 食料品(必須)金額
				domain.getShoppingFoodExpenses().getValue(),
				// 消費税:食料品(必須)金額
				domain.getShoppingFoodTaxExpenses().getValue(),
				// 食料品B(無駄遣い)金額
				domain.getShoppingFoodBExpenses().getValue(),
				// 消費税:食料品B(無駄遣い)金額
				domain.getShoppingFoodBTaxExpenses().getValue(),
				// 食料品C(お酒類)金額
				domain.getShoppingFoodCExpenses().getValue(),
				// 消費税:食料品C(お酒類)金額
				domain.getShoppingFoodCTaxExpenses().getValue(),
				// 外食金額
				domain.getShoppingDineOutExpenses().getValue(),
				// 消費税:外食金額
				domain.getShoppingDineOutTaxExpenses().getValue(),
				// 日用品金額
				domain.getShoppingConsumerGoodsExpenses().getValue(),
				// 消費税:日用品金額
				domain.getShoppingConsumerGoodsTaxExpenses().getValue(),
				// 衣料品(私服)金額
				domain.getShoppingClothesExpenses().getValue(),
				// 消費税:衣料品(私服)金額
				domain.getShoppingClothesTaxExpenses().getValue(),
				// 仕事金額
				domain.getShoppingWorkExpenses().getValue(),
				// 消費税:仕事金額
				domain.getShoppingWorkTaxExpenses().getValue(),
				// 住居設備金額
				domain.getShoppingHouseEquipmentExpenses().getValue(),
				// 消費税:住居設備金額
				domain.getShoppingHouseEquipmentTaxExpenses().getValue(),
				// クーポン金額
				domain.getShoppingCouponPrice().getValue(),
				// 購入金額合計
				domain.getTotalPurchasePrice().getValue(),
				// 消費税合計
				domain.getTaxTotalPurchasePrice().getValue(),
				// 買い物合計金額
				domain.getShoppingTotalAmount().getValue());
	}
}
