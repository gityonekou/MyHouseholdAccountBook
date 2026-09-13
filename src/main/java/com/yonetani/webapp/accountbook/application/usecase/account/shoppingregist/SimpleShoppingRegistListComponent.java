/**
 * 簡易タイプ買い物リスト取得コンポーネント
 * 簡易タイプ買い物リスト(excel家計簿の日々の買い物リストの項目)の情報を取得しAbstractSimpleShoppingRegistListResponseに設定するコンポーネントです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/12/28 : 1.00.00                      新規作成
 * 2026/08/18 : 1.01.00  feature-1.03-dev1   支払方法・銀行口座管理追加対応
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.shoppingregist;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.yonetani.webapp.accountbook.application.usecase.account.component.PaymentMethodInfoComponent;
import com.yonetani.webapp.accountbook.application.usecase.account.component.PaymentMethodInfoComponent.PaymentMethodNameResolver;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.SimpleShoppingRegistItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.repository.account.shoppingregist.ShoppingRegistTableRepository;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.AbstractSimpleShoppingRegistListResponse;
import com.yonetani.webapp.accountbook.presentation.response.account.regist.AbstractSimpleShoppingRegistListResponse.SimpleShoppingRegistListItem;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 簡易タイプ買い物リスト取得コンポーネント
 * 簡易タイプ買い物リスト(excel家計簿の日々の買い物リストの項目)の情報を取得しAbstractSimpleShoppingRegistListResponseに設定するコンポーネントです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class SimpleShoppingRegistListComponent {
	
	// 買い物登録情報リポジトリー
	private final ShoppingRegistTableRepository shoppingRegistRepository;
	// 支払方法コード→表示名解決コンポーネント
	private final PaymentMethodInfoComponent paymentMethodInfoComponent;

	/**
	 *<pre>
	 * 簡易タイプ買い物リスト情報を取得し、画面情報に設定します。
	 *</pre>
	 * @param search
	 * @param response
	 *
	 */
	public void setSimpleShoppingRegistList(SearchQueryUserIdAndYearMonth search, AbstractSimpleShoppingRegistListResponse response) {
		log.debug("setSimpleShoppingRegistList:userid="+ search.getUserId().getValue() + ",yearMonth=" + search.getYearMonth().getValue());
		
		// 対象月の登録されている買い物情報を取得
		SimpleShoppingRegistItemInquiryList resultList = shoppingRegistRepository.findBy(search);
		if(resultList.isEmpty()) {
			// 登録済み買い物情報が0件の場合、メッセージを設定
			response.addMessage("登録済みの買い物情報は0件です。");
		} else {
			// 支払方法コード→表示名解決リゾルバをリクエスト単位で1回だけ生成(N+1回避)
			PaymentMethodNameResolver paymentMethodNameResolver = paymentMethodInfoComponent.createResolver(search.getUserId());
			response.addShoppingRegistListItemInfo(resultList.getValues().stream().map(domain ->
				SimpleShoppingRegistListItem.from(
						// 対象年月
						domain.getTargetYearMonth(),
						// 買い物登録コード
						domain.getShoppingRegistCode(),
						// 買い物日
						domain.getShoppingDate(),
						// 店舗名
						domain.getShopName(),
						// 支払方法名(解決済み)
						paymentMethodNameResolver.getPaymentMethodName(domain.getPaymentMethodCode()),
						// 食料品(必須)
						domain.getShoppingFoodItem(),
						// 食料品B(無駄遣い)
						domain.getShoppingFoodBItem(),
						// 食料品C(お酒類)
						domain.getShoppingFoodCItem(),
						// 外食
						domain.getShoppingDineOutItem(),
						// 日用品
						domain.getShoppingConsumerGoodsItem(),
						// 衣料品(私服)
						domain.getShoppingClothesItem(),
						// 仕事
						domain.getShoppingWorkItem(),
						// 住居設備
						domain.getShoppingHouseEquipmentItem(),
						// クーポン金額
						domain.getShoppingCouponPrice(),
						// 買い物合計金額
						domain.getShoppingTotalAmount())
			).collect(Collectors.toUnmodifiableList()));
			
			// 月度の各種買い物項目の合計値を設定
			// 食料品(必須)合計
			response.setTotalShoppingFood(resultList.getTotalShoppingFoodItem().toFormatString());
			// 食料品B(無駄遣い)合計
			response.setTotalShoppingFoodB(resultList.getTotalShoppingFoodBItem().toFormatString());
			// 食料品C(お酒類)合計
			response.setTotalShoppingFoodC(resultList.getTotalShoppingFoodCItem().toFormatString());
			// 外食合計
			response.setTotalShoppingDineOut(resultList.getTotalShoppingDineOutItem().toFormatString());
			// 日用品合計
			response.setTotalShoppingConsumerGoods(resultList.getTotalShoppingConsumerGoodsItem().toFormatString());
			// 衣料品(私服)合計
			response.setTotalShoppingClothes(resultList.getTotalShoppingClothesItem().toFormatString());
			// 仕事合計
			response.setTotalShoppingWork(resultList.getTotalShoppingWorkItem().toFormatString());
			// 住居設備合計
			response.setTotalShoppingHouseEquipment(resultList.getTotalShoppingHouseEquipmentItem().toFormatString());
			// クーポン金額合計
			response.setTotalShoppingCouponPrice(resultList.getTotalShoppingCouponPrice().toFormatString());
			// 月度買い物合計金額
			response.setShoppingMonthTotalAmount(resultList.getShoppingMonthTotalAmount().toFormatString());
			
		}
	}
}
