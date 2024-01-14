/**
 * 店舗テーブル(BASE)情報の取得結果(リスト情報)の値を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.adminmenu;

import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 店舗テーブル(BASE)情報の取得結果(リスト情報)の値を表すドメインモデルです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ShopBaseList {

	// 店舗テーブル(BASE)情報のリスト
	private final List<ShopBase> values;
	
	/**
	 *<pre>
	 * 店舗テーブル(BASE)情報(ドメインモデル)のリストからShopBaseListのドメインモデルを生成して返します。
	 *</pre>
	 * @param values 店舗テーブル(BASE)情報のリスト
	 * @return ShopBaseListのドメインモデル
	 *
	 */
	public static ShopBaseList from(List<ShopBase> values) {
		if(CollectionUtils.isEmpty(values)) {
			return new ShopBaseList(Collections.emptyList());
		} else {
			return new ShopBaseList(values);
		}
	}
}
