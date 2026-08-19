/**
 * 支払方法テーブル:PAYMENT_METHOD_TABLEのデータを登録・更新・参照するリポジトリーです
 * 支払方法テーブル:PAYMENT_METHOD_TABLEのみの参照を行う場合はこのリポジトリーを使います。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/08/19 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.repository.account.paymentmethod;

import com.yonetani.webapp.accountbook.domain.model.account.paymentmethod.PaymentMethod;
import com.yonetani.webapp.accountbook.domain.model.account.paymentmethod.PaymentMethodInquiryList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndPaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndPaymentMethodSort;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndPaymentMethodSortBetweenAB;

/**
 *<pre>
 * 支払方法テーブル:PAYMENT_METHOD_TABLEのデータを登録・更新・参照するリポジトリーです
 * 支払方法テーブル:PAYMENT_METHOD_TABLEのみの参照を行う場合はこのリポジトリーを使います。
 *
 * 取得メソッドを用途別に2系統用意する（[支払方法固定値の設計指針] 2.3参照）。
 * ・findByUserId() / findById(各種SearchQuery)：表示・JOIN用（全件、システム予約値を含む）
 * ・findSelectableByUserId()：プルダウン用（ENABLE_FLG=trueかつシステム予約値を除外）
 * ・findEnabledByUserId()：固定費登録画面専用（ENABLE_FLG=trueのみ、システム予約値は除外しない）
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
public interface PaymentMethodTableRepository {
	/**
	 *<pre>
	 * 支払方法テーブル情報を新規登録します。
	 *</pre>
	 * @param data 新規追加データ
	 * @return 登録されたデータの件数
	 *
	 */
	int add(PaymentMethod data);

	/**
	 *<pre>
	 * 支払方法テーブル情報を更新します。
	 *</pre>
	 * @param data 更新データ
	 * @return 更新されたデータの件数
	 *
	 */
	int update(PaymentMethod data);

	/**
	 *<pre>
	 * 支払方法テーブル情報のうち、支払方法表示順の値を更新します。
	 *</pre>
	 * @param data 更新データ
	 * @return 更新されたデータの件数
	 *
	 */
	int updateSort(PaymentMethod data);

	/**
	 *<pre>
	 * ユーザIDに対応する支払方法情報を取得します(表示・JOIN用、全件・システム予約値を含む)。
	 *</pre>
	 * @param userId 検索対象のユーザID
	 * @return 支払方法情報のリスト
	 *
	 */
	PaymentMethodInquiryList findByUserId(SearchQueryUserId userId);

	/**
	 *<pre>
	 * ユーザIDと指定の支払方法表示順以降の支払方法情報を取得します。
	 *</pre>
	 * @param search 検索対象のユーザIDと支払方法表示順
	 * @return 支払方法情報のリスト
	 *
	 */
	PaymentMethodInquiryList findById(SearchQueryUserIdAndPaymentMethodSort search);

	/**
	 *<pre>
	 * ユーザIDと指定の支払方法表示順A～支払方法表示順Bまでの間の支払方法情報を取得します。
	 *</pre>
	 * @param search 検索対象のユーザIDと支払方法表示順A～支払方法表示順B
	 * @return 支払方法情報のリスト
	 *
	 */
	PaymentMethodInquiryList findById(SearchQueryUserIdAndPaymentMethodSortBetweenAB search);

	/**
	 *<pre>
	 * ユーザID、支払方法コードに対応する支払方法情報を取得します。
	 *</pre>
	 * @param search 検索対象のユーザIDと支払方法コード
	 * @return 支払方法情報
	 *
	 */
	PaymentMethod findById(SearchQueryUserIdAndPaymentMethodCode search);

	/**
	 *<pre>
	 * ユーザIDに対応する支払方法情報のうち、選択肢用(ENABLE_FLG=trueかつシステム予約値を除外、表示順ソート)の
	 * 支払方法情報を取得します。収支登録・買い物登録・店舗マスタのデフォルト支払方法選択で使用します。
	 *</pre>
	 * @param userId 検索対象のユーザID
	 * @return 支払方法情報のリスト
	 *
	 */
	PaymentMethodInquiryList findSelectableByUserId(SearchQueryUserId userId);

	/**
	 *<pre>
	 * ユーザIDに対応する支払方法情報のうち、有効な支払方法のみを表示順で取得します(システム予約値は除外しない)。
	 * 固定費登録画面専用（買い物集計8項目の登録で「支払方法がない」を選択できる必要があるため）。
	 *</pre>
	 * @param userId 検索対象のユーザID
	 * @return 支払方法情報のリスト
	 *
	 */
	PaymentMethodInquiryList findEnabledByUserId(SearchQueryUserId userId);

	/**
	 *<pre>
	 * 新規の支払方法コード発番用にユーザIDに対応する支払方法情報のうち、システム予約帯(990～999)を除いた件数を取得します。
	 *</pre>
	 * @param userId 検索対象のユーザID
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	int countByIdAndLessThanReserved(SearchQueryUserId userId);
}
