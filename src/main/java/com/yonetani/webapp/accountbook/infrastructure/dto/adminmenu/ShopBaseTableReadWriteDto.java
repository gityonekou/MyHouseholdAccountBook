/**
 * 店舗テーブル(BASE):SHOP_BASE_TABLE読込・出力情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.dto.adminmenu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 店舗テーブル(BASE):SHOP_BASE_TABLE読込・出力情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ShopBaseTableReadWriteDto {
	// 店舗コード
	private final String shopCode;
	// 店舗名
	private final String shopName;
	
	/**
	 *<pre>
	 * 引数のパラメータ値をもとにShopBaseTableReadWriteDtoを生成して返します。
	 *</pre>
	 * @param shopCode 店舗コード
	 * @param shopName 店舗名
	 * @return 店舗テーブル(BASE):SHOP_BASE_TABLE読込・出力情報
	 *
	 */
	public static ShopBaseTableReadWriteDto from(String shopCode, String shopName) {
		return new ShopBaseTableReadWriteDto(shopCode, shopName);
	}
}
