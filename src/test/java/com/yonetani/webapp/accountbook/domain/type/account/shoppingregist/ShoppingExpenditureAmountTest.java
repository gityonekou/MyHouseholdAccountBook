/**
 * ShoppingExpenditureAmount(買い物カテゴリごとの購入金額)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/09/20 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.shoppingregist;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * ShoppingExpenditureAmount(買い物カテゴリごとの購入金額)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("買い物カテゴリごとの購入金額(ShoppingExpenditureAmount)のテスト")
class ShoppingExpenditureAmountTest {
	@Test
	@DisplayName("正常系：正の金額で生成できる")
	void testFrom_正常系_正の金額() {
		// 実行
		ShoppingExpenditureAmount amount = ShoppingExpenditureAmount.from(new BigDecimal("10000.00"));

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
		ShoppingExpenditureAmount amount = ShoppingExpenditureAmount.from((BigDecimal)null);

		// 検証
		assertNotNull(amount);
		assertNull(amount.getValue());
		assertTrue(amount.isNull());
		assertEquals("", amount.toString());
		assertEquals("", amount.toFormatString());
	}

	@Test
	@DisplayName("異常系：負の金額で例外が発生する")
	void testFrom_異常系_負の金額() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> ShoppingExpenditureAmount.from(new BigDecimal("-1000.00"))
		);
		assertTrue(exception.getMessage().contains("買い物購入金額"));
		assertTrue(exception.getMessage().contains("マイナス"));
	}

	@Test
	@DisplayName("異常系：スケール値が2以外で例外が発生する")
	void testFrom_異常系_スケール値不正() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> ShoppingExpenditureAmount.from(new BigDecimal("10000"))
		);
		assertTrue(exception.getMessage().contains("買い物購入金額"));
		assertTrue(exception.getMessage().contains("スケール"));
	}
	
	@Test
	@DisplayName("正常系：from(Integer)でInteger値から生成できる")
	void testFromInteger_正常系_正の金額() {
		// 実行
		ShoppingExpenditureAmount amount = ShoppingExpenditureAmount.from(Integer.valueOf(10000));

		// 検証
		assertNotNull(amount);
		assertEquals(0, new BigDecimal("10000.00").compareTo(amount.getValue()));
		assertEquals("10,000円", amount.toFormatString());
	}

	@Test
	@DisplayName("正常系：from(Integer)で0を指定して生成できる")
	void testFromInteger_正常系_ゼロ() {
		// 実行
		ShoppingExpenditureAmount amount = ShoppingExpenditureAmount.from(Integer.valueOf(0));

		// 検証
		assertNotNull(amount);
		assertEquals(0, new BigDecimal("0.00").compareTo(amount.getValue()));
		assertTrue(amount.isZero());
	}

	@Test
	@DisplayName("正常系：from(Integer)でnullを指定するとnull値で生成できる")
	void testFromInteger_正常系_null値() {
		// 実行
		ShoppingExpenditureAmount amount = ShoppingExpenditureAmount.from((Integer)null);

		// 検証
		assertNotNull(amount);
		assertNull(amount.getValue());
		assertTrue(amount.isNull());
		assertEquals("", amount.toString());
	}

	@Test
	@DisplayName("異常系：from(Integer)でマイナス値を指定すると例外が発生する")
	void testFromInteger_異常系_負の金額() {
		// 実行 & 検証
		MyHouseholdAccountBookRuntimeException exception = assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> ShoppingExpenditureAmount.from(Integer.valueOf(-1000))
		);
		assertTrue(exception.getMessage().contains("買い物購入金額"));
		assertTrue(exception.getMessage().contains("マイナス"));
	}
	
	@Test
	@DisplayName("正常系：NULL定数が使用できる")
	void testNULL定数() {
		
		// 準備
		ShoppingExpenditureAmount nullValue = ShoppingExpenditureAmount.from((BigDecimal)null);
		
		// 検証
		assertNotNull(ShoppingExpenditureAmount.NULL);
		assertNull(ShoppingExpenditureAmount.NULL.getValue());
		assertEquals(ShoppingExpenditureAmount.NULL, nullValue);
	}
	
	@Test
	@DisplayName("正常系：加算が正しく動作する")
	void testAdd_正常系() {
		// 準備
		ShoppingExpenditureAmount amount1 = ShoppingExpenditureAmount.from(new BigDecimal("10000.00"));
		ShoppingExpenditureAmount amount2 = ShoppingExpenditureAmount.from(new BigDecimal("5000.00"));

		// 実行
		ShoppingExpenditureAmount result = amount1.add(amount2);

		// 検証
		assertEquals(new BigDecimal("15000.00"), result.getValue());
		assertEquals(new BigDecimal("10000.00"), amount1.getValue()); // 元の値が変わっていないこと
	}

	@Test
	@DisplayName("正常系：null値との加算")
	void testAdd_正常系_null値() {
		// 準備
		ShoppingExpenditureAmount amount1 = ShoppingExpenditureAmount.from(new BigDecimal("10000.00"));
		ShoppingExpenditureAmount amount2 = ShoppingExpenditureAmount.from((BigDecimal)null);

		// 実行
		ShoppingExpenditureAmount result = amount1.add(amount2);
		
		// 検証（null as zero扱い）
		assertEquals(new BigDecimal("10000.00"), result.getValue());
	}
}
