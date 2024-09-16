/**
 * 収入テーブル：INCOME_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/08 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.IncomeReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthSearchQueryDto;

/**
 *<pre>
 * 収入テーブル：INCOME_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Mapper
public interface IncomeTableMapper {

	/**
	 *<pre>
	 * 収入テーブル:INCOME_TABLEにデータを追加します。
	 *</pre>
	 * @param writeDto 収入テーブル:INCOME_TABLE出力情報
	 * @return 収入テーブルに追加されたデータ件数
	 *
	 */
	@Insert("sql/account/inquiry/IncomeTableInsertSql01.sql")
	public int insert(@Param("dto") IncomeReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 収入テーブル:INCOME_TABLEの情報を指定の収入情報で更新します。
	 *</pre>
	 * @param writeDto 収入テーブル:INCOME_TABLE出力情報
	 * @return 収入テーブルを更新した件数
	 *
	 */
	@Update("sql/account/inquiry/IncomeTableUpdateSql01.sql")
	public int update(@Param("dto") IncomeReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 指定の収入情報を収入テーブル:INCOME_TABLEから論理削除します。
	 *</pre>
	 * @param writeDto 論理削除対象の収入情報(収入テーブル:INCOME_TABLE情報)
	 * @return 収入テーブルを更新した件数
	 *
	 */
	@Update("sql/account/inquiry/IncomeTableUpdateSql02.sql")
	public int delete(@Param("dto") IncomeReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 指定のユーザID、対象年月を条件に収入テーブル:INCOME_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID、対象年月
	 * @return 収入テーブル:INCOME_TABLE参照結果のリスト
	 *
	 */
	@Select("sql/account/inquiry/IncomeTableSelectSql01.sql")
	public List<IncomeReadWriteDto> findById(@Param("dto") UserIdAndYearMonthSearchQueryDto search);

	/**
	 *<pre>
	 * 指定のユーザID、対象年月に対応する収入情報が何件あるかを取得します。
	 *</pre>
	 * @param search 検索条件:ユーザID、対象年月
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	@Select("sql/account/inquiry/IncomeTableCountSql01.sql")
	public int countById(@Param("dto") UserIdAndYearMonthSearchQueryDto search);
	
}
