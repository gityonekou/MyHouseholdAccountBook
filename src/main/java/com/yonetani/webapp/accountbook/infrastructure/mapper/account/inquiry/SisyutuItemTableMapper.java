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

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.SisyutuItemReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndSisyutuItemCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdSearchQueryDto;

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
	 * @return 支出項目テーブルに追加されたデータ件数
	 *
	 */
	@Insert("sql/account/inquiry/SisyutuItemTableInsertSql01.sql")
	public int insert(@Param("dto") SisyutuItemReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 支出項目テーブル:SISYUTU_ITEM_TABLEの情報を指定の支出項目情報で更新します。
	 *</pre>
	 * @param writeDto 支出項目テーブル:SISYUTU_ITEM_TABLE出力情報
	 * @return 支出項目テーブルを更新した件数
	 *
	 */
	@Update("sql/account/inquiry/SisyutuItemTableUpdateSql01.sql")
	public int update(@Param("dto") SisyutuItemReadWriteDto writeDto);

	/**
	 *<pre>
	 * 支出項目テーブル:SISYUTU_ITEM_TABLEの支出項目詳細内容項目の値を更新します。
	 *</pre>
	 * @param writeDto 支出項目テーブル:SISYUTU_ITEM_TABLE出力情報
	 * @return 支出項目テーブルを更新した件数
	 *
	 */
	@Update("sql/account/inquiry/SisyutuItemTableUpdateSql02.sql")
	public int updateSisyutuItemDetailContext(@Param("dto") SisyutuItemReadWriteDto writeDto);
	
	
	/**
	 *<pre>
	 * 支出項目テーブル:SISYUTU_ITEM_TABLEの支出項目表示順項目の値を更新します。
	 *</pre>
	 * @param writeDto 支出項目テーブル:SISYUTU_ITEM_TABLE出力情報
	 * @return 支出項目テーブルを更新した件数
	 *
	 */
	@Update("sql/account/inquiry/SisyutuItemTableUpdateSql03.sql")
	public int updateSisyutuItemSort(@Param("dto") SisyutuItemReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 指定のユーザIDを条件に支出項目テーブル:SISYUTU_ITEM_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID
	 * @return 支出項目テーブル:SISYUTU_ITEM_TABLE参照結果のリスト
	 *
	 */
	@Select("sql/account/inquiry/SisyutuItemTableSelectSql01.sql")
	public List<SisyutuItemReadWriteDto> findById(@Param("dto") UserIdSearchQueryDto search);
	
	/**
	 *<pre>
	 * 指定のユーザID、支出項目コードを条件に支出項目テーブル:SISYUTU_ITEM_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID、支出項目コード
	 * @return 支出項目テーブル:SISYUTU_ITEM_TABLE参照結果
	 *
	 */
	@Select("sql/account/inquiry/SisyutuItemTableSelectSql02.sql")
	public SisyutuItemReadWriteDto findByIdAndSisyutuItemCode(@Param("dto") UserIdAndSisyutuItemCodeSearchQueryDto search);
	
	
	/**
	 *<pre>
	 * 指定のユーザID、親の支出項目コードを条件に支出項目テーブル:SISYUTU_ITEM_TABLEを参照し、
	 * 親の支出項目に属する支出項目情報(リスト)を取得します。
	 *</pre>
	 * @param search 検索条件:ユーザID、支出項目コード
	 * @return 親の支出項目に属する支出項目情報のリスト
	 *
	 */
	@Select("sql/account/inquiry/ParentSisyutuItemMemberNameSelectSql01.sql")
	public List<SisyutuItemReadWriteDto> searchParentSisyutuItemMemberNameList(@Param("dto") UserIdAndSisyutuItemCodeSearchQueryDto search);

	/**
	 *<pre>
	 * 指定のユーザIDに対応する支出項目情報が何件あるかを取得します。
	 *</pre>
	 * @param userId 検索条件:ユーザID
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	@Select("sql/account/inquiry/SisyutuItemTableCountSql01.sql")
	public int countById(@Param("dto") UserIdSearchQueryDto userId);
	
}
