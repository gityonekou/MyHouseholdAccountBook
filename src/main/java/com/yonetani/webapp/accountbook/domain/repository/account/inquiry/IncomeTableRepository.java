/**
 * 収入テーブル：INCOME_TABLEのデータを登録・更新・参照するリポジトリーです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.account.inquiry;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.TotalAvailableFunds;

/**
 *<pre>
 *  収入テーブル：INCOME_TABLEのデータを登録・更新・参照するリポジトリーです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public interface IncomeTableRepository {

	/**
	 *<pre>
	 * 収入テーブル情報を新規登録します。
	 *</pre>
	 * @param data 新規追加データ
	 * @return 登録されたデータの件数
	 *
	 */
	int add(IncomeItem data);
	
	/**
	 *<pre>
	 * 収入テーブル情報を更新します。
	 *</pre>
	 * @param data 更新データ
	 * @return 更新されたデータの件数
	 *
	 */
	int update(IncomeItem data);
	
	/**
	 *<pre>
	 * 指定した収入情報を論理削除します。
	 * 収入テーブル:INCOME_TABLEの削除フラグ項目(DELETE_FLG)にtureの値を設定することで
	 * 論理的に削除します。
	 * 
	 *</pre>
	 * @param data 更新データ
	 * @return 更新されたデータの件数
	 *
	 */
	int delete(IncomeItem data);
	
	/**
	 *<pre>
	 * ユーザID、対象年月に対応する収入情報を取得します。
	 *</pre>
	 * @param searchQuery 検索条件(ユーザID, 対象年月(yyyyMM))
	 * @return 収入情報のリスト
	 *
	 */
	IncomeItemInquiryList findById(SearchQueryUserIdAndYearMonth searchQuery);
	
	/**
	 *<pre>
	 * 新規の収入コード発番用にユーザID、対象年月に対応する収入情報が何件あるかを取得します。
	 *</pre>
	 * @param searchQuery 検索条件(ユーザID, 対象年月(yyyyMM))
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	int countById(SearchQueryUserIdAndYearMonth searchQuery);
	
	/**
	 *<pre>
	 * ユーザID、対象年月に対応する利用可能資金合計を取得します。
	 *</pre>
	 * @param searchQuery 検索条件(ユーザID, 対象年月(yyyyMM))
	 * @return 検索条件に一致する利用可能資金の合計値
	 *
	 */
	TotalAvailableFunds sumIncomeKingaku(SearchQueryUserIdAndYearMonth searchQuery);
	
}
