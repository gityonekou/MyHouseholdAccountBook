/**
 * 店舗テーブル:SHOP_TABLE読込・出力情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/01/13 : 1.00.00                      新規作成
 * 2026/08/18 : 1.01.00  feature-1.03-dev1   支払方法・銀行口座管理追加対応
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.account.shop;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 店舗テーブル:SHOP_TABLE読込・出力情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ShopReadWriteDto {
	// ユーザID
	private final String userId;
	// 店舗コード
	private final String shopCode;
	// 店舗区分コード
	private final String shopKubunCode;
	// 店舗名
	private final String shopName;
	// 店舗表示順
	private final String shopSort;
	// デフォルト支払方法コード
	private final String defaultPaymentMethodCode;

	/**
	 *<pre>
	 * 引数のパラメータ値をもとにShopReadWriteDtoを生成して返します。
	 *</pre>
	 * @param userId   ユーザID
	 * @param shopCode 店舗コード
	 * @param shopKubunCode 店舗区分コード
	 * @param shopName 店舗名
	 * @param shopSort 表示順
	 * @param defaultPaymentMethodCode デフォルト支払方法コード
	 * @return 店舗テーブル:SHOP_TABLE出力情報
	 *
	 */
	public static ShopReadWriteDto from(String userId, String shopCode, String shopKubunCode, String shopName,
			String shopSort, String defaultPaymentMethodCode) {
		return new ShopReadWriteDto(userId, shopCode, shopKubunCode, shopName, shopSort, defaultPaymentMethodCode);
	}
}
