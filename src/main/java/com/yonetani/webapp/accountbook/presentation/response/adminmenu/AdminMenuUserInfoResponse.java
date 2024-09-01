/**
 * 管理者画面メニュー ユーザ情報管理画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/11/11 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.adminmenu;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yonetani.webapp.accountbook.common.content.MyHouseholdAccountBookContent;
import com.yonetani.webapp.accountbook.presentation.request.adminmenu.AdminMenuUserInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *<pre>
 * 管理者画面メニュー ユーザ情報管理画面表示情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminMenuUserInfoResponse extends AbstractResponse {
	
	/**
	 *<pre>
	 * ユーザ情報リストの表示情報です
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	@EqualsAndHashCode
	public static class UserInfoListItem {
		// ユーザID
		private final String userId;
		// ユーザ名
		private final String userName;
		// ステータス
		private final String userStatus;
		// ユーザロール
		private final List<String> role;
		// 決算年月
		private final String targetYearMonth;
		/**
		 *<pre>
		 * ユーザ情報リストの表示情報を生成して返します
		 *</pre>
		 * @param userId userName
		 * @param userName ユーザ名
		 * @param userStatus ステータス(有効／無効）
		 * @param role ユーザロール
		 * @param targetYearMonth 決算年月
		 * @return ユーザ情報リストの表示情報
		 *
		 */
		public static UserInfoListItem from(String userId, String userName, String userStatus,
				List<String> role, String targetYearMonth) {
			return new UserInfoListItem(userId, userName, userStatus, role, targetYearMonth);
		}
	}
	
	// ユーザ情報登録フォームデータ
	@Setter
	private AdminMenuUserInfoForm adminMenuUserInfoForm;
	
	// ユーザ情報リスト表示情報
	private List<UserInfoListItem> userInfoList = new ArrayList<>();
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @return 管理者画面メニュー ユーザ情報管理画面表示情報
	 *
	 */
	public static AdminMenuUserInfoResponse getInstance() {
		return new AdminMenuUserInfoResponse();
	}
	
	/**
	 *<pre>
	 * ユーザ情報リスト表示情報を追加します
	 *</pre>
	 * @param addList 追加するユーザ情報リスト
	 *
	 */
	public void addUserInfoListItems(List<UserInfoListItem> addList) {
		if(!CollectionUtils.isEmpty(addList)) {
			userInfoList.addAll(addList);
		}
	}
	
	/**
	 *<pre>
	 * 現在のレスポンス情報から画面返却データのModelAndViewを生成して返します。
	 *</pre>
	 * @return 画面返却データのModelAndView
	 *
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("adminmenu/userinfo");
		// メッセージリストを追加
		modelAndView.addObject("userInfoList", userInfoList);
		// ユーザ情報入力フォーム
		if(adminMenuUserInfoForm == null) {
			adminMenuUserInfoForm = new AdminMenuUserInfoForm();
			adminMenuUserInfoForm.setAction(MyHouseholdAccountBookContent.ACTION_TYPE_ADD);
		}
		modelAndView.addObject("adminMenuUserInfoForm", adminMenuUserInfoForm);
		
		return modelAndView;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String buildRedirectUrl(RedirectAttributes redirectAttributes) {
		// ユーザ登録完了後、リダイレクトするURL
		return "redirect:/myhacbook/admin/completeUseraAdd/";
	}

	/**
	 *<pre>
	 * バリデーションチェックを行った入力フォームの値から画面返却データのModelAndViewを生成して返します。
	 *</pre>
	 * @param loginUserInfo ログインユーザ情報
	 * @param userForm バリデーションチェックを行った入力フォームの値
	 * @return 画面返却データのModelAndView
	 *
	 */
	public static ModelAndView buildBindingError(LoginUserInfo loginUserInfo, AdminMenuUserInfoForm userForm) {
		AdminMenuUserInfoResponse res = new AdminMenuUserInfoResponse();
		// バリデーションチェックを行った入力フォームの値を設定
		res.setAdminMenuUserInfoForm(userForm);
		// ログインユーザ名を設定
		res.setLoginUserName(loginUserInfo.getUserName());
		// 画面表示のModelとViewを生成して返却
		return res.build();
	}
}
