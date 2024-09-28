/**
 * AdminMenuUserInfoRepository(管理者画面メニュー ユーザ情報管理)を実装したデータソースです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/11/12 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.infrastructure.datasource.adminmenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookException;
import com.yonetani.webapp.accountbook.domain.model.adminmenu.AdminMenuUserInfo;
import com.yonetani.webapp.accountbook.domain.model.adminmenu.AdminMenuUserInfoItemList;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.repository.adminmenu.AdminMenuUserInfoRepository;
import com.yonetani.webapp.accountbook.domain.type.adminmenu.UserRoles;
import com.yonetani.webapp.accountbook.infrastructure.dto.adminmenu.AdminMenuUserInfoAuthoritiesDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.adminmenu.AdminMenuUserInfoDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.adminmenu.AdminMenuUserInfoListDto;
import com.yonetani.webapp.accountbook.infrastructure.dto.searchquery.UserIdSearchQueryDto;
import com.yonetani.webapp.accountbook.infrastructure.mapper.adminmenu.AdminMenuUserInfoAuthoritiesMapper;
import com.yonetani.webapp.accountbook.infrastructure.mapper.adminmenu.AdminMenuUserInfoMapper;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * AdminMenuUserInfoRepository(管理者画面メニュー ユーザ情報管理)を実装したデータソースです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Repository
@RequiredArgsConstructor
public class AdminMenuUserInfoDataSource implements AdminMenuUserInfoRepository {

	// マッパー
	private final AdminMenuUserInfoMapper userInfoListMapper;
	private final AdminMenuUserInfoAuthoritiesMapper userInfoListAuthoritiesMapper;
	// ユーザ情報登録用(SpringSecurity管理)
	private final UserDetailsManager userDetailsManager;
	// パスワードエンコーダー
	private final PasswordEncoder passwordEncoder;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AdminMenuUserInfoItemList getUserInfoList(){
		// ユーザ情報の一覧を取得
		List<AdminMenuUserInfoListDto> resultListDto = userInfoListMapper.selectUserInfoList();
		return AdminMenuUserInfoItemList.from(resultListDto.stream().map(dto -> 
			AdminMenuUserInfoItemList.UserInfoItem.from(
					dto.getUserId(),
					dto.getUserName(),
					dto.isEnabled(),
					toUserRoleDomainList(userInfoListAuthoritiesMapper.selectAuthority(
							UserIdSearchQueryDto.from(dto.getUserId()))),
					dto.getNowTargetYear(),
					dto.getNowTargetMonth())
		).collect(Collectors.toUnmodifiableList()));
	}
	
	/**
	 * {@inheritDoc}
	 * @throws MyHouseholdAccountBookException 
	 */
	@Override
	public AdminMenuUserInfo getUserInfo(SearchQueryUserId userId) {
		// 指定ユーザIDに対応するユーザ情報を取得
		AdminMenuUserInfoDto dto = userInfoListMapper.selectUserInfo(UserIdSearchQueryDto.from(userId.getUserId().getValue()));
		if(dto == null) {
			return null;
		} else {
			return AdminMenuUserInfo.from(
					dto.getUserId(),
					dto.getUserName(),
					dto.isEnabled(),
					toUserRoleDomainList(userInfoListAuthoritiesMapper.selectAuthority(
							UserIdSearchQueryDto.from(dto.getUserId()))),
					// パスワードは取得しないので空文字列を設定
					"", 
					dto.getNowTargetYear(),
					dto.getNowTargetMonth());
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addUserInfo(AdminMenuUserInfo userInfo) throws MyHouseholdAccountBookException {
		// ユーザ権限情報を作成
		List<GrantedAuthority> roleList = new ArrayList<>();
		for(String userRole : userInfo.getUserRole().toValueStringList()) {
			roleList.add(new SimpleGrantedAuthority(convertUserRoleDB(userRole)));
		}
		
		// ユーザ情報を追加
		User user = new User(
				userInfo.getUserId().getValue(),
				passwordEncoder.encode(userInfo.getUserPassword().getValue()),
				userInfo.getUserStatus().isValue(),
				true,
				true,
				true,
				roleList);
		userDetailsManager.createUser(user);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateUserInfo(AdminMenuUserInfo userInfo) throws MyHouseholdAccountBookException {
		// ユーザ権限情報を作成
		List<GrantedAuthority> roleList = new ArrayList<>();
		for(String userRole : userInfo.getUserRole().toValueStringList()) {
			roleList.add(new SimpleGrantedAuthority(convertUserRoleDB(userRole)));
		}
		
		// ユーザ情報を更新
		User user = new User(
				userInfo.getUserId().getValue(),
				passwordEncoder.encode(userInfo.getUserPassword().getValue()),
				userInfo.getUserStatus().isValue(),
				true,
				true,
				true,
				roleList);
		userDetailsManager.updateUser(user);
	}
	
	/**
	 *<pre>
	 * ユーザロール情報をDtoからドメインに変換します。
	 *</pre>
	 *
	 */
	private List<UserRoles.UserRole> toUserRoleDomainList(List<AdminMenuUserInfoAuthoritiesDto> values) {
		if(CollectionUtils.isEmpty(values)) {
			return Collections.emptyList();
		} else {
			return values.stream().map(dto -> UserRoles.UserRole.from(convertUserRoleDomain(dto.getRole())))
					.collect(Collectors.toUnmodifiableList());
		}
	}
	
	/**
	 *<pre>
	 * ユーザロールの値をDB値からアプリケーション値に変換します。
	 *</pre>
	 * @param role ユーザロールの値(DB値)
	 * @return ユーザロールの値(アプリケーション値)
	 *
	 */
	private String convertUserRoleDomain(String role) {
		if(!StringUtils.hasLength(role)) {
			return role;
		} else if (role.equals("ROLE_ADMIN")) {
			return MyHouseholdAccountBookContent.USER_ROLE_ADMIN_VALUE;
		} else if (role.equals("ROLE_USER")) {
			return MyHouseholdAccountBookContent.USER_ROLE_USER_VALUE;
		} else {
			return role;
		}
	}
	
	/**
	 *<pre>
	 * ユーザロールの値をアプリケーション値からDB値に変換します。
	 *</pre>
	 * @param role ユーザロールの値(アプリケーション値)
	 * @return ユーザロールの値(DB値)
	 *
	 */
	private String convertUserRoleDB(String role) throws MyHouseholdAccountBookException {
		if(!StringUtils.hasLength(role)) {
			throw new MyHouseholdAccountBookException("ユーザロールの値が不正です。role=" + role);
		} else if (role.equals(MyHouseholdAccountBookContent.USER_ROLE_ADMIN_VALUE)) {
			return "ROLE_ADMIN";
		} else if (role.equals(MyHouseholdAccountBookContent.USER_ROLE_USER_VALUE)) {
			return "ROLE_USER";
		} else {
			throw new MyHouseholdAccountBookException("ユーザロールの値が不正です。role=" + role);
		}
	}
}
