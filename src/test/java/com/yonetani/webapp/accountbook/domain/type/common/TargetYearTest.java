/**
 * TargetYear（年）のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version     コメントなど
 * 2026/09/10 : 1.03.00  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * TargetYear（年）のテストクラスです。
 * C1（分岐網羅）で以下メソッドを検証します。
 * ・from(String year) ：ガード節3分岐＋正常系
 * ・toFormatString：4桁年（yyyy年）
 * ・toString：正常系
 * ・equals / hashCode：同値・異値
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("年項目(TargetYear)のテスト")
class TargetYearTest {

	// ===========================================================
	// from(String month)
	// ===========================================================

	@Test
	@DisplayName("正常系：有効な年(1900～2200年)で生成できる")
	void testFrom_正常系_string_有効な年1900() {
		// 実行
		TargetYear result = TargetYear.from("1900");

		// 検証
		assertNotNull(result);
		assertEquals("1900", result.getValue());
	}
	
	@Test
	@DisplayName("正常系：有効な年(1900～2200年)で生成できる")
	void testFrom_正常系_string_有効な年2200() {
		// 実行
		TargetYear result = TargetYear.from("2200");

		// 検証
		assertNotNull(result);
		assertEquals("2200", result.getValue());
	}
	
	@Test
	@DisplayName("異常系：null値で例外が発生する（ガード節①-A）")
	void testFrom_異常系_null() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetYear.from((String) null)
		);
	}

	@Test
	@DisplayName("異常系：空文字列で例外が発生する（ガード節①-B）")
	void testFrom_異常系_空文字列() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetYear.from("")
		);
	}

	@Test
	@DisplayName("異常系：3桁（4桁未満）で例外が発生する（ガード節②-A）")
	void testFrom_異常系_3桁() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetYear.from("123")
		);
	}

	@Test
	@DisplayName("異常系：5桁（4桁超）で例外が発生する（ガード節②-B）")
	void testFrom_異常系_5桁() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetYear.from("12345")
		);
	}
	
	@Test
	@DisplayName("異常系：1899年桁（1900未満）で例外が発生する（ガード節②-A）")
	void testFrom_異常系_1899() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetYear.from("1899")
		);
	}

	@Test
	@DisplayName("異常系：2201年（2200年超）で例外が発生する（ガード節②-B）")
	void testFrom_異常系_2201() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetYear.from("2201")
		);
	}
	
	// ===========================================================
	// toFormatString()
	// ===========================================================

	@Test
	@DisplayName("toFormatString：「YYYY年」形式で返す")
	void testToFormatString_yyyy年() {
		// 準備
		TargetYear result = TargetYear.from("2000");
		
		// 実行 & 検証
		assertEquals("2000年", result.toFormatString());
	}
	
	// ===========================================================
	// toString()
	// ===========================================================

	@Test
	@DisplayName("toString：YYYY形式の文字列値をそのまま返す")
	void testToString() {
		// 準備
		TargetYear result = TargetYear.from("2000");

		// 実行 & 検証
		assertEquals("2000", result.toString());
	}
	
	// ===========================================================
	// equals / hashCode
	// ===========================================================

	@Test
	@DisplayName("equals：同じ年は等しいと判定される")
	void testEquals_同値() {
		// 準備
		TargetYear result1 = TargetYear.from("2000");
		TargetYear result2 = TargetYear.from("2000");

		// 検証
		assertEquals(result1, result2);
	}
	
	@Test
	@DisplayName("equals：異なる年は等しくないと判定される")
	void testEquals_異なる値() {
		// 準備
		TargetYear result1 = TargetYear.from("2000");
		TargetYear result2 = TargetYear.from("1999");

		// 検証
		assertNotEquals(result1, result2);
	}
	
	@Test
	@DisplayName("hashCode：同じ年は同じhashCodeを返す")
	void testHashCode_同値() {
		// 準備
		TargetYear result1 = TargetYear.from("2000");
		TargetYear result2 = TargetYear.from("2000");

		// 検証
		assertEquals(result1.hashCode(), result2.hashCode());
	}
}
