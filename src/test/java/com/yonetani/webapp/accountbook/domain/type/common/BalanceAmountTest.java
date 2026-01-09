/**
 * BalanceAmount(収支金額)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/11/23 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.account.inquiry.TotalAvailableFunds;

/**
 *<pre>
 * BalanceAmount(収支金額)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("収支金額(BalanceAmount)のテスト")
class BalanceAmountTest {

	@Test
	@DisplayName("正常系：正の金額で生成できる")
	void testFrom_正常系_正の金額() {
		// 実行
		BalanceAmount amount = BalanceAmount.from(new BigDecimal("10000.00"));

		// 検証
		assertNotNull(amount);
		assertEquals(new BigDecimal("10000.00"), amount.getValue());
		assertEquals("10000.00", amount.toString());
		assertEquals("10,000円", amount.toFormatString());
	}

	@Test
	@DisplayName("正常系：負の金額で生成できる（赤字）")
	void testFrom_正常系_負の金額() {
		// 実行
		BalanceAmount amount = BalanceAmount.from(new BigDecimal("-5000.00"));

		// 検証
		assertNotNull(amount);
		assertEquals(new BigDecimal("-5000.00"), amount.getValue());
		assertTrue(amount.isNegative());
		assertTrue(amount.isDeficit());
	}

	@Test
	@DisplayName("正常系：ZERO定数が使用できる")
	void testZERO定数() {
		// 検証
		assertNotNull(BalanceAmount.ZERO);
		assertTrue(BalanceAmount.ZERO.isZero());
		assertEquals(BigDecimal.ZERO.setScale(2), BalanceAmount.ZERO.getValue());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> BalanceAmount.from(null)
		);
		assertTrue(exception.getMessage().contains("収支金額"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("異常系：スケール値が2以外で例外が発生する")
	void testFrom_異常系_スケール値不正() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> BalanceAmount.from(new BigDecimal("10000"))
		);
		assertTrue(exception.getMessage().contains("収支金額"));
		assertTrue(exception.getMessage().contains("スケール"));
	}

	@Test
	@DisplayName("正常系：isDeficitが正しく動作する")
	void testIsDeficit() {
		// 準備
		BalanceAmount deficit = BalanceAmount.from(new BigDecimal("-1000.00"));
		BalanceAmount surplus = BalanceAmount.from(new BigDecimal("1000.00"));
		BalanceAmount zero = BalanceAmount.ZERO;

		// 検証
		assertTrue(deficit.isDeficit());
		assertFalse(surplus.isDeficit());
		assertFalse(zero.isDeficit());
	}

	@Test
	@DisplayName("正常系：isSurplusが正しく動作する")
	void testIsSurplus() {
		// 準備
		BalanceAmount deficit = BalanceAmount.from(new BigDecimal("-1000.00"));
		BalanceAmount surplus = BalanceAmount.from(new BigDecimal("1000.00"));
		BalanceAmount zero = BalanceAmount.ZERO;

		// 検証
		assertFalse(deficit.isSurplus());
		assertTrue(surplus.isSurplus());
		assertFalse(zero.isSurplus());
	}

	@Test
	@DisplayName("正常系：calculate(TotalAvailableFunds, ExpenditureAmount)で黒字が正しく計算される")
	void testCalculate_新メソッド_正常系_黒字() {
		// 準備
		TotalAvailableFunds availableFunds = TotalAvailableFunds.from(new BigDecimal("150000.00"));
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("100000.00"));

		// 実行
		BalanceAmount balance = BalanceAmount.calculate(availableFunds, expenditure);

		// 検証
		assertEquals(new BigDecimal("50000.00"), balance.getValue());
		assertTrue(balance.isSurplus());
		assertFalse(balance.isDeficit());
	}

	@Test
	@DisplayName("正常系：calculate(TotalAvailableFunds, ExpenditureAmount)で赤字が正しく計算される")
	void testCalculate_新メソッド_正常系_赤字() {
		// 準備
		TotalAvailableFunds availableFunds = TotalAvailableFunds.from(new BigDecimal("80000.00"));
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("120000.00"));

		// 実行
		BalanceAmount balance = BalanceAmount.calculate(availableFunds, expenditure);

		// 検証
		assertEquals(new BigDecimal("-40000.00"), balance.getValue());
		assertTrue(balance.isDeficit());
		assertFalse(balance.isSurplus());
	}

	@Test
	@DisplayName("正常系：calculate(TotalAvailableFunds, ExpenditureAmount)で収支0が正しく計算される")
	void testCalculate_新メソッド_正常系_収支0() {
		// 準備
		TotalAvailableFunds availableFunds = TotalAvailableFunds.from(new BigDecimal("100000.00"));
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("100000.00"));

		// 実行
		BalanceAmount balance = BalanceAmount.calculate(availableFunds, expenditure);

		// 検証
		assertEquals(BigDecimal.ZERO.setScale(2), balance.getValue());
		assertTrue(balance.isZero());
		assertFalse(balance.isSurplus());
		assertFalse(balance.isDeficit());
	}
}
