/**
 * お店情報管理画面を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・情報管理(お店)表示：トップメニューからの遷移(初期表示)、お店情報登録・更新成功時→リダイレクト(GET)
 * ・指定(リストから対象のお店を選択)のお店情報を表示(GET)
 * ・お店情報登録・更新(POST)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller.itemmanage;

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

import com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShopInfoManageUseCase;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShopInfoForm;
import com.yonetani.webapp.accountbook.presentation.request.session.UserSession;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShopInfoManageResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * お店情報管理画面を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・情報管理(お店)表示：トップメニューからの遷移(初期表示)、お店情報登録・更新成功時→リダイレクト(GET)
 * ・指定(リストから対象のお店を選択)のお店情報を表示(GET)
 * ・お店情報登録・更新(POST)
 * 
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Controller
@RequestMapping("/myhacbook/managebaseinfo/shopinfo/")
@Log4j2
@RequiredArgsConstructor
public class ShopInfoManageController {
	// usecase
	private final ShopInfoManageUseCase usecase;
	// UserSession
	private final UserSession user;
	
	/**
	 *<pre>
	 * 情報管理(お店)画面表示のGET要求時マッピングです。
	 * トップメニューからの遷移(初期表示)時のGETリクエストに対応します。
	 *</pre>
	 * @return 情報管理(お店)画面
	 *
	 */
	@GetMapping("/initload/")
	public ModelAndView getInitLoad() {
		log.debug("getInitLoad:");
		return this.usecase.readShopInfo(this.user).build();
	}
	
	/**
	 *<pre>
	 * 情報管理(お店)画面で、更新対象の店舗選択のGET要求時マッピングです。
	 *</pre>
	 * @param shopCode 表示対象の店舗コード
	 * @return 情報管理(お店)画面
	 *
	 */
	@GetMapping("/updateload")
	public ModelAndView getTargetLoad(@RequestParam("shopCode") String shopCode) {
		log.debug("getTargetLoad:shopCode=" + shopCode);
		
		// 店舗コード未設定の場合、エラー
		if(StringUtils.hasLength(shopCode)) {
			return this.usecase.readShopInfo(this.user, shopCode).build();
		} else {
			return ShopInfoManageResponse.buildBindingError("予期しないエラーが発生しました。管理者に問い合わせてください。[key=shopCode]");
		}
	}
	
	/**
	 *<pre>
	 * お店情報登録・更新のPOST要求時マッピングです。
	 *</pre>
	 * @param shopForm 入力フォーム情報
	 * @param bindingResult フォームのバリデーションチェック結果
	 * @return 情報管理(お店)画面
	 *
	 */
	@PostMapping("/update/")
	public ModelAndView postUpdate(@ModelAttribute @Validated ShopInfoForm shopForm, BindingResult bindingResult) {
		log.debug("postUpdate:input=" + shopForm);
		/* 入力フィールドのバリデーションチェック結果を判定 */
		// チェック結果エラーの場合
		if(bindingResult.hasErrors()) {
			// 初期表示情報を取得し、入力チェックエラーを設定
			return this.usecase.readShopInfo(this.user).buildBindingError(shopForm);
		// チェック結果OKの場合
		} else {
			/* hidden項目(action)の値チェック */
			// actionが未設定の場合、予期しないエラー
			if(!StringUtils.hasLength(shopForm.getAction())) {
				log.error("予期しないエラー actionのバリデーションチェックでエラー:action=" + shopForm.getAction());
				return ShopInfoManageResponse.buildBindingError("予期しないエラーが発生しました。管理者に問い合わせてください。[key=action]");
				
			// actionに従い、処理を実行
			} else {
				return this.usecase.execAction(this.user, shopForm).buildRedirect();
			}
		}
		
	}
	
	/**
	 *<pre>
	 * お店情報登録・更新完了後のリダイレクト(Get要求時)のマッピングです。
	 *</pre>
	 * @return 情報管理(お店)画面
	 *
	 */
	@GetMapping("/updateComplete/")
	public ModelAndView updateComplete() {
		log.debug("updateComplete:");
		return this.usecase.readShopInfo(this.user).buildComplete();
	}
}
