/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・店舗コード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/02/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopCode;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 以下の照会条件の値を表すドメインモデルです。
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
@ToString
public class SearchQueryUserIdAndShopCode {
	// ユーザID
	private final UserId userId;
	// 店舗コード
	private final ShopCode shopCode;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・店舗コード
	 *</pre>
	 * @param userId ユーザID
	 * @param shopCode 店舗コード
	 * @return 検索条件(ユーザID, 店舗コード)
	 *
	 */
	public static SearchQueryUserIdAndShopCode from(UserId userId, ShopCode shopCode) {
		return new SearchQueryUserIdAndShopCode(userId, shopCode);
	}
}
