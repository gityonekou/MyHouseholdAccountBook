/**
 * 収支登録画面レスポンス情報です。
 * 収入一覧情報、支出一覧情報を持ちます。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.account.regist;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.ExpenditureItemForm;
import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.IncomeItemForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.session.ExpenditureRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.IncomeRegistItem;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *<pre>
 * 収支登録画面レスポンス情報です。
 * 収入一覧情報、支出一覧情報を持ちます。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class IncomeAndExpenditureRegistResponse extends AbstractResponse {
	
	/**
	 *<pre>
	 * 収支登録画面に表示する収入一覧情報です
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
	public static class IncomeListItem {
		// 収入コード(仮登録用収入コード)
		private final String incomeCode;
		// 収入区分名
		private final String incomeKubunName;
		// 収入詳細
		private final String incomeDetailContext;
		// 収入金額
		private final String incomeKingaku;
		
		/**
		 *<pre>
		 * 引数の値から収入一覧情報を生成して返します。
		 *</pre>
		 * @param incomeCode 収入コード(仮登録用収入コード)
		 * @param incomeKubunName 収入区分名
		 * @param incomeDetailContext 収入詳細
		 * @param incomeKingaku 収入金額
		 * @return 収入一覧情報
		 *
		 */
		public static IncomeListItem from(
				String incomeCode,
				String incomeKubunName,
				String incomeDetailContext,
				String incomeKingaku) {
			return new IncomeListItem(incomeCode, incomeKubunName, incomeDetailContext, incomeKingaku);
		}
	}
	
	/**
	 *<pre>
	 * 収支登録画面に表示する支出一覧情報です
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
	public static class ExpenditureListItem {
		// 支出コード(仮登録用支出コード)
		private final String expenditureCode;
		// 支出名と支出区分
		private final String expenditureName;
		// 支出詳細
		private final String expenditureDetailContext;
		// 支払日
		private final String siharaiDate;
		// 支払金額
		private final String shiharaiKingaku;
		
		/**
		 *<pre>
		 * 引数の値から支出一覧情報を生成して返します。
		 *</pre>
		 * @param expenditureCode 支出コード(仮登録用支出コード)
		 * @param expenditureName 支出名と支出区分
		 * @param expenditureDetailContext 支出詳細
		 * @param siharaiDate 支払日
		 * @param shiharaiKingaku 支払金額
		 * @return 支出一覧情報
		 *
		 */
		public static ExpenditureListItem from(
				String expenditureCode,
				String expenditureName,
				String expenditureDetailContext,
				String siharaiDate,
				String shiharaiKingaku) {
			return new ExpenditureListItem(expenditureCode, expenditureName, expenditureDetailContext,
					siharaiDate, shiharaiKingaku);
			
		}
	}
	
	// 収入情報
	private List<IncomeListItem> incomeListInfo = new ArrayList<>();
	// 収入金額合計
	@Setter
	private String incomeSumKingaku;
	// 収入情報入力フォーム
	private final IncomeItemForm incomeItemForm;
	// 収入区分選択ボックス
	private final SelectViewItem incomeKubunSelectList;
	
	// 支出情報
	private List<ExpenditureListItem> expenditureListInfo = new ArrayList<>();
	// 支出金額合計
	@Setter
	private String expenditureSumKingaku;
	// 支出情報入力フォーム
	private final ExpenditureItemForm expenditureItemForm;
	// 支出区分選択ボックス
	private final SelectViewItem expenditureKubunSelectList;
	
	// セッション管理する収入登録情報のリストです。
	@Setter
	@Getter
	private List<IncomeRegistItem> incomeRegistItemList;
	// セッション管理する支出登録情報のリストです。
	@Setter
	@Getter
	private List<ExpenditureRegistItem> expenditureRegistItemList;
	
	/**
	 *<pre>
	 * 収入一覧を追加します。
	 *</pre>
	 * @param addList 追加する収入一覧
	 *
	 */
	public void addIncomeListInfo(List<IncomeListItem> addList) {
		if(!CollectionUtils.isEmpty(addList)) {
			incomeListInfo.addAll(addList);
		}
	}
	
	/**
	 *<pre>
	 * 収入情報を収入一覧に追加します。
	 *</pre>
	 * @param addList 追加する収入情報
	 *
	 */
	public void addIncomeListItem(IncomeListItem addItem) {
		if(addItem != null) {
			incomeListInfo.add(addItem);
		}
	}
	
	/**
	 *<pre>
	 * 支出一覧を追加します。
	 *</pre>
	 * @param addList 追加する支出一覧
	 *
	 */
	public void addExpenditureListInfo(List<ExpenditureListItem> addList) {
		if(!CollectionUtils.isEmpty(addList)) {
			expenditureListInfo.addAll(addList);
		}
	}
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @return 収支登録画面表示情報
	 *
	 */
	public static IncomeAndExpenditureRegistResponse getInstance() {
		// 収入入力フォーム、収入区分選択ボックス、支出入力フォーム、支出区分選択ボックスなしで画面を表示
		return new IncomeAndExpenditureRegistResponse(null, null, null, null);
	}
	
	/**
	 *<pre>
	 * 収入入力フォームをもとにレスポンス情報を生成して返します。
	 * 支出入力フォームにはnullが設定されます。
	 *</pre>
	 * @param incomeItemForm 収入情報が格納されたフォームデータ
	 * @param addIncomeKubunList 収入区分選択ボックスの表示情報リスト
	 * @return 収支登録画面表示情報
	 *
	 */
	public static IncomeAndExpenditureRegistResponse getInstance(IncomeItemForm incomeItemForm, List<OptionItem> addIncomeKubunList) {
		// 収入情報フォームデータがnullなら空データを設定(アクションなしで処理継続となるので、後の登録ではエラーになる：継続処理可能)
		if(incomeItemForm == null) {
			incomeItemForm = new IncomeItemForm();
		}
		// 収入区分選択ボックスの表示情報リストを生成
		List<OptionItem> incomeKubunList = new ArrayList<>();
		incomeKubunList.add(OptionItem.from("", "収入区分を選択してください"));
		if(!CollectionUtils.isEmpty(addIncomeKubunList)) {
			incomeKubunList.addAll(addIncomeKubunList);
		}
		// 収入入力フォームの表示データを設定して画面を表示
		return new IncomeAndExpenditureRegistResponse(incomeItemForm, SelectViewItem.from(incomeKubunList), null, null);
	}
	
	/**
	 *<pre>
	 * 支出入力フォームをもとにレスポンス情報を生成して返します。
	 * 収入入力フォームにはnullが設定されます。
	 *</pre>
	 * @param expenditureItemForm 支出情報が格納されたフォームデータ
	 * @param addExpenditureKubunList 支出区分選択ボックスの表示情報リスト
	 * @return 収支登録画面表示情報
	 *
	 */
	public static IncomeAndExpenditureRegistResponse getInstance(
			ExpenditureItemForm expenditureItemForm, List<OptionItem> addExpenditureKubunList) {
		// 支出情報フォームデータがnullなら空データを設定(アクションなしで処理継続となるので、後の登録ではエラーになる：継続処理可能)
		if(expenditureItemForm == null) {
			expenditureItemForm = new ExpenditureItemForm();
		}
		// 支出区分選択ボックスの表示情報リストを生成
		List<OptionItem> expenditureKubunList = new ArrayList<>();
		expenditureKubunList.add(OptionItem.from("", "支出区分を選択してください"));
		if(!CollectionUtils.isEmpty(addExpenditureKubunList)) {
			expenditureKubunList.addAll(addExpenditureKubunList);
		}
		// 支出入力フォームの表示データを設定して画面を表示
		return new IncomeAndExpenditureRegistResponse(null, null, expenditureItemForm, SelectViewItem.from(expenditureKubunList));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("account/regist/IncomeAndExpenditureRegist");
		// 収入一覧情報
		modelAndView.addObject("incomeListInfo", incomeListInfo);
		// 収入金額合計
		modelAndView.addObject("incomeSumKingaku", incomeSumKingaku);
		// 収入登録フォーム
		modelAndView.addObject("incomeItemForm", incomeItemForm);
		// 収入区分選択ボックス
		modelAndView.addObject("incomeKubunSelectList", incomeKubunSelectList);
		// 支出一覧情報
		modelAndView.addObject("expenditureListInfo", expenditureListInfo);
		// 支出金額合計
		modelAndView.addObject("expenditureSumKingaku", expenditureSumKingaku);
		// 支出登録フォーム
		modelAndView.addObject("expenditureItemForm", expenditureItemForm);
		// 支出区分選択ボックス
		modelAndView.addObject("expenditureKubunSelectList", expenditureKubunSelectList);
		
		return modelAndView;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getRedirectUrl() {
		// 登録完了後、リダイレクトするURL(一覧画面)
		return "redirect:/myhacbook/accountregist/incomeandexpenditure/updateComplete/";
	}
}
