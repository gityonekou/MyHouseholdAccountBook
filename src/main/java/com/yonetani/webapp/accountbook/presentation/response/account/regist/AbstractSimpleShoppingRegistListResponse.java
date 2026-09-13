/**
 * 家計簿の買い物登録系画面で簡易タイプ買い物リスト(excel家計簿の日々の買い物リストの項目)を定義した基底クラスです。
 * 買い物登録(簡易タイプ)画面、買い物登録方法選択画面で表示する簡易タイプ買い物リストの各項目を定義しています。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/12/28 : 1.00.00  新規作成
 * 2026/08/18 : 1.03.00  支払方法・銀行口座管理追加対応(Feature1.03 dev1)
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.account.regist;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.application.usecase.account.component.PaymentMethodInfoComponent.ResolvedPaymentMethodName;
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
import lombok.Setter;

/**
 *<pre>
 * 家計簿の買い物登録系画面で簡易タイプ買い物リスト(excel家計簿の日々の買い物リストの項目)を定義した基底クラスです。
 * 買い物登録(簡易タイプ)画面、買い物登録方法選択画面で表示する簡易タイプ買い物リストの各項目を定義しています。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractSimpleShoppingRegistListResponse extends AbstractRegistResponse {
	
	/**
	 *<pre>
	 * 買い物登録(簡易タイプ)画面に表示する当月の買い物一覧情報です
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	@EqualsAndHashCode
	public static class SimpleShoppingRegistListItem {
		// 対象年月
		private final String targetYearMonth;
		// 買い物登録コード
		private final String shoppingRegistCode;
		// 買い物日(MM/dd形式の日付の値)
		private final String shoppingDay;
		// 店舗名
		private final String shopName;
		// 支払方法名(解決済み)
		private final String paymentMethodName;
		// 食料品(必須)
		private final String shoppingFood;
		// 食料品B(無駄遣い)
		private final String shoppingFoodB;
		// 食料品C(お酒類)
		private final String shoppingFoodC;
		// 外食
		private final String shoppingDineOut;
		// 日用品
		private final String shoppingConsumerGoods;
		// 衣料品(私服)
		private final String shoppingClothes;
		// 仕事
		private final String shoppingWork;
		// 住居設備
		private final String shoppingHouseEquipment;
		// クーポン金額
		private final String shoppingCoupon;
		// 買い物合計金額
		private final String shoppingTotalAmount;
		
		/**
		 *<pre>
		 * 引数の値から当月の買い物一覧情報を生成して返します。
		 *</pre>
		 * @param targetYearMonth 対象年月
		 * @param shoppingRegistCode 買い物登録コード
		 * @param shoppingDay 買い物日(DD:日付の値を設定)
		 * @param shopName 店舗名
		 * @param paymentMethodName 支払方法名(解決済み)
		 * @param shoppingFood 食料品(必須)
		 * @param shoppingFoodB 食料品B(無駄遣い)
		 * @param shoppingFoodC 食料品C(お酒類)金額
		 * @param shoppingDineOut 外食
		 * @param shoppingConsumerGoods 日用品
		 * @param shoppingClothes 衣料品(私服)
		 * @param shoppingWork 仕事
		 * @param shoppingHouseEquipment 住居設備
		 * @param shoppingCoupon クーポン金額
		 * @param shoppingTotalAmount 買い物合計金額
		 * @return 当月の買い物一覧情報
		 *
		 */
		public static SimpleShoppingRegistListItem from(
				TargetYearMonth targetYearMonth,
				ShoppingRegistCode shoppingRegistCode,
				ShoppingDate shoppingDay,
				ShopName shopName,
				ResolvedPaymentMethodName paymentMethodName,
				ShoppingFoodItem shoppingFood,
				ShoppingFoodBItem shoppingFoodB,
				ShoppingFoodCItem shoppingFoodC,
				ShoppingDineOutItem shoppingDineOut,
				ShoppingConsumerGoodsItem shoppingConsumerGoods,
				ShoppingClothesItem shoppingClothes,
				ShoppingWorkItem shoppingWork,
				ShoppingHouseEquipmentItem shoppingHouseEquipment,
				ShoppingCouponPrice shoppingCoupon,
				ShoppingTotalAmount shoppingTotalAmount) {
			return new SimpleShoppingRegistListItem(
					targetYearMonth.getValue(),
					shoppingRegistCode.getValue(),
					shoppingDay.toFormatString(),
					shopName.getValue(),
					paymentMethodName.getValue(),
					shoppingFood.toFormatString(),
					shoppingFoodB.toFormatString(),
					shoppingFoodC.toFormatString(),
					shoppingDineOut.toFormatString(),
					shoppingConsumerGoods.toFormatString(),
					shoppingClothes.toFormatString(),
					shoppingWork.toFormatString(),
					shoppingHouseEquipment.toFormatString(),
					shoppingCoupon.toFormatString(),
					shoppingTotalAmount.toFormatString());
		}
	}
	
	// 当月の買い物一覧情報
	private List<SimpleShoppingRegistListItem> simpleShoppingRegistListItemInfo = new ArrayList<>();
	// 食料品(必須)合計
	@Setter
	private String totalShoppingFood;
	// 食料品B(無駄遣い)合計
	@Setter
	private String totalShoppingFoodB;
	// 食料品C(お酒類)合計
	@Setter
	private String totalShoppingFoodC;
	// 外食合計
	@Setter
	private String totalShoppingDineOut;
	// 日用品合計
	@Setter
	private String totalShoppingConsumerGoods;
	// 衣料品(私服)合計
	@Setter
	private String totalShoppingClothes;
	// 仕事合計
	@Setter
	private String totalShoppingWork;
	// 住居設備合計
	@Setter
	private String totalShoppingHouseEquipment;
	// クーポン金額合計
	@Setter
	private String totalShoppingCouponPrice;
	// 月度買い物合計金額
	@Setter
	private String shoppingMonthTotalAmount;
	
	/**
	 *<pre>
	 * 当月の買い物一覧情報を追加します。
	 *</pre>
	 * @param addList 追加する当月の買い物一覧情報
	 *
	 */
	public void addShoppingRegistListItemInfo(List<SimpleShoppingRegistListItem> addList) {
		if(!CollectionUtils.isEmpty(addList)) {
			simpleShoppingRegistListItemInfo.addAll(addList);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ModelAndView createModelAndView(String viewName) {
		ModelAndView modelAndView = super.createModelAndView(viewName);
		// 当月の買い物一覧情報
		modelAndView.addObject("shoppingRegistList", simpleShoppingRegistListItemInfo);
		
		// 食料品(必須)合計
		modelAndView.addObject("totalShoppingFood", totalShoppingFood);
		// 食料品B(無駄遣い)合計
		modelAndView.addObject("totalShoppingFoodB", totalShoppingFoodB);
		// 食料品C(お酒類)合計
		modelAndView.addObject("totalShoppingFoodC", totalShoppingFoodC);
		// 外食合計
		modelAndView.addObject("totalShoppingDineOut", totalShoppingDineOut);
		// 日用品合計
		modelAndView.addObject("totalShoppingConsumerGoods", totalShoppingConsumerGoods);
		// 衣料品(私服)合計
		modelAndView.addObject("totalShoppingClothes", totalShoppingClothes);
		// 仕事合計
		modelAndView.addObject("totalShoppingWork", totalShoppingWork);
		// 住居設備合計
		modelAndView.addObject("totalShoppingHouseEquipment", totalShoppingHouseEquipment);
		// クーポン金額合計
		modelAndView.addObject("totalShoppingCouponPrice", totalShoppingCouponPrice);
		// 月度買い物合計金額
		modelAndView.addObject("shoppingMonthTotalAmount", shoppingMonthTotalAmount);
		
		return modelAndView;
	}

}
