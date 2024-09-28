/**
 * ShopTableRepository(店舗テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/10 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.shop;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.domain.model.account.shop.Shop;
import com.yonetani.webapp.accountbook.domain.model.account.shop.ShopInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopKubunCodeList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopSort;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopSortBetweenAB;
import com.yonetani.webapp.accountbook.domain.repository.account.shop.ShopTableRepository;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.shop.ShopReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndShopCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndShopKubunCodeListSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndShopSortBetweenABSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndShopSortSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.shop.ShopTableMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * ShopTableRepository(店舗テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Repository
@RequiredArgsConstructor
public class ShopTableDataSource implements ShopTableRepository {

	// マッパー
	private final ShopTableMapper mapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(Shop data) {
		// 店舗情報を店舗テーブルに出力
		return mapper.insert(createShopReadWriteDto(data));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(Shop data) {
		// 店舗テーブル:SHOP_TABLEを更新
		return mapper.update(createShopReadWriteDto(data));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int updateShopSort(Shop data) {
		// 店舗テーブル:SHOP_TABLEの表示順の値を更新
		return mapper.updateShopSort(createShopReadWriteDto(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ShopInquiryList findById(SearchQueryUserId userId) {
		// 検索結果を取得
		List<ShopReadWriteDto> searchResult = mapper.findById(UserIdSearchQueryDto.from(userId.getUserId().getValue()));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return ShopInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return ShopInquiryList.from(searchResult.stream().map(dto -> createShop(dto)).collect(Collectors.toUnmodifiableList()));
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Shop findByIdAndShopCode(SearchQueryUserIdAndShopCode search) {
		// 指定条件で店舗情報を取得
		ShopReadWriteDto result = mapper.findByIdAndShopCode(UserIdAndShopCodeSearchQueryDto.from(
				search.getUserId().getValue(), search.getShopCode().getValue()));
		if(result == null) {
			// 対象データなしの場合、nullを返却
			return null;
		} else {
			// 検索結果をドメインに変換して返却
			return createShop(result);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countByIdAndLessThanNineHundred(SearchQueryUserId userId) {
		// ユーザIDで検索し、店舗コードが900より小さいものの件数を返す
		return mapper.countByIdAndLessThanNineHundred(UserIdSearchQueryDto.from(userId.getUserId().getValue()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ShopInquiryList findByIdAndShopSort(SearchQueryUserIdAndShopSort search) {
		// 検索結果を取得
		List<ShopReadWriteDto> searchResult = mapper.findByIdAndShopSort(
				UserIdAndShopSortSearchQueryDto.from(search.getUserId().getValue(), search.getShopSort().getValue()));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return ShopInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return ShopInquiryList.from(searchResult.stream().map(dto -> createShop(dto)).collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ShopInquiryList findByIdAndShopSortBetween(SearchQueryUserIdAndShopSortBetweenAB search) {
		// 検索結果を取得
		List<ShopReadWriteDto> searchResult = mapper.findByIdAndShopSortBetween(
				UserIdAndShopSortBetweenABSearchQueryDto.from(
						search.getUserId().getValue(),
						search.getShopSortA().getValue(),
						search.getShopSortB().getValue()));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return ShopInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return ShopInquiryList.from(searchResult.stream().map(dto -> createShop(dto)).collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ShopInquiryList findByIdAndShopKubunCodeList(SearchQueryUserIdAndShopKubunCodeList search) {
		// 検索結果を取得
		List<ShopReadWriteDto> searchResult = mapper.findByIdAndShopKubunCodeList(
				UserIdAndShopKubunCodeListSearchQueryDto.from(
						search.getUserId().getValue(),
						search.getShopKubunCodeList().stream().map(model -> model.getValue()).collect(Collectors.toUnmodifiableList())));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return ShopInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return ShopInquiryList.from(searchResult.stream().map(dto -> createShop(dto)).collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 *<pre>
	 * 引数で指定した店舗テーブル:SHOP_TABLE読込・出力情報から店舗テーブル情報ドメインモデルを生成して返します。
	 *</pre>
	 * @param dto 店舗テーブル:SHOP_TABLE読込・出力情報
	 * @return 店舗テーブル情報ドメインモデル
	 *
	 */
	private Shop createShop(ShopReadWriteDto dto) {
		return Shop.from(
				// ユーザID
				dto.getUserId(),
				// 店舗コード
				dto.getShopCode(),
				// 店舗区分コード
				dto.getShopKubunCode(),
				// 店舗名
				dto.getShopName(),
				// 店舗表示順
				dto.getShopSort());
	}
	
	/**
	 *<pre>
	 * 引数で指定した店舗テーブル情報ドメインモデルからDTOを生成して返します。
	 *</pre>
	 * @param data　店舗テーブル情報ドメインモデル
	 * @return 店舗テーブル:SHOP_TABLE読込・出力情報
	 *
	 */
	private ShopReadWriteDto createShopReadWriteDto(Shop data) {
		return ShopReadWriteDto.from(
				// ユーザID
				data.getUserId().getValue(),
				// 店舗コード
				data.getShopCode().getValue(),
				// 店舗区分コード
				data.getShopKubunCode().getValue(),
				// 店舗名
				data.getShopName().getValue(),
				// 店舗表示順
				data.getShopSort().getValue());
	}
}
