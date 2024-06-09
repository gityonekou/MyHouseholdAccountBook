/**
 * 固定費テーブル:FIXED_COST_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/04 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.account.fixedcost;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yonetani.webapp.accountbook.infrastructure.dto.account.fixedcost.FixedCostInquiryReadDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.fixedcost.FixedCostReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndFixedCostCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdSearchQueryDto;

/**
 *<pre>
 *  固定費テーブル:FIXED_COST_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Mapper
public interface FixedCostTableMapper {
	
	/**
	 *<pre>
	 * 固定費テーブル:FIXED_COST_TABLEにデータを追加します。
	 *</pre>
	 * @param writeDto 固定費テーブル:FIXED_COST_TABLE出力情報
	 * @return 固定費テーブルに追加されたデータ件数
	 *
	 */
	@Insert("sql/account/fixedcost/FixedCostTableInsertSql01.sql")
	int insert(@Param("dto") FixedCostReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 固定費テーブル:FIXED_COST_TABLEの情報を指定の固定費情報で更新します。
	 *</pre>
	 * @param writeDto 固定費テーブル:FIXED_COST_TABLE出力情報
	 * @return 固定費テーブルを更新した件数
	 *
	 */
	@Update("sql/account/fixedcost/FixedCostTableUpdateSql01.sql")
	int update(@Param("dto") FixedCostReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 指定のユーザID、固定費コードを条件に固定費テーブル:FIXED_COST_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID、固定費コード
	 * @return 固定費テーブル:FIXED_COST_TABLE参照結果
	 *
	 */
	@Select("sql/account/fixedcost/FixedCostTableSelectSql01.sql")
	FixedCostReadWriteDto findByIdAndFixedCostCode(@Param("dto") UserIdAndFixedCostCodeSearchQueryDto search);
	
	/**
	 *<pre>
	 * 固定費テーブル:FIXED_COST_TABLE、支出項目テーブル:SISYUTU_ITEM_TABLEから指定のユーザIDを条件に
	 * 固定費情報を参照します。
	 *</pre>
	 * @param userId 検索条件:ユーザID　
	 * @return 固定費情報参照結果のリスト
	 *
	 */
	@Select("sql/account/fixedcost/FixedCostInquirySelectSql01.sql")
	List<FixedCostInquiryReadDto> findById(@Param("dto") UserIdSearchQueryDto userId);
	
	/**
	 *<pre>
	 * 指定のユーザIDに対応する固定費情報が何件あるかを取得します。
	 *</pre>
	 * @param userId 検索条件:ユーザID
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	@Select("sql/account/fixedcost/FixedCostTableCountSql01.sql")
	int countById(@Param("dto") UserIdSearchQueryDto userId);
	
}
