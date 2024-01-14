/**
 * ユーザ権限テーブル:AUTHORITIESの照会・更新を行うマッパーです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/11/25 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.mapper.adminmenu;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.yonetani.webapp.accountbook.infrastructure.dto.adminmenu.AdminMenuUserInfoAuthoritiesDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdSearchQueryDto;

/**
 *<pre>
 * ユーザ権限テーブル:AUTHORITIESの照会・更新を行うマッパーです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Mapper
public interface AdminMenuUserInfoAuthoritiesMapper {
	
	/**
	 *<pre>
	 * ユーザ権限テーブル:AUTHORITIESから指定ユーザの権限情報を取得します
	 *</pre>
	 * @param userId
	 * @return
	 *
	 */
	@Select("sql/adminmenu/AdminMenuUserInfoAuthoritiesSelectSql01.sql")
	public List<AdminMenuUserInfoAuthoritiesDto> selectAuthority(@Param("dto") UserIdSearchQueryDto userId);
	
}
