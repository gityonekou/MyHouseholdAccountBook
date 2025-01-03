/**
 * 支出テーブル：EXPENDITURE_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/08 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.ExpenditureReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthAndSisyutuCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthAndSisyutuItemCodeAndSisyutuKubunSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthAndSisyutuItemCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthSearchQueryDto;

/**
 *<pre>
 * 支出テーブル：EXPENDITURE_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Mapper
public interface ExpenditureTableMapper {
	
	/**
	 *<pre>
	 * 支出テーブル:EXPENDITURE_TABLEにデータを追加します。
	 *</pre>
	 * @param writeDto 支出テーブル:EXPENDITURE_TABLE出力情報
	 * @return 支出テーブルに追加されたデータ件数
	 *
	 */
	@Insert("sql/account/inquiry/ExpenditureTableInsertSql01.sql")
	public int insert(@Param("dto") ExpenditureReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 支出テーブル:EXPENDITURE_TABLEの情報を指定の支出情報で更新します。
	 *</pre>
	 * @param writeDto 支出テーブル:EXPENDITURE_TABLE出力情報
	 * @return 支出テーブルを更新した件数
	 *
	 */
	@Update("sql/account/inquiry/ExpenditureTableUpdateSql01.sql")
	public int update(@Param("dto") ExpenditureReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 指定の支出情報を支出テーブル:EXPENDITURE_TABLEから論理削除します。
	 *</pre>
	 * @param writeDto 論理削除対象の支出情報(支出テーブル:EXPENDITURE_TABLE情報)
	 * @return 支出テーブルを更新した件数
	 *
	 */
	@Update("sql/account/inquiry/ExpenditureTableUpdateSql02.sql")
	public int delete(@Param("dto") ExpenditureReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 指定のユーザID、対象年月を条件に支出テーブル:EXPENDITURE_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID、対象年月
	 * @return 支出テーブル:EXPENDITURE_TABLE参照結果のリスト
	 *
	 */
	@Select("sql/account/inquiry/ExpenditureTableSelectSql01.sql")
	public List<ExpenditureReadWriteDto> findById(@Param("dto") UserIdAndYearMonthSearchQueryDto search);
	
	/**
	 *<pre>
	 * ユニークキー(ユーザID、対象年、対象月、支出コード)を条件に支出テーブルを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID、対象年、対象月、支出コード
	 * @return 支出金額テーブル検索結果
	 *
	 */
	@Select("sql/account/inquiry/ExpenditureTableSelectSql02.sql")
	public ExpenditureReadWriteDto findByUniqueKey(@Param("dto") UserIdAndYearMonthAndSisyutuCodeSearchQueryDto search);
	
	/**
	 *<pre>
	 * 指定のユーザID、対象年月、支出項目コードを条件に支出テーブル:EXPENDITURE_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID、対象年月、支出項目コード
	 * @return 支出テーブル:EXPENDITURE_TABLE参照結果のリスト
	 *
	 */
	@Select("sql/account/inquiry/ExpenditureTableSelectSql03.sql")
	public List<ExpenditureReadWriteDto> findByIdAndSisyutuItemCode(@Param("dto")UserIdAndYearMonthAndSisyutuItemCodeSearchQueryDto search);
	
	/**
	 *<pre>
	 * 指定のユーザID、対象年月、支出項目コード、支出区分を条件に支出テーブル:EXPENDITURE_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID、対象年月、支出項目コード、支出区分
	 * @return 支出テーブル:EXPENDITURE_TABLE参照結果のリスト
	 *
	 */
	@Select("sql/account/inquiry/ExpenditureTableSelectSql04.sql")
	public List<ExpenditureReadWriteDto> findByIdAndSisyutuItemCodeAndSisyutuKubun(@Param("dto")UserIdAndYearMonthAndSisyutuItemCodeAndSisyutuKubunSearchQueryDto search);
	
	/**
	 *<pre>
	 * 指定のユーザID、対象年月に対応する支出情報が何件あるかを取得します。
	 *</pre>
	 * @param search 検索条件:ユーザID、対象年月
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	@Select("sql/account/inquiry/ExpenditureTableCountSql01.sql")
	public int countById(@Param("dto") UserIdAndYearMonthSearchQueryDto search);
	
	/**
	 *<pre>
	 * 指定のユーザID、対象年月に対応する支出金額合計値を取得します。
	 *</pre>
	 * @param search 検索条件:ユーザID、対象年月
	 * @return 支出金額合計値
	 *
	 */
	@Select("sql/account/inquiry/ExpenditureTableSumSql01.sql")
	public BigDecimal sumExpenditureKingaku(@Param("dto") UserIdAndYearMonthSearchQueryDto search);
	
}
