/**
 * ShoppingItemTableRepository(商品テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.shoppingitem;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.domain.model.account.shoppingitem.ShoppingItem;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingitem.ShoppingItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryShoppingItemInfoSearchCondition;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShoppingItemCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShoppingItemJanCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.repository.account.shoppingitem.ShoppingItemTableRepository;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.shoppingitem.ShoppingItemInquiryReadDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.shoppingitem.ShoppingItemReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.ShoppingItemInfoSearchConditionSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndShoppingItemCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndShoppingItemJanCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndSisyutuItemCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.shoppingitem.ShoppingItemTableMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * ShoppingItemTableRepository(商品テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Repository
@RequiredArgsConstructor
public class ShoppingItemTableDataSource implements ShoppingItemTableRepository {

	// マッパー
	private final ShoppingItemTableMapper mapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(ShoppingItem data) {
		// 商品テーブル:SHOPPING_ITEM_TABLEにデータを追加します。
		return mapper.insert(ShoppingItemReadWriteDto.from(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(ShoppingItem data) {
		// 商品テーブル:SHOPPING_ITEM_TABLEの情報を指定の商品情報で更新します。
		return mapper.update(ShoppingItemReadWriteDto.from(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ShoppingItem findByIdAndShoppingItemCode(SearchQueryUserIdAndShoppingItemCode search) {
		// 検索結果を取得
		ShoppingItemReadWriteDto searchResult = mapper.findByIdAndShoppingItemCode(
				UserIdAndShoppingItemCodeSearchQueryDto.from(search));
		if(searchResult == null) {
			// 検索結果なしの場合、nullを返却
			return null;
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return createShoppingItem(searchResult);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ShoppingItemInquiryList findByIdAndSisyutuItemCode(SearchQueryUserIdAndSisyutuItemCode search) {
		// 検索結果を取得
		List<ShoppingItemInquiryReadDto> searchResult = mapper.findByIdAndSisyutuItemCode(
				UserIdAndSisyutuItemCodeSearchQueryDto.from(search));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return ShoppingItemInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return ShoppingItemInquiryList.from(searchResult.stream().map(dto -> createShoppingItemInquiryItem(dto))
						.collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ShoppingItemInquiryList findByIdAndShoppingItemJanCode(SearchQueryUserIdAndShoppingItemJanCode search) {
		// 検索結果を取得
		List<ShoppingItemInquiryReadDto> searchResult = mapper.findByIdAndShoppingItemJanCode(
				UserIdAndShoppingItemJanCodeSearchQueryDto.from(search));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return ShoppingItemInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return ShoppingItemInquiryList.from(searchResult.stream().map(dto -> createShoppingItemInquiryItem(dto))
						.collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ShoppingItemInquiryList selectShoppingItemInfoSearchCondition(
			SearchQueryShoppingItemInfoSearchCondition search) {
		// 検索結果を取得
		List<ShoppingItemInquiryReadDto> searchResult = mapper.selectShoppingItemInfoSearchCondition(
				ShoppingItemInfoSearchConditionSearchQueryDto.from(search));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return ShoppingItemInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return ShoppingItemInquiryList.from(searchResult.stream().map(dto -> createShoppingItemInquiryItem(dto))
						.collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countById(SearchQueryUserId userId) {
		// ユーザIDで検索し、登録されている商品の件数を返す
		return mapper.countById(UserIdSearchQueryDto.from(userId));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countByIdAndShoppingItemJanCode(SearchQueryUserIdAndShoppingItemJanCode search) {
		// ユーザID、商品JANコードで検索し、登録されている商品の件数を返す
		return mapper.countByIdAndShoppingItemJanCode(UserIdAndShoppingItemJanCodeSearchQueryDto.from(search));
	}
	
	/**
	 *<pre>
	 * 引数で指定した商品テーブル:SHOPPING_ITEM_TABLE読込・出力情報から商品テーブル情報ドメインモデルを生成して返します。
	 *</pre>
	 * @param dto 商品テーブル:SHOPPING_ITEM_TABLE読込・出力情報
	 * @return 商品テーブル情報ドメインモデル
	 *
	 */
	private ShoppingItem createShoppingItem(ShoppingItemReadWriteDto dto) {
		return ShoppingItem.from(
				// ユーザID
				dto.getUserId(),
				// 商品コード
				dto.getShoppingItemCode(),
				// 商品区分名
				dto.getShoppingItemKubunName(),
				// 商品名
				dto.getShoppingItemName(),
				// 商品詳細
				dto.getShoppingItemDetailContext(),
				// 商品JANコード
				dto.getShoppingItemJanCode(),
				// 支出項目コード
				dto.getSisyutuItemCode(),
				// 会社名
				dto.getCompanyName(),
				// 基準店舗コード
				dto.getStandardShopCode(),
				// 基準価格
				dto.getStandardPrice(),
				// 内容量
				dto.getCapacity(),
				// 内容量単位
				dto.getCapacityUnit(),
				// カロリー
				dto.getCalories()
				);
	}
	
	/**
	 *<pre>
	 * 引数で指定した商品検索結果情報から商品検索結果明細情報(ドメイン)を生成して返します。
	 *</pre>
	 * @param dto 商品検索結果情報
	 * @return 商品検索結果明細情報(ドメイン)
	 *
	 */
	private ShoppingItemInquiryList.ShoppingItemInquiryItem createShoppingItemInquiryItem(ShoppingItemInquiryReadDto dto) {
		return ShoppingItemInquiryList.ShoppingItemInquiryItem.from(
				// 商品コード
				dto.getShoppingItemCode(),
				// 商品区分名
				dto.getShoppingItemKubunName(),
				// 商品名
				dto.getShoppingItemName(),
				// 商品詳細
				dto.getShoppingItemDetailContext(),
				// 商品JANコード
				dto.getShoppingItemJanCode(),
				// 支出項目名
				dto.getSisyutuItemName(),
				// 会社名
				dto.getCompanyName(),
				// 基準店舗名
				dto.getStandardShopName(),
				// 基準価格
				dto.getStandardPrice(),
				// 内容量
				dto.getCapacity(),
				// 内容量単位
				dto.getCapacityUnit(),
				// カロリー
				dto.getCalories()
				);
	}
}
