/**
 * 年間固定費合計画面のユースケースです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/05/23 : 1.01.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.application.usecase.itemmanage.fixedcost;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCostAnnualSummaryList;
import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCostAnnualSummaryList.AnnualSummaryColumn;
import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCostAnnualSummaryList.MonthlyRow;
import com.yonetani.webapp.accountbook.domain.model.account.fixedcost.FixedCostAnnualSummaryList.YearlyRow;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserId;
import com.yonetani.webapp.accountbook.domain.repository.account.fixedcost.FixedCostTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.common.AccountBookUserRepository;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.fixedcost.FixedCostAnnualSummaryResponse;
import com.yonetani.webapp.accountbook.presentation.response.itemmanage.fixedcost.FixedCostAnnualSummaryResponse.AnnualSummaryRowItem;
import com.yonetani.webapp.accountbook.presentation.session.LoginUserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *<pre>
 * 年間固定費合計画面のユースケースです。
 * 固定費情報を月別・カテゴリ別に集計し、年間固定費合計画面の表示データを提供します。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.01)
 *
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class FixedCostAnnualSummaryUseCase {

	// 固定費テーブル:FIXED_COST_TABLEリポジトリー
	private final FixedCostTableRepository fixedCostRepository;
	// 家計簿ユーザーリポジトリー
	private final AccountBookUserRepository accountBookUserRepository;

	/**
	 *<pre>
	 * 年間固定費合計画面の表示情報を取得します。
	 * 固定費情報を月別・カテゴリ別に集計し、12行のデータ行と合計行を含む表示情報を返します。
	 *</pre>
	 * @param user ログインユーザ情報
	 * @return 年間固定費合計画面の表示情報
	 *
	 */
	public FixedCostAnnualSummaryResponse readAnnualSummaryInfo(LoginUserInfo user) {
		log.debug("readAnnualSummaryInfo:userid=" + user.getUserId());

		UserId userId = UserId.from(user.getUserId());

		// 現在の対象年月を取得
		TargetYearMonth targetYearMonth = accountBookUserRepository.getTargetYearMonth(
				SearchQueryUserId.from(userId));

		// 年間固定費合計データを取得
		FixedCostAnnualSummaryList summaryList = fixedCostRepository.findForAnnualSummaryByUserId(
				SearchQueryUserId.from(userId));

		// レスポンス生成（現在対象月を月別固定費一覧タブリンク用に渡す）
		FixedCostAnnualSummaryResponse response = FixedCostAnnualSummaryResponse.getInstance(
				targetYearMonth.getMonth());

		if (summaryList.isEmpty()) {
			response.addMessage("登録済み固定費情報が0件です。");
		} else {
			response.setAnnualSummaryRowList(
					createAnnualSummaryRowList(summaryList.buildMonthlyRows(), summaryList.buildYearlyRow()));
		}
		return response;
	}

	/**
	 *<pre>
	 * 月別固定費合計(MonthlyRow)リストと年間合計(YearlyRow)から、画面表示用の年間固定費合計行リストを生成して返します。
	 * 12か月分のデータ行と年間合計行の計13行を含むリストを返します。
	 *</pre>
	 * @param monthlyRows 月別固定費合計データのリスト（12件）
	 * @param yearlyRow 年間固定費合計データ
	 * @return 画面表示用の年間固定費合計行リスト（12件のデータ行 + 1件の合計行）
	 *
	 */
	private List<AnnualSummaryRowItem> createAnnualSummaryRowList(List<MonthlyRow> monthlyRows, YearlyRow yearlyRow) {
		List<AnnualSummaryRowItem> rows = new ArrayList<>();
		for (MonthlyRow row : monthlyRows) {
			rows.add(createDataRow(row));
		}
		rows.add(createTotalRow(yearlyRow));
		return rows;
	}

	/**
	 *<pre>
	 * 月別固定費合計データ(MonthlyRow)から、画面表示用のデータ行(AnnualSummaryRowItem)を生成して返します。
	 * 各カテゴリの金額は0円の場合「ー」で表示されます。
	 *</pre>
	 * @param row 月別固定費合計データ
	 * @return 画面表示用の年間固定費合計データ行
	 *
	 */
	private AnnualSummaryRowItem createDataRow(MonthlyRow row) {
		return AnnualSummaryRowItem.createDataRow(
				String.format("%d月", row.getMonth()),
				row.getAmount(AnnualSummaryColumn.JIGYOU_KEIHI).toZeroDashString(),
				row.getAmount(AnnualSummaryColumn.HIKOZEI).toZeroDashString(),
				row.getAmount(AnnualSummaryColumn.SEIKATSUHI).toZeroDashString(),
				row.getAmount(AnnualSummaryColumn.TSUMITATE_TOUSHI).toZeroDashString(),
				row.getAmount(AnnualSummaryColumn.TSUMITATE_KIN).toZeroDashString(),
				row.getAmount(AnnualSummaryColumn.IRUI_JUKYO).toZeroDashString(),
				row.getAmount(AnnualSummaryColumn.INSHOKU).toZeroDashString(),
				row.getAmount(AnnualSummaryColumn.SHUMI).toZeroDashString(),
				row.getMonthTotal().toZeroDashString(),
				String.format("%02d", row.getMonth()));
	}

	/**
	 *<pre>
	 * 年間固定費合計データ(YearlyRow)から、画面表示用の合計行(AnnualSummaryRowItem)を生成して返します。
	 * 各カテゴリの年間合計金額は0円の場合「ー」で表示されます。
	 *</pre>
	 * @param yearlyRow 年間固定費合計データ
	 * @return 画面表示用の年間固定費合計行
	 *
	 */
	private AnnualSummaryRowItem createTotalRow(YearlyRow yearlyRow) {
		return AnnualSummaryRowItem.createTotalRow(
				yearlyRow.getAmount(AnnualSummaryColumn.JIGYOU_KEIHI).toZeroDashString(),
				yearlyRow.getAmount(AnnualSummaryColumn.HIKOZEI).toZeroDashString(),
				yearlyRow.getAmount(AnnualSummaryColumn.SEIKATSUHI).toZeroDashString(),
				yearlyRow.getAmount(AnnualSummaryColumn.TSUMITATE_TOUSHI).toZeroDashString(),
				yearlyRow.getAmount(AnnualSummaryColumn.TSUMITATE_KIN).toZeroDashString(),
				yearlyRow.getAmount(AnnualSummaryColumn.IRUI_JUKYO).toZeroDashString(),
				yearlyRow.getAmount(AnnualSummaryColumn.INSHOKU).toZeroDashString(),
				yearlyRow.getAmount(AnnualSummaryColumn.SHUMI).toZeroDashString(),
				yearlyRow.getYearTotal().toZeroDashString());
	}
}
