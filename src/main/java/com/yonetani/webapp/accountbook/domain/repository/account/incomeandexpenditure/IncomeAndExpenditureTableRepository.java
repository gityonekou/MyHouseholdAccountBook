/**
 * 収支テーブル：INCOME_AND_EXPENDITURE_TABLEのデータを登録・更新・参照するリポジトリーです
 * 
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/12 : 1.00.00  新規作成
 * 2026/03/20 : 1.01.00  リファクタリング対応(DDD適応)
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.account.incomeandexpenditure;

import com.yonetani.webapp.accountbook.domain.model.account.incomeandexpenditure.IncomeAndExpenditure;
import com.yonetani.webapp.accountbook.domain.model.account.incomeandexpenditure.IncomeAndExpenditureInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYear;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;

/**
 *<pre>
 * 収支テーブル：INCOME_AND_EXPENDITURE_TABLEのデータを登録・更新・参照するリポジトリーです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
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
	int add(IncomeAndExpenditure data);
	
	/**
	 *<pre>
	 * 収支テーブル情報を更新します。
	 *</pre>
	 * @param data 更新データ
	 * @return 更新されたデータの件数
	 *
	 */
	int update(IncomeAndExpenditure data);
	
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
	 * 指定年月度に対応する収支集約を取得します。
	 * データが存在しない場合は空の集約を返します。
	 *</pre>
	 * @param searchQuery 検索条件(ユーザID, 年月度)
	 * @return 収支集約（データなしの場合は空の集約）
	 *
	 */
	IncomeAndExpenditure findByPrimaryKey(SearchQueryUserIdAndYearMonth searchQuery);

}
