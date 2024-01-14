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
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopName;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
public class Shop {
	// ユーザID
	private final UserId userId;
	// 店舗コード
	private final ShopCode shopCode;
	// 店舗名
	private final ShopName shopName;
	
	/**
	 *<pre>
	 * 引数の値から店舗テーブル情報を表すドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param shopCode 店舗コード
	 * @param shopName 店舗名
	 * @return 店舗テーブル情報を表すドメインモデル
	 *
	 */
	public static Shop from(String userId, String shopCode, String shopName) {
		return new Shop(
				UserId.from(userId),
				ShopCode.from(shopCode),
				ShopName.from(shopName));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder(100);
		buff.append("userId:")
		.append(userId)
		.append(",shopCode:")
		.append(shopCode)
		.append(",shopName:")
		.append(shopName);
		return buff.toString();
	}
}
