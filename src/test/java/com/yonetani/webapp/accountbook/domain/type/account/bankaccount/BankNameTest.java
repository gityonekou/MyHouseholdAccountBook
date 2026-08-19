/**
 * BankNameクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/08/18 : 1.00.00     新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.bankaccount;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * BankNameクラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("銀行名(BankName)のテスト")
class BankNameTest {

	@Test
	@DisplayName("正常系：値から生成")
	void testFrom_正常系_値から生成() {
		BankName name = BankName.from("テスト銀行");
		// 検証
		assertNotNull(name);
		assertEquals("テスト銀行", name.getValue());
		assertEquals("テスト銀行", name.toString());
	}

	@Test
	@DisplayName("正常系：50文字ちょうどで生成できる")
	void testFrom_正常系_50文字ちょうど() {
		String value50 = "あ".repeat(50);
		BankName name = BankName.from(value50);
		// 検証
		assertEquals(50, name.getValue().length());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> BankName.from(null));
	}

	@Test
	@DisplayName("異常系：空文字列で例外が発生する")
	void testFrom_異常系_空文字列値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> BankName.from(""));
	}

	@Test
	@DisplayName("異常系：51文字で例外が発生する")
	void testFrom_異常系_51文字() {
		String value51 = "あ".repeat(51);
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> BankName.from(value51));
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		BankName name1 = BankName.from("テスト銀行");
		BankName name2 = BankName.from("テスト銀行");
		// 検証
		assertEquals(name1, name2);
		assertEquals(name1.hashCode(), name2.hashCode());
	}
}
