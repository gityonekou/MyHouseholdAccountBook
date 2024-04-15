/**
 * テーブルの検索条件が以下の場合に使用するTDOです。
 * ・ユーザID
 * ・支出項目表示順A
 * ・支出項目表示順B
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/04/15 : 1.00.00  新規作成
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
 * ・支出項目表示順A
 * ・支出項目表示順B
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserIdAndSisyutuItemSortBetweenABSearchQueryDto {
	// ユーザID
	private final String userId;
	// 支出項目表示順A
	private final String sisyutuItemSortA;
	// 支出項目表示順B
	private final String sisyutuItemSortB;
	
	/**
	 *<pre>
	 * 引数のパラメータ値をもとにUserIdAndSisyutuItemSortABSearchQueryDtoを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param sisyutuItemSortA 支出項目表示順A
	 * @param sisyutuItemSortB 支出項目表示順B
	 * @return テーブルの検索条件：ユーザID、支出項目表示順A、支出項目表示順B
	 *
	 */
	public static UserIdAndSisyutuItemSortBetweenABSearchQueryDto from(String userId, String sisyutuItemSortA,  String sisyutuItemSortB) {
		return new UserIdAndSisyutuItemSortBetweenABSearchQueryDto(userId, sisyutuItemSortA, sisyutuItemSortB);
	}
}
