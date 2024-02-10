/**
 * AccountYearMeisaiInquiryRepository(指定年度の年間収支(明細)情報取得)を実装したデータソースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/12 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.inquiry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.AccountYearMeisaiInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYear;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.AccountYearMeisaiInquiryRepository;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.AccountYearMeisaiInquiryReadDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.IncomeAndExpenseReadDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry.AccountYearMeisaiInquiryMapper;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry.IncomeAndExpenseInquiryMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * AccountYearMeisaiInquiryRepository(指定年度の年間収支(明細)情報取得)を実装したデータソースです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Repository
@RequiredArgsConstructor
public class AccountYearMeisaiInquiryDataSource implements AccountYearMeisaiInquiryRepository {

	// マッパー
	private final IncomeAndExpenseInquiryMapper incomeAndExpenseMapper;
	// マッパー
	private final AccountYearMeisaiInquiryMapper accountYearMeisaiMapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AccountYearMeisaiInquiryList select(SearchQueryUserIdAndYear searchQuery) {
		/* 対象の収支情報の年月の値を取得 */
		// 対象の年月の収支情報を取得
		List<IncomeAndExpenseReadDto> targetYearMonthList = incomeAndExpenseMapper.selectUserIdAndYear(
				UserIdAndYearSearchQueryDto.from(
						searchQuery.getUserId().toString(),
						searchQuery.getYear().toString()));
		// 検索結果を取得
		List<AccountYearMeisaiInquiryReadDto> result = new ArrayList<>(targetYearMonthList.size());
		for(IncomeAndExpenseReadDto targetYearMonth : targetYearMonthList) {
			AccountYearMeisaiInquiryReadDto resultDto = accountYearMeisaiMapper.select(UserIdAndYearMonthSearchQueryDto.from(
					searchQuery.getUserId().toString(),
					searchQuery.getYear().toString(),
					targetYearMonth.getTargetMonth()));
			result.add(resultDto);
		}
		// 検索結果をドメインモデルに変換して返却
		return AccountYearMeisaiInquiryList.from(result.stream().map(
				dto -> AccountYearMeisaiInquiryList.MeisaiInquiryListItem.from(
						dto.getMonth(),
						dto.getJigyouKeihiKingaku(),
						dto.getKoteiHikazeiKingaku(),
						dto.getKoteiKazeiKingaku(),
						dto.getIruiJyuukyoSetubiKingaku(),
						dto.getInsyokuNitiyouhinKingaku(),
						dto.getSyumiGotakuKingaku(),
						dto.getSisyutuKingakuB(),
						dto.getSisyutuKingaku(),
						dto.getSyuusiKingaku()
						)).collect(Collectors.toUnmodifiableList()));
	}

}
