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
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopSort;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndShopSortAB;
import com.yonetani.webapp.accountbook.domain.repository.account.shop.ShopTableRepository;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.shop.ShopReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndShopCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndShopSortABSearchQueryDto;
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
		return mapper.insert(ShopReadWriteDto.from(
				data.getUserId().toString(),
				data.getShopCode().toString(),
				data.getShopKubunCode().toString(),
				data.getShopName().toString(),
				data.getShopSort().toString()));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ShopInquiryList findById(SearchQueryUserId userId) {
		// 検索結果を取得
		List<ShopReadWriteDto> searchResult = mapper.findById(UserIdSearchQueryDto.from(userId.getUserId().toString()));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return ShopInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return ShopInquiryList.from(searchResult.stream().map(dto ->
				Shop.from(
						dto.getUserId(),
						dto.getShopCode(),
						dto.getShopKubunCode(),
						dto.getShopName(),
						dto.getShopSort())).collect(Collectors.toUnmodifiableList()));
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Shop findByIdAndShopCode(SearchQueryUserIdAndShopCode search) {
		// 指定条件で店舗情報を取得
		ShopReadWriteDto result = mapper.findByIdAndShopCode(UserIdAndShopCodeSearchQueryDto.from(
				search.getUserId().toString(), search.getShopCode().toString()));
		if(result == null) {
			// 対象データなしの場合、nullを返却
			return null;
		} else {
			// 検索結果をドメインに変換して返却
			return Shop.from(
					result.getUserId(),
					result.getShopCode(),
					result.getShopKubunCode(), 
					result.getShopName(), 
					result.getShopSort());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(Shop data) {
		// 店舗テーブル:SHOP_TABLEを更新
		return mapper.update(ShopReadWriteDto.from(
				data.getUserId().toString(),
				data.getShopCode().toString(),
				data.getShopKubunCode().toString(),
				data.getShopName().toString(),
				data.getShopSort().toString()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int updateShopSort(Shop data) {
		// 店舗テーブル:SHOP_TABLEの表示順の値を更新
		return mapper.updateShopSort(ShopReadWriteDto.from(
				data.getUserId().toString(),
				data.getShopCode().toString(),
				data.getShopKubunCode().toString(),
				data.getShopName().toString(),
				data.getShopSort().toString()));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countByIdAndLessThanNineHundred(SearchQueryUserId userId) {
		// ユーザIDで検索し、店舗コードが900より小さいものの件数を返す
		return mapper.countByIdAndLessThanNineHundred(UserIdSearchQueryDto.from(userId.getUserId().toString()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ShopInquiryList findByIdAndGreaterThanShopSort(SearchQueryUserIdAndShopSort search) {
		// 検索結果を取得
		List<ShopReadWriteDto> searchResult = mapper.findByIdAndGreaterThanShopSort(
				UserIdAndShopSortSearchQueryDto.from(search.getUserId().toString(), search.getShopSort().toString()));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return ShopInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return ShopInquiryList.from(searchResult.stream().map(dto ->
				Shop.from(
						dto.getUserId(),
						dto.getShopCode(),
						dto.getShopKubunCode(),
						dto.getShopName(),
						dto.getShopSort())).collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ShopInquiryList findByIdAndShopSortBetween(SearchQueryUserIdAndShopSortAB search) {
		// 検索結果を取得
		List<ShopReadWriteDto> searchResult = mapper.findByIdAndShopSortBetween(
				UserIdAndShopSortABSearchQueryDto.from(
						search.getUserId().toString(),
						search.getShopCodeA().toString(),
						search.getShopCodeB().toString()));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return ShopInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return ShopInquiryList.from(searchResult.stream().map(dto ->
				Shop.from(
						dto.getUserId(),
						dto.getShopCode(),
						dto.getShopKubunCode(),
						dto.getShopName(),
						dto.getShopSort())).collect(Collectors.toUnmodifiableList()));
		}
	}
}
