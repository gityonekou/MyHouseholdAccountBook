/**
 * FixedCostTableRepository(固定費テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/04 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.fixedcost;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCost;
import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCostInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCostList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndFixedCostCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndFixedCostShiharaiTukiList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndSisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.repository.account.fixedcost.FixedCostTableRepository;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.fixedcost.FixedCostInquiryReadDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.fixedcost.FixedCostReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndFixedCostCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndFixedCostShiharaiTukiListSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndSisyutuItemCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.fixedcost.FixedCostTableMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * FixedCostTableRepository(固定費テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Repository
@RequiredArgsConstructor
public class FixedCostTableDataSource implements FixedCostTableRepository {
	
	// マッパー
	private final FixedCostTableMapper mapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(FixedCost data) {
		//  固定費テーブル:FIXED_COST_TABLEにデータを追加します。
		return mapper.insert(createFixedCostReadWriteDto(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(FixedCost data) {
		// 固定費テーブル:FIXED_COST_TABLEの情報を指定の固定費情報で更新します。
		return mapper.update(createFixedCostReadWriteDto(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int delete(FixedCost data) {
		// 固定費テーブル:FIXED_COST_TABLEの情報から指定した固定費の情報を論理削除します。
		return mapper.delete(createFixedCostReadWriteDto(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public FixedCost findByIdAndFixedCostCode(SearchQueryUserIdAndFixedCostCode search) {
		// 検索結果を取得
		FixedCostReadWriteDto searchResult = mapper.findByIdAndFixedCostCode(
				UserIdAndFixedCostCodeSearchQueryDto.from(search));
		if(searchResult == null) {
			// 検索結果なしの場合、nullを返却
			return null;
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return createFixedCost(searchResult);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public FixedCostInquiryList findById(SearchQueryUserId userId) {
		// 検索結果を取得
		List<FixedCostInquiryReadDto> searchResult = mapper.findById(
				UserIdSearchQueryDto.from(userId));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return FixedCostInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return FixedCostInquiryList.from(searchResult.stream().map(dto -> createFixedCostInquiryItem(dto))
						.collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public FixedCostInquiryList findByIdAndSisyutuItemCode(SearchQueryUserIdAndSisyutuItemCode search) {
		// 検索結果を取得
		List<FixedCostInquiryReadDto> searchResult = mapper.findByIdAndSisyutuItemCode(
				UserIdAndSisyutuItemCodeSearchQueryDto.from(search));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return FixedCostInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return FixedCostInquiryList.from(searchResult.stream().map(dto -> createFixedCostInquiryItem(dto))
						.collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public FixedCostList findByIdAndFixedCostShiharaiTukiList(SearchQueryUserIdAndFixedCostShiharaiTukiList search) {
		// 検索結果を取得
		List<FixedCostReadWriteDto> searchResult = mapper.findByIdAndFixedCostShiharaiTukiList(
				UserIdAndFixedCostShiharaiTukiListSearchQueryDto.from(search));
		
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return FixedCostList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return FixedCostList.from(searchResult.stream().map(dto -> createFixedCost(dto))
						.collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countById(SearchQueryUserId userId) {
		// ユーザIDで検索し、登録されている固定費情報の件数を返す
		return mapper.countById(UserIdSearchQueryDto.from(userId));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countBySisyutuItemCode(SearchQueryUserIdAndSisyutuItemCode search) {
		// ユーザID、支出項目コードで検索し、登録されている固定費情報の件数を返す
		return mapper.countBySisyutuItemCode(UserIdAndSisyutuItemCodeSearchQueryDto.from(search));
	}
	
	/**
	 *<pre>
	 * 引数で指定した固定費テーブル:FIXED_COST_TABLE読込・出力情報から固定費情報ドメインモデルを生成して返します。
	 *</pre>
	 * @param dto 固定費テーブル:FIXED_COST_TABLE読込・出力情報
	 * @return 固定費情報ドメインモデル
	 *
	 */
	private FixedCost createFixedCost(FixedCostReadWriteDto dto) {
		return FixedCost.from(
				// ユーザID
				dto.getUserId(),
				// 固定費コード
				dto.getFixedCostCode(),
				// 固定費名(支払名)
				dto.getFixedCostName(),
				// 固定費内容詳細(支払内容詳細)
				dto.getFixedCostDetailContext(),
				// 支出項目コード
				dto.getSisyutuItemCode(),
				// 固定費区分
				dto.getFixedCostKubun(),
				// 固定費支払月(支払月)
				dto.getFixedCostShiharaiTuki(),
				// 固定費支払月任意詳細
				dto.getFixedCostShiharaiTukiOptionalContext(),
				// 固定費支払日(支払日)
				dto.getFixedCostShiharaiDay(),
				// 支払金額
				dto.getShiharaiKingaku());
	}
	
	/**
	 *<pre>
	 * 引数で指定した固定費情報ドメインモデルからDTOを生成して返します。
	 *</pre>
	 * @param data 固定費情報ドメインモデル
	 * @return 固定費テーブル:FIXED_COST_TABLE読込・出力情報
	 *
	 */
	private FixedCostReadWriteDto createFixedCostReadWriteDto(FixedCost data) {
		return FixedCostReadWriteDto.from(
				// ユーザID
				data.getUserId().getValue(),
				// 固定費コード
				data.getFixedCostCode().getValue(),
				// 固定費名(支払名)
				data.getFixedCostName().getValue(),
				// 固定費内容詳細(支払内容詳細)
				data.getFixedCostDetailContext().getValue(),
				// 支出項目コード
				data.getSisyutuItemCode().getValue(),
				// 固定費区分
				data.getFixedCostKubun().getValue(),
				// 固定費支払月(支払月)
				data.getFixedCostShiharaiTuki().getValue(),
				// 固定費支払月任意詳細
				data.getFixedCostShiharaiTukiOptionalContext().getValue(),
				// 固定費支払日(支払日)
				data.getFixedCostShiharaiDay().getValue(),
				// 支払金額
				data.getShiharaiKingaku().getValue());
	}
	
	/**
	 *<pre>
	 * 引数で指定した固定費情報一覧の明細果情報から固定費一覧明細情報(ドメイン)を生成して返します。
	 *</pre>
	 * @param dto 固定費情報一覧の明細果情報
	 * @return 固定費一覧明細情報(ドメイン)
	 *
	 */
	private FixedCostInquiryList.FixedCostInquiryItem createFixedCostInquiryItem(FixedCostInquiryReadDto dto) {
		return FixedCostInquiryList.FixedCostInquiryItem.from(
				// 固定費コード
				dto.getFixedCostCode(),
				// 固定費名(支払名)
				dto.getFixedCostName(),
				// 固定費内容詳細(支払内容詳細)
				dto.getFixedCostDetailContext(),
				// 支出項目名
				dto.getSisyutuItemName(),
				// 固定費支払月(支払月)
				dto.getFixedCostShiharaiTuki(),
				// 固定費支払月任意詳細
				dto.getFixedCostShiharaiTukiOptionalContext(),
				// 固定費支払日(支払日)
				dto.getFixedCostShiharaiDay(),
				// 支払金額
				dto.getShiharaiKingaku());
	}
}
