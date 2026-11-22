/**
 * 支払方法テーブル:PAYMENT_METHOD_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.account.paymentmethod;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yonetani.webapp.accountbook.infrastructure.dto.account.paymentmethod.PaymentMethodReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndPaymentMethodCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndPaymentMethodSortBetweenABSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndPaymentMethodSortSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdSearchQueryDto;

/**
 *<pre>
 * 支払方法テーブル:PAYMENT_METHOD_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@Mapper
public interface PaymentMethodTableMapper {

	/**
	 *<pre>
	 * 支払方法テーブル:PAYMENT_METHOD_TABLEにデータを追加します。
	 *</pre>
	 * @param writeDto 支払方法テーブル:PAYMENT_METHOD_TABLE出力情報
	 * @return 支払方法テーブルに追加されたデータ件数
	 *
	 */
	@Insert("sql/account/paymentmethod/PaymentMethodTableInsertSql01.sql")
	public int insert(@Param("dto") PaymentMethodReadWriteDto writeDto);

	/**
	 *<pre>
	 * 指定の支払方法情報で支払方法テーブル:PAYMENT_METHOD_TABLEを更新します。
	 *</pre>
	 * @param writeDto 支払方法テーブル:PAYMENT_METHOD_TABLE出力情報
	 * @return 支払方法テーブルのデータ更新件数
	 *
	 */
	@Update("sql/account/paymentmethod/PaymentMethodTableUpdateSql01.sql")
	public int update(@Param("dto") PaymentMethodReadWriteDto writeDto);

	/**
	 *<pre>
	 * 指定の支払方法情報で支払方法テーブル:PAYMENT_METHOD_TABLEの表示順の値を更新します。
	 *</pre>
	 * @param writeDto 支払方法テーブル:PAYMENT_METHOD_TABLE出力情報
	 * @return 支払方法テーブルのデータ更新件数
	 *
	 */
	@Update("sql/account/paymentmethod/PaymentMethodTableUpdateSql02.sql")
	public int updateSort(@Param("dto") PaymentMethodReadWriteDto writeDto);

	/**
	 *<pre>
	 * 指定のユーザIDを条件に支払方法テーブル:PAYMENT_METHOD_TABLEを参照します(全件、システム予約値を含む)。
	 *</pre>
	 * @param search 検索条件:ユーザID
	 * @return 支払方法テーブル:PAYMENT_METHOD_TABLE参照結果のリスト
	 *
	 */
	@Select("sql/account/paymentmethod/PaymentMethodTableSelectSql01.sql")
	public List<PaymentMethodReadWriteDto> findByUserId(@Param("dto") UserIdSearchQueryDto search);

	/**
	 *<pre>
	 * 指定のユーザID、支払方法コードを条件に支払方法テーブル:PAYMENT_METHOD_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID、支払方法コード
	 * @return 支払方法テーブル:PAYMENT_METHOD_TABLE参照結果
	 *
	 */
	@Select("sql/account/paymentmethod/PaymentMethodTableSelectSql02.sql")
	public PaymentMethodReadWriteDto findByIdAndPaymentMethodCode(@Param("dto") UserIdAndPaymentMethodCodeSearchQueryDto search);

	/**
	 *<pre>
	 * 指定のユーザIDと指定した支払方法表示順以降のデータを条件に支払方法テーブル:PAYMENT_METHOD_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID、支払方法表示順
	 * @return 支払方法テーブル:PAYMENT_METHOD_TABLE参照結果のリスト
	 *
	 */
	@Select("sql/account/paymentmethod/PaymentMethodTableSelectSql03.sql")
	public List<PaymentMethodReadWriteDto> findByIdAndPaymentMethodSort(@Param("dto") UserIdAndPaymentMethodSortSearchQueryDto search);

	/**
	 *<pre>
	 * 指定のユーザIDと指定した支払方法表示順A～支払方法表示順B間のデータを条件に支払方法テーブル:PAYMENT_METHOD_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID、支払方法表示順A、支払方法表示順B
	 * @return 支払方法テーブル:PAYMENT_METHOD_TABLE参照結果のリスト
	 *
	 */
	@Select("sql/account/paymentmethod/PaymentMethodTableSelectSql04.sql")
	public List<PaymentMethodReadWriteDto> findByIdAndPaymentMethodSortBetween(@Param("dto") UserIdAndPaymentMethodSortBetweenABSearchQueryDto search);

	/**
	 *<pre>
	 * 指定のユーザIDに対応する支払方法情報のうち、有効かつシステム予約値を除いたものを表示順で取得します(選択肢用)。
	 *</pre>
	 * @param search 検索条件:ユーザID
	 * @return 支払方法テーブル:PAYMENT_METHOD_TABLE参照結果のリスト
	 *
	 */
	@Select("sql/account/paymentmethod/PaymentMethodTableSelectSql05.sql")
	public List<PaymentMethodReadWriteDto> findSelectableByUserId(@Param("dto") UserIdSearchQueryDto search);

	/**
	 *<pre>
	 * 指定のユーザIDに対応する支払方法情報のうち、有効なもの(システム予約値は除外しない)を表示順で取得します(固定費登録画面専用)。
	 *</pre>
	 * @param search 検索条件:ユーザID
	 * @return 支払方法テーブル:PAYMENT_METHOD_TABLE参照結果のリスト
	 *
	 */
	@Select("sql/account/paymentmethod/PaymentMethodTableSelectSql06.sql")
	public List<PaymentMethodReadWriteDto> findEnabledByUserId(@Param("dto") UserIdSearchQueryDto search);

	/**
	 *<pre>
	 * 指定のユーザIDに対応する支払方法情報のうち、システム予約帯(990～999)を除いた件数を取得します(新規コード発番用)。
	 *</pre>
	 * @param search 検索条件:ユーザID
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	@Select("sql/account/paymentmethod/PaymentMethodTableCountSql01.sql")
	public int countByIdAndLessThanReserved(@Param("dto") UserIdSearchQueryDto search);
}
