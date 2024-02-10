/**
 * マイ家計簿 指定年度の年間収支(明細)情報取得を行うリポジトリーです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/12 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.account.inquiry;

import com.yonetani.webapp.accountbook.domain.model.account.inquiry.AccountYearMeisaiInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYear;

/**
 *<pre>
 * 指定年度の年間収支(明細)情報取得を行うリポジトリーです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
public interface AccountYearMeisaiInquiryRepository {

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
