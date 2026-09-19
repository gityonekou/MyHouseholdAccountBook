/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・銀行口座表示順
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/18 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.searchquery;

import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndBankAccountSort;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・銀行口座表示順
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserIdAndBankAccountSortSearchQueryDto {
	// ユーザID
	private final String userId;
	// 銀行口座表示順
	private final String bankAccountSort;

	/**
	 *<pre>
	 * 検索条件のドメイン情報をもとにUserIdAndBankAccountSortSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param search 検索条件(ユーザID、銀行口座表示順)
	 * @return テーブルの検索条件：ユーザID、銀行口座表示順
	 *
	 */
	public static UserIdAndBankAccountSortSearchQueryDto from(SearchQueryUserIdAndBankAccountSort search) {
		return new UserIdAndBankAccountSortSearchQueryDto(
				search.getUserId().getValue(),
				search.getBankAccountSort().getValue());
	}
}
