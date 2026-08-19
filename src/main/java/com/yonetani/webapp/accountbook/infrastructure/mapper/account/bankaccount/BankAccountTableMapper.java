/**
 * 銀行口座テーブル:BANK_ACCOUNT_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/08/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.account.bankaccount;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.yonetani.webapp.accountbook.infrastructure.dto.account.bankaccount.BankAccountReadWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndBankAccountCodeSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndBankAccountSortBetweenABSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdAndBankAccountSortSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdSearchQueryDto;

/**
 *<pre>
 * 銀行口座テーブル:BANK_ACCOUNT_TABLEのデータ追加・更新・参照を行うマッパーです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@Mapper
public interface BankAccountTableMapper {

	/**
	 *<pre>
	 * 銀行口座テーブル:BANK_ACCOUNT_TABLEにデータを追加します。
	 *</pre>
	 * @param writeDto 銀行口座テーブル:BANK_ACCOUNT_TABLE出力情報
	 * @return 銀行口座テーブルに追加されたデータ件数
	 *
	 */
	@Insert("sql/account/bankaccount/BankAccountTableInsertSql01.sql")
	public int insert(@Param("dto") BankAccountReadWriteDto writeDto);

	/**
	 *<pre>
	 * 指定の銀行口座情報で銀行口座テーブル:BANK_ACCOUNT_TABLEを更新します。
	 *</pre>
	 * @param writeDto 銀行口座テーブル:BANK_ACCOUNT_TABLE出力情報
	 * @return 銀行口座テーブルのデータ更新件数
	 *
	 */
	@Update("sql/account/bankaccount/BankAccountTableUpdateSql01.sql")
	public int update(@Param("dto") BankAccountReadWriteDto writeDto);

	/**
	 *<pre>
	 * 指定の銀行口座情報で銀行口座テーブル:BANK_ACCOUNT_TABLEの表示順の値を更新します。
	 *</pre>
	 * @param writeDto 銀行口座テーブル:BANK_ACCOUNT_TABLE出力情報
	 * @return 銀行口座テーブルのデータ更新件数
	 *
	 */
	@Update("sql/account/bankaccount/BankAccountTableUpdateSql02.sql")
	public int updateBankAccountSort(@Param("dto") BankAccountReadWriteDto writeDto);

	/**
	 *<pre>
	 * 指定のユーザIDを条件に銀行口座テーブル:BANK_ACCOUNT_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID
	 * @return 銀行口座テーブル:BANK_ACCOUNT_TABLE参照結果のリスト
	 *
	 */
	@Select("sql/account/bankaccount/BankAccountTableSelectSql01.sql")
	public List<BankAccountReadWriteDto> findById(@Param("dto") UserIdSearchQueryDto search);

	/**
	 *<pre>
	 * 指定のユーザID、銀行口座コードを条件に銀行口座テーブル:BANK_ACCOUNT_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID、銀行口座コード
	 * @return 銀行口座テーブル:BANK_ACCOUNT_TABLE参照結果
	 *
	 */
	@Select("sql/account/bankaccount/BankAccountTableSelectSql02.sql")
	public BankAccountReadWriteDto findByIdAndBankAccountCode(@Param("dto") UserIdAndBankAccountCodeSearchQueryDto search);

	/**
	 *<pre>
	 * 指定のユーザIDと指定した銀行口座表示順以降のデータを条件に銀行口座テーブル:BANK_ACCOUNT_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID、銀行口座表示順
	 * @return 銀行口座テーブル:BANK_ACCOUNT_TABLE参照結果のリスト
	 *
	 */
	@Select("sql/account/bankaccount/BankAccountTableSelectSql03.sql")
	public List<BankAccountReadWriteDto> findByIdAndBankAccountSort(@Param("dto") UserIdAndBankAccountSortSearchQueryDto search);

	/**
	 *<pre>
	 * 指定のユーザIDと指定した銀行口座表示順A～銀行口座表示順B間のデータを条件に銀行口座テーブル:BANK_ACCOUNT_TABLEを参照します。
	 *</pre>
	 * @param search 検索条件:ユーザID、銀行口座表示順A、銀行口座表示順B
	 * @return 銀行口座テーブル:BANK_ACCOUNT_TABLE参照結果のリスト
	 *
	 */
	@Select("sql/account/bankaccount/BankAccountTableSelectSql04.sql")
	public List<BankAccountReadWriteDto> findByIdAndBankAccountSortBetween(@Param("dto") UserIdAndBankAccountSortBetweenABSearchQueryDto search);

	/**
	 *<pre>
	 * 指定のユーザIDに対応する銀行口座情報のうち、有効な口座のみを表示順で取得します(選択肢用)。
	 *</pre>
	 * @param search 検索条件:ユーザID
	 * @return 銀行口座テーブル:BANK_ACCOUNT_TABLE参照結果のリスト
	 *
	 */
	@Select("sql/account/bankaccount/BankAccountTableSelectSql05.sql")
	public List<BankAccountReadWriteDto> findSelectableByUserId(@Param("dto") UserIdSearchQueryDto search);

	/**
	 *<pre>
	 * 指定のユーザIDに対応する銀行口座情報が何件あるかを取得します(上限99件チェック用)。
	 *</pre>
	 * @param search 検索条件:ユーザID
	 * @return 指定条件に該当するデータの件数
	 *
	 */
	@Select("sql/account/bankaccount/BankAccountTableCountSql01.sql")
	public int countById(@Param("dto") UserIdSearchQueryDto search);
}
