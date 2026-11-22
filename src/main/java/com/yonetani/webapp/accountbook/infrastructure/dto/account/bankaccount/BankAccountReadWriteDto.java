/**
 * 銀行口座テーブル:BANK_ACCOUNT_TABLE読込・出力情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/18 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.account.bankaccount;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 銀行口座テーブル:BANK_ACCOUNT_TABLE読込・出力情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class BankAccountReadWriteDto {
	// ユーザID
	private final String userId;
	// 銀行口座コード
	private final String bankAccountCode;
	// 銀行名
	private final String bankName;
	// 口座メモ
	private final String bankAccountMemo;
	// 銀行口座表示順
	private final String bankAccountSort;
	// 有効/無効フラグ
	private final boolean enableFlg;

	/**
	 *<pre>
	 * 引数のパラメータ値をもとにBankAccountReadWriteDtoを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param bankAccountCode 銀行口座コード
	 * @param bankName 銀行名
	 * @param bankAccountMemo 口座メモ
	 * @param bankAccountSort 銀行口座表示順
	 * @param enableFlg 有効/無効フラグ
	 * @return 銀行口座テーブル:BANK_ACCOUNT_TABLE出力情報
	 *
	 */
	public static BankAccountReadWriteDto from(String userId, String bankAccountCode, String bankName,
			String bankAccountMemo, String bankAccountSort, boolean enableFlg) {
		return new BankAccountReadWriteDto(userId, bankAccountCode, bankName, bankAccountMemo, bankAccountSort, enableFlg);
	}
}
