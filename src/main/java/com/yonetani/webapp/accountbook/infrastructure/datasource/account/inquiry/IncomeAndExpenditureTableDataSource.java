/**
 * IncomeAndExpenditureTableRepository(収支テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/12 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.inquiry;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditureInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditureItem;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYear;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeAndExpenditureTableRepository;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.IncomeAndExpenditureReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry.IncomeAndExpenditureTableMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * IncomeAndExpenditureTableRepository(収支テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Repository
@RequiredArgsConstructor
public class IncomeAndExpenditureTableDataSource implements IncomeAndExpenditureTableRepository {

	// マッパー
	private final IncomeAndExpenditureTableMapper mapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(IncomeAndExpenditureItem data) {
		// 収支テーブル情報を収支テーブルに出力
		return mapper.insert(IncomeAndExpenditureReadWriteDto.from(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(IncomeAndExpenditureItem data) {
		// 収支テーブル:INCOME_AND_EXPENDITURE_TABLEを更新
		return mapper.update(IncomeAndExpenditureReadWriteDto.from(data));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IncomeAndExpenditureInquiryList select(SearchQueryUserIdAndYear searchQuery) {
		// 検索結果を取得
		List<IncomeAndExpenditureReadWriteDto> result = mapper.selectUserIdAndYear(
				UserIdAndYearSearchQueryDto.from(searchQuery));
		// 検索結果をドメインモデルに変換して返却
		return IncomeAndExpenditureInquiryList.from(result.stream().map(dto ->	 createIncomeAndExpenditureItem(dto))
					.collect(Collectors.toUnmodifiableList()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IncomeAndExpenditureItem select(SearchQueryUserIdAndYearMonth searchQuery) {
		// 検索結果を取得
		IncomeAndExpenditureReadWriteDto result = mapper.selectUserIdAndYearMonth(
				UserIdAndYearMonthSearchQueryDto.from(searchQuery));
		// 検索結果をドメインモデルに変換して返却
		if(result != null) {
			return createIncomeAndExpenditureItem(result);
		} else {
			return IncomeAndExpenditureItem.fromEmpty();
		}
	}

	/**
	 *<pre>
	 * 引数で指定した収支テーブル:INCOME_AND_EXPENDITURE_TABLEテーブルの各項目のDTOから収支テーブル情報ドメインモデルを生成して返します。
	 *</pre>
	 * @param dto 収支テーブル:INCOME_AND_EXPENDITURE_TABLEテーブルの各項目のDTO
	 * @return 収支テーブル情報ドメインモデル
	 *
	 */
	private IncomeAndExpenditureItem createIncomeAndExpenditureItem(IncomeAndExpenditureReadWriteDto dto) {
		return IncomeAndExpenditureItem.from(
				// ユーザID
				dto.getUserId(),
				// 対象年
				dto.getTargetYear(),
				// 対象月
				dto.getTargetMonth(),
				// 収入金額
				dto.getIncomeKingaku(),
				// 支出予定金額
				dto.getExpenditureEstimateKingaku(),
				// 支出金額
				dto.getExpenditureKingaku(),
				// 収支
				dto.getIncomeAndExpenditureKingaku());
	}
}
