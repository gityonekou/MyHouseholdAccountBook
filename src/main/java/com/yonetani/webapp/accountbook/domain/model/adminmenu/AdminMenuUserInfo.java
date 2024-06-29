/**
 * 管理者メニュー ユーザ情報入力フォームに設定するユーザ情報を表すドメインモデルです 
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/11/26 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.adminmenu;

import java.util.List;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookException;
import com.yonetani.webapp.accountbook.domain.type.adminmenu.UserPassword;
import com.yonetani.webapp.accountbook.domain.type.adminmenu.UserRoles;
import com.yonetani.webapp.accountbook.domain.type.adminmenu.UserStatus;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.domain.type.common.UserName;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 管理者メニュー ユーザ情報入力フォームに設定するユーザ情報を表すドメインモデルです 
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString(exclude="userPassword")
public class AdminMenuUserInfo {

	// ユーザID
	private final UserId userId;
	// ユーザ名
	private final UserName userName;
	// ステータス
	private final UserStatus userStatus;
	// ユーザロール
	private final UserRoles userRole;
	// ユーザパスワード
	private final UserPassword userPassword;
	// 決算年月
	private final TargetYearMonth targetYearMonth;
	
	/**
	 *<pre>
	 * 引数の値からユーザ情報ドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param userName ユーザ名
	 * @param userStatus ステータス
	 * @param userRoles ユーザロール
	 * @param targetYear 決算年
	 * @param targetMonth 決算月
	 * @return ユーザ情報ドメインモデル
	 *
	 */
	public static AdminMenuUserInfo from(
			String userId,
			String userName,
			boolean userStatus,
			List<UserRoles.UserRole> userRoles,
			String userPassword,
			String targetYear,
			String targetMonth) {
		return new AdminMenuUserInfo(
				UserId.from(userId),
				UserName.from(userName),
				UserStatus.from(userStatus),
				UserRoles.fromDomain(userRoles),
				UserPassword.from(userPassword),
				TargetYearMonth.from(targetYear + targetMonth));
	}
	
	/**
	 *<pre>
	 * 引数の値からユーザ情報ドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param userName ユーザ名
	 * @param userStatus ステータス
	 * @param userRoles ユーザロール
	 * @param targetYearMonth 決算年月
	 * @return ユーザ情報ドメインモデル
	 * @throws MyHouseholdAccountBookException 
	 *
	 */
	public static AdminMenuUserInfo from(
			String userId,
			String userName,
			String userStatus,
			List<String> userRoles,
			String userPassword,
			String targetYearMonth) throws MyHouseholdAccountBookException {
		return new AdminMenuUserInfo(
				UserId.from(userId),
				UserName.from(userName),
				UserStatus.from(userStatus),
				UserRoles.fromString(userRoles),
				UserPassword.from(userPassword),
				TargetYearMonth.from(targetYearMonth));
	}
	
	/**
	 *<pre>
	 * 値が空となるドメインモデルを生成して返します。
	 *</pre>
	 * @return
	 *
	 */
	public static AdminMenuUserInfo fromEmpty() {
		return new AdminMenuUserInfo(null, null, null, null, null, null);
	}
	
	/**
	 *<pre>
	 * 検索結果が設定されているかどうかを判定します。
	 *</pre>
	 * @return 空の場合はtrue、値が設定されている場合はfalse
	 *
	 */
	public boolean isEmpty() {
		if(userId == null || !StringUtils.hasLength(userId.toString())) {
			return true;
		} else {
			return false;
		}
	}
}
