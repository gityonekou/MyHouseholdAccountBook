/**
 * ExpenditureCode(支出コード)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/03/15 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.inquiry;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * ExpenditureCode(支出コード)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@DisplayName("支出コード(ExpenditureCode)のテスト")
class ExpenditureCodeTest {

	@Test
	@DisplayName("正常系：有効な支出コード(3桁)で生成できる")
	void testFromString_正常系_有効な支出コード() {
		ExpenditureCode code = ExpenditureCode.from("001");
		assertNotNull(code);
		assertEquals("001", code.getValue());
		assertEquals("001", code.toString());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFromString_異常系_null値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ExpenditureCode.from((String) null));
	}

	@Test
	@DisplayName("異常系：空文字で例外が発生する")
	void testFromString_異常系_空文字() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ExpenditureCode.from(""));
	}

	@Test
	@DisplayName("異常系：3桁でない(2桁)で例外が発生する")
	void testFromString_異常系_桁数不正_2桁() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ExpenditureCode.from("01"));
	}

	@Test
	@DisplayName("異常系：3桁でない(4桁)で例外が発生する")
	void testFromString_異常系_桁数不正_4桁() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ExpenditureCode.from("0001"));
	}

	@Test
	@DisplayName("異常系：数値に変換できない場合に例外が発生する")
	void testFromString_異常系_数値変換不可() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ExpenditureCode.from("ABC"));
	}

	@Test
	@DisplayName("正常系：数値から支出コードを生成できる")
	void testFromInt_正常系() {
		ExpenditureCode code = ExpenditureCode.from(1);
		assertNotNull(code);
		assertEquals("001", code.getValue());
	}

	@Test
	@DisplayName("正常系：数値から0パディングされた支出コードが生成される")
	void testFromInt_正常系_ゼロパディング() {
		assertEquals("001", ExpenditureCode.from(1).getValue());
		assertEquals("010", ExpenditureCode.from(10).getValue());
		assertEquals("100", ExpenditureCode.from(100).getValue());
	}

	@Test
	@DisplayName("正常系：getNewCodeで新規発番コード文字列を取得できる")
	void testGetNewCode_正常系() {
		assertEquals("001", ExpenditureCode.getNewCode(1));
		assertEquals("099", ExpenditureCode.getNewCode(99));
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		ExpenditureCode code1 = ExpenditureCode.from("001");
		ExpenditureCode code2 = ExpenditureCode.from("001");
		ExpenditureCode code3 = ExpenditureCode.from("002");
		assertEquals(code1, code2);
		assertNotEquals(code1, code3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		ExpenditureCode code1 = ExpenditureCode.from("001");
		ExpenditureCode code2 = ExpenditureCode.from("001");
		assertEquals(code1.hashCode(), code2.hashCode());
	}

	@Test
	@DisplayName("正常系：toStringは値の文字列表現を返す")
	void testToString() {
		assertEquals("001", ExpenditureCode.from("001").toString());
	}
}
