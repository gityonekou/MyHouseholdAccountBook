/**
 * 買い物登録情報テーブル:SHOPPING_REGIST_TABLEのデータを登録・更新・参照するリポジトリーです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/11/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.account.shoppingregist;

import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.ShoppingRegist;
import com.yonetani.webapp.accountbook.domain.model.account.shoppingregist.SimpleShoppingRegistItemInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonthAndShoppingRegistCode;

/**
 *<pre>
 * 買い物登録情報テーブル:SHOPPING_REGIST_TABLEのデータを登録・更新・参照するリポジトリーです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public interface ShoppingRegistTableRepository {
	
	/**
	 *<pre>
	 * 買い物登録情報を新規登録します。
	 *</pre>
	 * @param data 新規追加データ
	 * @return 登録されたデータの件数
	 *
	 */
	int add(ShoppingRegist data);
	
	/**
	 *<pre>
	 * 買い物登録情報を更新します。
	 *</pre>
	 * @param data 更新データ
	 * @return 更新されたデータの件数
	 *
	 */
	int update(ShoppingRegist data);
	
	
	/**
	 *<pre>
	 * 検索条件(ユニークキー)に一致する買い物登録情報を取得します。
	 *</pre>
	 * @param search 検索条件(ユーザID, 年月 ,買い物登録コード)
	 * @return 買い物登録情報
	 *
	 */
	ShoppingRegist findByUniqueKey(SearchQueryUserIdAndYearMonthAndShoppingRegistCode search);
	
	/**
	 *<pre>
	 * 検索条件に一致する買い物登録情報を取得します。
	 *</pre>
	 * @param search 検索条件(ユーザID, 年月)
	 * @return 買い物登録情報の検索結果
	 *
	 */
	SimpleShoppingRegistItemInquiryList findById(SearchQueryUserIdAndYearMonth search);
	
	/**
	 *<pre>
	 * 新規の買い物登録コード発番用に条件に一致する買い物登録情報が何件あるかを取得します。
	 *</pre>
	 * @param search 検索条件(ユーザID, 年月)
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	int countById(SearchQueryUserIdAndYearMonth search);
	
}
