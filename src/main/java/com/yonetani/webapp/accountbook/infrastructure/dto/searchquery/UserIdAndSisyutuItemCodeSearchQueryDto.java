/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・支出項目コード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/02/23 : 1.00.00  新規作成
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
 * ・支出項目コード
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserIdAndSisyutuItemCodeSearchQueryDto {
	// ユーザID
	private final String userId;
	// 店舗コード
	private final String sisyutuItemCode;
	
	/**
	 *<pre>
	 * 引数のパラメータ値をもとにUserIdAndSisyutuItemCodeSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param sisyutuItemCode 店舗コード
	 * @return テーブルの検索条件：ユーザID、店舗コード
	 *
	 */
	public static UserIdAndSisyutuItemCodeSearchQueryDto from(String userId, String sisyutuItemCode) {
		return new UserIdAndSisyutuItemCodeSearchQueryDto(userId, sisyutuItemCode);
	}
}
