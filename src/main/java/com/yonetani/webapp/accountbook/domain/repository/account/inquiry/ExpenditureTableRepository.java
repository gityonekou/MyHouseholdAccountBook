/**
 * 支出テーブル：EXPENDITURE_TABLEのデータを登録・更新・参照するリポジトリーです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/09/07 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.account.inquiry;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.ExpenditureItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.ExpenditureItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonthAndSisyutuCode;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingakuTotalAmount;

/**
 *<pre>
 * 支出テーブル：EXPENDITURE_TABLEのデータを登録・更新・参照するリポジトリーです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public interface ExpenditureTableRepository {
	
	/**
	 *<pre>
	 * 支出テーブル情報を新規登録します。
	 *</pre>
	 * @param data 新規追加データ
	 * @return 登録されたデータの件数
	 *
	 */
	int add(ExpenditureItem data);
	
	/**
	 *<pre>
	 * 支出テーブル情報を更新します。
	 *</pre>
	 * @param data 更新データ
	 * @return 更新されたデータの件数
	 *
	 */
	int update(ExpenditureItem data);
	
	/**
	 *<pre>
	 * 指定した支出情報を論理削除します。
	 * 支出テーブル:EXPENDITURE_TABLEの削除フラグ項目(DELETE_FLG)にtureの値を設定することで
	 * 論理的に削除します。
	 * 
	 *</pre>
	 * @param data 更新データ
	 * @return 更新されたデータの件数
	 *
	 */
	int delete(ExpenditureItem data);
	
	/**
	 *<pre>
	 * 検索条件に一致する支出情報を取得します。
	 *</pre>
	 * @param searchQuery 検索条件(ユーザID, 年月,支出コード)
	 * @return 支出情報
	 *
	 */
	ExpenditureItem findByUniqueKey(SearchQueryUserIdAndYearMonthAndSisyutuCode searchQuery);
	
	/**
	 *<pre>
	 * 検索条件に一致する支出情報のリストを取得します。
	 *</pre>
	 * @param searchQuery 検索条件(ユーザID, 対象年月(yyyyMM))
	 * @return 支出情報のリスト
	 *
	 */
	ExpenditureItemInquiryList findById(SearchQueryUserIdAndYearMonth searchQuery);
	
	/**
	 *<pre>
	 * 新規の支出コード発番用にユーザID、対象年月に対応する支出情報が何件あるかを取得します。
	 *</pre>
	 * @param searchQuery 検索条件(ユーザID, 対象年月(yyyyMM))
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	int countById(SearchQueryUserIdAndYearMonth searchQuery);
	
	/**
	 *<pre>
	 * ユーザID、対象年月に対応する支出金額の合計値を取得します。
	 *</pre>
	 * @param searchQuery 検索条件(ユーザID, 対象年月(yyyyMM))
	 * @return 検索条件に一致する支出金額の合計値
	 *
	 */
	SisyutuKingakuTotalAmount sumExpenditureKingaku(SearchQueryUserIdAndYearMonth searchQuery);
}
