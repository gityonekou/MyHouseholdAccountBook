/**
 * 「ユーザID」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/24 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「ユーザID」項目の値を表すドメインタイプです。
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
public class UserId {
	// ユーザID
	private final String value;
	
	/**
	 *<pre>
	 *「ユーザID」項目の値を表すドメインタイプを生成します。
	 * 
	 * [ガード節]
	 * ・空文字列
	 * ・長さ50文字以内
	 *</pre>
	 * @param userId ユーザID
	 * @return 「ユーザID」項目ドメインタイプ
	 *
	 */
	public static UserId from(String userId) {
		// ガード節(空文字列)
		if(!StringUtils.hasLength(userId)) {
			throw new MyHouseholdAccountBookRuntimeException("「ユーザID」項目の設定値が空文字列です。管理者に問い合わせてください。");
		}
		// ガード節(長さ50文字以内)
		if(userId.length() > 50) {
			throw new MyHouseholdAccountBookRuntimeException("「ユーザID」項目の設定値が不正です。管理者に問い合わせてください。[userId=" + userId + "]");
		}
		return new UserId(userId);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return value;
	}
}
