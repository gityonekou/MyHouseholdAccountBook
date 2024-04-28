/**
 * 「ユーザロール」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/11/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.adminmenu;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「ユーザロール」項目の値を表すドメインタイプです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Getter
public class UserRoles {
	
	/**
	 *<pre>
	 * 「ユーザロール」項目の値で表示するユーザロールの値です。
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@EqualsAndHashCode
	public static class UserRole {
		
		// ユーザロール
		private final String value;
		
		/**
		 *<pre>
		 * ユーザロールのドメインタイプを生成します。
		 *</pre>
		 * @param role 
		 * @return
		 *
		 */
		public static UserRole from(String role) {
			return new UserRole(role);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return toValueString();
		}
		
		/**
		 *<pre>
		 * ユーザロールの値(文字列)を返します
		 *</pre>
		 * @return
		 *
		 */
		public String toValueString() {
			return value;
		}
		
		/**
		 *<pre>
		 * ユーザロールの値を表示値にして返します
		 *</pre>
		 * @return ユーザロールの表示値(管理者 or ユーザー)
		 *
		 */
		public String toViewString() {
			// ユーザロールの値が空またはnullの場合
			if(!StringUtils.hasLength(value)) {
				return "";
			// ユーザロールの値が管理者の場合
			} else if (value.equals(MyHouseholdAccountBookContent.USER_ROLE_ADMIN_VALUE)) {
				return MyHouseholdAccountBookContent.USER_ROLE_ADMIN_VIEW;
			// ユーザロールの値がユーザーの場合
			} else if (value.equals(MyHouseholdAccountBookContent.USER_ROLE_USER_VALUE)) {
				return MyHouseholdAccountBookContent.USER_ROLE_USER_VIEW;
			// ユーザロールの値が上記以外の場合
			} else {
				return "";
			}
		}
	}
	
	// ユーザロールのリスト
	private final List<UserRole> values;
	
	/**
	 *<pre>
	 * ユーザロールのドメインタイプ(リスト)を生成します。
	 *</pre>
	 * @param userRoles ユーザロールのリスト
	 * @return ドメインタイプ(リスト)
	 *
	 */
	public static UserRoles fromDomain(List<UserRole> userRoles) {
		if(CollectionUtils.isEmpty(userRoles)) {
			return new UserRoles(Collections.emptyList());
		} else {
			return new UserRoles(userRoles);
		}
	}
	
	
	/**
	 *<pre>
	 * 文字列のリストからユーザロールのドメインタイプ(リスト)を生成します。
	 * 以下に従い変換します。
	 *</pre>
	 * @param userRoles ユーザロールのリスト([userRoleAdmin][userRoleUser])
	 * @return ドメインタイプ(リスト)
	 * @throws MyHouseholdAccountBookException 文字列のリストに([userRoleAdmin][userRoleUser])以外の文字列が含まれている場合
	 *
	 */
	public static UserRoles fromString(List<String> userRoles)  {
		if(CollectionUtils.isEmpty(userRoles)) {
			return new UserRoles(Collections.emptyList());
		} else {
			return new UserRoles(userRoles.stream().map(str -> UserRoles.UserRole.from(str))
					.collect(Collectors.toUnmodifiableList()));
		}
	}
	/**
	 *<pre>
	 * ユーザロールのドメインタイプ(リスト)から表示値のリスト型の値を生成して返却します。
	 *</pre>
	 * @return 表示値のリスト型の値
	 *
	 */
	public List<String> toViewStringList() {
		if(CollectionUtils.isEmpty(values)) {
			return Collections.emptyList();
		} else {
			return values.stream().map(role -> role.toViewString()).collect(Collectors.toUnmodifiableList());
		}
	}
	
	/**
	 *<pre>
	 * ユーザロールのドメインタイプ(リスト)から値(文字列)リスト型の値を生成して返却します。
	 *</pre>
	 * @return 値(文字列)のリスト型の値
	 *
	 */
	public List<String> toValueStringList() {
		if(CollectionUtils.isEmpty(values)) {
			return Collections.emptyList();
		} else {
			return values.stream().map(role -> role.toValueString()).collect(Collectors.toUnmodifiableList());
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for(UserRole role : values) {
			if(str.length() > 0) {
				str.append(',');
			}
			str.append(role);
			
		}
		return str.toString();
	}
}
