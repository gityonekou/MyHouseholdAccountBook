/**
 * 銀行口座テーブル情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/18 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.bankaccount;

import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountCode;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountMemo;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankAccountSort;
import com.yonetani.webapp.accountbook.domain.type.account.bankaccount.BankName;
import com.yonetani.webapp.accountbook.domain.type.common.EnableFlg;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 銀行口座テーブル情報を表すドメインモデルです
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
public class BankAccount {
	// ユーザID
	private final UserId userId;
	// 銀行口座コード
	private final BankAccountCode bankAccountCode;
	// 銀行名
	private final BankName bankName;
	// 銀行口座メモ(null許容)
	private final BankAccountMemo bankAccountMemo;
	// 銀行口座表示順
	private final BankAccountSort bankAccountSort;
	// 有効/無効フラグ
	private final EnableFlg enableFlg;

	/**
	 *<pre>
	 * 引数の値から銀行口座テーブル情報を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param bankAccountCode 銀行口座コード
	 * @param bankName 銀行名
	 * @param bankAccountMemo 銀行口座メモ(null許容)
	 * @param bankAccountSort 銀行口座表示順
	 * @param enableFlg 有効/無効フラグ
	 * @return 銀行口座テーブル情報を表すドメインモデル
	 *
	 */
	public static BankAccount from(
			String userId,
			String bankAccountCode,
			String bankName,
			String bankAccountMemo,
			String bankAccountSort,
			boolean enableFlg) {
		return new BankAccount(
				UserId.from(userId),
				BankAccountCode.from(bankAccountCode),
				BankName.from(bankName),
				BankAccountMemo.from(bankAccountMemo),
				BankAccountSort.from(bankAccountSort),
				EnableFlg.from(enableFlg));
	}
}
