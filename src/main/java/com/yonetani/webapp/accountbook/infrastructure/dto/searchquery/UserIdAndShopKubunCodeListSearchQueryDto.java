/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・店舗区分コードのリスト
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/26 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.searchquery;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・店舗区分コードのリスト
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserIdAndShopKubunCodeListSearchQueryDto {
	// ユーザID
	private final String userId;
	// 店舗区分コードのリスト
	private final List<String> shopKubunCodeList;
	
	/**
	 *<pre>
	 * 引数のパラメータ値をもとにUserIdAndShopKubunCodeListSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param shopKubunCodeList 店舗区分コードのリスト
	 * @return テーブルの検索条件：ユーザID、店舗区分コードのリスト
	 *
	 */
	public static UserIdAndShopKubunCodeListSearchQueryDto from(String userId, List<String> shopKubunCodeList) {
		return new UserIdAndShopKubunCodeListSearchQueryDto(userId, shopKubunCodeList);
	}
}
