/**
 * 支出項目テーブル:SISYUTU_ITEM_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/01/13 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.SisyutuItemReadWriteDto;

/**
 *<pre>
 * 支出項目テーブル:SISYUTU_ITEM_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Mapper
public interface SisyutuItemTableMapper {
	/**
	 *<pre>
	 * 支出項目テーブル:SISYUTU_ITEM_TABLEにデータを追加します。
	 *</pre>
	 * @param writeDto 支出項目テーブル:SISYUTU_ITEM_TABLE出力情報
	 *
	 */
	@Insert("sql/account/inquiry/SisyutuItemTableInsertSql01.sql")
	public void insert(@Param("dto") SisyutuItemReadWriteDto writeDto);
}
