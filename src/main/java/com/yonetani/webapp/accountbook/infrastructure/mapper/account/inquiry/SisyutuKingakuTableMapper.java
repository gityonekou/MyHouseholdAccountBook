/**
 * 支出金額テーブル：SISYUTU_KINGAKU_TABLEのデータ追加・更新・参照を行うマッパーです
 * 各月の収支画面の支出項目毎の支出一覧情報と年間収支の支出項目レベル１毎の支出一覧情報取得も
 * このマッパーで定義します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/30 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.AccountYearMeisaiInquiryReadDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.SisyutuKingakuAndSisyutuItemReadDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.SisyutuKingakuReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthAndSisyutuItemCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthSearchQueryDto;

/**
 *<pre>
 * 支出金額テーブル：SISYUTU_KINGAKU_TABLEのデータ追加・更新・参照を行うマッパーです
 * 各月の収支画面の支出項目毎の支出一覧情報と年間収支の支出項目レベル１毎の支出一覧情報取得も
 * このマッパーで定義します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Mapper
public interface SisyutuKingakuTableMapper {

	/**
	 *<pre>
	 * 支出金額テーブル：SISYUTU_KINGAKU_TABLEにデータを追加します。
	 *</pre>
	 * @param writeDto 支出金額テーブル:SISYUTU_KINGAKU_TABLE出力情報
	 * @return 支出金額テーブルに追加されたデータ件数
	 *
	 */
	@Insert("sql/account/inquiry/SisyutuKingakuTableInsertSql01.sql")
	public int insert(@Param("dto") SisyutuKingakuReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * 支出金額テーブル：SISYUTU_KINGAKU_TABLEの情報を指定の支出金額情報で更新します。
	 *</pre>
	 * @param writeDto 支出金額テーブル:SISYUTU_KINGAKU_TABLE出力情報
	 * @return 支出金額テーブルを更新した件数
	 *
	 */
	@Update("sql/account/inquiry/SisyutuKingakuTableUpdateSql01.sql")
	public int update(@Param("dto") SisyutuKingakuReadWriteDto writeDto);
	
	/**
	 *<pre>
	 * ユニークキー(ユーザID、対象年、対象月、支出項目ID)を条件に支出金額テーブルを検索します。
	 *</pre>
	 * @param dto 検索条件:ユーザID、対象年、対象月、支出項目ID
	 * @return 支出金額テーブル検索結果
	 *
	 */
	@Select("sql/account/inquiry/SisyutuKingakuTableSelectSql01.sql")
	public SisyutuKingakuReadWriteDto findByUniqueKey(@Param("dto") UserIdAndYearMonthAndSisyutuItemCodeSearchQueryDto dto);
	
	/**
	 *<pre>
	 * ユーザID、対象年、対象月を条件に支出金額テーブルを検索します。
	 *</pre>
	 * @param dto 検索条件:ユーザID、対象年、対象月
	 * @return 支出金額テーブル検索結果のリスト
	 *
	 */
	@Select("sql/account/inquiry/SisyutuKingakuTableSelectSql02.sql")
	public List<SisyutuKingakuReadWriteDto> findById(@Param("dto") UserIdAndYearMonthSearchQueryDto dto);
	
	/**
	 *<pre>
	 * 支出金額テーブルと支出アイテムテーブルから該当月の支出項目一覧情報を検索します。
	 *</pre>
	 * @param dto 検索条件:ユーザID、対象年月
	 * @return 該当月の支出項目一覧情報検索結果
	 *
	 */
	@Select("sql/account/inquiry/SisyutuKingakuMonthInquirySelectSql01.sql")
	public List<SisyutuKingakuAndSisyutuItemReadDto> selectMonthSisyutuKingakuList(@Param("dto") UserIdAndYearMonthSearchQueryDto dto);
	
	
	/**
	 *<pre>
	 * 収支テーブルと支出金額テーブルから該当年の支出項目レベル１毎の支出一覧情報を検索します。
	 *</pre>
	 * @param dto 検索条件:ユーザID、対象年、対象月
	 * @return 支出項目レベル１毎の支出一覧情報検索結果
	 *
	 */
	@Select("sql/account/inquiry/SisyutuKingakuYearInquirySelectSql01.sql")
	public AccountYearMeisaiInquiryReadDto selectYearSisyutuKingakuList(@Param("dto") UserIdAndYearMonthSearchQueryDto dto);
	
}
