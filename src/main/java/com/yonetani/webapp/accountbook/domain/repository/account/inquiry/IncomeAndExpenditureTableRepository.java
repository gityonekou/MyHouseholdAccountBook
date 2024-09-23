/**
 * 収支テーブル：INCOME_AND_EXPENDITURE_TABLEのデータを登録・更新・参照するリポジトリーです
 * 
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/12 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.account.inquiry;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditureInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditureItem;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYear;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;

/**
 *<pre>
 * 収支テーブル：INCOME_AND_EXPENDITURE_TABLEのデータを登録・更新・参照するリポジトリーです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public interface IncomeAndExpenditureTableRepository {
	
	/**
	 *<pre>
	 * 収支テーブル情報を新規登録します。
	 *</pre>
	 * @param data 新規追加データ
	 * @return 登録されたデータの件数
	 *
	 */
	int add(IncomeAndExpenditureItem data);
	
	/**
	 *<pre>
	 * 収支テーブル情報を更新します。
	 *</pre>
	 * @param data 更新データ
	 * @return 更新されたデータの件数
	 *
	 */
	int update(IncomeAndExpenditureItem data);
	
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
	IncomeAndExpenditureItem select(SearchQueryUserIdAndYearMonth searchQuery);
	
}
