/**
 * 銀行口座テーブル:BANK_ACCOUNT_TABLEのデータを登録・更新・参照するリポジトリーです
 * 銀行口座テーブル:BANK_ACCOUNT_TABLEのみの参照を行う場合はこのリポジトリーを使います。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/08/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.account.bankaccount;

import com.yonetani.webapp.accountbook.domain.model.account.bankaccount.BankAccount;
import com.yonetani.webapp.accountbook.domain.model.account.bankaccount.BankAccountInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndBankAccountCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndBankAccountSort;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndBankAccountSortBetweenAB;

/**
 *<pre>
 * 銀行口座テーブル:BANK_ACCOUNT_TABLEのデータを登録・更新・参照するリポジトリーです
 * 銀行口座テーブル:BANK_ACCOUNT_TABLEのみの参照を行う場合はこのリポジトリーを使います。
 *
 * 銀行口座マスタはシステム行（予約帯）を持たないため、店舗マスタの
 * countByIdAndLessThanNineHundred 相当のメソッド名は使わず、単純な countById とする。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
public interface BankAccountTableRepository {
	/**
	 *<pre>
	 * 銀行口座テーブル情報を新規登録します。
	 *</pre>
	 * @param data 新規追加データ
	 * @return 登録されたデータの件数
	 *
	 */
	int add(BankAccount data);

	/**
	 *<pre>
	 * 銀行口座テーブル情報を更新します。
	 *</pre>
	 * @param data 更新データ
	 * @return 更新されたデータの件数
	 *
	 */
	int update(BankAccount data);

	/**
	 *<pre>
	 * 銀行口座テーブル情報のうち、銀行口座表示順の値を更新します。
	 *</pre>
	 * @param data 更新データ
	 * @return 更新されたデータの件数
	 *
	 */
	int updateBankAccountSort(BankAccount data);

	/**
	 *<pre>
	 * ユーザIDに対応する銀行口座情報を取得します。
	 *</pre>
	 * @param userId 検索対象のユーザID
	 * @return 銀行口座情報のリスト
	 *
	 */
	BankAccountInquiryList findById(SearchQueryUserId userId);

	/**
	 *<pre>
	 * ユーザIDと指定の銀行口座表示順以降の銀行口座情報を取得します。
	 *</pre>
	 * @param search 検索対象のユーザIDと銀行口座表示順
	 * @return 銀行口座情報のリスト
	 *
	 */
	BankAccountInquiryList findById(SearchQueryUserIdAndBankAccountSort search);

	/**
	 *<pre>
	 * ユーザIDと指定の銀行口座表示順A～銀行口座表示順Bまでの間の銀行口座情報を取得します。
	 *</pre>
	 * @param search 検索対象のユーザIDと銀行口座表示順A～銀行口座表示順B
	 * @return 銀行口座情報のリスト
	 *
	 */
	BankAccountInquiryList findById(SearchQueryUserIdAndBankAccountSortBetweenAB search);

	/**
	 *<pre>
	 * ユーザID、銀行口座コードに対応する銀行口座情報を取得します。
	 *</pre>
	 * @param search 検索対象のユーザIDと銀行口座コード
	 * @return 銀行口座情報
	 *
	 */
	BankAccount findById(SearchQueryUserIdAndBankAccountCode search);

	/**
	 *<pre>
	 * 新規の銀行口座コード発番用にユーザIDに対応する銀行口座情報が何件あるかを取得します(上限99件チェック用)。
	 *</pre>
	 * @param userId 検索対象のユーザID
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	int countById(SearchQueryUserId userId);

	/**
	 *<pre>
	 * ユーザIDに対応する銀行口座情報のうち、選択肢用(ENABLE_FLG=trueのみ、表示順ソート)の銀行口座情報を取得します。
	 * 支払方法マスタ管理画面の銀行口座選択で使用します。
	 *</pre>
	 * @param userId 検索対象のユーザID
	 * @return 銀行口座情報のリスト
	 *
	 */
	BankAccountInquiryList findSelectableByUserId(SearchQueryUserId userId);
}
