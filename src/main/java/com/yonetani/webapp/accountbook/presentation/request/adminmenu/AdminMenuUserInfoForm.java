/**
 * ユーザ情報管理画面のユーザ登録情報が格納されたフォームデータです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/11/26 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.request.adminmenu;

import java.util.List;
import java.util.Objects;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *<pre>
 * ユーザ情報管理画面のユーザ登録情報が格納されたフォームデータです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Data
public class AdminMenuUserInfoForm {
	
	// アクション
	private String action;
	
	// ユーザID
	@NotBlank
	@Size(min = 6, max = 50)
	private String userId;
	
	// ユーザ名
	@NotBlank
	@Size(max = 100)
	private String userName;
	
	// ステータス(有効)
	@NotBlank
	private String userStatus;
	
	// ユーザロール
	@NotEmpty
	private List<String> userRole;
	
	// ユーザパスワード
	@NotBlank
	@Size(min = 8,max = 15)
	@Pattern(regexp = "^[0-9a-zA-Z]+$")
	private String userPassword;
	
	// ユーザパスワード(再入力)
	@NotBlank
	@Size(min = 8,max = 15)
	@Pattern(regexp = "^[0-9a-zA-Z]+$")
	private String userPasswordRetry;
	
	// 決算年月
	@NotBlank
	@Size(min = 6,max = 6)
	private String targetYearMonth;
	
	/**
	 * ユーザパスワードとユーザパスワード(再入力)の値が等しいかどうか
	 * isSamePasswordの結果はsamePasswrodに格納されるので、html側で個の変数の値を出力してください
	 * 
	 * @return
	 */
	@AssertTrue(message = "ユーザパスワードとユーザパスワード(再入力)に入力された値が一致しません")
	public boolean isSamePassword() {
		return Objects.equals(this.userPassword, this.userPasswordRetry);
	}
	
	/**
	 *<pre>
	 * 決算年月の値がyyyyMM形式となっているかを判定します。
	 *</pre>
	 * @return
	 *
	 */
//	@AssertTrue(message = "指定した値がyyyyMM形式になっていません。")
//	public boolean isTargetYearMonthFormat() {
//		try {
//			LocalDate.parse(this.targetYearMonth, DateTimeFormatter.ofPattern("yyyyMM"));
//			return true;
//		} catch(DateTimeParseException ex) {
//			return false;
//		}
//		
//	}
}
