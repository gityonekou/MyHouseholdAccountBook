/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・店舗コード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/02/03 : 1.00.00  新規作成
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
 * ・店舗コード
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserIdAndShopCodeSearchQueryDto {
	// ユーザID
	private final String userId;
	// 店舗コード
	private final String shopCode;
	
	/**
	 *<pre>
	 * 引数のパラメータ値をもとにUserIdAndShopCodeSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param shopCode 店舗コード
	 * @return テーブルの検索条件：ユーザID、店舗コード
	 *
	 */
	public static UserIdAndShopCodeSearchQueryDto from(String userId, String shopCode) {
		return new UserIdAndShopCodeSearchQueryDto(userId, shopCode);
	}
}
