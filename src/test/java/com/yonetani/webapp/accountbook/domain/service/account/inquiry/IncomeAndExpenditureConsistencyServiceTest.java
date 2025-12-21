/**
 * IncomeAndExpenditureConsistencyService（収支整合性検証サービス）のテストクラスです。
 *
 */
package com.yonetani.webapp.accountbook.domain.service.account.inquiry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.yonetani.webapp.accountbook.domain.exception.DataInconsistencyException;
import com.yonetani.webapp.accountbook.domain.exception.ExpenditureAmountInconsistencyException;
import com.yonetani.webapp.accountbook.domain.exception.IncomeAmountInconsistencyException;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.AccountMonthInquiryExpenditureItemList;
import com.yonetani.webapp.accountbook.domain.model.account.inquiry.IncomeAndExpenditure;
import com.yonetani.webapp.accountbook.domain.model.searchquery.SearchQueryUserIdAndYearMonth;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.ExpenditureTableRepository;
import com.yonetani.webapp.accountbook.domain.repository.account.inquiry.IncomeTableRepository;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SisyutuKingakuTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.SyuunyuuKingakuTotalAmount;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;
import com.yonetani.webapp.accountbook.domain.type.common.IncomeAmount;
import com.yonetani.webapp.accountbook.domain.type.common.TargetYearMonth;
import com.yonetani.webapp.accountbook.domain.type.common.UserId;

