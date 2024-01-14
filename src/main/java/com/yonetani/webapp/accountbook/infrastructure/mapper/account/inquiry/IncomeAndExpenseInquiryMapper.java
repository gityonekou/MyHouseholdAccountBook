/**
 * マイ家計簿 収支テーブルを検索するマッパーです。
 * 以下DBアクセスを提供します。
 * ・指定年度の年間収支(マージ)情報取得
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/12 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.IncomeAndExpenseReadDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearSearchQueryDto;

/**
 *<pre>
 * マイ家計簿 収支テーブルを検索するマッパーです。
 * 以下DBアクセスを提供します。
 * ・指定年度の年間収支(マージ)情報取得
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Mapper
public interface IncomeAndExpenseInquiryMapper {
	
	/**
	 *<pre>
	 * ユーザID、対象年で収支テーブルを検索します。
	 *</pre>
	 * @param dto 検索条件:ユーザID、対象年
	 * @return 収支テーブル検索結果
	 *
	 */
	@Select("sql/account/inquiry/IncomeAndExpenseInquirySelectSql01.sql")
	List<IncomeAndExpenseReadDto> selectUserIdAndYear(@Param("dto") UserIdAndYearSearchQueryDto dto);
	
	/**
	 *<pre>
	 * ユーザID、対象年、対象月で収支テーブルを検索します。
	 *</pre>
	 * @param dto 検索条件:ユーザID、対象年、対象月
	 * @return 収支テーブル検索結果
	 *
	 */
	@Select("sql/account/inquiry/IncomeAndExpenseInquirySelectSql02.sql")
	IncomeAndExpenseReadDto selectUserIdAndYearMonth(@Param("dto") UserIdAndYearMonthSearchQueryDto dto);
}
