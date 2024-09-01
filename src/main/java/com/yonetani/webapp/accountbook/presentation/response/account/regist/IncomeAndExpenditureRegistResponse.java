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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.ExpenditureItemForm;
import com.yonetani.webapp.accountbook.presentation.request.account.inquiry.IncomeItemForm;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem;
import com.yonetani.webapp.accountbook.presentation.response.fw.SelectViewItem.OptionItem;
import com.yonetani.webapp.accountbook.presentation.session.ExpenditureRegistItem;
import com.yonetani.webapp.accountbook.presentation.session.IncomeRegistItem;

import lombok.AccessLevel;
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
public class IncomeAndExpenditureRegistResponse extends AbstractIncomeAndExpenditureRegistResponse {
	
	// 収入情報入力フォーム
	private final IncomeItemForm incomeItemForm;
	// 収入区分選択ボックス
	private final SelectViewItem incomeKubunSelectList;
	
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
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @param targetYearMonth 収支対象の年月(YYYMM)
	 * @return 収支登録画面表示情報
	 *
	 */
	public static IncomeAndExpenditureRegistResponse getInstance(String targetYearMonth) {
		// 収入入力フォーム、収入区分選択ボックス、支出入力フォーム、支出区分選択ボックスなしで画面を表示
		IncomeAndExpenditureRegistResponse response = new IncomeAndExpenditureRegistResponse(null, null, null, null);
		response.setYearMonth(targetYearMonth);
		return response;
	}
	
	/**
	 *<pre>
	 * 収入入力フォームをもとにレスポンス情報を生成して返します。
	 * 支出入力フォームにはnullが設定されます。
	 *</pre>
	 * @param targetYearMonth 収支対象の年月(YYYMM)
	 * @param incomeItemForm 収入情報が格納されたフォームデータ
	 * @param addIncomeKubunList 収入区分選択ボックスの表示情報リスト
	 * @return 収支登録画面表示情報
	 *
	 */
	public static IncomeAndExpenditureRegistResponse getInstance(String targetYearMonth,
			IncomeItemForm incomeItemForm, List<OptionItem> addIncomeKubunList) {
		
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
		IncomeAndExpenditureRegistResponse response = new IncomeAndExpenditureRegistResponse(incomeItemForm,
				SelectViewItem.from(incomeKubunList), null, null);
		response.setYearMonth(targetYearMonth);
		return response;
	}
	
	/**
	 *<pre>
	 * 支出入力フォームをもとにレスポンス情報を生成して返します。
	 * 収入入力フォームにはnullが設定されます。
	 *</pre>
	 * @param targetYearMonth 収支対象の年月(YYYMM)
	 * @param expenditureItemForm 支出情報が格納されたフォームデータ
	 * @param addExpenditureKubunList 支出区分選択ボックスの表示情報リスト
	 * @return 収支登録画面表示情報
	 *
	 */
	public static IncomeAndExpenditureRegistResponse getInstance(String targetYearMonth, 
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
		IncomeAndExpenditureRegistResponse response = new IncomeAndExpenditureRegistResponse(null, null,expenditureItemForm, SelectViewItem.from(expenditureKubunList));
		response.setYearMonth(targetYearMonth);
		return response;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelAndView build() {
		// 画面表示のModelとViewを生成
		ModelAndView modelAndView = createModelAndView("account/regist/IncomeAndExpenditureRegist");
		// 収入登録フォーム
		modelAndView.addObject("incomeItemForm", incomeItemForm);
		// 収入区分選択ボックス
		modelAndView.addObject("incomeKubunSelectList", incomeKubunSelectList);
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
	protected String buildRedirectUrl(RedirectAttributes redirectAttributes) {
		// 登録完了後、リダイレクトするURL(一覧画面)
		return "redirect:/myhacbook/accountregist/incomeandexpenditure/updateComplete/";
	}
	
}
