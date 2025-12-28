/**
 * SisyutuKingakuBC(支出金額BC)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/25 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.common.ExpenditureAmount;

/**
 *<pre>
 * SisyutuKingakuBC(支出金額BC)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("支出金額BC(SisyutuKingakuBC)のテスト")
class SisyutuKingakuBCTest {

	@Test
	@DisplayName("正常系：支出金額BとCの両方が値ありで生成できる")
	void testFrom_正常系_BとC両方値あり() {
		// 準備
		SisyutuKingakuB kingakuB = SisyutuKingakuB.from(new BigDecimal("10000.00"));
		SisyutuKingakuC kingakuC = SisyutuKingakuC.from(new BigDecimal("5000.00"));

		// 実行
		SisyutuKingakuBC amount = SisyutuKingakuBC.from(kingakuB, kingakuC);

		// 検証
		assertNotNull(amount);
		assertEquals(new BigDecimal("15000.00"), amount.getValue()); // B + C
		assertEquals(kingakuB, amount.getSisyutuKingakuB());
		assertEquals(kingakuC, amount.getSisyutuKingakuC());
		assertEquals("15000.00", amount.toString());
		assertEquals("15,000円", amount.toFormatString());
	}

	@Test
	@DisplayName("正常系：支出金額Bがnull値、Cが値ありで生成できる")
	void testFrom_正常系_Bがnull値_Cが値あり() {
		// 準備
		SisyutuKingakuB kingakuB = SisyutuKingakuB.from(null);
		SisyutuKingakuC kingakuC = SisyutuKingakuC.from(new BigDecimal("5000.00"));

		// 実行
		SisyutuKingakuBC amount = SisyutuKingakuBC.from(kingakuB, kingakuC);

		// 検証
		assertNotNull(amount);
		assertEquals(new BigDecimal("5000.00"), amount.getValue()); // Cの値
		assertEquals(kingakuB, amount.getSisyutuKingakuB());
		assertEquals(kingakuC, amount.getSisyutuKingakuC());
	}

	@Test
	@DisplayName("正常系：支出金額Bが値あり、Cがnull値で生成できる")
	void testFrom_正常系_Bが値あり_Cがnull値() {
		// 準備
		SisyutuKingakuB kingakuB = SisyutuKingakuB.from(new BigDecimal("10000.00"));
		SisyutuKingakuC kingakuC = SisyutuKingakuC.from(null);

		// 実行
		SisyutuKingakuBC amount = SisyutuKingakuBC.from(kingakuB, kingakuC);

		// 検証
		assertNotNull(amount);
		assertEquals(new BigDecimal("10000.00"), amount.getValue()); // Bの値
		assertEquals(kingakuB, amount.getSisyutuKingakuB());
		assertEquals(kingakuC, amount.getSisyutuKingakuC());
	}

	@Test
	@DisplayName("正常系：支出金額BとC両方がnull値で生成できる")
	void testFrom_正常系_BとC両方null値() {
		// 準備
		SisyutuKingakuB kingakuB = SisyutuKingakuB.from(null);
		SisyutuKingakuC kingakuC = SisyutuKingakuC.from(null);

		// 実行
		SisyutuKingakuBC amount = SisyutuKingakuBC.from(kingakuB, kingakuC);

		// 検証
		assertNotNull(amount);
		assertNull(amount.getValue());
		assertEquals(kingakuB, amount.getSisyutuKingakuB());
		assertEquals(kingakuC, amount.getSisyutuKingakuC());
		assertEquals("", amount.toString());
		assertEquals("", amount.toFormatString());
	}

	@Test
	@DisplayName("正常系：ZERO定数が使用できる")
	void testZERO定数() {
		// 検証
		assertNotNull(SisyutuKingakuBC.ZERO);
		assertEquals(BigDecimal.ZERO.setScale(2), SisyutuKingakuBC.ZERO.getValue());
		assertTrue(SisyutuKingakuBC.ZERO.getSisyutuKingakuB().isZero());
		assertTrue(SisyutuKingakuBC.ZERO.getSisyutuKingakuC().isZero());
	}

	@Test
	@DisplayName("異常系：支出金額Bがnullで例外が発生する")
	void testFrom_異常系_Bがnull() {
		// 準備
		SisyutuKingakuC kingakuC = SisyutuKingakuC.from(new BigDecimal("5000.00"));

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> SisyutuKingakuBC.from(null, kingakuC)
		);
		assertTrue(exception.getMessage().contains("支出金額B"));
	}

	@Test
	@DisplayName("異常系：支出金額Cがnullで例外が発生する")
	void testFrom_異常系_Cがnull() {
		// 準備
		SisyutuKingakuB kingakuB = SisyutuKingakuB.from(new BigDecimal("10000.00"));

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> SisyutuKingakuBC.from(kingakuB, null)
		);
		assertTrue(exception.getMessage().contains("支出金額C"));
	}

	@Test
	@DisplayName("正常系：加算が正しく動作する")
	void testAdd_正常系() {
		// 準備
		SisyutuKingakuBC amount1 = SisyutuKingakuBC.from(
			SisyutuKingakuB.from(new BigDecimal("10000.00")),
			SisyutuKingakuC.from(new BigDecimal("5000.00"))
		);
		SisyutuKingakuBC amount2 = SisyutuKingakuBC.from(
			SisyutuKingakuB.from(new BigDecimal("3000.00")),
			SisyutuKingakuC.from(new BigDecimal("2000.00"))
		);

		// 実行
		SisyutuKingakuBC result = amount1.add(amount2);

		// 検証
		assertEquals(new BigDecimal("20000.00"), result.getValue());
		assertEquals(new BigDecimal("13000.00"), result.getSisyutuKingakuB().getValue());
		assertEquals(new BigDecimal("7000.00"), result.getSisyutuKingakuC().getValue());
		// 元の値が変わっていないこと
		assertEquals(new BigDecimal("15000.00"), amount1.getValue());
	}

	@Test
	@DisplayName("正常系：null値との加算")
	void testAdd_正常系_null値() {
		// 準備
		SisyutuKingakuBC amount1 = SisyutuKingakuBC.from(
			SisyutuKingakuB.from(new BigDecimal("10000.00")),
			SisyutuKingakuC.from(new BigDecimal("5000.00"))
		);
		SisyutuKingakuBC amount2 = SisyutuKingakuBC.from(
			SisyutuKingakuB.from(null),
			SisyutuKingakuC.from(null)
		);

		// 実行
		SisyutuKingakuBC result = amount1.add(amount2);

		// 検証（null as zero扱い）
		assertEquals(new BigDecimal("15000.00"), result.getValue());
	}

	@Test
	@DisplayName("正常系：toSisyutuKingakuBCString()でフォーマット済み文字列を取得")
	void testToSisyutuKingakuBCString() {
		// 準備
		SisyutuKingakuBC amount = SisyutuKingakuBC.from(
			SisyutuKingakuB.from(new BigDecimal("10000.00")),
			SisyutuKingakuC.from(new BigDecimal("5000.00"))
		);

		// 実行
		@SuppressWarnings("deprecation")
		String result = amount.toSisyutuKingakuBCString();

		// 検証
		assertEquals("15,000円", result);
	}

	@Test
	@DisplayName("正常系：getPercentage()で支出金額に対する割合を取得")
	void testGetPercentage() {
		// 準備
		SisyutuKingakuBC amount = SisyutuKingakuBC.from(
			SisyutuKingakuB.from(new BigDecimal("10000.00")),
			SisyutuKingakuC.from(new BigDecimal("5000.00"))
		);
		ExpenditureAmount expenditureAmount = ExpenditureAmount.from(new BigDecimal("50000.00"));

		// 実行
		String result = amount.getPercentage(expenditureAmount);

		// 検証
		assertEquals("30", result); // 15000 / 50000 * 100 = 30
	}

	@Test
	@DisplayName("正常系：getPercentage()でnull値の場合は空文字列を返す")
	void testGetPercentage_null値() {
		// 準備
		SisyutuKingakuBC amount = SisyutuKingakuBC.from(
			SisyutuKingakuB.from(null),
			SisyutuKingakuC.from(null)
		);
		ExpenditureAmount expenditureAmount = ExpenditureAmount.from(new BigDecimal("50000.00"));

		// 実行
		String result = amount.getPercentage(expenditureAmount);

		// 検証
		assertEquals("", result);
	}

	@Test
	@DisplayName("異常系：getPercentage()で支出金額がnullの場合例外が発生する")
	void testGetPercentage_異常系() {
		// 準備
		SisyutuKingakuBC amount = SisyutuKingakuBC.from(
			SisyutuKingakuB.from(new BigDecimal("10000.00")),
			SisyutuKingakuC.from(new BigDecimal("5000.00"))
		);

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> amount.getPercentage(null)
		);
		assertTrue(exception.getMessage().contains("支出金額"));
	}

	@Test
	@DisplayName("正常系：getSisyutuKingakuBPercentage()で支出金額Bの割合を取得")
	void testGetSisyutuKingakuBPercentage() {
		// 準備
		SisyutuKingakuBC amount = SisyutuKingakuBC.from(
			SisyutuKingakuB.from(new BigDecimal("10000.00")),
			SisyutuKingakuC.from(new BigDecimal("5000.00"))
		);

		// 実行
		String result = amount.getSisyutuKingakuBPercentage();

		// 検証
		assertEquals("67", result); // 10000 / 15000 * 100 = 66.67 → 67
	}

	@Test
	@DisplayName("正常系：getSisyutuKingakuBPercentage()でnull値の場合は0を返す")
	void testGetSisyutuKingakuBPercentage_null値() {
		// 準備
		SisyutuKingakuBC amount = SisyutuKingakuBC.from(
			SisyutuKingakuB.from(null),
			SisyutuKingakuC.from(null)
		);

		// 実行
		String result = amount.getSisyutuKingakuBPercentage();

		// 検証
		assertEquals("0", result);
	}
}
