/**
 * AccountMonthInquiryRepository(マイ家計簿 指定月の支出金額情報取得)を実装したデータソースです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/30 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.inquiry;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.AccountMonthInquiryExpenditureItemList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.AccountMonthInquiryRepository;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.SisyutuKingakuAndSisyutuItemReadDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry.AccountMonthInquiryMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * AccountMonthInquiryRepository(マイ家計簿 指定月の支出金額情報取得)を実装したデータソースです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Repository
@RequiredArgsConstructor
public class AccountMonthInquiryDataSource implements AccountMonthInquiryRepository {

	// マッパー
	private final AccountMonthInquiryMapper mapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AccountMonthInquiryExpenditureItemList selectExpenditureItem(SearchQueryUserIdAndYearMonth searchQuery) {
		// 検索結果を取得
		return convertExpenditureItemList(mapper.selectExpenditureItem(UserIdAndYearMonthSearchQueryDto.from(
				searchQuery.getUserId().toString(),
				searchQuery.getYearMonth().getYear(),
				searchQuery.getYearMonth().getMonth()
				)));
	}
	
	/**
	 *<pre>
	 * 検索結果をドメインモデルに変換して返却
	 *</pre>
	 * @param resultDto 各月の収支取得結果(DTO)
	 * @return 月毎の支出金額情報のリスト結果ドメインモデル
	 *
	 */
	private AccountMonthInquiryExpenditureItemList convertExpenditureItemList(List<SisyutuKingakuAndSisyutuItemReadDto> resultDto) {
		return AccountMonthInquiryExpenditureItemList.from(resultDto.stream().map(dto -> 
		AccountMonthInquiryExpenditureItemList.ExpenditureItem.from(
					dto.getSisyutuItemCode(),
					dto.getSisyutuItemName(),
					dto.getSisyutuItemLevel(),
					dto.getSisyutuKingaku(),
					dto.getSisyutuKingakuB(),
					dto.getSiharaiDate(),
					dto.isClosingFlg())).collect(Collectors.toUnmodifiableList()));
	}

}
