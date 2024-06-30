/**
 * 店舗テーブル(BASE)情報を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.adminmenu;

import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopCode;
import com.yonetani.webapp.accountbook.domain.type.account.shop.ShopName;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 店舗テーブル(BASE)情報を表すドメインモデルです
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
public class ShopBase {
	// 店舗コード
	private final ShopCode shopCode;
	// 店舗名
	private final ShopName shopName;
	
	/**
	 *<pre>
	 * 引数の値から支出項目テーブル(BASE)情報ドメインモデルを生成して返します。
	 *</pre>
	 * @param shopCode 店舗コード
	 * @param shopName 店舗名
	 * @return 店舗テーブル(BASE)情報ドメインモデル
	 *
	 */
	public static ShopBase from(String shopCode, String shopName) {
		return new ShopBase(ShopCode.from(shopCode), ShopName.from(shopName));
	}
	
	/**
	 *<pre>
	 * 各データの値をカンマ区切りの1行の文字列として返します。
	 *</pre>
	 * @return
	 *
	 */
	public String toLineString() {
		StringBuilder buff = new StringBuilder(50);
		buff.append(shopCode)
		.append(',')
		.append(shopName);
		return buff.toString();
	}
}
