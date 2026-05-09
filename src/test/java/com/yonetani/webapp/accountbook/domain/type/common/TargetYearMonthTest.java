/**
 * TargetYearMonth（年月）のテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  コメントなど
 * 2026/05/07 : 1.01.03  新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * TargetYearMonth（年月）のテストクラスです。
 * C1（分岐網羅）で以下メソッドを検証します。
 * ・from(String yearMonth) ：ガード節3分岐＋正常系
 * ・from(String year, String month)：引数不正2分岐＋正常系
 * ・getYear / getMonth / toString：正常系
 * ・plusMonths：0加算・年内加算・年跨ぎ加算・負加算
 * ・toDisplayLabel：1桁月（先頭0あり）・2桁月
 * ・equals / hashCode：同値・異値
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.01.A)
 *
 */
@DisplayName("年月(TargetYearMonth)のテスト")
class TargetYearMonthTest {

	// ===========================================================
	// from(String yearMonth)
	// ===========================================================

	@Test
	@DisplayName("正常系：有効な年月(202511)で生成できる")
	void testFrom_正常系_有効な年月() {
		// 実行
		TargetYearMonth result = TargetYearMonth.from("202511");

		// 検証
		assertNotNull(result);
		assertEquals("202511", result.getValue());
		assertEquals("2025", result.getYear());
		assertEquals("11", result.getMonth());
	}

