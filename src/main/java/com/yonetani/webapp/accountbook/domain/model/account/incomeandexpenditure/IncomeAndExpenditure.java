/**
 * 収支（IncomeAndExpenditure）集約のルートエンティティです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/05 : 1.00.00  新規作成
 * 2026/04/16 : 1.02.00  IncomeAndExpenditureItemを統合（登録・更新機能を追加）
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.incomeandexpenditure;

import java.math.BigDecimal;
import java.util.Objects;

import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.ExpectedExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.ExpectedExpenditureTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.TotalAvailableFunds;
import com.yonetani.webapp.accountbook.domain.type.account.incomeandexpenditure.WithdrawingAmount;
import com.yonetani.webapp.accountbook.domain.type.common.BalanceAmount;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.RegularIncomeAmount;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 *<pre>
 * 収支（IncomeAndExpenditure）集約のルートエンティティです。
 *
 * [責務]
 * ・収支情報の一貫性を保証
 * ・照会機能における収支情報の表現
 * ・登録・更新機能における収支テーブルへの操作
 * ・収支金額の計算（利用可能資金合計・収支金額）
 *
 * [設計方針]
 * ・不変性：すべてのフィールドをfinalにし、生成後は変更不可
 * ・整合性保証：コンストラクタで不正な状態を拒否
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
public class IncomeAndExpenditure {

	// ユーザID
	private final UserId userId;
	// 対象年月
	private final TargetYearMonth targetYearMonth;
	// 収入金額(積立金取崩金額以外の収入金額)
	private final RegularIncomeAmount regularIncomeAmount;
	// 積立金取崩金額
	private final WithdrawingAmount withdrawingAmount;
	// 支出予定金額
	private final ExpectedExpenditureAmount expectedExpenditureAmount;
	// 支出金額
	private final ExpenditureAmount expenditureAmount;
	// 収支金額
	private final BalanceAmount balanceAmount;

	/**
	 *<pre>
	 * 引数の値から収支テーブル情報のドメインモデルを生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYear 対象年
	 * @param targetMonth 対象月
	 * @param regularIncomeAmount 収入金額(積立金取崩金額以外の収入金額)
	 * @param withdrawingAmount 積立金取崩金額
	 * @param expectedExpenditureAmount 支出予定金額
	 * @param expenditureAmount 支出金額
	 * @param balanceAmount 収支金額
	 * @return 収支集約
	 *
	 */
	public static IncomeAndExpenditure from(
			String userId,
			String targetYear,
			String targetMonth,
			BigDecimal regularIncomeAmount,
			BigDecimal withdrawingAmount,
			BigDecimal expectedExpenditureAmount,
			BigDecimal expenditureAmount,
			BigDecimal balanceAmount) {
		return new IncomeAndExpenditure(
				UserId.from(userId),
				TargetYearMonth.from(targetYear, targetMonth),
				RegularIncomeAmount.from(regularIncomeAmount),
				WithdrawingAmount.from(withdrawingAmount),
				ExpectedExpenditureAmount.from(expectedExpenditureAmount),
				ExpenditureAmount.from(expenditureAmount),
				BalanceAmount.from(balanceAmount));
	}

	/**
	 *<pre>
	 * データベースから取得した収支データを再構成して集約を生成します。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYearMonth 対象年月
	 * @param regularIncomeAmount 収入金額(積立金取崩金額以外の収入金額)
	 * @param withdrawingAmount 積立金取崩金額
	 * @param estimatedExpenditureAmount 支出予定金額
	 * @param expenditureAmount 支出金額
	 * @param balanceAmount 収支金額
	 * @return 収支集約
	 *
	 */
	public static IncomeAndExpenditure reconstruct(
			UserId userId,
			TargetYearMonth targetYearMonth,
			RegularIncomeAmount regularIncomeAmount,
			WithdrawingAmount withdrawingAmount,
			ExpectedExpenditureAmount estimatedExpenditureAmount,
			ExpenditureAmount expenditureAmount,
			BalanceAmount balanceAmount) {

		// 不変条件の検証
		Objects.requireNonNull(userId, "userId must not be null");
		Objects.requireNonNull(targetYearMonth, "targetYearMonth must not be null");

		return new IncomeAndExpenditure(
			userId,
			targetYearMonth,
			regularIncomeAmount,
			withdrawingAmount,
			estimatedExpenditureAmount,
			expenditureAmount,
			balanceAmount
		);
	}

	/**
	 *<pre>
	 * 空の収支集約を生成します（データなし状態）。
	 *</pre>
	 * @param userId ユーザID
	 * @param targetYearMonth 対象年月
	 * @return 空の収支集約
	 *
	 */
	public static IncomeAndExpenditure empty(UserId userId, TargetYearMonth targetYearMonth) {
		// 不変条件の検証
		Objects.requireNonNull(userId, "userId must not be null");
		Objects.requireNonNull(targetYearMonth, "targetYearMonth must not be null");

		return new IncomeAndExpenditure(
			userId,
			targetYearMonth,
			null,  // regularIncomeAmount
			null,  // withdrawingAmount
			null,  // estimatedExpenditureAmount
			null,  // expenditureAmount
			null   // balanceAmount
		);
	}

	/**
	 *<pre>
	 * すべての項目が未設定の空の収支集約を生成します。
	 *</pre>
	 * @return 空の収支集約
	 *
	 */
	public static IncomeAndExpenditure fromEmpty() {
		return new IncomeAndExpenditure(null, null, null, null, null, null, null);
	}

	/**
	 *<pre>
	 * 対象月の収支テーブル情報を新規追加する場合の収支集約を生成して返します。
	 *</pre>
	 * @param userId ユーザID
	 * @param yearMonth 対象年月(ドメイン)
	 * @param regularIncomeAmount 対象月の収入金額(積立金取崩金額以外の収入金額)
	 * @param withdrawingAmount 対象月の積立金取崩金額
	 * @param expectedExpenditureAmount 対象月の支出予定金額
	 * @param expenditureAmount 対象月の支出金額
	 * @return 収支集約
	 *
	 */
	public static IncomeAndExpenditure createForAdd(
			UserId userId,
			TargetYearMonth yearMonth,
			RegularIncomeAmount regularIncomeAmount,
			WithdrawingAmount withdrawingAmount,
			ExpectedExpenditureAmount expectedExpenditureAmount,
			ExpenditureAmount expenditureAmount) {

		// 利用可能資金合計を計算
		TotalAvailableFunds availableFunds = TotalAvailableFunds.from(regularIncomeAmount, withdrawingAmount);
		// 収支金額 = 利用可能資金合計 - 支出金額
		BalanceAmount balance = BalanceAmount.calculate(availableFunds, expenditureAmount);

		return IncomeAndExpenditure.from(
				// ユーザID
				userId.getValue(),
				// 対象年
				yearMonth.getYear(),
				// 対象月
				yearMonth.getMonth(),
				// 収入金額(積立金取崩金額以外の収入金額)
				regularIncomeAmount.getValue(),
				// 積立金取崩金額
				withdrawingAmount.getValue(),
				// 支出予定金額
				expectedExpenditureAmount.getValue(),
				// 支出金額
				expenditureAmount.getValue(),
				// 収支金額
				balance.getValue());
	}

	/**
	 *<pre>
	 * 収支テーブルを更新する場合の収支集約を生成して返します。
	 *
	 * 注意：支出予定金額は新規登録以降は更新不可となるため、引数には含めていません。
	 * (支出予定金額は0円で設定され、DB更新時に該当項目を更新しません)
	 *</pre>
	 * @param userId ユーザID
	 * @param yearMonth 対象年月(ドメイン)
	 * @param regularIncomeAmount 対象月の収入金額(積立金取崩金額以外の収入金額)
	 * @param withdrawingAmount 対象月の積立金取崩金額
	 * @param expenditureAmount 対象月の支出金額
	 * @return 収支集約
	 *
	 */
	public static IncomeAndExpenditure createForUpdate(
			UserId userId,
			TargetYearMonth yearMonth,
			RegularIncomeAmount regularIncomeAmount,
			WithdrawingAmount withdrawingAmount,
			ExpenditureAmount expenditureAmount) {

		// 利用可能資金合計を計算
		TotalAvailableFunds availableFunds = TotalAvailableFunds.from(regularIncomeAmount, withdrawingAmount);
		// 収支金額 = 利用可能資金合計 - 支出金額
		BalanceAmount balance = BalanceAmount.calculate(availableFunds, expenditureAmount);

		return IncomeAndExpenditure.from(
				// ユーザID
				userId.getValue(),
				// 対象年
				yearMonth.getYear(),
				// 対象月
				yearMonth.getMonth(),
				// 収入金額(積立金取崩金額以外の収入金額)
				regularIncomeAmount.getValue(),
				// 積立金取崩金額
				withdrawingAmount.getValue(),
				// 支出予定金額
				ExpectedExpenditureTotalAmount.ZERO.getValue(),
				// 支出金額
				expenditureAmount.getValue(),
				// 収支金額
				balance.getValue());
	}

	/**
	 *<pre>
	 * 利用可能資金合計を取得します（通常収入 + 積立取崩）。
	 *</pre>
	 * @return 利用可能資金合計（通常収入 + 積立取崩）
	 *
	 */
	public TotalAvailableFunds getTotalIncome() {
		RegularIncomeAmount regularIncome = regularIncomeAmount != null ? regularIncomeAmount : RegularIncomeAmount.ZERO;
		WithdrawingAmount withdrawing = withdrawingAmount != null ? withdrawingAmount : WithdrawingAmount.ZERO;
		return TotalAvailableFunds.from(regularIncome, withdrawing);
	}

	/**
	 *<pre>
	 * 支出金額を加算した収支集約を返します。
	 *</pre>
	 * @param addValue 加算する支出金額
	 * @return 支出金額を加算した収支集約
	 *
	 */
	public IncomeAndExpenditure addExpenditureAmount(ExpenditureAmount addValue) {

		// 新しい支出金額
		ExpenditureAmount updExpenditureAmount = expenditureAmount.add(addValue);

		// 利用可能資金合計を計算
		TotalAvailableFunds availableFunds = TotalAvailableFunds.from(regularIncomeAmount, withdrawingAmount);

		// 収支金額を計算
		BalanceAmount balance = BalanceAmount.calculate(availableFunds, updExpenditureAmount);

		return IncomeAndExpenditure.from(
				// ユーザID
				userId.getValue(),
				// 対象年
				targetYearMonth.getYear(),
				// 対象月
				targetYearMonth.getMonth(),
				// 収入金額(積立金取崩金額以外の収入金額)
				regularIncomeAmount.getValue(),
				// 積立金取崩金額
				withdrawingAmount.getValue(),
				// 支出予定金額
				expectedExpenditureAmount.getValue(),
				// 支出金額
				updExpenditureAmount.getValue(),
				// 収支金額
				balance.getValue());
	}

	/**
	 *<pre>
	 * 支出金額を減算した収支集約を返します。
	 *</pre>
	 * @param subtractValue 減算する支出金額
	 * @return 支出金額を減算した収支集約
	 *
	 */
	public IncomeAndExpenditure subtractExpenditureAmount(ExpenditureAmount subtractValue) {

		// 新しい支出金額
		ExpenditureAmount updExpenditureAmount = expenditureAmount.subtract(subtractValue);

		// 利用可能資金合計を計算
		TotalAvailableFunds availableFunds = TotalAvailableFunds.from(regularIncomeAmount, withdrawingAmount);

		// 収支金額を計算
		BalanceAmount balance = BalanceAmount.calculate(availableFunds, updExpenditureAmount);

		return IncomeAndExpenditure.from(
				// ユーザID
				userId.getValue(),
				// 対象年
				targetYearMonth.getYear(),
				// 対象月
				targetYearMonth.getMonth(),
				// 収入金額(積立金取崩金額以外の収入金額)
				regularIncomeAmount.getValue(),
				// 積立金取崩金額
				withdrawingAmount.getValue(),
				// 支出予定金額
				expectedExpenditureAmount.getValue(),
				// 支出金額
				updExpenditureAmount.getValue(),
				// 収支金額
				balance.getValue());
	}

	/**
	 *<pre>
	 * データが存在するかどうかを判定します。
	 *</pre>
	 * @return データが存在する場合はtrue、存在しない場合はfalse
	 *
	 */
	public boolean isDataExists() {
		return balanceAmount != null;
	}

	/**
	 *<pre>
	 * データが空かどうかを判定します。
	 *</pre>
	 * @return データが空の場合はtrue、データがある場合はfalse
	 *
	 */
	public boolean isEmpty() {
		return !isDataExists();
	}
}
