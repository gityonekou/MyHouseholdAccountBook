/**
 * ClosingDayクラスのテストクラスです。
 *
 *------------------------------------------------
 * 更新履歴
 * 日付       : version  ブランチ            コメントなど
 * 2026/08/19 : 1.00.00  feature-1.03-dev1   新規作成
 *
 */
package com.yonetani.webapp.accountbook.domain.type.account.paymentmethod;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.yonetani.webapp.accountbook.common.exception.MyHouseholdAccountBookRuntimeException;

/**
 *<pre>
 * ClosingDayクラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("集計開始日(ClosingDay)のテスト")
class ClosingDayTest {

	@Test
	@DisplayName("正常系：nullを許容する")
	void testFrom_正常系_null許容() {
		ClosingDay closingDay = ClosingDay.from(null);
		assertNotNull(closingDay);
		assertNull(closingDay.getValue());
	}

	@Test
	@DisplayName("正常系：空文字列もnullとして許容する")
	void testFrom_正常系_空文字列許容() {
		ClosingDay closingDay = ClosingDay.from("");
		assertNotNull(closingDay);
		assertNull(closingDay.getValue());
	}

	@Test
	@DisplayName("境界値：01は正常に生成できる")
	void testFrom_境界値_01() {
		assertEquals("01", ClosingDay.from("01").getValue());
	}

	@Test
	@DisplayName("境界値：28は正常に生成できる")
	void testFrom_境界値_28() {
		assertEquals("28", ClosingDay.from("28").getValue());
	}

	@Test
	@DisplayName("境界値：00は範囲外で例外が発生する")
	void testFrom_境界値_00は範囲外() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ClosingDay.from("00"));
	}

	@Test
	@DisplayName("境界値：29は範囲外で例外が発生する")
	void testFrom_境界値_29は範囲外() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ClosingDay.from("29"));
	}

	@Test
	@DisplayName("異常系：1桁の場合例外が発生する")
	void testFrom_異常系_桁数不正() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ClosingDay.from("1"));
	}

	@Test
	@DisplayName("異常系：数値に変換できない場合例外が発生する")
	void testFrom_異常系_数値変換不可() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> ClosingDay.from("ab"));
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		assertEquals(ClosingDay.from("15"), ClosingDay.from("15"));
		assertEquals(ClosingDay.from(null), ClosingDay.from(null));
	}
}
