/**
 * WithdrawingTotalAmount(積立金取崩金額合計)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/01/01 : 1.00.00     新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *<pre>
 * WithdrawingTotalAmount(積立金取崩金額合計)のテストクラスです。
 *
 * [注意]
 * WithdrawingTotalAmountは特殊なクラスで、null値を許容します。
 * これは積立金取崩しがない場合を表現するためです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00.00)
 *
 */
@DisplayName("積立金取崩金額合計(WithdrawingTotalAmount)のテスト")
class WithdrawingTotalAmountTest {
	
	@Test
	@DisplayName("正常系：正の金額で生成できる")
	void testFrom_正常系_正の金額() {
		// 実行
		WithdrawingTotalAmount amount = WithdrawingTotalAmount.from(new BigDecimal("25000.00"));

		// 検証
		assertNotNull(amount);
		assertEquals(new BigDecimal("25000.00"), amount.getValue());
		assertEquals("25000.00", amount.toString());
		assertEquals("25,000円", amount.toFormatString());
		assertNotEquals(WithdrawingTotalAmount.NULL, amount);
	}
	
	@Test
	@DisplayName("正常系：ZERO定数が使用できる")
	void testZERO定数() {
		// 検証
		assertNotNull(WithdrawingTotalAmount.ZERO);
		assertEquals(BigDecimal.ZERO.setScale(2), WithdrawingTotalAmount.ZERO.getValue());
	}
	
	@Test
	@DisplayName("正常系：null値で生成できる（積立金取崩しがない場合）")
	void testFrom_正常系_null値() {
		// 実行
		WithdrawingTotalAmount amount = WithdrawingTotalAmount.from((BigDecimal)null);

		// 検証
		assertNotNull(amount);
		assertNull(amount.getValue());
		assertEquals("", amount.toString());
		assertEquals("", amount.toFormatString());
	}
	
	@Test
	@DisplayName("正常系：NULL定数が使用できる")
	void testNULL定数() {
		// 検証
		assertNotNull(WithdrawingTotalAmount.NULL);
		assertNull(WithdrawingTotalAmount.NULL.getValue());
	}
	
	@Test
	@DisplayName("正常系：equalsが正しく動作する（null値とNULL定数）")
	void testEquals_null値とNULL定数() {
		// 準備
		WithdrawingTotalAmount nullAmount = WithdrawingTotalAmount.from((BigDecimal)null);

		// 検証
		assertEquals(WithdrawingTotalAmount.NULL, nullAmount);
	}
	
	@Test
	@DisplayName("正常系：ZERO定数とNULL定数は異なる")
	void testZEROとNULLの違い() {
		// 検証
		assertNotEquals(WithdrawingTotalAmount.ZERO, WithdrawingTotalAmount.NULL);
		assertNotNull(WithdrawingTotalAmount.ZERO.getValue());
		assertNull(WithdrawingTotalAmount.NULL.getValue());
		assertEquals(BigDecimal.ZERO.setScale(2), WithdrawingTotalAmount.ZERO.getValue());
		assertEquals(BigDecimal.ZERO.setScale(2), WithdrawingTotalAmount.NULL.getNullSafeValue());
	}
	
	@Test
	@DisplayName("正常系：複数回null生成しても同じ内容")
	void testMultipleNull生成() {
		// 実行
		WithdrawingTotalAmount null1 = WithdrawingTotalAmount.from((BigDecimal)null);
		WithdrawingTotalAmount null2 = WithdrawingTotalAmount.from((BigDecimal)null);
		WithdrawingTotalAmount null3 = WithdrawingTotalAmount.NULL;
		
		// 検証（すべて等しい）
		assertEquals(null1, null2);
		assertEquals(null2, null3);
		assertEquals(null1, null3);
	}
	
	@Test
	@DisplayName("正常系：加算が正しく動作する")
	void testAdd_正常系() {
		// 準備
		WithdrawingTotalAmount totalAmount = WithdrawingTotalAmount.from(new BigDecimal("5000.00"));
		WithdrawingAmount addValue = WithdrawingAmount.from(new BigDecimal("2000.00"));

		// 実行
		WithdrawingTotalAmount result = totalAmount.add(addValue);

		// 検証
		assertNotNull(result);
		assertEquals(new BigDecimal("7000.00"), result.getValue());
		assertEquals(new BigDecimal("5000.00"), totalAmount.getValue()); // 元の値が変わっていないこと
	}
	
	@Test
	@DisplayName("正常系：null値との加算が正しく動作する")
	void testAdd_正常系_null値との加算() {
		// 準備
		WithdrawingTotalAmount totalAmount = WithdrawingTotalAmount.from(new BigDecimal("5000.00"));
		WithdrawingAmount addValue = WithdrawingAmount.from((BigDecimal)null);

		// 実行
		WithdrawingTotalAmount result = totalAmount.add(addValue);

		// 検証
		assertNotNull(result);
		assertEquals(new BigDecimal("5000.00"), result.getValue());
		assertEquals(new BigDecimal("5000.00"), totalAmount.getValue()); // 元の値が変わっていないこと
	}
}
