/**
 * 店舗テーブル:SHOP_TABLEのデータ追加・更新・参照検索を行うマッパーです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/13 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.account.shop;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yonetani.webapp.accountbook.infrastructure.dto.account.shop.ShopReadWriteDto;

/**
 *<pre>
 * 店舗テーブル:SHOP_TABLEのデータ追加・更新・参照検索を行うマッパーです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Mapper
public interface ShopTableMapper {
	/**
	 *<pre>
	 * 店舗テーブル:SHOP_TABLEにデータを追加します。
	 *</pre>
	 * @param writeDto 店舗テーブル:SHOP_TABLE出力情報
	 *
	 */
	@Insert("sql/account/shop/ShopTableInsertSql01.sql")
	public void insert(@Param("dto") ShopReadWriteDto writeDto);
}
