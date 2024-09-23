/**
 * 収支テーブル：INCOME_AND_EXPENDITURE_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/12 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.IncomeAndExpenditureReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearSearchQueryDto;

/**
 *<pre>
 * 収支テーブル：INCOME_AND_EXPENDITURE_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Mapper
public interface IncomeAndExpenditureTableMapper {
	
	/**
	 *<pre>
	 * 収支テーブル：INCOME_AND_EXPENDITURE_TABLEにデータを追加します。
	 *</pre>
	 * @param writeDto 収支テーブル:INCOME_AND_EXPENDITURE_TABLE出力情報
	 * @return 収支テーブルに追加されたデータ件数
	 *
	 */
	@Insert("sql/account/inquiry/IncomeAndExpenditureTableInsertSql01.sql")
	public int insert(@Param("dto") IncomeAndExpenditureReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 収支テーブル：INCOME_AND_EXPENDITURE_TABLEの情報を指定の収支情報で更新します。
	 *</pre>
	 * @param writeDto 収支テーブル:INCOME_AND_EXPENDITURE_TABLE出力情報
	 * @return 収支テーブルを更新した件数
	 *
	 */
	@Update("sql/account/inquiry/IncomeAndExpenditureTableUpdateSql01.sql")
	public int update(@Param("dto") IncomeAndExpenditureReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * ユーザID、対象年で収支テーブルを検索します。
	 *</pre>
	 * @param dto 検索条件:ユーザID、対象年
	 * @return 収支テーブル検索結果
	 *
	 */
	@Select("sql/account/inquiry/IncomeAndExpenditureTableSelectSql01.sql")
	List<IncomeAndExpenditureReadWriteDto> selectUserIdAndYear(@Param("dto") UserIdAndYearSearchQueryDto dto);
	
	/**
	 *<pre>
	 * ユーザID、対象年、対象月で収支テーブルを検索します。
	 *</pre>
	 * @param dto 検索条件:ユーザID、対象年、対象月
	 * @return 収支テーブル検索結果
	 *
	 */
	@Select("sql/account/inquiry/IncomeAndExpenditureTableSelectSql02.sql")
	IncomeAndExpenditureReadWriteDto selectUserIdAndYearMonth(@Param("dto") UserIdAndYearMonthSearchQueryDto dto);
}
