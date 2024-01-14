/**
 * マイ家計簿 各月の収支取得で各表示値をDBから取得するIFを定義したマッパーです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/30 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.SisyutuKingakuAndSisyutuItemReadDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthSearchQueryDto;

/**
 *<pre>
 * マイ家計簿 各月の収支取得で各表示値をDBから取得するIFを定義したマッパーです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Mapper
public interface AccountMonthInquiryMapper {

	/**
	 *<pre>
	 * ユーザID、対象年月で支出金額テーブルと支出アイテムテーブルを検索します。
	 *</pre>
	 * @param dto 検索条件:ユーザID、対象年月
	 * @return 支出金額テーブルと支出アイテムテーブル検索結果
	 *
	 */
	@Select("sql/account/inquiry/AccountMonthInquirySelectSql01.sql")
	List<SisyutuKingakuAndSisyutuItemReadDto> selectExpenditureItem(@Param("dto") UserIdAndYearMonthSearchQueryDto dto);
	
}
