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

import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;
import com.yonetani.webapp.accountbook.presentation.session.IncomeAndExpenditureRegistInfo;

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
public class IncomeAndExpenditureRegistResponse extends AbstractResponse {
	
	/**
	 *<pre>
	 * 収支登録画面に表示する収入一覧の明細情報です
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public static class IncomeListItem {
		// 収入区分名
		private final String incomeKubunName;
		// 収入詳細
		private final String incomeDetailContext;
		// 収入金額
		private final String incomeKingaku;
		
	}
	
	/**
	 *<pre>
	 * 収支登録画面に表示する支出一覧の明細情報です
	 *
	 *</pre>
	 *
	 * @author ：Kouki Yonetani
	 * @since 家計簿アプリ(1.00.A)
	 *
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@Getter
	public static class ExpenditureListItem {
		// 支出項目名
		private final String sisyutuItemName;
		// 支出名
		private final String expenditureName;
		// 支出詳細
		private final String expenditureDetailContext;
		// 支払日
		private final String siharaiDate;
		// 支払金額
		private final String shiharaiKingaku;
	}
	
	// 収入情報
	private List<IncomeListItem> incomeListInfo = new ArrayList<>();
	// 支出情報
	private List<ExpenditureListItem> expenditureListInfo = new ArrayList<>();
	
	// セッションに設定する収支登録情報
	@Setter
	@Getter
	private IncomeAndExpenditureRegistInfo incomeAndExpenditureRegistInfo;
	
	/**
	 *<pre>
	 * デフォルト値からレスポンス情報を生成して返します。
	 *</pre>
	 * @return 収支登録画面表示情報
	 *
	 */
	public static IncomeAndExpenditureRegistResponse getInstance() {
		return new IncomeAndExpenditureRegistResponse();
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
		// 支出一覧情報
		modelAndView.addObject("expenditureListInfo", expenditureListInfo);
		
		return modelAndView;
	}

}
