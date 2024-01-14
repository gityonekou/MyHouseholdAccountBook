/**
 * 家計簿利用ユーザ:ACCOUNT_BOOK_USERテーブルの各項目のDTOです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/08 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 家計簿利用ユーザ:ACCOUNT_BOOK_USERテーブルの各項目のDTOです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AccountBookUserDto {
	// ユーザID
	private final String userId;
	// 現在の対象年
	private final String nowTargetYear;
	// 現在の対象月
	private final String nowTargetMonth;
	// ユーザ名
	private final String userName;
}
