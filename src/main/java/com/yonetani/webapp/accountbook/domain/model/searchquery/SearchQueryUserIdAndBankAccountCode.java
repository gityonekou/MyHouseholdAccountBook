/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・銀行口座コード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/08/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountCode;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 以下の照会条件の値を表すドメインモデルです。
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
@ToString
@EqualsAndHashCode
public class SearchQueryUserIdAndBankAccountCode {
	// ユーザID
	private final UserId userId;
	// 銀行口座コード
	private final BankAccountCode bankAccountCode;

	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・銀行口座コード
	 *</pre>
	 * @param userId ユーザID
	 * @param bankAccountCode 銀行口座コード
	 * @return 検索条件(ユーザID, 銀行口座コード)
	 *
	 */
	public static SearchQueryUserIdAndBankAccountCode from(UserId userId, BankAccountCode bankAccountCode) {
		return new SearchQueryUserIdAndBankAccountCode(userId, bankAccountCode);
	}
}
