/**
 * 支出金額テーブル：SISYUTU_KINGAKU_TABLEのデータを登録・更新・参照するリポジトリーです
 * 各月の収支画面の支出項目毎の支出一覧情報と年間収支の支出項目レベル１毎の支出一覧情報取得も
 * このリポジトリーで定義します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/10/06 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.account.inquiry;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.AccountMonthInquiryExpenditureItemList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.AccountYearMeisaiInquiryList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuKingakuItem;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.SisyutuKingakuItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYear;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonthAndSisyutuItemCode;

/**
 *<pre>
 * 支出金額テーブル：SISYUTU_KINGAKU_TABLEのデータを登録・更新・参照するリポジトリーです
 * 各月の収支画面の支出項目毎の支出一覧情報と年間収支の支出項目レベル１毎の支出一覧情報取得も
 * このリポジトリーで定義します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public interface SisyutuKingakuTableRepository {
	
	/**
	 *<pre>
	 * 支出金額テーブル情報を新規登録します。
	 *</pre>
	 * @param data 新規追加データ
	 * @return 登録されたデータの件数
	 *
	 */
	int add(SisyutuKingakuItem data);
	
	/**
	 *<pre>
	 * 支出金額テーブル情報を更新します。
	 *</pre>
	 * @param data 更新データ
	 * @return 更新されたデータの件数
	 *
	 */
	int update(SisyutuKingakuItem data);
	
	/**
	 *<pre>
	 * 検索条件(ユニークキー)に一致する支出金額テーブル情報を取得します。
	 *</pre>
	 * @param search 検索条件(ユーザID, 年月,支出項目コード)
	 * @return 支出金額情報
	 *
	 */
	SisyutuKingakuItem findByUniqueKey(SearchQueryUserIdAndYearMonthAndSisyutuItemCode search);
	
	/**
	 *<pre>
	 * 検索条件に一致する支出金額テーブル情報のリストを取得します。
	 *</pre>
	 * @param search 検索条件(ユーザID, 年月)
	 * @return 支出金額情報のリスト
	 *
	 */
	SisyutuKingakuItemInquiryList findById(SearchQueryUserIdAndYearMonth search);
	
	/**
	 *<pre>
	 * 指定月に対応する支出金額情報を取得します。
	 *</pre>
	 * @param searchQuery 検索条件(ユーザID, 年月)
	 * @return 月毎の支出金額情報取得結果
	 *
	 */
	AccountMonthInquiryExpenditureItemList select(SearchQueryUserIdAndYearMonth searchQuery);
	
	/**
	 *<pre>
	 * 指定年度に対応する収支(明細)のリストを取得します。
	 *</pre>
	 * @param searchQuery 検索条件(ユーザID, 年度)
	 * @return 指定年度の収支(明細)のリスト結果
	 *
	 */
	AccountYearMeisaiInquiryList select(SearchQueryUserIdAndYear searchQuery);
}
