/**
 * 収支登録情報のセッションスコープBeanです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2024/06/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.presentation.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import lombok.Data;

/**
 *<pre>
 * 収支登録情報のセッションスコープBeanです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.A)
 *
 */
@Component
@SessionScope
@Data
public class IncomeAndExpenditureRegistSession implements Serializable {
	private static final long serialVersionUID = 1L;
	
	// 収支の対象年月
	private String targetYearMonth;
	// 月度収支画面に戻るときに表示する対象年月
	private String returnYearMonth;
	// セッション管理する収入登録情報のリストです。
	private List<IncomeRegistItem> incomeRegistItemList = new ArrayList<IncomeRegistItem>();
	
	// セッション管理する支出登録情報のリストです。
	private List<ExpenditureRegistItem> expenditureRegistItemList = new ArrayList<ExpenditureRegistItem>();
	
	/**
	 *<pre>
	 * セッション管理している収支登録情報をクリアします。
	 *</pre>
	 * @param targetYearMonth 新規に設定する対象年月
	 * @param returnYearMonth 新規に設定する月度収支画面に戻るときに表示する対象年月
	 *
	 */
	public void clearData(String targetYearMonth, String returnYearMonth) {
		this.targetYearMonth = targetYearMonth;
		this.returnYearMonth = returnYearMonth;
		this.incomeRegistItemList = null;
		this.incomeRegistItemList = new ArrayList<IncomeRegistItem>();
		this.expenditureRegistItemList = null;
		this.expenditureRegistItemList = new ArrayList<ExpenditureRegistItem>();
	}
}
