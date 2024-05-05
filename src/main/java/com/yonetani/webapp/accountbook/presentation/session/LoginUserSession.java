/**
 * ログインユーザ情報のセッションスコープBeanです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/05/05 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.session;

import java.io.Serializable;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import lombok.Data;

/**
 *<pre>
 * ログインユーザ情報のセッションスコープBeanです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Component
@SessionScope
@Data
public class LoginUserSession implements Serializable {
	private static final long serialVersionUID = 1L;
	
	// セッション管理するログインユーザ情報
	private LoginUserInfo loginUserInfo;
	
	/**
	 *<pre>
	 * セッション管理しているログインユーザ情報をクリアします。
	 *</pre>
	 *
	 */
	public void clearData() {
		loginUserInfo = null;
	}
}
