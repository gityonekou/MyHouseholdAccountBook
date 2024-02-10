/**
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

import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopCode;
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
public class SearchQueryUserIdAndShopSortAB {
	// ユーザID
	private final UserId userId;
	// 店舗コード
	private final ShopCode shopCodeA;
	// 店舗コード
	private final ShopCode shopCodeB;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・店舗コード
	 *</pre>
	 * @param userId ユーザID
	 * @param shopCodeA 店舗コードA
	 * @param shopCodeB 店舗コードB
	 * @return 検索条件(ユーザID, 店舗コードA, 店舗コードA)
	 *
	 */
	public static SearchQueryUserIdAndShopSortAB from(String userId, String shopCodeA, String shopCodeB) {
		return new SearchQueryUserIdAndShopSortAB(UserId.from(userId), ShopCode.from(shopCodeA), ShopCode.from(shopCodeB));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "userId=" + userId.toString() + ",shopCodeA=" + shopCodeA.toString() + ",shopCodeB=" + shopCodeB.toString();
	}
}
