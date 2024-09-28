/**
 * 「ユーザパスワード」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/11/26 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.adminmenu;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「ユーザパスワード」項目の値を表すドメインタイプです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class UserPassword {
	
	// ユーザパスワード
	private final String value;
	
	/**
	 *<pre>
	 * 「ユーザパスワード」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param password パスワードの値
	 * @return 「ユーザパスワード」項目ドメインタイプ
	 *
	 */
	public static UserPassword from(String password) {
		return new UserPassword(password);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
