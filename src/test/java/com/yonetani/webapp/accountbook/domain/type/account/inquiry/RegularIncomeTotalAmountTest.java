/**
 * RegularIncomeTotalAmount(通常収入金額合計)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/01/02 : 1.00.00     新規作成
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
 * RegularIncomeTotalAmount(通常収入金額合計)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@DisplayName("通常収入金額合計(RegularIncomeTotalAmount)のテスト")
class RegularIncomeTotalAmountTest {

	@Test
	@DisplayName("正常系：正の金額で生成できる")
	void testFrom_正常系_正の金額() {
		// 実行
		RegularIncomeTotalAmount amount = RegularIncomeTotalAmount.from(new BigDecimal("10000.00"));

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
		assertNotNull(RegularIncomeTotalAmount.ZERO);
		assertTrue(RegularIncomeTotalAmount.ZERO.isZero());
		assertEquals(BigDecimal.ZERO.setScale(2), RegularIncomeTotalAmount.ZERO.getValue());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> RegularIncomeTotalAmount.from((BigDecimal)null)
		);
		assertTrue(exception.getMessage().contains("通常収入金額合計"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("異常系：負の金額で例外が発生する")
	void testFrom_異常系_負の金額() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> RegularIncomeTotalAmount.from(new BigDecimal("-1000.00"))
		);
		assertTrue(exception.getMessage().contains("通常収入金額合計"));
		assertTrue(exception.getMessage().contains("マイナス"));
	}

	@Test
	@DisplayName("異常系：スケール値が2以外で例外が発生する")
	void testFrom_異常系_スケール値不正() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> RegularIncomeTotalAmount.from(new BigDecimal("10000"))
		);
		assertTrue(exception.getMessage().contains("通常収入金額合計"));
		assertTrue(exception.getMessage().contains("スケール"));
	}

	@Test
	@DisplayName("正常系：加算が正しく動作する")
	void testAdd_正常系() {
		// 準備
		RegularIncomeTotalAmount amount1 = RegularIncomeTotalAmount.from(new BigDecimal("10000.00"));
		RegularIncomeAmount amount2 = RegularIncomeAmount.from(new BigDecimal("5000.00"));

		// 実行
		RegularIncomeTotalAmount result = amount1.add(amount2);

		// 検証
		assertEquals(new BigDecimal("15000.00"), result.getValue());
		// 元のオブジェクトは変更されていないことを確認（不変性）
		assertEquals(new BigDecimal("10000.00"), amount1.getValue());
		assertEquals(new BigDecimal("5000.00"), amount2.getValue());
	}

	@Test
	@DisplayName("正常系：0への加算が正しく動作する")
	void testAdd_正常系_0への加算() {
		// 準備
		RegularIncomeTotalAmount amount1 = RegularIncomeTotalAmount.ZERO;
		RegularIncomeAmount amount2 = RegularIncomeAmount.from(new BigDecimal("5000.00"));

		// 実行
		RegularIncomeTotalAmount result = amount1.add(amount2);

		// 検証
		assertEquals(new BigDecimal("5000.00"), result.getValue());
		// 元のオブジェクトは変更されていないことを確認（不変性）
		assertEquals(new BigDecimal("0.00"), amount1.getValue());
		assertEquals(new BigDecimal("5000.00"), amount2.getValue());
	}

}
