/**
 * 収支の整合性を検証するドメインサービスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/05 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.service.account.inquiry;

import org.springframework.stereotype.Service;

import com.yonetani.webapp.accountbook.domain.exception.DataInconsistencyException;
import com.yonetani.webapp.accountbook.domain.exception.ExpenditureAmountInconsistencyException;
import com.yonetani.webapp.accountbook.domain.exception.IncomeAmountInconsistencyException;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.AccountMonthInquiryExpenditureItemList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditure;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.ExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeTableRepository;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingakuTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuunyuuKingakuTotalAmount;

import lombok.RequiredArgsConstructor;

/**
 *<pre>
 * 収支の整合性を検証するドメインサービスです。
 *
 * [責務]
 * ・収支テーブルと収入テーブルの整合性検証
 * ・収支テーブルと支出テーブルの整合性検証
 * ・複数のリポジトリにまたがる業務ルールの実行
 *
 * [なぜドメインサービスが必要か]
 * ・整合性チェックは明確なビジネスルールである
 * ・複数のリポジトリにまたがる処理のため、集約単体では完結しない
 * ・外部データ（他のテーブル）との比較が必要
 *
 * [使用箇所]
 * ・月次収支照会機能のユースケース層
 * ・収支データの整合性を保証する必要がある場面
 *
 * [Phase 2の責務範囲]
 * ・照会機能における整合性検証
 * ・収入金額の整合性チェック
 * ・支出金額の整合性チェック
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@Service
@RequiredArgsConstructor
public class IncomeAndExpenditureConsistencyService {

	// 収入テーブルリポジトリ
	private final IncomeTableRepository incomeRepository;
	// 支出テーブルリポジトリ
	private final ExpenditureTableRepository expenditureRepository;

	/**
	 *<pre>
	 * 収入金額の整合性を検証します。
	 *
	 * [ビジネスルール]
	 * ・収支テーブルの(収入金額 + 積立取崩金額) = 収入テーブルの合計金額
	 *
	 * [検証内容]
	 * 1. 収入テーブルから収入金額の合計を取得
	 * 2. 収支集約から収入合計（収入 + 積立取崩）を取得
	 * 3. 2つの値を比較し、一致しない場合は例外をスロー
	 *
	 * [例外]
	 * ・IncomeAmountInconsistencyException：整合性エラー
	 *
	 * [使用例]
	 * <code>
	 * IncomeAndExpenditure aggregate = ...;
	 * SearchQueryUserIdAndYearMonth searchCondition = ...;
	 * consistencyService.validateIncomeConsistency(aggregate, searchCondition);
	 * </code>
	 *</pre>
	 * @param aggregate 検証対象の収支集約
	 * @param searchCondition 検索条件（ユーザID、対象年月）
	 * @throws IncomeAmountInconsistencyException 整合性エラー
	 *
	 */
	public void validateIncomeConsistency(
			IncomeAndExpenditure aggregate,
			SearchQueryUserIdAndYearMonth searchCondition) {

		// 収入テーブルから合計金額を取得
		SyuunyuuKingakuTotalAmount actualTotal =
			incomeRepository.sumIncomeKingaku(searchCondition);

		// 収支集約から期待値を取得（収入 + 積立取崩）
		SyuunyuuKingakuTotalAmount expectedTotal = aggregate.getTotalIncome();

		// 整合性チェック
		if (!expectedTotal.equals(actualTotal)) {
			throw new IncomeAmountInconsistencyException(
				String.format(
					"収入金額が一致しません。yearMonth=%s, expected=%s, actual=%s",
					searchCondition.getYearMonth(),
					expectedTotal.getValue(),
					actualTotal.getValue()
				)
			);
		}
	}

	/**
	 *<pre>
	 * 支出金額の整合性を検証します。
	 *
	 * [ビジネスルール]
	 * ・収支テーブルの支出金額 = 支出テーブルの合計金額
	 *
	 * [検証内容]
	 * 1. 支出テーブルから支出金額の合計を取得
	 * 2. 収支集約から支出金額を取得
	 * 3. 2つの値を比較し、一致しない場合は例外をスロー
	 *
	 * [例外]
	 * ・ExpenditureAmountInconsistencyException：整合性エラー
	 *
	 * [使用例]
	 * <code>
	 * IncomeAndExpenditure aggregate = ...;
	 * SearchQueryUserIdAndYearMonth searchCondition = ...;
	 * consistencyService.validateExpenditureConsistency(aggregate, searchCondition);
	 * </code>
	 *</pre>
	 * @param aggregate 検証対象の収支集約
	 * @param searchCondition 検索条件（ユーザID、対象年月）
	 * @throws ExpenditureAmountInconsistencyException 整合性エラー
	 *
	 */
	public void validateExpenditureConsistency(
			IncomeAndExpenditure aggregate,
			SearchQueryUserIdAndYearMonth searchCondition) {

		// 支出テーブルから合計金額を取得
		SisyutuKingakuTotalAmount actualTotal =
			expenditureRepository.sumExpenditureKingaku(searchCondition);

		// 収支集約から期待値を取得
		ExpenditureAmount expectedAmount = aggregate.getExpenditureAmount();

		// 整合性チェック（値オブジェクト同士で比較）
		if (expectedAmount != null) {
			SisyutuKingakuTotalAmount expectedTotal = SisyutuKingakuTotalAmount.from(expectedAmount);
			if (!expectedTotal.equals(actualTotal)) {
				throw new ExpenditureAmountInconsistencyException(
					String.format(
						"支出金額が一致しません。yearMonth=%s, expected=%s, actual=%s",
						searchCondition.getYearMonth(),
						expectedTotal.getValue(),
						actualTotal.getValue()
					)
				);
			}
		}
	}

	/**
	 *<pre>
	 * データ存在の整合性を検証します。
	 *
	 * [ビジネスルール]
	 * ・収支データが存在しない場合、支出金額データも存在してはならない
	 *
	 * [検証内容]
	 * 収支集約が空（データなし）の場合に、支出金額リストにデータが存在する場合は
	 * データ不整合エラーとする。
	 *
	 * [例外]
	 * ・DataInconsistencyException：データ存在の整合性エラー
	 *
	 * [使用例]
	 * <code>
	 * IncomeAndExpenditure aggregate = ...;
	 * AccountMonthInquiryExpenditureItemList expenditureList = ...;
	 * SearchQueryUserIdAndYearMonth searchCondition = ...;
	 * consistencyService.validateDataExistence(aggregate, expenditureList, searchCondition);
	 * </code>
	 *</pre>
	 * @param aggregate 検証対象の収支集約
	 * @param expenditureList 支出金額情報リスト
	 * @param searchCondition 検索条件（ユーザID、対象年月）
	 * @throws DataInconsistencyException データ存在の整合性エラー
	 *
	 */
	public void validateDataExistence(
			IncomeAndExpenditure aggregate,
			AccountMonthInquiryExpenditureItemList expenditureList,
			SearchQueryUserIdAndYearMonth searchCondition) {

		// 収支データが存在しない場合で、支出金額データが存在する場合はエラー
		if (aggregate.isEmpty() && !expenditureList.isEmpty()) {
			throw new DataInconsistencyException(
				String.format(
					"該当月の収支データが未登録の状態で支出金額情報が登録済みの状態です。管理者に問い合わせてください。[yearMonth=%s]",
					searchCondition.getYearMonth()
				)
			);
		}
	}

	/**
	 *<pre>
	 * すべての整合性を一括検証します。
	 *
	 * [検証内容]
	 * 1. 収入金額の整合性検証
	 * 2. 支出金額の整合性検証
	 *
	 * [例外]
	 * ・IncomeAmountInconsistencyException：収入金額の整合性エラー
	 * ・ExpenditureAmountInconsistencyException：支出金額の整合性エラー
	 *
	 * [使用例]
	 * <code>
	 * IncomeAndExpenditure aggregate = ...;
	 * SearchQueryUserIdAndYearMonth searchCondition = ...;
	 * consistencyService.validateAll(aggregate, searchCondition);
	 * </code>
	 *</pre>
	 * @param aggregate 検証対象の収支集約
	 * @param searchCondition 検索条件（ユーザID、対象年月）
	 * @throws IncomeAmountInconsistencyException 収入金額の整合性エラー
	 * @throws ExpenditureAmountInconsistencyException 支出金額の整合性エラー
	 *
	 */
	public void validateAll(
			IncomeAndExpenditure aggregate,
			SearchQueryUserIdAndYearMonth searchCondition) {

		// 収入金額の整合性検証
		validateIncomeConsistency(aggregate, searchCondition);

		// 支出金額の整合性検証
		validateExpenditureConsistency(aggregate, searchCondition);
	}
}
