/**
 * PaymentMethodMemoクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.paymentmethod;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * PaymentMethodMemoクラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("支払方法メモ(PaymentMethodMemo)のテスト")
class PaymentMethodMemoTest {

	@Test
	@DisplayName("正常系：値から生成")
	void testFrom_正常系_値から生成() {
		PaymentMethodMemo memo = PaymentMethodMemo.from("ポイント還元率1%");
		// 検証
		assertNotNull(memo);
		assertEquals("ポイント還元率1%", memo.getValue());
		assertEquals("ポイント還元率1%", memo.toString());
	}

	@Test
	@DisplayName("正常系：nullを許容する")
	void testFrom_正常系_null許容() {
		PaymentMethodMemo memo = PaymentMethodMemo.from(null);
		// 検証
		assertNotNull(memo);
		assertNull(memo.getValue());
	}

	@Test
	@DisplayName("正常系：400文字ちょうどで生成できる")
	void testFrom_正常系_400文字ちょうど() {
		String value400 = "あ".repeat(400);
		PaymentMethodMemo memo = PaymentMethodMemo.from(value400);
		// 検証
		assertEquals(400, memo.getValue().length());
	}

	@Test
	@DisplayName("異常系：401文字で例外が発生する")
	void testFrom_異常系_401文字() {
		String value401 = "あ".repeat(401);
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> PaymentMethodMemo.from(value401));
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		PaymentMethodMemo memo1 = PaymentMethodMemo.from("メモ");
		PaymentMethodMemo memo2 = PaymentMethodMemo.from("メモ");
		// 検証
		assertEquals(memo1, memo2);
		assertEquals(memo1.hashCode(), memo2.hashCode());
	}
}
