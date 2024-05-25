/**
 * 管理者画面表示を担当するコントローラーです。
 * 管理者メニューの以下画面遷移を担当します。
 * ・マイ家計簿ユーザ登録画面表示
 * ・マイ家計簿ユーザ登録処理
 * ・【保守用】ベース情報管理
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/06/11 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller.adminmenu;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yonetani.webapp.accountbook.application.usecase.adminmenu.AdminMenuBaseInfoDetailUseCase;
import com.yonetani.webapp.accountbook.application.usecase.adminmenu.AdminMenuBaseInfoUseCase;
import com.yonetani.webapp.accountbook.application.usecase.adminmenu.AdminMenuUserInfoUseCase;
import com.yonetani.webapp.accountbook.presentation.request.adminmenu.AdminMenuUploadBaseInfoFileForm;
import com.yonetani.webapp.accountbook.presentation.request.adminmenu.AdminMenuUserInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.adminmenu.AdminMenuBaseInfoResponse;
import com.yonetani.webapp.accountbook.presentation.response.adminmenu.AdminMenuUserInfoResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.CompleteRedirectMessages;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 管理者画面表示を担当するコントローラーです。
 * 管理者メニューの以下画面遷移を担当します。
 * ・マイ家計簿ユーザ登録画面表示
 * ・マイ家計簿ユーザ登録処理
 * ・【保守用】ベース情報管理
 * 
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Controller
@Log4j2
@RequestMapping("/myhacbook/admin/")
@RequiredArgsConstructor
public class AdminMenuServiceController {
	
	// admin usecase(ユーザ情報管理)
	private final AdminMenuUserInfoUseCase userInfoUseCase;
	// admin usecase(ベース情報管理)
	private final AdminMenuBaseInfoUseCase baseInfoUseCase;
	// admin usecase(ベース情報詳細表示)
	private final AdminMenuBaseInfoDetailUseCase baseInfoDetailUseCase;
	// ログインユーザセッションBean
	private final LoginUserSession loginUserSession;
	
	/**
	 *<pre>
	 * ★パッチ充て用に急遽作った処理：後で、パッチ充て処理として本格対応する（家計簿ベース完了後）
	 *</pre>
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 *
	 */
	@PostMapping("/custominfo/")
	public ModelAndView postCustomInfo(RedirectAttributes redirectAttributes) {
		log.debug("postCustomInfo:");
		
		// 指定ファイルの情報をベーステーブルに出力します。
		return this.userInfoUseCase.customInfo().buildRedirect(redirectAttributes);
	}
	
	/**
	 *<pre>
	 * 管理者画面メニュー ユーザ登録画面初期表示のGet要求時のマッピングです。
	 *</pre>
	 * @return ユーザ登録画面情報
	 *
	 */
	@GetMapping("/userinfo/")
	public ModelAndView getUserInfo() {
		log.debug("getUserInfo:");
		// 画面表示データ読込
		return this.userInfoUseCase.read()
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 管理者画面メニュー ユーザ登録画面初期表示のGet要求時のマッピングです。
	 *</pre>
	 * @param userid 選択したユーザのユーザID
	 * @return ユーザ登録画面情報
	 *
	 */
	@GetMapping("/userinfo")
	public ModelAndView getUserInfo(@RequestParam(name = "userid") String userid) {
		log.debug("getUserInfo: userid=" + userid);
		
		// 画面表示データを読込
		return this.userInfoUseCase.read(userid)
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 管理者画面メニュー ユーザ登録処理のマッピングです。
	 *</pre>
	 * @param userForm ユーザ情報入力フォームの入力値
	 * @param bindingResult バリデーション結果
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return ユーザ登録画面情報
	 *
	 */
	@PostMapping("/useradd/")
	public ModelAndView postUserAdd(@ModelAttribute @Validated AdminMenuUserInfoForm userForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		log.debug("postUserInfo: input=" + userForm);
		
		/* 入力フィールドのバリデーションチェック結果を判定 */
		// チェック結果エラーの場合
		if(bindingResult.hasErrors()) {
			return AdminMenuUserInfoResponse.buildBindingError(loginUserSession.getLoginUserInfo(), userForm);
			
		// チェック結果OKの場合
		} else {
			// actionに従い、処理を実行
			return this.userInfoUseCase.execAction(userForm).buildRedirect(redirectAttributes);
		}
	}
	
	/**
	 *<pre>
	 * 管理者画面メニュー ユーザ登録完了後のリダイレクト(Get要求時)のマッピングです。
	 *</pre>
	 * @param redirectMessages リダイレクト元から引き継いだメッセージ
	 * @return ユーザ登録画面情報
	 *
	 */
	@GetMapping("/completeUseraAdd/")
	public ModelAndView completeUseraAdd(@ModelAttribute CompleteRedirectMessages redirectMessages) {
		log.debug("completeUseraAdd: input=" + redirectMessages);
		// 画面表示データ読込
		return this.userInfoUseCase.read()
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.buildComplete(redirectMessages);
	}
	
	/**
	 *<pre>
	 * 管理者画面メニュー ベース情報管理のマッピングです
	 *</pre>
	 * @return ベース情報管理画面
	 *
	 */
	@GetMapping("/managebaseinfo/")
	public ModelAndView getManageBaseInfo() {
		log.debug("getManageBaseInfo:");
		// 画面表示データ読込
		return this.baseInfoUseCase.read()
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.build();
	}
	
	/**
	 *<pre>
	 * 管理者画面メニュー ベース情報管理のマッピングです
	 * アップロードしたファイルをベース情報に登録します。
	 *</pre>
	 * @param baseInfoFileForm ベース情報ファイル登録フォームの入力値
	 * @param bindingResult バリデーション結果
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return ベース情報管理画面
	 *
	 */
	@PostMapping("/uploadbaseinfo/")
	public ModelAndView postUploadBaseInfo(@Validated AdminMenuUploadBaseInfoFileForm baseInfoFileForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		log.debug("postUploadBaseInfo: baseInfoFileForm=" + baseInfoFileForm.getBaseInfoFile());
		
		// ファイルアップロードのサンプルはこちらが良いです。
		// https://qiita.com/MizoguchiKenji/items/0aa1f2b385e73c36c24d
		if(bindingResult.hasErrors()) {
			// ファイルアップロード(ベース情報データ)の入力チェックでエラーの場合
			if(bindingResult.hasFieldErrors("baseInfoFile")) {
				return AdminMenuBaseInfoResponse.buildBindingError(
						loginUserSession.getLoginUserInfo(),
						baseInfoFileForm,
						bindingResult.getFieldError("baseInfoFile").getDefaultMessage());
			// 上記以外の場合
			} else {
				return AdminMenuBaseInfoResponse.buildBindingError(loginUserSession.getLoginUserInfo(), baseInfoFileForm);
			}

		} else {
			// 指定ファイルの情報をベーステーブルに出力します。
			return this.baseInfoUseCase.upload(baseInfoFileForm).buildRedirect(redirectAttributes);
		}
	}
	
	/**
	 *<pre>
	 * 管理者画面メニュー ベース情報管理アップロード完了後のリダイレクト(Get要求時)のマッピングです。
	 *</pre>
	 * @param redirectMessages リダイレクト元から引き継いだメッセージ
	 * @return ベース情報管理画面
	 *
	 */
	@GetMapping("/completeUploadBaseInfo/")
	public ModelAndView completeUploadBaseInfo(@ModelAttribute CompleteRedirectMessages redirectMessages) {
		log.debug("completeUploadBaseInfo: input=" + redirectMessages);
		// 画面表示データ読込
		return this.baseInfoUseCase.read()
				// レスポンスにログインユーザ名を設定
				.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
				// レスポンスからModelAndViewを生成
				.buildComplete(redirectMessages);
	}
	
	/**
	 *<pre>
	 * 管理者画面メニュー ベーステーブル詳細表示のマッピングです
	 *</pre>
	 * @return ベーステーブル詳細表示画面
	 *
	 */
	@GetMapping("/baseinfodetail")
	public ModelAndView getBaseInfoDetail(@RequestParam(name = "target") String target) {
		log.debug("getBaseInfoDetail: target=" + target);
		if(StringUtils.hasLength(target)) {
			// 画面表示データ読込
			return this.baseInfoDetailUseCase.read(target)
					// レスポンスにログインユーザ名を設定
					.setLoginUserName(loginUserSession.getLoginUserInfo().getUserName())
					// レスポンスからModelAndViewを生成
					.build();
		} else {
			return AdminMenuBaseInfoResponse.buildBindingError(
					loginUserSession.getLoginUserInfo(), "予期しないエラーが発生しました。管理者に問い合わせてください。[key=target]");
		}
	}
}
