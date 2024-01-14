/**
 * 家計簿利用ユーザテーブル:ACCOUNT_BOOK_USER出力情報
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/12/10 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 家計簿利用ユーザテーブル:ACCOUNT_BOOK_USER出力情報
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AccountBookUserWriteDto {
	// ユーザID
	private final String userId;
	// 現在の対象年
	private final String nowTargetYear;
	// 現在の対象月
	private final String nowTargetMonth;
	// ユーザ名
	private final String userName;
	
	/**
	 *<pre>
	 * 引数のパラメータ値をもとにAccountBookUserWriteDtoを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param nowTargetYear 現在の対象年
	 * @param nowTargetMonth 現在の対象月
	 * @param userName ユーザ名
	 * @return 家計簿利用ユーザテーブル:ACCOUNT_BOOK_USER出力情報
	 *
	 */
	public static AccountBookUserWriteDto from(String userId, String nowTargetYear, String nowTargetMonth, String userName) {
		return new AccountBookUserWriteDto(userId, nowTargetYear, nowTargetMonth, userName);
	}
}
