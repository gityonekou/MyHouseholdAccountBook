/**
 * 支出項目テーブル(BASE):SISYUTU_ITEM_BASE_TABLEのデータ追加と全件検索を行うマッパーです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/03 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.adminmenu;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.yonetani.webapp.accountbook.infrastructure.dto.adminmenu.SisyutuItemBaseTableReadWriteDto;

/**
 *<pre>
 * 支出項目テーブル(BASE):SISYUTU_ITEM_BASE_TABLEのデータ追加と全件検索を行うマッパーです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Mapper
public interface SisyutuItemBaseTableMapper {
	
	/**
	 *<pre>
	 * 支出項目テーブル(BASE):SISYUTU_ITEM_BASE_TABLEにデータを追加します。
	 *</pre>
	 * @param writeDto 支出項目テーブル(BASE):SISYUTU_ITEM_BASE_TABLE出力情報
	 *
	 */
	@Insert("sql/adminmenu/SisyutuItemBaseTableInsertSql01.sql")
	public void insert(@Param("dto") SisyutuItemBaseTableReadWriteDto writeDto);

	/**
	 *<pre>
	 * 支出項目テーブル(BASE)情報の全データを取得します。
	 *</pre>
	 * @return 支出項目テーブル(BASE)情報のリスト
	 *
	 */
	@Select("sql/adminmenu/SisyutuItemBaseTableSelectSql01.sql")
	public List<SisyutuItemBaseTableReadWriteDto> findAll();
	
	/**
	 *<pre>
	 * 支出項目テーブル(BASE)情報の全件数を取得します。
	 *</pre>
	 * @return 支出項目テーブル(BASE)情報の全件数
	 *
	 */
	@Select("sql/adminmenu/SisyutuItemBaseTableCountSql01.sql")
	public int countAll();
}
