/**
 * 店舗テーブル情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/10 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.shop;

import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopCode;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopKubunCode;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopName;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopSort;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 店舗テーブル情報を表すドメインモデルです
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
public class Shop {
	// ユーザID
	private final UserId userId;
	// 店舗コード
	private final ShopCode shopCode;
	// 店舗区分コード
	private final ShopKubunCode shopKubunCode;
	// 店舗名
	private final ShopName shopName;
	// 店舗表示順
	private final ShopSort shopSort;
	
	/**
	 *<pre>
	 * 引数の値から店舗テーブル情報を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param shopCode 店舗コード
	 * @param shopKubunCode 店舗区分コード
	 * @param shopName 店舗名
	 * @param shopSort 店舗表示順
	 * @return 店舗テーブル情報を表すドメインモデル
	 *
	 */
	public static Shop from(String userId, String shopCode, String shopKubunCode, String shopName, String shopSort) {
		return new Shop(
				UserId.from(userId),
				ShopCode.from(shopCode),
				ShopKubunCode.from(shopKubunCode),
				ShopName.from(shopName),
				ShopSort.from(shopSort));
	}
}
