/**
 * BankAccountSortクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/18 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.bankaccount;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * BankAccountSortクラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("銀行口座表示順(BankAccountSort)のテスト")
class BankAccountSortTest {

	@Test
	@DisplayName("正常系：文字列から生成")
	void testFrom_正常系_文字列から生成() {
		BankAccountSort sort = BankAccountSort.from("01");
		// 検証
		assertNotNull(sort);
		assertEquals("01", sort.getValue());
		assertEquals("01", sort.toString());
	}

	@Test
	@DisplayName("正常系：数値から生成")
	void testFrom_正常系_数値から生成() {
		BankAccountSort sort = BankAccountSort.from(3);
		// 検証
		assertEquals("03", sort.getValue());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> BankAccountSort.from((String) null));
	}

	@Test
	@DisplayName("異常系：空文字列で例外が発生する")
	void testFrom_異常系_空文字列値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> BankAccountSort.from(""));
	}

	@Test
	@DisplayName("異常系：2桁でない場合に例外が発生する")
	void testFrom_異常系_桁数不正() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> BankAccountSort.from("1"));
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> BankAccountSort.from("100"));
	}

	@Test
	@DisplayName("異常系：数値に変換できない場合に例外が発生する")
	void testFrom_異常系_数値変換不可() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> BankAccountSort.from("ab"));
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		BankAccountSort sort1 = BankAccountSort.from("01");
		BankAccountSort sort2 = BankAccountSort.from("01");
		// 検証
		assertEquals(sort1, sort2);
		assertEquals(sort1.hashCode(), sort2.hashCode());
	}
}
