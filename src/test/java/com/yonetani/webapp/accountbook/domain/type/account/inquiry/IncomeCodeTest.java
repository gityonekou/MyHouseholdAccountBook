/**
 * IncomeCode(収入コード)のテストクラスです。
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
 * IncomeCode(収入コード)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@DisplayName("収入コード(IncomeCode)のテスト")
class IncomeCodeTest {

	@Test
	@DisplayName("正常系：有効な収入コード(2桁)で生成できる")
	void testFromString_正常系_有効な収入コード() {
		IncomeCode code = IncomeCode.from("01");
		assertNotNull(code);
		assertEquals("01", code.getValue());
		assertEquals("01", code.toString());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFromString_異常系_null値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> IncomeCode.from((String) null));
	}

	@Test
	@DisplayName("異常系：空文字で例外が発生する")
	void testFromString_異常系_空文字() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> IncomeCode.from(""));
	}

	@Test
	@DisplayName("異常系：2桁でない(1桁)で例外が発生する")
	void testFromString_異常系_桁数不正_1桁() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> IncomeCode.from("1"));
	}

	@Test
	@DisplayName("異常系：2桁でない(3桁)で例外が発生する")
	void testFromString_異常系_桁数不正_3桁() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> IncomeCode.from("001"));
	}

	@Test
	@DisplayName("異常系：数値に変換できない場合に例外が発生する")
	void testFromString_異常系_数値変換不可() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> IncomeCode.from("AB"));
	}

	@Test
	@DisplayName("正常系：数値から収入コードを生成できる")
	void testFromInt_正常系() {
		IncomeCode code = IncomeCode.from(1);
		assertNotNull(code);
		assertEquals("01", code.getValue());
	}

	@Test
	@DisplayName("正常系：数値から0パディングされた収入コードが生成される")
	void testFromInt_正常系_ゼロパディング() {
		assertEquals("01", IncomeCode.from(1).getValue());
		assertEquals("09", IncomeCode.from(9).getValue());
		assertEquals("10", IncomeCode.from(10).getValue());
	}

	@Test
	@DisplayName("正常系：getNewCodeで新規発番コード文字列を取得できる")
	void testGetNewCode_正常系() {
		assertEquals("01", IncomeCode.getNewCode(1));
		assertEquals("99", IncomeCode.getNewCode(99));
	}

	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		IncomeCode code1 = IncomeCode.from("01");
		IncomeCode code2 = IncomeCode.from("01");
		IncomeCode code3 = IncomeCode.from("02");
		assertEquals(code1, code2);
		assertNotEquals(code1, code3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		IncomeCode code1 = IncomeCode.from("01");
		IncomeCode code2 = IncomeCode.from("01");
		assertEquals(code1.hashCode(), code2.hashCode());
	}

	@Test
	@DisplayName("正常系：toStringは値の文字列表現を返す")
	void testToString() {
		assertEquals("01", IncomeCode.from("01").toString());
	}
}
