/**
 * ShoppingRegistTableRepository(買い物登録情報テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/11/23 : 1.00.00                      新規作成
 * 2026/03/20 : 1.01.00  feature-1.00-dev00  リファクタリング対応(DDD適応)
 * 2026/08/18 : 1.02.00  feature-1.03-dev1   支払方法・銀行口座管理追加対応、買い物登録ドメインタイプリファクタリング対応
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.shoppingregist;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.ShoppingRegist;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.SimpleShoppingRegistItem;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.SimpleShoppingRegistItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonthAndShoppingRegistCode;
import com.yonetani.webapp.accountbook.domain.repository.account.shoppingregist.ShoppingRegistTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopCode;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopKubunCode;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopName;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingClothesExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingConsumerGoodsExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingCouponPrice;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDate;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDineOutExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodMinorWasteExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodSevereWasteExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingHouseEquipmentExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingRegistCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingRemarks;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingTaxExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingWorkExpenditureItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.TaxTotalPurchasePrice;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.TotalPurchasePrice;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.shoppingregist.ShoppingRegistReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.shoppingregist.SimpleShoppingRegistItemReadDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthAndShoppingRegistCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.shoppingregist.ShoppingRegistTableMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * ShoppingRegistTableRepository(買い物登録情報テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@Repository
@RequiredArgsConstructor
public class ShoppingRegistTableDataSource implements ShoppingRegistTableRepository {
	
	// マッパー
	private final ShoppingRegistTableMapper mapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(ShoppingRegist data) {
		// 買い物登録情報を買い物登録情報テーブルに出力
		return mapper.insert(ShoppingRegistReadWriteDto.from(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(ShoppingRegist data) {
		// 買い物登録情報テーブル:SHOPPING_REGIST_TABLEを更新
		return mapper.update(ShoppingRegistReadWriteDto.from(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ShoppingRegist findByPrimaryKey(SearchQueryUserIdAndYearMonthAndShoppingRegistCode search) {
		// 検索結果を取得
		ShoppingRegistReadWriteDto searchResult = mapper.findByUniqueKey(
				UserIdAndYearMonthAndShoppingRegistCodeSearchQueryDto.from(search));
		if(searchResult == null) {
			// 検索結果なしの場合、nullを返却
			return null;
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return createShoppingRegist(searchResult);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public SimpleShoppingRegistItemInquiryList findBy(SearchQueryUserIdAndYearMonth search) {
		// 検索結果を取得
		List<SimpleShoppingRegistItemReadDto> searchResult = mapper.findById(UserIdAndYearMonthSearchQueryDto.from(search));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return SimpleShoppingRegistItemInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return SimpleShoppingRegistItemInquiryList.from(searchResult.stream().map(dto -> createSimpleShoppingRegistItem(dto))
					.collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countBy(SearchQueryUserIdAndYearMonth search) {
		// 検索条件に一致する買い物登録情報の件数を返します。
		return mapper.countById(UserIdAndYearMonthSearchQueryDto.from(search));
	}
	
	/**
	 *<pre>
	 * 引数で指定した買い物登録情報テーブル:SHOPPING_REGIST_TABLE読込・出力情報から買い物登録情報ドメインモデルを生成して返します。
	 *</pre>
	 * @param dto 買い物登録情報テーブル:SHOPPING_REGIST_TABLE読込・出力情報
	 * @return 買い物登録情報ドメインモデル
	 *
	 */
	private ShoppingRegist createShoppingRegist(ShoppingRegistReadWriteDto dto) {
		// 対象年月(YYYYMM)のドメインを生成
		TargetYearMonth targetYearMonth = TargetYearMonth.from(dto.getTargetYear() + dto.getTargetMonth());
		return ShoppingRegist.from(
				// ユーザID
				UserId.from(dto.getUserId()),
				// 対象年月(YYYYMM)
				targetYearMonth,
				// 買い物登録コード
				ShoppingRegistCode.from(dto.getShoppingRegistCode()),
				// 店舗区分コード
				ShopKubunCode.from(dto.getShopKubunCode()),
				// 店舗コード
				ShopCode.from(dto.getShopCode()),
				// 支払方法コード
				PaymentMethodCode.from(dto.getPaymentMethodCode()),
				// 買い物日
				ShoppingDate.from(dto.getShoppingDate(), targetYearMonth),
				// 備考
				ShoppingRemarks.from(dto.getShoppingRemarks()),
				// 食料品(必須)
				ShoppingFoodExpenditureItem.from(
						// 食料品(必須)購入金額
						ShoppingExpenditureAmount.from(dto.getShoppingFoodExpenses()),
						// 食料品(必須)消費税
						ShoppingTaxExpenses.from(dto.getShoppingFoodTaxExpenses())),
				// 食料品B(無駄遣い)
				ShoppingFoodMinorWasteExpenditureItem.from(
						// 食料品B(無駄遣い)購入金額
						ShoppingExpenditureAmount.from(dto.getShoppingFoodBExpenses()),
						// 食料品B(無駄遣い)消費税
						ShoppingTaxExpenses.from(dto.getShoppingFoodBTaxExpenses())),
				// 食料品C(お酒類)
				ShoppingFoodSevereWasteExpenditureItem.from(
						// 食料品C(お酒類)購入金額
						ShoppingExpenditureAmount.from(dto.getShoppingFoodCExpenses()),
						// 食料品BC(お酒類)消費税
						ShoppingTaxExpenses.from(dto.getShoppingFoodCTaxExpenses())),
				// 外食
				ShoppingDineOutExpenditureItem.from(
						// 外食購入金額
						ShoppingExpenditureAmount.from(dto.getShoppingDineOutExpenses()),
						// 外食消費税
						ShoppingTaxExpenses.from(dto.getShoppingDineOutTaxExpenses())),
				// 日用品
				ShoppingConsumerGoodsExpenditureItem.from(
						// 日用品購入金額
						ShoppingExpenditureAmount.from(dto.getShoppingConsumerGoodsExpenses()),
						// 日用品消費税
						ShoppingTaxExpenses.from(dto.getShoppingConsumerGoodsTaxExpenses())),
				// 衣料品(私服)
				ShoppingClothesExpenditureItem.from(
						// 衣料品(私服)購入金額
						ShoppingExpenditureAmount.from(dto.getShoppingClothesExpenses()),
						// 衣料品(私服)消費税
						ShoppingTaxExpenses.from(dto.getShoppingClothesTaxExpenses())),
				// 仕事
				ShoppingWorkExpenditureItem.from(
						// 仕事購入金額
						ShoppingExpenditureAmount.from(dto.getShoppingWorkExpenses()),
						// 仕事消費税
						ShoppingTaxExpenses.from(dto.getShoppingWorkTaxExpenses())),
				// 住居設備
				ShoppingHouseEquipmentExpenditureItem.from(
						// 住居設備購入金額
						ShoppingExpenditureAmount.from(dto.getShoppingHouseEquipmentExpenses()),
						// 住居設備消費税
						ShoppingTaxExpenses.from(dto.getShoppingHouseEquipmentTaxExpenses())),
				// クーポン金額
				ShoppingCouponPrice.from(dto.getShoppingCouponPrice()),
				// 購入金額合計
				TotalPurchasePrice.from(dto.getTotalPurchasePrice()),
				// 消費税合計
				TaxTotalPurchasePrice.from(dto.getTaxTotalPurchasePrice()),
				// 買い物合計金額
				ShoppingTotalAmount.from(dto.getShoppingTotalAmount()));
	}
	
	/**
	 *<pre>
	 * 引数で指定した簡易タイプ買い物登録画面に表示する買い物一覧情報のDB読込情報から簡易タイプ買い物登録情報ドメインモデルを生成して返します。
	 *</pre>
	 * @param dto 簡易タイプ買い物登録画面に表示する買い物一覧情報のDB読込情報
	 * @return 簡易タイプ買い物登録情報ドメインモデル
	 *
	 */
	private SimpleShoppingRegistItem createSimpleShoppingRegistItem(SimpleShoppingRegistItemReadDto dto) {
		// 対象年月(YYYYMM)のドメインを生成
		TargetYearMonth targetYearMonth = TargetYearMonth.from(dto.getTargetYear() + dto.getTargetMonth());
		return SimpleShoppingRegistItem.from(
				// 対象年月(YYYYMM)
				targetYearMonth,
				// 買い物登録コード
				ShoppingRegistCode.from(dto.getShoppingRegistCode()),
				// 店舗名
				ShopName.from(dto.getShopName()),
				// 支払方法コード
				PaymentMethodCode.from(dto.getPaymentMethodCode()),
				// 買い物日
				ShoppingDate.from(dto.getShoppingDate(), targetYearMonth),
				// 食料品(必須)
				ShoppingFoodExpenditureItem.from(
						// 食料品(必須)購入金額
						ShoppingExpenditureAmount.from(dto.getShoppingFoodExpenses()),
						// 食料品(必須)消費税
						ShoppingTaxExpenses.from(dto.getShoppingFoodTaxExpenses())),
				// 食料品B(無駄遣い)
				ShoppingFoodMinorWasteExpenditureItem.from(
						// 食料品B(無駄遣い)購入金額
						ShoppingExpenditureAmount.from(dto.getShoppingFoodBExpenses()),
						// 食料品B(無駄遣い)消費税
						ShoppingTaxExpenses.from(dto.getShoppingFoodBTaxExpenses())),
				// 食料品C(お酒類)
				ShoppingFoodSevereWasteExpenditureItem.from(
						// 食料品C(お酒類)購入金額
						ShoppingExpenditureAmount.from(dto.getShoppingFoodCExpenses()),
						// 食料品C(お酒類)消費税
						ShoppingTaxExpenses.from(dto.getShoppingFoodCTaxExpenses())),
				// 外食
				ShoppingDineOutExpenditureItem.from(
						// 外食購入金額
						ShoppingExpenditureAmount.from(dto.getShoppingDineOutExpenses()),
						// 外食消費税
						ShoppingTaxExpenses.from(dto.getShoppingDineOutTaxExpenses())),
				// 日用品
				ShoppingConsumerGoodsExpenditureItem.from(
						// 日用品購入金額
						ShoppingExpenditureAmount.from(dto.getShoppingConsumerGoodsExpenses()),
						// 日用品消費税
						ShoppingTaxExpenses.from(dto.getShoppingConsumerGoodsTaxExpenses())),
				// 衣料品(私服)
				ShoppingClothesExpenditureItem.from(
						// 衣料品(私服)購入金額
						ShoppingExpenditureAmount.from(dto.getShoppingClothesExpenses()),
						// 衣料品(私服)消費税
						ShoppingTaxExpenses.from(dto.getShoppingClothesTaxExpenses())),
				// 仕事
				ShoppingWorkExpenditureItem.from(
						// 仕事購入金額
						ShoppingExpenditureAmount.from(dto.getShoppingWorkExpenses()),
						// 仕事消費税
						ShoppingTaxExpenses.from(dto.getShoppingWorkTaxExpenses())),
				// 住居設備
				ShoppingHouseEquipmentExpenditureItem.from(
						// 住居設備購入金額
						ShoppingExpenditureAmount.from(dto.getShoppingHouseEquipmentExpenses()),
						// 住居設備金消費税
						ShoppingTaxExpenses.from(dto.getShoppingHouseEquipmentTaxExpenses())),
				// クーポン金額
				ShoppingCouponPrice.from(dto.getShoppingCouponPrice()),
				// 買い物合計金額
				ShoppingTotalAmount.from(dto.getShoppingTotalAmount()));
	}
}
