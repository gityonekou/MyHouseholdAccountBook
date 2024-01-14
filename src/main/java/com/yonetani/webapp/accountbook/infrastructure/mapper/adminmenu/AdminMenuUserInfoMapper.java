/**
 * 家計簿利用ユーザ:ACCOUNT_BOOK_USERテーブルとユーザテーブル:USERSに対して、以下アクセスを行うマッパーです
 * ・家計簿利用ユーザ:ACCOUNT_BOOK_USERテーブルとユーザテーブル:USERSを紐づけして参照
 * ・ユーザテーブル:USERSの更新(家計簿利用ユーザ:ACCOUNT_BOOK_USERテーブルの更新はAccountBookUserMapperを利用してください)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/11/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.adminmenu;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.yonetani.webapp.accountbook.infrastructure.dto.adminmenu.AdminMenuUserInfoDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.adminmenu.AdminMenuUserInfoListDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdSearchQueryDto;

/**
 *<pre>
 * 家計簿利用ユーザ:ACCOUNT_BOOK_USERテーブルとユーザテーブル:USERSに対して、以下アクセスを行うマッパーです
 * ・家計簿利用ユーザ:ACCOUNT_BOOK_USERテーブルとユーザテーブル:USERSを紐づけして参照
 * ・ユーザテーブル:USERSの更新(家計簿利用ユーザ:ACCOUNT_BOOK_USERテーブルの更新はAccountBookUserMapperを利用してください)
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Mapper
public interface AdminMenuUserInfoMapper {
	
	/**
	 *<pre>
	 * 家計簿利用ユーザテーブル:ACCOUNT_BOOK_USER、ユーザテーブル:USERSからユーザ情報の一覧を取得します。
	 *</pre>
	 * @return
	 *
	 */
	@Select("sql/adminmenu/AdminMenuUserInfoListSelectSql01.sql")
	public List<AdminMenuUserInfoListDto> selectUserInfoList();
	
	/**
	 *<pre>
	 * 家計簿利用ユーザテーブル:ACCOUNT_BOOK_USER、ユーザテーブル:USERSから指定ユーザIDのユーザ情報を取得します。
	 *</pre>
	 * @param userId
	 * @return
	 *
	 */
	@Select("sql/adminmenu/AdminMenuUserInfoSelectSql01.sql")
	public AdminMenuUserInfoDto selectUserInfo(@Param("dto") UserIdSearchQueryDto userId);
	
}
