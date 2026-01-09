/**
 * WasteExpenditureTotalAmount(無駄遣い支出金額合計)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/01/04 : 1.00.00  新規作成
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
 * WasteExpenditureTotalAmount(無駄遣い支出金額合計)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@DisplayName("無駄遣い支出金額合計(WasteExpenditureTotalAmount)のテスト")
class WasteExpenditureTotalAmountTest {

	@Test
	@DisplayName("正常系：from(MinorWasteExpenditure, SevereWasteExpenditure)メソッドで正常に生成")
	void testFrom_Success() {
		// 準備
		MinorWasteExpenditure minor = MinorWasteExpenditure.from(new BigDecimal("3000.00"));
		SevereWasteExpenditure severe = SevereWasteExpenditure.from(new BigDecimal("7000.00"));

		// 実行
		WasteExpenditureTotalAmount result = WasteExpenditureTotalAmount.from(minor, severe);

		// 検証
		assertNotNull(result);
		assertEquals(new BigDecimal("10000.00"), result.getValue()); // 軽度 + 重度
		assertEquals(minor, result.getMinorWasteExpenditure());
		assertEquals(severe, result.getSevereWasteExpenditure());
		assertEquals("10000.00", result.toString());
		assertEquals("10,000円", result.toFormatString());
	}

	@Test
	@DisplayName("正常系：ZERO定数のテスト")
	void testZero() {
		// 検証
		assertNotNull(WasteExpenditureTotalAmount.ZERO);
		assertEquals(BigDecimal.ZERO.setScale(2), WasteExpenditureTotalAmount.ZERO.getValue());
		assertTrue(WasteExpenditureTotalAmount.ZERO.getMinorWasteExpenditure().isZero());
		assertTrue(WasteExpenditureTotalAmount.ZERO.getSevereWasteExpenditure().isZero());
	}

	@Test
	@DisplayName("異常系：minor値がnullで例外発生")
	void testFrom_MinorNull() {
		// 準備
		SevereWasteExpenditure severe = SevereWasteExpenditure.from(new BigDecimal("5000.00"));

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> WasteExpenditureTotalAmount.from(null, severe));

		// エラーメッセージ検証
		assertTrue(exception.getMessage().contains("無駄遣い（軽度）支出金額"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("異常系：severe値がnullで例外発生")
	void testFrom_SevereNull() {
		// 準備
		MinorWasteExpenditure minor = MinorWasteExpenditure.from(new BigDecimal("3000.00"));

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> WasteExpenditureTotalAmount.from(minor, null));

		// エラーメッセージ検証
		assertTrue(exception.getMessage().contains("無駄遣い（重度）支出金額"));
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("正常系：add(TotalWasteExpenditure)メソッドで加算")
	void testAdd_Success() {
		// 準備
		WasteExpenditureTotalAmount base = WasteExpenditureTotalAmount.from(
			MinorWasteExpenditure.from(new BigDecimal("2000.00")),
			SevereWasteExpenditure.from(new BigDecimal("3000.00"))
		);
		TotalWasteExpenditure addValue = TotalWasteExpenditure.from(
			MinorWasteExpenditure.from(new BigDecimal("1000.00")),
			SevereWasteExpenditure.from(new BigDecimal("2000.00"))
		);

		// 実行
		WasteExpenditureTotalAmount result = base.add(addValue);

		// 検証
		assertNotNull(result);
		assertEquals(0, new BigDecimal("8000.00").compareTo(result.getValue()));
		assertEquals(0, new BigDecimal("3000.00").compareTo(result.getMinorWasteExpenditure().getValue()));
		assertEquals(0, new BigDecimal("5000.00").compareTo(result.getSevereWasteExpenditure().getValue()));
	}

	@Test
	@DisplayName("正常系：add(TotalWasteExpenditure)でnullオブジェクトを加算")
	void testAdd_NullObject() {
		// 準備
		WasteExpenditureTotalAmount base = WasteExpenditureTotalAmount.from(
			MinorWasteExpenditure.from(new BigDecimal("5000.00")),
			SevereWasteExpenditure.from(new BigDecimal("3000.00"))
		);

		// 実行
		WasteExpenditureTotalAmount result = base.add(null);

		// 検証（nullの場合は元の値がそのまま返される）
		assertNotNull(result);
		assertEquals(0, new BigDecimal("8000.00").compareTo(result.getValue()));
	}

	@Test
	@DisplayName("正常系：ZEROに値を加算")
	void testAdd_FromZero() {
		// 準備
		TotalWasteExpenditure addValue = TotalWasteExpenditure.from(
			MinorWasteExpenditure.from(new BigDecimal("3000.00")),
			SevereWasteExpenditure.from(new BigDecimal("2000.00"))
		);

		// 実行
		WasteExpenditureTotalAmount result = WasteExpenditureTotalAmount.ZERO.add(addValue);

		// 検証
		assertNotNull(result);
		assertEquals(0, new BigDecimal("5000.00").compareTo(result.getValue()));
	}

	@Test
	@DisplayName("正常系：getPercentage(ExpenditureAmount)メソッドで割合取得")
	void testGetPercentage() {
		// 準備
		WasteExpenditureTotalAmount wasteTotal = WasteExpenditureTotalAmount.from(
			MinorWasteExpenditure.from(new BigDecimal("3000.00")),
			SevereWasteExpenditure.from(new BigDecimal("2000.00"))
		);
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("50000.00"));

		// 実行
		String result = wasteTotal.getPercentage(expenditure);

		// 検証（5000 / 50000 * 100 = 10%）
		assertEquals("10", result);
	}

	@Test
	@DisplayName("正常系：getMinorWasteExpenditurePercentage()メソッドで軽度割合取得")
	void testGetMinorWasteExpenditurePercentage() {
		// 準備
		WasteExpenditureTotalAmount wasteTotal = WasteExpenditureTotalAmount.from(
			MinorWasteExpenditure.from(new BigDecimal("3000.00")),
			SevereWasteExpenditure.from(new BigDecimal("2000.00"))
		);

		// 実行
		String result = wasteTotal.getMinorWasteExpenditurePercentage();

		// 検証（3000 / 5000 * 100 = 60%）
		assertEquals("60", result);
	}

	@Test
	@DisplayName("正常系：無駄遣い（軽度）がnull値、無駄遣い（重度）が値ありで生成")
	void testFrom_MinorNullValue_SevereHasValue() {
		// 準備
		MinorWasteExpenditure minor = MinorWasteExpenditure.from(null);
		SevereWasteExpenditure severe = SevereWasteExpenditure.from(new BigDecimal("5000.00"));

		// 実行
		WasteExpenditureTotalAmount result = WasteExpenditureTotalAmount.from(minor, severe);

		// 検証
		assertNotNull(result);
		assertEquals(new BigDecimal("5000.00"), result.getValue()); // 重度の値
		assertEquals(minor, result.getMinorWasteExpenditure());
		assertEquals(severe, result.getSevereWasteExpenditure());
	}

	@Test
	@DisplayName("正常系：無駄遣い（軽度）が値あり、無駄遣い（重度）がnull値で生成")
	void testFrom_MinorHasValue_SevereNullValue() {
		// 準備
		MinorWasteExpenditure minor = MinorWasteExpenditure.from(new BigDecimal("10000.00"));
		SevereWasteExpenditure severe = SevereWasteExpenditure.from(null);

		// 実行
		WasteExpenditureTotalAmount result = WasteExpenditureTotalAmount.from(minor, severe);

		// 検証
		assertNotNull(result);
		assertEquals(new BigDecimal("10000.00"), result.getValue()); // 軽度の値
		assertEquals(minor, result.getMinorWasteExpenditure());
		assertEquals(severe, result.getSevereWasteExpenditure());
	}

	@Test
	@DisplayName("正常系：無駄遣い（軽度）と無駄遣い（重度）両方がnull値で生成")
	void testFrom_BothNullValue() {
		// 準備
		MinorWasteExpenditure minor = MinorWasteExpenditure.from(null);
		SevereWasteExpenditure severe = SevereWasteExpenditure.from(null);

		// 実行
		WasteExpenditureTotalAmount result = WasteExpenditureTotalAmount.from(minor, severe);

		// 検証
		assertNotNull(result);
		assertNull(result.getValue());
		assertEquals(minor, result.getMinorWasteExpenditure());
		assertEquals(severe, result.getSevereWasteExpenditure());
		assertEquals("", result.toString());
		assertEquals("", result.toFormatString());
	}

	@Test
	@DisplayName("正常系：add()でnull値との加算")
	void testAdd_WithNullValue() {
		// 準備
		WasteExpenditureTotalAmount base = WasteExpenditureTotalAmount.from(
			MinorWasteExpenditure.from(new BigDecimal("10000.00")),
			SevereWasteExpenditure.from(new BigDecimal("5000.00"))
		);
		TotalWasteExpenditure addValue = TotalWasteExpenditure.from(
			MinorWasteExpenditure.from(null),
			SevereWasteExpenditure.from(null)
		);

		// 実行
		WasteExpenditureTotalAmount result = base.add(addValue);

		// 検証（null値は0として扱われる）
		assertEquals(new BigDecimal("15000.00"), result.getValue());
	}

	@Test
	@DisplayName("正常系：getPercentage()でnull値の場合は空文字列を返す")
	void testGetPercentage_NullValue() {
		// 準備
		WasteExpenditureTotalAmount wasteTotal = WasteExpenditureTotalAmount.from(
			MinorWasteExpenditure.from(null),
			SevereWasteExpenditure.from(null)
		);
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("50000.00"));

		// 実行
		String result = wasteTotal.getPercentage(expenditure);

		// 検証
		assertEquals("", result);
	}

	@Test
	@DisplayName("異常系：getPercentage()で支出金額がnullの場合例外が発生")
	void testGetPercentage_ExpenditureNull() {
		// 準備
		WasteExpenditureTotalAmount wasteTotal = WasteExpenditureTotalAmount.from(
			MinorWasteExpenditure.from(new BigDecimal("10000.00")),
			SevereWasteExpenditure.from(new BigDecimal("5000.00"))
		);

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> wasteTotal.getPercentage(null)
		);
		assertTrue(exception.getMessage().contains("支出金額"));
	}

	@Test
	@DisplayName("正常系：getMinorWasteExpenditurePercentage()でnull値の場合は0を返す")
	void testGetMinorWasteExpenditurePercentage_NullValue() {
		// 準備
		WasteExpenditureTotalAmount wasteTotal = WasteExpenditureTotalAmount.from(
			MinorWasteExpenditure.from(null),
			SevereWasteExpenditure.from(null)
		);

		// 実行
		String result = wasteTotal.getMinorWasteExpenditurePercentage();

		// 検証
		assertEquals("0", result);
	}

	@Test
	@DisplayName("正常系：equalsメソッドのテスト")
	void testEquals() {
		// 準備
		WasteExpenditureTotalAmount value1 = WasteExpenditureTotalAmount.from(
			MinorWasteExpenditure.from(new BigDecimal("5000.00")),
			SevereWasteExpenditure.from(new BigDecimal("5000.00"))
		);
		WasteExpenditureTotalAmount value2 = WasteExpenditureTotalAmount.from(
			MinorWasteExpenditure.from(new BigDecimal("5000.00")),
			SevereWasteExpenditure.from(new BigDecimal("5000.00"))
		);
		WasteExpenditureTotalAmount value3 = WasteExpenditureTotalAmount.from(
			MinorWasteExpenditure.from(new BigDecimal("3000.00")),
			SevereWasteExpenditure.from(new BigDecimal("7000.00"))
		);

		// 検証
		assertEquals(value1, value2);
		assertNotEquals(value1, value3);
	}

	@Test
	@DisplayName("整合性検証：TotalWasteExpenditureと計算結果が一致すること")
	void testConsistencyWithTotalWasteExpenditure() {
		// 準備
		MinorWasteExpenditure minor = MinorWasteExpenditure.from(new BigDecimal("3000.00"));
		SevereWasteExpenditure severe = SevereWasteExpenditure.from(new BigDecimal("2000.00"));

		TotalWasteExpenditure monthly = TotalWasteExpenditure.from(minor, severe);
		WasteExpenditureTotalAmount yearly = WasteExpenditureTotalAmount.from(minor, severe);

		// 検証：同じ入力で同じ結果を返すこと
		assertEquals(monthly.getValue(), yearly.getValue(), "合計値が一致すること");
		assertEquals(monthly.getMinorWasteExpenditurePercentage(),
		             yearly.getMinorWasteExpenditurePercentage(), "軽度の割合が一致すること");

		// ExpenditureAmountを使った割合計算も一致すること
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("50000.00"));
		assertEquals(monthly.getPercentage(expenditure),
		             yearly.getPercentage(expenditure), "支出に対する割合が一致すること");
	}

}
