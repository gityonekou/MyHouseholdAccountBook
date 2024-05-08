/**
 * セッションに設定するログインユーザ情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/05 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.session;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * セッションに設定するログインユーザ情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class LoginUserInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	// ログインユーザID
	private final String userId;
	// ログインユーザ名
	private final String userName;
	
	/**
	 *<pre>
	 * 引数の値からログインユーザ情報を生成して返します。
	 *</pre>
	 * @param userId ログインユーザID
	 * @param userName ログインユーザ名
	 * @return ログインユーザ情報
	 *
	 */
	public static LoginUserInfo from(String userId, String userName) {
		return new LoginUserInfo(userId, userName);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder(40);
		buff.append("userId:")
		.append(userId)
		.append(",userName:")
		.append(userName);
		return buff.toString();
	}
}
