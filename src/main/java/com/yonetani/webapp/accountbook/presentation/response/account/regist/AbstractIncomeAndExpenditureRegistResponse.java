/**
 * 収支登録画面と収支登録内容確認画面で表示する収入一覧、支出一覧の各情報を定義したレスポンス情報です。
 * 収支登録画面、収支登録内容確認画面の各レスポンスはこのクラスを継承して作成します。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2024/08/31 : 1.00.00                      新規作成
 * 2026/03/20 : 1.01.00  feature-1.00-dev00  リファクタリング対応(DDD適応)
 * 2026/11/01 : 1.02.00  feature-1.03-dev1   支払方法・銀行口座管理追加対応(支出一覧に支払方法名・引落先口座名を追加)
 * 2026/11/08 : 1.02.01  feature-1.03-dev1   IncomeListItem/ExpenditureListItem.from()の引数を値オブジェクト型に変更(型安全性対応。06_コーディング規約.md §8)
 *
 */
package com.yonetani.webapp.accountbook.presentation.response.account.regist;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo.ExpenditureItemName;
import com.yonetani.webapp.accountbook.domain.type.account.income.IncomeDetailContext;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.ResolvedBankAccountName;
import com.yonetani.webapp.accountbook.domain.type.account.paymentmethod.ResolvedPaymentMethodName;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.IncomeAmount;

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
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractIncomeAndExpenditureRegistResponse extends AbstractRegistResponse {
	
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
		 * @param incomeCode 収入コード(仮登録用収入コード。セッション上の仮登録用コードはドメイン型IncomeCodeの桁数制約と不整合のためString)
		 * @param incomeKubunName 収入区分名(コードテーブルから解決済みの表示名のためString。収入区分コード自体のVOではない)
		 * @param incomeDetailContext 収入詳細
		 * @param incomeKingaku 収入金額
		 * @return 収入一覧情報
		 *
		 */
		public static IncomeListItem from(
				String incomeCode,
				String incomeKubunName,
				IncomeDetailContext incomeDetailContext,
				IncomeAmount incomeKingaku) {
			return new IncomeListItem(
					incomeCode,
					incomeKubunName,
					// 収入詳細(null許容)
					incomeDetailContext.getValue(),
					// 収入金額(フォーマット済み)
					incomeKingaku.toFormatString());
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
		// 支払方法名(解決済み。システム予約値の場合は「－」)
		private final String paymentMethodName;
		// 支払金額
		private final String shiharaiKingaku;
		// 引落先口座名(解決済み。銀行口座を持たない支払方法・システム予約値の場合は「－」)
		private final String bankAccountName;

		/**
		 *<pre>
		 * 引数の値から支出一覧情報を生成して返します。
		 *</pre>
		 * @param sisyutuItemName 支出項目名(＞で区切らない値なので注意)
		 * @param expenditureCode 支出コード(仮登録用支出コード。セッション上の仮登録用コードはドメイン型ExpenditureCodeの桁数制約と不整合のためString)
		 * @param expenditureName 支出名と支出区分(無駄遣い区分プレフィックス付与済みの合成文字列のためString)
		 * @param expenditureDetailContext 支出詳細
		 * @param siharaiDate 支払日(「日」付与済みの表示用文字列のため、対応するドメインVOが存在せずString)
		 * @param paymentMethodName 支払方法名(解決済み)
		 * @param shiharaiKingaku 支払金額
		 * @param bankAccountName 引落先口座名(解決済み)
		 * @return 支出一覧情報
		 *
		 */
		public static ExpenditureListItem from(
				ExpenditureItemName sisyutuItemName,
				String expenditureCode,
				String expenditureName,
				ExpenditureDetailContext expenditureDetailContext,
				String siharaiDate,
				ResolvedPaymentMethodName paymentMethodName,
				ExpenditureAmount shiharaiKingaku,
				ResolvedBankAccountName bankAccountName) {
			return new ExpenditureListItem(
					// 支出項目名(＞で区切らない値)
					sisyutuItemName.getValue(),
					expenditureCode, expenditureName,
					// 支出詳細(null許容)
					expenditureDetailContext.getValue(),
					siharaiDate,
					// 支払方法名(解決済み)
					paymentMethodName.getValue(),
					// 支払金額(フォーマット済み)
					shiharaiKingaku.toFormatString(),
					// 引落先口座名(解決済み)
					bankAccountName.getValue());

		}
	}
	
	// 収入情報
	@Getter
	private List<IncomeListItem> incomeListInfo = new ArrayList<>();
	// 収入金額合計
	@Setter
	@Getter
	private String incomeSumKingaku;

	// 支出情報
	@Getter
	private List<ExpenditureListItem> expenditureListInfo = new ArrayList<>();
	// 支出金額合計
	@Setter
	@Getter
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
}
