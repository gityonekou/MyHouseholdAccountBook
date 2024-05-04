/**
 * 商品情報管理画面を担当するコントローラーです。
 * 商品情報管理画面は以下4つの画面で構成されていますが、そのすべての画面遷移を子のコントローラーで
 * 管理します。
 * ・情報管理(商品)初期表示画面:商品を登録する支出項目一覧、商品検索条件入力
 * ・情報管理(商品)検索結果画面
 * ・情報管理(商品)処理選択画面
 * ・情報管理(商品)更新画面
 * 
 * 画面遷移
 * ・情報管理(商品)初期表示画面：トップメニューからの遷移(初期表示)(GET)
 * ・情報管理(商品)検索結果画面：入力した検索条件に一致する検索結果を表示(POST)
 * ・検索画面のキャンセルボタンを選択時(POST)
 * ・情報管理(商品)処理選択画面：検索結果画面から任意の商品を選択時(GET)
 * ・情報管理(商品)更新画面：商品情報登録(対象の支出項目を選択して追加)(GET)
 * ・情報管理(商品)検索結果画面：選択した支出項目に属する商品の検索結果を表示(GET)
 * ・情報管理(商品)更新画面：商品情報登録(選択した商品と同一の支出項目で商品を追加アクションを選択時)(POST)
 * ・情報管理(商品)更新画面：商品情報更新アクションを選択時(POST)
 * ・処理選択画面でキャンセルボタンを選択時(POST)
 * ・追加・更新処理(POST)
 * ・支出項目情報登録・更新成功時→リダイレクト(GET)
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yonetani.webapp.accountbook.application.usecase.itemmanage.ShoppingItemInfoManageUseCase;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShoppingItemInfoSearchForm;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ShoppingItemInfoUpdateForm;
import com.yonetani.webapp.accountbook.presentation.request.session.UserSession;
import com.yonetani.webapp.accountbook.presentation.response.fw.CompleteRedirectMessages;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShoppingItemInfoManageInitResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShoppingItemInfoManageSearchResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ShoppingItemInfoManageUpdateResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 商品情報管理画面を担当するコントローラーです。
 * 商品情報管理画面は以下4つの画面で構成されていますが、そのすべての画面遷移を子のコントローラーで
 * 管理します。
 * ・情報管理(商品)初期表示画面:商品を登録する支出項目一覧、商品検索条件入力
 * ・情報管理(商品)検索結果画面
 * ・情報管理(商品)処理選択画面
 * ・情報管理(商品)更新画面
 * 
 * 画面遷移
 * ・情報管理(商品)初期表示画面：トップメニューからの遷移(初期表示)(GET)
 * ・情報管理(商品)検索結果画面：入力した検索条件に一致する検索結果を表示(POST)
 * ・検索画面のキャンセルボタンを選択時(POST)
 * ・情報管理(商品)処理選択画面：検索結果画面から任意の商品を選択時(GET)
 * ・情報管理(商品)更新画面：商品情報登録(対象の支出項目を選択して追加)(GET)
 * ・情報管理(商品)検索結果画面：選択した支出項目に属する商品の検索結果を表示(GET)
 * ・情報管理(商品)更新画面：商品情報登録(選択した商品と同一の支出項目で商品を追加アクションを選択時)(POST)
 * ・情報管理(商品)更新画面：商品情報更新アクションを選択時(POST)
 * ・処理選択画面でキャンセルボタンを選択時(POST)
 * ・追加・更新処理(POST)
 * ・支出項目情報登録・更新成功時→リダイレクト(GET)
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
	 * トップメニューからの遷移(初期表示)時のGETリクエストに対応します。
	 *</pre>
	 * @return 情報管理(商品)初期表示画面
	 *
	 */
	@GetMapping("/initload/")
	public ModelAndView getInitLoad() {
		log.debug("getInitLoad:");
		return this.usecase.readInitInfo(this.user).build();
	}
	
	/**
	 *<pre>
	 * 商品情報検索のPOST要求時マッピングです。
	 * 情報管理(商品)検索結果画面に遷移します。
	 *</pre>
	 * @param inputForm 入力フォーム情報
	 * @param bindingResult フォームのバリデーションチェック結果
	 * @return 情報管理(商品)検索結果画面
	 *
	 */
	@PostMapping(value = "/search/", params = "search")
	public ModelAndView postSearch(@ModelAttribute @Validated ShoppingItemInfoSearchForm inputForm, BindingResult bindingResult) {
		log.debug("postSearchLoad: input=" + inputForm);
		/* 入力フィールドのバリデーションチェック結果を判定 */
		// チェック結果エラーの場合
		if(bindingResult.hasErrors()) {
			// 初期表示情報を取得し、入力チェックエラーを設定
			return ShoppingItemInfoManageSearchResponse.buildBindingError(inputForm);
		// チェック結果OKの場合
		} else {
			// 検索を実行
			ShoppingItemInfoManageSearchResponse searchResult = this.usecase.execSearch(this.user, inputForm);
			if(searchResult.isShoppingItemListEmpty()) {
				// 検索結果なしの場合、初期表示画面に戻る
				ShoppingItemInfoManageInitResponse initResponse = this.usecase.readInitInfo(this.user);
				searchResult.getMessagesList().forEach(message -> initResponse.addMessage(message));
				return initResponse.build();
			} else {
				// 検索結果ありの場合、検索結果表示画面に遷移
				return searchResult.build();
			}
		}
	}
	
	/**
	 *<pre>
	 * 商品情報検索でキャンセルボタン選択時のPOST要求マッピングです。
	 * 情報管理(商品)初期表示画面に遷移します。
	 *</pre>
	 * @return 情報管理(商品)初期表示画面
	 *
	 */
	@PostMapping(value = "/search/", params = "searchCancel")
	public ModelAndView postSearchCancel() {
		log.debug("postSearchCancel:");
		return this.usecase.readInitInfo(this.user).build();
	}
	
	/**
	 *<pre>
	 * 検索結果画面から任意の商品を選択時のGET要求マッピングです。
	 * 情報管理(商品)処理選択画面に遷移します。
	 *</pre>
	 * @return 情報管理(商品)処理選択画面
	 *
	 */
	@GetMapping("/select")
	public ModelAndView getActSelect(@RequestParam("shoppingItemCode") String shoppingItemCode) {
		log.debug("getSelectShoppingItem: shoppingItemCode=" + shoppingItemCode);
		return this.usecase.readActSelectItemInfo(this.user, shoppingItemCode).build();
	}
	
	/**
	 *<pre>
	 * 対象の支出項目を選択して商品を追加時のGET要求マッピングです。
	 * 情報管理(商品)更新画面に遷移します。
	 *</pre>
	 * @return 情報管理(商品)更新画面
	 *
	 */
	@GetMapping("/addload")
	public ModelAndView getAddLoad(@RequestParam("sisyutuItemCode") String sisyutuItemCode) {
		log.debug("getAddLoad: sisyutuItemCode=" + sisyutuItemCode);
		return this.usecase.readAddShoppingItemInfoBySisyutuItem(this.user, sisyutuItemCode).build();
	}
	
	/**
	 *<pre>
	 * 対象の支出項目を選択して商品を検索時のGET要求マッピングです。
	 * 情報管理(商品)検索結果画面に遷移します。
	 *</pre>
	 * @return 情報管理(商品)検索結果画面
	 *
	 */
	@GetMapping("/searchbysisyutuitem")
	public ModelAndView getSearchBySisyutuItem(@RequestParam("sisyutuItemCode") String sisyutuItemCode) {
		log.debug("getSearchBySisyutuItem: sisyutuItemCode=" + sisyutuItemCode);
		
		// 検索を実行
		ShoppingItemInfoManageSearchResponse searchResult = this.usecase.execSearchBySisyutuItem(this.user, sisyutuItemCode);
		if(searchResult.isShoppingItemListEmpty()) {
			// 検索結果なしの場合、初期表示画面に戻る
			ShoppingItemInfoManageInitResponse initResponse = this.usecase.readInitInfo(this.user);
			searchResult.getMessagesList().forEach(message -> initResponse.addMessage(message));
			return initResponse.build();
		} else {
			// 検索結果ありの場合、検索結果表示画面に遷移
			return searchResult.build();
		}
	}
	
	/**
	 *<pre>
	 * 選択した商品と同一の情報で商品を追加時のPOST要求マッピングです。
	 * 情報管理(商品)更新画面に遷移します。
	 *</pre>
	 * @return 情報管理(商品)更新画面
	 *
	 */
	@PostMapping(value = "/updateload/", params = "actionAdd")
	public ModelAndView postActionAddLoad(@RequestParam("shoppingItemCode") String shoppingItemCode) {
		log.debug("postActionAddLoad: shoppingItemCode=" + shoppingItemCode);
		return this.usecase.readAddShoppingItemInfoByShoppingItem(this.user, shoppingItemCode).build();
	}
	
	/**
	 *<pre>
	 * 選択した商品に対して、更新操作を選択時のPOST要求マッピングです。
	 * 情報管理(商品)更新画面に遷移します。
	 *</pre>
	 * @return 情報管理(商品)更新画面
	 *
	 */
	@PostMapping(value="/updateload/", params = "actionUpdate")
	public ModelAndView postActionUpdateLoad(@RequestParam("shoppingItemCode") String shoppingItemCode) {
		log.debug("postActionUpdateLoad: shoppingItemCode=" + shoppingItemCode);
		return this.usecase.readUpdateShoppingItemInfo(this.user, shoppingItemCode).build();
	}
	
	/**
	 *<pre>
	 * 選択した商品に対して、キャンセルアクションを選択時のPOST要求マッピングです。
	 * 情報管理(商品)初期表示画面に遷移します。
	 *</pre>
	 * @return 情報管理(商品)初期表示画面
	 *
	 */
	@PostMapping(value = "/updateload/", params = "actionCancel")
	public ModelAndView postActionCancel() {
		log.debug("postActionCancel:");
		return this.usecase.readInitInfo(this.user).build();
	}
	
	/**
	 *<pre>
	 * 商品情報登録・更新のPOST要求時マッピングです。
	 *</pre>
	 * @param inputForm 入力フォーム情報
	 * @param bindingResult フォームのバリデーションチェック結果
	 * @param redirectAttributes リダイレクト先引き継ぎ領域
	 * @return 登録成功時：リダイレクト、登録失敗時:情報管理(商品)更新画面
	 *
	 */
	@PostMapping("/update/")
	public ModelAndView postUpdate(@ModelAttribute @Validated ShoppingItemInfoUpdateForm inputForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		log.debug("postUpdate: input=" + inputForm);
		/* 入力フィールドのバリデーションチェック結果を判定 */
		// チェック結果エラーの場合
		if(bindingResult.hasErrors()) {
			// 初期表示情報を取得し、入力チェックエラーを設定
			return this.usecase.readUpdateBindingErrorSetInfo(this.user, inputForm).build();
		// チェック結果OKの場合
		} else {
			/* hidden項目(action)の値チェック */
			// actionが未設定の場合、予期しないエラー
			if(!StringUtils.hasLength(inputForm.getAction())) {
				log.error("予期しないエラー actionのバリデーションチェックでエラー:action=" + inputForm.getAction());
				return ShoppingItemInfoManageUpdateResponse.buildBindingError("予期しないエラーが発生しました。管理者に問い合わせてください。[key=action]");
			// actionに従い、処理を実行
			} else {
				return this.usecase.execAction(this.user, inputForm).buildRedirect(redirectAttributes);
			}
		}
	}
	
	/**
	 *<pre>
	 * 商品情報登録・更新完了後のリダイレクト(Get要求時)のマッピングです。
	 *</pre>
	 * @param redirectMessages リダイレクト元から引き継いだメッセージ
	 * @return 情報管理(商品)初期表示画面
	 *
	 */
	@GetMapping("/updateComplete/")
	public ModelAndView updateComplete(@ModelAttribute CompleteRedirectMessages redirectMessages) {
		log.debug("updateComplete: input=" + redirectMessages);
		return this.usecase.readInitInfo(this.user).buildComplete(redirectMessages);
	}
}
