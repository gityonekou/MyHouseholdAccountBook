/**
 * 情報管理(銀行口座)画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/08/18 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yonetani.webapp.accountbook.presentation.request.itemmanage.BankAccountInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 情報管理(銀行口座)画面表示情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BankAccountInfoManageResponse extends AbstractResponse {

	/**
	 *<pre>
	 * 銀行口座一覧情報の明細データです
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.03)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	@EqualsAndHashCode
	public static class BankAccountListItem {
		// 銀行口座コード
		private final String bankAccountCode;
		// 銀行名
		private final String bankName;
		// 口座メモ
		private final String bankAccountMemo;
		// 銀行口座表示順
		private final String bankAccountSort;
		// 有効/無効フラグ
		private final boolean enableFlg;

		/**
		 *<pre>
		 * 引数の値から銀行口座一覧情報の明細データを生成して返します。
		 *</pre>
		 * @param bankAccountCode 銀行口座コード
		 * @param bankName 銀行名
		 * @param bankAccountMemo 口座メモ
		 * @param bankAccountSort 銀行口座表示順
		 * @param enableFlg 有効/無効フラグ
		 * @return 銀行口座一覧情報の明細データ
		 *
		 */
		public static BankAccountListItem from(String bankAccountCode, String bankName, String bankAccountMemo,
				String bankAccountSort, boolean enableFlg) {
			return new BankAccountListItem(bankAccountCode, bankName, bankAccountMemo, bankAccountSort, enableFlg);
		}
	}

	// 銀行口座情報入力フォーム
	private final BankAccountInfoForm bankAccountInfoForm;
	// 銀行口座一覧情報の明細データ
	private List<BankAccountListItem> bankAccountList = new ArrayList<>();

	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @param bankAccountInfoForm 銀行口座情報入力フォーム
	 * @return 情報管理(銀行口座)画面表示情報
	 *
	 */
	public static BankAccountInfoManageResponse getInstance(BankAccountInfoForm bankAccountInfoForm) {
		return new BankAccountInfoManageResponse(bankAccountInfoForm);
	}

	/**
	 *<pre>
	 * 銀行口座一覧情報の明細リストを追加します。
	 *</pre>
	 * @param addList 追加する銀行口座一覧情報の明細リスト
	 *
	 */
	public void addBankAccountList(List<BankAccountListItem> addList) {
		if(!CollectionUtils.isEmpty(addList)) {
			bankAccountList.addAll(addList);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("itemmanage/BankAccountInfoManage");

		// 銀行口座情報入力フォーム
		modelAndView.addObject("bankAccountInfoForm", bankAccountInfoForm);
		// 銀行口座一覧情報を設定
		modelAndView.addObject("bankAccountList", bankAccountList);

		return modelAndView;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * BankAccountInfoManageResponseで同メソッドを呼び出した後、BankAccountInfoManageResponse独自定義のメソッド
	 * を呼び出す必要があるためAbstractResponseの同メソッドをオーバーライド
	 * </pre>
	 */
	@Override
	public BankAccountInfoManageResponse setLoginUserName(String loginUserName) {
		super.setLoginUserName(loginUserName);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String buildRedirectUrl(RedirectAttributes redirectAttributes) {
		// 銀行口座情報登録完了後、リダイレクトするURL
		return "redirect:/myhacbook/managebaseinfo/bankaccountinfo/updateComplete/";
	}
}
