/**
 * ParentExpenditureItemCode(親支出項目コード)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/03/15 : 1.00.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.expenditureinfo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * ParentExpenditureItemCode(親支出項目コード)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@DisplayName("親支出項目コード(ParentExpenditureItemCode)のテスト")
class ParentExpenditureItemCodeTest {

	@Test
	@DisplayName("正常系：有効な親支出項目コード(4桁)で生成できる")
	void testFrom_正常系_有効な親支出項目コード() {
		ParentExpenditureItemCode code = ParentExpenditureItemCode.from("0001");
		assertNotNull(code);
		assertEquals("0001", code.getValue());
		assertEquals("0001", code.toString());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ParentExpenditureItemCode.from(null));
	}

	@Test
	@DisplayName("異常系：空文字で例外が発生する")
	void testFrom_異常系_空文字() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ParentExpenditureItemCode.from(""));
	}

	@Test
	@DisplayName("異常系：4桁でない(3桁)で例外が発生する")
	void testFrom_異常系_桁数不正_3桁() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ParentExpenditureItemCode.from("001"));
	}

	@Test
	@DisplayName("異常系：4桁でない(5桁)で例外が発生する")
	void testFrom_異常系_桁数不正_5桁() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ParentExpenditureItemCode.from("00001"));
	}

	@Test
	@DisplayName("異常系：数値に変換できない場合に例外が発生する")
	void testFrom_異常系_数値変換不可() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ParentExpenditureItemCode.from("ABCD"));
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		ParentExpenditureItemCode code1 = ParentExpenditureItemCode.from("0001");
		ParentExpenditureItemCode code2 = ParentExpenditureItemCode.from("0001");
		ParentExpenditureItemCode code3 = ParentExpenditureItemCode.from("0002");
		assertEquals(code1, code2);
		assertNotEquals(code1, code3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		ParentExpenditureItemCode code1 = ParentExpenditureItemCode.from("0001");
		ParentExpenditureItemCode code2 = ParentExpenditureItemCode.from("0001");
		assertEquals(code1.hashCode(), code2.hashCode());
	}

	@Test
	@DisplayName("正常系：toStringは値の文字列表現を返す")
	void testToString() {
		assertEquals("0001", ParentExpenditureItemCode.from("0001").toString());
	}
}
