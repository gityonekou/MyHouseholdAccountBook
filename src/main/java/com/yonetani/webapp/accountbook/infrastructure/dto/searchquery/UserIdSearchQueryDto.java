/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/30 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.searchquery;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserIdSearchQueryDto {
	// ユーザID
	private final String userId;
	
	/**
	 *<pre>
	 * ユーザIDの文字列を元にUserIdSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param search 検索条件(String):ユーザID
	 * @return テーブルの検索条件：ユーザID
	 *
	 */
	public static UserIdSearchQueryDto from(String search) {
		return new UserIdSearchQueryDto(search);
	}
}
