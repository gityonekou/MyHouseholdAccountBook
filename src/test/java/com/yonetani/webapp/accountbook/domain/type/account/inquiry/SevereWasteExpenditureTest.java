/**
 * SevereWasteExpenditure(無駄遣い（重度）支出金額)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2025/12/23 : 1.00.00  新規作成
 * 2025/12/28 : 1.01.00  クラス名変更(SisyutuKingakuCTest → SevereWasteExpenditureTest)
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
 * SevereWasteExpenditure(無駄遣い（重度）支出金額)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("無駄遣い（重度）支出金額(SevereWasteExpenditure)のテスト")
class SevereWasteExpenditureTest {

	@Test
	@DisplayName("正常系：正の金額で生成できる")
	void testFrom_正常系_正の金額() {
		// 実行
		SevereWasteExpenditure amount = SevereWasteExpenditure.from(new BigDecimal("10000.00"));

		// 検証
		assertNotNull(amount);
		assertEquals(new BigDecimal("10000.00"), amount.getValue());
		assertEquals("10000.00", amount.toString());
		assertEquals("10,000円", amount.toFormatString());
	}

	@Test
	@DisplayName("正常系：null値で生成できる")
	void testFrom_正常系_null値() {
		// 実行
		SevereWasteExpenditure amount = SevereWasteExpenditure.from(null);

		// 検証
		assertNotNull(amount);
		assertNull(amount.getValue());
		assertTrue(amount.isNull());
		assertEquals("", amount.toString());
		assertEquals("", amount.toFormatString());
	}

	@Test
	@DisplayName("正常系：ZERO定数が使用できる")
	void testZERO定数() {
		// 検証
		assertNotNull(SevereWasteExpenditure.ZERO);
		assertTrue(SevereWasteExpenditure.ZERO.isZero());
		assertEquals(BigDecimal.ZERO.setScale(2), SevereWasteExpenditure.ZERO.getValue());
	}

	@Test
	@DisplayName("異常系：負の金額で例外が発生する")
	void testFrom_異常系_負の金額() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> SevereWasteExpenditure.from(new BigDecimal("-1000.00"))
		);
		assertTrue(exception.getMessage().contains("無駄遣い（重度）支出金額"));
		assertTrue(exception.getMessage().contains("マイナス"));
	}

	@Test
	@DisplayName("異常系：スケール値が2以外で例外が発生する")
	void testFrom_異常系_スケール値不正() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> SevereWasteExpenditure.from(new BigDecimal("10000"))
		);
		assertTrue(exception.getMessage().contains("無駄遣い（重度）支出金額"));
		assertTrue(exception.getMessage().contains("スケール"));
	}

	@Test
	@DisplayName("正常系：加算が正しく動作する")
	void testAdd_正常系() {
		// 準備
		SevereWasteExpenditure amount1 = SevereWasteExpenditure.from(new BigDecimal("10000.00"));
		SevereWasteExpenditure amount2 = SevereWasteExpenditure.from(new BigDecimal("5000.00"));

		// 実行
		SevereWasteExpenditure result = amount1.add(amount2);

		// 検証
		assertEquals(new BigDecimal("15000.00"), result.getValue());
		assertEquals(new BigDecimal("10000.00"), amount1.getValue()); // 元の値が変わっていないこと
	}

	@Test
	@DisplayName("正常系：null値との加算")
	void testAdd_正常系_null値() {
		// 準備
		SevereWasteExpenditure amount1 = SevereWasteExpenditure.from(new BigDecimal("10000.00"));
		SevereWasteExpenditure amount2 = SevereWasteExpenditure.from(null);

		// 実行
		SevereWasteExpenditure result = amount1.add(amount2);

		// 検証（null as zero扱い）
		assertEquals(new BigDecimal("10000.00"), result.getValue());
	}

	@Test
	@DisplayName("正常系：減算が正しく動作する")
	void testSubtract_正常系() {
		// 準備
		SevereWasteExpenditure amount1 = SevereWasteExpenditure.from(new BigDecimal("10000.00"));
		SevereWasteExpenditure amount2 = SevereWasteExpenditure.from(new BigDecimal("3000.00"));

		// 実行
		SevereWasteExpenditure result = amount1.subtract(amount2);

		// 検証
		assertEquals(new BigDecimal("7000.00"), result.getValue());
		assertEquals(new BigDecimal("10000.00"), amount1.getValue()); // 元の値が変わっていないこと
	}

	@Test
	@DisplayName("異常系：null値からの減算で例外が発生する")
	void testSubtract_異常系_null値から減算() {
		// 準備
		SevereWasteExpenditure amount1 = SevereWasteExpenditure.from(null);
		SevereWasteExpenditure amount2 = SevereWasteExpenditure.from(new BigDecimal("3000.00"));

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> amount1.subtract(amount2)
		);
		assertTrue(exception.getMessage().contains("null"));
	}

	@Test
	@DisplayName("正常系：null値を減算する")
	void testSubtract_正常系_null値を減算() {
		// 準備
		SevereWasteExpenditure amount1 = SevereWasteExpenditure.from(new BigDecimal("10000.00"));
		SevereWasteExpenditure amount2 = SevereWasteExpenditure.from(null);

		// 実行
		SevereWasteExpenditure result = amount1.subtract(amount2);

		// 検証（null as zero扱い）
		assertEquals(new BigDecimal("10000.00"), result.getValue());
	}

	@Test
	@DisplayName("正常系：getPercentageが正しく割合を計算する")
	void testGetPercentage_正常系() {
		// 準備
		SevereWasteExpenditure severeWaste = SevereWasteExpenditure.from(new BigDecimal("3000.00"));
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("10000.00"));

		// 実行
		String percentage = severeWaste.getPercentage(expenditure);

		// 検証（3000/10000*100 = 30%）
		assertEquals("30", percentage);
	}

	@Test
	@DisplayName("正常系：getPercentageで四捨五入が正しく動作する")
	void testGetPercentage_正常系_四捨五入() {
		// 準備
		SevereWasteExpenditure severeWaste = SevereWasteExpenditure.from(new BigDecimal("3333.00"));
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("10000.00"));

		// 実行
		String percentage = severeWaste.getPercentage(expenditure);

		// 検証（3333/10000*100 = 33.33 -> 33%）
		assertEquals("33", percentage);
	}

	@Test
	@DisplayName("正常系：getPercentageでnull値は空文字列を返す")
	void testGetPercentage_正常系_null値() {
		// 準備
		SevereWasteExpenditure severeWaste = SevereWasteExpenditure.from(null);
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("10000.00"));

		// 実行
		String percentage = severeWaste.getPercentage(expenditure);

		// 検証
		assertEquals("", percentage);
	}

	@Test
	@DisplayName("正常系：getPercentageで0円は空文字列を返す")
	void testGetPercentage_正常系_0円() {
		// 準備
		SevereWasteExpenditure severeWaste = SevereWasteExpenditure.ZERO;
		ExpenditureAmount expenditure = ExpenditureAmount.from(new BigDecimal("10000.00"));

		// 実行
		String percentage = severeWaste.getPercentage(expenditure);

		// 検証
		assertEquals("", percentage);
	}

	@Test
	@DisplayName("異常系：getPercentageで支出金額がnullの場合は例外")
	void testGetPercentage_異常系_支出金額null() {
		// 準備
		SevereWasteExpenditure severeWaste = SevereWasteExpenditure.from(new BigDecimal("3000.00"));

		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> severeWaste.getPercentage(null)
		);
		assertTrue(exception.getMessage().contains("支出金額"));
		assertTrue(exception.getMessage().contains("null"));
	}
}
