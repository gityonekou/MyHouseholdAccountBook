/**
 * 家計簿利用ユーザのユーザ情報を表すドメインモデルです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/12/16 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.common;

import com.yonetani.webapp.accountbook.domain.type.common.TargetMonth;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYear;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.domain.type.common.UserName;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 家計簿利用ユーザのユーザ情報を表すドメインモデルです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
public class AccountBookUser {
	// ユーザID
	private final UserId userId;
	// 現在の対象年
	private final TargetYear nowTargetYear;
	// 現在の対象月
	private final TargetMonth nowTargetMonth;
	// ユーザ名
	private final UserName userName;
	/**
	 *<pre>
	 * 引数の値から家計簿利用ユーザドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param nowTargetYear 現在の対象年
	 * @param nowTargetMonth 現在の対象月
	 * @param userName ユーザ名
	 * @return 家計簿利用ユーザドメインモデル
	 *
	 */
	public static AccountBookUser from(
			UserId userId,
			TargetYear nowTargetYear,
			TargetMonth nowTargetMonth,
			UserName userName) {
		return new AccountBookUser(
			userId,
			nowTargetYear,
			nowTargetMonth,
			userName);
	}
}
