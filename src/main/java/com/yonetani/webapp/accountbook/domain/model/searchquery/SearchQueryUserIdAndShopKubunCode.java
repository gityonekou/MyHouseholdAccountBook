/**
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・店舗区分コード
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/16 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.searchquery;

import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopKubunCode;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 以下の照会条件の値を表すドメインモデルです。
 * ・ユーザID
 * ・店舗区分コード
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
public class SearchQueryUserIdAndShopKubunCode {
	// ユーザID
	private final UserId userId;
	// 店舗区分コード
	private final ShopKubunCode shopKubunCode;
	
	/**
	 *<pre>
	 * 以下の照会条件の値を表すドメインモデルを生成します。
	 * ・ユーザID
	 * ・店舗区分コード
	 *</pre>
	 * @param userId ユーザID
	 * @param shopKubunCode 店舗区分コード
	 * @return 検索条件(ユーザID, 店舗区分コード)
	 *
	 */
	public static SearchQueryUserIdAndShopKubunCode from(UserId userId, ShopKubunCode shopKubunCode) {
		return new SearchQueryUserIdAndShopKubunCode(userId, shopKubunCode);
	}
}
