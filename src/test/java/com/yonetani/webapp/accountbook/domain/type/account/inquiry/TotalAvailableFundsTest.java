/**
 * TotalAvailableFunds(利用可能資金合計)のテストクラスです
 * リファクタリングにより、クラス名変更しました(SyuunyuuKingakuTotalAmountTest → TotalAvailableFundsTest)
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/15 : 1.00.00  新規作成
 * 2026/01/06 : 1.01.00  リファクタリング対応(クラス名変更: SyuunyuuKingakuTotalAmountTest → TotalAvailableFundsTest)
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.RegularIncomeAmount;

/**
 *<pre>
 * TotalAvailableFunds(利用可能資金合計)のテストクラスです
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("利用可能資金合計(TotalAvailableFunds)のテスト")
class TotalAvailableFundsTest {

	@Test
	@DisplayName("正常系：正の金額で生成できる")
	void testFrom_正常系_正の金額() {
		// 実行
		TotalAvailableFunds amount = TotalAvailableFunds.from(new BigDecimal("10000.00"));

		// 検証
		assertNotNull(amount);
		assertEquals(new BigDecimal("10000.00"), amount.getValue());
		assertEquals("10000.00", amount.toString());
		assertEquals("10,000円", amount.toFormatString());
	}

	@Test
	@DisplayName("正常系：ZERO定数が使用できる")
	void testZERO定数() {
		// 検証
		assertNotNull(TotalAvailableFunds.ZERO);
		assertTrue(TotalAvailableFunds.ZERO.isZero());
		assertEquals(BigDecimal.ZERO.setScale(2), TotalAvailableFunds.ZERO.getValue());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TotalAvailableFunds.from((BigDecimal)null)
		);
		assertTrue(exception.getMessage().contains("利用可能資金合計"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("異常系：負の金額で例外が発生する")
	void testFrom_異常系_負の金額() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TotalAvailableFunds.from(new BigDecimal("-1000.00"))
		);
		assertTrue(exception.getMessage().contains("利用可能資金合計"));
		assertTrue(exception.getMessage().contains("マイナス"));
	}

	@Test
	@DisplayName("異常系：スケール値が2以外で例外が発生する")
	void testFrom_異常系_スケール値不正() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TotalAvailableFunds.from(new BigDecimal("10000"))
		);
		assertTrue(exception.getMessage().contains("利用可能資金合計"));
		assertTrue(exception.getMessage().contains("スケール"));
	}

	@Test
	@DisplayName("正常系：通常収入金額と積立金取崩金額から生成できる")
	void testFrom_正常系_通常収入金額と積立金取崩金額() {
		// 準備
		RegularIncomeAmount income = RegularIncomeAmount.from(new BigDecimal("10000.00"));
		WithdrawingAmount withdrawing = WithdrawingAmount.from(new BigDecimal("5000.00"));

		// 実行
		TotalAvailableFunds total = TotalAvailableFunds.from(income, withdrawing);

		// 検証
		assertNotNull(total);
		assertEquals(new BigDecimal("15000.00"), total.getValue());
	}

	@Test
	@DisplayName("正常系：積立金取崩金額がnullの場合も正しく動作する")
	void testFrom_正常系_積立金取崩金額がnull() {
		// 準備
		RegularIncomeAmount income = RegularIncomeAmount.from(new BigDecimal("10000.00"));
		WithdrawingAmount withdrawing = WithdrawingAmount.NULL;

		// 実行
		TotalAvailableFunds total = TotalAvailableFunds.from(income, withdrawing);

		// 検証（getNullSafeValue()により0として扱われる）
		assertNotNull(total);
		assertEquals(new BigDecimal("10000.00"), total.getValue());
	}

	@Test
	@DisplayName("正常系：加算が正しく動作する")
	void testAdd_正常系() {
		// 準備
		TotalAvailableFunds total = TotalAvailableFunds.from(new BigDecimal("10000.00"));
		RegularIncomeAmount add = RegularIncomeAmount.from(new BigDecimal("5000.00"));

		// 実行
		TotalAvailableFunds result = total.add(add);

		// 検証
		assertEquals(new BigDecimal("15000.00"), result.getValue());
		// 不変性の確認
		assertEquals(new BigDecimal("10000.00"), total.getValue());
	}
}
