/**
 * 以下の照会条件の値を表すドメインモデルです。
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
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuItemSort;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 以下の照会条件の値を表すドメインモデルです。
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
@ToString
@EqualsAndHashCode
public class SearchQueryUserIdAndSisyutuItemSortBetweenAB {
	
	// ユーザID
	private final UserId userId;
	// 支出項目表示順A
	private final SisyutuItemSort sisyutuItemSortA;
	// 支出項目表示順B
	private final SisyutuItemSort sisyutuItemSortB;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・支出項目表示順A
	 * ・支出項目表示順B
	 *</pre>
	 * @param userId ユーザID
	 * @param sisyutuItemSortA 支出項目表示順A
	 * @param sisyutuItemSortB 支出項目表示順B
	 * @return 検索条件(ユーザID, 支出項目表示順A, 支出項目表示順B)
	 *
	 */
	public static SearchQueryUserIdAndSisyutuItemSortBetweenAB from(
			UserId userId, SisyutuItemSort sisyutuItemSortA, SisyutuItemSort sisyutuItemSortB) {
		return new SearchQueryUserIdAndSisyutuItemSortBetweenAB(userId, sisyutuItemSortA, sisyutuItemSortB);
	}
}
