/**
 * IncomeAndExpenseInquiryRepository(収支情報取得)を実装したデータソースです。
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

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenseInquiryItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenseInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYear;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeAndExpenseInquiryRepository;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.IncomeAndExpenseReadDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry.IncomeAndExpenseInquiryMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * IncomeAndExpenseInquiryRepository(収支情報取得)を実装したデータソースです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Repository
@RequiredArgsConstructor
public class IncomeAndExpenseInquiryDataSource implements IncomeAndExpenseInquiryRepository {

	// マッパー
	private final IncomeAndExpenseInquiryMapper mapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IncomeAndExpenseInquiryList select(SearchQueryUserIdAndYear searchQuery) {
		// 検索結果を取得
		List<IncomeAndExpenseReadDto> result = mapper.selectUserIdAndYear(UserIdAndYearSearchQueryDto.from(
				searchQuery.getUserId().toString(),
				searchQuery.getYear().toString()));
		// 検索結果をドメインモデルに変換して返却
		return IncomeAndExpenseInquiryList.from(result.stream().map(dto ->	 createIncomeAndExpenseInquiryItem(dto))
					.collect(Collectors.toUnmodifiableList()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IncomeAndExpenseInquiryItem select(SearchQueryUserIdAndYearMonth searchQuery) {
		// 検索結果を取得
		IncomeAndExpenseReadDto result = mapper.selectUserIdAndYearMonth(UserIdAndYearMonthSearchQueryDto.from(
				searchQuery.getUserId().toString(),
				searchQuery.getYearMonth().getYear(),
				searchQuery.getYearMonth().getMonth()
				));
		// 検索結果をドメインモデルに変換して返却
		if(result != null) {
			return createIncomeAndExpenseInquiryItem(result);
		} else {
			return IncomeAndExpenseInquiryItem.fromEmpty();
		}
	}

	/**
	 *<pre>
	 * 引数で指定した収支テーブル:INCOME_AND_EXPENSE_TABLEテーブルの各項目のDTOから収支情報ドメインモデルを生成して返します。
	 *</pre>
	 * @param dto 収支テーブル:INCOME_AND_EXPENSE_TABLEテーブルの各項目のDTO
	 * @return 収支情報ドメインモデル
	 *
	 */
	private IncomeAndExpenseInquiryItem createIncomeAndExpenseInquiryItem(IncomeAndExpenseReadDto dto) {
		return IncomeAndExpenseInquiryItem.from(
				// 対象月
				dto.getTargetMonth(),
				// 収入金額
				dto.getIncomeKingaku(),
				// 支出金額
				dto.getExpenseKingaku(),
				// 支出予定金額
				dto.getExpenseYoteiKingaku(),
				// 収支
				dto.getSyuusiKingaku());
	}
}