/**
 *<pre>
 * IncomeAndExpenditureConsistencyService（収支整合性検証サービス）のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@ExtendWith(MockitoExtension.class)
class IncomeAndExpenditureConsistencyServiceTest {

	@Mock
	private IncomeTableRepository incomeRepository;

	@Mock
	private ExpenditureTableRepository expenditureRepository;

	@InjectMocks
	private IncomeAndExpenditureConsistencyService service;

	@Test
	@DisplayName("正常系：validateIncomeConsistency - 収入金額が一致する場合、例外をスローしない")
	void testValidateIncomeConsistency_Success() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth = TargetYearMonth.from("202511");
		SearchQueryUserIdAndYearMonth searchCondition =
			SearchQueryUserIdAndYearMonth.from(userId, yearMonth);

		// 収支集約：収入350,000 + 積立取崩50,000 = 合計400,000
		IncomeAmount income = IncomeAmount.from(new BigDecimal("350000.00"));
		IncomeAndExpenditure aggregate = IncomeAndExpenditure.reconstruct(
			userId, yearMonth, income,
			com.yonetani.webapp.accountbook.domain.type.account.inquiry.WithdrewKingaku.from(new BigDecimal("50000.00")),
			null, null, null
		);

		// モック設定：収入テーブルの合計も400,000
		when(incomeRepository.sumIncomeKingaku(searchCondition))
			.thenReturn(SyuunyuuKingakuTotalAmount.from(new BigDecimal("400000.00")));

		// 実行 & 検証（例外がスローされないことを確認）
		assertDoesNotThrow(() -> service.validateIncomeConsistency(aggregate, searchCondition));

		// モックの呼び出し確認
		verify(incomeRepository, times(1)).sumIncomeKingaku(searchCondition);
	}

	@Test
	@DisplayName("異常系：validateIncomeConsistency - 収入金額が一致しない場合、例外をスロー")
	void testValidateIncomeConsistency_Inconsistent_ThrowsException() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth = TargetYearMonth.from("202511");
		SearchQueryUserIdAndYearMonth searchCondition =
			SearchQueryUserIdAndYearMonth.from(userId, yearMonth);

		// 収支集約：収入350,000 + 積立取崩50,000 = 合計400,000
		IncomeAmount income = IncomeAmount.from(new BigDecimal("350000.00"));
		IncomeAndExpenditure aggregate = IncomeAndExpenditure.reconstruct(
			userId, yearMonth, income,
			com.yonetani.webapp.accountbook.domain.type.account.inquiry.WithdrewKingaku.from(new BigDecimal("50000.00")),
			null, null, null
		);

		// モック設定：収入テーブルの合計は420,000（不整合）
		when(incomeRepository.sumIncomeKingaku(searchCondition))
			.thenReturn(SyuunyuuKingakuTotalAmount.from(new BigDecimal("420000.00")));

		// 実行 & 検証
		IncomeAmountInconsistencyException exception = assertThrows(
			IncomeAmountInconsistencyException.class,
			() -> service.validateIncomeConsistency(aggregate, searchCondition)
		);

		// 例外メッセージの検証
		assertTrue(exception.getMessage().contains("収入金額が一致しません"));
		assertTrue(exception.getMessage().contains("202511"));
		assertTrue(exception.getMessage().contains("400000.00"));  // expected
		assertTrue(exception.getMessage().contains("420000.00"));  // actual

		// モックの呼び出し確認
		verify(incomeRepository, times(1)).sumIncomeKingaku(searchCondition);
	}

	@Test
	@DisplayName("正常系：validateExpenditureConsistency - 支出金額が一致する場合、例外をスローしない")
	void testValidateExpenditureConsistency_Success() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth = TargetYearMonth.from("202511");
		SearchQueryUserIdAndYearMonth searchCondition =
			SearchQueryUserIdAndYearMonth.from(userId, yearMonth);

		// 収支集約：支出280,000
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("280000.00"));
		IncomeAndExpenditure aggregate = IncomeAndExpenditure.reconstruct(
			userId, yearMonth, null, null, null, expenditure, null
		);

		// モック設定：支出テーブルの合計も280,000
		when(expenditureRepository.sumExpenditureKingaku(searchCondition))
			.thenReturn(SisyutuKingakuTotalAmount.from(new BigDecimal("280000.00")));

		// 実行 & 検証（例外がスローされないことを確認）
		assertDoesNotThrow(() -> service.validateExpenditureConsistency(aggregate, searchCondition));

		// モックの呼び出し確認
		verify(expenditureRepository, times(1)).sumExpenditureKingaku(searchCondition);
	}

	@Test
	@DisplayName("異常系：validateExpenditureConsistency - 支出金額が一致しない場合、例外をスロー")
	void testValidateExpenditureConsistency_Inconsistent_ThrowsException() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth = TargetYearMonth.from("202511");
		SearchQueryUserIdAndYearMonth searchCondition =
			SearchQueryUserIdAndYearMonth.from(userId, yearMonth);

		// 収支集約：支出280,000
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("280000.00"));
		IncomeAndExpenditure aggregate = IncomeAndExpenditure.reconstruct(
			userId, yearMonth, null, null, null, expenditure, null
		);

		// モック設定：支出テーブルの合計は300,000（不整合）
		when(expenditureRepository.sumExpenditureKingaku(searchCondition))
			.thenReturn(SisyutuKingakuTotalAmount.from(new BigDecimal("300000.00")));

		// 実行 & 検証
		ExpenditureAmountInconsistencyException exception = assertThrows(
			ExpenditureAmountInconsistencyException.class,
			() -> service.validateExpenditureConsistency(aggregate, searchCondition)
		);

		// 例外メッセージの検証
		assertTrue(exception.getMessage().contains("支出金額が一致しません"));
		assertTrue(exception.getMessage().contains("202511"));
		assertTrue(exception.getMessage().contains("280000.00"));  // expected
		assertTrue(exception.getMessage().contains("300000.00"));  // actual

		// モックの呼び出し確認
		verify(expenditureRepository, times(1)).sumExpenditureKingaku(searchCondition);
	}

	@Test
	@DisplayName("正常系：validateDataExistence - 収支データあり、支出データなしの場合、例外をスローしない")
	void testValidateDataExistence_IncomeExistsExpenditureEmpty_Success() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth = TargetYearMonth.from("202511");
		SearchQueryUserIdAndYearMonth searchCondition =
			SearchQueryUserIdAndYearMonth.from(userId, yearMonth);

		// 収支集約：データあり
		IncomeAmount income = IncomeAmount.from(new BigDecimal("350000.00"));
		IncomeAndExpenditure aggregate = IncomeAndExpenditure.reconstruct(
			userId, yearMonth, income, null, null, null, null
		);

		// 支出リスト：空
		AccountMonthInquiryExpenditureItemList expenditureList =
			AccountMonthInquiryExpenditureItemList.from(Collections.emptyList());

		// 実行 & 検証（例外がスローされないことを確認）
		assertDoesNotThrow(() -> service.validateDataExistence(aggregate, expenditureList, searchCondition));
	}

	@Test
	@DisplayName("正常系：validateDataExistence - 収支データあり、支出データありの場合、例外をスローしない")
	void testValidateDataExistence_BothExist_Success() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth = TargetYearMonth.from("202511");
		SearchQueryUserIdAndYearMonth searchCondition =
			SearchQueryUserIdAndYearMonth.from(userId, yearMonth);

		// 収支集約：データあり
		IncomeAmount income = IncomeAmount.from(new BigDecimal("350000.00"));
		IncomeAndExpenditure aggregate = IncomeAndExpenditure.reconstruct(
			userId, yearMonth, income, null, null, null, null
		);

		// 支出リスト：データあり（モックで作成）
		List<AccountMonthInquiryExpenditureItemList.ExpenditureListItem> items = new ArrayList<>();
		AccountMonthInquiryExpenditureItemList.ExpenditureListItem item =
			mock(AccountMonthInquiryExpenditureItemList.ExpenditureListItem.class);
		items.add(item);
		AccountMonthInquiryExpenditureItemList expenditureList =
			AccountMonthInquiryExpenditureItemList.from(items);

		// 実行 & 検証（例外がスローされないことを確認）
		assertDoesNotThrow(() -> service.validateDataExistence(aggregate, expenditureList, searchCondition));
	}

	@Test
	@DisplayName("正常系：validateDataExistence - 収支データなし、支出データなしの場合、例外をスローしない")
	void testValidateDataExistence_BothEmpty_Success() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth = TargetYearMonth.from("202501");
		SearchQueryUserIdAndYearMonth searchCondition =
			SearchQueryUserIdAndYearMonth.from(userId, yearMonth);

		// 収支集約：空
		IncomeAndExpenditure aggregate = IncomeAndExpenditure.empty(userId, yearMonth);

		// 支出リスト：空
		AccountMonthInquiryExpenditureItemList expenditureList =
			AccountMonthInquiryExpenditureItemList.from(Collections.emptyList());

		// 実行 & 検証（例外がスローされないことを確認）
		assertDoesNotThrow(() -> service.validateDataExistence(aggregate, expenditureList, searchCondition));
	}

	@Test
	@DisplayName("異常系：validateDataExistence - 収支データなし、支出データありの場合、例外をスロー")
	void testValidateDataExistence_IncomeEmptyExpenditureExists_ThrowsException() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth = TargetYearMonth.from("202508");
		SearchQueryUserIdAndYearMonth searchCondition =
			SearchQueryUserIdAndYearMonth.from(userId, yearMonth);

		// 収支集約：空
		IncomeAndExpenditure aggregate = IncomeAndExpenditure.empty(userId, yearMonth);

		// 支出リスト：データあり（不整合）
		List<AccountMonthInquiryExpenditureItemList.ExpenditureListItem> items = new ArrayList<>();
		AccountMonthInquiryExpenditureItemList.ExpenditureListItem item =
			mock(AccountMonthInquiryExpenditureItemList.ExpenditureListItem.class);
		items.add(item);
		AccountMonthInquiryExpenditureItemList expenditureList =
			AccountMonthInquiryExpenditureItemList.from(items);

		// 実行 & 検証
		DataInconsistencyException exception = assertThrows(
			DataInconsistencyException.class,
			() -> service.validateDataExistence(aggregate, expenditureList, searchCondition)
		);

		// 例外メッセージの検証
		assertTrue(exception.getMessage().contains("収支データが未登録"));
		assertTrue(exception.getMessage().contains("支出金額情報が登録済み"));
		assertTrue(exception.getMessage().contains("202508"));
	}

	@Test
	@DisplayName("正常系：validateAll - すべての整合性が正しい場合、例外をスローしない")
	void testValidateAll_Success() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth = TargetYearMonth.from("202511");
		SearchQueryUserIdAndYearMonth searchCondition =
			SearchQueryUserIdAndYearMonth.from(userId, yearMonth);

		// 収支集約：収入400,000、支出280,000
		IncomeAmount income = IncomeAmount.from(new BigDecimal("350000.00"));
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("280000.00"));
		IncomeAndExpenditure aggregate = IncomeAndExpenditure.reconstruct(
			userId, yearMonth, income,
			com.yonetani.webapp.accountbook.domain.type.account.inquiry.WithdrewKingaku.from(new BigDecimal("50000.00")),
			null, expenditure, null
		);

		// モック設定
		when(incomeRepository.sumIncomeKingaku(searchCondition))
			.thenReturn(SyuunyuuKingakuTotalAmount.from(new BigDecimal("400000.00")));
		when(expenditureRepository.sumExpenditureKingaku(searchCondition))
			.thenReturn(SisyutuKingakuTotalAmount.from(new BigDecimal("280000.00")));

		// 実行 & 検証（例外がスローされないことを確認）
		assertDoesNotThrow(() -> service.validateAll(aggregate, searchCondition));

		// モックの呼び出し確認
		verify(incomeRepository, times(1)).sumIncomeKingaku(searchCondition);
		verify(expenditureRepository, times(1)).sumExpenditureKingaku(searchCondition);
	}

	@Test
	@DisplayName("異常系：validateAll - 収入金額が不整合の場合、例外をスロー")
	void testValidateAll_IncomeInconsistent_ThrowsException() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth = TargetYearMonth.from("202511");
		SearchQueryUserIdAndYearMonth searchCondition =
			SearchQueryUserIdAndYearMonth.from(userId, yearMonth);

		// 収支集約
		IncomeAmount income = IncomeAmount.from(new BigDecimal("350000.00"));
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("280000.00"));
		IncomeAndExpenditure aggregate = IncomeAndExpenditure.reconstruct(
			userId, yearMonth, income,
			com.yonetani.webapp.accountbook.domain.type.account.inquiry.WithdrewKingaku.from(new BigDecimal("50000.00")),
			null, expenditure, null
		);

		// モック設定：収入が不整合
		when(incomeRepository.sumIncomeKingaku(searchCondition))
			.thenReturn(SyuunyuuKingakuTotalAmount.from(new BigDecimal("420000.00")));

		// 実行 & 検証
		assertThrows(IncomeAmountInconsistencyException.class,
			() -> service.validateAll(aggregate, searchCondition)
		);

		// 収入チェックで失敗するため、支出チェックは呼ばれない
		verify(incomeRepository, times(1)).sumIncomeKingaku(searchCondition);
		verify(expenditureRepository, never()).sumExpenditureKingaku(searchCondition);
	}

	@Test
	@DisplayName("異常系：validateAll - 支出金額が不整合の場合、例外をスロー")
	void testValidateAll_ExpenditureInconsistent_ThrowsException() {
		// 準備
		UserId userId = UserId.from("user01");
		TargetYearMonth yearMonth = TargetYearMonth.from("202511");
		SearchQueryUserIdAndYearMonth searchCondition =
			SearchQueryUserIdAndYearMonth.from(userId, yearMonth);

		// 収支集約
		IncomeAmount income = IncomeAmount.from(new BigDecimal("350000.00"));
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("280000.00"));
		IncomeAndExpenditure aggregate = IncomeAndExpenditure.reconstruct(
			userId, yearMonth, income,
			com.yonetani.webapp.accountbook.domain.type.account.inquiry.WithdrewKingaku.from(new BigDecimal("50000.00")),
			null, expenditure, null
		);

		// モック設定：収入は一致、支出が不整合
		when(incomeRepository.sumIncomeKingaku(searchCondition))
			.thenReturn(SyuunyuuKingakuTotalAmount.from(new BigDecimal("400000.00")));
		when(expenditureRepository.sumExpenditureKingaku(searchCondition))
			.thenReturn(SisyutuKingakuTotalAmount.from(new BigDecimal("300000.00")));

		// 実行 & 検証
		assertThrows(ExpenditureAmountInconsistencyException.class,
			() -> service.validateAll(aggregate, searchCondition)
		);

		// 両方のチェックが呼ばれる
		verify(incomeRepository, times(1)).sumIncomeKingaku(searchCondition);
		verify(expenditureRepository, times(1)).sumExpenditureKingaku(searchCondition);
	}
}
