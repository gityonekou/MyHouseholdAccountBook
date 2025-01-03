/**
 * 簡易タイプ買い物リスト取得コンポーネント
 * 簡易タイプ買い物リスト(excel家計簿の日々の買い物リストの項目)の情報を取得しAbstractSimpleShoppingRegistListResponseに設定するコンポーネントです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/12/28 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.account.regist;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.SimpleShoppingRegistItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.repository.account.shoppingregist.ShoppingRegistTableRepository;
import com.yonetani.webapp.accountbook.domain.utils.DomainCommonUtils;
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
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class SimpleShoppingRegistListComponent {
	
	// 買い物登録情報リポジトリー
	private final ShoppingRegistTableRepository shoppingRegistRepository;
	
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
		SimpleShoppingRegistItemInquiryList resultList = shoppingRegistRepository.findById(search);
		if(resultList.isEmpty()) {
			// 登録済み買い物情報が0件の場合、メッセージを設定
			response.addMessage("登録済みの買い物情報は0件です。");
		} else {
			response.addShoppingRegistListItemInfo(resultList.getValues().stream().map(domain ->
				SimpleShoppingRegistListItem.from(
						// 対象年月
						domain.getTargetYearMonth().getValue(),
						// 買い物登録コード
						domain.getShoppingRegistCode().getValue(),
						// 買い物日(DD:日付の値)
						String.format("%02d", domain.getShoppingDate().getValue().getDayOfMonth()),
						// 店舗名
						domain.getShopName().getValue(),
						// 食料品(必須)
						DomainCommonUtils.formatKingakuAndYen(domain.getShoppingFoodItem().getValue()),
						// 食料品B(無駄遣い)
						DomainCommonUtils.formatKingakuAndYen(domain.getShoppingFoodBItem().getValue()),
						// 食料品C(お酒類)
						DomainCommonUtils.formatKingakuAndYen(domain.getShoppingFoodCItem().getValue()),
						// 外食
						DomainCommonUtils.formatKingakuAndYen(domain.getShoppingDineOutItem().getValue()),
						// 日用品
						DomainCommonUtils.formatKingakuAndYen(domain.getShoppingConsumerGoodsItem().getValue()),
						// 衣料品(私服)
						DomainCommonUtils.formatKingakuAndYen(domain.getShoppingClothesItem().getValue()),
						// 仕事
						DomainCommonUtils.formatKingakuAndYen(domain.getShoppingWorkItem().getValue()),
						// 住居設備
						DomainCommonUtils.formatKingakuAndYen(domain.getShoppingHouseEquipmentItem().getValue()),
						// クーポン金額
						DomainCommonUtils.formatKingakuAndYen(domain.getShoppingCouponPrice().getValue()),
						// 買い物合計金額
						DomainCommonUtils.formatKingakuAndYen(domain.getShoppingTotalAmount().getValue())
						)
			).collect(Collectors.toUnmodifiableList()));
			
			// 月度の各種買い物項目の合計値を設定
			// 食料品(必須)合計
			response.setTotalShoppingFood(DomainCommonUtils.formatKingakuAndYen(resultList.getTotalShoppingFoodItem().getValue()));
			// 食料品B(無駄遣い)合計
			response.setTotalShoppingFoodB(DomainCommonUtils.formatKingakuAndYen(resultList.getTotalShoppingFoodBItem().getValue()));
			// 食料品C(お酒類)合計
			response.setTotalShoppingFoodC(DomainCommonUtils.formatKingakuAndYen(resultList.getTotalShoppingFoodCItem().getValue()));
			// 外食合計
			response.setTotalShoppingDineOut(DomainCommonUtils.formatKingakuAndYen(resultList.getTotalShoppingDineOutItem().getValue()));
			// 日用品合計
			response.setTotalShoppingConsumerGoods(DomainCommonUtils.formatKingakuAndYen(resultList.getTotalShoppingConsumerGoodsItem().getValue()));
			// 衣料品(私服)合計
			response.setTotalShoppingClothes(DomainCommonUtils.formatKingakuAndYen(resultList.getTotalShoppingClothesItem().getValue()));
			// 仕事合計
			response.setTotalShoppingWork(DomainCommonUtils.formatKingakuAndYen(resultList.getTotalShoppingWorkItem().getValue()));
			// 住居設備合計
			response.setTotalShoppingHouseEquipment(DomainCommonUtils.formatKingakuAndYen(resultList.getTotalShoppingHouseEquipmentItem().getValue()));
			// クーポン金額合計
			response.setTotalShoppingCouponPrice(DomainCommonUtils.formatKingakuAndYen(resultList.getTotalShoppingCouponPrice().getValue()));
			// 月度買い物合計金額
			response.setShoppingMonthTotalAmount(DomainCommonUtils.formatKingakuAndYen(resultList.getShoppingMonthTotalAmount().getValue()));
			
		}
	}
}
