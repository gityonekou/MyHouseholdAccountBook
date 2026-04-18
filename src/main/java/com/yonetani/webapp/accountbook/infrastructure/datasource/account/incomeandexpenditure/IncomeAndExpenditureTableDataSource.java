/**
 * IncomeAndExpenditureTableRepository(収支テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/12 : 1.00.00  新規作成
 * 2026/03/20 : 1.01.00  リファクタリング対応(DDD適応)
 * 2026/04/16 : 1.02.00  IncomeAndExpenditureItem統合に伴う修正
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.incomeandexpenditure;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.domain.model.account.incomeandexpenditure.IncomeAndExpenditure;
import com.yonetani.webapp.accountbook.domain.model.account.incomeandexpenditure.IncomeAndExpenditureInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYear;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.repository.account.incomeandexpenditure.IncomeAndExpenditureTableRepository;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.incomeandexpenditure.IncomeAndExpenditureReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.incomeandexpenditure.IncomeAndExpenditureTableMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * IncomeAndExpenditureTableRepository(収支テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
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
	public int add(IncomeAndExpenditure data) {
		// 収支テーブル情報を収支テーブルに出力
		return mapper.insert(IncomeAndExpenditureReadWriteDto.from(data));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(IncomeAndExpenditure data) {
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
		return IncomeAndExpenditureInquiryList.from(result.stream()
				.map(dto -> createIncomeAndExpenditure(dto))
				.collect(Collectors.toUnmodifiableList()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IncomeAndExpenditure findByPrimaryKey(SearchQueryUserIdAndYearMonth searchQuery) {
		// 検索結果を取得
		IncomeAndExpenditureReadWriteDto result = mapper.selectUserIdAndYearMonth(
				UserIdAndYearMonthSearchQueryDto.from(searchQuery));

		// 検索結果をドメインモデルに変換して返却
		if(result != null) {
			return createIncomeAndExpenditure(result);
		} else {
			// データなしの場合は空の集約を返す
			return IncomeAndExpenditure.empty(searchQuery.getUserId(), searchQuery.getYearMonth());
		}
	}

	/**
	 *<pre>
	 * 収支テーブルDTOから収支集約を生成して返します。
	 *</pre>
	 * @param dto 収支テーブルDTO
	 * @return 収支集約
	 *
	 */
	private IncomeAndExpenditure createIncomeAndExpenditure(IncomeAndExpenditureReadWriteDto dto) {
		return IncomeAndExpenditure.from(
				// ユーザID
				dto.getUserId(),
				// 対象年
				dto.getTargetYear(),
				// 対象月
				dto.getTargetMonth(),
				// 収入金額
				dto.getIncomeKingaku(),
				// 積立金取崩金額
				dto.getWithdrewKingaku(),
				// 支出予定金額
				dto.getExpenditureEstimateKingaku(),
				// 支出金額
				dto.getExpenditureKingaku(),
				// 収支金額
				dto.getIncomeAndExpenditureKingaku());
	}
}
