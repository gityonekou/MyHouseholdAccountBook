/**
 * 簡易タイプ買い物登録画面に表示する買い物一覧情報のDB読込情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/12/02 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.account.shoppingregist;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 簡易タイプ買い物登録画面に表示する買い物一覧情報のDB読込情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SimpleShoppingRegistItemReadDto {
	// 対象年
	private final String targetYear;
	// 対象月
	private final String targetMonth;
	// 買い物登録コード
	private final String shoppingRegistCode;
	// 店舗名
	private final String shopName;
	// 買い物日
	private final LocalDate shoppingDate;
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
	// 買い物合計金額
	private final BigDecimal shoppingTotalAmount;
}
