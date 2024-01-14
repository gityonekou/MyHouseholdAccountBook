/**
 * 「ユーザステータス」項目の値を表すドメインタイプです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/11/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.adminmenu;

import org.springframework.util.StringUtils;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.domain.exception.MyHouseholdAccountBookException;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 「ユーザステータス」項目の値を表すドメインタイプです。
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
public class UserStatus {
	
	// ユーザステータス(true:有効、false:無効)
	private final boolean value;
	
	/**
	 *<pre>
	 * 「ユーザステータス」項目の値を表すドメインタイプを生成します。
	 *</pre>
	 * @param status ステータス(true:有効、false:無効)
	 * @return 「ユーザステータス」項目ドメインタイプ
	 *
	 */
	public static UserStatus from(boolean status) {
		return new UserStatus(status);
	}
	
	/**
	 *<pre>
	 * 引数で渡された値を判定し、「ユーザステータス」項目の値を表すドメインタイプを生成します。
	 * ステータス=有効ならtrue、ステータス=無効ならfalseの値となります。
	 * 値が不正の場合、MyHouseholdAccountBookExceptionがスローされます。
	 *</pre>
	 * @param value ステータス(value文字列)
	 * @return 「ユーザステータス」項目ドメインタイプ 有効ならtrue、無効ならfalse
	 * @throws MyHouseholdAccountBookException 値が不正な場合
	 *
	 */
	public static UserStatus from(String value) throws MyHouseholdAccountBookException {
		if(!StringUtils.hasLength(value)) {
			throw new MyHouseholdAccountBookException("UserStatusの値が不正です。value=" + value);
		} else if (value.equals(MyHouseholdAccountBookContent.USER_STATUS_ENABLED_VALUE)) {
			return new UserStatus(true);
		} else if (value.equals(MyHouseholdAccountBookContent.USER_STATUS_DISABLED_VALUE)) {
			return new UserStatus(false);
		} else {
			throw new MyHouseholdAccountBookException("UserStatusの値が不正です。value=" + value);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return toViewString();
	}
	
	/**
	 *<pre>
	 * ユーザステータスの値をvalueの文字列値(有効=userStatusEnabled、無効=userStatusDisabled)で返します。
	 *</pre>
	 * @return ユーザーステータス(有効=userStatusEnabled、無効=userStatusDisabled)
	 *
	 */
	public String toValueString() {
		if(value) {
			// ユーザーステータス＝有効
			return MyHouseholdAccountBookContent.USER_STATUS_ENABLED_VALUE;
		} else {
			// ユーザーステータス＝無効
			return MyHouseholdAccountBookContent.USER_STATUS_DISABLED_VALUE;
		}
	}
	
	/**
	 *<pre>
	 * ユーザステータスの値をvalueの表示値(有効、無効)で返します。
	 *</pre>
	 * @return ユーザーステータス(有効、無効)
	 *
	 */
	public String toViewString() {
		if(value) {
			// ユーザーステータス＝有効
			return MyHouseholdAccountBookContent.USER_STATUS_ENABLED_VIEW;
		} else {
			// ユーザーステータス＝無効
			return MyHouseholdAccountBookContent.USER_STATUS_DISABLED_VIEW;
		}
	}
}
