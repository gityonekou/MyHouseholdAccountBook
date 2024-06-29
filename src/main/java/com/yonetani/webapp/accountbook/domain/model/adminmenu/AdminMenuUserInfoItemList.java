/**
 * 管理者メニュー ユーザ情報管理画面(ユーザ情報一覧)の値を表すドメインモデルです
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/11/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.model.adminmenu;

import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

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
 * 管理者メニュー ユーザ情報管理画面(ユーザ情報一覧)の値を表すドメインモデルです 
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AdminMenuUserInfoItemList {

	/**
	 *<pre>
	 * ユーザ情報一覧の明細です
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	@ToString
	public static class UserInfoItem {
		// ユーザID
		private final UserId userId;
		// ユーザ名
		private final UserName userName;
		// ステータス
		private final UserStatus userStatus;
		// ユーザロール
		private final UserRoles userRole;
		// 決算年月
		private final TargetYearMonth targetYearMonth;
		
		/**
		 *<pre>
		 * 引数の値からユーザ情報一覧明細ドメインモデルを生成して返します。
		 *</pre>
		 * @param userId ユーザID
		 * @param userName ユーザ名
		 * @param userStatus ステータス
		 * @param userRoles ユーザロール
		 * @param targetYear 決算年
		 * @param targetMonth 決算月
		 * @return ユーザ情報一覧明細ドメインモデル
		 *
		 */
		public static UserInfoItem from(
				String userId,
				String userName,
				boolean userStatus,
				List<UserRoles.UserRole> userRoles,
				String targetYear,
				String targetMonth
			) {
			return new UserInfoItem(
					UserId.from(userId),
					UserName.from(userName),
					UserStatus.from(userStatus),
					UserRoles.fromDomain(userRoles),
					TargetYearMonth.from(targetYear + targetMonth));
		}
	}
	
	// ユーザ情報一覧
	private final List<UserInfoItem> values;
	
	/**
	 *<pre>
	 * ユーザ情報一覧の明細(ドメインモデル)のリストからAdminMenuUserInfoItemListのドメインモデルを生成して返します。
	 *</pre>
	 * @param values ユーザ情報一覧の明細(ドメインモデル)
	 * @return AdminMenuUserInfoItemListのドメインモデル
	 *
	 */
	public static AdminMenuUserInfoItemList from(List<UserInfoItem> values) {
		if(CollectionUtils.isEmpty(values)) {
			return new AdminMenuUserInfoItemList(Collections.emptyList());
		} else {
			return new AdminMenuUserInfoItemList(values);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if(values.size() > 0) {
			StringBuilder buff = new StringBuilder((values.size() + 1) * 150);
			buff.append("ユーザ情報一覧の明細:")
			.append(values.size())
			.append("件:");
			for(int i = 0; i < values.size(); i++) {
				buff.append("[[")
				.append(i)
				.append("][")
				.append(values.get(i))
				.append("]]");
			}
			return buff.toString();
		} else {
			return "ユーザ情報一覧の明細:0件";
		}
	}
	
	/**
	 *<pre>
	 * 検索結果が設定されているかどうかを判定します。
	 *</pre>
	 * @return 空の場合はtrue、値が設定されている場合はfalse
	 *
	 */
	public boolean isEmpty() {
		return CollectionUtils.isEmpty(values);
	}
}
