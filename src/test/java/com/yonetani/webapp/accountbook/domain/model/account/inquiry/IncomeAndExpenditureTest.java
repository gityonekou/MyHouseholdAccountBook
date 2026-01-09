/**
 * IncomeAndExpenditure（収支集約）のテストクラスです。
 *
 */
package com.yonetani.webapp.accountbook.domain.model.account.inquiry;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.domain.type.account.inquiry.ExpectedExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.TotalAvailableFunds;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.WithdrawingAmount;
import com.yonetani.webapp.accountbook.domain.type.common.BalanceAmount;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.RegularIncomeAmount;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

/**
 *<pre>
 * IncomeAndExpenditure（収支集約）のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
class IncomeAndExpenditureTest {

	@Test
	@DisplayName("正常系：reconstruct - すべてのフィールドが設定された収支集約を生成")
	void testReconstruct_AllFieldsSet() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth = TargetYearMonth.from("202511");
		RegularIncomeAmount regularIncome = RegularIncomeAmount.from(new BigDecimal("350000.00"));
		WithdrawingAmount withdrawing = WithdrawingAmount.from(new BigDecimal("50000.00"));
		ExpectedExpenditureAmount estimatedExpenditure = ExpectedExpenditureAmount.from(new BigDecimal("300000.00"));
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("280000.00"));
		BalanceAmount balance = BalanceAmount.from(new BigDecimal("120000.00"));

		// 実行
		IncomeAndExpenditure aggregate = IncomeAndExpenditure.reconstruct(
			userId,
			yearMonth,
			regularIncome,
			withdrawing,
			estimatedExpenditure,
			expenditure,
			balance
		);

		// 検証
		assertNotNull(aggregate);
		assertEquals(userId, aggregate.getUserId());
		assertEquals(yearMonth, aggregate.getTargetYearMonth());
		assertEquals(regularIncome, aggregate.getRegularIncomeAmount());
		assertEquals(withdrawing, aggregate.getWithdrawingAmount());
		assertEquals(estimatedExpenditure, aggregate.getExpectedExpenditureAmount());
		assertEquals(expenditure, aggregate.getExpenditureAmount());
		assertEquals(balance, aggregate.getBalanceAmount());
	}

	@Test
	@DisplayName("正常系：reconstruct - 積立取崩金額がnullの収支集約を生成")
	void testReconstruct_WithdrawingIsNull() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth = TargetYearMonth.from("202509");
		RegularIncomeAmount regularIncome = RegularIncomeAmount.from(new BigDecimal("400000.00"));
		ExpectedExpenditureAmount estimatedExpenditure = ExpectedExpenditureAmount.from(new BigDecimal("270000.00"));
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("250000.00"));
		BalanceAmount balance = BalanceAmount.from(new BigDecimal("150000.00"));

		// 実行
		IncomeAndExpenditure aggregate = IncomeAndExpenditure.reconstruct(
			userId,
			yearMonth,
			regularIncome,
			null,  // withdrawing
			estimatedExpenditure,
			expenditure,
			balance
		);

		// 検証
		assertNotNull(aggregate);
		assertNull(aggregate.getWithdrawingAmount());
		assertEquals(regularIncome, aggregate.getRegularIncomeAmount());
	}

	@Test
	@DisplayName("異常系：reconstruct - userIdがnullの場合、NullPointerExceptionをスロー")
	void testReconstruct_UserIdNull_ThrowsException() {
		// 準備
		TargetYearMonth yearMonth = TargetYearMonth.from("202511");
		RegularIncomeAmount income = RegularIncomeAmount.from(new BigDecimal("350000.00"));

		// 実行 & 検証
		assertThrows(NullPointerException.class, () -> {
			IncomeAndExpenditure.reconstruct(
				null,  // userId
				yearMonth,
				income,
				null,
				null,
				null,
				null
			);
		});
	}

	@Test
	@DisplayName("異常系：reconstruct - targetYearMonthがnullの場合、NullPointerExceptionをスロー")
	void testReconstruct_TargetYearMonthNull_ThrowsException() {
		// 準備
		UserId userId = UserId.from("user01");
		RegularIncomeAmount regularIncome = RegularIncomeAmount.from(new BigDecimal("350000.00"));

		// 実行 & 検証
		assertThrows(NullPointerException.class, () -> {
			IncomeAndExpenditure.reconstruct(
				userId,
				null,  // targetYearMonth
				regularIncome,
				null,
				null,
				null,
				null
			);
		});
	}

	@Test
	@DisplayName("正常系：empty - 空の収支集約を生成")
	void testEmpty() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth = TargetYearMonth.from("202501");

		// 実行
		IncomeAndExpenditure aggregate = IncomeAndExpenditure.empty(userId, yearMonth);

		// 検証
		assertNotNull(aggregate);
		assertEquals(userId, aggregate.getUserId());
		assertEquals(yearMonth, aggregate.getTargetYearMonth());
		assertNull(aggregate.getRegularIncomeAmount());
		assertNull(aggregate.getWithdrawingAmount());
		assertNull(aggregate.getExpectedExpenditureAmount());
		assertNull(aggregate.getExpenditureAmount());
		assertNull(aggregate.getBalanceAmount());
		assertTrue(aggregate.isEmpty());
		assertFalse(aggregate.isDataExists());
	}

	@Test
	@DisplayName("異常系：empty - userIdがnullの場合、NullPointerExceptionをスロー")
	void testEmpty_UserIdNull_ThrowsException() {
		// 準備
		TargetYearMonth yearMonth = TargetYearMonth.from("202501");

		// 実行 & 検証
		assertThrows(NullPointerException.class, () -> {
			IncomeAndExpenditure.empty(null, yearMonth);
		});
	}

	@Test
	@DisplayName("異常系：empty - targetYearMonthがnullの場合、NullPointerExceptionをスロー")
	void testEmpty_TargetYearMonthNull_ThrowsException() {
		// 準備
		UserId userId = UserId.from("user01");

		// 実行 & 検証
		assertThrows(NullPointerException.class, () -> {
			IncomeAndExpenditure.empty(userId, null);
		});
	}

	@Test
	@DisplayName("正常系：getTotalIncome - 収入と積立取崩の合計を取得")
	void testGetTotalIncome_Withdrawing() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth = TargetYearMonth.from("202511");
		RegularIncomeAmount income = RegularIncomeAmount.from(new BigDecimal("350000.00"));
		WithdrawingAmount withdrawing = WithdrawingAmount.from(new BigDecimal("50000.00"));

		// 実行
		IncomeAndExpenditure aggregate = IncomeAndExpenditure.reconstruct(
			userId, yearMonth, income, withdrawing, null, null, null
		);
		TotalAvailableFunds total = aggregate.getTotalIncome();

		// 検証
		assertEquals(new BigDecimal("400000.00"), total.getValue());
	}

	@Test
	@DisplayName("正常系：getTotalIncome - 積立取崩がnullの場合、収入金額のみ")
	void testGetTotalIncome_WithdrawingIsNull() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth = TargetYearMonth.from("202509");
		RegularIncomeAmount income = RegularIncomeAmount.from(new BigDecimal("400000.00"));

		// 実行
		IncomeAndExpenditure aggregate = IncomeAndExpenditure.reconstruct(
			userId, yearMonth, income, null, null, null, null
		);
		TotalAvailableFunds total = aggregate.getTotalIncome();

		// 検証
		assertEquals(new BigDecimal("400000.00"), total.getValue());
	}

	@Test
	@DisplayName("正常系：getTotalIncome - 収入と積立取崩がともにnullの場合、0を返す")
	void testGetTotalIncome_BothNull() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth = TargetYearMonth.from("202501");

		// 実行
		IncomeAndExpenditure aggregate = IncomeAndExpenditure.empty(userId, yearMonth);
		TotalAvailableFunds total = aggregate.getTotalIncome();

		// 検証
		assertEquals(new BigDecimal("0.00"), total.getValue());
	}

	@Test
	@DisplayName("正常系：isDataExists - 収入金額が存在する場合、trueを返す")
	void testIsDataExists_True() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth = TargetYearMonth.from("202511");
		RegularIncomeAmount income = RegularIncomeAmount.from(new BigDecimal("350000.00"));

		// 実行
		IncomeAndExpenditure aggregate = IncomeAndExpenditure.reconstruct(
			userId, yearMonth, income, null, null, null, null
		);

		// 検証
		assertTrue(aggregate.isDataExists());
		assertFalse(aggregate.isEmpty());
	}

	@Test
	@DisplayName("正常系：isDataExists - 収入金額がnullの場合、falseを返す")
	void testIsDataExists_False() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth = TargetYearMonth.from("202501");

		// 実行
		IncomeAndExpenditure aggregate = IncomeAndExpenditure.empty(userId, yearMonth);

		// 検証
		assertFalse(aggregate.isDataExists());
		assertTrue(aggregate.isEmpty());
	}

	@Test
	@DisplayName("正常系：equals - 同じ内容の収支集約は等しい")
	void testEquals_SameContent() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth = TargetYearMonth.from("202511");
		RegularIncomeAmount income = RegularIncomeAmount.from(new BigDecimal("350000.00"));
		WithdrawingAmount withdrawing = WithdrawingAmount.from(new BigDecimal("50000.00"));

		// 実行
		IncomeAndExpenditure aggregate1 = IncomeAndExpenditure.reconstruct(
			userId, yearMonth, income, withdrawing, null, null, null
		);
		IncomeAndExpenditure aggregate2 = IncomeAndExpenditure.reconstruct(
			userId, yearMonth, income, withdrawing, null, null, null
		);

		// 検証
		assertEquals(aggregate1, aggregate2);
		assertEquals(aggregate1.hashCode(), aggregate2.hashCode());
	}

	@Test
	@DisplayName("正常系：equals - 異なる内容の収支集約は等しくない")
	void testEquals_DifferentContent() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth1 = TargetYearMonth.from("202511");
		TargetYearMonth yearMonth2 = TargetYearMonth.from("202509");
		RegularIncomeAmount income = RegularIncomeAmount.from(new BigDecimal("350000.00"));

		// 実行
		IncomeAndExpenditure aggregate1 = IncomeAndExpenditure.reconstruct(
			userId, yearMonth1, income, null, null, null, null
		);
		IncomeAndExpenditure aggregate2 = IncomeAndExpenditure.reconstruct(
			userId, yearMonth2, income, null, null, null, null
		);

		// 検証
		assertNotEquals(aggregate1, aggregate2);
	}
}
