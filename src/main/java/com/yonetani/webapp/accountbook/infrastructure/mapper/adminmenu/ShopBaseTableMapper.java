/**
 * 店舗テーブル(BASE):SHOP_BASE_TABLEのデータ追加と全件検索を行うマッパーです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.adminmenu;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.yonetani.webapp.accountbook.infrastructure.dto.adminmenu.ShopBaseTableReadWriteDto;

/**
 *<pre>
 * 店舗テーブル(BASE):SHOP_BASE_TABLEのデータ追加と全件検索を行うマッパーです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Mapper
public interface ShopBaseTableMapper {
	
	/**
	 *<pre>
	 * 店舗テーブル(BASE):SHOP_BASE_TABLEにデータを追加します。
	 *</pre>
	 * @param writeDto 店舗テーブル(BASE):SHOP_BASE_TABLE出力情報
	 * @return 店舗テーブル(BASE)に追加されたデータ件数
	 *
	 */
	@Insert("sql/adminmenu/ShopBaseTableInsertSql01.sql")
	public int insert(@Param("dto") ShopBaseTableReadWriteDto writeDto);

	/**
	 *<pre>
	 * 店舗テーブル(BASE)情報の全データを取得します。
	 *</pre>
	 * @return 店舗テーブル(BASE)情報のリスト
	 *
	 */
	@Select("sql/adminmenu/ShopBaseTableSelectSql01.sql")
	public List<ShopBaseTableReadWriteDto> findAll();
	
	/**
	 *<pre>
	 * 店舗テーブル(BASE)情報の全件数を取得します。
	 *</pre>
	 * @return 店舗テーブル(BASE)情報の全件数
	 *
	 */
	@Select("sql/adminmenu/ShopBaseTableCountSql01.sql")
	public int countAll();
	
}
