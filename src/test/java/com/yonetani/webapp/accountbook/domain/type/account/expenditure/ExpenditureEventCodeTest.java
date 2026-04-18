/**
 * ExpenditureEventCode(支出テーブル情報のイベントコード項目)のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/04/12 : 1.00.00     新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.expenditure;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * ExpenditureEventCode(支出テーブル情報のイベントコード項目)のテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.00)
 *
 */
@DisplayName("支出テーブル情報のイベントコード(ExpenditureEventCode)のテスト")
class ExpenditureEventCodeTest {

	@Test
	@DisplayName("正常系：有効なイベントコード(4桁)で生成できる")
	void testFromString_正常系_有効なイベントコード() {
		ExpenditureEventCode code = ExpenditureEventCode.from("0001");
		assertNotNull(code);
		assertEquals("0001", code.getValue());
		assertEquals("0001", code.toString());
	}
	
	@Test
	@DisplayName("正常系：null値で生成できる")
	void testFrom_正常系_null値() {
		// 実行
		ExpenditureEventCode code = ExpenditureEventCode.from(null);

		// 検証
		assertNotNull(code);
		assertNull(code.getValue());
		assertEquals("", code.toString());
		assertEquals(ExpenditureEventCode.NULL.getValue(), code.getValue());
	}
	
	@Test
	@DisplayName("正常系：NULL定数が使用できる")
	void testNULL定数() {
		// 検証
		assertNotNull(ExpenditureEventCode.NULL);
		assertNull(ExpenditureEventCode.NULL.getValue());
	}
	
	@Test
	@DisplayName("正常系：空文字で生成できる(値はNULLと同様に扱う)")
	void testFrom_正常系_空文字() {
		// 実行
		ExpenditureEventCode code = ExpenditureEventCode.from("");

		// 検証
		assertNotNull(code);
		assertNull(code.getValue());
		assertEquals("", code.toString());
		assertEquals(ExpenditureEventCode.NULL.getValue(), code.getValue());
	}
	
	@Test
	@DisplayName("異常系：4桁でない(3桁)で例外が発生する")
	void testFromString_異常系_桁数不正_2桁() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ExpenditureEventCode.from("001"));
	}
	
	@Test
	@DisplayName("異常系：4桁でない(5桁)で例外が発生する")
	void testFromString_異常系_桁数不正_4桁() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ExpenditureEventCode.from("00001"));
	}
	
	@Test
	@DisplayName("異常系：数値に変換できない場合に例外が発生する")
	void testFromString_異常系_数値変換不可() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ExpenditureEventCode.from("ABCD"));
	}
	
	@Test
	@DisplayName("正常系：equalsが正しく動作する")
	void testEquals() {
		ExpenditureEventCode code1 = ExpenditureEventCode.from("0001");
		ExpenditureEventCode code2 = ExpenditureEventCode.from("0001");
		ExpenditureEventCode code3 = ExpenditureEventCode.from("0002");
		assertEquals(code1, code2);
		assertNotEquals(code1, code3);
	}

	@Test
	@DisplayName("正常系：hashCodeが正しく動作する")
	void testHashCode() {
		ExpenditureEventCode code1 = ExpenditureEventCode.from("0001");
		ExpenditureEventCode code2 = ExpenditureEventCode.from("0001");
		assertEquals(code1.hashCode(), code2.hashCode());
	}
}
