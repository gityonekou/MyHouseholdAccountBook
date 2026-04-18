/**
 * 「ユーザID」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/24 : 1.00.00  新規作成
 * 2025/11/25 : 1.00.00  Identifier抽象クラスを継承するようリファクタリング
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

import lombok.EqualsAndHashCode;

/**
 *<pre>
 * 「ユーザID」項目の値を表すドメインタイプです。
 *
 * [ビジネスルール]
 * ・ユーザIDは必須項目
 * ・空文字は許可されない
 * ・長さは50文字以内
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@EqualsAndHashCode(callSuper = true)
public class UserId extends Identifier {

	/**
	 *<pre>
	 * コンストラクタ（privateでファクトリメソッド経由のみ生成可能）
	 *</pre>
	 * @param value ユーザID
	 *
	 */
	private UserId(String value) {
		super(value);
	}

	/**
	 *<pre>
	 *「ユーザID」項目の値を表すドメインタイプを生成します。
	 *
	 * [ガード節]
	 * ・null値
	 * ・空文字列
	 * ・長さ50文字以内
	 *</pre>
	 * @param userId ユーザID
	 * @return 「ユーザID」項目ドメインタイプ
	 *
	 */
	public static UserId from(String userId) {
		// 基本検証（null、空文字）
		Identifier.validate(userId, "ユーザID");

		// ガード節(長さ50文字以内)
		if(userId.length() > 50) {
			throw new MyHouseholdAccountBookRuntimeException(
				"「ユーザID」項目の設定値が不正です。管理者に問い合わせてください。[userId=" + userId + "]");
		}

		// 「ユーザID」項目の値を生成して返却
		return new UserId(userId);
	}
}
