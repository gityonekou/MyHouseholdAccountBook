/**
 * 収支登録画面と収支登録内容確認画面で表示する収入一覧、支出一覧の各情報を定義したレスポンス情報です。
 * 収支登録画面、収支登録内容確認画面の各レスポンスはこのクラスを継承して作成します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/08/31 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.account.regist;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.presentation.response.fw.AbstractResponse;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 *<pre>
 * 収支登録画面と収支登録内容確認画面で表示する収入一覧、支出一覧の各情報を定義したレスポンス情報です。
 * 収支登録画面、収支登録内容確認画面の各レスポンスはこのクラスを継承して作成します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractIncomeAndExpenditureRegistResponse extends AbstractResponse {
	
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
		// 支出項目名(＞で区切らない値なので注意)
		private final String sisyutuItemName;
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
		 * @param sisyutuItemName 支出項目名(＞で区切らない値なので注意)
		 * @param expenditureCode 支出コード(仮登録用支出コード)
		 * @param expenditureName 支出名と支出区分
		 * @param expenditureDetailContext 支出詳細
		 * @param siharaiDate 支払日
		 * @param shiharaiKingaku 支払金額
		 * @return 支出一覧情報
		 *
		 */
		public static ExpenditureListItem from(
				String sisyutuItemName,
				String expenditureCode,
				String expenditureName,
				String expenditureDetailContext,
				String siharaiDate,
				String shiharaiKingaku) {
			return new ExpenditureListItem(sisyutuItemName, expenditureCode, expenditureName, expenditureDetailContext,
					siharaiDate, shiharaiKingaku);
			
		}
	}
	
	// 「yyyy年MM月度」の年の値
	private String viewYear;
	// 「yyyy年MM月度」の月の値
	private String viewMonth;
	
	// 収入情報
	private List<IncomeListItem> incomeListInfo = new ArrayList<>();
	// 収入金額合計
	@Setter
	private String incomeSumKingaku;
	
	// 支出情報
	private List<ExpenditureListItem> expenditureListInfo = new ArrayList<>();
	// 支出金額合計
	@Setter
	private String expenditureSumKingaku;
	
	
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
	 * {@inheritDoc}
	 */
	@Override
	protected ModelAndView createModelAndView(String viewName) {
		ModelAndView modelAndView = super.createModelAndView(viewName);
		// 「yyyy年MM月度」の年の値
		modelAndView.addObject("viewYear", viewYear);
		// 「yyyy年MM月度」の月の値
		modelAndView.addObject("viewMonth", viewMonth);
		// 収入一覧情報
		modelAndView.addObject("incomeListInfo", incomeListInfo);
		// 収入金額合計
		modelAndView.addObject("incomeSumKingaku", incomeSumKingaku);
		// 支出一覧情報
		modelAndView.addObject("expenditureListInfo", expenditureListInfo);
		// 支出金額合計
		modelAndView.addObject("expenditureSumKingaku", expenditureSumKingaku);
		
		return modelAndView;
	}
	
	/**
	 *<pre>
	 * 対象年月(yyyyMM)の値を年と月に分割して「yyyy年MM月度」の年の値、「yyyy年MM月度」の月の値に設定します。
	 *</pre>
	 * @param targetYearMonth 対象年月(yyyyMM)
	 *
	 */
	protected void setYearMonth(String targetYearMonth) {
		if(!StringUtils.hasLength(targetYearMonth) || targetYearMonth.length() != 6) {
			throw new MyHouseholdAccountBookRuntimeException("対象年月の値が不正です。管理者に問い合わせてください。[targetYearMonth=" + targetYearMonth + "]");
		}
		// yyyyMMのyyyyの値を設定(年の値を設定)
		viewYear = targetYearMonth.substring(0, 4);
		// yyyyMMのMMの値を設定(月の値を設定)
		viewMonth = targetYearMonth.substring(4);
	}
}
