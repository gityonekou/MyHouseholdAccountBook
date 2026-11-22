/**
 * PaymentMethodSortクラスのテストクラスです。
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
 * PaymentMethodSortクラスのテストクラスです。
 *
 *</pre>
 *
 * @author ：Kouki Yonetani
 * @since 家計簿アプリ(1.03)
 *
 */
@DisplayName("支払方法表示順(PaymentMethodSort)のテスト")
class PaymentMethodSortTest {

	@Test
	@DisplayName("正常系：文字列から生成")
	void testFrom_正常系_文字列から生成() {
		PaymentMethodSort sort = PaymentMethodSort.from("001");
		assertEquals("001", sort.getValue());
		assertEquals("001", sort.toString());
	}

	@Test
	@DisplayName("正常系：数値から生成")
	void testFrom_正常系_数値から生成() {
		assertEquals("005", PaymentMethodSort.from(5).getValue());
	}

	@Test
	@DisplayName("正常系：予約帯(990～999)の値も生成できる(バリデーションは別レイヤーの責務)")
	void testFrom_正常系_予約帯も生成可能() {
		assertEquals("999", PaymentMethodSort.from("999").getValue());
	}

	@Test
	@DisplayName("異常系：null値で例外が発生する")
	void testFrom_異常系_null値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> PaymentMethodSort.from((String) null));
	}

	@Test
	@DisplayName("異常系：空文字列で例外が発生する")
	void testFrom_異常系_空文字列値() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> PaymentMethodSort.from(""));
	}

	@Test
	@DisplayName("異常系：3桁でない場合に例外が発生する")
	void testFrom_異常系_桁数不正() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> PaymentMethodSort.from("1"));
	}

	@Test
	@DisplayName("異常系：数値に変換できない場合に例外が発生する")
	void testFrom_異常系_数値変換不可() {
		assertThrows(MyHouseholdAccountBookRuntimeException.class, () -> PaymentMethodSort.from("abc"));
	}

	@Test
	@DisplayName("正常系：equalsとhashCodeが値ベースで一致する")
	void testEquals_正常系_値ベースで一致() {
		assertEquals(PaymentMethodSort.from("001"), PaymentMethodSort.from("001"));
		assertEquals(PaymentMethodSort.from("001").hashCode(), PaymentMethodSort.from("001").hashCode());
	}
}
