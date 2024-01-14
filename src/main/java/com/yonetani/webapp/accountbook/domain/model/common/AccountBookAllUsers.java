/**
 * 家計簿利用ユーザの全ユーザ情報ドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/11/04 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.common;

import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 家計簿利用ユーザの全ユーザ情報ドメインタイプです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AccountBookAllUsers {
	
	// 家計簿利用ユーザのリスト
	private final List<AccountBookUser> values;
	
	/**
	 *<pre>
	 * 家計簿利用ユーザ(ドメインモデル)のリストからAccountBookAllUsersのドメインモデルを生成して返します。
	 *</pre>
	 * @param values 家計簿利用ユーザ(ドメインモデル)のリスト
	 * @return AccountBookAllUsersのドメインモデル
	 *
	 */
	public static AccountBookAllUsers from(List<AccountBookUser> values) {
		if(CollectionUtils.isEmpty(values)) {
			return new AccountBookAllUsers(Collections.emptyList());
		} else {
			return new AccountBookAllUsers(values);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if(values.size() > 0) {
			StringBuilder buff = new StringBuilder((values.size() + 1) * 130);
			buff.append("家計簿利用ユーザ:")
			.append(values.size())
			.append("件:");
			for(int i = 0; i < values.size(); i++) {
				buff.append("[[")
				.append(i)
				.append("][")
				.append(values.get(i))
				.append("]]");
			}
			return buff.toString();
		} else {
			return "家計簿利用ユーザ:0件";
		}
	}
}
