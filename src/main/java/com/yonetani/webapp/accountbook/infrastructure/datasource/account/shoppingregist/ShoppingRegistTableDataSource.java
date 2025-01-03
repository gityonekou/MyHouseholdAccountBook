/**
 * ShoppingRegistTableRepository(買い物登録情報テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/23 : 1.00.00  新規作成
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
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopCode;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopKubunCode;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopName;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingClothesExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingClothesItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingClothesTaxExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingConsumerGoodsExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingConsumerGoodsItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingConsumerGoodsTaxExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingCouponPrice;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDate;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDineOutExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDineOutItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingDineOutTaxExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodBExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodBItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodBTaxExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodCExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodCItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodCTaxExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingFoodTaxExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingHouseEquipmentExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingHouseEquipmentItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingHouseEquipmentTaxExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingRegistCode;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingRemarks;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingWorkExpenses;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingWorkItem;
import com.yonetani.webapp.accountbook.domain.type.account.shoppingregist.ShoppingWorkTaxExpenses;
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
 * @since 家計簿アプリ(1.00.A)
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
	public ShoppingRegist findByUniqueKey(SearchQueryUserIdAndYearMonthAndShoppingRegistCode search) {
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
	public SimpleShoppingRegistItemInquiryList findById(SearchQueryUserIdAndYearMonth search) {
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
	public int countById(SearchQueryUserIdAndYearMonth search) {
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
				// 買い物日
				ShoppingDate.from(dto.getShoppingDate(), targetYearMonth),
				// 備考
				ShoppingRemarks.from(dto.getShoppingRemarks()),
				// 食料品(必須)金額
				ShoppingFoodExpenses.from(dto.getShoppingFoodExpenses()),
				// 消費税:食料品(必須)金額
				ShoppingFoodTaxExpenses.from(dto.getShoppingFoodTaxExpenses()),
				// 食料品B(無駄遣い)金額
				ShoppingFoodBExpenses.from(dto.getShoppingFoodBExpenses()),
				// 消費税:食料品B(無駄遣い)金額
				ShoppingFoodBTaxExpenses.from(dto.getShoppingFoodBTaxExpenses()),
				// 食料品C(お酒類)金額
				ShoppingFoodCExpenses.from(dto.getShoppingFoodCExpenses()),
				// 消費税:食料品C(お酒類)金額
				ShoppingFoodCTaxExpenses.from(dto.getShoppingFoodCTaxExpenses()),
				// 外食金額
				ShoppingDineOutExpenses.from(dto.getShoppingDineOutExpenses()),
				// 消費税:外食金額
				ShoppingDineOutTaxExpenses.from(dto.getShoppingDineOutTaxExpenses()),
				// 日用品金額
				ShoppingConsumerGoodsExpenses.from(dto.getShoppingConsumerGoodsExpenses()),
				// 消費税:日用品金額
				ShoppingConsumerGoodsTaxExpenses.from(dto.getShoppingConsumerGoodsTaxExpenses()),
				// 衣料品(私服)金額
				ShoppingClothesExpenses.from(dto.getShoppingClothesExpenses()),
				// 消費税:衣料品(私服)金額
				ShoppingClothesTaxExpenses.from(dto.getShoppingClothesTaxExpenses()),
				// 仕事金額
				ShoppingWorkExpenses.from(dto.getShoppingWorkExpenses()),
				// 消費税:仕事金額
				ShoppingWorkTaxExpenses.from(dto.getShoppingWorkTaxExpenses()),
				// 住居設備金額
				ShoppingHouseEquipmentExpenses.from(dto.getShoppingHouseEquipmentExpenses()),
				// 消費税:住居設備金額
				ShoppingHouseEquipmentTaxExpenses.from(dto.getShoppingHouseEquipmentTaxExpenses()),
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
				// 買い物日
				ShoppingDate.from(dto.getShoppingDate(), targetYearMonth),
				// 食料品(必須)
				ShoppingFoodItem.from(
						// 食料品(必須)金額
						ShoppingFoodExpenses.from(dto.getShoppingFoodExpenses()),
						// 消費税:食料品(必須)金額
						ShoppingFoodTaxExpenses.from(dto.getShoppingFoodTaxExpenses())),
				// 食料品B(無駄遣い)
				ShoppingFoodBItem.from(
						// 食料品B(無駄遣い)金額
						ShoppingFoodBExpenses.from(dto.getShoppingFoodBExpenses()),
						// 消費税:食料品B(無駄遣い)金額
						ShoppingFoodBTaxExpenses.from(dto.getShoppingFoodBTaxExpenses())),
				// 食料品C(お酒類)
				ShoppingFoodCItem.from(
						// 食料品C(お酒類)金額
						ShoppingFoodCExpenses.from(dto.getShoppingFoodCExpenses()),
						// 消費税:食料品C(お酒類)金額
						ShoppingFoodCTaxExpenses.from(dto.getShoppingFoodCTaxExpenses())),
				// 外食
				ShoppingDineOutItem.from(
						// 外食金額
						ShoppingDineOutExpenses.from(dto.getShoppingDineOutExpenses()),
						// 消費税:外食金額
						ShoppingDineOutTaxExpenses.from(dto.getShoppingDineOutTaxExpenses())),
				// 日用品
				ShoppingConsumerGoodsItem.from(
						// 日用品金額
						ShoppingConsumerGoodsExpenses.from(dto.getShoppingConsumerGoodsExpenses()),
						// 消費税:日用品金額
						ShoppingConsumerGoodsTaxExpenses.from(dto.getShoppingConsumerGoodsTaxExpenses())),
				// 衣料品(私服)
				ShoppingClothesItem.from(
						// 衣料品(私服)金額
						ShoppingClothesExpenses.from(dto.getShoppingClothesExpenses()),
						// 消費税:衣料品(私服)金額
						ShoppingClothesTaxExpenses.from(dto.getShoppingClothesTaxExpenses())),
				// 仕事
				ShoppingWorkItem.from(
						// 仕事金額
						ShoppingWorkExpenses.from(dto.getShoppingWorkExpenses()),
						// 消費税:仕事金額
						ShoppingWorkTaxExpenses.from(dto.getShoppingWorkTaxExpenses())),
				// 住居設備
				ShoppingHouseEquipmentItem.from(
						// 仕事金額
						ShoppingHouseEquipmentExpenses.from(dto.getShoppingHouseEquipmentExpenses()),
						// 消費税:仕事金額
						ShoppingHouseEquipmentTaxExpenses.from(dto.getShoppingHouseEquipmentTaxExpenses())),
				// クーポン金額
				ShoppingCouponPrice.from(dto.getShoppingCouponPrice()),
				// 買い物合計金額
				ShoppingTotalAmount.from(dto.getShoppingTotalAmount()));
	}
}
