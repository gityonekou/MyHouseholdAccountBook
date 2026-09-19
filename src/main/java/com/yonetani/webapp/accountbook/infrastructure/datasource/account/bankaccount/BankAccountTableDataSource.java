/**
 * BankAccountTableRepository(銀行口座テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/18 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.account.bankaccount;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.domain.model.account.bankaccount.BankAccount;
import com.yonetani.webapp.accountbook.domain.model.account.bankaccount.BankAccountInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndBankAccountCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndBankAccountSort;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndBankAccountSortBetweenAB;
import com.yonetani.webapp.accountbook.domain.repository.account.bankaccount.BankAccountTableRepository;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.bankaccount.BankAccountReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndBankAccountCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndBankAccountSortBetweenABSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndBankAccountSortSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.account.bankaccount.BankAccountTableMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * BankAccountTableRepository(銀行口座テーブルのデータを登録・更新・参照する)を実装したデータソースです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@Repository
@RequiredArgsConstructor
public class BankAccountTableDataSource implements BankAccountTableRepository {

	// マッパー
	private final BankAccountTableMapper mapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(BankAccount data) {
		// 銀行口座情報を銀行口座テーブルに出力
		return mapper.insert(createBankAccountReadWriteDto(data));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(BankAccount data) {
		// 銀行口座テーブル:BANK_ACCOUNT_TABLEを更新
		return mapper.update(createBankAccountReadWriteDto(data));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int updateBankAccountSort(BankAccount data) {
		// 銀行口座テーブル:BANK_ACCOUNT_TABLEの表示順の値を更新
		return mapper.updateBankAccountSort(createBankAccountReadWriteDto(data));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BankAccountInquiryList findById(SearchQueryUserId userId) {
		List<BankAccountReadWriteDto> searchResult = mapper.findById(UserIdSearchQueryDto.from(userId));
		if(searchResult == null) {
			return BankAccountInquiryList.from(null);
		} else {
			return BankAccountInquiryList.from(searchResult.stream().map(dto -> createBankAccount(dto)).collect(Collectors.toUnmodifiableList()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BankAccountInquiryList findById(SearchQueryUserIdAndBankAccountSort search) {
		List<BankAccountReadWriteDto> searchResult = mapper.findByIdAndBankAccountSort(
				UserIdAndBankAccountSortSearchQueryDto.from(search));
		if(searchResult == null) {
			return BankAccountInquiryList.from(null);
		} else {
			return BankAccountInquiryList.from(searchResult.stream().map(dto -> createBankAccount(dto)).collect(Collectors.toUnmodifiableList()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BankAccountInquiryList findById(SearchQueryUserIdAndBankAccountSortBetweenAB search) {
		List<BankAccountReadWriteDto> searchResult = mapper.findByIdAndBankAccountSortBetween(
				UserIdAndBankAccountSortBetweenABSearchQueryDto.from(search));
		if(searchResult == null) {
			return BankAccountInquiryList.from(null);
		} else {
			return BankAccountInquiryList.from(searchResult.stream().map(dto -> createBankAccount(dto)).collect(Collectors.toUnmodifiableList()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BankAccount findById(SearchQueryUserIdAndBankAccountCode search) {
		BankAccountReadWriteDto result = mapper.findByIdAndBankAccountCode(UserIdAndBankAccountCodeSearchQueryDto.from(search));
		if(result == null) {
			return null;
		} else {
			return createBankAccount(result);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countById(SearchQueryUserId userId) {
		return mapper.countById(UserIdSearchQueryDto.from(userId));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BankAccountInquiryList findSelectableByUserId(SearchQueryUserId userId) {
		List<BankAccountReadWriteDto> searchResult = mapper.findSelectableByUserId(UserIdSearchQueryDto.from(userId));
		if(searchResult == null) {
			return BankAccountInquiryList.from(null);
		} else {
			return BankAccountInquiryList.from(searchResult.stream().map(dto -> createBankAccount(dto)).collect(Collectors.toUnmodifiableList()));
		}
	}

	/**
	 *<pre>
	 * 引数で指定した銀行口座テーブル:BANK_ACCOUNT_TABLE読込・出力情報から銀行口座テーブル情報ドメインモデルを生成して返します。
	 *</pre>
	 * @param dto 銀行口座テーブル:BANK_ACCOUNT_TABLE読込・出力情報
	 * @return 銀行口座テーブル情報ドメインモデル
	 *
	 */
	private BankAccount createBankAccount(BankAccountReadWriteDto dto) {
		return BankAccount.from(
				dto.getUserId(),
				dto.getBankAccountCode(),
				dto.getBankName(),
				dto.getBankAccountMemo(),
				dto.getBankAccountSort(),
				dto.isEnableFlg());
	}

	/**
	 *<pre>
	 * 引数で指定した銀行口座テーブル情報ドメインモデルからDTOを生成して返します。
	 *</pre>
	 * @param data 銀行口座テーブル情報ドメインモデル
	 * @return 銀行口座テーブル:BANK_ACCOUNT_TABLE読込・出力情報
	 *
	 */
	private BankAccountReadWriteDto createBankAccountReadWriteDto(BankAccount data) {
		return BankAccountReadWriteDto.from(
				data.getUserId().getValue(),
				data.getBankAccountCode().getValue(),
				data.getBankName().getValue(),
				data.getBankAccountMemo().getValue(),
				data.getBankAccountSort().getValue(),
				data.getEnableFlg().getValue());
	}
}
