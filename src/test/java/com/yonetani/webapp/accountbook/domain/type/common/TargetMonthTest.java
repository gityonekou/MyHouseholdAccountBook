/**
 * TargetMonth（月）のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
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
 * TargetMonth（月）のテストクラスです。
 * C1（分岐網羅）で以下メソッドを検証します。
 * ・from(String month) ：ガード節3分岐＋正常系
 * ・from(int month) ：ガード節分岐＋正常系
 * ・toFormatString：1桁月（先頭0あり）・2桁月
 * ・intValue：01月、12月
 * ・toString：正常系
 * ・equals / hashCode：同値・異値
 * ・plus：年をまたがない加算・年をまたぐ加算・複数年またぐ加算・0加算・負数加算（減算相当）
 * ・minus：年をまたがない減算・年をまたぐ減算・複数年またぐ減算・0減算・負数減算（加算相当）
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("月項目(TargetMonth)のテスト")
class TargetMonthTest {

	// ===========================================================
	// from(String month)
	// ===========================================================

	@Test
	@DisplayName("正常系：有効な月(文字列：12)で生成できる")
	void testFrom_正常系_string_有効な月() {
		// 実行
		TargetMonth result = TargetMonth.from("12");

		// 検証
		assertNotNull(result);
		assertEquals("12", result.getValue());
	}

	@Test
	@DisplayName("正常系：月が01（先頭0あり）で生成できる(string型)")
	void testFrom_正常系_1桁月先頭0_string() {
		// 実行
		TargetMonth result = TargetMonth.from("01");

		// 検証
		assertNotNull(result);
		assertEquals("01", result.getValue());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する（ガード節①-A）")
	void testFrom_異常系_null() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetMonth.from((String) null)
		);
	}

	@Test
	@DisplayName("異常系：空文字列で例外が発生する（ガード節①-B）")
	void testFrom_異常系_空文字列() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetMonth.from("")
		);
	}

	@Test
	@DisplayName("異常系：1桁（2桁未満）で例外が発生する（ガード節②-A）")
	void testFrom_異常系_1桁() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetMonth.from("1")
		);
	}

	@Test
	@DisplayName("異常系：3桁（2桁超）で例外が発生する（ガード節②-B）")
	void testFrom_異常系_3桁() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetMonth.from("202")
		);
	}

	@Test
	@DisplayName("異常系：00月（カレンダー上無効）で例外が発生する（ガード節③）")
	void testFrom_異常系_00月() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetMonth.from("00")
		);
	}
	
	@Test
	@DisplayName("異常系：13月（カレンダー上無効）で例外が発生する（ガード節③）")
	void testFrom_異常系_13月_string() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetMonth.from("13")
		);
	}
	
	// ===========================================================
	// from(int month)
	// ===========================================================
	
	@Test
	@DisplayName("正常系：有効な月(数値：12)で生成できる")
	void testFrom_正常系_12月() {
		// 実行
		TargetMonth result = TargetMonth.from(12);

		// 検証
		assertNotNull(result);
		assertEquals("12", result.getValue());
	}

	@Test
	@DisplayName("正常系：1月で生成できる(int型)")
	void testFrom_正常系_1月() {
		// 実行
		TargetMonth result = TargetMonth.from(1);

		// 検証
		assertNotNull(result);
		assertEquals("01", result.getValue());
	}
	
	@Test
	@DisplayName("異常系：0月（カレンダー上無効）で例外が発生する（ガード節）")
	void testFrom_異常系_0月() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetMonth.from(0)
		);
	}
	
	@Test
	@DisplayName("異常系：13月（カレンダー上無効）で例外が発生する（ガード節）")
	void testFrom_異常系_13月() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetMonth.from(13)
		);
	}
	
	// ===========================================================
	// toFormatString()
	// ===========================================================

	@Test
	@DisplayName("toFormatString：2桁月は「MM月」形式で返す（01月、11月）")
	void testToFormatString_2桁月() {
		// 準備
		TargetMonth result1 = TargetMonth.from("01");
		TargetMonth result2 = TargetMonth.from(1);
		TargetMonth result3 = TargetMonth.from("11");
		TargetMonth result4 = TargetMonth.from(11);
		
		// 実行 & 検証
		assertEquals("01月", result1.toFormatString());
		assertEquals("01月", result2.toFormatString());
		assertEquals("11月", result3.toFormatString());
		assertEquals("11月", result4.toFormatString());
	}
	
	// ===========================================================
	// intValue()
	// ===========================================================

	@Test
	@DisplayName("intValue 月の値を整数値で返す（01月、11月）")
	void testIntValue() {
		// 準備
		TargetMonth result1 = TargetMonth.from("01");
		TargetMonth result2 = TargetMonth.from(1);
		TargetMonth result3 = TargetMonth.from("11");
		TargetMonth result4 = TargetMonth.from(11);

		// 実行 & 検証
		assertEquals(1, result1.intValue());
		assertEquals(1, result2.intValue());
		assertEquals(11, result3.intValue());
		assertEquals(11, result4.intValue());
	}
	
	// ===========================================================
	// toString()
	// ===========================================================

	@Test
	@DisplayName("toString：MM形式の文字列値をそのまま返す（01月、11月）")
	void testToString() {
		// 準備
		TargetMonth result1 = TargetMonth.from("01");
		TargetMonth result2 = TargetMonth.from(1);
		TargetMonth result3 = TargetMonth.from("11");
		TargetMonth result4 = TargetMonth.from(11);

		// 実行 & 検証
		assertEquals("01", result1.toString());
		assertEquals("01", result2.toString());
		assertEquals("11", result3.toString());
		assertEquals("11", result4.toString());
	}
	
	// ===========================================================
	// equals / hashCode
	// ===========================================================

	@Test
	@DisplayName("equals：同じ月は等しいと判定される①")
	void testEquals_同値1() {
		// 準備
		TargetMonth result1 = TargetMonth.from("11");
		TargetMonth result2 = TargetMonth.from("11");

		// 検証
		assertEquals(result1, result2);
	}

	@Test
	@DisplayName("equals：同じ月は等しいと判定される②")
	void testEquals_同値2() {
		// 準備
		TargetMonth result1 = TargetMonth.from(1);
		TargetMonth result2 = TargetMonth.from(1);

		// 検証
		assertEquals(result1, result2);
	}
	
	@Test
	@DisplayName("equals：同じ月は等しいと判定される③")
	void testEquals_同値3() {
		// 準備
		TargetMonth result1 = TargetMonth.from("10");
		TargetMonth result2 = TargetMonth.from(10);

		// 検証
		assertEquals(result1, result2);
	}
	
	@Test
	@DisplayName("equals：異なる月は等しくないと判定される①")
	void testEquals_異なる値1() {
		// 準備
		TargetMonth result1 = TargetMonth.from("11");
		TargetMonth result2 = TargetMonth.from("12");

		// 検証
		assertNotEquals(result1, result2);
	}

	@Test
	@DisplayName("equals：異なる月は等しくないと判定される②")
	void testEquals_異なる値2() {
		// 準備
		TargetMonth result1 = TargetMonth.from("11");
		TargetMonth result2 = TargetMonth.from(12);

		// 検証
		assertNotEquals(result1, result2);
	}
	
	@Test
	@DisplayName("hashCode：同じ年月は同じhashCodeを返す1")
	void testHashCode_同値1() {
		// 準備
		TargetMonth result1 = TargetMonth.from("11");
		TargetMonth result2 = TargetMonth.from("11");

		// 検証
		assertEquals(result1.hashCode(), result2.hashCode());
	}
	
	@Test
	@DisplayName("hashCode：同じ年月は同じhashCodeを返す2")
	void testHashCode_同値2() {
		// 準備
		TargetMonth result1 = TargetMonth.from("11");
		TargetMonth result2 = TargetMonth.from(11);

		// 検証
		assertEquals(result1.hashCode(), result2.hashCode());
	}

	// ===========================================================
	// plus(TargetMonth month, int months)
	// ===========================================================

	@Test
	@DisplayName("plus：年をまたがない加算（5月+3=8月）")
	void testPlus_年をまたがない加算() {
		// 実行
		TargetMonth result = TargetMonth.from(5).plus(3);

		// 検証
		assertEquals("08", result.getValue());
	}

	@Test
	@DisplayName("plus：ちょうど12月になる加算（10月+2=12月）")
	void testPlus_12月ちょうど() {
		// 実行
		TargetMonth result = TargetMonth.from(10).plus(2);

		// 検証
		assertEquals("12", result.getValue());
	}

	@Test
	@DisplayName("plus：年をまたぐ加算（12月+1=1月）")
	void testPlus_年をまたぐ加算() {
		// 実行
		TargetMonth result = TargetMonth.from(12).plus(1);

		// 検証
		assertEquals("01", result.getValue());
	}

	@Test
	@DisplayName("plus：複数年またぐ加算（1月+13=2月）")
	void testPlus_複数年またぐ加算() {
		// 実行
		TargetMonth result = TargetMonth.from(1).plus(13);

		// 検証
		assertEquals("02", result.getValue());
	}

	@Test
	@DisplayName("plus：0を加算した場合は変化しない（5月+0=5月）")
	void testPlus_0加算() {
		// 実行
		TargetMonth result = TargetMonth.from(5).plus(0);

		// 検証
		assertEquals("05", result.getValue());
	}

	@Test
	@DisplayName("plus：負数を加算した場合は減算として扱われる（1月+(-1)=12月）")
	void testPlus_負数加算_年をまたぐ() {
		// 実行
		TargetMonth result = TargetMonth.from(1).plus(-1);

		// 検証
		assertEquals("12", result.getValue());
	}

	@Test
	@DisplayName("plus：負数を加算した場合は減算として扱われる（3月+(-5)=10月）")
	void testPlus_負数加算_複数年またぐ() {
		// 実行
		TargetMonth result = TargetMonth.from(3).plus(-5);

		// 検証
		assertEquals("10", result.getValue());
	}

	@Test
	@DisplayName("plus：大きな月数を加算しても1～12の範囲に収まる（1月+25=2月）")
	void testPlus_大きな月数() {
		// 実行
		TargetMonth result = TargetMonth.from(1).plus(25);

		// 検証
		assertEquals("02", result.getValue());
	}

	// ===========================================================
	// minus(TargetMonth month, int months)
	// ===========================================================

	@Test
	@DisplayName("minus：年をまたがない減算（8月-3=5月）")
	void testMinus_年をまたがない減算() {
		// 実行
		TargetMonth result = TargetMonth.from(8).minus(3);

		// 検証
		assertEquals("05", result.getValue());
	}

	@Test
	@DisplayName("minus：ちょうど1月になる減算（3月-2=1月）")
	void testMinus_1月ちょうど() {
		// 実行
		TargetMonth result = TargetMonth.from(3).minus(2);

		// 検証
		assertEquals("01", result.getValue());
	}

	@Test
	@DisplayName("minus：年をまたぐ減算（1月-1=12月）")
	void testMinus_年をまたぐ減算() {
		// 実行
		TargetMonth result = TargetMonth.from(1).minus(1);

		// 検証
		assertEquals("12", result.getValue());
	}

	@Test
	@DisplayName("minus：複数年またぐ減算（1月-13=12月）")
	void testMinus_複数年またぐ減算() {
		// 実行
		TargetMonth result = TargetMonth.from(1).minus(13);

		// 検証
		assertEquals("12", result.getValue());
	}

	@Test
	@DisplayName("minus：0を減算した場合は変化しない（5月-0=5月）")
	void testMinus_0減算() {
		// 実行
		TargetMonth result = TargetMonth.from(5).minus(0);

		// 検証
		assertEquals("05", result.getValue());
	}

	@Test
	@DisplayName("minus：負数を減算した場合は加算として扱われる（1月-(-1)=2月）")
	void testMinus_負数減算() {
		// 実行
		TargetMonth result = TargetMonth.from(1).minus(-1);

		// 検証
		assertEquals("02", result.getValue());
	}

	@Test
	@DisplayName("minus：大きな月数を減算しても1～12の範囲に収まる（1月-25=12月）")
	void testMinus_大きな月数() {
		// 実行
		TargetMonth result = TargetMonth.from(1).minus(25);

		// 検証
		assertEquals("12", result.getValue());
	}
}
