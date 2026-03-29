/**
 * ExpenditureItemCode(支出項目コード)のテストクラスです。
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
 * ExpenditureItemCode(支出項目コード)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@DisplayName("支出項目コード(ExpenditureItemCode)のテスト")
class ExpenditureItemCodeTest {

	@Test
	@DisplayName("正常系：有効な支出項目コード(4桁)で生成できる")
	void testFromString_正常系_有効な支出項目コード() {
		ExpenditureItemCode code = ExpenditureItemCode.from("0001");
		assertNotNull(code);
		assertEquals("0001", code.getValue());
		assertEquals("0001", code.toString());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFromString_異常系_null値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ExpenditureItemCode.from((String) null));
	}

	@Test
	@DisplayName("異常系：空文字で例外が発生する")
	void testFromString_異常系_空文字() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ExpenditureItemCode.from(""));
	}

	@Test
	@DisplayName("異常系：4桁でない(3桁)で例外が発生する")
	void testFromString_異常系_桁数不正_3桁() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ExpenditureItemCode.from("001"));
	}

	@Test
	@DisplayName("異常系：4桁でない(5桁)で例外が発生する")
	void testFromString_異常系_桁数不正_5桁() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ExpenditureItemCode.from("00001"));
	}

	@Test
	@DisplayName("異常系：数値に変換できない場合に例外が発生する")
	void testFromString_異常系_数値変換不可() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ExpenditureItemCode.from("ABCD"));
	}

	@Test
	@DisplayName("正常系：数値から支出項目コードを生成できる")
	void testFromInt_正常系() {
		ExpenditureItemCode code = ExpenditureItemCode.from(1);
		assertNotNull(code);
		assertEquals("0001", code.getValue());
	}

	@Test
	@DisplayName("正常系：数値から0パディングされた支出項目コードが生成される")
	void testFromInt_正常系_ゼロパディング() {
		assertEquals("0001", ExpenditureItemCode.from(1).getValue());
		assertEquals("0010", ExpenditureItemCode.from(10).getValue());
		assertEquals("0100", ExpenditureItemCode.from(100).getValue());
	}

	@Test
	@DisplayName("正常系：親支出項目コードから支出項目コードを生成できる")
	void testFromParentExpenditureItemCode_正常系() {
		ParentExpenditureItemCode parentCode = ParentExpenditureItemCode.from("0001");
		ExpenditureItemCode code = ExpenditureItemCode.from(parentCode);
		assertNotNull(code);
		assertEquals("0001", code.getValue());
	}

	@Test
	@DisplayName("正常系：getNewCodeで新規発番コード文字列を取得できる")
	void testGetNewCode_正常系() {
		assertEquals("0001", ExpenditureItemCode.getNewCode(1));
		assertEquals("0099", ExpenditureItemCode.getNewCode(99));
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		ExpenditureItemCode code1 = ExpenditureItemCode.from("0001");
		ExpenditureItemCode code2 = ExpenditureItemCode.from("0001");
		ExpenditureItemCode code3 = ExpenditureItemCode.from("0002");
		assertEquals(code1, code2);
		assertNotEquals(code1, code3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		ExpenditureItemCode code1 = ExpenditureItemCode.from("0001");
		ExpenditureItemCode code2 = ExpenditureItemCode.from("0001");
		assertEquals(code1.hashCode(), code2.hashCode());
	}

	@Test
	@DisplayName("正常系：toStringは値の文字列表現を返す")
	void testToString() {
		assertEquals("0001", ExpenditureItemCode.from("0001").toString());
	}
}
