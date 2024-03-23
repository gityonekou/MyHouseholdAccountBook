/**
 * 支出項目情報管理画面を担当するコントローラーです。
 * 支出項目情報管理画面は以下3つの画面で構成されていますが、そのすべての画面遷移を子のコントローラーで
 * 管理します。
 * ・情報管理(支出項目)の対象選択画面
 * ・情報管理(支出項目)の処理選択画面
 * ・情報管理(支出項目)の更新画面
 * 
 * 画面遷移
 * ・情報管理(支出項目)の対象選択画面表示：トップメニューからの遷移(初期表示)(GET)
 * ・情報管理(支出項目)の処理選択画面表示：対象選択画面から更新対象の支出項目選択時(GET)
 * ・情報管理(支出項目)の更新画面表示：処理選択画面から追加アクションを選択時(POST)
 * ・情報管理(支出項目)の更新画面表示：処理選択画面から更新アクションを選択時(POST)
 * ・情報管理(支出項目)の更新画面表示：処理選択画面からキャンセルアクションを選択時(POST)
 * ・支出項目情報登録・更新(POST)
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

import com.yonetani.webapp.accountbook.application.usecase.itemmanage.ExpenditureItemInfoManageUseCase;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.ExpenditureItemInfoForm;
import com.yonetani.webapp.accountbook.presentation.request.session.UserSession;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ExpenditureItemInfoManageActSelectResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ExpenditureItemInfoManageInitResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.ExpenditureItemInfoManageUpdateResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 支出項目情報管理画面を担当するコントローラーです。
 * 支出項目情報管理画面は以下3つの画面で構成されていますが、そのすべての画面遷移を子のコントローラーで
 * 管理します。
 * ・情報管理(支出項目)の対象選択画面
 * ・情報管理(支出項目)の処理選択画面
 * ・情報管理(支出項目)の更新画面
 * 
 * 画面遷移
 * ・情報管理(支出項目)の対象選択画面表示：トップメニューからの遷移(初期表示)(GET)
 * ・情報管理(支出項目)の処理選択画面表示：対象選択画面から更新対象の支出項目選択時(GET)
 * ・情報管理(支出項目)の更新画面表示：処理選択画面からアクションを選択時(追加・更新・キャンセル)(POST)
 * ・支出項目情報登録・更新(POST)
 * ・支出項目情報登録・更新成功時→リダイレクト(GET)
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Controller
@RequestMapping("/myhacbook/managebaseinfo/expenditeminfo/")
@Log4j2
@RequiredArgsConstructor
public class ExpenditureItemInfoManageController {
	// UseCase
	private final ExpenditureItemInfoManageUseCase usecase;
	// UserSession
	private final UserSession user;
	
	/**
	 *<pre>
	 * 情報管理(支出項目)の対象選択画面表示のGET要求時マッピングです。
	 * トップメニューからの遷移(初期表示)時のGETリクエストに対応します。
	 * 情報管理(支出項目)の対象選択画面に遷移します。
	 *</pre>
	 * @return 情報管理(支出項目)の対象選択画面
	 *
	 */
	@GetMapping("/initload/")
	public ModelAndView getInitLoad() {
		log.debug("getInitLoad:");
		return this.usecase.readInitInfo(this.user).build();
	}
	
	/**
	 *<pre>
	 * 情報管理(支出項目)の対象選択画面で、更新対象の支出項目選択時のGET要求マッピングです。
	 * 情報管理(支出項目)の処理選択画面に遷移します。
	 *</pre>
	 * @param sisyutuItemCode 更新対象の支出項目コード
	 * @return 情報管理(支出項目)の処理選択画面
	 *
	 */
	@GetMapping("/actselect")
	public ModelAndView getActSelect(@RequestParam("sisyutuItemCode") String sisyutuItemCode) {
		log.debug("getActSelect:sisyutuItemCode=" + sisyutuItemCode);
		// 選択した支出項目情報のレスポンスを取得
		ExpenditureItemInfoManageActSelectResponse response = this.usecase.readActSelectItemInfo(this.user, sisyutuItemCode);
		// 選択した支出項目情報有無を判定
		if(response.isErrorResponse()) {
			// 支出項目情報なしの場合、エラーとしてinit画面に遷移
			ExpenditureItemInfoManageInitResponse initResponse = this.usecase.readInitInfo(this.user);
			response.getdMessagesList().forEach(message -> initResponse.addErrorMessage(message));
			return initResponse.build();
		} else {
			// 支出項目情報ありの場合、選択画面に遷移
			return response.build();
		}
	}
	
	/**
	 *<pre>
	 * 情報管理(支出項目)の処理選択画面で追加アクションを選択時のPOST要求マッピングです。
	 * 情報管理(支出項目)の更新画面に遷移します。
	 *</pre>
	 * @return 情報管理(支出項目)の更新画面
	 *
	 */
	@PostMapping(value = "/updateload/", params = "actionAdd")
	public ModelAndView postActionAddLoad(@RequestParam("sisyutuItemCode") String sisyutuItemCode) {
		log.debug("postActionAddLoad:sisyutuItemCode=" + sisyutuItemCode);
		return this.usecase.readAddExpenditureItemInfo(this.user, sisyutuItemCode).build();
	}
	
	/**
	 *<pre>
	 * 情報管理(支出項目)の処理選択画面で更新アクションを選択時のPOST要求マッピングです。
	 * 情報管理(支出項目)の更新画面に遷移します。
	 *</pre>
	 * @return 情報管理(支出項目)の更新画面
	 *
	 */
	@PostMapping(value="/updateload/", params = "actionUpdate")
	public ModelAndView postActionUpdateLoad(@RequestParam("sisyutuItemCode") String sisyutuItemCode) {
		log.debug("postActionUpdateLoad:sisyutuItemCode=" + sisyutuItemCode);
		return this.usecase.readUpdateExpenditureItemInfo(this.user, sisyutuItemCode).build();
	}
	
	/**
	 *<pre>
	 * 情報管理(支出項目)の処理選択画面でキャンセルアクションを選択時のPOST要求マッピングです。
	 * 情報管理(支出項目)の対象選択画面に遷移します。
	 *</pre>
	 * @return 情報管理(支出項目)の対象選択画面
	 *
	 */
	@PostMapping(value = "/updateload/", params = "actionCancel")
	public ModelAndView postActionCancelLoad() {
		log.debug("postActionCancelLoad:");
		return this.usecase.readInitInfo(this.user).build();
	}
	
	/**
	 *<pre>
	 * 支出項目情報登録・更新のPOST要求時マッピングです。
	 *</pre>
	 * @param shopForm 入力フォーム情報
	 * @param bindingResult フォームのバリデーションチェック結果
	 * @return 情報管理(支出項目)の更新画面
	 *
	 */
	@PostMapping("/update/")
	public ModelAndView postUpdate(@ModelAttribute @Validated ExpenditureItemInfoForm inputForm, BindingResult bindingResult) {
		log.debug("postUpdate:input=" + inputForm);
		/* 入力フィールドのバリデーションチェック結果を判定 */
		// チェック結果エラーの場合
		if(bindingResult.hasErrors()) {
			// 初期表示情報を取得し、入力チェックエラーを設定
			return this.usecase.readUpdateInitInfo(this.user).buildBindingError(inputForm);
		// チェック結果OKの場合
		} else {
			/* hidden項目(action)の値チェック */
			// actionが未設定の場合、予期しないエラー
			if(!StringUtils.hasLength(inputForm.getAction())) {
				log.error("予期しないエラー actionのバリデーションチェックでエラー:action=" + inputForm.getAction());
				return ExpenditureItemInfoManageUpdateResponse.buildBindingError("予期しないエラーが発生しました。管理者に問い合わせてください。[key=action]");
			// actionに従い、処理を実行
			} else {
				return this.usecase.execAction(this.user, inputForm).buildRedirect();
			}
		}
	}
	
	/**
	 *<pre>
	 * 支出項目情報登録・更新完了後のリダイレクト(Get要求時)のマッピングです。
	 *</pre>
	 * @return 情報管理(支出項目)の対象選択画面
	 *
	 */
	@GetMapping("/updateComplete/")
	public ModelAndView updateComplete() {
		log.debug("updateComplete:");
		return this.usecase.readInitInfo(this.user).buildComplete();
	}
}
