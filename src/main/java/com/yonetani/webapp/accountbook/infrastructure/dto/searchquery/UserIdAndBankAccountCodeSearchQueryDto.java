/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・銀行口座コード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/08/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.searchquery;

import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndBankAccountCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・銀行口座コード
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserIdAndBankAccountCodeSearchQueryDto {
	// ユーザID
	private final String userId;
	// 銀行口座コード
	private final String bankAccountCode;

	/**
	 *<pre>
	 * 検索条件のドメイン情報をもとにUserIdAndBankAccountCodeSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param search 検索条件(ユーザID、銀行口座コード)
	 * @return テーブルの検索条件：ユーザID、銀行口座コード
	 *
	 */
	public static UserIdAndBankAccountCodeSearchQueryDto from(SearchQueryUserIdAndBankAccountCode search) {
		return new UserIdAndBankAccountCodeSearchQueryDto(
				search.getUserId().getValue(),
				search.getBankAccountCode().getValue());
	}
}
