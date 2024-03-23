/**
 * 家計簿利用ユーザ:ACCOUNT_BOOK_USERテーブルの照会を行うリポジトリーを実装したデータソースです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/08 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.common;

import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.yonetani.webapp.accountbook.domain.model.common.AccountBookAllUsers;
import com.yonetani.webapp.accountbook.domain.model.common.AccountBookUser;
import com.yonetani.webapp.accountbook.domain.model.common.NowTargetYearMonth;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.repository.common.AccountBookUserRepository;
import com.yonetani.webapp.accountbook.infrastructure.dto.common.AccountBookUserDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.common.AccountBookUserWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.common.AccountBookUserMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 家計簿利用ユーザ:ACCOUNT_BOOK_USERテーブルの照会を行うリポジトリーを実装したデータソースです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Repository
@RequiredArgsConstructor
public class AccountBookUserDataSource implements AccountBookUserRepository {

	// マッパー
	private final AccountBookUserMapper mapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public NowTargetYearMonth getNowTargetYearMonth(SearchQueryUserId searchQuery) {
		AccountBookUserDto result = mapper.selectUser(UserIdSearchQueryDto.from(searchQuery.getUserId().toString()));
		if(result == null) {
			return NowTargetYearMonth.from(null, null);
		} else {
			return NowTargetYearMonth.from(result.getNowTargetYear(), result.getNowTargetMonth());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AccountBookAllUsers getAllUsers() {
		// 検索結果を取得
		return AccountBookAllUsers.from(mapper.selectAllUsers().stream().map(dto ->
			AccountBookUser.from(
					dto.getUserId(),
					dto.getNowTargetYear(),
					dto.getNowTargetMonth(),
					dto.getUserName())
			).collect(Collectors.toUnmodifiableList()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(AccountBookUser userInfo) {
		// 家計簿利用ユーザテーブル:ACCOUNT_BOOK_USERにデータを追加
		return mapper.insertAccountBookUser(createAccountBookUserWriteDto(userInfo));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(AccountBookUser userInfo) {
		// 家計簿利用ユーザテーブル:ACCOUNT_BOOK_USERの指定ユーザ情報のデータを更新します。
		return mapper.updateAccountBookUser(createAccountBookUserWriteDto(userInfo));
	}
	
	/**
	 *<pre>
	 * 引数で指定した家計簿利用ユーザのユーザ情報ドメインモデルからDTOを生成して返します。
	 *</pre>
	 * @param userInfo 家計簿利用ユーザのユーザ情報
	 * @return 家計簿利用ユーザテーブル:ACCOUNT_BOOK_USER出力情報
	 *
	 */
	private AccountBookUserWriteDto createAccountBookUserWriteDto(AccountBookUser userInfo) {
		return AccountBookUserWriteDto.from(
				// ユーザID
				userInfo.getUserId().toString(),
				// 現在の対象年
				userInfo.getNowTargetYear().toString(),
				// 現在の対象月
				userInfo.getNowTargetMonth().toString(),
				// ユーザ名
				userInfo.getUserName().toString());
	}

}