	@Test
	@DisplayName("正常系：月が01（先頭0あり）で生成できる")
	void testFrom_正常系_1桁月先頭0() {
		// 実行
		TargetYearMonth result = TargetYearMonth.from("202601");

		// 検証
		assertNotNull(result);
		assertEquals("202601", result.getValue());
		assertEquals("2026", result.getYear());
		assertEquals("01", result.getMonth());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する（ガード節①-A）")
	void testFrom_異常系_null() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetYearMonth.from((String) null)
		);
	}

	@Test
	@DisplayName("異常系：空文字列で例外が発生する（ガード節①-B）")
	void testFrom_異常系_空文字列() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetYearMonth.from("")
		);
	}

	@Test
	@DisplayName("異常系：5桁（6桁未満）で例外が発生する（ガード節②-A）")
	void testFrom_異常系_5桁() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetYearMonth.from("20251")
		);
	}

	@Test
	@DisplayName("異常系：7桁（6桁超）で例外が発生する（ガード節②-B）")
	void testFrom_異常系_7桁() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetYearMonth.from("2025111")
		);
	}

	@Test
	@DisplayName("異常系：13月（カレンダー上無効）で例外が発生する（ガード節③）")
	void testFrom_異常系_13月() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetYearMonth.from("202513")
		);
	}

	// ===========================================================
	// from(String year, String month)
	// ===========================================================

	@Test
	@DisplayName("正常系：年と月を別々に指定して生成できる")
	void testFrom2arg_正常系() {
		// 実行
		TargetYearMonth result = TargetYearMonth.from("2025", "11");

		// 検証
		assertNotNull(result);
		assertEquals("202511", result.getValue());
		assertEquals("2025", result.getYear());
		assertEquals("11", result.getMonth());
	}

	@Test
	@DisplayName("正常系：年と月を別々に指定(01月)して生成できる")
	void testFrom2arg_正常系_01月() {
		// 実行
		TargetYearMonth result = TargetYearMonth.from("2026", "01");

		// 検証
		assertNotNull(result);
		assertEquals("202601", result.getValue());
		assertEquals("01", result.getMonth());
	}

	@Test
	@DisplayName("異常系：yearが不正（空文字列）で例外が発生する")
	void testFrom2arg_異常系_year不正() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetYearMonth.from("", "11")
		);
	}

	@Test
	@DisplayName("異常系：monthが不正（13月）で例外が発生する")
	void testFrom2arg_異常系_month不正() {
		// 実行 & 検証
		assertThrows(
			MyHouseholdAccountBookRuntimeException.class,
			() -> TargetYearMonth.from("2025", "13")
		);
	}

	// ===========================================================
	// getYear / getMonth
	// ===========================================================

	@Test
	@DisplayName("getYear：年(YYYY)の文字列値を返す")
	void testGetYear() {
		// 準備
		TargetYearMonth ym = TargetYearMonth.from("202511");

		// 実行 & 検証
		assertEquals("2025", ym.getYear());
	}

	@Test
	@DisplayName("getMonth：月(MM)の文字列値を返す")
	void testGetMonth() {
		// 準備
		TargetYearMonth ym = TargetYearMonth.from("202511");

		// 実行 & 検証
		assertEquals("11", ym.getMonth());
	}

	// ===========================================================
	// plusMonths(int months)
	// ===========================================================

	@Test
	@DisplayName("plusMonths：0加算は同月を返す")
	void testPlusMonths_0加算() {
		// 準備
		TargetYearMonth ym = TargetYearMonth.from("202511");

		// 実行
		TargetYearMonth result = ym.plusMonths(0);

		// 検証
		assertEquals("202511", result.getValue());
	}

	@Test
	@DisplayName("plusMonths：1加算で年内の次月を返す（11月→12月）")
	void testPlusMonths_年内加算() {
		// 準備
		TargetYearMonth ym = TargetYearMonth.from("202511");

		// 実行
		TargetYearMonth result = ym.plusMonths(1);

		// 検証
		assertEquals("202512", result.getValue());
		assertEquals("2025", result.getYear());
		assertEquals("12", result.getMonth());
	}

	@Test
	@DisplayName("plusMonths：2加算で年をまたいで翌年01月を返す（11月+2→翌年01月）")
	void testPlusMonths_年跨ぎ() {
		// 準備
		TargetYearMonth ym = TargetYearMonth.from("202511");

		// 実行
		TargetYearMonth result = ym.plusMonths(2);

		// 検証
		assertEquals("202601", result.getValue());
		assertEquals("2026", result.getYear());
		assertEquals("01", result.getMonth());
	}

	@Test
	@DisplayName("plusMonths：負の値で前月を返す（11月-1→10月）")
	void testPlusMonths_マイナス加算() {
		// 準備
		TargetYearMonth ym = TargetYearMonth.from("202511");

		// 実行
		TargetYearMonth result = ym.plusMonths(-1);

		// 検証
		assertEquals("202510", result.getValue());
		assertEquals("10", result.getMonth());
	}

	// ===========================================================
	// toDisplayLabel()
	// ===========================================================

	@Test
	@DisplayName("toDisplayLabel：2桁月は「YYYY年MM月」形式で返す（2025年11月）")
	void testToDisplayLabel_2桁月() {
		// 準備
		TargetYearMonth ym = TargetYearMonth.from("202511");

		// 実行 & 検証
		assertEquals("2025年11月", ym.toDisplayLabel());
	}

	@Test
	@DisplayName("toDisplayLabel：1桁月は先頭0ありで「YYYY年MM月」形式で返す（2026年01月）")
	void testToDisplayLabel_1桁月先頭0あり() {
		// 準備
		TargetYearMonth ym = TargetYearMonth.from("202601");

		// 実行 & 検証
		assertEquals("2026年01月", ym.toDisplayLabel());
	}

	// ===========================================================
	// toString()
	// ===========================================================

	@Test
	@DisplayName("toString：yyyyMM形式の文字列値をそのまま返す")
	void testToString() {
		// 準備
		TargetYearMonth ym = TargetYearMonth.from("202511");

		// 実行 & 検証
		assertEquals("202511", ym.toString());
	}

	// ===========================================================
	// equals / hashCode
	// ===========================================================

	@Test
	@DisplayName("equals：同じ年月は等しいと判定される")
	void testEquals_同値() {
		// 準備
		TargetYearMonth ym1 = TargetYearMonth.from("202511");
		TargetYearMonth ym2 = TargetYearMonth.from("202511");

		// 検証
		assertEquals(ym1, ym2);
	}

	@Test
	@DisplayName("equals：異なる年月は等しくないと判定される")
	void testEquals_異なる値() {
		// 準備
		TargetYearMonth ym1 = TargetYearMonth.from("202511");
		TargetYearMonth ym2 = TargetYearMonth.from("202512");

		// 検証
		assertNotEquals(ym1, ym2);
	}

	@Test
	@DisplayName("hashCode：同じ年月は同じhashCodeを返す")
	void testHashCode_同値() {
		// 準備
		TargetYearMonth ym1 = TargetYearMonth.from("202511");
		TargetYearMonth ym2 = TargetYearMonth.from("202511");

		// 検証
		assertEquals(ym1.hashCode(), ym2.hashCode());
	}
}
