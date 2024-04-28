/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・店舗表示順A
 * ・店舗表示順B
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/02/10 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopSort;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・店舗表示順A
 * ・店舗表示順B
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SearchQueryUserIdAndShopSortBetweenAB {
	// ユーザID
	private final UserId userId;
	// 店舗表示順A
	private final ShopSort shopSortA;
	// 店舗表示順B
	private final ShopSort shopSortB;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・店舗コード
	 *</pre>
	 * @param userId ユーザID
	 * @param shopSortA 店舗表示順A
	 * @param shopSortB 店舗表示順B
	 * @return 検索条件(ユーザID, 店舗表示順A, 店舗表示順B)
	 *
	 */
	public static SearchQueryUserIdAndShopSortBetweenAB from(String userId, String shopSortA, String shopSortB) {
		return new SearchQueryUserIdAndShopSortBetweenAB(UserId.from(userId), ShopSort.from(shopSortA), ShopSort.from(shopSortB));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "userId=" + userId.toString() + ",shopSortA=" + shopSortA.toString() + ",shopSortB=" + shopSortB.toString();
	}
}
