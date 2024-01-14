/**
 * 家計簿利用ユーザ:ACCOUNT_BOOK_USERテーブルの照会・更新を行うマッパーです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/08 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.common;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.yonetani.webapp.accountbook.infrastructure.dto.common.AccountBookUserDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.common.AccountBookUserWriteDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdSearchQueryDto;

/**
 *<pre>
 * 家計簿利用ユーザ:ACCOUNT_BOOK_USERテーブルの照会・更新を行うマッパーです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Mapper
public interface AccountBookUserMapper {
	
	/**
	 *<pre>
	 * 指定したユーザIDに対応するユーザ情報を検索します。
	 *</pre>
	 * @param searchQuery 検索条件(ユーザID)
	 * @return ユーザ情報
	 *
	 */
	@Select("sql/common/AccountBookUserSelectSql01.sql")
	public AccountBookUserDto selectUser(@Param("dto") UserIdSearchQueryDto userId);
	
	/**
	 *<pre>
	 * 登録されているすべての有効なユーザ情報を検索します。
	 *</pre>
	 * @return
	 *
	 */
	@Select("sql/common/AccountBookUserSelectSql02.sql")
	public List<AccountBookUserDto> selectAllUsers();
	
	/**
	 *<pre>
	 * 家計簿利用ユーザテーブル:ACCOUNT_BOOK_USERにデータを追加します。
	 *</pre>
	 * @param writeDto 家計簿利用ユーザテーブル:ACCOUNT_BOOK_USER出力情報
	 *
	 */
	@Insert("sql/common/AccountBookUserInsertSql01.sql")
	public void insertAccountBookUser(@Param("dto") AccountBookUserWriteDto writeDto);
	
	/**
	 *<pre>
	 * 家計簿利用ユーザテーブル:ACCOUNT_BOOK_USERの指定ユーザ情報のデータを更新します。
	 *</pre>
	 * @param writeDto 家計簿利用ユーザテーブル:ACCOUNT_BOOK_USER出力情報
	 *
	 */
	@Insert("sql/common/AccountBookUserUpdateSql01.sql")
	public void updateAccountBookUser(@Param("dto") AccountBookUserWriteDto writeDto);
}
