/**
 * 商品情報管理画面を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・情報管理(商品)表示：トップメニューからの遷移(初期表示)、商品情報登録・更新成功時→リダイレクト(GET)
 * ・商品情報登録・更新(POST)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2023/10/29 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.controller.baseinfo.manage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.application.usecase.baseinfo.manage.ShoppingItemInfoManageUseCase;
import com.yonetani.webapp.accountbook.presentation.request.session.UserSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 商品情報管理画面を担当するコントローラーです。
 * 以下画面遷移を担当します。
 * ・情報管理(商品)表示：トップメニューからの遷移(初期表示)、商品情報登録・更新成功時→リダイレクト(GET)
 * ・商品情報登録・更新(POST)
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Controller
@RequestMapping("/myhacbook/managebaseinfo/shoppingiteminfo/")
@Log4j2
@RequiredArgsConstructor
public class ShoppingItemInfoManageController {
	// UseCase
	private final ShoppingItemInfoManageUseCase usecase;
	// UserSession
	private final UserSession user;
	
	
	/**
	 *<pre>
	 * 情報管理(商品)画面表示のGET要求時マッピングです。
	 * 以下GETリクエストに対応します。
	 * ・トップメニューからの遷移(初期表示)
	 * ・お店情報登録・更新成功時→リダイレクト
	 *</pre>
	 * @return 情報管理(商品)画面
	 *
	 */
	@GetMapping("/initload/")
	public ModelAndView getInitLoad() {
		log.debug("getInitLoad:");
		return this.usecase.readShoppingItemInfo(this.user).build();
	}
	
	/**
	 *<pre>
	 * 商品情報登録・更新のPOST要求時マッピングです。
	 *</pre>
	 * @return 情報管理(商品)画面
	 *
	 */
	@PostMapping("/update/")
	public ModelAndView postUpdate() {
		log.debug("postUpdate:");
		return this.usecase.readShoppingItemInfo(this.user).build();
	}
}
