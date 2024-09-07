/**
 * IncomeAndExpenditureInquiryRepository(収支情報取得)を実装したデータソースです。
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

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditureInquiryItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditureInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYear;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeAndExpenditureInquiryRepository;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.IncomeAndExpenditureReadDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry.IncomeAndExpenditureInquiryMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * IncomeAndExpenditureInquiryRepository(収支情報取得)を実装したデータソースです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Repository
@RequiredArgsConstructor
public class IncomeAndExpenditureInquiryDataSource implements IncomeAndExpenditureInquiryRepository {

	// マッパー
	private final IncomeAndExpenditureInquiryMapper mapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IncomeAndExpenditureInquiryList select(SearchQueryUserIdAndYear searchQuery) {
		// 検索結果を取得
		List<IncomeAndExpenditureReadDto> result = mapper.selectUserIdAndYear(UserIdAndYearSearchQueryDto.from(
				searchQuery.getUserId().toString(),
				searchQuery.getYear().toString()));
		// 検索結果をドメインモデルに変換して返却
		return IncomeAndExpenditureInquiryList.from(result.stream().map(dto ->	 createIncomeAndExpenditureInquiryItem(dto))
					.collect(Collectors.toUnmodifiableList()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IncomeAndExpenditureInquiryItem select(SearchQueryUserIdAndYearMonth searchQuery) {
		// 検索結果を取得
		IncomeAndExpenditureReadDto result = mapper.selectUserIdAndYearMonth(UserIdAndYearMonthSearchQueryDto.from(
				searchQuery.getUserId().toString(),
				searchQuery.getYearMonth().getYear(),
				searchQuery.getYearMonth().getMonth()
				));
		// 検索結果をドメインモデルに変換して返却
		if(result != null) {
			return createIncomeAndExpenditureInquiryItem(result);
		} else {
			return IncomeAndExpenditureInquiryItem.fromEmpty();
		}
	}

	/**
	 *<pre>
	 * 引数で指定した収支テーブル:INCOME_AND_EXPENDITURE_TABLEテーブルの各項目のDTOから収支情報ドメインモデルを生成して返します。
	 *</pre>
	 * @param dto 収支テーブル:INCOME_AND_EXPENDITURE_TABLEテーブルの各項目のDTO
	 * @return 収支情報ドメインモデル
	 *
	 */
	private IncomeAndExpenditureInquiryItem createIncomeAndExpenditureInquiryItem(IncomeAndExpenditureReadDto dto) {
		return IncomeAndExpenditureInquiryItem.from(
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
