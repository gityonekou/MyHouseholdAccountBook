/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・銀行口座表示順A
 * ・銀行口座表示順B
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountSort;
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
 * ・銀行口座表示順A
 * ・銀行口座表示順B
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
public class SearchQueryUserIdAndBankAccountSortBetweenAB {
	// ユーザID
	private final UserId userId;
	// 銀行口座表示順A
	private final BankAccountSort bankAccountSortA;
	// 銀行口座表示順B
	private final BankAccountSort bankAccountSortB;

	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・銀行口座表示順A
	 * ・銀行口座表示順B
	 *</pre>
	 * @param userId ユーザID
	 * @param bankAccountSortA 銀行口座表示順A
	 * @param bankAccountSortB 銀行口座表示順B
	 * @return 検索条件(ユーザID, 銀行口座表示順A, 銀行口座表示順B)
	 *
	 */
	public static SearchQueryUserIdAndBankAccountSortBetweenAB from(
			UserId userId, BankAccountSort bankAccountSortA, BankAccountSort bankAccountSortB) {
		return new SearchQueryUserIdAndBankAccountSortBetweenAB(userId, bankAccountSortA, bankAccountSortB);
	}
}
