/**
 * マイ家計簿 指定年度の年間収支(明細)情報取得で各表示値をDBから取得するIFを定義したマッパーです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/17 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.account.inquiry;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.yonetani.webapp.accountbook.infrastructure.dto.account.inquiry.AccountYearMeisaiInquiryReadDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndYearMonthSearchQueryDto;

/**
 *<pre>
 * マイ家計簿 指定年度の年間収支(明細)情報取得で各表示値をDBから取得するIFを定義したマッパーです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Mapper
public interface AccountYearMeisaiInquiryMapper {

	/**
	 *<pre>
	 * ユーザID、対象年で収支テーブルと支出金額テーブルを検索します。
	 *</pre>
	 * @param dto 検索条件:ユーザID、対象年、対象月
	 * @return 収支テーブルと支出金額テーブル検索結果
	 *
	 */
	@Select("sql/account/inquiry/AccountYearMeisaiInquirySelectSql01.sql")
	AccountYearMeisaiInquiryReadDto select(@Param("dto") UserIdAndYearMonthSearchQueryDto dto);
}
