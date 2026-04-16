/**
 * EventCode(イベントコード)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/04/12 : 1.00.00     新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.event;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;
import com.yonetani.webapp.accountbook.domain.type.account.expenditure.ExpenditureCode;

/**
 *<pre>
 * EventCode(イベントコード)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@DisplayName("イベントコード(EventCode)のテスト")
class EventCodeTest {
	@Test
	@DisplayName("正常系：有効なイベントコード(4桁)で生成できる")
	void testFromString_正常系_有効なイベントコード() {
		EventCode code = EventCode.from("0001");
		assertNotNull(code);
		assertEquals("0001", code.getValue());
		assertEquals("0001", code.toString());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFromString_異常系_null値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> EventCode.from((String) null));
	}

	@Test
	@DisplayName("異常系：空文字で例外が発生する")
	void testFromString_異常系_空文字() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> EventCode.from(""));
	}

	@Test
	@DisplayName("異常系：4桁でない(3桁)で例外が発生する")
	void testFromString_異常系_桁数不正_2桁() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> EventCode.from("001"));
	}

	@Test
	@DisplayName("異常系：4桁でない(5桁)で例外が発生する")
	void testFromString_異常系_桁数不正_4桁() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> EventCode.from("00001"));
	}

	@Test
	@DisplayName("異常系：数値に変換できない場合に例外が発生する")
	void testFromString_異常系_数値変換不可() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> EventCode.from("ABCD"));
	}

	@Test
	@DisplayName("正常系：数値からイベントコードを生成できる")
	void testFromInt_正常系() {
		EventCode code = EventCode.from(1);
		assertNotNull(code);
		assertEquals("0001", code.getValue());
	}

	@Test
	@DisplayName("正常系：数値から0パディングされたイベントコードが生成される")
	void testFromInt_正常系_ゼロパディング() {
		assertEquals("0001", EventCode.from(1).getValue());
		assertEquals("0010", EventCode.from(10).getValue());
		assertEquals("0100", EventCode.from(100).getValue());
		assertEquals("1000", EventCode.from(1000).getValue());
	}

	@Test
	@DisplayName("正常系：getNewCodeで新規発番コード文字列を取得できる")
	void testGetNewCode_正常系() {
		assertEquals("0001", EventCode.getNewCode(1));
		assertEquals("0099", EventCode.getNewCode(99));
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
