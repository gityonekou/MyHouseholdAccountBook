/**
 * マイ家計簿 収支情報取得を行うリポジトリーです。
 * 以下IFを提供します。
 * ・指定年度の年間収支(マージ)情報取得
 * 
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/12 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.account.inquiry;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditureInquiryItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditureInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYear;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;

/**
 *<pre>
 * マイ家計簿 収支情報取得を行うリポジトリーです。
 * 以下IFを提供します。
 * ・指定年度の年間収支(マージ)情報取得
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public interface IncomeAndExpenditureInquiryRepository {
	
	/**
	 *<pre>
	 * 指定年度に対応する収支(マージ)のリストを取得します。
	 *</pre>
	 * @param searchQuery 検索条件(ユーザID, 年度)
	 * @return 指定年度の収支(マージ)のリスト結果
	 *
	 */
	IncomeAndExpenditureInquiryList select(SearchQueryUserIdAndYear searchQuery);
	
	/**
	 *<pre>
	 * 指定年月度に対応する収支情報を取得します。
	 *</pre>
	 * @param searchQuery 検索条件(ユーザID, 年月度)
	 * @return 収支情報
	 *
	 */
	IncomeAndExpenditureInquiryItem select(SearchQueryUserIdAndYearMonth searchQuery);
	
}
