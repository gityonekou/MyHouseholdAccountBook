/**
 * ユーザ情報をセッションから取得するためのクラスです。
 * UserSessionConfigクラスによりBean登録されています。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/24 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.session;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 *<pre>
 * ユーザ情報をセッションから取得するためのクラスです。
 * UserSessionConfigクラスによりBean登録されています。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public class UserSession {
	
	/**
	 *<pre>
	 * ログインしたユーザのユーザIDをセッションから取得して返します。
	 *</pre>
	 * @return ログインしたユーザのユーザID
	 *
	 */
	public String getUserId() {
		return  SecurityContextHolder.getContext().getAuthentication().getName();
	}
}
