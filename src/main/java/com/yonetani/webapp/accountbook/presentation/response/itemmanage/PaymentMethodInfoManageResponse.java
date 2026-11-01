/**
 * 情報管理(支払方法)画面表示情報です。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 * 2026/10/18 : 1.00.01  feature-1.03-dev1   PaymentMethodListItem/PaymentMethodKubunOptionItem.from()型安全性対応(06-§8.4)
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.itemmanage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodCode;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodKubun;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodName;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.PaymentMethodSort;
import com.yonetani.webapp.accountbook.domain.type.common.EnableFlg;
import com.yonetani.webapp.accountbook.presentation.request.itemmanage.PaymentMethodInfoForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 情報管理(支払方法)画面表示情報です。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentMethodInfoManageResponse extends AbstractResponse {

	/**
	 *<pre>
	 * 支払方法種別選択肢の明細データです。value/textに加え、requiresAccount/requiresClosingDayを持ちます。
	 * 画面制御JSはJavaのenumを直接参照できないため、この2つの値をHTMLのdata属性経由でJSに渡します(突合レビュー指摘E)。
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
	public static class PaymentMethodKubunOptionItem {
		// value属性の値(支払方法種別コード)
		private final String value;
		// text属性の値(表示名)
		private final String text;
		// 銀行口座の紐づけが必須かどうか
		private final boolean requiresAccount;
		// 集計開始日の設定が必須かどうか
		private final boolean requiresClosingDay;

		/**
		 *<pre>
		 * 引数の値から支払方法種別選択肢の明細データを生成して返します。
		 *</pre>
		 * @param value 支払方法種別
		 * @param text 表示名
		 * @param requiresAccount 銀行口座の紐づけが必須かどうか
		 * @param requiresClosingDay 集計開始日の設定が必須かどうか
		 * @return 支払方法種別選択肢の明細データ
		 *
		 */
		public static PaymentMethodKubunOptionItem from(PaymentMethodKubun value, String text, boolean requiresAccount, boolean requiresClosingDay) {
			return new PaymentMethodKubunOptionItem(
					// value属性の値(支払方法種別コード)
					value.getValue(),
					// text属性の値(表示名)
					text,
					// 銀行口座の紐づけが必須かどうか
					requiresAccount,
					// 集計開始日の設定が必須かどうか
					requiresClosingDay);
		}
	}

	/**
	 *<pre>
	 * 支払方法一覧情報の明細データです
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
	public static class PaymentMethodListItem {
		// 支払方法コード
		private final String paymentMethodCode;
		// 支払方法名
		private final String paymentMethodName;
		// 支払方法種別名称
		private final String paymentMethodKubunName;
		// 銀行口座名(銀行口座なしの場合は「－」)
		private final String bankAccountName;
		// 集計開始日(未設定の場合は「－」)
		private final String closingDay;
		// 支払方法表示順
		private final String paymentMethodSort;
		// 有効/無効フラグ
		private final boolean enableFlg;

		/**
		 *<pre>
		 * 引数の値から支払方法一覧情報の明細データを生成して返します。
		 *</pre>
		 * @param paymentMethodCode 支払方法コード
		 * @param paymentMethodName 支払方法名
		 * @param paymentMethodKubunName 支払方法種別名称
		 * @param bankAccountName 銀行口座名
		 * @param closingDay 集計開始日
		 * @param paymentMethodSort 支払方法表示順
		 * @param enableFlg 有効/無効フラグ
		 * @return 支払方法一覧情報の明細データ
		 *
		 */
		public static PaymentMethodListItem from(PaymentMethodCode paymentMethodCode, PaymentMethodName paymentMethodName,
				String paymentMethodKubunName, String bankAccountName, String closingDay, PaymentMethodSort paymentMethodSort,
				EnableFlg enableFlg) {
			return new PaymentMethodListItem(
					// 支払方法コード
					paymentMethodCode.getValue(),
					// 支払方法名
					paymentMethodName.getValue(),
					// 支払方法種別名称
					paymentMethodKubunName,
					// 銀行口座名
					bankAccountName,
					// 集計開始日
					closingDay,
					// 支払方法表示順
					paymentMethodSort.getValue(),
					// 有効/無効フラグ
					enableFlg.getValue());
		}
	}

	// 支払方法情報入力フォーム
	private final PaymentMethodInfoForm paymentMethodInfoForm;
	// 支払方法種別選択肢
	private final List<PaymentMethodKubunOptionItem> paymentMethodKubunOptionList;
	// 銀行口座選択肢
	private final SelectViewItem bankAccountItem;
	// 集計開始日選択肢(紙芝居準拠のプルダウン用。1～28日＋未選択用の空値オプション)
	private final List<OptionItem> closingDayOptionList;
	// 支払方法一覧情報の明細データ(システム予約行を含まない)
	private List<PaymentMethodListItem> paymentMethodList = new ArrayList<>();

	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @param paymentMethodInfoForm 支払方法情報入力フォーム
	 * @param paymentMethodKubunOptionList 支払方法種別選択肢
	 * @param bankAccountOptionList 銀行口座選択肢のリスト
	 * @param closingDayOptionList 集計開始日選択肢のリスト
	 * @return 情報管理(支払方法)画面表示情報
	 *
	 */
	public static PaymentMethodInfoManageResponse getInstance(
			PaymentMethodInfoForm paymentMethodInfoForm,
			List<PaymentMethodKubunOptionItem> paymentMethodKubunOptionList,
			List<OptionItem> bankAccountOptionList,
			List<OptionItem> closingDayOptionList) {
		// 銀行口座選択肢のリストを生成(先頭に未選択用の「設定しない」を追加)
		List<OptionItem> optionList = new ArrayList<>();
		optionList.add(OptionItem.from("", "設定しない"));
		if(!CollectionUtils.isEmpty(bankAccountOptionList)) {
			optionList.addAll(bankAccountOptionList);
		}
		// 入力フォームと各選択肢の表示情報を元に情報管理(支払方法)画面表示情報を生成
		return new PaymentMethodInfoManageResponse(
				// 支払方法情報入力フォーム
				paymentMethodInfoForm,
				// 支払方法種別選択肢
				paymentMethodKubunOptionList,
				// 銀行口座選択肢
				SelectViewItem.from(optionList),
				// 集計開始日選択肢
				closingDayOptionList);
	}

	/**
	 *<pre>
	 * 支払方法一覧情報の明細リストを追加します。
	 *</pre>
	 * @param addList 追加する支払方法一覧情報の明細リスト
	 *
	 */
	public void addPaymentMethodList(List<PaymentMethodListItem> addList) {
		if(!CollectionUtils.isEmpty(addList)) {
			paymentMethodList.addAll(addList);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("itemmanage/PaymentMethodInfoManage");

		// 支払方法情報入力フォーム
		modelAndView.addObject("paymentMethodInfoForm", paymentMethodInfoForm);
		// 支払方法種別選択肢
		modelAndView.addObject("paymentMethodKubunOptions", paymentMethodKubunOptionList);
		// 銀行口座選択肢
		modelAndView.addObject("bankAccount", bankAccountItem);
		// 集計開始日選択肢
		modelAndView.addObject("closingDayOptions", closingDayOptionList);
		// 支払方法一覧情報
		modelAndView.addObject("paymentMethodList", paymentMethodList);

		return modelAndView;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * PaymentMethodInfoManageResponseで同メソッドを呼び出した後、PaymentMethodInfoManageResponse独自定義のメソッド
	 * を呼び出す必要があるためAbstractResponseの同メソッドをオーバーライド
	 * </pre>
	 */
	@Override
	public PaymentMethodInfoManageResponse setLoginUserName(String loginUserName) {
		super.setLoginUserName(loginUserName);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String buildRedirectUrl(RedirectAttributes redirectAttributes) {
		// 支払方法情報登録完了後、リダイレクトするURL
		return "redirect:/myhacbook/managebaseinfo/paymentmethodinfo/updateComplete/";
	}
}
