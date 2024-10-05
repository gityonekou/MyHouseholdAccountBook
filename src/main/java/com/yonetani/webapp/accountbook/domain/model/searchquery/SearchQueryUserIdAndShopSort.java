/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・店舗表示順
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/02/05 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopSort;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・店舗表示順
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
public class SearchQueryUserIdAndShopSort {
	// ユーザID
	private final UserId userId;
	// 店舗表示順
	private final ShopSort shopSort;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・店舗表示順
	 *</pre>
	 * @param userId ユーザID
	 * @param shopSort 店舗表示順
	 * @return 検索条件(ユーザID, 店舗表示順)
	 *
	 */
	public static SearchQueryUserIdAndShopSort from(UserId userId, ShopSort shopSort) {
		return new SearchQueryUserIdAndShopSort(userId, shopSort);
	}
}
