/**
 * TotalWasteExpenditure(無駄遣い合計支出金額)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/25 : 1.00.00  新規作成
 * 2025/12/28 : 1.01.00  クラス名変更(SisyutuKingakuBCTest → TotalWasteExpenditureTest)
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
 * TotalWasteExpenditure(無駄遣い合計支出金額)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("無駄遣い合計支出金額(TotalWasteExpenditure)のテスト")
class TotalWasteExpenditureTest {

	@Test
	@DisplayName("正常系：無駄遣い（軽度）と無駄遣い（重度）の両方が値ありで生成できる")
	void testFrom_正常系_軽度と重度両方値あり() {
		// 準備
		MinorWasteExpenditure minor = MinorWasteExpenditure.from(new BigDecimal("10000.00"));
		SevereWasteExpenditure severe = SevereWasteExpenditure.from(new BigDecimal("5000.00"));

		// 実行
		TotalWasteExpenditure amount = TotalWasteExpenditure.from(minor, severe);

		// 検証
		assertNotNull(amount);
		assertEquals(new BigDecimal("15000.00"), amount.getValue()); // 軽度 + 重度
		assertEquals(minor, amount.getMinorWasteExpenditure());
		assertEquals(severe, amount.getSevereWasteExpenditure());
		assertEquals("15000.00", amount.toString());
		assertEquals("15,000円", amount.toFormatString());
	}

	@Test
	@DisplayName("正常系：無駄遣い（軽度）がnull値、無駄遣い（重度）が値ありで生成できる")
	void testFrom_正常系_軽度がnull値_重度が値あり() {
		// 準備
		MinorWasteExpenditure minor = MinorWasteExpenditure.from(null);
		SevereWasteExpenditure severe = SevereWasteExpenditure.from(new BigDecimal("5000.00"));

		// 実行
		TotalWasteExpenditure amount = TotalWasteExpenditure.from(minor, severe);

		// 検証
		assertNotNull(amount);
		assertEquals(new BigDecimal("5000.00"), amount.getValue()); // 重度の値
		assertEquals(minor, amount.getMinorWasteExpenditure());
		assertEquals(severe, amount.getSevereWasteExpenditure());
	}

	@Test
	@DisplayName("正常系：無駄遣い（軽度）が値あり、無駄遣い（重度）がnull値で生成できる")
	void testFrom_正常系_軽度が値あり_重度がnull値() {
		// 準備
		MinorWasteExpenditure minor = MinorWasteExpenditure.from(new BigDecimal("10000.00"));
		SevereWasteExpenditure severe = SevereWasteExpenditure.from(null);

		// 実行
		TotalWasteExpenditure amount = TotalWasteExpenditure.from(minor, severe);

		// 検証
		assertNotNull(amount);
		assertEquals(new BigDecimal("10000.00"), amount.getValue()); // 軽度の値
		assertEquals(minor, amount.getMinorWasteExpenditure());
		assertEquals(severe, amount.getSevereWasteExpenditure());
	}

	@Test
	@DisplayName("正常系：無駄遣い（軽度）と無駄遣い（重度）両方がnull値で生成できる")
	void testFrom_正常系_軽度と重度両方null値() {
		// 準備
		MinorWasteExpenditure minor = MinorWasteExpenditure.from(null);
		SevereWasteExpenditure severe = SevereWasteExpenditure.from(null);

		// 実行
		TotalWasteExpenditure amount = TotalWasteExpenditure.from(minor, severe);

		// 検証
		assertNotNull(amount);
		assertNull(amount.getValue());
		assertEquals(minor, amount.getMinorWasteExpenditure());
		assertEquals(severe, amount.getSevereWasteExpenditure());
		assertEquals("", amount.toString());
		assertEquals("", amount.toFormatString());
	}

	@Test
	@DisplayName("正常系：ZERO定数が使用できる")
	void testZERO定数() {
		// 検証
		assertNotNull(TotalWasteExpenditure.ZERO);
		assertEquals(BigDecimal.ZERO.setScale(2), TotalWasteExpenditure.ZERO.getValue());
		assertTrue(TotalWasteExpenditure.ZERO.getMinorWasteExpenditure().isZero());
		assertTrue(TotalWasteExpenditure.ZERO.getSevereWasteExpenditure().isZero());
	}

	@Test
	@DisplayName("異常系：無駄遣い（軽度）がnullで例外が発生する")
	void testFrom_異常系_軽度がnull() {
		// 準備
		SevereWasteExpenditure severe = SevereWasteExpenditure.from(new BigDecimal("5000.00"));

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TotalWasteExpenditure.from(null, severe)
		);
		assertTrue(exception.getMessage().contains("無駄遣い（軽度）支出金額"));
	}

	@Test
	@DisplayName("異常系：無駄遣い（重度）がnullで例外が発生する")
	void testFrom_異常系_重度がnull() {
		// 準備
		MinorWasteExpenditure minor = MinorWasteExpenditure.from(new BigDecimal("10000.00"));

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TotalWasteExpenditure.from(minor, null)
		);
		assertTrue(exception.getMessage().contains("無駄遣い（重度）支出金額"));
	}

	@Test
	@DisplayName("正常系：加算が正しく動作する")
	void testAdd_正常系() {
		// 準備
		TotalWasteExpenditure amount1 = TotalWasteExpenditure.from(
			MinorWasteExpenditure.from(new BigDecimal("10000.00")),
			SevereWasteExpenditure.from(new BigDecimal("5000.00"))
		);
		TotalWasteExpenditure amount2 = TotalWasteExpenditure.from(
			MinorWasteExpenditure.from(new BigDecimal("3000.00")),
			SevereWasteExpenditure.from(new BigDecimal("2000.00"))
		);

		// 実行
		TotalWasteExpenditure result = amount1.add(amount2);

		// 検証
		assertEquals(new BigDecimal("20000.00"), result.getValue());
		assertEquals(new BigDecimal("13000.00"), result.getMinorWasteExpenditure().getValue());
		assertEquals(new BigDecimal("7000.00"), result.getSevereWasteExpenditure().getValue());
		// 元の値が変わっていないこと
		assertEquals(new BigDecimal("15000.00"), amount1.getValue());
	}

	@Test
	@DisplayName("正常系：null値との加算")
	void testAdd_正常系_null値() {
		// 準備
		TotalWasteExpenditure amount1 = TotalWasteExpenditure.from(
			MinorWasteExpenditure.from(new BigDecimal("10000.00")),
			SevereWasteExpenditure.from(new BigDecimal("5000.00"))
		);
		TotalWasteExpenditure amount2 = TotalWasteExpenditure.from(
			MinorWasteExpenditure.from(null),
			SevereWasteExpenditure.from(null)
		);

		// 実行
		TotalWasteExpenditure result = amount1.add(amount2);

		// 検証（null as zero扱い）
		assertEquals(new BigDecimal("15000.00"), result.getValue());
	}

	@Test
	@DisplayName("正常系：getPercentage()で支出金額に対する割合を取得")
	void testGetPercentage() {
		// 準備
		TotalWasteExpenditure amount = TotalWasteExpenditure.from(
			MinorWasteExpenditure.from(new BigDecimal("10000.00")),
			SevereWasteExpenditure.from(new BigDecimal("5000.00"))
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
		TotalWasteExpenditure amount = TotalWasteExpenditure.from(
			MinorWasteExpenditure.from(null),
			SevereWasteExpenditure.from(null)
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
		TotalWasteExpenditure amount = TotalWasteExpenditure.from(
			MinorWasteExpenditure.from(new BigDecimal("10000.00")),
			SevereWasteExpenditure.from(new BigDecimal("5000.00"))
		);

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> amount.getPercentage(null)
		);
		assertTrue(exception.getMessage().contains("支出金額"));
	}

	@Test
	@DisplayName("正常系：getMinorWasteExpenditurePercentage()で無駄遣い（軽度）の割合を取得")
	void testGetMinorWasteExpenditurePercentage() {
		// 準備
		TotalWasteExpenditure amount = TotalWasteExpenditure.from(
			MinorWasteExpenditure.from(new BigDecimal("10000.00")),
			SevereWasteExpenditure.from(new BigDecimal("5000.00"))
		);

		// 実行
		String result = amount.getMinorWasteExpenditurePercentage();

		// 検証
		assertEquals("67", result); // 10000 / 15000 * 100 = 66.67 → 67
	}

	@Test
	@DisplayName("正常系：getMinorWasteExpenditurePercentage()でnull値の場合は0を返す")
	void testGetMinorWasteExpenditurePercentage_null値() {
		// 準備
		TotalWasteExpenditure amount = TotalWasteExpenditure.from(
			MinorWasteExpenditure.from(null),
			SevereWasteExpenditure.from(null)
		);

		// 実行
		String result = amount.getMinorWasteExpenditurePercentage();

		// 検証
		assertEquals("0", result);
	}
}
