/**
 * マイ家計簿 指定月の支出金額情報取得を行うリポジトリーです。
 * 以下のIFを提供します。
 * ・指定ユーザ、指定月の支出金額情報取得
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/09/24 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.account.inquiry;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.AccountMonthInquiryExpenditureItemList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;

/**
 *<pre>
 * マイ家計簿 指定月の支出金額情報取得を行うリポジトリーです。
 * 以下のIFを提供します。
 * ・指定ユーザ、指定月の支出金額情報取得
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public interface AccountMonthInquiryRepository {
	
	/**
	 *<pre>
	 * 指定月に対応する支出金額情報を取得します。
	 *</pre>
	 * @param searchQuery 検索条件(ユーザID, 年月)
	 * @return 月毎の支出金額情報取得結果
	 *
	 */
	AccountMonthInquiryExpenditureItemList selectExpenditureItem(SearchQueryUserIdAndYearMonth searchQuery);

}
