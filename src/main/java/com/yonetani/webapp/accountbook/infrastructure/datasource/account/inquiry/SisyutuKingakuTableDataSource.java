/**
 * SisyutuKingakuTableRepository(支出金額テーブルのデータを登録・更新・参照)を実装したデータソースです
 * 各月の収支画面の支出項目毎の支出一覧情報と年間収支の支出項目レベル１毎の支出一覧情報取得も
 * このデータソースで実装します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/30 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.inquiry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.AccountMonthInquiryExpenditureItemList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.AccountYearMeisaiInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.AccountYearMeisaiInquiryList.MeisaiInquiryListItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuKingakuItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuKingakuItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYear;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonthAndSisyutuItemCode;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.SisyutuKingakuTableRepository;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.AccountYearMeisaiInquiryReadDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.IncomeAndExpenditureReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.SisyutuKingakuAndSisyutuItemReadDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.SisyutuKingakuReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthAndSisyutuItemCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry.IncomeAndExpenditureTableMapper;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry.SisyutuKingakuTableMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * SisyutuKingakuTableRepository(支出金額テーブルのデータを登録・更新・参照)を実装したデータソースです
 * 各月の収支画面の支出項目毎の支出一覧情報と年間収支の支出項目レベル１毎の支出一覧情報取得も
 * このデータソースで実装します。
 * 
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Repository
@RequiredArgsConstructor
public class SisyutuKingakuTableDataSource implements SisyutuKingakuTableRepository {

	// マッパー
	private final IncomeAndExpenditureTableMapper incomeAndExpenditureMapper;
	// マッパー
	private final SisyutuKingakuTableMapper sisyutuKingakuTableMapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(SisyutuKingakuItem data) {
		// 支出金額テーブル：SISYUTU_KINGAKU_TABLEにデータを追加します。
		return sisyutuKingakuTableMapper.insert(SisyutuKingakuReadWriteDto.from(data));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(SisyutuKingakuItem data) {
		// 支出金額テーブル：SISYUTU_KINGAKU_TABLEの情報を指定の支出金額テーブル情報で更新します。
		return sisyutuKingakuTableMapper.update(SisyutuKingakuReadWriteDto.from(data));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SisyutuKingakuItem findByUniqueKey(SearchQueryUserIdAndYearMonthAndSisyutuItemCode search) {
		// 検索結果を取得
		SisyutuKingakuReadWriteDto searchResult = sisyutuKingakuTableMapper.findByUniqueKey(
				UserIdAndYearMonthAndSisyutuItemCodeSearchQueryDto.from(search));
		if(searchResult == null) {
			// 検索結果なしの場合、nullを返却
			return null;
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return createSisyutuKingakuItem(searchResult);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public SisyutuKingakuItemInquiryList findById(SearchQueryUserIdAndYearMonth search) {
		// 検索結果を取得
		List<SisyutuKingakuReadWriteDto> searchResult = sisyutuKingakuTableMapper.findById(UserIdAndYearMonthSearchQueryDto.from(search));
		if(searchResult == null) {
			// 検索結果なしの場合、0件データを返却
			return SisyutuKingakuItemInquiryList.from(null);
		} else {
			// 検索結果ありの場合、ドメインに変換して返却
			return SisyutuKingakuItemInquiryList.from(searchResult.stream().map(dto -> createSisyutuKingakuItem(dto))
					.collect(Collectors.toUnmodifiableList()));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AccountMonthInquiryExpenditureItemList select(SearchQueryUserIdAndYearMonth search) {
		// 検索結果を取得
		return convertExpenditureItemList(sisyutuKingakuTableMapper.selectMonthSisyutuKingakuList(
				UserIdAndYearMonthSearchQueryDto.from(search)));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AccountYearMeisaiInquiryList select(SearchQueryUserIdAndYear searchQuery) {
		/* 対象の収支情報の年月の値を取得 */
		// 対象の年月の収支情報を取得
		List<IncomeAndExpenditureReadWriteDto> targetYearMonthList = incomeAndExpenditureMapper.selectUserIdAndYear(
				UserIdAndYearSearchQueryDto.from(searchQuery));
		// 検索結果を取得
		List<AccountYearMeisaiInquiryReadDto> result = new ArrayList<>(targetYearMonthList.size());
		for(IncomeAndExpenditureReadWriteDto targetYearMonth : targetYearMonthList) {
			AccountYearMeisaiInquiryReadDto resultDto = sisyutuKingakuTableMapper.selectYearSisyutuKingakuList(
					UserIdAndYearMonthSearchQueryDto.fromString(
						searchQuery.getUserId().getValue(),
						searchQuery.getYear().getValue(),
						targetYearMonth.getTargetMonth()));
			// 対象月の支出金額情報なし(仕様的にパターンがあるため考慮必要)の場合、該当月の結果を無視する
			if(resultDto.getSisyutuKingaku() != null) {
				result.add(resultDto);
			}
		}
		// 検索結果をドメインモデルに変換して返却
		return AccountYearMeisaiInquiryList.from(result.stream().map(
				dto -> MeisaiInquiryListItem.from(
							dto.getMonth(),
							dto.getJigyouKeihiKingaku(),
							dto.getKoteiHikazeiKingaku(),
							dto.getKoteiKazeiKingaku(),
							dto.getIruiJyuukyoSetubiKingaku(),
							dto.getInsyokuNitiyouhinKingaku(),
							dto.getSyumiGotakuKingaku(),
							dto.getSisyutuKingakuB(),
							dto.getSisyutuKingakuC(),
							dto.getSisyutuKingaku(),
							dto.getSyuusiKingaku()
						)).collect(Collectors.toUnmodifiableList()));
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
		AccountMonthInquiryExpenditureItemList.ExpenditureListItem.from(
					dto.getSisyutuItemCode(),
					dto.getSisyutuItemName(),
					dto.getSisyutuItemLevel(),
					dto.getSisyutuKingaku(),
					dto.getSisyutuKingakuB(),
					dto.getSisyutuKingakuC(),
					dto.getSisyutuSiharaiDate())).collect(Collectors.toUnmodifiableList()));
	}
	
	/**
	 *<pre>
	 * 引数で指定した支出金額テーブル:SISYUTU_KINGAKU_TABLE読込・出力情報から支出金額テーブル情報ドメインモデルを生成して返します。
	 *</pre>
	 * @param dto 支出金額テーブル:SISYUTU_KINGAKU_TABLE読込・出力情報
	 * @return 支出金額テーブル情報ドメインモデル
	 *
	 */
	private SisyutuKingakuItem createSisyutuKingakuItem(SisyutuKingakuReadWriteDto dto) {
		return SisyutuKingakuItem.from(
				// ユーザID
				dto.getUserId(),
				// 対象年
				dto.getTargetYear(),
				// 対象月
				dto.getTargetMonth(),
				// 支出項目コード
				dto.getSisyutuItemCode(),
				// 親支出項目コード
				dto.getParentSisyutuItemCode(),
				// 支出予定金額
				dto.getSisyutuYoteiKingaku(),
				// 支出金額
				dto.getSisyutuKingaku(),
				// 支出金額B
				dto.getSisyutuKingakuB(),
				// 支出金額C
				dto.getSisyutuKingakuC(),
				// 支出支払日
				dto.getSisyutuSiharaiDate());
	}
}
