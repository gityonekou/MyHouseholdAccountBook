/**
 * 家計簿利用ユーザテーブル:ACCOUNT_BOOK_USER、ユーザテーブル:USERSから指定ユーザIDのユーザ情報を
 * 取得した結果を格納するDTOです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/11/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.adminmenu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 家計簿利用ユーザテーブル:ACCOUNT_BOOK_USER、ユーザテーブル:USERSから指定ユーザIDのユーザ情報を
 * 取得した結果を格納するDTOです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AdminMenuUserInfoDto {
	// ユーザID
	private final String userId;
	// 現在の対象年
	private final String nowTargetYear;
	// 現在の対象月
	private final String nowTargetMonth;
	// ユーザ名
	private final String userName;
	// フラグ
	private final boolean enabled;
}
