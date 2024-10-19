/**
 * SisyutuItemTableRepository(支出項目テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/10 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.inquiry;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemSortBetweenAB;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.SisyutuItemTableRepository;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.SisyutuItemReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndSisyutuItemCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndSisyutuItemSortBetweenABSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry.SisyutuItemTableMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * SisyutuItemTableRepository(支出項目テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Repository
@RequiredArgsConstructor
public class SisyutuItemTableDataSource implements SisyutuItemTableRepository {

	// マッパー
	private final SisyutuItemTableMapper mapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(SisyutuItem data) {
		// 支出項目テーブル:SISYUTU_ITEM_TABLEにデータを追加します。
		return mapper.insert(SisyutuItemReadWriteDto.from(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(SisyutuItem data) {
		// 支出項目テーブル:SISYUTU_ITEM_TABLEの情報を指定の支出項目情報で更新します。
		return mapper.update(SisyutuItemReadWriteDto.from(data));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int updateSisyutuItemDetailContext(SisyutuItem data) {
		// 支出項目テーブル:SISYUTU_ITEM_TABLEの支出項目詳細内容項目の値を更新します。
		return mapper.updateSisyutuItemDetailContext(SisyutuItemReadWriteDto.from(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int updateSisyutuItemSort(SisyutuItem data) {
		// 支出項目テーブル:SISYUTU_ITEM_TABLEの支出項目表示順項目の値を更新します。
		return mapper.updateSisyutuItemSort(SisyutuItemReadWriteDto.from(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public SisyutuItemInquiryList findById(SearchQueryUserId userId) {
		// 検索結果を取得
		List<SisyutuItemReadWriteDto> searchResult = mapper.findById(UserIdSearchQueryDto.from(userId));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return SisyutuItemInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return SisyutuItemInquiryList.from(searchResult.stream().map(dto -> createSisyutuItem(dto))
						.collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public SisyutuItemInquiryList findByIdAndSisyutuItemSortBetween(SearchQueryUserIdAndSisyutuItemSortBetweenAB search) {
		// 検索結果を取得
		List<SisyutuItemReadWriteDto> searchResult = mapper.findByIdAndSisyutuItemSortBetween(
				UserIdAndSisyutuItemSortBetweenABSearchQueryDto.from(search));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return SisyutuItemInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return SisyutuItemInquiryList.from(searchResult.stream().map(dto -> createSisyutuItem(dto))
						.collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public SisyutuItem findByIdAndSisyutuItemCode(SearchQueryUserIdAndSisyutuItemCode search) {
		// 検索結果を取得
		SisyutuItemReadWriteDto searchResult = mapper.findByIdAndSisyutuItemCode(UserIdAndSisyutuItemCodeSearchQueryDto.from(search));
		if(searchResult == null) {
			// 検索結果なしの場合、nullを返却
			return null;
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return createSisyutuItem(searchResult);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SisyutuItemInquiryList searchParentMemberSisyutuItemList(SearchQueryUserIdAndSisyutuItemCode search) {
		// 検索結果を取得
		List<SisyutuItemReadWriteDto> searchResult = mapper.searchParentSisyutuItemMemberNameList(
				UserIdAndSisyutuItemCodeSearchQueryDto.from(search));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return SisyutuItemInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return SisyutuItemInquiryList.from(searchResult.stream().map(dto -> createSisyutuItem(dto))
				.collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countById(SearchQueryUserId userId) {
		// ユーザIDで検索し、登録されている支出項目の件数を返す
		return mapper.countById(UserIdSearchQueryDto.from(userId));
	}
	
	/**
	 *<pre>
	 * 引数で指定した支出項目テーブル:SISYUTU_ITEM_TABLE読込・出力情報から支出項目テーブル情報ドメインモデルを生成して返します。
	 *</pre>
	 * @param dto 支出項目テーブル:SISYUTU_ITEM_TABLE読込・出力情報
	 * @return 支出項目テーブル情報ドメインモデル
	 *
	 */
	private SisyutuItem createSisyutuItem(SisyutuItemReadWriteDto dto) {
		return SisyutuItem.from(
				// ユーザID
				dto.getUserId(),
				// 支出項目コード
				dto.getSisyutuItemCode(),
				// 支出項目名
				dto.getSisyutuItemName(),
				// 支出項目詳細内容
				dto.getSisyutuItemDetailContext(),
				// 親支出項目コード
				dto.getParentSisyutuItemCode(),
				// 支出項目レベル(1～5)
				dto.getSisyutuItemLevel(),
				// 支出項目表示順
				dto.getSisyutuItemSort(),
				// 更新可否フラグ
				dto.isEnableUpdateFlg());
	}
}
